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

import org.corehunter.services.CorehunterRun;
import org.corehunter.services.CorehunterRunStatus;
import org.joda.time.DateTime;

import uno.informatics.data.pojo.SimpleEntityPojo;

public class CorehunterRunPojo extends SimpleEntityPojo implements CorehunterRun {
    
    private DateTime startDate;
    private DateTime endDate;
    private CorehunterRunStatus status;

    public CorehunterRunPojo(String name) {
        super(name);
        
        startDate = new DateTime() ;
        endDate = null ;
        status = CorehunterRunStatus.NOT_STARTED ;
    }
    
    public CorehunterRunPojo(String uniqueIdentifier, String name) {
        super(uniqueIdentifier, name);
        
        startDate = new DateTime() ;
        endDate = null ;
        status = CorehunterRunStatus.NOT_STARTED ;
    }

    @Override
    public DateTime getStartDate() {
        return startDate;
    }

    public final void setStartDate(DateTime startDate) {
        
        this.startDate = startDate;
    }

    @Override
    public DateTime getEndDate() {
        return endDate;
    }

    public final void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public CorehunterRunStatus getStatus() {
        return status;
    }

    public final void setStatus(CorehunterRunStatus status) {
        this.status = status;
    }

}