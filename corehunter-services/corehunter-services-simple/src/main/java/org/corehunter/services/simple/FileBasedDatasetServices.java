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
package org.corehunter.services.simple;

import java.io.File;
import java.io.FileOutputStream;
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
import java.util.ListIterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.corehunter.services.DatasetServices;
import org.corehunter.services.DatasetType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import uno.informatics.common.io.FileType;
import uno.informatics.data.Dataset;
import uno.informatics.data.Entity;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.feature.array.ArrayFeatureDataset;
import uno.informatics.data.feature.array.ZipFeatureDatasetReader;
import uno.informatics.data.feature.array.ZipFeatureDatasetWriter;
import uno.informatics.data.pojo.SimpleEntityPojo;

public class FileBasedDatasetServices implements DatasetServices {
    private static final String DATASET_DESCRIPTIONS = "datasetDescriptions.xml";

    private static List<SimpleEntity> datasetDescriptions;

    private static Map<String, Dataset> datasetMap;

    private static List<Dataset> datasetCache;

    private Path path;

    public FileBasedDatasetServices(Path path) throws DatasetException {
	setPath(path);

	initialise();
    }

    public synchronized final Path getPath() {
	return path;
    }

    protected synchronized final void setPath(Path path) {
	if (path == null)
	    throw new IllegalArgumentException("Path must be defined!");

	this.path = path;
    }

    @Override
    public List<Dataset> getAllDatasets() throws DatasetException {
	if (datasetCache == null) {
	    datasetCache = new ArrayList<Dataset>(datasetDescriptions.size());

	    Iterator<SimpleEntity> iterator = datasetDescriptions.iterator();

	    while (iterator.hasNext())
		datasetCache.add(getDataset(iterator.next().getUniqueIdentifier()));
	}

	return datasetCache;
    }

    @Override
    public List<SimpleEntity> getDatasetDescriptions() {
	return datasetDescriptions;
    }

    @Override
    public Dataset getDataset(String datasetId) throws DatasetException {
	if (datasetMap.containsKey(datasetId)) {
	    return datasetMap.get(datasetId);
	} else {
	    Dataset dataset = loadDataset(datasetId);

	    datasetMap.put(datasetId, dataset);

	    return dataset;
	}
    }

    @Override
    public void addDataset(Path path, FileType fileType, DatasetType datasetType) throws DatasetException {
	Dataset dataset = createDataset(path, fileType, datasetType);

	if (!datasetMap.containsKey(dataset.getUniqueIdentifier())) {
	    datasetDescriptions.add(new SimpleEntityPojo(dataset));
	    datasetMap.put(dataset.getUniqueIdentifier(), dataset);

	    if (datasetCache != null)
		datasetCache.add(dataset);

	    XStream xstream = createXStream();

	    OutputStream outputStream;

	    try {
		outputStream = Files.newOutputStream(Paths.get(getPath().toString(), DATASET_DESCRIPTIONS));

		xstream.toXML(datasetDescriptions, outputStream);
	    } catch (IOException e) {
		throw new DatasetException(e);
	    }
	} else {
	    throw new DatasetException("Dataset already present : " + dataset.getUniqueIdentifier());
	}
    }

    @Override
    public void removeDataset(String datasetId) throws DatasetException {
	Dataset dataset = getDataset(datasetId);

	if (dataset != null) {
	    ListIterator<SimpleEntity> listIterator = datasetDescriptions.listIterator();

	    boolean found = false;

	    while (!found && listIterator.hasNext()) {
		found = datasetId.equals(listIterator.next().getUniqueIdentifier());

		if (found)
		    listIterator.remove();
	    }

	    datasetDescriptions.remove(datasetId);

	    Dataset removed = datasetMap.remove(datasetId);

	    if (removed != null && datasetCache != null)
		datasetCache.remove(dataset);
	}
    }

    @SuppressWarnings("unchecked")
    private void initialise() throws DatasetException {
	try {
	    Path datasetDescriptionsPath = Paths.get(getPath().toString(), DATASET_DESCRIPTIONS);

	    if (Files.exists(datasetDescriptionsPath)) {
		InputStream inputStream = Files.newInputStream(datasetDescriptionsPath);

		XStream xstream = createXStream();

		datasetDescriptions = (List<SimpleEntity>) xstream.fromXML(inputStream);
	    } else {
		datasetDescriptions = new ArrayList<SimpleEntity>();
	    }

	    datasetMap = new HashMap<String, Dataset>();
	    datasetCache = null;
	} catch (IOException e) {
	    throw new DatasetException(e);
	}
    }

    private Dataset loadDataset(String datasetId) throws DatasetException {
	Dataset dataset = null;

	ZipFeatureDatasetReader reader = new ZipFeatureDatasetReader(
		Paths.get(getPath().toString(), datasetId).toFile());

	dataset = reader.read();

	return dataset;
    }

    private Dataset createDataset(Path path, FileType fileType, DatasetType datasetType) throws DatasetException {
	Dataset dataset = null;

	switch (datasetType) {
	case BI_ALLELIC_GENOTYPIC:
	    dataset = createBiAllelicGenotypicDataset(path, fileType);
	    break;
	case MULTI_ALLELIC_GENOTYPIC:
	    dataset = createMultiAllelicGenotypicDataset(path, fileType);
	    break;
	case PHENOTYPIC:
	    dataset = createPhenotypicDataset(path, fileType);

	    ZipFeatureDatasetWriter writer = new ZipFeatureDatasetWriter(
		    Paths.get(getPath().toString(), dataset.getUniqueIdentifier()).toFile());

	    writer.write(dataset);
	    break;
	default:
	    throw new IllegalArgumentException("Unknown dataset type : " + datasetType);
	}

	return dataset;
    }

    private Dataset createBiAllelicGenotypicDataset(Path path, FileType fileType) throws DatasetException {
	// TODO Auto-generated method stub
	return null;
    }

    private Dataset createMultiAllelicGenotypicDataset(Path path, FileType fileType) throws DatasetException {
	// TODO Auto-generated method stub
	return null;
    }

    private Dataset createPhenotypicDataset(Path path, FileType fileType) throws DatasetException {
	return ArrayFeatureDataset.readFeatureDatasetFromTextFile(path.toFile(), fileType);
    }

    private XStream createXStream() {
	XStream xstream = new XStream(new StaxDriver());

	xstream.setClassLoader(getClass().getClassLoader());

	return xstream;
    }
}
