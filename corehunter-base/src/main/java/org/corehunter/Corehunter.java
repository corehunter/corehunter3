/*******************************************************************************
 * Copyright 2014 Herman De Beukelaer, Guy Davenport Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package org.corehunter;

import java.util.concurrent.TimeUnit;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.distance.GowersDistanceMatrixGenerator;
import org.corehunter.objectives.distance.AverageDistanceObjective;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;

import uno.informatics.common.model.FeatureDataset;

/**
 * Provides support for executing pre-defined core subset searches. Can be
 * re-used.
 * 
 * @author Guy Davenport
 */
public class Corehunter
{
  private CorehunterArguments arguments;
  private long                timeLimit = 60;
  
  public Corehunter(CorehunterArguments arguments)
  {
    this.arguments = arguments;
    
    validate();
  }
  
  public final SubsetSolution execute()
  {
    return execute(null);
  }
  
  public SubsetSolution execute(SearchListener<SubsetSolution> searchListener)
  {
    Search<SubsetSolution> search = null ; 
        
    if (arguments.getDataset() instanceof FeatureDataset)
    {      
      search = createAverageGowersDistanceSearch(arguments) ;
          
      search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
      
      if (searchListener != null)
        search.addSearchListener(searchListener);
        
      // start search
      search.start();
      
      // dispose search
      search.dispose();
      

    }
    else
    {
      // TOOD other types of data
      
      return null;
    }
    
    if (search != null)
      return search.getBestSolution();
    else
      return null ;
      
    
  }
  
  protected Search<SubsetSolution> createAverageGowersDistanceSearch(CorehunterArguments arguments)
  {
    GowersDistanceMatrixGenerator generator = new GowersDistanceMatrixGenerator(
        (FeatureDataset) arguments.getDataset());
        
    DistanceMatrixData data = generator.generateDistanceMatrix();

    Objective<SubsetSolution, DistanceMatrixData> objective = new AverageDistanceObjective();
    
    SubsetProblem<DistanceMatrixData> problem;
    
    if (arguments.getMinimumSubsetSize() == arguments.getMaximumSubsetSize())
      problem = new SubsetProblem<DistanceMatrixData>(data, objective, arguments.getMinimumSubsetSize());
    else
      problem = new SubsetProblem<DistanceMatrixData>(data, objective, arguments.getMinimumSubsetSize(),
          arguments.getMaximumSubsetSize());
          
    RandomDescent<SubsetSolution> search = new RandomDescent<SubsetSolution>(problem,
        new SinglePerturbationNeighbourhood());
    
    return search ;
  }

  public final long getTimeLimit()
  {
    return timeLimit;
  }
  
  public final void setTimeLimit(long timeLimit)
  {
    this.timeLimit = timeLimit;
  }
  
  private void validate() throws IllegalArgumentException
  {
    if (arguments.getDataset() instanceof FeatureDataset)
    {
      if (!CorehunterObjective.GD.equals(arguments.getObjective()))
      {
        throw new IllegalArgumentException(
            CorehunterObjective.GD.getName() + " must be used for characterisation datasets!");
      }
    }
  }
}
