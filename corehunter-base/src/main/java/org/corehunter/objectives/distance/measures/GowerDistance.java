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
public class GowerDistance extends AbstractDistanceMeasure {

    private static final int BINARY = 0;
    private static final int NOMINAL = 1;
    private static final int ORDINAL = 2;
    private static final int RANGED = 3;

    // scale type and range cache
    private final Map<FeatureData, FeatureMetadata> cache = new HashMap<>();
    
    private class FeatureMetadata {
        
        private final int[] scaleTypes;
        private final Scale[] scales;
        private final double[] ranges;

        public FeatureMetadata(int[] gowerScaleTypes, Scale[] scaleTypes, double[] ranges) {
            this.scaleTypes = gowerScaleTypes;
            this.scales = scaleTypes;
            this.ranges = ranges;
        }

        public int getNumFeatures() {
            return scaleTypes.length;
        }

        public int[] getScaleTypes() {
            return scaleTypes;
        }
        
        public Scale[] getScales() {
            return scales;
        }

        public double[] getRanges() {
            return ranges;
        }
        
    }
    
    @Override
    public double computeDistance(int idX, int idY, CoreHunterData data) {

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
        Scale[] scales = featureMetadata.getScales();
        double[] ranges  = featureMetadata.getRanges();
        
        double distSum = 0.0;
        double weightSum = 0.0;
        for (int k = 0; k < featureMetadata.getNumFeatures(); k++) {
            
            double distance = distance(scaleTypes[k], scales[k], ranges[k], rowX.getValue(k), rowY.getValue(k));
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
            Scale[] scales = new Scale[numFeatures];
            double[] ranges = new double[numFeatures];
            for(int k = 0; k < numFeatures; k++){
                Scale scale = features.get(k).getMethod().getScale();
                scales[k] = scale;
                switch (scale.getScaleType()) {
                    case NOMINAL:
                        switch (scale.getDataType()) {
                            case BOOLEAN:
                                scaleTypes[k] = BINARY; // assymetric binary
                                break;
                            default:
                                scaleTypes[k] = NOMINAL; // default nominal
                        }
                        break;
                    case ORDINAL:
                        scaleTypes[k] = ORDINAL;
                        if(scale.getValues().isEmpty()){
                            throw new IllegalArgumentException(
                                    "Ordered list of possible values should be provided for scale type "
                                    + scale.getScaleType() + "."
                            );
                        }
                        ranges[k] = scale.getValues().size()-1;
                        break;
                    case INTERVAL:
                    case RATIO:
                        switch (scale.getDataType()) {
                            case BIG_DECIMAL:
                            case BIG_INTEGER:
                            case DOUBLE:
                            case FLOAT:
                            case INTEGER:
                            case LONG:
                            case SHORT:
                                scaleTypes[k] = RANGED;
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
            metadata = new FeatureMetadata(scaleTypes, scales, ranges);
            cache.put(data, metadata);
        }
        return metadata;
    }
    
    private double distance(int scaleType, Scale scale, double range, Object elementA, Object elementB) {
        if (elementA != null && elementB != null) {
            switch (scaleType) {
                case BINARY:
                    if ((Boolean) elementA && (Boolean) elementB) {
                        return 0.0;
                    } else {
                        return 1.0;
                    }
                case ORDINAL:
                    if(range > 0.0){
                        // convert values to indices in list of possible values
                        int indexA = scale.indexOf(elementA);
                        int indexB = scale.indexOf(elementB);
                        // treat indices as interval variables
                        return Math.abs(indexA - indexB) / range;
                    } else {
                        return 0.0;
                    }
                case NOMINAL:
                    if (Objects.equals(elementA, elementB)) {
                        return 0.0;
                    } else {
                        return 1.0;
                    }
                case RANGED:
                    if(range > 0.0){
                        double aValue = ((Number) elementA).doubleValue();
                        double bValue = ((Number) elementB).doubleValue();
                        return Math.abs(aValue - bValue) / range;
                    } else {
                        return 0.0;
                    }
                default:
                    throw new RuntimeException(
                            "This should not happen: unexpected scale type " + scaleType + " in Gower distance."
                    );
            }
        }
        return missingValueContribution(1.0);
    }

    private double weight(int scaleType, Object elementA, Object elementB) {
        if (elementA != null && elementB != null) {
            switch (scaleType) {
                case BINARY:
                    if ((Boolean) elementA || (Boolean) elementB) {
                        return 1.0;
                    } else {
                        return 0.0;
                    }
                case ORDINAL:
                case NOMINAL:
                case RANGED:
                    return 1.0;
                default:
                    throw new RuntimeException(
                            "This should not happen: unexpected scale type " + scaleType + " in Gower distance."
                    );
            }
        }
        return 1.0;
    }
    
    @Override
    public String toString(){
        return "Gower";
    }

}
