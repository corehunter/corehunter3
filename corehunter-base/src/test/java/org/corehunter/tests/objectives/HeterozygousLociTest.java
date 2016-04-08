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

package org.corehunter.tests.objectives;

import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SUBSET1;
import static org.corehunter.tests.TestData.SUBSET2;
import static org.corehunter.tests.TestData.HETROZYGOUS_LOCI_SUBSET1;
import static org.corehunter.tests.TestData.HETROZYGOUS_LOCI_SUBSET2;
import static org.corehunter.tests.TestData.HETROZYGOUS_LOCI_SUBSET3;
import static org.corehunter.tests.TestData.SUBSET3;
import static org.corehunter.tests.TestData.SUBSET_EMPTY;

import org.corehunter.data.CoreHunterData;
import org.corehunter.data.simple.SimpleGenotypeVariantData;
import org.corehunter.objectives.HeterozygousLoci;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.Test;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class HeterozygousLociTest extends EvaluationTest {

    @Test
    public void test() {
        
        SimpleGenotypeVariantData geno = new SimpleGenotypeVariantData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );
        CoreHunterData data = new CoreHunterData(geno);

        HeterozygousLoci objective = new HeterozygousLoci();

        assertEquals(
                "Evaluation for empty subset is not correct!",
                SimpleEvaluation.WITH_VALUE(0.0),
                objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET_EMPTY), data),
                PRECISION
        );
        
        assertEquals(
                "Evaluation for subset 1 is not correct!", 
                HETROZYGOUS_LOCI_SUBSET1,
                objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET1), data),
                PRECISION
        );
        
        assertEquals(
                "Evaluation for subset 2 is not correct!",
                HETROZYGOUS_LOCI_SUBSET2,
                objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET2), data),
                PRECISION
        );
        
        assertEquals(
                "Evaluation for subset 3 is not correct!",
                HETROZYGOUS_LOCI_SUBSET3,
                objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET3), data),
                PRECISION
        );
        
    }

}
