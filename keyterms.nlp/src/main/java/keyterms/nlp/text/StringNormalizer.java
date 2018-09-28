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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import keyterms.util.text.Strings;

public class StringNormalizer {

    public static final char DEFAULT_NGRAM_DELIMITER = '#';

    /**
     * Get a space-free version of the specified text.  Will remove control and other non-printing characters.
     *
     * <p> This method will not alter the original text. </p>
     *
     * @param text The text.
     *
     * @return The trimmed text.
     */
    public static String removeSpaces(CharSequence text) {
        return removeSpaces(text, true);
    }

    /**
     * Get a space-free version of the specified text.
     *
     * <p> This method will not alter the original text. </p>
     *
     * @param text The text.
     * @param removeNonprinting a flag indicating whether to remove non-printing characters
     *
     * @return The trimmed text.
     */
    public static String removeSpaces(CharSequence text, boolean removeNonprinting) {
        StringBuilder sb = new StringBuilder();
        if (Strings.hasText(text)) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (Character.isSpaceChar(c)) {
                    continue;
                }
                if (removeNonprinting && Characters.isNonPrinting(c)) {
                    continue;
                }
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Remove non-printing control characters from string
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public static String clean(CharSequence input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char curChar = input.charAt(i);
            if (!Character.isISOControl(curChar)) {
                sb.append(curChar);
            }
        }
        return sb.toString();
    }

    /**
     * Replace any sequence or two or more spaces, where space is as defined by the Unicode standard,
     * with a single space character (U+0200).
     * This method does not trim the resulting string.
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public static String squinch(CharSequence input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean previousWasSpace = false;
        for (int i = 0; i < input.length(); i++) {
            char curChar = input.charAt(i);
            boolean curCharIsSpace = Character.isSpaceChar(curChar);
            if (!curCharIsSpace) {
                sb.append(curChar);
            } else {
                if (!previousWasSpace) {
                    sb.append(" ");
                }
                previousWasSpace = true;
            }
        }
        return sb.toString();
    }

    /**
     * Replace any sequence or two or more nonprinting characters, meaning space or control characters as defined by
     * the unicode standard, with a single space character (U+0200).
     * This method does not trim the resulting string.
     *
     * @param input the input text
     *
     * @return a string with no control characters, and only single space characters as white space
     */
    public static String squinchClean(CharSequence input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean previousWasNonprinting = false;
        for (int i = 0; i < input.length(); i++) {
            char curChar = input.charAt(i);
            boolean curCharIsNonprinting = Characters.isNonPrinting(curChar);
            if (!curCharIsNonprinting) {
                sb.append(curChar);
            } else {
                if (!previousWasNonprinting) {
                    sb.append(" ");
                }
                previousWasNonprinting = curCharIsNonprinting;
            }
        }
        return sb.toString();
    }

