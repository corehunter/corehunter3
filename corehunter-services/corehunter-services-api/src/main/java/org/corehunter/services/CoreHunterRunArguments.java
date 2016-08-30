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

package org.corehunter.services;

import java.util.List;

import org.corehunter.CoreHunterObjective;

import uno.informatics.data.SimpleEntity;

public interface CoreHunterRunArguments extends SimpleEntity {
   
    /** 
     * Gets the required subset size for the run. The value must
     * be greater than 2 and less than the dataset size
     * 
     * @return the required subset size for the run
     */
    public int getSubsetSize() ;

    /** 
     * Gets the id of the dataset to analysed. This
     * data must be made available to the CoreHunter Run Services  
     * @return the id of the dataset to analysed
     */
    public String getDatasetId();
    
    /**
     * Gets the objectives to apply this run. If no objectives 
     * are defined the default objective for the 
     * CoreHunter Run Services is used
     * @return the objectives to apply this run or an empty list
     */
    public List<CoreHunterObjective> getObjectives() ;

    /**
     * Sets the absolute time limit (in seconds).
     * A negative value means that specific no time limit is imposed
     * for this run, however the CoreHunter Run Services implementation
     * may impose such a limit
     * 
     * @return seconds absolute time limit in seconds
     */
    long getTimeLimit();

    /**
     * Sets the maximum time without finding any improvements (in seconds).
     * A negative value means that no such stop condition is set
     * for this run, however the CoreHunter Run Services implementation
     * may impose such a limit
     * 
     * @return seconds maximum time without improvement in seconds
     */
    long getMaxTimeWithoutImprovement();
}
