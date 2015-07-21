/*******************************************************************************
 * Copyright 2014 Herman De Beukelaer, Guy Davenport Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package org.corehunter;

import java.util.concurrent.TimeUnit;

import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;

/**
 * Provides support for executing pre-defined core subset searches. 
 * Can be re-used.
 * 
 * @author Guy Davenport
 */
public class Corehunter<DataType extends IntegerIdentifiedData>
{
	private long	                         timeLimit	= 60;
	
	public SubsetSolution executeRandomDescent(DataType data,
	    Objective<SubsetSolution, DataType> objective, int subsetSize, SearchListener<SubsetSolution> searchListener)
	{
		SubsetProblem<DataType> problem = new SubsetProblem<DataType>(
				data, objective, subsetSize);

		RandomDescent<SubsetSolution> search = new RandomDescent<SubsetSolution>(
		    problem, new SinglePerturbationNeighbourhood());

		search.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));

		if (searchListener != null)
			search.addSearchListener(searchListener);

		// start search
		search.start();

		// dispose search
		search.dispose();

		return search.getBestSolution();
	}

	public final long getTimeLimit()
	{
		return timeLimit;
	}

	public final void setTimeLimit(long timeLimit)
	{
		this.timeLimit = timeLimit;
	}
}
