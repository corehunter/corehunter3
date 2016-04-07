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

package org.corehunter.objectives.distance.eval;

import java.util.HashMap;
import java.util.Map;
import org.corehunter.objectives.distance.AverageAccessionToNearestEntryDistance;
import org.corehunter.objectives.distance.AverageEntryToNearestEntryDistance;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;

/**
 * Stores metadata for efficient delta evaluation in {@link AverageEntryToNearestEntryDistance} and
 * {@link AverageAccessionToNearestEntryDistance}.
 * 
 * @author Herman De Beukelaer
 */
public class NearestEntryEvaluation implements Evaluation {
    
    // maps items to closest entries (IDs) and the corresponding distance
    private final Map<Integer, NearestEntry> nearestEntryMap;

    // sum of distances from items to respective closest entries
    private double minDistSum;

    public NearestEntryEvaluation() {
        nearestEntryMap = new HashMap<>();
        minDistSum = 0.0;
    }

    /**
     * Deep copy constructor
     * 
     * @param toCopy evaluation to copy
     */
    public NearestEntryEvaluation(NearestEntryEvaluation toCopy){
        nearestEntryMap = new HashMap<>(toCopy.nearestEntryMap);
        minDistSum = toCopy.minDistSum;
    }

    /**
     * Register the closest entry of a given item.
     * 
     * @param nearestEntry id of and distance to the closest (other) selected item
     * @param distance distance between both items
     */
    public void add(int itemId, NearestEntry nearestEntry){
        // update minimum distance sum
        minDistSum += nearestEntry.getDistance();
        // update metadata
        nearestEntryMap.put(itemId, nearestEntry);
    }

    /**
     * Remove item and the registered closest entry (if any).
     * 
     * @param itemId id of the item
     * @return <code>true</code> if the item had been registered and is now removed
     */
    public boolean remove(int itemId){
        if(nearestEntryMap.containsKey(itemId)){
            // update minimum distance sum
            minDistSum -= nearestEntryMap.get(itemId).getDistance();
            // update metadata
            nearestEntryMap.remove(itemId);
            return true;
        }
        return false;
    }

    /**
     * Update the closest entry of a previously registered item.
     * 
     * @param nearestEntry id of and distance to the closest (other) selected item
     * @param distance distance between both items
     * @return <code>true</code> if the item had been registered and is now updated
     */
    public boolean update(int itemId, NearestEntry nearestEntry){
        if(nearestEntryMap.containsKey(itemId)){
            // update minimum distance sum
            minDistSum -= nearestEntryMap.get(itemId).getDistance();
            minDistSum += nearestEntry.getDistance();
            // update metadata
            nearestEntryMap.put(itemId, nearestEntry);
            return true;
        }
        return false;
    }

    /**
     * Get the id of and distance to the closest selected item for an item with given id.
     * 
     * @param itemId id of the item
     * @return id of and distance to closest selected item;
     *         <code>null</code> if no nearest entry has been registered
     */
    public NearestEntry getClosest(int itemId){
        return nearestEntryMap.get(itemId);
    }

    /**
     * Compute average distance from each registered item to closest selected item.
     * 
     * @return average distance; 0.0 if no items have been registered
     */
    @Override
    public double getValue() {
        int n = nearestEntryMap.size();
        return n > 0 ? minDistSum/n : 0.0;
    }
    
}
