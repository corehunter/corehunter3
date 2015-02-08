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
package org.corehunter.tests.objectives.distance.multiallelic;

import static org.junit.Assert.*;
import static org.corehunter.tests.TestData.* ;

import java.util.Iterator;

import org.corehunter.objectives.distance.multiallelic.CavalliSforzaEdwardsDistanceMultiAllelic;
import org.corehunter.simple.SimpleMultiAllelicGenotypeVariantData;
import org.junit.Test;

/**
 * @author Guy Davenport
 *
 */
public class ModifiedRogersDistanceMultiAllelicTest
{
	/**
	 * Test method for {@link org.corehunter.objectives.distance.multiallelic.CavalliSforzaEdwardsDistanceMultiAllelic#getDistance(int, int)}.
	 */
	@Test
	public void test()
	{
		SimpleMultiAllelicGenotypeVariantData data = new SimpleMultiAllelicGenotypeVariantData(NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELES) ;

		CavalliSforzaEdwardsDistanceMultiAllelic distanceMetric = 
				new CavalliSforzaEdwardsDistanceMultiAllelic(data) ;
		
		assertEquals("Data is not correct!", data, distanceMetric.getData()) ;
		
		assertEquals("Ids not correct!", SET, distanceMetric.getIDs()) ;

		Iterator<Integer> iteratorX = distanceMetric.getIDs().iterator() ;
		Iterator<Integer> iteratorY ;
		
		Integer idX ;
		Integer idY ;
		
		int i = 0 ;
		int j = 0 ;
		
		while (iteratorX.hasNext())
		{
			idX = iteratorX.next() ;
			
			iteratorY = distanceMetric.getIDs().iterator() ;
			
			j = 0 ;
			
			while (iteratorY.hasNext())
			{
				idY = iteratorY.next() ;
				
				assertEquals("Distance["+i+"]["+j+"] not correct!", MODIFIED_ROGERS_DISTANCES[i][j], distanceMetric.getDistance(idX, idY), PRECISION) ;
				
				++j ;
			}
			
			++i ;
		}
	}

}
