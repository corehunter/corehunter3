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
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.CoreHunterDataType;
import org.corehunter.data.GenotypeData;
import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleGenotypeData;
import org.corehunter.data.simple.SimplePhenotypeData;
import org.corehunter.services.DatasetServices;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import uno.informatics.data.Data;
import uno.informatics.data.Dataset;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.DatasetPojo;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * A simple FileBasedDatasetServices implementation that persists datasets on
 * the file system. Sub-classes, can use the {@link #FileBasedDatasetServices()
 * constructor} provided the path is defined in the overloaded constructor using
 * the {@link #setPath(Path)} method
 * 
 * @author daveneti
 *
 */
public class FileBasedDatasetServices implements DatasetServices {
    private static final String DATASETS = "datasets.xml";
    private static final String ORIGINAL_FORMAT_SUFFIX = ".original";

    private static final String GENOTYPIC_PATH = "GENOTYPIC_PATH";

    private static final String PHENOTYPIC_PATH = "PHENOTYPIC_PATH";

    private static final String DISTANCES_PATH = "DISTANCES_PATH";

    private static final String TXT_SUFFIX = ".txt";
    private static final String SUFFIX = ".corehunter";
    private static final String DATA_SUFFIX = ".data";

    private static Map<String, DatasetPojo> datasetMap;
    private static Map<String, CoreHunterData> dataCache;

    private Path path;

    /**
     * Constructor that can be used by sub-classes provided the path is defined
     * in the overloaded constructor using the {@link #setPath(Path)} method
     * 
     * @throws IOException
     *             if the path can not be set or is invalid
     */
    protected FileBasedDatasetServices() throws IOException {
        datasetMap = new HashMap<String, DatasetPojo>();
        dataCache = new HashMap<String, CoreHunterData>();
    }

    /**
     * Constructor that is a path to defined the location of the datasets
     * 
     * @param path
     *            the location of the datasets
     * @throws IOException
     *             if the path can not be set or is invalid
     */
    public FileBasedDatasetServices(Path path) throws IOException {
        this();

        setPath(path);
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
            throw new DatasetException("Dataset undefined!");
        }

        if (dataset.getName() == null || dataset.getName().trim().isEmpty()) {
            throw new DatasetException("Dataset name must be provided and not blank text!");
        }

        if (dataset.getStudy() != null) {
            throw new DatasetException("Study not supported yet!");
        }

        if (dataset.getType() != null) {
            throw new DatasetException("Type not supported yet!");
        }

        if (dataset.getSize() != 0) {
            throw new DatasetException("No data has been associated so size must be 0!");
        }

        DatasetPojo datasetToBeAdded = new DatasetPojo(dataset);

        // create an ID if not given
        if (datasetToBeAdded.getUniqueIdentifier() == null) {
            datasetToBeAdded.setUniqueIdentifier(UUID.randomUUID().toString());
        }

        synchronized (datasetMap) {
            if (!datasetMap.containsKey(dataset.getUniqueIdentifier())) {
                datasetMap.put(dataset.getUniqueIdentifier(), new DatasetPojo(dataset));
            } else {
                throw new DatasetException("Dataset already added : " + dataset.getUniqueIdentifier());
            }

            writeDatasets();
        }
    }

    @Override
    public synchronized boolean removeDataset(String datasetId) throws DatasetException {

        Dataset dataset = getDataset(datasetId);

        if (dataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + datasetId);
        }

        removeDataInternal(datasetId);

        boolean removedDataset = false;

        synchronized (datasetMap) {
            removedDataset = datasetMap.remove(datasetId, dataset);

            writeDatasets();
        }

        return removedDataset;
    }

    @Override
    public synchronized boolean updateDataset(Dataset dataset) throws DatasetException {

        if (dataset == null) {
            throw new DatasetException("Dataset not defined!");
        }

        if (dataset.getUniqueIdentifier() == null) {
            throw new DatasetException("Dataset identifier not defined!");
        }

        Dataset currentDataset = getDataset(dataset.getUniqueIdentifier());

        if (currentDataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + dataset.getUniqueIdentifier());
        }

        // copy of the updated dataset
        DatasetPojo updatedDataset = new DatasetPojo(dataset);

        // copy of the current dataset
        DatasetPojo currentDatasetCopy = new DatasetPojo(currentDataset);

        boolean datasetUpdated = false;

        if (!ObjectUtils.equals(currentDatasetCopy.getName(), updatedDataset.getName())) {

            if (updatedDataset.getName() == null || updatedDataset.getName().trim().isEmpty()) {
                throw new DatasetException("Dataset name must be provided and not blank text!");
            }

            currentDatasetCopy.setName(updatedDataset.getName());
            datasetUpdated = true;
        }

        if (!ObjectUtils.equals(currentDatasetCopy.getDescription(), updatedDataset.getDescription())) {
            currentDatasetCopy.setDescription(updatedDataset.getDescription());
            datasetUpdated = true;
        }

        if (!ObjectUtils.equals(currentDatasetCopy.getAbbreviation(), updatedDataset.getAbbreviation())) {
            currentDatasetCopy.setAbbreviation(updatedDataset.getAbbreviation());
            datasetUpdated = true;
        }

        if (currentDatasetCopy.getSize() != updatedDataset.getSize()) {
            throw new DatasetException("Can not update size on dataset!");
        }

        if (!ObjectUtils.equals(currentDatasetCopy.getStudy(), updatedDataset.getStudy())) {
            throw new DatasetException("Study not supported yet!");
        }

        if (!ObjectUtils.equals(currentDatasetCopy.getType(), updatedDataset.getType())) {
            throw new DatasetException("Type not supported yet!");
        }

        // save the new version of the current dataset
        synchronized (datasetMap) {
            if (datasetUpdated) {
                datasetMap.put(currentDatasetCopy.getUniqueIdentifier(), currentDatasetCopy);
            }

            writeDatasets();
        }

        return datasetUpdated;
    }

    @Override
    public SimpleEntity[] getHeaders(String datasetId) throws DatasetException {
        CoreHunterData data = getCoreHunterData(datasetId);

        if (data == null) {
            throw new DatasetException("No data associated with datasetId : " + datasetId);
        }

        SimpleEntity[] headers = new SimpleEntity[data.getSize()];

        for (int i = 0; i < headers.length; ++i) {
            headers[i] = data.getHeader(i);
        }

        return headers;
    }

    @Override
    public CoreHunterData getCoreHunterData(String datasetId) throws DatasetException {
        Dataset dataset = getDataset(datasetId);

        if (dataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + datasetId);
        }

        CoreHunterData data = dataCache.get(datasetId);

        if (data != null) {
            return data;
        } else {
            try {
                return readCoreHunterDataInternal(datasetId);
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
    public void loadData(Dataset dataset, Path path, FileType fileType, CoreHunterDataType dataType,
        Object... options) throws IOException, DatasetException {

        if (dataset == null) {
            throw new DatasetException("Dataset not defined!");
        }

        if (fileType == null) {
            throw new DatasetException("File type not defined!");
        }

        if (dataType == null) {
            throw new DatasetException("Data type not defined!");
        }

        DatasetPojo internalDataset = datasetMap.get(dataset.getUniqueIdentifier());

        if (internalDataset == null) {
            throw new DatasetException("Unknown dataset with datasetId : " + dataset.getUniqueIdentifier());
        }

        if (!Files.exists(path)) {
            throw new DatasetException("Unknown path : " + path);
        }

        String datasetId = internalDataset.getUniqueIdentifier();

        String dataId = dataset.getUniqueIdentifier();
        String dataName = dataset.getName();

        Path copyPath;
        Path internalPath;

        Path originalFormatPath;
        Path dataPath;

        CoreHunterData coreHunterData = getCoreHunterData(internalDataset.getUniqueIdentifier());

        switch (dataType) {
            case GENOTYPIC:

                copyPath = Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + getSuffix(fileType));

                internalPath = Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + SUFFIX);

                originalFormatPath = Paths.get(getPath().toString(), GENOTYPIC_PATH,
                    datasetId + ORIGINAL_FORMAT_SUFFIX);

                dataPath = Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + DATA_SUFFIX);

                if (coreHunterData != null
                    && (coreHunterData.getGenotypicData() != null || Files.exists(copyPath))) {
                    throw new DatasetException(
                        "Genotypic Data is already associated for this dataset : " + dataset.getName());
                }

                try {
                    copyOrMoveFile(path, copyPath);
                } catch (Exception e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                GenotypeDataFormat genotypeDataFormat = getGenotypeDataFormat(options);
                GenotypeData genotypeData;

                try {
                    switch (genotypeDataFormat) {
                        case BIPARENTAL:
                            genotypeData = SimpleBiAllelicGenotypeData.readData(copyPath, fileType);
                            break;
                        default:
                            genotypeData = SimpleGenotypeData.readData(copyPath, fileType,
                                genotypeDataFormat);
                            break;
                    }
                } catch (IOException e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                // TODO write data method should be on interface?
                try {
                    switch (genotypeDataFormat) {
                        case BIPARENTAL:
                            ((SimpleBiAllelicGenotypeData) genotypeData).writeData(internalPath,
                                FileType.TXT);
                            break;
                        default:
                            ((SimpleGenotypeData) genotypeData).writeData(internalPath, FileType.TXT);
                            break;
                    }
                } catch (IOException e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                try {
                    writeToFile(originalFormatPath, genotypeDataFormat);
                } catch (IOException e) {
                    Files.deleteIfExists(originalFormatPath);
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                if (coreHunterData != null) {
                    coreHunterData = new CoreHunterData(genotypeData, coreHunterData.getPhenotypicData(),
                        coreHunterData.getDistancesData());
                } else {
                    coreHunterData = new CoreHunterData(genotypeData, null, null);
                }

                dataCache.put(datasetId, coreHunterData);

                break;
            case PHENOTYPIC:

                copyPath = Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + getSuffix(fileType));

                internalPath = Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + SUFFIX);

                dataPath = Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + DATA_SUFFIX);

                if (coreHunterData != null
                    && (coreHunterData.getPhenotypicData() != null || Files.exists(copyPath))) {
                    throw new DatasetException(
                        "Phenotypic Data is already associated for this dataset : " + dataset.getName());
                }

                try {
                    copyOrMoveFile(path, copyPath);
                } catch (Exception e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                SimplePhenotypeData phenotypeData;

                try {
                    phenotypeData = SimplePhenotypeData.readPhenotypeData(copyPath, fileType);
                } catch (IOException e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                // TODO write data method should be on interface?
                try {
                    phenotypeData.writeData(internalPath, FileType.TXT);
                } catch (IOException e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                if (coreHunterData != null) {
                    coreHunterData = new CoreHunterData(coreHunterData.getGenotypicData(), phenotypeData,
                        coreHunterData.getDistancesData());
                } else {
                    coreHunterData = new CoreHunterData(null, phenotypeData, null);
                }

                dataCache.put(datasetId, coreHunterData);

                break;
            case DISTANCES:

                copyPath = Paths.get(getPath().toString(), DISTANCES_PATH, datasetId + getSuffix(fileType));

                internalPath = Paths.get(getPath().toString(), DISTANCES_PATH, datasetId + SUFFIX);

                dataPath = Paths.get(getPath().toString(), DISTANCES_PATH, datasetId + DATA_SUFFIX);

                if (coreHunterData != null
                    && (coreHunterData.getDistancesData() != null || Files.exists(copyPath))) {
                    throw new DatasetException(
                        "Distances Data is already associated for this dataset : " + dataset.getName());
                }

                try {
                    copyOrMoveFile(path, copyPath);
                } catch (Exception e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                SimpleDistanceMatrixData distanceData;

                try {
                    distanceData = SimpleDistanceMatrixData.readData(copyPath, fileType);
                } catch (IOException e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                try {
                    distanceData.writeData(internalPath, FileType.TXT);
                } catch (IOException e) {
                    Files.deleteIfExists(copyPath);
                    throw e;
                }

                if (coreHunterData != null) {
                    coreHunterData = new CoreHunterData(coreHunterData.getGenotypicData(),
                        coreHunterData.getPhenotypicData(), distanceData);
                } else {
                    coreHunterData = new CoreHunterData(null, null, distanceData);
                }

                dataCache.put(datasetId, coreHunterData);
                break;
            default:
                throw new IllegalArgumentException("Unknown data type : " + dataType);
        }

        try {
            writeToFile(dataPath, new SimpleEntityPojo(dataId, dataName));
        } catch (IOException e) {
            throw new DatasetException(e);
        }

        internalDataset.setSize(coreHunterData.getSize());

        writeDatasets();
    }

    @Override
    public Data getOriginalData(String datasetId, CoreHunterDataType dataType) throws DatasetException {

        if (path == null) {
            throw new DatasetException("Path not defined!");
        }

        if (datasetId == null) {
            throw new DatasetException("Dataset Id not defined!");
        }

        if (dataType == null) {
            throw new DatasetException("Data type not defined!");
        }

        Path originalPath;
        Path originalFormatPath;

        FileType fileType;

        try {
            switch (dataType) {
                case GENOTYPIC:

                    fileType = getFileType(path, GENOTYPIC_PATH, datasetId);

                    if (fileType == null) {
                        return null;
                    }

                    originalPath = Paths.get(getPath().toString(), GENOTYPIC_PATH,
                        datasetId + getSuffix(fileType));
                    originalFormatPath = Paths.get(getPath().toString(), GENOTYPIC_PATH,
                        datasetId + ORIGINAL_FORMAT_SUFFIX);

                    GenotypeDataFormat genotypeDataFormat = (GenotypeDataFormat) readFromFile(
                        originalFormatPath);

                    GenotypeData genotypeData;

                    switch (genotypeDataFormat) {
                        case BIPARENTAL:
                            genotypeData = SimpleBiAllelicGenotypeData.readData(originalPath, fileType);
                            break;
                        default:
                            genotypeData = SimpleGenotypeData.readData(originalPath, fileType,
                                genotypeDataFormat);
                            break;
                    }

                    return genotypeData;
                case PHENOTYPIC:

                    fileType = getFileType(path, PHENOTYPIC_PATH, datasetId);

                    if (fileType == null) {
                        return null;
                    }

                    originalPath = Paths.get(getPath().toString(), PHENOTYPIC_PATH,
                        datasetId + getSuffix(fileType));

                    SimplePhenotypeData phenotypeData = SimplePhenotypeData.readPhenotypeData(originalPath,
                        fileType);

                    return phenotypeData;
                case DISTANCES:

                    fileType = getFileType(path, DISTANCES_PATH, datasetId);

                    if (fileType == null) {
                        return null;
                    }

                    originalPath = Paths.get(getPath().toString(), DISTANCES_PATH,
                        datasetId + getSuffix(fileType));

                    SimpleDistanceMatrixData distanceData = SimpleDistanceMatrixData.readData(originalPath,
                        fileType);

                    return distanceData;
                default:
                    throw new IllegalArgumentException("Unknown data type : " + dataType);

            }
        } catch (IOException e) {
            throw new DatasetException("Can not reload original data!", e);
        }
    }

    private void writeDatasets() throws DatasetException {
        ArrayList<Dataset> datasets = new ArrayList<Dataset>(datasetMap.values());

        try {
            writeToFile(Paths.get(getPath().toString(), DATASETS), datasets);
        } catch (IOException e) {
            throw new DatasetException(e);
        }
    }

    private FileType getFileType(Path path, String dataTypePath, String datasetId) {

        Path originalPath = Paths.get(path.toString(), dataTypePath, datasetId + getSuffix(FileType.CSV));

        if (Files.exists(originalPath)) {
            return FileType.CSV;
        }

        originalPath = Paths.get(path.toString(), dataTypePath, datasetId + getSuffix(FileType.TXT));

        if (Files.exists(originalPath)) {
            return FileType.TXT;
        }

        originalPath = Paths.get(path.toString(), dataTypePath, datasetId + getSuffix(FileType.XLS));

        if (Files.exists(originalPath)) {
            return FileType.XLS;
        }

        originalPath = Paths.get(path.toString(), dataTypePath, datasetId + getSuffix(FileType.XLSX));

        if (Files.exists(originalPath)) {
            return FileType.XLSX;
        }

        return null;
    }

    private String getSuffix(FileType fileType) {

        switch (fileType) {
            case CSV:
                return ".csv";
            default:
            case TXT:
                return ".txt";
            case XLS:
                return ".xls";
            case XLSX:
                return ".xlsx";
        }
    }

    private void copyOrMoveFile(Path source, Path target) throws IOException {

        Files.createDirectories(target.getParent());

        Files.copy(source, target);
    }

    private GenotypeDataFormat getGenotypeDataFormat(Object[] options) {

        GenotypeDataFormat format = null;

        if (options != null) {
            for (int i = 0; i < options.length; ++i) {
                if (options[i] instanceof GenotypeDataFormat) {
                    if (format != null) {
                        throw new IllegalArgumentException("Genotype Data Format given twice as an option!");
                    }

                    format = (GenotypeDataFormat) options[i];
                }
            }
        }

        if (format == null) {
            format = GenotypeDataFormat.FREQUENCY; // default option
        }

        return format;
    }

    /**
     * Reads an object from a file. The default implementation uses XStream.
     * Override to use another way to read objects. Must be compatible with the
     * {@link #writeToFile(Path, Object)} method
     * 
     * @param path
     *            the path of the file to be read
     * @return the object read from the file
     * @throws IOException
     *             if the object can not be read from the file
     */
    protected Object readFromFile(Path path) throws IOException {
        XStream xstream = createXStream();

        InputStream inputStream = Files.newInputStream(path);

        // TODO output to temp file and then copy

        try {
            return xstream.fromXML(inputStream);
        } catch (XStreamException e) {
            throw new IOException(e);
        }
    }

    /**
     * Write an object to a file. The default implementation uses XStream.
     * Override to use another way to write objects. Must be compatible with the
     * {@link #readFromFile(Path)} method
     * 
     * @param path
     *            the path of the file to be written
     * @param object
     *            the object to be written
     * @throws IOException
     *             if the object can not be write to the file
     */
    protected void writeToFile(Path path, Object object) throws IOException {
        XStream xstream = createXStream();

        OutputStream outputStream;

        // TODO output to temp file and then copy

        outputStream = Files.newOutputStream(path);

        try {
            xstream.toXML(object, outputStream);
        } catch (XStreamException e) {
            throw new IOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void initialise() throws IOException {

        if (!Files.exists(getPath())) {
            Files.createDirectories(getPath());
        }

        Path datasetsPath = Paths.get(getPath().toString(), DATASETS);

        List<DatasetPojo> datasets;
        if (Files.exists(datasetsPath)) {

            datasets = (List<DatasetPojo>) readFromFile(datasetsPath);

            Iterator<DatasetPojo> iterator = datasets.iterator();

            DatasetPojo dataset = null;

            while (iterator.hasNext()) {
                dataset = iterator.next();

                datasetMap.put(dataset.getUniqueIdentifier(), dataset);
            }

        }
    }

    private CoreHunterData readCoreHunterDataInternal(String datasetId) throws IOException {

        GenotypeData genotypicData = null;
        SimplePhenotypeData phenotypicData = null;
        SimpleDistanceMatrixData distance = null;

        Path path = Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + SUFFIX);
        Path dataPath;

        if (Files.exists(path)) {

            Path originalFormatPath = Paths.get(getPath().toString(), GENOTYPIC_PATH,
                datasetId + ORIGINAL_FORMAT_SUFFIX);

            GenotypeDataFormat genotypeDataFormat = (GenotypeDataFormat) readFromFile(originalFormatPath);

            switch (genotypeDataFormat) {
                case BIPARENTAL:
                    genotypicData = SimpleBiAllelicGenotypeData.readData(path, FileType.TXT);
                    break;
                default:
                    genotypicData = SimpleGenotypeData.readData(path, FileType.TXT);
                    break;
            }

            dataPath = Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + DATA_SUFFIX);

            updateData(phenotypicData, dataPath);
        }

        path = Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + SUFFIX);

        if (Files.exists(path)) {
            phenotypicData = SimplePhenotypeData.readPhenotypeData(path, FileType.TXT);

            dataPath = Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + DATA_SUFFIX);

            updateData(phenotypicData, dataPath);
        }

        path = Paths.get(getPath().toString(), DISTANCES_PATH, datasetId + SUFFIX);

        if (Files.exists(path)) {
            distance = SimpleDistanceMatrixData.readData(path, FileType.TXT);

            dataPath = Paths.get(getPath().toString(), DISTANCES_PATH, datasetId + DATA_SUFFIX);

            updateData(distance, dataPath);
        }

        if (genotypicData != null || phenotypicData != null || distance != null) {
            return new CoreHunterData(genotypicData, phenotypicData, distance);
        } else {
            return null;
        }
    }

    private void updateData(Data data, Path path) throws IOException {

        SimpleEntityPojo simpleEntityPojo = (SimpleEntityPojo) readFromFile(path);

        if (data instanceof SimpleEntityPojo) {
            ((SimpleEntityPojo) data).setUniqueIdentifier(simpleEntityPojo.getUniqueIdentifier());
            ((SimpleEntityPojo) data).setName(simpleEntityPojo.getName());
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
            Files.deleteIfExists(getDataPath(datasetId, CoreHunterDataType.GENOTYPIC));

            Files.deleteIfExists(getDataPath(datasetId, CoreHunterDataType.PHENOTYPIC));

            Files.deleteIfExists(getDataPath(datasetId, CoreHunterDataType.DISTANCES));
        } catch (IOException e) {
            throw new DatasetException(e);
        }
    }

    private Path getDataPath(String datasetId, CoreHunterDataType dataType) {
        switch (dataType) {
            case GENOTYPIC:
                return Paths.get(getPath().toString(), GENOTYPIC_PATH, datasetId + TXT_SUFFIX);
            case PHENOTYPIC:
                return Paths.get(getPath().toString(), PHENOTYPIC_PATH, datasetId + TXT_SUFFIX);
            case DISTANCES:
                return Paths.get(getPath().toString(), DISTANCES_PATH, datasetId + TXT_SUFFIX);
            default:
                throw new IllegalArgumentException("Unknown dataset type : " + dataType);
        }
    }
}
