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

import java.util.Collection;

/**
 * Genotype variant data contains relative frequencies of markers that have two or more alleles.
 * If all the markers used in these data have two and only two alleles, such as SNP data, then it
 * is more efficient to use the subclass {@link BiAllelicGenotypeVariantData}.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public interface GenotypeVariantData extends NamedData {

    /**
     * Get the total number of markers used in this dataset.
     *
     * @return the total number of markers
     */
    public int getNumberOfMarkers();
    
    /**
     * Get the name of a marker by index, if assigned.
     *
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of markers as
     *                    returned by {@link #getNumberOfMarkers()}
     * @return marker name, <code>null</code> if no name has been set
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     */
    public String getMarkerName(int markerIndex) throws ArrayIndexOutOfBoundsException;
    
    /**
     * Get the number of alleles for a given marker.
     *
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of markers as
     *                    returned by {@link #getNumberOfMarkers()}
     * @return the number of alleles for the given marker (two or more)
     */
    public int getNumberOfAlleles(int markerIndex);
    
    /**
     * Get the total number of allele across all markers.
     *
     * @return total number of allele
     */
    public int getTotalNumberOfAlleles();
    
    /**
     * Get the name of an allele, if assigned.
     *
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of markers as
     *                    returned by {@link #getNumberOfMarkers()}
     * @param alleleIndex allele index within the range 0 to a-1, where a is the number of alleles for the given marker
     *                    as returned by {@link #getNumberOfAlleles(int)}
     * @return the allele name, <code>null</code> if no name has been set
     * @throws ArrayIndexOutOfBoundsException if the marker or allele index is out of range
     */
    public String getAlleleName(int markerIndex, int alleleIndex) throws ArrayIndexOutOfBoundsException;
    
    /**
     * Get the relative frequency of an allele for the given entry (sample/accession).
     *
     * @param id    the id of the entry, must be one of the IDs returned by {@link #getIDs()}
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of markers as
     *                    returned by {@link #getNumberOfMarkers()}
     * @param alleleIndex allele index within the range 0 to a-1, where a is the number of alleles for the given marker
     *                    as returned by {@link #getNumberOfAlleles(int)}
     * @return the relative allele frequency, <code>null</code> if missing
     */
    public Double getAlelleFrequency(int id, int markerIndex, int alleleIndex);

    /**
     * Get the average frequency of an allele for the given entries (samples/accession).
     * Missing values are treated as zero when computing the average.
     *
     * @param entryIds   the IDs of the entry, must be a subset of the IDs returned by {@link #getIDs()}
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of markers as
     *                    returned by {@link #getNumberOfMarkers()}
     * @param alleleIndex allele index within the range 0 to a-1, where a is the number of alleles for the given marker
     *                    as returned by {@link #getNumberOfAlleles(int)}
     * @return average allele frequency across the given entries
     */
    public double getAverageAlelleFrequency(Collection<Integer> entryIds, int markerIndex, int alleleIndex);

}
