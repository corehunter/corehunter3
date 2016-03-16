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
import org.corehunter.data.simple.SimplePhenotypicTraitData;

import static org.corehunter.tests.TestData.BLANK_HEADERS;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_NAMES_AND_IDS;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_INFERRED_BOUNDS;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_EXPLICIT_BOUNDS;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_NAMES;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_VALUES;
import static org.corehunter.tests.TestData.SET;

import uno.informatics.common.io.FileType;
import uno.informatics.data.FeatureDataset;
import uno.informatics.data.FeatureDatasetRow;
import uno.informatics.data.SimpleEntity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * @author Herman De Beukelaer
 */
public class SimplePhenotypicTraitDataTest {

    private static final String CSV_NAMES = "/phenotypes/names.csv";
    private static final String CSV_NAMES_IDS = "/phenotypes/names-and-ids.csv";
    private static final String CSV_NAMES_MIN_MAX = "/phenotypes/names-min-max.csv";
    private static final String TXT_NO_NAMES = "/phenotypes/no-names.txt";

    private static final String ERRONEOUS_FILES_DIR = "/phenotypes/err/";
    
    private SimpleEntity[] expectedHeaders;
    private Object[][] expectedBounds;
    private String datasetName;
    
    @BeforeClass
    public static void beforeClass(){
        System.out.println("Test simple phenotypic trait data");
    }
    
    @AfterClass
    public static void afterClass(){
        System.out.println("Done");
    }
    
    @Test
    public void fromCsvFileWithNames() throws IOException {
        datasetName = "names.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedBounds = PHENOTYPIC_TRAIT_INFERRED_BOUNDS;
        System.out.println(" |- File " + datasetName);
        testData(SimplePhenotypicTraitData.readData(
            Paths.get(SimplePhenotypicTraitDataTest.class.getResource(CSV_NAMES).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithNamesAndIds() throws IOException {
        datasetName = "names-and-ids.csv";
        expectedHeaders = HEADERS_NAMES_AND_IDS;
        expectedBounds = PHENOTYPIC_TRAIT_INFERRED_BOUNDS;
        System.out.println(" |- File " + datasetName);
        testData(SimplePhenotypicTraitData.readData(
            Paths.get(SimplePhenotypicTraitDataTest.class.getResource(CSV_NAMES_IDS).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromCsvFileWithNamesAndBounds() throws IOException {
        datasetName = "names-min-max.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedBounds = PHENOTYPIC_TRAIT_EXPLICIT_BOUNDS;
        System.out.println(" |- File " + datasetName);
        testData(SimplePhenotypicTraitData.readData(
            Paths.get(SimplePhenotypicTraitDataTest.class.getResource(CSV_NAMES_MIN_MAX).getPath()),
            FileType.CSV
        ));
    }
    
    @Test
    public void fromTxtFileWithoutNames() throws IOException {
        datasetName = "no-names.txt";
        expectedHeaders = BLANK_HEADERS;
        expectedBounds = PHENOTYPIC_TRAIT_INFERRED_BOUNDS;
        System.out.println(" |- File " + datasetName);
        testData(SimplePhenotypicTraitData.readData(
            Paths.get(SimplePhenotypicTraitDataTest.class.getResource(TXT_NO_NAMES).getPath()),
            FileType.TXT
        ));
    }
    
    @Test
    public void erroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        Path dir = Paths.get(SimplePhenotypicTraitDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
        try(DirectoryStream<Path> directory = Files.newDirectoryStream(dir)){
            for(Path file : directory){
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimplePhenotypicTraitData.readData(file, type);
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

    private void testData(SimplePhenotypicTraitData data) {
        
        // check dataset name, if set
        String expectedDatasetName = datasetName != null ? datasetName : "Phenotypic trait data";
        assertEquals("Incorrect dataset name.", expectedDatasetName, data.getDatasetName());
        
        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());
        // check size
        assertEquals("Incorrect dataset size.", SET.size(), data.getDatasetSize());
        
        // check trait names and bounds (min/max)
        FeatureDataset fData = data.getData();
        for(int t = 0; t < fData.getFeatures().size(); t++){
            
            // check name
            assertEquals("Trait name for trait " + t + " is not correct.",
                         PHENOTYPIC_TRAIT_NAMES[t], fData.getFeatures().get(t).getName());
            
            // check min and max
            assertEquals("Minimum value for trait " + t + " is not correct.",
                         expectedBounds[t][0], fData.getFeatures().get(t).getMethod().getScale().getMinimumValue());
            assertEquals("Maximum value for trait " + t + " is not correct.",
                         expectedBounds[t][1], fData.getFeatures().get(t).getMethod().getScale().getMaximumValue());
            
        }
        
        // check individuals (headers and trait values)
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

            // check trait values
            FeatureDatasetRow row = fData.getRow(i);
            for(int t = 0; t < row.getColumnCount(); t++){
                assertEquals(
                    "Incorrect value for trait " + t + " in individual " + i + ".",
                    PHENOTYPIC_TRAIT_VALUES[i][t],
                    row.getValue(t)
                );
            }
            
        }
        
    }
}
