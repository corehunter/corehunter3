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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.corehunter.data.CoreHunterData;
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
import org.jamesframework.ext.problems.objectives.NormalizedObjective;
import org.jamesframework.ext.problems.objectives.WeightedIndex;

/**
 * A facade for executing Core Hunter searches. Can be re-used.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public class CoreHunter {

    private static final int DEFAULT_MAX_TIME_WITHOUT_IMPROVEMENT = 5;
    private static final int FAST_MAX_TIME_WITHOUT_IMPROVEMENT = 1;
    private static final double RELATIVE_NORMALIZATION_TIME = 0.2;
    
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
     * <p>
     * In {@link CoreHunterExecutionMode#DEFAULT} mode parallel tempering is applied
     * and terminated when no improvement has been made for 5 seconds.
     * In {@link CoreHunterExecutionMode#FAST} mode random descent is applied
     * and terminated when no improvement has been made for 1 second.
     * By default no absolute time limit is set in any of the two modes.
     * Stop conditions can be altered with {@link #setMaxTimeWithoutImprovement(long)}
     * and {@link #setTimeLimit(long)}.
     * <p>
     * In case of a multi-objective configuration with normalization enabled, a preliminary
     * search is performed per objective to determine suitable bounds based  on the Pareto
     * minima/maxima. If a time limit has been set, 20% of the available time is reserved
     * for normalization of all objectives in total (equally divided among objectives).
     * The actual execution than takes up to the remaining 80% of time.
     * When limiting the time without finding an improvement, one fifth of that limit is applied
     * for each preliminary normalization search (i.e. per objective).
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
        long remainingTime = 1000 * timeLimit; // sec to ms
        if(arguments.isNormalized()){
            // subtract time taken for normalization
            remainingTime -= RELATIVE_NORMALIZATION_TIME * remainingTime;
        }
        if(remainingTime <= 0 && maxTimeWithoutImprovement <= 0){
            throw new IllegalStateException(
                    "Please specify time limit and/or maximum time without improvement before execution."
            );
        }
        if (remainingTime > 0) {
            search.addStopCriterion(new MaxRuntime(remainingTime, TimeUnit.MILLISECONDS));
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
    
    /**
     * Evaluate the given solution with the specified objective. The weight of the objective is ignored.
     * 
     * @param sol subset solution
     * @param data Core Hunter data
     * @param objective objective used to evaluate the subset (weight is ignored)
     * @return value of the subset according to the specified objective
     */
    public double evaluate(SubsetSolution sol, CoreHunterData data, CoreHunterObjective objective){
        Objective<SubsetSolution, CoreHunterData> obj = createObjective(data, objective);
        return obj.evaluate(sol, data).getValue();
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
        
        Search<SubsetSolution> search = createSearch(problem, neigh);

        return search;

    }
    
    private Search<SubsetSolution> createSearch(SubsetProblem<CoreHunterData> problem,
                                                Neighbourhood<SubsetSolution> neigh){
        switch(mode){
            case DEFAULT:
                return new ParallelTempering<>(problem, neigh, PT_NUM_REPLICAS, PT_MIN_TEMP, PT_MAX_TEMP);
            case FAST:
                return new RandomDescent<>(problem, neigh);
            default:
                throw new CoreHunterException("Unknown execution mode " + mode + ".");
        }
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
                // create all objectives
                List<Objective<SubsetSolution, CoreHunterData>> jamesObjectives = objectives.stream()
                        .map(obj -> createObjective(data, obj))
                        .collect(Collectors.toList());
                // normalize if requested
                if(arguments.isNormalized()){
                    jamesObjectives = normalizeObjectives(arguments, jamesObjectives);
                }
                // combine in weighted index
                for(int o = 0; o < objectives.size(); o++) {
                    weightedIndex.addObjective(jamesObjectives.get(o), objectives.get(o).getWeight());
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
    
    private List<Objective<SubsetSolution, CoreHunterData>> normalizeObjectives(
            CoreHunterArguments arguments,
            List<Objective<SubsetSolution, CoreHunterData>> objectives
    ){
        
        if(listener != null){
            listener.preprocessingStarted("Normalizing objectives.");
        }
        
        CoreHunterData data = arguments.getData();
        int size = arguments.getSubsetSize();
        
        // determine optimal solution for each objective (extremes of Pareto front)
        // through preliminary search per objective
        List<SubsetSolution> optimalSolutions = new ArrayList<>();
        objectives.forEach(obj -> {
            // include other objectives as well with a very small weight to avoid
            // weakly Pareto optimal solutions and improve stability of the normalization
            WeightedIndex<SubsetSolution, CoreHunterData> index = new WeightedIndex<>();
            index.addObjective(obj, 1.0);
            objectives.forEach(otherObj -> {
                if(!otherObj.equals(obj)){
                    index.addObjective(otherObj, 1e-8);
                }
            });
            SubsetProblem<CoreHunterData> problem = new SubsetProblem(data, index, size);
            Search<SubsetSolution> normalizationSearch = createSearch(problem, new SingleSwapNeighbourhood());
            // limit total normalization runtime to 20% of execution time limit (in ms)
            long normTime = (long) (1000 * timeLimit * RELATIVE_NORMALIZATION_TIME / objectives.size());
            if(normTime > 0){
                normalizationSearch.addStopCriterion(new MaxRuntime(normTime, TimeUnit.MILLISECONDS));
            }
            // set improvement time to one fifth of that used for execution, per normalization search (in ms)
            long normalizationImprTime = (long) (1000 * maxTimeWithoutImprovement * RELATIVE_NORMALIZATION_TIME);
            if(normalizationImprTime > 0){
                normalizationSearch.addStopCriterion(
                        new MaxTimeWithoutImprovement(normalizationImprTime, TimeUnit.MILLISECONDS)
                );
            }
            // execute normalization search
            normalizationSearch.run();
            // store optimal solution for considered objective
            optimalSolutions.add(normalizationSearch.getBestSolution());
        });
        
        // normalize objective (lower-upper bound scaling with Pareto maxima/minima)
        StringBuilder message = new StringBuilder();
        List<Objective<SubsetSolution, CoreHunterData>> normalizedObjectives = new ArrayList<>();
        for(int o = 0; o < objectives.size(); o++){
            Objective<SubsetSolution, CoreHunterData> obj = objectives.get(o);
            // determine value of best solution in terms of this objective
            double bestValue = obj.evaluate(optimalSolutions.get(o), data).getValue();
            // determine values of all optimal solutions when evaluated with this objective
            List<Double> allOpt = optimalSolutions.stream()
                                                  .map(sol -> obj.evaluate(sol, data).getValue())
                                                  .collect(Collectors.toList());
            // set bounds taking into account whether the objective is minimized or maximized
            double min, max;
            if(obj.isMinimizing()){
                // best solution value = lower bound
                min = bestValue;
                // max of all values = upper bound
                max = Collections.max(allOpt);
            } else {
                // best solution value = upper bound
                max = bestValue;
                // min of all values = lower bound
                min = Collections.min(allOpt);
            }
            // scale objective
            normalizedObjectives.add(new NormalizedObjective<>(obj, min, max));
            message.append(String.format("%s: [%.3f, %.3f]%n", obj, min, max));
        }
        
        message.append("Finished normalization.");
        if(listener != null){
            listener.preprocessingStopped(message.toString());
        }
        
        return normalizedObjectives;
        
    }
    
}
