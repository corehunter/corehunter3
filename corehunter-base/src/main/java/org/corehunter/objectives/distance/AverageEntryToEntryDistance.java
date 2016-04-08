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
import org.corehunter.objectives.distance.measures.MissingValuesPolicy;

import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

/**
 * Evaluates a core set by computing the average distance between all pairs of selected items.
 * This value is to be maximized. If less than two items are selected, the value is set to 0.0.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class AverageEntryToEntryDistance implements Objective<SubsetSolution, CoreHunterData> {

    private final DistanceMeasure distanceMeasure;

    public AverageEntryToEntryDistance(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
        // floor missing values contribution (worst case when maximizing distances)
        distanceMeasure.setMissingValuesPolicy(MissingValuesPolicy.FLOOR);
    }
    
    @Override
    public Evaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        double value = 0.0;
                
        if (solution.getNumSelectedIDs() >= 2) {
            // at least two items selected: compute average pairwise distance
            double sumDist = 0.0;
            Integer[] selected = new Integer[solution.getNumSelectedIDs()];
            solution.getSelectedIDs().toArray(selected);
            int n = selected.length;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    sumDist += distanceMeasure.getDistance(selected[i], selected[j], data);
                }
            }
            int numDist = n*(n-1)/2;
            value = sumDist/numDist;
        }
        return SimpleEvaluation.WITH_VALUE(value);
    }

    @Override
    public Evaluation evaluate(Move move, SubsetSolution curSolution, Evaluation curEvaluation, CoreHunterData data) {
        // check move type
        if (!(move instanceof SubsetMove)) {
            throw new IncompatibleDeltaEvaluationException(
                    "Entry-to-entry distance objective should be used in combination "
                  + "with neighbourhoods that generate moves of type SubsetMove."
            );
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;

        // get current evaluation
        double curEval = curEvaluation.getValue();
        // undo average to get sum of distances
        int n = curSolution.getNumSelectedIDs();
        int numDistances = n*(n-1)/2;
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
                sumDist -= distanceMeasure.getDistance(rem, ret, data);
                numDistances--;
            }
        }

        // subtract distances from removed to other removed items
        for (int rem1 : removed) {
            for (int rem2 : removed) {
                // account for each distinct pair only once
                if (rem1 < rem2) {
                    sumDist -= distanceMeasure.getDistance(rem1, rem2, data);
                    numDistances--;
                }
            }
        }

        // add distances from new items to retained items
        for (int add : added) {
            for (int ret : retained) {
                sumDist += distanceMeasure.getDistance(add, ret, data);
                numDistances++;
            }
        }

        // add distances from new items to other new items
        for (int add1 : added) {
            for (int add2 : added) {
                // account for each distinct pair only once
                if (add1 < add2) {
                    sumDist += distanceMeasure.getDistance(add1, add2, data);
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
