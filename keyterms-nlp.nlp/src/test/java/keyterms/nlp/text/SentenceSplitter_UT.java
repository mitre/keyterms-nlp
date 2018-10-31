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

import keyterms.nlp.iso.WrittenLanguage;
import keyterms.testing.TestData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SentenceSplitter_UT {

    @Test
    public void noSentences() {
        assertEquals(Collections.emptyList(), new SentenceSplitter(WrittenLanguage.ENGLISH).split(null));
        assertEquals(Collections.emptyList(), new SentenceSplitter(WrittenLanguage.ENGLISH).split(""));
    }

    private void testSplitter(String text, WrittenLanguage written, int expectedSentences) {
        List<String> sentences = new SentenceSplitter(written).split(text);
        assertNotNull(sentences);
        assertEquals(expectedSentences, sentences.size());
        StringBuilder merged = new StringBuilder();
        sentences.forEach(merged::append);
        assertEquals(text, merged.toString());
    }

    @Test
    public void onlyWhiteSpace() {
        testSplitter("    ", WrittenLanguage.ENGLISH, 1);
    }

    @Test
    public void sentenceFragment() {
        testSplitter("Hello world I am a partially complete sentence", WrittenLanguage.ENGLISH, 1);
    }

    @Test
    public void singleSentence() {
        testSplitter("Hello world, I am a single sentence. ", WrittenLanguage.ENGLISH, 1);
    }

    @Test
    public void multipleSentences() {
        testSplitter("Hello World! I am multiple sentences.  I even have correct punctuation.", WrittenLanguage.ENGLISH,
                3);
    }

    @Test
    public void loremIpsum() {
        testSplitter(TestData.LOREM_IPSUM, WrittenLanguage.valueOf("latin", "latn"), 4);
    }

    @Test
    public void largeLoremIpsum() {
        testSplitter(TestData.LOREM_IPSUM_LARGE, WrittenLanguage.valueOf("latin", "latn"), 128);
    }
}