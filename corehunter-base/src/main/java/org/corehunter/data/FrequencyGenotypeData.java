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

import java.io.IOException;
import java.nio.file.Path;
import org.jamesframework.core.subset.SubsetSolution;
import uno.informatics.data.Data;
import uno.informatics.data.io.FileType;

/**
 * Frequency genotype data is the most general type of genotype data, containing
 * relative frequencies of markers that have any number of alleles. If all the markers
 * used in these data have two and only two alleles, such as SNP data, then it is more
 * efficient to use the subtype {@link BiAllelicGenotypeData}. For datasets with a fixed
 * number of allele observations per individuals, for each marker, the subtype
 * {@link DefaultGenotypeData} can be used.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public interface FrequencyGenotypeData extends Data {

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
     * Write data to file.
     * 
     * @param filePath file path
     * @param fileType {@link FileType#TXT} or {@link FileType#CSV}
     * @throws IOException if the data can not be written to the file
     */
    default public void writeData(Path filePath, FileType fileType) throws IOException {
        // create auxiliary solution in which all IDs are selected
        SubsetSolution all = new SubsetSolution(getIDs());
        all.selectAll();
        // write selected (all)
        writeData(filePath, fileType, all, true, false, false);
    }
    
    /**
     * Write selected data to file.
     * 
     * @param filePath file path
     * @param fileType {@link FileType#TXT} or {@link FileType#CSV}
     * @param solution the solution to subset the data (selected core)
     * @param includeSelected includes selected accessions in output file
     * @param includeUnselected includes unselected accessions output file
     * @param includeIndex includes accession indices, i.e. the internal integer IDs used by the solution
     * @throws IOException if the data can not be written to the file
     */
    public void writeData(Path filePath, FileType fileType, SubsetSolution solution,
                          boolean includeSelected, boolean includeUnselected, boolean includeIndex)
                          throws IOException;

}
