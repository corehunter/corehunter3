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

import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.ALLELE_NAMES_DIPLOID;
import static org.corehunter.tests.TestData.MARKER_NAMES_DIPLOID;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES_DIPLOID;
import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES;
import static org.corehunter.tests.TestData.HEADERS_NAMES_AND_IDS;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.BLANK_HEADERS;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.corehunter.data.simple.SimpleGenotypeVariantData;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;


import uno.informatics.common.io.FileType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import uno.informatics.data.SimpleEntity;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleGenotypeVariantDataTest {

    private static final String TXT_NAMES = "/multiallelic/names.txt";
    private static final String CSV_NAMES = "/multiallelic/names.csv";
    private static final String CSV_NAMES_IDS = "/multiallelic/names-and-ids.csv";
    private static final String CSV_NO_NAMES = "/multiallelic/no-names.csv";
    
    private static final String DIPLOID_TXT_NAMES = "/multiallelic/diploid/names.txt";
    private static final String DIPLOID_CSV_NAMES = "/multiallelic/diploid/names.csv";
    private static final String DIPLOID_CSV_NAMES_IDS = "/multiallelic/diploid/names-and-ids.csv";
    private static final String DIPLOID_TXT_NO_NAMES = "/multiallelic/diploid/no-names.txt";
    private static final String DIPLOID_TXT_NO_MARKER_NAMES = "/multiallelic/diploid/no-marker-names.txt";
    private static final String DIPLOID_TXT_NO_NAMES_NO_MARKER_NAMES
                                                            = "/multiallelic/diploid/no-names-no-marker-names.txt";

    private static final String ERRONEOUS_FILES_DIR = "/multiallelic/err/";
    private static final String DIPLOID_ERRONEOUS_FILES_DIR = "/multiallelic/diploid/err/";
    
    private SimpleEntity[] expectedHeaders;
    private String datasetName;
    
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
        datasetName = null;
        expectedHeaders = HEADERS_NAMES_AND_IDS;
        testDataGeneral(new SimpleGenotypeVariantData(HEADERS_NAMES_AND_IDS, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES));
    }
    
    @Test
    public void inMemoryWithName() {
        System.out.println(" |- In memory test with dataset name");
        datasetName = NAME;
        expectedHeaders = HEADERS_NAMES_AND_IDS;
        testDataGeneral(new SimpleGenotypeVariantData(NAME, HEADERS_NAMES_AND_IDS, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES));
    }

    @Test
    public void fromTxtFileWithNames() throws IOException {
        datasetName = "names.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        System.out.println(" |- File " + datasetName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(TXT_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void fromCsvFileWithNames() throws IOException {
        datasetName = "names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        System.out.println(" |- File " + datasetName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithNamesAndIDs() throws IOException {
        datasetName = "names-and-ids.csv";
        expectedHeaders = HEADERS_NAMES_AND_IDS;
        System.out.println(" |- File " + datasetName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NAMES_IDS).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithoutNames() throws IOException {
        datasetName = "no-names.csv";
        expectedHeaders = BLANK_HEADERS;
        System.out.println(" |- File " + datasetName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NO_NAMES).getPath()),
            FileType.CSV
        ));
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
        datasetName = NAME;
        expectedHeaders = HEADERS_NAMES_AND_IDS;
        testDataDiploid(new SimpleGenotypeVariantData(NAME, HEADERS_NAMES_AND_IDS, MARKER_NAMES_DIPLOID,
                                                      ALLELE_NAMES_DIPLOID, ALLELE_FREQUENCIES_DIPLOID));
    }
    
    @Test
    public void diploidFromTxtFileWithNames() throws IOException {
        datasetName = "names.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        System.out.println(" |- File diploid/" + datasetName);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void diploidFromCsvFileWithNames() throws IOException {
        datasetName = "names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        System.out.println(" |- File diploid/" + datasetName);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_CSV_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void diploidFromCsvFileWithNamesAndIDs() throws IOException {
        datasetName = "names-and-ids.csv";
        expectedHeaders = HEADERS_NAMES_AND_IDS;
        System.out.println(" |- File diploid/" + datasetName);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_CSV_NAMES_IDS).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void diploidFromTxtFileWithoutNames() throws IOException {
        datasetName = "no-names.txt";
        expectedHeaders = BLANK_HEADERS;
        System.out.println(" |- File diploid/" + datasetName);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_NO_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void diploidFromTxtFileWithoutMarkerNames() throws IOException {
        datasetName = "no-marker-names.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        System.out.println(" |- File diploid/" + datasetName);
        // store copy of all marker names
        String[] markerNamesBackup = Arrays.copyOf(MARKER_NAMES_DIPLOID, MARKER_NAMES_DIPLOID.length);
        // erase marker names
        Arrays.fill(MARKER_NAMES_DIPLOID, null);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_NO_MARKER_NAMES).getPath()),
            FileType.TXT
        ));
        // restore
        System.arraycopy(markerNamesBackup, 0, MARKER_NAMES_DIPLOID, 0, MARKER_NAMES_DIPLOID.length);
    }
    
    @Test
    public void diploidFromTxtFileWithoutNamesOrMarkerNames() throws IOException {
        datasetName = "no-names-no-marker-names.txt";
        expectedHeaders = BLANK_HEADERS;
        System.out.println(" |- File diploid/" + datasetName);
        // store copy of all marker names
        String[] markerNamesBackup = Arrays.copyOf(MARKER_NAMES_DIPLOID, MARKER_NAMES_DIPLOID.length);
        // erase marker names
        Arrays.fill(MARKER_NAMES_DIPLOID, null);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_NO_NAMES_NO_MARKER_NAMES).getPath()),
            FileType.TXT
        ));
        // restore
        System.arraycopy(markerNamesBackup, 0, MARKER_NAMES_DIPLOID, 0, MARKER_NAMES_DIPLOID.length);
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
                    SimpleGenotypeVariantData.readDiploidData(file, type);
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
    
    private void testData(SimpleGenotypeVariantData data, String[] markerNames,
                          String[][] alleleNames, Double[][][] alleles) {
        
        // check dataset name, if set
        String expectedDatasetName = datasetName != null ? datasetName : "Multiallelic marker data";
        assertEquals("Incorrect dataset name.", expectedDatasetName, data.getDatasetName());
        
        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());
        
        // check number of markers
        assertEquals("Number of markers is not correct.", markerNames.length, data.getNumberOfMarkers());
        // check total number of alleles
        assertEquals("Incorrect total number of alleles.", Arrays.stream(alleleNames)
                                                                 .mapToInt(names -> names.length)
                                                                 .sum(), data.getTotalNumberOfAlleles());
        
        // check marker names + allele counts and names
        for(int m = 0; m < data.getNumberOfMarkers(); m++){
            
            assertEquals("Marker name for marker " + m + " is not correct.",
                         markerNames[m], data.getMarkerName(m));

            assertEquals("Number of alelles for marker " + m + " is not correct.",
                         alleleNames[m].length, data.getNumberOfAlleles(m));

            for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                assertEquals("Allele name for allele " + a + " of marker " + m + " is not correct.",
                             alleleNames[m][a], data.getAlleleName(m, a));
            }
            
        }
        
        // check individuals (headers and frequencies)
        int size = data.getDatasetSize();

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
                    if(alleles[i][m][a] == null){
                        assertNull("Frequency should be missing for allele " + a
                                 + " of marker " + m + " in individual " + i + ".",
                                data.getAlleleFrequency(i, m, a));
                    } else {
                        assertNotNull("Frequency should not be missing for allele " + a
                                    + " of marker " + m + " in individual " + i + ".",
                                   data.getAlleleFrequency(i, m, a));
                        assertEquals("Incorrect frequency for allele " + a
                               + " of marker " + m + " in individual " + i + ".",
                               alleles[i][m][a],
                               data.getAlleleFrequency(i, m, a),
                               PRECISION);
                    }
                }
                
            }
            
        }
        
    }
    
    private void testDataGeneral(SimpleGenotypeVariantData data) {
        testData(data, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES);
    }
    
    private void testDataDiploid(SimpleGenotypeVariantData data) {
        testData(data, MARKER_NAMES_DIPLOID, ALLELE_NAMES_DIPLOID, ALLELE_FREQUENCIES_DIPLOID);
    }
    
}
