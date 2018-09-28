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

package keyterms.util.config;

import org.junit.Test;

import keyterms.testing.Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RangeResolver_UT {

    @Test
    public void badRange() {
        Tests.testError(IllegalArgumentException.class,
                "Minimum greater than maximum \\[.*]",
                () -> new RangeResolver<>(11, 10));
    }

    @Test
    public void nullValue() {
        assertNull(new RangeResolver<>(5, 95).resolve(null));
    }

    @Test
    public void minimumEnforced() {
        RangeResolver<Integer> resolver = new RangeResolver<>(5, 95);
        assertEquals(25, (int)resolver.resolve(25));
        assertEquals(5, (int)resolver.resolve(4));
    }

    @Test
    public void maximumEnforced() {
        RangeResolver<Integer> resolver = new RangeResolver<>(5, 95);
        assertEquals(90, (int)resolver.resolve(90));
        assertEquals(95, (int)resolver.resolve(96));
    }
}