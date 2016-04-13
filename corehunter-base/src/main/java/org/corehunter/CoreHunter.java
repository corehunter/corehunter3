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

import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;

import org.corehunter.data.CoreHunterData;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;

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
        
        int minSize = arguments.getMinimumSubsetSize();
        int maxSize = arguments.getMaximumSubsetSize();
        SubsetProblem<CoreHunterData> problem = new SubsetProblem<>(
                arguments.getData(),
                arguments.getObjective(),
                minSize, maxSize
        );

        Neighbourhood<SubsetSolution> neigh = new SingleSwapNeighbourhood();
        if(minSize != maxSize){
            neigh = new SinglePerturbationNeighbourhood(minSize, maxSize);
        }
        RandomDescent<SubsetSolution> search = new RandomDescent<>(problem, neigh);

        return search;
        
    }

    public final long getTimeLimit() {
        return timeLimit;
    }

    public final void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

}
