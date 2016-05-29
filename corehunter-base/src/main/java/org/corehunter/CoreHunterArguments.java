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

import java.util.LinkedList;
import java.util.List;

import org.corehunter.data.CoreHunterData;

public class CoreHunterArguments {

    private int subsetSize;
    
    private CoreHunterData data;

    private List<CoreHunterObjective> objectives;
    
    /**
     * Creates arguments with no objectives.
     *
     * 
     * @param data the data for the run
     * @param subsetSize the desired subset size
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize) {
        if (data == null) {
            throw new IllegalArgumentException("Data undefined!");
        }

        if (subsetSize < 2) {
            throw new IllegalArgumentException("Requested subset size must at least be 2 or more");
        }
        
        if (subsetSize >= data.getSize()) {
            throw new IllegalArgumentException(
                    String.format("Requested subset size less than total data size %s", data.getSize()));
        }
        
        this.data = data ;
        this.subsetSize = subsetSize ;
        
        objectives = new LinkedList<CoreHunterObjective>() ;
    }
    
    /**
     * Creates a single objective configuration with no defined measure
     *
     * 
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objective the objective type 
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize, CoreHunterObjectiveType objective) {
        this(data, subsetSize) ;
        
        if (objective == null) {
            throw new IllegalArgumentException("Objective not defined!");
        }
        
        objectives.add(new CoreHunterObjective(objective)) ; 
    }

    /**
     * Creates a single objective configuration.
     *
     * 
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objective the objective type 
     * @param measure the optional measure required for the objective
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize, CoreHunterObjectiveType objective,
                              CoreHunterMeasure measure) {
        this(data, subsetSize) ;
        objectives.add(new CoreHunterObjective(objective, measure)) ; 
    }
    
    /**
     * Creates a multiple objective configuration.
     *
     * 
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objectives the objectives for the run
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize, List<CoreHunterObjective> objectives) {
        this(data, subsetSize) ;
        
        if (objectives == null) {
            throw new IllegalArgumentException("Objectives not defined!");
        }
        
        objectives.addAll(objectives) ; 
    }

    public final CoreHunterData getData() {
        return data;
    }
    
    public final List<CoreHunterObjective> getObjectives() {
        return objectives;
    }

    public final int getSubsetSize() {
        return subsetSize;
    }
}
