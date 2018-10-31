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

package keyterms.nlp.languages.und;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.ibm.icu.text.Transliterator;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextTransformer_Und_UT {

    private static TextTransformer_Und ttUnd = new TextTransformer_Und();

    private static final String normalizationInput = "La Tierra es el tercer planeta del Sol y el único objeto en el " +
            "Universo conocido por albergar vida.";
    private static final String bgnTransliteration = ""; // insert BGN transliteration of normalization input here

    @Test
    public void normalizeForDisplay() {
        /*
         * newlines removed? yep!
         * control characters removed? yep!
         * punctuation transliterated? nope!
         * normalized to NFKC? yep!
         */
        assertEquals(normalizationInput, ttUnd.normalizeForDisplay(normalizationInput));
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
        String expected = "LaTierraeseltercerplanetadelSolyelunicoobjetoenelUniversoconocidoporalbergarvida";
        assertEquals(expected, ttUnd.normalizeForScoring(normalizationInput));
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

        // normalized, then stemmed, then normalized without spaces
        String expected = "latierraeseltercerplanetadelsolyelunicoobjetoeneluniversoconocidoporalbergarvida";
        assertEquals(expected, ttUnd.normalizeForIndex(normalizationInput));
    }

    @Test
    public void transliterate_KeyTerms() {
        try {
            assertEquals(normalizationInput, ttUnd.transliterate(normalizationInput, TextType.KEY_TERMS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Ignore
    @Test
    public void transliterate_bgn() {
        // NB 01/02/2018: fails due to missing BGN transliterator
        assertEquals(bgnTransliteration, ttUnd.transliterate(normalizationInput, TextType.BGN));
    }

    @Test
    public void getTransliterationCandidates() {
        String[] actual = ttUnd.getTransliterationCandidates(normalizationInput, null).toArray(new String[] {});
        String[] expected = { normalizationInput };

        assertArrayEquals(expected, actual);
    }

    @Test
    public void getAvailableTransforms() {
        try {
            List<Transliteration> result = ttUnd.getAvailableTransforms(normalizationInput, null);
            assertTrue(result.size() > 0);

            Transliteration expected = new Transliteration(
                    true, // isSrcScript
                    0, // order
                    Script.LATN.getCode(), // scriptCode
                    TextType.ORIGINAL.getDisplayLabel(), // transformType
                    ttUnd.normalizeForDisplay(normalizationInput), // text
                    ttUnd.normalizeForIndex(normalizationInput)); // textIndex

            assertEquals(expected.toString(), result.get(0).toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getAvailableTransliterations() {

        try {
            // Latin input
            List<Transliteration> result = ttUnd.getAvailableTransliterations(normalizationInput, null);
            assertNull(result);

            // Ukrainian input, no language code
            String ukrInput = "Земля - це третя планета від Сонця та єдиний об'єкт у Всесвіті, відомий для життя.";
            result = ttUnd.getAvailableTransliterations(ukrInput, null);
            assertTrue(result.size() > 0);

            Transliterator transliterator = Transliterator.getInstance("Any-Latin/BGN");
            Transliteration expected = new Transliteration(
                    false, // isSrcScript
                    1, // order
                    Script.LATN.getCode(), // scriptCode
                    TextType.BGN.getDisplayLabel(), // transformType
                    ttUnd.normalizeForDisplay(transliterator.transliterate(ukrInput)), // text
                    ttUnd.normalizeForIndex(transliterator.transliterate(ukrInput))); // textIndex

            assertEquals(expected.toString(), result.get(0).toString());

            // Ukrainian input, Ukrainian language code
            result = ttUnd.getAvailableTransliterations(ukrInput, Language.UKRANIAN);
            assertTrue(result.size() > 0);

            transliterator = Transliterator.getInstance("Ukrainian-Latin/Bgn");
            expected = new Transliteration(
                    false, // isSrcScript
                    1, // order
                    Script.LATN.getCode(), // scriptCode
                    TextType.BGN.getDisplayLabel(), // transformType
                    ttUnd.normalizeForDisplay(transliterator.transliterate(ukrInput)), // text
                    ttUnd.normalizeForIndex(transliterator.transliterate(ukrInput))); // textIndex

            assertEquals(expected.toString(), result.get(0).toString());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}