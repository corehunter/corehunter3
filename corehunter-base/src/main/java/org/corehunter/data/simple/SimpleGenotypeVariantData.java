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

import static uno.informatics.common.Constants.UNKNOWN_INDEX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import uno.informatics.common.io.FileProperties;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import org.corehunter.data.GenotypeVariantData;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleGenotypeVariantData extends SimpleNamedData implements GenotypeVariantData {

    private static final double DELTA = 1e-10;

    private final Double[][][] alleleFrequencies;   // null element means missing value
    private final int numberOfMarkers;
    private final int[] numberOfAllelesForMarker;
    private final int totalNumberAlleles;
    private final String[] markerNames;             // null element means no marker name assigned
    private final String[][] alleleNames;           // null element means no allele name assigned

    /**
     * Create data with name "Multi-allelic marker data".
     * For details of the arguments see 
     * {@link #SimpleGenotypeVariantData(String, String[], String[], String[][], Double[][][])}.
     * 
     * @param itemNames item names
     * @param markerNames marker names
     * @param alleleNames allele names per marker
     * @param alleleFrequencies allele frequencies
     */
    public SimpleGenotypeVariantData(String[] itemNames, String[] markerNames, String[][] alleleNames,
                                     Double[][][] alleleFrequencies) {
        this("Multi-allelic marker data", itemNames, markerNames, alleleNames, alleleFrequencies);
    }
    
    /**
     * Create data with given dataset name, item names, marker/allele names and allele frequencies.
     * The length of <code>alleleFrequencies</code> denotes the number of items in
     * the dataset. The length of <code>alleleFrequencies[i]</code> should be the same
     * for all <code>i</code> and denotes the number of markers. Finally, the length of
     * <code>alleleFrequencies[i][m]</code> should also be the same for all <code>i</code>
     * and denotes the number of alleles of the m-th marker. Allele counts may differ for
     * different markers.
     * <p>
     * All frequencies should be positive and the values in <code>alleleFrequencies[i][m]</code> should
     * sum to one for all <code>i</code> and <code>m</code>. Missing values are encoded as <code>null</code>.
     * If one or more allele frequencies are missing at a certain marker for a certain individual, the
     * remaining frequencies should sum to a value less than or equal to one.
     * <p>
     * Names (items, markers, alleles) are optional. Missing names are encoded as <code>null</code>.
     * Alternatively, if no item, marker or allele names are assigned, the respective array argument
     * itself may also be <code>null</code>. If not <code>null</code> the length of each name array
     * should correspond to the dimensions of <code>alleleFrequencies</code> (number of individuals,
     * markers and alleles per marker).
     * <p>
     * Violating any of the requirements will produce an exception.
     * <p>
     * Allele frequencies as well as any assigned names are copied into internal data structures,
     * i.e. no references are retained to any of the arrays passed as arguments.
     * 
     * @param datasetName name of the dataset
     * @param itemNames item names, <code>null</code> if no names are assigned; if not <code>null</code>
     *                  its length should correspond to the number of individuals
     * @param markerNames marker names, <code>null</code> if no marker names are assigned; if not
     *                    <code>null</code> its length should correspond to the number of markers
     * @param alleleNames allele names per marker, <code>null</code> if no allele names are assigned;
     *                    if not <code>null</code> the length of <code>alleleNames</code> should correspond
     *                    to the number of markers and the length of <code>alleleNames[m]</code> to the number
     *                    of alleles of the m-th marker
     * @param alleleFrequencies allele frequencies, may not be <code>null</code>; dimensions indicate number of
     *                          individuals, markers and alleles per marker
     */
    public SimpleGenotypeVariantData(String datasetName, String[] itemNames, String[] markerNames,
                                     String[][] alleleNames, Double[][][] alleleFrequencies) {
        
        // pass dataset name, size and item names to parent
        super(datasetName, alleleFrequencies.length, itemNames);

        // check allele frequencies and infer number of individuals, markers and alleles per marker
        if (alleleFrequencies == null) {
            throw new IllegalArgumentException("Allele frequency entries not defined.");
        }
        int n = alleleFrequencies.length;
        int m = -1;
        int[] a = null;
        // individuals
        for(int i = 0; i < n; i++){
            Double[][] indFreqs = alleleFrequencies[i];
            if(indFreqs == null){
                throw new IllegalArgumentException("Allele frequencies not defined for individual " + i);
            }
            if(m == -1){
                m = indFreqs.length;
                a = new int[m];
                Arrays.fill(a, -1);
            } else if (indFreqs.length != m) {
                throw new IllegalArgumentException("All individuals should have same number of markers.");
            }
            // markers
            for(int j = 0; j < m; j++){
                Double[] alleleFreqs = indFreqs[j];
                if(alleleFreqs == null){
                    throw new IllegalArgumentException(String.format(
                            "Allele frequencies not defined for individual %d at marker %d.", i, j
                    ));
                }
                if(a[j] == -1){
                    a[j] = alleleFreqs.length;
                } else if (alleleFreqs.length != a[j]){
                    throw new IllegalArgumentException(
                            "Number of alleles per marker should be consistent across all individuals."
                    );
                }
                // alleles
                if(Arrays.stream(alleleFreqs).filter(Objects::nonNull).anyMatch(f -> f < 0.0)){
                    throw new IllegalArgumentException("All frequencies should be positive.");
                }
                double sum = Arrays.stream(alleleFreqs)
                                   .filter(Objects::nonNull)
                                   .mapToDouble(Double::doubleValue)
                                   .sum();
                // sum should not exceed 1.0
                if(sum > 1.0){
                    throw new IllegalArgumentException("Allele frequency sum for marker should not exceed one.");
                }
                if(Arrays.stream(alleleFreqs).noneMatch(Objects::isNull)){
                    // no missing values: should sum to 1.0
                    if(1.0 - sum > DELTA){
                        throw new IllegalArgumentException("Allele frequencies for marker should sum to one.");
                    }
                }
            }
        }
        numberOfMarkers = m;
        numberOfAllelesForMarker = a;
        
        // set total number of alleles
        totalNumberAlleles = Arrays.stream(numberOfAllelesForMarker).sum();
        
        // copy allele frequencies
        this.alleleFrequencies = new Double[n][m][];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                this.alleleFrequencies[i][j] = Arrays.copyOf(
                        alleleFrequencies[i][j], numberOfAllelesForMarker[j]
                );
            }
        }
        
        // check and copy marker names
        if(markerNames == null){
            this.markerNames = new String[m];
        } else {
            if(markerNames.length != m){
                throw new IllegalArgumentException("Incorrect number of marker names provided.");
            }
            this.markerNames = Arrays.copyOf(markerNames, m);
        }
        
        // check and copy allele names
        this.alleleNames = new String[m][];
        if(alleleNames == null){
            for(int j = 0; j < m; j++){
                this.alleleNames[j] = new String[numberOfAllelesForMarker[j]];
            }
        } else {
            if(alleleNames.length != m){
                throw new IllegalArgumentException("Incorrect number of allele names provided.");
            }
            for(int j = 0; j < m; j++){
                if(alleleNames[j] == null){
                    this.alleleNames[j] = new String[numberOfAllelesForMarker[j]];
                } else if (alleleNames[j].length != numberOfAllelesForMarker[j]) {
                    throw new IllegalArgumentException(
                            "Incorrect number of allele names provided for marker " + j
                    );
                } else {
                    this.alleleNames[j] = Arrays.copyOf(alleleNames[j], numberOfAllelesForMarker[j]);
                }
            }
        }
        
    }

    @Override
    public int getNumberOfMarkers() {
        return numberOfMarkers;
    }
    
    @Override
    public int getNumberOfAlleles(int markerIndex) {
        return numberOfAllelesForMarker[markerIndex];
    }
    
    @Override
    public String getMarkerName(int markerIndex) {
        return markerNames[markerIndex];
    }
    
    @Override
    public int getTotalNumberOfAlleles() {
        return totalNumberAlleles;
    }
    
    @Override
    public String getAlleleName(int markerIndex, int alleleIndex) {
        return alleleNames[markerIndex][alleleIndex];
    }

    @Override
    public Double getAlelleFrequency(int id, int markerIndex, int alleleIndex) {
        return alleleFrequencies[id][markerIndex][alleleIndex];
    }
    
    @Override
    public double getAverageAlelleFrequency(Collection<Integer> entityIds, int markerIndex, int alleleIndex) {
        // TODO: check exception (null pointer) in case of missing data
        return entityIds.stream()
                        .mapToDouble(id -> getAlelleFrequency(id, markerIndex, alleleIndex))
                        .average()
                        .getAsDouble();
    }

    public final static SimpleGenotypeVariantData readData(FileProperties fileProperties)
            throws IOException {
        
        RowReader reader;

        if (fileProperties == null) {
            throw new IOException("File properties not defined!");
        }

        if (fileProperties.getFile() == null) {
            throw new IOException("File not defined!");
        }

        if (fileProperties.getFileType() == null) {
            throw new IOException("File type not defined!");
        }

        if (fileProperties.getRowHeaderPosition() > UNKNOWN_INDEX
                && fileProperties.getDataRowPosition() > UNKNOWN_INDEX
                && fileProperties.getDataRowPosition() <= fileProperties.getColumnHeaderPosition()) {
            throw new IOException("Column header position : " + fileProperties.getDataRowPosition()
                                + " must be before data position : " + fileProperties.getColumnHeaderPosition());
        }

        if (!fileProperties.getFile().exists()) {
            throw new IOException("File does not exist : " + fileProperties.getFile());
        }

        List<String> itemNames = null;
        List<String> markerNames = new LinkedList<>();
        String markerName;
        String lastMarkerName = null;
        String alleleName;
        List<String> markerAlleleNames = new LinkedList<>();
        List<List<String>> alleleNames = new LinkedList<>();
        List<Double> alleleFrequencies;
        List<Double> markerFrequencies;
        List<List<List<Double>>> frequencies;

        int row = 0;

        try {
            reader = IOUtilities.createRowReader(fileProperties);

            if (reader != null && reader.ready()) {
                int columnCount = 0;

                if (reader.nextRow()) {
                    if (fileProperties.getRowHeaderPosition() > UNKNOWN_INDEX) {
                        while (row < fileProperties.getRowHeaderPosition() && reader.nextRow()) {
                            ++row;
                        }
                    }

                    reader.nextColumn();
                    reader.nextColumn();
                    reader.nextColumn();

                    itemNames = reader.getRowCellsAsString();

                    columnCount = itemNames.size();

                    frequencies = new ArrayList<>(columnCount);

                    for (int i = 0; i < columnCount; ++i) {
                        frequencies.add(new LinkedList<>());
                    }

                    if (fileProperties.getDataRowPosition() > UNKNOWN_INDEX) {
                        while (row < fileProperties.getDataRowPosition() && reader.nextRow()) {
                            ++row;
                        }
                    }

                    Iterator<Double> frequencyIterator;
                    int markerIndex = -1;
                    int index = 0;

                    while (reader.nextRow()) {
                        reader.nextColumn();

                        markerName = reader.getCellAsString();

                        reader.nextColumn();

                        alleleName = reader.getCellAsString();

                        reader.nextColumn();

                        alleleFrequencies = reader.getRowCellsAsDouble();

                        if (frequencies.size() != columnCount) {
                            throw new IOException("Rows are not all the same size!");
                        }

                        if (lastMarkerName == null || !lastMarkerName.equals(markerName)) {
                            markerAlleleNames = new LinkedList<>();
                            alleleNames.add(markerAlleleNames);
                            markerNames.add(markerName);

                            index = 0;

                            frequencyIterator = alleleFrequencies.iterator();

                            while (frequencyIterator.hasNext()) {
                                markerFrequencies = new LinkedList<>();
                                markerFrequencies.add(frequencyIterator.next());

                                frequencies.get(index).add(markerFrequencies);

                                ++index;
                            }

                            ++markerIndex;
                        } else {
                            frequencyIterator = alleleFrequencies.iterator();

                            index = 0;

                            while (frequencyIterator.hasNext()) {
                                frequencies.get(index).get(markerIndex).add(frequencyIterator.next());
                                ++index;
                            }
                        }

                        markerAlleleNames.add(alleleName);

                        lastMarkerName = markerName;

                        ++row;
                    }
                } else {
                    frequencies = new ArrayList<>(0);
                }
            } else {
                frequencies = new ArrayList<>(0);
            }

            if (reader != null) {
                reader.close();
            }

            return new SimpleGenotypeVariantData(fileProperties.getFile().getName(),
                                                             itemNames, markerNames, alleleNames, frequencies);

        } catch (IOException e) {
            throw new IOException("Error reading file at row : " + row + " due to " + e.getMessage(), e);
        }
    }
}
