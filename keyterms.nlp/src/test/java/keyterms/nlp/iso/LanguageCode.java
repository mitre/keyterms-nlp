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

package keyterms.nlp.iso;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * based on ISO 639-2 source: www.loc.gov/standards/iso639-2/php/code_list.php
 * ISO 639-3 source: www-01.sil.org/iso639-3/codes.asp?order=639_3"
 */
public enum LanguageCode {

    AFR("Afrikaans", "Afrikaans", "af", "afr", ScriptCode.LATN),
    AMH("Amharic", "አማርኛ", "am", "amh", ScriptCode.ETHI),
    SQI("Albanian", "Shqip", "sq", "sqi", ScriptCode.LATN),
    ARA("Arabic", "العربية", "ar", "ara", ScriptCode.ARAB),
    HYE("Armenian", "Հայերեն", "hy", "hye", ScriptCode.ARMN),
    AZE("Azerbaijani", "azərbaycan dili", "az", "aze", ScriptCode.LATN),
    // Also ScriptCode.ARAB and ScriptCode.CYRL
    BAL("Baluchi", "", "", "bal", ScriptCode.ARAB),
    EUS("Basque", "euskara", "eu", "eus", ScriptCode.LATN, "euskera"),
    BEL("Belarusian", "беларуская мова", "be", "bel", ScriptCode.CYRL),
    //// Also ScriptCode.LATN, ScriptCode.ARAB and ScriptCode.HEBR
    BEN("Bengali", "বাংলা", "bn", "ben", ScriptCode.BENG),
    BUL("Bulgarian", "български език", "bg", "bul", ScriptCode.CYRL),
    CAT("Catalan", "català", "ca", "cat", ScriptCode.LATN, "valencià", "Valencian"),
    CBK("Chavacano", "Chavacano", "", "cbk", ScriptCode.LATN),
    CHE("Chechen", "нохчийн мотт", "ce", "che", ScriptCode.CYRL),
    /// Also ScriptCode.LATN and ScriptCode.ARAB
    ZHO("Chinese", "中文", "zh", "zho", ScriptCode.HANI, "Zhōngwén", "汉语", "漢語"),
    HRV("Croatian", "hrvatski jezik", "hr", "hrv", ScriptCode.LATN),
    CES("Czech", "čeština", "cs", "ces", ScriptCode.LATN, "český jazyk"),
    DAN("Danish", "dansk", "da", "dan", ScriptCode.LATN),
    PRS("Dari", "دری", "", "prs", ScriptCode.ARAB),
    DIV("Divehi", "ދިވެހި", "dv", "div", ScriptCode.THAA, "Dhivehi", "Maldivian"),
    NLD("Dutch", "Nederlands", "nl", "nld", ScriptCode.LATN, "Vlaams"),
    ENG("English", "English", "en", "eng", ScriptCode.LATN),
    EST("Estonian", "eesti", "et", "est", ScriptCode.LATN, "eesti keel"),
    FIN("Finnish", "suomi", "fi", "fin", ScriptCode.LATN, "suomen kieli"),
    FRA("French", "français", "fr", "fra", ScriptCode.LATN, "langue française"),
    GUJ("Gujarati", "ગુજરાતી", "gu", "guj", ScriptCode.GUJR),
    KAT("Georgian", "ქართული", "ka", "kat", ScriptCode.GEOR),
    DEU("German", "Deutsch", "de", "deu", ScriptCode.LATN),
    ELL("Greek", "", "el", "ell", ScriptCode.GREK, "Modern Greek"),
    HAT("Haitian Creole", "Kreyòl ayisyen", "ht", "hat", ScriptCode.LATN),
    HAU("Hausa", "هَوُسَ", "ha", "hau", ScriptCode.ARAB),
    //Also ScriptCode.LATN
    HEB("Hebrew", "", "he", "heb", ScriptCode.HEBR),
    HIN("Hindi", "हिन्दी", "hi", "hin", ScriptCode.DEVA, "हिंदी"),
    MWW("Hmong Daw", "Hmong Daw", "", "mww", ScriptCode.LATN),
    //Also Miao in Chinese characters or Pollard Miao, or In THai or in Pahawh Hmong
    HUN("Hungarian", "magyar", "hu", "hun", ScriptCode.LATN),
    IND("Indonesian", "Bahasa Indonesia", "id", "ind", ScriptCode.LATN),
    ITA("Italian", "italiano", "it", "ita", ScriptCode.LATN),
    JPN("Japanese", "日本語", "ja", "jpn", ScriptCode.HANI, "にほんご"),
    // Plus Hirigana and Katakana
    JAV("Javanese", "", "jv", "jav", ScriptCode.JAVA, "Carakan"),
    KAZ("Kazakh", "қазақ тілі", "kk", "kaz", ScriptCode.CYRL),
    // Also ScriptCode.LATN and ScriptCode.ARA
    //TLH("Klingon","","","tlh"),
    KOR("Korean", "한국어", "ko", "kor", ScriptCode.HANG, "조선어"),
    CKB("Central Kurdish", "", "", "ckb", ScriptCode.ARAB, "Sorani", "kurdiy nawendi"),
    KUR("Kurdish", "كوردی‎", "ku", "kur", ScriptCode.ARAB, "Kurdî"),
    KIR("Kirghiz", "", "ky", "kir", ScriptCode.ARAB, "Kyrgyz"),
    // Also Cyrillic
    LAO("Lao", "ພາສາລາວ", "lo", "lao", ScriptCode.LAOO),
    LAV("Latvian", "latviešu valoda", "lv", "lav", ScriptCode.LATN),
    LIN("Lingala", "Lingála", "ln", "lin", ScriptCode.LATN),
    LIT("Lithuanian", "lietuvių kalba", "lt", "lit", ScriptCode.LATN),
    MKD("Macedonian", "македонски јазик", "mk", "mkd", ScriptCode.CYRL),
    // also has an official romanization in latin
    MSA("Malay", "bahasa Melayu‎", "ms", "msa", ScriptCode.LATN, "بهاس ملايو"),
    // also ,ScriptCode.ARAB
    MAL("Malayalam", "മലയാളം‎", "ml", "mal", ScriptCode.MLYM),
    MLT("Maltese", "Malti", "mt", "mlt", ScriptCode.LATN),
    MON("Mongolian", "монгол", "mn", "mon", ScriptCode.MONG, "Mongolic"),
    //also ScriptCode.CYRL and ScriptCode.LATN
    NOR("Norwegian", "Norsk", "no", "nor", ScriptCode.LATN),
    ORI("Oriya", "ଓଡ଼ିଆ", "or", "ori", ScriptCode.ORYA),
    PUS("Pashto, Pushto", "پښتو", "ps", "pus", ScriptCode.ARAB),
    FAS("Persian", "فارسی", "fa", "fas", ScriptCode.ARAB, "Farsi"),
    POL("Polish", "polszczyzna", "pl", "pol", ScriptCode.LATN, "język polski"),
    POR("Portuguese", "português", "pt", "por", ScriptCode.LATN),
    RON("Romanian", "limba română", "ro", "ron", ScriptCode.LATN),
    RUS("Russian", "русский", "ru", "rus", ScriptCode.CYRL, "Русский язык"),
    SRP("Serbian", "српски језик", "sr", "srp", ScriptCode.CYRL),
    SLK("Slovak", "slovenčina", "sk", "slk", ScriptCode.LATN, "slovenský jazyk"),
    SLV("Slovenian", "slovenščina", "sl", "slv", ScriptCode.LATN, "slovenski jezik", "Slovene"),
    SOM("Somali", "Soomaaliga", "so", "som", ScriptCode.LATN, "af Soomaali"),
    // Also ,ScriptCode.ARAB and Borama, Osmanya and Kaddare
    SPA("Spanish", "español", "es", "spa", ScriptCode.LATN, "castellano", "castillian"),
    SUN("Sundanese", "Basa Sunda", "su", "sun", ScriptCode.LATN),
    //also ScriptCode.SUND
    SWA("Swahili", "Kiswahili", "sw", "swa", ScriptCode.ARAB),
    //also ScriptCode.LATN
    SWE("Swedish", "Svenska", "sv", "swe", ScriptCode.LATN),
    TGL("Tagalog", "", "tl", "tgl", ScriptCode.LATN),
    //Also ,ScriptCode.TGLG but mostly historically
    TGK("Tajik", "тоҷикӣ", "tg", "tgk", ScriptCode.ARAB, "toğikī", "تاجیکی"),
    // Also ScriptCode.LATN and ScriptCode.CYRL
    TAM("Tamil", "தமிழ்", "ta", "tam", ScriptCode.TAML),
    TSG("Tausug", "", "", "tsg", ScriptCode.LATN),
    // also ScriptCode.ARA
    TET("Tetum", "", "", "tet", ScriptCode.LATN),
    THA("Thai", "ไทย", "th", "tha", ScriptCode.THAI),
    TPI("Tok Pisin", "", "", "tpi", ScriptCode.LATN),
    TUR("Turkish", "Türkçe", "tr", "tur", ScriptCode.LATN),
    // Also, historically, ScriptCode.ARAB
    TUK("Turkmen", "Türkmen", "tk", "tuk", ScriptCode.LATN, "Түркмен"),
    // Historically ScriptCode.CYRL and ScriptCode.ARAB
    UND("undetermined", "undetermined", "uk", "und", ScriptCode.ZYYY),
    UKR("Ukrainian", "українська мова", "uk", "ukr", ScriptCode.CYRL),
    URD("Urdu", "اردو", "ur", "urd", ScriptCode.ARAB),
    UZB("Uzbek", "O‘zbek‎", "uz", "uzb", ScriptCode.CYRL, "Ўзбек", "أۇزبېك"),
    //Also ScriptCode.LATN
    VIE("Vietnamese", "Tiếng Việt", "vi", "vie", ScriptCode.LATN),
    CYM("Welsh", "Cymraeg", "cy", "cym", ScriptCode.LATN);

