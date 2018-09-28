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
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class Keyed_UT {

    @Test
    public void nullKeysAllowed() {
        Keyed<String, String> nulls = new Keyed<>(null, null);
        new HashSet<Keyed<String, String>>().add(nulls);
    }

    @Test
    public void mutability() {
        Keyed<String, Number> keyed = new Keyed<>("test.keyed");
        assertEquals("test.keyed", keyed.getKey());
        assertNull(keyed.getValue());
        List<Number> values = Bags.arrayList(1, 2, 3.4);
        values.forEach((value) -> {
            keyed.setValue(value);
            assertEquals(value, keyed.getValue());
        });
    }

    @Test
    public void hashing() {
        Keyed<String, String> v1 = new Keyed<>("Hello", "World");
        Keyed<String, String> v2 = new Keyed<>("Hello", "Jello");
        Keyed<String, String> v3 = new Keyed<>("Jello", "Pudding");
        Keyed<String, String> v4 = new Keyed<>(null, "Jello");
        Keyed<String, String> v5 = new Keyed<>(null, "Pudding");
        assertEquals(v1, v2);
        assertNotEquals(v1, v3);
        assertNotEquals(v1, v4);
        assertNotEquals(v1, v5);
        assertEquals(v4, v5);
    }
}