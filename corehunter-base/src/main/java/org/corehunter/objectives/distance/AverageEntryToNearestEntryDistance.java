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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.corehunter.data.CoreHunterData;
import org.corehunter.objectives.distance.eval.NearestEntry;
import org.corehunter.objectives.distance.eval.NearestEntryEvaluation;

import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

/**
 * Evaluates a core set by computing the average distance between each selected item and the closest other
 * selected item. This value is to be maximized. If less than two items are selected, the value is set to 0.0.
 * 
 * @author Herman De Beukelaer
 */
public class AverageEntryToNearestEntryDistance implements Objective<SubsetSolution, CoreHunterData> {

    private final DistanceMeasure distanceMeasure;

    public AverageEntryToNearestEntryDistance(DistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }
    
    @Override
    public NearestEntryEvaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        // initialize evaluation object
        NearestEntryEvaluation eval = new NearestEntryEvaluation();
        // find closest neighbour of each selected item
        Set<Integer> selected = solution.getSelectedIDs();
        for(int sel : selected){
            // find and register closest other selected item (if any)
            NearestEntry closest = findClosest(sel, selected, data);
            if(closest != null){
                eval.add(sel, closest);
            }
        }
        return eval;
    }
    
    @Override
    public NearestEntryEvaluation evaluate(Move move, SubsetSolution curSolution,
                                           Evaluation curEvaluation, CoreHunterData data){
        // check move type
        if (!(move instanceof SubsetMove)) {
            throw new IncompatibleDeltaEvaluationException(
                    "Entry-to-nearest-entry distance objective should be used in combination "
                  + "with neighbourhoods that generate moves of type SubsetMove."
            );
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;

        // cast evaluation (cannot fail as both evaluate methods return such evaluation object)
        NearestEntryEvaluation eval = (NearestEntryEvaluation) curEvaluation;
        // copy to initialize new evaluation
        NearestEntryEvaluation newEval = new NearestEntryEvaluation(eval);

        // get added and deleted IDs from move
        Set<Integer> added = subsetMove.getAddedIDs();
        Set<Integer> deleted = subsetMove.getDeletedIDs();
        // get current selection from solution
        Set<Integer> curSelection = curSolution.getSelectedIDs();
        // infer new selection
        List<Integer> newSelection = new ArrayList<>(curSelection);
        newSelection.addAll(added);
        newSelection.removeAll(deleted);

        // discard contribution of removed items
        for(int item : deleted){
            newEval.remove(item);
        }

        // update closest items in new selection
        for(int item : newSelection){
            NearestEntry curClosest = newEval.getClosest(item);
            if(curClosest == null){
                // case 1: previously unselected or no closest item set (less than two items were selected);
                //         search for closest item in new selection
                NearestEntry newClosest = findClosest(item, newSelection, data);
                // register, if any
                if(newClosest != null){
                    newEval.add(item, newClosest);
                }
            } else {
                // case 2: current closest item needs to be updated
                if(deleted.contains(curClosest.getId())){
                    // case 2A: current closest item removed, rescan entire new selection
                    NearestEntry newClosest = findClosest(item, newSelection, data);
                    // update, if any
                    if(newClosest != null){
                        newEval.update(item, newClosest);
                    } else {
                        // no closest item left (new selection consists of single item);
                        // discard contribution
                        newEval.remove(item);
                    }
                } else {
                    // case 2B: current closest item retained; only check if any newly
                    //          added item is closer
                    NearestEntry closestAddedItem = findClosest(item, added, data);
                    if(closestAddedItem != null
                        && closestAddedItem.getDistance() < curClosest.getDistance()){
                        // update closest item
                        newEval.update(item, closestAddedItem);
                    }
                }
            }
        }

        return newEval;
    }
    
    /**
     * Find the closest item in the given group to the given item.
     * 
     * @param itemId ID of the item
     * @param group IDs of other items
     * @param data Core Hunter data
     * @return id of and distance to the item from the group that is closest to the given item;
     *         <code>null</code> if the group does not contain any items other than the given item
     */
    private NearestEntry findClosest(int itemId, Collection<Integer> group, CoreHunterData data){
        double dist;
        Double minDist = Double.POSITIVE_INFINITY;
        Integer closest = null;
        for(int other : group){
            if(other != itemId){
                dist = distanceMeasure.getDistance(itemId, other, data);
                if(dist < minDist){
                    minDist = dist;
                    closest = other;
                }
            }
        }
        return closest != null ? new NearestEntry(closest, minDist) : null;
    }

    @Override
    public boolean isMinimizing() {
        return false;
    }

}
