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

import uno.informatics.data.Data;

/**
 * Genotype variant data contains relative frequencies of markers that have two or more alleles.
 * If all the markers used in these data have two and only two alleles, such as SNP data, then it
 * is more efficient to use the subclass {@link BiAllelicGenotypeVariantData}.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public interface GenotypeVariantData extends Data {

    /**
     * Get the total number of markers used in this dataset.
     *
     * @return the total number of markers
     */
    public int getNumberOfMarkers();
    
    /**
     * Get the name of a marker by index, if assigned.
     *
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of
     *                    markers as returned by {@link #getNumberOfMarkers()}
     * @return marker name, <code>null</code> if no name has been set
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     */
    public String getMarkerName(int markerIndex) throws ArrayIndexOutOfBoundsException;
    
    /**
     * Get the number of alleles for a given marker.
     *
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of
     *                    markers as returned by {@link #getNumberOfMarkers()}
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
    public Double getAlleleFrequency(int id, int markerIndex, int alleleIndex);
    
    /**
     * Indicates whether there are missing values (frequencies)
     * for the given entry (sample/accession) at the given marker.
     * 
     * @param id    the id of the entry, must be one of the IDs returned by {@link #getIDs()}
     * @param markerIndex the index of the marker within the range 0 to n-1, where n is the total number of
     *                    markers as returned by {@link #getNumberOfMarkers()}
     * @return <code>true</code> if some or all values are missing for the given marker in the given entry
     */
    public boolean hasMissingValues(int id, int markerIndex);

    /**
     * Get the average genotype for the given entries (samples/accessions).
     * The default implementation relies on {@link #getAlleleFrequency(int, int, int)} to access
     * individual frequencies and treats missing values as zero when computing the average.
     *
     * @param itemIds the IDs of the entry, must be a subset of the IDs returned by {@link #getIDs()}
     * @return average allele frequency across the given entries
     */
    public default double[][] getAverageGenotype(Collection<Integer> itemIds) {
        double[][] average = new double[getNumberOfMarkers()][];
        for(int m = 0; m < average.length; m++){
            average[m] = new double[getNumberOfAlleles(m)];
            for(int a = 0; a < average[m].length; a++){
                double freqSum = 0.0;
                for(int id : itemIds){
                    Double freq = getAlleleFrequency(id, m, a);
                    freqSum += (freq == null ? 0.0 : freq);
                }
                average[m][a] = freqSum/itemIds.size();
            }
        }
        return average;
    }

}
