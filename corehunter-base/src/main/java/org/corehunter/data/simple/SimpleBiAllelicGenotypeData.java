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

import static org.corehunter.util.CoreHunterConstants.MISSING_ALLELE_SCORE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.corehunter.data.BiAllelicGenotypeData;
import org.corehunter.util.CoreHunterConstants;
import org.jamesframework.core.subset.SubsetSolution;

import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.common.io.text.TextFileRowWriter;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.DataPojo;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleBiAllelicGenotypeData extends DataPojo implements BiAllelicGenotypeData {

    private static final long serialVersionUID = 1L;
    
    private static final String ID_HEADER = "X";
    private static final String NAMES_HEADER = "NAME";
    private static final String IDENTIFIERS_HEADER = "ID";
    private static final String SELECTED_HEADER = "SELECTED";
    
    private final byte[][] alleleScores;
    private final String[] markerNames; // null element means no marker name assigned

    /**
     * Create data with name "Biallelic marker data". For details of the arguments see
     * {@link #SimpleBiAllelicGenotypeData(String, SimpleEntity[], String[], byte[][])}
     * .
     * 
     * @param itemHeaders item headers (include name and/or unique identifier)
     * @param markerNames marker names
     * @param alleleScores 0/1/2 allele score matrix
     */
    public SimpleBiAllelicGenotypeData(SimpleEntity[] itemHeaders, String[] markerNames, byte[][] alleleScores) {
        this("Biallelic marker data", itemHeaders, markerNames, alleleScores);
    }

    /**
     * Create data with given dataset name, item headers, marker names and allele scores.
     * The number of rows and columns of <code>alleleScores</code> indicates the number of
     * items and markers, respectively. All entries in this matrix should be 0, 1 or 2 (or
     * {@link CoreHunterConstants#MISSING_ALLELE_SCORE} for missing values).
     * <p>
     * Item headers are required but marker names are optional. If marker names
     * are given they need not be defined for all markers nor unique. Each item
     * should at least have a unique identifier (names are optional). The length
     * of <code>itemHeaders</code> and <code>markerNames</code> (if not
     * <code>null</code>) should be equal to the number of items and markers,
     * respectively, as inferred from the dimensions of <code>alleleScores</code>.
     * <p>
     * Violating any of the requirements will produce an exception.
     * <p>
     * Allele scores as well as assigned headers and names are copied into
     * internal data structures, i.e. no references are retained to any of the
     * arrays passed as arguments.
     * 
     * @param datasetName
     *            name of the dataset
     * @param itemHeaders
     *            item headers; its length should equal the number of rows in
     *            <code>alleleScores</code> and each item should at least have a
     *            unique identifier
     * @param markerNames
     *            marker names, <code>null</code> if no marker names are
     *            assigned; if not <code>null</code> its length should equal the
     *            number of columns in <code>alleleScores</code>; can contain
     *            <code>null</code> values for markers whose name is undefined
     * @param alleleScores
     *            allele scores, may not be <code>null</code>; contains only values
     *            0, 1 and 2, and possibly {@link CoreHunterConstants#MISSING_ALLELE_SCORE}
     *            in case of missing values; dimensions indicate number of items (rows) and
     *            markers (columns)
     */
    public SimpleBiAllelicGenotypeData(String datasetName, SimpleEntity[] itemHeaders,
                                       String[] markerNames, byte[][] alleleScores) {

        // pass dataset name and item headers to parent
        super(datasetName, itemHeaders);

        // check allele scores and infer number of items and markers
        int n = alleleScores.length;
        int m = -1;
        if (n == 0) {
            throw new IllegalArgumentException("No data (zero rows).");
        }
        for (int i = 0; i < n; i++) {
            byte[] geno = alleleScores[i];
            // check: genotype defined
            if (geno == null) {
                throw new IllegalArgumentException(String.format("Allele scores not defined for item %d.", i));
            }
            // set/check number of markers
            if (m == -1) {
                m = geno.length;
                if (m == 0) {
                    throw new IllegalArgumentException(String.format("No markers (zero columns) for item %d.", i));
                }
            } else if (geno.length != m) {
                throw new IllegalArgumentException(String.format(
                    "Incorrect number of markers for item %d. Expected: %d, actual: %d.", i, m, geno.length));
            }
            // check values
            for (int j = 0; j < m; j++) {
                if (geno[j] != MISSING_ALLELE_SCORE && (geno[j] < 0 || geno[j] > 2)) {
                    throw new IllegalArgumentException(String.format(
                        "Unexpected value at data row %d and data column %d. Got: %d (allowed: 0, 1, 2).", i,
                        j, geno[j]));
                }
            }
        }
        
        // copy allele scores
        this.alleleScores = new byte[n][m];
        for (int i = 0; i < n; i++) {
            this.alleleScores[i] = Arrays.copyOf(alleleScores[i], m);
        }
        
        // check and copy marker names
        if (markerNames == null) {
            this.markerNames = new String[m];
        } else {
            if (markerNames.length != m) {
                throw new IllegalArgumentException(
                    String.format(
                        "Incorrect number of marker names provided. Expected: %d, actual: %d.",
                        m, markerNames.length
                    )
                );
            }
            this.markerNames = Arrays.copyOf(markerNames, m);
        }

    }

    /**
     * Read biallelic genotype data from file. Only file types
     * {@link FileType#TXT} and {@link FileType#CSV} are allowed. Values are
     * separated with a single tab (txt) or comma (csv) character. The file
     * contains an allele score matrix with one row per individual and one
     * column per marker. Only values 0, 1 and 2 are valid. Empty cells are also
     * allowed in case of missing data.
     * <p>
     * The file contains one required header row and column ("ID") specifying
     * item identifiers (row headers) and marker names (colum headers). Item
     * identifiers should be unique and defined for all items. Marker names may
     * be undefined for some or all markers and need not be unique. An optional
     * second header column ("NAME") can also be included, specifying (not
     * necessarily unique) item names. If no explicit item names are provided
     * the unique identifiers are used as names as well.
     * <p>
     * Leading and trailing whitespace is removed from names and unique
     * identifiers and they are unquoted if wrapped in single or double quotes
     * after whitespace removal. If it is intended to start or end a
     * name/identifier with whitespace this whitespace should be contained
     * within the quotes, as it will then not be removed.
     * <p>
     * Trailing empty cells can be omitted at any row in the file.
     * <p>
     * The dataset name is set to the name of the file to which
     * <code>filePath</code> points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return biallelic genotype data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public static SimpleBiAllelicGenotypeData readData(Path filePath, FileType type) throws IOException {

        // validate arguments

        if (filePath == null) {
            throw new IllegalArgumentException("File path not defined.");
        }

        if (!filePath.toFile().exists()) {
            throw new IOException("File does not exist : " + filePath + ".");
        }

        if (type == null) {
            throw new IllegalArgumentException("File type not defined.");
        }

        if (type != FileType.TXT && type != FileType.CSV) {
            throw new IllegalArgumentException(
                String.format("Only file types TXT and CSV are supported. Got: %s.", type));
        }

        // read data from file
        try (RowReader reader = IOUtilities.createRowReader(filePath, type,
            TextFileRowReader.REMOVE_WHITE_SPACE,
            TextFileRowReader.REMOVE_QUOTES)) {

            if (reader == null || !reader.ready()) {
                throw new IOException("Can not create reader for file " + filePath + ". File may be empty.");
            }

            if (!reader.hasNextRow()) {
                throw new IOException("File is empty.");
            }

            // read all data
            List<String[]> rows = new ArrayList<>();
            while (reader.nextRow()) {
                rows.add(reader.getRowCellsAsStringArray());
            }
            if (rows.isEmpty()) {
                throw new IOException("File is empty.");
            }

            // infer number of columns
            int numCols = rows.stream().mapToInt(row -> row.length).max().getAsInt();
            // extend rows with null values where needed
            for (int r = 0; r < rows.size(); r++) {
                String[] row = rows.get(r);
                if (row.length < numCols) {
                    row = Arrays.copyOf(row, numCols);
                }
                rows.set(r, row);
            }

            // check for presence of ids and names
            String[] firstRow = rows.get(0);
            if (firstRow.length == 0 || !Objects.equals(firstRow[0], IDENTIFIERS_HEADER)) {
                throw new IOException("Missing header row/column ID.");
            }
            boolean withNames = firstRow.length >= 2 && Objects.equals(firstRow[1], NAMES_HEADER);
            int numHeaderCols = 1;
            if (withNames) {
                numHeaderCols++;
            }
            int numHeaderRows = 1;

            // infer number of individuals
            int n = rows.size() - numHeaderRows;
            if (n == 0) {
                throw new IOException("No data rows.");
            }

            // infer number of markers
            int m = numCols - numHeaderCols;
            if (m == 0) {
                throw new IOException("No data columns.");
            }

            // 1: extract marker names (if provided)

            String[] markerNames = new String[m];
            for (int c = numHeaderCols; c < firstRow.length; c++) {
                markerNames[c - numHeaderCols] = firstRow[c];
            }

            // 2: extract item names, identifiers and alelle scores

            String[] itemNames = new String[n];
            String[] itemIdentifiers = new String[n];
            byte[][] alleleScores = new byte[n][m];
            for (int i = 0; i < n; i++) {

                String[] row = rows.get(numHeaderRows + i);

                // extract item name and identifier
                itemIdentifiers[i] = row[0];
                itemNames[i] = withNames ? row[1] : itemIdentifiers[i];

                // extract allele scores
                for (int j = 0; j < m; j++) {
                    String s = row[numHeaderCols + j];
                    try {
                        alleleScores[i][j] = (s == null ? MISSING_ALLELE_SCORE : Byte.parseByte(s.trim()));
                    } catch (NumberFormatException ex) {
                        // wrap in IO exception
                        throw new IOException(String.format(
                            "Invalid allele score at row %d, column %d. Expected integer value 0/1/2, got: \"%s\".",
                            numHeaderRows + i, numHeaderCols + j, s), ex);
                    }
                }

            }

            // combine names and identifiers in headers
            SimpleEntity[] headers = new SimpleEntity[n];
            for (int i = 0; i < n; i++) {
                if (itemNames[i] != null) {
                    headers[i] = new SimpleEntityPojo(itemIdentifiers[i], itemNames[i]);
                } else {
                    headers[i] = new SimpleEntityPojo(itemIdentifiers[i]);
                }
            }

            try {
                // create data
                return new SimpleBiAllelicGenotypeData(
                        filePath.getFileName().toString(),
                        headers, markerNames, alleleScores
                );
            } catch (IllegalArgumentException ex) {
                // convert to IO exception
                throw new IOException(ex.getMessage());
            }

        }

    }

    @Override
    public int getNumberOfMarkers() {
        return markerNames.length;
    }

    @Override
    public String getMarkerName(int markerIndex) throws ArrayIndexOutOfBoundsException {
        return markerNames[markerIndex];
    }

    @Override
    public int getNumberOfAlleles(int markerIndex) {
        return 2;
    }

    @Override
    public int getTotalNumberOfAlleles() {
        return 2 * getNumberOfMarkers();
    }

    @Override
    public String getAlleleName(int markerIndex, int alleleIndex) throws ArrayIndexOutOfBoundsException {
        if(alleleIndex < 0 || alleleIndex > 1){
            throw new ArrayIndexOutOfBoundsException(alleleIndex);
        }
        // convert index to string
        return alleleIndex + "";
    }
    
    @Override
    public byte getAlleleScore(int id, int markerIndex) {
        return alleleScores[id][markerIndex];
    }

    @Override
    public double getAlleleFrequency(int id, int markerIndex, int alleleIndex) {
        byte score = alleleScores[id][markerIndex];
        if(alleleIndex < 0 || alleleIndex > 1){
            throw new ArrayIndexOutOfBoundsException(alleleIndex);
        }
        if(score == MISSING_ALLELE_SCORE){
            return Double.NaN;
        } else {
            double f = score / 2.0;
            return alleleIndex == 1 ? f : 1.0 - f;
        }
    }

    @Override
    public boolean hasMissingValues(int id, int markerIndex) {
        return alleleScores[id][markerIndex] == MISSING_ALLELE_SCORE;
    }

    @Override
    public void writeData(Path filePath, FileType fileType, SubsetSolution solution,
                          boolean includeSelected, boolean includeUnselected, boolean includeIndex)
                          throws IOException {

        // validate arguments
        if (filePath == null) {
            throw new IllegalArgumentException("File path not defined.");
        }

        if (filePath.toFile().exists()) {
            throw new IOException("File already exists: " + filePath + ".");
        }

        if (fileType == null) {
            throw new IllegalArgumentException("File type not defined.");
        }

        if (fileType != FileType.TXT && fileType != FileType.CSV) {
            throw new IllegalArgumentException(
                String.format("Only file types TXT and CSV are supported. Got: %s.", fileType)
            );
        }
        
        if (solution == null) {
            throw new NullPointerException("Solution must be defined.");
        }

        if (!(solution.getAllIDs().equals(getIDs()))) {
            throw new IllegalArgumentException("Solution ids must match data.");
        }
        
        if(!includeSelected && !includeUnselected){
            throw new IllegalArgumentException(
                    "At least of 'includeSelected' or 'includeUnselected' must be used."
            );
        }

        Files.createDirectories(filePath.getParent());

        // write data to file
        boolean markSelection = includeSelected && includeUnselected;
        try (RowWriter writer = IOUtilities.createRowWriter(
                filePath, fileType, TextFileRowWriter.ADD_QUOTES
        )) {

            if (writer == null || !writer.ready()) {
                throw new IOException("Can not create writer for file " + filePath + ".");
            }

            // write internal integer id column header
            if (includeIndex) {
                writer.writeCell(ID_HEADER);
                writer.newColumn();
            }

            // write string id and name column headers
            writer.writeCell(IDENTIFIERS_HEADER);
            writer.newColumn();
            writer.writeCell(NAMES_HEADER);
            
            // write selection column header if both selected and unselected are included
            if (markSelection) {
                writer.newColumn();
                writer.writeCell(SELECTED_HEADER);
            }
            
            // write marker column headers
            for (int m = 0; m < getNumberOfMarkers(); m++) {
                writer.newColumn();
                writer.writeCell(getMarkerName(m));
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
                        "At least one of 'includeSelected' or 'includeUnselected' must be used."
                );
            }
            List<Integer> sortedIDs = new ArrayList<>(includedIDs);
            sortedIDs.sort(null);
            
            // write data rows
            Set<Integer> selected = solution.getSelectedIDs();
            for (int id : sortedIDs) {
                
                writer.newRow();
                
                // write integer id if requested
                if(includeIndex){
                    writer.writeCell(id);
                    writer.newColumn();
                }
                
                // write string id and name
                SimpleEntity header = getHeader(id);
                writer.writeCell(header.getUniqueIdentifier());
                writer.newColumn();
                writer.writeCell(header.getName());
                
                // mark selection if needed
                if (markSelection) {
                    writer.newColumn();
                    writer.writeCell(selected.contains(id));
                }
                
                // write allele scores
                for (int a = 0; a < alleleScores[id].length; a++) {
                    writer.newColumn();
                    byte score = alleleScores[id][a];
                    writer.writeCell(score == MISSING_ALLELE_SCORE ? null : score);
                }
                
            }

            writer.close();
        }
    }
    
}
