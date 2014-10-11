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
package org.corehunter.objectives;

import java.util.ArrayList;
import java.util.List;

import org.corehunter.DistanceMatrixData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.SubsetSolution;

public class AverageDistanceObjective implements
    Objective<SubsetSolution, DistanceMatrixData>
{
	/**
	 * Evaluates the given subset solution using the given data, by computing the
	 * average distance between all pairs of selected items. If less than two items are
	 * selected, this method always returns 0.
	 * 
	 * @param solution the subset solution to be evaluated 
	 * @param data the distance matrix
	 */
	@Override
	public double evaluate(SubsetSolution solution, DistanceMatrixData data)
	{
		if (solution.getNumSelectedIDs() < 2)
		{
			return 0.0;
		}
		else
		{
			// at least two items selected: compute average distance
			List<Integer> ids = new ArrayList<Integer>(solution.getSelectedIDs());
			int id1, id2, numDist = 0;
			double sumDist = 0.0;
			for (int i = 0; i < ids.size(); i++)
			{
				id1 = ids.get(i);
				for (int j = i + 1; j < ids.size(); j++)
				{
					id2 = ids.get(j);
					sumDist += data.getDistance(id1, id2);
					numDist++;
				}
			}
			return sumDist / numDist;
		}
	}

	@Override
	public boolean isMinimizing()
	{
		return false;
	}

}