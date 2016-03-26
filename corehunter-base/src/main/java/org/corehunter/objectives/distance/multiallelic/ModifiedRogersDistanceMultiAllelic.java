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

package org.corehunter.objectives.distance.multiallelic;

import org.corehunter.data.GenotypeVariantData;
import org.corehunter.objectives.distance.AbstractGenotypeVariantDistanceMetric;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class ModifiedRogersDistanceMultiAllelic extends
        AbstractGenotypeVariantDistanceMetric<GenotypeVariantData> {

    public ModifiedRogersDistanceMultiAllelic(GenotypeVariantData data) {
        super(data);
    }
    
    @Override
    public double getDistance(int idX, int idY) {
        
        int numberOfMarkers = getData().getNumberOfMarkers();
        double sumSquareDiff = 0.0;
        
        for (int markerIndex = 0; markerIndex < numberOfMarkers; ++markerIndex) {
            
            int numberOfAlleles = getData().getNumberOfAlleles(markerIndex);
            for (int alleleIndex = 0; alleleIndex < numberOfAlleles; ++alleleIndex) {
                
                Double pxla = getData().getAlleleFrequency(idX, markerIndex, alleleIndex);
                Double pyla = getData().getAlleleFrequency(idY, markerIndex, alleleIndex);
                if (pxla != null && pyla != null) {
                    sumSquareDiff += (pxla - pyla) * (pxla - pyla);
                }
                
            }
            
        }

        double distance = Math.sqrt(sumSquareDiff / (2*numberOfMarkers));

        return distance;
        
    }
}
