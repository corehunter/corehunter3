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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import org.corehunter.data.GenotypeVariantData;
import org.corehunter.util.StringUtils;
import uno.informatics.common.io.FileType;

import static uno.informatics.common.Constants.UNKNOWN_COUNT;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleGenotypeVariantData extends SimpleNamedData implements GenotypeVariantData {

    private static final double SUM_TO_ONE_PRECISION = 0.01 + 1e-8;
    private static final int UNDEFINED_COLUMN = -1;
    private static final String NAMES_HEADER = "NAME";
    private static final String IDENTIFIERS_HEADER = "ID";
    
    private final Double[][][] alleleFrequencies;   // null element means missing value
    private final int numberOfMarkers;
    private final int[] numberOfAllelesForMarker;
    private final int totalNumberAlleles;
    private final String[] markerNames;             // null element means no marker name assigned
    private final String[][] alleleNames;           // null element means no allele name assigned

    /**
     * Create data with name "Multiallelic marker data". For details of the arguments see 
     * {@link #SimpleGenotypeVariantData(String, SimpleEntity[], String[], String[][], Double[][][])}.
     * 
     * @param itemHeaders item headers, specifying name and/or unique identifier
     * @param markerNames marker names
     * @param alleleNames allele names per marker
     * @param alleleFrequencies allele frequencies
     */
    public SimpleGenotypeVariantData(SimpleEntity[] itemHeaders, String[] markerNames, String[][] alleleNames,
                                     Double[][][] alleleFrequencies) {
        this("Multiallelic marker data", itemHeaders, markerNames, alleleNames, alleleFrequencies);
    }
    
    /**
     * Create data with given dataset name, item headers, marker/allele names and allele frequencies.
     * The length of <code>alleleFrequencies</code> denotes the number of items in
     * the dataset. The length of <code>alleleFrequencies[i]</code> should be the same
     * for all <code>i</code> and denotes the number of markers. Finally, the length of
     * <code>alleleFrequencies[i][m]</code> should also be the same for all <code>i</code>
     * and denotes the number of alleles of the <code>m</code>-th marker. Allele counts may
     * differ for different markers.
     * <p>
     * All frequencies should be positive and the values in <code>alleleFrequencies[i][m]</code> should
     * sum to one for all <code>i</code> and <code>m</code>, with a precision of 0.01. Missing values are
     * encoded as <code>null</code>. If one or more allele frequencies are missing at a certain marker for
     * a certain individual, the remaining frequencies should sum to a value less than or equal to one.
     * <p>
     * Item headers and marker/allele names are optional. Missing names/headers are encoded as <code>null</code>.
     * Alternatively, if no item headers or no marker or allele names are assigned, the respective array
     * itself may also be <code>null</code>. If not <code>null</code> the length of each header/name array
     * should correspond to the dimensions of <code>alleleFrequencies</code> (number of individuals,
     * markers and alleles per marker).
     * <p>
     * Violating any of the requirements will produce an exception.
     * <p>
     * Allele frequencies as well as assigned headers and names are copied into internal data structures,
     * i.e. no references are retained to any of the arrays passed as arguments.
     * 
     * @param datasetName name of the dataset
     * @param itemHeaders item headers, <code>null</code> if no headers are assigned; if not <code>null</code>
     *                    its length should correspond to the number of individuals
     * @param markerNames marker names, <code>null</code> if no marker names are assigned; if not
     *                    <code>null</code> its length should correspond to the number of markers
     * @param alleleNames allele names per marker, <code>null</code> if no allele names are assigned;
     *                    if not <code>null</code> the length of <code>alleleNames</code> should correspond
     *                    to the number of markers and the length of <code>alleleNames[m]</code> to the number
     *                    of alleles of the m-th marker
     * @param alleleFrequencies allele frequencies, may not be <code>null</code> but can contain <code>null</code>
     *                          values (missing); dimensions indicate number of individuals, markers and alleles
     *                          per marker
     */
    public SimpleGenotypeVariantData(String datasetName, SimpleEntity[] itemHeaders, String[] markerNames,
                                     String[][] alleleNames, Double[][][] alleleFrequencies) {
        
        // pass dataset name, size and item headers to parent
        super(datasetName, alleleFrequencies.length, itemHeaders);

        // check allele frequencies and infer number of individuals, markers and alleles per marker
        int n = alleleFrequencies.length;
        int m = -1;
        int[] a = null;
        // loop over individuals
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
            // loop over markers
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
                // loop over alleles
                if(Arrays.stream(alleleFreqs).filter(Objects::nonNull).anyMatch(f -> f < 0.0)){
                    throw new IllegalArgumentException("All frequencies should be positive.");
                }
                double sum = Arrays.stream(alleleFreqs)
                                   .filter(Objects::nonNull)
                                   .mapToDouble(Double::doubleValue)
                                   .sum();
                // sum should not exceed 1.0
                if(sum > 1.0){
                    throw new IllegalArgumentException("Allele frequency sum per marker should not exceed one.");
                }
                if(Arrays.stream(alleleFreqs).noneMatch(Objects::isNull)){
                    // no missing values: should sum to 1.0
                    if(1.0 - sum > SUM_TO_ONE_PRECISION){
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
                throw new IllegalArgumentException(String.format(
                        "Incorrect number of marker names provided. Expected: %d, actual: %d.",
                        m, markerNames.length
                ));
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
                throw new IllegalArgumentException(String.format(
                        "Incorrect number of marker-allele names provided. Expected: %d, actual: %d.",
                        m, alleleNames.length
                ));
            }
            for(int j = 0; j < m; j++){
                if(alleleNames[j] == null){
                    this.alleleNames[j] = new String[numberOfAllelesForMarker[j]];
                } else if (alleleNames[j].length != numberOfAllelesForMarker[j]) {
                    throw new IllegalArgumentException(String.format(
                            "Incorrect number of allele names provided for marker %d. Expected: %d, actual: %d.",
                            j, numberOfAllelesForMarker[j], alleleNames[j].length
                    ));
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

    /**
     * Read genotype variant data from file. Only file types {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * Values are separated with a single tab (txt) or comma (csv) character. Allele frequencies should follow the
     * requirements as described in the constructor {@link #SimpleGenotypeVariantData(String, SimpleEntity[], String[],
     * String[][], Double[][][])}. Missing frequencies are encoding as empty cells. Trailing empty cells can be
     * omitted from any row.
     * <p>
     * The file should start with two compulsory header rows specifying the marker and allele names, respectively.
     * Marker names should be unique and specified for each data column. All columns corresponding to the same marker
     * should occur consecutively in the file and are marked with the name of this marker. The second header row
     * specifies the allele names, which need not be unique and can be undefined for some or all alleles by leaving
     * the corresponding cells blank. This second header row should always be present, even if it consists of empty
     * cells only, but trailing empty cells can be omitted. Depending on the number of header columns (if any, see
     * below) some additional column header or empty cells may be prepended to both header rows.
     * <p>
     * Two optional (leftmost) header columns can be included to specify individual names and/or unique identifiers.
     * The former is identified with column header "NAME", the latter with column header "ID". The column headers should
     * be placed in the corresponding cell of the second header row (allele names). The corresponding cells of the first
     * header row (marker names) should be left blank. Assigned identifiers, if any, should be unique and are used to
     * distinguish between individuals with the same name.
     * <p>
     * Leading and trailing whitespace is removed from names and unique identifiers and they are unquoted if wrapped
     * in single or double quotes after whitespace removal. If it is intended to start or end a name/identifier with
     * whitespace this whitespace should be contained within the quotes, as it will then not be removed. It is allowed
     * that names/identifiers are missing for some individuals/alleles but marker names are required. If a name/id is
     * missing the corresponding cell should be left blank. Trailing blank cells at the end of a row can be omitted.
     * <p>
     * The dataset name is set to the name of the file to which <code>filePath</code> points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return genotype variant data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public final static SimpleGenotypeVariantData readData(Path filePath, FileType type) throws IOException {
        
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
        try (RowReader reader = IOUtilities.createRowReader(
                filePath.toFile(), type, TextFileRowReader.REMOVE_WHITE_SPACE
        )) {
            
            if (reader == null || !reader.ready()) {
                throw new IOException("Can not create reader for file " + filePath + ". File may be empty.");
            }
            
            if(!reader.hasNextRow()){
                throw new IOException("File is empty.");
            }
            
            // 1: read marker names
            
            reader.nextRow();
            String[] markerNamesRow = reader.getRowCellsAsStringArray();
            // skip empty cells corresponding to header columns (at most two)
            int numHeaderCols = 0;
            while(numHeaderCols < 2 && numHeaderCols < markerNamesRow.length
                    && StringUtils.isBlank(markerNamesRow[numHeaderCols])){
                numHeaderCols++;
            }
            int numCols = markerNamesRow.length;
            int numDataCols = numCols - numHeaderCols;
            if(numDataCols == 0){
                throw new IOException("No data columns.");
            }
            markerNamesRow = Arrays.stream(markerNamesRow)
                                   .skip(numHeaderCols)
                                   .map(StringUtils::unquote)
                                   .toArray(n -> new String[n]);
            // extract/check names and infer number of alleles per marker
            Set<String> markerNames = new LinkedHashSet<>(); // follows insertion order
            List<Integer> allelesPerMarker = new ArrayList<>();
            String prevName = null;
            int curMarkerAlleleCount = UNKNOWN_COUNT;
            for(String curName : markerNamesRow){
                if(StringUtils.isBlank(curName)){
                    throw new IOException("Some marker names are missing or empty.");
                }
                if(!curName.equals(prevName)){
                    // start of new marker (check uniqueness)
                    if(!markerNames.add(curName)){
                        throw new IOException(String.format(
                                "Marker names should be unique. Duplicate name: %s. "
                              + "Columns with allele frequencies for the same marker "
                              + "should occur consecutively in the file.", curName
                        ));
                    }
                    // store allele count of previous marker
                    if(prevName != null){
                        allelesPerMarker.add(curMarkerAlleleCount);
                    }
                    // reset allele count
                    curMarkerAlleleCount = 1;
                } else {
                    // increase allele count
                    curMarkerAlleleCount++;
                }
                // update previous marker name
                prevName = curName;
            }
            // store allele count of last marker
            allelesPerMarker.add(curMarkerAlleleCount);
            // infer number of markers
            int numMarkers = markerNames.size();
            
            // 2: read allele names
            
            reader.nextRow();
            String[] alleleNamesRow = reader.getRowCellsAsStringArray();
            // extend with null values if needed
            if(alleleNamesRow.length < numCols){
                alleleNamesRow = Arrays.copyOf(alleleNamesRow, numCols);
            }
            // check row length
            if(alleleNamesRow.length != numCols){
                throw new IOException(String.format(
                        "Unexpected number of columns at row 1. Expected: %d, actual: %d.",
                        numCols, alleleNamesRow.length
                ));
            }
            // infer item name and identifier column, if provided
            int itemNameColumn = UNDEFINED_COLUMN;
            int itemIdentifierColumn = UNDEFINED_COLUMN;
            for(int c = 0; c < numHeaderCols; c++){
                String str = StringUtils.unquote(alleleNamesRow[c]);
                if(str == null){
                    throw new IOException(String.format(
                            "Missing column header. Row: 1, column: %d. Expected: %s or %s, but found blank cell.",
                            c, NAMES_HEADER, IDENTIFIERS_HEADER
                    ));
                }
                switch(str){
                    case NAMES_HEADER:
                        if(itemNameColumn == UNDEFINED_COLUMN){
                            itemNameColumn = c;
                        } else {
                            throw new IOException(String.format(
                                    "Duplicate %s column.", NAMES_HEADER
                            ));
                        }
                        break;
                    case IDENTIFIERS_HEADER:
                        if(itemIdentifierColumn == UNDEFINED_COLUMN){
                            itemIdentifierColumn = c;
                        } else {
                            throw new IOException(String.format(
                                    "Duplicate %s column.", IDENTIFIERS_HEADER
                            ));
                        }
                        break;
                    default: throw new IOException(String.format(
                            "Unexpected column header. Row: 1, column: %d. Expected: %s or %s, actual: \"%s\".",
                            c, NAMES_HEADER, IDENTIFIERS_HEADER, str
                    ));
                }
            }
            // group allele names by marker
            int aglob = numHeaderCols;
            String[][] alleleNames = new String[numMarkers][];
            for(int m = 0; m < numMarkers; m++){
                alleleNames[m] = new String[allelesPerMarker.get(m)];
                for(int a = 0; a < alleleNames[m].length; a++){
                    alleleNames[m][a] = StringUtils.unquote(alleleNamesRow[aglob]);
                    aglob++;
                }
            }
            
            // 3: read data rows
            
            if(!reader.hasNextRow()){
                throw new IOException("No data rows.");
            }
            List<String> itemNames = new ArrayList<>();
            List<String> itemIdentifiers = new ArrayList<>();
            List<Double[][]> alleleFreqs = new ArrayList<>();
            int r = 2;
            while(reader.nextRow()){
                
                // read row as strings
                String[] dataRow = reader.getRowCellsAsStringArray();
                // extend with null values if needed
                if(dataRow.length < numCols){
                    dataRow = Arrays.copyOf(dataRow, numCols);
                }
                // check length
                if(dataRow.length != numCols){
                    throw new IOException(String.format(
                            "Incorrect number of columns at row %d. Expected: %d, actual: %d.",
                            r, numCols, dataRow.length
                    ));
                }
                
                // extract row headers, if any (name/identifier)
                for(int c = 0; c < numHeaderCols; c++){
                    String nameOrId = StringUtils.unquote(dataRow[c]);
                    if(itemNameColumn == c){
                        itemNames.add(nameOrId);
                    } else {
                        itemIdentifiers.add(nameOrId);
                    }
                }
                
                // group per marker
                Double[][] freqsPerMarker = new Double[numMarkers][];
                int fglob = numHeaderCols;
                for(int m = 0; m < numMarkers; m++){
                    freqsPerMarker[m] = new Double[allelesPerMarker.get(m)];
                    for(int f = 0; f < freqsPerMarker[m].length; f++){
                        Double freq;
                        try {
                            freq = (dataRow[fglob] == null ? null : Double.parseDouble(dataRow[fglob]));
                        } catch (NumberFormatException ex){
                            // wrap in IO exception
                            throw new IOException(String.format(
                                    "Invalid frequency at row %d, column %d. Expected double value, got: \"%s\".",
                                    r, fglob, dataRow[fglob]
                            ), ex);
                        }
                        freqsPerMarker[m][f] = freq;
                        fglob++;
                    }
                }
                // store frequencies
                alleleFreqs.add(freqsPerMarker);
                
                // next row
                r++;
                
            }
            int n = alleleFreqs.size();
            
            // combine names and identifiers in item headers
            SimpleEntity[] headers = null;
            if(!itemNames.isEmpty() || !itemIdentifiers.isEmpty()){
                headers = new SimpleEntity[n];
                for(int i = 0; i < n; i++){
                    String name = !itemNames.isEmpty() ? itemNames.get(i) : null;
                    String identifier = !itemIdentifiers.isEmpty() ? itemIdentifiers.get(i) : null;
                    headers[i] = new SimpleEntityPojo(identifier, name);
                }
            }
            
            // convert collections to arrays
            String[] markerNamesArray = markerNames.stream().toArray(k -> new String[k]);
            Double[][][] alleleFreqsArray = alleleFreqs.stream().toArray(k -> new Double[k][][]);
            
            try{
                // create data
                return new SimpleGenotypeVariantData(filePath.getFileName().toString(),
                                                     headers, markerNamesArray, alleleNames, alleleFreqsArray);
            } catch(IllegalArgumentException ex){
                // convert to IO exception
                throw new IOException(ex.getMessage());
            }
            
        }
        
    }
    
    /**
     * Read genotype variant data from file with simplified format suited for diploid genotypes only.
     * Only file types {@link FileType#TXT} and {@link FileType#CSV} are allowed. Values are separated with a
     * single tab (txt) or comma (csv) character. The file contains two consecutive columns per marker in which
     * the two observed alleles are specified (by number/name/id) for each individual. The number of alleles may
     * be larger than two and different per marker, but each diploid genotype contains only one (homozygous) or
     * two (heterozygous) of all possible alleles of a certain marker. This means that all inferred frequencies
     * are either 0.0, 0.5 or 1.0. Missing values are encoding as empty cells. Trailing empty cells can be
     * omitted at any row.
     * <p>
     * The file starts with a compulsory header row specifying the marker names. Although this row is required
     * some or all names may be undefined by leaving the corresponding cells blank. Each pair of two consecutive
     * columns corresponds to a single marker and the headers of these two columns, if provided, should share a
     * unique prefix. The longest shared prefix of both headers is used as the marker name. From each inferred name
     * ending with a dash, underscore or dot character this final character is removed, but only if this modification
     * does not introduce duplicate names. The latter allows to use column names such as "M1-1" and "M1-2", "M1.a"
     * and "M1.b" or "M1_1" and "M1_2" for a marker named "M1". A name should either be provided or undefined for
     * both columns corresponding to the same marker. Depending on the number of header columns (if any, see below)
     * some additional column header cells may be prepended to the header row.
     * <p>
     * Two optional (leftmost) header columns can be included to specify individual names and/or unique identifiers.
     * The former is identified with column header "NAME", the latter with column header "ID". The column headers should
     * be placed in the corresponding cell of the header row. Assigned identifiers, if any, should be unique and are
     * used to distinguish between individuals with the same name.
     * <p>
     * Leading and trailing whitespace is removed from names and unique identifiers and they are unquoted if wrapped
     * in single or double quotes after whitespace removal. If it is intended to start or end a name/identifier with
     * whitespace this whitespace should be contained within the quotes, as it will then not be removed. It is allowed
     * that names/identifiers are missing for some individuals/markers. In this case the corresponding cells should be
     * left blank. The name and identifier columns can be omitted if no names/identifiers are assigned, but the header
     * row with marker names should always be present, even if no marker names are assigned. Yet, trailing blank cells
     * at the end of the header row can be omitted.
     * <p>
     * The dataset name is set to the name of the file to which <code>filePath</code> points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return genotype variant data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public final static SimpleGenotypeVariantData readDiploidData(Path filePath, FileType type)
                                                                  throws IOException {
        
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
            // extend rows with null values where needed + unquote
            for(int r = 0; r <= n; r++){
                String[] row = rows.get(r);
                row = StringUtils.unquote(row);
                if(row.length < numCols){
                    row = Arrays.copyOf(row, numCols);
                }
                rows.set(r, row);
            }
            
            // 1: extract marker names
            
            String[] headerRow = rows.get(0);
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
            int numDataCols = numCols - numHeaderCols;
            if(numDataCols == 0){
                throw new IOException("No data columns.");
            }
            if(numDataCols % 2 != 0){
                throw new IOException("Number of data columns is not a multiple of two. Got: " + numDataCols + ".");
            }
            // infer marker names
            int numMarkers = numDataCols/2;
            String[] markerNames = new String[numMarkers];
            for(int m = 0; m < numMarkers; m++){
                int c1 = numHeaderCols + 2*m;
                int c2 = c1 + 1;
                String colName1 = headerRow[c1];
                String colName2 = headerRow[c2];
                String markerName;
                if(colName1 == null && colName2 == null){
                    markerName = null;
                } else if (colName1 != null && colName2 != null){
                    // infer longest shared prefix
                    markerName = longestSharedPrefix(colName1, colName2);
                    // check not empty
                    if(markerName.equals("")){
                        throw new IOException(String.format(
                                "Marker column names %s and %s do not share a prefix.",
                                colName1, colName2
                        ));
                    }
                } else {
                    throw new IOException(String.format(
                            "Marker column name should either be set for both or none of the two columns corresponding "
                          + "to the same marker. Found name %s for column %d but none for column %d.",
                            colName1 != null ? colName1 : colName2,
                            colName1 != null ? c1 : c2,
                            colName1 == null ? c1 : c2
                    ));
                }
                markerNames[m] = markerName;
            }
            // check uniqueness
            uniqueNames(markerNames, true);
            // remove trailing dots, underscores and dashes from
            // inferred marker names if this does not compromise uniqueness
            String[] trimmedMarkerNames = Arrays.stream(markerNames)
                                                .map(str -> 
                                                     str != null
                                                       && (str.endsWith(".")
                                                           || str.endsWith("-")
                                                           || str.endsWith("_"))
                                                     ? str.substring(0, str.length()-1)
                                                     : str
                                                )
                                                .toArray(k -> new String[k]);
            markerNames = uniqueNames(trimmedMarkerNames, false) ? trimmedMarkerNames : markerNames;
            
            // 2: extract item names/identifiers

            String[] itemNames = null;
            String[] itemIdentifiers = null;
            if(numHeaderCols > 0){
                itemNames = new String[n];
                itemIdentifiers = new String[n];
                for(int i = 0; i < n; i++){
                
                    String[] row = rows.get(i+1);

                    // extract row headers, if any (name/identifier)
                    for(int c = 0; c < numHeaderCols; c++){
                        if(itemNameColumn == c){
                            itemNames[i] = row[c];
                        } else {
                            itemIdentifiers[i] = row[c];
                        }
                    }

                }
            }
            
            // 3: extract alleles
            
            // infer allele names per marker
            String[][] alleleNames = new String[numMarkers][];
            for(int m = 0; m < numMarkers; m++){
                // infer set of observed alleles (sort lexicographically)
                Set<String> alleles = new TreeSet<>();
                for(int i = 0; i < n; i++){
                    String[] row = rows.get(i+1);
                    for(int a = 0; a < 2; a++){
                        String allele = row[numHeaderCols + 2*m + a];
                        if(allele != null){
                            alleles.add(allele);
                        }
                    }
                }
                // check: at least one allele
                if(alleles.isEmpty()){
                    throw new IOException(String.format(
                            "No data for marker %d (columns %d and %d).",
                            m, numHeaderCols + 2*m, numHeaderCols + 2*m+1
                    ));
                }
                // convert to array and store
                alleleNames[m] = alleles.toArray(new String[alleles.size()]);
            }
            
            // infer allele frequencies
            Double[][][] alleleFreqs = new Double[n][numMarkers][];
            for(int i = 0; i < n; i++){
                for(int m = 0; m < numMarkers; m++){
                    int numAlleles = alleleNames[m].length;
                    Double[] freqs = new Double[numAlleles];
                    String[] row = rows.get(i+1);
                    String allele1 = row[numHeaderCols + 2*m];
                    String allele2 = row[numHeaderCols + 2*m+1];
                    if(allele1 != null && allele2 != null){
                        // no missing values
                        Arrays.fill(freqs, 0.0);
                    }
                    // check which alleles are present
                    for(int a = 0; a < numAlleles; a++){
                        // note: increased twice (to 1.0) if allele1 == allele2 == checkAllele
                        String checkAllele = alleleNames[m][a];
                        if(checkAllele.equals(allele1)){
                            increaseFrequency(freqs, a, 0.5);
                        }
                        if(checkAllele.equals(allele2)){
                            increaseFrequency(freqs, a, 0.5);
                        }
                    }
                    alleleFreqs[i][m] = freqs;
                }
            }
            
            // combine names and identifiers in item headers
            SimpleEntity[] headers = null;
            if(itemNames != null){
                headers = new SimpleEntity[n];
                for(int i = 0; i < n; i++){
                    String name = itemNames[i];
                    String identifier = itemIdentifiers[i];
                    headers[i] = new SimpleEntityPojo(identifier, name);
                }
            }
            
            try{
                // create data
                return new SimpleGenotypeVariantData(filePath.getFileName().toString(),
                                                     headers, markerNames, alleleNames, alleleFreqs);
            } catch(IllegalArgumentException ex){
                // convert to IO exception
                throw new IOException(ex.getMessage());
            }
            
        }
                
    }
    
    private static String longestSharedPrefix(String str1, String str2){
        int end = 1;
        int l = Math.min(str1.length(), str2.length());
        while(end <= l && str1.substring(0, end).equals(str2.substring(0, end))){
            end++;
        }
        return str1.substring(0, end-1);
    }
    
    private static boolean uniqueNames(String[] names, boolean throwException) throws IOException{
        if(names == null){
            return true;
        }
        Set<String> checked = new HashSet<>();
        for(String name : names){
            if(name != null && !checked.add(name)){
                if(throwException){
                    throw new IOException(String.format(
                            "Duplicate marker name %s (longest shared prefix of both marker column names).", name
                    ));
                } else {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static void increaseFrequency(Double[] freqs, int a, double incr){
        freqs[a] = freqs[a] == null ? incr : freqs[a] + incr;
    }
    
}
