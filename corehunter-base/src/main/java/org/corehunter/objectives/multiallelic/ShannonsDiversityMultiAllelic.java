/*******************************************************************************
 * Copyright 2014 Herman De Beukelaer, Guy Davenport Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package org.corehunter.objectives.multiallelic;

import java.util.Iterator;

import org.corehunter.data.MultiAllelicGenotypeVariantData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * @author Guy Davenport
 */
public class ShannonsDiversityMultiAllelic implements
	Objective<SubsetSolution, MultiAllelicGenotypeVariantData>
{
	/*
	 * (non-Javadoc)
	 * @see org.jamesframework.core.problems.objectives.Objective#evaluate(org.
	 * jamesframework.core.problems.solutions.Solution, java.lang.Object)
	 */
	@Override
	public Evaluation evaluate(SubsetSolution solution,
	    MultiAllelicGenotypeVariantData data)
	{
		int numberOfMarkers = data.getNumberOfMarkers();
		int numberOfAlleles;

		double summedDiversity = 0;
		double alleleFrequency = 0;

		for (int markerIndex = 0; markerIndex < numberOfMarkers; ++markerIndex)
		{
			numberOfAlleles = data.getNumberOfAlleles(markerIndex);

			for (int alleleIndex = 0; alleleIndex < numberOfAlleles; ++alleleIndex)
			{
				alleleFrequency = data.getAverageAlelleFrequency(solution.getSelectedIDs(), markerIndex,
					    alleleIndex) / numberOfMarkers ;

				if (alleleFrequency > 0)
					summedDiversity = summedDiversity + (alleleFrequency * Math.log(alleleFrequency)) ;
				}
		}
		
		return SimpleEvaluation.WITH_VALUE(-summedDiversity) ;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jamesframework.core.problems.objectives.Objective#isMinimizing()
	 */
	@Override
	public boolean isMinimizing()
	{
		return false;
	}
}
