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
package org.corehunter.simple;

import org.corehunter.data.BiAllelicGenotypeVariantData;
import org.corehunter.data.NamedGenotypeVariantData;

/**
 * @author Guy Davenport
 *
 */
public class SimpleBiAllelicGenotypeVariantData extends AbstractNamedSubsetData
    implements BiAllelicGenotypeVariantData, NamedGenotypeVariantData
{
	private int[][] alleles ;
	private int numberOfMarkers;
	private String[] markerNames;
	
	/**
	 * @param names
	 */
  public SimpleBiAllelicGenotypeVariantData(String[] names, String[] markerNames, int[][] alleles)
  {
	  super(names);

	  if (markerNames == null)
	  	throw new IllegalArgumentException("Marker names not defined!") ;
	  
	  if (alleles == null)
	  	throw new IllegalArgumentException("Alleles not deifned!") ;
	  
	  if (names.length != alleles.length)
	  	throw new IllegalArgumentException("Number of alleles don't match number of names!") ;
	  
	  if (alleles.length > 0)
	  {
		  numberOfMarkers = alleles[0].length ;
		  
		  if (numberOfMarkers != markerNames.length)
		  	throw new IllegalArgumentException("Number of marker names don't match number of markers!") ;
		  
		  this.markerNames = markerNames ;
		  
		  for (int i = 1 ; i < alleles.length ; ++i)
		  {
			  if (numberOfMarkers != alleles[i].length)
			  	throw new IllegalArgumentException("Number of markers don't match for id : " + i) ;
		  }
	  }
	  
	  this.alleles = alleles ;
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
		return alleles[id][markerIndex];
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
