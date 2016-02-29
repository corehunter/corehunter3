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

package org.corehunter.objectives.multiallelic;

import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.corehunter.data.GenotypeVariantData;

/**
 * @author Guy Davenport
 */
public class HetrozygousLociDiversityMultiAllelic
        implements Objective<SubsetSolution, GenotypeVariantData> {

    /*
     * (non-Javadoc)
     * @see org.jamesframework.core.problems.objectives.Objective#evaluate(org.
     * jamesframework.core.problems.solutions.Solution, java.lang.Object)
     */
    @Override
    public Evaluation evaluate(SubsetSolution solution,
            GenotypeVariantData data) {
        
        double summedAverageAlleleFrequencySquared;
        double total = 0.0;

        double averageAlleleFrequency, squared;

        int numberOfMarkers = data.getNumberOfMarkers();
        int numberOfAlleles;

        for (int markerIndex = 0; markerIndex < numberOfMarkers; ++markerIndex) {
            numberOfAlleles = data.getNumberOfAlleles(markerIndex);

            summedAverageAlleleFrequencySquared = 0.0;

            for (int alleleIndex = 0; alleleIndex < numberOfAlleles; ++alleleIndex) {
                
                averageAlleleFrequency = data.getAverageAlelleFrequency(
                                                solution.getSelectedIDs(),
                                                markerIndex,
                                                alleleIndex);
                squared = averageAlleleFrequency * averageAlleleFrequency;
                summedAverageAlleleFrequencySquared = summedAverageAlleleFrequencySquared + squared;
                
            }

            total = total + (1.0 - summedAverageAlleleFrequencySquared);
        }

        return SimpleEvaluation.WITH_VALUE(total / (double) numberOfMarkers);
    }

    /*
     * (non-Javadoc)
     * @see org.jamesframework.core.problems.objectives.Objective#isMinimizing()
     */
    @Override
    public boolean isMinimizing() {
        return false;
    }
}
