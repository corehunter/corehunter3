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

import java.util.Iterator;

import org.corehunter.simple.SimpleBiAllelicGenotypeVariantData;
import org.corehunter.simple.SimpleMultiAllelicGenotypeVariantData;
import org.corehunter.tests.TestData;
import org.junit.Test;

/**
 * @author Guy Davenport
 *
 */
public class SimpleMultiAllelicGenotypeVariantDataTest extends TestData
{
	private static final String[] MARKER_NAMES = new String [] 
			{
				"mk1",
				"mk2",
				"mk3",
				"mk4",
				"mk5",
				"mk6",
				"mk7"
			};
	
	private static final double[][][] ALLELES = new double [][][]
	{
	    new double[][] 
	    { 
	    		new double[] { 0.33, 0.33, 0.33 },
	    		new double[] { 0.5, 0.5 }, 
	    		new double[] { 0, 0.5, 0.5 },
	    		new double[] { 0.25, 0.25, 0.25, 0.25 },
	    		new double[] { 0.33, 0.33, 0.33 }, 
	    		new double[] { 0.0, 1.0 },
	    		new double[] { 1.0, 0.0 } 
	    },
	    new double[][] 
	    { 
	    		new double[] { 0.33, 0.33, 0.33 },
	    		new double[] { 0.5, 0.5 }, 
	    		new double[] { 0, 0.5, 0.5 },
	    		new double[] { 0.25, 0.25, 0.25, 0.25 },
	    		new double[] { 0.33, 0.33, 0.33 }, 
	    		new double[] { 0.0, 1.0 },
	    		new double[] { 1.0, 0.0 } 
	    },
	    new double[][] 
	    { 
	    		new double[] { 0.33, 0.33, 0.33 },
	    		new double[] { 0.5, 0.5 }, 
	    		new double[] { 0, 0.5, 0.5 },
	    		new double[] { 0.25, 0.25, 0.25, 0.25 },
	    		new double[] { 0.33, 0.33, 0.33 }, 
	    		new double[] { 0.0, 1.0 },
	    		new double[] { 1.0, 0.0 } 
	    },
	    new double[][] 
	    { 
	    		new double[] { 0.33, 0.33, 0.33 },
	    		new double[] { 0.5, 0.5 }, 
	    		new double[] { 0, 0.5, 0.5 },
	    		new double[] { 0.25, 0.25, 0.25, 0.25 },
	    		new double[] { 0.33, 0.33, 0.33 }, 
	    		new double[] { 0.0, 1.0 },
	    		new double[] { 1.0, 0.0 } 
	    },
	    new double[][] 
	    { 
	    		new double[] { 0.33, 0.33, 0.33 },
	    		new double[] { 0.5, 0.5 }, 
	    		new double[] { 0, 0.5, 0.5 },
	    		new double[] { 0.25, 0.25, 0.25, 0.25 },
	    		new double[] { 0.33, 0.33, 0.33 }, 
	    		new double[] { 0.0, 1.0 },
	    		new double[] { 1.0, 0.0 } 
	    }
	};

	private static final String[][] ALLELE_NAMES = new String [][] 
			{
		new String [] {"mk1-1","mk1-2","mk1-3"},
		new String [] {"mk2-1","mk2-2"},
		new String [] {"mk3-1","mk3-2","mk3-3"},
		new String [] {"mk4-1","mk4-2","mk4-3","mk4-3"},
		new String [] {"mk5-1","mk5-2","mk5-3"},
		new String [] {"mk6-1","mk6-2"},
		new String [] {"mk7-1","mk7-2"}
	};

	private static final double PRESCISION = 0;


	@Test
	public void test()
	{
		SimpleMultiAllelicGenotypeVariantData data = new SimpleMultiAllelicGenotypeVariantData(NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELES) ;
		
		assertEquals("Ids not correct!", SET, data.getIDs()) ;
		
		assertEquals("Number of marker is not correct!", MARKER_NAMES.length, data.getNumberOfMarkers()) ;
		
		int size = data.getIDs().size() ;
		
		for (int j = 0; j < data.getNumberOfMarkers(); j++)
		{
			assertEquals("Marker name for "+j+" is not correct!", MARKER_NAMES[j], data.getMarkerName(j)) ;
			
			for (int k = 0; k < data.getNumberOfAlleles(j); k++)
			{
				assertEquals("Allele name for marker "+j+" allele "+k+" is not correct!", ALLELE_NAMES[j][k], data.getAlleleName(j, k)) ;
			}
		}
		
		Iterator<Integer> iterator = data.getIDs().iterator() ;
		
		int index ;
		int i = 0 ;
		
		while (iterator.hasNext())
		{
			index = iterator.next() ;
			
			assertEquals("Name for "+ index +" is not correct!", NAMES[i], data.getName(index)) ;

			for (int j = 0; j < size; j++)
			{
				for (int k = 0; k < data.getNumberOfAlleles(j); k++)
				{
					assertEquals("Alele["+index+"]["+j+"] not correct!", ALLELES[i][j][k], data.getAlelleFrequency(index, j, k), PRESCISION) ;
				}
			}		
		
			++ i ;
		}
	}
}
