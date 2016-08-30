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
package org.corehunter.services.simple.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.corehunter.CoreHunterMeasure;
import org.corehunter.CoreHunterObjective;
import org.corehunter.CoreHunterObjectiveType;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.CoreHunterDataType;
import org.corehunter.objectives.AverageEntryToEntry;
import org.corehunter.objectives.distance.measures.PrecomputedDistance;
import org.corehunter.services.CoreHunterRun;
import org.corehunter.services.CoreHunterRunServices;
import org.corehunter.services.DatasetServices;
import org.corehunter.services.simple.CoreHunterRunArgumentsPojo;
import org.corehunter.services.simple.FileBasedDatasetServices;
import org.corehunter.services.simple.SimpleCoreHunterRunServices;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.exh.ExhaustiveSearch;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.algo.exh.SubsetSolutionIterator;
import org.junit.Test;

import uno.informatics.data.Dataset;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.DatasetPojo;

/**
 * @author Guy Davenport
 */
public class ITSimpleCoreHunterRunServices {
    
    private static final String DATASET_UID = "dataset1";
    private static final String DATASET_NAME = "dataset 1";
    
    private static final String DISTANCES_DATA = "distances.csv";
    private static final String GENOTYPES_DATA = "genotypes.csv";
    private static final String PHENOTYPES_DATA = "phenotypes.csv";

    private static final String TARGET_DIRECTORY = "target";
    private static final Path ROOT_DIRECTORY = Paths.get(TARGET_DIRECTORY, 
            ITSimpleCoreHunterRunServices.class.getSimpleName());
    

    private Path createTempDirectory() throws IOException {

        Files.createDirectories(ROOT_DIRECTORY);

        Path path = Files.createTempDirectory(ROOT_DIRECTORY, null);

        Files.createDirectories(path);

        return path;
    }

    /**
     * Test execution with distance matrix.
     */
    @Test
    public void testExecuteDistanceMatrix() {
        
        DatasetServices databaseServices = null;
        try {
            databaseServices = new FileBasedDatasetServices(createTempDirectory());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage()) ;
        }  
        CoreHunterRunServices coreHunterRunServices = new SimpleCoreHunterRunServices(databaseServices) ;
        
        Dataset dataset = new DatasetPojo(DATASET_UID, DATASET_NAME); 
        
        try {
            databaseServices.addDataset(dataset);
        } catch (DatasetException e) {
            e.printStackTrace();
            fail(e.getMessage()) ;
        }

        int size = 2 ;

        try {
            Path distancesDataPath = Paths.get(ClassLoader.getSystemResource(DISTANCES_DATA).toURI());
            
            databaseServices.loadData(dataset, distancesDataPath, FileType.CSV, CoreHunterDataType.DISTANCES);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage()) ;
        } catch (DatasetException e) {
            e.printStackTrace();
            fail(e.getMessage()) ;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail(e.getMessage()) ;
        }
        
        CoreHunterRunArgumentsPojo arguments = 
                new CoreHunterRunArgumentsPojo(DISTANCES_DATA, size, DATASET_UID, 
                        new CoreHunterObjective(CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterMeasure.PRECOMPUTED_DISTANCE)) ;
        
        arguments.setTimeLimit(2);
        
        // run Core Hunter
        
        CoreHunterRun run = coreHunterRunServices.executeCoreHunter(arguments) ;
        
        boolean finished = false ;

        while (!finished) {
            
            switch (coreHunterRunServices.getCoreHunterRun(run.getUniqueIdentifier()).getStatus()) {
                case FAILED:
                    finished = true ;
                    break;
                case FINISHED:
                    finished = true ;
                    break;
                case NOT_STARTED:
                case RUNNING:
                default:
                    break;
                
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage()) ;
            }
        }

        switch (coreHunterRunServices.getCoreHunterRun(run.getUniqueIdentifier()).getStatus()) {
            case FAILED:
                assertNotNull("Failed with no error message!", coreHunterRunServices.getErrorMessage(run.getUniqueIdentifier())) ;
                assertNotNull("Failed with no error stream!", coreHunterRunServices.getErrorStream(run.getUniqueIdentifier())) ;
                
                fail(coreHunterRunServices.getErrorMessage(run.getUniqueIdentifier())) ;
                break;
            case FINISHED:
                Objective<SubsetSolution, CoreHunterData> objective = new AverageEntryToEntry(new PrecomputedDistance());

                SubsetSolution result = coreHunterRunServices.getSubsetSolution(run.getUniqueIdentifier()) ;
                
                assertNotNull("Result is null!", result) ;
                assertNotNull("Success but with no output stream!", coreHunterRunServices.getOutputStream(run.getUniqueIdentifier())) ;
                assertNull("Success with error message!", coreHunterRunServices.getErrorMessage(run.getUniqueIdentifier())) ;
                assertNull("Success with error stream!", coreHunterRunServices.getErrorStream(run.getUniqueIdentifier())) ;
                try {
                    // compare with optimal solution
                    assertEquals(getOptimalSolution(databaseServices.getCoreHunterData(DATASET_UID), objective, size), result);
                } catch (DatasetException e) {
                    e.printStackTrace();
                    fail(e.getMessage()) ;
                }
                break;
            case NOT_STARTED:
                fail("Not started!") ;
                break ;
            case RUNNING:
                fail("Still running") ;
                break ;
            default:
                fail("No status") ;
                break ;
        }
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
