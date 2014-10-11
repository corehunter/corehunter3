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

import org.corehunter.MultiAllelicGenotypeVariantData;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.solutions.SubsetSolution;

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
	public double evaluate(SubsetSolution solution,
	    MultiAllelicGenotypeVariantData data)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jamesframework.core.problems.objectives.Objective#isMinimizing()
	 */
	@Override
	public boolean isMinimizing()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
