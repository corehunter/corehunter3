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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.corehunter.CoreHunterObjective;
import org.corehunter.services.CoreHunterRunArguments;

import uno.informatics.data.pojo.SimpleEntityPojo;

public class CoreHunterRunArgumentsPojo extends SimpleEntityPojo implements CoreHunterRunArguments {

    private int subsetSize;
    private String datasetId;
    private List<CoreHunterObjective> objectives;

    public CoreHunterRunArgumentsPojo(String name, int subsetSize, String datasetId,
            List<CoreHunterObjective> objectives) {
        super(UUID.randomUUID().toString(), name);
        setSubsetSize(subsetSize);
        setDatasetId(datasetId);
        setObjectives(objectives);
    }

    public CoreHunterRunArgumentsPojo(CoreHunterRunArguments arguments) {
        super(arguments.getUniqueIdentifier(), arguments.getName());
        setSubsetSize(arguments.getSubsetSize());
        setDatasetId(arguments.getDatasetId());
        setObjectives(arguments.getObjectives()) ;

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
    public List<CoreHunterObjective> getObjectives() {
        return objectives;
    }

    protected final void setSubsetSize(int subsetSize) {
        this.subsetSize = subsetSize;
    }

    protected final void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    protected final void setObjectives(List<CoreHunterObjective> objectives) {
        if (objectives != null) {
            Iterator<CoreHunterObjective> iterator = objectives.iterator();
            
            this.objectives = new ArrayList<CoreHunterObjective>(objectives.size()) ;
            
            while (iterator.hasNext()) {
                this.objectives.add(new CoreHunterObjective(iterator.next())) ; 
            }
        } else {
            this.objectives = new ArrayList<CoreHunterObjective>(0) ;
        }
    }
}
