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

package org.corehunter.tests;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;

import uno.informatics.data.DataType;
import uno.informatics.data.Feature;
import uno.informatics.data.ScaleType;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.pojo.SimpleEntityPojo;
import uno.informatics.data.pojo.SimpleFeaturePojo;

/**
 * @author Guy Davenport, Herman De Beukelaer
 */
public class TestData {

    public static final double PRECISION = 1e-10;

    public static final double[][] DISTANCES = new double[][]{
        new double[]{0.0, 0.2, 0.4, 0.6, 0.8},
        new double[]{0.2, 0.0, 0.2, 0.4, 0.6},
        new double[]{0.4, 0.2, 0.0, 0.1, 0.4},
        new double[]{0.6, 0.4, 0.1, 0.0, 0.2},
        new double[]{0.8, 0.6, 0.4, 0.2, 0.0}};

    public static final String NAME = "Test Dataset";

    public static final String[] UNIQUE_NAMES = new String[]{
        "Alice", "Dave", "Bob", "Carol", "Eve"
    };
    
    public static final String[] NON_UNIQUE_NAMES = new String[]{
        "Alice", "Unknown", "Bob", "Bob", "Car\"ol"
    };
    
    public static final String[] UNIQUE_IDENTIFIERS = new String[]{
        "Alice", "Unknown", "Bob-1", "Bob'-2", "Carol"
    };
    
    public static final SimpleEntity[] HEADERS_UNIQUE_NAMES;
    public static final SimpleEntity[] HEADERS_NON_UNIQUE_NAMES;
    
    static{
        HEADERS_UNIQUE_NAMES = new SimpleEntity[NON_UNIQUE_NAMES.length];
        for(int i = 0; i < HEADERS_UNIQUE_NAMES.length; i++){
            HEADERS_UNIQUE_NAMES[i] = new SimpleEntityPojo(UNIQUE_NAMES[i]);
        }
        HEADERS_NON_UNIQUE_NAMES = new SimpleEntity[NON_UNIQUE_NAMES.length];
        for(int i = 0; i < HEADERS_NON_UNIQUE_NAMES.length; i++){
            HEADERS_NON_UNIQUE_NAMES[i] = new SimpleEntityPojo(UNIQUE_IDENTIFIERS[i], NON_UNIQUE_NAMES[i]);
        }
    }

    public static final Set<Integer> SET = new TreeSet<>();

    static {
        SET.add(0);
        SET.add(1);
        SET.add(2);
        SET.add(3);
        SET.add(4);
    }

    public static final String[] MARKER_NAMES = {
        "mk1",
        "mk2",
        "mk3",
        "mk4",
        "mk5",
        "mk6",
        "mk7"
    };
    public static final String[] UNDEFINED_MARKER_NAMES = {
        null,
        null,
        null,
        null,
        null,
        null,
        null
    };
    
    public static final String[] MARKER_NAMES_DEFAULT = {
        "mk1",
        "mk2",
        "mk3",
        "mk4"
    };
    
    public static final Double[][][] ALLELE_FREQUENCIES = {
        {
            {null, null, null},
            {0.5, 0.5},
            {0.0, 0.5, 0.5},
            {0.0, 0.0, 0.5, 0.5},
            {null, null, null},
            {0.0, 1.0},
            {1.0, 0.0}
        },
        {
            {1.0, 0.0, 0.0},
            {0.5, 0.5},
            {0.0, 0.5, 0.5},
            {1.0, 0.0, 0.0, 0.0},
            {0.3333333333, 0.3333333333, 0.3333333333},
            {1.0, 0.0},
            {0.0, 1.0}
        },
        {
            {0.6, 0.0, 0.4},
            {0.5, 0.5},
            {0.0, 0.5, 0.5},
            {0.25, 0.25, 0.25, 0.25},
            {0.0, 0.5, 0.5},
            {0.0, 1.0},
            {1.0, 0.0}
        },
        {
            {null, null, null},
            {1.0, 0.0},
            {null, null, null},
            {0.0, 0.0, 1.0, 0.0},
            {0.3333333333, 0.3333333333, 0.3333333333},
            {0.0, 1.0},
            {1.0, 0.0}
        },
        {
            {0.3333333333, 0.3333333333, 0.3333333333},
            {0.5, 0.5},
            {0.0, 0.5, 0.5},
            {0.5, 0.0, 0.5, 0.0},
            {0.3333333333, 0.3333333333, 0.3333333333},
            {1.0, 0.0},
            {null, null}
        }
    };
    
