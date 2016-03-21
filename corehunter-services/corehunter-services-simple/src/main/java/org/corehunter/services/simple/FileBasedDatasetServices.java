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

import org.corehunter.data.NamedData;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeVariantData;
import org.corehunter.data.simple.SimpleGenotypeVariantData;
import org.corehunter.data.simple.SimplePhenotypicTraitData;
import org.corehunter.services.DatasetServices;
import org.corehunter.services.DataType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import uno.informatics.common.io.FileType;
import uno.informatics.data.Dataset;
import uno.informatics.data.dataset.DatasetException;

public class FileBasedDatasetServices implements DatasetServices {
    private static final String DATASETS = "datasets.xml";

    private static final String BI_ALLELIC_GENOTYPIC_PATH = "BI_ALLELIC_GENOTYPIC_PATH";

    private static final String MULTI_ALLELIC_GENOTYPIC_PATH = "MULTI_ALLELIC_GENOTYPIC_PATH";

    private static final String PHENOTYPIC_PATH = "PHENOTYPIC_PATH";

    private static final String TXT_SUFFIX = ".txt";

    private static Map<String, Dataset> datasetMap;
    private static Map<String, NamedData> dataCache;

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
            throw new DatasetException("Dataset already loaded : " + dataset.getUniqueIdentifier());
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
    public boolean removeDataset(String datasetId) {

        Dataset dataset = datasetMap.remove(datasetId);

        return dataset != null;
    }

    @Override
    public NamedData getData(String datasetId) throws DatasetException {
        Dataset dataset = getDataset(datasetId);

        if (dataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + datasetId);
        }

        NamedData data = dataCache.get(datasetId);

        if (data != null) {
            return data;
        } else {
            try {
                return readData(datasetId);
            } catch (IOException e) {
                throw new DatasetException(e);
            }
        }
    }

    @Override
    public void loadData(Dataset dataset, Path path, FileType fileType, DataType datasetType)
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

        NamedData data = readData(path, fileType, datasetType);

        Path newPath;

        switch (datasetType) {
            case BI_ALLELIC_GENOTYPIC:
                newPath = Paths.get(getPath().toString(), BI_ALLELIC_GENOTYPIC_PATH,
                        internalDataset.getUniqueIdentifier() + TXT_SUFFIX);
                break;
            case MULTI_ALLELIC_GENOTYPIC:
                newPath = Paths.get(getPath().toString(), MULTI_ALLELIC_GENOTYPIC_PATH,
                        internalDataset.getUniqueIdentifier() + TXT_SUFFIX);
                break;
            case PHENOTYPIC:
                newPath = Paths.get(getPath().toString(), PHENOTYPIC_PATH,
                        internalDataset.getUniqueIdentifier() + TXT_SUFFIX);
                break;
            default:
                throw new IllegalArgumentException("Unknown dataset type : " + datasetType);
        }

        if (!Files.exists(newPath)) {
            throw new DatasetException("Data of this type already associate for this dataset : " + dataset.getName());
        }

        writeData(data, newPath, FileType.TXT, DataType.BI_ALLELIC_GENOTYPIC);

        dataCache.put(internalDataset.getUniqueIdentifier(), data);
    }

    @Override
    public boolean removeData(Dataset dataset, String dataId) {
        // TODO Auto-generated method stub
        return false;
    }

    @SuppressWarnings("unchecked")
    private void initialise() throws IOException {

        datasetMap = new HashMap<String, Dataset>();
        dataCache = new HashMap<String, NamedData>();

        if (!Files.exists(getPath())) {
            Files.createDirectories(getPath());
        }

        Path datasetDescriptionsPath = Paths.get(getPath().toString(), DATASETS);

        List<Dataset> datasets;
        if (Files.exists(datasetDescriptionsPath)) {
            InputStream inputStream = Files.newInputStream(datasetDescriptionsPath);

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

    private NamedData readData(String datasetId) throws IOException {

        Path biAllelicGenotypicPath = Paths.get(getPath().toString(), BI_ALLELIC_GENOTYPIC_PATH,
                datasetId + TXT_SUFFIX);

        if (!Files.exists(biAllelicGenotypicPath)) {
            biAllelicGenotypicPath = null;
        }

        Path multiAllelicGenotypicPath = Paths.get(getPath().toString(), MULTI_ALLELIC_GENOTYPIC_PATH,
                datasetId + TXT_SUFFIX);

        if (!Files.exists(multiAllelicGenotypicPath)) {
            multiAllelicGenotypicPath = null;
        }

        Path phenotypicPath = Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + TXT_SUFFIX);

        if (Files.exists(phenotypicPath)) {
            phenotypicPath = null;
        }

        return readData(biAllelicGenotypicPath, multiAllelicGenotypicPath, phenotypicPath);
    }

    private NamedData readData(Path biAllelicGenotypicPath, Path multiAllelicGenotypicPath, Path phenotypicPath) {
        // TODO Auto-generated method stub
        return null;
    }

    private NamedData readData(Path path, FileType fileType, DataType datasetType) throws IOException {
        NamedData data = null;

        switch (datasetType) {
            case BI_ALLELIC_GENOTYPIC:
                data = createBiAllelicGenotypicData(path, fileType);
                break;
            case MULTI_ALLELIC_GENOTYPIC:
                data = createMultiAllelicGenotypicData(path, fileType);
                break;
            case PHENOTYPIC:
                data = createPhenotypicData(path, fileType);
                break;
            default:
                throw new IllegalArgumentException("Unknown dataset type : " + datasetType);
        }

        return data;
    }

    private void writeData(NamedData data, Path newPath, FileType txt, DataType biAllelicGenotypic) {
        // TODO Auto-generated method stub

    }

    private NamedData createBiAllelicGenotypicData(Path path, FileType type) throws IOException {
        NamedData data = SimpleBiAllelicGenotypeVariantData.readData(path, type);

        return data;
    }

    private NamedData createMultiAllelicGenotypicData(Path path, FileType type) throws IOException {
        NamedData data = SimpleGenotypeVariantData.readData(path, type);

        return data;
    }

    private NamedData createPhenotypicData(Path path, FileType type) throws IOException {
        NamedData data = SimplePhenotypicTraitData.readData(path, type);

        return data;
    }

    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());

        xstream.setClassLoader(getClass().getClassLoader());

        return xstream;
    }
}
