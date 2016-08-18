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
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.corehunter.data.CoreHunterData;
import org.corehunter.exceptions.CoreHunterException;
import org.corehunter.objectives.AverageAccessionToNearestEntry;
import org.corehunter.objectives.AverageEntryToEntry;
import org.corehunter.objectives.AverageEntryToNearestEntry;
import org.corehunter.objectives.Coverage;
import org.corehunter.objectives.HeterozygousLoci;
import org.corehunter.objectives.Shannon;
import org.corehunter.objectives.distance.DistanceMeasure;
import org.corehunter.objectives.distance.measures.CavalliSforzaEdwardsDistance;
import org.corehunter.objectives.distance.measures.GowerDistance;
import org.corehunter.objectives.distance.measures.ModifiedRogersDistance;
import org.corehunter.objectives.distance.measures.PrecomputedDistance;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.search.stopcriteria.MaxTimeWithoutImprovement;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.SingleSwapNeighbourhood;
import org.jamesframework.ext.problems.objectives.NormalizedObjective;
import org.jamesframework.ext.problems.objectives.WeightedIndex;

/**
 * A facade for executing Core Hunter searches. Can be re-used.
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public class CoreHunter {

    private static final int DEFAULT_MAX_TIME_WITHOUT_IMPROVEMENT = 10000;
    private static final int FAST_MAX_TIME_WITHOUT_IMPROVEMENT = 2000;
        
    private static final int PT_NUM_REPLICAS = 10;
    private static final double PT_MIN_TEMP = 1e-8;
    private static final double PT_MAX_TEMP = 1e-4;
    
    private CoreHunterListener listener;
    private long timeLimit = -1;
    private long maxTimeWithoutImprovement = -1;
    private CoreHunterExecutionMode mode;

    public CoreHunter() {
        this(CoreHunterExecutionMode.DEFAULT);
    }

    /**
     * Create Core Hunter facade with the specified mode.
     * <p>
     * In {@link CoreHunterExecutionMode#DEFAULT} mode parallel tempering is applied
     * and terminated when no improvement has been made for ten seconds.
     * In {@link CoreHunterExecutionMode#FAST} mode random descent is applied
     * and terminated when no improvement has been made for two seconds.
     * By default no absolute time limit is set in any of the two modes.
     * Stop conditions can be altered with {@link #setMaxTimeWithoutImprovement(long)}
     * and {@link #setTimeLimit(long)}.
     * <p>
     * In case of a multi-objective configuration with normalization enabled, a preliminary
     * random descent search is performed per objective to determine suitable bounds based
     * on the Pareto minima/maxima. If a time limit and/or maximum time without finding an
     * improvement have been set, the same limit is applied to all normalization searches as
     * well as the main multi-objective search. Normalization searches are executed in parallel
     * which means that in case of a limited number of objectives the total execution time should
     * usually not exceed twice the imposed search time limit.
     * 
     * @param mode execution mode
     */
    public CoreHunter(CoreHunterExecutionMode mode) {
        this.mode = mode;
        maxTimeWithoutImprovement = DEFAULT_MAX_TIME_WITHOUT_IMPROVEMENT;
        if(mode == CoreHunterExecutionMode.FAST){
            maxTimeWithoutImprovement = FAST_MAX_TIME_WITHOUT_IMPROVEMENT;
        }
    }

    /**
     * Determine normalization ranges of all objectives in a multi-objective configuration, based on the
     * Pareto minima/maxima. Executes a random descent search per objective (in parallel).
     * For a single-objective setting or when <code>normalize</code> is set to <code>false</code> in the
     * given <code>arguments</code>, an exception is thrown.
     * 
     * @param arguments Core Hunter arguments specifying dataset, core size and objectives
     * @return List with normalization ranges of all objectives, in the same order as the objectives
     * @throws IllegalArgumentException If normalization is disabled in the arguments or in case of a
     *                                  single-objective configuration.
     */
    public List<Range<Double>> normalize(CoreHunterArguments arguments){
        
        // check arguments
        if(arguments == null){
            throw new IllegalArgumentException("Arguments not defined!");
        }
        CoreHunterData data = arguments.getData();
        if(data == null){
            throw new IllegalArgumentException("Dataset not defined!");
        }
        if(!arguments.isNormalized()){
            throw new IllegalArgumentException("Normalization is disabled.");
        }
        List<CoreHunterObjective> objectives = arguments.getObjectives();
        if(objectives == null || objectives.isEmpty()){
            throw new IllegalArgumentException("Objectives not defined!");
        }
        if(objectives.size() < 2){
            throw new IllegalArgumentException("At least two objectives required for Pareto normalization.");
        }
        
        // set size and neighbourhood
        int size = arguments.getSubsetSize();
        Neighbourhood<SubsetSolution> neigh = new SingleSwapNeighbourhood();
        
        // optimize each objective separately (in parallel)
        List<SubsetSolution> bestSolutions = objectives.parallelStream().map(obj -> {
            Objective<SubsetSolution, CoreHunterData> jamesObj = createObjective(data, obj);
            // create normalization problem and search
            SubsetProblem<CoreHunterData> problem = new SubsetProblem<>(data, jamesObj, size);
            RandomDescent<SubsetSolution> normSearch = new RandomDescent<>(problem, neigh);
            // set stop conditions
            if(timeLimit > 0){
                normSearch.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.MILLISECONDS));
            }
            if(maxTimeWithoutImprovement > 0){
                normSearch.addStopCriterion(
                        new MaxTimeWithoutImprovement(maxTimeWithoutImprovement, TimeUnit.MILLISECONDS)
                );
            }
            // execute normalization search
            normSearch.run();
            // return best solution
            return normSearch.getBestSolution();
        })  .collect(Collectors.toList());
        
        // determine normalization ranges (based on Pareto maxima/minima)
        List<Range<Double>> ranges = new ArrayList<>();
        for(int o = 0; o < objectives.size(); o++){
            Objective<SubsetSolution, CoreHunterData> obj = createObjective(data, objectives.get(o));
            // evaluate all optimal solutions with this objective
            List<Double> allValues = bestSolutions.stream().map(
                sol -> obj.evaluate(sol, data).getValue()
            ).collect(Collectors.toList());
            // take best solution value for the considered objective
            double bestValue = allValues.get(o);
            // set bounds taking into account whether the objective is minimized or maximized
            double min, max;
            if(obj.isMinimizing()){
                // best solution value = lower bound
                min = bestValue;
                // max of all values = upper bound
                max = Collections.max(allValues);
            } else {
                // best solution value = upper bound
                max = bestValue;
                // min of all values = lower bound
                min = Collections.min(allValues);
            }
            // set range
            ranges.add(new Range<>(min, max));
        }
        return ranges;
        
    }
    
    public SubsetSolution execute(CoreHunterArguments arguments) {

        if (arguments == null) {
            throw new IllegalArgumentException("Arguments not defined!");
        }

        if (arguments.getData() == null) {
            throw new IllegalArgumentException("Dataset not defined!");
        }

        // create search from arguments
        Search<SubsetSolution> search = createSearch(arguments);

        // set stop criteria
        if(timeLimit <= 0 && maxTimeWithoutImprovement <= 0){
            throw new IllegalStateException(
                    "Please specify time limit and/or maximum time without improvement before execution."
            );
        }
        if (timeLimit > 0) {
            search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.MILLISECONDS));
        }
        if (maxTimeWithoutImprovement > 0){
            search.addStopCriterion(new MaxTimeWithoutImprovement(maxTimeWithoutImprovement, TimeUnit.MILLISECONDS));
        }
        
        // add search listener (if any)
        if (listener != null) {
            search.addSearchListener(listener);
        }

        // start search
        search.start();

        // dispose search
        search.dispose();

        return search.getBestSolution();
    }
    
    /**
     * Evaluate the given solution with the specified objective. The weight of the objective is ignored.
     * 
     * @param sol subset solution
     * @param data Core Hunter data
     * @param objective objective used to evaluate the subset (weight is ignored)
     * @return value of the subset according to the specified objective
     */
    public double evaluate(SubsetSolution sol, CoreHunterData data, CoreHunterObjective objective){
        Objective<SubsetSolution, CoreHunterData> obj = createObjective(data, objective);
        return obj.evaluate(sol, data).getValue();
    }

    /**
     * Get execution time limit (milliseconds).
     * 
     * @return time limit
     */
    public long getTimeLimit() {
        return timeLimit;
    }

    /**
     * Sets the absolute time limit (in milliseconds).
     * A negative value means that no time limit is imposed.
     * 
     * @param ms absolute time limit in milliseconds
     */
    public void setTimeLimit(long ms) {
        this.timeLimit = ms;
    }
    
    /**
     * Get the maximum allowed time without finding an improvement (milliseconds).
     * 
     * @return maximum time without improvement
     */
    public long getMaxTimeWithoutImprovement(){
        return maxTimeWithoutImprovement;
    }
    
    /**
     * Sets the maximum time without finding any improvements (in milliseconds).
     * A negative value means that no such stop condition is set.
     * 
     * @param ms maximum time without improvement in milliseconds
     */
    public void setMaxTimeWithoutImprovement(long ms){
        maxTimeWithoutImprovement = ms;
    }
    
    public CoreHunterListener getListener(){
        return listener;
    }
    
    public void setListener(CoreHunterListener listener){
        this.listener = listener;
    }

    protected Search<SubsetSolution> createSearch(CoreHunterArguments arguments) {

        int size = arguments.getSubsetSize();

        Objective<SubsetSolution, CoreHunterData> objective = createObjective(arguments);

        SubsetProblem<CoreHunterData> problem = new SubsetProblem<>(arguments.getData(), objective, size);
        Neighbourhood<SubsetSolution> neigh = new SingleSwapNeighbourhood();
        
        Search<SubsetSolution> search = createSearch(problem, neigh);

        return search;

    }
    
    private Search<SubsetSolution> createSearch(SubsetProblem<CoreHunterData> problem,
                                                Neighbourhood<SubsetSolution> neigh){
        switch(mode){
            case DEFAULT:
                return new ParallelTempering<>(problem, neigh, PT_NUM_REPLICAS, PT_MIN_TEMP, PT_MAX_TEMP);
            case FAST:
                return new RandomDescent<>(problem, neigh);
            default:
                throw new CoreHunterException("Unknown execution mode " + mode + ".");
        }
    }

    private Objective<SubsetSolution, CoreHunterData> createObjective(CoreHunterArguments arguments) {
        // extract data and objectives
        CoreHunterData data = arguments.getData();
        List<CoreHunterObjective> objectives = arguments.getObjectives();
        // compose objective
        if (objectives == null || objectives.isEmpty()) {
            throw new CoreHunterException("No objective(s) given.");
        } else {
            if (objectives.size() == 1) {
                // single objective
                return createObjective(data, objectives.get(0));
            } else {
                // multiple objectives (weighted index)
                WeightedIndex<SubsetSolution, CoreHunterData> weightedIndex =  new WeightedIndex<>();
                // create all objectives
                List<Objective<SubsetSolution, CoreHunterData>> jamesObjectives = objectives.stream()
                        .map(obj -> createObjective(data, obj))
                        .collect(Collectors.toList());
                // normalize if requested
                if(arguments.isNormalized()){
                    jamesObjectives = normalizeObjectives(arguments, jamesObjectives);
                }
                // combine in weighted index
                for(int o = 0; o < objectives.size(); o++) {
                    weightedIndex.addObjective(jamesObjectives.get(o), objectives.get(o).getWeight());
                }
                return weightedIndex;
            }
        }
    }

    private Objective<SubsetSolution, CoreHunterData> createObjective(CoreHunterData data,
                                                                      CoreHunterObjective coreHunterObjective) {

        Objective<SubsetSolution, CoreHunterData> objective = null;
        DistanceMeasure distanceMeasure = null;

        if (coreHunterObjective.getMeasure() != null) {
            switch (coreHunterObjective.getMeasure()) {
                case MODIFIED_ROGERS:
                    if (!data.hasGenotypes()) {
                        throw new CoreHunterException("Genotypes are required for Modified Rogers distance.");
                    }
                    distanceMeasure = new ModifiedRogersDistance();
                    break;
                case CAVALLI_SFORZA_EDWARDS:
                    if (!data.hasGenotypes()) {
                        throw new CoreHunterException(
                                "Genotypes are required for Cavalli-Sforza and Edwards distance."
                        );
                    }
                    distanceMeasure = new CavalliSforzaEdwardsDistance();
                    break;
                case GOWERS:
                    if (!data.hasPhenotypes()) {
                        throw new CoreHunterException("Phenotypes are required for Gower distance.");
                    }
                    distanceMeasure = new GowerDistance();
                    break;
                case PRECOMPUTED_DISTANCE:
                    if (!data.hasDistances()) {
                        throw new CoreHunterException("No precomputed distance matrix has been defined.");
                    }
                    distanceMeasure = new PrecomputedDistance();
                    break;
                default:
                    // do nothing (not all objectives require a distance measure)
            }
        }

        switch (coreHunterObjective.getObjectiveType()) {
            case AV_ACCESSION_TO_NEAREST_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY));
                }
                objective = new AverageAccessionToNearestEntry(distanceMeasure);
                break;
            case AV_ENTRY_TO_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY));
                }
                objective = new AverageEntryToEntry(distanceMeasure);
                break;
            case AV_ENTRY_TO_NEAREST_ENTRY:
                if (distanceMeasure == null) {
                    throw new CoreHunterException(String.format(
                            "No distance measure defined. A distance measure is required for %s",
                            CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY));
                }
                objective = new AverageEntryToNearestEntry(distanceMeasure);
                break;
            case COVERAGE:
                if (!data.hasGenotypes()) {
                    throw new CoreHunterException("Genotypes are required for coverage objective.");
                }
                objective = new Coverage();
                break;
            case HETEROZYGOUS_LOCI:
                if (!data.hasGenotypes()) {
                    throw new CoreHunterException(
                            "Genotypes are required for expected proportion of heterozygous loci objective."
                    );
                }
                objective = new HeterozygousLoci();
                break;
            case SHANNON_DIVERSITY:
                if (!data.hasGenotypes()) {
                    throw new CoreHunterException("Genotypes are required for Shannon's index.");
                }
                objective = new Shannon();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown objective : %s", coreHunterObjective));

        }

        return objective;
    }
    
    private List<Objective<SubsetSolution, CoreHunterData>> normalizeObjectives(
            CoreHunterArguments arguments,
            List<Objective<SubsetSolution, CoreHunterData>> objectives
    ){
        
        if(listener != null){
            listener.preprocessingStarted("Normalizing objectives.");
        }
        
        // get normalization ranges
        List<Range<Double>> ranges = normalize(arguments);
        // normalize objectives
        StringBuilder message = new StringBuilder();
        List<Objective<SubsetSolution, CoreHunterData>> normalizedObjectives = new ArrayList<>();
        for(int o = 0; o < objectives.size(); o++){
            Objective<SubsetSolution, CoreHunterData> obj = objectives.get(o);
            Range<Double> range = ranges.get(o);
            double min = range.getLower();
            double max = range.getUpper();
            normalizedObjectives.add(new NormalizedObjective<>(obj, min, max));
            message.append(String.format(Locale.ROOT, "%s: [%.3f, %.3f]%n", obj, min, max));
        }
        
        message.append("Finished normalization.");
        if(listener != null){
            listener.preprocessingStopped(message.toString());
        }
        
        return normalizedObjectives;
        
    }
    
}
