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

package org.corehunter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.GenotypeData;
import org.corehunter.data.GenotypeDataFormat;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.corehunter.data.simple.SimpleGenotypeData;
import org.corehunter.listener.SimpleCoreHunterListener;
import org.jamesframework.core.subset.SubsetSolution;
import uno.informatics.data.Data;
import uno.informatics.data.Feature;
import uno.informatics.data.Scale;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.dataset.FeatureData;
import uno.informatics.data.feature.array.ArrayFeatureData;
import uno.informatics.data.io.FileType;
import uno.informatics.data.pojo.DataPojo;
import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * Simple API used by the R interface.
 *
 * @author Herman De Beukelaer, Guy Davenport
 */
public class API {

    /* -------- */
    /* All data */
    /* -------- */
    
    public static String[] getIds(Data data){
        int n = data.getSize();
        String[] ids = new String[n];
        for(int i = 0; i < n; i++){
            ids[i] = data.getHeader(i).getUniqueIdentifier();
        }
        return ids;
    }
    
    public static String[] getNames(Data data){
        int n = data.getSize();
        String[] ids = new String[n];
        for(int i = 0; i < n; i++){
            ids[i] = data.getHeader(i).getName();
        }
        return ids;
    }
    
    /**
     * Get unique identifier strings from item indices.
     * 
     * @param data a data object
     * @param indices item indices (zero-based).
     * @return unique identifiers
     */
    public static String[] getIdsFromIndices(Data data, int[] indices){
        String[] ids = Arrays.stream(indices)
                             .mapToObj(i -> data.getHeader(i).getUniqueIdentifier())
                             .toArray(n -> new String[n]);
        return ids;
    }
    
    /**
     * Get item indices from their string identifiers.
     * 
     * @param data a data object
     * @param ids string identifiers
     * @return item indices (zero-based)
     */
    public static int[] getIndicesFromIds(DataPojo data, String[] ids){
        int[] indices = Arrays.stream(ids)
                              .mapToInt(id -> data.indexOf(id))
                              .toArray();
        return indices;
    }
    
    /* -------------------- */
    /* Distance matrix data */
    /* -------------------- */
    
    public static DistanceMatrixData readDistanceMatrixData(String file) throws IOException{
        return SimpleDistanceMatrixData.readData(Paths.get(file), inferFileType(file));
    }
    
    public static DistanceMatrixData createDistanceMatrixData(double[][] distances, String[] ids, String[] names){
        // check arguments
        if(distances == null){
            throw new IllegalArgumentException("Distances are required.");
        }
        int n = distances.length;
        if(ids == null){
            throw new IllegalArgumentException("Ids are required.");
        }
        if(ids.length != n){
            throw new IllegalArgumentException("Number of ids does not correspond to size of matrix.");
        }
        if(names != null && names.length != n){
            throw new IllegalArgumentException("Number of names does not correspond to size of matrix.");
        }
        // create and return data
        return new SimpleDistanceMatrixData(createHeaders(ids, names), distances);
    }
    
    /* ------------- */
    /* Genotype data */
    /* ------------- */
    
    public static GenotypeData readGenotypeData(String file, String format) throws IOException{
        return SimpleGenotypeData.readData(
                Paths.get(file),
                inferFileType(file),
                GenotypeDataFormat.valueOf(format.toUpperCase())
        );
    }
    
    public static GenotypeData createBiparentalGenotypeData(int[][] alleleScores,
                                                            String[] ids, String[] names,
                                                            String[] markerNames){
        // check arguments
        if(alleleScores == null){
            throw new IllegalArgumentException("Allele scores are required.");
        }
        int n = alleleScores.length;
        int m = alleleScores[0].length;
        if(ids == null){
            throw new IllegalArgumentException("Ids are required.");
        }
        if(ids.length != n){
            throw new IllegalArgumentException("Number of ids does not correspond to number of rows.");
        }
        if(names != null && names.length != n){
            throw new IllegalArgumentException("Number of names does not correspond to number of rows.");
        }
        if(markerNames != null && markerNames.length != m){
            throw new IllegalArgumentException("Number of marker names does not correspond to number of columns.");
        }
        // create and return data
        return new SimpleBiAllelicGenotypeData(createHeaders(ids, names), markerNames, toIntegerMatrix(alleleScores));
    }
    
    public static String[][] getAlleles(GenotypeData data){
        int numMarkers = data.getNumberOfMarkers();
        String[][] alleles = new String[numMarkers][];
        for(int m = 0; m < numMarkers; m++){
            int numAlleles = data.getNumberOfAlleles(m);
            alleles[m] = new String[numAlleles];
            for(int a = 0; a < numAlleles; a++){
                alleles[m][a] = data.getAlleleName(m, a);
            }
        }
        return alleles;
    }
    
    public static String[] getMarkerNames(GenotypeData data){
        int numMarkers = data.getNumberOfMarkers();
        String[] markerNames = new String[numMarkers];
        for(int m = 0; m < numMarkers; m++){
            markerNames[m] = data.getMarkerName(m);
        }
        return markerNames;
    }
    
    /* -------------- */
    /* Phenotype data */
    /* -------------- */
    
