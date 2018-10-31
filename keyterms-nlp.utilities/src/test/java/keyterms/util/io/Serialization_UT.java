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

package keyterms.util.io;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.Test;

import keyterms.testing.TestFiles;
import keyterms.util.Errors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

public class Serialization_UT {

    @Test
    public void serialization()
            throws Exception {
        assertNull(Serialization.toBytes(null));
        assertNull(Serialization.fromBytes(Serializable.class, null));
        byte[] helloWorldBytes = Serialization.toBytes("Hello World");
        String helloWorld = Serialization.fromBytes(String.class, helloWorldBytes);
        assertNotNull(helloWorldBytes);
        assertEquals("Hello World", helloWorld);
    }

    @Test
    public void serialCopy()
            throws Exception {
        assertNull(Serialization.serialCopy(ArrayList.class, null));
        String[] strings = new String[] { "Hello", "World", "Jello", "Pudding" };
        String[] cloned = Serialization.serialCopy(String[].class, strings);
        assertArrayEquals(strings, cloned);
        assertNotSame(strings, cloned);
    }

    @Test
    public void fileOperations()
            throws Exception {
        String helloWorld = "Hello World";
        Path tempFile = null;
        try {
            tempFile = TestFiles.createTempFile();
            Serialization.toFile(helloWorld, tempFile);
            String copy = Serialization.fromFile(String.class, tempFile);
            assertEquals(helloWorld, copy);
        } finally {
            if (tempFile != null) {
                try {
                    IO.delete(tempFile);
                } catch (Exception error) {
                    Errors.check(error);
                }
            }
        }
    }
}