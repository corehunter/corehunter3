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

package org.corehunter.services.simple;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.corehunter.CoreHunter;
import org.corehunter.CoreHunterArguments;
import org.corehunter.listener.SimpleCoreHunterListener;
import org.corehunter.services.CoreHunterRun;
import org.corehunter.services.CoreHunterRunArguments;
import org.corehunter.services.CoreHunterRunServices;
import org.corehunter.services.CoreHunterRunStatus;
import org.corehunter.services.DatasetServices;
import org.jamesframework.core.subset.SubsetSolution;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uno.informatics.data.pojo.SimpleEntityPojo;

public class SimpleCoreHunterRunServices implements CoreHunterRunServices {

    Logger logger = LoggerFactory.getLogger(SimpleCoreHunterRunServices.class);
    
    private DatasetServices datasetServices;
    private ExecutorService executor;
    private List<CoreHunterRun> corehunterRuns;
    private Map<String, CoreHunterRunnable> corehunterRunnableMap;
    public String charsetName = "utf-8";
    private boolean shuttingDown;
    private boolean shutDown;

    public SimpleCoreHunterRunServices(DatasetServices datasetServices) {
        this.datasetServices = datasetServices;

        executor = createExecutorService();

        corehunterRunnableMap = new HashMap<>();
    }

    @Override
    public CoreHunterRun executeCoreHunter(CoreHunterRunArguments arguments) {

        if (shuttingDown) {
            throw new IllegalStateException("Can not accept any new runs, in the process of shutting down!") ;
        }
        
        if (shutDown) {
            throw new IllegalStateException("Can not accept any new runs, service is not running!") ;
        }
        
        CoreHunterRunnable corehunterRunnable = new CoreHunterRunnable(arguments);

        corehunterRunnableMap.put(corehunterRunnable.getUniqueIdentifier(), corehunterRunnable);

        executor.submit(corehunterRunnable);

        return new CoreHunterRunFromRunnable(corehunterRunnable);
    }

