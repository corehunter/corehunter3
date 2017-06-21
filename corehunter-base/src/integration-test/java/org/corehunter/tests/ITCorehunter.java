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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.corehunter.CoreHunter;
import org.corehunter.CoreHunterArguments;
import org.corehunter.CoreHunterMeasure;
import org.corehunter.CoreHunterObjective;
import org.corehunter.CoreHunterObjectiveType;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeData;
import org.corehunter.data.PhenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleGenotypeData;
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
import org.junit.Test;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class ITCorehunter {
    
    private static final CoreHunterData DISTANCES_DATA;
    private static final CoreHunterData GENOTYPES_DATA;
    private static final CoreHunterData PHENOTYPES_DATA;
    private static final CoreHunterData DATA;
    
    static {
        
        DistanceMatrixData distances = new SimpleDistanceMatrixData(HEADERS_UNIQUE_NAMES, DISTANCES);
        DISTANCES_DATA = new CoreHunterData(distances);
        
        GenotypeData genotypes = new SimpleGenotypeData(
                HEADERS_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );
        GENOTYPES_DATA = new CoreHunterData(genotypes);
        
        PhenotypeData phenotypes = new SimplePhenotypeData(
                NAME, PHENOTYPIC_TRAIT_FEATURES, PHENOTYPIC_TRAIT_VALUES_WITH_HEADERS
        );
        PHENOTYPES_DATA = new CoreHunterData(phenotypes);
        
        DATA = new CoreHunterData(genotypes, phenotypes, distances);
    }   
    
    /**
     * Test execution with distance matrix.
     */
    @Test
    public void testExecuteDistanceMatrix() {

        CoreHunterData data = DISTANCES_DATA;
        
        int size = 2;
        int time = 2;

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
        assertEquals(getOptimalSolution(data, obj, size), result);

    }
    
    @Test
    public void testMultiObjectiveConfiguration(){

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
        
        assertTrue(heValue <= heMax);
        assertTrue(heValue >= heMin);
        assertTrue(aneValue <= aneMax);
        assertTrue(aneValue >= aneMin);
        
    }
    
    /**
     * Test execution with genotype data and fixed seed.
     */
    @Test
    public void testGenotypeDataWithSeed() {

        CoreHunterData data = GENOTYPES_DATA;
        
        int size = 3;
        int time = 1;
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
        
        assertEquals(1, results.size());

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
