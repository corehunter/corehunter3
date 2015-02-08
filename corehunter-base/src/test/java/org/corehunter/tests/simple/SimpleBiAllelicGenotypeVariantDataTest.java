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
package org.corehunter.tests.simple;

import static org.junit.Assert.*;
import static org.corehunter.tests.TestData.* ;

import java.util.Iterator;

import org.corehunter.simple.SimpleBiAllelicGenotypeVariantData;

import org.junit.Test;

/**
 * @author Guy Davenport
 *
 */
public class SimpleBiAllelicGenotypeVariantDataTest
{
	@Test
	public void test()
	{
		SimpleBiAllelicGenotypeVariantData data = new SimpleBiAllelicGenotypeVariantData(NAMES, MARKER_NAMES, ALLELE_SCORES) ;
		
		assertEquals("Ids not correct!", SET, data.getIDs()) ;
		
		assertEquals("Number of marker is not correct!", MARKER_NAMES.length, data.getNumberOfMarkers()) ;
		
		int size = data.getIDs().size() ;
		
		for (int i = 0; i < size; i++)
		{
			assertEquals("Marker name for "+i+" is not correct!", MARKER_NAMES[i], data.getMarkerName(i)) ;
		}
		
		Iterator<Integer> iterator = data.getIDs().iterator() ;
		
		int index ;
		int i = 0 ;
		
		while (iterator.hasNext())
		{
			index = iterator.next() ;
			
			assertEquals("Marker name for "+ index +" is not correct!", NAMES[i], data.getName(index)) ;
			
			for (int j = 0; j < size; j++)
			{
				assertEquals("Alele["+index+"]["+j+"] not correct!", ALLELE_SCORES[index][j], data.getAlleleScore(index, j)) ;
			}		
			
			++ i ;
		}
	}
}
