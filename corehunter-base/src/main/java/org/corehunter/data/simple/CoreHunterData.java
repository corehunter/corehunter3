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

package org.corehunter.data.simple;

import java.util.ArrayList;
import java.util.List;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeVariantData;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;
import uno.informatics.data.FeatureDataset;

/**
 * Combines all data used in Core Hunter.
 * Includes marker data, phenotypic traits and/or a precomputed distance matrix.
 * 
 * @author Herman De Beukelaer
 */
public class CoreHunterData extends SimpleNamedData {

    /**
     * Initialize Core Hunter data consisting of marker data, phenotypic traits and/or a precomputed distance matrix.
     * At least one of these should be defined (i.e. non <code>null</code>). Items should be ordered in the same way
     * across all datasets, which should all be of the same size n. If headers are specified (item names and/or unique
     * identifiers) in some or all datasets these should also be consistent across datasets. If this is not the case,
     * an exception will be thrown.
     * <p>
     * Integer IDs as required by {@link IntegerIdentifiedData} are set to [0, n-1].
     * 
     * @param markers marker data (bi- or multiallelic)
     * @param phenotypes phenotypic traits
     * @param distances precomputed distance matrix
     */
    public CoreHunterData(GenotypeVariantData markers, FeatureDataset phenotypes, DistanceMatrixData distances) {
        super(inferDatasetSize(markers, phenotypes, distances));
    }
    
    private static int inferDatasetSize(GenotypeVariantData markers,
                                        FeatureDataset phenotypes,
                                        DistanceMatrixData distances){
        // check not all undefined
        if(markers == null && phenotypes == null && distances == null){
            throw new IllegalArgumentException(
                    "At least one type of data (markers, phenotypes, distances) should be defined."
            );
        }
        // check same size
        // TODO ...
        return 0;
    }

}
