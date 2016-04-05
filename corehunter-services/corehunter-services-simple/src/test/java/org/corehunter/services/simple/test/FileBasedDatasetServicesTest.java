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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.corehunter.data.CoreHunterData;
import org.corehunter.services.DataType;
import org.corehunter.services.simple.FileBasedDatasetServices;
import org.junit.Test;

import uno.informatics.common.io.FileType;
import uno.informatics.data.Dataset;
import uno.informatics.data.pojo.DatasetPojo;

public class FileBasedDatasetServicesTest {

    private static final String PHENOTYPIC_FILE = "phenotypic_data.csv";
    private static final String NAME1 = "phenotypic_data.csv";
    private static final String DATA_UID = "dataset1";
    private static final String DATASET_NAME = "dataset 1";

    @Test
    public void testAddDatasetNoData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertEquals("Added Dataset name is not correct", dataset.getName(), addedDataset.getName());
            assertEquals("Added Dataset unique identifier is not correct", dataset.getUniqueIdentifier(),
                    addedDataset.getUniqueIdentifier());
            assertEquals("Added Dataset abbreviation is not correct", dataset.getAbbreviation(),
                    addedDataset.getAbbreviation());
            assertEquals("Added Dataset description is not correct", dataset.getDescription(),
                    addedDataset.getDescription());
            assertEquals("Added Dataset type is not correct", dataset.getType(), addedDataset.getType());
            assertEquals("Added Dataset study is not correct", dataset.getStudy(), addedDataset.getStudy());

            List<Dataset> datasets = fileBasedDatasetServices.getAllDatasets();

            assertEquals("Number of datasets is not 1", 1, datasets.size());

            Dataset addedDatasetInList = datasets.get(0);

            assertEquals("Added Dataset In List is not correct", dataset.getName(), addedDatasetInList.getName());
            assertEquals("Added Dataset In List  unique identifier is not correct", dataset.getUniqueIdentifier(),
                    addedDatasetInList.getUniqueIdentifier());
            assertEquals("Added Dataset In List abbreviation is not correct", dataset.getAbbreviation(),
                    addedDatasetInList.getAbbreviation());
            assertEquals("Added Dataset In List description is not correct", dataset.getDescription(),
                    addedDatasetInList.getDescription());
            assertEquals("Added Dataset type is not correct", dataset.getType(), addedDatasetInList.getType());
            assertEquals("Added Dataset study is not correct", dataset.getStudy(), addedDatasetInList.getStudy());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRemoveDatasetNoData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Added dataset found", addedDataset);

            boolean removed = fileBasedDatasetServices.removeDataset(addedDataset.getUniqueIdentifier());

            assertTrue("Dataset not removed", removed);

            List<Dataset> datasets = fileBasedDatasetServices.getAllDatasets();

            assertEquals("Number of dataset is not 0", 0, datasets.size());

            Dataset dataset3 = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNull("Dataset is still found after removing!", dataset3);

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testSimpleRestoreNoData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Path path = fileBasedDatasetServices.getPath();

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertEquals("Restored Dataset name is not correct", dataset.getName(), addedDataset.getName());
            assertEquals("Restored Dataset unique identifier is not correct", dataset.getUniqueIdentifier(),
                    addedDataset.getUniqueIdentifier());
            assertEquals("Restored Dataset abbreviation is not correct", dataset.getAbbreviation(),
                    addedDataset.getAbbreviation());
            assertEquals("Restored Dataset description is not correct", dataset.getDescription(),
                    addedDataset.getDescription());
            assertEquals("Restored Dataset type is not correct", dataset.getType(), addedDataset.getType());
            assertEquals("Restored Dataset study is not correct", dataset.getStudy(), addedDataset.getStudy());

            List<Dataset> datasets = fileBasedDatasetServices.getAllDatasets();

            assertEquals("Number of datasets is not 1", 1, datasets.size());

            Dataset addedDatasetInList = datasets.get(0);

            assertEquals("Restored Dataset In List is not correct", dataset.getName(), addedDatasetInList.getName());
            assertEquals("Restored Dataset In List  unique identifier is not correct", dataset.getUniqueIdentifier(),
                    addedDatasetInList.getUniqueIdentifier());
            assertEquals("Restored Dataset In List abbreviation is not correct", dataset.getAbbreviation(),
                    addedDatasetInList.getAbbreviation());
            assertEquals("Restored Dataset In List description is not correct", dataset.getDescription(),
                    addedDatasetInList.getDescription());
            assertEquals("Restored Dataset type is not correct", dataset.getType(), addedDatasetInList.getType());
            assertEquals("Restored Dataset study is not correct", dataset.getStudy(), addedDatasetInList.getStudy());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }
    
    //@Test
    public void testAddDatasetWithPhenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV, DataType.PHENOTYPIC);

            CoreHunterData data = fileBasedDatasetServices.getData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", data);

            assertNotNull("Phenotypic Data not found", data.getPhenotypicData());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }
    
    //@Test
    public void testRestoreDatasetWithPhenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));
            
            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV, DataType.PHENOTYPIC);
            
            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            CoreHunterData data = fileBasedDatasetServices.getData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", data);

            assertNotNull("Restored Phenotypic Data not found", data.getPhenotypicData());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }
}
