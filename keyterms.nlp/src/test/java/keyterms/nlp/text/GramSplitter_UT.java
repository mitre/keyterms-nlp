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

import static org.junit.Assert.assertEquals;

public class GramSplitter_UT {

    @Test
    public void unigrams() {
        GramSplitter grams = new GramSplitter(1, '$');
        assertEquals(List.of(), grams.split(""));
        assertEquals(List.of("a"), grams.split("a"));
        assertEquals(List.of("a", "b"), grams.split("ab"));
        assertEquals(List.of("a", "b", "c"), grams.split("abc"));
    }

    @Test
    public void bigrams() {
        GramSplitter grams = new GramSplitter(2, '$');
        assertEquals(List.of(), grams.split(""));
        assertEquals(List.of("$a", "a$"), grams.split("a"));
        assertEquals(List.of("$a", "ab", "b$"), grams.split("ab"));
        assertEquals(List.of("$a", "ab", "bc", "cd", "de", "e$"), grams.split("abcde"));
    }

    @Test
    public void trigrams() {
        GramSplitter grams = new GramSplitter(3, '$');
        assertEquals(List.of(), grams.split(""));
        assertEquals(List.of("$$a", "$a$", "a$$"), grams.split("a"));
        assertEquals(List.of("$$a", "$ab", "ab$", "b$$"), grams.split("ab"));
        assertEquals(List.of("$$a", "$ab", "abc", "bc$", "c$$"), grams.split("abc"));
        assertEquals(List.of("$$a", "$ab", "abc", "bcd", "cde", "de$", "e$$"), grams.split("abcde"));
    }

    @Test
    public void noPadding() {
        GramSplitter grams = new GramSplitter(1);
        assertEquals(List.of(), grams.split(""));
        assertEquals(Collections.singletonList("a"), grams.split("a"));
        assertEquals(List.of("a", "b"), grams.split("ab"));
        assertEquals(List.of("a", "b", "c"), grams.split("abc"));
        grams = new GramSplitter(2);
        assertEquals(List.of(), grams.split(""));
        assertEquals(List.of(), grams.split("a"));
        assertEquals(List.of("ab"), grams.split("ab"));
        assertEquals(List.of("ab", "bc"), grams.split("abc"));
        grams = new GramSplitter(3);
        assertEquals(List.of(), grams.split(""));
        assertEquals(List.of(), grams.split("a"));
        assertEquals(List.of(), grams.split("ab"));
        assertEquals(List.of("abc", "bcd"), grams.split("abcd"));
        assertEquals(List.of("abc", "bcd", "cde"), grams.split("abcde"));
    }

}