    public static final Double[][][] ALLELE_FREQUENCIES_DIPLOID = {
        {
            {0.5, 0.0, 0.5},
            {0.0, 1.0, 0.0, 0.0},
            {1.0, 0.0},
            {null, null}
        },
        {
            {0.0, 1.0, 0.0},
            {0.5, 0.0, 0.5, 0.0},
            {0.5, 0.5},
            {0.5, 0.5}
        },
        {
            {0.5, 0.5, 0.0},
            {0.0, 0.0, 0.0, 1.0},
            {0.0, 1.0},
            {1.0, 0.0}
        },
        {
            {0.0, 0.5, 0.5},
            {0.0, 1.0, 0.0, 0.0},
            {0.5, 0.5},
            {0.5, 0.5}
        },
        {
            {1.0, 0.0, 0.0},
            {null, null, null, null},
            {1.0, 0.0},
            {0.0, 1.0}
        }
    };
    
    public static final Double[][][] ALLELE_FREQUENCIES_HOMOZYGOUS = {
        {
            {1.0, 0.0},
            {1.0, 0.0, 0.0},
            {1.0, 0.0},
            {null, null}
        },
        {
            {0.0, 1.0},
            {0.0, 1.0, 0.0},
            {1.0, 0.0},
            {1.0, 0.0}
        },
        {
            {1.0, 0.0},
            {0.0, 0.0, 1.0},
            {0.0, 1.0},
            {1.0, 0.0}
        },
        {
            {0.0, 1.0},
            {1.0, 0.0, 0.0},
            {0.0, 1.0},
            {1.0, 0.0}
        },
        {
            {1.0, 0.0},
            {null, null, null},
            {1.0, 0.0},
            {0.0, 1.0}
        }
    };

    public static final String[][] ALLELE_NAMES = {
        {"mk1-1", "mk1-2", "mk1-3"},
        {"mk2-1", "mk2-2"},
        {null, "mk3-2", null},
        {"mk4-1", "mk4-2", "mk4-3", "mk4-4"},
        {null, "mk5-2", "mk5-3"},
        {"mk6-1", "mk6-2"},
        {"mk7-1", "mk7-2"}
    };
    public static final String[][] UNDEFINED_ALLELE_NAMES = {
        {null, null, null},
        {null, null},
        {null, null, null},
        {null, null, null, null},
        {null, null, null},
        {null, null},
        {null, null}
    };
    
    public static final String[][] ALLELE_NAMES_DIPLOID = {
        {"1", "2", "3"},
        {"A", "B", "C", "D"},
        {"a1", "a2"},
        {"+", "-"}
    };
    
    public static final String[][] ALLELE_NAMES_HOMOZYGOUS = {
        {"1", "2"},
        {"B", "C", "D"},
        {"a1", "a2"},
        {"+", "-"}
    };
    
    public static final Integer[][] ALLELE_SCORES_BIALLELIC = {
        {1, 0, 2, 1, 1, 0, 0},
        {2, 0, 2, 0, 1, 2, 1},
        {1, 0, null, 0, 1, 1, 0},
        {1, 0, 1, 1, 1, 2, null},
        {1, 0, null, 0, null, 2, 0}
    };
    
    public static final Double[][][] ALLELE_FREQUENCIES_BIALLELIC = {
        {
            {0.5, 0.5},
            {1.0, 0.0},
            {0.0, 1.0},
            {0.5, 0.5},
            {0.5, 0.5},
            {1.0, 0.0},
            {1.0, 0.0}
        },
        {
            {0.0, 1.0},
            {1.0, 0.0},
            {0.0, 1.0},
            {1.0, 0.0},
            {0.5, 0.5},
            {0.0, 1.0},
            {0.5, 0.5}
        },
        {
            {0.5, 0.5},
            {1.0, 0.0},
            {null, null},
            {1.0, 0.0},
            {0.5, 0.5},
            {0.5, 0.5},
            {1.0, 0.0}
        },
        {
            {0.5, 0.5},
            {1.0, 0.0},
            {0.5, 0.5},
            {0.5, 0.5},
            {0.5, 0.5},
            {0.0, 1.0},
            {null, null}
        },
        {
            {0.5, 0.5},
            {1.0, 0.0},
            {null, null},
            {1.0, 0.0},
            {null, null},
            {0.0, 1.0},
            {1.0, 0.0}
        },
    };
    
    
    public static final String[] PHENOTYPIC_TRAIT_NAMES  = {
        "trait 1",
        "trait 2",
        "trait 3",
        "trait 4",
        "trait 5"
    };
    
