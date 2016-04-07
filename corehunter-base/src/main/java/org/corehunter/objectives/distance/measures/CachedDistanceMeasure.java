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

package org.corehunter.objectives.distance.measures;

import java.util.HashMap;
import java.util.Map;
import org.corehunter.data.CoreHunterData;
import org.corehunter.objectives.distance.DistanceMeasure;

/**
 * Caches pairwise distances upon computation.
 * 
 * @author Herman De Beukelaer
 */
public abstract class CachedDistanceMeasure implements DistanceMeasure {
    
    private final Map<CoreHunterData, Double[][]> cache;
    private final MissingValuesPolicy missingDataPolicy;
    
    public CachedDistanceMeasure() {
        this(MissingValuesPolicy.FLOOR);
    }
    
    public CachedDistanceMeasure(MissingValuesPolicy policy){
        cache = new HashMap<>();
        missingDataPolicy = policy;
    }
    
    /**
     * Retrieve a distance from the cache. If the distance is
     * not found in the cache it is first computed and stored.
     * 
     * @param idX id of the first item
     * @param idY id of the second item
     * @param data data from which the distance is computed
     * @return distance as computed or retrieved from the cache
     */
    @Override
    public double getDistance(int idX, int idY, CoreHunterData data){
        Double[][] distances = cache.get(data);
        if(distances != null && distances[idX][idY] != null){
            // return cached value
            return distances[idX][idY];
        } else {
            if(distances == null){
                int n = data.getSize();
                distances = new Double[n][n];
                cache.put(data, distances);
            }
            // compute, store and return
            double d = (idX != idY ? computeDistance(idX, idY, data, missingDataPolicy) : 0.0);
            distances[idX][idY] = d;
            distances[idY][idX] = d;
            return d;
        }
    }
    
    /**
     * Compute distance. This method is called when a distance is not found in the cache.
     * 
     * @param idX id of the first item
     * @param idY id of the second item
     * @param data data from which the distance is computed
     * @param missingDataPolicy determines the contribution of variables with missing values
     * @return distance between the two items with given id
     */
    protected abstract double computeDistance(int idX, int idY,
                                              CoreHunterData data,
                                              MissingValuesPolicy missingDataPolicy);
    
    protected double missingValueContribution(MissingValuesPolicy policy, double ceilValue){
        switch(policy){
            case FLOOR:
                return 0.0;
            case CEIL:
                return ceilValue;
            default:
                throw new RuntimeException(
                        "This should not happen: unexpected missing values policy " + policy
                );
        }
    }
    
}
