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
import org.corehunter.data.GenotypeVariantData;

/**
 * @author Herman De Beukelaer
 */
public class CoverageEvaluation extends AllelicDiversityEvaluation {

    private static final double TOL = 1e-10;
    
    public CoverageEvaluation(Collection<Integer> ids, GenotypeVariantData data) {
        super(ids, data);
    }

    public CoverageEvaluation(AllelicDiversityEvaluation curEval,
                              Set<Integer> add, Set<Integer> remove,
                              GenotypeVariantData data) {
        super(curEval, add, remove, data);
    }
    
    @Override
    public double getValue() {
        double[][] avgGeno = getAverageGenotype();
        
        int numberOfMarkers = avgGeno.length;
        int totalNumberOfAlleles = 0;
        int alleleCount = 0;
        for(int m = 0; m < numberOfMarkers; m++){
            int numberOfAlleles = avgGeno[m].length;
            totalNumberOfAlleles += numberOfAlleles;
            for(int a = 0; a < numberOfAlleles; a++){
                if(avgGeno[m][a] > TOL){
                    alleleCount++;
                }
            }
        }

        return ((double) alleleCount) / totalNumberOfAlleles;
    }

}
