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
import org.corehunter.listener.SimpleCorehunterListener;
import org.corehunter.services.CorehunterRun;
import org.corehunter.services.CorehunterRunArguments;
import org.corehunter.services.CorehunterRunServices;
import org.corehunter.services.CorehunterRunStatus;
import org.corehunter.services.DatasetServices;
import org.jamesframework.core.subset.SubsetSolution;
import org.joda.time.DateTime;

import uno.informatics.data.pojo.SimpleEntityPojo;

public class SimpleCorehunterRunServices implements CorehunterRunServices {

    private DatasetServices datasetServices;
    private ExecutorService executor;
    private List<CorehunterRun> corehunterRuns;
    private Map<String, CorehunterRunnable> corehunterRunnableMap;
    public String charsetName = "utf-8";
    private boolean shuttingDown;
    private boolean shutDown;

    public SimpleCorehunterRunServices(DatasetServices datasetServices) {
        this.datasetServices = datasetServices;

        executor = createExecutorService();

        corehunterRunnableMap = new HashMap<>();
    }

    @Override
    public CorehunterRun executeCorehunter(CorehunterRunArguments arguments) {

        if (shuttingDown) {
            throw new IllegalStateException("Can not accept any new runs, in the process of shutting down!") ;
        }
        
        if (shutDown) {
            throw new IllegalStateException("Can not accept any new runs, service is not running!") ;
        }
        
        CorehunterRunnable corehunterRunnable = new CorehunterRunnable(arguments);

        corehunterRunnableMap.put(corehunterRunnable.getUniqueIdentifier(), corehunterRunnable);

        executor.submit(corehunterRunnable);

        return new CorehunterRunFromRunnable(corehunterRunnable);
    }

    @Override
    public CorehunterRun getCorehunterRun(String uniqueIdentifier) {
        CorehunterRunnable corehunterRunnable = corehunterRunnableMap.remove(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return new CorehunterRunFromRunnable(corehunterRunnable);
        } else {
            return null;
        }
    }

    @Override
    public boolean removeCorehunterRun(String uniqueIdentifier) {
        CorehunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);
        
        // TODO needs to try to stop run!

        return corehunterRunnable != null;
    }
    
    @Override
    public void deleteCorehunterRun(String uniqueIdentifier) {
        CorehunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        // TODO needs to try to stop run or kill it
    }

    @Override
    public List<CorehunterRun> getAllCorehunterRuns() {

        // iterates through all runnables can create new CorehunterRun objects, which will be a snapshot 
        // of the current status of that runnable
        Iterator<CorehunterRunnable> iterator = corehunterRunnableMap.values().iterator();

        corehunterRuns = new ArrayList<>(corehunterRunnableMap.size());

        while (iterator.hasNext()) {
            corehunterRuns.add(new CorehunterRunFromRunnable(iterator.next()));
        }

        return corehunterRuns;
    }

    @Override
    public String getOutputStream(String uniqueIdentifier) {
        CorehunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getOutputStream();
        } else {
            return null;
        }
    }

    @Override
    public String getErrorStream(String uniqueIdentifier) {
        CorehunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getErrorStream();
        } else {
            return null;
        }
    }

    @Override
    public String getErrorMessage(String uniqueIdentifier) {
        CorehunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getErrorMessage();
        } else {
            return null;
        }
    }

    @Override
    public SubsetSolution getSubsetSolution(String uniqueIdentifier) {
        CorehunterRunnable corehunterRunnable = corehunterRunnableMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getSubsetSolution();
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

    private class CorehunterRunnable extends SimpleEntityPojo implements Runnable {
        private CorehunterRunArguments corehunterRunArguments;
        private CoreHunter corehunter;
        private ByteArrayOutputStream outputStream;
        private ByteArrayOutputStream errorStream;
        private String errorMessage;
        private SubsetSolution subsetSolution;
        private DateTime startDate;
        private DateTime endDate;
        private CorehunterRunStatus status;

        public CorehunterRunnable(CorehunterRunArguments corehunterRunArguments) {
            super(createUniqueIdentifier(), corehunterRunArguments.getName());
            this.corehunterRunArguments = corehunterRunArguments;

            status = CorehunterRunStatus.NOT_STARTED;
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

        public CorehunterRunStatus getStatus() {
            return status;
        }

        @Override
        public void run() {

            try {
                startDate = new DateTime();

                CoreHunterArguments arguments = new CoreHunterArguments(
                        datasetServices.getCoreHunterData(corehunterRunArguments.getDatasetId()),
                        corehunterRunArguments.getSubsetSize(), corehunterRunArguments.getObjectives());

                PrintStream printStream = new PrintStream(outputStream);

                corehunter = new CoreHunter(new SimpleCorehunterListener(printStream));

                outputStream = new ByteArrayOutputStream();
                
                status = CorehunterRunStatus.RUNNING;

                subsetSolution = corehunter.execute(arguments);

                printStream.close();

                status = CorehunterRunStatus.FINISHED;
            } catch (Exception e) {
                status = CorehunterRunStatus.FAILED;
                errorMessage = e.getMessage();
                errorStream = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(errorStream);
                e.printStackTrace(printStream);

                printStream.close();
            }

            endDate = new DateTime();

        }

    }

    private class CorehunterRunFromRunnable extends CorehunterRunPojo {

        public CorehunterRunFromRunnable(CorehunterRunnable corehunterRunnable) {
            super(corehunterRunnable.getUniqueIdentifier(), corehunterRunnable.getName());

            setStartDate(corehunterRunnable.getStartDate());
            setEndDate(corehunterRunnable.getEndDate());
            setStatus(corehunterRunnable.getStatus());
        }

    }
}
