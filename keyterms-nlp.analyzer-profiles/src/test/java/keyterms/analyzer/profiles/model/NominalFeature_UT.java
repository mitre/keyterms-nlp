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

import java.util.List;

import org.junit.Test;

import keyterms.testing.Tests;
import keyterms.util.collect.Bags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NominalFeature_UT {

    @Test
    public void dynamicValues()
            throws Exception {
        NominalFeature feature = new NominalFeature("test");
        assertEquals(0, feature.size());
        List<String> tests = Bags.staticList("one", "two", "three");
        for (int t = 0; t < tests.size(); t++) {
            String test = tests.get(t);
            assertTrue(test, feature.test(test));
            assertEquals(t, feature.toOrdinal(test));
            assertEquals(test, feature.toValue(t));
        }
        assertEquals(tests.size(), feature.size());
        feature.close();
        assertTrue(feature.test("four"));
        assertEquals(-1, feature.toOrdinal("four"));
        assertNull(feature.toValue(-1));
        assertEquals(tests.size(), feature.size());
        feature = Tests.testSerialize(feature);
        assertEquals(tests.size(), feature.size());
    }
}