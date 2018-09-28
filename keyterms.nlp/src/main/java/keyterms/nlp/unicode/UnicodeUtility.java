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

package keyterms.nlp.unicode;

import java.text.Normalizer;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.nlp.text.StringNormalizer;
import keyterms.util.text.Strings;

/**
 * This class contains various convenience functions for getting information about unicode properties of Characters
 * and Strings
 */
public class UnicodeUtility {
    /**
     * Get the logging topic for the class.
     *
     * @return The logging topic for the class.
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(UnicodeUtility.class);
    }

    /**
     * Get the Character value for a unicode hex or decimal code. Supported formats include
     * plain numbers, {@code U+XXXXX?}, {@code \\uXXXX} and {@code &#x###?#?}.
     *
     * <p> If a plain number is passed in without a one of the prefixes
     * {{@code U+},{@code \\u}, {@code &#x}, {@code &#}}, the number will be treated as a decimal number
     * and parsed as base 10.  To explicitly control the base, use the alternative method </p>
     *
     * @param characterCode The character code.
     *
     * @return A Character corresponding to the specified numeric code, or null if the input cannot be interpreted.
     *
     * <p> This method will not alter the original text. </p>
     */
    public static Character getCharacterForCode(String characterCode) {
        if (Strings.isBlank(characterCode)) {
            return null;
        }
        int base = 10;
        // note:  you must look for &#x before &#
        String[] hexPrefixes = { "u+", "\\\\u", "&#x" };
        String[] decPrefixes = { "&#" };
        characterCode = StringNormalizer.removeSpaces(characterCode, true);
        characterCode = characterCode.toLowerCase();
        boolean pfxFound = false;
        for (String pfx : hexPrefixes) {
            if (characterCode.startsWith(pfx)) {
                base = 16;
                characterCode = characterCode.substring(pfx.length());
                pfxFound = true;
            }
        }
        if (!pfxFound) {
            for (String pfx : decPrefixes) {
                if (characterCode.startsWith(pfx)) {
                    base = 10;
                    characterCode = characterCode.substring(pfx.length());
                    pfxFound = true;
                }
            }
        }
        if (characterCode.endsWith(";")) {
            characterCode = characterCode.substring(0, characterCode.length() - 2);
        }
        if (!pfxFound) {
            if (characterCode.matches("^[0-9]+$")) {
                base = 10;
            } else {
                if (characterCode.matches("^[0-9a-f]+$")) {
                    base = 16;
                }
            }
        }
        return getCharacterForCode(characterCode, base);
    }

    public static Character getCharacterForCode(String characterCode, int base) {
        Character result = null;
        if (Strings.hasText(characterCode)) {
            try {
                Integer outputDecimal = Integer.parseInt(characterCode, base);
                result = (char)outputDecimal.intValue();
            } catch (NumberFormatException ne) {
                getLogger().error("Error creating character from hex code: {}.  Issue: {}", characterCode, ne);
            }
        }
        return result;
    }

    public static String getUnicodeBlocksForDecomposedString(String input) {
        if (Strings.isBlank(input)) {
            return "";
        }
        String alreadyDecomposed = Normalizer.normalize(input, Normalizer.Form.NFKD);
        StringBuilder sb = new StringBuilder();
        HashSet<String> blocksSeen = new HashSet<>();
        boolean printBlockForSpace = alreadyDecomposed.length() > 1;
        for (int i = 0; i < alreadyDecomposed.length(); i++) {
            char curChar = alreadyDecomposed.charAt(i);
            if (Character.isSpaceChar(curChar) && !printBlockForSpace) {
                continue;
            }
            Character.UnicodeBlock uBlock = Character.UnicodeBlock.of(curChar);
            if (uBlock == null) {
                getLogger().error("no unicode block for char ", curChar);
                continue;
            }
            String charBlock = uBlock.toString();
            if (!blocksSeen.contains(charBlock)) {
                if (i > 0) {
                    sb.append(";;;");
                }
                sb.append(charBlock);
                blocksSeen.add(charBlock);
            }
        }
        return sb.toString();
    }
}