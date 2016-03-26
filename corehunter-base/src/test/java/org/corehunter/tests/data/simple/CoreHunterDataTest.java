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
import java.nio.file.Paths;

import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeVariantData;
import org.corehunter.data.matrix.SymmetricMatrixFormat;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeVariantData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.tests.TestData;

import uno.informatics.common.io.FileType;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.feature.array.ArrayFeatureData;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;

import static org.junit.Assert.assertEquals;


/**
 * @author Herman De Beukelaer
 */
public class CoreHunterDataTest {

    private static final String DISTANCES_NO_HEADERS = "/distances/full.txt";
    private static final String DISTANCES_NO_HEADERS_SMALL = "/distances/small.txt";
    private static final String DISTANCES_UNIQUE_NAMES = "/distances/full-names.txt";
    private static final String DISTANCES_NON_UNIQUE_NAMES = "/distances/full-names-ids.txt";
    
    private static final String PHENOTYPES_NO_HEADERS = "/phenotypes/no-names.txt";
    private static final String PHENOTYPES_UNIQUE_NAMES = "/phenotypes/names.csv";
    private static final String PHENOTYPES_NON_UNIQUE_NAMES = "/phenotypes/names-and-ids.csv";
    private static final String PHENOTYPES_SAME_IDS_DIFFERENT_NAMES = "/phenotypes/same-ids-different-names.csv";
    
    private static final String MARKERS_NO_HEADERS = "/biallelic/no-names-no-marker-names.csv";
    private static final String MARKERS_UNIQUE_NAMES = "/biallelic/names.csv";
    private static final String MARKERS_NON_UNIQUE_NAMES = "/biallelic/names-and-ids.csv";
    private static final String MARKERS_IDS_SOME_NAMES = "/biallelic/ids-with-some-names.csv";

    private SimpleEntity[] expectedHeaders;
    
    @BeforeClass
    public static void beforeClass(){
        System.out.println("Test Core Hunter data");
    }
    
    @AfterClass
    public static void afterClass(){
        System.out.println("Done");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAllDatasetsUndefined(){
        System.out.println(" |- Test no data defined");
        CoreHunterData data = new CoreHunterData(null, null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithIncompatibleHeaders() throws IOException {
        
        System.out.println(" |- Test with incompatible headers");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_UNIQUE_NAMES);
        FeatureData pheno = readPhenotypicTraitData(PHENOTYPES_NO_HEADERS);
        GenotypeVariantData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithIncompatibleHeaders2() throws IOException {
        
        System.out.println(" |- Test with incompatible headers (2)");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_UNIQUE_NAMES);
        FeatureData pheno = null;
        GenotypeVariantData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithIncompatibleHeaders3() throws IOException {
        
        System.out.println(" |- Test with incompatible headers (3)");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_UNIQUE_NAMES);
        FeatureData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        GenotypeVariantData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDifferentSizes() throws IOException {
        
        System.out.println(" |- Test with different sizes");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_NO_HEADERS_SMALL);
        FeatureData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        GenotypeVariantData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSameIdsDifferentNames() throws IOException {
        
        System.out.println(" |- Test with same ids but different names");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_NON_UNIQUE_NAMES);
        FeatureData pheno = readPhenotypicTraitData(PHENOTYPES_SAME_IDS_DIFFERENT_NAMES);
        GenotypeVariantData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test
    public void testWhenAllDatasetsHaveSameHeaders() throws IOException {
        
        System.out.println(" |- Test when all datsets have same headers");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_UNIQUE_NAMES);
        FeatureData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        GenotypeVariantData geno = readMarkerData(MARKERS_UNIQUE_NAMES);
        
        expectedHeaders = TestData.HEADERS_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        testData(data);
        
    }
    
    @Test
    public void testIfNamesAreMerged() throws IOException {
        
        System.out.println(" |- Test if names are merged");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_NON_UNIQUE_NAMES);
        FeatureData pheno = readPhenotypicTraitData(PHENOTYPES_NON_UNIQUE_NAMES);
        GenotypeVariantData geno = readMarkerData(MARKERS_IDS_SOME_NAMES);
        
        expectedHeaders = TestData.HEADERS_NON_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        testData(data);
        
    }
    
    @Test
    public void testDistancesOnly() throws IOException {
        
        System.out.println(" |- Test with distances only");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_NON_UNIQUE_NAMES);
        expectedHeaders = TestData.HEADERS_NON_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(dist);
        testData(data);
        
    }
    
    @Test
    public void testMarkersOnly() throws IOException {
        
        System.out.println(" |- Test with markers only");
        
        GenotypeVariantData markers = readMarkerData(MARKERS_UNIQUE_NAMES);
        expectedHeaders = TestData.HEADERS_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(markers);
        testData(data);
        
    }
    
    @Test
    public void testPhenotypesOnly() throws IOException {
        
        System.out.println(" |- Test with phenotypes only");
        
        FeatureData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        expectedHeaders = TestData.HEADERS_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(pheno);
        testData(data);
        
    }
    
    private DistanceMatrixData readDistanceMatrixData(String file) throws IOException{
        return SimpleDistanceMatrixData.readData(
            Paths.get(CoreHunterDataTest.class.getResource(file).getPath()),
            inferFileType(file), SymmetricMatrixFormat.FULL
        );
    }
    
    private FeatureData readPhenotypicTraitData(String file) throws IOException {
        return ArrayFeatureData.readData(
            Paths.get(CoreHunterDataTest.class.getResource(file).getPath()),
            inferFileType(file)
        );
    }
    
    private GenotypeVariantData readMarkerData(String file) throws IOException{
        return SimpleBiAllelicGenotypeVariantData.readData(
            Paths.get(CoreHunterDataTest.class.getResource(file).getPath()),
            inferFileType(file)
        );
    }
    
    private FileType inferFileType(String file){
        return file.endsWith(".txt") ? FileType.TXT : FileType.CSV;
    }
    
    private void testData(CoreHunterData data){
        
        // check dataset name
        assertEquals("Incorrect data name.", "Core Hunter data", data.getName());
        
        // check dataset size
        assertEquals("Incorrect data size.", expectedHeaders.length, data.getSize());
        
        // check IDs
        assertEquals("Incorrect integer IDs.", TestData.SET, data.getIDs());
        
        // check headers
        for(int i = 0; i < data.getSize(); i++){
            
            assertEquals("Incorrect header for item " + i + ".", expectedHeaders[i], data.getHeader(i));
            
        }
        
    }
    
}
