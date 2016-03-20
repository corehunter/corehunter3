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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.corehunter.data.NamedData;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.pojo.DataPojo;

/**
 * Stores headers of n dataset entries which are assigned consecutive IDs from 0 to n-1.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleNamedData extends DataPojo implements NamedData {

    // headers
    private final SimpleEntity[] headers;
    // item IDs (0..n-1)
    private final Set<Integer> ids;
    
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
     * @param name name of the data
     * @param n number of entries
     */
    public SimpleNamedData(String name, int n) {
        this(name, n, null);
    }
    
    /**
     * Initialize data with given data name and item headers. IDs are set to [0, n-1].
     * 
     * @param name name of the data
     * @param n number of entries
     * @param headers item headers, <code>null</code> if no headers are assigned;
     *                if not <code>null</code> its length should equal <code>n</code>
     *                and each item should at least have a unique identifier (name is optional)
     * @throws IllegalArgumentException if an incorrect number of headers are specified
     *                                  or if unique identifiers are missing in one or
     *                                  more headers
     */
    public SimpleNamedData(String name, int n, SimpleEntity[] headers){
        super(UUID.randomUUID().toString(), name) ;
        
        ids = Collections.unmodifiableSet(
                IntStream.range(0, n).boxed().collect(Collectors.toSet())
        );
        if(headers == null){
            this.headers = null;
        } else {
            if(headers.length != n){
                throw new IllegalArgumentException(String.format(
                        "Incorrect number of headers. Expected: %d, actual: %d.", n, headers.length
                ));
            }
            // check unique identifiers
            Set<String> identifiers = new HashSet<>();
            for(int i = 0 ; i < n; i++){
                SimpleEntity header = headers[i];
                if(header == null || header.getUniqueIdentifier() == null){
                    throw new IllegalArgumentException(String.format(
                            "No identifier defined for item %d.", i
                    ));
                }
                if(!identifiers.add(header.getUniqueIdentifier())){
                    throw new IllegalArgumentException(String.format(
                            "Identifiers are not unique. Duplicate identifier %s for item %d.",
                            header.getUniqueIdentifier(), i
                    ));
                }
            }
            this.headers = Arrays.copyOf(headers, n);
        }
    }
    
    @Override
    public SimpleEntity getHeader(int id) {
        return headers == null ? null : headers[id];
    }

    @Override
    public Set<Integer> getIDs() {
        return ids;
    }

    @Override
    public int getDatasetSize() {
        return ids.size();
    }
}
