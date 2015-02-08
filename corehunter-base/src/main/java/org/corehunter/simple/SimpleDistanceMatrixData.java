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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.corehunter.data.DistanceMatrixData;

/**
 * @author Guy Davenport
 */
public class SimpleDistanceMatrixData implements DistanceMatrixData
{
	private double[][] distances;
	
	private Set<Integer> ids;
	private Map<Integer, Integer> idMap;

	public SimpleDistanceMatrixData(Set<Integer> ids, double[][] distances)
	{
	  if (ids == null)
	  	throw new IllegalArgumentException("ids not defined!") ;
	  
		this.ids = new TreeSet<Integer>(ids);
		
		idMap = new HashMap<Integer, Integer>() ;
		
	  if (distances == null)
	  	throw new IllegalArgumentException("Distances not defined!") ;
	  
	  if (this.ids.size() != distances.length)
	  	throw new IllegalArgumentException("Number of ids do not match number of distances!") ;
	  
		this.distances = new double[distances.length][distances.length] ;

		Iterator<Integer> iterator = this.ids.iterator() ;

		for (int i = 0; i < distances.length; i++)
		{
			idMap.put(i, iterator.next());
			
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
	 * @see org.jamesframework.core.problems.datatypes.SubsetData#getIDs()
	 */
	@Override
	public Set<Integer> getIDs()
	{
		return ids;
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
