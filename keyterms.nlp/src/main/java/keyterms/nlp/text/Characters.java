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
import java.util.Set;

import keyterms.util.collect.Bags;

/**
 * Methods for identifying characters from different scripts / writing systems.
 */
public final class Characters {
    /**
     * Punctuation character types.
     */
    private static final Set<Byte> PUNCTUATION_TYPES = Bags.staticSet(
            Character.CONNECTOR_PUNCTUATION,
            Character.CURRENCY_SYMBOL,
            Character.DASH_PUNCTUATION,
            Character.ENCLOSING_MARK,
            Character.END_PUNCTUATION,
            Character.FINAL_QUOTE_PUNCTUATION,
            Character.INITIAL_QUOTE_PUNCTUATION,
            Character.MATH_SYMBOL,
            Character.OTHER_SYMBOL,
            Character.OTHER_PUNCTUATION,
            Character.START_PUNCTUATION
    );

    /**
     * Non-printing character types.
     */
    private static final Set<Byte> NON_PRINTING_TYPES = Bags.staticSet(
            Character.CONTROL,
            Character.SPACE_SEPARATOR,
            Character.LINE_SEPARATOR,
            Character.DIRECTIONALITY_PARAGRAPH_SEPARATOR
    );

    /**
     * Diacritic character types.
     */
    private static final Set<Byte> DIACRITIC_TYPES = Bags.staticSet(
            Character.COMBINING_SPACING_MARK,
            Character.ENCLOSING_MARK,
            Character.NON_SPACING_MARK
    );

    /**
     * Arabic character blocks.
     */
    private static final Set<Character.UnicodeBlock> ARABIC_BLOCKS = Bags.staticSet(
            Character.UnicodeBlock.ARABIC,
            Character.UnicodeBlock.ARABIC_SUPPLEMENT,
            Character.UnicodeBlock.ARABIC_EXTENDED_A,
            Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A,
            Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B,
            Character.UnicodeBlock.ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS,
            Character.UnicodeBlock.RUMI_NUMERAL_SYMBOLS
    );

    /**
     * CJK character blocks.
     */
    private static final Set<Character.UnicodeBlock> CJK_BLOCKS = Bags.staticSet(
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D,
            Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS,
            Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT,
            Character.UnicodeBlock.CJK_COMPATIBILITY,
            Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS,
            Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION,
            Character.UnicodeBlock.CJK_STROKES,
            Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT,
            Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS
    );

    /**
     * Latin character blocks.
     */
    private static final Set<Character.UnicodeBlock> LATIN_BLOCKS = Bags.staticSet(
            Character.UnicodeBlock.BASIC_LATIN,
            Character.UnicodeBlock.LATIN_1_SUPPLEMENT,
            Character.UnicodeBlock.LATIN_EXTENDED_A,
            Character.UnicodeBlock.LATIN_EXTENDED_B,
            Character.UnicodeBlock.LATIN_EXTENDED_C,
            Character.UnicodeBlock.LATIN_EXTENDED_D,
            Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL,
            Character.UnicodeBlock.NUMBER_FORMS,
            Character.UnicodeBlock.SUPERSCRIPTS_AND_SUBSCRIPTS,
            Character.UnicodeBlock.ENCLOSED_ALPHANUMERICS,
            Character.UnicodeBlock.ENCLOSED_ALPHANUMERIC_SUPPLEMENT,
            Character.UnicodeBlock.MATHEMATICAL_ALPHANUMERIC_SYMBOLS  // contains some Greek characters
    );

    /**
     * Determine if the specified character is punctuation.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is punctuation.
     */
    public static boolean isPunctuation(char character) {
        return ((!Character.isLetterOrDigit(character)) &&
                (!Character.isWhitespace(character)) &&
                (isPunctuationType(character)));
    }

