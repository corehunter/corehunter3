/*******************************************************************************
 * Copyright 2015 Guy Davenport, Herman De Beukelaer
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
package org.corehunter.data;

/**
 * @author Guy Davenport
 *
 */
public interface NamedGenotypeVariantData extends GenotypeVariantData
{
	/**
	 * Gets the name of a marker by index
	 * 
	 * @param markerIndex of a marker 
	 * @return the name of an marker by index
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 */
	public String getMarkerName(int markerIndex) throws ArrayIndexOutOfBoundsException ;
}
