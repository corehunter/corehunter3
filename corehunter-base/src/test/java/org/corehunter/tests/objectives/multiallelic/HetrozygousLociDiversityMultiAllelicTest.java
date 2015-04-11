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
import static org.corehunter.tests.TestData.HETROZYGOUS_LOCI_DIVERSITY_SUBSET1;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAMES;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SUBSET1;
import static org.junit.Assert.assertEquals;

import org.corehunter.data.simple.SimpleMultiAllelicGenotypeVariantData;
import org.corehunter.objectives.multiallelic.HetrozygousLociDiversityMultiAllelic;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.junit.Test;

/**
 * @author Guy Davenport
 *
 */
public class HetrozygousLociDiversityMultiAllelicTest
{
	/**
	 * Test method for {@link org.corehunter.objectives.distance.multiallelic.CavalliSforzaEdwardsDistanceMultiAllelic#getDistance(int, int)}.
	 */
	@Test
	public void test()
	{
		SimpleMultiAllelicGenotypeVariantData data = 
				new SimpleMultiAllelicGenotypeVariantData(NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELES) ;

		HetrozygousLociDiversityMultiAllelic objective = 
				new HetrozygousLociDiversityMultiAllelic() ;

		assertEquals("Evaluation for subset 1 is not correct!", HETROZYGOUS_LOCI_DIVERSITY_SUBSET1, 
				objective.evaluate(new SubsetSolution(data.getIDs(), SUBSET1), data), PRECISION) ;
	}

}
