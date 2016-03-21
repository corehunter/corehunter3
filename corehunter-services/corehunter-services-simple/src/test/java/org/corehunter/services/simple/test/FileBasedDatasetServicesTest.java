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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.corehunter.services.simple.FileBasedDatasetServices;
import org.junit.Test;

import uno.informatics.data.Dataset;
import uno.informatics.data.pojo.DatasetPojo;

public class FileBasedDatasetServicesTest {

    private static final String PHENOTYPIC_FILE = "phenotypic_data.csv";
    private static final String NAME1 = "phenotypic_data.csv";
    private static final String DATA_UID = "dataset1";
    private static final String DATASET_NAME = "dataset 1";

    @Test
    public void testSimpleAddAndRemoveDataset() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset dataset1 = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertEquals("Dataset 1 name is not correct", dataset.getName(), dataset1.getName());
            assertEquals("Dataset 1 unique identifier is not correct", dataset.getUniqueIdentifier(),
                    dataset1.getUniqueIdentifier());
            assertEquals("Dataset 1 abbreviation is not correct", dataset.getAbbreviation(),
                    dataset1.getAbbreviation());
            assertEquals("Dataset 1 description is not correct", dataset.getDescription(), dataset1.getDescription());

            List<Dataset> datasets = fileBasedDatasetServices.getAllDatasets();

            assertEquals("Number of dataset id is not 1", 1, datasets.size());

            Dataset dataset2 = datasets.get(0);

            assertEquals("Dataset 2 name is not correct", dataset.getName(), dataset2.getName());
            assertEquals("Dataset 2 unique identifier is not correct", dataset.getUniqueIdentifier(),
                    dataset2.getUniqueIdentifier());
            assertEquals("Dataset 2 abbreviation is not correct", dataset.getAbbreviation(),
                    dataset2.getAbbreviation());
            assertEquals("Dataset 2 description is not correct", dataset.getDescription(), dataset2.getDescription());

            boolean removed = fileBasedDatasetServices.removeDataset(dataset.getUniqueIdentifier());
            
            assertTrue("Dataset not removed", removed);

            datasets = fileBasedDatasetServices.getAllDatasets();

            assertEquals("Number of dataset is not 0", 0, datasets.size());

            Dataset dataset3 = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNull("Dataset 3 not null", dataset3);

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testSimpleRestore() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Path path = fileBasedDatasetServices.getPath();

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset dataset1 = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertEquals("Dataset 1 name is not correct", dataset.getName(), dataset1.getName());
            assertEquals("Dataset 1 unique identifier is not correct", dataset.getUniqueIdentifier(),
                    dataset1.getUniqueIdentifier());
            assertEquals("Dataset 1 abbreviation is not correct", dataset.getAbbreviation(),
                    dataset1.getAbbreviation());
            assertEquals("Dataset 1 description is not correct", dataset.getDescription(), dataset1.getDescription());

            List<Dataset> datasets = fileBasedDatasetServices.getAllDatasets();

            assertEquals("Number of dataset id is not 1", 1, datasets.size());

            Dataset dataset2 = datasets.get(0);

            assertEquals("Dataset 2 name is not correct", dataset.getName(), dataset2.getName());
            assertEquals("Dataset 2 unique identifier is not correct", dataset.getUniqueIdentifier(),
                    dataset2.getUniqueIdentifier());
            assertEquals("Dataset 2 abbreviation is not correct", dataset.getAbbreviation(),
                    dataset2.getAbbreviation());
            assertEquals("Dataset 2 description is not correct", dataset.getDescription(), dataset2.getDescription());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }
}