    @Override
    public CoreHunterRun getCoreHunterRun(String uniqueIdentifier) {
        CoreHunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return new CoreHunterRunFromRunnable(corehunterRunnable);
        } else {
            return null;
        }
    }

    @Override
    public boolean removeCoreHunterRun(String uniqueIdentifier) {
        CoreHunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);
        
        if (corehunterRunnable != null) {
            boolean stopped = corehunterRunnable.stop(); 
            
            if (stopped) {
                corehunterRunnableMap.remove(uniqueIdentifier); // only remove if it was stopped
                
                return true ;
            }
        }
        
        return false ;
    }
    
    @Override
    public void deleteCoreHunterRun(String uniqueIdentifier) {
        // remove regardless if it can not be stopped
        CoreHunterRunnable corehunterRunnable = corehunterRunnableMap.remove(uniqueIdentifier); 

        if (corehunterRunnable != null) {
            if (!corehunterRunnable.stop()) {
                logger.error("Can not stop runnable {}", corehunterRunnable.getName()); 
            }
        }
    }

    @Override
    public List<CoreHunterRun> getAllCoreHunterRuns() {

        // iterates through all runnables can create new CoreHunterRun objects, which will be a snapshot 
        // of the current status of that runnable
        Iterator<CoreHunterRunnable> iterator = corehunterRunnableMap.values().iterator();

        corehunterRuns = new ArrayList<>(corehunterRunnableMap.size());

        while (iterator.hasNext()) {
            corehunterRuns.add(new CoreHunterRunFromRunnable(iterator.next()));
        }

        return corehunterRuns;
    }

    @Override
    public String getOutputStream(String uniqueIdentifier) {
        CoreHunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getOutputStream();
        } else {
            return null;
        }
    }

    @Override
    public String getErrorStream(String uniqueIdentifier) {
        CoreHunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getErrorStream();
        } else {
            return null;
        }
    }

    @Override
    public String getErrorMessage(String uniqueIdentifier) {
        CoreHunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getErrorMessage();
        } else {
            return null;
        }
    }

    @Override
    public SubsetSolution getSubsetSolution(String uniqueIdentifier) {
        CoreHunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getSubsetSolution();
        } else {
            return null;
        }
    }
    
    @Override
    public CoreHunterRunArguments getArguments(String uniqueIdentifier) {
        CoreHunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getArguments();
        } else {
            return null;
        }
    }
    
    public void shutdown() {
        if (!shuttingDown || shutDown) {
            shuttingDown = true ;
            executor.shutdown(); 
            shutDown = true ;
        }
    }

    private ExecutorService createExecutorService() {

        return Executors.newSingleThreadExecutor();
    }

    private String createUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private class CoreHunterRunnable extends SimpleEntityPojo implements Runnable {
        private CoreHunterRunArguments corehunterRunArguments;
        private CoreHunter corehunter;
        private ByteArrayOutputStream outputStream;
        private ByteArrayOutputStream errorStream;
        private String errorMessage;
        private SubsetSolution subsetSolution;
        private DateTime startDate;
        private DateTime endDate;
        private CoreHunterRunStatus status;

        public CoreHunterRunnable(CoreHunterRunArguments corehunterRunArguments) {
            super(createUniqueIdentifier(), corehunterRunArguments.getName());
            this.corehunterRunArguments = new CoreHunterRunArgumentsPojo(corehunterRunArguments);

            status = CoreHunterRunStatus.NOT_STARTED;
            outputStream = new ByteArrayOutputStream() ;
        }

        public final String getOutputStream() {
            try {
                return outputStream.toString(charsetName);
            } catch (UnsupportedEncodingException e) {
                return outputStream.toString();
            }
        }

        public final String getErrorStream() {
            try {
                return errorStream.toString(charsetName);
            } catch (UnsupportedEncodingException e) {
                return errorStream.toString();
            }
        }

        public synchronized final String getErrorMessage() {
            return errorMessage;
        }

        public synchronized final SubsetSolution getSubsetSolution() {
            return subsetSolution;
        }

        public synchronized final DateTime getStartDate() {
            return startDate;
        }

        public synchronized final DateTime getEndDate() {
            return endDate;
        }

        public synchronized CoreHunterRunStatus getStatus() {
            return status;
        }

        public final CoreHunterRunArguments getArguments() {
            return corehunterRunArguments;
        }

        @Override
        public void run() {

            try {
                startDate = new DateTime();

                CoreHunterArguments arguments = new CoreHunterArguments(
                        datasetServices.getCoreHunterData(corehunterRunArguments.getDatasetId()),
                        corehunterRunArguments.getSubsetSize(), corehunterRunArguments.getObjectives());

                PrintStream printStream = new PrintStream(outputStream);

                status = CoreHunterRunStatus.RUNNING;

                corehunter = new CoreHunter();
                corehunter.setListener(new SimpleCoreHunterListener(printStream));

                outputStream = new ByteArrayOutputStream();

                subsetSolution = corehunter.execute(arguments);
                printStream.close();

                status = CoreHunterRunStatus.FINISHED;
            } catch (Exception e) {
                status = CoreHunterRunStatus.FAILED;
                errorMessage = e.getMessage();
                errorStream = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(errorStream);
                e.printStackTrace(printStream);

                printStream.close();
            }

            endDate = new DateTime();

        }
        
        public boolean stop() {
            return false ; // This simple implementation can not be stopped
        }
        

    }

    private class CoreHunterRunFromRunnable extends CoreHunterRunPojo {

        public CoreHunterRunFromRunnable(CoreHunterRunnable corehunterRunnable) {
            super(corehunterRunnable.getUniqueIdentifier(), corehunterRunnable.getName());

            setStartDate(corehunterRunnable.getStartDate());
            setEndDate(corehunterRunnable.getEndDate());
            setStatus(corehunterRunnable.getStatus());
        }

    }
}
