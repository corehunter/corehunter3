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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.corehunter.data.PhenotypicTraitData;
import uno.informatics.common.io.FileType;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.feature.array.ArrayFeatureDataset;

/**
 * @author Herman De Beukelaer
 */
public class SimplePhenotypicTraitData extends SimpleNamedData implements PhenotypicTraitData {

    private final FeatureData data;
    
    /**
     * Create dataset.
     * 
     * @param data feature data
     */
    public SimplePhenotypicTraitData(FeatureData data){
        super(data.getName(), data.getRowCount());
        this.data = data;
    }
    
    @Override
    public FeatureData getData() {
        return data;
    }

    @Override
    public SimpleEntity getHeader(int id) {
        return data.getRow(id).getHeader();
    } 
    
    /**
     * Read phenotypic trait data from file. Only file types {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * Relays to {@link ArrayFeatureDataset#readFeatureDatasetFromTextFile(File, FileType)}. The dataset name is set to
     * the name of the file to which <code>filePath</code> points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return genotype variant data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public static final SimplePhenotypicTraitData readData(Path filePath, FileType type) throws IOException {
        
        // validate arguments
        
        if (filePath == null) {
            throw new IllegalArgumentException("File path not defined.");
        }
        
        if(!filePath.toFile().exists()){
            throw new IOException("File does not exist : " + filePath + ".");
        }

        if(type == null){
            throw new IllegalArgumentException("File type not defined.");
        }
        
        if(type != FileType.TXT && type != FileType.CSV){
            throw new IllegalArgumentException(
                    String.format("Only file types TXT and CSV are supported. Got: %s.", type)
            );
        }
        
        // read data from file
        try {
            
            FeatureData data = ArrayFeatureDataset.readFeatureDatasetFromTextFile(filePath.toFile(), type);
            
            if(data == null){
                throw new IOException("Cannot read file. File may be empty.");
            }
            
            // check unique identifiers
            Set<String> uniqueIds = new HashSet<>();
            for(int i = 0; i < data.getRowCount(); i++){
                SimpleEntity header = data.getRow(i).getHeader();                    
                if(header != null && !uniqueIds.add(header.getUniqueIdentifier())){
                    throw new IOException(String.format(
                            "Duplicate name/id %s for item %d. "
                          + "Names should either be unique or complemented with unique identifiers.",
                            header.getUniqueIdentifier(), i
                    ));
                }
            }
            // check for missing identifiers
            if(!uniqueIds.isEmpty() && uniqueIds.size() < data.getRowCount()){
                throw new IOException("Missing names/ids. Unique identifier is "
                                    + "required for items whose name is not defined.");
            }

            return new SimplePhenotypicTraitData(data);
            
        } catch (DatasetException | IllegalArgumentException ex ){
            // convert to IO exception
            throw new IOException(ex.getMessage());
        }
        
    }

}
