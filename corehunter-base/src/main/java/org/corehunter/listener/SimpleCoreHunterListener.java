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

package org.corehunter.listener;

import java.io.PrintStream;

import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.subset.SubsetSolution;
import org.corehunter.CoreHunterListener;

public class SimpleCoreHunterListener implements CoreHunterListener {

    private static final String DEFAULT_PREFIX = "";

    private String prefix;
    private PrintStream printStream;

    public SimpleCoreHunterListener() {
        this(System.err);
    }

    public SimpleCoreHunterListener(PrintStream printStream) {
        super();

        this.printStream = printStream;
        prefix = DEFAULT_PREFIX;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void searchStarted(Search<? extends SubsetSolution> search) {
        printStream.println(String.format("%sSearch : %s started.", prefix, search.getName()));
    }

    @Override
    public void searchStopped(Search<? extends SubsetSolution> search) {
        double t = search.getRuntime() / 1000;
        long s = search.getSteps();
        printStream.println(String.format("%sSearch : %s stopped after %f seconds and %d steps", prefix, search.getName(), t, s));
        printStream.println(String.format("%sBest solution with evaluation : %f", prefix, search.getBestSolutionEvaluation().getValue()));
        printStream.println(String.format("%sBest solution with evaluation : %s", prefix, search.getBestSolution()));
    }

    @Override
    public void newBestSolution(Search<? extends SubsetSolution> search,
                                SubsetSolution newBestSolution,
                                Evaluation newBestSolutionEvaluation,
                                Validation newBestSolutionValidation) {
        printStream.println(String.format("%sNew best solution with evaluation : %f", prefix, newBestSolutionEvaluation.getValue()));
    }

    @Override
    public void preprocessingStarted(String message) {
        printStream.println(String.format("%s%s", prefix, message));
    }

    @Override
    public void preprocessingStopped(String message) {
        printStream.println(String.format("%s%s", prefix, message));
    }
}
