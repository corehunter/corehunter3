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


import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeVariantData;

import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES_BIALLELIC;
import static org.corehunter.tests.TestData.ALLELE_SCORES_BIALLELIC;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.BLANK_HEADERS;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.UNDEFINED_MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;

import uno.informatics.common.io.FileType;
import uno.informatics.data.SimpleEntity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimpleBiAllelicGenotypeVariantDataTest {

    private static final String TXT_NAMES = "/biallelic/names.txt";
    private static final String TXT_NO_NAMES = "/biallelic/no-names.txt";
    private static final String CSV_NAMES = "/biallelic/names.csv";
    private static final String CSV_NAMES_IDS = "/biallelic/names-and-ids.csv";
    private static final String CSV_NO_MARKER_NAMES = "/biallelic/no-marker-names.csv";
    private static final String CSV_NO_NAMES_NO_MARKER_NAMES = "/biallelic/no-names-no-marker-names.csv";

    private static final String ERRONEOUS_FILES_DIR = "/biallelic/err/";
    
    private SimpleEntity[] expectedHeaders;
    private String[] expectedMarkerNames;
    private String datasetName;
    
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
        datasetName = null;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        testData(new SimpleBiAllelicGenotypeVariantData(
                HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_SCORES_BIALLELIC
        ));
    }
    
    @Test
    public void inMemoryTestWithName() {
        System.out.println(" |- In memory test with dataset name");
        datasetName = NAME;
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        testData(new SimpleBiAllelicGenotypeVariantData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_SCORES_BIALLELIC
        ));
    }
    
    @Test
    public void fromTxtFileWithoutNames() throws IOException {
        datasetName = "no-names.txt";
        expectedHeaders = BLANK_HEADERS;
        expectedMarkerNames = MARKER_NAMES;
        System.out.println(" |- File " + datasetName);
        testData(SimpleBiAllelicGenotypeVariantData.readData(
            Paths.get(SimpleBiAllelicGenotypeVariantDataTest.class.getResource(TXT_NO_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void fromTxtFileWithNames() throws IOException {
        datasetName = "names.txt";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        System.out.println(" |- File " + datasetName);
        testData(SimpleBiAllelicGenotypeVariantData.readData(
            Paths.get(SimpleBiAllelicGenotypeVariantDataTest.class.getResource(TXT_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void fromCsvFileWithNames() throws IOException {
        datasetName = "names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        System.out.println(" |- File " + datasetName);
        testData(SimpleBiAllelicGenotypeVariantData.readData(
            Paths.get(SimpleBiAllelicGenotypeVariantDataTest.class.getResource(CSV_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithNamesAndIds() throws IOException {
        datasetName = "names-and-ids.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedMarkerNames = MARKER_NAMES;
        System.out.println(" |- File " + datasetName);
        testData(SimpleBiAllelicGenotypeVariantData.readData(
            Paths.get(SimpleBiAllelicGenotypeVariantDataTest.class.getResource(CSV_NAMES_IDS).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithoutMarkerNames() throws IOException {
        datasetName = "no-marker-names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedMarkerNames = UNDEFINED_MARKER_NAMES;
        System.out.println(" |- File " + datasetName);
        testData(SimpleBiAllelicGenotypeVariantData.readData(
            Paths.get(SimpleBiAllelicGenotypeVariantDataTest.class.getResource(CSV_NO_MARKER_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithoutNamesOrMarkerNames() throws IOException {
        datasetName = "no-names-no-marker-names.csv";
        expectedHeaders = BLANK_HEADERS;
        expectedMarkerNames = UNDEFINED_MARKER_NAMES;
        System.out.println(" |- File " + datasetName);
        testData(SimpleBiAllelicGenotypeVariantData.readData(
            Paths.get(SimpleBiAllelicGenotypeVariantDataTest.class.getResource(CSV_NO_NAMES_NO_MARKER_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void erroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        Path dir = Paths.get(SimpleBiAllelicGenotypeVariantDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
        try(DirectoryStream<Path> directory = Files.newDirectoryStream(dir)){
            for(Path file : directory){
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimpleBiAllelicGenotypeVariantData.readData(file, type);
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

    private void testData(SimpleBiAllelicGenotypeVariantData data) {
        
        // check dataset name, if set
        String expectedDatasetName = datasetName != null ? datasetName : "Biallelic marker data";
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

            // check scores and frequencies
            for (int m = 0; m < data.getNumberOfMarkers(); m++) {
                for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                    if(ALLELE_SCORES_BIALLELIC[i][m] == null){
                        assertNull("Allele score should be missing for marker " + m
                                 + " in individual " + i + ".",
                                data.getAlleleScore(i, m));
                        assertNull("Frequency should be missing for allele " + a
                                 + " of marker " + m + " in individual " + i + ".",
                                data.getAlleleFrequency(i, m, a));
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
