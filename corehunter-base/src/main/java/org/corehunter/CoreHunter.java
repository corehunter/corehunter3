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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.ext.problems.objectives.WeightedIndex;

/**
 * A facade for executing CoreHunter searches. Can be re-used.
 *
 * @author Guy Davenport
 */
public class CoreHunter {

    private CoreHunterListener listener;
    private long timeLimit = 60;

    public CoreHunter() {

    }

    public CoreHunter(CoreHunterListener listener) {
        this.listener = listener;
    }

    public final SubsetSolution execute() {
        return execute(null);
    }

    public SubsetSolution execute(CoreHunterArguments arguments) {

        if (arguments == null) {
            throw new IllegalArgumentException("Arguments not defined!");
        }

        if (arguments.getData() == null) {
            throw new IllegalArgumentException("Dataset not defined!");
        }

        Search<SubsetSolution> search = createSearch(arguments);

        if (timeLimit > 0) {
            search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
        }

        if (listener != null) {
            search.addSearchListener(listener);
        }

        // start search
        search.start();

        // dispose search
        search.dispose();

        return search.getBestSolution();
    }

    public final long getTimeLimit() {
        return timeLimit;
    }

    public final void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    protected Search<SubsetSolution> createSearch(CoreHunterArguments arguments) {

        int size = arguments.getSubsetSize();

        Objective<SubsetSolution, CoreHunterData> objective = createObjective(arguments.getData(),
                arguments.getObjectives());

        SubsetProblem<CoreHunterData> problem = new SubsetProblem<CoreHunterData>(arguments.getData(), objective, size,
                size);

        Neighbourhood<SubsetSolution> neigh = new SingleSwapNeighbourhood();

        RandomDescent<SubsetSolution> search = new RandomDescent<>(problem, neigh);

        return search;

    }

    private Objective<SubsetSolution, CoreHunterData> createObjective(CoreHunterData data,
            List<CoreHunterObjective> objectives) {
        if (objectives.size() == 0) {
            throw new CoreHunterException("No objective given.");
        } else {
            if (objectives.size() == 1) {
                return createObjective(data, objectives.get(0));
            } else {
                Iterator<CoreHunterObjective> iterator = objectives.iterator();

                double totalWeight = 0;

                while (iterator.hasNext()) {
                    totalWeight = totalWeight + iterator.next().getWeight();
                }

                iterator = objectives.iterator();

                CoreHunterObjective coreHunterObjective;

                WeightedIndex<SubsetSolution, CoreHunterData> weightedIndex = 
                        new WeightedIndex<SubsetSolution, CoreHunterData>();

                while (iterator.hasNext()) {
                    coreHunterObjective = iterator.next();

                    weightedIndex.addObjective(createObjective(data, coreHunterObjective),
                            coreHunterObjective.getWeight());
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
                case CAVALLI_SFORZA_EDWARDS:
                    if (data.getGenotypicData() == null) {
                        throw new CoreHunterException(
                                "Genotypes are required for Cavalli-Sforza and Edwards distance.");
                    }
                    distanceMeasure = new CavalliSforzaEdwardsDistance();
                    break;
                case GOWERS:
                    if (data.getPhenotypicData() == null) {
                        throw new CoreHunterException("Phenotypes are required for Gower distance.");
                    }
                    distanceMeasure = new GowerDistance();
                    break;
                case MODIFIED_ROGERS:
                    if (data.getGenotypicData() == null) {
                        throw new CoreHunterException("Genotypes are required for Modified Rogers distance.");
                    }
                    distanceMeasure = new ModifiedRogersDistance();
                    break;
                case PRECOMPUTED_DISTANCE:
                    if (data.getDistancesData() == null) {
                        throw new CoreHunterException("No precomputed distance matrix has been defined.");
                    }
                    distanceMeasure = new PrecomputedDistance();
                    break;
                default:
                    break;
            }
        }

        switch (coreHunterObjective.getObjectiveType()) {
            case AV_ACCESSION_TO_NEAREST_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY.name()));
                }
                objective = new AverageAccessionToNearestEntry(distanceMeasure);
                break;
            case AV_ENTRY_TO_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY.name()));
                }
                objective = new AverageEntryToEntry(distanceMeasure);
                break;
            case AV_ENTRY_TO_NEAREST_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY.name()));
                }
                objective = new AverageEntryToNearestEntry(distanceMeasure);
                break;
            case COVERAGE:
                if (data.getGenotypicData() == null) {
                    throw new CoreHunterException("Genotypes are required for coverage objective.");
                }
                objective = new Coverage();
                break;
            case HETEROZYGOUS_LOCI:
                if (data.getGenotypicData() == null) {
                    throw new CoreHunterException(
                            "Genotypes are required for expected proportion of heterozygous loci objective.");
                }
                objective = new HeterozygousLoci();
                break;
            case SHANNON_DIVERSITY:
                if (data.getGenotypicData() == null) {
                    throw new CoreHunterException("Genotypes are required for Shannon's index.");
                }
                objective = new Shannon();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown format : %s", coreHunterObjective));

        }

        return objective;
    }
}
