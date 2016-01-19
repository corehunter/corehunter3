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
 * Data contains the allele score for markers have two and only two alleles. If any of the markers used in these data
 * that three or more alleles, such as classic SSR data, then {@link MultiAllelicGenotypeVariantData} must be used
 *
 * @author Guy Davenport
 */
public interface BiAllelicGenotypeVariantData extends GenotypeVariantData {

    /**
     * Gets the allele score of the marker for the given entity. The marker scores are encoded as 0, 1 or 2. 0 is used
     * for homozygote for one allele and 2 is used for homozygote for one allele. 1 is used to denote heterozygotes. It
     * is not important which allele is encoded as 0 or 2, as long as it is consistent for a specific marker.
     *
     * @param id    the id of the entity, must be one of the IDs returned by {@link #getIDs()}
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of markers and
     *                    is returned by {@link #getNumberOfMarkers()}
     * @return the allele score of the marker for the given entity, must be 0, 1 or 2.
     */
    public int getAlleleScore(int id, int markerIndex);
}
