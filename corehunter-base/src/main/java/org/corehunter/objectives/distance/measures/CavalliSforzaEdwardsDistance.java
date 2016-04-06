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

import org.corehunter.data.GenotypeVariantData;
import org.corehunter.data.CoreHunterData;
import org.corehunter.exceptions.CoreHunterException;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class CavalliSforzaEdwardsDistance extends CachedDistanceMeasure {

    public CavalliSforzaEdwardsDistance() {
    }

    public CavalliSforzaEdwardsDistance(MissingValuesPolicy policy) {
        super(policy);
    }
    
    @Override
    public double computeDistance(int idX, int idY,
                                  CoreHunterData data,
                                  MissingValuesPolicy missingDataPolicy) {
        
        if(idX == idY){
            return 0.0;
        }
        
        GenotypeVariantData genotypes = data.getGenotypicData();
        
        if(genotypes == null){
            throw new CoreHunterException("Genotypes are required for Cavalli-Sforza and Edwards distance.");
        }
        
        int numberOfMarkers = genotypes.getNumberOfMarkers();
        double sumSquareDiff = 0.0;
        
        for (int markerIndex = 0; markerIndex < numberOfMarkers; ++markerIndex) {
            
            if(genotypes.hasMissingValues(idX, markerIndex) || genotypes.hasMissingValues(idY, markerIndex)){
                // missing frequencies in at least one individual
                sumSquareDiff += missingValueContribution(missingDataPolicy, 2.0);
            } else {
                // frequencies available for both individuals
                int numberOfAlleles = genotypes.getNumberOfAlleles(markerIndex);
                for (int alleleIndex = 0; alleleIndex < numberOfAlleles; ++alleleIndex) {
                    double pxla = genotypes.getAlleleFrequency(idX, markerIndex, alleleIndex);
                    double pyla = genotypes.getAlleleFrequency(idY, markerIndex, alleleIndex);
                    double diff = Math.sqrt(pxla) - Math.sqrt(pyla);
                    sumSquareDiff += diff * diff;
                }
            }
            
        }

        double distance = Math.sqrt(sumSquareDiff / (2*numberOfMarkers));

        return distance;
    }

}
