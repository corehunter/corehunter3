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

import java.io.File;
import static org.corehunter.tests.TestData.ALLELE_OBS_DIPLOID;
import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES_DIPLOID;
import static org.corehunter.tests.TestData.ALLELE_OBS_HOMOZYGOUS;
import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES_HOMOZYGOUS;
import static org.corehunter.tests.TestData.ALLELE_NAMES_DIPLOID;
import static org.corehunter.tests.TestData.ALLELE_NAMES_HOMOZYGOUS;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES_DEFAULT;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;
import org.corehunter.data.FrequencyGenotypeData;
import org.corehunter.data.simple.SimpleDefaultGenotypeData;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleDefaultGenotypeDataTest {
    
    private static final String DIPLOID_TXT_IDS = "/diploid_genotypes/ids.txt";
    private static final String DIPLOID_CSV_IDS = "/diploid_genotypes/ids.csv";
    private static final String DIPLOID_CSV_IDS_NAMES = "/diploid_genotypes/ids-and-names.csv";
    private static final String DIPLOID_LONG_FILE = "/diploid_genotypes/diploid_genotypic_data.csv";
    
    private static final String HOMOZYGOUS_TXT_IDS = "/homozygous_genotypes/ids.txt";
    private static final String HOMOZYGOUS_CSV_IDS = "/homozygous_genotypes/ids.csv";
    private static final String HOMOZYGOUS_CSV_IDS_NAMES = "/homozygous_genotypes/ids-and-names.csv";
    private static final String HOMOZYGOUS_LONG_FILE = "/homozygous_genotypes/homozygous_genotypic_data.csv";
    
    private static final String ERRONEOUS_FILES_DIR = "/diploid_genotypes/err/";
    private static final String TEST_OUTPUT = "target/testoutput";
    
    private static final int[] SELECTION = new int[] {
        1, 3, 4
    };
    
    private SimpleEntity[] expectedHeaders;
    private String[] expectedMarkerNames;
    private String[][] expectedAlleleNames;
    private String dataName;
    
    @BeforeClass
    public static void beforeClass(){
        System.out.println("Test simple default genotype variant data");
    }
    
    @AfterClass
    public static void afterClass(){
        System.out.println("Done");
    }
    
    /**************/
    /* HOMOZYGOUS */
    /**************/
    
    @Test
    public void homozygousInMemory() {
        System.out.println(" |- In memory test (homozygous)");
        dataName = NAME;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_HOMOZYGOUS;
        testDataHomozygous(new SimpleDefaultGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_HOMOZYGOUS
        ));
    }
    
    @Test
    public void homozygousFromCsvFileWithIdsAndNames() throws IOException {
        dataName = "ids-and-names.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_HOMOZYGOUS;
        System.out.println(" |- File homozygous/" + dataName);
        testDataHomozygous(
                SimpleDefaultGenotypeData.readData(
                        Paths.get(SimpleDefaultGenotypeDataTest.class.getResource(HOMOZYGOUS_CSV_IDS_NAMES).getPath()),
                        FileType.CSV
                )
        );
    }
    
    @Test
    public void homozygousFromTxtFileWithIds() throws IOException {
        dataName = "ids.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_HOMOZYGOUS;
        System.out.println(" |- File homozygous/" + dataName);
        testDataHomozygous(
                SimpleDefaultGenotypeData.readData(
                        Paths.get(SimpleDefaultGenotypeDataTest.class.getResource(HOMOZYGOUS_TXT_IDS).getPath()),
                        FileType.TXT
                )
        );
    }
    
    @Test
    public void homozygousFromCsvFileWithIds() throws IOException {
        dataName = "ids.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_HOMOZYGOUS;
        System.out.println(" |- File homozygous/" + dataName);
        testDataHomozygous(
                SimpleDefaultGenotypeData.readData(
                        Paths.get(SimpleDefaultGenotypeDataTest.class.getResource(HOMOZYGOUS_CSV_IDS).getPath()),
                        FileType.CSV
                )
        );
    }
    
    @Test
    public void homozygousfromCsvLargeFiles() throws IOException {
        SimpleDefaultGenotypeData.readData(
                Paths.get(SimpleDefaultGenotypeData.class.getResource(HOMOZYGOUS_LONG_FILE).getPath()),
                FileType.CSV
        );
    }
    
    @Test
    public void homozygousToCsvFile() throws IOException {
        dataName = "out.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_HOMOZYGOUS;
        
        SimpleDefaultGenotypeData genotypicData = new SimpleDefaultGenotypeData(
                NAME, HEADERS_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_HOMOZYGOUS
        );
        
        Path path = Paths.get(TEST_OUTPUT);
        
        Files.createDirectories(path);
        
        path = Files.createTempDirectory(path, "GenoHomozygous-Csv");
        
        path = Paths.get(path.toString(), dataName);
                
        System.out.println(" |- Write homozygous File " + dataName);
        genotypicData.writeData(path, FileType.CSV);
        
        System.out.println(" |- Read written File " + dataName);
        testDataHomozygous(SimpleDefaultGenotypeData.readData(path, FileType.CSV));
    }
    
    @Test
    public void homozygousToCsvFileWithAllIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_HOMOZYGOUS;
        
        SimpleDefaultGenotypeData genotypicData = new SimpleDefaultGenotypeData(
                NAME, HEADERS_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_HOMOZYGOUS
        );
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoHomozygous-AllIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (homozygous, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/homozygous_genotypes/out/all-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (homozygous, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/homozygous_genotypes/out/all-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void homozygousToCsvFileWithSelectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_HOMOZYGOUS;
        
        SimpleDefaultGenotypeData genotypicData = new SimpleDefaultGenotypeData(
                NAME, HEADERS_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_HOMOZYGOUS
        );
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoHomozygous-SelectedIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (homozygous, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, false, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/homozygous_genotypes/out/sel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (homozygous, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, false, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/homozygous_genotypes/out/sel-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void homozygousToCsvFileWithUnselectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_HOMOZYGOUS;
        
        SimpleDefaultGenotypeData genotypicData = new SimpleDefaultGenotypeData(
                NAME, HEADERS_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_HOMOZYGOUS
        );
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoHomozygous-UnselectedIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (homozygous, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, false, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/homozygous_genotypes/out/unsel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (homozygous, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, false, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/homozygous_genotypes/out/unsel-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    /***********/
    /* DIPLOID */
    /***********/

    @Test
    public void diploidInMemory() {
        System.out.println(" |- In memory test (diploid)");
        dataName = NAME;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        testDataDiploid(new SimpleDefaultGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_DIPLOID
        ));
    }
    
    @Test
    public void diploidFromTxtFileWithIds() throws IOException {
        dataName = "ids.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        System.out.println(" |- File diploid/" + dataName);
        testDataDiploid(
                SimpleDefaultGenotypeData.readData(
                        Paths.get(SimpleDefaultGenotypeDataTest.class.getResource(DIPLOID_TXT_IDS).getPath()),
                        FileType.TXT
                )
        );
    }
    
    @Test
    public void diploidFromCsvFileWithIds() throws IOException {
        dataName = "ids.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        System.out.println(" |- File diploid/" + dataName);
        testDataDiploid(
                SimpleDefaultGenotypeData.readData(
                        Paths.get(SimpleDefaultGenotypeDataTest.class.getResource(DIPLOID_CSV_IDS).getPath()),
                        FileType.CSV
                )
        );
    }
    
    @Test
    public void diploidFromCsvFileWithIdsAndNames() throws IOException {
        dataName = "ids-and-names.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        System.out.println(" |- File diploid/" + dataName);
        testDataDiploid(
                SimpleDefaultGenotypeData.readData(
                        Paths.get(SimpleDefaultGenotypeDataTest.class.getResource(DIPLOID_CSV_IDS_NAMES).getPath()),
                        FileType.CSV
                )
        );
    }
    
    @Test
    public void diploidFromCsvLargeFiles() throws IOException {
        SimpleDefaultGenotypeData.readData(
                Paths.get(SimpleDefaultGenotypeDataTest.class.getResource(DIPLOID_LONG_FILE).getPath()),
                FileType.CSV
        );
    }
    
    @Test
    public void diploidToCsvFile() throws IOException {
        dataName = "out.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        
        SimpleDefaultGenotypeData genotypicData = new SimpleDefaultGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_DIPLOID
        );
        
        Path path = Paths.get(TEST_OUTPUT);
        
        Files.createDirectories(path);
        
        path = Files.createTempDirectory(path, "GenoDiploid-Csv");
        
        path = Paths.get(path.toString(), dataName);
                
        System.out.println(" |- Write diploid File " + dataName);
        genotypicData.writeData(path, FileType.CSV);
        
        System.out.println(" |- Read written File " + dataName);
        testDataDiploid(SimpleDefaultGenotypeData.readData(path, FileType.CSV));
    }
    
    @Test
    public void diploidToCsvFileWithAllIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        
        SimpleDefaultGenotypeData genotypicData = new SimpleDefaultGenotypeData(
                NAME, HEADERS_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_DIPLOID
        );
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoDiploid-AllIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (diploid, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/diploid_genotypes/out/all-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (diploid, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/diploid_genotypes/out/all-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void diploidToCsvFileWithSelectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        
        SimpleDefaultGenotypeData genotypicData = new SimpleDefaultGenotypeData(
                NAME, HEADERS_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_DIPLOID
        );
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoDiploid-SelectedIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (diploid, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, false, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/diploid_genotypes/out/sel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (diploid, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, false, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/diploid_genotypes/out/sel-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void diploidToCsvFileWithUnselectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DEFAULT;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        
        SimpleDefaultGenotypeData genotypicData = new SimpleDefaultGenotypeData(
                NAME, HEADERS_UNIQUE_NAMES, MARKER_NAMES_DEFAULT, ALLELE_OBS_DIPLOID
        );
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoDiploid-UnselectedIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (diploid, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, false, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/diploid_genotypes/out/unsel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write default genotypes file (diploid, with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, false, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/diploid_genotypes/out/unsel-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void diploidErroneousFiles() throws IOException {
        System.out.println(" |- Test diploid erroneous files:");
        Path dir = Paths.get(SimpleDefaultGenotypeDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
        try(DirectoryStream<Path> directory = Files.newDirectoryStream(dir)){
            for(Path file : directory){
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimpleDefaultGenotypeData.readData(file, type);
                } catch (IOException ex){
                    thrown = true;
                    System.out.print(ex.getMessage());
                } finally {
                    System.out.println();
                }
                assertTrue("File " + file + " should throw exception.", thrown);
            }
        }
    }
    
    /*********/
    /* CHECK */
    /*********/
    
    private void testData(FrequencyGenotypeData data, Double[][][] freqs) {
        
        // check dataset name, if set
        String expectedDatasetName = dataName != null ? dataName : "Multiallelic marker data";
        assertEquals("Incorrect data name.", expectedDatasetName, data.getName());
        
        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());
        
        // check number of markers
        assertEquals("Number of markers is not correct.", expectedMarkerNames.length, data.getNumberOfMarkers());
        // check total number of alleles
        assertEquals("Incorrect total number of alleles.", Arrays.stream(expectedAlleleNames)
                                                                 .mapToInt(names -> names.length)
                                                                 .sum(), data.getTotalNumberOfAlleles());
        
        // check marker names + allele counts and names
        for(int m = 0; m < data.getNumberOfMarkers(); m++){
            
            assertEquals("Marker name for marker " + m + " is not correct.",
                         expectedMarkerNames[m], data.getMarkerName(m));

            assertEquals("Number of alelles for marker " + m + " is not correct.",
                         expectedAlleleNames[m].length, data.getNumberOfAlleles(m));

            for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                assertEquals("Allele name for allele " + a + " of marker " + m + " is not correct.",
                             expectedAlleleNames[m][a], data.getAlleleName(m, a));
            }
            
        }
        
        // check individuals (headers and frequencies)
        int size = data.getSize();

        for (int i = 0; i < size; i++) {
            
            // check header
            assertEquals("Header for individual " + i + " is not correct.", expectedHeaders[i], data.getHeader(i));
            // check name and id separately
            if(expectedHeaders[i] != null){
                assertNotNull("Header not defined for individual " + i + ".", data.getHeader(i));
                assertEquals("Name for individual " + i + " is not correct.",
                             expectedHeaders[i].getName(), data.getHeader(i).getName());
                assertEquals("Id for individual " + i + " is not correct.",
                             expectedHeaders[i].getUniqueIdentifier(), data.getHeader(i).getUniqueIdentifier());
            }

            // check frequencies
            for (int m = 0; m < data.getNumberOfMarkers(); m++) {
                for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                    if(freqs[i][m][a] == null){
                        assertNull("Frequency should be missing for allele " + a
                                 + " of marker " + m + " in individual " + i + ".",
                                data.getAlleleFrequency(i, m, a));
                    } else {
                        assertNotNull("Frequency should not be missing for allele " + a
                                    + " of marker " + m + " in individual " + i + ".",
                                   data.getAlleleFrequency(i, m, a));
                        assertEquals("Incorrect frequency for allele " + a
                               + " of marker " + m + " in individual " + i + ".",
                               freqs[i][m][a],
                               data.getAlleleFrequency(i, m, a),
                               PRECISION);
                    }
                }
                
            }
            
        }
        
    }
    
    private void testDataDiploid(FrequencyGenotypeData data) {
        testData(data, ALLELE_FREQUENCIES_DIPLOID);
    }
    
    private void testDataHomozygous(FrequencyGenotypeData data) {
        testData(data, ALLELE_FREQUENCIES_HOMOZYGOUS);
    }
    
}
