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

package keyterms.util.collect;

import java.util.HashSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Unique_UT {

    @Test
    public void nullKeysAllowed() {
        Unique<String> nulls = new Unique<>(null);
        new HashSet<Unique<String>>().add(nulls);
    }

    @Test
    public void hashing() {
        Unique<String> u1 = new Unique<>("Hello");
        Unique<String> u2 = new Unique<>("Hello");
        Unique<String> u3 = new Unique<>("Jello");
        Unique<String> u4 = new Unique<>(null);
        Unique<String> u5 = new Unique<>(null);
        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertNotEquals(u1, u4);
        assertNotEquals(u1, u5);
        assertEquals(u4, u5);
    }
}