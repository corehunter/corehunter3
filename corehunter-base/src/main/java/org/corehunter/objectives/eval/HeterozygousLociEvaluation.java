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

package org.corehunter.objectives.eval;

import java.util.Collection;
import java.util.Set;
import org.corehunter.data.GenotypeData;

/**
 * @author Herman De Beukelaer
 */
public class HeterozygousLociEvaluation extends AllelicDiversityEvaluation {
    
    public HeterozygousLociEvaluation(Collection<Integer> ids, GenotypeData data) {
        super(ids, data);
    }

    public HeterozygousLociEvaluation(AllelicDiversityEvaluation curEval,
                              Set<Integer> add, Set<Integer> remove,
                              GenotypeData data) {
        super(curEval, add, remove, data);
    }
    
    @Override
    public double getValue() {
        if(getNumSelected() == 0){
            // empty selection
            return 0.0;
        } else {
            double[][] avgGeno = getAverageGenotype();
            // compute expected proportion of heterozygous loci in offspring
            double total = 0.0;
            int numberOfMarkers = avgGeno.length;
            for (int m = 0; m < numberOfMarkers; m++) {
                int numberOfAlleles = avgGeno[m].length;
                double summedAverageAlleleFrequencySquared = 0.0;
                for (int a = 0; a < numberOfAlleles; a++) {
                    summedAverageAlleleFrequencySquared += avgGeno[m][a] * avgGeno[m][a];
                }
                total += (1.0 - summedAverageAlleleFrequencySquared);
            }
            return total/numberOfMarkers;
        }
    }

}