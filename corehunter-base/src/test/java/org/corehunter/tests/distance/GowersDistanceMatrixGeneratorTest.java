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

package org.corehunter.tests.distance;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.matrix.SymmetricMatrixFormat;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.distance.GowersDistanceMatrixGenerator;
import org.junit.Test;

import uno.informatics.common.io.FileType;
import uno.informatics.data.DataType;
import uno.informatics.data.Feature;
import uno.informatics.data.ScaleType;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.feature.array.ArrayFeatureData;
import uno.informatics.data.pojo.SimpleFeaturePojo;

import static org.junit.Assert.assertEquals;

/**
 * @author Guy Davenport
 */
public class GowersDistanceMatrixGeneratorTest {

    private static final String DATA_FILE = "/phenotypes/phenotypic_data.csv";
    private static final String MATRIX_FILE = "/phenotypes/matrix.csv";

    private static final Object[][] DATA = {
        {"row1", 1, 1.0, "1", true},
        {"row2", 2, 3.0, "2", true},
        {"row3", 3, 3.0, "1", false},
        {"row4", 4, 5.0, "2", false},
        {"row5", 5, 4.0, "1", true}
    };

    private static final Feature[] FEATURES = new Feature[]{
        new SimpleFeaturePojo("feature1", "feature1", DataType.INTEGER, ScaleType.INTERVAL, 0, 5),
        new SimpleFeaturePojo("feature2", "feature2", DataType.DOUBLE, ScaleType.RATIO, 0.0, 5.0),
        new SimpleFeaturePojo("feature3", "feature3", DataType.STRING, ScaleType.NOMINAL),
        new SimpleFeaturePojo("feature4", "feature4", DataType.BOOLEAN, ScaleType.NOMINAL)
    };

    private static final double[][] MATRIX = new double[][]{
        new double[]{0.00, 0.40, 0.45, 0.85, 0.35},
        new double[]{0.40, 0.00, 0.55, 0.45, 0.45},
        new double[]{0.45, 0.55, 0.00, 0.65, 0.40},
        new double[]{0.85, 0.45, 0.65, 0.00, 0.60},
        new double[]{0.35, 0.45, 0.40, 0.60, 0.00}
    };

    private static final double DELTA = 1e-8;
    private static final String NAME = "Name";

    @Test
    public void testGenerateDistanceMatrix() {
        GowersDistanceMatrixGenerator generator = new GowersDistanceMatrixGenerator(new ArrayFeatureData(NAME, FEATURES, DATA));

        DistanceMatrixData matrix = generator.generateDistanceMatrix();

        Iterator<Integer> iterator1 = matrix.getIDs().iterator();
        Iterator<Integer> iterator2 = matrix.getIDs().iterator();

        int index1;
        int index2;

        while (iterator1.hasNext()) {
            index1 = iterator1.next();

            while (iterator2.hasNext()) {
                index2 = iterator2.next();
                assertEquals("cell (" + index1 + "," + index2 + ")",
                             MATRIX[index1][index2], matrix.getDistance(index1, index2), 0.0000000001);
            }
        }
    }
    
    @Test
    public void testGenerateDistanceMatrixFromFile() throws IOException, DatasetException, URISyntaxException {

        FeatureData data = ArrayFeatureData.readData(
                Paths.get(GowersDistanceMatrixGeneratorTest.class.getResource(DATA_FILE).toURI()),
                FileType.CSV
        );

        GowersDistanceMatrixGenerator generator = new GowersDistanceMatrixGenerator(data);

        DistanceMatrixData distances = generator.generateDistanceMatrix();
        DistanceMatrixData expected = SimpleDistanceMatrixData.readData(
                Paths.get(GowersDistanceMatrixGeneratorTest.class.getResource(MATRIX_FILE).getPath()),
                FileType.CSV, SymmetricMatrixFormat.FULL
        );

        int n = data.getRowCount();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                assertEquals(
                        "x=" + x + " y=" + y,
                        expected.getDistance(x, y),
                        distances.getDistance(x, y),
                        DELTA
                );
            }
        }
        
    }
}
