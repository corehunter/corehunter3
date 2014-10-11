/*******************************************************************************
 * Copyright 2014 Guy Davenport
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
package org.corehunter.objectives;

import java.util.Set;

import org.corehunter.DistanceMatrixData;
import org.corehunter.GenotypeVariantData;

/**
 * @author Guy Davenport
 *
 */
public abstract class AbstractGenotypeVariantDistanceMetric<
	GenotypeVariantDataType extends GenotypeVariantData> implements DistanceMatrixData
{
	private GenotypeVariantDataType data ;

	public AbstractGenotypeVariantDistanceMetric(GenotypeVariantDataType data)
  {
	  super();
	  
	  setData(data) ;
  }

	/* (non-Javadoc)
	 * @see org.jamesframework.core.problems.datatypes.SubsetData#getIDs()
	 */
  @Override
  public final Set<Integer> getIDs()
  {
	  return data.getIDs();
  }

	public final GenotypeVariantDataType getData()
	{
		return data;
	}

	protected final void setData(GenotypeVariantDataType data)
	{
		this.data = data;
	}
}
