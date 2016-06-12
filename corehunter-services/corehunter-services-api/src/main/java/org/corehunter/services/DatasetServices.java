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

package org.corehunter.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.corehunter.data.CoreHunterData;

import uno.informatics.data.Data;
import uno.informatics.data.Dataset;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.dataset.MatrixData;
import uno.informatics.data.io.FileType;

public interface DatasetServices {

    /**
     * Gets all databases
     * 
     * @return all databases
     */
    public List<Dataset> getAllDatasets();

    /**
     * Gets a single dataset by unique dataset identifier
     * 
     * @param datasetId
     *            the identifier of the dataset
     * @return a single dataset by unique dataset identifier
     */
    public Dataset getDataset(String datasetId);
    
    /**
     * Add a new dataset
     * 
     * @param dataset
     *            the dataset to be added
     * @throws DatasetException
     *             if the dataset is invalid or is already present
     */
    public void addDataset(Dataset dataset) throws DatasetException;

    /**
     * Removes a dataset and all associated data
     * 
     * @param datasetId
     *            the identifier of the dataset to be removed
     * @return <code>true</code> if the dataset was present and was removed,
     *         <code>false</code> otherwise
     * @throws DatasetException
     *             if the dataset does not exist
     */
    public boolean removeDataset(String datasetId) throws DatasetException;

    /**
     * Gets the data associated with a dataset by unique dataset identifier
     * 
     * @param datasetId
     *            the identifier of the dataset
     * @return the data associated with a dataset by unique dataset identifier
     * @throws DatasetException
     *             if the data can not be accessed or the dataset does not exist
     */
    public CoreHunterData getCoreHunterData(String datasetId) throws DatasetException;
    
    /**
     * Loads the data and associates it with a dataset. If the dataset already
     * has data associated with it, an attempt will be made to add to the
     * dataset merging with existing data. 
     * 
     * <p>Data loading options can include the Genotype Data Format
     * (see {@link org.corehunter.data.GenotypeDataFormat}) for {@link DataType#GENOTYPIC} 
     * 
     * @param dataset
     *            the dataset to which the data will be associated
     * @param path
     *            a path where the data file can be found
     * @param fileType
     *            the type of file from which the data will be loaded
     * @param dataType
     *            the type of data
     * @param options
     *            the data loading options. 
     * @throws IOException
     *             if the is an issue with reading the data from the path
     * @throws DatasetException
     *             if the data can not be merged with existing data for a
     *             dataset
     */
    public void loadData(Dataset dataset, Path path, FileType fileType, DataType dataType, 
            Object... options) throws IOException, DatasetException;
    
    /**
     * Removes the all the data associated with a dataset
     *      
     * @param datasetId
     *            the identifier of the dataset for which all data will be removed
     * @throws DatasetException
     *             if the data can not be accessed or the dataset does not exist
     */
    public void removeData(String datasetId) throws DatasetException;
    
    /**
     * Gets the original data upload by the user
     * @param datasetId
     *            the identifier of the dataset
     * @param dataType
     *            the type of data
     * @return the original data upload by the user
     * @throws DatasetException if the data can not be accessed or the dataset does not exist
     */
    public Data getOriginalData(String datasetId, DataType dataType) throws DatasetException ;

}
