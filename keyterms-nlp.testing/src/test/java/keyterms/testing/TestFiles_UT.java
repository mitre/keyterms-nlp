/*
 * NOTICE
 * This software was produced for the U.S. Government and is subject to the
 * Rights in Data-General Clause 5.227-14 (May 2014).
 * Copyright 2018 The MITRE Corporation. All rights reserved.
 *
 * “Approved for Public Release; Distribution Unlimited” Case  18-2165
 *
 * This project contains content developed by The MITRE Corporation.
 * If this code is used in a deployment or embedded within another project,
 * it is requested that you send an email to opensource@mitre.org
 * in order to let us know where this software is being used.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package keyterms.testing;

import java.nio.file.Path;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class TestFiles_UT {

    @Test
    public void baseDirectories() {
        assertNotNull(TestFiles.CWD);
        assertNotNull(TestFiles.PROJECT_HOME);
        assertNotNull(TestFiles.JAVA_TEMP);
        assertNotNull(TestFiles.TEMP);
        assertNotEquals(TestFiles.JAVA_TEMP, TestFiles.TEMP);
    }

    @Test
    public void resourceLocation()
            throws Exception {
        Path resource = TestFiles.getResourcePath(getClass(), "txt");
        assertNotNull(resource);
        assertEquals(resource, TestFiles.getResourcePath(getClass(), "txt"));
    }

    @Test
    public void temps()
            throws Exception {
        Path tempDir = TestFiles.createTempDirectory();
        assertNotNull(tempDir);
        Path tempFile = TestFiles.createTempFile();
        assertNotNull(tempFile);
    }
}