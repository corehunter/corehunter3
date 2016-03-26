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

package org.corehunter.tests.objectives.multiallelic;

import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.COVERAGE_SUBSET1;
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SUBSET1;

import org.corehunter.data.simple.SimpleGenotypeVariantData;
import org.corehunter.objectives.multiallelic.CoverageMultiAllelic;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.Test;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class CoverageMultiAllelicTest extends EvaluationTest {

    @Test
    public void test() {
        SimpleGenotypeVariantData data = new SimpleGenotypeVariantData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );

        CoverageMultiAllelic distanceMetric = new CoverageMultiAllelic();

        assertEquals("Evaluation for subset 1 is not correct!", COVERAGE_SUBSET1,
                distanceMetric.evaluate(new SubsetSolution(data.getIDs(), SUBSET1), data), PRECISION);

    }

}
