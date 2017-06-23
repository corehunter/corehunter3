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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.corehunter.data.CoreHunterData;

public class CoreHunterArguments {

    private final int subsetSize;
    private final CoreHunterData data;
    private final List<CoreHunterObjective> objectives;
    private final boolean normalize;
    
    /**
     * Creates a single objective configuration with no defined measure.
     * 
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objective the objective type 
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize, CoreHunterObjectiveType objective) {
        this(data, subsetSize, objective, null);
    }

    /**
     * Creates a single objective configuration.
     * 
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objective the objective type 
     * @param measure the optional measure required for the objective
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize,
                               CoreHunterObjectiveType objective, CoreHunterMeasure measure) {
        this(data, subsetSize, Collections.singletonList(new CoreHunterObjective(objective, measure)));
        if (objective == null) {
            throw new IllegalArgumentException("Objective not defined.");
        }
        if (measure == null) {
            throw new IllegalArgumentException("Measure not defined.");
        }
    }
    
    /**
     * Creates a multiple objective configuration.
     * Automatic normalization is enabled whenever more than one objective is included.
     * 
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objectives the objectives for the run
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize, List<CoreHunterObjective> objectives) {
        this(data, subsetSize, objectives, true);
    }

    /**
     * Creates a multiple objective configuration.
     * If <code>normalize</code> is <code>true</code> automatic normalization is enabled, but
     * only if more than one objective is included. In case of a single objective, this argument
     * is ignored.
     *
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objectives the objectives for the run
     * @param normalize indicates whether objectives should be normalized prior to execution
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize,
                               List<CoreHunterObjective> objectives,
                               boolean normalize) {
        // set data and size
        if (data == null) {
            throw new IllegalArgumentException("Data undefined.");
        }
        if (subsetSize < 2) {
            throw new IllegalArgumentException("Requested subset size must at least be 2 or more.");
        }
        if (subsetSize >= data.getSize()) {
            throw new IllegalArgumentException(
                    String.format("Requested subset size must be less than total data size %d.", data.getSize())
            );
        }
        this.data = data ;
        this.subsetSize = subsetSize ;
        // set objectives
        if (objectives == null || objectives.isEmpty()) {
            throw new IllegalArgumentException("Objectives not defined.");
        }
        this.objectives = Collections.unmodifiableList(new ArrayList<>(objectives));
        // set normalization flag
        this.normalize = objectives.size() > 1 && normalize;
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
    
    public final boolean isNormalized(){
        return normalize;
    }
    
}
