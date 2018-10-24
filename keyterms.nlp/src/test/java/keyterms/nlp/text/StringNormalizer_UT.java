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

package keyterms.nlp.text;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringNormalizer_UT {
    @Test
    public void StringNormalizer_isPunct() {
        String input = ";;;;;";
        boolean actual = StringNormalizer.isPunct(input);
        assertTrue(actual);
    }

    @Test
    public void StringNormalizer_isPunctSpacesNotAllowed() {
        String input = ";;;  ;;  ";
        boolean actual = StringNormalizer.isPunct(input);
        assertFalse(actual);
    }

    @Test
    public void StringNormalizer_isPunctSpacesAllowed() {
        String input = ";?;  ;-  ";
        boolean actual = StringNormalizer.isPunct(input, true);
        assertTrue(actual);
    }

    @Test
    public void unigrams() {
        assertEquals(Collections.emptyList(), StringNormalizer.gramify("", 1, '$'));
        assertEquals(Collections.singletonList("a"), StringNormalizer.gramify("a", 1, '$'));
        assertEquals(List.of("a", "b"), StringNormalizer.gramify("ab", 1, '$'));
        assertEquals(List.of("a", "b", "c"), StringNormalizer.gramify("abc", 1, '$'));
    }

    @Test
    public void noPadding() {
        assertEquals(Collections.emptyList(), StringNormalizer.gramify("", 1, null));
        assertEquals(Collections.singletonList("a"), StringNormalizer.gramify("a", 1, null));
        assertEquals(List.of("a", "b"), StringNormalizer.gramify("ab", 1, null));
        assertEquals(List.of("a", "b", "c"), StringNormalizer.gramify("abc", 1, null));
        // bigrams
        assertEquals(Collections.emptyList(), StringNormalizer.gramify("", 2, null));
        assertEquals(Collections.emptyList(), StringNormalizer.gramify("a", 2, null));
        assertEquals(List.of("ab"), StringNormalizer.gramify("ab", 2, null));
        assertEquals(List.of("ab", "bc"), StringNormalizer.gramify("abc", 2, null));
        // trigrams
        assertEquals(Collections.emptyList(), StringNormalizer.gramify("", 3, null));
        assertEquals(Collections.emptyList(), StringNormalizer.gramify("a", 3, null));
        assertEquals(Collections.emptyList(), StringNormalizer.gramify("ab", 3, null));
        assertEquals(List.of("abc", "bcd"), StringNormalizer.gramify("abcd", 3, null));
    }

    @Test
    public void bigramify() {
        String input = "";
        String[] expected = {};
        String[] actual = StringNormalizer.bigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);

        input = "a";
        expected = new String[] { "$a", "a$" };
        actual = StringNormalizer.bigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);

        input = "ab";
        expected = new String[] { "$a", "ab", "b$" };
        actual = StringNormalizer.bigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);

        input = "abcde";
        expected = new String[] { "$a", "ab", "bc", "cd", "de", "e$" };
        actual = StringNormalizer.bigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);
    }

    @Test
    public void trigramify() {
        String input = "";
        String[] expected = {};
        String[] actual = StringNormalizer.trigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);

        input = "a";
        expected = new String[] { "$$a", "$a$", "a$$" };
        actual = StringNormalizer.trigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);

        input = "ab";
        expected = new String[] { "$$a", "$ab", "ab$", "b$$" };
        actual = StringNormalizer.trigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);

        input = "abc";
        expected = new String[] { "$$a", "$ab", "abc", "bc$", "c$$" };
        actual = StringNormalizer.trigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);

        input = "abcde";
        expected = new String[] { "$$a", "$ab", "abc", "bcd", "cde", "de$", "e$$" };
        actual = StringNormalizer.trigramify(input, '$').toArray(new String[] {});
        assertArrayEquals(expected, actual);
    }
}