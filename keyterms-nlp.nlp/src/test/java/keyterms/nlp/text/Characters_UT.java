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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Characters_UT {

    private static final char[] PUNCTUATION = {
            '_', // '\u005f' CONNECTOR_PUNCTUATION
            '$', // '\u0024' CURRENCY_SYMBOL
            '-', // '\u002D' DASH_PUNCTUATION
            '⃝', // '\u20DD' ENCLOSING_MARK
            ')', // '\u0029' END_PUNCTUATION
            '»', // '\u00BB' FINAL_QUOTE_PUNCTUATION
            '«', // '\u00AB' INITIAL_QUOTE_PUNCTUATION
            '+', // '\u002B' MATH_SYMBOL
            '¦', // '\u00A6' OTHER_SYMBOL
            '!', // '\u0021' OTHER_PUNCTUATION
            '(',  // '\u0028' START_PUNCTUATION
            '！', // '\uFF01' HALFWIDTH_AND_FULLWIDTH_FORMS
            '／', // '\uFF0F' HALFWIDTH_AND_FULLWIDTH_FORMS
            '：', // '\uFF1A' HALFWIDTH_AND_FULLWIDTH_FORMS
            '＠', // '\uFF20' HALFWIDTH_AND_FULLWIDTH_FORMS
            '［', // '\uFF3B' HALFWIDTH_AND_FULLWIDTH_FORMS
            '｀' // '\uFF40' HALFWIDTH_AND_FULLWIDTH_FORMS
    };

    private static final char[] NON_PRINTING = {
            '\u0000', // CONTROL
            '\u0020', // SPACE_SEPARATOR
            '\u2028'  // LINE_SEPARATOR
            // DIRECTIONALITY_PARAGRAPH_SEPARATOR ?
    };

    private static final char[] DIACRITIC = {
            'á', // '\u00E1' LATIN SMALL LETTER A WITH ACUTE
            'ö', // '\u00F6' LATIN SMALL LETTER O WITH DIAERESIS
            'ः', // '\u0903' COMBINING_SPACING_MARK
            '⃝', // '\u20DD' ENCLOSING_MARK
            '̀'  // '\u0300' NON_SPACING_MARK
    };

    private static final char[] ARABIC_BMP = {
            '؀', // '\u0600' ARABIC
            '۝', // '\u06DD' ARABIC
            'ݒ', // '\u0752' ARABIC_SUPPLEMENT
            'ݾ', // '\u077E' ARABIC_SUPPLEMENT
            '\u08A2', // ARABIC_EXTENDED_A
            '\u08F9', // ARABIC_EXTENDED_
            'ﭖ', // '\uFB56' ARABIC_PRESENTATION_FORMS_A
            'ﶫ', // '\uFDAB' ARABIC_PRESENTATION_FORMS_A
            'ﹱ', // '\uFE71' ARABIC_PRESENTATION_FORMS_B
            'ﻵ' // '\uFEF5' ARABIC_PRESENTATION_FORMS_B
    };

    private static final String[] ARABIC_SUPPLEMENTAL = {
            new String(Character.toChars(0x1EE00)), // ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS
            new String(Character.toChars(0x1EEB4)), // ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS
            new String(Character.toChars(0x10E60)), // RUMI_NUMERAL_SYMBOLS
            new String(Character.toChars(0x10E7A))  // RUMI_NUMERAL_SYMBOLS
    };

    private static final char[] CJK_BMP = {
            '一', // '\u4E00' CJK_UNIFIED_IDEOGRAPHS
            '隗', // '\u9697' CJK_UNIFIED_IDEOGRAPHS
            '㐀', // '\u3400' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            '䌍', // '\u430D' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            '豈', // '\uF900' CJK_COMPATIBILITY_IDEOGRAPHS
            '刺', // '\uF9FF' CJK_COMPATIBILITY_IDEOGRAPHS
            '㌀', // '\u3300' CJK_COMPATIBILITY
            '㏝', // '\u33DD' CJK_COMPATIBILITY
            '︰', // '\uFE30' CJK_COMPATIBILITY_FORMS
            '﹇', // '\uFE47' CJK_COMPATIBILITY_FORMS
            '〈', // '\u3008' CJK_SYMBOLS_AND_PUNCTUATION
            '〮', // '\u302E' CJK_SYMBOLS_AND_PUNCTUATION
            '㇀', // '\u31C0' CJK_STROKES
            '㇏', // '\u31CF' CJK_STROKES
            '⺀', // '\u2E80' CJK_RADICALS_SUPPLEMENT
            '⻨', // '\u2EE8' CJK_RADICALS_SUPPLEMENT
            '㈠', // '\u3220' ENCLOSED_CJK_LETTERS_AND_MONTHS
            '㋋' // ENCLOSED_CJK_LETTERS_AND_MONTHS
    };

    private static final String[] CJK_SUPPLEMENTAL = {
            "𠀋", // '\u2000B' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            "𠁎", // '\u2004E' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            "𪾢", // '\u2AFA2' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
            "𫘨", // '\u2B628' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
            "𫝆", // '\u2B746' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
            "𫠚", // '\u2B81A' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
            "你", // '\u2F804' CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
            "軔"  // '\u2F9DE' CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
    };

    private static final char[] CJK_LOGOGRAPH_BMP = { // cjk + ideographic
            '一', // '\u4E00' CJK_UNIFIED_IDEOGRAPHS
            '隗', // '\u9697' CJK_UNIFIED_IDEOGRAPHS
            '㐀', // '\u3400' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            '䌍', // '\u430D' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            '豈', // '\uF900' CJK_COMPATIBILITY_IDEOGRAPHS
            '刺', // '\uF9FF' CJK_COMPATIBILITY_IDEOGRAPHS
    };

    private static final String[] CJK_LOGOGRAPH_SUPPLEMENTAL = { // cjk + ideographic
            "𠀋", // '\u2000B' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            "𠁎", // '\u2004E' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            "𪾢", // '\u2AFA2' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
            "𫘨", // '\u2B628' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
            "𫝆", // '\u2B746' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
            "𫠚", // '\u2B81A' CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
            "你", // '\u2F804' CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
            "軔"  // '\u2F9DE' CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
    };

    private static final char[] CJK_PUNCTUATION = {
            '︰', // '\uFE30' CJK_COMPATIBILITY_FORMS
            '﹇', // '\uFE47' CJK_COMPATIBILITY_FORMS
    };

    private static final char[] LATIN_BMP = {
            'A', // '\u0041' BASIC_LATIN
            '\u0080', // LATIN_1_SUPPLEMENT
            'Ā', // '\u0100' LATIN_EXTENDED_A
            'ƀ', // '\u0180' LATIN_EXTENDED_B
            'Ⱡ', // '\u2C60' LATIN_EXTENDED_C
            '꜠', // '\uA720' LATIN_EXTENDED_D
            'Ḁ', // '\u1E00' LATIN_EXTENDED_ADDITIONAL
            '⅐', // '\u2150' NUMBER_FORMS
            'ₓ', // '\u2093' SUPERSCRIPTS_AND_SUBSCRIPTS
            'ⓙ', // '\u24D9' ENCLOSED_ALPHANUMERICS
    };

    private static final String[] LATIN_SUPPLEMENTAL = {
            "🄹", // '\u1F139' ENCLOSED_ALPHANUMERIC_SUPPLEMENT
            "𝓾", // '\u1D4FE' MATHEMATICAL_ALPHANUMERIC_SYMBOLS
            "𝙹", // '\u1D679' MATHEMATICAL_ALPHANUMERIC_SYMBOLS
    };

    private static final char[] LATIN_LETTER = { // letter + latin_bmp
            'A', // '\u0041' UPPERCASE_LETTER
            'a', // '\u0061' LOWERCASE_LETTER
            'ǅ', // '\u01C5' TITLECASE_LETTER
            'ƻ'  // '\u01BB' OTHER_LETTER
            // no Latin letters (according to Unicode standards) within Modifier Letter Block
    };

    private static final char[] LATIN_DIGIT = { // digit + latin_bmp
            '0', // '\u0030'
            '5', // '\u0035'
            '9'  // '\u0039'
    };

    @Test
    public void punctuation_chars() {
        for (char c : PUNCTUATION) {
            assertTrue(String.valueOf(c), Characters.isPunctuation(c));
        }
    }

    @Test
    public void punctuation_strings() {
        for (char c : PUNCTUATION) {
            assertTrue(String.valueOf(c), Characters.isPunctuation(String.valueOf(c)));
        }
    }

    @Test
    public void nonPrinting_chars() {
        for (char c : NON_PRINTING) {
            assertTrue(String.valueOf(c), Characters.isNonPrinting(c));
        }
    }

    @Test
    public void nonPrinting_strings() {
        for (char c : NON_PRINTING) {
            assertTrue(String.valueOf(c), Characters.isNonPrinting(String.valueOf(c)));
        }
    }

    @Test
    public void diacritic_chars() {
        for (char c : DIACRITIC) {
            assertTrue(String.valueOf(c), Characters.isDiacritic(c));
        }
    }

    @Test
    public void diacritic_strings() {
        for (char c : DIACRITIC) {
            assertTrue(String.valueOf(c), Characters.isDiacritic(String.valueOf(c)));
        }
    }

    @Test
    public void arabic_bmp_chars() {
        for (char c : ARABIC_BMP) {
            assertTrue(String.valueOf(c), Characters.isArabic(c));
        }
    }

    @Test
    public void arabic_bmp_strings() {
        for (char c : ARABIC_BMP) {
            assertTrue(String.valueOf(c), Characters.isArabic(String.valueOf(c)));
        }
    }

    @Test
    public void arabic_supplemental() {
        for (String s : ARABIC_SUPPLEMENTAL) {
            assertTrue(s, Characters.isArabic(s));
        }
    }

    @Test
    public void cjk_bmp_chars() {
        for (char c : CJK_BMP) {
            assertTrue(String.valueOf(c), Characters.isCJK(c));
        }
    }

    @Test
    public void cjk_bmp_strings() {
        for (char c : CJK_BMP) {
            assertTrue(String.valueOf(c), Characters.isCJK(String.valueOf(c)));
        }
    }

    @Test
    public void cjk_supplemental() {
        for (String s : CJK_SUPPLEMENTAL) {
            assertTrue(s, Characters.isCJK(s));
        }
    }

    @Test
    public void cjkLogograph_bmp_chars() {
        for (char c : CJK_LOGOGRAPH_BMP) {
            assertTrue(String.valueOf(c), Characters.isCJKLogograph(c));
        }
    }

    @Test
    public void cjkLogograph_bmp_strings() {
        for (char c : CJK_LOGOGRAPH_BMP) {
            assertTrue(String.valueOf(c), Characters.isCJKLogograph(String.valueOf(c)));
        }
    }

    @Test
    public void cjkLogograph_bmp_supplemental() {
        for (String s : CJK_LOGOGRAPH_SUPPLEMENTAL) {
            assertTrue(s, Characters.isCJKLogograph(s));
        }
    }

    @Test
    public void cjkPunctuation_chars() {
        for (char c : CJK_PUNCTUATION) {
            assertTrue(String.valueOf(c), Characters.isCJKPunctuation(c));
        }
    }

    @Test
    public void cjkPunctuation_strings() {
        for (char c : CJK_PUNCTUATION) {
            assertTrue(String.valueOf(c), Characters.isCJKPunctuation(String.valueOf(c)));
        }
    }

    @Test
    public void latin_bmp_chars() {
        for (char c : LATIN_BMP) {
            assertTrue(String.valueOf(c), Characters.isLatin(c));
        }
    }

    @Test
    public void latin_bmp_strings() {
        for (char c : LATIN_BMP) {
            assertTrue(String.valueOf(c), Characters.isLatin(String.valueOf(c)));
        }
    }

    @Test
    public void latin_supplemental() {
        for (String s : LATIN_SUPPLEMENTAL) {
            assertTrue(s, Characters.isLatin(s));
        }
    }

    @Test
    public void latinLetter_chars() {
        for (char c : LATIN_LETTER) {
            assertTrue(String.valueOf(c), Characters.isLatinLetter(c));
        }
    }

    @Test
    public void latinLetter_strings() {
        for (char c : LATIN_LETTER) {
            assertTrue(String.valueOf(c), Characters.isLatinLetter(Character.toString(c)));
        }
    }

    @Test
    public void latinDigit_chars() {
        for (char c : LATIN_DIGIT) {
            assertTrue(String.valueOf(c), Characters.isLatinDigit(c));
        }
    }

    @Test
    public void latinDigit_strings() {
        for (char c : LATIN_DIGIT) {
            assertTrue(String.valueOf(c), Characters.isLatinDigit(Character.toString(c)));
        }
    }

    @Test
    public void isLatinDigit() {
        // Check for prior NPE in character array based method.
        String text = "5";
        int codePoint = text.codePointAt(0);
        char[] chars = Character.toChars(codePoint);
        assertTrue(Characters.isLatinDigit(chars));
        text = "Z";
        codePoint = text.codePointAt(0);
        chars = Character.toChars(codePoint);
        assertFalse(Characters.isLatinDigit(chars));
    }

    @Test
    public void isZwj_char() {
        char zwj = '\u200D';
        char nonZwj = '\u0192';
        assertTrue(String.valueOf(zwj), Characters.isZWJ(zwj));
        assertFalse(String.valueOf(nonZwj), Characters.isZWJ(nonZwj));
    }

    @Test
    public void isZwj_string() {
        String zwj = String.valueOf('\u200D');
        String nonZwj = String.valueOf('\u0192');
        assertTrue(zwj, Characters.isZWJ(zwj));
        assertFalse(zwj, Characters.isZWJ(nonZwj));
    }

    @Test
    public void isZwj_chArray() {
        String zwj = String.valueOf('\u200D');
        String nonZwj_1 = String.valueOf('\u0192');
        String nonZwj_2 = "not ZWJ";
        assertTrue(zwj, Characters.isZWJ(zwj));
        assertFalse(nonZwj_1, Characters.isZWJ(nonZwj_1));
        assertFalse(nonZwj_2, Characters.isZWJ(nonZwj_2));
    }
}