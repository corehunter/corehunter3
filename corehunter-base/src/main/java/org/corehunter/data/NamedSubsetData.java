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

import org.jamesframework.core.problems.datatypes.SubsetData;

/**
 * @author Guy Davenport
 *
 */
public interface NamedSubsetData extends SubsetData
{
	/**
	 * Gets the name of an entry by id
	 * 
	 * @param id of an entry 
	 * @return the name of an entry by id
	 * @throws ArrayIndexOutOfBoundsException if the id is out of range
	 * 
	 */
	public String getName(int id) throws ArrayIndexOutOfBoundsException ;
}
