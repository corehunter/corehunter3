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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleMultiAllelicGenotypeVariantData;
import org.corehunter.tests.TestData;
import org.junit.Test;

import uno.informatics.common.io.FileProperties;

/**
 * @author Guy Davenport
 *
 */
public class SimpleDistanceMatrixDataTest extends TestData
{
	private static final String TXT_FILE1 = "/distances.txt" ;
	private static final String TXT_FILE2 = "/distances_with_names.txt";
	
	@Test
	public void inMemoryTest()
	{
		testData(new SimpleDistanceMatrixData(SET, DISTANCES)) ;
	}
	
	@Test
	public void loadFromFileTest1()
	{
		try
    {
	    testData(SimpleDistanceMatrixData.readData(new FileProperties(SimpleDistanceMatrixDataTest.class.getResource(TXT_FILE1).getFile()))) ;
    }
    catch (IOException e)
    {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    fail(e.getMessage()) ;
    }
	}
	
	@Test
	public void loadFromFileTest2()
	{
		try
    {
	    testData(SimpleDistanceMatrixData.readData(new FileProperties(SimpleDistanceMatrixDataTest.class.getResource(TXT_FILE2).getFile()))) ;
    }
    catch (IOException e)
    {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    fail(e.getMessage()) ;
    }
	}
	
	private void testData(SimpleDistanceMatrixData data)
	{		
		assertEquals("Ids not correct!", SET, data.getIDs()) ;

		int size = data.getIDs().size() ;
		
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				assertEquals("Distance["+i+"]["+j+"] not correct!", DISTANCES[i][j], data.getDistance(i, j), PRECISION) ;
			}		
		}
	}

}
