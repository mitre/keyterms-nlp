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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.ibm.icu.text.Transliterator;

import keyterms.nlp.interfaces.INormalizer;
import keyterms.nlp.interfaces.ITextTransformer;
import keyterms.nlp.interfaces.ITokenizer;
import keyterms.nlp.iso.Script;
import keyterms.nlp.languages.ukr.TextTransformer_Ukr;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;
import keyterms.nlp.text.Characters;
import keyterms.nlp.text.StringNormalizer;
import keyterms.nlp.transliterate.zho.ZhoUtils;
import keyterms.util.text.Strings;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class TextTransformer_Zho
//        implements ITextTransformer, INormalizer {
        extends TextTransformer_Ukr implements INormalizer {

    public static final boolean REMOVE_SPACES_FOR_INDEX = false;
    public static final boolean REMOVE_LINEBREAKS_FOR_INDEX = true;

    private static String toneDigits = "0123456789";
    // private static char[] vowels = { 'a', 'e', 'i', 'o', 'u', 'v', 'ü' };
    private static String vowels = "aeiouvü";

    private static Transliterator simplifier;
    private static Transliterator traditionalifier;


    private static Hashtable<String, String> pinyinToWg;
    private static Hashtable<String, String> toneMarkToNumber;

    private ITokenizer tokenizer;
    static {
        simplifier = Transliterator.getInstance("Traditional-Simplified");
        traditionalifier = Transliterator.getInstance("Simplified-Traditional");
        SetUpPinyinToWg();
        SetUpToneMarkToNumber();
    }

    public TextTransformer_Zho() {
        tokenizer = new Tokenizer_Zho();
        //numerifier = Transliterator.getInstance("Pinyin-NumericPinyin");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForDisplay(String input) {
        return normForDisplay(input);
    }

    /**
     * Normalize method of the INormalizer interface
     *
     * @param input the input text
     * @param normalizePunctuation a flag indicating whether to normalize punctuation
     * @param transliteratePunctuation a flag indicating whether to transliterate punctuation
     *
     * @return the processed text
     */
    public static String normalizeForDisplay(String input, boolean normalizePunctuation, boolean
            transliteratePunctuation) {
        return StringNormalizer.normalize(input, true/*removeNewLine*/, false/*removeSpace*/, true/*removeControl*/,
                false/*removePunctuation*/, normalizePunctuation/*normalizePunctuation*/,
                transliteratePunctuation/*transliteratePunctuation*/,
                false/*removeDiacritics*/, false/*normalizeCase*/, Normalizer.Form.NFKC);
    }

    public static String normForDisplay(String input) {
        return StringNormalizer.normalize(input, true/*removeNewLine*/, false/*removeSpace*/, true/*removeControl*/,
                false/*removePunctuation*/, false/*normalizePunctuation*/, false/*transliteratePunctuation*/,
                false/*removeDiacritics*/,
                false/*normalizeCase*/, Normalizer.Form.NFKC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForScoring(String input) {
        return StringNormalizer.normalize(input, true/*removeNewLine*/, false/*removeSpace*/, true/*removeControl*/,
                false/*removePunctuation*/, false/*normalizePunctuation*/, false/*transliteratePunctuation*/,
                false/*removeDiacritics*/,
                false/*normalizeCase*/, Normalizer.Form.NFKD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prepareIndexForm(String input) {
        //String normalized = ZhoUtils.spacify(input,true);
        //return normalized;
        return normForIndex(input);
    }

    /**
     * {@inheritDoc}
     */
//    @Override
//    public String normalizeForIndex(String input, boolean removeSpacesForIndex) {
//        return normForIndex(input,REMOVE_SPACES_FOR_INDEX);
//    }

    public static String normForIndex(String input) {
        if (input == null) {
            return null;
        }
        input = StringNormalizer.normalize(input, true/*removeNewLine*/, false/*removeSpace*/, true/*removeControl*/,
                false/*removePunctuation*/, true/*normalizePunctuation*/, true/*transliteratePunctuation*/,
                false/*removeDiacritics*/,
                true/*normalizeCase*/, Normalizer.Form.NFKC);
        input = simplify(input);
        input = ZhoUtils.spacify(input,true);
        String[] sa = input.split("\\s");
        StringBuilder sb = new StringBuilder();
        if (sa == null) {
            return StringNormalizer
                    .normalize(input, REMOVE_LINEBREAKS_FOR_INDEX/*removeNewLine*/, false/*removeSpace*/,
                            true/*removeControl*/,
                            true/*removePunctuation*/, true/*normalizePunctuation*/, true/*transliteratePunctuation*/,
                            true/*removeDiacritics*/, true/*normalizeCase*/, Normalizer.Form.NFKD);
        }
        int i = 0;
        for (String curToken : sa) {
            if (i > 0) {
                sb.append(" ");
            }
            if (isPinyinOrWadeGilesSyllable(curToken)) {
                sb.append(" ");
                sb.append(convertToPinyinForIndex(curToken));
            } else {
                if (curToken.contains("'") || curToken.contains("`")) {
                    String[] realSyls = curToken.split("['`]");
                    int j = 0;
                    for (String tok : realSyls) {
                        if (isPinyinOrWadeGilesSyllable(tok)) {
                            String curString = StringNormalizer.removeDiacritics(tok);
                            curString = ZhoUtils.replaceNumbers(curString, "");
                            if (j > 0) {
                                sb.append(" ");
                            }
                            sb.append(curString);
                        } else {
                            if (j > 0) {
                                sb.append("'");
                            }
                            sb.append(tok);
                        }
                        j++;
                    }
                } else {
                    sb.append(StringNormalizer
                            .normalize(curToken, REMOVE_LINEBREAKS_FOR_INDEX/*removeNewLine*/,
                                    false/*removeSpace*/,
                                    true/*removeControl*/,
                                    true/*removePunctuation*/, false/*normalizePunctuation*/,
                                    true/*transliteratePunctuation*/,
                                    true/*removeDiacritics*/, true/*normalizeCase*/, Normalizer.Form.NFKD));
                }
            }
            i++;
        }
        String results = sb.toString();
        results = StringNormalizer.squinch(results, REMOVE_LINEBREAKS_FOR_INDEX);
        return results;
    }

    /**
     * {@inheritDoc}
     */
    public String transliterate(String input, TextType standard) {
        return translit(input, standard);
    }

    public String transliterate(String input) {
        return getPinyinTransliteration(input, TextType.ZHO_PINYIN);
    }

    public static String translit(String input, TextType standard) {
        input = normalizeForDisplay(input, true, true);
        input = separateLatinFromChinese(input);
        if (standard == TextType.ZHO_WADEGILES || standard == TextType.ZHO_WADEGILES_NOTONE) {
            return getWadeGilesTransliteration(input, standard);
        }
        return getPinyinTransliteration(input, standard);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Transliteration> getAvailableTransforms(String input) {
        if (input == null) {
            return null;
        }
        ArrayList<Transliteration> results = new ArrayList<>();
        Transliteration curXlit;

        curXlit = new Transliteration(true, 0, Script.HANI.getCode(), TextType.ORIGINAL.getDisplayLabel(),
                normalizeForDisplay(input), prepareIndexForm(input));
        results.add(curXlit);

        curXlit = new Transliteration(true, 1, Script.HANS.getCode(), TextType.ZHO_SIMPLIFIED.getDisplayLabel(),
                normalizeAndSimplify(input), prepareIndexForm(input));
        results.add(curXlit);

        curXlit = new Transliteration(true, 2, Script.HANT.getCode(), TextType.ZHO_TRADITIONAL.getDisplayLabel(),
                normalizeAndTraditionalify(input), prepareIndexForm(input));
        results.add(curXlit);

        curXlit = new Transliteration(false, 3, Script.LATN.getCode(), TextType.ZHO_PINYIN.getDisplayLabel(),
                transliterate(input, TextType.ZHO_PINYIN), convertToPinyinForIndex(input));
        results.add(curXlit);

        curXlit = new Transliteration(false, 4, Script.LATN.getCode(), TextType.ZHO_WADEGILES.getDisplayLabel(),
                transliterate(input, TextType.ZHO_WADEGILES), convertToWadeGilesForIndex(input));
        results.add(curXlit);

        return results;
    }

    @Override
    public String normalizeForIndex(String input, boolean removeSpacesForIndex) {
        return prepareIndexForm(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getRemoveSpacesForIndex() {
        return REMOVE_SPACES_FOR_INDEX;
    }

    /**
     * Normalizes a Pinyin string to remove diacritics flatten the distinctions
     * between canonical(C) and compatibility(K) variants,toneless pinyin
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public static String convertToPinyinForIndex(String input) {
        if (input == null || input.trim().equals("")) {
            return input;
        }
        String hanyuPinyin = translit(input, TextType.ZHO_PINYIN);
        hanyuPinyin = removeTone(hanyuPinyin);
        hanyuPinyin = StringNormalizer.squinch(hanyuPinyin, REMOVE_LINEBREAKS_FOR_INDEX);
        return hanyuPinyin;
    }

    /**
     * Normalizes a Pinyin string to remove diacritics flatten the distinctions
     * between canonical(C) and compatibility(K) variants, and finally convert
     * the input to simplified Chinese
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public String convertToWadeGilesForIndex(String input) {
        if (input == null || input.trim().equals("")) {
            return input;
        }
        String wadeGiles = transliterate(input, TextType.ZHO_WADEGILES);
        wadeGiles = removeTone(wadeGiles);
        wadeGiles = StringNormalizer.squinch(wadeGiles, REMOVE_LINEBREAKS_FOR_INDEX);
        return wadeGiles;
    }

    public static String removeTone(String pinyin) {
        if (pinyin == null) {
            return null;
        }
        String[] sa = pinyin.split("[\\s\\t]+");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String s : sa) {
            if (i > 0) {
                sb.append(" ");
            }
            if (isPinyinOrWadeGilesSyllable(s)) {
                String curString = StringNormalizer.removeDiacritics(s);
                curString = ZhoUtils.replaceNumbers(curString, "");
                sb.append(curString);
            } else {
                if (s.contains("'")) {
                    String[] realSyls = s.split("'");
                    int j = 0;
                    for (String tok : realSyls) {
                        if (isPinyinOrWadeGilesSyllable(tok)) {
                            String curString = StringNormalizer.removeDiacritics(tok);
                            curString = ZhoUtils.replaceNumbers(curString, "");
                            sb.append(curString);
                        } else {
                            if (j > 0) {
                                sb.append("'");
                            }
                            sb.append(tok);
                        }
                        j++;
                    }
                } else {
                    sb.append(s);
                }
            }
            i++;
        }
        return sb.toString();
    }

    /**
     * Normalizes a Pinyin string to remove diacritics flatten the distinctions
     * between canonical(C) and compatibility(K) variants, and finally convert
     * the input to simplified Chinese
     *
     * @param wadeGilesInput the wade giles input
     * @param maintainTone a flag indicating whether to maintain tone
     *
     * @return the processed text
     */
    public String normalizeWadeGilesForIndex(String wadeGilesInput, boolean maintainTone) {
        if (wadeGilesInput == null) {
            return null;
        }
        StringBuilder wadeGilesIndex = new StringBuilder();
        String[] sa = wadeGilesInput.split("\\s");
        int tokenNum=0;
        for (String aSa : sa) {
            String chunk = aSa.trim().toLowerCase();
            if(tokenNum>0)
                    wadeGilesIndex.append(" ");
            wadeGilesIndex.append(convertTonalPinyinToNumeric(chunk, true));
            tokenNum++;
        }
        String results = wadeGilesIndex.toString();
        results = StringNormalizer.removeAllNonWordChars(results, false, REMOVE_LINEBREAKS_FOR_INDEX);
        results = results.replaceAll("\\s\\s+", " ");
        return results.trim();
    }

    public static boolean isPinyinOrWadeGilesSyllable(String input) {
        if (input == null || input.trim().equals("")) {
            return false;
        }

        String tmpSyllab = input.trim().toLowerCase();

        //  delete trailing punctuation before checking if string is pinyin symbol
        while (tmpSyllab.length() > 0 && Characters.isPunctuation(tmpSyllab.charAt(tmpSyllab.length() - 1))) {
            tmpSyllab = tmpSyllab.substring(0, tmpSyllab.length() - 1);
        }

        tmpSyllab = ZhoUtils.replaceNumbers(tmpSyllab, "");

        if (pinyinToWg.containsKey(tmpSyllab)) {
            return true;
        }
        if (pinyinToWg.containsValue(tmpSyllab)) {
            return true;
        }
        tmpSyllab = StringNormalizer.removeDiacritics(tmpSyllab);
        if (pinyinToWg.containsKey(tmpSyllab)) {
            return true;
        }
        return pinyinToWg.containsValue(tmpSyllab);
    }

    public String convertTonalPinyinToNumeric(String pinyinString, boolean markFifth) {
        if (pinyinString == null || pinyinString.trim().equals("")) {
            return pinyinString;
        }
        pinyinString = Normalizer.normalize(pinyinString, Normalizer.Form.NFKD);
        pinyinString = ZhoUtils.replaceSuperscriptsWithToneNumbers(pinyinString);
        pinyinString = ZhoUtils.addSpacesAfterNumbers(pinyinString);
        pinyinString = StringNormalizer.squinch(pinyinString, REMOVE_LINEBREAKS_FOR_INDEX);
        pinyinString =
                pinyinString.replaceAll("[\u2019\u0027\u02BC]", "'");  // replace apostrophes with a single apostrophe
        pinyinString = pinyinString.replaceAll("[\u002D\u2010\u2011\u2012\u2013\u2043\u2212]",
                "-");  // replace hyphens and hyphen-like marks with a single hyphen

        StringBuilder result = new StringBuilder();


        StringTokenizer sToker = new StringTokenizer(pinyinString, " '-_?,。.!~$%&*()+=;:}{[]|\\/<>\"", true);
        while (sToker.hasMoreTokens()) {
            String curSyll = sToker.nextToken();
            if (curSyll.length() == 1) {
                result.append(curSyll);
                continue;
            }
            int toneSpot = indexOfFirstToneMarkAfterVowel(curSyll);
            if (toneSpot > -1) {
                result.append(convertPinyinSyllableToNumeric(curSyll));
            } else {
                result.append(curSyll);
                if (markFifth && isPinyinOrWadeGilesSyllable(curSyll) && !isNumericToneSyllable(curSyll)) {
                    result.append("5");
                }
            }
        }
        return result.toString();
    }

    public static String getPinyinTransliteration(String input, TextType standard) {
        if (Strings.isBlank(input)) {
            return input;
        }
        input = separateLatinFromChinese(input);
        StringBuilder sb = new StringBuilder();
        HanyuPinyinOutputFormat outputFormat = getOutputFormat(standard);

        boolean spaceWasInsertedAfterPrevChar = false;
        for (int c = 0; c < input.length(); c++) {
            char ch = input.charAt(c);
            //String curInput = Character.toString(ch);
            String[] pinyinStrings;
            try {
                pinyinStrings = PinyinHelper.toHanyuPinyinStringArray(ch, outputFormat);
                if (pinyinStrings != null && pinyinStrings.length > 0) {
                    sb.append(pinyinStrings[0]);
                } else {
                    if (Characters.isPunctuation(ch) && spaceWasInsertedAfterPrevChar) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    sb.append(ch);
                }
                if (c < input.length() - 1 && (Character.isIdeographic(ch) && !Characters.isPunctuation(ch)) ||
                        ch == ',' || ch == 65292) {
                    sb.append(" ");
                    spaceWasInsertedAfterPrevChar = true;
                } else {
                    spaceWasInsertedAfterPrevChar = false;
                }
            } catch (Exception eek) {
                System.err.println(eek.getMessage());
            }
        }
        String results = sb.toString();
        results = StringNormalizer.squinch(results, REMOVE_LINEBREAKS_FOR_INDEX);
        return results;
    }

    public static String getWadeGilesTransliteration(String input, TextType standard) {
        String numericPinyin = getPinyinTransliteration(input,
                TextType.ZHO_PINYIN_NUMERIC);
        if (numericPinyin == null) {
            return numericPinyin;
        }
        StringBuilder wadeGiles = new StringBuilder();
        String[] sa = numericPinyin.split("\\s+");
        if (sa != null) {
            int s = 0;
            for (String syllable : sa) {

                boolean useTones = true;
                if (standard == TextType.ZHO_WADEGILES_NOTONE) {
                    useTones = false;
                }
                StringBuilder endMatter = new StringBuilder();
                for (int i = syllable.length() - 1; i >= 0; i--) {
                    //String curChar = syllable.substring(i, i + 1);
                    //if (Strings.isPunct(curChar)) {
                    char curChar = syllable.charAt(i);
                    if (Characters.isPunctuation(curChar)) {
                        endMatter.append(curChar);
                    } else {
                        break;
                    }
                }
                if (endMatter.length() > 0) {
                    endMatter = endMatter.reverse();
                    int startEndMatter = syllable.length() - endMatter.length();
                    syllable = syllable.substring(0, startEndMatter);
                }

                String wgSyllab = convertNumericToWgSyllable(syllable, useTones);
                if (wgSyllab != null && !wgSyllab.trim().equals("")) {
                    wadeGiles.append(wgSyllab);
                } else {
                    if (StringNormalizer.isPunct(syllable)) {
                        wadeGiles.deleteCharAt(wadeGiles.length() - 1);
                    }
                    wadeGiles.append(syllable);
                }
                s++;
                if (endMatter.length() > 0) {
                    endMatter.reverse();
                    wadeGiles.append(endMatter);
                    if (s < sa.length) {
                        wadeGiles.append(" ");
                    }
                } else {
                    if (s < sa.length) {
                        wadeGiles.append(" ");
                    }
                }
            }
        }
        return wadeGiles.toString();
    }

    public boolean isNumericToneSyllable(String input) {
        if (input == null || input.trim().equals("")) {
            return false;
        }

        while (input.length() > 0 && Characters.isPunctuation(input.charAt(input.length() - 1))) {
            input = input.substring(0, input.length() - 1);
        }
        return toneDigits.indexOf(input.charAt(input.length() - 1)) != -1 && isPinyinOrWadeGilesSyllable(input);
    }

      public static String convertNumericToWgSyllable(String numericPinyin, boolean useTones) {

        if (Strings.isBlank(numericPinyin)) {
            return numericPinyin;
        }
        String toneStr = "";
        String tonelessSyllable = "";

        // NB 11/22/2017: store trailing punctuation before checking if string is pinyin symbol
        StringBuilder suffix = new StringBuilder();
        while (numericPinyin.length() > 0 &&
                Characters.isPunctuation(numericPinyin.charAt(numericPinyin.length() - 1))) {
            suffix.append(numericPinyin.charAt(numericPinyin.length() - 1));
            numericPinyin = numericPinyin.substring(0, numericPinyin.length() - 1);
        }

        if (toneDigits.indexOf(numericPinyin.charAt(numericPinyin.length() - 1)) != -1) {
            tonelessSyllable = numericPinyin.substring(0, numericPinyin.length() - 1);
            toneStr = numericPinyin.substring(numericPinyin.length() - 1);
        }
        String wg = tonelessSyllable;
        try {
            wg = pinyinToWg.get(tonelessSyllable);
            if (wg == null) {
                wg = tonelessSyllable;
            }
        } catch (Exception eek) {
            // noop, use pinyin
        }
        if (useTones) {
            wg = wg + toneStr;
        }

        // NB 11/22/2017: restore suffix
        if (wg.length() > 0) {
            wg += suffix.toString();
        }

        return wg;
    }

    /*
     * Normalizes a Chinese string to flatten the distinctions between
     * canonical(C) and compatibility(K) variants, and then convert to
     * simplified Chinese
     */
    public String normalizeAndSimplify(String input) {
        if (input == null) {
            return null;
        }
        String normalizedText = normalizeForDisplay(input);
        return simplify(normalizedText);
    }

    /**
     * Normalizes a Chinese string to flatten the distinctions between
     * canonical(C) and compatibility(K) variants,
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public String normalizeAndTraditionalify(String input) {
        if (input == null) {
            return null;
        }
        String normalizedText = normalizeForDisplay(input);
        return traditionalify(normalizedText);
    }

    /*
     * Converts a Chinese string to Simplified characters
     */
    public static String simplify(String input) {
        if (input == null) {
            return null;
        }
        input = simplifier.transliterate(input);
        return input;
    }

    /**
     * Normalizes a Chinese string to flatten the distinctions between
     * canonical(C) and compatibility(K) variants,
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public String traditionalify(String input) {
        if (input == null) {
            return null;
        }
        input = traditionalifier.transliterate(input);
        return input;
    }

    private static void SetUpToneMarkToNumber() {
        toneMarkToNumber = new Hashtable<>();
        toneMarkToNumber.put("\u02C9", "1");  //  ˉ     U+02C9     First Tone     preferred
        toneMarkToNumber.put("\u00AF", "1");  //  ¯     U+00AF     First Tone
        toneMarkToNumber.put("\u0304", "1");  //  ̄     U+0304     First Tone
        toneMarkToNumber.put("\u02CA", "2");  //  ˊ     U+02CA     Second Tone     preferred
        toneMarkToNumber.put("\u00B4", "2");  //  ´     U+00B4     Second Tone
        toneMarkToNumber.put("\u0301", "2");  //  ́     U+0301     Second Tone
        toneMarkToNumber.put("\u02B9", "2");  //  ʹ     U+02B9     Second Tone
        toneMarkToNumber.put("\u0306", "3");  //  ˇ     U+02C7     Third Tone     preferred
        toneMarkToNumber.put("\u02C7", "3");  //  ˇ     U+02C7     Third Tone     preferred
        toneMarkToNumber.put("\u030C", "3");  //  ̌     U+030C     Third Tone
        toneMarkToNumber.put("\u0300", "4");  //  ́ˋ   U+0300     Fourth Tone
        toneMarkToNumber.put("\u02CB", "4");  //  ˋ     U+02CB     Fourth Tone     preferred
        toneMarkToNumber.put("\u0060", "4");  //   `     U+0060     Fourth Tone
        toneMarkToNumber.put("\u02D9", "5");  //  ˙     U+02D9     Fifth Tone     preferred (or unmarked)
        toneMarkToNumber.put("\u0307", "5");  //  ̇     U+0307     Fifth Tone
    }

    public int indexOfFirstToneMarkAfterVowel(String input) {
        // NB 11/22/2017: decompose first (otherwise, this method always returns -1)
        input = Normalizer.normalize(input, Normalizer.Form.NFD);

        if (input == null || input.trim().equals("")) {
            return -1;
        }

        //("[\u02C9\u00AF\u0304\u02CA\u00B4\u0301\u02B9\u02C7\u030C\u02CB\u0060\u02D9\u0307]"))
        boolean prevCharWasVowel = false;
        for (int i = 0; i < input.length(); i++) {
            String toneNum = null;
            String curCharStr = input.substring(i, i + 1);
            if (prevCharWasVowel) {
                try {
                    toneNum = toneMarkToNumber.get(curCharStr);
                } catch (Exception eek) {
                    //System.err.println("Unknown tone encountered by TextTransformer_Zh");
                }
                if (toneNum != null) {
                    //	currentToneValue = toneNum;  // so lazy, do something better some day
                    return i;
                }
            }
            prevCharWasVowel = vowels.contains(curCharStr);
        }
        return -1;
    }

    public String convertPinyinSyllableToNumeric(String input) {
        if (input == null || input.trim().equals("")) {
            return input;
        }

        // NB 11/24/2017: store trailing punctuation before checking if string is pinyin symbol
        StringBuilder suffix = new StringBuilder();
        while (input.length() > 0 &&
                Characters.isPunctuation(input.charAt(input.length() - 1))) {
            suffix.append(input.charAt(input.length() - 1));
            input = input.substring(0, input.length() - 1);
        }

        // NB 11/24/2017: decompose input
        input = Normalizer.normalize(input, Normalizer.Form.NFD);

        //("[\u02C9\u00AF\u0304\u02CA\u00B4\u0301\u02B9\u02C7\u030C\u02CB\u0060\u02D9\u0307]"))
        boolean prevCharWasVowel = false;
        StringBuilder sb = new StringBuilder();
        String toneNum = null;
        for (int i = 0; i < input.length(); i++) {

            String curCharStr = input.substring(i, i + 1);
            if (prevCharWasVowel) {
                try {
                    toneNum = toneMarkToNumber.get(curCharStr);
                } catch (Exception eek) {
                    //System.err.println("Unknown tone encountered by TextTransformer_Zh");
                }
                if (toneNum == null) {
                    sb.append(curCharStr);
                } else {
                    String tmpString = sb.toString() + input.substring(i + 1);
                    if (isPinyinOrWadeGilesSyllable(tmpString)) {
                        sb.append(input.substring(i + 1));
                        sb.append(toneNum);
                    } else {
                        sb.append(input.substring(i));
                    }
                    return sb.toString() + suffix; // NB 11/24/2017: add stored suffix to output
                }
            } else {
                sb.append(curCharStr);
            }
            prevCharWasVowel = vowels.contains(curCharStr);
        }

        return sb.toString() + suffix; // NB 11/24/2017: add stored suffix to output
    }

    private static void SetUpPinyinToWg() {
        pinyinToWg = new Hashtable<>();
        pinyinToWg.put("a", "a");
        pinyinToWg.put("ai", "ai");
        pinyinToWg.put("an", "an");
        pinyinToWg.put("ang", "ang");
        pinyinToWg.put("ao", "ao");
        pinyinToWg.put("ba", "pa");
        pinyinToWg.put("bai", "pai");
        pinyinToWg.put("ban", "pan");
        pinyinToWg.put("bang", "pang");
        pinyinToWg.put("bao", "pao");
        pinyinToWg.put("bei", "pei");
        pinyinToWg.put("ben", "pen");
        pinyinToWg.put("beng", "peng");
        pinyinToWg.put("bi", "pi");
        pinyinToWg.put("bian", "pien");
        pinyinToWg.put("biao", "piao");
        pinyinToWg.put("bie", "pieh");
        pinyinToWg.put("bin", "pin");
        pinyinToWg.put("bing", "ping");
        pinyinToWg.put("bo", "po");
        pinyinToWg.put("bu", "pu");
        pinyinToWg.put("ca", "ts`a");
        pinyinToWg.put("cai", "ts`ai");
        pinyinToWg.put("can", "ts`an");
        pinyinToWg.put("cang", "ts`ang");
        pinyinToWg.put("cao", "ts`ao");
        pinyinToWg.put("ce", "ts`e");
        pinyinToWg.put("cen", "ts`en");
        pinyinToWg.put("ceng", "ts`eng");
        pinyinToWg.put("cha", "ch`a");
        pinyinToWg.put("chai", "ch`ai");
        pinyinToWg.put("chan", "ch`an");
        pinyinToWg.put("chang", "ch`ang");
        pinyinToWg.put("chao", "ch`ao");
        pinyinToWg.put("che", "ch`e");
        pinyinToWg.put("chen", "ch`en");
        pinyinToWg.put("cheng", "ch`eng");
        pinyinToWg.put("chi", "ch`ih");
        pinyinToWg.put("chong", "ch`ung");
        pinyinToWg.put("chou", "ch`ou");
        pinyinToWg.put("chu", "ch`u");
        pinyinToWg.put("chua", "ch`ua");
        pinyinToWg.put("chuai", "ch`uai");
        pinyinToWg.put("chuan", "ch`uan");
        pinyinToWg.put("chuang", "ch`uang");
        pinyinToWg.put("chui", "ch`ui");
        pinyinToWg.put("chun", "ch`un");
        pinyinToWg.put("chuo", "ch`o");
        pinyinToWg.put("ci", "tz`u");
        pinyinToWg.put("cong", "ts`ung");
        pinyinToWg.put("cou", "ts`ou");
        pinyinToWg.put("cu", "ts`u");
        pinyinToWg.put("cuan", "ts`uan");
        pinyinToWg.put("cui", "ts`ui");
        pinyinToWg.put("cun", "ts`un");
        pinyinToWg.put("cuo", "ts`o");
        pinyinToWg.put("da", "ta");
        pinyinToWg.put("dai", "tai");
        pinyinToWg.put("dan", "tan");
        pinyinToWg.put("dang", "tang");
        pinyinToWg.put("dao", "tao");
        pinyinToWg.put("de", "te");
        pinyinToWg.put("dei", "tei");
        pinyinToWg.put("den", "ten");
        pinyinToWg.put("deng", "teng");
        pinyinToWg.put("di", "ti");
        pinyinToWg.put("dian", "tien");
        pinyinToWg.put("diang", "tiang");
        pinyinToWg.put("diao", "tiao");
        pinyinToWg.put("die", "tieh");
        pinyinToWg.put("ding", "ting");
        pinyinToWg.put("diu", "tiu");
        pinyinToWg.put("dong", "tung");
        pinyinToWg.put("dou", "tou");
        pinyinToWg.put("du", "tu");
        pinyinToWg.put("duan", "tuan");
        pinyinToWg.put("dui", "tui");
        pinyinToWg.put("dun", "tun");
        pinyinToWg.put("duo", "to");
        pinyinToWg.put("e", "o");
        pinyinToWg.put("ei", "ei");
        pinyinToWg.put("en", "en");
        pinyinToWg.put("er", "erh");
        pinyinToWg.put("fa", "fa");
        pinyinToWg.put("fan", "fan");
        pinyinToWg.put("fang", "fang");
        pinyinToWg.put("fei", "fei");
        pinyinToWg.put("fen", "fen");
        pinyinToWg.put("feng", "feng");
        pinyinToWg.put("fo", "fo");
        pinyinToWg.put("fou", "fou");
        pinyinToWg.put("fu", "fu");
        pinyinToWg.put("ga", "ka");
        pinyinToWg.put("gai", "kai");
        pinyinToWg.put("gan", "kan");
        pinyinToWg.put("gang", "kang");
        pinyinToWg.put("gao", "kao");
        pinyinToWg.put("ge", "ko");
        pinyinToWg.put("gei", "kei");
        pinyinToWg.put("gen", "ken");
        pinyinToWg.put("geng", "keng");
        pinyinToWg.put("gong", "kung");
        pinyinToWg.put("gou", "kou");
        pinyinToWg.put("gu", "ku");
        pinyinToWg.put("gua", "kua");
        pinyinToWg.put("guai", "kuai");
        pinyinToWg.put("guan", "kuan");
        pinyinToWg.put("guang", "kuang");
        pinyinToWg.put("gui", "kuei");
        pinyinToWg.put("gun", "kun");
        pinyinToWg.put("guo", "kuo");
        pinyinToWg.put("ha", "ha");
        pinyinToWg.put("hai", "hai");
        pinyinToWg.put("han", "han");
        pinyinToWg.put("hang", "hang");
        pinyinToWg.put("hao", "hao");
        pinyinToWg.put("he", "ho");
        pinyinToWg.put("hei", "hei");
        pinyinToWg.put("hen", "hen");
        pinyinToWg.put("heng", "heng");
        pinyinToWg.put("hong", "hung");
        pinyinToWg.put("hou", "hou");
        pinyinToWg.put("hu", "hu");
        pinyinToWg.put("hua", "hua");
        pinyinToWg.put("huai", "huai");
        pinyinToWg.put("huan", "huan");
        pinyinToWg.put("huang", "huang");
        pinyinToWg.put("hui", "hui");
        pinyinToWg.put("hun", "hun");
        pinyinToWg.put("huo", "huo");
        pinyinToWg.put("ji", "chi");
        pinyinToWg.put("jia", "chia");
        pinyinToWg.put("jian", "chien");
        pinyinToWg.put("jiang", "chiang");
        pinyinToWg.put("jiao", "chiao");
        pinyinToWg.put("jie", "chieh");
        pinyinToWg.put("jin", "chin");
        pinyinToWg.put("jing", "ching");
        pinyinToWg.put("jiong", "chiung");
        pinyinToWg.put("jiu", "chiu");
        pinyinToWg.put("ju", "chü");
        pinyinToWg.put("juan", "chüan");
        pinyinToWg.put("jue", "chüeh");
        pinyinToWg.put("jun", "chün");
        pinyinToWg.put("ka", "k`a");
        pinyinToWg.put("kai", "k`ai");
        pinyinToWg.put("kan", "k`an");
        pinyinToWg.put("kang", "k`ang");
        pinyinToWg.put("kao", "k`ao");
        pinyinToWg.put("ke", "k`o");
        pinyinToWg.put("ken", "k`en");
        pinyinToWg.put("keng", "k`eng");
        pinyinToWg.put("kong", "k`ung");
        pinyinToWg.put("kou", "k`ou");
        pinyinToWg.put("ku", "k`u");
        pinyinToWg.put("kua", "k`ua");
        pinyinToWg.put("kuai", "k`uai");
        pinyinToWg.put("kuan", "k`uan");
        pinyinToWg.put("kuang", "k`uang");
        pinyinToWg.put("kui", "k`uei");
        pinyinToWg.put("kun", "k`un");
        pinyinToWg.put("kuo", "k`uo");
        pinyinToWg.put("la", "la");
        pinyinToWg.put("lai", "lai");
        pinyinToWg.put("lan", "lan");
        pinyinToWg.put("lang", "lang");
        pinyinToWg.put("lao", "lao");
        pinyinToWg.put("le", "le");
        pinyinToWg.put("lei", "lei");
        pinyinToWg.put("leng", "leng");
        pinyinToWg.put("li", "li");
        pinyinToWg.put("lia", "lia");
        pinyinToWg.put("lian", "lien");
        pinyinToWg.put("liang", "liang");
        pinyinToWg.put("liao", "liao");
        pinyinToWg.put("lie", "lieh");
        pinyinToWg.put("lin", "lin");
        pinyinToWg.put("ling", "ling");
        pinyinToWg.put("liu", "liu");
        pinyinToWg.put("lo", "lo");
        pinyinToWg.put("long", "lung");
        pinyinToWg.put("lou", "lou");
        pinyinToWg.put("lu", "lu");
        pinyinToWg.put("lu:", "lü");
        pinyinToWg.put("lv", "lü");
        pinyinToWg.put("lü", "lü");
        pinyinToWg.put("luan", "luan");
        pinyinToWg.put("lve", "lüeh");
        pinyinToWg.put("lu:e", "lüeh");
        pinyinToWg.put("lüe", "lüeh");
        pinyinToWg.put("lun", "lun");
        pinyinToWg.put("lvn", "lün");
        pinyinToWg.put("lu:n", "lün");
        pinyinToWg.put("lün", "lün");
        pinyinToWg.put("luo", "lo");
        pinyinToWg.put("ma", "ma");
        pinyinToWg.put("mai", "mai");
        pinyinToWg.put("man", "man");
        pinyinToWg.put("mang", "mang");
        pinyinToWg.put("mao", "mao");
        pinyinToWg.put("me", "me");
        pinyinToWg.put("mei", "mei");
        pinyinToWg.put("men", "men");
        pinyinToWg.put("meng", "meng");
        pinyinToWg.put("mi", "mi");
        pinyinToWg.put("mian", "mien");
        pinyinToWg.put("miao", "miao");
        pinyinToWg.put("mie", "mieh");
        pinyinToWg.put("min", "min");
        pinyinToWg.put("ming", "ming");
        pinyinToWg.put("miu", "miu");
        pinyinToWg.put("mo", "mo");
        pinyinToWg.put("mou", "mou");
        pinyinToWg.put("mu", "mu");
        pinyinToWg.put("na", "na");
        pinyinToWg.put("nai", "nai");
        pinyinToWg.put("nan", "nan");
        pinyinToWg.put("nang", "nang");
        pinyinToWg.put("nao", "nao");
        pinyinToWg.put("ne", "ne");
        pinyinToWg.put("nei", "nei");
        pinyinToWg.put("nen", "nen");
        pinyinToWg.put("neng", "neng");
        pinyinToWg.put("ni", "ni");
        pinyinToWg.put("nia", "nia");
        pinyinToWg.put("nian", "nien");
        pinyinToWg.put("niang", "niang");
        pinyinToWg.put("niao", "niao");
        pinyinToWg.put("nie", "nie");
        pinyinToWg.put("nin", "nin");
        pinyinToWg.put("ning", "ning");
        pinyinToWg.put("niu", "niu");
        pinyinToWg.put("nong", "nung");
        pinyinToWg.put("nou", "nou");
        pinyinToWg.put("nu", "nu");
        pinyinToWg.put("nu:", "nü");
        pinyinToWg.put("nv", "nü");
        pinyinToWg.put("nü", "nü");
        pinyinToWg.put("nuan", "nuan");
        pinyinToWg.put("nu:e", "nüeh");
        pinyinToWg.put("nve", "nüeh");
        pinyinToWg.put("nüe", "nüeh");
        pinyinToWg.put("nun", "nun");
        pinyinToWg.put("nuo", "no");
        pinyinToWg.put("ou", "ou");
        pinyinToWg.put("pa", "p`a");
        pinyinToWg.put("pai", "p`ai");
        pinyinToWg.put("pan", "p`an");
        pinyinToWg.put("pang", "p`ang");
        pinyinToWg.put("pao", "p`ao");
        pinyinToWg.put("pei", "p`ei");
        pinyinToWg.put("pen", "p`en");
        pinyinToWg.put("peng", "p`eng");
        pinyinToWg.put("pi", "p`i");
        pinyinToWg.put("pian", "p`ien");
        pinyinToWg.put("piao", "p`iao");
        pinyinToWg.put("pie", "p`ieh");
        pinyinToWg.put("pin", "p`in");
        pinyinToWg.put("ping", "p`ing");
        pinyinToWg.put("po", "p`o");
        pinyinToWg.put("pou", "p`ou");
        pinyinToWg.put("pu", "p`u");
        pinyinToWg.put("qi", "ch`i");
        pinyinToWg.put("qia", "ch`ia");
        pinyinToWg.put("qian", "ch`ien");
        pinyinToWg.put("qiang", "ch`iang");
        pinyinToWg.put("qiao", "ch`iao");
        pinyinToWg.put("qie", "ch`ieh");
        pinyinToWg.put("qin", "ch`in");
        pinyinToWg.put("qing", "ch`ing");
        pinyinToWg.put("qiong", "ch`iung");
        pinyinToWg.put("qiu", "ch`iu");
        pinyinToWg.put("qu", "ch`ü");
        pinyinToWg.put("quan", "ch`üan");
        pinyinToWg.put("que", "ch`üeh");
        pinyinToWg.put("qun", "ch`ün");
        pinyinToWg.put("ran", "jan");
        pinyinToWg.put("rang", "jang");
        pinyinToWg.put("rao", "jao");
        pinyinToWg.put("re", "je");
        pinyinToWg.put("ren", "jen");
        pinyinToWg.put("reng", "jeng");
        pinyinToWg.put("ri", "jih");
        pinyinToWg.put("rong", "jung");
        pinyinToWg.put("rou", "jou");
        pinyinToWg.put("ru", "ju");
        pinyinToWg.put("ruan", "juan");
        pinyinToWg.put("rui", "jui");
        pinyinToWg.put("run", "jun");
        pinyinToWg.put("ruo", "jo");
        pinyinToWg.put("sa", "sa");
        pinyinToWg.put("sai", "sai");
        pinyinToWg.put("san", "san");
        pinyinToWg.put("sang", "sang");
        pinyinToWg.put("sao", "sao");
        pinyinToWg.put("se", "se");
        pinyinToWg.put("sei", "sei");
        pinyinToWg.put("sen", "sen");
        pinyinToWg.put("seng", "seng");
        pinyinToWg.put("sha", "sha");
        pinyinToWg.put("shai", "shai");
        pinyinToWg.put("shan", "shan");
        pinyinToWg.put("shang", "shang");
        pinyinToWg.put("shao", "shao");
        pinyinToWg.put("she", "she");
        pinyinToWg.put("shei", "shei");
        pinyinToWg.put("shen", "shen");
        pinyinToWg.put("sheng", "sheng");
        pinyinToWg.put("shi", "shih");
        pinyinToWg.put("shong", "shung");
        pinyinToWg.put("shou", "shou");
        pinyinToWg.put("shu", "shu");
        pinyinToWg.put("shua", "shua");
        pinyinToWg.put("shuai", "shuai");
        pinyinToWg.put("shuan", "shuan");
        pinyinToWg.put("shuang", "shuang");
        pinyinToWg.put("shui", "shui");
        pinyinToWg.put("shun", "shun");
        pinyinToWg.put("shuo", "shuo");
        pinyinToWg.put("si", "ssu");
        pinyinToWg.put("song", "sung");
        pinyinToWg.put("sou", "sou");
        pinyinToWg.put("su", "su");
        pinyinToWg.put("suan", "suan");
        pinyinToWg.put("sui", "sui");
        pinyinToWg.put("sun", "sun");
        pinyinToWg.put("suo", "so");
        pinyinToWg.put("ta", "t`a");
        pinyinToWg.put("tai", "t`ai");
        pinyinToWg.put("tan", "t`an");
        pinyinToWg.put("tang", "t`ang");
        pinyinToWg.put("tao", "t`ao");
        pinyinToWg.put("te", "t`e");
        pinyinToWg.put("teng", "t`eng");
        pinyinToWg.put("ti", "t`i");
        pinyinToWg.put("tian", "t`ien");
        pinyinToWg.put("tiao", "t`iao");
        pinyinToWg.put("tie", "t`ieh");
        pinyinToWg.put("ting", "t`ing");
        pinyinToWg.put("tong", "t`ung");
        pinyinToWg.put("tou", "t`ou");
        pinyinToWg.put("tu", "t`u");
        pinyinToWg.put("tuan", "t`uan");
        pinyinToWg.put("tui", "t`ui");
        pinyinToWg.put("tun", "t`un");
        pinyinToWg.put("tuo", "t`o");
        pinyinToWg.put("wa", "wa");
        pinyinToWg.put("wai", "wai");
        pinyinToWg.put("wan", "wan");
        pinyinToWg.put("wang", "wang");
        pinyinToWg.put("wei", "wei");
        pinyinToWg.put("wen", "wen");
        pinyinToWg.put("weng", "weng");
        pinyinToWg.put("wo", "wo");
        pinyinToWg.put("wu", "wu");
        pinyinToWg.put("xi", "hsi");
        pinyinToWg.put("xia", "hsia");
        pinyinToWg.put("xian", "hsien");
        pinyinToWg.put("xiang", "hsiang");
        pinyinToWg.put("xiao", "hsiao");
        pinyinToWg.put("xie", "hsieh");
        pinyinToWg.put("xin", "hsin");
        pinyinToWg.put("xing", "hsing");
        pinyinToWg.put("xiong", "hsiung");
        pinyinToWg.put("xiu", "hsiu");
        pinyinToWg.put("xu", "hsü");
        pinyinToWg.put("xuan", "hsüan");
        pinyinToWg.put("xue", "hsüeh");
        pinyinToWg.put("xun", "hsün");
        pinyinToWg.put("ya", "ya");
        pinyinToWg.put("yai", "yai");
        pinyinToWg.put("yan", "yen");
        pinyinToWg.put("yang", "yang");
        pinyinToWg.put("yao", "yao");
        pinyinToWg.put("ye", "yeh");
        pinyinToWg.put("yi", "i");
        pinyinToWg.put("yin", "yin");
        pinyinToWg.put("ying", "ying");
        pinyinToWg.put("yo", "yo");
        pinyinToWg.put("yong", "yung");
        pinyinToWg.put("you", "yu");
        pinyinToWg.put("yu", "yü");
        pinyinToWg.put("yuan", "yüan");
        pinyinToWg.put("yue", "yüeh");
        pinyinToWg.put("yun", "yün");
        pinyinToWg.put("za", "tsa");
        pinyinToWg.put("zai", "tsai");
        pinyinToWg.put("zan", "tsan");
        pinyinToWg.put("zang", "tsang");
        pinyinToWg.put("zao", "tsao");
        pinyinToWg.put("ze", "tse");
        pinyinToWg.put("zei", "tsei");
        pinyinToWg.put("zen", "tsen");
        pinyinToWg.put("zeng", "tseng");
        pinyinToWg.put("zha", "cha");
        pinyinToWg.put("zhai", "chai");
        pinyinToWg.put("zhan", "chan");
        pinyinToWg.put("zhang", "chang");
        pinyinToWg.put("zhao", "chao");
        pinyinToWg.put("zhe", "che");
        pinyinToWg.put("zhei", "chei");
        pinyinToWg.put("zhen", "chen");
        pinyinToWg.put("zheng", "cheng");
        pinyinToWg.put("zhi", "chih");
        pinyinToWg.put("zhong", "chung");
        pinyinToWg.put("zhou", "chou");
        pinyinToWg.put("zhu", "chu");
        pinyinToWg.put("zhua", "chua");
        pinyinToWg.put("zhuai", "chuai");
        pinyinToWg.put("zhuan", "chuan");
        pinyinToWg.put("zhuang", "chuang");
        pinyinToWg.put("zhui", "chui");
        pinyinToWg.put("zhun", "chun");
        pinyinToWg.put("zhuo", "cho");
        pinyinToWg.put("zi", "tzu");
        pinyinToWg.put("zong", "tsung");
        pinyinToWg.put("zou", "tsou");
        pinyinToWg.put("zu", "tsu");
        pinyinToWg.put("zuan", "tsuan");
        pinyinToWg.put("zui", "tsui");
        pinyinToWg.put("zun", "tsun");
        pinyinToWg.put("zuo", "tso");
    }

    private static HanyuPinyinOutputFormat getOutputFormat(TextType standard) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        switch (standard) {
            case ZHO_PINYIN: {
                format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
                format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
                break;
            }
            case ZHO_PINYIN_NUMERIC: {
                format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
                format.setVCharType(HanyuPinyinVCharType.WITH_V);
                break;
            }
            case ZHO_PINYIN_NOTONE: {
                format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                format.setVCharType(HanyuPinyinVCharType.WITH_V);
                break;
            }
            default: {
                format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
                format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
                break;
            }
        }
        return format;
    }

    public static String separateLatinFromChinese(String input) {
        if (input == null || input.trim().equals("")) {
            return input;
        }
        input = input.replaceAll(
                "([\\p{L}\\p{N}&&[\\p{InBasicLatin}]])([\\p{InCJKUnifiedIdeographs}\\p{InCJKCompatibility}])", "$1 $2");
        input = input.replaceAll(
                "([\\p{InCJKUnifiedIdeographs}\\p{InCJKCompatibility}])([\\p{L}\\p{N}&&[\\p{InBasicLatin}]])", "$1 $2");
        return input;
    }
}