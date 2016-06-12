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

package org.corehunter.services.simple;

import java.util.UUID;

import org.corehunter.data.CoreHunterData;
import org.corehunter.services.CoreHunterRunArguments;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.subset.SubsetSolution;

import uno.informatics.data.pojo.SimpleEntityPojo;

public class CoreHunterRunArgumentsPojo extends SimpleEntityPojo implements CoreHunterRunArguments {
    private int subsetSize;
    private String datasetId;
    private Objective<SubsetSolution, CoreHunterData> objective;

    public CoreHunterRunArgumentsPojo(String name, int subsetSize, String datasetId,
            Objective<SubsetSolution, CoreHunterData> objective) {
        super(UUID.randomUUID().toString(), name);
        this.subsetSize = subsetSize;
        this.datasetId = datasetId;
        this.objective = objective;
    }

    @Override
    public int getSubsetSize() {
        return subsetSize;
    }

    @Override
    public String getDatasetId() {
        return datasetId;
    }

    @Override
    public Objective<SubsetSolution, CoreHunterData> getObjective() {
        return objective;
    }
    
    

}