    public static final Feature[] PHENOTYPIC_TRAIT_FEATURES = {
        new SimpleFeaturePojo("trait 1", DataType.STRING, ScaleType.NOMINAL),
        new SimpleFeaturePojo("trait 2", DataType.INTEGER, ScaleType.ORDINAL, Arrays.asList(0, 1, 2, 3)),
        new SimpleFeaturePojo("trait 3", DataType.INTEGER, ScaleType.INTERVAL, 1, 9),
        new SimpleFeaturePojo("trait 4", DataType.DOUBLE, ScaleType.RATIO, 0.5, 1.4),
        new SimpleFeaturePojo("trait 5", DataType.BOOLEAN, ScaleType.NOMINAL)        
    };
    
    public static final Object[][] PHENOTYPIC_TRAIT_VALUES  = {
        {"A", 3, 4, 1.4, false},
        {"B", 1, 5, 0.5, true},
        {"A", 0, 6, 0.5, true},
        {"C", 2, 9, 0.5, false},
        {"B", 2, 1, 1.3, true}
    };
    
    public static final Object[][] PHENOTYPIC_TRAIT_VALUES_WITH_HEADERS  = {
        {"Alice", "A", 3, 4, 1.4, false},
        {"Dave" , "B", 1, 5, 0.5, true},
        {"Bob"  , "A", 0, 6, 0.5, true},
        {"Carol", "C", 2, 9, 0.5, false},
        {"Eve"  , "B", 2, 1, 1.3, true}
    };
    
    public static final Object[][] PHENOTYPIC_TRAIT_MISSING_VALUES  = {
        {"A" , 3, 4, 1.4 , false},
        {"B" , 1, 5, 0.5 , null},
        {null, 0, 6, null, true},
        {"C" , 2, 9, null, false},
        {"B" , null    , 1, 1.3 , true}
    };
    
    public static final Object[][] PHENOTYPIC_TRAIT_INFERRED_BOUNDS = {
        {null, null},
        {null, null},
        {1, 9},
        {0.5, 1.4},
        {null, null}
    };
    public static final Object[][] PHENOTYPIC_TRAIT_EXPLICIT_BOUNDS = {
        {null, null},
        {null, null},
        {0, 10},
        {0.0, 2.0},
        {null, null}
    };
    
    // all genotypic evaluations are computed from the data in frequency format
    public static final double[][] MODIFIED_ROGERS_DISTANCES = {
        {0, 0.626783170528009, 0.133630620956212, 0.267261241912424, 0.422577127364258},
        {0.626783170528009, 0, 0.611594325552174, 0.681385143869247, 0.288675134594813},
        {0.133630620956212, 0.611594325552174, 0, 0.318104505140176, 0.431221962511629},
        {0.267261241912424, 0.681385143869247, 0.318104505140176, 0, 0.462910049886276},
        {0.422577127364258, 0.288675134594813, 0.431221962511629, 0.462910049886276, 0}
    };
    public static final double[][] MODIFIED_ROGERS_DISTANCES_CEIL_MISSING = {
        {0, 0.823754471047914, 0.550973165019340, 0.707106781186548, 0.779193722473980},
        {0.823754471047914, 0, 0.611594325552174, 0.866025403784439, 0.475594865605671},
        {0.550973165019340, 0.611594325552174, 0, 0.622016689410149, 0.573419151938199},
        {0.707106781186548, 0.866025403784439, 0.622016689410149, 0, 0.801783725737273},
        {0.779193722473980, 0.475594865605671, 0.573419151938199, 0.801783725737273, 0}
    };
    
    // computed from multi-allelic frequency data
    public static final double[][] CAVALLI_SFORZA_EDWARDS_DISTANCES = {
        {0, 0.654653670707977, 0.204552898786406, 0.289281483686467, 0.462910049886276},
        {0.654653670707977, 0, 0.644637965930246, 0.685866836181303, 0.319719285271990},
        {0.204552898786406, 0.644637965930246, 0, 0.373477220167007, 0.487564599487766},
        {0.289281483686467, 0.685866836181303, 0.373477220167007, 0, 0.475963149477968},
        {0.462910049886276, 0.319719285271990, 0.487564599487766, 0.475963149477968, 0}
    };
    public static final double[][] CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING = {
        {0, 0.845154254728517, 0.572325234561790, 0.715720060760681, 0.801783725737273},
        {0.845154254728517, 0, 0.644637965930246, 0.869555980191981, 0.495053092336545},
        {0.572325234561790, 0.644637965930246, 0, 0.652073247187738, 0.616908730308470},
        {0.715720060760681, 0.869555980191981, 0.652073247187738, 0, 0.809390108805646},
        {0.801783725737273, 0.495053092336545, 0.616908730308470, 0.809390108805646, 0}
    };
    
