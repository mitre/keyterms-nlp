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

import keyterms.nlp.text.StringNormalizer;

public class UnicodeCharacterInfo {

    /* This class contains information about unicode characters as read by the UnicodeDataResource class from
        a file based on an embellished version of the unicode character database file.  The primary purpose of this
        file is to provide enhanced normalization information beyond what is provided by standard unicode normalization
        forms.
     */
    private char symbol;
    private int decimalValue;
    private String hexCode;
    private String characterName;
    private String generalCategory;
    private String characterNotes;
    private String unicodeBlock;
    private String graphoSemanticDescription;
    private String latinLookAlikeNormalization;
    private String latinFunctionAlikeTransliteration;
    private String canonicalCombiningClasses;
    private String bidirectionalCategory;
    private String characterDecompositionMapping;
    private int decimalDigitValue;
    private int digitValue;
    private String numericValueAsString;
    private double numericValue;
    private boolean mirrored;
    private String unicodeOneName;
    private char uppercaseMapping;
    private char lowercaseMapping;
    private char titlecaseMapping;
    private char openBracket;
    private char closeBracket;

    public UnicodeCharacterInfo() {
    }

    public UnicodeCharacterInfo(char baseChar) {
        symbol = baseChar;
    }

    public boolean hasTranslitValue() {
        return StringNormalizer.isNullOrWhiteSpace(latinLookAlikeNormalization) ||
                StringNormalizer
                        .isNullOrWhiteSpace(latinFunctionAlikeTransliteration);
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char lSymbol) {
        symbol = lSymbol;
    }

    public int getDecimalValue() {
        return decimalValue;
    }

    public void setDecimalValue(int lDecimalValue) {
        decimalValue = lDecimalValue;
    }

    public String getHexCode() {
        return hexCode;
    }

    public void setHexCode(String lHexCode) {
        hexCode = lHexCode;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String lCharacterName) {
        characterName = lCharacterName;
    }

    public String getGeneralCategory() {
        return generalCategory;
    }

    public void setGeneralCategory(String lGeneralCategory) {
        generalCategory = lGeneralCategory;
    }

    public String getCharacterNotes() {
        return characterNotes;
    }

    public void setCharacterNotes(String lCharacterNotes) {
        characterNotes = lCharacterNotes;
    }

    public String getUnicodeBlock() {
        return unicodeBlock;
    }

    public void setUnicodeBlock(String lUnicodeBlock) {
        unicodeBlock = lUnicodeBlock;
    }

    public String getGraphoSemanticDescription() {
        return graphoSemanticDescription;
    }

    public void setGraphoSemanticDescription(String lGraphoSemanticDescription) {
        graphoSemanticDescription = lGraphoSemanticDescription;
    }

    public String getLatinLookAlikeNormalization() {
        return latinLookAlikeNormalization;
    }

    public void setLatinLookAlikeNormalization(String lLatinLookAlikeNormalization) {
        latinLookAlikeNormalization = lLatinLookAlikeNormalization;
    }

    public String getLatinFunctionAlikeTransliteration() {
        return latinFunctionAlikeTransliteration;
    }

    public void setLatinFunctionAlikeTransliteration(String lLatinFunctionAlikeTransliteration) {
        latinFunctionAlikeTransliteration = lLatinFunctionAlikeTransliteration;
    }

    public String getCanonicalCombiningClasses() {
        return canonicalCombiningClasses;
    }

    public void setCanonicalCombiningClasses(String lCanonicalCombiningClasses) {
        canonicalCombiningClasses = lCanonicalCombiningClasses;
    }

    public String getBidirectionalCategory() {
        return bidirectionalCategory;
    }

    public void setBidirectionalCategory(String lBidirectionalCategory) {
        bidirectionalCategory = lBidirectionalCategory;
    }

    public String getCharacterDecompositionMapping() {
        return characterDecompositionMapping;
    }

    public void setCharacterDecompositionMapping(String lCharacterDecompositionMapping) {
        characterDecompositionMapping = lCharacterDecompositionMapping;
    }

    public int getDecimalDigitValue() {
        return decimalDigitValue;
    }

    public void setDecimalDigitValue(int lDecimalDigitValue) {
        decimalDigitValue = lDecimalDigitValue;
    }

    public int getDigitValue() {
        return digitValue;
    }

    public void setDigitValue(int lDigitValue) {
        digitValue = lDigitValue;
    }

    public String getNumericValueAsString() {
        return numericValueAsString;
    }

    public void setNumericValueAsString(String lNumericValueAsString) {
        numericValueAsString = lNumericValueAsString;
    }

    public double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(double lNumericValue) {
        numericValue = lNumericValue;
    }

    public boolean getMirrored() {
        return mirrored;
    }

    public void setMirrored(boolean lMirrored) {
        mirrored = lMirrored;
    }

    public String getUnicodeOneName() {
        return unicodeOneName;
    }

    public void setUnicodeOneName(String lUnicodeOneName) {
        unicodeOneName = lUnicodeOneName;
    }

    public char getUppercaseMapping() {
        return uppercaseMapping;
    }

    public void setUppercaseMapping(char lUppercaseMapping) {
        uppercaseMapping = lUppercaseMapping;
    }

    public char getLowercaseMapping() {
        return lowercaseMapping;
    }

    public void setLowercaseMapping(char lLowercaseMapping) {
        lowercaseMapping = lLowercaseMapping;
    }

    public char getTitlecaseMapping() {
        return titlecaseMapping;
    }

    public void setTitlecaseMapping(char lTitlecaseMapping) {
        titlecaseMapping = lTitlecaseMapping;
    }

    public char getOpenBracket() {
        return openBracket;
    }

    public void setOpenBracket(char lOpenBracket) {
        openBracket = lOpenBracket;
    }

    public char getCloseBracket() {
        return closeBracket;
    }

    public void setCloseBracket(char lCloseBracket) {
        closeBracket = lCloseBracket;
    }
}