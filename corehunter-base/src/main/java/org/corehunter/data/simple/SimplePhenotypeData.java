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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.corehunter.data.PhenotypeData;
import org.jamesframework.core.subset.SubsetSolution;

import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.Feature;
import uno.informatics.data.Scale;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.feature.array.ArrayFeatureData;
import uno.informatics.data.io.FileType;
import uno.informatics.data.utils.DataOption;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimplePhenotypeData extends ArrayFeatureData implements PhenotypeData {

    private static final String ID_HEADER = "X";
    private static final String SELECTED_HEADER = "SELECTED";

    private static final long serialVersionUID = 1L;

    public SimplePhenotypeData(String name, Feature[] features, Object[][] values) {
        super(name, features, values);
    }

    public SimplePhenotypeData(String name, List<? extends Feature> features, List<List<Object>> values) {
        super(name, features, values);
    }

    public SimplePhenotypeData(String name, List<? extends Feature> features, List<SimpleEntity> rowHeaders,
        List<List<Object>> values) {
        super(name, features, rowHeaders, values);
    }

    public SimplePhenotypeData(String name, List<? extends Feature> features, SimpleEntity[] rowHeaders,
        Object[][] values) {
        super(name, features, rowHeaders, values);
    }

    public SimplePhenotypeData(String uniqueIdentifier, String name, Feature[] features, Object[][] values) {
        super(uniqueIdentifier, name, features, values);
    }

    public SimplePhenotypeData(String uniqueIdentifier, String name, List<? extends Feature> features,
        List<List<Object>> values) {
        super(uniqueIdentifier, name, features, values);
    }

    public SimplePhenotypeData(String uniqueIdentifier, String name, List<? extends Feature> features,
        List<SimpleEntity> rowHeaders, List<List<Object>> values) {
        super(uniqueIdentifier, name, features, rowHeaders, values);
    }

    public SimplePhenotypeData(String uniqueIdentifier, String name, List<? extends Feature> features,
        SimpleEntity[] rowHeaders, Object[][] values) {
        super(uniqueIdentifier, name, features, rowHeaders, values);
    }

    public SimplePhenotypeData(ArrayFeatureData data) {
        super(data.getUniqueIdentifier(), data.getName(), data.getFeatures(), data.getRowHeaders(),
            data.getValues());
    }

    /**
     * Write phenotype data to file. Only file types
     * {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * 
     * @param filePath
     *            path to file where the data will be written
     * @param fileType
     *            the type of data file
     * @param solution
     *            the solution to subset the data
     * @param includeId
     *            includes the integer id used by the solution
     * @param includeSelected
     *            includes selected accessions
     * @param includeUnselected
     *            includes unselected accessions
     * @throws IOException
     *             if the file can not be written
     */
    public void writeData(Path filePath, FileType fileType, SubsetSolution solution, boolean includeId,
        boolean includeSelected, boolean includeUnselected) throws IOException {

        // validate arguments

        if (filePath == null) {
            throw new IllegalArgumentException("File path not defined.");
        }

        if (filePath.toFile().exists()) {
            throw new IOException("File does  exist : " + filePath + ".");
        }

        if (fileType == null) {
            throw new IllegalArgumentException("File type not defined.");
        }

        if (fileType != FileType.TXT && fileType != FileType.CSV) {
            throw new IllegalArgumentException(
                String.format("Only file types TXT and CSV are supported. Got: %s.", fileType));
        }

        if (solution == null) {
            throw new NullPointerException("Solution must be defined");
        }

        if (!(solution.getAllIDs().equals(getIDs()))) {
            throw new IllegalArgumentException("Solution ids must match data.");
        }
        
        if(!includeSelected && !includeUnselected){
            throw new IllegalArgumentException(
                    "One of 'includeSelected' or 'includeUnselected' must be used."
            );
        }

        Files.createDirectories(filePath.getParent());

        // write header row
        boolean markSelection = includeSelected && includeUnselected;
        try (RowWriter writer = IOUtilities.createRowWriter(
                filePath, fileType, TextFileRowReader.REMOVE_WHITE_SPACE
        )) {
            
            // write internal integer id column header
            if (includeId) {
                writer.writeCell(ID_HEADER);
                writer.newColumn();
            }
            
            // write string id and name column headers
            writer.writeCell(ID);
            writer.newColumn();
            writer.writeCell(NAME);
            
            // write selection column header if both selected and unselected are included
            if (markSelection) {
                writer.newColumn();
                writer.writeCell(SELECTED_HEADER);
            }
            
            // write trait ids (data column headers)
            for(Feature feature : getFeatures()){
                writer.newColumn();
                writer.writeCell(feature.getUniqueIdentifier());
            }
            
            // write trait name row
            writer.newRow();
            
            if (includeId) {
                writer.newColumn();
            }
            
            writer.writeCell(NAME);
            writer.newColumn();
            
            if(markSelection){
                writer.newColumn();
            }
            
            for(Feature feature : getFeatures()){
                writer.newColumn();
                writer.writeCell(feature.getName());
            }
            
            // write trait types row
            writer.newRow();
            
            if (includeId) {
                writer.newColumn();
            }
            
            writer.writeCell(TYPE);
            writer.newColumn();
            
            if(markSelection){
                writer.newColumn();
            }
            
            for(Feature feature : getFeatures()){
                writer.newColumn();
                Scale scale = feature.getMethod().getScale();
                writer.writeCell(scale.getScaleType().getAbbreviation() + scale.getDataType().getAbbreviation());
            }
            
            // write trait min value row
            writer.newRow();
            
            if (includeId) {
                writer.newColumn();
            }
            
            writer.writeCell(MIN);
            writer.newColumn();
            
            if(markSelection){
                writer.newColumn();
            }
            
            for(Feature feature : getFeatures()){
                writer.newColumn();
                writer.writeCell(feature.getMethod().getScale().getMinimumValue());
            }
            
            // write trait max value row
            writer.newRow();
            
            if (includeId) {
                writer.newColumn();
            }
            
            writer.writeCell(MAX);
            writer.newColumn();
            
            if(markSelection){
                writer.newColumn();
            }
            
            for(Feature feature : getFeatures()){
                writer.newColumn();
                writer.writeCell(feature.getMethod().getScale().getMaximumValue());
            }

            // obtain sorted list of IDs included in output
            Set<Integer> includedIDs;
            if (markSelection) {
                includedIDs = getIDs();
            } else if (includeSelected) {
                includedIDs = solution.getSelectedIDs();
            } else if (includeUnselected) {
                includedIDs = solution.getUnselectedIDs();
            } else {
                throw new IllegalArgumentException(
                        "One of 'includeSelected' or 'includeUnselected' must be used."
                );
            }
            List<Integer> sortedIDs = new ArrayList<>(includedIDs);
            sortedIDs.sort(null);

            // write data rows
            Set<Integer> selected = solution.getSelectedIDs();
            for (int id : sortedIDs) {
                
                writer.newRow();
                
                // write integer id if requestd
                if (includeId) {
                    writer.writeCell(id);
                    writer.newColumn();
                }
                
                // write string id and name
                SimpleEntity header = getHeader(id);
                writer.writeCell(header.getUniqueIdentifier());
                writer.newColumn();
                writer.writeCell(header.getName());
                
                // mark selection if needed
                if(markSelection){
                    writer.newColumn();
                    writer.writeCell(selected.contains(id));
                }
                
                // write trait values
                writer.newColumn();
                writer.writeRowCells(getRow(id).getValues());
            }
        }
    }

    public static final SimplePhenotypeData readPhenotypeData(Path filePath, FileType type,
        DataOption... options) throws IOException {
        return new SimplePhenotypeData(ArrayFeatureData.readData(filePath, type, options));
    }
}