    public static final double[][] GOWER_DISTANCES = {
        {0, 0.758333333333333, 0.65, 0.739583333333333, 0.563888888888889},
        {0.758333333333333, 0, 0.291666666666667, 0.566666666666667, 0.344444444444444},
        {0.65, 0.291666666666667, 0, 0.608333333333333, 0.636111111111111},
        {0.739583333333333, 0.566666666666667, 0.608333333333333, 0, 0.777777777777778},
        {0.563888888888889, 0.344444444444444, 0.636111111111111, 0.777777777777778, 0}
    };
    public static final double[][] GOWER_DISTANCES_MISSING_VALUES_FLOOR = {
        {0, 0.558333333333333, 0.45, 0.4895833333333333, 0.497222222222222},
        {0.558333333333333, 0, 0.0916666666666667, 0.366666666666667, 0.277777777777778},
        {0.45, 0.0916666666666667, 0, 0.408333333333333, 0.125},
        {0.4895833333333333, 0.366666666666667, 0.408333333333333, 0, 0.6},
        {0.497222222222222, 0.277777777777778, 0.125, 0.6, 0}
    };
    public static final double[][] GOWER_DISTANCES_MISSING_VALUES_CEIL = {
        {0, 0.758333333333333, 0.85, 0.7395833333333333, 0.697222222222222},
        {0.758333333333333, 0, 0.691666666666667, 0.766666666666667, 0.677777777777778},
        {0.85, 0.691666666666667, 0, 0.808333333333333, 0.725},
        {0.7395833333333333, 0.766666666666667, 0.808333333333333, 0, 1.0},
        {0.697222222222222, 0.677777777777778, 0.725, 1.0, 0}
    };

    // subset with missing data
    public static final Set<Integer> SUBSET1 = new TreeSet<>();

    static {
        SUBSET1.add(2);
        SUBSET1.add(3);
    }
    
    // subset without missing data
    public static final Set<Integer> SUBSET2 = new TreeSet<>();

    static {
        SUBSET2.add(1);
        SUBSET2.add(2);
    }
    
    // larger subset (contains missing data)
    public static final Set<Integer> SUBSET3 = new TreeSet<>();

    static {
        SUBSET3.add(1);
        SUBSET3.add(2);
        SUBSET3.add(4);
    }
    
    // empty and full subset
    public static final Set<Integer> SUBSET_EMPTY = new TreeSet<>();
    public static final Set<Integer> SUBSET_FULL = new TreeSet<>();
    
    static {
        SUBSET_FULL.add(0);
        SUBSET_FULL.add(1);
        SUBSET_FULL.add(2);
        SUBSET_FULL.add(3);
        SUBSET_FULL.add(4);
    }
    
    // coverage
    public static final Evaluation COVERAGE_SUBSET1 = SimpleEvaluation.WITH_VALUE(0.7894736842105263);
    public static final Evaluation COVERAGE_SUBSET2 = SimpleEvaluation.WITH_VALUE(0.894736842105263);
    public static final Evaluation COVERAGE_SUBSET3 = SimpleEvaluation.WITH_VALUE(0.947368421052632);
    
    // shannon
    public static final Evaluation SHANNONS_SUBSET1 = SimpleEvaluation.WITH_VALUE(2.47831006598635);
    public static final Evaluation SHANNONS_SUBSET2 = SimpleEvaluation.WITH_VALUE(2.71372698498666);
    public static final Evaluation SHANNONS_SUBSET3 = SimpleEvaluation.WITH_VALUE(2.7566146463933);
    
    // heterozygous loci
    public static final Evaluation HETROZYGOUS_LOCI_SUBSET1 = SimpleEvaluation.WITH_VALUE(0.3225);
    public static final Evaluation HETROZYGOUS_LOCI_SUBSET2 = SimpleEvaluation.WITH_VALUE(0.501071428571429);
    public static final Evaluation HETROZYGOUS_LOCI_SUBSET3 = SimpleEvaluation.WITH_VALUE(0.518994708994709);

