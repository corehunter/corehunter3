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

package org.corehunter.tests.objectives.distance.multiallelic;

import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.CAVALLI_SFORZA_EDWARDS_DISTANCES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.HEADERS;
import static org.corehunter.tests.TestData.PRECISION;
import static org.corehunter.tests.TestData.SET;
import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES;

import java.util.Iterator;

import org.corehunter.data.simple.SimpleGenotypeVariantData;
import org.corehunter.objectives.distance.multiallelic.CavalliSforzaEdwardsDistanceMultiAllelic;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class CavalliSforzaEdwardsDistanceMultiAllelicTest {

    @Test
    public void test() {
        SimpleGenotypeVariantData data = new SimpleGenotypeVariantData(
                NAME, HEADERS, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );

        CavalliSforzaEdwardsDistanceMultiAllelic distanceMetric
                = new CavalliSforzaEdwardsDistanceMultiAllelic(data);

        assertEquals("Data is not correct!", data, distanceMetric.getData());

        assertEquals("Ids not correct!", SET, distanceMetric.getIDs());

        Iterator<Integer> iteratorX = distanceMetric.getIDs().iterator();
        Iterator<Integer> iteratorY;

        Integer idX;
        Integer idY;

        int i = 0;
        int j = 0;

        while (iteratorX.hasNext()) {
            idX = iteratorX.next();

            iteratorY = distanceMetric.getIDs().iterator();

            j = 0;

            while (iteratorY.hasNext()) {
                idY = iteratorY.next();

                assertEquals("Distance[" + i + "][" + j + "] not correct!",
                             CAVALLI_SFORZA_EDWARDS_DISTANCES[i][j], distanceMetric.getDistance(idX, idY), PRECISION);

                ++j;
            }

            ++i;
        }
    }

}
