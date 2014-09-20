/*******************************************************************************
 * Copyright 2014 Guy Davenport
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.corehunter;

import org.jamesframework.core.problems.datatypes.SubsetData;

/**
 * Extends SubsetData to introduce the concept of a
 * distance that indicates how closely two entries are
 * similar to each other. A value of zero for the distance
 * indicates that specific measure used to evaluate the distance 
 * can differentiate between the two entities
 * 
 * @author Guy Davenport
 *
 */
public interface DistanceMatrixData extends SubsetData
{

	/**
	 * Gets the distance between two entities 
	 * 
	 * @param index1 the index of the first entity
	 * @param index2 the index of the first entity
	 * @return
	 */
  public double getDistance(int index1, int index2);

}