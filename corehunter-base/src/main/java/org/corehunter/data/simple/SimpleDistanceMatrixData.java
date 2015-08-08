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
package org.corehunter.data.simple;

import static uno.informatics.common.Constants.UNKNOWN_INDEX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.corehunter.data.DistanceMatrixData;

import uno.informatics.common.io.FileProperties;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;

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
	}

	public SimpleDistanceMatrixData(Set<Integer> ids,
		List<List<Double>> distances)
	{
	  if (ids == null)
	  	throw new IllegalArgumentException("ids not defined!") ;
	  
		this.ids = new TreeSet<Integer>(ids);
		
		idMap = new HashMap<Integer, Integer>() ;
		
	  if (distances == null)
	  	throw new IllegalArgumentException("Distances not defined!") ;
	  
	  if (this.ids.size() != distances.size())
	  	throw new IllegalArgumentException("Number of ids do not match number of distances!") ;
	  
		this.distances = new double[distances.size()][distances.size()] ;

		Iterator<Integer> idIterator = this.ids.iterator() ;
		Iterator<List<Double>> distanceRowIterator = distances.iterator() ;
		List<Double> distanceRow ;
		Iterator<Double> distanceIterator ;
		
		int i = 0 ;
		int j = 0 ;

		while (distanceRowIterator.hasNext())
		{
			idMap.put(i, idIterator.next());
			
			distanceRow = distanceRowIterator.next() ;
			
			distanceIterator = distanceRow.iterator() ;
			
			if (this.ids.size() != distanceRow.size())
		  	throw new IllegalArgumentException("Number of distances do not match number of ids in row  :" + i + "!") ;
			
			j = 0;
			
			while (distanceIterator.hasNext())
			{
				this.distances[i][j] = distanceIterator.next() ;
				
				++j ;
			}		
			
			++i ;
		}
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

	public static final SimpleDistanceMatrixData readData(
		FileProperties fileProperties) throws IOException
	{
		RowReader reader = null ;

		if (fileProperties == null)
			throw new IOException("File properties not defined!") ;

		if (fileProperties.getFile() == null)
			throw new IOException("File not defined!") ;

		if (fileProperties.getFileType() == null)
			throw new IOException("File type not defined!") ;
		
		if (fileProperties.getRowHeaderPosition() > UNKNOWN_INDEX && 
				fileProperties.getDataRowPosition() > UNKNOWN_INDEX && 
				fileProperties.getDataRowPosition() <= fileProperties.getColumnHeaderPosition())
			throw new IOException("Column header position : " + 
					fileProperties.getDataRowPosition() + " must be before data position : " + fileProperties.getColumnHeaderPosition()) ;

		if (!fileProperties.getFile().exists())
			throw new IOException("File does not exist : " + fileProperties.getFile()) ;
		
		List<String> columnNames = new LinkedList<String>() ;
		List<String> rowNames = new LinkedList<String>() ;
		
		String name ;
		
		List<Double> distancesScoresRow ;
		List<List<Double>> distances ;
		
		Set<Integer> ids = new HashSet<Integer>() ;
		
		int row = 0 ;
		
		try
		{
			reader = IOUtilities.createRowReader(fileProperties) ;

			if (reader != null && reader.ready())
			{
				int columnCount = 0 ;

				if (reader.nextRow())
				{
					if (fileProperties.getRowHeaderPosition() > UNKNOWN_INDEX) 
					{
						while (row < fileProperties.getRowHeaderPosition() && reader.nextRow())
							++row ;	
					
						reader.nextColumn() ;
						reader.nextColumn() ;
					
						columnNames = reader.getRowCellsAsString() ;
					
						columnCount = columnNames.size() ;
					}
					
					distances = new LinkedList<List<Double>>() ;
					
					if (fileProperties.getDataRowPosition() > UNKNOWN_INDEX) 
						while (row < fileProperties.getDataRowPosition() && reader.nextRow())
							++row ;		
					
					while (reader.nextRow())
					{				
						reader.nextColumn() ;	
						
						name = reader.getCellAsString() ;
						
						if (fileProperties.getRowHeaderPosition() > UNKNOWN_INDEX) 
						{
							rowNames.add(name) ;
						
							reader.nextColumn() ;	
						}

						distancesScoresRow = reader.getRowCellsAsDouble() ;

						if (distancesScoresRow.size() != columnCount)
							throw new IOException("Rows are not all the same size!") ;
						
						distances.add(distancesScoresRow) ;
						ids.add(row) ;

						++row ;
					}
				}
				else
				{
					distances = new ArrayList<List<Double>>(0) ;
				}
			}
			else
			{
				distances = new ArrayList<List<Double>>(0) ;
			}

			if (reader != null)
				reader.close() ;
			
			return new SimpleDistanceMatrixData(ids, distances) ;

		}
		catch (IOException e)
		{
			throw new IOException("Error reading file at row : " + row + " due to " + e.getMessage(), e) ;
		}
	}
}
