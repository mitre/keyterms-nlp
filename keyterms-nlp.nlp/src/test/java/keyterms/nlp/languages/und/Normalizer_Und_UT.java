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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Normalizer_Und_UT {

    private static final Normalizer_Und NORM_UND = new Normalizer_Und();

    private static final String nl = System.lineSeparator();
    private static final String normFormTest = "ẛ̣";
    private static final String normFormTest_nfkd = "\u0073\u0323\u0307";
    private static final String normFormTest_nfkc = "ṩ";
    private static final String normForm_noDiacritic = "s";
    private static final String controlChar = Character.toString('\u0001');
    private static final String latPunct = ",";
    private static final String zhoPunct = "、";
    private static final String diacriticChar = "à";
    private static final String nonDiacriticChar = "a";

    private static final String input = "Control character: '" + controlChar + "',"
            + nl + "Latin punctuation: '" + latPunct + "',"
            + nl + "Chinese punctuation: '" + zhoPunct + "',"
            + nl + "Diacritic character: '" + diacriticChar + "'"
            + nl + "Normalization form test: '" + normFormTest + "'";

    @Test
    public void normalizeForIndex() {
        /*
         * newlines removed? yep!
         * spaces removed? yep! (if applicable)
         * control characters removed? yep!
         * punctuation removed? yep!
         * punctuation normalized? doesn't matter since it's removed!
         * punctuation transliterated? doesn't matter since it's removed!
         * diacritics removed? yep!
         * case normalized? yep!
         * normalized to NFKD? yep!
         */
        String expected_spaces = "control character "
                + "latin punctuation "
                + "chinese punctuation "
                + "diacritic character " + nonDiacriticChar
                + "normalization form test " + normForm_noDiacritic;
        assertEquals(expected_spaces, NORM_UND.normalizeForIndex(input, false));

        String expected_noSpaces = "controlcharacter"
                + "latinpunctuation"
                + "chinesepunctuation"
                + "diacriticcharacter" + nonDiacriticChar
                + "normalizationformtest" + normForm_noDiacritic;
        assertEquals(expected_noSpaces, NORM_UND.normalizeForIndex(input, true));
    }

    @Test
    public void normalizerForScoring() {
        /*
         * newlines removed? yep!
         * spaces removed? yep!
         * control characters removed? yep!
         * punctuation removed? yep!
         * diacritics removed? yep!
         * normalized to NFKD? yep!
         */
        String expected = "Control character "
                + "Latin punctuation "
                + "Chinese punctuation "
                + "Diacritic character " + nonDiacriticChar
                + "Normalization form test " + normForm_noDiacritic; // diacritics stripped

        assertEquals(expected, NORM_UND.normalizeForScoring(input));
    }

    @Test
    public void normalizeForDisplay() {
        /*
         * newlines removed? yep!
         * control characters removed? yep!
         * punctuation transliterated? nope!
         * normalized to NFKC? yep!
         */
        String expected = "Control character: '',"
                + "Latin punctuation: '" + latPunct + "',"
                + "Chinese punctuation: '" + zhoPunct + "',"
                + "Diacritic character: '" + diacriticChar + "'"
                + "Normalization form test: '" + normFormTest_nfkc + "'";

        assertEquals(expected, NORM_UND.normalizeForDisplay(input));
    }
}