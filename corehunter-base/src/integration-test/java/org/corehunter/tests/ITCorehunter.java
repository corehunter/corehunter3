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
package org.corehunter.tests;

import static org.junit.Assert.assertEquals;

import org.corehunter.Corehunter;
import org.corehunter.CorehunterArguments;
import org.jamesframework.core.subset.SubsetSolution;
import org.junit.Test;

/**
 * @author Guy Davenport
 */
public class ITCorehunter extends TestData {

    /**
     * Test method for
     * {@link org.corehunter.Corehunter#executeRandomDescent
     * (org.corehunter.DistanceMatrixData, org.jamesframework.core.problems.objectives.Objective, int)}.
     */
    //@Test
    public void testExecuteDistanceMatrix() {
        CorehunterArguments arguments = new CorehunterArguments(2);

        arguments.setDataset(DATA);

        Corehunter corehunter = new Corehunter(arguments);

        corehunter.setTimeLimit(2);

        SubsetSolution result = corehunter.execute();

        assertEquals(SUBSET1, result.getSelectedIDs());
    }

    /**
     * Test method for {@link org.corehunter.Corehunter#getTimeLimit()}.
     */
    @Test
    public void testGetTimeLimit() {

    }

    /**
     * Test method for {@link org.corehunter.Corehunter#setTimeLimit(long)}.
     */
    @Test
    public void testSetTimeLimit() {

    }

    /**
     * Test method for {@link org.corehunter.Corehunter#getSearchListener()}.
     */
    @Test
    public void testGetSearchListener() {

    }

    /**
     * Test method for
     * {@link org.corehunter.Corehunter#setSearchListener(org.jamesframework.core.search.listeners.SearchListener)} .
     */
    @Test
    public void testSetSearchListener() {

    }

}
