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

package org.corehunter.tests.data.simple;

import static org.corehunter.tests.TestData.HEADERS_NON_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_EXPLICIT_BOUNDS;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_INFERRED_BOUNDS;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_MISSING_VALUES;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_NAMES;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_VALUES;
import static org.corehunter.tests.TestData.SET;

import org.corehunter.data.simple.SimplePhenotypeData;
import org.jamesframework.core.subset.SubsetSolution;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uno.informatics.data.DataType;
import uno.informatics.data.Feature;
import uno.informatics.data.ScaleType;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.dataset.FeatureDataRow;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.FeaturePojo;
import uno.informatics.data.pojo.MethodPojo;
import uno.informatics.data.pojo.ScalePojo;

/**
 * @author Herman De Beukelaer
 */
public class SimplePhenotypeDataTest {

    private static final String CSV_IDS = "/phenotypes/ids.csv";
    private static final String CSV_IDS_NAMES = "/phenotypes/ids-and-names.csv";
    private static final String CSV_IDS_MIN_MAX = "/phenotypes/ids-min-max.csv";
    private static final String CSV_IDS_MISSING_VALUES = "/phenotypes/missing-values.csv";
    private static final String LONG_FILE = "/phenotypes/phenotype_data.csv";
    
    private static final String ERRONEOUS_FILES_DIR = "/phenotypes/err/";

    private SimpleEntity[] expectedHeaders;
    private Object[][] expectedBounds;
    private Object[][] expectedValues;
    private String dataName;
    
    private static final String TEST_OUTPUT = "target/testoutput";

    protected final static Object[] OBJECT_ROW1 = new Object[] {
        "Alice", 1, 1.1, "R1C3", true, "12/12/2012"
    };
    protected final static Object[] OBJECT_ROW2 = new Object[] {
        "Bob", 2, 2.2, "R2C3", false, "13/12/2012"
    };
    protected final static Object[] OBJECT_ROW3 = new Object[] {
        "Carol", 3, 3.3, "R3C3", true, "14/12/2012"
    };

    protected final static Object[] OBJECT_COL1 = new Object[] {
        OBJECT_ROW1[0], OBJECT_ROW2[0], OBJECT_ROW3[0]
    };

    protected final static Object[] OBJECT_COL2 = new Object[] {
        OBJECT_ROW1[1], OBJECT_ROW2[1], OBJECT_ROW3[1]
    };

    protected final static Object[] OBJECT_COL3 = new Object[] {
        OBJECT_ROW1[2], OBJECT_ROW2[2], OBJECT_ROW3[2]
    };

    protected final static Object[] OBJECT_COL4 = new Object[] {
        OBJECT_ROW1[3], OBJECT_ROW2[3], OBJECT_ROW3[3]
    };

    protected final static Object[] OBJECT_COL5 = new Object[] {
        OBJECT_ROW1[4], OBJECT_ROW2[4], OBJECT_ROW3[4]
    };

    protected static final List<Feature> OBJECT_FEATURES_MIN_MAX_COL = new ArrayList<>();

    static {
        OBJECT_FEATURES_MIN_MAX_COL.add(new FeaturePojo("col1", "Col 1", new MethodPojo("col1", "Col 1",
            new ScalePojo("col1", "Col 1", DataType.INTEGER, ScaleType.INTERVAL, 0, 4, OBJECT_COL1))));
        OBJECT_FEATURES_MIN_MAX_COL.add(new FeaturePojo("col2", "Col 2", new MethodPojo("col2", "Col 2",
            new ScalePojo("col2", "Col 2", DataType.DOUBLE, ScaleType.RATIO, 0.0, 4.0))));
        OBJECT_FEATURES_MIN_MAX_COL.add(new FeaturePojo("col3", "Col 3", new MethodPojo("col3", "Col 3",
            new ScalePojo("col3", "Col 3", DataType.STRING, ScaleType.NOMINAL, OBJECT_COL3))));
        OBJECT_FEATURES_MIN_MAX_COL.add(new FeaturePojo("col4", "Col 4", new MethodPojo("col4", "Col 4",
            new ScalePojo("col4", "Col 4", DataType.BOOLEAN, ScaleType.NOMINAL, OBJECT_COL4))));
        OBJECT_FEATURES_MIN_MAX_COL.add(new FeaturePojo("col5", "Col 5", new MethodPojo("col5", "Col 5",
            new ScalePojo("col5", "Col 5", DataType.STRING, ScaleType.NOMINAL, OBJECT_COL5))));
    }

