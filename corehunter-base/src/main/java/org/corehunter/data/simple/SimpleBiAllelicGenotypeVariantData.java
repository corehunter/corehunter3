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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.corehunter.data.BiAllelicGenotypeVariantData;
import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.util.StringUtils;

import uno.informatics.common.io.FileType;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleBiAllelicGenotypeVariantData extends SimpleGenotypeVariantData
                                                implements BiAllelicGenotypeVariantData {

    private static final String NAMES_HEADER = "NAME";
    private static final String IDENTIFIERS_HEADER = "ID";
    private static final Collection<GenotypeDataFormat> SUPPORTED_OUTPUT_FORMATS
            = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                    GenotypeDataFormat.FREQUENCY, GenotypeDataFormat.BIALLELIC
            )));

    private final Integer[][] alleleScores; // null element means missing value

    /**
     * Create data with name "Biallelic marker data". For details of the
     * arguments see {@link #SimpleBiAllelicGenotypeVariantData(String, SimpleEntity[], String[], Integer[][])} .
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
     * Create data with given dataset name, item headers, marker names and
     * allele scores. The number of rows and columns of
     * <code>alleleScores</code> indicates the number of items and markers,
     * respectively. All entries in this matrix should be 0, 1 or 2 (or
     * <code>null</code> for missing values).
     * <p>
     * Item headers are required but marker names are optional.
     * If marker names are given they need not be defined for
     * all markers nor unique. Each item should at
     * least have a unique identifier (names are optional).
     * The length of <code>itemHeaders</code> and
     * <code>markerNames</code> (if not <code>null</code>)
     * should be equal to the number of items and
     * markers, respectively, as inferred from the dimensions of
     * <code>alleleScores</code>.
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
     *            item headers; its length should equal the number of
     *            rows in <code>alleleScores</code> and each item should at
     *            least have a unique identifier
     * @param markerNames
     *            marker names, <code>null</code> if no marker names are
     *            assigned; if not <code>null</code> its length should equal the
     *            number of columns in <code>alleleScores</code>; can contain
     *            <code>null</code> values for markers whose name is undefined
     * @param alleleScores
     *            allele scores, may not be <code>null</code> but can contain
     *            <code>null</code> values (missing); dimensions indicate number
     *            of items (rows) and markers (columns)
     */
    public SimpleBiAllelicGenotypeVariantData(String datasetName, SimpleEntity[] itemHeaders, String[] markerNames,
            Integer[][] alleleScores) {

        // pass dataset name, size and item headers to parent
        super(datasetName, itemHeaders, markerNames,
              inferAlleleNames(alleleScores), inferAlleleFrequencies(alleleScores));

        // check allele scores and infer number of items/markers
        int n = alleleScores.length;
        int m = -1;
        if (n == 0) {
            throw new IllegalArgumentException("No data (zero rows).");
        }
        for (int i = 0; i < n; i++) {
            Integer[] geno = alleleScores[i];
            // check: genotype defined
            if (geno == null) {
                throw new IllegalArgumentException(String.format("Allele scores not defined for item %d.", i));
            }
            // set/check number of markers
            if (m == -1) {
                m = geno.length;
                if (m == 0) {
                    throw new IllegalArgumentException("No markers (zero columns)");
                }
            } else if (geno.length != m) {
                throw new IllegalArgumentException(String.format(
                        "Incorrect number of markers for item %d. Expected: %d, actual: %d.", i, m, geno.length));
            }
            // check values
            for (int j = 0; j < m; j++) {
                if (geno[j] != null && (geno[j] < 0 || geno[j] > 2)) {
                    throw new IllegalArgumentException(String.format(
                            "Unexpected value at data row %d and data column %d. Got: %d (allowed: 0, 1, 2).", i, j,
                            geno[j]));
                }
            }
        }
        // copy allele scores
        this.alleleScores = new Integer[n][m];
        for (int i = 0; i < n; i++) {
            this.alleleScores[i] = Arrays.copyOf(alleleScores[i], m);
        }

    }
    
    private static String[][] inferAlleleNames(Integer[][] alleleScores){
        int numMarkers = alleleScores[0].length;
        String[][] alleleNames = new String[numMarkers][2];
        for(int m = 0; m < numMarkers; m++){
            alleleNames[m][0] = "0";
            alleleNames[m][1] = "1";
        }
        return alleleNames;
    }
 
    private static Double[][][] inferAlleleFrequencies(Integer[][] alleleScores){
        int numGenotypes = alleleScores.length;
        int numMarkers = alleleScores[0].length;
        Double[][][] freqs = new Double[numGenotypes][numMarkers][2];
        for(int i = 0; i < numGenotypes; i++){
            for (int m = 0; m < numMarkers; m++) {
                if(alleleScores[i][m] == null){
                    freqs[i][m][0] = freqs[i][m][1] = null;
                } else {
                    freqs[i][m][1] = ((double) alleleScores[i][m])/2.0;
                    freqs[i][m][0] = 1.0 - freqs[i][m][1];
                }
            }
        }
        return freqs;
    }

    @Override
    public Integer getAlleleScore(int id, int markerIndex) {
        return alleleScores[id][markerIndex];
    }

    /**
     * Read biallelic genotype variant data from file. Only file types
     * {@link FileType#TXT} and {@link FileType#CSV} are allowed. Values are
     * separated with a single tab (txt) or comma (csv) character. The file
     * contains an allele score matrix with one row per individual and one
     * column per marker. Only values 0, 1 and 2 are valid. Empty cells are also
     * allowed in case of missing data.
     * <p>
     * A header row and column are included to specify
     * individual and marker names, identified with column/row header "NAME".
     * Some or all marker names may be undefined by leaving the corresponding
     * cells blank and marker names need not be unique. If item names are not
     * unique or defined for some but not all items, a second header column "ID"
     * should be included to provide unique identifiers for at least those items
     * whose name is undefined or not unique.
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
     * @param filePath
     *            path to file that contains the data
     * @param type
     *            {@link FileType#TXT} or {@link FileType#CSV}
     * @return biallelic genotype variant data
     * @throws IOException
     *             if the file can not be read or is not correctly formatted
     */
    public static SimpleBiAllelicGenotypeVariantData readData(Path filePath, FileType type) throws IOException {

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
        try (RowReader reader = IOUtilities.createRowReader(filePath.toFile(), type,
                TextFileRowReader.REMOVE_WHITE_SPACE)) {

            if (reader == null || !reader.ready()) {
                throw new IOException("Can not create reader for file " + filePath + ". File may be empty.");
            }

            if (!reader.hasNextRow()) {
                throw new IOException("File is empty.");
            }

            // read and unquote all data
            List<String[]> rows = new ArrayList<>();
            while (reader.nextRow()) {
                rows.add(StringUtils.unquote(reader.getRowCellsAsStringArray()));
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

            // check for presence of names and ids
            boolean withNames = numCols >= 1 && Objects.equals(rows.get(0)[0], NAMES_HEADER);
            boolean withIds = numCols >= 2 && Objects.equals(rows.get(0)[1], IDENTIFIERS_HEADER);
            int numHeaderCols = 0;
            if (withNames) {
                numHeaderCols++;
            }
            if (withIds) {
                numHeaderCols++;
            }
            int numHeaderRows = (withNames ? 1 : 0);

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

            String[] markerNames = (withNames ? Arrays.copyOfRange(rows.get(0), numHeaderCols, numCols) : null);

            // 2: extract item names, identifiers and alelle scores

            String[] itemNames = new String[n];
            String[] itemIdentifiers = new String[n];
            Integer[][] alleleScores = new Integer[n][m];
            for (int i = 0; i < n; i++) {

                String[] row = rows.get(numHeaderRows + i);

                // extract item name and identifier
                if (withNames) {
                    itemNames[i] = row[0];
                }
                if (withIds) {
                    itemIdentifiers[i] = row[1];
                }

                // extract allele scores
                for (int j = 0; j < m; j++) {
                    String s = row[numHeaderCols + j];
                    try {
                        alleleScores[i][j] = (s == null ? null : Integer.parseInt(s.trim()));
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
                String name = itemNames[i];
                String identifier = itemIdentifiers[i];
                if (name != null || identifier != null) {
                    if (identifier == null) {
                        headers[i] = new SimpleEntityPojo(name, name);
                    } else {
                        headers[i] = new SimpleEntityPojo(identifier, name);
                    }
                }
            }
            if (Arrays.stream(headers).allMatch(Objects::isNull)) {
                headers = null;
            }

            try {
                // create data
                return new SimpleBiAllelicGenotypeVariantData(filePath.getFileName().toString(), headers, markerNames,
                        alleleScores);
            } catch (IllegalArgumentException ex) {
                // convert to IO exception
                throw new IOException(ex.getMessage());
            }

        }

    }

    @Override
    public Collection<GenotypeDataFormat> getSupportedOutputFormats() {
        return SUPPORTED_OUTPUT_FORMATS;
    }
    
    @Override
    public void writeData(Path filePath, FileType fileType) throws IOException {
        writeData(filePath, fileType, GenotypeDataFormat.BIALLELIC);
    }

    @Override
    public void writeData(Path filePath, FileType fileType, GenotypeDataFormat format) throws IOException {
        
        if(format == null){
            throw new IllegalArgumentException("Output format not defined.");
        }
        
        switch(format){
            case BIALLELIC:
                writeBiallelicData(filePath, fileType);
                break;
            default:
                super.writeData(filePath, fileType, format);
        }
        
    }
    
    public void writeBiallelicData(Path filePath, FileType fileType) throws IOException {

        // validate arguments
        if (filePath == null) {
            throw new IllegalArgumentException("File path not defined.");
        }

        if (filePath.toFile().exists()) {
            throw new IOException("File already exists : " + filePath + ".");
        }

        if (fileType == null) {
            throw new IllegalArgumentException("File type not defined.");
        }

        if (fileType != FileType.TXT && fileType != FileType.CSV) {
            throw new IllegalArgumentException(
                    String.format("Only file types TXT and CSV are supported. Got: %s.", fileType));
        }

        Files.createDirectories(filePath.getParent());

        // read data from file
        try (RowWriter writer = IOUtilities.createRowWriter(filePath.toFile(), fileType,
                TextFileRowReader.REMOVE_WHITE_SPACE)) {

            if (writer == null || !writer.ready()) {
                throw new IOException("Can not create writer for file " + filePath + ".");
            }

            writer.writeCell(NAMES_HEADER);
            
            writer.newColumn() ;

            writer.writeCell(IDENTIFIERS_HEADER);
            
            for(int m = 0; m < getNumberOfMarkers(); m++){
                writer.newColumn();
                writer.writeCell(getMarkerName(m));
            }

            SimpleEntity header;

            for (int r = 0; r < alleleScores.length; ++r) {

                writer.newRow();
                
                header = getHeader(r);
                writer.writeCell(header.getName());
                
                writer.newColumn() ;
                
                writer.writeCell(header.getUniqueIdentifier());
                
                writer.newColumn() ;
                
                writer.writeRowCellsAsArray(alleleScores[r]);
            }

            writer.close();
        }
    }
}
