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

package org.corehunter.services.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.corehunter.data.BiAllelicGenotypeVariantData;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.GenotypeVariantData;
import org.corehunter.data.matrix.SymmetricMatrixFormat;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeVariantData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleGenotypeVariantData;
import org.corehunter.services.DatasetServices;
import org.corehunter.services.DataType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import uno.informatics.common.io.FileType;
import uno.informatics.data.Data;
import uno.informatics.data.Dataset;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.feature.array.ArrayFeatureData;

public class FileBasedDatasetServices implements DatasetServices {
    private static final String DATASETS = "datasets.xml";

    private static final String BI_ALLELIC_GENOTYPIC_PATH = "BI_ALLELIC_GENOTYPIC_PATH";

    private static final String GENOTYPIC_PATH = "GENOTYPIC_PATH";

    private static final String PHENOTYPIC_PATH = "PHENOTYPIC_PATH";

    private static final String DISTANCE_PATH = "DISTANCE_PATH";

    private static final String TXT_SUFFIX = ".txt";

    private static Map<String, Dataset> datasetMap;
    private static Map<String, CoreHunterData> dataCache;

    private Path path;

    public FileBasedDatasetServices() throws IOException {
        setPath(Paths.get(""));

        initialise();
    }

    public FileBasedDatasetServices(Path path) throws IOException {
        setPath(path);

        initialise();
    }

    public final Path getPath() {
        return path;
    }

    public synchronized final void setPath(Path path) throws IOException {
        if (path == null) {
            throw new IOException("Path must be defined!");
        }

        this.path = path;

        initialise();
    }

    @Override
    public List<Dataset> getAllDatasets() {
        return new ArrayList<Dataset>(datasetMap.values());
    }

    @Override
    public Dataset getDataset(String datasetId) {

        return datasetMap.get(datasetId);
    }

    @Override
    public void addDataset(Dataset dataset) throws DatasetException {

        if (dataset == null) {
            throw new DatasetException("Dataset undefined");
        }

        if (!datasetMap.containsKey(dataset.getUniqueIdentifier())) {
            datasetMap.put(dataset.getUniqueIdentifier(), dataset);
        } else {
            throw new DatasetException("Dataset already added : " + dataset.getUniqueIdentifier());
        }

        ArrayList<Dataset> datasets = new ArrayList<Dataset>(datasetMap.values());

        XStream xstream = createXStream();

        OutputStream outputStream;

        // TODO output to temp file and then copy

        try {
            outputStream = Files.newOutputStream(Paths.get(getPath().toString(), DATASETS));

            xstream.toXML(datasets, outputStream);
        } catch (IOException e) {
            throw new DatasetException(e);
        }
    }

    @Override
    public boolean removeDataset(String datasetId) throws DatasetException {

        Dataset dataset = getDataset(datasetId);

        if (dataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + datasetId);
        }

        removeDataInternal(datasetId);

