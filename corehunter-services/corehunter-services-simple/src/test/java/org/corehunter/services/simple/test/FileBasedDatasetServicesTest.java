/*******************************************************************************
 * Copyright 2016 Guy Davenport
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.corehunter.services.simple.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.corehunter.services.DatasetType;
import org.corehunter.services.simple.FileBasedDatasetServices;
import org.junit.Test;

import uno.informatics.common.io.FileType;
import uno.informatics.data.Dataset;
import uno.informatics.data.FeatureDataset;
import uno.informatics.data.SimpleEntity;

public class FileBasedDatasetServicesTest {

    private static final String PHENOTYPIC_FILE = "phenotypic_data.csv";
    private static final Object NAME1 = "phenotypic_data.csv";
    private static final Object UNIQUE_IDENTIFIER1 = "phenotypic_data.csv";
    private static final Object DESCRIPTION1 = "Dataset loading from ";
    private static final Object ABBREVIATION1 = null;

    @Test
    public void testSimpleAddAndRemove() {
	try {
	    FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
		    Files.createTempDirectory(null));

	    Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

	    fileBasedDatasetServices.addDataset(phenotypicDataPath, FileType.CSV, DatasetType.PHENOTYPIC);

	    List<SimpleEntity> datasetDescriptions = fileBasedDatasetServices.getDatasetDescriptions();

	    assertEquals("Number of dataset id is not 1", 1, datasetDescriptions.size());

	    SimpleEntity datasetId = datasetDescriptions.get(0);

	    assertNotNull("Dataset 1 id is null", datasetId);
	    assertNotNull("Dataset 1 name is null", datasetId.getName());
	    assertNotNull("Dataset 1 UniqueIdentifier is null", datasetId.getUniqueIdentifier());

	    Dataset dataset1 = fileBasedDatasetServices.getDataset(datasetId.getUniqueIdentifier());

	    assertTrue("Dataset is not correct type", dataset1 instanceof FeatureDataset);

	    assertEquals("Number of dataset 1 name is not correct", NAME1, dataset1.getName());
	    assertEquals("Number of dataset 1 UniqueIdentifier is not correct", UNIQUE_IDENTIFIER1,
		    dataset1.getUniqueIdentifier());
	    assertEquals("Number of dataset 1 Description is not correct", DESCRIPTION1 + phenotypicDataPath.toString(),
		    dataset1.getDescription());
	    assertEquals("Number of dataset 1 Abbreviation is not correct", ABBREVIATION1, dataset1.getAbbreviation());

	    List<Dataset> datasets = fileBasedDatasetServices.getAllDatasets();

	    assertEquals("Number of dataset is not 1", 1, datasets.size());

	    assertTrue("dataset 1 not in list of Datasets", datasets.contains(dataset1));

	    fileBasedDatasetServices.removeDataset(datasetId.getUniqueIdentifier());

	    datasetDescriptions = fileBasedDatasetServices.getDatasetDescriptions();

	    assertEquals("Number of dataset id is not 0", 0, datasetDescriptions.size());

	    datasets = fileBasedDatasetServices.getAllDatasets();

	    assertEquals("Number of dataset is not 0", 0, datasets.size());

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

	    Path path = fileBasedDatasetServices.getPath();

	    Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

	    fileBasedDatasetServices.addDataset(phenotypicDataPath, FileType.CSV, DatasetType.PHENOTYPIC);

	    fileBasedDatasetServices = new FileBasedDatasetServices(path);

	    List<SimpleEntity> datasetDescriptions = fileBasedDatasetServices.getDatasetDescriptions();

	    assertEquals("Number of dataset id is not 1", 1, datasetDescriptions.size());

	    SimpleEntity datasetId = datasetDescriptions.get(0);

	    assertNotNull("Dataset 1 id is null", datasetId);
	    assertNotNull("Dataset 1 name is null", datasetId.getName());
	    assertNotNull("Dataset 1 UniqueIdentifier is null", datasetId.getUniqueIdentifier());

	    Dataset dataset1 = fileBasedDatasetServices.getDataset(datasetId.getUniqueIdentifier());

	    assertTrue("Dataset is not correct type", dataset1 instanceof FeatureDataset);

	    assertEquals("Number of dataset 1 name is not correct", NAME1, dataset1.getName());
	    assertEquals("Number of dataset 1 UniqueIdentifier is not correct", UNIQUE_IDENTIFIER1,
		    dataset1.getUniqueIdentifier());
	    assertEquals("Number of dataset 1 Description is not correct", DESCRIPTION1 + phenotypicDataPath.toString(),
		    dataset1.getDescription());
	    assertEquals("Number of dataset 1 Abbreviation is not correct", ABBREVIATION1, dataset1.getAbbreviation());

	    List<Dataset> datasets = fileBasedDatasetServices.getAllDatasets();

	    assertEquals("Number of dataset is not 1", 1, datasets.size());

	    assertTrue("dataset 1 not in list of Datasets", datasets.contains(dataset1));
	} catch (Exception e) {
	    e.printStackTrace();

	    fail(e.getMessage());
	}
    }
}
