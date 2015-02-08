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
package org.corehunter.objectives.distance.multiallelic;

import org.corehunter.data.MultiAllelicGenotypeVariantData;
import org.corehunter.objectives.distance.AbstractGenotypeVariantDistanceMetric;

/**
 * @author Guy Davenport
 */
public class CavalliSforzaEdwardsDistanceMultiAllelic extends
    AbstractGenotypeVariantDistanceMetric<MultiAllelicGenotypeVariantData>
{

	/**
	 * @param data
	 */
	public CavalliSforzaEdwardsDistanceMultiAllelic(
	    MultiAllelicGenotypeVariantData data)
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
		double sqrtDiff = 0;
		double Pxla;
		double Pyla;

		for (int markerIndex = 0; markerIndex < numberOfMarkers; ++markerIndex)
		{
			numberOfAlleles = getData().getNumberOfAlleles(markerIndex);

			for (int alleleIndex = 0; alleleIndex < numberOfAlleles; ++alleleIndex)
			{
				Pxla = getData().getAlelleFrequency(idX, markerIndex, alleleIndex);
				Pyla = getData().getAlelleFrequency(idY, markerIndex, alleleIndex);

				if (Pxla >= 0 && Pyla >= 0)
				{
					sqrtDiff = Math.sqrt(Pxla) - Math.sqrt(Pyla);
					markerSqDiff += (sqrtDiff) * (sqrtDiff);
				}
			}

			sumMarkerSqDiff += markerSqDiff;
		}

		distance = 1.0 / (Math.sqrt(2.0 * numberOfMarkers))
		    * Math.sqrt(sumMarkerSqDiff);

		return distance;
	}
}
