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
import org.corehunter.data.CoreHunterData;
import org.corehunter.data.DistanceMatrixData;
import org.corehunter.data.simple.SimpleDistanceMatrixData;
import org.jamesframework.core.subset.SubsetSolution;
import uno.informatics.data.Data;
import uno.informatics.data.SimpleEntity;
import uno.informatics.data.io.FileType;
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
    
    /* -------------------- */
    /* Distance matrix data */
    /* -------------------- */
    
    public static DistanceMatrixData readDistanceMatrixData(String file) throws IOException{
        return SimpleDistanceMatrixData.readData(Paths.get(file), inferFileType(file));
    }
    
    public static DistanceMatrixData createDistanceMatrixData(double[][] distances, String[] ids, String[] names){
        // combine ids and names into headers
        int n = distances.length;
        if(ids == null){
            throw new IllegalArgumentException("Ids are required.");
        }
        if(ids.length != n){
            throw new IllegalArgumentException("Number of ids does not correspond to size of matrix.");
        }
        if(names == null){
            // use ids as names if no names are given
            names = ids;
        }
        if(names.length != n){
            throw new IllegalArgumentException("Number of names does not correspond to size of matrix.");
        }
        SimpleEntity[] headers = new SimpleEntity[n];
        for(int i = 0; i < n; i++){
            headers[i] = new SimpleEntityPojo(ids[i], names[i]);
        }
        // create and return data
        return new SimpleDistanceMatrixData(headers, distances);
    }
    
    /**
     * Get a copy of the full distance matrix.
     * 
     * @param data distance data
     * @return full distance matrix (copy)
     */
    public static double[][] getDistanceMatrix(DistanceMatrixData data){
        int n = data.getSize();
        double[][] dist = new double[n][n];
        for(int i = 0; i < n; i++){
            for (int j = 0; j < n; j++) {
                dist[i][j] = data.getDistance(i, j);
            }
        }
        return dist;
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
    
    public static String[] sampleCore(CoreHunterArguments args){
        CoreHunter ch = new CoreHunter();
        SubsetSolution core = ch.execute(args);
        String[] ids = core.getSelectedIDs().stream()
                                            .map(i -> args.getData().getHeader(i).getUniqueIdentifier())
                                            .toArray(n -> new String[n]);
        return ids;
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
    
}
