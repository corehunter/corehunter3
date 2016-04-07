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


import java.util.Collection;
import java.util.Set;
import org.corehunter.data.CoreHunterData;
import org.corehunter.objectives.distance.eval.NearestEntry;
import org.corehunter.objectives.distance.eval.NearestEntryEvaluation;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * Evaluates a core set by computing the average distance between each item (selected or unselected)
 * and the closest selected item. This value is to be minimized.  If no items are selected, the value
 * is set to {@link Double#POSITIVE_INFINITY}.
 * 
 * @author Herman De Beukelaer
 */
public class AverageAccessionToNearestEntryDistance implements Objective<SubsetSolution, CoreHunterData> {

    private final DistanceMeasure distanceMeasure;

    public AverageAccessionToNearestEntryDistance(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }
    
    @Override
    public NearestEntryEvaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        // initialize evaluation object (evaluate to infinity if no items are selected)
        NearestEntryEvaluation eval = new NearestEntryEvaluation(Double.POSITIVE_INFINITY);
        // find closest selected item for each accession
        Set<Integer> selected = solution.getSelectedIDs();
        Set<Integer> all = solution.getAllIDs();
        for(int item : all){
            // find and register closest selected item (if any)
            NearestEntry closest = findClosest(item, selected, data);
            if(closest != null){
                eval.add(item, closest);
            }
        }
        return eval;
    }
    
    /**
     * Find the item in the given group that is closest to the given item.
     * The closest item is allowed to be the same as the given item.
     * 
     * @param itemId ID of an item
     * @param group IDs of group of items
     * @param data Core Hunter data
     * @return id of and distance to the item from the group that is closest to the given item;
     *         <code>null</code> if the group is empty
     */
    private NearestEntry findClosest(int itemId, Collection<Integer> group, CoreHunterData data){
        double dist;
        Double minDist = Double.POSITIVE_INFINITY;
        Integer closest = null;
        for(int groupMember : group){
            dist = distanceMeasure.getDistance(itemId, groupMember, data);
            if(dist < minDist){
                minDist = dist;
                closest = groupMember;
            }
        }
        return closest != null ? new NearestEntry(closest, minDist) : null;
    }

    @Override
    public boolean isMinimizing() {
        return true;
    }

}
