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

package org.corehunter.data;

import java.util.Objects;
import uno.informatics.data.SimpleEntity;

/**
 * Unmodifiable header associated with an individual. A header can have a name and/or unique identifier.
 * 
 * @author Herman De Beukelaer
 */
public class Header implements SimpleEntity {

    private final String name;
    private final String uniqueIdentifier;

    public Header(String name, String uniqueIdentifier) {
        this.name = name;
        this.uniqueIdentifier = uniqueIdentifier;
    }

    @Override
    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if(uniqueIdentifier != null){
            hash = 67 * hash + Objects.hashCode(uniqueIdentifier);        
        } else {
            hash = 71 * hash + Objects.hashCode(name);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Header other = (Header) obj;
        
        // compare by identifier if defined for at least one header; else by name
        if(this.uniqueIdentifier != null || other.uniqueIdentifier != null){
            // by identifier
            return Objects.equals(this.uniqueIdentifier, other.uniqueIdentifier);
        } else {
            // by name
            return Objects.equals(this.name, other.name);
        }
    }

    @Override
    public String toString() {
        return "Header{" + "name=" + name + ", uniqueIdentifier=" + uniqueIdentifier + '}';
    }

}
