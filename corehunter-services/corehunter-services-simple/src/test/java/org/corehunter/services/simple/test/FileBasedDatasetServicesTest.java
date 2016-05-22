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
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeData;
import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleGenotypeData;
import org.corehunter.services.DataType;
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

public class FileBasedDatasetServicesTest {

    private static final String PHENOTYPIC_FILE = "phenotypic_data.csv";
    private static final String DIPLOID_GENOTYPIC_FILE = "diploid_genotypic_data.csv";
    private static final String BIPARENTAL_GENOTYPIC_FILE = "biparental_genotypic_data.csv";
    private static final String FRQUENCY_GENOTYPIC_FILE = "frequency_genotypic_data.csv";
    private static final String HOMOZYGOUS_GENOTYPIC_FILE = "homozygous_genotypic_data.csv";
    private static final String DISTANCES_FILE = "distances_data.csv";
    
    private static final String DEFAULT_GENOTYPIC_FILE = FRQUENCY_GENOTYPIC_FILE ;

    private static final String DATA_UID = "dataset1";
    private static final String DATASET_NAME = "dataset 1";
    private static final Path ROOT_DIRECTORY = Paths.get("target", "datasetServicesTests");
    private static final double PRECISION = 0.0000000001;

    @Test
    public void testAddDatasetNoData() {
        try {

            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

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

    @Test
    public void testAddDatasetWithPhenotypicData() {
        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            Dataset dataset = new DatasetPojo(DATA_UID, DATASET_NAME);

            fileBasedDatasetServices.addDataset(dataset);

            Dataset addedDataset = fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier());

            Path dataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.PHENOTYPIC);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Phenotypic Data not found", addedData.getPhenotypicData());

            ArrayFeatureData data = ArrayFeatureData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareFeatureData(data, addedData.getPhenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.PHENOTYPIC);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Phenotypic Data not found", restoredData.getPhenotypicData());

            ArrayFeatureData data = ArrayFeatureData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareFeatureData(data, restoredData.getPhenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath,
                    FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC, 
                    GenotypeDataFormat.DEFAULT);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());
            
            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath,
                    FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC, 
                    GenotypeDataFormat.BIPARENTAL);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            SimpleBiAllelicGenotypeData data = (SimpleBiAllelicGenotypeData) 
                    SimpleBiAllelicGenotypeData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC, 
                    GenotypeDataFormat.DEFAULT);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());
            
            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath,
                    FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC, 
                    GenotypeDataFormat.FREQUENCY);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());
            
            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath,
                    FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, addedData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());
            
            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath,
                    FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC, 
                    GenotypeDataFormat.DEFAULT);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());
            
            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath,
                    FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC, 
                    GenotypeDataFormat.BIPARENTAL);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            SimpleBiAllelicGenotypeData data = (SimpleBiAllelicGenotypeData) 
                    SimpleBiAllelicGenotypeData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC, 
                    GenotypeDataFormat.DEFAULT);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());
            
            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath,
                    FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.GENOTYPIC, 
                    GenotypeDataFormat.FREQUENCY);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());
            
            dataPath = Paths.get(ClassLoader.getSystemResource(DEFAULT_GENOTYPIC_FILE).toURI());

            SimpleGenotypeData data = (SimpleGenotypeData) SimpleGenotypeData.readData(dataPath,
                    FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareGenotypeVariantData(data, restoredData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.DISTANCES);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Phenotypic Data not found", addedData.getDistancesData());

            SimpleDistanceMatrixData data = SimpleDistanceMatrixData.readData(dataPath, FileType.CSV);

            //data.setUniqueIdentifier(DATA_UID);
            //data.setName(DATASET_NAME);

            compareDistanceMatrixData(data, addedData.getDistancesData());

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

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.DISTANCES);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored data not found", restoredData);

            assertNotNull("Restored Distances Data not found", restoredData.getDistancesData());

            SimpleDistanceMatrixData data = SimpleDistanceMatrixData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareDistanceMatrixData(data, restoredData.getDistancesData());

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

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV, DataType.PHENOTYPIC);

            Path genotypicDataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, genotypicDataPath, FileType.CSV, DataType.GENOTYPIC);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Phenotypic Data not found", addedData.getPhenotypicData());

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());

            ArrayFeatureData phenotypicData = ArrayFeatureData.readData(phenotypicDataPath, FileType.CSV);

            phenotypicData.setUniqueIdentifier(DATA_UID);
            phenotypicData.setName(DATASET_NAME);

            compareFeatureData(phenotypicData, addedData.getPhenotypicData());

            SimpleGenotypeData genotypicData = (SimpleGenotypeData) SimpleGenotypeData
                    .readData(genotypicDataPath, FileType.CSV);

            genotypicData.setUniqueIdentifier(DATA_UID);
            genotypicData.setName(DATASET_NAME);

            compareGenotypeVariantData(genotypicData, addedData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV, DataType.PHENOTYPIC);

            Path genotypicDataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, genotypicDataPath, FileType.CSV, DataType.GENOTYPIC);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored Data not found", restoredData);

            assertNotNull("Restored Phenotypic Data not found", restoredData.getPhenotypicData());

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            ArrayFeatureData phenotypicData = ArrayFeatureData.readData(phenotypicDataPath, FileType.CSV);

            phenotypicData.setUniqueIdentifier(DATA_UID);
            phenotypicData.setName(DATASET_NAME);

            compareFeatureData(phenotypicData, restoredData.getPhenotypicData());

            SimpleGenotypeData genotypicData = (SimpleGenotypeData) SimpleGenotypeData
                    .readData(genotypicDataPath, FileType.CSV);

            genotypicData.setUniqueIdentifier(DATA_UID);
            genotypicData.setName(DATASET_NAME);

            compareGenotypeVariantData(genotypicData, restoredData.getGenotypicData());

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

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV, DataType.PHENOTYPIC);

            Path genotypicDataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, genotypicDataPath, FileType.CSV, DataType.GENOTYPIC);
            
            Path dataPath = Paths.get(ClassLoader.getSystemResource(DISTANCES_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.DISTANCES);

            CoreHunterData addedData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Data not found", addedData);

            assertNotNull("Phenotypic Data not found", addedData.getPhenotypicData());

            assertNotNull("Genotypic Data not found", addedData.getGenotypicData());
            
            assertNotNull("Distances Data not found", addedData.getDistancesData());

            ArrayFeatureData phenotypicData = ArrayFeatureData.readData(phenotypicDataPath, FileType.CSV);

            phenotypicData.setUniqueIdentifier(DATA_UID);
            phenotypicData.setName(DATASET_NAME);

            compareFeatureData(phenotypicData, addedData.getPhenotypicData());

            SimpleGenotypeData genotypicData = (SimpleGenotypeData) SimpleGenotypeData
                    .readData(genotypicDataPath, FileType.CSV);

            genotypicData.setUniqueIdentifier(DATA_UID);
            genotypicData.setName(DATASET_NAME);

            compareGenotypeVariantData(genotypicData, addedData.getGenotypicData());

            SimpleDistanceMatrixData data = SimpleDistanceMatrixData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareDistanceMatrixData(data, addedData.getDistancesData());

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

            fileBasedDatasetServices.loadData(addedDataset, phenotypicDataPath, FileType.CSV, DataType.PHENOTYPIC);

            Path genotypicDataPath = Paths.get(ClassLoader.getSystemResource(FRQUENCY_GENOTYPIC_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, genotypicDataPath, FileType.CSV, DataType.GENOTYPIC);
            
            Path dataPath = Paths.get(ClassLoader.getSystemResource(DISTANCES_FILE).toURI());

            fileBasedDatasetServices.loadData(addedDataset, dataPath, FileType.CSV, DataType.DISTANCES);

            fileBasedDatasetServices = new FileBasedDatasetServices(path);

            assertNotNull("Restored dataset not found",
                    fileBasedDatasetServices.getDataset(dataset.getUniqueIdentifier()));

            CoreHunterData restoredData = fileBasedDatasetServices.getCoreHunterData(dataset.getUniqueIdentifier());

            assertNotNull("Restored Data not found", restoredData);

            assertNotNull("Restored Phenotypic Data not found", restoredData.getPhenotypicData());

            assertNotNull("Restored Genotypic Data not found", restoredData.getGenotypicData());

            assertNotNull("Restored Distances Data not found", restoredData.getDistancesData());

            ArrayFeatureData phenotypicData = ArrayFeatureData.readData(phenotypicDataPath, FileType.CSV);

            phenotypicData.setUniqueIdentifier(DATA_UID);
            phenotypicData.setName(DATASET_NAME);

            compareFeatureData(phenotypicData, restoredData.getPhenotypicData());

            SimpleGenotypeData genotypicData = (SimpleGenotypeData) SimpleGenotypeData
                    .readData(genotypicDataPath, FileType.CSV);

            genotypicData.setUniqueIdentifier(DATA_UID);
            genotypicData.setName(DATASET_NAME);

            compareGenotypeVariantData(genotypicData, restoredData.getGenotypicData());
            
            SimpleDistanceMatrixData data = SimpleDistanceMatrixData.readData(dataPath, FileType.CSV);

            data.setUniqueIdentifier(DATA_UID);
            data.setName(DATASET_NAME);

            compareDistanceMatrixData(data, restoredData.getDistancesData());

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }


    private void compareData(Data expected, Data actual) {
        //assertEquals("Unique Identifier not correct", expected.getUniqueIdentifier(), actual.getUniqueIdentifier());
        //assertEquals("Name not correct", expected.getName(), actual.getName());

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
