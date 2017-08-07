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

package org.corehunter.tests;

import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.DISTANCES;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_FEATURES;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_VALUES_WITH_HEADERS;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import org.corehunter.CoreHunter;
import org.corehunter.CoreHunterArguments;
import org.corehunter.CoreHunterExecutionMode;
import org.corehunter.CoreHunterMeasure;
import org.corehunter.CoreHunterObjective;
import org.corehunter.CoreHunterObjectiveType;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.PhenotypeData;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleFrequencyGenotypeData;
import org.corehunter.data.simple.SimplePhenotypeData;
import org.corehunter.objectives.AverageAccessionToNearestEntry;
import org.corehunter.objectives.AverageEntryToNearestEntry;
import org.corehunter.objectives.HeterozygousLoci;
import org.corehunter.objectives.distance.measures.GowerDistance;
import org.corehunter.objectives.distance.measures.PrecomputedDistance;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.exh.ExhaustiveSearch;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.algo.exh.SubsetSolutionIterator;

import org.junit.Assert;
import org.junit.Test;

import uno.informatics.data.io.FileType;
import org.corehunter.data.FrequencyGenotypeData;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class ITCorehunter {
    
    private static final CoreHunterData DISTANCES_DATA;
    private static final CoreHunterData GENOTYPES_DATA;
    private static final CoreHunterData PHENOTYPES_DATA;
    private static final CoreHunterData DATA;
    
    private static final int SECOND = 1000;
    
    static {
        
        DistanceMatrixData distances = new SimpleDistanceMatrixData(HEADERS_UNIQUE_NAMES, DISTANCES);
        DISTANCES_DATA = new CoreHunterData(distances);
        
        FrequencyGenotypeData genotypes = new SimpleFrequencyGenotypeData(
                HEADERS_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );
        GENOTYPES_DATA = new CoreHunterData(genotypes);
        
        PhenotypeData phenotypes = new SimplePhenotypeData(
                NAME, PHENOTYPIC_TRAIT_FEATURES, PHENOTYPIC_TRAIT_VALUES_WITH_HEADERS
        );
        PHENOTYPES_DATA = new CoreHunterData(phenotypes);
        
        DATA = new CoreHunterData(genotypes, phenotypes, distances);
    }   
    
    /*
     * Test execution with distance matrix.
     */
    @Test
    public void testExecuteDistanceMatrix() {
        
        System.out.println(" - sample from distance matrix (2 sec time limit)");

        CoreHunterData data = DISTANCES_DATA;
        
        int size = 2;
        int time = 2 * SECOND;

        // run Core Hunter
        CoreHunterArguments arguments = 
                new CoreHunterArguments(data, size, 
                        CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, 
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE);
        CoreHunter corehunter = new CoreHunter();
        corehunter.setTimeLimit(time);
        SubsetSolution result = corehunter.execute(arguments);

        // compare with optimal solution
        Objective<SubsetSolution, CoreHunterData> obj = new AverageEntryToNearestEntry(new PrecomputedDistance());
        Assert.assertEquals(getOptimalSolution(data, obj, size), result);

    }
    
    @Test
    public void testMultiObjectiveConfiguration(){
        
        System.out.println(" - multi-objective (default <= 10 sec without improvement)");

        CoreHunterData data = DATA;
        
        int size = 3;
        Objective<SubsetSolution, CoreHunterData> he = new HeterozygousLoci();
        Objective<SubsetSolution, CoreHunterData> ane = new AverageAccessionToNearestEntry(new GowerDistance());
        
        // determine optimal solution for each objective through exhaustive search
        SubsetSolution heOpt = getOptimalSolution(data, he, size);
        SubsetSolution aneOpt = getOptimalSolution(data, ane, size);
        
        // run Core Hunter (with equal weights for all objectives)
        List<CoreHunterObjective> chObjs = Arrays.asList(
                new CoreHunterObjective(CoreHunterObjectiveType.HETEROZYGOUS_LOCI),
                new CoreHunterObjective(
                        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS
                )
        );
        CoreHunterArguments arguments = new CoreHunterArguments(data, size, chObjs);
        CoreHunter corehunter = new CoreHunter();
        SubsetSolution result = corehunter.execute(arguments);
        
        // check: solution should be located on Pareto front
        double heValue = he.evaluate(result, data).getValue();
        double aneValue = ane.evaluate(result, data).getValue();
        // heterozygous loci is maximized, A-NE distance is minimized
        double heMax = he.evaluate(heOpt, data).getValue();
        double heMin = he.evaluate(aneOpt, data).getValue();
        double aneMin = ane.evaluate(aneOpt, data).getValue();
        double aneMax = ane.evaluate(heOpt, data).getValue();
        
        Assert.assertTrue(heValue <= heMax);
        Assert.assertTrue(heValue >= heMin);
        Assert.assertTrue(aneValue <= aneMax);
        Assert.assertTrue(aneValue >= aneMin);
        
    }
    
    /*
     * Test execution with genotype data and fixed seed.
     */
    @Test
    public void testGenotypeDataWithSeed() {
        
        System.out.println(" - 5 x sample from genotype data with fixed seed (1 sec time limit)");

        CoreHunterData data = GENOTYPES_DATA;
        
        int size = 3;
        int time = 1 * SECOND;
        long seed = 42;
        
        Set<SubsetSolution> results = new HashSet<>();
        for(int i = 0; i < 5; i++){
            // run Core Hunter
            CoreHunterArguments arguments = 
                    new CoreHunterArguments(data, size, 
                            CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, 
                            CoreHunterMeasure.MODIFIED_ROGERS);
            CoreHunter corehunter = new CoreHunter();
            corehunter.setTimeLimit(time);
            corehunter.setSeed(seed);
            SubsetSolution result = corehunter.execute(arguments);
            results.add(result);
        }
        
        Assert.assertEquals(1, results.size());

    }
    
    /*
     * Test execution with large genotype data and fixed seed.
     */
    @Test
    public void testLargeGenoWithSeed() throws IOException{
        
        System.out.println(" - 5 x sample n=2 from large genotype data with fixed seed (3 sec time limit) ");
        
        FrequencyGenotypeData geno = SimpleBiAllelicGenotypeData.readData(
            Paths.get(ITCorehunter.class.getResource("/biallelic_genotypes/biallelic_genotypes_data.csv").getPath()),
            FileType.CSV
        );
        CoreHunterData data = new CoreHunterData(geno);
        
        int size = 2;
        int time = 3 * SECOND;
        long seed = 42;
        
        Set<SubsetSolution> results = new HashSet<>();
        for(int i = 0; i < 5; i++){
            // run Core Hunter
            CoreHunterArguments arguments = 
                    new CoreHunterArguments(data, size, 
                            CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, 
                            CoreHunterMeasure.MODIFIED_ROGERS);
            CoreHunter corehunter = new CoreHunter();
            corehunter.setTimeLimit(time);
            corehunter.setSeed(seed);
            SubsetSolution result = corehunter.execute(arguments);
            results.add(result);
        }
        
        Assert.assertEquals(1, results.size());
        
    }
    
    /*
     * Test execution with large genotype data and fixed seed, in fast mode.
     */
    @Test
    public void testLargeGenoWithSeedFastMode() throws IOException{
        
        System.out.println(" - 5 x sample n=2 from large genotype data with fixed seed (fast, 3 sec time limit) ");
        
        FrequencyGenotypeData geno = SimpleBiAllelicGenotypeData.readData(
            Paths.get(ITCorehunter.class.getResource("/biallelic_genotypes/biallelic_genotypes_data.csv").getPath()),
            FileType.CSV
        );
        CoreHunterData data = new CoreHunterData(geno);
        
        int size = 2;
        int time = 3 * SECOND;
        long seed = 42;
        
        Set<SubsetSolution> results = new HashSet<>();
        for(int i = 0; i < 5; i++){
            // run Core Hunter
            CoreHunterArguments arguments = 
                    new CoreHunterArguments(data, size, 
                            CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, 
                            CoreHunterMeasure.MODIFIED_ROGERS);
            CoreHunter corehunter = new CoreHunter(CoreHunterExecutionMode.FAST);
            corehunter.setTimeLimit(time);
            corehunter.setSeed(seed);
            SubsetSolution result = corehunter.execute(arguments);
            results.add(result);
        }
        
        Assert.assertEquals(1, results.size());
        
    }
    
    /*
     * Test execution with large genotype data, multiple objectives (not normalized), and fixed seed.
     */
    @Test
    public void testLargeGenoMultiObjWithSeed() throws IOException{
        
        System.out.println(
            " - 5 x sample n=2 from large genotype data with fixed seed (multi-obj, not normalized, 10 sec time limit)"
        );
        
        FrequencyGenotypeData geno = SimpleBiAllelicGenotypeData.readData(
            Paths.get(ITCorehunter.class.getResource("/biallelic_genotypes/biallelic_genotypes_data.csv").getPath()),
            FileType.CSV
        );
        CoreHunterData data = new CoreHunterData(geno);
        
        int size = 2;
        int time = 10 * SECOND;
        long seed = 42;
        
        Set<SubsetSolution> results = new HashSet<>();
        for(int i = 0; i < 5; i++){
            // run Core Hunter
            CoreHunterArguments arguments = 
                    new CoreHunterArguments(data, size, Arrays.asList(
                        new CoreHunterObjective(
                                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                                CoreHunterMeasure.MODIFIED_ROGERS
                        ),
                        new CoreHunterObjective(
                                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                                CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
                        )
                    ), Collections.emptySet(), Collections.emptySet(), false); // disable normalization
            CoreHunter corehunter = new CoreHunter();
            corehunter.setTimeLimit(time);
            corehunter.setSeed(seed);
            SubsetSolution result = corehunter.execute(arguments);
            results.add(result);
        }
        
        Assert.assertEquals(1, results.size());
        
    }
    
    /*
     * Test execution with large genotype data, multiple objectives (normalized), and fixed seed.
     */
    @Test
    public void testLargeGenoMultiObjNormalizedWithSeed() throws IOException{
        
        System.out.println(
            " - 5 x sample n=2 from large genotype data with fixed seed (multi-obj, normalized, 10 sec time limit)"
        );
        
        FrequencyGenotypeData geno = SimpleBiAllelicGenotypeData.readData(
            Paths.get(ITCorehunter.class.getResource("/biallelic_genotypes/biallelic_genotypes_data.csv").getPath()),
            FileType.CSV
        );
        CoreHunterData data = new CoreHunterData(geno);
        
        int size = 2;
        int time = 10 * SECOND;
        long seed = 42;
        
        Set<SubsetSolution> results = new HashSet<>();
        for(int i = 0; i < 5; i++){
            // run Core Hunter
            CoreHunterArguments arguments = 
                    new CoreHunterArguments(data, size, Arrays.asList(
                        new CoreHunterObjective(
                                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                                CoreHunterMeasure.MODIFIED_ROGERS
                        ),
                        new CoreHunterObjective(
                                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                                CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
                        )
                    )); // with normalization
            CoreHunter corehunter = new CoreHunter();
            corehunter.setTimeLimit(time);
            corehunter.setSeed(seed);
            SubsetSolution result = corehunter.execute(arguments);
            results.add(result);
        }
        
        Assert.assertEquals(1, results.size());
        
    }
    
    /*
     * Test execution with phenotype data and always selected IDs.
     */
    @Test
    public void testPhenotypeDataWithAlwaysSelectedIDs() {
        
        System.out.println(" - phenotype data with always selected ids");

        CoreHunterData data = PHENOTYPES_DATA;
        
        int size = 3;
        int time = 1 * SECOND;
        
        Set<Integer> always = new HashSet<>(Arrays.asList(0, 3));
        
        List<CoreHunterObjective> obj = Arrays.asList(
            new CoreHunterObjective(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS
            )
        );
        CoreHunterArguments arguments = new CoreHunterArguments(data, size, obj, always);
        CoreHunter corehunter = new CoreHunter();
        corehunter.setTimeLimit(time);
        SubsetSolution result = corehunter.execute(arguments);
        
        Assert.assertTrue(result.getSelectedIDs().contains(0));
        Assert.assertTrue(result.getSelectedIDs().contains(3));

    }
    
    /*
     * Test execution with phenotype data and always selected IDs (fast mode).
     */
    @Test
    public void testPhenotypeDataWithAlwaysSelectedIDsFastMode() {
        
        System.out.println(" - phenotype data with always selected ids (fast mode)");

        CoreHunterData data = PHENOTYPES_DATA;
        
        int size = 3;
        int time = 1 * SECOND;
        
        Set<Integer> always = new HashSet<>(Arrays.asList(0, 3));
        
        List<CoreHunterObjective> obj = Arrays.asList(
            new CoreHunterObjective(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS
            )
        );
        CoreHunterArguments arguments = new CoreHunterArguments(data, size, obj, always);
        CoreHunter corehunter = new CoreHunter(CoreHunterExecutionMode.FAST);
        corehunter.setTimeLimit(time);
        SubsetSolution result = corehunter.execute(arguments);
        
        Assert.assertTrue(result.getSelectedIDs().contains(0));
        Assert.assertTrue(result.getSelectedIDs().contains(3));

    }
    
    /*
     * Test execution with phenotype data and always selected IDs (multi-objective).
     */
    @Test
    public void testPhenotypeDataWithAlwaysSelectedIDsMultiObj() {
        
        System.out.println(" - phenotype data with always selected ids (multi-objective)");

        CoreHunterData data = PHENOTYPES_DATA;
        
        int size = 3;
        int time = 1 * SECOND;
        
        Set<Integer> always = new HashSet<>(Arrays.asList(0, 3));
        
        List<CoreHunterObjective> obj = Arrays.asList(
            new CoreHunterObjective(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS
            ),
            new CoreHunterObjective(
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS
            )
        );
        CoreHunterArguments arguments = new CoreHunterArguments(data, size, obj, always);
        CoreHunter corehunter = new CoreHunter();
        corehunter.setTimeLimit(time);
        SubsetSolution result = corehunter.execute(arguments);
        
        Assert.assertTrue(result.getSelectedIDs().contains(0));
        Assert.assertTrue(result.getSelectedIDs().contains(3));

    }

    /*
     * Test execution with phenotype data and always and never selected IDs.
     */
    @Test
    public void testPhenotypeDataWithAlwaysAndNeverSelectedIDs() {
        
        System.out.println(" - phenotype data with always and never selected ids");

        CoreHunterData data = PHENOTYPES_DATA;
        
        int size = 3;
        int time = 1 * SECOND;
        
        Set<Integer> always = new HashSet<>(Arrays.asList(0, 3));
        Set<Integer> never = new HashSet<>(Arrays.asList(1));
        
        List<CoreHunterObjective> obj = Arrays.asList(
            new CoreHunterObjective(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS
            )
        );
        CoreHunterArguments arguments = new CoreHunterArguments(data, size, obj, always, never);
        CoreHunter corehunter = new CoreHunter();
        corehunter.setTimeLimit(time);
        SubsetSolution result = corehunter.execute(arguments);
        
        Assert.assertTrue(result.getSelectedIDs().contains(0));
        Assert.assertTrue(result.getSelectedIDs().contains(3));
        Assert.assertFalse(result.getSelectedIDs().contains(1));

    }
    
    /*
     * Test execution with phenotype data and always and never selected IDs (2).
     */
    @Test
    public void testPhenotypeDataWithAlwaysAndNeverSelectedIDs2() {
        
        System.out.println(" - phenotype data with always and never selected ids (2)");

        CoreHunterData data = PHENOTYPES_DATA;
        
        int size = 3;
        int time = 1 * SECOND;
        
        Set<Integer> always = new HashSet<>(Arrays.asList(0, 3));
        Set<Integer> never = new HashSet<>(Arrays.asList(1, 2));
        
        List<CoreHunterObjective> obj = Arrays.asList(
            new CoreHunterObjective(
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS
            )
        );
        CoreHunterArguments arguments = new CoreHunterArguments(data, size, obj, always, never);
        CoreHunter corehunter = new CoreHunter();
        corehunter.setTimeLimit(time);
        SubsetSolution result = corehunter.execute(arguments);
        
        Assert.assertEquals(
                new HashSet<>(Arrays.asList(0, 3, 4)),
                result.getSelectedIDs()
        );

    }
    
    // get best solution through exhaustive search
    private SubsetSolution getOptimalSolution(CoreHunterData data,
                                              Objective<SubsetSolution, CoreHunterData> obj,
                                              int size){
        SubsetProblem<CoreHunterData> problem = new SubsetProblem<>(data, obj, size);
        Search<SubsetSolution> exh = new ExhaustiveSearch<>(problem, new SubsetSolutionIterator(data.getIDs(), size));
        exh.run();
        return exh.getBestSolution();
    }

}