    public static FeatureData readPhenotypeData(String file) throws IOException{
        return ArrayFeatureData.readData(Paths.get(file), inferFileType(file));
    }
    
    public static Double[] getRanges(FeatureData data){
        List<Feature> features = data.getFeatures();
        int numTraits = features.size();
        Double[] ranges = new Double[numTraits];
        for(int t = 0; t < numTraits; t++){
            Scale scale = features.get(t).getMethod().getScale();
            Number min = scale.getMinimumValue();
            Number max = scale.getMaximumValue();
            if(min != null && max != null){
                ranges[t] = max.doubleValue() - min.doubleValue();
            }
        }
        return ranges;
    }
    
    /* --------- */
    /* Arguments */
    /* --------- */
    
    public static CoreHunterObjective createObjective(String type, String measure, double weight){        
        return new CoreHunterObjective(
                CoreHunterObjectiveType.createFromAbbreviation(type),
                CoreHunterMeasure.createFromAbbreviation(measure),
                weight
        );
    }
    
    public static CoreHunterArguments createArguments(CoreHunterData data, int size, CoreHunterObjective[] objectives){
        return new CoreHunterArguments(data, size, Arrays.asList(objectives));
    }
    
    /* --------- */
    /* Execution */
    /* --------- */
    
    /**
     * Sample a core collection.
     * 
     * @param args Core Hunter arguments including data, objective and subset size.
     * @param mode Execution mode, one of "default" or "fast".
     * @param timeLimit Absolute runtime limit in seconds.
     * @param maxTimeWithoutImprovement Maximum time without finding an improvement, in seconds.
     * @param silent If <code>true</code> no output is written to the console.
     * @return Indices of selected items (zero-based).
     */
    public static int[] sampleCore(CoreHunterArguments args, String mode,
                                   int timeLimit, int maxTimeWithoutImprovement,
                                   boolean silent){
        // interpret arguments
        CoreHunterExecutionMode exMode = CoreHunterExecutionMode.DEFAULT;
        if(mode.equals("fast")){
            exMode = CoreHunterExecutionMode.FAST;
        }
        // create Core Hunter executor
        CoreHunter ch = new CoreHunter(exMode);
        if(timeLimit > 0){
            ch.setTimeLimit(timeLimit);
        }
        if(maxTimeWithoutImprovement > 0){
            ch.setMaxTimeWithoutImprovement(maxTimeWithoutImprovement);
        }
        if(!silent){
            ch.setListener(new SimpleCoreHunterListener());
        }
        // sample core
        SubsetSolution core = ch.execute(args);
        // convert result
        int[] ids = new int[core.getNumSelectedIDs()];
        int i = 0;
        for(int id : core.getSelectedIDs()){
            ids[i++] = id;
        }
        return ids;
    }
    
    /* ---------- */
    /* Evaluation */
    /* ---------- */
    
    /**
     * Evaluate a core collection.
     * 
     * @param selected Indices of selected items (zero-based).
     * @param data Core Hunter data
     * @param obj Objective used to evaluate the core.
     * @return Core value according to the used objective.
     */
    public static double evaluateCore(int[] selected, CoreHunterData data, CoreHunterObjective obj){
        CoreHunter ch = new CoreHunter();
        SubsetSolution sol = new SubsetSolution(data.getIDs());
        for(int sel : selected){
            sol.select(sel);
        }
        return ch.evaluate(sol, data, obj);
    }
        
    /* ----------------------- */
    /* Private utility methods */
    /* ----------------------- */
    
    /**
     * Infer file type from the extension of the file.
     * Only supports {@link FileType#TXT} and {@link FileType#CSV} and returns <code>null</code> for other extensions.
     * 
     * @param file file path
     * @return file type
     */
    private static FileType inferFileType(String file){
        file = file.toLowerCase();
        if(file.endsWith(".txt")){
            return FileType.TXT;
        }
        if(file.endsWith(".csv")){
            return FileType.CSV;
        }
        return null;
    }
    
    private static SimpleEntity[] createHeaders(String[] ids, String[] names){
        if(names == null){
            names = ids;
        }
        int n = ids.length;
        SimpleEntity[] headers = new SimpleEntity[n];
        for(int i = 0; i < n; i++){
            if(names[i] != null){
                headers[i] = new SimpleEntityPojo(ids[i], names[i]);
            } else {
                headers[i] = new SimpleEntityPojo(ids[i]);
            }
        }
        return headers;
    }
    
    /**
     * Convert primitive to Integer matrix.
     * All occurrences of {@link Integer#MIN_VALUE} are replaced with <code>null</code>,
     * which is used by rJava to encode missing values (NAs in R).
     * 
     * @param matrix primitive integer matrix
     * @return Integer matrix
     */
    private static Integer[][] toIntegerMatrix(int[][] matrix){
        Integer[][] conv = new Integer[matrix.length][];
        for(int i = 0; i < conv.length; i++){
            conv[i] = new Integer[matrix[i].length];
            for(int j = 0; j < conv[i].length; j++){
                conv[i][j] = (matrix[i][j] != Integer.MIN_VALUE ? matrix[i][j] : null);
            }
        }
        return conv;
    }
    
}
