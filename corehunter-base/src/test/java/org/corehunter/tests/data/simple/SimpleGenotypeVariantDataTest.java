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

import static org.corehunter.tests.TestData.ALLELES;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.ALLELES_DIPLOID;
import static org.corehunter.tests.TestData.ALLELE_NAMES_DIPLOID;
import static org.corehunter.tests.TestData.MARKER_NAMES_DIPLOID;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.HEADERS;
import static org.corehunter.tests.TestData.NAMES;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.corehunter.tests.TestData.UNIQUE_IDENTIFIERS;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import uno.informatics.common.io.FileType;

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
    private static final String DIPLOID_TXT_UNDEFINED_MARKER_NAMES = "/multiallelic/diploid/undefined-marker-names.txt";

    private static final String ERRONEOUS_FILES_DIR = "/multiallelic/err/";
    private static final String DIPLOID_ERRONEOUS_FILES_DIR = "/multiallelic/diploid/err/";
    
    private boolean withNames;
    private boolean withUniqueIdentifiers;
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
        datasetName = NAME;
        withNames = true;
        withUniqueIdentifiers = true;
        testDataGeneral(new SimpleGenotypeVariantData(NAME, HEADERS, MARKER_NAMES, ALLELE_NAMES, ALLELES));
    }

    @Test
    public void fromTxtFileWithNames() throws IOException {
        datasetName = "names.txt";
        withNames = true;
        withUniqueIdentifiers = false;
        System.out.println(" |- File " + datasetName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(TXT_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void fromCsvFileWithNames() throws IOException {
        datasetName = "names.csv";
        withNames = true;
        withUniqueIdentifiers = false;
        System.out.println(" |- File " + datasetName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithNamesAndIDs() throws IOException {
        datasetName = "names-and-ids.csv";
        withNames = true;
        withUniqueIdentifiers = true;
        System.out.println(" |- File " + datasetName);
        testDataGeneral(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NAMES_IDS).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithoutNames() throws IOException {
        datasetName = "no-names.csv";
        withNames = false;
        withUniqueIdentifiers = false;
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
        withNames = true;
        withUniqueIdentifiers = true;
        testDataDiploid(new SimpleGenotypeVariantData(NAME, HEADERS, MARKER_NAMES_DIPLOID,
                                                      ALLELE_NAMES_DIPLOID, ALLELES_DIPLOID));
    }
    
    @Test
    public void diploidFromTxtFileWithNames() throws IOException {
        datasetName = "names.txt";
        withNames = true;
        withUniqueIdentifiers = false;
        System.out.println(" |- File diploid/" + datasetName);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void diploidFromCsvFileWithNames() throws IOException {
        datasetName = "names.csv";
        withNames = true;
        withUniqueIdentifiers = false;
        System.out.println(" |- File diploid/" + datasetName);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_CSV_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void diploidFromCsvFileWithNamesAndIDs() throws IOException {
        datasetName = "names-and-ids.csv";
        withNames = true;
        withUniqueIdentifiers = true;
        System.out.println(" |- File diploid/" + datasetName);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_CSV_NAMES_IDS).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void diploidFromTxtFileWithoutNames() throws IOException {
        datasetName = "no-names.txt";
        withNames = false;
        withUniqueIdentifiers = false;
        System.out.println(" |- File diploid/" + datasetName);
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_NO_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void diploidFromTxtFileWithUndefinedMarkerNames() throws IOException {
        datasetName = "undefined-marker-names.txt";
        withNames = false;
        withUniqueIdentifiers = false;
        System.out.println(" |- File diploid/" + datasetName);
        // store copy of all marker names
        String[] allMarkerNames = Arrays.copyOf(MARKER_NAMES_DIPLOID, MARKER_NAMES_DIPLOID.length);
        // erase name of 2nd and 3rd marker
        MARKER_NAMES_DIPLOID[1] = null;
        MARKER_NAMES_DIPLOID[2] = null;
        testDataDiploid(SimpleGenotypeVariantData.readDiploidData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(DIPLOID_TXT_UNDEFINED_MARKER_NAMES).getPath()),
            FileType.TXT
        ));
        // restore
        MARKER_NAMES_DIPLOID[1] = allMarkerNames[1];
        MARKER_NAMES_DIPLOID[2] = allMarkerNames[2];
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
        String expectedDatasetName = datasetName != null ? datasetName : "Multi-allelic marker data";
        assertEquals("Incorrect dataset name.", expectedDatasetName, data.getDatasetName());
        
        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());
        
        // check number of markers
        assertEquals("Number of marker is not correct.", markerNames.length, data.getNumberOfMarkers());
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
            
            // check name, if assigned
            if(withNames){
                assertEquals("Name for " + i + " not correct.", NAMES[i], data.getHeader(i).getName());
            }
            
            // check unique identifier, if assigned
            if(withUniqueIdentifiers){
                assertEquals("Unique identifier for " + i + " not correct.",
                             UNIQUE_IDENTIFIERS[i], data.getHeader(i).getUniqueIdentifier());
            }

            // check frequencies
            for (int m = 0; m < data.getNumberOfMarkers(); m++) {
                for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                    if(alleles[i][m][a] == null){
                        assertNull("Frequency should be missing for allele " + a
                                 + " of marker " + m + " in individual " + i + ".",
                                data.getAlelleFrequency(i, m, a));
                    } else {
                        assertNotNull("Frequency should not be missing for allele " + a
                                 + " of marker " + m + " in individual " + i + ".",
                                data.getAlelleFrequency(i, m, a));
                        assertEquals("Incorrect frequency for allele " + a
                               + " of marker " + m + " in individual " + i + ".",
                               alleles[i][m][a],
                               data.getAlelleFrequency(i, m, a),
                               PRECISION);
                    }
                }
                
            }
            
        }
        
    }
    
    private void testDataGeneral(SimpleGenotypeVariantData data) {
        testData(data, MARKER_NAMES, ALLELE_NAMES, ALLELES);
    }
    
    private void testDataDiploid(SimpleGenotypeVariantData data) {
        testData(data, MARKER_NAMES_DIPLOID, ALLELE_NAMES_DIPLOID, ALLELES_DIPLOID);
    }
    
}