    // E-E distance
    public static final Evaluation ENTRY_TO_ENTRY_MODIFIED_ROGERS_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            MODIFIED_ROGERS_DISTANCES[2][3]
    );
    public static final Evaluation ENTRY_TO_ENTRY_CAVALLI_SFORZA_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            CAVALLI_SFORZA_EDWARDS_DISTANCES[2][3]
    );
    public static final Evaluation ENTRY_TO_ENTRY_GOWER_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            GOWER_DISTANCES[2][3]
    );
    
    public static final Evaluation ENTRY_TO_ENTRY_MODIFIED_ROGERS_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            MODIFIED_ROGERS_DISTANCES[1][2]
    );
    public static final Evaluation ENTRY_TO_ENTRY_CAVALLI_SFORZA_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            CAVALLI_SFORZA_EDWARDS_DISTANCES[1][2]
    );
    public static final Evaluation ENTRY_TO_ENTRY_GOWER_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            GOWER_DISTANCES[1][2]
    );
    
    public static final Evaluation ENTRY_TO_ENTRY_MODIFIED_ROGERS_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (MODIFIED_ROGERS_DISTANCES[1][2]
           + MODIFIED_ROGERS_DISTANCES[2][4]
           + MODIFIED_ROGERS_DISTANCES[1][4]) / 3.0
    );
    public static final Evaluation ENTRY_TO_ENTRY_CAVALLI_SFORZA_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (CAVALLI_SFORZA_EDWARDS_DISTANCES[1][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES[2][4]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES[1][4]) / 3.0
    );
    public static final Evaluation ENTRY_TO_ENTRY_GOWER_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (GOWER_DISTANCES[1][2]
           + GOWER_DISTANCES[2][4]
           + GOWER_DISTANCES[1][4]) / 3.0
    );
    
    // E-NE distance
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_MODIFIED_ROGERS_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            MODIFIED_ROGERS_DISTANCES[2][3]
    );
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_CAVALLI_SFORZA_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            CAVALLI_SFORZA_EDWARDS_DISTANCES[2][3]
    );
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_GOWER_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            GOWER_DISTANCES[2][3]
    );
    
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_MODIFIED_ROGERS_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            MODIFIED_ROGERS_DISTANCES[1][2]
    );
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_CAVALLI_SFORZA_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            CAVALLI_SFORZA_EDWARDS_DISTANCES[1][2]
    );
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_GOWER_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            GOWER_DISTANCES[1][2]
    );
    
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_MODIFIED_ROGERS_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (MODIFIED_ROGERS_DISTANCES[1][4]
           + MODIFIED_ROGERS_DISTANCES[2][4]
           + MODIFIED_ROGERS_DISTANCES[4][1]) / 3.0
    );
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_CAVALLI_SFORZA_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (CAVALLI_SFORZA_EDWARDS_DISTANCES[1][4]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES[2][4]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES[4][1]) / 3.0
    );
    public static final Evaluation ENTRY_TO_NEAREST_ENTRY_GOWER_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (GOWER_DISTANCES[1][2]
           + GOWER_DISTANCES[2][1]
           + GOWER_DISTANCES[4][1]) / 3.0
    );
    
    // A-NE distance
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_MODIFIED_ROGERS_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            (MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[0][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[1][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[2][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[3][3]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[4][2]) / 5.0
    );
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_CAVALLI_SFORZA_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            (CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[0][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[1][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[2][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[3][3]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[4][2]) / 5.0
    );
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_GOWER_SUBSET1 = SimpleEvaluation.WITH_VALUE(
            (GOWER_DISTANCES[0][2]
           + GOWER_DISTANCES[1][2]
           + GOWER_DISTANCES[2][2]
           + GOWER_DISTANCES[3][3]
           + GOWER_DISTANCES[4][2]) / 5.0
    );
    
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_MODIFIED_ROGERS_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            (MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[0][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[1][1]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[2][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[3][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[4][1]) / 5.0
    );
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_CAVALLI_SFORZA_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            (CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[0][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[1][1]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[2][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[3][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[4][1]) / 5.0
    );
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_GOWER_SUBSET2 = SimpleEvaluation.WITH_VALUE(
            (GOWER_DISTANCES[0][2]
           + GOWER_DISTANCES[1][1]
           + GOWER_DISTANCES[2][2]
           + GOWER_DISTANCES[3][1]
           + GOWER_DISTANCES[4][1]) / 5.0
    );
    
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_MODIFIED_ROGERS_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[0][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[1][1]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[2][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[3][2]
           + MODIFIED_ROGERS_DISTANCES_CEIL_MISSING[4][4]) / 5.0
    );
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_CAVALLI_SFORZA_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[0][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[1][1]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[2][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[3][2]
           + CAVALLI_SFORZA_EDWARDS_DISTANCES_CEIL_MISSING[4][4]) / 5.0
    );
    public static final Evaluation ACCESSION_TO_NEAREST_ENTRY_GOWER_SUBSET3 = SimpleEvaluation.WITH_VALUE(
            (GOWER_DISTANCES[0][4]
           + GOWER_DISTANCES[1][1]
           + GOWER_DISTANCES[2][2]
           + GOWER_DISTANCES[3][1]
           + GOWER_DISTANCES[4][4]) / 5.0
    );
    
}
