package org.corehunter.objectives.distance.biallelic;

import java.util.Set;

import org.corehunter.data.BiAllelicGenotypeVariantData;
import org.corehunter.data.MultiAllelicGenotypeVariantData;
import org.corehunter.objectives.distance.GenotypeVariantDistanceMetric;

public class CavalliSforzaEdwardsDistanceBiAllelic
    implements GenotypeVariantDistanceMetric<MultiAllelicGenotypeVariantData>
{
  
  public CavalliSforzaEdwardsDistanceBiAllelic(BiAllelicGenotypeVariantData dataset)
  {
    // TODO Auto-generated constructor stub
  }

  @Override
  public double getDistance(int idX, int idY)
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public Set<Integer> getIDs()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public MultiAllelicGenotypeVariantData getData()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}
