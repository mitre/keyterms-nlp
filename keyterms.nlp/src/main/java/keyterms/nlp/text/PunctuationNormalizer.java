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

import keyterms.nlp.unicode.UnicodeNormalizationData;

/**
 * This class normalizes punctuation from various Unicode ranges into the common Latin blocks, including:
 *
 * <ul>
 * <li> Basic Latin </li>
 * <li> Latin Supplement-1 </li>
 * <li> Latin Supplement-2 </li>
 * </ul>
 *
 * <p> This utility preferentially reads normalization mappings from a file.  If the mapping file cannot be found,
 * built-in mappings are used.  When no mapping is available for a given character, standard Unicode canonical
 * decomposition is applied.  (NOTE:  CURRENTLY NO MAPPING FILE ) </p>
 */
public final class PunctuationNormalizer {

    public static String normalize(CharSequence input) {
        return normalize(input,/*normalizeSpace*/true,/*removeControlChars*/true,/*normalizeEmoji*/ true);
    }

    public static String normalize(CharSequence input, boolean normalizeSpace) {
        return normalize(input, normalizeSpace,/*removeControlChars*/true,/*normalizeEmoji*/ true);
    }

    public static String normalize(CharSequence input, boolean normalizeSpace, boolean removeControlChars) {
        return normalize(input, normalizeSpace, removeControlChars,/*normalizeEmoji*/ true);
    }

    /**
     * Normalize punctuation and punctuation-like symbols into common Latin blocks.
     * Note:  This method does not trim or squnich the resulting string, nor does it handle diacritics such
     * as accents, etc.
     *
     * Note, emoticion detection will be incorporated in later versions
     *
     * @param input the input text
     * @param normalizeSpace a flag indicating whether to normalize whitespace
     * @param removeControlChars a flag indicating whether to remove control characters
     * @param normalizeEmoji a flag indicating whether to normalize emoji
     *
     * @return a space-free string
     */
    //  public static String normalizePunctuation(CharSequence input, boolean canonical, boolean transliterating) {
    public static String normalize(CharSequence input, boolean normalizeSpace, boolean removeControlChars, boolean
            normalizeEmoji) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            Character curChar = input.charAt(i);
            if (Character.isSpaceChar(curChar)) {
                if (normalizeSpace) {
                    sb.append(" ");
                } else {
                    sb.append(curChar);
                }
            } else {
                if (Character.isISOControl(curChar)) {
                    if (curChar == '\t' && normalizeSpace) {
                        sb.append("    ");
                        continue;
                    }
                    if (removeControlChars) {
                        continue;
                    }
                    sb.append(curChar);
                } else {
                    // --here insert a trie containing known emoticon sequences and mappings for them to emoji
                    // --consider adding another boolean controlling whether to transliterate digits to their
                    // numerical values
                    // --should also perhaps verify whether the character being normalized is a punctuation symbol (
                    //  regardless of what GeneralCategory it falls into)
                    // -- @todo get rid of hard-coded "delete"
                    //            String normalization = unicodeData.getTransliteration(curChar);
                    String normalization = UnicodeNormalizationData.getReplacement(curChar.toString());
                    if ("delete".equalsIgnoreCase(normalization)) {
                        continue;
                    }
                    if (normalization != null) {
                        sb.append(normalization);
                    } else {
                        sb.append(curChar);
                    }
                }
            }
        }
        return sb.toString();
    }

    private PunctuationNormalizer() {
        super();
    }
}