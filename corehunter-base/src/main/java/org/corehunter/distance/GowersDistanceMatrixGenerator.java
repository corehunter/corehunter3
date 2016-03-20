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

package org.corehunter.distance;


import org.apache.commons.lang3.ObjectUtils;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;

import uno.informatics.data.Feature;
import uno.informatics.data.Scale;
import uno.informatics.data.dataset.FeatureData;

/**
 * @author Guy Davenport
 */
public class GowersDistanceMatrixGenerator implements DistanceMatrixGenerator {

    private static final int BINARY_SCALE_TYPE = 0;
    private static final int DISCRETE_SCALE_TYPE = 1;
    private static final int RANGED_SCALE_TYPE = 2;

    private Object[][] data;
    private Feature[] features;

    private Scale[] scales;
    private int[] scaleTypes;
    private double[] ranges;

    public GowersDistanceMatrixGenerator(FeatureData dataset) {
        this(dataset.getValuesAsArray(), dataset.getFeaturesAsArray());
    }

    public GowersDistanceMatrixGenerator(Object[][] data, Feature[] features) {
        if (data == null || features == null) {
            throw new IllegalArgumentException("Features and data must be defined!");
        }

        scales = new Scale[features.length];
        scaleTypes = new int[features.length];
        ranges = new double[features.length];

        for (int i = 0; i < features.length; ++i) {
            scales[i] = features[i].getMethod().getScale();

            switch (scales[i].getScaleType()) {
                case NOMINAL:
                    switch (scales[i].getDataType()) {
                        case BOOLEAN:
                            scaleTypes[i] = BINARY_SCALE_TYPE;
                            break;
                        default:
                            scaleTypes[i] = DISCRETE_SCALE_TYPE;
                            break;
                    }
                    break;
                case INTERVAL:
                case ORDINAL:
                case RATIO:
                    switch (scales[i].getDataType()) {
                        case BIG_DECIMAL:
                        case BIG_INTEGER:
                        case DOUBLE:
                        case FLOAT:
                        case INTEGER:
                        case LONG:
                        case SHORT:
                            ranges[i] = scales[i].getMaximumValue().doubleValue()
                                        - scales[i].getMinimumValue().doubleValue();
                            if (ranges[i] > 0) {
                                scaleTypes[i] = RANGED_SCALE_TYPE;
                            } else {
                                scaleTypes[i] = DISCRETE_SCALE_TYPE; // default to discrete 
                            }
                            break;
                        case BOOLEAN:
                        case DATE:
                        case STRING:
                        default:
                            throw new IllegalArgumentException("Illegal scale type : "
                                    + scales[i].getScaleType()
                                    + " for data type " + scales[i].getDataType());
                    }
                    break;
                case NONE:
                default:
                    throw new IllegalArgumentException("Illegal scale type : " + scales[i].getScaleType());
            }
        }

        this.data = data;
        this.features = features;
    }

    @Override
    public DistanceMatrixData generateDistanceMatrix() {
        int n = data.length;
        double[][] distances = new double[n][n];
        double[][] weights = new double[n][n];

        for (int i = 0; i < n; ++i) {
            if (data[i].length != features.length) {
                throw new IllegalArgumentException("Number of features must match number of elements in a row!");
            }

            double distance;
            double weight;

            for (int j = i; j < data.length; ++j) {
                for (int k = 0; k < features.length; ++k) {
                    distance = distance(scaleTypes[k], ranges[k], data[i][k], data[j][k]);
                    weight = weight(scaleTypes[k], ranges[k], data[i][k], data[j][k]);

                    distances[i][j] = distances[i][j] + (distance * weight);
                    weights[i][j] = weights[i][j] + weight;
                }

                distances[i][j] = distances[i][j] / weights[i][j];
                distances[j][i] = distances[i][j];
            }
        }

        return new SimpleDistanceMatrixData(distances);
    }

    private double distance(int scaleType, double range, Object elementA, Object elementB) {
        if (elementA != null && elementB != null) {
            switch (scaleType) {
                case BINARY_SCALE_TYPE:
                    if ((Boolean) elementA && (Boolean) elementB) {
                        return 0.0;
                    } else {
                        return 1.0;
                    }
                case DISCRETE_SCALE_TYPE:
                    if (ObjectUtils.equals(elementA, elementB)) {
                        return 0.0;
                    } else {
                        return 1.0;
                    }
                case RANGED_SCALE_TYPE:
                    double aValue = ((Number) elementA).doubleValue();
                    double bValue = ((Number) elementB).doubleValue();
                    return Math.abs(aValue - bValue) / range;
                default:
                    throw new RuntimeException(
                            "This should not happen: unexpected scale type in Gower distance matrix generator."
                    );
            }
        }
        return 0.0;
    }

    private double weight(int scaleType, double range, Object elementA, Object elementB) {
        if (elementA != null && elementB != null) {
            switch (scaleType) {
                case BINARY_SCALE_TYPE:
                    if ((Boolean) elementA || (Boolean) elementB) {
                        return 1.0;
                    } else {
                        return 0.0;
                    }
                case DISCRETE_SCALE_TYPE:
                case RANGED_SCALE_TYPE:
                    return 1.0;
                default:
                    throw new RuntimeException(
                            "This should not happen: unexpected scale type in Gower distance matrix generator."
                    );
            }
        }
        return 0.0;
    }
}
