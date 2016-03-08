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
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.HEADERS;
import static org.corehunter.tests.TestData.NAMES;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.corehunter.tests.TestData.UNIQUE_IDENTIFIERS;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.corehunter.data.simple.SimpleDistanceMatrixData;

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

    private static final String[] ERRONEOUS_FILES = {
        "/multiallelic/err/duplicate-id-column.csv",
        "/multiallelic/err/duplicate-id.csv",
        "/multiallelic/err/duplicate-marker-names.csv",
        "/multiallelic/err/duplicate-names-column.csv",
        "/multiallelic/err/empty.csv",
        "/multiallelic/err/incorrect-row-length.csv",
        "/multiallelic/err/incorrect-row-length-2.csv",
        "/multiallelic/err/incorrect-row-length-3.csv",
        "/multiallelic/err/incorrect-row-length-4.csv",
        "/multiallelic/err/incorrect-row-length-5.csv",
        "/multiallelic/err/missing-marker-names.csv",
        "/multiallelic/err/missing-marker-names-2.csv",
        "/multiallelic/err/negative-frequencies.csv",
        "/multiallelic/err/no-data-columns.csv",
        "/multiallelic/err/no-data-rows.csv",
        "/multiallelic/err/sum-below-one.csv",
        "/multiallelic/err/sum-exceeds-one.csv",
        "/multiallelic/err/too-few-allele-names.csv",
        "/multiallelic/err/unexpected-column-header.csv"
    };
    
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
    
    @Test
    public void inMemory() {
        System.out.println(" |- In memory test");
        datasetName = NAME;
        withNames = true;
        withUniqueIdentifiers = true;
        testData(new SimpleGenotypeVariantData(NAME, HEADERS, MARKER_NAMES, ALLELE_NAMES, ALLELES));
    }

    @Test
    public void fromTxtFileWithNames() throws IOException {
        datasetName = "names.txt";
        withNames = true;
        withUniqueIdentifiers = false;
        System.out.println(" |- File " + datasetName);
        testData(SimpleGenotypeVariantData.readData(
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
        testData(SimpleGenotypeVariantData.readData(
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
        testData(SimpleGenotypeVariantData.readData(
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
        testData(SimpleGenotypeVariantData.readData(
            Paths.get(SimpleGenotypeVariantDataTest.class.getResource(CSV_NO_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void testErroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        for(int i = 0; i < ERRONEOUS_FILES.length; i++){
            Path file = Paths.get(SimpleGenotypeVariantDataTest.class.getResource(ERRONEOUS_FILES[i]).getPath());
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

    private void testData(SimpleGenotypeVariantData data) {
        
        // check dataset name, if set
        String expectedDatasetName = datasetName != null ? datasetName : "Multi-allelic marker data";
        assertEquals("Incorrect dataset name.", expectedDatasetName, data.getDatasetName());
        
        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());

        // check number of markers
        assertEquals("Number of marker is not correct.", MARKER_NAMES.length, data.getNumberOfMarkers());
        // check total number of alleles
        assertEquals("Incorrect total number of alleles.", Arrays.stream(ALLELE_NAMES)
                                                                 .mapToInt(names -> names.length)
                                                                 .sum(), data.getTotalNumberOfAlleles());
        
        // check marker names + allele counts and names
        for(int m = 0; m < data.getNumberOfMarkers(); m++){
            
            assertEquals("Marker name for marker " + m + " is not correct.",
                         MARKER_NAMES[m], data.getMarkerName(m));

            assertEquals("Number of alelles for marker " + m + " is not correct.",
                         ALLELE_NAMES[m].length, data.getNumberOfAlleles(m));

            for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                assertEquals("Allele name for allele " + a + " of marker " + m + " is not correct.",
                             ALLELE_NAMES[m][a], data.getAlleleName(m, a));
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
            
            // check header if both identifiers and names assigned
            if(withNames && withUniqueIdentifiers){
                assertEquals("Header for " + i + " not correct.", HEADERS[i], data.getHeader(i));
            }

            // check frequencies
            for (int m = 0; m < data.getNumberOfMarkers(); m++) {
                for (int a = 0; a < data.getNumberOfAlleles(m); a++) {
                    if(ALLELES[i][m][a] == null){
                        assertNull("Frequency should be missing for allele " + a
                                 + " of marker " + m + " in individual " + i + ".",
                                data.getAlelleFrequency(i, m, a));
                    } else {
                        assertNotNull("Frequency should not be missing for allele " + a
                                 + " of marker " + m + " in individual " + i + ".",
                                data.getAlelleFrequency(i, m, a));
                        assertEquals("Incorrect frequency for allele " + a
                               + " of marker " + m + " in individual " + i + ".",
                               ALLELES[i][m][a],
                               data.getAlelleFrequency(i, m, a),
                               PRECISION);
                    }
                }
                
            }
            
        }
        
    }
}
