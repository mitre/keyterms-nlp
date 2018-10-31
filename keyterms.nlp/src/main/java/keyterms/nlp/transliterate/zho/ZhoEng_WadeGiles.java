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

package keyterms.nlp.transliterate.zho;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.nlp.text.Characters;
import keyterms.nlp.transliterate.Transliterator;

public class ZhoEng_WadeGiles
        extends Transliterator {

    private final Logger myLogger = LoggerFactory.getLogger(ZhoEng_WadeGiles.class);

    /**
     * Tone format for transliterator output
     */
    private ToneFormat outputToneFormat;

    /**
     * HanyuPinyin transliterator for converting Han characters to Pinyin, then pinyin is converted to Wade Giles
     */
    private final ZhoEng_HanyuPinyin pinyinTransliterator;

    public ZhoEng_WadeGiles() {
        this(new ZhoEng_HanyuPinyin_Numeric());
    }

    /**
     * Constructor
     *
     * @param hanyuPinyinTransliterator the hanyu-pinyin transliterator
     */
    public ZhoEng_WadeGiles(ZhoEng_HanyuPinyin hanyuPinyinTransliterator) {
        super(true, "zho-eng/WadeGiles_" + hanyuPinyinTransliterator.getOutputToneFormat().getLabel());
        pinyinTransliterator = hanyuPinyinTransliterator;
    }

    /**
     * Determine if this transliterator is set to produce tonal, numeric or tone-free pinyin
     *
     * @return the output tone format
     */
    public ToneFormat getOutputToneFormat() {
        return outputToneFormat;
    }

    /**
     * Specify whether this transliterator is set to produce tonal, numeric or tone-free pinyin
     *
     * @param outputToneFormatl the output tone format
     */
    public void setOutputToneFormat(ToneFormat outputToneFormatl) {
        outputToneFormat = outputToneFormatl;
    }

    /**
     * Apply Wade-Giles transliteration to
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public String apply(CharSequence input) {
        if (pinyinTransliterator == null) {
            myLogger.warn("Cannot perform Wade-Giles romanization, missing HanyuPinyin transliterator.");
            return input.toString();
        }
        String numericPinyin = pinyinTransliterator.apply(input);
        if (numericPinyin == null) {
            return numericPinyin;
        }
        StringBuilder wadeGiles = new StringBuilder();
        String[] sa = numericPinyin.split("\\s+");
        if (sa != null) {
            int s = 0;
            for (String syllable : sa) {
                boolean useTones = (outputToneFormat == ToneFormat.DIACRITIC_TONE)
                        || (outputToneFormat == ToneFormat.NUMERIC_TONE);

                StringBuilder endMatter = new StringBuilder();
                for (int i = syllable.length() - 1; i >= 0; i--) {
                    char curChar = syllable.substring(i, i + 1).charAt(0);
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
                String wgSyllab = ZhoUtils.convertToWgSyllable(syllable, useTones);
                if (wgSyllab != null && !wgSyllab.trim().equals("")) {
                    wadeGiles.append(wgSyllab);
                } else {
                    if (Characters.isPunctuation(syllable.charAt(0))) {
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
}