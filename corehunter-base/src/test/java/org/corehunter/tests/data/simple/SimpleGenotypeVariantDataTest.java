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
import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES_DIPLOID;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.ALLELE_NAMES_DIPLOID;
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES_DIPLOID;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.corehunter.tests.TestData.UNDEFINED_ALLELE_NAMES;
import static org.corehunter.tests.TestData.UNDEFINED_MARKER_NAMES_DIPLOID;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.data.GenotypeVariantData;
import org.corehunter.data.simple.SimpleGenotypeVariantData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uno.informatics.common.io.FileType;
import uno.informatics.data.SimpleEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleGenotypeVariantDataTest {

    private static final String TXT_NAMES = "/frequency_genotypes/names.txt";
    private static final String CSV_NAMES = "/frequency_genotypes/names.csv";
    private static final String CSV_NAMES_IDS = "/frequency_genotypes/names-and-ids.csv";
    private static final String CSV_NO_ALLELE_NAMES = "/frequency_genotypes/no-allele-names.csv";
    
    private static final String DIPLOID_TXT_NAMES = "/diploid_genotypes/names.txt";
    private static final String DIPLOID_CSV_NAMES = "/diploid_genotypes/names.csv";
    private static final String DIPLOID_CSV_NAMES_IDS = "/diploid_genotypes/names-and-ids.csv";
    private static final String DIPLOID_TXT_NO_MARKER_NAMES = "/diploid_genotypes/no-marker-names.txt";

    private static final String ERRONEOUS_FILES_DIR = "/frequency_genotypes/err/";
    private static final String DIPLOID_ERRONEOUS_FILES_DIR = "/diploid_genotypes/err/";
    private static final String TEST_OUTPUT = "target/testoutput";
    
    private SimpleEntity[] expectedHeaders;
    private String[] expectedMarkerNames;
    private String[][] expectedAlleleNames;
    private String dataName;
    
    @BeforeClass
    public static void beforeClass(){
        System.out.println("Test simple genotype variant data");
    }
    
    @AfterClass
    public static void afterClass(){
        System.out.println("Done");
    }
    
    /***********/
    /* GENERAL */
    /***********/
    
    @Test
    public void inMemory() {
        System.out.println(" |- In memory test");
        dataName = null;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        testDataGeneral(new SimpleGenotypeVariantData(
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
        testDataGeneral(new SimpleGenotypeVariantData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        ));
    }

    @Test
    public void fromTxtFileWithNames() throws IOException {
        dataName = "names.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        System.out.println(" |- File " + dataName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(TXT_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void fromCsvFileWithNames() throws IOException {
        dataName = "names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        System.out.println(" |- File " + dataName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithNamesAndIDs() throws IOException {
        dataName = "names-and-ids.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        System.out.println(" |- File " + dataName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NAMES_IDS).getPath()),
            FileType.CSV
        ));
    }
    
    // TODO should not allele names be compulsory?
    @Test
    public void fromCsvFileWithoutAlleleNames() throws IOException {
        dataName = "no-allele-names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = UNDEFINED_ALLELE_NAMES;
        System.out.println(" |- File " + dataName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NO_ALLELE_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void toTxtFileWithNames() throws IOException {
        dataName = "names.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = UNDEFINED_ALLELE_NAMES;
        
        SimpleGenotypeVariantData genotypicData = new SimpleGenotypeVariantData(expectedHeaders, 
                expectedMarkerNames, expectedAlleleNames, ALLELE_FREQUENCIES) ;
        
        Path path = Paths.get(TEST_OUTPUT) ;
        
        Files.createDirectories(path) ;
        
        path = Files.createTempDirectory(path, "GenoFreqs-TxtFileWithNames") ;
        
        path = Paths.get(path.toString(), dataName) ;
        
        Files.deleteIfExists(path) ;
        
        System.out.println(" |- Write File " + dataName);
        genotypicData.writeData(path, FileType.TXT);
        
        System.out.println(" |- Read written File " + dataName);
        testData(SimpleGenotypeVariantData.readData(path, FileType.TXT), ALLELE_FREQUENCIES);
    }
    
    @Test
    public void toCsvFileWithNamesAndAlleleNames() throws IOException {
        dataName = "allele-names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        expectedAlleleNames = ALLELE_NAMES;
        
        SimpleGenotypeVariantData genotypicData = new SimpleGenotypeVariantData(expectedHeaders, 
                expectedMarkerNames, expectedAlleleNames, ALLELE_FREQUENCIES) ;
        
        Path path = Paths.get(TEST_OUTPUT) ;
        
        Files.createDirectories(path) ;
        
        path = Files.createTempDirectory(path, "GenoFreqs-CsvFileWithNamesAndAlleleNames") ;
        
        path = Paths.get(path.toString(), dataName) ;
        
        Files.deleteIfExists(path) ;
        
        System.out.println(" |- Write File " + dataName);
        genotypicData.writeData(path, FileType.CSV);
        
        System.out.println(" |- Read written File " + dataName);
        testData(SimpleGenotypeVariantData.readData(path, FileType.CSV), ALLELE_FREQUENCIES);
    }
    
    @Test
    public void erroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        Path dir = Paths.get(SimpleGenotypeVariantDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
        try(DirectoryStream<Path> directory = Files.newDirectoryStream(dir)){
            for(Path file : directory){
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimpleGenotypeVariantData.readData(file, type);
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
    
    /***********/
    /* DIPLOID */
    /***********/

    @Test
    public void diploidInMemory() {
        System.out.println(" |- In memory test (diploid)");
        dataName = NAME;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DIPLOID;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        testDataDiploid(new SimpleGenotypeVariantData(NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES_DIPLOID,
                                                      ALLELE_NAMES_DIPLOID, ALLELE_FREQUENCIES_DIPLOID));
    }
    
    @Test
    public void diploidFromTxtFileWithNames() throws IOException {
        dataName = "names.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DIPLOID;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        System.out.println(" |- File diploid/" + dataName);
        testDataDiploid(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_NAMES).getPath()),
            FileType.TXT, GenotypeDataFormat.DIPLOID
        ));
    }
    
    @Test
    public void diploidFromCsvFileWithNames() throws IOException {
        dataName = "names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DIPLOID;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        System.out.println(" |- File diploid/" + dataName);
        testDataDiploid(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_CSV_NAMES).getPath()),
            FileType.CSV, GenotypeDataFormat.DIPLOID
        ));
    }
    
    @Test
    public void diploidFromCsvFileWithNamesAndIDs() throws IOException {
        dataName = "names-and-ids.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DIPLOID;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        System.out.println(" |- File diploid/" + dataName);
        testDataDiploid(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_CSV_NAMES_IDS).getPath()),
            FileType.CSV, GenotypeDataFormat.DIPLOID
        ));
    }
    
    @Test
    public void diploidFromTxtFileWithoutMarkerNames() throws IOException {
        dataName = "no-marker-names.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = UNDEFINED_MARKER_NAMES_DIPLOID;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        System.out.println(" |- File diploid/" + dataName);
        testDataDiploid(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_NO_MARKER_NAMES).getPath()),
            FileType.TXT, GenotypeDataFormat.DIPLOID
        ));
    }
    
    @Test
    public void diploidToCsvFileWithNamesAndIDs() throws IOException {
        dataName = "names-and-ids.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES_DIPLOID;
        expectedAlleleNames = ALLELE_NAMES_DIPLOID;
        
        SimpleGenotypeVariantData genotypicData = new SimpleGenotypeVariantData(
                expectedHeaders, expectedMarkerNames, expectedAlleleNames, ALLELE_FREQUENCIES_DIPLOID
        );
        
        Path path = Paths.get(TEST_OUTPUT);
        
        Files.createDirectories(path);
        
        path = Files.createTempDirectory(path, "GenoDiploid-CsvFileWithNamesAndIDs");
        
        path = Paths.get(path.toString(), dataName);
        
        Files.deleteIfExists(path);
        
        System.out.println(" |- Write diploid File " + dataName);
        genotypicData.writeData(path, FileType.CSV);
        
        System.out.println(" |- Read written File " + dataName);
        testData(SimpleGenotypeVariantData.readData(path, FileType.CSV, GenotypeDataFormat.FREQUENCY), 
                ALLELE_FREQUENCIES_DIPLOID);
    }
    
    @Test
    public void diploidErroneousFiles() throws IOException {
        System.out.println(" |- Test diploid erroneous files:");
        Path dir = Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_ERRONEOUS_FILES_DIR).getPath());
        try(DirectoryStream<Path> directory = Files.newDirectoryStream(dir)){
            for(Path file : directory){
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimpleGenotypeVariantData.readData(file, type, GenotypeDataFormat.DIPLOID);
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
    
    private void testData(GenotypeVariantData data, Double[][][] freqs) {
        
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
    
    private void testDataGeneral(GenotypeVariantData data) {
        testData(data, ALLELE_FREQUENCIES);
    }
    
    private void testDataDiploid(GenotypeVariantData data) {
        testData(data, ALLELE_FREQUENCIES_DIPLOID);
    }
    
}
