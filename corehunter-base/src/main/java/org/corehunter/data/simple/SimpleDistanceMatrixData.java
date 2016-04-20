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
import java.util.Iterator;
import java.util.List;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.SymmetricMatrixFormat;
import org.corehunter.util.StringUtils;

import uno.informatics.common.Constants;
import uno.informatics.common.io.FileType;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.pojo.DataPojo;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * Simple implementation of a distance matrix that stores all values in a two-dimensional double array.
 * The assigned entry IDs correspond to the row/column indices in the matrix.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleDistanceMatrixData extends DataPojo implements DistanceMatrixData {

    private static final double DELTA = 1e-10;
    private static final String NAMES_HEADER = "NAME";
    private static final String IDENTIFIERS_HEADER = "ID";
    
    // distance matrix
    private final double[][] distances;
    
    /**
     * Create distance matrix data given the item headers and distances.
     * Item headers and distances are copied to internal data structures.
     * The dataset name is set to "Precomputed distance matrix".
     * <p>
     * All values should be positive and the diagonal values equal to zero.
     * The distance matrix should be symmetric with all rows of equal length.
     * Violating any of these requirements will produce an exception.
     * <p>
     * Item headers are required. Each item should at least have a unique identifier
     * (names are optional).
     * 
     * @param headers item headers; its length should be the same as the dimension
     *                of the given distance matrix and each item should at least have a
     *                unique identifier (names are optional)
     * @param distances distance matrix (symmetric)
     * @throws IllegalArgumentException if an illegal distance matrix is given
     *                                  or the number of headers does not match
     *                                  the dimension of the distance matrix
     */
    public SimpleDistanceMatrixData(SimpleEntity[] headers, double[][] distances) {
        this("Precomputed distance matrix", headers, distances);
    }
    
    /**
     * Create distance matrix data given the dataset name, item headers and distances.
     * Item headers and distances are copied to internal data structures.
     * <p>
     * All values should be positive and the diagonal values equal to zero.
     * The distance matrix should be symmetric with all rows of equal length.
     * Violating any of these requirements will produce an exception.
     * <p>
     * Item headers are required. Each item should at least have a unique identifier
     * (names are optional).
     * 
     * @param name dataset name
     * @param headers item headers; its length should be the same as the dimension
     *                of the given distance matrix and each item should at least have a
     *                unique identifier (names are optional)
     * @param distances distance matrix (symmetric)
     * @throws IllegalArgumentException if an illegal distance matrix is given,
     *                                  if the number of headers does not match
     *                                  the dimension of the distance matrix, or
     *                                  if unique identifiers are missing in one
     *                                  or more headers
     */
    public SimpleDistanceMatrixData(String name, SimpleEntity[] headers, double[][] distances) {
        
        // pass dataset name, size and item headers to parent
        super(name, headers);
        
        // validate distances and copy to internal array
        int n = distances.length;
        this.distances = new double[n][n];

        for (int r = 0; r < n; r++) {
            // check row length
            if (distances[r].length != n) {
                throw new IllegalArgumentException(
                        String.format("Number of distances in row %d does not match number of rows.", r)
                );
            }
            // validate and copy row values
            for(int c = 0; c < n; c++){
                // check positive
                if(distances[r][c] < 0.0){
                    throw new IllegalArgumentException("All distances should be positive.");
                }
                // check symmetric
                if(Math.abs(distances[r][c] - distances[c][r]) > DELTA){
                    throw new IllegalArgumentException("Distance matrix should be symmetric.");
                }
                // check diagonal zero
                if(r == c && distances[r][c] > DELTA){
                    throw new IllegalArgumentException("Diagonal values should be zero.");
                }
                // copy
                this.distances[r][c] = distances[r][c];
            }
        }
        
    }

    @Override
    public double getDistance(int idX, int idY) {
        return distances[idX][idY];
    }

    /**
     * Read distance matrix data from file. Only file types {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * Values are separated with a single tab (txt) or comma (csv) character and should all be positive.
     * If the matrix is provided in a format that includes diagonal values ({@link SymmetricMatrixFormat#FULL}
     * or {@link SymmetricMatrixFormat#LOWER_DIAG}) these should equal zero. If the full matrix is specified it
     * should be symmetric. Violating these requirements will produce an exception.
     * <p>
     * One required and one optional header row are included at the beginning of the file to specify individual
     * names and (optionally) unique identifiers. The former is required and identified with row header "NAME".
     * The latter is optional and has row header "ID".
     * If only names are specified they should be defined for each item and unique. Else, additional unique
     * identifiers are also required for at least those items whose name is undefined or not unique.
     * Leading and trailing whitespace is removed from names and unique identifiers and they are unquoted if
     * wrapped in single or double quotes after whitespace removal. If it is intended to start or end a
     * name/identifier with whitespace this whitespace should be contained within the quotes, as it
     * will then not be removed.
     * <p>
     * The dataset name is set to the name of the file to which <code>filePath</code> points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @param format specifies how the symmetric distance matrix is encoded in the file
     * @return distance matrix data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public static final SimpleDistanceMatrixData readData(Path filePath, FileType type,
                                                          SymmetricMatrixFormat format) throws IOException {
        
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
            
            // read row per row
            List<double[]> datarows = new ArrayList<>();
            double[] row;
            String firstCell;
            int rowCount, prevRowCount = Constants.UNKNOWN_COUNT;
            int r = 0; // row counter
            String[] names = null;
            String[] identifiers  = null;
            while(reader.nextRow()){
                                
                // peek first cell
                reader.nextColumn();
                firstCell = reader.getCellAsString();
                
                switch (StringUtils.unquote(firstCell)) {
                    
                    // names
                    case NAMES_HEADER:
                        
                        // check: no data rows read yet
                        if(!datarows.isEmpty()){
                            throw new IOException(String.format(
                                    "Row %s should be at the top of the file.", NAMES_HEADER
                            ));
                        }
                        // check: no names read yet
                        if(names != null){
                            throw new IOException(String.format(
                                    "Duplicate %s row.", NAMES_HEADER
                            ));
                        }
                        // read names
                        reader.nextColumn();
                        names = StringUtils.unquote(reader.getRowCellsAsStringArray());
                        break;
                    
                    // identifiers
                    case IDENTIFIERS_HEADER:
                        
                        // check: no data rows read yet
                        if(!datarows.isEmpty()){
                            throw new IOException(String.format(
                                    "Row %s should be at the top of the file.", IDENTIFIERS_HEADER
                            ));
                        }
                        // check: no identifiers read yet
                        if(identifiers != null){
                            throw new IOException(String.format(
                                    "Duplicate %s row.", IDENTIFIERS_HEADER
                            ));
                        }
                        // read identifiers
                        reader.nextColumn();
                        identifiers = StringUtils.unquote(reader.getRowCellsAsStringArray());
                        break;
                        
                    // data row
                    default:
                        
                        // read values
                        row = reader.getRowCellsAsDoubleArray();
                        // check number of values
                        rowCount = row.length;
                        checkNumValuesInRow(format, r, rowCount, prevRowCount);
                        // store
                        datarows.add(row);
                        prevRowCount = rowCount;
                        break;
                        
                }
                
                // next row
                r++;
                
            }
            
            if(datarows.isEmpty()){
                throw new IOException("No data rows in file.");
            }
            
            // infer number of accessions
            int n = datarows.get(datarows.size()-1).length;
            if(format == SymmetricMatrixFormat.LOWER){
                // diagonal not included
                n++;
            }
            
            // check number of rows
            int expectedRows = (format == SymmetricMatrixFormat.LOWER) ? n-1 : n;
            if(datarows.size() != expectedRows){
                throw new IOException(String.format(
                        "Incorrect number of data rows. Expected: %d, actual: %d.",
                        expectedRows, datarows.size()
                ));
            }
            
            // pad names with extra null values if needed when identifiers are also specified
            if(names != null && identifiers != null && names.length < n){
                names = Arrays.copyOf(names, n);
            }
            
            // check number of names
            if(names != null && names.length != n){
                throw new IOException(
                        String.format("Incorrect number of names. Expected: %d, actual: %d.", n, names.length)
                );
            }
            
            // check number of identifiers
            if(identifiers != null && identifiers.length != n){
                throw new IOException(
                        String.format(
                                "Incorrect number of identifiers. Expected: %d, actual: %d.",
                                n, identifiers.length
                        )
                );
            }
            
            // combine names and identifiers in headers
            SimpleEntity[] headers = null;
            if(identifiers != null || names != null){
                headers = new SimpleEntity[n];
                for(int i = 0; i < n; i++){
                    String name = names != null ? names[i] : null;
                    String identifier = identifiers != null ? identifiers[i] : null;
                    if(identifier == null){
                        headers[i] = new SimpleEntityPojo(name, name);
                    } else {
                        headers[i] = new SimpleEntityPojo(identifier, name);
                    }
                }
            }
            
            // init distance matrix
            double[][] dist = new double[n][n];
            // skip first row if lower triangular encoding without diagonal
            int s = (format == SymmetricMatrixFormat.LOWER) ? 1 : 0;
            // fill matrix
            Iterator<double[]> rowIterator = datarows.iterator();
            for(r = s; r < n; r++){
                row = rowIterator.next();
                for(int c = 0; c < row.length; c++){
                    dist[r][c] = row[c];
                }
            }
            
            // complete upper triangular part of matrix
            if(format != SymmetricMatrixFormat.FULL){
                for(r = 0; r < n; r++){
                    for(int c = r+1; c < n; c++){
                        dist[r][c] = dist[c][r];
                    }
                }
            }
            
            return new SimpleDistanceMatrixData(filePath.getFileName().toString(), headers, dist);
        }
    }
    
    /**
     * Write distance matrix to file.
     * 
     * @param filePath path to file
     * @param fileType {@link FileType#TXT} or {@link FileType#CSV}
     * @throws IOException if the file can not be written
     */
    public void writeData(Path filePath, FileType fileType) throws IOException {
        
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

        // write data to file
        try (RowWriter writer = IOUtilities.createRowWriter(filePath.toFile(), fileType,
                TextFileRowReader.REMOVE_WHITE_SPACE)) {

            if (writer == null || !writer.ready()) {
                throw new IOException("Can not create writer for file " + filePath + ".");
            }

            writer.writeCell(NAMES_HEADER);
            
            Iterator<Integer> iterator = getIDs().iterator() ;
            
            while (iterator.hasNext()) {
                
                writer.newColumn() ;
                
                writer.writeCell(getHeader(iterator.next()).getName()) ;
            }
            
            writer.newRow() ;
            
            writer.writeCell(IDENTIFIERS_HEADER);
                
            iterator = getIDs().iterator() ;
            
            while (iterator.hasNext()) {
                
                writer.newColumn() ;
                
                writer.writeCell(getHeader(iterator.next()).getUniqueIdentifier()) ;
            }
                        
            for (int i = 0; i < distances.length; ++i) {
                writer.newRow() ;
                writer.writeCell(distances[i][0]);
                for (int j = 1; j < distances.length; ++j) {
                    writer.newColumn() ;
                    writer.writeCell(distances[i][j]);
                }
            }

            writer.close();
        } 
    }
    
    private static void checkNumValuesInRow(SymmetricMatrixFormat format, int row,
                                     int rowCount, int prevRowCount) throws IOException{
        
        int expected = Constants.UNKNOWN_COUNT;
        switch(format){
                
            case FULL:
                // same as previous row, if any
                if(prevRowCount != Constants.UNKNOWN_COUNT){
                    expected = prevRowCount;
                }
                break;
            case LOWER_DIAG:
            case LOWER:
                // one more per row
                expected = prevRowCount == Constants.UNKNOWN_COUNT ? 1 : prevRowCount + 1;
                break;

            default: throw new IOException("Unknown matrix format " + format + ".");

        }
        
        if(expected != Constants.UNKNOWN_COUNT && expected != rowCount){
            throw new IOException(String.format(
                    "Incorrect number of values at row %d. Expected: %d, actual: %d", row, expected, rowCount
            ));
        }
        
    }
}
