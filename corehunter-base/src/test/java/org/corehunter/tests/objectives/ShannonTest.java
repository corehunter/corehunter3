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
import static org.corehunter.tests.TestData.SHANNONS_SUBSET1;
import static org.corehunter.tests.TestData.SHANNONS_SUBSET2;
import static org.corehunter.tests.TestData.SHANNONS_SUBSET3;
import static org.corehunter.tests.TestData.SUBSET1;
import static org.corehunter.tests.TestData.SUBSET2;
import static org.corehunter.tests.TestData.SUBSET3;
import static org.corehunter.tests.TestData.SUBSET_EMPTY;

import java.util.Random;

import org.corehunter.data.CoreHunterData;
import org.corehunter.data.simple.SimpleGenotypeData;
import org.corehunter.objectives.Shannon;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;
import org.junit.Test;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class ShannonTest extends EvaluationTest {

    @Test
    public void test() {
        
        SimpleGenotypeData geno = new SimpleGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );
        CoreHunterData data = new CoreHunterData(geno);

        Shannon objective = new Shannon();

        assertEquals(
                "Evaluation for empty subset is not correct!",
                SimpleEvaluation.WITH_VALUE(0.0),
                objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET_EMPTY), data),
                PRECISION
        );
        
        assertEquals(
                "Evaluation for subset 1 is not correct!",
                SHANNONS_SUBSET1,
                objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET1), data),
                PRECISION
        );
        
        assertEquals("Evaluation for subset 2 is not correct!",
                SHANNONS_SUBSET2,
                objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET2), data),
                PRECISION
        );
        
        assertEquals(
                "Evaluation for subset 3 is not correct!",
                SHANNONS_SUBSET3,
                objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET3), data),
                PRECISION
        );
        
    }
    
    @Test
    public void testDeltaEvaluation() {
        
        Random rng = new Random();
        
        SimpleGenotypeData geno = new SimpleGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );
        CoreHunterData data = new CoreHunterData(geno);
        
        Shannon objective = new Shannon();

        SubsetProblem<CoreHunterData> problem = new SubsetProblem<>(data, objective);
        
        Neighbourhood<SubsetSolution> neigh = new SinglePerturbationNeighbourhood();
        SubsetSolution sol = problem.createRandomSolution(rng);
        Evaluation curEval = objective.evaluate(sol, data);
        int numMoves = 10000;
        for(int m = 0; m < numMoves; m++){
            Move<? super SubsetSolution> move = neigh.getRandomMove(sol, rng);
            Evaluation deltaEval = objective.evaluate(move, sol, curEval, data);
            move.apply(sol);
            Evaluation fullEval = objective.evaluate(sol, data);
            assertEquals("Delta evaluation and full neighbour evaluation differ!",
                         fullEval, deltaEval, PRECISION);
            curEval = fullEval;
        }
        
    }
    
}
