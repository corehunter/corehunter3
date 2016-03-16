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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.corehunter.data.BiAllelicGenotypeVariantData;
import org.corehunter.util.StringUtils;

import uno.informatics.common.io.FileType;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleBiAllelicGenotypeVariantData extends SimpleNamedData
                                                implements BiAllelicGenotypeVariantData {

    private static final int UNDEFINED_COLUMN = -1;
    private static final String NAMES_HEADER = "NAME";
    private static final String IDENTIFIERS_HEADER = "ID";
    
    private final Integer[][] alleleScores; // null element means missing value
    private final String[] markerNames;     // null element means no marker name assigned

    /**
     * Create data with name "Biallelic marker data". For details of the arguments see 
     * {@link #SimpleBiAllelicGenotypeVariantData(String, SimpleEntity[], String[], Integer[][])}.
     * 
     * @param itemHeaders item headers (include name and/or unique identifier)
     * @param markerNames marker names
     * @param alleleScores 0/1/2 allele score matrix
     */
    public SimpleBiAllelicGenotypeVariantData(SimpleEntity[] itemHeaders, String[] markerNames,
                                              Integer[][] alleleScores) {
        this("Biallelic marker data", itemHeaders, markerNames, alleleScores);
    }
    
    /**
     * Create data with given dataset name, item headers, marker names and allele scores.
     * The number of rows and columns of <code>alleleScores</code> indicates the number of
     * items and markers, respectively. All entries in this matrix should be 0, 1 or 2
     * (or <code>null</code> for missing values).
     * <p>
     * Item headers and marker names are optional. Both arguments can be <code>null</code>.
     * If marker names are given they need not be defined for all markers nor unique.
     * If item headers are specified each item should at least have a unique identifier
     * (names are optional). If not <code>null</code> the length of <code>itemHeaders</code> and
     * <code>markerNames</code> should be equal to the number of items and markers, respectively,
     * as inferred from the dimensions of <code>alleleScores</code>.
     * <p>
     * Violating any of the requirements will produce an exception.
     * <p>
     * Allele scores as well as assigned headers and names are copied into internal data structures,
     * i.e. no references are retained to any of the arrays passed as arguments.
     * 
     * @param datasetName name of the dataset
     * @param itemHeaders item headers, <code>null</code> if no headers are assigned; if not <code>null</code>
     *                    its length should equal the number of rows in <code>alleleScores</code> and each item
     *                    should at least have a unique identifier
     * @param markerNames marker names, <code>null</code> if no marker names are assigned; if not
     *                    <code>null</code> its length should equal the number of columns in
     *                    <code>alleleScores</code>; can contain <code>null</code> values for
     *                    markers whose name is undefined
     * @param alleleScores allele scores, may not be <code>null</code> but can contain <code>null</code>
     *                     values (missing); dimensions indicate number of items (rows) and markers (columns)
     */
    public SimpleBiAllelicGenotypeVariantData(String datasetName, SimpleEntity[] itemHeaders,
                                              String[] markerNames, Integer[][] alleleScores) {
        
        // pass dataset name, size and item headers to parent
        super(datasetName, alleleScores.length, itemHeaders);
        
        // check allele scores and infer number of items/markers
        int n = alleleScores.length;
        int m = -1;
        if(n == 0){
            throw new IllegalArgumentException("No data (zero rows).");
        }
        for(int i = 0; i < n; i++){
            Integer[] geno = alleleScores[i];
            // check: genotype defined
            if(geno == null){
                throw new IllegalArgumentException(String.format(
                        "Allele scores not defined for item %d.", i
                ));
            }
            // set/check number of markers
            if(m == -1){
                m = geno.length;
                if(m == 0){
                    throw new IllegalArgumentException("No markers (zero columns)");
                }
            } else if (geno.length != m){
                throw new IllegalArgumentException(String.format(
                        "Incorrect number of markers for item %d. Expected: %d, actual: %d.",
                        i, m, geno.length
                ));
            }
            // check values
            for(int j = 0; j < m; j++){
                if(geno[j] != null && (geno[j] < 0 || geno[j] > 2)){
                    throw new IllegalArgumentException(String.format(
                            "Unexpected value at data row %d and data column %d. Got: %d (allowed: 0, 1, 2).",
                            i, j, geno[j]
                    ));
                }
            }
        }
        // copy allele scores
        this.alleleScores = new Integer[n][m];
        for(int i = 0; i < n; i++){
            this.alleleScores[i] = Arrays.copyOf(alleleScores[i], m);
        }
        
        // check and copy marker names
        if(markerNames == null){
            this.markerNames = new String[m];
        } else {
            if(markerNames.length != m){
                throw new IllegalArgumentException(String.format(
                        "Incorrect number of marker names provided. Expected: %d, actual: %d.",
                        m, markerNames.length
                ));
            }
            this.markerNames = Arrays.copyOf(markerNames, m);
        }
        
    }
    
    @Override
    public int getNumberOfMarkers() {
        return alleleScores[0].length;
    }
    
    @Override
    public String getMarkerName(int markerIndex) {
        return markerNames[markerIndex];
    }
    
    @Override
    public Integer getAlleleScore(int id, int markerIndex) {
        return alleleScores[id][markerIndex];
    }
    
    /**
     * Returns 2 for each marker.
     * 
     * @param markerIndex marker index; ignored unless it falls outside the valid range
     * @return 2
     * @throws ArrayIndexOutOfBoundsException if <code>markerIndex</code> falls outsize the valid
     *                                        range 0..m-1 with m the number of markers
     */
    @Override
    public int getNumberOfAlleles(int markerIndex) {
        validateMarkerIndex(markerIndex);
        return 2;
    }

    /**
     * Since each marker has two alleles this method returns twice the number of markers.
     * 
     * @return twice the number of markers
     */
    @Override
    public int getTotalNumberOfAlleles() {
        return 2 * getNumberOfMarkers();
    }

    /**
     * The name of each allele is simply a string version of the allele index (0/1). 
     * 
     * @param markerIndex marker index; ignored unless it falls outside the valid range
     * @param alleleIndex allele index; 0 or 1
     * @return "0" or "1" depending on the allele index
     * @throws ArrayIndexOutOfBoundsException if <code>markerIndex</code> falls outsize the valid
     *                                        range 0..m-1 with m the number of markers, or if
     *                                        <code>alleleIndex</code> is not 0 or 1
     */
    @Override
    public String getAlleleName(int markerIndex, int alleleIndex) {
        validateMarkerIndex(markerIndex);
        validateAlleleIndex(alleleIndex);
        return "" + alleleIndex;
    }

    /**
     * Computes the the allele frequency at a certain marker in a certain individual.
     * For allele 1, the frequency <code>f</code> is defined as the allele score divided by two.
     * The frequency of allele 0 equals <code>1.0 - f</code>.
     * 
     * @param id item id within 0..n-1 with n the number of items
     * @param markerIndex marker index within 0..m-1 with m the number of markers
     * @param alleleIndex allele index; 0 or 1
     * @return allele frequency; 0.0, 0.5 or 1.0 (or <code>null</code> if allele score is missing)
     * @throws ArrayIndexOutOfBoundsException if the id or marker/allele index is out of range
     */
    @Override
    public Double getAlleleFrequency(int id, int markerIndex, int alleleIndex) {
        
        validateAlleleIndex(alleleIndex);
        Integer a = alleleScores[id][markerIndex];
        
        if(a == null){
            return null;
        }
        
        double f = ((double) a) / 2.0;
        return alleleIndex == 1 ? f : 1.0 - f;
        
    }
    
    private void validateMarkerIndex(int markerIndex){
        if(markerIndex < 0 || markerIndex >= getNumberOfMarkers()){
            throw new ArrayIndexOutOfBoundsException("Invalid marker index: " + markerIndex + ".");
        }
    }
    
    private void validateAlleleIndex(int alleleIndex){
        if(alleleIndex < 0 || alleleIndex >= 2){
            throw new ArrayIndexOutOfBoundsException("Invalid allele index: " + alleleIndex + ".");
        }
    }

    /**
     * Read biallelic genotype variant data from file. Only file types {@link FileType#TXT} and {@link FileType#CSV}
     * are allowed. Values are separated with a single tab (txt) or comma (csv) character. The file contains an
     * allele score matrix with one row per individual and one column per marker. Only values 0, 1 and 2 are valid.
     * Empty cells are also allowed in case of missing data.
     * <p>
     * The file starts with a compulsory header row specifying the marker names. Although this row is required
     * some or all names may be undefined by leaving the corresponding cells blank. Depending on the number of
     * header columns (if any, see below) some additional column header cells may be prepended to the header row.
     * <p>
     * Two optional (leftmost) header columns can be included to specify individual names and/or unique identifiers.
     * The former is identified with column header "NAME", the latter with column header "ID". The column headers should
     * be placed in the corresponding cell of the header row. If only names are specified they should be defined for
     * all individuals and unique. Else, additional unique identifiers are required at least for those individuals
     * whose name is undefined or not unique.
     * <p>
     * Leading and trailing whitespace is removed from names and unique identifiers and they are unquoted if wrapped
     * in single or double quotes after whitespace removal. If it is intended to start or end a name/identifier with
     * whitespace this whitespace should be contained within the quotes, as it will then not be removed.
     * <p>
     * Trailing empty cells can be omitted at any row in the file.
     * <p>
     * The dataset name is set to the name of the file to which <code>filePath</code> points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return biallelic genotype variant data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public static final SimpleBiAllelicGenotypeVariantData readData(Path filePath, FileType type) throws IOException {
        
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
        try(RowReader reader = IOUtilities.createRowReader(
                filePath.toFile(), type, TextFileRowReader.REMOVE_WHITE_SPACE
        )){
            
            if (reader == null || !reader.ready()) {
                throw new IOException("Can not create reader for file " + filePath + ". File may be empty.");
            }
            
            if(!reader.hasNextRow()){
                throw new IOException("File is empty.");
            }
            
            // read all data
            List<String[]> rows = new ArrayList<>();
            while(reader.nextRow()){
                rows.add(reader.getRowCellsAsStringArray());
            }
            int n = rows.size()-1;
            
            if(n < 0){
                throw new IOException("File is empty.");
            }
            if(n == 0){
                throw new IOException("No data rows.");
            }
            
            // infer number of columns
            int numCols = rows.stream().mapToInt(row -> row.length).max().getAsInt();
            // extend rows with null values where needed
            for(int r = 0; r <= n; r++){
                String[] row = rows.get(r);
                if(row.length < numCols){
                    row = Arrays.copyOf(row, numCols);
                }
                rows.set(r, row);
            }
            
            // 1: extract marker names
            
            // unquote header row
            String[] headerRow = StringUtils.unquote(rows.get(0));
            // check for presence of header columns
            int itemNameColumn = UNDEFINED_COLUMN;
            int itemIdentifierColumn = UNDEFINED_COLUMN;
            int numHeaderCols = 0;
            while(numHeaderCols < 2 && numHeaderCols < headerRow.length
                    && (Objects.equals(headerRow[numHeaderCols], NAMES_HEADER)
                        || Objects.equals(headerRow[numHeaderCols], IDENTIFIERS_HEADER))){
                if(headerRow[numHeaderCols].equals(NAMES_HEADER)){
                    if(itemNameColumn == UNDEFINED_COLUMN){
                        itemNameColumn = numHeaderCols;
                    } else {
                        throw new IOException(String.format(
                                "Duplicate %s column.", NAMES_HEADER
                        ));
                    }
                } else {
                    if(itemIdentifierColumn == UNDEFINED_COLUMN){
                        itemIdentifierColumn = numHeaderCols;
                    } else {
                        throw new IOException(String.format(
                                "Duplicate %s column.", IDENTIFIERS_HEADER
                        ));
                    }
                }
                numHeaderCols++;
            }
            int m = numCols - numHeaderCols;
            if(m == 0){
                throw new IOException("No data columns.");
            }
            // store marker names
            String[] markerNames = Arrays.copyOfRange(headerRow, numHeaderCols, headerRow.length);
            
            // 2: extract item names, identifiers and alelle scores
            
            String[] itemNames = (itemNameColumn == UNDEFINED_COLUMN ? null : new String[n]);
            String[] itemIdentifiers = (itemIdentifierColumn == UNDEFINED_COLUMN ? null : new String[n]);
            Integer[][] alleleScores = new Integer[n][m];
            for(int i = 0; i < n; i++){
                
                String[] row = rows.get(i+1);
                
                // extract item name and/or identifier
                for(int c = 0; c < numHeaderCols; c++){
                    String nameOrId = StringUtils.unquote(row[c]);
                    if(itemNameColumn == c){
                        itemNames[i] = nameOrId;
                    } else {
                        itemIdentifiers[i] = nameOrId;
                    }
                }
                
                // extract allele scores
                for(int j = 0; j < m; j++){
                    String s = row[numHeaderCols + j];
                    try {
                        alleleScores[i][j] = (s == null ? null : Integer.parseInt(s.trim()));
                    } catch (NumberFormatException ex){
                        // wrap in IO exception
                        throw new IOException(String.format(
                                "Invalid allele score at row %d, column %d. Expected integer value 0/1/2, got: \"%s\".",
                                i+1, numHeaderCols + j, s
                        ), ex);
                    }
                }
                
            }
            
            // combine names and identifiers in headers
            SimpleEntity[] headers = null;
            if(itemIdentifiers != null || itemNames != null){
                headers = new SimpleEntity[n];
                for(int i = 0; i < n; i++){
                    String name = itemNames != null ? itemNames[i] : null;
                    String identifier = itemIdentifiers != null ? itemIdentifiers[i] : null;
                    if(identifier == null){
                        headers[i] = new SimpleEntityPojo(name);
                    } else {
                        headers[i] = new SimpleEntityPojo(identifier, name);
                    }
                }
            }
            
            try{
                // create data
                return new SimpleBiAllelicGenotypeVariantData(filePath.getFileName().toString(),
                                                              headers, markerNames, alleleScores);
            } catch(IllegalArgumentException ex){
                // convert to IO exception
                throw new IOException(ex.getMessage());
            }
            
        }

    }
    
}
