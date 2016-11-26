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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
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
import org.corehunter.services.CoreHunterRunResult;
import org.corehunter.services.CoreHunterRunServices;
import org.corehunter.services.CoreHunterRunStatus;
import org.corehunter.services.DatasetServices;
import org.jamesframework.core.subset.SubsetSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * A simple CoreHunterRunServices implementation. Sub-classes, can use the
 * {@link #SimpleCoreHunterRunServices(DatasetServices) constructor} provided
 * path is defined in the overloaded constructor using the
 * {@link #setPath(Path)} method
 * 
 * @author daveneti
 *
 */
public class SimpleCoreHunterRunServices implements CoreHunterRunServices {

    Logger logger = LoggerFactory.getLogger(SimpleCoreHunterRunServices.class);

    private static final String RESULTS_PATH = "RESULTS_PATH";

    private DatasetServices datasetServices;
    private ExecutorService executor;
    private List<CoreHunterRun> corehunterRuns;
    private Map<String, CoreHunterRunResult> corehunterResultsMap;
    public String charsetName = "utf-8";
    private boolean shuttingDown;
    private boolean shutDown;

    private Path path;

    /**
     * Constructor that can be used by sub-classes provided the path is defined
     * in the overloaded constructor using the {@link #setPath(Path)} method
     * 
     * @param datasetServices
     *            the dataset services in to be used by these services
     * @throws IOException
     *             if the path can not be set or is invalid
     */
    protected SimpleCoreHunterRunServices(DatasetServices datasetServices) throws IOException {
        this.datasetServices = datasetServices;

        executor = createExecutorService();

        corehunterResultsMap = new HashMap<>();
    }

    /**
     * Constructor that is a path to defined the location of the datasets
     * 
     * @param path
     *            the location of the datasets
     * @param datasetServices
     *            the dataset services in to be used by these services
     * @throws IOException
     *             if the path can not be set or is invalid
     */
    public SimpleCoreHunterRunServices(Path path, DatasetServices datasetServices) throws IOException {
        this(datasetServices);

        setPath(path);
    }

    public final Path getPath() {
        return path;
    }

    public synchronized final void setPath(Path path) throws IOException {
        if (path == null) {
            throw new IOException("Path must be defined!");
        }

        this.path = path;

        initialise();
    }

    @Override
    public CoreHunterRun executeCoreHunter(CoreHunterRunArguments arguments) {

        if (shuttingDown) {
            throw new IllegalStateException("Can not accept any new runs, in the process of shutting down!");
        }

        if (shutDown) {
            throw new IllegalStateException("Can not accept any new runs, service is not running!");
        }

        CoreHunterRunnable corehunterRunnable = new CoreHunterRunnable(arguments);

        corehunterResultsMap.put(corehunterRunnable.getUniqueIdentifier(), corehunterRunnable);

        executor.submit(corehunterRunnable);

        return new CoreHunterRunFromRunnable(corehunterRunnable);
    }

    @Override
    public CoreHunterRun getCoreHunterRun(String uniqueIdentifier) {
        CoreHunterRunResult coreHunterRunResult = corehunterResultsMap.get(uniqueIdentifier);

        if (coreHunterRunResult != null) {
            return new CoreHunterRunFromRunnable(coreHunterRunResult);
        } else {
            return null;
        }
    }

    @Override
    public boolean removeCoreHunterRun(String uniqueIdentifier) {
        CoreHunterRunResult coreHunterRunResult = corehunterResultsMap.get(uniqueIdentifier);

        if (coreHunterRunResult != null && coreHunterRunResult instanceof CoreHunterRunnable) {
            boolean stopped = ((CoreHunterRunnable) coreHunterRunResult).stop();

            if (stopped) {
                // only remove if it was stopped
                corehunterResultsMap.remove(uniqueIdentifier);

                return true;
            }
        }

        return false;
    }

    @Override
    public void deleteCoreHunterRun(String uniqueIdentifier) {
        // remove regardless if it can not be stopped
        CoreHunterRunResult coreHunterRunResult = corehunterResultsMap.remove(uniqueIdentifier);

        if (coreHunterRunResult != null && coreHunterRunResult instanceof CoreHunterRunnable) {
            if (!((CoreHunterRunnable) coreHunterRunResult).stop()) {
                logger.error("Can not stop runnable {}", coreHunterRunResult.getName());
            }
        }
    }

    @Override
    public List<CoreHunterRun> getAllCoreHunterRuns() {

        // iterates through all runnables can create new CoreHunterRun objects,
        // which will be a snapshot
        // of the current status of that runnable
        Iterator<CoreHunterRunResult> iterator = corehunterResultsMap.values().iterator();

        corehunterRuns = new ArrayList<>(corehunterResultsMap.size());

        while (iterator.hasNext()) {
            corehunterRuns.add(new CoreHunterRunFromRunnable(iterator.next()));
        }

        return corehunterRuns;
    }

    @Override
    public String getOutputStream(String uniqueIdentifier) {
        CoreHunterRunResult corehunterRunnable = corehunterResultsMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getOutputStream();
        } else {
            return null;
        }
    }

    @Override
    public String getErrorStream(String uniqueIdentifier) {
        CoreHunterRunResult corehunterRunnable = corehunterResultsMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getErrorStream();
        } else {
            return null;
        }
    }

    @Override
    public String getErrorMessage(String uniqueIdentifier) {
        CoreHunterRunResult corehunterRunnable = corehunterResultsMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getErrorMessage();
        } else {
            return null;
        }
    }

    @Override
    public SubsetSolution getSubsetSolution(String uniqueIdentifier) {
        CoreHunterRunResult corehunterRunnable = corehunterResultsMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getSubsetSolution();
        } else {
            return null;
        }
    }

    @Override
    public CoreHunterRunArguments getArguments(String uniqueIdentifier) {
        CoreHunterRunResult corehunterRunnable = corehunterResultsMap.get(uniqueIdentifier);

        if (corehunterRunnable != null) {
            return corehunterRunnable.getArguments();
        } else {
            return null;
        }
    }

    public void shutdown() {
        if (!shuttingDown || shutDown) {
            shuttingDown = true;
            executor.shutdown();
            shutDown = true;
        }
    }

    private ExecutorService createExecutorService() {

        return Executors.newSingleThreadExecutor();
    }

    private String createUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private void initialise() throws IOException {

        Path resultsPath = Paths.get(getPath().toString(), RESULTS_PATH);

        if (!Files.exists(resultsPath)) {
            Files.createDirectories(resultsPath);
        }

        Iterator<Path> iterator = Files.list(resultsPath).iterator();

        while (iterator.hasNext()) {
            loadResult(iterator.next());
        }
    }

    private void loadResult(Path path) {

        try {
            CoreHunterRunResult result = (CoreHunterRunResult) readFromFile(path);

            corehunterResultsMap.put(result.getUniqueIdentifier(), result);

        } catch (IOException e) {
            logger.error("Can not load result from path {} due to {}", path, e.getMessage());
            logger.error("Full error ", e);

            e.printStackTrace();
        }

    }

    private void saveResult(CoreHunterRunResultPojo coreHunterRunResult) {

        Path path = Paths.get(getPath().toString(), RESULTS_PATH, coreHunterRunResult.getUniqueIdentifier());

        logger.info("Writing result for {} with id {} to path {}", coreHunterRunResult.getName(),
                coreHunterRunResult.getUniqueIdentifier(), path.toString());

        try {
            writeToFile(path, coreHunterRunResult);
        } catch (IOException e) {
            logger.error("Can not save result to path {} due to {}", path, e.getMessage());
            logger.error("Full error ", e);

            e.printStackTrace();
        }
    }

    /**
     * Reads an object from a file. The default implementation uses XStream.
     * Override to use another way to read objects. Must be compatible with the
     * {@link #writeToFile(Path, Object)} method
     * 
     * @param path
     *            the path of the file to be read
     * @return the object read from the file
     * @throws IOException
     *             if the object can not be read from the file
     */
    protected Object readFromFile(Path path) throws IOException {
        XStream xstream = createXStream();

        InputStream inputStream = Files.newInputStream(path);

        // TODO output to temp file and then copy

        try {
            return xstream.fromXML(inputStream);
        } catch (XStreamException e) {
            e.printStackTrace();

            throw new IOException(e);
        }
    }

    /**
     * Write an object to a file. The default implementation uses XStream.
     * Override to use another way to write objects. Must be compatible with the
     * {@link #readFromFile(Path)} method
     * 
     * @param path
     *            the path of the file to be written
     * @param object
     *            the object to be written
     * @throws IOException
     *             if the object can not be write to the file
     */
    protected void writeToFile(Path path, Object object) throws IOException {
        XStream xstream = createXStream();

        OutputStream outputStream;

        // TODO output to temp file and then copy

        outputStream = Files.newOutputStream(path);

        try {
            xstream.marshal(object, new PrettyPrintWriter(new OutputStreamWriter(outputStream)));
        } catch (XStreamException e) {
            throw new IOException(e);
        }
    }

    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());

        xstream.setClassLoader(getClass().getClassLoader());

        return xstream;
    }

    private class CoreHunterRunnable extends SimpleEntityPojo implements Runnable, CoreHunterRunResult {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private CoreHunterRunArguments corehunterRunArguments;
        private transient CoreHunter corehunter;
        private transient ByteArrayOutputStream outputStream;
        private transient ByteArrayOutputStream errorStream;
        private String errorMessage;
        private transient SubsetSolution subsetSolution;
        private Instant startInstant;
        private Instant endInstant;
        private CoreHunterRunStatus status;

        public CoreHunterRunnable(CoreHunterRunArguments corehunterRunArguments) {
            super(createUniqueIdentifier(), corehunterRunArguments.getName());
            this.corehunterRunArguments = new CoreHunterRunArgumentsPojo(corehunterRunArguments);

            status = CoreHunterRunStatus.NOT_STARTED;
            outputStream = new ByteArrayOutputStream();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.corehunter.services.simple.CoreHunterResult#getOutputStream()
         */
        @Override
        public final String getOutputStream() {
            if (outputStream != null) {

                try {
                    outputStream.flush();
                    return outputStream.toString(charsetName);
                } catch (UnsupportedEncodingException e) {
                    return outputStream.toString();
                } catch (IOException e) {
                    return "Output stream can not flushed!";
                }
            } else {
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.corehunter.services.simple.CoreHunterResult#getErrorStream()
         */
        @Override
        public final String getErrorStream() {
            if (errorStream != null) {
                try {
                    errorStream.flush();
                    return errorStream.toString(charsetName);
                } catch (UnsupportedEncodingException e) {
                    return errorStream.toString();
                } catch (IOException e) {
                    return "Error stream can not flushed!";
                }
            } else {
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.corehunter.services.simple.CoreHunterResult#getErrorMessage()
         */
        @Override
        public synchronized final String getErrorMessage() {
            return errorMessage;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.corehunter.services.simple.CoreHunterResult#getSubsetSolution()
         */
        @Override
        public synchronized final SubsetSolution getSubsetSolution() {
            return subsetSolution;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.corehunter.services.simple.CoreHunterResult#getStartDate()
         */
        @Override
        public synchronized final Instant getStartInstant() {
            return startInstant;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.corehunter.services.simple.CoreHunterResult#getEndDate()
         */
        @Override
        public synchronized final Instant getEndInstant() {
            return endInstant;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.corehunter.services.simple.CoreHunterResult#getStatus()
         */
        @Override
        public synchronized CoreHunterRunStatus getStatus() {
            return status;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.corehunter.services.simple.CoreHunterResult#getArguments()
         */
        @Override
        public final CoreHunterRunArguments getArguments() {
            return corehunterRunArguments;
        }

        @Override
        public void run() {

            outputStream = new ByteArrayOutputStream();
            PrintStream outputPrintStream = new PrintStream(outputStream);

            try {
                startInstant = Instant.now() ;
                status = CoreHunterRunStatus.RUNNING;

                outputPrintStream.println(String.format("Starting run : %s at %s", getName(), startInstant.toString()));

                CoreHunterArguments arguments = new CoreHunterArguments(
                        datasetServices.getCoreHunterData(corehunterRunArguments.getDatasetId()),
                        corehunterRunArguments.getSubsetSize(), corehunterRunArguments.getObjectives());

                corehunter = new CoreHunter();
                corehunter.setListener(new SimpleCoreHunterListener(outputPrintStream));

                subsetSolution = corehunter.execute(arguments);

                status = CoreHunterRunStatus.FINISHED;
            } catch (Exception e) {
                status = CoreHunterRunStatus.FAILED;
                errorMessage = e.getMessage();
                errorStream = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(errorStream);
                e.printStackTrace(printStream);

                outputPrintStream.println(String.format(
                        "Error in run : %s at due to %s. See error log for more details", getName(), errorMessage));

                printStream.close();
            }

            endInstant = Instant.now() ;
            outputPrintStream.println(String.format("Ending run : %s at %s", getName(), endInstant.toString()));

            saveResult(new CoreHunterRunResultPojo(this));

            outputPrintStream.close();
        }

        public boolean stop() {
            return false; // This simple implementation can not be stopped
        }

    }

    private class CoreHunterRunFromRunnable extends CoreHunterRunPojo {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public CoreHunterRunFromRunnable(CoreHunterRunResult coreHunterRunResult) {
            super(coreHunterRunResult.getUniqueIdentifier(), coreHunterRunResult.getName());

            setStartDate(coreHunterRunResult.getStartInstant());
            setEndDate(coreHunterRunResult.getEndInstant());
            setStatus(coreHunterRunResult.getStatus());
        }

    }
}
