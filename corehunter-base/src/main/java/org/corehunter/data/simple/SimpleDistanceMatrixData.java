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

import uno.informatics.common.Constants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.matrix.SymmetricMatrixFormat;
import org.corehunter.util.StringUtils;
import uno.informatics.common.io.FileProperties;

import uno.informatics.common.io.FileType;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;

/**
 * Simple implementation of a distance matrix that stores all values in a two-dimensional double array.
 * The assigned entry IDs correspond to the row/column indices in the matrix.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleDistanceMatrixData extends SimpleNamedData implements DistanceMatrixData {

    private static final double DELTA = 1e-10;
    
    // distance matrix
    private final double[][] distances;

    /**
     * Create distance matrix with given distances (two-dimensional array).
     * Distances are copied to an internal data structure. The dataset name
     * is set to "Precomputed distance matrix".
     * <p>
     * All values should be positive and the diagonal values equal to zero.
     * The distance matrix should be symmetric with all rows of equal length.
     * Violating any of these requirements will produce an exception.
     * 
     * @param distances pairwise distances
     * @throws IllegalArgumentException if an illegal distance matrix is given
     */
    public SimpleDistanceMatrixData(double[][] distances) {
        // name of each item is set to null
        this(new String[distances.length], distances);
    }
    
    /**
     * Create distance matrix data given the item names (array) and distances (two-dimensional array).
     * Item names and distances are copied to internal data structures. The dataset name is set to
     * "Precomputed distance matrix".
     * <p>
     * All values should be positive and the diagonal values equal to zero.
     * The distance matrix should be symmetric with all rows of equal length.
     * Violating any of these requirements will produce an exception.
     * <p>
     * Item names are optional, missing names should be encoded as <code>null</code>
     * values in the name array. Alternatively, if no names are assigned to any items,
     * <code>itemNames</code> itself may also be <code>null</code>.
     * 
     * @param itemNames item names, <code>null</code> if no names are assigned; if not
     *                  <code>null</code> its length should be the same as the dimension
     *                  of the given distance matrix
     * @param distances distance matrix (symmetric)
     * @throws IllegalArgumentException if an illegal distance matrix is given
     *                                  or the number of names does not match
     *                                  the dimension of the distance matrix
     */
    public SimpleDistanceMatrixData(String[] itemNames, double[][] distances) {
        this("Precomputed distance matrix", itemNames, distances);
    }
    
    /**
     * Create distance matrix data given the dataset name, item names (array)
     * and distances (two-dimensional array). Item names and distances
     * are copied to internal data structures.
     * <p>
     * All values should be positive and the diagonal values equal to zero.
     * The distance matrix should be symmetric with all rows of equal length.
     * Violating any of these requirements will produce an exception.
     * <p>
     * Item names are optional, missing names should be encoded as <code>null</code>
     * values in the name array. Alternatively, if no names are assigned to any items,
     * <code>itemNames</code> itself may also be <code>null</code>.
     * 
     * @param name dataset name
     * @@param itemNames item names, <code>null</code> if no names are assigned; if not
     *                  <code>null</code> its length should be the same as the dimension
     *                  of the given distance matrix
     * @param distances distance matrix (symmetric)
     * @throws IllegalArgumentException if an illegal distance matrix is given
     *                                  or the number of names does not match
     *                                  the dimension of the distance matrix
     */
    public SimpleDistanceMatrixData(String name, String[] itemNames, double[][] distances) {
        
        // pass dataset name, size and item names to parent
        super(name, distances.length, itemNames);
        
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
     * Read distance matrix data. Only file types {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * Values are separated with a single tab (txt) or comma (csv) character and should all be positive.
     * If the matrix is provided in a format that includes diagonal values ({@link SymmetricMatrixFormat#FULL}
     * or {@link SymmetricMatrixFormat#LOWER_DIAG}) these should equal zero. If the full matrix is specified it
     * should be symmetric. Violating these requirements will produce an exception.
     * <p>
     * If <code>named</code> is <code>true</code> the first line of the file should contain the accession names.
     * Leading and trailing whitespace is removed from accession names and they are unquoted if wrapped in single
     * or double quotes after whitespace removal. If it is intended to start or end a name with whitespace this
     * whitespace should be contained within the quotes, as it will then not be removed. Missing item names are
     * set to <code>null</code>. The dataset name is set to the name of the file to which <code>filePath</code>
     * points.
     * 
     * @param filePath path to file that contains the data
     * @param type {@link FileType#TXT} or {@link FileType#CSV}
     * @param format specifies how the symmetric distance matrix is encoded in the file
     * @param named indicates whether a header row with accession names is included
     * @return distance matrix data
     * @throws IOException if the file can not be read or is not correctly formatted
     */
    public static final SimpleDistanceMatrixData readData(Path filePath, FileType type,
                                                          SymmetricMatrixFormat format,
                                                          boolean named) throws IOException {
        
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

        // row counter
        int r = 0;
        // read data from file
        try (RowReader reader = IOUtilities.createRowReader(
                new FileProperties(filePath.toFile(), type)
            )) {
            
            if (reader == null || !reader.ready()) {
                throw new IOException("Can not create reader for file " + filePath + ". File may be empty.");
            }
            
            if(!reader.hasNextRow()){
                throw new IOException("File is empty.");
            }
            
            // read names if included
            String[] names = null;
            if(named){
                // go to row with names
                reader.nextRow();
                // read, trim and unquote names
                names = Arrays.stream(reader.getRowCellsAsStringArray())
                              .map(name -> StringUtils.unquote(StringUtils.trim(name)))
                              .toArray(n -> new String[n]);
                // next row
                r++;
            }
            
            // read data rows
            List<List<Double>> rows = new ArrayList<>();
            int rowCount, prevRowCount = Constants.UNKNOWN_COUNT;
            while(reader.nextRow()){
                                
                // read row
                List<Double> row = reader.getRowCellsAsDouble();
                rowCount = row.size();
                
                // check number of values
                checkNumValuesInRow(format, r, rowCount, prevRowCount);
                
                // store
                rows.add(row);
                
                // next row
                prevRowCount = rowCount;
                r++;
                
            }
            
            if(rows.isEmpty()){
                throw new IOException("No data rows in file.");
            }
            
            // infer number of accessions
            int n = rows.get(rows.size()-1).size();
            if(format == SymmetricMatrixFormat.LOWER){
                // diagonal not included
                n++;
            }
            
            // check number of rows
            int expectedRows = (format == SymmetricMatrixFormat.LOWER) ? n-1 : n;
            if(rows.size() != expectedRows){
                throw new IOException(String.format(
                        "Incorrect number of data rows. Expected: %d, actual: %d.",
                        expectedRows, rows.size()
                ));
            }
            
            // check number of names
            if(names != null && names.length != n){
                throw new IOException(
                        String.format("Incorrect number of names. Expected: %d, actual: %d.", n, names.length)
                );
            }
            
            // init distance matrix
            double[][] dist = new double[n][n];
            // skip first row if lower triangular encoding without diagonal
            int s = (format == SymmetricMatrixFormat.LOWER) ? 1 : 0;
            // fill matrix
            Iterator<List<Double>> rowIterator = rows.iterator();
            for(r = s; r < n; r++){
                List<Double> row = rowIterator.next();
                for(int c = 0; c < row.size(); c++){
                    dist[r][c] = row.get(c);
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
            
            try{
                // create data
                return new SimpleDistanceMatrixData(filePath.getFileName().toString(), names, dist);
            } catch(IllegalArgumentException ex){
                // convert to IO exception
                throw new IOException(ex.getMessage());
            }

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
