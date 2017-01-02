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

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uno.informatics.data.io.FileType;
import uno.informatics.data.SimpleEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleDistanceMatrixDataTest {

    private static final String TXT_FULL_IDS = "/distances/full-ids.txt";
    private static final String TXT_FULL_IDS_NAMES = "/distances/full-ids-names.txt";
    private static final String CSV_LOWER_DIAG_IDS = "/distances/lower-diag-ids.csv";
    private static final String TEST_OUTPUT = "target/testoutput";

    private static final String ERRONEOUS_FILES_DIR = "/distances/err/";
    private static final int[] SOLUTION = new int[] {
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

        List<Integer> ids = new ArrayList<Integer>(distanceData.getIDs());

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "DistanceMatrix-Csv-AllIds");

        // solution in natural order
        SubsetSolution solution = new SubsetSolution(new TreeSet<Integer>(ids));

        for (int i = 0; i < SOLUTION.length; ++i) {
            solution.select(SOLUTION[i]);
        }

        dataName = "out1.csv";

        Path path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with solution) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, true, true);

        assertTrue("Output 1 is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/DistanceMatrix-Csv-AllIds1.csv").getPath()),
                    path.toFile()));

        dataName = "out2.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with solution) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, true, true);

        assertTrue("Output 2 is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/DistanceMatrix-Csv-AllIds2.csv").getPath()),
                    path.toFile()));
        
        // solution in reverse natural order
        solution = new SubsetSolution(new TreeSet<Integer>(ids), Comparator.reverseOrder());
        
        for (int i = 0; i < SOLUTION.length; ++i) {
            solution.select(SOLUTION[i]);
        }
        
        dataName = "out3.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with solution reverse order) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, true, true);

        assertTrue("Output 3 is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/DistanceMatrix-Csv-AllIds3.csv").getPath()),
                    path.toFile()));

        dataName = "out4.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with solution reverse order) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, true, true);

        assertTrue("Output 4 is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/distances/out/DistanceMatrix-Csv-AllIds4.csv").getPath()),
                    path.toFile()));
    }

    @Test
    public void toCsvFileWithSelectedlIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;

        SimpleDistanceMatrixData distanceData = new SimpleDistanceMatrixData(expectedHeaders, DISTANCES);

        List<Integer> ids = new ArrayList<Integer>(distanceData.getIDs());

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "DistanceMatrix-Csv-SelectedlIds");

        // solution in natural order
        SubsetSolution solution = new SubsetSolution(new TreeSet<Integer>(ids));

        for (int i = 0; i < SOLUTION.length; ++i) {
            solution.select(SOLUTION[i]);
        }

        dataName = "out1.csv";

        Path path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with selected) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, true, false);

        assertTrue("Output 1 is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class
                    .getResource("/distances/out/DistanceMatrix-Csv-SelectedlIds1.csv").getPath()),
                path.toFile()));

        dataName = "out2.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with selected and index) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, true, false);

        assertTrue("Output 2 is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class
                    .getResource("/distances/out/DistanceMatrix-Csv-SelectedlIds2.csv").getPath()),
                path.toFile()));
        
        // solution in reverse natural order
        solution = new SubsetSolution(new TreeSet<Integer>(ids), Comparator.reverseOrder());
        
        for (int i = 0; i < SOLUTION.length; ++i) {
            solution.select(SOLUTION[i]);
        }
       
        dataName = "out3.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with selected reverse order) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, true, false);

        assertTrue("Output 3 is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class
                    .getResource("/distances/out/DistanceMatrix-Csv-SelectedlIds3.csv").getPath()),
                path.toFile()));

        dataName = "out4.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with selected reverse order and index) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, true, false);

        assertTrue("Output 4 is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class
                    .getResource("/distances/out/DistanceMatrix-Csv-SelectedlIds4.csv").getPath()),
                path.toFile()));
    }

    @Test
    public void toCsvFileWithUnselectedlIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;

        SimpleDistanceMatrixData distanceData = new SimpleDistanceMatrixData(expectedHeaders, DISTANCES);

        List<Integer> ids = new ArrayList<Integer>(distanceData.getIDs());

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "DistanceMatrix-Csv-UnselectedlIds");

        // solution in natural order
        SubsetSolution solution = new SubsetSolution(new TreeSet<Integer>(ids));

        for (int i = 0; i < SOLUTION.length; ++i) {
            solution.select(SOLUTION[i]);
        }

        dataName = "out1.csv";

        Path path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with unselected) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, false, true);

        assertTrue("Output 1 is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class
                    .getResource("/distances/out/DistanceMatrix-Csv-UnselectedlIds1.csv").getPath()),
                path.toFile()));

        dataName = "out2.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with unselected and index) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, false, true);

        assertTrue("Output 2 is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class
                    .getResource("/distances/out/DistanceMatrix-Csv-UnselectedlIds2.csv").getPath()),
                path.toFile()));
        
        // solution in reverse natural order
        solution = new SubsetSolution(new TreeSet<Integer>(ids), Comparator.reverseOrder());
        
        for (int i = 0; i < SOLUTION.length; ++i) {
            solution.select(SOLUTION[i]);
        }
        
        dataName = "out3.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with unselected reverse order) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, false, false, true);

        assertTrue("Output 3 is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class
                    .getResource("/distances/out/DistanceMatrix-Csv-UnselectedlIds3.csv").getPath()),
                path.toFile()));

        dataName = "out4.csv";

        path = Paths.get(dirPath.toString(), dataName);

        Files.deleteIfExists(path);

        System.out.println(" |- Write distance File (with unselected reverse order and index) " + dataName);

        distanceData.writeData(path, FileType.CSV, solution, true, false, true);

        assertTrue("Output 4 is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class
                    .getResource("/distances/out/DistanceMatrix-Csv-UnselectedlIds4.csv").getPath()),
                path.toFile()));
    }

    @Test
    public void testErroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        Path dir = Paths.get(SimpleGenotypeDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
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
