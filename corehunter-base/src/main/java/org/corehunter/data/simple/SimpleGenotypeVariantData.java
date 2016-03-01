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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uno.informatics.common.io.FileProperties;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import org.corehunter.data.GenotypeVariantData;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleGenotypeVariantData extends SimpleNamedData implements GenotypeVariantData {

    private final Double[][][] alleleFrequencies;   //  null means missing
    private final int numberOfMarkers;
    private final int[] numberOfAllelesForMarker;
    private final int totalNumberAlleles;
    private final String[] markerNames;             // all null if marker names are not provided
    private final String[][] alleleNames;           // all null if allele names are not provided

    /**
     * Create data with given dataset name, item names, marker/allele names and allele frequencies.
     * All names (items, markers, alleles) are optional but the arrays lengths are important.
     * The length of <code>itemNames</code> should match that of <code>alleleFrequencies</code>.
     * The length of <code>markerNames</code> indicates the number of markers and should correspond
     * to the length of <code>alleleNames</code>. Furthermore, the length of <code>alleleNames[m]</code>
     * reflects the number of alleles of marker m. Missing names are encoded as <code>null</code> values
     * in the respective array.
     * 
     * @param datasetName
     * @param itemNames
     * @param markerNames
     * @param alleleNames
     * @param alleleFrequencies 
     */
    public SimpleGenotypeVariantData(String datasetName, String[] itemNames, String[] markerNames,
                                     String[][] alleleNames, double[][][] alleleFrequencies) {
        
        super(itemNames, datasetName);

        if (markerNames == null) {
            throw new IllegalArgumentException("Marker names not defined!");
        }

        if (alleleNames == null) {
            throw new IllegalArgumentException("Allele names not defined!");
        }

        if (alleleFrequencies == null) {
            throw new IllegalArgumentException("Allele Frequency entries not deifned!");
        }

        if (itemNames.length != alleleFrequencies.length) {
            throw new IllegalArgumentException("Number of allele frequency entries does not match number of names!");
        }

        if (markerNames.length != alleleNames.length) {
            throw new IllegalArgumentException("Number of marker names entries does not match "
                                             + "number of marker/allele names!");
        }

        totalNumberAlleles = 0;

        if (alleleFrequencies.length > 0) {
            numberOfMarkers = markerNames.length;

            numberOfAllelesForMarker = new int[numberOfMarkers];

            this.markerNames = new String[numberOfMarkers];
            this.alleleNames = new String[numberOfMarkers][];

            for (int j = 0; j < numberOfMarkers; ++j) {
                if (alleleNames[j] == null) {
                    throw new IllegalArgumentException("Allele names not defined for marker : " + j + "!");
                }

                this.markerNames[j] = markerNames[j];

                numberOfAllelesForMarker[j] = alleleNames[j].length;

                totalNumberAlleles = totalNumberAlleles + numberOfAllelesForMarker[j];

                this.alleleNames[j] = new String[numberOfAllelesForMarker[j]];

                for (int k = 0; k < numberOfAllelesForMarker[j]; ++k) {
                    this.alleleNames[j][k] = alleleNames[j][k];
                }
            }

            this.alleleFrequencies = new double[alleleFrequencies.length][numberOfMarkers][];

            for (int i = 0; i < alleleFrequencies.length; ++i) {
                if (numberOfMarkers != alleleFrequencies[i].length) {
                    throw new IllegalArgumentException("Number of markers does not match "
                                                     + "allele frequencies for id : " + i + "!");
                }

                for (int j = 0; j < numberOfMarkers; ++j) {
                    if (numberOfAllelesForMarker[j] != alleleFrequencies[0][j].length) {
                        throw new IllegalArgumentException("Number of alleles for marker " + j
                                                         + " at entry id : " + i + "!");
                    }

                    this.alleleFrequencies[i][j] = new double[numberOfAllelesForMarker[j]];

                    for (int k = 0; k < numberOfAllelesForMarker[j]; ++k) {
                        this.alleleFrequencies[i][j][k] = alleleFrequencies[i][j][k];
                    }
                }
            }
        } else {
            this.markerNames = new String[0];
            this.alleleNames = new String[0][0];
            this.numberOfAllelesForMarker = new int[0];
            this.alleleFrequencies = new double[0][0][0];
        }
    }

    @Override
    public int getNumberOfMarkers() {
        return numberOfMarkers;
    }

    @Override
    public double getAlelleFrequency(int id, int markerIndex, int alleleIndex) {
        return alleleFrequencies[id][markerIndex][alleleIndex];
    }
    
    @Override
    public double getAverageAlelleFrequency(Collection<Integer> entityIds,
            int markerIndex, int alleleIndex) {
        Iterator<Integer> iterator = entityIds.iterator();

        double summedAlleleFrequency = 0.0;
        Integer id;

        while (iterator.hasNext()) {
            id = iterator.next();

            summedAlleleFrequency = summedAlleleFrequency + getAlelleFrequency(id, markerIndex, alleleIndex);
        }

        return summedAlleleFrequency / entityIds.size();
    }

    @Override
    public int getNumberOfAlleles(int markerIndex) {
        return numberOfAllelesForMarker[markerIndex];
    }

    @Override
    public int getTotalNumberOfAlleles() {
        return totalNumberAlleles;
    }

    @Override
    public String getMarkerName(int markerIndex)
            throws ArrayIndexOutOfBoundsException {
        return markerNames[markerIndex];
    }

    @Override
    public String getAlleleName(int markerIndex, int alleleIndex)
            throws ArrayIndexOutOfBoundsException {
        return alleleNames[markerIndex][alleleIndex];
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
