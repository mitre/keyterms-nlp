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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.Errors;
import keyterms.util.text.Strings;

public class UnicodeDataResource {

    private static final String RESOURCE = "rsc/UnicodeCharacters_20170103.tab";

    private Map<Character, UnicodeCharacterInfo> characterInfo;

    public UnicodeDataResource() {
        this(true);
    }

    public UnicodeDataResource(boolean withTranslitValuesOnly) {
        characterInfo = loadUnicodeCharacterInfo(withTranslitValuesOnly);
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    public String getTransliteration(Character originalCharacter) {
        String translitValue = originalCharacter.toString();
        if (characterInfo != null && characterInfo.containsKey(originalCharacter)) {
            UnicodeCharacterInfo info = characterInfo.get(originalCharacter);
            translitValue = info.getLatinFunctionAlikeTransliteration();
            if (Strings.isBlank(translitValue)) {
                translitValue = info.getLatinLookAlikeNormalization();
            }
            if (Strings.isBlank(translitValue)) {
                translitValue = originalCharacter.toString();
            }
        }
        return translitValue;
    }

    private Map<Character, UnicodeCharacterInfo> loadUnicodeCharacterInfo(boolean withTranslitValuesOnly) {
        Map<Character, UnicodeCharacterInfo> unicodeCharacterInfoMap = new HashMap<>();
        BufferedReader fin = null;
        try {
            Reader reader = new InputStreamReader(getClass().getResourceAsStream(RESOURCE), "UTF-8");
            fin = new BufferedReader(reader);
            String s;
            int lineNum = 0;
            while ((s = fin.readLine()) != null) {
                lineNum++;
                if (lineNum == 1 || Strings.isBlank(s)) {
                    continue;
                }
                s = s.replaceAll("[\r\n]", "");
                String[] sa = s.split("\\t");
                if (sa.length < 2) {
                    getLogger().error("Blank line in Unicode file at line {}", lineNum);
                    continue;
                }
                try {
                    UnicodeCharacterInfo charInf = processRow(sa);
                    if (charInf != null) {
                        if (!withTranslitValuesOnly || charInf.hasTranslitValue()) {
                            unicodeCharacterInfoMap.put(charInf.getSymbol(), charInf);
                        }
                    }
                } catch (Exception eek) {
                    getLogger().error("Error loading character at line {}", lineNum, eek);
                }
            }
        } catch (Exception error) {
            getLogger().error("Error loading unicode data.", error);
        } finally {
            try {
                fin.close();
            } catch (Exception eek) {
                Errors.ignore(eek);
            }
        }
        return unicodeCharacterInfoMap;
    }

    public int getNumberOfCharacters() {
        if (characterInfo == null) {
            return -1;
        }
        return characterInfo.size();
    }

    public UnicodeCharacterInfo processRow(String[] row) {
        UnicodeCharacterInfo charInfo = new UnicodeCharacterInfo();
        if (row.length >= 18) {
            // 0  Symbol
            char curChar = getCharFromString(row[0]);
            charInfo.setSymbol(curChar);
            // 2  Hex Code
            charInfo.setHexCode(tidyString(row[2]));
            // 1  Decimal Value
            int charDecimalValue = (int)charInfo.getSymbol();
            if (!checkDecimalAgainstHexidecimal(charInfo.getHexCode(), charDecimalValue)) {
                getLogger().error("hex conversion and and decimal value of character do not match: {},{}",
                        charInfo.getHexCode(), charDecimalValue);
            }
            // 3  Character name
            charInfo.setCharacterName(tidyString(row[3]));
            // 4  General category
            charInfo.setGeneralCategory(tidyString(row[4]));
            // 5  Character Notes
            charInfo.setCharacterNotes(tidyString(row[5]));
            // 6  Unicode block
            charInfo.setUnicodeBlock(tidyString(row[6]));
            // 7  GraphoSemanticDescription
            charInfo.setGraphoSemanticDescription(tidyString(row[7]));
            // 8  Latin_Look-Alike_Translit
            charInfo.setLatinLookAlikeNormalization(tidyString(row[8]));
            // 9  Latin_Function-Alike_Translit
            charInfo.setLatinFunctionAlikeTransliteration(tidyString(row[9]));
            // 10  Canonical combining classes
            charInfo.setCanonicalCombiningClasses(tidyString(row[10]));
            // 11  Bidirectional category
            charInfo.setBidirectionalCategory(tidyString(row[11]));
            // 12  Character decomposition mapping
            charInfo.setCharacterDecompositionMapping(tidyString(row[12]));
            // 13  Decimal digit value
            charInfo.setDecimalDigitValue(getIntFromString(row[13]));
            // 14  Digit value
            charInfo.setDigitValue(getIntFromString(row[14]));
            // 15  Numeric value
            charInfo.setNumericValueAsString(tidyString(row[15]));
            charInfo.setNumericValue(getDoubleFromString(charInfo.getNumericValueAsString()));
            // 16  Mirrored
            charInfo.setMirrored(getBooleanFromString(row[16]));
            // 17  Unicode 1.0 Name
            charInfo.setUnicodeOneName(tidyString(row[17]));
            // 18  Uppercase mapping
            if (row.length > 18) {
                setInfo(row[18], charInfo::setUppercaseMapping);
            }
            // 19  Lowercase mapping
            if (row.length > 19) {
                setInfo(row[19], charInfo::setLowercaseMapping);
            }
            // 20  Titlecase mapping
            if (row.length > 20) {
                setInfo(row[20], charInfo::setTitlecaseMapping);
            }
            // 21  Open bracket
            if (row.length > 21) {
                setInfo(row[21], charInfo::setOpenBracket);
            }
            // 22  Close Bracket
            if (row.length > 22) {
                setInfo(row[22], charInfo::setCloseBracket);
            }
        }
        return charInfo;
    }

    private void setInfo(String code, Consumer<Character> fn) {
        if (Strings.hasText(code)) {
            Character c = UnicodeUtility.getCharacterForCode(code, 16);
            if (c != null) {
                fn.accept(c);
            } else {
                getLogger().error("No unicode character for code: {}", code);
            }
        }
    }

    public char getCharFromString(String cellString) {
        if (cellString == null) {
            return '\0';
        }
        cellString = cellString.trim();
        if (cellString.length() < 1) {
            return '\0';
        }
        return cellString.charAt(0);
    }

    public int getIntFromString(String cellString) {
        int integerValue = -1;
        if (cellString != null) {

            cellString = cellString.trim();
            try {
                integerValue = Integer.parseInt(cellString, 10);
            } catch (NumberFormatException nfe) {
                integerValue = -1;
            }
        }
        return integerValue;
    }

    public double getDoubleFromString(String cellString) {
        double doubleValue = -1.0;
        if (cellString != null) {
            cellString = cellString.trim();
            try {
                doubleValue = Double.parseDouble(cellString);
            } catch (NumberFormatException nfe) {
                doubleValue = -1.0;
            }
        }
        return doubleValue;
    }

    public boolean getBooleanFromString(String cellString) {
        boolean booleanValue = false;
        if (cellString != null) {
            cellString = cellString.trim().toLowerCase();
            try {
                booleanValue = Boolean.parseBoolean(cellString);
            } catch (Exception notBooleanError) {
                booleanValue = false;
            }
        }
        return booleanValue;
    }

    public String tidyString(String cellString) {
        if (cellString == null) {
            return "";
        }
        cellString = cellString.trim();
        return cellString;
    }

    public boolean checkDecimalAgainstHexidecimal(String hexCode, int charDecimalValue) {
        boolean matches = false;
        if (Strings.isBlank(hexCode)) {
            return false;
        }
        int convertedDecimalValue = UnicodeUtility.getCharacterForCode(hexCode, 16);
        if (convertedDecimalValue == charDecimalValue) {
            matches = true;
        }
        return matches;
    }
}