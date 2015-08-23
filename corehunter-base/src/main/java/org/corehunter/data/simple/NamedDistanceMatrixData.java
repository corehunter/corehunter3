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

import static uno.informatics.common.Constants.UNKNOWN_COUNT;
import static uno.informatics.common.Constants.UNKNOWN_INDEX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.NamedSubsetData;

import uno.informatics.common.io.FileProperties;
import uno.informatics.common.io.IOUtilities;
import uno.informatics.common.io.RowReader;

/**
 * @author Guy Davenport
 */
public class NamedDistanceMatrixData extends AbstractNamedSubsetData implements DistanceMatrixData, NamedSubsetData
{
  // distance matrix
  private double[][] distances;
  
  public NamedDistanceMatrixData(String name, String[] itemNames, double[][] distances)
  {
    super(name, itemNames);
    
    this.distances = new double[distances.length][distances.length];
    
    if (getIDs().size() != distances.length)
      throw new IllegalArgumentException("Number of ids do not match number of distances!");
      
    for (int i = 0; i < distances.length; i++)
    {
      if (distances.length != distances[i].length)
        throw new IllegalArgumentException("Number of distances do not match number of ids in row  :" + i + "!");
        
      for (int j = 0; j < distances[i].length; j++)
      {
        this.distances[i][j] = distances[i][j];
      }
    }
    
    this.distances = distances;
  }
  
  public NamedDistanceMatrixData(String name, List<String> itemNames, List<List<Double>> distances)
  {
    super(name, itemNames);
    
    if (distances == null)
      throw new IllegalArgumentException("Distances not defined!");
      
    if (getNames().length != distances.size())
      throw new IllegalArgumentException("Number of ids do not match number of distances!");
      
    this.distances = new double[distances.size()][distances.size()];
    
    Iterator<List<Double>> distanceRowIterator = distances.iterator();
    List<Double> distanceRow;
    Iterator<Double> distanceIterator;
    
    int i = 0;
    int j = 0;
    
    while (distanceRowIterator.hasNext())
    {
      distanceRow = distanceRowIterator.next();
      
      distanceIterator = distanceRow.iterator();
      
      if (this.getIDs().size() != distanceRow.size())
        throw new IllegalArgumentException("Number of distances do not match number of ids in row  :" + i + "!");
        
      j = 0;
      
      while (distanceIterator.hasNext())
      {
        this.distances[i][j] = distanceIterator.next();
        
        ++j;
      }
      
      ++i;
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.corehunter.DistanceMatrixData#getDistance(int, int)
   */
  @Override
  public double getDistance(int idX, int idY)
  {
    return distances[idX][idY];
  }
  
  public static final NamedDistanceMatrixData readData(FileProperties fileProperties) throws IOException
  {
    RowReader reader = null;
    
    if (fileProperties == null)
      throw new IOException("File properties not defined!");
      
    if (fileProperties.getFile() == null)
      throw new IOException("File not defined!");
      
    if (fileProperties.getFileType() == null)
      throw new IOException("File type not defined!");
      
    if (!fileProperties.hasColumnHeader())
      throw new IOException("No column headers");
      
    if (!fileProperties.hasRowHeader())
      throw new IOException("No row headers");
      
    if (fileProperties.getRowHeaderPosition() > UNKNOWN_INDEX && fileProperties.getDataRowPosition() > UNKNOWN_INDEX
        && fileProperties.getDataRowPosition() <= fileProperties.getColumnHeaderPosition())
      throw new IOException("Column header position : " + fileProperties.getDataRowPosition()
          + " must be before data position : " + fileProperties.getColumnHeaderPosition());
          
    if (!fileProperties.getFile().exists())
      throw new IOException("File does not exist : " + fileProperties.getFile());
      
    List<String> columnNames = new LinkedList<String>();
    
    List<Double> distancesScoresRow;
    List<List<Double>> distances;
    
    int row = 0;
    int column = 0;
    
    try
    {
      reader = IOUtilities.createRowReader(fileProperties);
      
      if (reader != null && reader.ready())
      {
        int columnCount = UNKNOWN_COUNT;
        
        if (reader.nextRow())
        {
          if (fileProperties.getRowHeaderPosition() > UNKNOWN_INDEX)
          {
            while (row < fileProperties.getRowHeaderPosition() && reader.nextRow())
              ++row;
              
            column = 0;
            
            if (fileProperties.getDataColumnPosition() > UNKNOWN_INDEX)
              while (column < fileProperties.getDataColumnPosition() && reader.nextColumn())
                ++column;
                
            reader.nextColumn();
            
            columnNames = reader.getRowCellsAsString();
            
            columnCount = columnNames.size();
          }
          
          column = 0;
          
          distances = new LinkedList<List<Double>>();
          
          if (fileProperties.getDataRowPosition() > UNKNOWN_INDEX)
            while (row < fileProperties.getDataRowPosition() && reader.nextRow())
              ++row;
              
          // read first data row
          
          if (fileProperties.getDataColumnPosition() > UNKNOWN_INDEX)
            while (column < fileProperties.getDataColumnPosition() && reader.nextColumn())
              ++column;
              
          reader.nextColumn();
          
          distancesScoresRow = reader.getRowCellsAsDouble();
          
          if (columnCount == UNKNOWN_COUNT)
            columnCount = distancesScoresRow.size();
            
          if (distancesScoresRow.size() != columnCount)
            throw new IOException("Rows are not all the same size!");
            
          distances.add(distancesScoresRow);
          
          ++row;
          
          while (reader.nextRow())
          {
            column = 0;
            
            if (fileProperties.getDataColumnPosition() > UNKNOWN_INDEX)
              while (column < fileProperties.getDataColumnPosition() && reader.nextColumn())
                ++column;
                
            reader.nextColumn();
            
            distancesScoresRow = reader.getRowCellsAsDouble();
            
            if (distancesScoresRow.size() != columnCount)
              throw new IOException("Rows are not all the same size!");
              
            distances.add(distancesScoresRow);
            
            ++row;
          }
        }
        else
        {
          distances = new ArrayList<List<Double>>(0);
        }
      }
      else
      {
        distances = new ArrayList<List<Double>>(0);
      }
      
      if (reader != null)
        reader.close();
        
      return new NamedDistanceMatrixData(fileProperties.getFile().getName(), columnNames, distances);
      
    }
    catch (IOException e)
    {
      throw new IOException("Error reading file at row : " + row + " due to " + e.getMessage(), e);
    }
  }
}