    /**
     * Remove all spaces, where space is as defined by the Unicode standard.
     * This method does not trim the resulting string.
     *
     * @param input the input text
     *
     * @return a space-free string
     */
    public static String scrunch(CharSequence input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isSpaceChar(input.charAt(i))) {
                sb.append(input.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * Remove all spaces and control characters, where space and control is as defined by the Unicode standard.
     * This method does not trim the resulting string.
     *
     * @param input the input text
     *
     * @return a space-free string
     */
    public static String scrunchClean(CharSequence input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (!Characters.isNonPrinting(input.charAt(i))) {
                sb.append(input.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * Determines whether a string is either null or is composed completely of white space, where white space is defined
     * according to the Unicode standard
     *
     * @param input the input text
     *
     * @return a flag indicating whether the string is null or zero length when compressed
     */
    public static boolean isNullOrWhiteSpace(String input) {
        return ((input == null) || (scrunch(input).isEmpty()));
    }

    /**
     * Determines whether a string is either null or is composed completely of white space or control characters,
     * where white space and control characters are as defined by to the Unicode standard
     *
     * @param input the input text
     *
     * @return a flag indicating whether the string is null or zero length when compressed
     */
    public static boolean isNullOrCleanWhiteSpace(String input) {
        return ((input == null) || (scrunchClean(input).isEmpty()));
    }

    /**
     * Remove combining marks and other adornments that modify letters
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public static String removeDiacritics(CharSequence input) {
        if (input == null) {
            return null;
        }
        input = Normalizer.normalize(input, Normalizer.Form.NFKD);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (!Characters.isDiacritic(input.charAt(i))) {
                sb.append(input.charAt(i));
            }
        }
        String output = sb.toString();
        output = Normalizer.normalize(output, Normalizer.Form.NFC);
        return output;
    }

    /**
     * Remove combining marks and other adornments that modify letters
     *
     * @param input the input text
     *
     * @return the processed text
     */
    public static String removePunctuation(CharSequence input) {
        if (input == null) {
            return null;
        }
        input = Normalizer.normalize(input, Normalizer.Form.NFKD);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (!Characters.isPunctuation(input.charAt(i))) {
                sb.append(input.charAt(i));
            }
        }
        String output = sb.toString();
        output = Normalizer.normalize(output, Normalizer.Form.NFC);
        return output;
    }

    // NB 01/17/2018: added bigramify() and trigramify()
    public static List<String> bigramify(String input, char delimiter) {
        List<String> bigrams = new ArrayList<>();

        if (input.length() > 0) {
            char[] bounded = (delimiter + input + delimiter).toCharArray();
            for (int i = 0; i < bounded.length - 1; i++) {
                bigrams.add(String.join("",
                        Character.toString(bounded[i]),
                        Character.toString(bounded[i + 1]))
                );
            }
        }

        return bigrams;
    }

    public static List<String> bigramify(String input) {
        return bigramify(input, DEFAULT_NGRAM_DELIMITER);
    }

    public static List<String> trigramify(String input, char delimiter) {
        List<String> trigrams = new ArrayList<>();
        String stringDelimiter = Character.toString(delimiter) + delimiter;

        if (input.length() > 0) {
            char[] bounded = (stringDelimiter + input + stringDelimiter).toCharArray();
            for (int i = 0; i < bounded.length - 2; i++) {
                trigrams.add(String.join("",
                        Character.toString(bounded[i]),
                        Character.toString(bounded[i + 1]),
                        Character.toString(bounded[i + 2]))
                );
            }
        }

        return trigrams;
    }

    public static List<String> trigramify(String input) {
        return trigramify(input, DEFAULT_NGRAM_DELIMITER);
    }

    // NB 11/10/2017: refactored this method and incorporated the removeLineBreaks parameter (was previously ignored)
    public static String normalize(String input, boolean removeLineBreaks, boolean removeSpace, boolean
            removeControl, boolean removePunctuation, boolean normalizePunctuation, boolean transliteratePunctuation,
            boolean removeDiacritics, boolean normalizeCase, Normalizer.Form outputForm) {

        if (input == null) {
            return null;
        }

        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        input = input.replace('\u00B7', ' ');  //  00B7 == '·', dot char in foreign names represented in Chinese

        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (!((removeSpace && Character.isSpaceChar(c))
                    || (removeControl && Character.isISOControl(c))
                    || (removePunctuation && Characters.isPunctuation(c))
                    || (removeDiacritics && Characters.isDiacritic(c))
                    || (removeLineBreaks && !String.valueOf(c).matches(".")))) {
                sb.append(c);
            }
        }

        String output = Normalizer.normalize(sb.toString(), Normalizer.Form.NFC);

        if (normalizeCase) {
            output = output.toLowerCase();
            // TODO: Chinese simplification here????
        }

        return Normalizer.normalize(output, outputForm);
    }

    public static String squinch(String input, boolean removeLineBreaks) {
        if (input == null) {
            return null;
        }

        if (removeLineBreaks) {
            input = input.replaceAll("[-]\\n\\r\\p{Zl}\\p{Zp}", "-");
            input = input.replaceAll("\\n\\r\\p{Zl}\\p{Zp}", " ");
        }
        input = input.trim();
        input = input.replaceAll("[\\s\\t]{2,}", " ");
        return input;
    }

    public static String scrunch(String input, boolean removeLineBreaks) {
        if (input == null) {
            return null;
        }
        input = input.trim();
        input = input.replaceAll("[\\s\\t]", "");
        if (removeLineBreaks) {
            input = input.replaceAll("[-]\\n\\r\\p{Zl}\\p{Zp}", "");
            input = input.replaceAll("\\n\\r\\p{Zl}\\p{Zp}", " ");
        }
        return input;
    }

    public static String normalizeForIndex(String input, boolean removeWhitespaceForIndex) {
        String idxText = StringNormalizer
                .normalize(input, true/*removeNewLine*/, removeWhitespaceForIndex/*removeSpace*/, true/*removeControl*/,
                        true/*removePunctuation*/, true/*normalizePunctuation*/, true/*transliteratePunctuation*/,
                        true/*removeDiacritics*/,
                        true/*normalizeCase*/, Normalizer.Form.NFKD);
        //idxText = get a simplifier in here
        return idxText;
    }

    public static String normalizeForIndex(String input) {
        return StringNormalizer.normalize(input, true/*removeNewLine*/, true/*removeSpace*/, true/*removeControl*/,
                true/*removePunctuation*/,
                true/*normalizePunctuation*/, true/*transliteratePunctuation*/, true/*removeDiacritics*/,
                true/*normalizeCase*/,
                Normalizer.Form.NFKD);
    }

    public static String normalizeForScoring(String input) {
        return StringNormalizer.normalize(input, true/*removeNewLine*/, true/*removeSpace*/, true/*removeControl*/,
                true/*removePunctuation*/,
                false/*normalizePunctuation*/, false/*transliteratePunctuation*/, true/*removeDiacritics*/, false
                /*normalizeCase*/,
                Normalizer.Form.NFKD);
    }

    public static String normalizeForDisplay(String input) {
        return StringNormalizer
                .normalize(input, true/*removeNewLine*/, false/*removeSpace*/, true/*removeControl*/,
                        false/*removePunctuation*/,
                        true/*normalizePunctuation*/, false/*transliteratePunctuation*/, false/*removeDiacritics*/,
                        false/*normalizeCase*/, Normalizer.Form.NFKC);
    }

    public static String removeAllNonWordChars(String input, boolean removeSpaces, boolean removeLineBreaks) {
        if (input == null) {
            return input;
        }
        input = squinch(input, removeLineBreaks);
        input = Normalizer.normalize(input, Normalizer.Form.NFKD);
        input = input.replaceAll("[\\p{M}\\p{P}\\p{S}\\p{C}\\p{Lm}]",
                "");  // control, formatting, symbols, marks, modifier letters
        input = input.replaceAll("[\\u2027\\u00B7]",
                "");  // the dot used in Chinese to separate tokens in foreign person names
        //input = input.replaceAll("[_\\-]", "");    // remove hyphens, underscores and other word joiners : waffle:
        // these are wordchars under \w
        input = input.replaceAll("\u00DF", "ss");  // Latin ligature not handled by NFKD
        input = input.replaceAll("\u00E6", "ae");  // Latin ligature not handled by NFKD
        input = input.replaceAll("\u0153", "oe");  // Latin ligature not handled by NFKD
        input = input.replaceAll("\u1D6B", "ue");  // Latin ligature not handled by NFKD
        if (removeSpaces) {
            input = input.replaceAll("\\s\\t\\n", "");  // \p{Z} ?
        }
        input = Normalizer.normalize(input, Normalizer.Form.NFKC);
        return input;
    }

    /**
     * Determines whether the input string is entirely punctuation,no white space allowed
     *
     * @param input the input text
     *
     * @return true if the entire string consists of punctuation, false otherwise
     */
    public static boolean isPunct(String input) {
        return isPunct(input, false);
    }

    /**
     * Determines whether the input string is entirely punctuation, optionally allowing spaces
     *
     * @param input the input text
     * @param allowSpaces a flag indicating whether to ignore spaces
     *
     * @return true if the entire string consists of punctuation and spaces if allowed, false otherwise.  At least one
     * punctuation character must be found in the string.
     */
    public static boolean isPunct(String input, boolean allowSpaces) {
        if (input == null || input.trim().equals("")) {
            return false;
        }
        boolean onePunctFound = false;
        for (int i = 0; i < input.length(); i++) {
            char curChar = input.charAt(i);
            if (!Characters.isPunctuation(curChar)) {
                if (allowSpaces && Character.isSpaceChar(curChar)) {
                    continue;
                }
                return false;
            } else {
                onePunctFound = true;
            }
        }
        return onePunctFound;
    }
}