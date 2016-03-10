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

import org.corehunter.objectives.distance.AbstractGenotypeVariantDistanceMetric;
import org.corehunter.data.GenotypeVariantData;

/**
 * @author Guy Davenport
 */
public class ModifiedRogersDistanceMultiAllelic extends
        AbstractGenotypeVariantDistanceMetric<GenotypeVariantData> {

    public ModifiedRogersDistanceMultiAllelic(GenotypeVariantData data) {
        super(data);
    }

    /*
     * (non-Javadoc)
     * @see org.corehunter.DistanceMatrixData#getDistance(int, int)
     */
    @Override
    public double getDistance(int idX, int idY) {
        
        double distance;

        int numberOfMarkers = getData().getNumberOfMarkers();
        int numberOfAlleles;

        double markerSqDiff = 0;
        double sumMarkerSqDiff = 0;

        for (int markerIndex = 0; markerIndex < numberOfMarkers; ++markerIndex) {
            numberOfAlleles = getData().getNumberOfAlleles(markerIndex);

            for (int alleleIndex = 0; alleleIndex < numberOfAlleles; ++alleleIndex) {
                double pxla = getData().getAlleleFrequency(idX, markerIndex, alleleIndex);
                double pyla = getData().getAlleleFrequency(idY, markerIndex, alleleIndex);

                if (pxla >= 0 && pyla >= 0) {
                    markerSqDiff += (pxla - pyla) * (pxla - pyla);
                }
            }

            sumMarkerSqDiff += markerSqDiff;
        }

        distance = 1.0 / (Math.sqrt(2.0 * numberOfMarkers))
                * Math.sqrt(sumMarkerSqDiff);

        return distance;
    }
}
