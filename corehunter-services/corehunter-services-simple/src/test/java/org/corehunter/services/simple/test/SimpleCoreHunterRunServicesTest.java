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

package org.corehunter.services.simple.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.corehunter.services.simple.FileBasedDatasetServices;
import org.corehunter.services.simple.SimpleCoreHunterRunServices;
import org.junit.Test;

public class SimpleCoreHunterRunServicesTest {

    private static final String TARGET_DIRECTORY = "target";
    private static final Path ROOT_DIRECTORY = Paths.get(TARGET_DIRECTORY,
            SimpleCoreHunterRunServicesTest.class.getSimpleName());

    @Test
    public void testSimpleCoreHunterRunServices() {

        try {
            FileBasedDatasetServices fileBasedDatasetServices = new FileBasedDatasetServices(createTempDirectory());

            SimpleCoreHunterRunServices coreHunterRunServices = new SimpleCoreHunterRunServices(createTempDirectory(),
                    fileBasedDatasetServices);

        } catch (Exception e) {

            e.printStackTrace();

            fail(e.getMessage());
        }
    }

    private Path createTempDirectory() throws IOException {

        Files.createDirectories(ROOT_DIRECTORY);

        Path path = Files.createTempDirectory(ROOT_DIRECTORY, null);

        Files.createDirectories(path);

        return path;
    }
}
