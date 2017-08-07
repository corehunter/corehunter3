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


import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES_BIALLELIC;
import static org.corehunter.tests.TestData.ALLELE_SCORES_BIALLELIC;
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.corehunter.tests.TestData.UNDEFINED_MARKER_NAMES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeData;
import org.corehunter.util.CoreHunterConstants;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleBiAllelicGenotypeDataTest {

    private static final String TXT_IDS = "/biallelic_genotypes/ids.txt";
    private static final String CSV_IDS = "/biallelic_genotypes/ids.csv";
    private static final String CSV_IDS_NAMES = "/biallelic_genotypes/ids-and-names.csv";
    private static final String CSV_NO_MARKER_NAMES = "/biallelic_genotypes/no-marker-names.csv";
    private static final String LONG_FILE = "/biallelic_genotypes/biallelic_genotypes_data.csv";
    
    private static final String ERRONEOUS_FILES_DIR = "/biallelic_genotypes/err/";
    private static final String TEST_OUTPUT = "target/testoutput";
    
    private static final int[] SELECTION = new int[] {
        1, 3, 4
    };
    
    private SimpleEntity[] expectedHeaders;
    private String[] expectedMarkerNames;
    private String dataName;
    
    @BeforeClass
    public static void beforeClass(){
        System.out.println("Test simple biallelic genotype variant data");
    }
    
    @AfterClass
    public static void afterClass(){
        System.out.println("Done");
    }
    
    @Test
    public void inMemoryTest() {
        System.out.println(" |- In memory test");
        dataName = null;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        testData(new SimpleBiAllelicGenotypeData(
                HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_SCORES_BIALLELIC
        ));
    }
    
    @Test
    public void inMemoryTestWithName() {
        System.out.println(" |- In memory test with dataset name");
        dataName = NAME;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        testData(new SimpleBiAllelicGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_SCORES_BIALLELIC
        ));
    }
    
    @Test
    public void fromTxtFileWithIds() throws IOException {
        dataName = "ids.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        System.out.println(" |- Read File " + dataName);
        testData(SimpleBiAllelicGenotypeData.readData(
            Paths.get(SimpleBiAllelicGenotypeDataTest.class.getResource(TXT_IDS).getPath()), FileType.TXT
        ));
    }
    
    @Test
    public void fromCsvFileWithIds() throws IOException {
        dataName = "ids.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        System.out.println(" |- Read File " + dataName);
        testData(SimpleBiAllelicGenotypeData.readData(
            Paths.get(SimpleBiAllelicGenotypeDataTest.class.getResource(CSV_IDS).getPath()), FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithIdsAndNames() throws IOException {
        dataName = "ids-and-names.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        System.out.println(" |- Read File " + dataName);
        testData(SimpleBiAllelicGenotypeData.readData(
            Paths.get(SimpleBiAllelicGenotypeDataTest.class.getResource(CSV_IDS_NAMES).getPath()), FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithoutMarkerNames() throws IOException {
        dataName = "no-marker-names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = UNDEFINED_MARKER_NAMES;
        System.out.println(" |- Read File " + dataName);
        testData(SimpleBiAllelicGenotypeData.readData(
            Paths.get(SimpleBiAllelicGenotypeDataTest.class.getResource(CSV_NO_MARKER_NAMES).getPath()), FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileLargeFiles() throws IOException {
        SimpleBiAllelicGenotypeData.readData(
            Paths.get(SimpleBiAllelicGenotypeDataTest.class.getResource(LONG_FILE).getPath()), FileType.CSV
        );
    }
    
    @Test
    public void toTxtFile() throws IOException {
        dataName = "out.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        
        SimpleBiAllelicGenotypeData genotypicData = 
                new SimpleBiAllelicGenotypeData(expectedHeaders, expectedMarkerNames, ALLELE_SCORES_BIALLELIC) ;
        
        Path path = Paths.get(TEST_OUTPUT) ;
        
        Files.createDirectories(path) ;
        
        path = Files.createTempDirectory(path, "GenoBiallelic-Txt") ;
        
        path = Paths.get(path.toString(), dataName) ;
                
        System.out.println(" |- Write File " + dataName);
        genotypicData.writeData(path, FileType.TXT);
        
        System.out.println(" |- Read written File " + dataName);
        testData(SimpleBiAllelicGenotypeData.readData(path, FileType.TXT));
    }
    
    @Test
    public void toCsvFile() throws IOException {
        dataName = "out.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        
        SimpleBiAllelicGenotypeData genotypicData = 
                new SimpleBiAllelicGenotypeData(expectedHeaders, expectedMarkerNames, ALLELE_SCORES_BIALLELIC) ;
        
        Path path = Paths.get(TEST_OUTPUT) ;
        
        Files.createDirectories(path) ;
        
        path = Files.createTempDirectory(path, "GenoBiallelic-Csv") ;
        
        path = Paths.get(path.toString(), dataName) ;
                
        System.out.println(" |- Write File " + dataName);
        genotypicData.writeData(path, FileType.CSV);
        
        System.out.println(" |- Read written File " + dataName);
        testData(SimpleBiAllelicGenotypeData.readData(path, FileType.CSV));
    }
        
    @Test
    public void toCsvFileWithAllIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        
        SimpleBiAllelicGenotypeData genotypicData = 
            new SimpleBiAllelicGenotypeData(expectedHeaders, expectedMarkerNames, ALLELE_SCORES_BIALLELIC) ;
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoBiallelic-AllIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write allele score format with integer ids
        dataName = "bi-with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write biallelic genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, true, true);

        assertTrue("Output file is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class.getResource(
                        "/biallelic_genotypes/out/all-bi-with-ids.csv"
                ).getPath()),
                path.toFile()
            )
        );
        
        // write allele score format without integer ids
        dataName = "bi-no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write biallelic genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, true, false);

        assertTrue("Output file is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class.getResource(
                        "/biallelic_genotypes/out/all-bi-no-ids.csv"
                ).getPath()),
                path.toFile()
            )
        );
        
    }
    
    @Test
    public void toCsvFileWithSelectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        
        SimpleBiAllelicGenotypeData genotypicData = 
            new SimpleBiAllelicGenotypeData(expectedHeaders, expectedMarkerNames, ALLELE_SCORES_BIALLELIC) ;
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoBiallelic-SelectedIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write allele score format with integer ids
        dataName = "bi-with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write biallelic genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, false, true);

        assertTrue("Output file is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class.getResource(
                        "/biallelic_genotypes/out/sel-bi-with-ids.csv"
                ).getPath()),
                path.toFile()
            )
        );
        
        // write allele score format without integer ids
        dataName = "bi-no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write biallelic genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, true, false, false);

        assertTrue("Output file is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class.getResource(
                        "/biallelic_genotypes/out/sel-bi-no-ids.csv"
                ).getPath()),
                path.toFile()
            )
        );
        
    }
    
    @Test
    public void toCsvFileWithUnselectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        
        SimpleBiAllelicGenotypeData genotypicData = 
            new SimpleBiAllelicGenotypeData(expectedHeaders, expectedMarkerNames, ALLELE_SCORES_BIALLELIC) ;
        
        Set<Integer> ids = genotypicData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "GenoBiallelic-UnselectedIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write allele score format with integer ids
        dataName = "bi-with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write biallelic genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, false, true, true);

        assertTrue("Output file is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class.getResource(
                        "/biallelic_genotypes/out/unsel-bi-with-ids.csv"
                ).getPath()),
                path.toFile()
            )
        );
        
        // write allele score format without integer ids
        dataName = "bi-no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write biallelic genotypes file (with solution) " + dataName);

        genotypicData.writeData(path, FileType.CSV, solution, false, true, false);

        assertTrue("Output file is not correct!",
            FileUtils.contentEquals(
                new File(SimpleDistanceMatrixDataTest.class.getResource(
                        "/biallelic_genotypes/out/unsel-bi-no-ids.csv"
                ).getPath()),
                path.toFile()
            )
        );
        
    }
    
    @Test
    public void erroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        Path dir = Paths.get(SimpleBiAllelicGenotypeDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
        try(DirectoryStream<Path> directory = Files.newDirectoryStream(dir)){
            for(Path file : directory){
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimpleBiAllelicGenotypeData.readData(file, type);
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

    private void testData(SimpleBiAllelicGenotypeData data) {
        
        // check dataset name, if set
        String expectedDatasetName = dataName != null ? dataName : "Biallelic marker data";
        assertEquals("Incorrect data name.", expectedDatasetName, data.getName());
        
        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());
        
        // check number of markers
        assertEquals("Number of markers is not correct.", expectedMarkerNames.length, data.getNumberOfMarkers());
        // check total number of alleles
        assertEquals("Incorrect total number of alleles.",
                     2 * expectedMarkerNames.length, data.getTotalNumberOfAlleles());
        
        // check marker names + allele counts and names
        for(int m = 0; m < data.getNumberOfMarkers(); m++){
            
            assertEquals("Marker name for marker " + m + " is not correct.",
                         expectedMarkerNames[m], data.getMarkerName(m));

            assertEquals("Number of alelles for marker " + m + " is not correct.",
                         2, data.getNumberOfAlleles(m));

            for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                assertEquals("Allele name for allele " + a + " of marker " + m + " is not correct.",
                             "" + a, data.getAlleleName(m, a));
            }
            
        }
        
        // check individuals (headers, allele scores and frequencies)
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

            // check scores and frequencies
            for (int m = 0; m < data.getNumberOfMarkers(); m++) {
                for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                    if(ALLELE_SCORES_BIALLELIC[i][m] == CoreHunterConstants.MISSING_ALLELE_SCORE){
                        assertEquals(
                            "Allele score should be missing for marker " + m + " in individual " + i + ".",
                            CoreHunterConstants.MISSING_ALLELE_SCORE, data.getAlleleScore(i, m)
                        );
                        assertNull(
                            "Frequency should be missing for allele " + a + " of marker " + m
                            + " in individual " + i + ".",
                            data.getAlleleFrequency(i, m, a)
                        );
                    } else {
                        assertNotNull("Allele score should not be missing for marker " + m
                                    + " in individual " + i + ".",
                                   data.getAlleleScore(i, m));
                        assertNotNull("Frequency should not be missing for allele " + a
                                    + " of marker " + m + " in individual " + i + ".",
                                   data.getAlleleFrequency(i, m, a));
                        assertEquals("Incorrect allele score for marker " + m
                                   + " in individual " + i + ".",
                                   ALLELE_SCORES_BIALLELIC[i][m],
                                   data.getAlleleScore(i, m));
                        assertEquals("Incorrect frequency for allele " + a
                                   + " of marker " + m + " in individual " + i + ".",
                                   ALLELE_FREQUENCIES_BIALLELIC[i][m][a],
                                   data.getAlleleFrequency(i, m, a),
                                   PRECISION);
                    }
                }
                
            }
            
        }
        
    }
}
