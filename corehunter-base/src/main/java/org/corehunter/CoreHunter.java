/*--------------------------------------------------------------*/
/* Licensed to the Apache Software Foundation (ASF) under one   */
/* or more contributor license agreements.  See the NOTICE file */
/* distributed with this work for additional information        */
/* regarding copyright ownership.  The ASF licenses this file   */
/* to you under the Apache License, Version 2.0 (the            */
/* "License"); you may not use this file except in compliance   */
/* with the License.  You may obtain a copy of the License at   */
/*                                                              */
/*   http://www.apache.org/licenses/LICENSE-2.0                 */
/*                                                              */
/* Unless required by applicable law or agreed to in writing,   */
/* software distributed under the License is distributed on an  */
/* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       */
/* KIND, either express or implied.  See the License for the    */
/* specific language governing permissions and limitations      */
/* under the License.                                           */
/*--------------------------------------------------------------*/

package org.corehunter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.exceptions.CoreHunterException;
import org.corehunter.objectives.AverageAccessionToNearestEntry;
import org.corehunter.objectives.AverageEntryToEntry;
import org.corehunter.objectives.AverageEntryToNearestEntry;
import org.corehunter.objectives.Coverage;
import org.corehunter.objectives.HeterozygousLoci;
import org.corehunter.objectives.Shannon;
import org.corehunter.objectives.distance.DistanceMeasure;
import org.corehunter.objectives.distance.measures.CavalliSforzaEdwardsDistance;
import org.corehunter.objectives.distance.measures.GowerDistance;
import org.corehunter.objectives.distance.measures.ModifiedRogersDistance;
import org.corehunter.objectives.distance.measures.PrecomputedDistance;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.search.stopcriteria.MaxTimeWithoutImprovement;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.ext.problems.objectives.WeightedIndex;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * A facade for executing Core Hunter searches. Can be re-used.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public class CoreHunter {

    private static final int DEFAULT_MAX_TIME_WITHOUT_IMPROVEMENT = 5;
    private static final int FAST_MAX_TIME_WITHOUT_IMPROVEMENT = 1;
    
    private static final int PT_NUM_REPLICAS = 10;
    private static final double PT_MIN_TEMP = 1e-8;
    private static final double PT_MAX_TEMP = 1e-4;
    
    private CoreHunterListener listener;
    private long timeLimit = -1;
    private long maxTimeWithoutImprovement = -1;
    private CoreHunterExecutionMode mode;

    public CoreHunter() {
        this(CoreHunterExecutionMode.DEFAULT);
    }

    /**
     * Create Core Hunter facade with the specified mode.
     * In {@link CoreHunterExecutionMode#DEFAULT} mode parallel tempering is applied
     * and terminated when no improvement has been made for 5 seconds.
     * In {@link CoreHunterExecutionMode#FAST} mode random descent is applied
     * and terminated when no improvement has been made for 1 second.
     * By default no absolute time limit is set in any of the two modes.
     * Stop conditions can be altered with {@link #setMaxTimeWithoutImprovement(long)}
     * and {@link #setTimeLimit(long)}.
     * 
     * @param mode execution mode
     */
    public CoreHunter(CoreHunterExecutionMode mode) {
        this.mode = mode;
        maxTimeWithoutImprovement = DEFAULT_MAX_TIME_WITHOUT_IMPROVEMENT;
        if(mode == CoreHunterExecutionMode.FAST){
            maxTimeWithoutImprovement = FAST_MAX_TIME_WITHOUT_IMPROVEMENT;
        }
    }

    public SubsetSolution execute(CoreHunterArguments arguments) {

        if (arguments == null) {
            throw new IllegalArgumentException("Arguments not defined!");
        }

        if (arguments.getData() == null) {
            throw new IllegalArgumentException("Dataset not defined!");
        }

        // create search from arguments
        Search<SubsetSolution> search = createSearch(arguments);

        // set stop criteria
        if(timeLimit <= 0 && maxTimeWithoutImprovement <= 0){
            throw new IllegalStateException(
                    "Please specify time limit and/or maximum time without improvement before execution."
            );
        }
        if (timeLimit > 0) {
            search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
        }
        if (maxTimeWithoutImprovement > 0){
            search.addStopCriterion(new MaxTimeWithoutImprovement(maxTimeWithoutImprovement, TimeUnit.SECONDS));
        }
        
        // add search listener (if any)
        if (listener != null) {
            search.addSearchListener(listener);
        }

        // start search
        search.start();

        // dispose search
        search.dispose();

        return search.getBestSolution();
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    /**
     * Sets the absolute time limit (in seconds).
     * A negative value means that no time limit is imposed.
     * 
     * @param seconds absolute time limit in seconds
     */
    public void setTimeLimit(long seconds) {
        this.timeLimit = seconds;
    }
    
    public long getMaxTimeWithoutImprovement(){
        return maxTimeWithoutImprovement;
    }
    
    /**
     * Sets the maximum time without finding any improvements (in seconds).
     * A negative value means that no such stop condition is set.
     * 
     * @param seconds maximum time without improvement in seconds
     */
    public void setMaxTimeWithoutImprovement(long seconds){
        maxTimeWithoutImprovement = seconds;
    }
    
    public CoreHunterListener getListener(){
        return listener;
    }
    
    public void setListener(CoreHunterListener listener){
        this.listener = listener;
    }

    protected Search<SubsetSolution> createSearch(CoreHunterArguments arguments) {

        int size = arguments.getSubsetSize();

        Objective<SubsetSolution, CoreHunterData> objective = createObjective(arguments);

        SubsetProblem<CoreHunterData> problem = new SubsetProblem<>(arguments.getData(), objective, size);
        Neighbourhood<SubsetSolution> neigh = new SingleSwapNeighbourhood();
        
        Search<SubsetSolution> search;
        switch(mode){
            case DEFAULT:
                search = new ParallelTempering<>(problem, neigh, PT_NUM_REPLICAS, PT_MIN_TEMP, PT_MAX_TEMP);
                break;
            case FAST:
                search = new RandomDescent<>(problem, neigh);
                break;
            default:
                throw new CoreHunterException("Unknown execution mode " + mode + ".");

        }

        return search;

    }

    private Objective<SubsetSolution, CoreHunterData> createObjective(CoreHunterArguments arguments) {
        // extract data and objectives
        CoreHunterData data = arguments.getData();
        List<CoreHunterObjective> objectives = arguments.getObjectives();
        // compose objective
        if (objectives == null || objectives.isEmpty()) {
            throw new CoreHunterException("No objective(s) given.");
        } else {
            if (objectives.size() == 1) {
                // single objective
                return createObjective(data, objectives.get(0));
            } else {
                // multiple objectives (weighted index)
                WeightedIndex<SubsetSolution, CoreHunterData> weightedIndex =  new WeightedIndex<>();
                for(CoreHunterObjective obj : objectives) {
                    weightedIndex.addObjective(createObjective(data, obj), obj.getWeight());
                }
                return weightedIndex;
            }
        }
    }

    private Objective<SubsetSolution, CoreHunterData> createObjective(CoreHunterData data,
                                                                      CoreHunterObjective coreHunterObjective) {

        Objective<SubsetSolution, CoreHunterData> objective = null;
        DistanceMeasure distanceMeasure = null;

        if (coreHunterObjective.getMeasure() != null) {
            switch (coreHunterObjective.getMeasure()) {
                case MODIFIED_ROGERS:
                    if (!data.hasGenotypes()) {
                        throw new CoreHunterException("Genotypes are required for Modified Rogers distance.");
                    }
                    distanceMeasure = new ModifiedRogersDistance();
                    break;
                case CAVALLI_SFORZA_EDWARDS:
                    if (!data.hasGenotypes()) {
                        throw new CoreHunterException(
                                "Genotypes are required for Cavalli-Sforza and Edwards distance."
                        );
                    }
                    distanceMeasure = new CavalliSforzaEdwardsDistance();
                    break;
                case GOWERS:
                    if (!data.hasPhenotypes()) {
                        throw new CoreHunterException("Phenotypes are required for Gower distance.");
                    }
                    distanceMeasure = new GowerDistance();
                    break;
                case PRECOMPUTED_DISTANCE:
                    if (!data.hasDistances()) {
                        throw new CoreHunterException("No precomputed distance matrix has been defined.");
                    }
                    distanceMeasure = new PrecomputedDistance();
                    break;
                default:
                    // do nothing (not all objectives require a distance measure)
            }
        }

        switch (coreHunterObjective.getObjectiveType()) {
            case AV_ACCESSION_TO_NEAREST_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY));
                }
                objective = new AverageAccessionToNearestEntry(distanceMeasure);
                break;
            case AV_ENTRY_TO_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY));
                }
                objective = new AverageEntryToEntry(distanceMeasure);
                break;
            case AV_ENTRY_TO_NEAREST_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY));
                }
                objective = new AverageEntryToNearestEntry(distanceMeasure);
                break;
            case COVERAGE:
                if (!data.hasGenotypes()) {
                    throw new CoreHunterException("Genotypes are required for coverage objective.");
                }
                objective = new Coverage();
                break;
            case HETEROZYGOUS_LOCI:
                if (!data.hasGenotypes()) {
                    throw new CoreHunterException(
                            "Genotypes are required for expected proportion of heterozygous loci objective."
                    );
                }
                objective = new HeterozygousLoci();
                break;
            case SHANNON_DIVERSITY:
                if (!data.hasGenotypes()) {
                    throw new CoreHunterException("Genotypes are required for Shannon's index.");
                }
                objective = new Shannon();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown objective : %s", coreHunterObjective));

        }

        return objective;
    }
    
}
