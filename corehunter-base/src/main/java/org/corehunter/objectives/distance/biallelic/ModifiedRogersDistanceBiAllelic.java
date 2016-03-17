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

package org.corehunter.objectives.distance.biallelic;

import java.util.Set;

import org.corehunter.data.BiAllelicGenotypeVariantData;
import org.corehunter.objectives.distance.GenotypeVariantDistanceMetric;
import org.corehunter.data.GenotypeVariantData;
import uno.informatics.data.SimpleEntity;

public class ModifiedRogersDistanceBiAllelic implements GenotypeVariantDistanceMetric<GenotypeVariantData> {

    public ModifiedRogersDistanceBiAllelic(BiAllelicGenotypeVariantData dataset) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public double getDistance(int idX, int idY) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Set<Integer> getIDs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenotypeVariantData getData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleEntity getHeader(int id) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getDatasetSize() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
