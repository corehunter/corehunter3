/*******************************************************************************
 * Copyright 2014 Herman De Beukelaer, Guy Davenport Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package org.corehunter.data;

import org.jamesframework.core.problems.datatypes.SubsetData;

/**
 * Base interface for all Genotype Variant Data. Implementation should not
 * implement this interface directly but use one of the sub-interfaces 
 * {@link MultiAllelicGenotypeVariantData} or {@link BiAllelicGenotypeVariantData}
 * 
 * @author Guy Davenport
 */
public interface GenotypeVariantData extends SubsetData
{
	/**
	 * Gets the total number of markers used in this dataset for which there are
	 * frequency values
	 *
	 * @return the total number of markers used in this datase
	 */
	public int getNumberOfMarkers();
}
