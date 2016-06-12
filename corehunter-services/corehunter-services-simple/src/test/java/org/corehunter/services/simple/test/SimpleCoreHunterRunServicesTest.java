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

import org.corehunter.services.CoreHunterRun;
import org.corehunter.services.CoreHunterRunArguments;
import org.corehunter.services.CoreHunterRunStatus;
import org.corehunter.services.DataType;
import org.corehunter.services.simple.CoreHunterRunArgumentsPojo;
import org.corehunter.services.simple.FileBasedDatasetServices;
import org.corehunter.services.simple.SimpleCoreHunterRunServices;
import org.junit.Test;

import uno.informatics.data.Dataset;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.DatasetPojo;

public class SimpleCoreHunterRunServicesTest {

    private static final String PHENOTYPIC_FILE = "phenotypic_data.csv";
    private static final String NAME1 = "phenotypic_data.csv";
    private static final int SUBSET_SIZE1 = 10;
    private static final String UNIQUE_IDENTIFIER1 = "phenotypic_data.csv";
    private static final String DESCRIPTION1 = "Dataset loading from ";
    private static final String ABBREVIATION1 = null;
    private static final String DATA_UID = "dataset1";
    private static final String DATASET_NAME = "dataset 1";

    @Test
    public void testSimpleCoreHunterRunServices() {

        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            new SimpleCoreHunterRunServices(fileBasedDatasetServices);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    //@Test
    public void testExecuteCoreHunter() {

        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));
            
            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);
            
            String datasetId = dataset.getUniqueIdentifier() ;

            fileBasedDatasetServices.addDataset(dataset);

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(dataset, phenotypicDataPath, FileType.CSV,
                    DataType.PHENOTYPIC);
            
            

            SimpleCoreHunterRunServices simpleCoreHunterRunServices = new SimpleCoreHunterRunServices(
                    fileBasedDatasetServices);

            CoreHunterRunArguments arguments = new CoreHunterRunArgumentsPojo(NAME1, SUBSET_SIZE1, datasetId, null);

            CoreHunterRun startCoreHunterRun = simpleCoreHunterRunServices.executeCoreHunter(arguments);
            
            String corehunterRunId = startCoreHunterRun.getUniqueIdentifier() ;

            simpleCoreHunterRunServices.getOutputStream(datasetId);

            assertEquals("Status is not not started", CoreHunterRunStatus.NOT_STARTED, startCoreHunterRun.getStatus());

            CoreHunterRun corehunterRun = startCoreHunterRun;

            while (CoreHunterRunStatus.NOT_STARTED.equals(corehunterRun.getStatus())
                    || CoreHunterRunStatus.RUNNING.equals(corehunterRun.getStatus())) {
                corehunterRun = simpleCoreHunterRunServices.getCoreHunterRun(corehunterRunId);
                
                Thread.sleep(100);
            }

            assertEquals("Status is not finished", CoreHunterRunStatus.FINISHED, corehunterRun.getStatus());

            List<CoreHunterRun> allCoreHunterRuns = simpleCoreHunterRunServices.getAllCoreHunterRuns();

            assertEquals("Number of corehunterRuns is not 1", 1, allCoreHunterRuns.size());

            assertNotNull("Output Stream is null", simpleCoreHunterRunServices.getOutputStream(datasetId));

            assertNull("Error Stream is not null", simpleCoreHunterRunServices.getErrorStream(datasetId));

            assertNull("Error Message is not null", simpleCoreHunterRunServices.getErrorMessage(datasetId));

            assertNotNull("Subset Solution is null", simpleCoreHunterRunServices.getSubsetSolution(datasetId));

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }
}
