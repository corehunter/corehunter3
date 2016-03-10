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

package org.corehunter.tests.data.simple;

import static org.corehunter.tests.TestData.ALLELE_SCORES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.NAMES;
import static org.corehunter.tests.TestData.SET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Iterator;

import org.corehunter.data.simple.SimpleBiAllelicGenotypeVariantData;
import org.junit.Test;

import uno.informatics.common.io.FileProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Guy Davenport
 */
public class SimpleBiAllelicGenotypeVariantDataTest {

    private static final String TXT_FILE = "/biallelic.txt";

    @Test
    public void inMemoryTest() {
//        testData(new SimpleBiAllelicGenotypeVariantData(NAME, NAMES, MARKER_NAMES, ALLELE_SCORES));
    }

    @Test
    public void loadFromFileTest() {
        try {
            testData(SimpleBiAllelicGenotypeVariantData.readData(new FileProperties(
                    SimpleBiAllelicGenotypeVariantData.class.getResource(TXT_FILE).getFile(),
                    0, 1, 0, 2
            )));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void testData(SimpleBiAllelicGenotypeVariantData data) {
//        assertEquals("Ids not correct!", SET, data.getIDs());
//
//        assertEquals("Number of marker is not correct!", MARKER_NAMES.length, data.getNumberOfMarkers());
//
//        int size = data.getIDs().size();
//
//        for (int i = 0; i < size; i++) {
//            assertEquals("Marker name for " + i + " is not correct!", MARKER_NAMES[i], data.getMarkerName(i));
//        }
//
//        Iterator<Integer> iterator = data.getIDs().iterator();
//
//        int index;
//        int i = 0;
//
//        while (iterator.hasNext()) {
//            index = iterator.next();
//
//            assertEquals("Marker name for " + index + " is not correct!", NAMES[i], data.getName(index));
//
//            for (int j = 0; j < size; j++) {
//                assertEquals("Alele[" + index + "][" + j + "] not correct!",
//                             ALLELE_SCORES[index][j], data.getAlleleScore(index, j));
//            }
//
//            ++i;
//        }
    }
}
