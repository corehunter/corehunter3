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

import org.corehunter.data.MultiAllelicGenotypeVariantData;
import org.corehunter.data.NamedAllelicGenotypeVariantData;

/**
 * @author Guy Davenport
 *
 */
public class SimpleMultiAllelicGenotypeVariantData extends AbstractNamedSubsetData
    implements MultiAllelicGenotypeVariantData, NamedAllelicGenotypeVariantData
{
	private double[][][] alleleFrequencies ;
	private int numberOfMarkers;
	private int[] numberOfAlleles;
	private String[] markerNames;
	private String[][] alleleNames;
	private int totalNumberAlleles;

	/**
	 * @param names
	 */
  public SimpleMultiAllelicGenotypeVariantData(String[] names, String[] markerNames, 
  		String[][] alleleNames, double[][][] alleleFrequencies)
  {
	  super(names);

	  if (names == null)
	  	throw new IllegalArgumentException("Names not defined!") ;
	  
	  if (markerNames == null)
	  	throw new IllegalArgumentException("Marker names not defined!") ;
	  
	  if (alleleNames == null)
	  	throw new IllegalArgumentException("Allele names not defined!") ;
	  
	  if (alleleFrequencies == null)
	  	throw new IllegalArgumentException("Allele Frequency entries not deifned!") ;
	  
	  if (names.length != alleleFrequencies.length)
	  	throw new IllegalArgumentException("Number of allele frequency entries don't match number of names!") ;
	  
	  totalNumberAlleles = 0 ;
	  
	  if (alleleFrequencies.length > 0)
	  {
		  numberOfMarkers = markerNames.length ;
		  
		  numberOfAlleles = new int[numberOfMarkers] ;
		  
		  this.markerNames = new String[numberOfMarkers] ;
		  this.alleleNames = new String[numberOfMarkers][] ;

		  for (int j = 0 ; j < numberOfMarkers ; ++j)
		  {
			  if (alleleNames[j] == null)
			  	throw new IllegalArgumentException("Allele names not defined for marker : " + j + "!") ;
			  
			  this.markerNames[j] = markerNames[j] ;
			  
			  numberOfAlleles[j] = alleleNames[j].length ; 
			  
			  totalNumberAlleles = totalNumberAlleles + numberOfAlleles[j] ;
			  
			  this.alleleNames[j] = new String[numberOfAlleles[j]] ;
			  
			  for (int k = 0 ; k < numberOfAlleles[j] ; ++k)
			  	this.alleleNames[j][k] = alleleNames[j][k] ;
		  }

		  this.alleleFrequencies = new double[alleleFrequencies.length][numberOfMarkers][] ;
		  
		  for (int i = 0 ; i < alleleFrequencies.length ; ++i)
		  {
			  if (numberOfMarkers != alleleFrequencies[i].length)
			  	throw new IllegalArgumentException("Number of markers don't match allele frequencies for id : " + i + "!") ; 	

			  for (int j = 0 ; j < numberOfMarkers ; ++j)
			  {
				  if (numberOfAlleles[j] != alleleFrequencies[0][j].length)
				  	throw new IllegalArgumentException("Number of alleles for marker " + j + "  at entry id : " + i + "!") ;
				  
				  this.alleleFrequencies[i][j] = new double[numberOfAlleles[j]] ;
				  		
				  for (int k = 0 ; k < numberOfAlleles[j] ; ++k)
				  {
				  	this.alleleFrequencies[i][j][k] = alleleFrequencies[i][j][k] ; 
				  }
			  }
		  }
	  }
	  else
	  {
	  	this.markerNames = new String[0] ;
	  	this.alleleNames = new String[0][0] ;
	  	this.numberOfAlleles = new int[0] ;
	  	this.alleleFrequencies = new double[0][0][0] ;
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
	 * @see org.corehunter.data.MultiAllelicGenotypeVariantData#getAlelleFrequency(int, int, int)
	 */
  @Override
  public double getAlelleFrequency(int id, int markerIndex, int alleleIndex)
  {
	  return alleleFrequencies[id][markerIndex][alleleIndex];
  }

	/* (non-Javadoc)
	 * @see org.corehunter.data.MultiAllelicGenotypeVariantData#getNumberOfAllele(int)
	 */
  @Override
  public int getNumberOfAlleles(int markerIndex)
  {
	  return numberOfAlleles[markerIndex];
  }
  
	/* (non-Javadoc)
	 * @see org.corehunter.data.MultiAllelicGenotypeVariantData#getTotalNumberAlleles()
	 */
  @Override
  public int getTotalNumberAlleles()
  {
	  return totalNumberAlleles;
  }

	/* (non-Javadoc)
	 * @see org.corehunter.data.NamedAllelicGenotypeVariantData#getMarkerName(int)
	 */
  @Override
  public String getMarkerName(int markerIndex)
      throws ArrayIndexOutOfBoundsException
  {
	  return markerNames[markerIndex];
  }

	/* (non-Javadoc)
	 * @see org.corehunter.data.NamedAllelicGenotypeVariantData#getAlleleName(int)
	 */
  @Override
  public String getAlleleName(int markerIndex, int alleleIndex)
      throws ArrayIndexOutOfBoundsException
  {
	  return alleleNames[markerIndex][alleleIndex];
  }
}
