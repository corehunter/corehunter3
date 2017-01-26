/*--------------------------------------------------------------*/
/* Licensed to the Apache Software Foundation (ASF) under one   */
/* or more contributor license agreements.  See the NOTICE file */
/* distributed with this work for additional information        */
/* regarding copyright ownership.  The ASF licenses this file   */
/* to you under the Apache License, Version 2.0 (the            */
/* "License"); you may not use this file except in compliance   */
/* with the License.  You may obtain a copy of the License at   */
/*                                                              */
/*   http://www.apache.org/licenses/LICENSE-2.0                 */
/*                                                              */
/* Unless required by applicable law or agreed to in writing,   */
/* software distributed under the License is distributed on an  */
/* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       */
/* KIND, either express or implied.  See the License for the    */
/* specific language governing permissions and limitations      */
/* under the License.                                           */
/*--------------------------------------------------------------*/

package org.corehunter.services;

import java.util.List;

import org.jamesframework.core.subset.SubsetSolution;

/**
 * Services for running CoreHunter
 * 
 * @author daveneti
 *
 */
public interface CoreHunterRunServices {

    /**
     * Starts the execution of a Core Hunter run with the given arguments
     * 
     * @param arguments
     *            the Core Hunter Run Arguments
     * @return The initial CoreHunterRun object containing the current status,
     *         arguments and unique identifier of the run
     * @throws IllegalStateException if the service is not running or shutting down
     */
    public CoreHunterRun executeCoreHunter(CoreHunterRunArguments arguments);

    /**
     * Gets the current information about the Core Hunter run
     * 
     * @param uniqueIdentifier
     *            the unique identifier of the run that was provided on
     *            execution
     * @return the current information about the Core Hunter run, or
     *  <code>null</code> if not such run exists.
     */

    public CoreHunterRun getCoreHunterRun(String uniqueIdentifier);

    /**
     * Removes the CoreHunterRun and tries to stop the run if it is
     * still running, If the run can not be removed, the client will need to
     * check at later time or use the {@link #deleteCoreHunterRun(String)} method
     * 
     * @param uniqueIdentifier
     *            the unique identifier of the run that was provided on
     *            execution
     * @return <code>true</code> if the CoreHunterRun was successfully removed,
     *         <code>false</code> if the run can not be removed.
     * @throws java.util.NoSuchElementException if no such run exists
     */
    public boolean removeCoreHunterRun(String uniqueIdentifier);

    /**
     * Deletes the CoreHunterRun and tries to stop the run if it is
     * still running, This method guarantees to be able to delete the run
     * regardless of if it can be stopped.
     * 
     * @param uniqueIdentifier
     *            the unique identifier of the run that was provided on
     *            execution
     * @throws java.util.NoSuchElementException if no such run exists  
     */
    public void deleteCoreHunterRun(String uniqueIdentifier);
    
    /**
     * Updates the CoreHunterRun. Only editable fields such as the name can be changed.
     * 
     * @param coreHunterRun a CoreHunterRun run of  run that was provided on
     *            execution
     * @throws java.util.NoSuchElementException if no such run exists
     */
    public void updateCoreHunterRun(CoreHunterRun coreHunterRun);

    /**
     * Gets the current information about all Core Hunter runs
     * 
     * @return the current information about all Core Hunter runs
     */
    public List<CoreHunterRun> getAllCoreHunterRuns();

    /**
     * Gets the current output stream provides by the run 
     * 
     * @param uniqueIdentifier
     *            the unique identifier of the run that was provided on
     *            execution
     * @return the current output stream provided by the run 
     */
    public String getOutputStream(String uniqueIdentifier);

    /**
     * Gets the current error stream provided by the run 
     * 
     * @param uniqueIdentifier
     *            the unique identifier of the run that was provided on
     *            execution
     * @return the current error stream provided by the run 
     */
    public String getErrorStream(String uniqueIdentifier);

    /**
     * Gets the current error message provided by the run 
     * 
     * @param uniqueIdentifier
     *            the unique identifier of the run that was provided on
     *            execution
     * @return the current error message provided by the run 
     */
    public String getErrorMessage(String uniqueIdentifier);

    /**
     * Depending on the status the method returns the solution of the run.
     * If the run is still running the method returns the current solution,
     * if the run is finished it will return the final solution, otherwise
     * the method will return <code>null</code>.
     * 
     * @param uniqueIdentifier
     *            the unique identifier of the run that was provided on
     *            execution
     * @return the current error message provided by the run 
     */
    public SubsetSolution getSubsetSolution(String uniqueIdentifier);

    /**
     * Gets the arguments provided when the run was executed
     * 
     * @param uniqueIdentifier
     *            the unique identifier of the run that was provided on
     *            execution
     * @return the arguments provided when the run was executed
     */
    CoreHunterRunArguments getArguments(String uniqueIdentifier);
}
