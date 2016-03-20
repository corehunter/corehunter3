/*******************************************************************************
 * Copyright 2016 Guy Davenport
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.corehunter.services.simple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.corehunter.services.CorehunterRunArguments;

import uno.informatics.data.pojo.SimpleEntityPojo;

public class CorehunterRunArgumentsPojo extends SimpleEntityPojo implements CorehunterRunArguments {
    private int subsetSize;
    private List<String> datasetIds;

    private CorehunterRunArgumentsPojo(String name, int subsetSize) {
        super(UUID.randomUUID().toString(), name);
        this.subsetSize = subsetSize;
        this.datasetIds = new ArrayList<String>();
    }

    public CorehunterRunArgumentsPojo(String name, int subsetSize, String... datasetId) {
        this(name, subsetSize);

        this.datasetIds = new ArrayList<String>();

        for (int i = 0; i < datasetId.length; ++i) {
            if (!this.datasetIds.contains(datasetId[i])) {
                this.datasetIds.add(datasetId[i]);
            } else {
                throw new IllegalArgumentException("Dataset id already present in list of ids" + datasetId[i]);
            }
        }
    }

    public CorehunterRunArgumentsPojo(String name, int subsetSize, List<String> datasetIds) {
        this(name, subsetSize);

        this.datasetIds = new ArrayList<String>();

        Iterator<String> iterator = datasetIds.iterator();

        String datasetId;

        while (iterator.hasNext()) {
            datasetId = iterator.next();

            if (!this.datasetIds.contains(datasetId)) {
                this.datasetIds.add(datasetId);
            } else {
                throw new IllegalArgumentException("Dataset id already present in list of ids" + datasetId);
            }
        }
    }

    @Override
    public int getSubsetSize() {
        return subsetSize;
    }

    @Override
    public List<String> getDatasetIds() {
        return datasetIds;
    }

}
