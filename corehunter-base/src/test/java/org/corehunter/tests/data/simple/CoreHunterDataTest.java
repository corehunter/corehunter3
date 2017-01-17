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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.corehunter.CoreHunterObjectiveType;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeData;
import org.corehunter.data.PhenotypeData;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimplePhenotypeData;
import org.corehunter.tests.TestData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;


/**
 * @author Herman De Beukelaer
 */
public class CoreHunterDataTest {

    private static final String DISTANCES_SMALL = "/distances/small-ids.txt";
    private static final String DISTANCES_UNIQUE_NAMES = "/distances/full-ids.txt";
    private static final String DISTANCES_NON_UNIQUE_NAMES = "/distances/full-ids-names.txt";
    
    private static final String PHENOTYPES_UNIQUE_NAMES = "/phenotypes/ids.csv";
    private static final String PHENOTYPES_NON_UNIQUE_NAMES = "/phenotypes/ids-and-names.csv";
    private static final String PHENOTYPES_SAME_IDS_DIFFERENT_NAMES = "/phenotypes/same-ids-different-names.csv";
    
    private static final String MARKERS_UNIQUE_NAMES = "/biallelic_genotypes/ids.csv";
    private static final String MARKERS_NON_UNIQUE_NAMES = "/biallelic_genotypes/ids-and-names.csv";
    private static final String MARKERS_IDS_SOME_NAMES = "/biallelic_genotypes/ids-with-some-names.csv";

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
        
