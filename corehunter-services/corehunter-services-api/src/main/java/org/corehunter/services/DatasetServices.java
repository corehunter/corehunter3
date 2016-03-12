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

package org.corehunter.services;

import java.nio.file.Path;
import java.util.List;

import uno.informatics.common.io.FileType;
import uno.informatics.data.Dataset;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.DatasetException;

public interface DatasetServices {
    public List<SimpleEntity> getDatasetDescriptions();

    public List<Dataset> getAllDatasets() throws DatasetException;

    public Dataset getDataset(String datasetId) throws DatasetException;

    public String addDataset(Path path, FileType fileType, DatasetType datasetType) throws DatasetException;

    public boolean removeDataset(String datasetId) throws DatasetException;

}
