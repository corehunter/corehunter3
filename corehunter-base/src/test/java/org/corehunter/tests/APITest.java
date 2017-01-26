
package org.corehunter.tests;

import static org.corehunter.tests.TestData.ALLELE_FREQUENCIES;
import static org.corehunter.tests.TestData.ALLELE_NAMES;
import static org.corehunter.tests.TestData.DISTANCES;
import static org.corehunter.tests.TestData.HEADERS_UNIQUE_NAMES;
import static org.corehunter.tests.TestData.MARKER_NAMES;
import static org.corehunter.tests.TestData.NAME;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_FEATURES;
import static org.corehunter.tests.TestData.PHENOTYPIC_TRAIT_VALUES_WITH_HEADERS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.corehunter.API;
import org.corehunter.CoreHunterMeasure;
import org.corehunter.CoreHunterObjective;
import org.corehunter.CoreHunterObjectiveType;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeData;
import org.corehunter.data.PhenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleGenotypeData;
import org.corehunter.data.simple.SimplePhenotypeData;
import org.corehunter.tests.data.simple.CoreHunterDataTest;
import org.junit.Test;

public class APITest {

    private static final CoreHunterData GENO_DATA;
    private static final CoreHunterData PHENOTYPES_DATA;
    private static final CoreHunterData DISTANCES_DATA;

    private static final CoreHunterData GENO_AND_PHENOTYPES_DATA;
    private static final CoreHunterData GENO_AND_DISTANCES_DATA;
    private static final CoreHunterData PHENOTYPES_AND_DISTANCES_DATA;

    private static final CoreHunterData GENO_PHENOTYPES_AND_DISTANCES_DATA;

