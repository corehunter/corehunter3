/*--------------------------------------------------------------*/
/* Licensed to the Apache Software Foundation (ASF) under one   */
/* or more contributor license agreements.  See the NOTICE file */
/* distributed with this work for additional information        */
/* regarding copyright ownership.  The ASF licenses this file   */
/* to you under the Apache License, Version 2.0 (the            */
/* "License"); you may not use this file except in compliance   */
/* with the License.  You may obtain a copy of the License at   */
/*                                                              */
/*   http://www.apache.org/licenses/LICENSE-2.0                 */
/*                                                              */
/* Unless required by applicable law or agreed to in writing,   */
/* software distributed under the License is distributed on an  */
/* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       */
/* KIND, either express or implied.  See the License for the    */
/* specific language governing permissions and limitations      */
/* under the License.                                           */
/*--------------------------------------------------------------*/

package org.corehunter.data;

/**
 * Default genotype data with a fixed number of allele observations per individual, for each marker.
 * Values are allele references, i.e. names, numbers, or any other token used to identify the detected
 * alleles. Common cases are those with one or two observed alleles per individual and marker, e.g. suited
 * for homozygous/haploid and diploid data, respectively. The number of observed alleles per individual
 * may be different for different markers.
 *
 * @author Herman De Beukelaer
 */
public interface DefaultGenotypeData extends FrequencyGenotypeData {

    /**
     * Get the number of observed alleles per individual for a given marker.
     * 
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of
     *                    markers as returned by {@link #getNumberOfMarkers()}
     * @return the number of allele observations per individual for the given marker (one or more)
     */
    public int getNumberOfObservedAllelesPerIndividual(int markerIndex);
    
    /**
     * Get the reference of the i-th observed allele for the given marker, in the given entry.
     * Returns <code>null</code> if no allele was detected here.
     *
     * @param id the id of the entry, must be one of the IDs returned by {@link #getIDs()}
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of markers
     *                    as returned by {@link #getNumberOfMarkers()}
     * @param i observation index within the range 0 to k-1, where k is the number of observed alleles per individual,
     *          for the given marker, as returned by {@link #getNumberOfObservedAllelesPerIndividual(int)}
     * @return observed allele; <code>null</code> if missing
     */
    public String getObservedAllele(int id, int markerIndex, int i);
    
}
