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

package org.corehunter.data;

import uno.informatics.data.Data;

/**
 * A distance matrix that indicates how different each pair of entries is.
 * All values are between 0.0 and 1.0 where the former means that the specific
 * measure used to evaluate the distance can not differentiate between two entries.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public interface DistanceMatrixData extends Data {

    /**
     * Get the distance between two entries.
     *
     * @param idX the id of the first entry
     * @param idY the id of the second entry
     * @return the distance between two entries
     */
    public double getDistance(int idX, int idY);
    
}
