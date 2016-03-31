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

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.simple.CoreHunterData;
import org.corehunter.exceptions.CoreHunterException;

/**
 * @author Herman De Beukelaer
 */
public class PrecomputedDistance implements DistanceMeasure {

    @Override
    public double getDistance(int idX, int idY, CoreHunterData data) {
        
        DistanceMatrixData dist = data.getDistances();
        
        if(dist == null){
            throw new CoreHunterException("No precomputed distance matrix has been defined.");
        }
        
        return dist.getDistance(idX, idY);
        
    }

}
