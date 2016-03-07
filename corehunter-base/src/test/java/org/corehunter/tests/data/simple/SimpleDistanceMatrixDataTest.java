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

package org.corehunter.tests.data.simple;

import static org.corehunter.tests.TestData.DISTANCES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.NAMES;
import static org.corehunter.tests.TestData.UNIQUE_IDENTIFIERS;
import static org.corehunter.tests.TestData.HEADERS;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.corehunter.data.matrix.SymmetricMatrixFormat;

import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.junit.AfterClass;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import uno.informatics.common.io.FileType;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleDistanceMatrixDataTest {

    private static final String TXT_FULL = "/distances/full.txt";
    private static final String TXT_FULL_NAMES = "/distances/full-names.txt";
    private static final String TXT_FULL_NAMES_IDS = "/distances/full-names-ids.txt";
    private static final String CSV_LOWER = "/distances/lower.csv";
    private static final String CSV_LOWER_DIAG_NAMES = "/distances/lower-diag-names.csv";
    
    private static final String[] ERRONEOUS_FILES = {
        "/distances/err/empty.txt",        
        "/distances/err/excessive-names.txt",        
        "/distances/err/excessive-ids.txt",        
        "/distances/err/incorrect-row-length.csv",        
        "/distances/err/incorrect-row-length-2.csv",        
        "/distances/err/incorrect-row-length.txt",        
        "/distances/err/names-only.txt",        
        "/distances/err/negative-values.csv",        
        "/distances/err/non-symmetric.txt",        
        "/distances/err/non-zero-diagonal-values.csv",        
        "/distances/err/non-zero-diagonal-values.txt",        
        "/distances/err/too-few-names.txt",        
        "/distances/err/too-few-ids.txt",        
        "/distances/err/missing-rows.txt",
        "/distances/err/names-after-data.txt",
        "/distances/err/ids-after-data.txt",
        "/distances/err/duplicate-ids.txt"
    };
    
    private static final SymmetricMatrixFormat[] ERRONEOUS_FILE_FORMATS = {
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.LOWER,
        SymmetricMatrixFormat.LOWER,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.LOWER_DIAG,
        SymmetricMatrixFormat.LOWER,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.LOWER_DIAG,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL
    };
    
    private boolean withNames;
    private boolean withUniqueIdentifiers;
    private String datasetName;

    @BeforeClass
    public static void beforeClass(){
        System.out.println("Test simple distance matrix data");
    }
    
    @AfterClass
    public static void afterClass(){
        System.out.println("Done");
    }
    
    @Test
    public void inMemoryWithHeaders() {
        System.out.println(" |- In memory test with headers");
        datasetName = NAME;
        withNames = true;
        withUniqueIdentifiers = true;
        testData(new SimpleDistanceMatrixData(NAME, HEADERS, DISTANCES));
    }
    
    @Test
    public void inMemory() {
        System.out.println(" |- In memory test without headers");
        datasetName = null;
        withNames = false;
        withUniqueIdentifiers = false;
        testData(new SimpleDistanceMatrixData(DISTANCES));
    }

    @Test
    public void fromFile() throws IOException {
        datasetName = "full.txt";
        withNames = false;
        withUniqueIdentifiers = false;
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(TXT_FULL).getPath()),
                FileType.TXT, SymmetricMatrixFormat.FULL
        ));
    }
    
    @Test
    public void fromFileWithNames() throws IOException {
        datasetName = "full-names.txt";
        withNames = true;
        withUniqueIdentifiers = false;
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(TXT_FULL_NAMES).getPath()),
                FileType.TXT, SymmetricMatrixFormat.FULL
        ));
    }
    
    @Test
    public void fromFileWithNamesAndIdentifiers() throws IOException {
        datasetName = "full-names-ids.txt";
        withNames = true;
        withUniqueIdentifiers = true;
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(TXT_FULL_NAMES_IDS).getPath()),
                FileType.TXT, SymmetricMatrixFormat.FULL
        ));
    }
    
    @Test
    public void fromFileLower() throws IOException {
        datasetName = "lower.csv";
        withNames = false;
        withUniqueIdentifiers = false;
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(CSV_LOWER).getPath()),
                FileType.CSV, SymmetricMatrixFormat.LOWER
        ));
    }
    
    @Test
    public void fromFileLowerDiagWithNames() throws IOException {
        datasetName = "lower-diag-names.csv";
        withNames = true;
        withUniqueIdentifiers = false;
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(CSV_LOWER_DIAG_NAMES).getPath()),
                FileType.CSV, SymmetricMatrixFormat.LOWER_DIAG
        ));
    }
    
    @Test
    public void testErroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        for(int i = 0; i < ERRONEOUS_FILES.length; i++){
            Path file = Paths.get(SimpleDistanceMatrixDataTest.class.getResource(ERRONEOUS_FILES[i]).getPath());
            System.out.print("  |- " + file.getFileName().toString() + ": ");
            FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
            boolean thrown = false;
            try {
                SimpleDistanceMatrixData.readData(file, type, ERRONEOUS_FILE_FORMATS[i]);
            } catch (IOException ex){
                thrown = true;
                System.out.print(ex.getMessage());
            } finally {
                System.out.println();
            }
            assertTrue("File " + file + " should throw exception.", thrown);
        }
    }

    private void testData(SimpleDistanceMatrixData data) {
        
        // check dataset name, if set
        String expectedDatasetName = datasetName != null ? datasetName : "Precomputed distance matrix";
        assertEquals("Incorrect dataset name.", expectedDatasetName, data.getDatasetName());
        
        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());

        int size = data.getDatasetSize();

        for (int i = 0; i < size; i++) {
            
            // check names, if assigned
            if(withNames){
                assertEquals("Name for " + i + " not correct.", NAMES[i], data.getHeader(i).getName());
            }
            
            // check unique identifiers, if assigned
            if(withUniqueIdentifiers){
                assertEquals("Unique identifier for " + i + " not correct.",
                             UNIQUE_IDENTIFIERS[i], data.getHeader(i).getUniqueIdentifier());
            }
            
            // check headers if both identifiers and names assigned
            if(withNames && withUniqueIdentifiers){
                assertEquals("Header for " + i + " not correct.", HEADERS[i], data.getHeader(i));
            }

            // check distances
            for (int j = 0; j < size; j++) {
                assertEquals("Distance[" + i + "][" + j + "] not correct!",
                             DISTANCES[i][j], data.getDistance(i, j), PRECISION);
            }
            
        }
    }
}
