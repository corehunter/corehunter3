
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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.corehunter.API;
import org.corehunter.CoreHunterMeasure;
import org.corehunter.CoreHunterObjectiveType;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleGenotypeData;
import org.corehunter.tests.data.simple.CoreHunterDataTest;
import org.junit.Test;

import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.feature.array.ArrayFeatureData;

public class APITest {

    private static final CoreHunterData GENOTYPES_DATA;
    private static final CoreHunterData PHENOTYPES_DATA;
    private static final CoreHunterData DISTANCES_DATA;

    private static final CoreHunterData GENOTYPES_AND_PHENOTYPES_DATA;
    private static final CoreHunterData GENOTYPES_AND_DISTANCES_DATA;
    private static final CoreHunterData PHENOTYPES_AND_DISTANCES_DATA;

    private static final CoreHunterData GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA;

    static {
        GenotypeData genotypes = new SimpleGenotypeData(HEADERS_UNIQUE_NAMES, MARKER_NAMES, ALLELE_NAMES,
                ALLELE_FREQUENCIES);
        GENOTYPES_DATA = new CoreHunterData(genotypes);
        FeatureData phenotypes = new ArrayFeatureData(NAME, PHENOTYPIC_TRAIT_FEATURES,
                PHENOTYPIC_TRAIT_VALUES_WITH_HEADERS);
        PHENOTYPES_DATA = new CoreHunterData(phenotypes);
        DistanceMatrixData distances = new SimpleDistanceMatrixData(HEADERS_UNIQUE_NAMES, DISTANCES);
        DISTANCES_DATA = new CoreHunterData(distances);

        GENOTYPES_AND_PHENOTYPES_DATA = new CoreHunterData(genotypes, phenotypes, null);

        GENOTYPES_AND_DISTANCES_DATA = new CoreHunterData(genotypes, null, distances);

        PHENOTYPES_AND_DISTANCES_DATA = new CoreHunterData(null, phenotypes, distances);

        GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA = new CoreHunterData(genotypes, phenotypes, distances);
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
            API.readGenotypeData(CoreHunterDataTest.class.getResource(MARKERS_UNIQUE_NAMES).getPath(), "DEFAULT");
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
        // TODO Not yet implemented
    }

    @Test
    public void testCreateDefaultObjective() {
        // TODO Not yet implemented
    }

    @Test
    public void testGetAllowedObjectives() {
        assertArrayEquals("Allowed objectives for genotypes data not correct", new CoreHunterObjectiveType[] {
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY,
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY, CoreHunterObjectiveType.COVERAGE,
                CoreHunterObjectiveType.HETEROZYGOUS_LOCI, CoreHunterObjectiveType.SHANNON_DIVERSITY
        }, API.getAllowedObjectives(GENOTYPES_DATA).toArray());

        assertArrayEquals("Allowed objectives for phenotypes data not correct", new CoreHunterObjectiveType[] {
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY,
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY
        }, API.getAllowedObjectives(PHENOTYPES_DATA).toArray());

        assertArrayEquals("Allowed objectives for distance data not correct", new CoreHunterObjectiveType[] {
                CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY,
                CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY
        }, API.getAllowedObjectives(DISTANCES_DATA).toArray());

        assertArrayEquals("Allowed objectives for genotypes and phenotypes data not correct",
                new CoreHunterObjectiveType[] {
                        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                        CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                        CoreHunterObjectiveType.COVERAGE, CoreHunterObjectiveType.HETEROZYGOUS_LOCI,
                        CoreHunterObjectiveType.SHANNON_DIVERSITY
                }, API.getAllowedObjectives(GENOTYPES_AND_PHENOTYPES_DATA).toArray());

        assertArrayEquals("Allowed objectives for genotypes and distance data not correct",
                new CoreHunterObjectiveType[] {
                        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                        CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                        CoreHunterObjectiveType.COVERAGE, CoreHunterObjectiveType.HETEROZYGOUS_LOCI,
                        CoreHunterObjectiveType.SHANNON_DIVERSITY
                }, API.getAllowedObjectives(GENOTYPES_AND_DISTANCES_DATA).toArray());

        assertArrayEquals("Allowed objectives for phenotypes and distance data not correct",
                new CoreHunterObjectiveType[] {
                        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                        CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY
                }, API.getAllowedObjectives(PHENOTYPES_AND_DISTANCES_DATA).toArray());

        assertArrayEquals("Allowed objectives for genotypes, phenotypes and distance data not correct",
                new CoreHunterObjectiveType[] {
                        CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY,
                        CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY,
                        CoreHunterObjectiveType.COVERAGE, CoreHunterObjectiveType.HETEROZYGOUS_LOCI,
                        CoreHunterObjectiveType.SHANNON_DIVERSITY
                }, API.getAllowedObjectives(GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA).toArray());
    }

