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

import org.corehunter.data.GenotypeVariantData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.corehunter.data.CoreHunterData;
import org.corehunter.exceptions.CoreHunterException;

/**
 * Expected proportion of heterozygous loci in offspring.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class HeterozygousLoci extends AllelicDiversity
                              implements Objective<SubsetSolution, CoreHunterData> {

    @Override
    public Evaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        
        GenotypeVariantData geno = data.getGenotypicData();
        
        if(geno == null){
            throw new CoreHunterException(
                    "Genotypes are required for expected proportion of heterozygous loci objective."
            );
        }
        
        // get average genotype of selection (taking into account missing values)
        double[][] avgGeno = getAverageGenotype(geno, solution.getSelectedIDs());
        
        // compute expected proportion of heterozygous loci in offspring
        double total = 0.0;
        int numberOfMarkers = geno.getNumberOfMarkers();
        for (int m = 0; m < numberOfMarkers; m++) {
            int numberOfAlleles = geno.getNumberOfAlleles(m);
            double summedAverageAlleleFrequencySquared = 0.0;
            for (int a = 0; a < numberOfAlleles; a++) {
                summedAverageAlleleFrequencySquared += avgGeno[m][a] * avgGeno[m][a];
            }
            total += (1.0 - summedAverageAlleleFrequencySquared);
        }

        return SimpleEvaluation.WITH_VALUE(total / (double) numberOfMarkers);
    }
    
    @Override
    public boolean isMinimizing() {
        return false;
    }
    
}
