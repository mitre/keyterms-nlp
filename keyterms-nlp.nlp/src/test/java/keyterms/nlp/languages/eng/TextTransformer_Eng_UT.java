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

package keyterms.nlp.languages.eng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextTransformer_Eng_UT {

    private static final TextTransformer_Eng TT_ENG = new TextTransformer_Eng();

    private static final String normalizationInput = "Earth is the third planet from the Sun and the only object in " +
            "the Universe known to harbor life.";

    @Test
    public void normalizeForDisplay() {
        /*
         * newlines removed? yep!
         * control characters removed? yep!
         * punctuation transliterated? nope!
         * normalized to NFKC? yep!
         */
        assertEquals(normalizationInput, TT_ENG.normalizeForDisplay(normalizationInput));
    }

    @Test
    public void normalizeForScoring() {
        /*
         * newlines removed? yep!
         * spaces removed? yep!
         * control characters removed? yep!
         * punctuation removed? yep!
         * diacritics removed? yep!
         * normalized to NFKD? yep!
         */
        String expected = "EarthisthethirdplanetfromtheSunandtheonlyobjectintheUniverseknowntoharborlife";
        assertEquals(expected, TT_ENG.normalizeForScoring(normalizationInput));
    }

    @Test
    public void normalizeForIndex() {
        /*
         * newlines removed? yep!
         * spaces removed? yep!
         * control characters removed? yep!
         * punctuation removed? yep!
         * punctuation normalized? doesn't matter since it's removed!
         * punctuation transliterated? doesn't matter since it's removed!
         * diacritics removed? yep!
         * case normalized? yep!
         * normalized to NFKD? yep!
         * stemmed? yep!
         */
        String expected = "earththirdplanetfromsunonliobjectuniversknownharborlife";
        assertEquals(expected, TT_ENG.normalizeForIndex(normalizationInput));
    }

    @Test
    public void transliterate() {
        assertEquals(normalizationInput, TT_ENG.transliterate(normalizationInput, null));
    }

    @Test
    public void getAvailableTransforms() {
        try {
            List<Transliteration> result = TT_ENG.getAvailableTransforms(normalizationInput, null);
            assertTrue(result.size() > 0);

            Transliteration expected = new Transliteration(
                    true, // isSrcScript
                    0, // order
                    Script.LATN.getCode(), // scriptCode
                    TextType.ORIGINAL.getDisplayLabel(), // transformType
                    TT_ENG.normalizeForDisplay(normalizationInput), // text
                    TT_ENG.normalizeForIndex(normalizationInput)); // textIndex

            assertEquals(expected.toString(), result.get(0).toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getTransliterationCandidates() {
        List<String> expected = new ArrayList<>(Collections.singleton(normalizationInput));
        assertArrayEquals(expected.toArray(), TT_ENG.getTransliterationCandidates(normalizationInput, null).toArray());
    }
}