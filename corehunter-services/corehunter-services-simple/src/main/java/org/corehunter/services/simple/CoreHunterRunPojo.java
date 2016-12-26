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

package org.corehunter.services.simple;

import org.corehunter.services.CoreHunterRun;
import org.corehunter.services.CoreHunterRunStatus;
import java.time.Instant ;

import uno.informatics.data.pojo.SimpleEntityPojo;

/**
 * Basic Pojo for CoreHunterRun
 * 
 * @author daveneti
 *
 */
public class CoreHunterRunPojo extends SimpleEntityPojo implements CoreHunterRun {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private Instant startInstant;
    private Instant endInstant;
    private CoreHunterRunStatus status;

    public CoreHunterRunPojo(String name) {
        super(name);
        
        startInstant = Instant.now() ;
        endInstant = null ;
        status = CoreHunterRunStatus.NOT_STARTED ;
    }
    
    public CoreHunterRunPojo(String uniqueIdentifier, String name) {
        super(uniqueIdentifier, name);
        
        startInstant = Instant.now() ;
        endInstant = null ;
        status = CoreHunterRunStatus.NOT_STARTED ;
    }
    
    public CoreHunterRunPojo(CoreHunterRun run) {
        super(run.getUniqueIdentifier(), run.getName());
        
        startInstant = run.getStartInstant() != null ? Instant.from(run.getStartInstant()) : null;
        endInstant = run.getEndInstant() != null ? Instant.from(run.getEndInstant()) : null;
        status = run.getStatus() ;
    }

    @Override
    public Instant getStartInstant() {
        return startInstant;
    }

    public final void setStartInstant(Instant startInstant) {
        
        this.startInstant = startInstant;
    }

    @Override
    public Instant getEndInstant() {
        return endInstant;
    }

    public final void setEndInstant(Instant endInstant) {
        this.endInstant = endInstant;
    }

    @Override
    public CoreHunterRunStatus getStatus() {
        return status;
    }

    public final void setStatus(CoreHunterRunStatus status) {
        this.status = status;
    }

}