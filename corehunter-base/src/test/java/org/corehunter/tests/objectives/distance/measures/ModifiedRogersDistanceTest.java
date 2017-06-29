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

package org.corehunter.tests.objectives.distance.measures;

import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.MODIFIED_ROGERS_DISTANCES;
import static org.corehunter.tests.TestData.MODIFIED_ROGERS_DISTANCES_CEIL_MISSING;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PRECISION;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.corehunter.data.CoreHunterData;
import org.corehunter.data.simple.SimpleFrequencyGenotypeData;
import org.corehunter.objectives.distance.measures.MissingValuesPolicy;
import org.corehunter.objectives.distance.measures.ModifiedRogersDistance;
import org.junit.Test;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class ModifiedRogersDistanceTest {

    @Test
    public void test() {
        
        SimpleFrequencyGenotypeData geno = new SimpleFrequencyGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );
        CoreHunterData data = new CoreHunterData(geno);

        ModifiedRogersDistance distanceMetric = new ModifiedRogersDistance();

        Iterator<Integer> iteratorX = data.getIDs().iterator();
        Iterator<Integer> iteratorY;

        while (iteratorX.hasNext()) {
            int idX = iteratorX.next();
            iteratorY = data.getIDs().iterator();
            while (iteratorY.hasNext()) {
                int idY = iteratorY.next();
                assertEquals(
                        "Distance[" + idX + "][" + idY + "] not correct!",
                        MODIFIED_ROGERS_DISTANCES[idX][idY],
                        distanceMetric.getDistance(idX, idY, data),
                        PRECISION);
            }
        }
    }
    
    @Test
    public void testCeilMissing() {
        
        SimpleFrequencyGenotypeData geno = new SimpleFrequencyGenotypeData(
                NAME, HEADERS_NON_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES, ALLELE_FREQUENCIES
        );
        CoreHunterData data = new CoreHunterData(geno);

        ModifiedRogersDistance distanceMetric = new ModifiedRogersDistance();
        distanceMetric.setMissingValuesPolicy(MissingValuesPolicy.CEIL);

        Iterator<Integer> iteratorX = data.getIDs().iterator();
        Iterator<Integer> iteratorY;

        while (iteratorX.hasNext()) {
            int idX = iteratorX.next();
            iteratorY = data.getIDs().iterator();
            while (iteratorY.hasNext()) {
                int idY = iteratorY.next();
                assertEquals(
                        "Distance[" + idX + "][" + idY + "] not correct!",
                        MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[idX][idY],
                        distanceMetric.getDistance(idX, idY, data),
                        PRECISION);
            }
        }
    }
    
}
