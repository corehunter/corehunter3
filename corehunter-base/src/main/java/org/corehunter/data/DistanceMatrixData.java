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
package org.corehunter.data;

import org.jamesframework.core.problems.datatypes.SubsetData;

/**
 * Extends SubsetData to introduce the concept of a distance that indicates how
 * closely two entries are similar to each other. A value of zero for the
 * distance indicates that specific measure used to evaluate the distance can
 * differentiate between the two entities
 * 
 * @author Guy Davenport
 */
public interface DistanceMatrixData extends SubsetData
{

	/**
	 * Gets the distance between two entities
	 * 
	 * @param idX
	 *          the id of the first entity
	 * @param idY
	 *          the id of the second entity
	 * @return the distance between two entities
	 */
	public double getDistance(int idX, int idY);

}
