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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.corehunter.data.CoreHunterData;
import org.corehunter.data.CoreHunterDataType;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeData;
import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleGenotypeData;
import org.corehunter.services.simple.FileBasedDatasetServices;
import org.junit.Test;

import uno.informatics.data.Data;
import uno.informatics.data.Dataset;
import uno.informatics.data.Feature;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.dataset.FeatureDataRow;
import uno.informatics.data.feature.array.ArrayFeatureData;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.DatasetPojo;
import uno.informatics.data.pojo.OntologyTermPojo;
import uno.informatics.data.pojo.StudyPojo;

public class FileBasedDatasetServicesTest {

    private static final String PHENOTYPIC_FILE = "phenotypic_data.csv";
    private static final String DIPLOID_GENOTYPIC_FILE = "diploid_genotypic_data.csv";
    private static final String BIPARENTAL_GENOTYPIC_FILE = "biparental_genotypic_data.csv";
    private static final String FRQUENCY_GENOTYPIC_FILE = "frequency_genotypic_data.csv";
    private static final String HOMOZYGOUS_GENOTYPIC_FILE = "homozygous_genotypic_data.csv";
    private static final String DISTANCES_FILE = "distances_data.csv";

    private static final String DEFAULT_GENOTYPIC_FILE = FRQUENCY_GENOTYPIC_FILE;

    private static final String DEFAULT_ALT_GENOTYPIC_FILE = "frequency_alt_genotypic_data.txt";

    private static final String DATA_UID = "dataset";
    private static final String DATASET_NAME = "dataset";

    private static final String DATA_UID1 = "dataset1";
    private static final String DATASET_NAME1 = "dataset 1";

    private static final String DATA_UID2 = "dataset2";
    private static final String DATASET_NAME2 = "dataset 2";

    private static final String DATA_UID3 = "dataset3";
    private static final String DATASET_NAME3 = "dataset 3";

    private static final String DATA_UID4 = "dataset4";
    private static final String DATASET_NAME4 = "dataset 4";

    private static final String DATA_UID5 = "dataset5";
    private static final String DATASET_NAME5 = "dataset 5";

    private static final String TARGET_DIRECTORY = "target";
    private static final Path ROOT_DIRECTORY = Paths.get(TARGET_DIRECTORY,
            FileBasedDatasetServicesTest.class.getSimpleName());
    private static final double PRECISION = 0.0000000001;
    private static final int DATASET_SIZE = 1000;
    private static final String STUDY_NAME = "Study 1";
    private static final String ONTOLOGY_TERM = "term 1";
    private static final String DATASET_DESCRIPTION = "Description";
    private static final String DATASET_ABBREVIATION = "Abbreviation";

