package org.corehunter.objectives.distance;

import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeVariantData;

public interface GenotypeVariantDistanceMetric<GenotypeVariantDataType extends GenotypeVariantData> 
  extends DistanceMatrixData
{
  public GenotypeVariantDataType getData() ;
}
