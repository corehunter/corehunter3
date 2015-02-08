/*******************************************************************************
 * Copyright Herman De Beukelaer, 2014 Guy Davenport Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package org.corehunter.simple;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.NamedSubsetData;

/**
 * @author Guy Davenport
 */
public class NamedDistanceMatrixData extends AbstractNamedSubsetData implements DistanceMatrixData, NamedSubsetData
{
	// distance matrix
	private double[][] distances;

	public NamedDistanceMatrixData(String[] names, double[][] distances)
	{
		super(names) ;
		
		this.distances = new double[distances.length][distances.length] ;
		
	  if (getIDs().size() != distances.length)
	  	throw new IllegalArgumentException("Number of ids do not match number of distances!") ;

		for (int i = 0; i < distances.length; i++)
		{
			if (distances.length != distances[i].length)
		  	throw new IllegalArgumentException("Number of distances do not match number of ids in row  :" + i + "!") ;
			
			for (int j = 0; j < distances[i].length; j++)
			{
				this.distances[i][j] = distances[i][j] ;
			}		
		}
		
		this.distances = distances;
	}

	/*
	 * (non-Javadoc)
	 * @see org.corehunter.DistanceMatrixData#getDistance(int, int)
	 */
	@Override
	public double getDistance(int idX, int idY)
	{
		return distances[idX][idY];
	}
}
