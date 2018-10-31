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

package keyterms.nlp.languages.ukr;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextTransformer_Ukr_UT {

    private static TextTransformer_Ukr ttUkr = new TextTransformer_Ukr();

    private static final String normalizationInput = "Земля - це третя планета від Сонця та єдиний об'єкт у " +
            "Всесвіті, відомий для життя.";
    private static final String keyTermsTransliteration = "Zeemlya - tsee treetya planeeta vid Sontsya ta yedynyй " +
            "obyekt u Vseesviti, vidomyй dlya zhyttya.";
    private static final String bgnTransliteration = ""; // insert BGN transliteration of normalization input here

    @Test
    public void normalizeForDisplay() {
        /*
         * newlines removed? yep!
         * control characters removed? yep!
         * punctuation transliterated? nope!
         * normalized to NFKC? yep!
         */
        assertEquals(normalizationInput, ttUkr.normalizeForDisplay(normalizationInput));
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
        String expected = "ЗемляцетретяпланетавідСонцятаєдинииобєктуВсесвітівідомиидляжиття";
        assertEquals(expected, ttUkr.normalizeForScoring(normalizationInput));
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
        String expected = "земляцетретяпланетавідсонцятаєдиниобєктувсесвітвідомидляжиття";
        assertEquals(expected, ttUkr.normalizeForIndex(normalizationInput));
    }

    @Test
    public void transliterate_KeyTerms() {
        try {
            assertEquals(keyTermsTransliteration, ttUkr.transliterate(normalizationInput, TextType.KEY_TERMS));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void transliterateToKeyTerms() {
        try {
            assertEquals(keyTermsTransliteration, ttUkr.transliterateToKeyTerms(normalizationInput));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Ignore
    @Test
    public void transliterate_bgn() {
        // NB 01/02/2018: fails due to missing BGN transliterator
        assertEquals(bgnTransliteration, ttUkr.transliterate(normalizationInput, TextType.BGN));
    }

    @Ignore
    @Test
    public void transliterateToBgn() {
        // NB 01/02/2018: fails due to missing BGN transliterator
        assertEquals(bgnTransliteration, ttUkr.transliterateToBgn(normalizationInput));
    }

    @Test
    public void getAvailableTransforms() {
        try {
            List<Transliteration> result = ttUkr.getAvailableTransforms(normalizationInput, null);
            assertTrue(result.size() > 0);

            Transliteration expected = new Transliteration(
                    true, // isSrcScript
                    0, // order
                    Script.CYRL.getCode(), // scriptCode
                    TextType.ORIGINAL.getDisplayLabel(), // transformType
                    ttUkr.normalizeForDisplay(normalizationInput), // text
                    ttUkr.normalizeForIndex(normalizationInput)); // textIndex

            assertEquals(expected.toString(), result.get(0).toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}