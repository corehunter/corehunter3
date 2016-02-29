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
    private static final String CSV_LOWER = "/distances/lower.csv";
    private static final String CSV_LOWER_DIAG_NAMES = "/distances/lower-diag-names.csv";
    
    private static final String[] ERRONEOUS_FILES = {
        "/distances/err/duplicate-names.txt",
        "/distances/err/empty.txt",        
        "/distances/err/empty.txt",        
        "/distances/err/excessive-names.txt",        
        "/distances/err/incorrect-row-length.csv",        
        "/distances/err/incorrect-row-length-2.csv",        
        "/distances/err/incorrect-row-length.txt",        
        "/distances/err/names-only.txt",        
        "/distances/err/negative-values.csv",        
        "/distances/err/no-names.txt",        
        "/distances/err/no-names.csv",        
        "/distances/err/non-symmetric.txt",        
        "/distances/err/non-zero-diagonal-values.csv",        
        "/distances/err/non-zero-diagonal-values.txt",        
        "/distances/err/too-few-names.txt",        
        "/distances/err/unexpected-names.txt"      
    };
    
    private static final SymmetricMatrixFormat[] ERRONEOUS_FILE_FORMATS = {
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.LOWER,
        SymmetricMatrixFormat.LOWER,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.LOWER_DIAG,
        SymmetricMatrixFormat.LOWER,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.LOWER,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.LOWER_DIAG,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL,
        SymmetricMatrixFormat.FULL
    };
    
    private static final boolean[] ERRONEOUS_FILE_NAMES = {
        true,
        true,
        false,
        true,
        false,
        false,
        true,
        true,
        false,
        true,
        true,
        false,
        true,
        false,
        true,
        false
    };
    
    private boolean withNames;
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
    public void inMemoryWithNames() {
        System.out.println(" |- In memory test with names");
        withNames = true;
        datasetName = NAME;
        testData(new SimpleDistanceMatrixData(NAME, NAMES, DISTANCES));
    }
    
    @Test
    public void inMemory() {
        System.out.println(" |- In memory test without names");
        withNames = false;
        datasetName = null;
        testData(new SimpleDistanceMatrixData(DISTANCES));
    }

    @Test
    public void fromFile() throws IOException {
        withNames = false;
        datasetName = "full.txt";
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(TXT_FULL).getPath()),
                FileType.TXT, SymmetricMatrixFormat.FULL, withNames
        ));
    }
    
    @Test
    public void fromFileWithNames() throws IOException {
        withNames = true;
        datasetName = "full-names.txt";
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(TXT_FULL_NAMES).getPath()),
                FileType.TXT, SymmetricMatrixFormat.FULL, withNames
        ));
    }
    
    @Test
    public void fromFileLower() throws IOException {
        withNames = false;
        datasetName = "lower.csv";
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(CSV_LOWER).getPath()),
                FileType.CSV, SymmetricMatrixFormat.LOWER, withNames
        ));
    }
    
    @Test
    public void fromFileLowerDiagWithNames() throws IOException {
        withNames = true;
        datasetName = "lower-diag-names.csv";
        System.out.println(" |- File " + datasetName);
        testData(SimpleDistanceMatrixData.readData(
                Paths.get(SimpleDistanceMatrixDataTest.class.getResource(CSV_LOWER_DIAG_NAMES).getPath()),
                FileType.CSV, SymmetricMatrixFormat.LOWER_DIAG, withNames
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
                SimpleDistanceMatrixData.readData(file, type, ERRONEOUS_FILE_FORMATS[i], ERRONEOUS_FILE_NAMES[i]);
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
            
            // check accession name
            if(withNames){
                assertEquals("Name for " + i + " not correct.", NAMES[i], data.getName(i));
            }

            // check distances
            for (int j = 0; j < size; j++) {
                assertEquals("Distance[" + i + "][" + j + "] not correct!",
                             DISTANCES[i][j], data.getDistance(i, j), PRECISION);
            }
            
        }
    }
}