        assertNotNull(data.getValidObjectiveTypes()) ;
        assertTrue("Valid Objective Types should be empty", data.getValidObjectiveTypes().isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.COVERAGE)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.COVERAGE).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.HETEROZYGOUS_LOCI)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.SHANNON_DIVERSITY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.SHANNON_DIVERSITY).isEmpty()) ;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithIncompatibleHeaders() throws IOException {
        
        System.out.println(" |- Test with incompatible headers");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_UNIQUE_NAMES);
        PhenotypeData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        GenotypeData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);      
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithIncompatibleHeaders2() throws IOException {
        
        System.out.println(" |- Test with incompatible headers (2)");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_UNIQUE_NAMES);
        PhenotypeData pheno = null;
        GenotypeData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithIncompatibleHeaders3() throws IOException {
        
        System.out.println(" |- Test with incompatible headers (3)");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_UNIQUE_NAMES);
        PhenotypeData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        GenotypeData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDifferentSizes() throws IOException {
        
        System.out.println(" |- Test with different sizes");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_SMALL);
        PhenotypeData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        GenotypeData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSameIdsDifferentNames() throws IOException {
        
        System.out.println(" |- Test with same ids but different names");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_NON_UNIQUE_NAMES);
        PhenotypeData pheno = readPhenotypicTraitData(PHENOTYPES_SAME_IDS_DIFFERENT_NAMES);
        GenotypeData geno = readMarkerData(MARKERS_NON_UNIQUE_NAMES);
                
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        
    }
    
    @Test
    public void testWhenAllDatasetsHaveSameHeaders() throws IOException {
                
        System.out.println(" |- Test when all datsets have same headers");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_UNIQUE_NAMES);
        PhenotypeData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        GenotypeData geno = readMarkerData(MARKERS_UNIQUE_NAMES);
        
        expectedHeaders = TestData.HEADERS_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        testData(data);
        
        assertNotNull(data.getValidObjectiveTypes()) ;
        assertEquals("Number of Objective Types should be 6", 6, data.getValidObjectiveTypes().size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 4", 4, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY)) ;
        assertEquals("Number of Measures should be 4", 4, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 4", 4, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.COVERAGE)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.COVERAGE).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.HETEROZYGOUS_LOCI)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.SHANNON_DIVERSITY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.SHANNON_DIVERSITY).isEmpty()) ;
    }
    
    @Test
    public void testIfNamesAreMerged() throws IOException {
        
        System.out.println(" |- Test if names are merged");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_NON_UNIQUE_NAMES);
        PhenotypeData pheno = readPhenotypicTraitData(PHENOTYPES_NON_UNIQUE_NAMES);
        GenotypeData geno = readMarkerData(MARKERS_IDS_SOME_NAMES);
        
        expectedHeaders = TestData.HEADERS_NON_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(geno, pheno, dist);
        testData(data);
        
        assertNotNull(data.getValidObjectiveTypes()) ;
        assertEquals("Number of Objective Types should be 6", 6, data.getValidObjectiveTypes().size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 4", 4, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY)) ;
        assertEquals("Number of Measures should be 4", 4, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 4", 4, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.COVERAGE)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.COVERAGE).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.HETEROZYGOUS_LOCI)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.SHANNON_DIVERSITY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.SHANNON_DIVERSITY).isEmpty()) ;
    }
    
    @Test
    public void testDistancesOnly() throws IOException {
        
        System.out.println(" |- Test with distances only");
        
        DistanceMatrixData dist = readDistanceMatrixData(DISTANCES_NON_UNIQUE_NAMES);
        expectedHeaders = TestData.HEADERS_NON_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(dist);
        testData(data);
        
        assertNotNull(data.getValidObjectiveTypes()) ;
        assertEquals("Number of Objective Types should be 3", 3, data.getValidObjectiveTypes().size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 1", 1, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY)) ;
        assertEquals("Number of Measures should be 1", 1, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 1", 1, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.COVERAGE)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.COVERAGE).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.HETEROZYGOUS_LOCI)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.SHANNON_DIVERSITY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.SHANNON_DIVERSITY).isEmpty()) ;
    }
    
    @Test
    public void testMarkersOnly() throws IOException {
        
        System.out.println(" |- Test with markers only");
        
        GenotypeData markers = readMarkerData(MARKERS_UNIQUE_NAMES);
        expectedHeaders = TestData.HEADERS_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(markers);
        testData(data);
        
        assertNotNull(data.getValidObjectiveTypes()) ;
        assertEquals("Number of Objective Types should be 6", 6, data.getValidObjectiveTypes().size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 2", 2, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY)) ;
        assertEquals("Number of Measures should be 2", 2, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 2", 2, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.COVERAGE)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.COVERAGE).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.HETEROZYGOUS_LOCI)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.SHANNON_DIVERSITY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.SHANNON_DIVERSITY).isEmpty()) ;
    }
    
    @Test
    public void testPhenotypesOnly() throws IOException {
        
        System.out.println(" |- Test with phenotypes only");
        
        PhenotypeData pheno = readPhenotypicTraitData(PHENOTYPES_UNIQUE_NAMES);
        expectedHeaders = TestData.HEADERS_UNIQUE_NAMES;
        
        CoreHunterData data = new CoreHunterData(pheno);
        testData(data);
        
        assertNotNull(data.getValidObjectiveTypes()) ;
        assertEquals("Number of Objective Types should be 3", 3, data.getValidObjectiveTypes().size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 1", 1, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY)) ;
        assertEquals("Number of Measures should be 1", 1, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY)) ;
        assertEquals("Number of Measures should be 1", 1, data.getValidMeasures(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).size()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.COVERAGE)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.COVERAGE).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.HETEROZYGOUS_LOCI)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).isEmpty()) ;
        assertNotNull(data.getValidMeasures(CoreHunterObjectiveType.SHANNON_DIVERSITY)) ;
        assertTrue("Valid Measures Types should be empty", data.getValidMeasures(
                CoreHunterObjectiveType.SHANNON_DIVERSITY).isEmpty()) ;
    }
    
    private DistanceMatrixData readDistanceMatrixData(String file) throws IOException{
        return SimpleDistanceMatrixData.readData(
            Paths.get(CoreHunterDataTest.class.getResource(file).getPath()),
            inferFileType(file)
        );
    }
    
    private PhenotypeData readPhenotypicTraitData(String file) throws IOException {
        return new SimplePhenotypeData(SimplePhenotypeData.readPhenotypeData(
            Paths.get(CoreHunterDataTest.class.getResource(file).getPath()),
            inferFileType(file))
        );
    }
    
    private GenotypeData readMarkerData(String file) throws IOException{
        return SimpleBiAllelicGenotypeData.readData(
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
