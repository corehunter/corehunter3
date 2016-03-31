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

package org.corehunter.objectives.distance;

import org.apache.commons.lang3.ObjectUtils;
import org.corehunter.data.PhenotypicTraitData;
import org.corehunter.data.simple.CoreHunterData;
import org.corehunter.exceptions.CoreHunterException;
import uno.informatics.data.Feature;
import uno.informatics.data.FeatureDataset;
import uno.informatics.data.Scale;

/**
 * @author Herman De Beukelaer, Guy Davenport
 */
public class GowerDistance implements DistanceMeasure {

    private static final int BINARY_SCALE_TYPE = 0;
    private static final int DISCRETE_SCALE_TYPE = 1;
    private static final int RANGED_SCALE_TYPE = 2;
    
    // TODO cache (also scale types and ranges)
    @Override
    public double getDistance(int idX, int idY, CoreHunterData data) {

        PhenotypicTraitData phenotypes = data.getPhenotypes();
                
        if(phenotypes == null){
            throw new CoreHunterException("Phenotypes are required for Gower distance.");
        }
        
        FeatureDataset featureData = phenotypes.getData();
        Object[][] values = featureData.getValuesAsArray();
        Feature[] features = featureData.getFeaturesAsArray();
        
        double distSum = 0.0;
        double weightSum = 0.0;
        for (int k = 0; k < features.length; k++) {
            
            Scale scale = features[k].getMethod().getScale();
            int scaleType = -1;
            double range = 0.0;
            
            switch (scale.getScaleType()) {
                case NOMINAL:
                    switch (scale.getDataType()) {
                        case BOOLEAN:
                            scaleType = BINARY_SCALE_TYPE; // assymetric binary
                            break;
                        default:
                            scaleType = DISCRETE_SCALE_TYPE; // default nominal
                    }
                    break;
                case INTERVAL:
                case ORDINAL:
                case RATIO:
                    switch (scale.getDataType()) {
                        case BIG_DECIMAL:
                        case BIG_INTEGER:
                        case DOUBLE:
                        case FLOAT:
                        case INTEGER:
                        case LONG:
                        case SHORT:
                            scaleType = RANGED_SCALE_TYPE;
                            range = scale.getMaximumValue().doubleValue() - scale.getMinimumValue().doubleValue();
                            break;
                        case BOOLEAN:
                        case DATE:
                        case STRING:
                        case UNKNOWN:
                        default:
                            throw new IllegalArgumentException("Illegal data type " + scale.getDataType()
                                                             + " for scale type " + scale.getScaleType());
                    }
                    break;
                case NONE:
                default:
                    throw new IllegalArgumentException("Illegal scale type: " + scale.getScaleType());
            }
            
            double distance = distance(scaleType, range, values[idX][k], values[idY][k]);
            double weight = weight(scaleType, range, values[idX][k], values[idY][k]);
            distSum += distance * weight;
            weightSum += weight;
            
        }
        
        return distSum/weightSum;

    }
    
    // TODO review treatment of missing data
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
                            "This should not happen: unexpected scale type " + scaleType + " in Gower distance."
                    );
            }
        }
        return 0.0;
    }

    // TODO review treatment of missing data
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
