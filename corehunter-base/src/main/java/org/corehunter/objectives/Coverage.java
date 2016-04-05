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

import java.util.Iterator;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.subset.SubsetSolution;

import org.corehunter.data.GenotypeVariantData;
import org.corehunter.data.CoreHunterData;
import org.corehunter.exceptions.CoreHunterException;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class Coverage implements Objective<SubsetSolution, CoreHunterData> {

    @Override
    public Evaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        
        GenotypeVariantData genotypes = data.getGenotypicData();
        
        if(genotypes == null){
            throw new CoreHunterException("Genotypes are required for coverage objective.");
        }
        
        int numberOfMarkers = genotypes.getNumberOfMarkers();

        double alleleCount = 0;

        for (int m = 0; m < numberOfMarkers; m++) {
            int numberOfAlleles = genotypes.getNumberOfAlleles(m);
            for (int a = 0; a < numberOfAlleles; a++) {
                
                Iterator<Integer> iterator = solution.getSelectedIDs().iterator();
                int scanned = 0;
                while (iterator.hasNext() && !observed(genotypes.getAlleleFrequency(iterator.next(), m, a))){
                    scanned++;
                }
                
                if (scanned < solution.getNumSelectedIDs()) {
                    alleleCount++;
                }
                
            }
        }

        return SimpleEvaluation.WITH_VALUE(alleleCount/genotypes.getTotalNumberOfAlleles());
        
    }
    
    private boolean observed(Double freq){
        return freq != null && freq > 0.0;
    }

    @Override
    public boolean isMinimizing() {
        return false;
    }
    
}
