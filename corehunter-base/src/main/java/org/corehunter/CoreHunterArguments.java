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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.corehunter.data.CoreHunterData;

public class CoreHunterArguments {

    private final int subsetSize;
    private final CoreHunterData data;
    private final List<CoreHunterObjective> objectives;
    private final boolean normalize;
    private final Set<Integer> alwaysSelected;
    private final Set<Integer> neverSelected;
    
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
     * Creates a single- or multi-objective configuration.
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
     * Creates a single- or multi-objective configuration.
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
        this(data, subsetSize, objectives, Collections.emptySet(), normalize);
    }
    
    /**
     * Creates a single- or multi-objective configuration with a set of always selected IDs.
     * If <code>normalize</code> is <code>true</code> automatic normalization is enabled, but
     * only if more than one objective is included. In case of a single objective, this argument
     * is ignored.
     *
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objectives the objectives for the run
     * @param alwaysSelected set of IDs that will always be selected in the core
     * @param normalize indicates whether objectives should be normalized prior to execution
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize, List<CoreHunterObjective> objectives,
                               Set<Integer> alwaysSelected, boolean normalize) {
        this(data, subsetSize, objectives, alwaysSelected, Collections.emptySet(), normalize);
    }
    
    /**
     * Creates a single- or multi-objective configuration with set of always and/or never selected IDs.
     * If <code>normalize</code> is <code>true</code> automatic normalization is enabled, but
     * only if more than one objective is included. In case of a single objective, this argument
     * is ignored.
     *
     * @param data the data for the run
     * @param subsetSize the desired subset size
     * @param objectives the objectives for the run
     * @param alwaysSelected set of IDs that will always be selected in the core
     * @param neverSelected set of IDs that will never be selected in the core
     * @param normalize indicates whether objectives should be normalized prior to execution
     */
    public CoreHunterArguments(CoreHunterData data, int subsetSize, List<CoreHunterObjective> objectives,
                               Set<Integer> alwaysSelected, Set<Integer> neverSelected, boolean normalize) {
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
        // store set of always/never selected ids
        if (alwaysSelected == null) {
            throw new IllegalArgumentException("Set of always selected IDs can not be null.");
        }
        if (neverSelected == null) {
            throw new IllegalArgumentException("Set of never selected IDs can not be null.");
        }
        if(alwaysSelected.stream().anyMatch(neverSelected::contains)
            || neverSelected.stream().anyMatch(alwaysSelected::contains)){
            throw new IllegalArgumentException("Sets of always and never selected IDs should be disjoint.");
        }
        if(alwaysSelected.size() > subsetSize){
            throw new IllegalArgumentException("Set of always selected IDs can not be larger than subset size.");
        }
        if(data.getSize() - neverSelected.size() < subsetSize){
            throw new IllegalArgumentException("Too many never selected IDs: can not obtain requested subset size.");
        }
        this.alwaysSelected = Collections.unmodifiableSet(new HashSet<>(alwaysSelected));
        this.neverSelected = Collections.unmodifiableSet(new HashSet<>(neverSelected));
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
    
    public final Set getAlwaysSelected(){
        return alwaysSelected;
    }
    
    public final Set getNeverSelected(){
        return neverSelected;
    }
    
    public final boolean isNormalized(){
        return normalize;
    }
    
}
