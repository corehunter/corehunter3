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
import java.util.List;
import java.util.Objects;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.util.StringUtils;

import uno.informatics.data.io.FileType;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.pojo.DataPojo;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * Simple implementation of a distance matrix that stores all values in a two-dimensional double array.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleDistanceMatrixData extends DataPojo implements DistanceMatrixData {

    private static final double DELTA = 1e-10;
    private static final String IDENTIFIERS_HEADER = "ID";
    private static final String NAMES_HEADER = "NAME";
    
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
     * Values are separated with a single tab (txt) or comma (csv) character and should all be positive. Matrix entries
     * are included row-wise and rows can be truncated at or after the diagonal. If included, diagonal values should
     * always be zero. Any provided values after the diagonal (upper triangular part) are validated based on the
     * compulsory lower triangular part, verifying that the distance matrix is symmetric.
     * <p>
     * The file includes one required and one optional header column specifying unique identifiers and item names,
     * respectively. The first, required header column is identified with column header "ID". The second, optional
     * header column is identified with column header "NAME", when included. If no explicit names are provided, the
     * unique identifiers are used as names as well. Optionally, the same item identifiers from the first header
     * column may also be included on the first row (in the same order).
     * <p>
     * Leading and trailing whitespace is removed from names and unique identifiers and they are unquoted if
     * wrapped in single or double quotes after whitespace removal. If it is intended to start or end a
     * name/identifier with whitespace this whitespace should be contained within the quotes, as it
     * will then not be removed.
     * <p>
     * The dataset name is set to the name of the file from which the data is read.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @return distance matrix data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public static SimpleDistanceMatrixData readData(Path filePath, FileType type) throws IOException {
        
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
                filePath, type, TextFileRowReader.REMOVE_WHITE_SPACE
        )) {
            
            if (reader == null || !reader.ready()) {
                throw new IOException("Can not create reader for file " + filePath + ". File may be empty.");
            }
            
            if(!reader.hasNextRow()){
                throw new IOException("File is empty.");
            }
            
            // read all lines
            List<String[]> rows = new ArrayList<>();
            while (reader.nextRow()) {
                rows.add(reader.getRowCellsAsStringArray());
            }
            
            // infer dataset size
            int n = rows.size()-1;
            if (n <= 0) {
                throw new IOException("No data.");
            }
            
            // check presence of ID column
            String[] firstRow = rows.get(0);
            if(firstRow.length == 0 || !Objects.equals(IDENTIFIERS_HEADER, firstRow[0])){
                throw new IOException("Missing ID column.");
            }
            
            // check for presence of item names
            int numHeaderCols = 1;
            boolean withNames = false;
            if(firstRow.length > 1 && Objects.equals(NAMES_HEADER, firstRow[1])){
                withNames = true;
                numHeaderCols++;
            }
            
            // extract ids and names
            String[] ids = new String[n];
            String[] names = new String[n];
            for(int i = 0; i < n; i++){
                ids[i] = StringUtils.unquote(rows.get(i+1)[0]);
                names[i] = withNames ? StringUtils.unquote(rows.get(i+1)[1]) : ids[i];
            }
            
            // verify ids on header row, if provided
            if(firstRow.length > numHeaderCols){
                for(int i = 0; i < n; i++){
                    if(numHeaderCols + i >= firstRow.length
                            || !Objects.equals(ids[i], StringUtils.unquote(firstRow[numHeaderCols+i]))){
                        throw new IOException("Row and column identifiers differ.");
                    }
                }
            }
            
            // read matrix entries
            Double[][] dist = new Double[n][n];
            for(int i = 0; i < n; i++){
                String[] row = rows.get(i+1);
                if(row.length-numHeaderCols < i){
                    throw new IOException("Too few values at row " + (i+1) + ".");
                }
                if(row.length-numHeaderCols > n){
                    throw new IOException("Too many values at row " + (i+1) + ".");
                }
                for(int j = 0; j < row.length-numHeaderCols; j++){
                    String entry = row[numHeaderCols + j];
                    Double d = entry == null ? null : Double.parseDouble(entry);
                    dist[i][j] = d;
                }
            }
            
            // check and complete matrix
            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    Double d = dist[i][j];
                    if(i > j){
                        // lower triangular
                        if(d == null){
                            throw new IOException(String.format(
                                    "Missing value at row %d, col %d.",
                                    i + 1, numHeaderCols + j
                            ));
                        }
                    } else if (i == j){
                        // diagonal
                        if(d != null){
                            if(d > DELTA){
                                throw new IOException("Non-zero diagonal value at row " + (i+1) + ".");
                            }
                        } else {
                            dist[i][j] = 0.0;                            
                        }
                    } else {
                        // upper triangular
                        if(dist[j][i] == null){
                            throw new IOException(String.format(
                                    "Missing value at row %d, col %d.",
                                    j + 1, numHeaderCols + i
                            ));
                        }
                        if(d != null){
                            if(Math.abs(d - dist[j][i]) > DELTA){
                                throw new IOException("Matrix is not symmetric.");
                            }
                        } else {
                            dist[i][j] = dist[j][i];
                        }
                    }
                }
            }
            
            // unbox matrix
            double[][] distances = new double[n][n];
            for(int i = 0; i < n; i++){
                for(int j = 0; j < n; j++){
                    distances[i][j] = dist[i][j];
                }
            }
            
            // combine names and identifiers in headers
            SimpleEntity[] headers = new SimpleEntity[n];
            for(int i = 0; i < n; i++){
                if (names[i] != null) {
                    headers[i] = new SimpleEntityPojo(ids[i], names[i]);
                } else {
                    headers[i] = new SimpleEntityPojo(ids[i]);
                }             
            }
            
            return new SimpleDistanceMatrixData(filePath.getFileName().toString(), headers, distances);
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
        try (RowWriter writer = IOUtilities.createRowWriter(filePath, fileType,
                TextFileRowReader.REMOVE_WHITE_SPACE)) {

            if (writer == null || !writer.ready()) {
                throw new IOException("Can not create writer for file " + filePath + ".");
            }

            // write header row
            writer.writeCell(IDENTIFIERS_HEADER);
            writer.newColumn();
            writer.writeCell(NAMES_HEADER);
            for (int i = 0; i < getSize(); i++) {
                writer.newColumn() ;
                writer.writeCell(getHeader(i).getUniqueIdentifier()) ;
            }
            
            // write data rows
            for(int i = 0; i < getSize(); i++){
                writer.newRow();
                // write id and name
                writer.writeCell(getHeader(i).getUniqueIdentifier());
                writer.newColumn();
                writer.writeCell(getHeader(i).getName());
                // write matrix entries
                for(int j = 0; j < getSize(); j++){
                    writer.newColumn();
                    writer.writeCell(distances[i][j]);
                }
            }
            
            writer.close();
        } 
    }
    
}
