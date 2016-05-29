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

/**
 * Available objective measures
 *
 * @author Guy Davenport, Herman De Beukelaer
 */
public enum CoreHunterMeasure {
    GOWERS("Gowers distance", "GD"),
    MODIFIED_ROGERS("Modified Rogers distance", "MR"),
    CAVALLI_SFORZA_EDWARDS("Cavalli-Sforza and Edwards distance", "CE"),
    PRECOMPUTED_DISTANCE("Precomputed Distance", "PD") ;

    private String name ;
    private String abbreviation ;
    
    private CoreHunterMeasure(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation ;
    }

    public final String getName() {
        return name;
    }

    public final String getAbbreviation() {
        return abbreviation;
    } 
}
