/*******************************************************************************
 * Copyright 2014 Guy Davenport
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
package org.corehunter.extended.test;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.extended.GowersDistanceMatrixGenerator;
import org.junit.Test;

import uno.informatics.data.dataset.DatasetException;
import uno.informatics.data.feature.ColumnFeature;
import uno.informatics.data.feature.array.ArrayFeatureDataset;
import uno.informatics.data.io.FileProperties;
import uno.informatics.data.io.FileType;
import uno.informatics.data.utils.DatasetUtils;
import uno.informatics.model.DataType;
import uno.informatics.model.Feature;
import uno.informatics.model.FeatureDataset;
import uno.informatics.model.ScaleType;

/**
 * @author Guy Davenport
 *
 */
public class GowersDistanceMatrixGeneratorTest
{

	private static final String DATA_FILE = "Datos_11_25_2013.csv";
	private static final String UID = "test";
	private static final String NAME = "test";
	private static final String DESCRIPTION = "test";
	
	private static final Object[][] DATA = new Object[][] {
				new Object[] {1, 1.0, "1", true},
				new Object[] {2, 3.0, "2", true},
				new Object[] {3, 3.0, "1", false},
				new Object[] {4, 5.0, "2", false},
				new Object[] {5, 4.0, "1", true}
			};
	
	private static final Feature[] FEATURES = new Feature[] {
		new MockFeaure(DataType.INTEGER, ScaleType.INTERVAL, 0, 5),  
		new MockFeaure(DataType.DOUBLE, ScaleType.RATIO, 0.0, 5.0), 
		new MockFeaure(DataType.STRING, ScaleType.NOMINAL), 
		new MockFeaure(DataType.BOOLEAN, ScaleType.NOMINAL)
		};
	
	private static final double[][] MATRIX = new double[][] {
			new double[] {0.0, 0.6, 0.55, 0.15, 0.65},
			new double[] {0.6, 0.0, 0.45, 0.55, 0.55},
			new double[] {0.55, 0.45, 0.0, 0.35, 0.6},
			new double[] {0.15, 0.55, 0.35, 0.0, 0.4},
			new double[] {0.65, 0.55, 0.6, 0.4, 0.0}
	};
	
	/**
	 * Test method for {@link org.corehunter.extended.test.GowersDistanceMatrixGenerator#GowersDistanceMatrixGenerator(java.lang.Object[][], uno.informatics.model.Feature[])}.
	 */
	@Test
	public void testGowersDistanceMatrixGenerator()
	{
		new GowersDistanceMatrixGenerator(DATA, FEATURES) ;
		
	}

	/**
	 * Test method for {@link org.corehunter.extended.test.GowersDistanceMatrixGenerator#generateDistanceMatrix()}.
	 */
	@Test
	public void testGenerateDistanceMatrix()
	{
		GowersDistanceMatrixGenerator generator = new GowersDistanceMatrixGenerator(DATA, FEATURES) ;
		
		DistanceMatrixData matrix = generator.generateDistanceMatrix() ;
		
		Iterator<Integer> iterator1 = matrix.getIDs().iterator();
		Iterator<Integer> iterator2 = matrix.getIDs().iterator();
		
		int index1 ;
		int index2 ;
		
		while (iterator1.hasNext())
		{
			index1 = iterator1.next() ;
			
			while (iterator2.hasNext())
			{
				index2 = iterator2.next() ;
				assertEquals("cell (" + index1 + "," + index2 + ")", MATRIX[index1][index2], matrix.getDistance(index1, index2), 0.0000000001) ;
			}
		}
	}
	
	public void testGenerateDistanceMatrixFromFile() 
	{
		try
    {
	    FileProperties fileProperties = new FileProperties(GowersDistanceMatrixGeneratorTest.class.getResource(DATA_FILE).getPath(), FileType.CSV) ;
	    
	    fileProperties.setColumnHeaderPosition(0) ;
	    fileProperties.setDataPosition(1) ;
	    
	    List<ColumnFeature> features = DatasetUtils.generateDatasetFeatures(fileProperties, null, 10) ;
	    
	    FeatureDataset dataset = 
	    		ArrayFeatureDataset.createFeatureDataset(UID, NAME, DESCRIPTION, DatasetUtils.createFeatures(features), fileProperties) ;
	    
			GowersDistanceMatrixGenerator generator = new GowersDistanceMatrixGenerator(dataset) ;
			
	    DistanceMatrixData data = generator.generateDistanceMatrix() ;
	    
	    

    }
    catch (DatasetException e)
    {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		
		
	}
}
