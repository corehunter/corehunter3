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
 * Evaluates a core set by computing the average distance between
 * each selected item and the closest other selected item.
 * If less than two items are selected, 0.0 is returned.
 * 
 * @author Herman De Beukelaer
 */
public class AverageEntryToNearestEntryDistance implements Objective<SubsetSolution, CoreHunterData> {

    private final DistanceMeasure distanceMeasure;

    public AverageEntryToNearestEntryDistance(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }
    
    // TODO efficient delta evaluation
    @Override
    public Evaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        double value = 0.0;
        if (solution.getNumSelectedIDs() >= 2) {
            Set<Integer> selected = solution.getSelectedIDs();
            value = selected.stream().mapToDouble(idX -> {
                double minDist = Double.POSITIVE_INFINITY;
                for(int idY : selected){
                    double dist = distanceMeasure.getDistance(idX, idY, data);
                    if(idY != idX && dist < minDist){
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
        return false;
    }

}
