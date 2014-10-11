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

import org.jamesframework.core.problems.SubsetProblemWithData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.SubsetSolution;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.search.neigh.subset.SingleSwapNeighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;

/**
 * @author Guy Davenport
 */
public class Corehunter
{
	private long	                         timeLimit	= 60;
	private SearchListener<SubsetSolution>	searchListener;

	public SubsetSolution executeRandomDescent(DistanceMatrixData data,
	    Objective<SubsetSolution, DistanceMatrixData> objective, int subsetSize)
	{
		SubsetProblemWithData<DistanceMatrixData> problem = new SubsetProblemWithData<DistanceMatrixData>(
		    objective, data, subsetSize);

		RandomDescent<SubsetSolution> search = new RandomDescent<SubsetSolution>(
		    problem, new SingleSwapNeighbourhood());

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

	public final SearchListener<SubsetSolution> getSearchListener()
	{
		return searchListener;
	}

	public final void setSearchListener(
	    SearchListener<SubsetSolution> searchListener)
	{
		this.searchListener = searchListener;
	}
}
