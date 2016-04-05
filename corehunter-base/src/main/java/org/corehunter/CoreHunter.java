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

import java.util.concurrent.TimeUnit;

import org.corehunter.objectives.distance.AverageEntryToEntryDistance;
import org.corehunter.objectives.distance.measures.CavalliSforzaEdwardsDistance;
import org.corehunter.objectives.distance.measures.ModifiedRogersDistance;
import org.corehunter.objectives.Coverage;
import org.corehunter.objectives.HeterozygousLoci;
import org.corehunter.objectives.Shannon;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;

import org.corehunter.data.CoreHunterData;
import org.corehunter.objectives.distance.measures.GowerDistance;
import org.corehunter.objectives.distance.measures.PrecomputedDistance;

/**
 * Provides support for executing pre-defined core subset searches. Can be re-used.
 *
 * @author Guy Davenport
 */
public class CoreHunter {

    private CoreHunterArguments arguments;
    private long timeLimit = 60;

    public CoreHunter(CoreHunterArguments arguments) {
        this.arguments = arguments;
    }

    public final SubsetSolution execute() {
        return execute(null);
    }

    public SubsetSolution execute(CoreHunterListener listener) {
        
        if (arguments.getData() == null) {
            throw new IllegalArgumentException("Dataset not defined!");
        }

        Search<SubsetSolution> search = createSearch(arguments);
        search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));

        if (listener != null) {
            search.addSearchListener(listener);
        }

        // start search
        search.start();

        // dispose search
        search.dispose();

        return search.getBestSolution();
    }

    protected Search<SubsetSolution> createSearch(CoreHunterArguments arguments) {
        
        Objective<SubsetSolution, CoreHunterData> objective;
        switch (arguments.getObjective()) {
            case MR:
                objective = new AverageEntryToEntryDistance(new ModifiedRogersDistance());
                break;
            case CE:
                objective = new AverageEntryToEntryDistance(new CavalliSforzaEdwardsDistance());
                break;
            case PD:
                objective = new AverageEntryToEntryDistance(new PrecomputedDistance());
                break;
            case CV:
                objective = new Coverage();
                break;
            case HE:
                objective = new HeterozygousLoci();
                break;
            case SH:
                objective = new Shannon();
                break;
            case GD:
                objective = new AverageEntryToEntryDistance(new GowerDistance());
                break;
            default:
                throw new IllegalArgumentException("Unknown objective : " + arguments.getObjective());
        }
        
        CoreHunterData dataset = arguments.getData();

        SubsetProblem<CoreHunterData> problem = new SubsetProblem<>(
                dataset, objective, arguments.getMinimumSubsetSize(), arguments.getMaximumSubsetSize()
        );

        RandomDescent<SubsetSolution> search = new RandomDescent<>(problem, new SinglePerturbationNeighbourhood());

        return search;
        
    }

    public final long getTimeLimit() {
        return timeLimit;
    }

    public final void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

}
