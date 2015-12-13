/*******************************************************************************
 * Copyright 2015 Herman De Beukelaer, Guy Davenport Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package org.corehunter;

import org.jamesframework.core.search.listeners.SearchListener;
import org.jamesframework.core.subset.SubsetSolution;

/**
 * Extends SearchListener with corehunter specific steps.
 * 
 * @author daveneti
 *
 */
public interface CorehunterListener extends SearchListener<SubsetSolution>
{
  /**
   * Fired when the pre-processing has started. Called only once prior to the search run
   * 
   * @param message the message to be sent to the listener
   */
  default public void preprocessingStarted(String message) {}
  
  /**
   * Fired when the pre-processing has stopped. Called only once prior to the search run
   * 
   * @param message the message to be sent to the listener
   */
  default public void preprocessingStopped(String message) {}
}
