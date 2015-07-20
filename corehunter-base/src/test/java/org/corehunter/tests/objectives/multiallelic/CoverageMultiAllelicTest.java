/*******************************************************************************
 * Copyright 2015 Guy Davenport
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.corehunter.tests.objectives.multiallelic;

import static org.corehunter.tests.TestData.ALLELES;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.COVERAGE_SUBSET1;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAMES;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SUBSET1;

import org.corehunter.data.simple.SimpleMultiAllelicGenotypeVariantData;
import org.corehunter.objectives.multiallelic.CoverageMultiAllelic;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.Test;

/**
 * @author Guy Davenport
 *
 */
public class CoverageMultiAllelicTest extends EvaluationTest
{
	/**
	 * Test method for {@link org.corehunter.objectives.distance.multiallelic.CavalliSforzaEdwardsDistanceMultiAllelic#getDistance(int, int)}.
	 */
	@Test
	public void test()
	{
		SimpleMultiAllelicGenotypeVariantData data = 
				new SimpleMultiAllelicGenotypeVariantData(NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELES) ;

		CoverageMultiAllelic distanceMetric = 
				new CoverageMultiAllelic() ;

		assertEquals("Evaluation for subset 1 is not correct!", COVERAGE_SUBSET1, 
				distanceMetric.evaluate(new SubsetSolution(data.getIDs(), SUBSET1), data), PRECISION) ;
		
	}

}