        return datasetMap.remove(datasetId, dataset);
    }

    @Override
    public CoreHunterData getData(String datasetId) throws DatasetException {
        Dataset dataset = getDataset(datasetId);

        if (dataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + datasetId);
        }

        CoreHunterData data = dataCache.get(datasetId);

        if (data != null) {
            return data;
        } else {
            try {
                return readDataInternal(datasetId);
            } catch (IOException e) {
                throw new DatasetException(e);
            }
        }
    }

    @Override
    public void removeData(String datasetId) throws DatasetException {
        Dataset dataset = getDataset(datasetId);

        if (dataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + datasetId);
        }

        removeDataInternal(datasetId);
    }

    @Override
    public void loadData(Dataset dataset, Path path, FileType fileType, DataType dataType)
            throws IOException, DatasetException {

        if (dataset == null) {
            throw new DatasetException("Dataset not defined!");
        }

        Dataset internalDataset = getDataset(dataset.getUniqueIdentifier());

        if (internalDataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + dataset.getUniqueIdentifier());
        }

        if (!Files.exists(path)) {
            throw new DatasetException("Unknown path : " + path);
        }

        String datasetId = internalDataset.getUniqueIdentifier();

        Path newPath;

        CoreHunterData coreHunterData = getData(internalDataset.getUniqueIdentifier());

        switch (dataType) {
            case BI_ALLELIC_GENOTYPIC:
                if (coreHunterData != null && coreHunterData.getGenotypicData() != null) {
                    throw new DatasetException(
                            "Genotypic Data is already associated for this dataset : " + dataset.getName());
                }

                SimpleBiAllelicGenotypeVariantData biAllelicenotypeData = SimpleBiAllelicGenotypeVariantData
                        .readData(path, fileType);

                if (coreHunterData != null) {
                    coreHunterData = new CoreHunterData(biAllelicenotypeData, coreHunterData.getPhenotypicData(),
                            coreHunterData.getDistancesData());
                } else {
                    coreHunterData = new CoreHunterData(biAllelicenotypeData, null, null);

                    dataCache.put(datasetId, coreHunterData);
                }

                newPath = Paths.get(getPath().toString(), BI_ALLELIC_GENOTYPIC_PATH, datasetId + TXT_SUFFIX);

                biAllelicenotypeData.writeData(newPath, fileType);

                break;
            case GENOTYPIC:
                if (coreHunterData != null && coreHunterData.getGenotypicData() != null) {
                    throw new DatasetException(
                            "Genotypic Data is already associated for this dataset : " + dataset.getName());
                }

                SimpleGenotypeVariantData genotypeData = 
                        (SimpleGenotypeVariantData)SimpleGenotypeVariantData.readData(path, fileType);

                if (coreHunterData != null) {
                    coreHunterData = new CoreHunterData(genotypeData, coreHunterData.getPhenotypicData(),
                            coreHunterData.getDistancesData());
                } else {
                    coreHunterData = new CoreHunterData(genotypeData, null, null);

                    dataCache.put(datasetId, coreHunterData);
                }

                newPath = Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + TXT_SUFFIX);

                genotypeData.writeData(newPath, fileType);
                break;
            case PHENOTYPIC:
                if (coreHunterData != null && coreHunterData.getGenotypicData() != null) {
                    throw new DatasetException(
                            "Genotypic Data is already associated for this dataset : " + dataset.getName());
                }

                ArrayFeatureData arrayFeatureData = ArrayFeatureData.readData(path, fileType);
                if (coreHunterData != null) {
                    coreHunterData = new CoreHunterData(coreHunterData.getGenotypicData(), arrayFeatureData,
                            coreHunterData.getDistancesData());
                } else {
                    coreHunterData = new CoreHunterData(null, arrayFeatureData, null);

                    dataCache.put(datasetId, coreHunterData);
                }

                newPath = Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + TXT_SUFFIX);

                ArrayFeatureData.writeData(newPath, arrayFeatureData, fileType);
                
                break;
            case DISTANCE:
                if (coreHunterData != null && coreHunterData.getGenotypicData() != null) {
                    throw new DatasetException(
                            "Genotypic Data is already associated for this dataset : " + dataset.getName());
                }

                SimpleDistanceMatrixData distance = SimpleDistanceMatrixData.readData(path, fileType,
                        SymmetricMatrixFormat.FULL);

                if (coreHunterData != null) {
                    coreHunterData = new CoreHunterData(coreHunterData.getGenotypicData(),
                            coreHunterData.getPhenotypicData(), distance);
                } else {
                    coreHunterData = new CoreHunterData(null, null, distance);

                    dataCache.put(datasetId, coreHunterData);
                }

                newPath = Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + TXT_SUFFIX);

                distance.writeData(newPath, fileType);
                
                break;
            default:
                throw new IllegalArgumentException("Unknown data type : " + dataType);
        }
    }

    @SuppressWarnings("unchecked")
    private void initialise() throws IOException {

        datasetMap = new HashMap<String, Dataset>();
        dataCache = new HashMap<String, CoreHunterData>();

        if (!Files.exists(getPath())) {
            Files.createDirectories(getPath());
        }

        Path datasetsPath = Paths.get(getPath().toString(), DATASETS);

        List<Dataset> datasets;
        if (Files.exists(datasetsPath)) {
            InputStream inputStream = Files.newInputStream(datasetsPath);

            XStream xstream = createXStream();

            datasets = (List<Dataset>) xstream.fromXML(inputStream);

            Iterator<Dataset> iterator = datasets.iterator();

            Dataset dataset = null;

            while (iterator.hasNext()) {
                dataset = iterator.next();

                datasetMap.put(dataset.getUniqueIdentifier(), dataset);
            }

        }
    }

    private Path getDataPath(String datasetId, DataType dataType) {
        switch (dataType) {
            case BI_ALLELIC_GENOTYPIC:
                return Paths.get(getPath().toString(), BI_ALLELIC_GENOTYPIC_PATH, datasetId + TXT_SUFFIX);
            case GENOTYPIC:
                return Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + TXT_SUFFIX);
            case PHENOTYPIC:
                return Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + TXT_SUFFIX);
            case DISTANCE:
                return Paths.get(getPath().toString(), DISTANCE_PATH, datasetId + TXT_SUFFIX);
            default:
                throw new IllegalArgumentException("Unknown dataset type : " + dataType);
        }
    }

    private CoreHunterData readDataInternal(String datasetId) throws IOException {

        GenotypeVariantData genotypicData = null;
        ArrayFeatureData phenotypicData = null;
        SimpleDistanceMatrixData distance = null;

        Path path = Paths.get(getPath().toString(), BI_ALLELIC_GENOTYPIC_PATH, datasetId + TXT_SUFFIX);

        if (Files.exists(path)) {
            genotypicData = SimpleBiAllelicGenotypeVariantData.readData(path, FileType.TXT);
        }

        path = Paths.get(getPath().toString(), BI_ALLELIC_GENOTYPIC_PATH, datasetId + TXT_SUFFIX);

        if (Files.exists(path)) {
            genotypicData = SimpleGenotypeVariantData.readData(path, FileType.TXT);
        }

        path = Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + TXT_SUFFIX);

        if (Files.exists(path)) {
            phenotypicData = ArrayFeatureData.readData(path, FileType.TXT);
        }

        path = Paths.get(getPath().toString(), DISTANCE_PATH, datasetId + TXT_SUFFIX);

        if (Files.exists(path)) {
            distance = SimpleDistanceMatrixData.readData(path, FileType.TXT, SymmetricMatrixFormat.FULL);
        }

        if (genotypicData != null || phenotypicData != null || distance != null) {
            return new CoreHunterData(genotypicData, phenotypicData, distance);
        } else {
            return null ;
        }
    }

    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());

        xstream.setClassLoader(getClass().getClassLoader());

        return xstream;
    }

    private void removeDataInternal(String datasetId) throws DatasetException {

        dataCache.remove(datasetId);

        try {
            Files.deleteIfExists(getDataPath(datasetId, DataType.BI_ALLELIC_GENOTYPIC));

            Files.deleteIfExists(getDataPath(datasetId, DataType.GENOTYPIC));

            Files.deleteIfExists(getDataPath(datasetId, DataType.PHENOTYPIC));

            Files.deleteIfExists(getDataPath(datasetId, DataType.DISTANCE));
        } catch (IOException e) {
            throw new DatasetException(e);
        }
    }
}
