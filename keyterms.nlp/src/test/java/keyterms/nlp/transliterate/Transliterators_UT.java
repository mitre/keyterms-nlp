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
import java.util.Set;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;

import keyterms.nlp.transliterate.zho.ToneFormat;
import keyterms.testing.TestFiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class Transliterators_UT {

    @BeforeClass
    public static void loadTransliterators() {
        Transliterators.loadIcuBuiltIns();
        Transliterators.loadCustomRules();
        Transliterators.loadCustomTransliterators();
    }

    @Test
    public void builtIns() {
        assertNotEquals(0, Transliterators.BUILT_IN.size());
    }

    @Test
    public void custom() {
        assertNotEquals(0, Transliterators.CUSTOM.size());
    }

    @Test
    public void fullSet() {
        assertEquals(Transliterators.BUILT_IN.size() + Transliterators.CUSTOM.size(),
                Transliterators.getTransformKeys().size());
    }

    @Test
    public void loadWithImport()
            throws Exception {
        Path testFile = TestFiles.getResourcePath(getClass(), "rules");
        String contents = Transliterators.loadIcuRules(testFile);
        String expected = "common rule\n"
                + "rule 1\n"
                + "rule 2\n"
                + "rule 3\n";
        assertEquals(expected, contents);
    }

    @Test
    public void getKeys() {
        Set<TransformKey> keys = Transliterators.getTransformKeys();
        assertNotNull(keys);
        assertNotEquals(0, keys.size());
    }

    @Test
    public void customRules() {
        Set<TransformKey> keys = Transliterators.getTransformKeys("rus", "latn", "Acronym");
        assertNotNull(keys);
        assertEquals(1, keys.size());
    }

    @Test
    public void customClasses() {
        Set<TransformKey> keys = Transliterators.getTransformKeys("zho", "eng", "HanyuPinyin");
        assertNotNull(keys);
        assertEquals(3, keys.size());
        keys = Transliterators.getTransformKeys("zho", "eng", "WadeGiles");
        assertNotNull(keys);
        assertEquals(3, keys.size());
        Stream.of(ToneFormat.values()).forEach((toneFormat) -> {
            Set<TransformKey> toneKeys = Transliterators.getTransformKeys("zho", "eng", toneFormat.getLabel());
            assertNotNull(toneFormat.getLabel(), toneKeys);
            assertEquals(toneFormat.getLabel(), 2, toneKeys.size());
        });
    }
}