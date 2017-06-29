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
 * Biallelic data contains the allele score (0/1/2) for markers that have two and only two alleles.
 * If any of the markers have three or more alleles, such as classic SSR data, then the general
 * {@link FrequencyGenotypeData} or {@link DefaultGenotypeData} must be used. Biallelic datasets
 * may be treated separately to reduce computation time, for example in the objectives.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public interface BiAllelicGenotypeData extends FrequencyGenotypeData {

    /**
     * Get the allele score of the marker for the given entry. The marker scores are encoded as 0, 1 or 2.
     * 0 is used for homozygote for one allele and 2 is used for homozygote for the other allele. 1 is used
     * to denote heterozygotes. It is not important which allele is encoded as 0 or 2, as long as it is consistent
     * for a specific marker. If the allele score is missing this method returns <code>null</code>.
     *
     * @param id    the id of the entry, must be one of the IDs returned by {@link #getIDs()}
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of markers and
     *                    is returned by {@link #getNumberOfMarkers()}
     * @return the allele score of the marker for the given entry (0, 1 or 2); <code>null</code> if missing
     */
    public Integer getAlleleScore(int id, int markerIndex);
    
}
