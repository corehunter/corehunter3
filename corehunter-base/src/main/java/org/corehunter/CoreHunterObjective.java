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

/**
 * Defines the objective by providing the objective type, optional measure and optional weight.
 * A measure is required for some data type / objective type combinations. If provided the 
 * weight is ignored if the objective is not used with any other objectives.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public class CoreHunterObjective {
    
    private CoreHunterObjectiveType objectiveType;
    private CoreHunterMeasure measure;
    private double weight;
    private Range<Double> normalizationRange;
    
    /**
     * Creates a Core Hunter objective with for specific object type with no defined measure. 
     * A measure is required for some data type / objective type combinations
     * 
     * @param objectiveType the objective type to be used
     */
    public CoreHunterObjective(CoreHunterObjectiveType objectiveType) {
        this(objectiveType, null);
    }
    
    /**
     * Creates a Core Hunter objective with for specific object type and measure.
     * 
     * @param objectiveType the objective type to be used
     * @param measure the measure to be used for this objective type
     */
    public CoreHunterObjective(CoreHunterObjectiveType objectiveType, CoreHunterMeasure measure) {
        this(objectiveType, measure, 1.0);
    }
    
    /**
     * Creates a Core Hunter objective with for specific object type and weight with no defined measure. 
     * A measure is required for some data type / objective type combinations
     * 
     * @param objectiveType the objective type to be used
     * @param weight the relative weight of this objective when used in conjunction with another objective
     */
    public CoreHunterObjective(CoreHunterObjectiveType objectiveType, double weight) {
        this(objectiveType, null, weight);
    }

    /**
     * Creates a Core Hunter objective with for specific object type, measure and weight. 
     * The weight is ignored if the objective is not used with any other objectives.
     * 
     * @param objectiveType the objective type to be used
     * @param measure the measure to be used for this objective type
     * @param weight the relative weight of this objective when used in conjunction with another objective
     */
    public CoreHunterObjective(CoreHunterObjectiveType objectiveType, CoreHunterMeasure measure, double weight) {
        this(objectiveType, measure, weight, null);
    }
    
    /**
     * Creates a Core Hunter objective with for specific object type, weight and normalization range.
     * The weight and normalization range are ignored if the objective is not used in a multi-objective configuration.
     * 
     * @param objectiveType the objective type to be used
     * @param weight the relative weight of this objective when used in conjunction with another objective
     * @param normalizationRange the normalization range of this objective in a multi-objective configuration
     */
    public CoreHunterObjective(CoreHunterObjectiveType objectiveType, double weight, Range<Double> normalizationRange) {
        this(objectiveType, null, weight, normalizationRange);
    }
    
    /**
     * Creates a Core Hunter objective with for specific object type, measure, weight and normalization range.
     * The weight and normalization range are ignored if the objective is not used in a multi-objective configuration.
     * 
     * @param objectiveType the objective type to be used
     * @param measure the measure to be used for this objective type
     * @param weight the relative weight of this objective when used in conjunction with another objective
     * @param normalizationRange the normalization range of this objective in a multi-objective configuration
     */
    public CoreHunterObjective(CoreHunterObjectiveType objectiveType, CoreHunterMeasure measure,
                               double weight, Range<Double> normalizationRange) {
        setObjectiveType(objectiveType);
        setMeasure(measure);
        setWeight(weight);
        setNormalizationRange(normalizationRange);
    }
    
    /**
     * Creates a CoreHunter Objective from another objective. Copy Constructor.
     * 
     * @param objective the objective to be copied
     */
    public CoreHunterObjective(CoreHunterObjective objective) {
        setObjectiveType(objective.getObjectiveType()) ;
        setMeasure(objective.getMeasure()) ;
        setWeight(objective.getWeight()) ;
    }

    /**
     * Gets the objective type for this objective
     * @return the objective type for this objective
     */
    public final CoreHunterObjectiveType getObjectiveType() {
        return objectiveType;
    }

    /**
     * Sets the objective type for this objective
     * @param objectiveType the objective type for this objective
     */
    public final void setObjectiveType(CoreHunterObjectiveType objectiveType) {
        this.objectiveType = objectiveType;
    } 

    /**
     * Gets the measure for this objective if defined
     * @return the measure for this objective if defined, or 
     * <code>null</code> if no measure has been given
     */
    public final CoreHunterMeasure getMeasure() {
        return measure;
    }

    /**
     * Gets the measure for this objective if defined
     * @param measure the measure for this objective if defined, or 
     * <code>null</code> if no measure is given
     */
    public final void setMeasure(CoreHunterMeasure measure) {
        this.measure = measure;
    }

    /**
     * Gets the relative weight of this objective when used in conjunction with another objective.
     * The weight is ignored if the objective is not used with any other objectives.
     * 
     * @return the relative weight of this objective
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the relative weight of this objective when used in conjunction with another objective.
     * The weight is ignored if the objective is not used with any other objectives.
     * 
     * @param weight the relative weight of this objective
     */
    public final void setWeight(double weight) {
        this.weight = weight;
    }
    
    /**
     * Gets the normalization range of this objective when used in a multi-objective configuration.
     * Returns <code>null</code> if no normalization range has been set.
     * 
     * @return normalization range of this objective
     */
    public final Range<Double> getNormalizationRange(){
        return normalizationRange;
    }
    
    /**
     * Sets the normalization range of this objective when used in a multi-objective configuration.
     * 
     * @param range normalization range of this objective
     */
    public final void setNormalizationRange(Range<Double> range){
        this.normalizationRange = range;
    }
    
}
