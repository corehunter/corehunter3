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

package org.corehunter.objectives.eval;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.corehunter.data.GenotypeData;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;

/**
 * @author Herman De Beukelaer
 */
public abstract class AllelicDiversityEvaluation implements Evaluation {

    // average genotype with missing values treated as zero
    private final double[][] origAverageGenotype;
    // average genotype after resolving missing values
    private double[][] averageGenotype;
    // number of selected items in core collection
    private final int numSelected;
    
    /**
     * Initialize evaluation based on IDs of selected items.
     * Computes and stores the average genotype of the selection.
     * 
     * @param ids IDs of selected items
     * @param data genotype variant data
     */
    public AllelicDiversityEvaluation(Collection<Integer> ids, GenotypeData data){
        
        numSelected = ids.size();
        
        // infer average genotype (missing values treated as zero)
        int numMarkers = data.getNumberOfMarkers();
        origAverageGenotype = new double[numMarkers][];
        for(int m = 0; m < numMarkers; m++){
            int numAlleles = data.getNumberOfAlleles(m);
            origAverageGenotype[m] = new double[numAlleles];
            if(numSelected > 0){
                for(int a = 0; a < numAlleles; a++){
                    double freqSum = 0.0;
                    for(int id : ids){
                        freqSum += frequency(data, id, m, a);
                    }
                    double avgFreq = freqSum/numSelected;
                    origAverageGenotype[m][a] = avgFreq;
                }
            }
        }
        
        // modify average genotype to take into account missing values
        resolveMissingValues();
                
    }
    
    /**
     * Infer a modified evaluation by removing and/or adding some items from/to the selection.
     * EFficiently updates the average genotype based on that from the current selection.
     * 
     * @param curEval current evaluation
     * @param add IDs of added items
     * @param remove IDs of removed items
     * @param data genotype variant data
     */
    public AllelicDiversityEvaluation(AllelicDiversityEvaluation curEval,
                                      Set<Integer> add, Set<Integer> remove,
                                      GenotypeData data){
        
        int origNumSelected = curEval.numSelected;
        numSelected = origNumSelected + add.size() - remove.size();
        
        if(numSelected == 0){
            // new selection is empty: set all average frequencies to zero
            origAverageGenotype = init2Darray(curEval.origAverageGenotype);
        } else {
            
            // new selection is not empty: update average genotype
            
            // copy original average genotype with missing values treated as zero
            origAverageGenotype = copy2Darray(curEval.origAverageGenotype);

            // update average genotype based on added/removed items
            for(int m = 0; m < origAverageGenotype.length; m++){
                for(int a = 0; a < origAverageGenotype[m].length; a++){
                    // undo average (only if current selection was not empty)
                    if(origNumSelected > 0) {
                        origAverageGenotype[m][a] *= origNumSelected;
                    }
                    // add
                    for(int id : add){
                        origAverageGenotype[m][a] += frequency(data, id, m, a);
                    }
                    // remove
                    for(int id : remove){
                        origAverageGenotype[m][a] -= frequency(data, id, m, a);
                    }
                    // redo average
                    origAverageGenotype[m][a] /= numSelected;
                }
            }
            
        }
        
        // modify average genotype to take into account missing values
        resolveMissingValues();
        
    }
    
    private double frequency(GenotypeData data, int id, int m, int a){
        Double freq = data.getAlleleFrequency(id, m, a);
        return freq == null ? 0.0 : freq;
    }
    
    private void resolveMissingValues(){
        if(numSelected > 0){
            // resolve missing values by increasing the highest frequency
            // per marker so that its allele frequencies sum to one
            averageGenotype = copy2Darray(origAverageGenotype);
            for (int m = 0; m < averageGenotype.length; m++) {
                // find and increase highest frequency
                double[] alleleFreqs = averageGenotype[m];
                int mostCommonAllele = 0;
                double mostCommonAlleleFreq = alleleFreqs[0];
                double freqSum = alleleFreqs[0];
                for (int a = 1; a < alleleFreqs.length; a++) {
                    freqSum += alleleFreqs[a];
                    if (alleleFreqs[a] > mostCommonAlleleFreq) {
                        mostCommonAllele = a;
                        mostCommonAlleleFreq = alleleFreqs[a];
                    }
                }
                alleleFreqs[mostCommonAllele] += (1.0 - freqSum); 
            }
        } else {
            // empty selection: set all frequencies to zero
            averageGenotype = init2Darray(origAverageGenotype);
        }
    }
        
    private double[][] init2Darray(double[][] template){
        double[][] array = new double[template.length][];
        for(int i = 0; i < array.length; i++){
            array[i] = new double[template[i].length];
        }
        return array;
    }
    
    private double[][] copy2Darray(double[][] toCopy){
        double[][] copy = new double[toCopy.length][];
        for(int i = 0; i < copy.length; i++){
            copy[i] = Arrays.copyOf(toCopy[i], toCopy[i].length);
        }
        return copy;
    }
    
    /**
     * Get average genotype. Missing values are resolved by increasing
     * the highest frequency per marker so that its allele frequencies
     * sum to one (worst case approach with minimal diversity). If the
     * selection is empty all average frequencies are set to zero.
     * 
     * @return average genotype
     */
    public double[][] getAverageGenotype(){
        return averageGenotype;
    }
    
    public int getNumSelected(){
        return numSelected;
    }
    
}
