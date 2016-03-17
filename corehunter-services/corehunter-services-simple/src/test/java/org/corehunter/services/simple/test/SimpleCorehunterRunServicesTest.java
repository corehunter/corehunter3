
package org.corehunter.services.simple.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.corehunter.services.CorehunterRun;
import org.corehunter.services.CorehunterRunArguments;
import org.corehunter.services.CorehunterRunStatus;
import org.corehunter.services.DatasetType;
import org.corehunter.services.simple.CorehunterRunArgumentsPojo;
import org.corehunter.services.simple.FileBasedDatasetServices;
import org.corehunter.services.simple.SimpleCorehunterRunServices;
import org.junit.Test;

import uno.informatics.common.io.FileType;

public class SimpleCorehunterRunServicesTest {

    private static final String PHENOTYPIC_FILE = "phenotypic_data.csv";
    private static final String NAME1 = "phenotypic_data.csv";
    private static final int SUBSET_SIZE1 = 10;
    private static final String UNIQUE_IDENTIFIER1 = "phenotypic_data.csv";
    private static final String DESCRIPTION1 = "Dataset loading from ";
    private static final String ABBREVIATION1 = null;

    @Test
    public void testSimpleCorehunterRunServices() {

        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            new SimpleCorehunterRunServices(fileBasedDatasetServices);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    //@Test
    public void testExecuteCorehunter() {

        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(
                    Files.createTempDirectory(null));

            Path phenotypicDataPath = Paths.get(ClassLoader.getSystemResource(PHENOTYPIC_FILE).toURI());

            String datasetId = fileBasedDatasetServices.addDataset(phenotypicDataPath, FileType.CSV,
                    DatasetType.PHENOTYPIC);

            SimpleCorehunterRunServices simpleCorehunterRunServices = new SimpleCorehunterRunServices(
                    fileBasedDatasetServices);

            CorehunterRunArguments arguments = new CorehunterRunArgumentsPojo(NAME1, SUBSET_SIZE1, datasetId);

            CorehunterRun startCorehunterRun = simpleCorehunterRunServices.executeCorehunter(arguments);
            
            String corehunterRunId = startCorehunterRun.getUniqueIdentifier() ;

            simpleCorehunterRunServices.getOutputStream(datasetId);

            assertEquals("Status is not not started", CorehunterRunStatus.NOT_STARTED, startCorehunterRun.getStatus());

            CorehunterRun corehunterRun = startCorehunterRun;

            while (CorehunterRunStatus.NOT_STARTED.equals(corehunterRun.getStatus())
                    || CorehunterRunStatus.RUNNING.equals(corehunterRun.getStatus())) {
                corehunterRun = simpleCorehunterRunServices.getCorehunterRun(corehunterRunId);
                
                Thread.sleep(100);
            }

            assertEquals("Status is not finished", CorehunterRunStatus.FINISHED, corehunterRun.getStatus());

            List<CorehunterRun> allCorehunterRuns = simpleCorehunterRunServices.getAllCorehunterRuns();

            assertEquals("Number of corehunterRuns is not 1", 1, allCorehunterRuns.size());

            assertNotNull("Output Stream is null", simpleCorehunterRunServices.getOutputStream(datasetId));

            assertNull("Error Stream is not null", simpleCorehunterRunServices.getErrorStream(datasetId));

            assertNull("Error Message is not null", simpleCorehunterRunServices.getErrorMessage(datasetId));

            assertNotNull("Subset Solution is null", simpleCorehunterRunServices.getSubsetSolution(datasetId));

        } catch (Exception e) {
            e.printStackTrace();

            fail(e.getMessage());
        }
    }
}
