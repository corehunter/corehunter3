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

package org.corehunter.objectives;

import java.util.Set;

import org.corehunter.data.CoreHunterData;
import org.corehunter.data.GenotypeData;
import org.corehunter.exceptions.CoreHunterException;
import org.corehunter.objectives.eval.HeterozygousLociEvaluation;
import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

/**
 * Expected proportion of heterozygous loci in offspring.
 * 
 * @author Guy Davenport, Herman De Beukelaer
 */
public class HeterozygousLoci implements Objective<SubsetSolution, CoreHunterData> {

    @Override
    public HeterozygousLociEvaluation evaluate(SubsetSolution solution, CoreHunterData data) {
        
        GenotypeData geno = data.getGenotypicData();
        
        if(geno == null){
            throw new CoreHunterException(
                    "Genotypes are required for expected proportion of heterozygous loci objective."
            );
        }
        
        return new HeterozygousLociEvaluation(solution.getSelectedIDs(), geno);
        
    }
    
    @Override
    public HeterozygousLociEvaluation evaluate(Move move, SubsetSolution curSolution,
                                               Evaluation curEvaluation, CoreHunterData data) {

        // check move type
        if (!(move instanceof SubsetMove)) {
            throw new IncompatibleDeltaEvaluationException(
                    "Heterozygous loci objective should be used in combination "
                  + "with neighbourhoods that generate moves of type SubsetMove."
            );
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;
        // cast evaluation (cannot fail as both evaluate methods return such evaluation object)
        HeterozygousLociEvaluation eval = (HeterozygousLociEvaluation) curEvaluation;
        
        // get set of added and deleted IDs
        Set<Integer> added = subsetMove.getAddedIDs();
        Set<Integer> deleted = subsetMove.getDeletedIDs();
        
        // return updated evaluation
        return new HeterozygousLociEvaluation(eval, added, deleted, data.getGenotypicData());

    }
    
    @Override
    public boolean isMinimizing() {
        return false;
    }
    
    @Override
    public String toString(){
        return "Expected proportion of heterozygous loci";
    }
    
}