    protected static final List<List<Object>> OBJECT_TABLE_AS_LIST = new ArrayList<>();

    static {        
        OBJECT_TABLE_AS_LIST.add(Arrays.asList(OBJECT_ROW1));
        OBJECT_TABLE_AS_LIST.add(Arrays.asList(OBJECT_ROW2));
        OBJECT_TABLE_AS_LIST.add(Arrays.asList(OBJECT_ROW3));
    }

    private static final int[] SELECTION = new int[] {
        0, 2
    };

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Test simple phenotypic trait data");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("Done");
    }

    @Test
    public void fromCsvFileWithIds() throws IOException {
        dataName = "ids.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedBounds = PHENOTYPIC_TRAIT_INFERRED_BOUNDS;
        expectedValues = PHENOTYPIC_TRAIT_VALUES;
        System.out.println(" |- File " + dataName);
        testData(SimplePhenotypeData.readPhenotypeData(
            Paths.get(SimplePhenotypeDataTest.class.getResource(CSV_IDS).getPath()), FileType.CSV));
    }

    @Test
    public void fromCsvFileWithIdsAndNames() throws IOException {
        dataName = "ids-and-names.csv";
        expectedHeaders = HEADERS_NON_UNIQUE_NAMES;
        expectedBounds = PHENOTYPIC_TRAIT_INFERRED_BOUNDS;
        expectedValues = PHENOTYPIC_TRAIT_VALUES;
        System.out.println(" |- File " + dataName);
        testData(SimplePhenotypeData.readPhenotypeData(
            Paths.get(SimplePhenotypeDataTest.class.getResource(CSV_IDS_NAMES).getPath()), FileType.CSV));
    }

    @Test
    public void fromCsvFileWithIdsAndBounds() throws IOException {
        dataName = "ids-min-max.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedBounds = PHENOTYPIC_TRAIT_EXPLICIT_BOUNDS;
        expectedValues = PHENOTYPIC_TRAIT_VALUES;
        System.out.println(" |- File " + dataName);
        testData(SimplePhenotypeData.readPhenotypeData(
            Paths.get(SimplePhenotypeDataTest.class.getResource(CSV_IDS_MIN_MAX).getPath()), FileType.CSV));
    }

    @Test
    public void fromCsvFileWithMissingValues() throws IOException {
        dataName = "missing-values.csv";
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        expectedBounds = PHENOTYPIC_TRAIT_INFERRED_BOUNDS;
        expectedValues = PHENOTYPIC_TRAIT_MISSING_VALUES;
        System.out.println(" |- File " + dataName);
        testData(SimplePhenotypeData.readPhenotypeData(
            Paths.get(SimplePhenotypeDataTest.class.getResource(CSV_IDS_MISSING_VALUES).getPath()),
            FileType.CSV));
    }
    
    @Test
    public void fromCsvFileLargeFiles() throws IOException {
        SimplePhenotypeData.readPhenotypeData(
            Paths.get(SimplePhenotypeDataTest.class.getResource(LONG_FILE).getPath()),
            FileType.CSV);
    }

    @Test
    public void erroneousFiles() throws IOException {
        System.out.println(" |- Test erroneous files:");
        Path dir = Paths.get(SimplePhenotypeDataTest.class.getResource(ERRONEOUS_FILES_DIR).getPath());
        try (DirectoryStream<Path> directory = Files.newDirectoryStream(dir)) {
            for (Path file : directory) {
                System.out.print("  |- " + file.getFileName().toString() + ": ");
                FileType type = file.toString().endsWith(".txt") ? FileType.TXT : FileType.CSV;
                boolean thrown = false;
                try {
                    SimplePhenotypeData.readPhenotypeData(file, type);
                } catch (IOException | IllegalArgumentException ex) {
                    thrown = true;
                    System.out.print(ex.getMessage());
                } finally {
                    System.out.println();
                }
                assertTrue("File " + file + " should throw exception.", thrown);
            }
        }
    }

    private void testData(FeatureData data) {

        // check data name, if set
        String expectedDatasetName = dataName != null ? dataName : "Phenotypic trait data";
        assertEquals("Incorrect dataset name.", expectedDatasetName, data.getName());

        // check IDs
        assertEquals("Ids not correct.", SET, data.getIDs());
        // check size
        assertEquals("Incorrect dataset size.", SET.size(), data.getSize());

        // check trait names and bounds (min/max)
        for (int t = 0; t < data.getFeatures().size(); t++) {

            // check name
            assertEquals("Trait name for trait " + t + " is not correct.", PHENOTYPIC_TRAIT_NAMES[t],
                data.getFeatures().get(t).getName());

            // check min and max
            assertEquals("Minimum value for trait " + t + " is not correct.", expectedBounds[t][0],
                data.getFeatures().get(t).getMethod().getScale().getMinimumValue());
            assertEquals("Maximum value for trait " + t + " is not correct.", expectedBounds[t][1],
                data.getFeatures().get(t).getMethod().getScale().getMaximumValue());

        }

        // check individuals (headers and trait values)
        int size = data.getSize();

        for (int i = 0; i < size; i++) {

            // check header
            assertEquals("Header for individual " + i + " is not correct.", expectedHeaders[i],
                data.getHeader(i));
            // check name and id separately
            if (expectedHeaders[i] != null) {
                assertNotNull("Header not defined for individual " + i + ".", data.getHeader(i));
                assertEquals("Name for individual " + i + " is not correct.", expectedHeaders[i].getName(),
                    data.getHeader(i).getName());
                assertEquals("Id for individual " + i + " is not correct.",
                    expectedHeaders[i].getUniqueIdentifier(), data.getHeader(i).getUniqueIdentifier());
            }

            // check trait values
            FeatureDataRow row = data.getRow(i);
            for (int t = 0; t < row.getColumnCount(); t++) {
                assertEquals("Incorrect value for trait " + t + " in individual " + i + ".",
                    expectedValues[i][t], row.getValue(t));
            }

        }

    }

    @Test
    public void toCsvFileWithAllIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        SimplePhenotypeData phenotypeData = new SimplePhenotypeData("Phenotype Data",
            OBJECT_FEATURES_MIN_MAX_COL, OBJECT_TABLE_AS_LIST);

        Set<Integer> ids = phenotypeData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "Phenotype-AllIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write phenotype file (with solution) " + dataName);

        phenotypeData.writeData(path, FileType.CSV, solution, true, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/phenotypes/out/all-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write with integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write phenotype file (with solution) " + dataName);

        phenotypeData.writeData(path, FileType.CSV, solution, false, true, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/phenotypes/out/all-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void toCsvFileWithSelectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        SimplePhenotypeData phenotypeData = new SimplePhenotypeData("Phenotype Data",
            OBJECT_FEATURES_MIN_MAX_COL, OBJECT_TABLE_AS_LIST);

        Set<Integer> ids = phenotypeData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "Phenotype-SelectedIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write phenotype file (with solution) " + dataName);

        phenotypeData.writeData(path, FileType.CSV, solution, true, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/phenotypes/out/sel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write with integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write phenotype file (with solution) " + dataName);

        phenotypeData.writeData(path, FileType.CSV, solution, false, true, false);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/phenotypes/out/sel-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
    @Test
    public void toCsvFileWithUnselectedIds() throws IOException {
        expectedHeaders = HEADERS_UNIQUE_NAMES;
        SimplePhenotypeData phenotypeData = new SimplePhenotypeData("Phenotype Data",
            OBJECT_FEATURES_MIN_MAX_COL, OBJECT_TABLE_AS_LIST);

        Set<Integer> ids = phenotypeData.getIDs();

        Path dirPath = Paths.get(TEST_OUTPUT);

        Files.createDirectories(dirPath);

        dirPath = Files.createTempDirectory(dirPath, "Phenotype-UnselectedIds");

        // create solution
        SubsetSolution solution = new SubsetSolution(ids);
        for(int sel : SELECTION){
            solution.select(sel);
        }
        
        Path path;
        
        // write with integer ids
        dataName = "with-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write phenotype file (with solution) " + dataName);

        phenotypeData.writeData(path, FileType.CSV, solution, true, false, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/phenotypes/out/unsel-with-ids.csv").getPath()),
                    path.toFile()));
        
        // write with integer ids
        dataName = "no-ids.csv";

        path = Paths.get(dirPath.toString(), dataName);

        System.out.println(" |- Write phenotype file (with solution) " + dataName);

        phenotypeData.writeData(path, FileType.CSV, solution, false, false, true);

        assertTrue("Output is not correct!",
            FileUtils
                .contentEquals(
                    new File(SimpleDistanceMatrixDataTest.class
                        .getResource("/phenotypes/out/unsel-no-ids.csv").getPath()),
                    path.toFile()));

    }
    
}
