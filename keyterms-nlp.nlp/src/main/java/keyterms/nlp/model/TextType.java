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

package keyterms.nlp.model;

public enum TextType {

    ORIGINAL("textOriginal", "Original Text"),
    BGN("BGN", "BGN Standard"),
    GOST("GOST", "GOST Standard"),
    KEY_TERMS("KeyTerms", "Key Terms"),
    KEY_TERMS_ACRONYM("KeyTerms_acronym", "Key Terms-Acronym"),
    ICU_LATIN("icu Latin", "ICU Latin"),
    NORMALIZED_DISPLAY("textDisplay", "Text"),
    NORMALIZED_INDEX("textIndex", "Index Text"),
    NORMALIZED_SCORING("textScoring", "Scoring Text"),
    ENG_LATIN("eng_Latin", "English"),
    CYRILLIC("rus_Cyrillic", "Cyrillic"),
    CYRILLIC_STRESSED("rus_CyrillicStressed", "Cyrillic Stressed"),
    ARABIC("ara_Arabic", "Arabic"),
    ARABIC_VOWELED("ara_ArabicVoweled", "Arabic Voweled"),
    ZHO_PINYIN("zho_pinyin", "Pinyin"),
    ZHO_PINYIN_NUMERIC("zho_pinyin_numeric", "Pinyin-Numeric"),
    ZHO_PINYIN_NOTONE("zho_pinyin_noTone", "Pinyin-NoTone"),
    ZHO_PINYIN_INDEX("zho_pinyin", "Pinyin Index"),
    ZHO_WADEGILES("zho_wadeGiles", "Wade-Giles"),
    ZHO_WADEGILES_NOTONE("zho_wadeGiles_noTone", "Wade-Giles-NoTone"),
    ZHO_SIMPLIFIED("zho_simplified", "Simplified"),
    ZHO_TRADITIONAL("zho_traditional", "Traditional"),
    ZHO_NORMALIZED_SIMPLIFIED("zho_normalized_simplified", "Normalized Simplified"),
    ZHO_NORMALIZED_TRADITIONAL("zho_normalized_traditional", "Normalized Traditional"),
    NONE("none", "none");

    String label;
    String displayLabel;

    TextType(String textLabel, String textDisplay) {
        label = textLabel;
        displayLabel = textDisplay;
    }

    public String getLabel() {
        return label;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}