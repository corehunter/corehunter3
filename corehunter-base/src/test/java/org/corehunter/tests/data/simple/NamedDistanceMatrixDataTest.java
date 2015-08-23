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
package org.corehunter.tests.data.simple;

import static org.corehunter.tests.TestData.DISTANCES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.NAMES;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.corehunter.data.simple.NamedDistanceMatrixData;
import org.junit.Test;

import uno.informatics.common.io.FileProperties;

/**
 * @author Guy Davenport
 *
 */
public class NamedDistanceMatrixDataTest
{
	private static final String TXT_FILE = "/distances_with_names.txt";
	
	@Test
	public void inMemoryTest()
	{
		testData(new NamedDistanceMatrixData(NAME, NAMES, DISTANCES)) ;
	}
	
	@Test
	public void loadFromFileTest()
	{
		try
    {
	    testData(NamedDistanceMatrixData.readData(new FileProperties(NamedDistanceMatrixDataTest.class.getResource(TXT_FILE).getFile(), true, true))) ;
    }
    catch (IOException e)
    {
	    e.printStackTrace();
	    fail(e.getMessage()) ;
    }
	}
	
	private void testData(NamedDistanceMatrixData data)
	{		
		assertEquals("Ids not correct!", SET, data.getIDs()) ;

		int size = data.getIDs().size() ;
		
		for (int i = 0; i < size; i++)
		{
			assertEquals("Name for "+i+" not correct!", NAMES[i], data.getName(i)) ;
			
			for (int j = 0; j < size; j++)
			{
				assertEquals("Distance["+i+"]["+j+"] not correct!", DISTANCES[i][j], data.getDistance(i, j), PRECISION) ;
			}		
		}
	}
}
