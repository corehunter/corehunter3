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

package org.corehunter.objectives;

import java.util.Collection;
import org.corehunter.data.GenotypeVariantData;

/**
 * @author Herman De Beukelaer
 */
public class AllelicDiversity {

    public double[][] getAverageGenotype(GenotypeVariantData data, Collection<Integer> ids){
        
        // infer average genotype (missing values treated as zero)
        double[][] geno = data.getAverageGenotype(ids);
        
        // handle missing values by increasing the highest frequency per marker so that
        // its allele frequencies sum to one (for markers with missing values only)
        for (int m = 0; m < geno.length; m++) {
            double[] alleleFreqs = geno[m];
            if(hasMissingValues(data, ids, m)){
                // find and increase highest frequency
                int mostCommonAllele = -1;
                double mostCommonAlleleFreq = 0.0;
                double freqSum = 0.0;
                for (int a = 0; a < alleleFreqs.length; a++) {
                    freqSum += alleleFreqs[a];
                    if (alleleFreqs[a] > mostCommonAlleleFreq) {
                        mostCommonAllele = a;
                        mostCommonAlleleFreq = alleleFreqs[a];
                    }
                }
                alleleFreqs[mostCommonAllele] += (1.0 - freqSum); 
            }
        }
        
        return geno;
        
    }
    
    private boolean hasMissingValues(GenotypeVariantData data, Collection<Integer> ids, int markerIndex){
        for(int id : ids){
            if(data.hasMissingValues(id, markerIndex)){
                return true;
            }
        }
        return false;
    }
    
}
