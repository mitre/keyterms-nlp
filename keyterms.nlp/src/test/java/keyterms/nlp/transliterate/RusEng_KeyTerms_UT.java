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

package keyterms.nlp.transliterate;

import java.nio.file.Path;

import org.junit.BeforeClass;
import org.junit.Test;

import keyterms.testing.TestFiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RusEng_KeyTerms_UT {

    private static Transliterator myTransliterator;

    @BeforeClass
    public static void init()
            throws Exception {
        Path rulePath = TestFiles.getFilePath(Transliterators_UT.class, "rsc/rus-eng.KeyTerms.rules");
        String rules = Transliterators.loadIcuRules(rulePath);
        myTransliterator = new IcuTransliterator("rus-eng/KeyTerms", rules);
    }

    @Test
    public void transliteratorNotNull() {
        assertNotNull(myTransliterator);
    }

    @Test
    public void ruseng_alphabet_lower_nodigraphs() {
        String input = "№абвгѓғҕґдђеэєәзҙѕийіјкќқлљмнњопрстћуўФџъьыѣѧѫѯѵ";
        String expected = "No.abvggggg̀ddeeeezzziйijkḱkllmnnoprstcuŭfdyěẽõxẏ";
        String actual = myTransliterator.apply(input);
        assertEquals(expected, actual);
    }

    @Test
    public void ruseng_alphabet_upper_nodigraphs() {
        String input = "АБВГЃҒҔҐДЂЕЭЄӘЗҘЅИЙІЈКЌҚЛЉМНЊОПРСТЋУЎфЏЪЬЫѢѦѪѮѴ";
        String expected = "ABVGGGGG̀DDEEEEZZZIЙIJKḰKLLMNNOPRSTCUŬFDYĚẼÕXẎ";
        String actual = myTransliterator.apply(input);
        assertEquals(expected, actual);
    }

    @Test
    public void ruseng_alphabet_lower_digraphs() {
        String input = "шщжю";
        String expected = "shshchzhyu";
        String actual = myTransliterator.apply(input);
        assertEquals(expected, actual);
    }

    @Test
    public void ruseng_alphabet_upper_digraphs() {
        String input = "ШЩЖЮ";
        String expected = "SHSHCHZHYU";
        String actual = myTransliterator.apply(input);
        assertEquals(expected, actual);
    }

    @Test
    public void ruseng_alphabet_upper_digraphs_beforeLower() {
        String input = "ШаЩаЖаЮа";
        String expected = "ShaShchaZhaYua";
        String actual = myTransliterator.apply(input);
        assertEquals(expected, actual);
    }
}