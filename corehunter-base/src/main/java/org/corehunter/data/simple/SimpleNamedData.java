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

package org.corehunter.data.simple;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.corehunter.data.NamedData;

/**
 * Stores names of n dataset entries which are assigned consecutive IDs from 0 to n-1.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleNamedData implements NamedData {

    // item names
    private final String[] names;
    // item IDs (0..n-1)
    private final Set<Integer> ids;
    
    // dataset name (e.g. name of file from which data was read)
    private final String datasetName;
    
    /**
     * Initialize data. IDs are set to [0, n-1].
     * All names are set to <code>null</code>.
     * 
     * @param n number of entries
     */
    public SimpleNamedData(int n) {
        this(n, "Named data");
    }
    
    /**
     * Initialize data with given names. IDs are set to [0, n-1] where n is the number of names.
     * Names are copied into an internal array.
     * 
     * @param names entry names
     */
    public SimpleNamedData(String[] names) {
        this(names.length);
        // copy names
        System.arraycopy(names, 0, this.names, 0, names.length);
    }
    
    /**
     * Initialize data. IDs are set to [0, n-1].
     * All names are set to <code>null</code>.
     * 
     * @param n number of entries
     * @param datasetName name of the dataset
     */
    public SimpleNamedData(int n, String datasetName) {
        names = new String[n];
        ids = Collections.unmodifiableSet(
                IntStream.range(0, n).boxed().collect(Collectors.toSet())
        );
        this.datasetName = datasetName;
    }
    
    /**
     * Initialize data with given names. IDs are set to [0, n-1] where n is the number of names.
     * Names are copied to an internal data structure.
     * 
     * @param names entry names
     * @param datasetName dataset name
     */
    public SimpleNamedData(String[] names, String datasetName) {
        this(names.length, datasetName);
        // copy names
        System.arraycopy(names, 0, this.names, 0, names.length);
    }
    
    @Override
    public String getName(int id) throws NoSuchElementException {
        validateId(id);
        return names[id];
    }
    
    @Override
    public void setName(int id, String name){
        validateId(id);
        names[id] = name;
    }
    
    private void validateId(int id){
        if(id < 0 || id >= names.length){
            throw new NoSuchElementException(String.format("There is no entry with ID %d.", id));
        }
    }

    @Override
    public Set<Integer> getIDs() {
        return ids;
    }

    @Override
    public int getDatasetSize() {
        return names.length;
    }
    
    /**
     * Get the name of the dataset as specified at construction.
     * 
     * @return dataset name
     */
    public String getDatasetName() {
        return datasetName;
    }

}
