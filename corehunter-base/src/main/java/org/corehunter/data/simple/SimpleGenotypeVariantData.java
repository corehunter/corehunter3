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


import static uno.informatics.common.Constants.UNKNOWN_COUNT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.data.GenotypeVariantData;
import org.corehunter.util.StringUtils;

import uno.informatics.common.io.FileType;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.pojo.DataPojo;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleGenotypeVariantData extends DataPojo implements GenotypeVariantData {

    private static final double SUM_TO_ONE_PRECISION = 0.01 + 1e-8;
    private static final String NAMES_HEADER = "NAME";
    private static final String ALLELE_NAMES_HEADER = "ALLELE";
    private static final String IDENTIFIERS_HEADER = "ID";
    
    private final Double[][][] alleleFrequencies;   // null element means missing value
    private final boolean[][] hasMissingValues;     // which individuals have missing values for which markers
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
     * Item headers are required and marker/allele names are optional. 
     * If marker and/or allele names are given they need not be defined for all
     * markers/alleles nor unique. Each item should at least have a unique identifier
     * (names are optional). If not <code>null</code> the length of each header/name
     * array should correspond to the dimensions of <code>alleleFrequencies</code>
     * (number of individuals, markers and alleles per marker).
     * <p>
     * Violating any of the requirements will produce an exception.
     * <p>
     * Allele frequencies as well as assigned headers and names are copied into internal data structures,
     * i.e. no references are retained to any of the arrays passed as arguments.
     * 
     * @param datasetName name of the dataset
     * @param itemHeaders item headers; its length should correspond to the number of individuals
     *                    and each item should at least have a unique identifier (names are optional)
     * @param markerNames marker names, <code>null</code> if no marker names are assigned; if not
     *                    <code>null</code> its length should correspond to the number of markers
     *                    (can contain <code>null</code> values)
     * @param alleleNames allele names per marker, <code>null</code> if no allele names are assigned;
     *                    if not <code>null</code> the length of <code>alleleNames</code> should correspond
     *                    to the number of markers and the length of <code>alleleNames[m]</code> to the number
     *                    of alleles of the m-th marker (can contain <code>null</code> values)
     * @param alleleFrequencies allele frequencies, may not be <code>null</code> but can contain <code>null</code>
     *                          values (missing); dimensions indicate number of individuals, markers and alleles
     *                          per marker
     */
    public SimpleGenotypeVariantData(String datasetName, SimpleEntity[] itemHeaders, String[] markerNames,
                                     String[][] alleleNames, Double[][][] alleleFrequencies) {
        
        // pass dataset name, size and item headers to parent
        super(datasetName, itemHeaders);

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
                    // normalize to avoid numerical imprecisions
                    for(int k = 0; k  < alleleFreqs.length; k++){
                        alleleFreqs[k] /= sum;
                    }
                }
            }
        }
        numberOfMarkers = m;
        numberOfAllelesForMarker = a;
        
        // set total number of alleles
        totalNumberAlleles = Arrays.stream(numberOfAllelesForMarker).sum();
        
        // copy allele frequencies and detect missing values
        this.alleleFrequencies = new Double[n][m][];
        hasMissingValues = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                hasMissingValues[i][j] = Arrays.stream(alleleFrequencies[i][j]).anyMatch(Objects::isNull);
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
    
    @Override
    public boolean hasMissingValues(int id, int markerIndex) {
        return hasMissingValues[id][markerIndex];
    }
    
    /**
     * Read genotype variant data from file in frequency format. See 
     * {@link #readData(Path, FileType, GenotypeDataFormat)} for details.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return genotype variant data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public final static GenotypeVariantData readData(Path filePath, FileType type) throws IOException {
        return readData(filePath, type, GenotypeDataFormat.FREQUENCY) ;
    }

    /**
     * Read genotype variant data from file. Only file types {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * Values are separated with a single tab (txt) or comma (csv) character. 
     * 
     * <p>
     * Several formats are supported.  
     * 
     * <p>For {@link GenotypeDataFormat#FREQUENCY} format allele frequencies should follow the
     * requirements as described in the constructor {@link #SimpleGenotypeVariantData(String, SimpleEntity[], String[],
     * String[][], Double[][][])}. Missing frequencies are encoding as empty cells.
     * The file starts with a compulsory header row from which (unique) marker names and allele counts are inferred.
     * All columns corresponding to the same marker occur consecutively in the file and are named after that marker.
     * There is one required header column with item names, which is identified with column header "NAME".
     * If the provided item names are not unique or defined for some but not all items, a second header column "ID"
     * should be included to provide unique identifiers for at least those items whose name is undefined or not unique.
     * Finally, an optional second header row can be included to define allele names per marker, identified with row
     * header "ALLELE". Allele names need not be unique and can be undefined for some alleles by leaving the
     * corresponding cells empty.
     * 
     * <p>For {@link GenotypeDataFormat#DIPLOID} Diploid data the file contains two consecutive columns per marker in 
     * which the two observed alleles are specified (by number/name/id) for each individual. The number of alleles may
     * be larger than two and different per marker, but each diploid genotype contains only one (homozygous) or
     * two (heterozygous) of all possible alleles of a certain marker. This means that all inferred frequencies
     * are either 0.0, 0.5 or 1.0. Missing values are encoding as empty cells.
     * <p>
     * An required first header row and optional header column are included to specify individual and marker names,
     * respectively, identified with column/row header "NAME". If item names are not unique or defined for some but
     * not all items, a second header column "ID" should be included to provide unique identifiers for at least those
     * items whose name is undefined or not unique.
     * <p>
     * Each pair of two consecutive columns corresponds to a single marker and the headers of these two columns,
     * if provided, should share a unique prefix. The longest shared prefix of both column names is used as the marker
     * name. A single trailing dash, underscore or dot character is removed from the inferred marker name, but only
     * if this modification does not introduce duplicate names. The latter allows to use column names such as
     * "M1-1" and "M1-2", "M1.a" and "M1.b" or "M1_1" and "M1_2" for a marker named "M1". It is allowed that
     * some column names are undefined but a name should either be provided or not for both columns corresponding
     * to the same marker.
     * 
     * <p>
     * In all formats the leading and trailing whitespace is removed from names and unique identifiers and they are 
     * unquoted if wrapped in single or double quotes after whitespace removal. If it is intended to start or end a 
     * name/identifier with whitespace this whitespace should be contained within the quotes, as it will then not be 
     * removed.
     * <p>
     * Trailing empty cells can be omitted from any row in the file.
     * <p>
     * The dataset name is set to the name of the file to which <code>filePath</code> points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @param format the format of the data file
     * @return genotype variant data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public final static GenotypeVariantData readData(Path filePath, FileType type, 
            GenotypeDataFormat format) throws IOException {
        
        if (format == null) {
            throw new IllegalArgumentException("Format not defined.");
        }
        
        switch (format) {
            case DIPLOID:
                return readDiploidData(filePath, type);
            case FREQUENCY:
                return readFrequencyData(filePath, type);
            case BIALLELIC:
                return SimpleBiAllelicGenotypeVariantData.readData(filePath, type) ;
            default:
                throw new IllegalArgumentException("Unsupported format : " + format);

        }
    }
    
    /**
     * Write genotype variant data to file in frequency format. See 
     * {@link #writeData(Path, GenotypeVariantData, FileType, GenotypeDataFormat)}
     * for details. Only file types {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * 
     * @param filePath path to file where the data will be written
     * @param genotypicData the data to be written
     * @param type the type of data file
     * @throws IOException if the file can not be written
     */
    public final static void writeData(Path filePath, 
            SimpleGenotypeVariantData genotypicData, FileType type) throws IOException {
        
        writeData(filePath, genotypicData, type, GenotypeDataFormat.FREQUENCY);
    }
    
    /**
     * Write genotype variant data to file to supported formats. See 
     * {@link #readData(Path, FileType, GenotypeDataFormat)}
     * for a description of the supported formats. 
     * Only file types {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * 
     * @param filePath path to file where the data will be written
     * @param genotypicData the data to be written
     * @param type the type of data file
     * @param format the format of the data file
     * @throws IOException if the file can not be written
     */
    public final static void writeData(Path filePath, 
            GenotypeVariantData genotypicData, FileType type, 
            GenotypeDataFormat format) throws IOException {
        
        if (format == null) {
            throw new IllegalArgumentException("Format not defined.");
        }
        
        switch (format) {
            case DIPLOID:
                if (genotypicData instanceof GenotypeVariantData) {
                    writeDiploidData(filePath, (SimpleGenotypeVariantData) genotypicData, type);
                } else {
                    throw new IllegalArgumentException("Unsupported GenotypeVariantData type : " + genotypicData);
                }
                break;
            case FREQUENCY:
                if (genotypicData instanceof GenotypeVariantData) {
                    writeFrequencyData(filePath, (SimpleGenotypeVariantData) genotypicData, type);
                } else {
                    throw new IllegalArgumentException("Unsupported GenotypeVariantData type : " + genotypicData);
                }
                break;
            case BIALLELIC:
                if (genotypicData instanceof SimpleBiAllelicGenotypeVariantData) {
                    SimpleBiAllelicGenotypeVariantData.writeData(filePath,
                            (SimpleBiAllelicGenotypeVariantData) genotypicData, type);
                } else {
                    throw new IllegalArgumentException("Unsupported GenotypeVariantData type : " + genotypicData);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported format : " + format);

        }
    }
    
    private final static SimpleGenotypeVariantData readFrequencyData(Path filePath, FileType type) throws IOException {
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
                filePath.toFile(), type,
                TextFileRowReader.REMOVE_WHITE_SPACE,
                TextFileRowReader.ROWS_SAME_SIZE
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
            // check presence of header columns
            boolean withNames = (markerNamesRow.length >= 1 && Objects.equals(markerNamesRow[0], NAMES_HEADER));
            boolean withIds = (markerNamesRow.length >= 2 && Objects.equals(markerNamesRow[1], IDENTIFIERS_HEADER));
            int numHeaderCols = 0;
            if(withNames){
                numHeaderCols++;
            }
            if(withIds){
                numHeaderCols++;
            }
            // infer and check number of data columns
            int numCols = markerNamesRow.length;
            int numDataCols = numCols - numHeaderCols;
            if(numDataCols == 0){
                throw new IOException("No data columns.");
            }
            // extract and unquote marker column names
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
            
            // 2: read data rows (and allele names header if provided)
            
            String[][] alleleNames = null;
            List<String> itemNames = new ArrayList<>();
            List<String> itemIdentifiers = new ArrayList<>();
            List<Double[][]> alleleFreqs = new ArrayList<>();
            int r = 1;
            while(reader.nextRow()){
                
                // read row as strings
                String[] row = reader.getRowCellsAsStringArray();
                // check length
                if(row.length != numCols){
                    throw new IOException(String.format(
                            "Incorrect number of columns at row %d. Expected: %d, actual: %d.",
                            r, numCols, row.length
                    ));
                }
                
                // check for allele names row
                if(withNames && Objects.equals(row[0], ALLELE_NAMES_HEADER)){
                    // verify: second row
                    if(r != 1){
                        throw new IOException("Allele names header should be second row in the file.");
                    }
                    // extract allele names grouped per marker
                    int aglob = numHeaderCols;
                    alleleNames = new String[numMarkers][];
                    for(int m = 0; m < numMarkers; m++){
                        alleleNames[m] = new String[allelesPerMarker.get(m)];
                        for(int a = 0; a < alleleNames[m].length; a++){
                            alleleNames[m][a] = StringUtils.unquote(row[aglob]);
                            aglob++;
                        }
                    }
                } else {
                    
                    // process data row
                    
                    // extract row headers, if any (name/identifier)
                    if(withNames){
                        itemNames.add(StringUtils.unquote(row[0]));
                    }
                    if(withIds){
                        itemIdentifiers.add(StringUtils.unquote(row[1]));
                    }

                    // group frequencies per marker
                    Double[][] freqsPerMarker = new Double[numMarkers][];
                    int fglob = numHeaderCols;
                    for(int m = 0; m < numMarkers; m++){
                        freqsPerMarker[m] = new Double[allelesPerMarker.get(m)];
                        for(int f = 0; f < freqsPerMarker[m].length; f++){
                            Double freq;
                            try {
                                freq = (row[fglob] == null ? null : Double.parseDouble(row[fglob]));
                            } catch (NumberFormatException ex){
                                // wrap in IO exception
                                throw new IOException(String.format(
                                        "Invalid frequency at row %d, column %d. Expected double value, got: \"%s\".",
                                        r, fglob, row[fglob]
                                ), ex);
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
            if(n == 0){
                throw new IOException("No data rows.");
            }
            
            // combine names and identifiers in headers
            SimpleEntity[] headers = new SimpleEntity[n];
            for(int i = 0; i < n; i++){
                String name = withNames ? itemNames.get(i) : null;
                String identifier = withIds ? itemIdentifiers.get(i) : null;
                if(name != null || identifier != null){
                    if(identifier == null){
                        headers[i] = new SimpleEntityPojo(name, name);
                    } else {
                        headers[i] = new SimpleEntityPojo(identifier, name);
                    }
                }
            }
            if(Arrays.stream(headers).allMatch(Objects::isNull)){
                headers = null;
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

    private final static SimpleGenotypeVariantData readDiploidData(Path filePath, FileType type)
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
            if(rows.isEmpty()){
                throw new IOException("File is empty.");
            }
            
            // infer number of columns
            int numCols = rows.stream().mapToInt(row -> row.length).max().getAsInt();
            // extend rows with null values where needed + unquote
            for(int r = 0; r < rows.size(); r++){
                String[] row = rows.get(r);
                row = StringUtils.unquote(row);
                if(row.length < numCols){
                    row = Arrays.copyOf(row, numCols);
                }
                rows.set(r, row);
            }
            
            // check for presence of names and ids
            boolean withNames = numCols >= 1 && Objects.equals(rows.get(0)[0], NAMES_HEADER);
            boolean withIds = numCols >= 2 && Objects.equals(rows.get(0)[1], IDENTIFIERS_HEADER);
            int numHeaderCols = 0;
            if(withNames){
                numHeaderCols++;
            }
            if(withIds){
                numHeaderCols++;
            }
            int numHeaderRows = (withNames ? 1 : 0);
            
            // infer number of individuals
            int n = rows.size() - numHeaderRows;
            if(n == 0){
                throw new IOException("No data rows.");
            }
            
            // infer number of markers
            int numDataCols = numCols - numHeaderCols;
            if(numDataCols == 0){
                throw new IOException("No data columns.");
            }
            if(numDataCols % 2 != 0){
                throw new IOException("Number of data columns is not a multiple of two. Got: " + numDataCols + ".");
            }
            int numMarkers = numDataCols/2;
            
            // 1: infer marker names (if column names provided)

            String[] markerNames = null;
            if(withNames){
                String[] headerRow = rows.get(0);
                markerNames = new String[numMarkers];
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
                                "Marker column name should either be set for both or none of the two columns "
                              + "corresponding to the same marker. Found name %s for column %d but none for column %d.",
                                colName1 != null ? colName1 : colName2,
                                colName1 != null ? c1 : c2,
                                colName1 == null ? c1 : c2
                        ));
                    }
                    markerNames[m] = markerName;
                }
                // check uniqueness
                uniqueMarkerNames(markerNames, true);
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
                markerNames = uniqueMarkerNames(trimmedMarkerNames, false) ? trimmedMarkerNames : markerNames;
            }
            
            // 2: extract item names/identifiers

            String[] itemNames = new String[n];
            String[] itemIdentifiers = new String[n];
            if(numHeaderCols > 0){
                for(int i = 0; i < n; i++){
                    String[] row = rows.get(numHeaderRows + i);
                    // extract row headers, if any (name/identifier)
                    if(withNames){
                        itemNames[i] = row[0];
                    }
                    if(withIds){
                        itemIdentifiers[i] = row[1];
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
                    String[] row = rows.get(numHeaderRows + i);
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
                    String[] row = rows.get(numHeaderRows + i);
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
            
            // combine names and identifiers in headers
            SimpleEntity[] headers = new SimpleEntity[n];
            for(int i = 0; i < n; i++){
                String name = itemNames[i];
                String identifier = itemIdentifiers[i];
                if(name != null || identifier != null){
                    if(identifier == null){
                        headers[i] = new SimpleEntityPojo(name);
                    } else {
                        headers[i] = new SimpleEntityPojo(identifier, name);
                    }
                }
            }
            if(Arrays.stream(headers).allMatch(Objects::isNull)){
                headers = null;
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

    private static void writeFrequencyData(Path filePath, SimpleGenotypeVariantData genotypicData, 
            FileType fileType) throws IOException {

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

            String[] markerNames = genotypicData.markerNames;

            writer.writeCell(NAMES_HEADER);
            
            writer.newColumn() ;

            writer.writeCell(IDENTIFIERS_HEADER);

            String[][] alleleNames = genotypicData.alleleNames ;
            
            for (int i = 0 ; i < alleleNames.length ; ++i) {
                for (int j = 0 ; j < alleleNames[i].length ; ++j) {
                    writer.newColumn() ;
                    writer.writeCell(markerNames[i]);  
                }
            }
            
            writer.newRow() ;
            
            writer.writeCell(ALLELE_NAMES_HEADER);
            
            for (int i = 0 ; i < alleleNames.length ; ++i) {
                writer.newColumn() ;
                writer.writeRowCellsAsArray(alleleNames[i]);  
            }

            Double[][][] alleleFrequencies = genotypicData.alleleFrequencies ;

            SimpleEntity header;

            for (int i = 0; i < alleleFrequencies.length; ++i) {

                writer.newRow();
                
                header = genotypicData.getHeader(i);
                writer.writeCell(header.getName());
                
                writer.newColumn() ;
                
                writer.writeCell(header.getUniqueIdentifier());

                for (int j = 0; j < alleleFrequencies[i].length; ++j) {
                    writer.newColumn() ;
                    
                    writer.writeRowCellsAsArray(alleleFrequencies[i][j]);
                }
            }

            writer.close();
        }
    }
    

    private static void writeDiploidData(Path filePath, SimpleGenotypeVariantData genotypicData, 
            FileType fileType) throws IOException {

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
        
        String[][] alleleNames = genotypicData.alleleNames ;
        
        String[] markerNames = genotypicData.markerNames;
        
        for (int i = 0 ; i < alleleNames.length ; ++i) {
            if (alleleNames[i].length != 2) {
                throw new IllegalArgumentException("Marker : " + markerNames[i]
                        + " does not have 2 alleles : " + alleleNames.length);
            }
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
            
            for (int i = 0 ; i < alleleNames.length ; ++i) {
                for (int j = 0 ; j < alleleNames[i].length ; ++j) {
                    writer.newColumn() ;
                    writer.writeCell(markerNames[i]);  
                }
            }
            
            writer.newRow() ;
            
            writer.writeCell(ALLELE_NAMES_HEADER);
            
            for (int i = 0 ; i < alleleNames.length ; ++i) {
                writer.newColumn() ;
                writer.writeRowCellsAsArray(alleleNames[i]);  
            }

            Double[][][] alleleFrequencies = genotypicData.alleleFrequencies ;

            SimpleEntity header;

            for (int i = 0; i < alleleFrequencies.length; ++i) {

                writer.newRow();
                
                header = genotypicData.getHeader(i);
                writer.writeCell(header.getName());
                
                writer.newColumn() ;
                
                writer.writeCell(header.getUniqueIdentifier());

                for (int j = 0; j < alleleFrequencies[i].length; ++j) {
                    writer.newColumn() ;
                    
                    writer.writeRowCellsAsArray(alleleFrequencies[i][j]);
                }
            }

            writer.close();
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
    
    private static boolean uniqueMarkerNames(String[] names, boolean throwException) throws IOException{
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
