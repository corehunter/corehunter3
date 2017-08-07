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
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.jamesframework.core.subset.SubsetSolution;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleDistanceMatrixDataTest {

    private static final String TXT_FULL_IDS = "/distances/full-ids.txt";
    private static final String TXT_FULL_IDS_NAMES = "/distances/full-ids-names.txt";
    private static final String CSV_LOWER_DIAG_IDS = "/distances/lower-diag-ids.csv";
    private static final String TEST_OUTPUT = "target/testoutput";
    private static final String LONG_FILE = "/distances/distances_data.csv";
    
    private static final String ERRONEOUS_FILES_DIR = "/distances/err/";
    private static final int[] SELECTION = new int[] {
        1, 3, 4
    };

    private SimpleEntity[] expectedHeaders;
    private String dataName;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Test simple distance matrix data");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("Done");
    }

    @Test
    public void inMemoryWithHeaders() {
        System.out.println(" |- In memory test with headers");
        dataName = NAME;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        testData(new SimpleDistanceMatrixData(NAME, HEADERS_NON_UNIQUE_NAMES, DISTANCES));
    }

    @Test
    public void fromFileWithIds() throws IOException {
        dataName = "full-ids.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        System.out.println(" |- File " + dataName);
        testData(SimpleDistanceMatrixData.readData(
            Paths.get(SimpleDistanceMatrixDataTest.class.getResource(TXT_FULL_IDS).getPath()), FileType.TXT));
    }

    @Test
    public void fromFileWithIdsAndNames() throws IOException {
        dataName = "full-ids-names.txt";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        System.out.println(" |- File " + dataName);
        testData(SimpleDistanceMatrixData.readData(
            Paths.get(SimpleDistanceMatrixDataTest.class.getResource(TXT_FULL_IDS_NAMES).getPath()),
            FileType.TXT));
    }

    @Test
    public void fromFileLowerDiagWithIds() throws IOException {
        dataName = "lower-diag-ids.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        System.out.println(" |- File " + dataName);
        testData(SimpleDistanceMatrixData.readData(
            Paths.get(SimpleDistanceMatrixDataTest.class.getResource(CSV_LOWER_DIAG_IDS).getPath()),
            FileType.CSV));
    }
    
    @Test
    public void fromFileLargeFiles() throws IOException {
        SimpleDistanceMatrixData.readData(
            Paths.get(SimpleDistanceMatrixDataTest.class.getResource(LONG_FILE).getPath()),
            FileType.CSV);
    }

    @Test
    public void toCsvFile() throws IOException {
        dataName = "out.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;

        SimpleDistanceMatrixData distanceData = new SimpleDistanceMatrixData(expectedHeaders, DISTANCES);

        Path path = Paths.get(TEST_OUTPUT);

        Files.createDirectories(path);

        path = Files.createTempDirectory(path, "DistanceMatrix-Csv");

        path = Paths.get(path.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File " + dataName);
        distanceData.writeData(path, FileType.CSV);

        testData(SimpleDistanceMatrixData.readData(path, FileType.CSV));
    }

    @Test
    public void toCsvFileWithAllIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        SimpleDistanceMatrixData distanceData = new SimpleDistanceMatrixData(expectedHeaders, DISTANCES);

        Set<Integer> ids = distanceData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "DistanceMatrix-AllIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write distance matrix file (with solution) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/all-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write distance matrix file (with solution) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/all-no-ids.csv").getPath()),
                    path.toFile()));
        
    }
    
    @Test
    public void toCsvFileWithSelectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        SimpleDistanceMatrixData distanceData = new SimpleDistanceMatrixData(expectedHeaders, DISTANCES);

        Set<Integer> ids = distanceData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "DistanceMatrix-SelectedIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write distance matrix file (with solution) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/sel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write distance matrix file (with solution) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/sel-no-ids.csv").getPath()),
                    path.toFile()));
        
    }
    
    @Test
    public void toCsvFileWithUnselectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        SimpleDistanceMatrixData distanceData = new SimpleDistanceMatrixData(expectedHeaders, DISTANCES);

        Set<Integer> ids = distanceData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "DistanceMatrix-UnselectedIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write distance matrix file (with solution) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, false, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/unsel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write distance matrix file (with solution) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, false, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/unsel-no-ids.csv").getPath()),
                    path.toFile()));
        
    }

    @Test
    public void testErroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        Path dir = Paths.get(SimpleFrequencyGenotypeDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
        try (DirectoryStream<Path> directory = Files.newDirectoryStream(dir)) {
            for (Path file : directory) {
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimpleDistanceMatrixData.readData(file, type);
                } catch (IOException ex) {
                    thrown = true;
                    System.out.print(ex.getMessage());
                } finally {
                    System.out.println();
                }
                assertTrue("File " + file + " should throw exception.", thrown);
            }
        }
    }

    private void testData(SimpleDistanceMatrixData data) {

        // check data name, if set
        String expectedDatasetName = dataName != null ? dataName : "Precomputed distance matrix";
        assertEquals("Incorrect data name.", expectedDatasetName, data.getName());

        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());

        int size = data.getSize();

        // check items (headers and distances)
        for (int i = 0; i < size; i++) {

            // check header
            assertEquals("Header for individual " + i + " is not correct.", expectedHeaders[i],
                data.getHeader(i));
            // check name and id separately
            if (expectedHeaders[i] != null) {
                assertNotNull("Header not defined for individual " + i + ".", data.getHeader(i));
                assertEquals("Name for individual " + i + " is not correct.", expectedHeaders[i].getName(),
                    data.getHeader(i).getName());
                assertEquals("Id for individual " + i + " is not correct.",
                    expectedHeaders[i].getUniqueIdentifier(), data.getHeader(i).getUniqueIdentifier());
            }

            // check distances
            for (int j = 0; j < size; j++) {
                assertEquals("Distance[" + i + "][" + j + "] not correct!", DISTANCES[i][j],
                    data.getDistance(i, j), PRECISION);
            }

        }
    }
}
