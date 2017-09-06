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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;
import org.corehunter.data.DefaultGenotypeData;


import org.jamesframework.core.subset.SubsetSolution;

import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.SimpleEntityPojo;
import org.corehunter.data.FrequencyGenotypeData;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowWriter;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleDefaultGenotypeData extends SimpleFrequencyGenotypeData implements DefaultGenotypeData {

    private static final long serialVersionUID = 1L;

    private static final String ID_HEADER = "X";
    private static final String NAMES_HEADER = "NAME";
    private static final String IDENTIFIERS_HEADER = "ID";
    private static final String SELECTED_HEADER = "SELECTED";

    private final String[][][] observedAlleles; // null element means missing value

    /**
     * Create data with name "Default marker data". For details of the arguments see
     * {@link #SimpleDefaultGenotypeData(String, SimpleEntity[], String[], String[][][])}.
     * 
     * @param itemHeaders item headers, specifying name and/or unique identifier
     * @param markerNames marker names
     * @param observedAlleles observed allele references
     */
    public SimpleDefaultGenotypeData(SimpleEntity[] itemHeaders, String[] markerNames, String[][][] observedAlleles) {
        this("Default marker data", itemHeaders, markerNames, observedAlleles);
    }

    /**
     * Create data with given dataset name, item headers, marker/allele names
     * and allele frequencies. The length of <code>alleleFrequencies</code>
     * denotes the number of items in the dataset. The length of
     * <code>alleleFrequencies[i]</code> should be the same for all
     * <code>i</code> and denotes the number of markers. Finally, the length of
     * <code>alleleFrequencies[i][m]</code> should also be the same for all
     * <code>i</code> and denotes the number of alleles of the <code>m</code>-th
     * marker. Allele counts may differ for different markers.
     * <p>
     * All frequencies should be positive and the values in
     * <code>alleleFrequencies[i][m]</code> should sum to one for all
     * <code>i</code> and <code>m</code>, with a precision of 0.01. Missing
     * values are encoded as <code>null</code>. If one or more allele
     * frequencies are missing at a certain marker for a certain individual, the
     * remaining frequencies should sum to a value less than or equal to one.
     * <p>
     * Item headers are required and marker/allele names are optional. If marker
     * and/or allele names are given they need not be defined for all
     * markers/alleles nor unique. Each item should at least have a unique
     * identifier (names are optional). If not <code>null</code> the length of
     * each header/name array should correspond to the dimensions of
     * <code>alleleFrequencies</code> (number of individuals, markers and
     * alleles per marker).
     * <p>
     * Violating any of the requirements will produce an exception.
     * <p>
     * Allele frequencies as well as assigned headers and names are copied into
     * internal data structures, i.e. no references are retained to any of the
     * arrays passed as arguments.
     * 
     * @param datasetName
     *            name of the dataset
     * @param itemHeaders
     *            item headers; its length should correspond to the number of
     *            individuals and each item should at least have a unique
     *            identifier (names are optional)
     * @param markerNames
     *            marker names, <code>null</code> if no marker names are
     *            assigned; if not <code>null</code> its length should
     *            correspond to the number of markers (can contain
     *            <code>null</code> values)
     * @param observedAlleles 
     *            observed allele references, may not be <code>null</code> but can
     *            contain <code>null</code> values (missing); dimensions indicate
     *            number of individuals, markers, and allele observations per
     *            individual for each specific marker
     */
    public SimpleDefaultGenotypeData(String datasetName, SimpleEntity[] itemHeaders,
                                     String[] markerNames, String[][][] observedAlleles) {

        // infer allele names and delegate to private auxiliary constructor
        this(datasetName, itemHeaders, markerNames, inferAlleleNames(observedAlleles), observedAlleles);

    }
    
    // private constructor infers allele frequencies after names have already been inferred
    private SimpleDefaultGenotypeData(String datasetName, SimpleEntity[] itemHeaders,
                                      String[] markerNames, String[][] alleleNames,
                                      String[][][] observedAlleles) {
        
        // pass dataset name, item headers, marker names, allele names and inferred frequencies to parent
        super(datasetName, itemHeaders, markerNames, alleleNames, inferAlleleFrequencies(observedAlleles, alleleNames));
        
        int numGeno = observedAlleles.length;
        int numMark = alleleNames.length;
        
        // check and copy observed alleles
        if (numGeno == 0) {
            throw new IllegalArgumentException("No data (zero rows).");
        }
        this.observedAlleles = new String[numGeno][][];
        int[] colsPerMarker = IntStream.generate(() -> -1).limit(numMark).toArray();
        for(int i = 0; i < numGeno; i++){
            String[][] geno = observedAlleles[i];
            // check: accession defined
            if (geno == null) {
                throw new IllegalArgumentException(String.format("Marker data not defined for item %d.", i));
            }
            // set/check number of markers
            if (numMark == -1) {
                numMark = geno.length;
                if (numMark == 0) {
                    throw new IllegalArgumentException(String.format("No markers (zero columns) for item %d.", i));
                }
            } else if (geno.length != numMark) {
                throw new IllegalArgumentException(
                    String.format(
                        "Incorrect number of markers for item %d. Expected: %d, actual: %d.", i, numMark, geno.length
                    )
                );
            }
            this.observedAlleles[i] = new String[numMark][];
            // check and copy data for each marker
            for(int m = 0; m < numMark; m++){
                String[] alleleObs = geno[m];
                // check: allele refs defined
                if(alleleObs == null){
                    throw new IllegalArgumentException(
                        String.format("Observed alleles not defined for item %d at marker %d.", i, m)
                    );
                }
                // set/check number of columns
                if (colsPerMarker[m] == -1) {
                    colsPerMarker[m] = alleleObs.length;
                    if (colsPerMarker[m] == 0) {
                        throw new IllegalArgumentException(
                            String.format("No allele references (zero columns) for item %d at marker %d.", i, m)
                        );
                    }
                } else if (alleleObs.length != colsPerMarker[m]) {
                    throw new IllegalArgumentException(
                        String.format(
                            "Incorrect number of columns for item %d at marker %d. Expected: %d, actual: %d.",
                            i, m, colsPerMarker[m], alleleObs.length
                        )
                    );
                }
                // copy
                this.observedAlleles[i][m] = Arrays.copyOf(alleleObs, colsPerMarker[m]);
            }
        }
        
    }
    
    // infer (sorted) allele names per marker
    private static String[][] inferAlleleNames(String[][][] observedAlleles){
        int n = observedAlleles.length;
        int numMarkers = observedAlleles[0].length;
        String[][] alleleNames = new String[numMarkers][];
        for (int m = 0; m < numMarkers; m++) {
            // infer set of observed alleles for marker m (sort)
            Set<String> alleles = new TreeSet<>();
            for (int i = 0; i < n; i++) {
                for (String observed : observedAlleles[i][m]) {
                    if (observed != null) {
                        alleles.add(observed);
                    }
                }
            }
            // check: at least one allele
            if (alleles.isEmpty()) {
                throw new IllegalArgumentException(String.format("No data for marker %d.", m));
            }
            // convert to array and store
            alleleNames[m] = alleles.toArray(new String[alleles.size()]);
        }
        return alleleNames;
    }
    
    // infer allele frequencies
    private static double[][][] inferAlleleFrequencies(String[][][] observedAlleles, String[][] alleleNames){
        int n = observedAlleles.length;
        int numMarkers = observedAlleles[0].length;
        double[][][] alleleFreqs = new double[n][numMarkers][];
        // infer freqs marker per marker
        for(int m = 0; m < numMarkers; m++){
            String[] markerAlleleNames = alleleNames[m];
            int numMarkerAlleles = markerAlleleNames.length;
            // map allele names on index
            Map<String, Integer> markerAlleleIndices = new HashMap<>();
            for(int a = 0; a < numMarkerAlleles; a++){
                markerAlleleIndices.put(markerAlleleNames[a], a);
            }
            // infer freqs for each accession at the current marker
            for(int i = 0; i < n; i++){
                String[] observed = observedAlleles[i][m];
                double[] freqs = new double[numMarkerAlleles];
                // prefill frequency array
                if (Arrays.stream(observed).noneMatch(Objects::isNull)) {
                    // no missing values: fill with zero (no uncertainty)
                    Arrays.fill(freqs, 0.0);
                } else {
                    // missing values: fill with NaN
                    Arrays.fill(freqs, Double.NaN);
                }
                // increase frequencies according to the observed alleles
                double incr = 1.0 / observed.length;
                Arrays.stream(observed).filter(Objects::nonNull).forEach(
                    observedAllele -> increaseFrequency(freqs, markerAlleleIndices.get(observedAllele), incr)
                );
                alleleFreqs[i][m] = freqs;
            }
        }
        return alleleFreqs;
    }
    
    private static void increaseFrequency(double[] freqs, int a, double incr) {
        freqs[a] = (Double.isNaN(freqs[a]) ? incr : freqs[a] + incr);
    }
    
    @Override
    public int getNumberOfObservedAllelesPerIndividual(int markerIndex) {
        return observedAlleles[0][markerIndex].length;
    }

    @Override
    public String getObservedAllele(int id, int markerIndex, int i) {
        return observedAlleles[id][markerIndex][i];
    }

    /**
     * Read default genotype data from file. Only file types {@link FileType#TXT} and
     * {@link FileType#CSV} are allowed. Values are separated with a single tab
     * (txt) or comma (csv) character.
     * <p>
     * The file contains one or more consecutive columns per marker, in which the
     * observed alleles are specified (by name/id/number/...). This format is suited
     * for datasets with a fixed number of allele observations per individual, for each
     * marker. Common cases are those with one or two columns per marker, e.g. suited
     * for fully homozygous and diploid datasets, respectively. Any (possibly varying)
     * number of columns per marker is supported.
     * <p>
     * Missing values are encoding as empty cells.
     * <p>
     * A required first header row and column are included to specify unique
     * item identifiers and marker names, respectively, identified with
     * column/row header "ID". Optionally a second header column "NAME" can be
     * included to provide (not necessarily unique) item names in addition to
     * the unique identifiers.
     * <p>
     * Consecutive columns corresponding to the same marker should have the same
     * name, optionally extended with a suffix starting with a dash, underscore
     * or dot character. The latter allows to use column names such as "M1-1"
     * and "M1-2", "M1.a" and "M1.b" or "M1_1" and "M1_2" for a marker named
     * "M1" with two columns. The column name prefix up to before the last
     * occurrence of any dash, underscore or dot character is taken to be
     * the marker name.
     * <p>
     * Leading and trailing whitespace is removed from names and
     * unique identifiers and they are unquoted if wrapped in single or double
     * quotes after whitespace removal. If it is intended to start or end a
     * name/identifier with whitespace, this whitespace should be contained
     * within the quotes, as it will then not be removed.
     * <p>
     * Trailing empty cells can be omitted from any row in the file.
     * <p>
     * The dataset name is set to the name of the file to which
     * <code>filePath</code> points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return default genotype data read from the given file
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public static FrequencyGenotypeData readData(Path filePath, FileType type) throws IOException {
        
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
        try(RowReader reader = IOUtilities.createRowReader(
                filePath, type, 
                TextFileRowReader.REMOVE_WHITE_SPACE,
                TextFileRowReader.REMOVE_QUOTES
        )){
            
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
            for(int r = 0; r < rows.size(); r++){
                String[] row = rows.get(r);
                if(row.length < numCols){
                    row = Arrays.copyOf(row, numCols);
                }
                rows.set(r, row);
            }

            // check for presence of names and ids
            if (numCols < 1 || !Objects.equals(rows.get(0)[0], IDENTIFIERS_HEADER)) {
                throw new IOException("Missing header row/column ID.");
            }
            boolean withNames = numCols >= 2 && Objects.equals(rows.get(0)[1], NAMES_HEADER);
            int numHeaderCols = 1;
            if (withNames) {
                numHeaderCols++;
            }

            // infer number of individuals
            int n = rows.size() - 1;
            if (n == 0) {
                throw new IOException("No data rows.");
            }

            // 1: infer marker names and number of columns per marker

            int numDataCols = numCols - numHeaderCols;
            if (numDataCols == 0) {
                throw new IOException("No data columns.");
            }

            String[] headerRow = rows.get(0);
            String[] dataColumnNames = Arrays.copyOfRange(headerRow, numHeaderCols, numCols);
            HashMap<String, Integer> markers;
            try {
                markers = inferMarkerNames(dataColumnNames);
            } catch (IllegalArgumentException ex) {
                throw new IOException(ex);
            }
            int numMarkers = markers.size();
            String[] markerNames = markers.keySet().toArray(new String[0]);
            Integer[] markerNumCols = markers.values().toArray(new Integer[0]);

            // 2: extract item names/identifiers

            String[] itemNames = new String[n];
            String[] itemIdentifiers = new String[n];
            for (int i = 0; i < n; i++) {
                String[] row = rows.get(i + 1);
                // extract row headers
                itemIdentifiers[i] = row[0];
                itemNames[i] = withNames ? row[1] : itemIdentifiers[i];
            }

            // 3: split observed alleles per marker
            String[][][] observedAlleles = new String[n][numMarkers][];
            for (int i = 0; i < n; i++) {
                String[] row = rows.get(i + 1);
                int c = numHeaderCols;
                for (int m = 0; m < numMarkers; m++) {
                    int nCol = markerNumCols[m];
                    observedAlleles[i][m] = new String[nCol];
                    for (int mc = 0; mc < nCol; mc++) {
                        observedAlleles[i][m][mc] = row[c++];
                    }
                }
            }

            // combine names and identifiers in headers
            SimpleEntity[] headers = new SimpleEntity[n];
            for (int i = 0; i < n; i++) {
                String identifier = itemIdentifiers[i];
                String name = withNames ? itemNames[i] : itemIdentifiers[i];

                if (name != null) {
                    headers[i] = new SimpleEntityPojo(identifier, name);
                } else {
                    headers[i] = new SimpleEntityPojo(identifier);
                }
            }
            
            // 4: create data
            try {
                // attempt to create data
                return new SimpleDefaultGenotypeData(
                        filePath.getFileName().toString(),
                        headers, markerNames, observedAlleles
                );
            } catch (IllegalArgumentException ex) {
                // convert to IO exception
                throw new IOException(ex);
            }

        }

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
            throw new NullPointerException("Solution must be defined");
        }

        if (!(solution.getAllIDs().equals(getIDs()))) {
            throw new IllegalArgumentException("Solution ids must match data.");
        }
        
        if(!includeSelected && !includeUnselected){
            throw new IllegalArgumentException(
                    "At least one of 'includeSelected' or 'includeUnselected' must be used."
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

            // write marker names (column headers)
            for (int m = 0; m < getNumberOfMarkers(); ++m) {
                for (int c = 0; c < getNumberOfObservedAllelesPerIndividual(m); ++c) {
                    writer.newColumn();
                    writer.writeCell(getMarkerName(m));
                }
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

                // write integer ID if included
                if (includeIndex) {
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
                
                // write allele observations
                for (int c = 0; c < observedAlleles[id].length; ++c) {
                    writer.newColumn();
                    writer.writeRowCellsAsArray(observedAlleles[id][c]);
                }

            }

            writer.close();
        }

    }

}
