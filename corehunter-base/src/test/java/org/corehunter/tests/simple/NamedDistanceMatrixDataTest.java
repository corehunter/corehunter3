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

import org.corehunter.simple.NamedDistanceMatrixData;
import org.corehunter.tests.TestData;
import org.junit.Test;

/**
 * @author Guy Davenport
 *
 */
public class NamedDistanceMatrixDataTest extends TestData
{

	@Test
	public void test()
	{
		NamedDistanceMatrixData data = new NamedDistanceMatrixData(NAMES, DISTANCES) ;
		
		assertEquals("Ids not correct!", SET, data.getIDs()) ;

		int size = data.getIDs().size() ;
		
		for (int i = 0; i < size; i++)
		{
			assertEquals("Name for "+i+" not correct!", NAMES[i], data.getName(i)) ;
			
			for (int j = 0; j < size; j++)
			{
				assertEquals("Distance["+i+"]["+i+"] not correct!", DISTANCES[i][j], data.getDistance(i, j), PRECISION) ;
			}		
		}
	}
}
