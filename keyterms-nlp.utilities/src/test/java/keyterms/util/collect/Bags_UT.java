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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Bags_UT {

    @Test
    public void arrayList() {
        assertNotNull(Bags.arrayList((String)null));
        ArrayList<String> strings = Bags.arrayList("hello", "world", "hello");
        assertEquals(3, strings.size());
    }

    @Test
    public void staticList() {
        List<String> strings = Bags.staticList("hello", "world", "hello");
        assertEquals(3, strings.size());
    }

    @Test
    public void hashSet() {
        Set<String> emptySet = Bags.hashSet((String[])null);
        assertNotNull(emptySet);
        assertEquals(0, emptySet.size());

        Set<String> strings = Bags.hashSet((String)null);
        assertNotNull(strings);
        assertEquals(1, strings.size());
        assertTrue(strings.contains(null));

        strings = Bags.hashSet("hello", "world", "hello");
        assertEquals(2, strings.size());
        assertTrue(strings.contains("hello"));
        assertTrue(strings.contains("world"));
    }

    @Test
    public void naturalTreeSet() {
        List<Integer> unsorted = Bags.arrayList(5, 2, 6, 3, 1, 9, 0);
        TreeSet<Integer> expected = new TreeSet<>(unsorted);
        TreeSet<Integer> actual = Bags.sortedSet(5, 2, 6, 3, 1, 9, 0);
        assertEquals(expected, actual);
        List<Integer> sorted = new ArrayList<>(unsorted);
        Collections.sort(sorted);
        assertEquals(sorted, new ArrayList<>(actual));
    }

    @Test
    public void reverseTreeSet() {
        List<Integer> unsorted = Bags.arrayList(5, 2, 6, 3, 1, 9, 0);
        TreeSet<Integer> expected = new TreeSet<>(Comparator.reverseOrder());
        expected.addAll(unsorted);
        TreeSet<Integer> actual = Bags.sortedSet(Comparator.reverseOrder(), 5, 2, 6, 3, 1, 9, 0);
        assertEquals(expected, actual);
        List<Integer> sorted = new ArrayList<>(unsorted);
        Collections.sort(sorted);
        Collections.reverse(sorted);
        assertEquals(sorted, new ArrayList<>(actual));
    }

    @Test
    public void staticSet() {
        Set<String> strings = Bags.staticSet("hello", "world", "hello");
        assertEquals(2, strings.size());
        assertTrue(strings.contains("hello"));
        assertTrue(strings.contains("world"));
    }

    @Test
    public void hashMap() {
        Map<String, String> map = Bags.hashMap(
                new Keyed<>("hello", "world"),
                new Keyed<>("jello", "pudding")
        );
        assertEquals(2, map.size());
        assertTrue(map.containsKey("hello"));
        assertTrue(map.containsKey("jello"));
    }

    @Test
    public void orderedMap() {
        LinkedHashMap<String, String> map = Bags.orderedMap(
                new Keyed<>("hello", "world"),
                new Keyed<>("jello", "pudding")
        );
        assertEquals(2, map.size());
        assertTrue(map.containsKey("hello"));
        assertTrue(map.containsKey("jello"));
        assertEquals("hello", map.keySet().stream().findFirst().orElse(null));
    }

    @Test
    public void sortedMap() {
        TreeMap<String, String> map = Bags.sortedMap(
                new Keyed<>("hello", "world"),
                new Keyed<>("jello", "pudding")
        );
        assertEquals(2, map.size());
        assertTrue(map.containsKey("hello"));
        assertTrue(map.containsKey("jello"));
        assertEquals("hello", map.firstKey());
    }

    @Test
    public void reverseSortedMap() {
        TreeMap<String, String> map = Bags.sortedMap(
                Comparator.reverseOrder(),
                new Keyed<>("hello", "world"),
                new Keyed<>("jello", "pudding")
        );
        assertEquals(2, map.size());
        assertTrue(map.containsKey("hello"));
        assertTrue(map.containsKey("jello"));
        assertEquals("jello", map.firstKey());
    }

    @Test
    public void staticMap() {
        Map<String, String> map = Bags.staticMap(
                new Keyed<>("hello", "world"),
                new Keyed<>("jello", "pudding")
        );
        assertEquals(2, map.size());
        assertTrue(map.containsKey("hello"));
        assertTrue(map.containsKey("jello"));
    }
}