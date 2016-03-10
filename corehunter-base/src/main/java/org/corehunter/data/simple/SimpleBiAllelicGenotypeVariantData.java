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
import java.util.Arrays;

import org.corehunter.data.BiAllelicGenotypeVariantData;

import uno.informatics.common.io.FileType;
import uno.informatics.data.SimpleEntity;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleBiAllelicGenotypeVariantData extends SimpleNamedData
                                                implements BiAllelicGenotypeVariantData {

    private final Integer[][] alleleScores; // null element means missing value
    private final String[] markerNames;     // null element means no marker name assigned

    /**
     * Create data with given dataset name, item headers, marker names and allele scores.
     * The number of rows and columns of <code>alleleScores</code> indicates the number of
     * items and markers, respectively. All entries in this matrix should be 0, 1 or 2
     * (or <code>null</code> for missing values).
     * <p>
     * Item headers and marker names are optional. Missing names/headers are encoded as <code>null</code>.
     * Alternatively, if no item headers or no marker names are assigned, the respective array itself may
     * also be <code>null</code>. If not <code>null</code> the length of <code>itemHeaders</code> and
     * <code>markerNames</code> should be equal to the number of items and markers, respectively, as inferred
     * from the dimensions of <code>alleleScores</code>.
     * <p>
     * Violating any of the requirements will produce an exception.
     * <p>
     * Allele scores as well as assigned headers and names are copied into internal data structures,
     * i.e. no references are retained to any of the arrays passed as arguments.
     * 
     * @param datasetName name of the dataset
     * @param itemHeaders item headers, <code>null</code> if no headers are assigned; if not <code>null</code>
     *                    its length should equal the number of rows in <code>alleleScores</code>
     * @param markerNames marker names, <code>null</code> if no marker names are assigned; if not
     *                    <code>null</code> its length should equal the number of columns in <code>alleleScores</code>
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
                            "Unexpected value at row %d and column %d. Got: %d (allowed: 0, 1, 2, null).",
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
    public int getAlleleScore(int id, int markerIndex) {
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
     * ... TODO
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return biallelic genotype variant data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public static final SimpleBiAllelicGenotypeVariantData readData(Path filePath, FileType type) throws IOException {
        
        return null;

    }
    
}
