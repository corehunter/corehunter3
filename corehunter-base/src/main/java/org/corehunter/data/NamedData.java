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

import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;

import uno.informatics.data.Data;
import uno.informatics.data.SimpleEntity;

/**
 * Extends {@link IntegerIdentifiedData} by optionally assigning a header to each entry.
 * A header consists of a name and/or unique identifier.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public interface NamedData extends Data, IntegerIdentifiedData {

    /**
     * Get the header of an entry by id.
     *
     * @param id of an entry, should be within the range from 0 to n-1, where n is the number of
     *           entries as returned by {@link #getDatasetSize()}
     * @return the header of an entry by id, <code>null</code> if no header is assigned to this entry
     */
    public SimpleEntity getHeader(int id);
    
    /**
     * Get the number of entries in the dataset.
     * 
     * @return dataset size
     */
    public int getDatasetSize();
    
}
