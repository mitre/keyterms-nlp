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
import keyterms.nlp.text.StringNormalizer;
import keyterms.nlp.transliterate.Transliterator;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;

public class ZhoEng_HanyuPinyin
        extends Transliterator {
    /**
     * Tone format for transliterator output
     */
    private ToneFormat outputToneFormat;

    public ZhoEng_HanyuPinyin() {
        this(ToneFormat.DIACRITIC_TONE);
    }

    public ZhoEng_HanyuPinyin(ToneFormat toneFormat) {
        super(true, "zho-eng/HanyuPinyin_" + toneFormat.getLabel());
        outputToneFormat = toneFormat;
    }

    /**
     * Get the logging topic for this object.
     *
     * @return The logging topic for this object.
     */
    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
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
     * Apply the transliteration
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public String apply(CharSequence input) {
        if (input == null) {
            return "";
        }
        //@todo  ALLOW FOR EXTERNAL NORMALIZER
        String normalizedInput = ZhoUtils.normalizeForDisplay(input.toString());
        normalizedInput = ZhoUtils.SeparateLatinFromChinese(normalizedInput);
        return getPinyinTransliteration(normalizedInput);
    }

    public String getPinyinTransliteration(String input) {

        if (StringNormalizer.isNullOrWhiteSpace(input)) {
            return "";
        }
        input = ZhoUtils.SeparateLatinFromChinese(input);
        StringBuilder sb = new StringBuilder();
        boolean spaceWasInsertedAfterPrevChar = false;
        for (int c = 0; c < input.length(); c++) {
            char ch = input.charAt(c);
            String[] pinyinStrings;
            try {
                HanyuPinyinOutputFormat outputFormat = ZhoUtils.getOutputFormat(outputToneFormat);
                pinyinStrings = PinyinHelper.toHanyuPinyinStringArray(ch, outputFormat);
                if (pinyinStrings != null && pinyinStrings.length > 0) {
                    sb.append(pinyinStrings[0]);
                } else {
                    if (Characters.isPunctuation(ch) && spaceWasInsertedAfterPrevChar) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    sb.append(ch);
                }
                if (c < input.length() - 1 && (Character.isIdeographic(ch)
                        && !Characters.isPunctuation(ch)) || ch == ',' || ch == 65292) {
                    sb.append(" ");
                    spaceWasInsertedAfterPrevChar = true;
                } else {
                    spaceWasInsertedAfterPrevChar = false;
                }
            } catch (Exception eek) {
                getLogger().error("Unexpected error.", eek);
            }
        }
        String results = sb.toString();
        results = StringNormalizer.squinchClean(results);
        return results;
    }
}