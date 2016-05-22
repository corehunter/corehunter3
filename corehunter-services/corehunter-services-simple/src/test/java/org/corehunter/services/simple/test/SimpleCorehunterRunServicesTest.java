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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.corehunter.services.CorehunterRun;
import org.corehunter.services.CorehunterRunArguments;
import org.corehunter.services.CorehunterRunStatus;
import org.corehunter.services.DataType;
import org.corehunter.services.simple.CorehunterRunArgumentsPojo;
import org.corehunter.services.simple.FileBasedDatasetServices;
import org.corehunter.services.simple.SimpleCorehunterRunServices;
import org.junit.Test;

import uno.informatics.data.Dataset;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.DatasetPojo;

public class SimpleCorehunterRunServicesTest {

    private static final String PHENOTYPIC_FILE = "phenotypic_data.csv";
    private static final String NAME1 = "phenotypic_data.csv";
    private static final int SUBSET_SIZE1 = 10;
    private static final String UNIQUE_IDENTIFIER1 = "phenotypic_data.csv";
    private static final String DESCRIPTION1 = "Dataset loading from ";
    private static final String ABBREVIATION1 = null;
    private static final String DATA_UID = "dataset1";
    private static final String DATASET_NAME = "dataset 1";

    @Test
    public void testSimpleCorehunterRunServices() {

        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            new SimpleCorehunterRunServices(fileBasedDatasetServices);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    //@Test
    public void testExecuteCorehunter() {

        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));
            
            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);
            
            String datasetId = dataset.getUniqueIdentifier() ;

            fileBasedDatasetServices.addDataset(dataset);

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(dataset, phenotypicDataPath, FileType.CSV,
                    DataType.PHENOTYPIC);
            
            

            SimpleCorehunterRunServices simpleCorehunterRunServices = new SimpleCorehunterRunServices(
                    fileBasedDatasetServices);

            CorehunterRunArguments arguments = new CorehunterRunArgumentsPojo(NAME1, SUBSET_SIZE1, datasetId, null);

            CorehunterRun startCorehunterRun = simpleCorehunterRunServices.executeCorehunter(arguments);
            
            String corehunterRunId = startCorehunterRun.getUniqueIdentifier() ;

            simpleCorehunterRunServices.getOutputStream(datasetId);

            assertEquals("Status is not not started", CorehunterRunStatus.NOT_STARTED, startCorehunterRun.getStatus());

            CorehunterRun corehunterRun = startCorehunterRun;

            while (CorehunterRunStatus.NOT_STARTED.equals(corehunterRun.getStatus())
                    || CorehunterRunStatus.RUNNING.equals(corehunterRun.getStatus())) {
                corehunterRun = simpleCorehunterRunServices.getCorehunterRun(corehunterRunId);
                
                Thread.sleep(100);
            }

            assertEquals("Status is not finished", CorehunterRunStatus.FINISHED, corehunterRun.getStatus());

            List<CorehunterRun> allCorehunterRuns = simpleCorehunterRunServices.getAllCorehunterRuns();

            assertEquals("Number of corehunterRuns is not 1", 1, allCorehunterRuns.size());

            assertNotNull("Output Stream is null", simpleCorehunterRunServices.getOutputStream(datasetId));

            assertNull("Error Stream is not null", simpleCorehunterRunServices.getErrorStream(datasetId));

            assertNull("Error Message is not null", simpleCorehunterRunServices.getErrorMessage(datasetId));

            assertNotNull("Subset Solution is null", simpleCorehunterRunServices.getSubsetSolution(datasetId));

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }
}
