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

package keyterms.util.collect.storagetrie;

import java.util.Vector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StorageTrie_UT {

    @Test
    public void constructorWithCase_addWithoutData_containsFullWord() {
        String prefix1 = "Bob";
        String prefix2 = "bob";
        String prefix3 = "bo";

        StorageTrie<Integer> s = new StorageTrie<>(false);
        assertEquals(0, s.getWordCount());
        assertFalse(s.contains(prefix1, true));
        assertFalse(s.contains(prefix2, true));
        assertFalse(s.contains(prefix3, true));

        s.add(prefix1);
        assertEquals(1, s.getWordCount());
        assertTrue(s.contains(prefix1, true));
        assertFalse(s.contains(prefix2, true));
        assertFalse(s.contains(prefix3, true));

        s.add(prefix2);
        assertEquals(2, s.getWordCount());
        assertTrue(s.contains(prefix1, true));
        assertTrue(s.contains(prefix2, true));
        assertFalse(s.contains(prefix3, true));

        s.add(prefix3);
        assertEquals(3, s.getWordCount());
        assertTrue(s.contains(prefix1, true));
        assertTrue(s.contains(prefix2, true));
        assertTrue(s.contains(prefix3, true));

        s = new StorageTrie<>();
        s.add(prefix3);
        assertFalse(s.contains(prefix1, true));
        assertFalse(s.contains(prefix2, true));
        assertTrue(s.contains(prefix3, true));
    }

    @Test
    public void constructorWithCase_addWithoutData_containsPartialWord() {
        String prefix1 = "Bob";
        String prefix2 = "bob";
        String prefix3 = "bo";

        StorageTrie<Integer> s = new StorageTrie<>(false);
        assertEquals(0, s.getWordCount());
        assertFalse(s.contains(prefix1, false));
        assertFalse(s.contains(prefix2, false));
        assertFalse(s.contains(prefix3, false));

        s.add(prefix1);
        assertEquals(1, s.getWordCount());
        assertTrue(s.contains(prefix1, false));
        assertFalse(s.contains(prefix2, false));
        assertFalse(s.contains(prefix3, false));

        s.add(prefix2);
        assertEquals(2, s.getWordCount());
        assertTrue(s.contains(prefix1, false));
        assertTrue(s.contains(prefix2, false));
        assertTrue(s.contains(prefix3, false));

        s.add(prefix3);
        assertEquals(3, s.getWordCount());
        assertTrue(s.contains(prefix1, false));
        assertTrue(s.contains(prefix2, false));
        assertTrue(s.contains(prefix3, false));

        s = new StorageTrie<>();
        s.add(prefix3);
        assertFalse(s.contains(prefix1, false));
        assertFalse(s.contains(prefix2, false));
        assertTrue(s.contains(prefix3, false));
    }

    @Test
    public void getTermsStartingWithPrefix_defaultMax() {
        String one = "1";
        String oneTwo = "12";
        String oneTwoThree = "123";

        StorageTrie<String> s = new StorageTrie<>();
        s.add(one);
        s.add(oneTwo);
        s.add(oneTwoThree);

        Vector<String> results = s.searchPrefix("");
        assertEquals(3, results.size());
        assertTrue(results.contains(one));
        assertTrue(results.contains(oneTwo));
        assertTrue(results.contains(oneTwoThree));

        results = s.searchPrefix(one);
        assertEquals(3, results.size());
        assertTrue(results.contains(one));
        assertTrue(results.contains(oneTwo));
        assertTrue(results.contains(oneTwoThree));

        results = s.searchPrefix("2");
        assertEquals(0, results.size());

        results = s.searchPrefix(oneTwo);
        assertEquals(2, results.size());
        assertTrue(results.contains(oneTwo));
        assertTrue(results.contains(oneTwoThree));

        results = s.searchPrefix(oneTwoThree);
        assertEquals(1, results.size());
        assertTrue(results.contains(oneTwoThree));
    }

    @Test
    public void getTermsStartingWithPrefix_customMax() {
        String one = "1";
        String oneTwo = "12";
        String oneTwoThree = "123";

        StorageTrie<String> s = new StorageTrie<>();
        s.add(one);
        s.add(oneTwo);
        s.add(oneTwoThree);

        Vector<String> results = s.searchPrefix("", 1);
        assertEquals(1, results.size());
        assertTrue(results.contains(one));

        results = s.searchPrefix(one, 1);
        assertEquals(1, results.size());
        assertTrue(results.contains(one));

        results = s.searchPrefix("2", 1);
        assertEquals(0, results.size());

        results = s.searchPrefix(oneTwo, 1);
        assertEquals(1, results.size());
        assertTrue(results.contains(oneTwo));

        results = s.searchPrefix(oneTwoThree, 1);
        assertEquals(1, results.size());
        assertTrue(results.contains(oneTwoThree));
    }

    @Test
    public void defaultConstructor_addWithData_searchPrefixForData_01() {
        String one = "1";
        String oneTwo = "12";
        String oneTwoThree = "123";
        String a = "a";
        String b = "b";
        String c = "c";

        StorageTrie<String> s = new StorageTrie<>();
        s.add(one, a);
        s.add(oneTwo, b);
        s.add(oneTwoThree, c);

        Vector<String> results = s.searchPrefixForData("");
        assertEquals(3, results.size());
        assertTrue(results.contains(a));
        assertTrue(results.contains(b));
        assertTrue(results.contains(c));

        results = s.searchPrefixForData(one);
        assertEquals(3, results.size());
        assertTrue(results.contains(a));
        assertTrue(results.contains(b));
        assertTrue(results.contains(c));

        results = s.searchPrefixForData("2");
        assertEquals(0, results.size());

        results = s.searchPrefixForData(oneTwo);
        assertEquals(2, results.size());
        assertTrue(results.contains(b));
        assertTrue(results.contains(c));

        results = s.searchPrefixForData(oneTwoThree);
        assertEquals(1, results.size());
        assertTrue(results.contains(c));
    }

    @Test
    public void defaultConstructor_addWithData_searchPrefixForData_02() {
        String one = "1";
        String oneTwo = "12";
        String oneTwoThree = "123";
        String a = "a";
        String b = "b";
        String c = "c";

        StorageTrie<String> s = new StorageTrie<>();
        s.add(one, a);
        s.add(oneTwo, b);
        s.add(oneTwoThree, c);

        Vector<String> results = s.searchPrefixForData("", 1);
        assertEquals(1, results.size());
        assertTrue(results.contains(a));

        results = s.searchPrefixForData(one, 1);
        assertEquals(1, results.size());
        assertTrue(results.contains(a));

        results = s.searchPrefixForData("2", 1);
        assertEquals(0, results.size());

        results = s.searchPrefixForData(oneTwo, 1);
        assertEquals(1, results.size());
        assertTrue(results.contains(b));

        results = s.searchPrefixForData(oneTwoThree, 1);
        assertEquals(1, results.size());
        assertTrue(results.contains(c));
    }

    @Test
    public void getPrefix() {
        StorageTrie s = new StorageTrie();
        String hello = "Hello!";
        String hi = "hi";

        assertEquals(s.getPrefix(hello), hello);
        assertEquals("", s.getPrefix(""));

        int newPrefix = 3;
        s.setPrefixLength(newPrefix);
        assertEquals(newPrefix, s.getPrefixLength());
        assertEquals(s.getPrefix(hello), hello.substring(0, newPrefix));
        assertEquals(s.getPrefix(hi), hi);
        assertEquals("", s.getPrefix(""));
    }

    @Test
    public void getPrefixCount_01() {
        String one = "1";
        String oneTwo = "12";
        String oneTwoThree = "123";

        StorageTrie<String> s = new StorageTrie<>();
        s.add(one);
        s.add(oneTwo);
        s.add(oneTwoThree);

        assertEquals(3, s.getPrefixCount(""));
        assertEquals(3, s.getPrefixCount(one));
        assertEquals(0, s.getPrefixCount("2"));
        assertEquals(2, s.getPrefixCount(oneTwo));
        assertEquals(1, s.getPrefixCount(oneTwoThree));
    }

    @Test
    public void getPrefixCount_02() {
        String one = "1";
        String oneTwo = "12";
        String oneTwoThree = "123";

        StorageTrie<String> s = new StorageTrie<>();
        s.setPrefixLength(2);
        s.add(one);
        s.add(oneTwo);
        s.add(oneTwoThree);

        assertEquals(3, s.getPrefixCount(""));
        assertEquals(3, s.getPrefixCount(one));
        assertEquals(0, s.getPrefixCount("2"));
        assertEquals(2, s.getPrefixCount(oneTwo));
        assertEquals(0, s.getPrefixCount(oneTwoThree));
    }
}