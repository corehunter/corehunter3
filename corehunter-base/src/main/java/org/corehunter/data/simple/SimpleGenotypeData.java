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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.corehunter.data.GenotypeData;
import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.util.StringUtils;
import org.jamesframework.core.subset.SubsetSolution;

import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.DataPojo;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleGenotypeData extends DataPojo implements GenotypeData {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final double SUM_TO_ONE_PRECISION = 0.01 + 1e-8;
    private static final String ID_HEADER = "X";
    private static final String NAMES_HEADER = "NAME";
    private static final String ALLELE_NAMES_HEADER = "ALLELE";
    private static final String IDENTIFIERS_HEADER = "ID";
    private static final String SELECTED_HEADER = "SELECTED";

    private final Double[][][] alleleFrequencies; // null element means missing value
    private final int numberOfMarkers;
    private final int[] numberOfAllelesForMarker;
    private final int totalNumberAlleles;
    private final String[] markerNames; // null element means no marker name assigned
    private final String[][] alleleNames; // null element means no allele name assigned

    /**
     * Create data with name "Multiallelic marker data". For details of the
     * arguments see
     * {@link #SimpleGenotypeData(String, SimpleEntity[], String[], String[][], Double[][][])}.
     * 
     * @param itemHeaders
     *            item headers, specifying name and/or unique identifier
     * @param markerNames
     *            marker names
     * @param alleleNames
     *            allele names per marker
     * @param alleleFrequencies
     *            allele frequencies
     */
    public SimpleGenotypeData(SimpleEntity[] itemHeaders, String[] markerNames, String[][] alleleNames,
        Double[][][] alleleFrequencies) {
        this("Multiallelic marker data", itemHeaders, markerNames, alleleNames, alleleFrequencies);
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
     * @param alleleNames
     *            allele names per marker, <code>null</code> if no allele names
     *            are assigned; if not <code>null</code> the length of
     *            <code>alleleNames</code> should correspond to the number of
     *            markers and the length of <code>alleleNames[m]</code> to the
     *            number of alleles of the m-th marker (can contain
     *            <code>null</code> values)
     * @param alleleFrequencies
     *            allele frequencies, may not be <code>null</code> but can
     *            contain <code>null</code> values (missing); dimensions
     *            indicate number of individuals, markers and alleles per marker
     */
    public SimpleGenotypeData(String datasetName, SimpleEntity[] itemHeaders, String[] markerNames,
        String[][] alleleNames, Double[][][] alleleFrequencies) {

        // pass dataset name, size and item headers to parent
        super(datasetName, itemHeaders);

        // check allele frequencies and infer number of individuals, markers and
        // alleles per marker
        int n = alleleFrequencies.length;
        int m = -1;
        int[] a = null;
        // loop over individuals
        for (int i = 0; i < n; i++) {
            Double[][] indFreqs = alleleFrequencies[i];
            if (indFreqs == null) {
                throw new IllegalArgumentException("Allele frequencies not defined for individual " + i);
            }
            if (m == -1) {
                m = indFreqs.length;
                a = new int[m];
                Arrays.fill(a, -1);
            } else if (indFreqs.length != m) {
                throw new IllegalArgumentException("All individuals should have same number of markers.");
            }
            // loop over markers
            for (int j = 0; j < m; j++) {
                Double[] alleleFreqs = indFreqs[j];
                if (alleleFreqs == null) {
                    throw new IllegalArgumentException(String
                        .format("Allele frequencies not defined for individual %d at marker %d.", i, j));
                }
                if (a[j] == -1) {
                    a[j] = alleleFreqs.length;
                } else if (alleleFreqs.length != a[j]) {
                    throw new IllegalArgumentException(
                        "Number of alleles per marker should be consistent across all individuals.");
                }
                // loop over alleles
                if (Arrays.stream(alleleFreqs).filter(Objects::nonNull).anyMatch(f -> f < 0.0)) {
                    throw new IllegalArgumentException("All frequencies should be positive.");
                }
                double sum = Arrays.stream(alleleFreqs).filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue).sum();
                // sum should not exceed 1.0
                if (sum > 1.0) {
                    throw new IllegalArgumentException(
                        "Allele frequency sum per marker should not exceed one.");
                }
                if (Arrays.stream(alleleFreqs).noneMatch(Objects::isNull)) {
                    // no missing values: should sum to 1.0
                    if (1.0 - sum > SUM_TO_ONE_PRECISION) {
                        throw new IllegalArgumentException(
                            "Allele frequencies for marker should sum to one.");
                    }
                    // normalize to avoid numerical imprecisions
                    for (int k = 0; k < alleleFreqs.length; k++) {
                        alleleFreqs[k] /= sum;
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
                this.alleleFrequencies[i][j] = Arrays.copyOf(alleleFrequencies[i][j],
                    numberOfAllelesForMarker[j]);
            }
        }

        // check and copy marker names
        if (markerNames == null) {
            this.markerNames = new String[m];
        } else {
            if (markerNames.length != m) {
                throw new IllegalArgumentException(
                    String.format("Incorrect number of marker names provided. Expected: %d, actual: %d.", m,
                        markerNames.length));
            }
            this.markerNames = Arrays.copyOf(markerNames, m);
        }

        // check and copy allele names
        this.alleleNames = new String[m][];
        if (alleleNames == null) {
            for (int j = 0; j < m; j++) {
                this.alleleNames[j] = new String[numberOfAllelesForMarker[j]];
            }
        } else {
            if (alleleNames.length != m) {
                throw new IllegalArgumentException(String.format(
                    "Incorrect number of marker-allele names provided. Expected: %d, actual: %d.", m,
                    alleleNames.length));
            }
            for (int j = 0; j < m; j++) {
                if (alleleNames[j] == null) {
                    this.alleleNames[j] = new String[numberOfAllelesForMarker[j]];
                } else if (alleleNames[j].length != numberOfAllelesForMarker[j]) {
                    throw new IllegalArgumentException(String.format(
                        "Incorrect number of allele names provided for marker %d. Expected: %d, actual: %d.",
                        j, numberOfAllelesForMarker[j], alleleNames[j].length));
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
    public Double getAlleleFrequency(int id, int markerIndex, int alleleIndex) {
        return alleleFrequencies[id][markerIndex][alleleIndex];
    }

    @Override
    public boolean hasMissingValues(int id, int markerIndex) {
        return Arrays.stream(alleleFrequencies[id][markerIndex]).anyMatch(Objects::isNull);
    }

    /**
     * Read genotype data from file in frequency format. See
     * {@link #readData(Path, FileType, GenotypeDataFormat)} for details.
     * 
     * @param filePath
     *            path to file that contains the data
     * @param type
     *            {@link FileType#TXT} or {@link FileType#CSV}
     * @return genotype data
     * @throws IOException
     *             if the file can not be read or is not correctly formatted
     */
    public static GenotypeData readData(Path filePath, FileType type) throws IOException {
        return readData(filePath, type, GenotypeDataFormat.FREQUENCY);
    }

    /**
     * Read genotype data from file. Only file types {@link FileType#TXT} and
     * {@link FileType#CSV} are allowed. Values are separated with a single tab
     * (txt) or comma (csv) character.
     * <p>
     * Several formats are supported.
     * 
     * <p>
     * For {@link GenotypeDataFormat#DEFAULT} the file contains one or more
     * consecutive columns per marker in which the observed alleles are
     * specified (by name/id/number). This format is suited for datasets with a
     * fixed number of allele observations per marker in each individual. Common
     * cases are those with one or two columns per marker, e.g. suited for fully
     * homozygous and diploid datasets, respectively. Any (possibly varying)
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
     * name, optionally extended with a suffix as described below.
     * 
     * <p>
     * For {@link GenotypeDataFormat#FREQUENCY} the file contains allele
     * frequencies following the requirements as described in the constructor
     * {@link #SimpleGenotypeData(String, SimpleEntity[], String[], String[][], Double[][][])}.
     * Missing frequencies are encoding as empty cells. The file starts with a
     * compulsory header row from which marker names and allele counts are
     * inferred. All columns corresponding to the same marker occur
     * consecutively in the file and are named after that marker, optionally
     * including a suffix as described below. Marker names should be unique.
     * There is one compulsory header column "ID" containing unique item
     * identifiers. Optionally a second header column "NAME" can be included to
     * provide (not necessarily unique) item names in addition to the unique
     * identifiers. Finally, an optional second header row can be included to
     * define allele names per marker, identified with row header "ALLELE".
     * Allele names need not be unique and can be undefined for some alleles by
     * leaving the corresponding cells empty.
     * 
     * <p>
     * For {@link GenotypeDataFormat#BIPARENTAL} the file contains 0/1/2 allele
     * scores for homozygous (parent 1), heterozygous and homozygous (parent 2),
     * respectively. In this case the file is read with
     * {@link SimpleBiAllelicGenotypeData#readData(Path, FileType)}.
     * 
     * <p>
     * In all formats leading and trailing whitespace is removed from names and
     * unique identifiers and they are unquoted if wrapped in single or double
     * quotes after whitespace removal. If it is intended to start or end a
     * name/identifier with whitespace this whitespace should be contained
     * within the quotes, as it will then not be removed. Also, column names may
     * optionally include an arbitrary suffix added to the marker name, starting
     * with a dash, underscore or dot character. The latter allows to use column
     * names such as "M1-1" and "M1-2", "M1.a" and "M1.b" or "M1_1" and "M1_2"
     * for a marker named "M1" with two columns. The column name prefix up to
     * before the last occurrence of any dash, underscore or dot character is
     * taken to be the marker name.
     * <p>
     * Trailing empty cells can be omitted from any row in the file.
     * <p>
     * The dataset name is set to the name of the file to which
     * <code>filePath</code> points.
     * 
     * @param filePath
     *            path to file that contains the data
     * @param type
     *            {@link FileType#TXT} or {@link FileType#CSV}
     * @param format
     *            the format of the data file
     * @return genotype data
     * @throws IOException
     *             if the file can not be read or is not correctly formatted
     */
    public static GenotypeData readData(Path filePath, FileType type, GenotypeDataFormat format)
        throws IOException {

        if (format == null) {
            throw new IllegalArgumentException("Format not defined.");
        }

        switch (format) {
            case DEFAULT:
                return readDefaultData(filePath, type);
            case FREQUENCY:
                return readFrequencyData(filePath, type);
            case BIPARENTAL:
                return SimpleBiAllelicGenotypeData.readData(filePath, type);
            default:
                throw new IllegalArgumentException("Unsupported format : " + format);

        }
    }

    private static SimpleGenotypeData readFrequencyData(Path filePath, FileType type) throws IOException {

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
            TextFileRowReader.REMOVE_WHITE_SPACE, TextFileRowReader.ROWS_SAME_SIZE)) {

            if (reader == null || !reader.ready()) {
                throw new IOException("Can not create reader for file " + filePath + ". File may be empty.");
            }

            if (!reader.hasNextRow()) {
                throw new IOException("File is empty.");
            }

            // 1: read marker names

            reader.nextRow();
            String[] markerNamesRow = reader.getRowCellsAsStringArray();
            // check presence of header columns
            if (markerNamesRow.length < 1 || !Objects.equals(markerNamesRow[0], IDENTIFIERS_HEADER)) {
                throw new IOException("Missing header row/column ID.");
            }
            boolean withNames = (markerNamesRow.length >= 2
                && Objects.equals(markerNamesRow[1], NAMES_HEADER));
            int numHeaderCols = 1;
            if (withNames) {
                numHeaderCols++;
            }
            // infer and check number of data columns
            int numCols = markerNamesRow.length;
            int numDataCols = numCols - numHeaderCols;
            if (numDataCols == 0) {
                throw new IOException("No data columns.");
            }
            // extract and unquote marker column names
            markerNamesRow = Arrays.stream(markerNamesRow).skip(numHeaderCols).map(StringUtils::unquote)
                .toArray(n -> new String[n]);
            // extract/check names and infer number of alleles per marker
            HashMap<String, Integer> markers;
            try {
                markers = inferMarkerNames(markerNamesRow);
            } catch (IllegalArgumentException ex) {
                throw new IOException(ex);
            }
            String[] markerNames = markers.keySet().toArray(new String[0]);
            Integer[] alleleCounts = markers.values().toArray(new Integer[0]);
            // infer number of markers
            int numMarkers = markers.size();

            // 2: read data rows (and allele names header if provided)

            String[][] alleleNames = null;
            List<String> itemNames = new ArrayList<>();
            List<String> itemIdentifiers = new ArrayList<>();
            List<Double[][]> alleleFreqs = new ArrayList<>();
            int r = 1;
            while (reader.nextRow()) {

                // read row as strings
                String[] row = reader.getRowCellsAsStringArray();
                // check length
                if (row.length != numCols) {
                    throw new IOException(
                        String.format("Incorrect number of columns at row %d. Expected: %d, actual: %d.", r,
                            numCols, row.length));
                }

                // check for allele names row
                if (Objects.equals(row[0], ALLELE_NAMES_HEADER)) {
                    // verify: second row
                    if (r != 1) {
                        throw new IOException("Allele names header should be the second row in the file.");
                    }
                    // extract allele names grouped per marker
                    int aglob = numHeaderCols;
                    alleleNames = new String[numMarkers][];
                    for (int m = 0; m < numMarkers; m++) {
                        alleleNames[m] = new String[alleleCounts[m]];
                        for (int a = 0; a < alleleNames[m].length; a++) {
                            alleleNames[m][a] = StringUtils.unquote(row[aglob]);
                            aglob++;
                        }
                    }
                } else {

                    // process data row

                    // extract unique item identifier
                    itemIdentifiers.add(StringUtils.unquote(row[0]));
                    // extract item name, if included
                    if (withNames) {
                        itemNames.add(StringUtils.unquote(row[1]));
                    }

                    // group frequencies per marker
                    Double[][] freqsPerMarker = new Double[numMarkers][];
                    int fglob = numHeaderCols;
                    for (int m = 0; m < numMarkers; m++) {
                        freqsPerMarker[m] = new Double[alleleCounts[m]];
                        for (int f = 0; f < freqsPerMarker[m].length; f++) {
                            Double freq;
                            try {
                                freq = (row[fglob] == null ? null : Double.parseDouble(row[fglob]));
                            } catch (NumberFormatException ex) {
                                // wrap in IO exception
                                throw new IOException(String.format(
                                    "Invalid frequency at row %d, column %d. Expected double value, got: \"%s\".",
                                    r, fglob, row[fglob]), ex);
                            }
                            freqsPerMarker[m][f] = freq;
                            fglob++;
                        }
                    }
                    // store frequencies
                    alleleFreqs.add(freqsPerMarker);

                }

                // next row
                r++;

            }
            int n = alleleFreqs.size();
            if (n == 0) {
                throw new IOException("No data rows.");
            }

            // combine names and identifiers in headers
            SimpleEntity[] headers = new SimpleEntity[n];
            for (int i = 0; i < n; i++) {
                String identifier = itemIdentifiers.get(i);
                String name = withNames ? itemNames.get(i) : itemIdentifiers.get(i);

                if (name != null) {
                    headers[i] = new SimpleEntityPojo(identifier, name);
                } else {
                    headers[i] = new SimpleEntityPojo(identifier);
                }
            }

            // convert collections to arrays
            Double[][][] alleleFreqsArray = alleleFreqs.stream().toArray(k -> new Double[k][][]);

            try {
                // create data
                return new SimpleGenotypeData(filePath.getFileName().toString(), headers, markerNames,
                    alleleNames, alleleFreqsArray);
            } catch (IllegalArgumentException ex) {
                // convert to IO exception
                throw new IOException(ex.getMessage());
            }

        }
    }

    private static SimpleGenotypeData readDefaultData(Path filePath, FileType type) throws IOException {

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
            TextFileRowReader.REMOVE_WHITE_SPACE)) {

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
            // extend rows with null values where needed + unquote
            for (int r = 0; r < rows.size(); r++) {
                String[] row = rows.get(r);
                row = StringUtils.unquote(row);
                if (row.length < numCols) {
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

            // 4: create data (convert to frequencies)
            try {
                return createDefaultData(observedAlleles, itemIdentifiers, itemNames, markerNames,
                    filePath.getFileName().toString());
            } catch (IllegalArgumentException ex) {
                throw new IOException(ex);
            }

        }

    }

    public static SimpleGenotypeData createDefaultData(String[][][] data, String[] ids, String[] names,
        String[] markerNames) {
        return createDefaultData(data, ids, names, markerNames, null);
    }

    public static SimpleGenotypeData createDefaultData(String[][][] data, String[] ids, String[] names,
        String[] markerNames, String dataName) {
        int n = data.length;
        int numMarkers = data[0].length;

        // 1: infer and sort allele names per marker
        String[][] alleleNames = new String[numMarkers][];
        for (int m = 0; m < numMarkers; m++) {
            // infer set of observed alleles for marker m (sort)
            Set<String> alleles = new TreeSet<>();
            for (int i = 0; i < n; i++) {
                for (String observed : data[i][m]) {
                    if (observed != null) {
                        alleles.add(observed);
                    }
                }
            }
            // check: at least one allele
            if (alleles.isEmpty()) {
                throw new IllegalArgumentException(String.format("No data for marker %s.", markerNames[m]));
            }
            // convert to array and store
            alleleNames[m] = alleles.toArray(new String[alleles.size()]);
        }

        // 2: infer frequencies
        Double[][][] alleleFreqs = new Double[n][numMarkers][];
        for (int i = 0; i < n; i++) {
            for (int m = 0; m < numMarkers; m++) {
                String[] markerAlleleNames = alleleNames[m];
                int totalNumAlleles = markerAlleleNames.length;
                String[] observed = data[i][m];
                Double[] freqs = new Double[totalNumAlleles];
                // check for missing values
                if (Arrays.stream(observed).noneMatch(Objects::isNull)) {
                    // no missing values
                    Arrays.fill(freqs, 0.0);
                }
                // increase frequencies according to the observed alleles
                double incr = 1.0 / observed.length;
                for (int possibleAllele = 0; possibleAllele < totalNumAlleles; possibleAllele++) {
                    for (int observedAllele = 0; observedAllele < observed.length; observedAllele++) {
                        if (markerAlleleNames[possibleAllele].equals(observed[observedAllele])) {
                            increaseFrequency(freqs, possibleAllele, incr);
                        }
                    }
                }
                alleleFreqs[i][m] = freqs;
            }
        }

        // combine names and identifiers in headers
        SimpleEntity[] headers = new SimpleEntity[n];
        for (int i = 0; i < n; i++) {
            if (names[i] != null) {
                headers[i] = new SimpleEntityPojo(ids[i], names[i]);
            } else {
                headers[i] = new SimpleEntityPojo(ids[i]);
            }
        }

        // create data
        if (dataName != null) {
            return new SimpleGenotypeData(dataName, headers, markerNames, alleleNames, alleleFreqs);
        } else {
            return new SimpleGenotypeData(headers, markerNames, alleleNames, alleleFreqs);
        }

    }

    private static String inferMarkerName(String columnName) {
        int i = Stream.of('-', '_', '.').mapToInt(suf -> columnName.lastIndexOf(suf)).max().orElse(-1);
        String markerName = (i >= 0 ? columnName.substring(0, i) : columnName);
        return markerName;
    }

    /**
     * Infer marker names and number of columns per marker from column names.
     * Any suffix after the last dot, dash or underscore character is removed
     * from the column name to obtain the marker name.
     * 
     * @param columnNames
     *            column names
     * @return Linked hash map that maps the inferred marker names on the number
     *         of columns for that marker. The order of entries in the map
     *         corresponds to the original column order and is retained due to
     *         the behavior of a linked hash map.
     */
    public static LinkedHashMap<String, Integer> inferMarkerNames(String[] columnNames) {
        if (columnNames == null) {
            throw new IllegalArgumentException("Column names undefined.");
        }
        LinkedHashMap<String, Integer> markerNames = new LinkedHashMap<>();
        String curName = null;
        for (int c = 0; c < columnNames.length; c++) {
            String columnName = columnNames[c];
            if (columnName == null) {
                throw new IllegalArgumentException("Missing column name for column " + c + ".");
            }
            String markerName = inferMarkerName(columnName);
            if (markerName.equals("")) {
                throw new IllegalArgumentException(
                    String.format("Invalid marker name at column %d (%s).", c, columnName));
            }
            if (curName == null || !markerName.equals(curName)) {
                // first column for new marker
                if (markerNames.containsKey(markerName)) {
                    throw new IllegalArgumentException("Duplicate marker name: " + markerName + ". "
                        + "Columns corresponding to same marker should occur consecutively.");
                }
                markerNames.put(markerName, 1);
                curName = markerName;
            } else {
                // additional column for current marker
                markerNames.put(markerName, markerNames.get(markerName) + 1);
            }
        }
        return markerNames;
    }

    private static void increaseFrequency(Double[] freqs, int a, double incr) {
        freqs[a] = freqs[a] == null ? incr : freqs[a] + incr;
    }

    /**
     * Get a list of supported output formats that may be used in
     * {@link #writeData(Path, FileType)}.
     * 
     * @return singleton list containing only
     *         {@link GenotypeDataFormat#FREQUENCY}
     */
    public List<GenotypeDataFormat> getSupportedOutputFormats() {
        return Collections.singletonList(GenotypeDataFormat.FREQUENCY);
    }

    /**
     * Write genotype data to file in frequency format. Only file types
     * {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * 
     * @param filePath
     *            path to file where the data will be written
     * @param fileType
     *            the type of data file
     * @throws IOException
     *             if the file can not be written
     */
    public void writeData(Path filePath, FileType fileType) throws IOException {
        writeData(filePath, fileType, GenotypeDataFormat.FREQUENCY);
    }

    /**
     * Write genotype data to file in the chosen format. By default the only
     * supported format is {@link GenotypeDataFormat#FREQUENCY} but the method
     * may be overridden in subclasses to support other formats. Only file types
     * {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * 
     * @param filePath
     *            path to file where the data will be written
     * @param fileType
     *            the type of data file
     * @param format
     *            output format
     * @throws IOException
     *             if the file can not be written
     */
    public void writeData(Path filePath, FileType fileType, GenotypeDataFormat format) throws IOException {

        // create auxiliary solution in which all IDs are selected
        SubsetSolution all = new SubsetSolution(getIDs());
        all.selectAll();
        // write selected (all)
        writeData(filePath, fileType, format, all, false, true, false);

    }

    /**
     * Write genotype data to file in the chosen format. By default the only
     * supported format is {@link GenotypeDataFormat#FREQUENCY} but the method
     * may be overridden in subclasses to support other formats. Only file types
     * {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * 
     * @param filePath
     *            path to file where the data will be written
     * @param fileType
     *            the type of data file
     * @param format
     *            output format
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
    public void writeData(Path filePath, FileType fileType, GenotypeDataFormat format,
        SubsetSolution solution, boolean includeId, boolean includeSelected, boolean includeUnselected)
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

        if (format != GenotypeDataFormat.FREQUENCY) {
            throw new IllegalArgumentException("Unsupported output format: " + format);
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

        // write data to file
        boolean markSelection = includeSelected && includeUnselected;
        try (RowWriter writer = IOUtilities.createRowWriter(
                filePath, fileType, TextFileRowReader.REMOVE_WHITE_SPACE
        )) {

            if (writer == null || !writer.ready()) {
                throw new IOException("Can not create writer for file " + filePath + ".");
            }

            // write internal integer id column header
            if (includeId) {
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
            for (int i = 0; i < markerNames.length; ++i) {
                for (int j = 0; j < alleleNames[i].length; ++j) {
                    writer.newColumn();
                    writer.writeCell(markerNames[i]);
                }
            }

            writer.newRow();

            // skip integer id column if included
            if (includeId) {
                writer.newColumn();
            }

            // write allele names row header
            writer.writeCell(ALLELE_NAMES_HEADER);

            writer.newColumn();
            
            // skip selection column if included
            if (markSelection) {
                writer.newColumn();
            }

            // write allele names
            for (int i = 0; i < alleleNames.length; ++i) {
                writer.newColumn();
                writer.writeRowCellsAsArray(alleleNames[i]);
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

                // write integer ID if included
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
                
                // write allele frequencies
                for (int j = 0; j < alleleFrequencies[id].length; ++j) {
                    writer.newColumn();
                    writer.writeRowCellsAsArray(alleleFrequencies[id][j]);
                }

            }

            writer.close();
        }

    }

}