    /**
     * Determine if the specified text is a single valid Unicode punctuation character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid Unicode punctuation character.
     */
    public static boolean isPunctuation(CharSequence text) {
        boolean isPunctuation = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isPunctuation = isPunctuation(text.charAt(0));
            } else {
                isPunctuation = ((length == 2) && (isPunctuation(text.toString().toCharArray())));
            }
        }
        return isPunctuation;
    }

    /**
     * Determine if the specified character array is a single valid Unicode punctuation character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode punctuation character. </p>
     *
     * @param characters The characters.
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode punctuation character.
     */
    public static boolean isPunctuation(char[] characters) {
        boolean isPunctuation = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isPunctuation = isPunctuation(characters[0]);
            } else {
                isPunctuation = ((length == 2) &&
                        (Character.isSurrogatePair(characters[0], characters[1])) &&
                        isPunctuationType(Character.toCodePoint(characters[0], characters[1])));
            }
        }
        return isPunctuation;
    }

    /**
     * Determine if the specified Unicode code point represents a type of punctuation.
     *
     * @param codePoint The Unicode code point.
     *
     * @return A flag indicating whether the specified Unicode code point represents a type of punctuation.
     */
    private static boolean isPunctuationType(int codePoint) {
        return ((PUNCTUATION_TYPES.contains((byte)Character.getType(codePoint))) ||
                (isHalfOrFullWidthPunctuation((char)codePoint)));
    }

    /**
     * Determine if the specified unicode character falls within a run of punctuation symbols inside of the Half-width
     * and Full-width Forms block.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified unicode character falls within a run of punctuation symbols
     * inside of the Half-width and Full-width Forms block.
     */
    private static boolean isHalfOrFullWidthPunctuation(char character) {
        return ((('\uFF01' <= character) && (character <= '\uFF0F')) ||
                (('\uFF1A' <= character) && (character <= '\uFF20')) ||
                (('\uFF3B' <= character) && (character <= '\uFF40')) ||
                (('\uFF5B' <= character) && (character <= '\uFF64')));
    }

    /**
     * Determine if the specified character is non-printing.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is non-printing.
     */
    public static boolean isNonPrinting(char character) {
        return isNonPrintingType(character);
    }

    /**
     * Determine if the specified text is a single valid Unicode non-printing character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid Unicode non-printing character.
     */
    public static boolean isNonPrinting(CharSequence text) {
        boolean isNonPrinting = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isNonPrinting = isNonPrinting(text.charAt(0));
            } else {
                isNonPrinting = ((length == 2) && (isNonPrinting(text.toString().toCharArray())));
            }
        }
        return isNonPrinting;
    }

    /**
     * Determine if the specified character array is a single valid Unicode non-printing character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode non-printing character. </p>
     *
     * @param characters The characters.
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode non-printing character.
     */
    public static boolean isNonPrinting(char[] characters) {
        boolean isNonPrinting = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isNonPrinting = isNonPrinting(characters[0]);
            } else {
                isNonPrinting = ((length == 2) &&
                        (Character.isSurrogatePair(characters[0], characters[1])) &&
                        (isNonPrintingType(Character.toCodePoint(characters[0], characters[1]))));
            }
        }
        return isNonPrinting;
    }

    /**
     * Determine if the specified Unicode code point represents a type of non-printing character.
     *
     * @param codePoint The Unicode code point.
     *
     * @return A flag indicating whether the specified Unicode code point represents a type of non-printing character.
     */
    private static boolean isNonPrintingType(int codePoint) {
        return NON_PRINTING_TYPES.contains((byte)Character.getType(codePoint));
    }

    /**
     * Determine if the specified character is a diacritic character.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is diacritic character.
     */
    public static boolean isDiacritic(char character) {
        boolean isDiacritic = false;
        String normalized = Normalizer.normalize(String.valueOf(character), Normalizer.Form.NFD);
        for (char c : normalized.toCharArray()) {
            isDiacritic |= isDiacriticType(c);
        }
        return isDiacritic;
    }

    /**
     * Determine if the specified text is a single valid Unicode diacritic character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid Unicode diacritic character.
     */
    public static boolean isDiacritic(CharSequence text) {
        boolean isDiacritic = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isDiacritic = isDiacritic(text.charAt(0));
            } else {
                isDiacritic = ((length == 2) && (isDiacritic(text.toString().toCharArray())));
            }
        }
        return isDiacritic;
    }

    /**
     * Determine if the specified character array is a single valid Unicode diacritic character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode diacritic character. </p>
     *
     * @param characters The characters.
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode diacritic character.
     */
    public static boolean isDiacritic(char[] characters) {
        boolean isDiacritic = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isDiacritic = isDiacritic(characters[0]);
            } else {
                isDiacritic = ((length == 2) &&
                        (Character.isSurrogatePair(characters[0], characters[1])) &&
                        (isDiacriticType(Character.toCodePoint(characters[0], characters[1]))));
            }
        }
        return isDiacritic;
    }

    /**
     * Determine if the specified Unicode code point represents a type of diacritic character.
     *
     * @param codePoint The Unicode code point.
     *
     * @return A flag indicating whether the specified Unicode code point represents a type of diacritic character.
     */
    private static boolean isDiacriticType(int codePoint) {
        return DIACRITIC_TYPES.contains((byte)Character.getType(codePoint));
    }

    /**
     * Determine if the specified character is a valid Unicode Arabic script character.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is a valid Unicode Arabic script character.
     */
    public static boolean isArabic(char character) {
        return isArabic(Character.UnicodeBlock.of(character));
    }

    /**
     * Determine if the specified text is a single valid Arabic script character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid Arabic script character.
     */
    public static boolean isArabic(CharSequence text) {
        boolean isArabic = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isArabic = isArabic(text.charAt(0));
            } else {
                isArabic = ((length == 2) && (isArabic(text.toString().toCharArray())));
            }
        }
        return isArabic;
    }

    /**
     * Determine if the specified character array is a single valid Unicode Arabic script character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode Arabic script character. </p>
     *
     * @param characters a char array
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode Arabic script
     * character.
     */
    public static boolean isArabic(char[] characters) {
        boolean isArabic = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isArabic = isArabic(characters[0]);
            } else {
                isArabic = ((length == 2) &&
                        (Character.isSurrogatePair(characters[0], characters[1])) &&
                        (isArabic(Character.UnicodeBlock.of(Character.toCodePoint(characters[0], characters[1])))));
            }
        }
        return isArabic;
    }

    /**
     * Determine if the specified unicode block represents Arabic characters.
     *
     * @param unicodeBlock The Unicode block.
     *
     * @return A flag indicating whether the specified unicode block represents Arabic characters.
     */
    private static boolean isArabic(Character.UnicodeBlock unicodeBlock) {
        return ARABIC_BLOCKS.contains(unicodeBlock);
    }

    /**
     * Determine if the specified character is a valid Unicode CJK script character.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is a valid Unicode CJK script character.
     */
    public static boolean isCJK(char character) {
        return isCJK(Character.UnicodeBlock.of(character));
    }

    /**
     * Determine if the specified text is a single valid CJK script character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid CJK script character.
     */
    public static boolean isCJK(CharSequence text) {
        boolean isCJK = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isCJK = isCJK(text.charAt(0));
            } else {
                isCJK = ((length == 2) && (isCJK(text.toString().toCharArray())));
            }
        }
        return isCJK;
    }

    /**
     * Determine if the specified character array is a single valid Unicode CJK script character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode CJK script character. </p>
     *
     * @param characters a char array
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode CJK script character.
     */
    public static boolean isCJK(char[] characters) {
        boolean isCJK = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isCJK = isCJK(characters[0]);
            } else {
                isCJK = ((length == 2) &&
                        (Character.isSurrogatePair(characters[0], characters[1])) &&
                        (isCJK(Character.UnicodeBlock.of(Character.toCodePoint(characters[0], characters[1])))));
            }
        }
        return isCJK;
    }

    /**
     * Determine if the specified character is a valid Unicode CJK logograph character.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is a valid Unicode CJK logograph character.
     */
    public static boolean isCJKLogograph(char character) {
        return ((isCJK(character)) && (Character.isIdeographic(character)));
    }

    /**
     * Determine if the specified text is a single valid CJK script character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid CJK logograph character.
     */
    public static boolean isCJKLogograph(CharSequence text) {
        boolean isCJKLogograph = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isCJKLogograph = isCJKLogograph(text.charAt(0));
            } else {
                isCJKLogograph = ((length == 2) && (isCJKLogograph(text.toString().toCharArray())));
            }
        }
        return isCJKLogograph;
    }

    /**
     * Determine if the specified character array is a single valid Unicode CJK logograph character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode CJK logograph character. </p>
     *
     * @param characters a char array
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode CJK logograph
     * character.
     */
    public static boolean isCJKLogograph(char[] characters) {
        boolean isCJKLogograph = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isCJKLogograph = isCJKLogograph(characters[0]);
            } else {
                int codePoint = Character.toCodePoint(characters[0], characters[1]);
                isCJKLogograph = ((length == 2) &&
                        (Character.isSurrogatePair(characters[0], characters[1])) &&
                        (Character.isLetter(codePoint)) &&
                        (isCJK(Character.UnicodeBlock.of(codePoint))));
            }
        }
        return isCJKLogograph;
    }

    /**
     * Determine if the specified character is a valid Unicode CJK punctuation character.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is a valid Unicode CJK punctuation character.
     */
    public static boolean isCJKPunctuation(char character) {
        return ((isCJK(character)) && (isPunctuation(character)));
    }

    /**
     * Determine if the specified text is a single valid CJK script character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid CJK punctuation character.
     */
    public static boolean isCJKPunctuation(CharSequence text) {
        boolean isCJKPunctuation = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isCJKPunctuation = isCJKPunctuation(text.charAt(0));
            } else {
                isCJKPunctuation = ((length == 2) && (isCJKPunctuation(text.toString().toCharArray())));
            }
        }
        return isCJKPunctuation;
    }

    /**
     * Determine if the specified character array is a single valid Unicode CJK punctuation character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode CJK punctuation character. </p>
     *
     * @param characters a char array
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode CJK punctuation
     * character.
     */
    public static boolean isCJKPunctuation(char[] characters) {
        boolean isCJKPunctuation = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isCJKPunctuation = isCJKPunctuation(characters[0]);
            } else {
                int codePoint = Character.toCodePoint(characters[0], characters[1]);
                isCJKPunctuation = ((length == 2) &&
                        (Character.isSurrogatePair(characters[0], characters[1])) &&
                        (Character.isLetter(codePoint)) &&
                        (isCJKPunctuation(characters)));
            }
        }
        return isCJKPunctuation;
    }

    /**
     * Determine if the specified unicode block represents CJK characters.
     *
     * @param unicodeBlock The Unicode block.
     *
     * @return A flag indicating whether the specified unicode block represents CJK characters.
     */
    private static boolean isCJK(Character.UnicodeBlock unicodeBlock) {
        return CJK_BLOCKS.contains(unicodeBlock);
    }

    /**
     * Determine if the specified character is a Latin script character.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is a Latin script character.
     */
    public static boolean isLatin(char character) {
        return ((isLatin(Character.UnicodeBlock.of(character))) || (isHalfOrFullWidthLatin(character)));
    }

    /**
     * Determine if the specified text is a single valid Latin script character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid Latin script character.
     */
    public static boolean isLatin(CharSequence text) {
        boolean isLatin = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isLatin = isLatin(text.charAt(0));
            } else {
                isLatin = ((length == 2) && (isLatin(text.toString().toCharArray())));
            }
        }
        return isLatin;
    }

    /**
     * Determine if the specified character array is a single valid Unicode Latin script character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode Latin script character. </p>
     *
     * @param characters The characters.
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode Latin script character.
     */
    public static boolean isLatin(char[] characters) {
        boolean isLatin = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isLatin = isLatin(characters[0]);
            } else {
                isLatin = ((length == 2) &&
                        (Character.isSurrogatePair(characters[0], characters[1])) &&
                        (isLatin(Character.UnicodeBlock.of(Character.toCodePoint(characters[0], characters[1])))));
            }
        }
        return isLatin;
    }

    /**
     * Determine if the specified character is a Latin script letter.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is a Latin script letter.
     */
    public static boolean isLatinLetter(char character) {
        return ((isLatin(character)) && (Character.isLetter(character)));
    }

    /**
     * Determine if the specified text is a single valid Latin script letter.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid Latin script letter.
     */
    public static boolean isLatinLetter(CharSequence text) {
        boolean isLatinLetter = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isLatinLetter = isLatin(text.charAt(0));
            } else {
                isLatinLetter = ((length == 2) && (isLatinLetter(text.toString().toCharArray())));
            }
        }
        return isLatinLetter;
    }

    /**
     * Determine if the specified character array is a single valid Unicode Latin script letter.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode Latin script letter. </p>
     *
     * @param characters The characters.
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode letter.
     */
    public static boolean isLatinLetter(char[] characters) {
        boolean isLatinLetter = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isLatinLetter = isLatinLetter(characters[0]);
            } else {
                int codePoint = Character.toCodePoint(characters[0], characters[1]);
                isLatinLetter = ((length == 2) &&
                        (Character.isLetter(codePoint)) &&
                        (isLatin(Character.UnicodeBlock.of(codePoint))));
            }
        }
        return isLatinLetter;
    }

    /**
     * Determine if the specified character is a Latin script digit.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is a Latin script digit.
     */
    public static boolean isLatinDigit(char character) {
        return ((isLatin(character)) && (Character.isDigit(character)));
    }

    /**
     * Determine if the specified text is a single valid Latin script digit.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid Latin script digit.
     */
    public static boolean isLatinDigit(CharSequence text) {
        boolean isLatinDigit = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isLatinDigit = isLatin(text.charAt(0));
            } else {
                isLatinDigit = ((length == 2) && (isLatinDigit(text.toString().toCharArray())));
            }
        }
        return isLatinDigit;
    }

    /**
     * Determine if the specified character array is a single valid Unicode Latin script digit.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode Latin script digit. </p>
     *
     * @param characters The characters.
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode digit.
     */
    public static boolean isLatinDigit(char[] characters) {
        boolean isLatinDigit = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isLatinDigit = isLatinDigit(characters[0]);
            } else {
                int codePoint = Character.toCodePoint(characters[0], characters[1]);
                isLatinDigit = ((length == 2) &&
                        (Character.isDigit(codePoint)) &&
                        (isLatin(Character.UnicodeBlock.of(codePoint))));
            }
        }
        return isLatinDigit;
    }

    /**
     * Determine if the specified unicode block represents Latin characters.
     *
     * <p>
     * Additional Latin-like characters may also appear in the following blocks:
     * <ul>
     * <li>Spacing Modifier Letters, 02B0–02FF</li>
     * <li>Phonetic Extensions, 1D00–1D7F</li>
     * <li>Phonetic Extensions Supplement, 1D80–1DBF</li>
     * <li>Superscripts and Subscripts, 2070–209F</li>
     * <li>Letter-like Symbols, 2100–214F</li>
     * <li>Latin Extended-E, AB30–AB6F</li>
     * <li>Alphabetic Presentation Forms (Latin ligatures) FB00–FB4F</li>
     * <li>Currency Symbols</li>
     * <li>Control Pictures</li>
     * <li>CJK Compatibility</li>
     * <li>Enclosed CJK Letters and Months</li>
     * </ul>
     * </p>
     *
     * <p> Also, Lisu also consists almost entirely of Latin forms but uses its own script property, so Lisu characters
     * are not identified by this method. </p>
     *
     * @param unicodeBlock The Unicode block.
     *
     * @return A flag indicating whether the specified unicode block represents Latin characters.
     */
    private static boolean isLatin(Character.UnicodeBlock unicodeBlock) {
        return LATIN_BLOCKS.contains(unicodeBlock);
    }

    /**
     * Check whether a unicode character falls within a run of Latin symbols inside of the Half-width and Full-width
     * Forms block.
     *
     * @param character The character.
     *
     * @return true if the provided character is a halfwidth or fullwidth Latin symbol
     */
    private static boolean isHalfOrFullWidthLatin(char character) {
        return ((('\uFF21' <= character) && (character <= '\uFF3A')) ||
                (('\uFF41' <= character) && (character <= '\uFF5A')));
    }

    /**
     * Determine if the specified character is a valid Unicode ZWJ script character.
     *
     * @param character The character.
     *
     * @return A flag indicating whether the specified character is a valid Unicode ZWJ script character.
     */
    public static boolean isZWJ(char character) {
        return (character == '\u200D');
    }

    /**
     * Determine if the specified text is a single valid ZWJ script character.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is a single valid ZWJ script character.
     */
    public static boolean isZWJ(CharSequence text) {
        boolean isZWJ = false;
        if (text != null) {
            int length = text.length();
            if (length == 1) {
                isZWJ = isZWJ(text.charAt(0));
            } else {
                isZWJ = ((length == 2) && (isZWJ(text.toString().toCharArray())));
            }
        }
        return isZWJ;
    }

    /**
     * Determine if the specified character array is a single valid Unicode ZWJ script character.
     *
     * <p> If the character array contains more than one character, the first element is evaluated as a high-surrogate
     * code unit and the second element is evaluated as a low-surrogate code unit to determine if those two elements
     * together represent a valid Unicode ZWJ script character. </p>
     *
     * @param characters a char array
     *
     * @return A flag indicating whether the specified character array is a single valid Unicode ZWJ script character.
     */
    public static boolean isZWJ(char[] characters) {
        boolean isZWJ = false;
        if (characters != null) {
            int length = characters.length;
            if (length == 1) {
                isZWJ = isZWJ(characters[0]);
            }
        }
        return isZWJ;
    }

    /**
     * Determine if the specified unicode block represents ZWJ characters.
     *
     * @param unicodeBlock The Unicode block.
     *
     * @return A flag indicating whether the specified unicode block represents ZWJ characters.
     */
    private static boolean isZWJ(Character.UnicodeBlock unicodeBlock) {
        return ARABIC_BLOCKS.contains(unicodeBlock);
    }

    /**
     * Constructor.
     */
    private Characters() {
        super();
    }
}