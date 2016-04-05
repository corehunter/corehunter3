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


import java.util.Set;
import org.corehunter.data.CoreHunterData;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * Evaluates a core set by computing the average distance between each unselected item and the closest selected item.
 * This value is to be minimized. If all items are selected, the value is set to 0.0. If no items are selected, the
 * value is set to {@link Double#POSITIVE_INFINITY}.
 * 
 * @author Herman De Beukelaer
 */
public class AverageAccessionToNearestEntryDistance implements Objective<SubsetSolution, CoreHunterData> {

    private final DistanceMeasure distanceMeasure;

    public AverageAccessionToNearestEntryDistance(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }
    
    // TODO efficient delta evaluation
    @Override
    public Evaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        double value;
        if (solution.getNumUnselectedIDs() == 0){
            // all items selected
            value = 0.0;
        } else if (solution.getNumSelectedIDs() == 0){
            // no items selected
            value = Double.POSITIVE_INFINITY;
        } else {
            // at least one selected, at least one unselected
            Set<Integer> unselected = solution.getUnselectedIDs();
            Set<Integer> selected = solution.getSelectedIDs();
            value = unselected.stream().mapToDouble(unsel -> {
                double minDist = Double.POSITIVE_INFINITY;
                for(int sel : selected){
                    double dist = distanceMeasure.getDistance(unsel, sel, data);
                    if(sel != unsel && dist < minDist){
                        minDist = dist;
                    }
                }
                return minDist;
            }).average().getAsDouble();
        }
        return SimpleEvaluation.WITH_VALUE(value);
    }

    @Override
    public boolean isMinimizing() {
        return true;
    }

}
