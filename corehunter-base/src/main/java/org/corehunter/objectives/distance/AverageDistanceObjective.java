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

package org.corehunter.objectives.distance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

public class AverageDistanceObjective implements
        Objective<SubsetSolution, CoreHunterData> {

    /**
     * Evaluates the given subset solution using the given data, by computing the average distance between all pairs of
     * selected items. If less than two items are selected, this method always returns 0.
     *
     * @param solution the subset solution to be evaluated
     * @param data the distance matrix
     */
    @Override
    public Evaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        double value = 0.0;
        
        DistanceMatrixData distanceData = data.getDistancesData() ;
        
        if (solution.getNumSelectedIDs() >= 2) {
            // at least two items selected: compute average distance
            int numDist = 0;
            double sumDist = 0.0;
            Integer[] selected = new Integer[solution.getNumSelectedIDs()];
            solution.getSelectedIDs().toArray(selected);
            for (int i = 0; i < selected.length; i++) {
                for (int j = i + 1; j < selected.length; j++) {
                    sumDist += distanceData.getDistance(selected[i], selected[j]);
                    numDist++;
                }
            }
            value = sumDist / numDist;
        }
        return SimpleEvaluation.WITH_VALUE(value);
    }

    @Override
    public Evaluation evaluate(Move move, SubsetSolution curSolution,
                               Evaluation curEvaluation, CoreHunterData data) {
        
        DistanceMatrixData distanceData = data.getDistancesData() ;
        // check move type
        if (!(move instanceof SubsetMove)) {
            throw new IncompatibleDeltaEvaluationException("Core subset objective should be used in combination "
                    + "with neighbourhoods that generate moves of type SubsetMove.");
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;

        // get current evaluation
        double curEval = curEvaluation.getValue();
        // undo average to get sum of distances
        int numSelected = curSolution.getNumSelectedIDs();
        int numDistances = numSelected * (numSelected - 1) / 2;
        double sumDist = curEval * numDistances;

        // get set of added and removed IDs
        Set<Integer> added = subsetMove.getAddedIDs();
        Set<Integer> removed = subsetMove.getDeletedIDs();
        // infer list of retained IDs
        List<Integer> retained = new ArrayList<>(curSolution.getSelectedIDs());
        retained.removeAll(removed);

        // subtract distances from removed items to retained items
        for (int rem : removed) {
            for (int ret : retained) {
                sumDist -= distanceData.getDistance(rem, ret);
                numDistances--;
            }
        }

        // subtract distances from removed to other removed items
        for (int rem1 : removed) {
            for (int rem2 : removed) {
                // account for each distinct pair only once
                if (rem1 < rem2) {
                    sumDist -= distanceData.getDistance(rem1, rem2);
                    numDistances--;
                }
            }
        }

        // add distances from new items to retained items
        for (int add : added) {
            for (int ret : retained) {
                sumDist += distanceData.getDistance(add, ret);
                numDistances++;
            }
        }

        // add distances from new items to other new items
        for (int add1 : added) {
            for (int add2 : added) {
                // account for each distinct pair only once
                if (add1 < add2) {
                    sumDist += distanceData.getDistance(add1, add2);
                    numDistances++;
                }
            }
        }

        double newEval;
        if (numDistances > 0) {
            // take average based on updated number of distances
            newEval = sumDist / numDistances;
        } else {
            // no distances (less than two items remain selected)
            newEval = 0.0;
        }

        // return new evaluation
        return SimpleEvaluation.WITH_VALUE(newEval);

    }

    @Override
    public boolean isMinimizing() {
        return false;
    }

}
