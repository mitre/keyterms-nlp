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

package keyterms.nlp.languages.zho;

import java.util.List;

import org.junit.Test;

import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextTransformer_Zho_UT {

    private static TextTransformer_Zho ttZho = new TextTransformer_Zho();
    private static final String normalizationInput = "地球是来自太阳的第三颗行星， 也是宇宙中唯一被称为拥有生命的物体。";

    @Test
    public void normalizeForDisplay_01() {
        /*
         * newlines removed? yep!
         * spaces removed? nope!
         * control characters removed? yep!
         * punctuation removed? nope!
         * punctuation normalized? nope!
         * punctuation transliterated? nope!
         * case normalized? nope!
         * normalized to NFKC? yep!
         */
        String expected = "地球是来自太阳的第三颗行星, 也是宇宙中唯一被称为拥有生命的物体。";
        assertEquals(expected, ttZho.normalizeForDisplay(normalizationInput));
    }

    @Test
    public void normalizeForDisplay_02() {
        /*
         * newlines removed? yep!
         * spaces removed? nope!
         * control characters removed? yep!
         * punctuation removed? nope!
         * punctuation normalized? (parameterized, but doesn't matter)
         * punctuation transliterated? (parameterized, but doesn't matter)
         * case normalized? nope!
         * normalized to NFKC? yep!
         */
        String expected = "地球是来自太阳的第三颗行星, 也是宇宙中唯一被称为拥有生命的物体。";
        assertEquals(expected, TextTransformer_Zho.normalizeForDisplay(normalizationInput, true, true));
    }

    @Test
    public void normForDisplay() {
        // NOTE: same as normalizeForDisplay_01()

        /*
         * newlines removed? yep!
         * spaces removed? nope!
         * control characters removed? yep!
         * punctuation removed? nope!
         * punctuation normalized? nope!
         * punctuation transliterated? nope!
         * case normalized? nope!
         * normalized to NFKC? yep!
         */
        String expected = "地球是来自太阳的第三颗行星, 也是宇宙中唯一被称为拥有生命的物体。";
        assertEquals(expected, TextTransformer_Zho.normForDisplay(normalizationInput));
    }

    @Test
    public void normalizeForScoring() {
        /*
         * newlines removed? yep!
         * spaces removed? nope!
         * control characters removed? yep!
         * punctuation removed? nope!
         * punctuation normalized? nope!
         * punctuation transliterated? nope!
         * case normalized? nope!
         * normalized to NFKD? yep!
         */
        String expected = "地球是来自太阳的第三颗行星, 也是宇宙中唯一被称为拥有生命的物体。";
        assertEquals(expected, ttZho.normalizeForScoring(normalizationInput));
    }

    @Test
    public void normalizeForIndex_01() {
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
        String expected = "地球是来自太阳的第三颗行星也是宇宙中唯一被称为拥有生命的物体";
        assertEquals(expected, ttZho.normalizeForIndex(normalizationInput));
    }

    @Test
    public void normalizeForIndex_02() {
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
        String expected = "地球是来自太阳的第三颗行星也是宇宙中唯一被称为拥有生命的物体";
        assertEquals(expected, ttZho.normalizeForIndex(normalizationInput, true));
        assertEquals(expected, ttZho.normalizeForIndex(normalizationInput, false));
    }

    @Test
    public void normForIndex() {
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
        String expected = "地球是来自太阳的第三颗行星也是宇宙中唯一被称为拥有生命的物体";
        assertEquals(expected, TextTransformer_Zho.normForIndex(normalizationInput, true));

        expected = "地球是来自太阳的第三颗行星 也是宇宙中唯一被称为拥有生命的物体";
        assertEquals(expected, TextTransformer_Zho.normForIndex(normalizationInput, false));
    }

    @Test
    public void transliterate_simplified() {
        // with explicit standard
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng, yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_SIMPLIFIED));
    }

    @Test
    public void transliterate_normalizedSimplified() {
        // with explicit standard
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng, yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_NORMALIZED_SIMPLIFIED));
    }

    @Test
    public void transliterate_traditional() {
        // with explicit standard
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng, yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_TRADITIONAL));
    }

    @Test
    public void transliterate_normalizedTraditional() {
        // with explicit standard
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng, yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_NORMALIZED_TRADITIONAL));
    }

    @Test
    public void transliterate_pinyin() {
        // with explicit standard
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng, yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_PINYIN));
    }

    @Test
    public void transliterate_pinyinNoTone() {
        // with explicit standard
        String expected = "di qiu shi lai zi tai yang de di san ke xing xing, ye shi yu zhou zhong wei yi bei cheng " +
                "wei yong you sheng ming de wu ti。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_PINYIN_NOTONE));
    }

    @Test
    public void transliterate_pinyinNumeric() {
        // with explicit standard
        String expected = "di4 qiu2 shi4 lai2 zi4 tai4 yang2 de5 di4 san1 ke1 xing2 xing1, ye3 shi4 yu3 zhou4 zhong1 " +
                "wei2 yi1 bei4 cheng1 wei2 yong1 you3 sheng1 ming4 de5 wu4 ti3。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_PINYIN_NUMERIC));
    }

    @Test
    public void transliterate_pinyinIndex() {
        // with explicit standard
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng, yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_PINYIN_INDEX));
    }

    @Test
    public void transliterate_wadeGiles() {
        // with explicit standard
        String expected = "ti4 ch`iu2 shih4 lai2 tzu4 t`ai4 yang2 te5 ti4 san1 k`o1 hsing2 hsing1, yeh3 shih4 yü3 " +
                "chou4 chung1 wei2 i1 pei4 ch`eng1 wei2 yung1 yu3 sheng1 ming4 te5 wu4 t`i3。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_WADEGILES));
    }

    @Test
    public void transliterate_wadeGiles_noTone() {
        // with explicit standard
        String expected = "ti ch`iu shih lai tzu t`ai yang te ti san k`o hsing hsing, yeh shih yü chou chung wei i " +
                "pei ch`eng wei yung yu sheng ming te wu t`i。";
        assertEquals(expected, ttZho.transliterate(normalizationInput, TextType.ZHO_WADEGILES_NOTONE));
    }

    @Test
    public void transliterate_default() {
        // no standard (single param)
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng， yĕ shì yŭ zhòu zhōng wéi yī bèi " +
                "chēng wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, ttZho.transliterate(normalizationInput));
    }

    @Test
    public void translit() {
        // with standard; should return same result as transliterate_01()
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng, yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, TextTransformer_Zho.translit(normalizationInput, TextType.ZHO_PINYIN));
    }

    @Test
    public void getAvailableTransforms() {
        try {
            List<Transliteration> result = ttZho.getAvailableTransforms(normalizationInput, null);
            assertTrue(result.size() > 0);

            Transliteration expected = new Transliteration(
                    true, // isSrcScript
                    0, // order
                    Script.HANI.getCode(), // scriptCode
                    TextType.ORIGINAL.getDisplayLabel(), // transformType
                    ttZho.normalizeForDisplay(normalizationInput), // text
                    ttZho.normalizeForIndex(normalizationInput)); // textIndex

            assertEquals(expected.toString(), result.get(0).toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void convertToPinyinForIndex() {
        /*
         * - converted to Pinyin
         * - tones removed
         * - "squinched," i.e. spaces collapsed
         */
        String expected = "di qiu shi lai zi tai yang de di san ke xing xing, ye shi yu zhou zhong wei yi bei cheng " +
                "wei yong you sheng ming de wu ti。";
        assertEquals(expected, TextTransformer_Zho.convertToPinyinForIndex(normalizationInput));
    }

    @Test
    public void convertToWadeGilesForIndex() {
        String expected = "ti ch`iu shih lai tzu t`ai yang te ti san k`o hsing hsing, yeh shih yu chou chung wei i " +
                "pei ch`eng wei yung yu sheng ming te wu t`i。";
        assertEquals(expected, ttZho.convertToWadeGilesForIndex(normalizationInput));
    }

    @Test
    public void removeTone() {
        // pinyin
        assertEquals("di", TextTransformer_Zho.removeTone("dì"));
        assertEquals("qiu", TextTransformer_Zho.removeTone("qiú"));
        assertEquals("san", TextTransformer_Zho.removeTone("sān"));
        assertEquals("ti", TextTransformer_Zho.removeTone("tĭ"));
        assertEquals("ti。", TextTransformer_Zho.removeTone("tĭ。"));

        // pinyin numeric
        assertEquals("di", TextTransformer_Zho.removeTone("di4"));
        assertEquals("qiu", TextTransformer_Zho.removeTone("qiu2"));
        assertEquals("san", TextTransformer_Zho.removeTone("san1"));
        assertEquals("ti", TextTransformer_Zho.removeTone("ti3"));
        assertEquals("ti。", TextTransformer_Zho.removeTone("ti3。"));

        // wade giles
        assertEquals("ti", TextTransformer_Zho.removeTone("ti4"));
        assertEquals("ch`iu", TextTransformer_Zho.removeTone("ch`iu2"));
        assertEquals("san", TextTransformer_Zho.removeTone("san1"));
        assertEquals("t`i", TextTransformer_Zho.removeTone("t`i3"));
        assertEquals("t`i。", TextTransformer_Zho.removeTone("t`i3。"));
    }

    @Test
    public void normalizeWadeGilesForIndex() {
        String input = "ti4 ch`iu2 shih4 lai2 tzu4 t`ai4 yang2 te5 ti4 san1 k`o1 hsing2 hsing1, yeh3 shih4 yü3 " +
                "chou4 chung1 wei2 i1 pei4 ch`eng1 wei2 yung1 yu3 sheng1 ming4 te5 wu4 t`i3。";
        String expected = "ti4chiu2shih4lai2tzu4tai4yang2te5ti4san1ko1hsing2hsing1yeh3shih4yu3chou4chung1wei2i1pei4" +
                "cheng1wei2yung1yu3sheng1ming4te5wu4ti3";
        assertEquals(expected, ttZho.normalizeWadeGilesForIndex(input, true));

        // NB 11/24/2017: this fails because the second parameter of normalizeWadeGilesForIndex() is ignored
        expected = "tichiushihlaitzutaiyangtetisankohsinghsingyehshihyuchouchungweiipeichengweiyungyushengmingtewuti";
        assertEquals(expected, ttZho.normalizeWadeGilesForIndex(input, false));
    }

    @Test
    public void isPinyinOrWadeGilesSyllable() {
        // pinyin
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("dì"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("di4"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("di"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("qiú"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("qiu2"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("qiu"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("sān"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("san1"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("san"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("tĭ"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti3"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("tĭ。"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti3。"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti。"));

        // wade giles
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti4"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ch`iu2"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("chiu2"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ch`iu"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("chiu"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("san1"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("san"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("t`i3"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti3"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("t`i"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("t`i3。"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti3。"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("t`i。"));
        assertTrue(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ti。"));

        // pinyin symbols with single-character changes (not Pinyin symbols)
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("dìd"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("d4"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("edi"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("qiúl"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("qiup2"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("q"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("swān"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("sane1"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("sand"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("tĭp"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("tti3"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("tmi"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("tĭc。"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("t。"));
        assertFalse(TextTransformer_Zho.isPinyinOrWadeGilesSyllable("ii。"));
    }

    @Test
    public void convertTonalPinyinToNumeric() {
        String input = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng, yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";

        String expected = "di4 qiu2 shi4 lai2 zi4 tai4 yang2 de5 di4 san1 ke1 xing2 xing1, ye3 shi4 yu3 zhou4 zhong1 " +
                "wei2 yi1 bei4 cheng1 wei2 yong1 you3 sheng1 ming4 de5 wu4 ti3。";
        assertEquals(expected, ttZho.convertTonalPinyinToNumeric(input, true));

        expected = "di4 qiu2 shi4 lai2 zi4 tai4 yang2 de di4 san1 ke1 xing2 xing1, ye3 shi4 yu3 zhou4 zhong1 " +
                "wei2 yi1 bei4 cheng1 wei2 yong1 you3 sheng1 ming4 de wu4 ti3。";
        assertEquals(expected, ttZho.convertTonalPinyinToNumeric(input, false));
    }

    @Test
    public void getPinyinTransliteration() {
        String expected = "dì qiú shì lái zì tài yáng de dì sān kē xíng xīng， yĕ shì yŭ zhòu zhōng wéi yī bèi chēng " +
                "wéi yōng yŏu shēng mìng de wù tĭ。";
        assertEquals(expected, TextTransformer_Zho.getPinyinTransliteration(normalizationInput, TextType.ZHO_PINYIN));
        assertEquals(expected,
                TextTransformer_Zho.getPinyinTransliteration(normalizationInput, TextType.ZHO_WADEGILES));
        assertEquals(expected,
                TextTransformer_Zho.getPinyinTransliteration(normalizationInput, TextType.ZHO_WADEGILES_NOTONE));

        expected = "di4 qiu2 shi4 lai2 zi4 tai4 yang2 de5 di4 san1 ke1 xing2 xing1， ye3 shi4 yu3 zhou4 zhong1 wei2 " +
                "yi1 bei4 cheng1 wei2 yong1 you3 sheng1 ming4 de5 wu4 ti3。";
        assertEquals(expected,
                TextTransformer_Zho.getPinyinTransliteration(normalizationInput, TextType.ZHO_PINYIN_NUMERIC));

        expected = "di qiu shi lai zi tai yang de di san ke xing xing， ye shi yu zhou zhong wei yi bei cheng wei " +
                "yong you sheng ming de wu ti。";
        assertEquals(expected,
                TextTransformer_Zho.getPinyinTransliteration(normalizationInput, TextType.ZHO_PINYIN_NOTONE));
    }

    @Test
    public void getWadeGilesTransliteration() {
        String expected = "ti4 ch`iu2 shih4 lai2 tzu4 t`ai4 yang2 te5 ti4 san1 k`o1 hsing2 hsing1， yeh3 shih4 yü3 " +
                "chou4 chung1 wei2 i1 pei4 ch`eng1 wei2 yung1 yu3 sheng1 ming4 te5 wu4 t`i3。";

        assertEquals(expected,
                TextTransformer_Zho.getWadeGilesTransliteration(normalizationInput, TextType.ZHO_WADEGILES));
        assertEquals(expected,
                TextTransformer_Zho.getWadeGilesTransliteration(normalizationInput, TextType.ZHO_PINYIN));
        assertEquals(expected,
                TextTransformer_Zho.getWadeGilesTransliteration(normalizationInput, TextType.ZHO_PINYIN_NUMERIC));
        assertEquals(expected,
                TextTransformer_Zho.getWadeGilesTransliteration(normalizationInput, TextType.ZHO_PINYIN_NOTONE));

        expected = "ti ch`iu shih lai tzu t`ai yang te ti san k`o hsing hsing， yeh shih yü chou chung wei i pei " +
                "ch`eng wei yung yu sheng ming te wu t`i。";
        assertEquals(expected,
                TextTransformer_Zho.getWadeGilesTransliteration(normalizationInput, TextType.ZHO_WADEGILES_NOTONE));
    }

    @Test
    public void isNumericToneSyllable() {
        // pinyin
        assertFalse(ttZho.isNumericToneSyllable("dì"));
        assertTrue(ttZho.isNumericToneSyllable("di4"));
        assertFalse(ttZho.isNumericToneSyllable("di"));
        assertFalse(ttZho.isNumericToneSyllable("qiú"));
        assertTrue(ttZho.isNumericToneSyllable("qiu2"));
        assertFalse(ttZho.isNumericToneSyllable("qiu"));
        assertFalse(ttZho.isNumericToneSyllable("sān"));
        assertTrue(ttZho.isNumericToneSyllable("san1"));
        assertFalse(ttZho.isNumericToneSyllable("san"));
        assertFalse(ttZho.isNumericToneSyllable("tĭ"));
        assertTrue(ttZho.isNumericToneSyllable("ti3"));
        assertFalse(ttZho.isNumericToneSyllable("ti"));
        assertFalse(ttZho.isNumericToneSyllable("tĭ。"));
        assertTrue(ttZho.isNumericToneSyllable("ti3。"));
        assertFalse(ttZho.isNumericToneSyllable("ti。"));

        // wade giles
        assertTrue(ttZho.isNumericToneSyllable("ti4"));
        assertFalse(ttZho.isNumericToneSyllable("ti"));
        assertTrue(ttZho.isNumericToneSyllable("ch`iu2"));
        assertTrue(ttZho.isNumericToneSyllable("chiu2"));
        assertFalse(ttZho.isNumericToneSyllable("ch`iu"));
        assertFalse(ttZho.isNumericToneSyllable("chiu"));
        assertTrue(ttZho.isNumericToneSyllable("san1"));
        assertFalse(ttZho.isNumericToneSyllable("san"));
        assertTrue(ttZho.isNumericToneSyllable("t`i3"));
        assertTrue(ttZho.isNumericToneSyllable("ti3"));
        assertFalse(ttZho.isNumericToneSyllable("t`i"));
        assertFalse(ttZho.isNumericToneSyllable("ti"));
        assertTrue(ttZho.isNumericToneSyllable("t`i3。"));
        assertTrue(ttZho.isNumericToneSyllable("ti3。"));
        assertFalse(ttZho.isNumericToneSyllable("t`i。"));
        assertFalse(ttZho.isNumericToneSyllable("ti。"));

        // pinyin symbols with single-character changes (not Pinyin symbols)
        assertFalse(ttZho.isNumericToneSyllable("dìd"));
        assertFalse(ttZho.isNumericToneSyllable("d4"));
        assertFalse(ttZho.isNumericToneSyllable("edi"));
        assertFalse(ttZho.isNumericToneSyllable("qiúl"));
        assertFalse(ttZho.isNumericToneSyllable("qiup2"));
        assertFalse(ttZho.isNumericToneSyllable("q"));
        assertFalse(ttZho.isNumericToneSyllable("swān"));
        assertFalse(ttZho.isNumericToneSyllable("sane1"));
        assertFalse(ttZho.isNumericToneSyllable("sand"));
        assertFalse(ttZho.isNumericToneSyllable("tĭp"));
        assertFalse(ttZho.isNumericToneSyllable("tti3"));
        assertFalse(ttZho.isNumericToneSyllable("tmi"));
        assertFalse(ttZho.isNumericToneSyllable("tĭc。"));
        assertFalse(ttZho.isNumericToneSyllable("t。"));
        assertFalse(ttZho.isNumericToneSyllable("ii。"));
    }

    @Test
    public void convertNumericToWgSyllable_includeToneNumbers() {
        // non-numeric
        String expected = "";
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("dì", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("di", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("qiú", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("qiu", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("ch`iu", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("chiu", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("sān", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("san", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("tĭ", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("ti", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("t`i", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("tĭ。", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("ti。", true));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("t`i。", true));

        // numeric
        assertEquals("t`i4", TextTransformer_Zho.convertNumericToWgSyllable("ti4", true));
        assertEquals("ti4", TextTransformer_Zho.convertNumericToWgSyllable("di4", true));
        assertEquals("ch`iu2", TextTransformer_Zho.convertNumericToWgSyllable("qiu2", true));
        assertEquals("ch`iu2", TextTransformer_Zho.convertNumericToWgSyllable("ch`iu2", true));
        assertEquals("san1", TextTransformer_Zho.convertNumericToWgSyllable("san1", true));
        assertEquals("t`i3", TextTransformer_Zho.convertNumericToWgSyllable("ti3", true));
        assertEquals("t`i3。", TextTransformer_Zho.convertNumericToWgSyllable("ti3。", true));
        assertEquals("t`i3", TextTransformer_Zho.convertNumericToWgSyllable("t`i3", true));

        // bogus non-numeric
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("dìd", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("edi", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("qiúl", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("q", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("swān", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("sand", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("tĭp", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("tmi", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("tĭc。", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("t。", true));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("ii。", true));

        // bogus numeric
        assertEquals("d4", TextTransformer_Zho.convertNumericToWgSyllable("d4", true));
        assertEquals("qiup2", TextTransformer_Zho.convertNumericToWgSyllable("qiup2", true));
        assertEquals("sane1", TextTransformer_Zho.convertNumericToWgSyllable("sane1", true));
        assertEquals("tti3", TextTransformer_Zho.convertNumericToWgSyllable("tti3", true));
    }

    @Test
    public void convertNumericToWgSyllable_noToneNumbers() {
        // non-numeric
        String expected = "";
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("dì", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("di", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("qiú", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("qiu", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("ch`iu", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("chiu", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("sān", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("san", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("tĭ", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("ti", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("t`i", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("tĭ。", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("ti。", false));
        assertEquals(expected, TextTransformer_Zho.convertNumericToWgSyllable("t`i。", false));

        // numeric
        assertEquals("t`i", TextTransformer_Zho.convertNumericToWgSyllable("ti4", false));
        assertEquals("ti", TextTransformer_Zho.convertNumericToWgSyllable("di4", false));
        assertEquals("ch`iu", TextTransformer_Zho.convertNumericToWgSyllable("qiu2", false));
        assertEquals("ch`iu", TextTransformer_Zho.convertNumericToWgSyllable("ch`iu2", false));
        assertEquals("san", TextTransformer_Zho.convertNumericToWgSyllable("san1", false));
        assertEquals("t`i", TextTransformer_Zho.convertNumericToWgSyllable("ti3", false));
        assertEquals("t`i。", TextTransformer_Zho.convertNumericToWgSyllable("ti3。", false));
        assertEquals("t`i", TextTransformer_Zho.convertNumericToWgSyllable("t`i3", false));

        // bogus non-numeric
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("dìd", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("edi", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("qiúl", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("q", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("swān", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("sand", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("tĭp", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("tmi", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("tĭc。", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("t。", false));
        assertEquals("", TextTransformer_Zho.convertNumericToWgSyllable("ii。", false));

        // bogus numeric
        assertEquals("d", TextTransformer_Zho.convertNumericToWgSyllable("d4", false));
        assertEquals("qiup", TextTransformer_Zho.convertNumericToWgSyllable("qiup2", false));
        assertEquals("sane", TextTransformer_Zho.convertNumericToWgSyllable("sane1", false));
        assertEquals("tti", TextTransformer_Zho.convertNumericToWgSyllable("tti3", false));
    }

    @Test
    public void normalizeAndSimplify() {
        assertEquals(ttZho.normalizeForDisplay(normalizationInput), ttZho.normalizeAndSimplify(normalizationInput));
    }

    @Test
    public void normalizeAndTraditionalify() {
        String expected = "地球是來自太陽的第三顆行星, 也是宇宙中唯一被稱為擁有生命的物體。";
        assertEquals(expected, ttZho.normalizeAndTraditionalify(normalizationInput));
    }

    @Test
    public void simplify() {
        assertEquals(normalizationInput, TextTransformer_Zho.simplify(normalizationInput));
    }

    @Test
    public void traditionalify() {
        String expected = "地球是來自太陽的第三顆行星， 也是宇宙中唯一被稱為擁有生命的物體。";
        assertEquals(expected, ttZho.traditionalify(normalizationInput));
    }

    @Test
    public void indexOfFirstToneMarkAfterVowel() {
        assertEquals(2, ttZho.indexOfFirstToneMarkAfterVowel("dì"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("di4"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("di"));
        assertEquals(3, ttZho.indexOfFirstToneMarkAfterVowel("qiú"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("qiu2"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("qiu"));
        assertEquals(2, ttZho.indexOfFirstToneMarkAfterVowel("sān"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("san1"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("san"));
        assertEquals(2, ttZho.indexOfFirstToneMarkAfterVowel("tĭ"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("ti3"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("ti"));
        assertEquals(2, ttZho.indexOfFirstToneMarkAfterVowel("tĭ。"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("ti3。"));
        assertEquals(-1, ttZho.indexOfFirstToneMarkAfterVowel("ti。"));
    }

    @Test
    public void convertPinyinSyllableToNumeric() {
        // pinyin
        assertEquals("di4", ttZho.convertPinyinSyllableToNumeric("dì"));
        assertEquals("di4", ttZho.convertPinyinSyllableToNumeric("di4"));
        assertEquals("di", ttZho.convertPinyinSyllableToNumeric("di"));
        assertEquals("qiu2", ttZho.convertPinyinSyllableToNumeric("qiú"));
        assertEquals("qiu2", ttZho.convertPinyinSyllableToNumeric("qiu2"));
        assertEquals("qiu", ttZho.convertPinyinSyllableToNumeric("qiu"));
        assertEquals("san1", ttZho.convertPinyinSyllableToNumeric("sān"));
        assertEquals("san1", ttZho.convertPinyinSyllableToNumeric("san1"));
        assertEquals("san", ttZho.convertPinyinSyllableToNumeric("san"));
        assertEquals("ti3", ttZho.convertPinyinSyllableToNumeric("tĭ"));
        assertEquals("ti3", ttZho.convertPinyinSyllableToNumeric("ti3"));
        assertEquals("ti", ttZho.convertPinyinSyllableToNumeric("ti"));
        assertEquals("ti3。", ttZho.convertPinyinSyllableToNumeric("tĭ。"));
        assertEquals("ti3。", ttZho.convertPinyinSyllableToNumeric("ti3。"));
        assertEquals("ti。", ttZho.convertPinyinSyllableToNumeric("ti。"));

        // wade giles
        assertEquals("ti4", ttZho.convertPinyinSyllableToNumeric("ti4"));
        assertEquals("ti", ttZho.convertPinyinSyllableToNumeric("ti"));
        assertEquals("ch`iu2", ttZho.convertPinyinSyllableToNumeric("ch`iu2"));
        assertEquals("chiu2", ttZho.convertPinyinSyllableToNumeric("chiu2"));
        assertEquals("ch`iu", ttZho.convertPinyinSyllableToNumeric("ch`iu"));
        assertEquals("chiu", ttZho.convertPinyinSyllableToNumeric("chiu"));
        assertEquals("san1", ttZho.convertPinyinSyllableToNumeric("san1"));
        assertEquals("san", ttZho.convertPinyinSyllableToNumeric("san"));
        assertEquals("t`i3", ttZho.convertPinyinSyllableToNumeric("t`i3"));
        assertEquals("ti3", ttZho.convertPinyinSyllableToNumeric("ti3"));
        assertEquals("t`i", ttZho.convertPinyinSyllableToNumeric("t`i"));
        assertEquals("ti", ttZho.convertPinyinSyllableToNumeric("ti"));
        assertEquals("t`i3。", ttZho.convertPinyinSyllableToNumeric("t`i3。"));
        assertEquals("ti3。", ttZho.convertPinyinSyllableToNumeric("ti3。"));
        assertEquals("t`i。", ttZho.convertPinyinSyllableToNumeric("t`i。"));
        assertEquals("ti。", ttZho.convertPinyinSyllableToNumeric("ti。"));
    }

    @Test
    public void separateLatinFromChinese() {
        String input = "地s球e是c来r自e太t阳h的i第d三d颗e行n星m，e s也s是a宇g宙e中唯一被称为拥有生命的物体。";
        String expected = "地 s 球 e 是 c 来 r 自 e 太 t 阳 h 的 i 第 d 三 d 颗 e 行 n 星 m，e s 也 s 是 a 宇 g 宙 e 中唯" +
                "一被称为拥有生命的物体。";
        assertEquals(expected, TextTransformer_Zho.separateLatinFromChinese(input));
    }
}