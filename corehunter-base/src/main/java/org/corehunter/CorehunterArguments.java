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

package org.corehunter;

import org.corehunter.data.NamedData;

public class CorehunterArguments {

    private int minimumSubsetSize;

    private int maximumSubsetSize;

    private CorehunterObjective objective;

    private NamedData dataset;

    public CorehunterArguments(int subsetSize) {
        this(subsetSize, subsetSize);
    }

    public CorehunterArguments(int minimumSubsetSize, int maximumSubsetSize) {
        super();
        this.minimumSubsetSize = minimumSubsetSize;
        this.maximumSubsetSize = maximumSubsetSize;
    }

    public final int getMinimumSubsetSize() {
        return minimumSubsetSize;
    }

    public final void setMinimumSubsetSize(int minimumSubsetSize) {
        this.minimumSubsetSize = minimumSubsetSize;
    }

    public final int getMaximumSubsetSize() {
        return maximumSubsetSize;
    }

    public final void setMaximumSubsetSize(int maximumSubsetSize) {
        this.maximumSubsetSize = maximumSubsetSize;
    }

    public final NamedData getDataset() {
        return dataset;
    }

    public final void setDataset(NamedData dataset) {
        this.dataset = dataset;
    }

    public final CorehunterObjective getObjective() {
        return objective;
    }

    public final void setObjective(CorehunterObjective objective) {
        this.objective = objective;
    }
}
