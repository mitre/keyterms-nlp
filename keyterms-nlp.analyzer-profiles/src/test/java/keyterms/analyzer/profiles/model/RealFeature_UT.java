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

package keyterms.analyzer.profiles.model;

import org.junit.Test;

import keyterms.testing.Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RealFeature_UT {

    private final RealFeature feature = new RealFeature("test");

    @Test
    public void parsing() {
        assertNull(feature.parse(null));
        assertNull(feature.parse(""));
        assertNull(feature.parse("?"));
        assertEquals(1.2, feature.parse("1.2"), 0.0);
        assertEquals(3.0, feature.parse("3"), 0.0);
    }

    @Test
    public void formatting() {
        assertEquals("?", feature.asText(null));
        assertEquals("2.1", feature.asText(2.1));
    }

    @Test
    public void predicate() {
        assertTrue(feature.test(null));
        assertFalse(feature.test("text"));
        assertTrue(feature.test(0L));
        assertTrue(feature.test(0));
        assertTrue(feature.test((short)0));
        assertTrue(feature.test(1.2f));
        assertTrue(feature.test(1.2));
    }

    @Test
    public void serialization()
            throws Exception {
        Tests.testSerialize(feature);
    }
}