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

import java.util.HashMap;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Diff_UT {

    @Test
    public void setDiff() {
        Set<String> set1 = Bags.hashSet(
                "Hello", "World", "Jello", "Pudding", "Hot", "Dog"
        );
        Set<String> set2 = Bags.hashSet(
                "Jello", "Pudding", "Tinker", "Toy"
        );
        HashMap<String, Diff> diffMap;
        diffMap = Bags.hashMap(
                Keyed.of("Hello", Diff.REMOVED),
                Keyed.of("World", Diff.REMOVED),
                Keyed.of("Jello", Diff.REMOVED),
                Keyed.of("Pudding", Diff.REMOVED),
                Keyed.of("Hot", Diff.REMOVED),
                Keyed.of("Dog", Diff.REMOVED)
        );
        assertEquals(diffMap, Diff.compare(set1, null));
        diffMap = Bags.hashMap(
                Keyed.of("Hello", Diff.ADDED),
                Keyed.of("World", Diff.ADDED),
                Keyed.of("Jello", Diff.ADDED),
                Keyed.of("Pudding", Diff.ADDED),
                Keyed.of("Hot", Diff.ADDED),
                Keyed.of("Dog", Diff.ADDED)
        );
        assertEquals(diffMap, Diff.compare(null, set1));
        diffMap = Bags.hashMap(
                Keyed.of("Hello", Diff.REMOVED),
                Keyed.of("World", Diff.REMOVED),
                Keyed.of("Jello", Diff.SAME),
                Keyed.of("Pudding", Diff.SAME),
                Keyed.of("Hot", Diff.REMOVED),
                Keyed.of("Dog", Diff.REMOVED),
                Keyed.of("Tinker", Diff.ADDED),
                Keyed.of("Toy", Diff.ADDED)
        );
        assertEquals(diffMap, Diff.compare(set1, set2));
        diffMap = Bags.hashMap(
                Keyed.of("Hello", Diff.ADDED),
                Keyed.of("World", Diff.ADDED),
                Keyed.of("Jello", Diff.SAME),
                Keyed.of("Pudding", Diff.SAME),
                Keyed.of("Hot", Diff.ADDED),
                Keyed.of("Dog", Diff.ADDED),
                Keyed.of("Tinker", Diff.REMOVED),
                Keyed.of("Toy", Diff.REMOVED)
        );
        assertEquals(diffMap, Diff.compare(set2, set1));
    }

    @Test
    public void mapDiff() {
        HashMap<String, String> map1 = Bags.hashMap(
                Keyed.of("Hello", "World"),
                Keyed.of("Jello", "Pudding"),
                Keyed.of("Hot", "Dog")
        );
        HashMap<String, String> map2 = Bags.hashMap(
                Keyed.of("Jello", "pudding"),
                Keyed.of("Tinker", "Toy")
        );
        HashMap<String, Diff> diffMap;
        diffMap = Bags.hashMap(
                Keyed.of("Hello", Diff.REMOVED),
                Keyed.of("Jello", Diff.REMOVED),
                Keyed.of("Hot", Diff.REMOVED)
        );
        assertEquals(diffMap, Diff.compare(map1, null));
        diffMap = Bags.hashMap(
                Keyed.of("Hello", Diff.ADDED),
                Keyed.of("Jello", Diff.ADDED),
                Keyed.of("Hot", Diff.ADDED)
        );
        assertEquals(diffMap, Diff.compare(null, map1));
        diffMap = Bags.hashMap(
                Keyed.of("Hello", Diff.REMOVED),
                Keyed.of("Jello", Diff.MODIFIED),
                Keyed.of("Hot", Diff.REMOVED),
                Keyed.of("Tinker", Diff.ADDED)
        );
        assertEquals(diffMap, Diff.compare(map1, map2));
        diffMap = Bags.hashMap(
                Keyed.of("Hello", Diff.ADDED),
                Keyed.of("Jello", Diff.MODIFIED),
                Keyed.of("Hot", Diff.ADDED),
                Keyed.of("Tinker", Diff.REMOVED)
        );
        assertEquals(diffMap, Diff.compare(map2, map1));
    }
}