    private static final CoreHunterObjective GENO_AV_ACC_TO_NEAR_MODIFIED_ROGERS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterMeasure.MODIFIED_ROGERS, 1.0);
    private static final CoreHunterObjective GENO_AV_ACC_TO_NEAR_CAVALLI_SFORZA_EDWARDS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS, 1.0);
    private static final CoreHunterObjective GENO_AV_ENTRY_TO_ENTRY_MODIFIED_ROGERS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterMeasure.MODIFIED_ROGERS, 1.0);
    private static final CoreHunterObjective GENO_AV_ENTRY_TO_ENTRY_CAVALLI_SFORZA_EDWARDS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS, 1.0);
    private static final CoreHunterObjective GENO_AV_ENTRY_TO_NEAR_MODIFIED_ROGERS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.MODIFIED_ROGERS, 1.0);
    private static final CoreHunterObjective GENO_AV_ENTRY_TO_NEAR_CAVALLI_SFORZA_EDWARDS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS, 1.0);
    private static final CoreHunterObjective PHENOTYPES_AV_ACC_TO_NEAR_GOWERS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS, 1.0);
    private static final CoreHunterObjective PHENOTYPES_AV_ENTRY_TO_ENTRY_GOWERS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterMeasure.GOWERS, 1.0);
    private static final CoreHunterObjective PHENOTYPES_AV_ENTRY_TO_NEAR_GOWERS = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.GOWERS, 1.0);
    private static final CoreHunterObjective DISTANCES_AV_ACC_TO_NEAR_PRE_DISTANCE = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterMeasure.PRECOMPUTED_DISTANCE, 1.0);
    private static final CoreHunterObjective DISTANCES_AV_ENTRY_TO_ENTRY_PRE_DISTANCE = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterMeasure.PRECOMPUTED_DISTANCE, 1.0);
    private static final CoreHunterObjective DISTANCES_AV_ENTRY_TO_NEAR_PRE_DISTANCE = new CoreHunterObjective(
        CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterMeasure.PRECOMPUTED_DISTANCE, 1.0);

    static {
        GenotypeData genotypes = new SimpleGenotypeData(HEADERS_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES,
            ALLELE_FREQUENCIES);
        GENO_DATA = new CoreHunterData(genotypes);
        PhenotypeData phenotypes = new SimplePhenotypeData(NAME, PHENOTYPIC_TRAIT_FEATURES,
            PHENOTYPIC_TRAIT_VALUES_WITH_HEADERS);
        PHENOTYPES_DATA = new CoreHunterData(phenotypes);
        DistanceMatrixData distances = new SimpleDistanceMatrixData(HEADERS_UNIQUE_NAMES, DISTANCES);
        DISTANCES_DATA = new CoreHunterData(distances);

        GENO_AND_PHENOTYPES_DATA = new CoreHunterData(genotypes, phenotypes, null);

        GENO_AND_DISTANCES_DATA = new CoreHunterData(genotypes, null, distances);

        PHENOTYPES_AND_DISTANCES_DATA = new CoreHunterData(null, phenotypes, distances);

        GENO_PHENOTYPES_AND_DISTANCES_DATA = new CoreHunterData(genotypes, phenotypes, distances);
    }

    private static final String DISTANCES_SMALL = "/distances/small-ids.txt";

    private static final String PHENOTYPES_UNIQUE_NAMES = "/phenotypes/ids.csv";

    private static final String MARKERS_UNIQUE_NAMES = "/biallelic_genotypes/ids.csv";

    @Test
    public void testGetIds() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetNames() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetIdsFromIndices() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetIndicesFromIds() {
        // TODO Not yet implemented
    }

    @Test
    public void testReadDistanceMatrixData() {
        try {
            API.readDistanceMatrixData(CoreHunterDataTest.class.getResource(DISTANCES_SMALL).getPath());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateDistanceMatrixData() {
        // TODO Not yet implemented
    }

    @Test
    public void testReadGenotypeData() {
        try {
            API.readGenotypeData(CoreHunterDataTest.class.getResource(MARKERS_UNIQUE_NAMES).getPath(),
                "DEFAULT");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        // TODO other formats
    }

    @Test
    public void testCreateDefaultGenotypeData() {
        // TODO Not yet implemented
    }

    @Test
    public void testCreateBiparentalGenotypeData() {
        // TODO Not yet implemented
    }

    @Test
    public void testCreateFrequencyGenotypeData() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetAlleles() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetMarkerNames() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetAlleleFrequencies() {
        // TODO Not yet implemented
    }

    @Test
    public void testReadPhenotypeData() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetRanges() {
        // TODO Not yet implemented
    }

    @Test
    public void testCreateObjectiveStringStringDouble() {
        // TODO Not yet implemented
    }

    @Test
    public void testCreateObjectiveStringStringDoubleDoubleDouble() {
        // TODO Not yet implemented
    }

    @Test
    public void testCreateArguments() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetNormalizationRanges() {
        // TODO Not yet implemented
    }

    @Test
    public void testSampleCore() {
        // TODO Not yet implemented
    }

    @Test
    public void testEvaluateCore() {
        // TODO Not yet implemented
    }

    @Test
    public void testCreateDefaultObjectives() {
        assertArrayEquals("Default objectives for genotypes data not correct", new CoreHunterObjective[] {
            GENO_AV_ENTRY_TO_NEAR_MODIFIED_ROGERS
        }, API.createDefaultObjectives(GENO_DATA).toArray());

        assertArrayEquals("Default objectives for phenotypes data not correct", new CoreHunterObjective[] {
            PHENOTYPES_AV_ENTRY_TO_NEAR_GOWERS
        }, API.createDefaultObjectives(PHENOTYPES_DATA).toArray());

        assertArrayEquals("Default objectives for distance data not correct", new CoreHunterObjective[] {
            DISTANCES_AV_ENTRY_TO_NEAR_PRE_DISTANCE
        }, API.createDefaultObjectives(DISTANCES_DATA).toArray());

        assertArrayEquals("Default objectives for genotypes and phenotypes data not correct",
            new CoreHunterObjective[] {
                new CoreHunterObjective(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                    CoreHunterMeasure.MODIFIED_ROGERS, 0.5),
                new CoreHunterObjective(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                    CoreHunterMeasure.GOWERS, 0.5)
            }, API.createDefaultObjectives(GENO_AND_PHENOTYPES_DATA).toArray());

        assertArrayEquals("Default objectives for phenotypes and distance data not correct",
            new CoreHunterObjective[] {
                new CoreHunterObjective(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                    CoreHunterMeasure.GOWERS, 0.5),
                new CoreHunterObjective(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                    CoreHunterMeasure.PRECOMPUTED_DISTANCE, 0.5)
            }, API.createDefaultObjectives(PHENOTYPES_AND_DISTANCES_DATA).toArray());

        assertArrayEquals("Default objectives for genotypes, phenotypes and distance data not correct",
            new CoreHunterObjective[] {
                new CoreHunterObjective(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                    CoreHunterMeasure.MODIFIED_ROGERS, 1.0 / 3.0),
                new CoreHunterObjective(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                    CoreHunterMeasure.GOWERS, 1.0 / 3.0),
                new CoreHunterObjective(CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                    CoreHunterMeasure.PRECOMPUTED_DISTANCE, 1.0 / 3.0)
            }, API.createDefaultObjectives(GENO_PHENOTYPES_AND_DISTANCES_DATA).toArray());
    }

    @Test
    public void testCreateDefaultObjectiveCoreHunterData() {
        assertEquals("Default objective for genotypes data not correct",
            GENO_AV_ENTRY_TO_NEAR_MODIFIED_ROGERS, API.createDefaultObjective(GENO_DATA));

        assertEquals("Default objective for phenotypes data not correct", PHENOTYPES_AV_ENTRY_TO_NEAR_GOWERS,
            API.createDefaultObjective(PHENOTYPES_DATA));

        assertEquals("Default objective for distance data not correct",
            DISTANCES_AV_ENTRY_TO_NEAR_PRE_DISTANCE, API.createDefaultObjective(DISTANCES_DATA));

        assertEquals("Default objective for genotypes and phenotypes data not correct",
            GENO_AV_ENTRY_TO_NEAR_MODIFIED_ROGERS, API.createDefaultObjective(GENO_AND_PHENOTYPES_DATA));

        assertEquals("Default objective for phenotypes and distance data not correct",
            PHENOTYPES_AV_ENTRY_TO_NEAR_GOWERS, API.createDefaultObjective(PHENOTYPES_AND_DISTANCES_DATA));

        assertEquals("Default objective for genotypes, phenotypes and distance data not correct",
            GENO_AV_ENTRY_TO_NEAR_MODIFIED_ROGERS,
            API.createDefaultObjective(GENO_PHENOTYPES_AND_DISTANCES_DATA));
    }

    @Test
    public void testCreateDefaultObjectiveCoreHunterDataListCoreHunterObjective() {
        testCreateDefaultObjectiveCoreHunterDataListCoreHunterObjective(GENO_DATA);
        testCreateDefaultObjectiveCoreHunterDataListCoreHunterObjective(PHENOTYPES_DATA);
        testCreateDefaultObjectiveCoreHunterDataListCoreHunterObjective(DISTANCES_DATA);
        testCreateDefaultObjectiveCoreHunterDataListCoreHunterObjective(GENO_AND_PHENOTYPES_DATA);
        testCreateDefaultObjectiveCoreHunterDataListCoreHunterObjective(PHENOTYPES_AND_DISTANCES_DATA);
        testCreateDefaultObjectiveCoreHunterDataListCoreHunterObjective(GENO_PHENOTYPES_AND_DISTANCES_DATA);
    }

    private void testCreateDefaultObjectiveCoreHunterDataListCoreHunterObjective(
        CoreHunterData coreHunterData) {
        List<CoreHunterObjective> allObjectives = API.getAllAllowedObjectives(coreHunterData);

        List<CoreHunterObjective> usedObjectives = new LinkedList<CoreHunterObjective>();

        CoreHunterObjective objective;

        while (!allObjectives.isEmpty()) {
            objective = allObjectives.get(0);
            assertEquals(objective, API.createDefaultObjective(coreHunterData, usedObjectives));
            usedObjectives.add(objective);
            allObjectives.remove(objective);
        }
    }

    @Test
    public void testGetAllAllowedObjectives() {
        assertArrayEquals("Allowed objectives for genotypes data not correct", new CoreHunterObjective[] {
            GENO_AV_ACC_TO_NEAR_MODIFIED_ROGERS, GENO_AV_ACC_TO_NEAR_CAVALLI_SFORZA_EDWARDS,
            GENO_AV_ENTRY_TO_ENTRY_MODIFIED_ROGERS, GENO_AV_ENTRY_TO_ENTRY_CAVALLI_SFORZA_EDWARDS,
            GENO_AV_ENTRY_TO_NEAR_MODIFIED_ROGERS, GENO_AV_ENTRY_TO_NEAR_CAVALLI_SFORZA_EDWARDS,
            PHENOTYPES_AV_ACC_TO_NEAR_GOWERS, PHENOTYPES_AV_ENTRY_TO_ENTRY_GOWERS,
            PHENOTYPES_AV_ENTRY_TO_NEAR_GOWERS, DISTANCES_AV_ACC_TO_NEAR_PRE_DISTANCE,
            DISTANCES_AV_ENTRY_TO_ENTRY_PRE_DISTANCE, DISTANCES_AV_ENTRY_TO_NEAR_PRE_DISTANCE
        }, API.getAllAllowedObjectives(GENO_PHENOTYPES_AND_DISTANCES_DATA).toArray());
    }

    @Test
    public void testGetAllowedObjectiveTypes() {
        assertArrayEquals("Allowed objectives for genotypes data not correct", new CoreHunterObjectiveType[] {
            CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY,
            CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterObjectiveType.COVERAGE,
            CoreHunterObjectiveType.HETEROZYGOUS_LOCI, CoreHunterObjectiveType.SHANNON_DIVERSITY
        }, API.getAllowedObjectiveTypes(GENO_DATA).toArray());

        assertArrayEquals("Allowed objectives for phenotypes data not correct",
            new CoreHunterObjectiveType[] {
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY
            }, API.getAllowedObjectiveTypes(PHENOTYPES_DATA).toArray());

        assertArrayEquals("Allowed objectives for distance data not correct", new CoreHunterObjectiveType[] {
            CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY,
            CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY
        }, API.getAllowedObjectiveTypes(DISTANCES_DATA).toArray());

        assertArrayEquals("Allowed objectives for genotypes and phenotypes data not correct",
            new CoreHunterObjectiveType[] {
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                CoreHunterObjectiveType.COVERAGE, CoreHunterObjectiveType.HETEROZYGOUS_LOCI,
                CoreHunterObjectiveType.SHANNON_DIVERSITY
            }, API.getAllowedObjectiveTypes(GENO_AND_PHENOTYPES_DATA).toArray());

        assertArrayEquals("Allowed objectives for genotypes and distance data not correct",
            new CoreHunterObjectiveType[] {
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                CoreHunterObjectiveType.COVERAGE, CoreHunterObjectiveType.HETEROZYGOUS_LOCI,
                CoreHunterObjectiveType.SHANNON_DIVERSITY
            }, API.getAllowedObjectiveTypes(GENO_AND_DISTANCES_DATA).toArray());

        assertArrayEquals("Allowed objectives for phenotypes and distance data not correct",
            new CoreHunterObjectiveType[] {
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY
            }, API.getAllowedObjectiveTypes(PHENOTYPES_AND_DISTANCES_DATA).toArray());

        assertArrayEquals("Allowed objectives for genotypes, phenotypes and distance data not correct",
            new CoreHunterObjectiveType[] {
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                CoreHunterObjectiveType.COVERAGE, CoreHunterObjectiveType.HETEROZYGOUS_LOCI,
                CoreHunterObjectiveType.SHANNON_DIVERSITY
            }, API.getAllowedObjectiveTypes(GENO_PHENOTYPES_AND_DISTANCES_DATA).toArray());
    }

    @Test
    public void testGetAllowedMeasuresCoreHunterDataCoreHunterObjectiveType() {

        // GENO_DATA

        assertArrayEquals(
            "Allowed objectives for genotypes data and av. accession entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
            }, API.getAllowedMeasures(GENO_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and av. entry to entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
            }, API.getAllowedMeasures(GENO_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
            }, API.getAllowedMeasures(GENO_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_DATA, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for genotypes data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_DATA, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for genotypes data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_DATA, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // PHENOTYPES_DATA

        assertArrayEquals(
            "Allowed measures for phenotypes data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(PHENOTYPES_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(PHENOTYPES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(PHENOTYPES_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(PHENOTYPES_DATA, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(PHENOTYPES_DATA, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(PHENOTYPES_DATA, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // DISTANCES_DATA

        assertArrayEquals("Allowed measures for distance data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and av. nearest entry to entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(DISTANCES_DATA, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for distance data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(DISTANCES_DATA, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for distance data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(DISTANCES_DATA, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // GENO_AND_PHENOTYPES_DATA

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(GENO_AND_PHENOTYPES_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and av. entry to entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(GENO_AND_PHENOTYPES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(GENO_AND_PHENOTYPES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_AND_PHENOTYPES_DATA, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_AND_PHENOTYPES_DATA, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                .toArray());

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_AND_PHENOTYPES_DATA, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY)
                .toArray());

        // GENO_AND_DISTANCES_DATA

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and av acc to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(GENO_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(GENO_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(GENO_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distances data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                .toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY)
                .toArray());

        // PHENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
            "Allowed measures for phenotypes and distance data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allow measures for phenotypes and distance data and av. entry to entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distance data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distance data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.COVERAGE)
                .toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distance data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                .toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distance data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY)
                .toArray());

        // GENO_PHENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and av. acc. to near entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(GENO_PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and av. entry to near entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(GENO_PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and av. entry to near entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(GENO_PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(GENO_PHENOTYPES_AND_DISTANCES_DATA, 
                CoreHunterObjectiveType.COVERAGE)
                .toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and hetrozygous loci not correct",
            new CoreHunterMeasure[0], API.getAllowedMeasures(GENO_PHENOTYPES_AND_DISTANCES_DATA,
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and shannon diversity not correct",
            new CoreHunterMeasure[0], API.getAllowedMeasures(GENO_PHENOTYPES_AND_DISTANCES_DATA,
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());
    }

    @Test
    public void testGetAllowedMeasuresBooleanBooleanBooleanCoreHunterObjectiveType() {
        // GENO_DATA

        assertArrayEquals(
            "Allowed objectives for genotypes data and av. accession entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
            }, API.getAllowedMeasures(true, false, false, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and av. entry to entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
            }, API.getAllowedMeasures(true, false, false, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
            }, API.getAllowedMeasures(true, false, false, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, false, false, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for genotypes data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, false, false, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for genotypes data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, false, false, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // PHENOTYPES_DATA

        assertArrayEquals(
            "Allowed measures for phenotypes data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(false, true, false, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(false, true, false, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(false, true, false, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, true, false, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, true, false, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, true, false, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // DISTANCES_DATA

        assertArrayEquals("Allowed measures for distance data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(false, false, true, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(false, false, true, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and av. nearest entry to entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(false, false, true, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, false, true, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for distance data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, false, true, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for distance data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, false, true, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // GENO_AND_PHENOTYPES_DATA

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(true, true, false, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and av. entry to entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(true, true, false, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS
            }, API.getAllowedMeasures(true, true, false, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, true, false, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, true, false, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes and phenotypes data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, true, false, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // GENO_AND_DISTANCES_DATA

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(true, false, true, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(true, false, true, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(true, false, true, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distances data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, false, true, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, false, true, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distances data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, false, true, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // PHENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
            "Allowed measures for phenotypes and distance data and av. accession to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(false, true, true, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allow measures for phenotypes and distance data and av. entry to entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(false, true, true, 
                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distance data and av. entry to nearest entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(false, true, true, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distance data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, true, true, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distance data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, true, true, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals(
            "Allowed measures for phenotypes and distance data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(false, true, true, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // GENO_PHENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and av. acc. to near entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(true, true, true, 
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and av. entry to near entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(true, true, true,

                CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and av. entry to near entry not correct",
            new CoreHunterMeasure[] {
                CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
            }, API.getAllowedMeasures(true, true, true, 
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and coverage not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, true, true, 
                CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and hetrozygous loci not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, true, true, 
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals(
            "Allowed measures for genotypes, phenotypes and distance data and shannon diversity not correct",
            new CoreHunterMeasure[0],
            API.getAllowedMeasures(true, true, true, 
                CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());
    }

}
