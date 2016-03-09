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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.corehunter.data.NamedData;
import uno.informatics.data.SimpleEntity;

/**
 * Stores headers of n dataset entries which are assigned consecutive IDs from 0 to n-1.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleNamedData implements NamedData {

    // headers
    private final SimpleEntity[] headers;
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
        this("Named data", n);
    }
    
    /**
     * Initialize data with given dataset name. IDs are set to [0, n-1].
     * All names are set to <code>null</code>.
     * 
     * @param datasetName name of the dataset
     * @param n number of entries
     */
    public SimpleNamedData(String datasetName, int n) {
        this(datasetName, n, null);
    }
    
    /**
     * Initialize data with given dataset name and item headers. IDs are set to [0, n-1].
     * Missing item headers should be encoded as <code>null</code> values in the array.
     * Alternatively, if no headers are assigned to any items, <code>headers</code> itself
     * may also be <code>null</code>.
     * 
     * @param datasetName name of the dataset
     * @param n number of entries
     * @param headers item headers, <code>null</code> if no headers are assigned;
     *                if not <code>null</code> its length should equal <code>n</code>
     * @throws IllegalArgumentException if an incorrect number of names are specified
     */
    public SimpleNamedData(String datasetName, int n, SimpleEntity[] headers){
        ids = Collections.unmodifiableSet(
                IntStream.range(0, n).boxed().collect(Collectors.toSet())
        );
        this.datasetName = datasetName;
        if(headers == null){
            this.headers = new SimpleEntity[n];
        } else {
            if(headers.length != n){
                throw new IllegalArgumentException(String.format(
                        "Incorrect number of headers. Expected: %d, actual: %d.", n, headers.length
                ));
            }
            // check unique identifiers
            Set<String> identifiers = new HashSet<>();
            for(SimpleEntity header : headers){
                if(header != null){
                    String identifier = header.getUniqueIdentifier();
                    if(identifier != null && !identifiers.add(identifier)){
                        throw new IllegalArgumentException("Identifiers are not unique. "
                                + "Duplicate identifier: " + identifier + ".");
                    }
                }
            }
            this.headers = Arrays.copyOf(headers, n);
        }
    }
    
    @Override
    public SimpleEntity getHeader(int id) throws NoSuchElementException {
        validateId(id);
        return headers[id];
    }
    
    public void setHeader(int id, SimpleEntity header){
        validateId(id);
        // check unique identifier
        String identifier = header.getUniqueIdentifier();
        if(identifier != null && Arrays.stream(headers).anyMatch(h -> h.getUniqueIdentifier().equals(identifier))){
            throw new IllegalArgumentException("Identifier " + identifier + " already assigned");
        }
        headers[id] = header;
    }
    
    private void validateId(int id){
        if(id < 0 || id >= getDatasetSize()){
            throw new NoSuchElementException(String.format("There is no entry with ID %d.", id));
        }
    }

    @Override
    public Set<Integer> getIDs() {
        return ids;
    }

    @Override
    public int getDatasetSize() {
        return headers.length;
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
