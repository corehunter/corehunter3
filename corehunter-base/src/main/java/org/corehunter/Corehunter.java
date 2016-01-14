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

import org.corehunter.data.BiAllelicGenotypeVariantData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.MultiAllelicGenotypeVariantData;
import org.corehunter.distance.GowersDistanceMatrixGenerator;
import org.corehunter.objectives.biallelic.CoverageBiAllelic;
import org.corehunter.objectives.biallelic.HetrozygousLociDiversityBiAllelic;
import org.corehunter.objectives.biallelic.NumberEffectiveAllelesBiAllelic;
import org.corehunter.objectives.biallelic.ProportionNonInformativeAllelesBiAllelic;
import org.corehunter.objectives.biallelic.ShannonsDiversityBiAllelic;
import org.corehunter.objectives.distance.AverageDistanceObjective;
import org.corehunter.objectives.distance.GenotypeVariantDistanceMetric;
import org.corehunter.objectives.distance.biallelic.CavalliSforzaEdwardsDistanceBiAllelic;
import org.corehunter.objectives.distance.biallelic.ModifiedRogersDistanceBiAllelic;
import org.corehunter.objectives.distance.multiallelic.CavalliSforzaEdwardsDistanceMultiAllelic;
import org.corehunter.objectives.distance.multiallelic.ModifiedRogersDistanceMultiAllelic;
import org.corehunter.objectives.multiallelic.CoverageMultiAllelic;
import org.corehunter.objectives.multiallelic.HetrozygousLociDiversityMultiAllelic;
import org.corehunter.objectives.multiallelic.NumberEffectiveAllelesMultiAllelic;
import org.corehunter.objectives.multiallelic.ProportionNonInformativeAllelesMultiAllelic;
import org.corehunter.objectives.multiallelic.ShannonsDiversityMultiAllelic;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.RandomDescent;
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
  
  public SubsetSolution execute(CorehunterListener listener)
  {
    Search<SubsetSolution> search = null ; 
    
    if (arguments.getDataset() == null)
      throw new IllegalArgumentException("Dataset not defined!") ;
        
    // Phenotypic data where the distance matrix is generated before 
    if (arguments.getDataset() instanceof FeatureDataset)
    {      
      search = createGowersDistanceSearch((FeatureDataset)arguments.getDataset(), arguments, listener) ;
    }
    else
    {
      // Any data for which a distance matrix has already been generated
      if (arguments.getDataset() instanceof DistanceMatrixData)
      {      
        search = createDistanceSearch((DistanceMatrixData)arguments.getDataset(), arguments) ;
      }
      else
      {
        // MultiAllelicGenotypeVariantData
        if (arguments.getDataset() instanceof MultiAllelicGenotypeVariantData)
        {      
          search = createMultiAllelicGenotypeSearch((MultiAllelicGenotypeVariantData)arguments.getDataset(), arguments) ;
        }
        else
        {
          // BiAllelicGenotypeVariantData
          if (arguments.getDataset() instanceof BiAllelicGenotypeVariantData)
          {      
            search = createBiAllelicGenotypeSearch((BiAllelicGenotypeVariantData)arguments.getDataset(), arguments) ;
          }
          else
          {
            // TODO support for combined genotype / phenotype, this could be via DistanceMatrixData or some other specific dataset type
            throw new IllegalArgumentException("Dataset type not supported by Core Hunter : " + arguments.getDataset().getClass()) ;
          }
        }
      }
    }
    
    search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
    
    if (listener != null)
      search.addSearchListener(listener);
      
    // start search
    search.start();
    
    // dispose search
    search.dispose();
    
    return search.getBestSolution(); 
  }

  protected Search<SubsetSolution> createGowersDistanceSearch(FeatureDataset dataset, CorehunterArguments arguments, CorehunterListener listener)
  {
    GowersDistanceMatrixGenerator generator = new GowersDistanceMatrixGenerator(
        (FeatureDataset) arguments.getDataset());
        
    listener.preprocessingStarted("Generating distance matrix using gowers distance.");
    
    DistanceMatrixData distanceMatrix = generator.generateDistanceMatrix() ;
    
    listener.preprocessingStopped("Distance matrix generated.");
    
    return createDistanceSearch(distanceMatrix, arguments) ;
  }
  
  protected Search<SubsetSolution> createMultiAllelicGenotypeSearch(MultiAllelicGenotypeVariantData dataset,
      CorehunterArguments arguments)
  {
    switch (arguments.getObjective())
    {
      case CE:
        return createMultiAllelicGenotypeDistanceSearch(new CavalliSforzaEdwardsDistanceMultiAllelic(dataset), dataset, arguments) ;
      case CV:
        return createMultiAllelicGenotypeSearch(new CoverageMultiAllelic(), dataset, arguments) ;
      case GD:
        throw new IllegalArgumentException("Invalid objective for multiallelic data : " + arguments.getObjective()) ;
      case HE:
        return createMultiAllelicGenotypeSearch(new HetrozygousLociDiversityMultiAllelic(), dataset, arguments) ;
      case MR:
        return createMultiAllelicGenotypeDistanceSearch(new ModifiedRogersDistanceMultiAllelic(dataset), dataset, arguments) ;
      case NE:
        return createMultiAllelicGenotypeSearch(new NumberEffectiveAllelesMultiAllelic(), dataset, arguments) ;
      case PN:
        return createMultiAllelicGenotypeSearch(new ProportionNonInformativeAllelesMultiAllelic(), dataset, arguments) ;
      case SH:
        return createMultiAllelicGenotypeSearch(new ShannonsDiversityMultiAllelic(), dataset, arguments) ;
      default:
        throw new IllegalArgumentException("Unknown objective : " + arguments.getObjective()) ;   
    }
  }
  

  private Search<SubsetSolution> createBiAllelicGenotypeSearch(BiAllelicGenotypeVariantData dataset,
      CorehunterArguments arguments)
  {
    switch (arguments.getObjective())
    {
      case CE:
        return createBiAllelicGenotypeDistanceSearch(new CavalliSforzaEdwardsDistanceBiAllelic(dataset), dataset, arguments) ;
      case CV:
        return createBiAllelicGenotypeSearch(new CoverageBiAllelic(), dataset, arguments) ;
      case GD:
        throw new IllegalArgumentException("Invalid objective for multiallelic data : " + arguments.getObjective()) ;
      case HE:
        return createBiAllelicGenotypeSearch(new HetrozygousLociDiversityBiAllelic(), dataset, arguments) ;
      case MR:
        return createBiAllelicGenotypeDistanceSearch(new ModifiedRogersDistanceBiAllelic(dataset), dataset, arguments) ;
      case NE:
        return createBiAllelicGenotypeSearch(new NumberEffectiveAllelesBiAllelic(), dataset, arguments) ;
      case PN:
        return createBiAllelicGenotypeSearch(new ProportionNonInformativeAllelesBiAllelic(), dataset, arguments) ;
      case SH:
        return createBiAllelicGenotypeSearch(new ShannonsDiversityBiAllelic(), dataset, arguments) ;
      default:
        throw new IllegalArgumentException("Unknown objective : " + arguments.getObjective()) ;   
    }
  }
  
  private Search<SubsetSolution> createMultiAllelicGenotypeDistanceSearch(
      GenotypeVariantDistanceMetric<MultiAllelicGenotypeVariantData> genotypeVariantDistanceMetric,
      MultiAllelicGenotypeVariantData dataset, CorehunterArguments arguments)
  {
    return createDistanceSearch(genotypeVariantDistanceMetric, arguments) ;
  }

  private Search<SubsetSolution> createMultiAllelicGenotypeSearch(
      Objective<SubsetSolution, MultiAllelicGenotypeVariantData> objective,
      MultiAllelicGenotypeVariantData dataset, CorehunterArguments arguments)
  {
    SubsetProblem<MultiAllelicGenotypeVariantData> problem;
    
    if (arguments.getMinimumSubsetSize() == arguments.getMaximumSubsetSize())
      problem = new SubsetProblem<MultiAllelicGenotypeVariantData>(dataset, objective, arguments.getMinimumSubsetSize());
    else
      problem = new SubsetProblem<MultiAllelicGenotypeVariantData>(dataset, objective, arguments.getMinimumSubsetSize(),
          arguments.getMaximumSubsetSize());
          
    RandomDescent<SubsetSolution> search = new RandomDescent<SubsetSolution>(problem,
        new SinglePerturbationNeighbourhood());
    
    return search ;
  }
  
  private Search<SubsetSolution> createBiAllelicGenotypeDistanceSearch(
      GenotypeVariantDistanceMetric<MultiAllelicGenotypeVariantData> genotypeVariantDistanceMetric,
      BiAllelicGenotypeVariantData dataset, CorehunterArguments arguments)
  {
    return createDistanceSearch(genotypeVariantDistanceMetric, arguments) ;
  }

  private Search<SubsetSolution> createBiAllelicGenotypeSearch(
      Objective<SubsetSolution, BiAllelicGenotypeVariantData> objective,
      BiAllelicGenotypeVariantData dataset, CorehunterArguments arguments)
  {
    SubsetProblem<BiAllelicGenotypeVariantData> problem;
    
    if (arguments.getMinimumSubsetSize() == arguments.getMaximumSubsetSize())
      problem = new SubsetProblem<BiAllelicGenotypeVariantData>(dataset, objective, arguments.getMinimumSubsetSize());
    else
      problem = new SubsetProblem<BiAllelicGenotypeVariantData>(dataset, objective, arguments.getMinimumSubsetSize(),
          arguments.getMaximumSubsetSize());
          
    RandomDescent<SubsetSolution> search = new RandomDescent<SubsetSolution>(problem,
        new SinglePerturbationNeighbourhood());
    
    return search ;
  }
  
  private Search<SubsetSolution> createDistanceSearch(DistanceMatrixData dataset, CorehunterArguments arguments)
  {
    SubsetProblem<DistanceMatrixData> problem;
    
    if (arguments.getMinimumSubsetSize() == arguments.getMaximumSubsetSize())
      problem = new SubsetProblem<DistanceMatrixData>(dataset, new AverageDistanceObjective(), arguments.getMinimumSubsetSize());
    else
      problem = new SubsetProblem<DistanceMatrixData>(dataset, new AverageDistanceObjective(), arguments.getMinimumSubsetSize(),
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
