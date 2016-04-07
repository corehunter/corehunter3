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

package org.corehunter.objectives.distance.measures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.corehunter.data.CoreHunterData;
import org.corehunter.exceptions.CoreHunterException;
import uno.informatics.data.Feature;
import uno.informatics.data.Scale;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.dataset.FeatureDataRow;

/**
 * @author Herman De Beukelaer, Guy Davenport
 */
public class GowerDistance extends CachedDistanceMeasure {

    private static final int BINARY_SCALE_TYPE = 0;
    private static final int DISCRETE_SCALE_TYPE = 1;
    private static final int RANGED_SCALE_TYPE = 2;

    // scale type and range cache
    private final Map<FeatureData, FeatureMetadata> cache;
    
    private class FeatureMetadata {
        
        private final int numFeatures;
        private final int[] scaleTypes;
        private final double[] ranges;

        public FeatureMetadata(int numFeatures, int[] scaleTypes, double[] ranges) {
            this.numFeatures = numFeatures;
            this.scaleTypes = scaleTypes;
            this.ranges = ranges;
        }

        public int getNumFeatures() {
            return numFeatures;
        }

        public int[] getScaleTypes() {
            return scaleTypes;
        }

        public double[] getRanges() {
            return ranges;
        }
        
    }
    
    public GowerDistance() {
        this(MissingValuesPolicy.FLOOR);
    }

    public GowerDistance(MissingValuesPolicy policy) {
        super(policy);
        cache = new HashMap<>();
    }
    
    @Override
    public double computeDistance(int idX, int idY, CoreHunterData data, MissingValuesPolicy missingValuesPolicy) {

        if(idX == idY){
            return 0.0;
        }
        
        FeatureData phenotypes = data.getPhenotypicData();
                
        if(phenotypes == null){
            throw new CoreHunterException("Phenotypes are required for Gower distance.");
        }
        
        // get feature data rows of both items
        FeatureDataRow rowX = phenotypes.getRow(idX);
        FeatureDataRow rowY = phenotypes.getRow(idY);
        
        // get cached scale types and ranges
        FeatureMetadata featureMetadata = getFeatureMetadata(phenotypes);
        int[] scaleTypes = featureMetadata.getScaleTypes();
        double[] ranges  = featureMetadata.getRanges();
        
        double distSum = 0.0;
        double weightSum = 0.0;
        for (int k = 0; k < featureMetadata.getNumFeatures(); k++) {
            
            double distance = distance(scaleTypes[k], ranges[k],
                                       rowX.getValue(k), rowY.getValue(k),
                                       missingValuesPolicy);
            double weight = weight(scaleTypes[k], rowX.getValue(k), rowY.getValue(k));
            distSum += distance * weight;
            weightSum += weight;
            
        }
        
        return distSum/weightSum;
        
    }
    
    private FeatureMetadata getFeatureMetadata(FeatureData data){
        // retrieve from cache (if present)
        FeatureMetadata metadata = cache.get(data);
        if(metadata == null){
            // infer scale types and ranges
            List<Feature> features = data.getFeatures();
            int numFeatures = features.size();
            int[] scaleTypes = new int[numFeatures];
            double[] ranges = new double[numFeatures];
            for(int k = 0; k < numFeatures; k++){
                Scale scale = features.get(k).getMethod().getScale();
                switch (scale.getScaleType()) {
                    case NOMINAL:
                        switch (scale.getDataType()) {
                            case BOOLEAN:
                                scaleTypes[k] = BINARY_SCALE_TYPE; // assymetric binary
                                break;
                            default:
                                scaleTypes[k] = DISCRETE_SCALE_TYPE; // default nominal
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
                                scaleTypes[k] = RANGED_SCALE_TYPE;
                                ranges[k] = scale.getMaximumValue().doubleValue()
                                          - scale.getMinimumValue().doubleValue();
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
            }
            // combine in metadata and store in cache
            metadata = new FeatureMetadata(numFeatures, scaleTypes, ranges);
            cache.put(data, metadata);
        }
        return metadata;
    }
    
    private double distance(int scaleType, double range,
                            Object elementA, Object elementB,
                            MissingValuesPolicy missingValuesPolicy) {
        if (elementA != null && elementB != null) {
            switch (scaleType) {
                case BINARY_SCALE_TYPE:
                    if ((Boolean) elementA && (Boolean) elementB) {
                        return 0.0;
                    } else {
                        return 1.0;
                    }
                case DISCRETE_SCALE_TYPE:
                    if (Objects.equals(elementA, elementB)) {
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
        return missingValueContribution(missingValuesPolicy, 1.0);
    }

    private double weight(int scaleType, Object elementA, Object elementB) {
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
                            "This should not happen: unexpected scale type " + scaleType + " in Gower distance."
                    );
            }
        }
        return 1.0;
    }

}
