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

import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.corehunter.tests.TestData.UNDEFINED_ALLELE_NAMES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.corehunter.data.simple.SimpleFrequencyGenotypeData;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;
import org.corehunter.data.FrequencyGenotypeData;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleFrequencyGenotypeDataTest {

    private static final String TXT_IDS = "/frequency_genotypes/ids.txt";
    private static final String CSV_IDS = "/frequency_genotypes/ids.csv";
    private static final String CSV_IDS_NAMES = "/frequency_genotypes/ids-and-names.csv";
    private static final String CSV_NO_ALLELE_NAMES = "/frequency_genotypes/no-allele-names.csv";
    private static final String FREQUENCY_LONG_FILE = "/frequency_genotypes/frequency_genotypic_data.csv";
    
    private static final String ERRONEOUS_FILES_DIR = "/frequency_genotypes/err/";
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
        System.out.println("Test simple frequency genotype variant data");
    }
    
    @AfterClass
    public static void afterClass(){
        System.out.println("Done");
    }
    
    @Test
    public void inMemory() {
        System.out.println(" |- In memory test");
        dataName = null;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        testData(new SimpleFrequencyGenotypeData(
                HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        ));
    }
    
    @Test
    public void inMemoryWithName() {
        System.out.println(" |- In memory test with dataset name");
        dataName = NAME;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        testData(new SimpleFrequencyGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        ));
    }

    @Test
    public void fromTxtFileWithIds() throws IOException {
        dataName = "ids.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        System.out.println(" |- File " + dataName);
        testData(
                SimpleFrequencyGenotypeData.readData(
                        Paths.get(SimpleFrequencyGenotypeDataTest.class.getResource(TXT_IDS).getPath()),
                        FileType.TXT
                )
        );
    }
    
    @Test
    public void fromCsvFileWithIds() throws IOException {
        dataName = "ids.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        System.out.println(" |- File " + dataName);
        testData(
                SimpleFrequencyGenotypeData.readData(
                        Paths.get(SimpleFrequencyGenotypeDataTest.class.getResource(CSV_IDS).getPath()),
                        FileType.CSV
                )
        );
    }
    
    @Test
    public void fromCsvFileWithIdsAndNames() throws IOException {
        dataName = "ids-and-names.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        System.out.println(" |- File " + dataName);
        testData(
                SimpleFrequencyGenotypeData.readData(
                        Paths.get(SimpleFrequencyGenotypeDataTest.class.getResource(CSV_IDS_NAMES).getPath()),
                        FileType.CSV
                )
        );
    }
    
    @Test
    public void fromCsvFileWithoutAlleleNames() throws IOException {
        dataName = "no-allele-names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = UNDEFINED_ALLELE_NAMES;
        System.out.println(" |- File " + dataName);
        testData(
                SimpleFrequencyGenotypeData.readData(
                        Paths.get(SimpleFrequencyGenotypeDataTest.class.getResource(CSV_NO_ALLELE_NAMES).getPath()),
                        FileType.CSV
                )
        );
    }
    
    @Test
    public void fromCsvLargeFiles() throws IOException {
        SimpleFrequencyGenotypeData.readData(
                Paths.get(SimpleFrequencyGenotypeDataTest.class.getResource(FREQUENCY_LONG_FILE).getPath()),
                FileType.CSV
        );
    }
    
    @Test
    public void toTxtFile() throws IOException {
        dataName = "out.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = UNDEFINED_ALLELE_NAMES;
        
        SimpleFrequencyGenotypeData genotypicData = new SimpleFrequencyGenotypeData(expectedHeaders, 
                expectedMarkerNames, expectedAlleleNames, ALLELE_FREQUENCIES) ;
        
        Path path = Paths.get(TEST_OUTPUT) ;
        
        Files.createDirectories(path) ;
        
        path = Files.createTempDirectory(path, "GenoFreqs-Txt") ;
        
        path = Paths.get(path.toString(), dataName) ;
        
        Files.deleteIfExists(path) ;
        
        System.out.println(" |- Write File " + dataName);
        genotypicData.writeData(path, FileType.TXT);
        
        System.out.println(" |- Read written File " + dataName);
        testData(SimpleFrequencyGenotypeData.readData(path, FileType.TXT));
    }
    
    @Test
    public void toCsvFileWithAlleleNames() throws IOException {
        dataName = "out.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        
        SimpleFrequencyGenotypeData genotypicData = new SimpleFrequencyGenotypeData(expectedHeaders, 
                expectedMarkerNames, expectedAlleleNames, ALLELE_FREQUENCIES) ;
        
        Path path = Paths.get(TEST_OUTPUT) ;
        
        Files.createDirectories(path) ;
        
        path = Files.createTempDirectory(path, "GenoFreqs-CsvAlleleNames") ;
        
        path = Paths.get(path.toString(), dataName) ;
                
        System.out.println(" |- Write File " + dataName);
        genotypicData.writeData(path, FileType.CSV);
        
        System.out.println(" |- Read written File " + dataName);
        testData(SimpleFrequencyGenotypeData.readData(path, FileType.CSV));
    }
    
    @Test
    public void toCsvFileWithAllIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        
        SimpleFrequencyGenotypeData genotypicData = new SimpleFrequencyGenotypeData(
                expectedHeaders, expectedMarkerNames, expectedAlleleNames, ALLELE_FREQUENCIES
        );
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoFreqs-AllIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write frequency genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/frequency_genotypes/out/all-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write frequency genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/frequency_genotypes/out/all-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void toCsvFileWithSelectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        
        SimpleFrequencyGenotypeData genotypicData = new SimpleFrequencyGenotypeData(expectedHeaders, 
                expectedMarkerNames, expectedAlleleNames, ALLELE_FREQUENCIES) ;
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoFreqs-SelectedIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write frequency genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, false, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/frequency_genotypes/out/sel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write frequency genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, false, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/frequency_genotypes/out/sel-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void toCsvFileWithUnselectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        
        SimpleFrequencyGenotypeData genotypicData = new SimpleFrequencyGenotypeData(expectedHeaders, 
                expectedMarkerNames, expectedAlleleNames, ALLELE_FREQUENCIES) ;
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoFreqs-UnselectedIds");
        
        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write frequency genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, false, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/frequency_genotypes/out/unsel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write without integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write frequency genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, false, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/frequency_genotypes/out/unsel-no-ids.csv").getPath()),
                    path.toFile()));

    }

    @Test
    public void erroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        Path dir = Paths.get(SimpleFrequencyGenotypeDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
        try(DirectoryStream<Path> directory = Files.newDirectoryStream(dir)){
            for(Path file : directory){
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimpleFrequencyGenotypeData.readData(file, type);
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
    
    private void testData(FrequencyGenotypeData data) {
        
        // check dataset name, if set
        String expectedDatasetName = dataName != null ? dataName : "Allele frequency data";
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
                    if(Double.isNaN(ALLELE_FREQUENCIES[i][m][a])){
                        assertTrue("Frequency should be missing for allele " + a
                                   + " of marker " + m + " in individual " + i + ".",
                                   Double.isNaN(data.getAlleleFrequency(i, m, a)));
                    } else {
                        assertFalse("Frequency should not be missing for allele " + a
                                    + " of marker " + m + " in individual " + i + ".",
                                    Double.isNaN(data.getAlleleFrequency(i, m, a)));
                        assertEquals("Incorrect frequency for allele " + a
                               + " of marker " + m + " in individual " + i + ".",
                               ALLELE_FREQUENCIES[i][m][a],
                               data.getAlleleFrequency(i, m, a),
                               PRECISION);
                    }
                }
                
            }
            
        }
        
    }
    
}