    @Test
    public void testGetAllowedMeasuresCoreHunterDataCoreHunterObjectiveType() {

        // GENOTYPES_DATA

        assertArrayEquals(
                "Allowed objectives for genotypes data and average accession entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
                }, API.getAllowedMeasures(GENOTYPES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and average entry to entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
                }, API.getAllowedMeasures(GENOTYPES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
                }, API.getAllowedMeasures(GENOTYPES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and coverage to not correct", new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_DATA, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for genotypes data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_DATA, CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for genotypes data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_DATA, CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // PHENOTYPES_DATA

        assertArrayEquals("Allowed measures for phenotypes data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(PHENOTYPES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(PHENOTYPES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(PHENOTYPES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and coverage to not correct", new CoreHunterMeasure[0],
                API.getAllowedMeasures(PHENOTYPES_DATA, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(PHENOTYPES_DATA, CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(PHENOTYPES_DATA, CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // DISTANCES_DATA

        assertArrayEquals("Allowed measures for distance data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(DISTANCES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(DISTANCES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and average nearest entry to entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(DISTANCES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and coverage to not correct", new CoreHunterMeasure[0],
                API.getAllowedMeasures(DISTANCES_DATA, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for distance data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(DISTANCES_DATA, CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for distance data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(DISTANCES_DATA, CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // GENOTYPES_AND_PHENOTYPES_DATA

        assertArrayEquals(
                "Allowed measures for genotypes and phenotypes data and average accession to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(GENOTYPES_AND_PHENOTYPES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and average entry to entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(GENOTYPES_AND_PHENOTYPES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for genotypes and phenotypes data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(GENOTYPES_AND_PHENOTYPES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and coverage to not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_AND_PHENOTYPES_DATA, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_AND_PHENOTYPES_DATA, CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                        .toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_AND_PHENOTYPES_DATA, CoreHunterObjectiveType.SHANNON_DIVERSITY)
                        .toArray());

        // GENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
                "Allowed measures for phenotypes and distances data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(GENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for phenotypes and distances data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(GENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for phenotypes and distances data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(GENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distances data and coverage to not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distances data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                        .toArray());

        assertArrayEquals("Allowed measures for phenotypes and distances data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.SHANNON_DIVERSITY)
                        .toArray());

        // PHENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
                "Allowed measures for phenotypes and distance data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allow measures for phenotypes and distance data and average entry to entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for phenotypes and distance data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distance data and coverage to not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distance data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                        .toArray());

        assertArrayEquals("Allowed measures for phenotypes and distance data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.SHANNON_DIVERSITY)
                        .toArray());

        // GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes, phenotypes and distance data and coverage to not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA, CoreHunterObjectiveType.COVERAGE)
                        .toArray());

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and hetrozygous loci not correct",
                new CoreHunterMeasure[0], API.getAllowedMeasures(GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA,
                        CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and shannon diversity not correct",
                new CoreHunterMeasure[0], API.getAllowedMeasures(GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA,
                        CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());
    }

    @Test
    public void testGetAllowedMeasuresBooleanBooleanBooleanCoreHunterObjectiveType() {
        // GENOTYPES_DATA

        assertArrayEquals(
                "Allowed objectives for genotypes data and average accession entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
                }, API.getAllowedMeasures(true, false, false, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and average entry to entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
                }, API.getAllowedMeasures(true, false, false, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS
                }, API.getAllowedMeasures(true, false, false, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes data and coverage to not correct", new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, false, false, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for genotypes data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, false, false, CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for genotypes data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, false, false, CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // PHENOTYPES_DATA

        assertArrayEquals("Allowed measures for phenotypes data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(false, true, false, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(false, true, false, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(false, true, false,  CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and coverage to not correct", new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, true, false, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, true, false, CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for phenotypes data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, true, false, CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // DISTANCES_DATA

        assertArrayEquals("Allowed measures for distance data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(false, false, true, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(false, false, true, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and average nearest entry to entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(false, false, true, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for distance data and coverage to not correct", new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, false, true, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for distance data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, false, true, CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals("Allowed measures for distance data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, false, true, CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());

        // GENOTYPES_AND_PHENOTYPES_DATA

        assertArrayEquals(
                "Allowed measures for genotypes and phenotypes data and average accession to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(true, true, false, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and average entry to entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(true, true, false, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for genotypes and phenotypes data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS
                }, API.getAllowedMeasures(true, true, false, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and coverage to not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, true, false, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, true, false, CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                        .toArray());

        assertArrayEquals("Allowed measures for genotypes and phenotypes data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, true, false, CoreHunterObjectiveType.SHANNON_DIVERSITY)
                        .toArray());

        // GENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
                "Allowed measures for phenotypes and distances data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(true, false, true, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for phenotypes and distances data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(true, false, true, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for phenotypes and distances data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(true, false, true, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distances data and coverage to not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, false, true, CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distances data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, false, true, CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                        .toArray());

        assertArrayEquals("Allowed measures for phenotypes and distances data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, false, true, CoreHunterObjectiveType.SHANNON_DIVERSITY)
                        .toArray());

        // PHENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
                "Allowed measures for phenotypes and distance data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(false, true, true,  CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allow measures for phenotypes and distance data and average entry to entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(false, true, true,  CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for phenotypes and distance data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(false, true, true,  CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distance data and coverage to not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, true, true,  CoreHunterObjectiveType.COVERAGE).toArray());

        assertArrayEquals("Allowed measures for phenotypes and distance data and hetrozygous loci not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, true, true,  CoreHunterObjectiveType.HETEROZYGOUS_LOCI)
                        .toArray());

        assertArrayEquals("Allowed measures for phenotypes and distance data and shannon diversity not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(false, true, true, CoreHunterObjectiveType.SHANNON_DIVERSITY)
                        .toArray());

        // GENOTYPES_PHENOTYPES_AND_DISTANCES_DATA

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and average accession to nearest entry to not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(true, true, true, CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(true, true, true, CoreHunterObjectiveType.AV_ENTRY_TO_ENTRY).toArray());

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and average entry to nearest entry not correct",
                new CoreHunterMeasure[] {
                        CoreHunterMeasure.MODIFIED_ROGERS, CoreHunterMeasure.CAVALLI_SFORZA_EDWARDS,
                        CoreHunterMeasure.GOWERS, CoreHunterMeasure.PRECOMPUTED_DISTANCE
                }, API.getAllowedMeasures(true, true, true, CoreHunterObjectiveType.AV_ENTRY_TO_NEAREST_ENTRY).toArray());

        assertArrayEquals("Allowed measures for genotypes, phenotypes and distance data and coverage to not correct",
                new CoreHunterMeasure[0],
                API.getAllowedMeasures(true, true, true, CoreHunterObjectiveType.COVERAGE)
                        .toArray());

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and hetrozygous loci not correct",
                new CoreHunterMeasure[0], API.getAllowedMeasures(true, true, true,
                        CoreHunterObjectiveType.HETEROZYGOUS_LOCI).toArray());

        assertArrayEquals(
                "Allowed measures for genotypes, phenotypes and distance data and shannon diversity not correct",
                new CoreHunterMeasure[0], API.getAllowedMeasures(true, true, true,
                        CoreHunterObjectiveType.SHANNON_DIVERSITY).toArray());
    }

}
