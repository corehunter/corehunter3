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
package org.corehunter.data.simple;

import org.corehunter.data.BiAllelicGenotypeVariantData;
import org.corehunter.data.NamedGenotypeVariantData;

/**
 * @author Guy Davenport
 *
 */
public class SimpleBiAllelicGenotypeVariantData extends AbstractNamedSubsetData
    implements BiAllelicGenotypeVariantData, NamedGenotypeVariantData
{
	private int[][] alleleScores ;
	private int numberOfMarkers;
	private String[] markerNames;
	
	/**
	 * @param names
	 */
  public SimpleBiAllelicGenotypeVariantData(String[] names, String[] markerNames, int[][] alleleScores)
  {
	  super(names);

	  if (markerNames == null)
	  	throw new IllegalArgumentException("Marker names not defined!") ;
	  
	  if (alleleScores == null)
	  	throw new IllegalArgumentException("Alleles not deifned!") ;
	  
	  if (names.length != alleleScores.length)
	  	throw new IllegalArgumentException("Number of alleleScores don't match number of names!") ;
	  
	  if (alleleScores.length > 0)
	  {
		  numberOfMarkers = alleleScores[0].length ;
		  
		  if (numberOfMarkers != markerNames.length)
		  	throw new IllegalArgumentException("Number of marker names don't match number of markers!") ;
		  
		  this.markerNames = new String[numberOfMarkers] ;
		  
		  this.alleleScores = new int[alleleScores.length][numberOfMarkers] ;
		  
		  for (int j = 0 ; j < numberOfMarkers ; ++j)
		  {
		  	this.markerNames[j] = markerNames[j] ;
		  }
		  
		  for (int i = 0 ; i < alleleScores.length ; ++i)
		  {
			  if (numberOfMarkers != alleleScores[i].length)
			  	throw new IllegalArgumentException("Number of markers don't match for id : " + i) ;
			  
			  for (int j = 0 ; j < numberOfMarkers ; ++j)
			  {
			  	this.alleleScores[i][j] = alleleScores[i][j] ;
			  }
		  }
	  }
	  else
	  {
		  this.alleleScores = new int[0][0] ;
	  }
  }

	/* (non-Javadoc)
	 * @see org.corehunter.data.GenotypeVariantData#getNumberOfMarkers()
	 */
	@Override
	public int getNumberOfMarkers()
	{
		return numberOfMarkers;
	}

	/* (non-Javadoc)
	 * @see org.corehunter.data.BiAllelicGenotypeVariantData#getAlleleScore(int, int)
	 */
	@Override
	public int getAlleleScore(int id, int markerIndex)
	{
		return alleleScores[id][markerIndex];
	}

	/* (non-Javadoc)
	 * @see org.corehunter.data.NamedGenotypeVariantData#getMarkerName(int)
	 */
  @Override
  public String getMarkerName(int markerIndex)
      throws ArrayIndexOutOfBoundsException
  {
	  return markerNames[markerIndex];
  }
}