    public static LanguageCode getCodeForString(String codeString) {
        if (codeString == null) {
            return null;
        }
        codeString = codeString.trim();
        for (LanguageCode curCode : values()) {
            if (curCode.name().equalsIgnoreCase(codeString)) {
                return curCode;
            }
            if (curCode.iso3.equalsIgnoreCase(codeString)) {
                return curCode;
            }
            if (curCode.iso2.equalsIgnoreCase(codeString)) {
                return curCode;
            }
            if (curCode.englishName.equalsIgnoreCase(codeString)) {
                return curCode;
            }
            if (curCode.nativeName.equalsIgnoreCase(codeString)) {
                return curCode;
            }
            for (String curForm : curCode.altforms) {
                if (codeString.equalsIgnoreCase(curForm)) {
                    return curCode;
                }
            }
        }
        return LanguageCode.UND;
    }

    private final String englishName;
    private final String nativeName;
    private final String iso2;
    private final String iso3;
    private final ScriptCode preferredScriptCode;
    private final Set<String> altforms;

    LanguageCode(String engName, String nativeName, String iso2code, String iso3code,
            ScriptCode preferredScript, String... otherVariants) {
        this.englishName = engName;
        this.nativeName = nativeName;
        this.iso2 = iso2code;
        this.iso3 = iso3code;
        this.preferredScriptCode = preferredScript;
        altforms = new HashSet<>();
        if (otherVariants != null) {
            Collections.addAll(altforms, otherVariants);
        }
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getIso2() {
        return iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public ScriptCode getPreferredScriptCode() {
        return preferredScriptCode;
    }

    public Set<String> getAltForms() {
        return altforms;
    }
}