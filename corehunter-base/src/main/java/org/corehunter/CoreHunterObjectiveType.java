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

import java.util.List;

/**
 * Available objective types
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public enum CoreHunterObjectiveType {
    AV_ENTRY_TO_ENTRY("Average Entry to Entry", "AE"),
    AV_ENTRY_TO_NEAREST_ENTRY("Average Entry to Nearest Entry", "AN") ,
    AV_ACCESSION_TO_NEAREST_ENTRY("Average Accession to Nearest Entry", "AC") ,
    SHANNON_DIVERSITY("Shannon diversity index", "SH"),
    HETEROZYGOUS_LOCI("Expected proportion of heterozygous loci per individual", "HE"),
    COVERAGE("Coverage", "CV") ;
    
    private String name ;
    private String abbreviation ;
    
    private CoreHunterObjectiveType(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation ;
    }

    /**
     * Gets the name of the type 
     * @return the name of the type 
     */
    public final String getName() {
        return name;
    }  
    
    /**
     * Gets the two letter abbreviation of the type 
     * @return the abbreviation of the type
     */
    public final String getAbbreviation() {
        return abbreviation;
    }
}