    @Test
    public void testAddDatasetNoData() {
        try {

            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset1 = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            fileBasedDatasetServices.addDataset(dataset1);

            Dataset addedDataset1 = fileBasedDatasetServices.getDataset(dataset1.getUniqueIdentifier());

            assertEquals("Added Dataset name is not correct", dataset1.getName(), addedDataset1.getName());
            assertEquals("Added Dataset unique identifier is not correct", dataset1.getUniqueIdentifier(),
                    addedDataset1.getUniqueIdentifier());
            assertEquals("Added Dataset abbreviation is not correct", dataset1.getAbbreviation(),
                    addedDataset1.getAbbreviation());
            assertEquals("Added Dataset description is not correct", dataset1.getDescription(),
                    addedDataset1.getDescription());
            assertEquals("Added Dataset type is not correct", dataset1.getType(), addedDataset1.getType());
            assertEquals("Added Dataset study is not correct", dataset1.getStudy(), addedDataset1.getStudy());
            assertEquals("Added Dataset size is not correct", 0, addedDataset1.getSize());

            List<Dataset> datasets = fileBasedDatasetServices.getAllDatasets();

            assertEquals("Number of datasets is not 1", 1, datasets.size());

            Dataset addedDatasetInList = datasets.get(0);

            assertEquals("Added Dataset In List is not correct", dataset1.getName(), addedDatasetInList.getName());
            assertEquals("Added Dataset In List  unique identifier is not correct", dataset1.getUniqueIdentifier(),
                    addedDatasetInList.getUniqueIdentifier());
            assertEquals("Added Dataset In List abbreviation is not correct", dataset1.getAbbreviation(),
                    addedDatasetInList.getAbbreviation());
            assertEquals("Added Dataset In List description is not correct", dataset1.getDescription(),
                    addedDatasetInList.getDescription());
            assertEquals("Added Dataset type is not correct", dataset1.getType(), addedDatasetInList.getType());
            assertEquals("Added Dataset study is not correct", dataset1.getStudy(), addedDatasetInList.getStudy());
            assertEquals("Added Dataset size is not correct", 0, addedDatasetInList.getSize());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset1.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNull("Genotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset1.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset1.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            // dataset with description

            DatasetPojo dataset2 = new DatasetPojo(DATA_UID2, DATASET_NAME2);

            dataset2.setDescription(DATASET_DESCRIPTION);

            fileBasedDatasetServices.addDataset(dataset2);

            Dataset addedDataset2 = fileBasedDatasetServices.getDataset(dataset2.getUniqueIdentifier());

            assertEquals("Added Dataset name is not correct", dataset2.getName(), addedDataset2.getName());
            assertEquals("Added Dataset unique identifier is not correct", dataset2.getUniqueIdentifier(),
                    addedDataset2.getUniqueIdentifier());
            assertEquals("Added Dataset abbreviation is not correct", dataset2.getAbbreviation(),
                    addedDataset2.getAbbreviation());
            assertEquals("Added Dataset description is not correct", dataset2.getDescription(),
                    addedDataset2.getDescription());
            assertEquals("Added Dataset type is not correct", dataset2.getType(), addedDataset2.getType());
            assertEquals("Added Dataset study is not correct", dataset2.getStudy(), addedDataset2.getStudy());
            assertEquals("Added Dataset size is not correct", 0, addedDataset2.getSize());

            // dataset with abbreviation

            DatasetPojo dataset3 = new DatasetPojo(DATA_UID3, DATASET_NAME3);

            dataset3.setAbbreviation(DATASET_ABBREVIATION);

            fileBasedDatasetServices.addDataset(dataset3);

            Dataset addedDataset3 = fileBasedDatasetServices.getDataset(dataset3.getUniqueIdentifier());

            assertEquals("Added Dataset name is not correct", dataset3.getName(), addedDataset3.getName());
            assertEquals("Added Dataset unique identifier is not correct", dataset3.getUniqueIdentifier(),
                    addedDataset3.getUniqueIdentifier());
            assertEquals("Added Dataset abbreviation is not correct", dataset3.getAbbreviation(),
                    addedDataset3.getAbbreviation());
            assertEquals("Added Dataset description is not correct", dataset3.getDescription(),
                    addedDataset3.getDescription());
            assertEquals("Added Dataset type is not correct", dataset3.getType(), addedDataset3.getType());
            assertEquals("Added Dataset study is not correct", dataset3.getStudy(), addedDataset3.getStudy());
            assertEquals("Added Dataset size is not correct", 0, addedDataset3.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    private Path createTempDirectory() throws IOException {

        Files.createDirectories(ROOT_DIRECTORY);

        Path path = Files.createTempDirectory(ROOT_DIRECTORY, null);

        Files.createDirectories(path);

        return path;
    }

    @Test
    public void testRemoveDatasetNoData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

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
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Path path = fileBasedDatasetServices.getPath();

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            assertEquals("Restored Dataset name is not correct", dataset.getName(), restoredDataset.getName());
            assertEquals("Restored Dataset unique identifier is not correct", dataset.getUniqueIdentifier(),
                    restoredDataset.getUniqueIdentifier());
            assertEquals("Restored Dataset abbreviation is not correct", dataset.getAbbreviation(),
                    restoredDataset.getAbbreviation());
            assertEquals("Restored Dataset description is not correct", dataset.getDescription(),
                    restoredDataset.getDescription());
            assertEquals("Restored Dataset type is not correct", dataset.getType(), restoredDataset.getType());
            assertEquals("Restored Dataset study is not correct", dataset.getStudy(), restoredDataset.getStudy());
            assertEquals("Restored Dataset size is not correct", 0, restoredDataset.getSize());

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

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNull("Genotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithPhenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.PHENOTYPIC);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Phenotypic Data not found", addedData.getPhenotypicData());

            ArrayFeatureData data = ArrayFeatureData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareFeatureData(data, addedData.getPhenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNull("Genotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNotNull("Phenotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithPhenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.PHENOTYPIC);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Phenotypic Data not found", restoredData.getPhenotypicData());

            ArrayFeatureData data = ArrayFeatureData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareFeatureData(data, restoredData.getPhenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNull("Genotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNotNull("Phenotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithDiploidGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(DIPLOID_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.DEFAULT);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithBiparentalGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(BIPARENTAL_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.BIPARENTAL);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            SimpleBiAllelicGenotypeData data = (SimpleBiAllelicGenotypeData) SimpleBiAllelicGenotypeData
                    .readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithHomozygousGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(HOMOZYGOUS_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.DEFAULT);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_ALT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath, FileType.TXT);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithFrequencyGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.FREQUENCY);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithDiploidGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(DIPLOID_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.DEFAULT);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithBiparentalGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(BIPARENTAL_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.BIPARENTAL);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            SimpleBiAllelicGenotypeData data = (SimpleBiAllelicGenotypeData) SimpleBiAllelicGenotypeData
                    .readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithHomozygousGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(HOMOZYGOUS_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.DEFAULT);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_ALT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath, FileType.TXT);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithFrequencyGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.FREQUENCY);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithDistanceData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(DISTANCES_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.DISTANCES);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Phenotypic Data not found", addedData.getDistancesData());

            SimpleDistanceMatrixData data = SimpleDistanceMatrixData.readData(dataPath, FileType.CSV);

            // data.setUniqueIdentifier(DATA_UID);
            // data.setName(DATASET_NAME);

            compareDistanceMatrixData(data, addedData.getDistancesData());

            assertNotNull("Distances data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNull("Genotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithDistanceData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(DISTANCES_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.DISTANCES);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Distances Data not found", restoredData.getDistancesData());

            SimpleDistanceMatrixData data = SimpleDistanceMatrixData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareDistanceMatrixData(data, restoredData.getDistancesData());

            assertNotNull("Distances data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNull("Genotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithPhenotypicAndGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV,
                    CoreHunterDataType.PHENOTYPIC);

            Path genotypicDataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, genotypicDataPath, FileType.CSV,
                    CoreHunterDataType.GENOTYPIC);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Phenotypic Data not found", addedData.getPhenotypicData());

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            ArrayFeatureData phenotypicData = ArrayFeatureData.readData(phenotypicDataPath, FileType.CSV);

            phenotypicData.setUniqueIdentifier(DATA_UID);
            phenotypicData.setName(DATASET_NAME);

            compareFeatureData(phenotypicData, addedData.getPhenotypicData());

            SimpleGenotypeData genotypicData = (SimpleGenotypeData) SimpleGenotypeData.readData(genotypicDataPath,
                    FileType.CSV);

            genotypicData.setUniqueIdentifier(DATA_UID);
            genotypicData.setName(DATASET_NAME);

            compareGenotypeVariantData(genotypicData, addedData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNotNull("Phenotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithPhenotypicAndGenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV,
                    CoreHunterDataType.PHENOTYPIC);

            Path genotypicDataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, genotypicDataPath, FileType.CSV,
                    CoreHunterDataType.GENOTYPIC);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored Data not found", restoredData);

            assertNotNull("Restored Phenotypic Data not found", restoredData.getPhenotypicData());

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            ArrayFeatureData phenotypicData = ArrayFeatureData.readData(phenotypicDataPath, FileType.CSV);

            phenotypicData.setUniqueIdentifier(DATA_UID);
            phenotypicData.setName(DATASET_NAME);

            compareFeatureData(phenotypicData, restoredData.getPhenotypicData());

            SimpleGenotypeData genotypicData = (SimpleGenotypeData) SimpleGenotypeData.readData(genotypicDataPath,
                    FileType.CSV);

            genotypicData.setUniqueIdentifier(DATA_UID);
            genotypicData.setName(DATASET_NAME);

            compareGenotypeVariantData(genotypicData, restoredData.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNotNull("Phenotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddDatasetWithPhenotypiGenotypicAndDistanceData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV,
                    CoreHunterDataType.PHENOTYPIC);

            Path genotypicDataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, genotypicDataPath, FileType.CSV,
                    CoreHunterDataType.GENOTYPIC);

            Path dataPath = Paths.get(ClassLoader.getSystemResource(DISTANCES_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.DISTANCES);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Phenotypic Data not found", addedData.getPhenotypicData());

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            assertNotNull("Distances Data not found", addedData.getDistancesData());

            ArrayFeatureData phenotypicData = ArrayFeatureData.readData(phenotypicDataPath, FileType.CSV);

            phenotypicData.setUniqueIdentifier(DATA_UID);
            phenotypicData.setName(DATASET_NAME);

            compareFeatureData(phenotypicData, addedData.getPhenotypicData());

            SimpleGenotypeData genotypicData = (SimpleGenotypeData) SimpleGenotypeData.readData(genotypicDataPath,
                    FileType.CSV);

            genotypicData.setUniqueIdentifier(DATA_UID);
            genotypicData.setName(DATASET_NAME);

            compareGenotypeVariantData(genotypicData, addedData.getGenotypicData());

            SimpleDistanceMatrixData data = SimpleDistanceMatrixData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareDistanceMatrixData(data, addedData.getDistancesData());

            assertNotNull("Distances data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNotNull("Phenotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, addedDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreDatasetWithPhenotypicGenotypicAndDistanceData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV,
                    CoreHunterDataType.PHENOTYPIC);

            Path genotypicDataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, genotypicDataPath, FileType.CSV,
                    CoreHunterDataType.GENOTYPIC);

            Path dataPath = Paths.get(ClassLoader.getSystemResource(DISTANCES_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, CoreHunterDataType.DISTANCES);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset);

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored Data not found", restoredData);

            assertNotNull("Restored Phenotypic Data not found", restoredData.getPhenotypicData());

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            assertNotNull("Restored Distances Data not found", restoredData.getDistancesData());

            ArrayFeatureData phenotypicData = ArrayFeatureData.readData(phenotypicDataPath, FileType.CSV);

            phenotypicData.setUniqueIdentifier(DATA_UID);
            phenotypicData.setName(DATASET_NAME);

            compareFeatureData(phenotypicData, restoredData.getPhenotypicData());

            SimpleGenotypeData genotypicData = (SimpleGenotypeData) SimpleGenotypeData.readData(genotypicDataPath,
                    FileType.CSV);

            genotypicData.setUniqueIdentifier(DATA_UID);
            genotypicData.setName(DATASET_NAME);

            compareGenotypeVariantData(genotypicData, restoredData.getGenotypicData());

            SimpleDistanceMatrixData data = SimpleDistanceMatrixData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareDistanceMatrixData(data, restoredData.getDistancesData());

            assertNotNull("Distances data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNotNull("Phenotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testRestoreFourDatasetsWithGenotypicData() {
        try {
            // create service
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            // create and load dataset 1

            Dataset dataset1 = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            fileBasedDatasetServices.addDataset(dataset1);

            Dataset addedDataset1 = fileBasedDatasetServices.getDataset(dataset1.getUniqueIdentifier());

            Path dataPath1 = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset1, dataPath1, FileType.CSV, CoreHunterDataType.GENOTYPIC);

            // create and load dataset 2

            Dataset dataset2 = new DatasetPojo(DATA_UID2, DATASET_NAME2);

            fileBasedDatasetServices.addDataset(dataset2);

            Dataset addedDataset2 = fileBasedDatasetServices.getDataset(dataset2.getUniqueIdentifier());

            Path dataPath2 = Paths.get(ClassLoader.getSystemResource(DIPLOID_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset2, dataPath2, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.DEFAULT);

            // create and load dataset 3

            Dataset dataset3 = new DatasetPojo(DATA_UID3, DATASET_NAME3);

            fileBasedDatasetServices.addDataset(dataset3);

            Dataset addedDataset3 = fileBasedDatasetServices.getDataset(dataset3.getUniqueIdentifier());

            Path dataPath3 = Paths.get(ClassLoader.getSystemResource(BIPARENTAL_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset3, dataPath3, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.BIPARENTAL);

            // create and load dataset 4

            Dataset dataset4 = new DatasetPojo(DATA_UID4, DATASET_NAME4);

            fileBasedDatasetServices.addDataset(dataset4);

            Dataset addedDataset4 = fileBasedDatasetServices.getDataset(dataset4.getUniqueIdentifier());

            Path dataPath4 = Paths.get(ClassLoader.getSystemResource(HOMOZYGOUS_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset4, dataPath4, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.DEFAULT);

            // create and load dataset 5

            Dataset dataset5 = new DatasetPojo(DATA_UID5, DATASET_NAME5);

            fileBasedDatasetServices.addDataset(dataset5);

            Dataset addedDataset5 = fileBasedDatasetServices.getDataset(dataset5.getUniqueIdentifier());

            Path dataPath5 = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset5, dataPath5, FileType.CSV, CoreHunterDataType.GENOTYPIC,
                    GenotypeDataFormat.FREQUENCY);

            // Restore
            // create the services

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            // Restore and check dataset 1

            Dataset restoredDataset1 = fileBasedDatasetServices.getDataset(dataset1.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset1);

            CoreHunterData restoredData1 = fileBasedDatasetServices
                    .getCoreHunterData(restoredDataset1.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData1);

            assertNotNull("Restored Genotypic Data not found", restoredData1.getGenotypicData());

            dataPath1 = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data1 = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath1, FileType.CSV);

            data1.setUniqueIdentifier(DATA_UID);
            data1.setName(DATASET_NAME);

            compareGenotypeVariantData(data1, restoredData1.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset1.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset1.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset1.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset1.getSize());

            // Restore and check dataset 2

            Dataset restoredDataset2 = fileBasedDatasetServices.getDataset(dataset2.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset2);

            CoreHunterData restoredData2 = fileBasedDatasetServices
                    .getCoreHunterData(restoredDataset2.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData2);

            assertNotNull("Restored Genotypic Data not found", restoredData2.getGenotypicData());

            dataPath2 = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data2 = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath2, FileType.CSV);

            data2.setUniqueIdentifier(DATA_UID);
            data2.setName(DATASET_NAME);

            compareGenotypeVariantData(data2, restoredData2.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset2.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset2.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset2.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset2.getSize());

            // Restore and check dataset 3

            Dataset restoredDataset3 = fileBasedDatasetServices.getDataset(dataset3.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset3);

            CoreHunterData restoredData3 = fileBasedDatasetServices
                    .getCoreHunterData(restoredDataset3.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData3);

            assertNotNull("Restored Genotypic Data not found", restoredData3.getGenotypicData());

            SimpleBiAllelicGenotypeData data3 = (SimpleBiAllelicGenotypeData) SimpleBiAllelicGenotypeData
                    .readData(dataPath3, FileType.CSV);

            data3.setUniqueIdentifier(DATA_UID);
            data3.setName(DATASET_NAME);

            compareGenotypeVariantData(data3, restoredData3.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset3.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset3.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset3.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset3.getSize());

            // Restore and check dataset 4

            Dataset restoredDataset4 = fileBasedDatasetServices.getDataset(dataset4.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset4);

            CoreHunterData restoredData4 = fileBasedDatasetServices
                    .getCoreHunterData(restoredDataset4.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData4);

            assertNotNull("Restored Genotypic Data not found", restoredData4.getGenotypicData());

            dataPath4 = Paths.get(ClassLoader.getSystemResource(DEFAULT_ALT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data4 = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath4, FileType.TXT);

            data4.setUniqueIdentifier(DATA_UID);
            data4.setName(DATASET_NAME);

            compareGenotypeVariantData(data4, restoredData4.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset4.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset4.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset4.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset4.getSize());

            // Restore and check dataset 5

            Dataset restoredDataset5 = fileBasedDatasetServices.getDataset(dataset5.getUniqueIdentifier());

            assertNotNull("Restored dataset not found", restoredDataset5);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(restoredDataset5.getUniqueIdentifier()));

            CoreHunterData restoredData5 = fileBasedDatasetServices
                    .getCoreHunterData(restoredDataset5.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData5);

            assertNotNull("Restored Genotypic Data not found", restoredData5.getGenotypicData());

            dataPath5 = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data5 = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath5, FileType.CSV);

            data5.setUniqueIdentifier(DATA_UID);
            data5.setName(DATASET_NAME);

            compareGenotypeVariantData(data5, restoredData5.getGenotypicData());

            assertNull("Distances data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset5.getUniqueIdentifier(), CoreHunterDataType.DISTANCES));
            assertNotNull("Genotypic data should be not null", fileBasedDatasetServices
                    .getOriginalData(dataset5.getUniqueIdentifier(), CoreHunterDataType.GENOTYPIC));
            assertNull("Phenotypic data should be null", fileBasedDatasetServices
                    .getOriginalData(dataset5.getUniqueIdentifier(), CoreHunterDataType.PHENOTYPIC));

            assertEquals("Dataset size is not correct", DATASET_SIZE, restoredDataset5.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testAddInvalidDatasets() {

        FileBasedDatasetServices fileBasedDatasetServices = null;

        try {
            // create service
            fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }

        try {
            DatasetPojo dataset1 = new DatasetPojo(DATA_UID1, null);

            fileBasedDatasetServices.addDataset(dataset1);

            fail("Dataset with no name, should not have worked");

        } catch (Exception e) {
            ;
        }

        try {
            DatasetPojo dataset1 = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            dataset1.setStudy(new StudyPojo(STUDY_NAME));

            fileBasedDatasetServices.addDataset(dataset1);

            fail("Dataset with study, should not have worked");

        } catch (Exception e) {
            ;
        }

        try {
            DatasetPojo dataset1 = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            dataset1.setType(new OntologyTermPojo(ONTOLOGY_TERM));

            fileBasedDatasetServices.addDataset(dataset1);

            fail("Dataset with ontology term, should not have worked");

        } catch (Exception e) {
            ;
        }

        try {
            DatasetPojo dataset1 = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            dataset1.setSize(1);

            fileBasedDatasetServices.addDataset(dataset1);

            fail("Dataset not zero size, should not have worked");

        } catch (Exception e) {
            ;
        }
    }

    @Test
    public void testUpdateDataset() {

        FileBasedDatasetServices fileBasedDatasetServices = null;

        try {
            // create service
            fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Path path = fileBasedDatasetServices.getPath();

            DatasetPojo dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            // add dataset
            fileBasedDatasetServices.addDataset(dataset);

            // test name update

            DatasetPojo datasetToBeUpdated1 = new DatasetPojo(DATA_UID, DATASET_NAME2);

            boolean updated1 = fileBasedDatasetServices.updateDataset(datasetToBeUpdated1);

            assertTrue(updated1);

            Dataset updatedDataset1 = fileBasedDatasetServices.getDataset(datasetToBeUpdated1.getUniqueIdentifier());

            assertEquals("Updated Dataset name is not correct", datasetToBeUpdated1.getName(),
                    updatedDataset1.getName());
            assertEquals("Updated Dataset unique identifier is not correct", datasetToBeUpdated1.getUniqueIdentifier(),
                    updatedDataset1.getUniqueIdentifier());
            assertEquals("Updated Dataset abbreviation is not correct", datasetToBeUpdated1.getAbbreviation(),
                    updatedDataset1.getAbbreviation());
            assertEquals("Updated Dataset description is not correct", datasetToBeUpdated1.getDescription(),
                    updatedDataset1.getDescription());
            assertEquals("Updated Dataset type is not correct", datasetToBeUpdated1.getType(),
                    updatedDataset1.getType());
            assertEquals("Updated Dataset study is not correct", datasetToBeUpdated1.getStudy(),
                    updatedDataset1.getStudy());
            assertEquals("Updated Dataset size is not correct", datasetToBeUpdated1.getSize(),
                    updatedDataset1.getSize());

            // test description update

            DatasetPojo datasetToBeUpdated2 = new DatasetPojo(DATA_UID, DATASET_NAME);

            datasetToBeUpdated2.setDescription(DATASET_DESCRIPTION);

            boolean updated2 = fileBasedDatasetServices.updateDataset(datasetToBeUpdated2);

            assertTrue(updated2);

            Dataset updatedDataset2 = fileBasedDatasetServices.getDataset(datasetToBeUpdated1.getUniqueIdentifier());

            assertEquals("Updated Dataset name is not correct", datasetToBeUpdated2.getName(),
                    updatedDataset2.getName());
            assertEquals("Updated Dataset unique identifier is not correct", datasetToBeUpdated2.getUniqueIdentifier(),
                    updatedDataset2.getUniqueIdentifier());
            assertEquals("Updated Dataset abbreviation is not correct", datasetToBeUpdated2.getAbbreviation(),
                    updatedDataset2.getAbbreviation());
            assertEquals("Updated Dataset description is not correct", datasetToBeUpdated2.getDescription(),
                    updatedDataset2.getDescription());
            assertEquals("Updated Dataset type is not correct", datasetToBeUpdated2.getType(),
                    updatedDataset2.getType());
            assertEquals("Updated Dataset study is not correct", datasetToBeUpdated2.getStudy(),
                    updatedDataset2.getStudy());
            assertEquals("Updated Dataset size is not correct", datasetToBeUpdated2.getSize(),
                    updatedDataset2.getSize());

            // test abbreviation update

            DatasetPojo datasetToBeUpdated3 = new DatasetPojo(DATA_UID, DATASET_NAME);

            datasetToBeUpdated3.setAbbreviation(DATASET_ABBREVIATION);

            boolean updated3 = fileBasedDatasetServices.updateDataset(datasetToBeUpdated3);

            assertTrue(updated3);

            Dataset updatedDataset3 = fileBasedDatasetServices.getDataset(datasetToBeUpdated1.getUniqueIdentifier());

            assertEquals("Updated Dataset name is not correct", datasetToBeUpdated3.getName(),
                    updatedDataset3.getName());
            assertEquals("Updated Dataset unique identifier is not correct", datasetToBeUpdated3.getUniqueIdentifier(),
                    updatedDataset3.getUniqueIdentifier());
            assertEquals("Updated Dataset abbreviation is not correct", datasetToBeUpdated3.getAbbreviation(),
                    updatedDataset3.getAbbreviation());
            assertEquals("Updated Dataset description is not correct", datasetToBeUpdated3.getDescription(),
                    updatedDataset3.getDescription());
            assertEquals("Updated Dataset type is not correct", datasetToBeUpdated3.getType(),
                    updatedDataset3.getType());
            assertEquals("Updated Dataset study is not correct", datasetToBeUpdated3.getStudy(),
                    updatedDataset3.getStudy());
            assertEquals("Updated Dataset size is not correct", datasetToBeUpdated3.getSize(),
                    updatedDataset3.getSize());

            // test all update and restore

            DatasetPojo datasetToBeUpdated4 = new DatasetPojo(DATA_UID, DATASET_NAME2);

            datasetToBeUpdated4.setDescription(DATASET_DESCRIPTION);
            datasetToBeUpdated4.setAbbreviation(DATASET_ABBREVIATION);

            boolean updated4 = fileBasedDatasetServices.updateDataset(datasetToBeUpdated4);

            assertTrue(updated4);

            Dataset updatedDataset4 = fileBasedDatasetServices.getDataset(datasetToBeUpdated1.getUniqueIdentifier());

            assertEquals("Updated Dataset name is not correct", datasetToBeUpdated4.getName(),
                    updatedDataset4.getName());
            assertEquals("Updated Dataset unique identifier is not correct", datasetToBeUpdated4.getUniqueIdentifier(),
                    updatedDataset4.getUniqueIdentifier());
            assertEquals("Updated Dataset abbreviation is not correct", datasetToBeUpdated4.getAbbreviation(),
                    updatedDataset4.getAbbreviation());
            assertEquals("Updated Dataset description is not correct", datasetToBeUpdated4.getDescription(),
                    updatedDataset4.getDescription());
            assertEquals("Updated Dataset type is not correct", datasetToBeUpdated4.getType(),
                    updatedDataset4.getType());
            assertEquals("Updated Dataset study is not correct", datasetToBeUpdated4.getStudy(),
                    updatedDataset4.getStudy());
            assertEquals("Updated Dataset size is not correct", datasetToBeUpdated4.getSize(),
                    updatedDataset4.getSize());

            // check to see if changes are persisted

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            Dataset restoredDataset = fileBasedDatasetServices.getDataset(datasetToBeUpdated1.getUniqueIdentifier());

            assertEquals("Updated Dataset name is not correct", datasetToBeUpdated4.getName(),
                    restoredDataset.getName());
            assertEquals("Updated Dataset unique identifier is not correct", restoredDataset.getUniqueIdentifier(),
                    restoredDataset.getUniqueIdentifier());
            assertEquals("Updated Dataset abbreviation is not correct", datasetToBeUpdated4.getAbbreviation(),
                    restoredDataset.getAbbreviation());
            assertEquals("Updated Dataset description is not correct", datasetToBeUpdated4.getDescription(),
                    restoredDataset.getDescription());
            assertEquals("Updated Dataset type is not correct", datasetToBeUpdated4.getType(),
                    restoredDataset.getType());
            assertEquals("Updated Dataset study is not correct", datasetToBeUpdated4.getStudy(),
                    restoredDataset.getStudy());
            assertEquals("Updated Dataset size is not correct", datasetToBeUpdated4.getSize(),
                    restoredDataset.getSize());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateInvalidDatasets() {

        FileBasedDatasetServices fileBasedDatasetServices = null;

        try {
            // create service
            fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            DatasetPojo dataset = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            // add dataset
            fileBasedDatasetServices.addDataset(dataset);

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }

        try {
            DatasetPojo datasetToBeUpdated = new DatasetPojo(DATA_UID1, null);

            fileBasedDatasetServices.updateDataset(datasetToBeUpdated);

            fail("Updated Dataset with no name, should not have worked");

        } catch (Exception e) {
            ;
        }

        try {
            DatasetPojo datasetToBeUpdated = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            datasetToBeUpdated.setStudy(new StudyPojo(STUDY_NAME));

            fileBasedDatasetServices.updateDataset(datasetToBeUpdated);

            fail("Updated Dataset with study, should not have worked");

        } catch (Exception e) {
            ;
        }

        try {
            DatasetPojo datasetToBeUpdated = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            datasetToBeUpdated.setType(new OntologyTermPojo(ONTOLOGY_TERM));

            fileBasedDatasetServices.updateDataset(datasetToBeUpdated);

            fail("Updated Dataset with ontology term, should not have worked");

        } catch (Exception e) {
            ;
        }

        try {
            DatasetPojo datasetToBeUpdated = new DatasetPojo(DATA_UID1, DATASET_NAME1);

            datasetToBeUpdated.setSize(1);

            fileBasedDatasetServices.updateDataset(datasetToBeUpdated);

            fail("Updated Dataset not zero size, should not have worked");

        } catch (Exception e) {
            ;
        }
    }

    private void compareData(Data expected, Data actual) {
        // assertEquals("Unique Identifier not correct",
        // expected.getUniqueIdentifier(), actual.getUniqueIdentifier());
        // assertEquals("Name not correct", expected.getName(),
        // actual.getName());

        assertEquals("Dataset size is not the name", expected.getSize(), actual.getSize());

        assertEquals("Ids are not the name", expected.getIDs(), actual.getIDs());

        assertEquals("Dataset is not the name", expected.getDataset(), actual.getDataset());
    }

    private void compareGenotypeVariantData(GenotypeData expected, GenotypeData actual) {

        compareData(expected, actual);

        assertEquals("Total Number Of Alleles is not the same", expected.getTotalNumberOfAlleles(),
                actual.getTotalNumberOfAlleles());

        assertEquals("Number Of Markers is not the same", expected.getNumberOfMarkers(), actual.getNumberOfMarkers());

        int numberOfMarkers = expected.getNumberOfMarkers();

        int size = expected.getSize();

        Double expectedFrequency;
        Double actualFrequency;

        for (int markerIndex = 0; markerIndex < numberOfMarkers; ++markerIndex) {
            assertEquals("Name for marker : " + markerIndex + " is not the same", expected.getMarkerName(markerIndex),
                    actual.getMarkerName(markerIndex));

            assertEquals("Number Of Alleles for marker : " + markerIndex + " is not the same",
                    expected.getNumberOfAlleles(markerIndex), actual.getNumberOfAlleles(markerIndex));

            int numberOfAlleles = expected.getNumberOfAlleles(markerIndex);

            for (int alleleIndex = 0; alleleIndex < numberOfAlleles; ++alleleIndex) {
                assertEquals("Name for allele : " + alleleIndex + "  for marker : " + markerIndex + " is not the same",
                        expected.getAlleleName(markerIndex, alleleIndex),
                        actual.getAlleleName(markerIndex, alleleIndex));

                for (int index = 0; index < size; ++index) {

                    expectedFrequency = expected.getAlleleFrequency(index, markerIndex, alleleIndex);
                    actualFrequency = actual.getAlleleFrequency(index, markerIndex, alleleIndex);

                    if (expectedFrequency != null) {
                        assertNotNull("Expecting frequency to be non-null: ", actualFrequency);

                        assertEquals(
                                "Frequency for allele : " + alleleIndex + "  for marker : " + markerIndex
                                        + " in entry : " + index + " is not the same",
                                expectedFrequency, actualFrequency, PRECISION);
                    } else {
                        assertNull("Expecting frequency to be null: ", actualFrequency);

                    }
                }
            }

        }
    }

    private void compareFeatureData(FeatureData expected, FeatureData actual) {
        compareData(expected, actual);

        assertEquals("Feature count is not the name", expected.getFeatures().size(), actual.getFeatures().size());

        Iterator<Feature> expectedFeatureIterator = expected.getFeatures().iterator();
        Iterator<Feature> actualFeatureIterator = actual.getFeatures().iterator();

        Feature expectedFeature;
        Feature actualFeature;

        int i = 0;

        while (expectedFeatureIterator.hasNext()) {
            expectedFeature = expectedFeatureIterator.next();
            actualFeature = actualFeatureIterator.next();

            assertEquals("Feature : " + i + " id is not the same ", expectedFeature.getUniqueIdentifier(),
                    actualFeature.getUniqueIdentifier());

            assertEquals("Feature : " + i + " name is not the same ", expectedFeature.getName(),
                    actualFeature.getName());

            assertEquals("Feature : " + i + " description is not the same ", expectedFeature.getDescription(),
                    actualFeature.getDescription());

            assertEquals("Feature : " + i + " abbreviation is not the same ", expectedFeature.getAbbreviation(),
                    actualFeature.getAbbreviation());

            assertEquals("Feature : " + i + " method id is not the same ",
                    expectedFeature.getMethod().getUniqueIdentifier(), actualFeature.getMethod().getUniqueIdentifier());

            assertEquals("Feature : " + i + " method name is not the same ", expectedFeature.getMethod().getName(),
                    actualFeature.getMethod().getName());

            assertEquals("Feature : " + i + " method description is not the same ",
                    expectedFeature.getMethod().getDescription(), actualFeature.getMethod().getDescription());

            assertEquals("Feature : " + i + " method abbreviation is not the same ",
                    expectedFeature.getMethod().getAbbreviation(), actualFeature.getMethod().getAbbreviation());

            assertEquals("Feature : " + i + " scale id is not the same ",
                    expectedFeature.getMethod().getScale().getUniqueIdentifier(),
                    actualFeature.getMethod().getScale().getUniqueIdentifier());

            assertEquals("Feature : " + i + " scale name is not the same ",
                    expectedFeature.getMethod().getScale().getName(), actualFeature.getMethod().getScale().getName());

            assertEquals("Feature : " + i + " scale description is not the same ",
                    expectedFeature.getMethod().getScale().getDescription(),
                    actualFeature.getMethod().getScale().getDescription());

            assertEquals("Feature : " + i + " scale abbreviation is not the same ",
                    expectedFeature.getMethod().getScale().getAbbreviation(),
                    actualFeature.getMethod().getScale().getAbbreviation());

            assertEquals("Feature : " + i + " scale data type is not the same ",
                    expectedFeature.getMethod().getScale().getDataType(),
                    actualFeature.getMethod().getScale().getDataType());

            assertEquals("Feature : " + i + " scale name is not the same ",
                    expectedFeature.getMethod().getScale().getScaleType(),
                    actualFeature.getMethod().getScale().getScaleType());

            ++i;
        }

        assertEquals("Row count is not the name", expected.getRowCount(), actual.getRowCount());

        int size = expected.getRowCount();
        int featureCount = expected.getFeatures().size();

        FeatureDataRow expectedRow;
        FeatureDataRow actualRow;

        for (int index = 0; index < size; ++index) {

            expectedRow = expected.getRow(index);
            actualRow = actual.getRow(index);

            assertEquals("Row : " + index + " size not the same", expectedRow.getColumnCount(),
                    actualRow.getColumnCount());

            assertEquals("Row : " + index + " header name is not the same", expectedRow.getHeader().getName(),
                    actualRow.getHeader().getName());

            assertEquals("Row : " + index + " header id is not the same", expectedRow.getHeader().getName(),
                    actualRow.getHeader().getName());

            for (int columnIndex = 0; columnIndex < featureCount; ++columnIndex) {

                assertEquals("Row : " + index + " value : " + columnIndex + " is not the same",
                        actualRow.getValue(columnIndex), expectedRow.getValue(columnIndex));
            }

        }
    }

    private void compareDistanceMatrixData(DistanceMatrixData expected, DistanceMatrixData actual) {
        compareData(expected, actual);

        int size = expected.getSize();

        for (int x = 0; x < size; ++x) {

            for (int y = 0; y < size; ++y) {

                assertEquals("Value at position : " + x + "," + y + " header id is not the same",
                        expected.getDistance(x, y), expected.getDistance(x, y), PRECISION);

            }

        }
    }
}
