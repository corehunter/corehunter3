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

package org.corehunter.tests.objectives.distance.measures;

import static org.corehunter.tests.TestData.PRECISION;

import java.io.IOException;
import java.nio.file.Paths;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.matrix.SymmetricMatrixFormat;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.objectives.distance.measures.GowerDistance;
import org.corehunter.objectives.distance.measures.MissingValuesPolicy;

import static org.corehunter.tests.TestData.GOWER_DISTANCES_MISSING_VALUES_CEIL;
import static org.corehunter.tests.TestData.GOWER_DISTANCES_MISSING_VALUES_FLOOR;

import uno.informatics.common.io.FileType;
import uno.informatics.data.DataType;
import uno.informatics.data.Feature;
import uno.informatics.data.ScaleType;
import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.feature.array.ArrayFeatureData;
import uno.informatics.data.pojo.SimpleFeaturePojo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class GowerDistanceTest {

    private static final String DATA_FILE = "/phenotypes/phenotypic_data.csv";
    private static final String MATRIX_FILE = "/phenotypes/matrix.csv";
    
    private static final String DATA_FILE_MISSING_VALUES = "/phenotypes/missing-values.csv";

    private static final Object[][] DATA = {
        {"row1", 1, 1.0, "1", true},
        {"row2", 2, 3.0, "2", true},
        {"row3", 3, 3.0, "1", false},
        {"row4", 4, 5.0, "2", false},
        {"row5", 5, 4.0, "1", true}
    };

    private static final Feature[] FEATURES = {
        new SimpleFeaturePojo("feature1", DataType.INTEGER, ScaleType.INTERVAL, 0, 5),
        new SimpleFeaturePojo("feature2", DataType.DOUBLE, ScaleType.RATIO, 0.0, 5.0),
        new SimpleFeaturePojo("feature3", DataType.STRING, ScaleType.NOMINAL),
        new SimpleFeaturePojo("feature4", DataType.BOOLEAN, ScaleType.NOMINAL)
    };

    private static final double[][] MATRIX = new double[][]{
        new double[]{0.00, 0.40, 0.45, 0.85, 0.35},
        new double[]{0.40, 0.00, 0.55, 0.45, 0.45},
        new double[]{0.45, 0.55, 0.00, 0.5333333, 0.40},
        new double[]{0.85, 0.45, 0.5333333, 0.00, 0.60},
        new double[]{0.35, 0.45, 0.40, 0.60, 0.00}
    };

    @Test
    public void testInMemory() {

        FeatureData pheno = new ArrayFeatureData("in-memory", FEATURES, DATA);
        CoreHunterData data = new CoreHunterData(pheno);
        
        GowerDistance distanceMetric = new GowerDistance();

        int n = data.getSize();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                assertEquals(
                        "x=" + x + " y=" + y,
                        MATRIX[x][y],
                        distanceMetric.getDistance(x, y, data),
                        1e-7
                );
            }
        }
    }

    @Test
    public void testFromFile() throws IOException, DatasetException {
        
        FeatureData pheno = ArrayFeatureData.readData(
                Paths.get(GowerDistanceTest.class.getResource(DATA_FILE).getPath()),
                FileType.CSV
        );
        CoreHunterData data = new CoreHunterData(pheno);

        DistanceMatrixData expected = SimpleDistanceMatrixData.readData(
                Paths.get(GowerDistanceTest.class.getResource(MATRIX_FILE).getPath()),
                FileType.CSV, SymmetricMatrixFormat.FULL
        );
        
        GowerDistance distanceMetric = new GowerDistance();

        int n = data.getSize();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                assertEquals(
                        "x=" + x + " y=" + y,
                        expected.getDistance(x, y),
                        distanceMetric.getDistance(x, y, data),
                        PRECISION
                );
            }
        }

    }
    
    @Test
    public void testFromFileWithMissingValues() throws IOException, DatasetException {
        
        FeatureData pheno = ArrayFeatureData.readData(
                Paths.get(GowerDistanceTest.class.getResource(DATA_FILE_MISSING_VALUES).getPath()),
                FileType.CSV
        );
        CoreHunterData data = new CoreHunterData(pheno);
        
        GowerDistance distanceMetric;
        int n = data.getSize();
        
        // test with missing data policy FLOOR
        distanceMetric = new GowerDistance(MissingValuesPolicy.FLOOR);
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                assertEquals(
                        "x=" + x + " y=" + y,
                        GOWER_DISTANCES_MISSING_VALUES_FLOOR[x][y],
                        distanceMetric.getDistance(x, y, data),
                        PRECISION
                );
            }
        }
        
        // test with missing data policy CEIL
        distanceMetric = new GowerDistance(MissingValuesPolicy.CEIL);
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                assertEquals(
                        "x=" + x + " y=" + y,
                        GOWER_DISTANCES_MISSING_VALUES_CEIL[x][y],
                        distanceMetric.getDistance(x, y, data),
                        PRECISION
                );
            }
        }

    }
    
}
