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
package org.corehunter.extended;

import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.simple.SimpleDistanceMatrixData;

import uno.informatics.model.ContinuousScale;
import uno.informatics.model.Feature;
import uno.informatics.model.Scale;

/**
 * @author Guy Davenport
 *
 */
public class GowersDistanceMatrixGenerator implements DistanceMatrixGenerator
{
	private static final int BINARY_SCALE_TYPE = 0;
	private static final int DISCRETE_SCALE_TYPE = 1;
	private static final int RANGED_SCALE_TYPE = 2;
	
	private Set<Integer> ids ;
	private Object[][] data ;
	private Feature[] features ;
	
	private Scale[] scales ;
	private int[] scaleTypes ;
	private double[] ranges;
	
	public GowersDistanceMatrixGenerator(Object[][] data, Feature[] features)
	{
		if (data == null || features == null)
			throw new IllegalArgumentException("Features and data must be defined!") ;
		
		for (int id = 0; id < data.length; ++id)
		{
			ids.add(id);
		}
		
		scales = new Scale[features.length] ;
		scaleTypes = new int[features.length] ;
		ranges = new double[features.length] ;
		
		for (int i = 0 ; i < features.length ; ++i)
		{
			scales[i] = features[i].getScale() ;
			
			switch (features[i].getScale().getScaleType())
			{
				case NOMINAL:
					switch (features[i].getScale().getDataType())
					{
						case BOOLEAN:
							scaleTypes[i] = BINARY_SCALE_TYPE ;
							break;
						default:
							scaleTypes[i] = DISCRETE_SCALE_TYPE ;
							break;
					}
				case INTERVAL:
				case ORDINAL:
				case RATIO:
					switch (features[i].getScale().getDataType())
					{
						case BIG_DECIMAL:
						case BIG_INTEGER:
						case DOUBLE:
						case FLOAT:
						case INTEGER:
						case LONG:
						case SHORT:
							ranges[i] = calculateRange(features[i].getScale()) ;
							
							if (ranges[i] >= 0)
								scaleTypes[i] = RANGED_SCALE_TYPE ;
							else
								scaleTypes[i] = DISCRETE_SCALE_TYPE ; // default to discrete 
							break ;
						case BOOLEAN:
						case DATE:
						case LSID:
						case STRING:
						default:
							throw new IllegalArgumentException("Illegal scale type : " + features[i].getScale().getScaleType() + 
									" for data type " + features[i].getScale().getDataType()) ;
					}
				case NONE:
				default:
					throw new IllegalArgumentException("Illegal scale type : " + features[i].getScale().getScaleType()) ;
			}
		}
		
		this.data = data ;
		this.features = features ;
	}
	
	public DistanceMatrixData generateDistanceMatrix()
	{
		double[][] distances = new double[ids.size()][ids.size()];
		
		for (int i = 0 ; i < data.length ; ++i)
		{
			if (data[i].length != features.length)
				throw new IllegalArgumentException("Number of features must match number of elements in a row!") ;
			
			for (int j = i+1 ; j < data.length; ++j)
			{
				for (int k = 0 ; k < features.length; ++j)
				{
					distances[i][j] = calculate(scaleTypes[k], ranges[k], data[i][k], data[j][k]) ;
					distances[j][i] = distances[i][j] ;
				}		
			}		
		}			
		
		return new SimpleDistanceMatrixData(ids, distances) ;
	}
	
	/**
	 * @param scale
	 * @return
	 */
  private double calculateRange(Scale scale)
  {
  	if (scale instanceof ContinuousScale)
  		return ((ContinuousScale)scale).getMaximumValue().doubleValue() - ((ContinuousScale)scale).getMinimumValue().doubleValue();
  	else
  		return 0;
  }

	
	private double calculate(int scaleType, double range, Object elementA, Object elementB)
	{
		if (elementA != null && elementB != null)
		{
			switch (scaleType)
			{
				case BINARY_SCALE_TYPE:
					if ((Boolean)elementA && (Boolean)elementB)
						return 1;
					else
						return 0;
				case DISCRETE_SCALE_TYPE:
					if (ObjectUtils.equals(elementA, elementB))
						return 1;
					else
						return 0;
				case RANGED_SCALE_TYPE:
					return 1 - (Math.abs(((Number) elementA).doubleValue() - ((Number) elementB).doubleValue()) / range) ;
				default:
					break;
			}
		}

		return 0;
	}
}
