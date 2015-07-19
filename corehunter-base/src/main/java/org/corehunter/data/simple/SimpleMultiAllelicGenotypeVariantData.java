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

import static uno.informatics.common.Constants.INVALID_INDEX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.corehunter.data.MultiAllelicGenotypeVariantData;
import org.corehunter.data.NamedAllelicGenotypeVariantData;

import uno.informatics.common.io.FileProperties;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;

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
	  
	  if (markerNames.length != alleleNames.length)
	  	throw new IllegalArgumentException("Number of marker names entries don't match number of marker/allele names!") ;
	  
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

	/**
	 * @param names
	 * @param markerNames2
	 * @param alleleNames2
	 * @param frequencies
	 */
  public SimpleMultiAllelicGenotypeVariantData(List<String> names,
      List<String> markerNames, List<List<String>> alleleNames,
      List<List<List<Double>>> frequencies)
  {
	  super(names);

	  if (names == null)
	  	throw new IllegalArgumentException("Names not defined!") ;
	  
	  if (markerNames == null)
	  	throw new IllegalArgumentException("Marker names not defined!") ;
	  
	  if (alleleNames == null)
	  	throw new IllegalArgumentException("Allele names not defined!") ;
	  
	  if (frequencies == null)
	  	throw new IllegalArgumentException("Allele Frequency entries not deifned!") ;
	  
	  if (names.size() != frequencies.size())
	  	throw new IllegalArgumentException("Number of allele frequency entries don't match number of names!") ;
	  
	  if (markerNames.size() != alleleNames.size())
	  	throw new IllegalArgumentException("Number of marker names entries don't match number of marker/allele names!") ;
	  
	  totalNumberAlleles = 0 ;
	  
	  if (frequencies.size() > 0)
	  {
		  numberOfMarkers = markerNames.size() ;
		  
		  numberOfAlleles = new int[numberOfMarkers] ;
		  
		  this.markerNames = new String[numberOfMarkers] ;
		  this.alleleNames = new String[numberOfMarkers][] ;
		  
		  Iterator<String> iterator1 = markerNames.iterator() ;
		  Iterator<List<String>> iterator2 = alleleNames.iterator() ;
		  
		  String markerName ;
		  List<String> alleleNamesForMarker ;
		  
		  int j = 0 ;
		  
		  while (iterator1.hasNext() && iterator2.hasNext())
		  {
		  	markerName = iterator1.next() ;
		  	alleleNamesForMarker = iterator2.next() ;
		  	
			  if (alleleNamesForMarker == null)
			  	throw new IllegalArgumentException("Allele names not defined for marker : " + j + "!") ;
			  
			  this.markerNames[j] = markerName;
			  
			  numberOfAlleles[j] = alleleNamesForMarker.size() ; 
			  
			  totalNumberAlleles = totalNumberAlleles + numberOfAlleles[j] ;
			  
			  this.alleleNames[j] = new String[numberOfAlleles[j]] ;
			  
			  for (int k = 0 ; k < numberOfAlleles[j] ; ++k)
			  	this.alleleNames[j][k] = alleleNamesForMarker.get(k);
			  
			  ++j ;
		  }

		  this.alleleFrequencies = new double[frequencies.size()][numberOfMarkers][] ;
		  
		  Iterator<List<List<Double>>> iterator3 = frequencies.iterator() ;
		  Iterator<List<Double>> iterator4 ;
		  
		  int i = 0 ;
		  j = 0 ;
		  
		  List<List<Double>> markerFrequencies ;
		  List<Double> alleleFrequencies ;
		  		
		  while(iterator3.hasNext())
		  {
		  	markerFrequencies = iterator3.next() ;
		  	
			  if (numberOfMarkers != markerFrequencies.size())
			  	throw new IllegalArgumentException("Number of markers don't match allele frequencies for id : " + i + "!") ; 	
			  
			  iterator4 = markerFrequencies.iterator() ;

			  while(iterator4.hasNext())
			  {
			  	alleleFrequencies = iterator4.next() ;
			  	
				  if (numberOfAlleles[j] != alleleFrequencies.size())
				  	throw new IllegalArgumentException("Number of alleles for marker " + j + "  at entry id : " + i + "!") ;
				  
				  this.alleleFrequencies[i][j] = new double[numberOfAlleles[j]] ;
				  		
				  for (int k = 0 ; k < numberOfAlleles[j] ; ++k)
				  {
				  	this.alleleFrequencies[i][j][k] = alleleFrequencies.get(k) ; 
				  }
				  
				  ++j ;
			  }
			  ++i ;
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
  
  public final static SimpleMultiAllelicGenotypeVariantData readData(FileProperties fileProperties) throws IOException
  {
		RowReader reader = null ;

		if (fileProperties == null)
			throw new IOException("File properties not defined!") ;

		if (fileProperties.getFile() == null)
			throw new IOException("File not defined!") ;

		if (fileProperties.getFileType() == null)
			throw new IOException("File type not defined!") ;
		
		if (fileProperties.getRowHeaderPosition() > INVALID_INDEX && 
				fileProperties.getDataPosition() > INVALID_INDEX && 
				fileProperties.getDataPosition() <= fileProperties.getColumnHeaderPosition())
			throw new IOException("Column header position : " + 
					fileProperties.getDataPosition() + " must be before data position : " + fileProperties.getColumnHeaderPosition()) ;

		if (!fileProperties.getFile().exists())
			throw new IOException("File does not exist : " + fileProperties.getFile()) ;
		
		List<String> names = null ;
		List<String> markerNames = new LinkedList<String>() ;
		String markerName = null ;
		String lastMarkerName ;
		String alleleName ;
		List<String> markerAlleleNames = new LinkedList<String>() ;
		List<List<String>> alleleNames = new LinkedList<List<String>>() ;
		List<Double> alleleFrequencies = new LinkedList<Double>() ;
		List<Double> markerFrequencies = new LinkedList<Double>() ;
		List<List<List<Double>>> frequencies ;
		
		int row = 0 ;
		
		try
		{
			reader = IOUtilities.createRowReader(fileProperties) ;

			if (reader != null && reader.ready())
			{
				int columnCount = 0 ;

				if (reader.nextRow())
				{
					if (fileProperties.getRowHeaderPosition() > INVALID_INDEX) 
						while (row < fileProperties.getRowHeaderPosition() && reader.nextRow())
							++row ;	
					
					reader.nextColumn() ;
					reader.nextColumn() ;
					reader.nextColumn() ;
					
					names = reader.getRowCellsAsString() ;
					
					columnCount = names.size() ;
					
					frequencies = new ArrayList<List<List<Double>>>(columnCount) ;
					
					for (int i = 0 ; i < columnCount ; ++i)
						frequencies.add(new LinkedList<List<Double>>()) ;
					
					reader.nextRow() ;
					
					++row ;

					if (fileProperties.getDataPosition() > INVALID_INDEX) 
						while (row < fileProperties.getDataPosition() && reader.nextRow())
							++row ;		
					
					Iterator<Double> iterator ;
					int i = 0 ;
					int k = 0 ;
					
					while (reader.nextRow())
					{					
						reader.nextColumn() ;
						
						lastMarkerName = markerName ;
						markerName = reader.getCellAsString() ;
						
						reader.nextColumn() ;
						
						alleleName = reader.getCellAsString() ;
						
						reader.nextColumn() ;
							
						alleleFrequencies = reader.getRowCellsAsDouble() ;

						if (frequencies.size() != columnCount)
							throw new IOException("Rows are not all the same size!") ;
						
						if (lastMarkerName != markerName)
						{
							markerAlleleNames = new LinkedList<String>() ;
							alleleNames.add(markerAlleleNames) ;
							markerNames.add(markerName) ;
							
							k = 0 ;	
							
							iterator = alleleFrequencies.iterator() ;

							i = 0 ;
							
							while(iterator.hasNext())
							{
								markerFrequencies = new LinkedList<Double>() ;
								markerFrequencies.add(iterator.next()) ;
								
								frequencies.get(i).add(markerFrequencies) ;
								
								++i ;
							}	
						}
						else
						{
							iterator = alleleFrequencies.iterator() ;
							
							i = 0 ;
							
							while(iterator.hasNext())
							{
								frequencies.get(i).get(k).add(iterator.next()) ;
								++i ;
							}
							
							++k ;
						}
						
						markerAlleleNames.add(alleleName) ;

						++row ;
					}
				}
				else
				{
					frequencies = new ArrayList<List<List<Double>>>(0) ;
				}
			}
			else
			{
				frequencies = new ArrayList<List<List<Double>>>(0) ;
			}

			if (reader != null)
				reader.close() ;
			
			return new SimpleMultiAllelicGenotypeVariantData(names, markerNames, alleleNames, frequencies) ;

		}
		catch (IOException e)
		{
			throw new IOException("Error reading file at row : " + row + " due to " + e.getMessage(), e) ;
		}
  }
}
