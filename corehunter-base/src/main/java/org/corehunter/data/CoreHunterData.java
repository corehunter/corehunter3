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

import java.util.Arrays;
import java.util.Objects;

import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;

import uno.informatics.data.Data;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.pojo.DataPojo;

/**
 * Combines all data used in Core Hunter. Includes marker data, phenotypic
 * traits and/or a precomputed distance matrix.
 * 
 * @author Herman De Beukelaer
 */
public class CoreHunterData extends DataPojo implements IntegerIdentifiedData {

    private final GenotypeVariantData genotypicData;
    private final FeatureData phenotypicData;
    private final DistanceMatrixData distancesData;

    /**
     * Initialize Core Hunter data consisting of genotypic data, phenotypic
     * traits and/or a precomputed distance matrix. At least one of these should
     * be defined (i.e. non <code>null</code>). Items should be ordered in the
     * same way across all datasets, which should all be of the same size n. If
     * headers are specified (item names and/or unique identifiers) in some or
     * all datasets these should also be consistent across datasets. If this is
     * not the case, an exception will be thrown.
     * <p>
     * Integer IDs as required by {@link IntegerIdentifiedData} are set to [0,
     * n-1]. The name of the dataset is set to "Core Hunter data".
     * 
     * @param genotypicData
     *            genotypic data (bi- or multiallelic)
     * @param phenotypicData
     *            phenotypic traits
     * @param distancesData
     *            precomputed distance matrix
     */
    public CoreHunterData(GenotypeVariantData genotypicData, FeatureData phenotypicData,
            DistanceMatrixData distancesData) {
        super("Core Hunter data", mergeHeaders(genotypicData, phenotypicData, distancesData));
        // store data
        this.genotypicData = genotypicData;
        this.phenotypicData = phenotypicData;
        this.distancesData = distancesData;
    }

    /**
     * Initialize Core Hunter data consisting only of genotypic data.
     * <p>
     * Integer IDs as required by {@link IntegerIdentifiedData} are set to [0,
     * n-1]. The name of the dataset is set to "Core Hunter data".
     * 
     * @param genotypicData
     *            genotypic data (bi- or multiallelic)
     */
    public CoreHunterData(GenotypeVariantData genotypicData) {
        this(genotypicData, null, null);
    }

    /**
     * Initialize Core Hunter data consisting only of phenotypicData data.
     * <p>
     * Integer IDs as required by {@link IntegerIdentifiedData} are set to [0,
     * n-1]. The name of the dataset is set to "Core Hunter data".
     * 
     * @param phenotypicData
     *            phenotypic traits
     */
    public CoreHunterData(FeatureData phenotypicData) {
        this(null, phenotypicData, null);
    }

    /**
     * Initialize Core Hunter data consisting only of distances data.
     * <p>
     * Integer IDs as required by {@link IntegerIdentifiedData} are set to [0,
     * n-1]. The name of the dataset is set to "Core Hunter data".
     * 
     * @param distancesData
     *            precomputed distance matrix
     */
    public CoreHunterData(DistanceMatrixData distancesData) {
        this(null, null, distancesData);
    }

    public GenotypeVariantData getGenotypicData() {
        return genotypicData;
    }

    public FeatureData getPhenotypicData() {
        return phenotypicData;
    }

    public DistanceMatrixData getDistancesData() {
        return distancesData;
    }

    private static int inferSize(GenotypeVariantData genotypicData, FeatureData phenotypicData,
            DistanceMatrixData distancesData) {

        // check not all undefined
        if (genotypicData == null && phenotypicData == null && distancesData == null) {
            throw new IllegalArgumentException(
                    "At least one type of data (genotypic, phenotypic, distances) should be defined.");
        }
        // check same size
        int[] sizes = Arrays.asList(genotypicData, phenotypicData, distancesData).stream().filter(Objects::nonNull)
                .mapToInt(Data::getSize).distinct().toArray();
        boolean sameSize = (sizes.length == 1);

        if (!sameSize) {
            if (sizes.length == 2) {
                throw new IllegalArgumentException(
                        String.format("Provided datasets have different sizes. %d %d", sizes[0], sizes[1]));
            } else {
                if (sizes.length == 3) {
                    throw new IllegalArgumentException(String.format("Provided datasets have different sizes. %d %d %d",
                            sizes[0], sizes[1], sizes[2]));
                }
            }
        }

        return sizes[0];

    }

    // assumes that sizes have already been checked
    private static SimpleEntity[] mergeHeaders(GenotypeVariantData genotypicData, FeatureData phenotypicData,
            DistanceMatrixData distancesData) {

        int size = inferSize(genotypicData, phenotypicData, distancesData);

        SimpleEntity[] headers = Arrays.asList(genotypicData, phenotypicData, distancesData).stream()
                .filter(Objects::nonNull)
                // extract headers from each dataset
                .map(data -> {
                    SimpleEntity[] h = new SimpleEntity[data.getSize()];
                    for (int i = 0; i < h.length; i++) {
                        h[i] = data.getHeader(i);
                    }
                    return h;
                })
                // merge and check for inconsistencies
                .reduce((headers1, headers2) -> {
                    SimpleEntity[] h = new SimpleEntity[headers1.length];
                    for (int i = 0; i < h.length; i++) {
                        SimpleEntity h1 = headers1[i];
                        SimpleEntity h2 = headers2[i];
                        if (h1 == null) {
                            h[i] = h2;
                        } else if (h2 == null) {
                            h[i] = h1;
                        } else {
                            // both datasets have header for this item: check
                            // consistency
                            if (h1.equals(h2)) {
                                // equal means same id: check name
                                if (h1.getName() != null && h2.getName() != null
                                        && !h1.getName().equals(h2.getName())) {
                                    throw new IllegalArgumentException(String.format(
                                            "Headers do not match for item %d. "
                                                    + "Got same id %s but different names %s and %s.",
                                            i, h1.getUniqueIdentifier(), h1.getName(), h2.getName()));
                                }
                                // store (make sure to pick the one which has a
                                // name set, if not both)
                                if (h1.getName() != null) {
                                    h[i] = h1;
                                } else {
                                    h[i] = h2;
                                }
                            } else {
                                throw new IllegalArgumentException(
                                        String.format("Headers do not match for item %d. Got different ids %s and %s.",
                                                i, h1.getUniqueIdentifier(), h2.getUniqueIdentifier()));
                            }
                        }
                    }
                    return h;
                }).get();

        // create headers if not provided in any dataset
        if (Arrays.stream(headers).allMatch(Objects::isNull)) {
            headers = DataPojo.updateOrCreateHeaders(null, size);
        }

        return headers;

    }

}
