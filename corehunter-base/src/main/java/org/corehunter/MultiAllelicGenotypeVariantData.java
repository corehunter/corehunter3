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
package org.corehunter;

/**
 * Data contains relative frequencies of markers that two or more alleles. If
 * all the markers used in these data have two and only two alleles, such as
 * classic SNP data, then it more efficient to use
 * {@link BiAllelicGenotypeVariantData}
 * 
 * @author Guy Davenport
 */
public interface MultiAllelicGenotypeVariantData extends GenotypeVariantData
{
	/**
	 * Gets the relative frequency of an allele for the given entity (sample)
	 * 
	 * @param entityId
	 *          the id of the entity, must be one of the ids returned by
	 *          {@link #getIDs()}
	 * @param markerIndex
	 *          the index of the marker within the range 0 to n-1, where n is the
	 *          total number of markers and is returned by
	 *          {@link #getNumberOfMarkers()}
	 * @param alleleIndex
	 *          the index of the allele for the given marker within the range 0 to
	 *          m-1, where m is the total number of alleles for a given marker and
	 *          is returned by {@link #getNumberOfAllele(int)}
	 * @return the relative frequency of an allele for a given marker in a given
	 *         entity
	 */
	public double getAlelleFrequency(int id, int markerIndex, int alleleIndex);

	/**
	 * Gets the number of alleles for a given marker
	 * 
	 * @param markerIndex
	 *          the index of the marker within the range 0 to n-1, where n is the
	 *          total number of markers and is returned by
	 *          {@link #getNumberOfMarkers()}
	 * @return the number of alleles for a given marker
	 */
	public int getNumberOfAllele(int markerIndex);
}
