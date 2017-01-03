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

package org.corehunter.data.simple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.data.PhenotypeData;
import org.jamesframework.core.subset.SubsetSolution;

import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowWriter;
import uno.informatics.common.io.text.TextFileRowReader;
import uno.informatics.data.Feature;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.FeatureDataRow;
import uno.informatics.data.feature.array.ArrayFeatureData;
import uno.informatics.data.io.FileType;
import uno.informatics.data.utils.DataOption;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class SimplePhenotypeData extends ArrayFeatureData implements PhenotypeData {

    private static final String ID_HEADER = "X";
    private static final String SELECTED_HEADER = "SELECTED";

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SimplePhenotypeData(String name, Feature[] features, Object[][] values) {
        super(name, features, values);
    }

    public SimplePhenotypeData(String name, List<? extends Feature> features, List<List<Object>> values) {
        super(name, features, values);
    }

    public SimplePhenotypeData(String name, List<? extends Feature> features, List<SimpleEntity> rowHeaders,
        List<List<Object>> values) {
        super(name, features, rowHeaders, values);
    }

    public SimplePhenotypeData(String name, List<? extends Feature> features, SimpleEntity[] rowHeaders,
        Object[][] values) {
        super(name, features, rowHeaders, values);
    }

    public SimplePhenotypeData(String uniqueIdentifier, String name, Feature[] features, Object[][] values) {
        super(uniqueIdentifier, name, features, values);
    }

    public SimplePhenotypeData(String uniqueIdentifier, String name, List<? extends Feature> features,
        List<List<Object>> values) {
        super(uniqueIdentifier, name, features, values);
    }

    public SimplePhenotypeData(String uniqueIdentifier, String name, List<? extends Feature> features,
        List<SimpleEntity> rowHeaders, List<List<Object>> values) {
        super(uniqueIdentifier, name, features, rowHeaders, values);
    }

    public SimplePhenotypeData(String uniqueIdentifier, String name, List<? extends Feature> features,
        SimpleEntity[] rowHeaders, Object[][] values) {
        super(uniqueIdentifier, name, features, rowHeaders, values);
    }

    public SimplePhenotypeData(ArrayFeatureData data) {
        super(data.getUniqueIdentifier(), data.getName(), data.getFeatures(), data.getRowHeaders(),
            data.getValues());
    }

    /**
     * Write genotype data to file in the chosen format. By default the only
     * supported format is {@link GenotypeDataFormat#FREQUENCY} but the method
     * may be overridden in subclasses to support other formats. Only file types
     * {@link FileType#TXT} and {@link FileType#CSV} are allowed.
     * 
     * @param filePath
     *            path to file where the data will be written
     * @param fileType
     *            the type of data file
     * @param solution
     *            the solution to subset the data
     * @param includeId
     *            includes the id used by the solution
     * @param includeSelected
     *            includes selected
     * @param includeUnselected
     *            includes unselected
     * @throws IOException
     *             if the file can not be written
     */
    public void writeData(Path filePath, FileType fileType, SubsetSolution solution, boolean includeId,
        boolean includeSelected, boolean includeUnselected) throws IOException {

        // validate arguments

        if (filePath == null) {
            throw new IllegalArgumentException("File path not defined.");
        }

        if (filePath.toFile().exists()) {
            throw new IOException("File does  exist : " + filePath + ".");
        }

        if (fileType == null) {
            throw new IllegalArgumentException("File type not defined.");
        }

        if (fileType != FileType.TXT && fileType != FileType.CSV) {
            throw new IllegalArgumentException(
                String.format("Only file types TXT and CSV are supported. Got: %s.", fileType));
        }

        if (solution == null) {
            throw new NullPointerException("Solution must be defined");
        }

        if (solution.getTotalNumIDs() != getSize()) {
            throw new IllegalArgumentException("Solution size must match data size");
        }

        if (!includeSelected && !includeUnselected) {
            throw new IllegalArgumentException("One of includeSelected or includeUnselected must be used");
        }

        Files.createDirectories(filePath.getParent());

        List<Integer> ids = new ArrayList<Integer>(getIDs());
        List<Integer> allIDs = new ArrayList<Integer>(solution.getAllIDs());
        Set<Integer> selectedIDs = new TreeSet<Integer>(solution.getSelectedIDs());

        RowWriter writer = IOUtilities.createRowWriter(filePath, fileType, TextFileRowReader.ROWS_SAME_SIZE,
            TextFileRowReader.REMOVE_WHITE_SPACE);

        // write header row
        if (includeId) {
            writer.writeCell(ID_HEADER);
            writer.newColumn();
        }

        writer.writeCell(ID);
        writer.newColumn();
        writer.writeCell(NAME);

        Iterator<Feature> iterator = getFeatures().iterator();

        Feature feature;

        while (iterator.hasNext()) {
            writer.newColumn();
            feature = iterator.next();
            writer.writeCell(feature.getUniqueIdentifier());
        }

        if (includeSelected && includeUnselected) {
            writer.newColumn();
            writer.writeCell(SELECTED_HEADER);
        }

        writer.newRow();

        if (includeId) {
            writer.newColumn();
        }

        writer.writeCell(NAME);
        writer.newColumn();

        iterator = getFeatures().iterator();

        while (iterator.hasNext()) {
            writer.newColumn();
            feature = iterator.next();
            writer.writeCell(feature.getName());
        }

        if (includeSelected && includeUnselected) {
            writer.newColumn();
        }

        writer.newRow();

        if (includeId) {
            writer.newColumn();
        }

        writer.writeCell(TYPE);
        writer.newColumn();

        iterator = getFeatures().iterator();

        while (iterator.hasNext()) {
            writer.newColumn();
            feature = iterator.next();
            writer.writeCell(feature.getMethod().getScale().getScaleType().getAbbreviation()
                + feature.getMethod().getScale().getDataType().getAbbreviation());
        }

        if (includeSelected && includeUnselected) {
            writer.newColumn();
        }

        writer.newRow();

        if (includeId) {
            writer.newColumn();
        }

        writer.writeCell(MIN);
        writer.newColumn();

        iterator = getFeatures().iterator();

        while (iterator.hasNext()) {
            writer.newColumn();
            feature = iterator.next();
            writer.writeCell(feature.getMethod().getScale().getMinimumValue());
        }

        if (includeSelected && includeUnselected) {
            writer.newColumn();
        }

        writer.newRow();

        if (includeId) {
            writer.newColumn();
        }

        writer.writeCell(MAX);
        writer.newColumn();

        iterator = getFeatures().iterator();

        while (iterator.hasNext()) {
            writer.newColumn();
            feature = iterator.next();
            writer.writeCell(feature.getMethod().getScale().getMaximumValue());
        }

        FeatureDataRow row;

        if (includeSelected && includeUnselected) {
            writer.newColumn();
        }

        SimpleEntity header;

        Iterator<Integer> iterator2 = allIDs.iterator();

        if (includeSelected && includeUnselected) {
            iterator2 = allIDs.iterator();
        } else {
            if (includeSelected) {
                iterator2 = solution.getSelectedIDs().iterator();
            } else {
                if (includeUnselected) {
                    iterator2 = solution.getUnselectedIDs().iterator();
                }
            }
        }

        int i = 0;
        Integer id;

        while (iterator2.hasNext()) {

            id = iterator2.next();
            i = ids.indexOf(id);

            writer.newRow();

            if (includeId) {
                writer.writeCell(id);
                writer.newColumn();
            }

            header = getHeader(i);

            row = getRow(i);
            writer.writeCell(header.getUniqueIdentifier());
            writer.newColumn();
            writer.writeCell(header.getName());
            writer.newColumn();
            writer.writeRowCells(row.getValues());

            if (includeSelected && includeUnselected) {
                writer.newColumn();
                writer.writeCell(selectedIDs.contains(id));
            }
        }

        writer.close();
    }

    public static final SimplePhenotypeData readPhenotypeData(Path filePath, FileType type,
        DataOption... options) throws IOException {
        return new SimplePhenotypeData(ArrayFeatureData.readData(filePath, type, options));
    }
}
