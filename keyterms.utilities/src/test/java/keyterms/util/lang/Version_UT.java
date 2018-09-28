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

package keyterms.util.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import keyterms.testing.Tests;
import keyterms.util.collect.Bags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Version_UT {

    @Test
    public void toText() {
        assertEquals("v1.0", new Version(1).toString());
        assertEquals("v1.0", new Version(1, 0).toString());
        assertEquals("v1.0", new Version(1, 0, 0).toString());
        assertEquals("v1.0.1", new Version(1, 0, 1).toString());
    }

    @Test
    public void fromText() {
        Version version = new Version("1");
        assertEquals(1, version.getMajorNumber());
        assertEquals(0, version.getMinorNumber());
        assertEquals(0, version.getPatchNumber());
        version = new Version("1.2");
        assertEquals(1, version.getMajorNumber());
        assertEquals(2, version.getMinorNumber());
        assertEquals(0, version.getPatchNumber());
        version = new Version("1.2.3");
        assertEquals(1, version.getMajorNumber());
        assertEquals(2, version.getMinorNumber());
        assertEquals(3, version.getPatchNumber());
        version = new Version("5.3.18 release Multi-LP");
        assertNotNull(version);
        assertEquals(5, version.getMajorNumber());
        assertEquals(3, version.getMinorNumber());
        assertEquals(18, version.getPatchNumber());
    }

    @Test
    public void sorting() {
        List<Version> unsorted = Bags.arrayList(
                new Version(1),
                new Version(2),
                new Version(1, 2),
                new Version(1, 1),
                new Version(1, 1, 1),
                new Version(2, 0, 1)
        );
        List<Version> expected = Bags.arrayList(
                new Version(1),
                new Version(1, 1),
                new Version(1, 1, 1),
                new Version(1, 2),
                new Version(2),
                new Version(2, 0, 1)
        );
        ArrayList<Version> sorted = new ArrayList<>(unsorted);
        Collections.sort(sorted);
        assertEquals(expected, sorted);
    }

    @Test
    public void serialization()
            throws Exception {
        Tests.testSerialize(new Version(1, 2, 3));
    }
}