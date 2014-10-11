/*******************************************************************************
 * Copyright 2014 Guy Davenport Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package org.corehunter.objectives;

import org.corehunter.MultiAllelicGenotypeVariantData;

/**
 * @author Guy Davenport
 */
public class ModifiedRogersDistanceMultiAllelic extends
    AbstractGenotypeVariantDistanceMetric<MultiAllelicGenotypeVariantData>
{

	/**
	 * @param data
	 */
	public ModifiedRogersDistanceMultiAllelic(MultiAllelicGenotypeVariantData data)
	{
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * @see org.corehunter.DistanceMatrixData#getDistance(int, int)
	 */
	@Override
	public double getDistance(int idX, int idY)
	{
		double distance = 0;

		int numberOfMarkers = getData().getNumberOfMarkers();
		int numberOfAlleles;

		double markerSqDiff = 0;
		double sumMarkerSqDiff = 0;

		for (int markerIndex = 0; markerIndex < numberOfMarkers; ++markerIndex)
		{
			numberOfAlleles = getData().getNumberOfAllele(markerIndex);

			for (int alleleIndex = 0; alleleIndex < numberOfAlleles; ++alleleIndex)
			{
				double Pxla = getData().getAlelleFrequency(idX, markerIndex,
				    alleleIndex);
				double Pyla = getData().getAlelleFrequency(idY, markerIndex,
				    alleleIndex);

				if (Pxla >= 0 && Pyla >= 0)
				{
					markerSqDiff += (Pxla - Pyla) * (Pxla - Pyla);
				}
			}

			sumMarkerSqDiff += markerSqDiff;
		}

		distance = 1.0 / (Math.sqrt(2.0 * numberOfMarkers))
		    * Math.sqrt(sumMarkerSqDiff);

		return distance;
	}

}
