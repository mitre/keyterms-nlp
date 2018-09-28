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

import keyterms.util.collect.Bags;

public enum ScriptCode {

    ARAB("Arabic", "Arab", Character.UnicodeScript.ARABIC, "arabe"),
    ARMI("Imperial Aramaic", "Armi", Character.UnicodeScript.IMPERIAL_ARAMAIC, "araméen impérial"),
    ARMN("Armenian", "Armn", Character.UnicodeScript.ARMENIAN, "arménien"),
    AVST("Avestan", "Avst", Character.UnicodeScript.AVESTAN, "avestique"),
    BALI("Balinese", "Bali", Character.UnicodeScript.BALINESE, "balinais"),
    BAMU("Bamum", "Bamu", Character.UnicodeScript.BAMUM, "bamoum"),
    BATK("Batak", "Batk", Character.UnicodeScript.BATAK, "batik"),
    BENG("Bengali", "Beng", Character.UnicodeScript.BENGALI, "bengalî"),
    BOPO("Bopomofo", "Bopo", Character.UnicodeScript.BOPOMOFO, "bopomofo"),
    BRAH("Brahmi", "Brah", Character.UnicodeScript.BRAHMI, "brahma"),
    BRAI("Braille", "Brai", Character.UnicodeScript.BRAILLE, "braille"),
    BUGI("Buginese", "Bugi", Character.UnicodeScript.BUGINESE, "bouguis"),
    BUHD("Buhid", "Buhd", Character.UnicodeScript.BUHID, "bouhide"),
    CANS("Unified Canadian Aboriginal Syllabics", "Cans", Character.UnicodeScript.CANADIAN_ABORIGINAL,
            "syllabaire autochtone canadien unifié"),
    CARI("Carian", "Cari", Character.UnicodeScript.CARIAN, "carien"),
    CHAM("Cham", "Cham", Character.UnicodeScript.CHAM, "cham", "čam", "tcham"),
    CHER("Cherokee", "Cher", Character.UnicodeScript.CHEROKEE, "tchérokî"),
    COPT("Coptic", "Copt", Character.UnicodeScript.COPTIC, "copte"),
    CPRT("Cypriot", "Cprt", Character.UnicodeScript.CYPRIOT, "syllabaire chypriote"),
    CYRL("Cyrillic", "Cyrl", Character.UnicodeScript.CYRILLIC, "cyrillique"),
    DEVA("Devanagari", "Deva", Character.UnicodeScript.DEVANAGARI, "dévanâgarî", "Nagari"),
    DSRT("Deseret", "Dsrt", Character.UnicodeScript.DESERET, "déseret", "Mormon"),
    EGYP("Egyptian hieroglyphs", "Egyp", Character.UnicodeScript.EGYPTIAN_HIEROGLYPHS, "hiéroglyphes égyptiens"),
    ETHI("Ethiopic", "Ethi", Character.UnicodeScript.ETHIOPIC, "éthiopien", "Geʻez", "guèze", "Ge'ez"),
    GEOR("Georgian", "Geor", Character.UnicodeScript.GEORGIAN, "géorgien", "Mkhedruli", "mkhédrouli"),
    GLAG("Glagolitic", "Glag", Character.UnicodeScript.GLAGOLITIC, "glagolitique"),
    GOTH("Gothic", "Goth", Character.UnicodeScript.GOTHIC, "gotique"),
    GREK("Greek", "Grek", Character.UnicodeScript.GREEK, "grec"),
    GUJR("Gujarati", "Gujr", Character.UnicodeScript.GUJARATI, "goudjarâtî", "gujrâtî"),
    GURU("Gurmukhi", "Guru", Character.UnicodeScript.GURMUKHI, "gourmoukhî"),
    HANG("Hangul", "Hang", Character.UnicodeScript.HANGUL, "hangûl", "Hangŭl", "Hangeul"),
    HANI("Han", "Hani", Character.UnicodeScript.HAN, "idéogrammes han", "sinogrammes", "idéogrammes", "Hanzi", "Kanji",
            "Hanja"),
    HANS("Han Simplified", "hans", Character.UnicodeScript.HAN, "idéogrammes simplifiés", "han simplifié",
            "simplified zho"),
    HANT("Han Traditional", "hant", Character.UnicodeScript.HAN, "idéogrammes traditionnels", "han traditionnel",
            "traditional zho"),
    HANO("Hanunoo", "Hano", Character.UnicodeScript.HANUNOO, "hanounóo", "Hanunóo"),
    HEBR("Hebrew", "Hebr", Character.UnicodeScript.HEBREW, "hébreu"),
    HIRA("Hiragana", "Hira", Character.UnicodeScript.HIRAGANA, "hiragana"),
    ITAL("Old Italic", "Ital", Character.UnicodeScript.OLD_ITALIC, "ancien italique", "Etruscan", "Oscan", "étrusque",
            "osque"),
    JAVA("Javanese", "Java", Character.UnicodeScript.JAVANESE, "javanais"),
    KALI("Kayah Li", "Kali", Character.UnicodeScript.KAYAH_LI, "kayah li"),
    KANA("Katakana", "Kana", Character.UnicodeScript.KATAKANA, "katakana"),
    KHAR("Kharoshthi", "Khar", Character.UnicodeScript.KHAROSHTHI, "kharochthî"),
    KHMR("Khmer", "Khmr", Character.UnicodeScript.KHMER, "khmer"),
    KNDA("Kannada", "Knda", Character.UnicodeScript.KANNADA, "kannara", "canara"),
    KTHI("Kaithi", "Kthi", Character.UnicodeScript.KAITHI, "kaithî"),
    LANA("Tai Tham", "Lana", Character.UnicodeScript.TAI_THAM, "taï tham", "Lanna"),
    LAOO("Lao", "Laoo", Character.UnicodeScript.LAO, "laotien"),
    LATN("Latin", "Latn", Character.UnicodeScript.LATIN, "latin"),
    LEPC("Lepcha", "Lepc", Character.UnicodeScript.LEPCHA, "lepcha", "Róng"),
    LIMB("Limbu", "Limb", Character.UnicodeScript.LIMBU, "limbou"),
    LINB("Linear B", "Linb", Character.UnicodeScript.LINEAR_B, "linéaire B"),
    LISU("Lisu", "Lisu", Character.UnicodeScript.LISU, "lisu", "Fraser"),
    LYCI("Lycian", "Lyci", Character.UnicodeScript.LYCIAN, "lycien"),
    LYDI("Lydian", "Lydi", Character.UnicodeScript.LYDIAN, "lydien"),
    MAND("Mandaic", "Mand", Character.UnicodeScript.MANDAIC, "mandéen", "Mandaean"),
    MLYM("Malayalam", "Mlym", Character.UnicodeScript.MALAYALAM, "malayâlam"),
    MONG("Mongolian", "Mong", Character.UnicodeScript.MONGOLIAN, "mongol"),
    MTEI("Meitei Mayek", "Mtei", Character.UnicodeScript.MEETEI_MAYEK, "meitei mayek", "Meithei", "Meetei"),
    MYMR("Myanmar", "Mymr", Character.UnicodeScript.MYANMAR, "birman", "Burmese"),
    NKOO("N’Ko", "Nkoo", Character.UnicodeScript.NKO, "n’ko"),
    OGAM("Ogham", "Ogam", Character.UnicodeScript.OGHAM, "ogam"),
    OLCK("Ol Chiki", "Olck", Character.UnicodeScript.OL_CHIKI, "ol tchiki", "Ol Cemet’", "Ol", "Santali"),
    ORKH("Old Turkic, Orkhon Runic", "Orkh", Character.UnicodeScript.OLD_TURKIC, "orkhon"),
    ORYA("Oriya", "Orya", Character.UnicodeScript.ORIYA, "oriyâ"),
    OSMA("Osmanya", "Osma", Character.UnicodeScript.OSMANYA, "osmanais"),
    PHAG("Phags-pa", "Phag", Character.UnicodeScript.PHAGS_PA, "’phags pa"),
    PHLI("Inscriptional Pahlavi", "Phli", Character.UnicodeScript.INSCRIPTIONAL_PAHLAVI, "pehlevi des inscriptions"),
    PHNX("Phoenician", "Phnx", Character.UnicodeScript.PHOENICIAN, "phénicien"),
    PRTI("Inscriptional Parthian", "Prti", Character.UnicodeScript.INSCRIPTIONAL_PARTHIAN, "parthe des inscriptions"),
    RJNG("Rejang", "Rjng", Character.UnicodeScript.REJANG, "redjang", "Redjang", "Kaganga"),
    RUNR("Runic", "Runr", Character.UnicodeScript.RUNIC, "runique"),
    SAMR("Samaritan", "Samr", Character.UnicodeScript.SAMARITAN, "samaritain"),
    SARB("Old South Arabian", "Sarb", Character.UnicodeScript.OLD_SOUTH_ARABIAN, "sud-arabique", "himyarite"),
    SAUR("Saurashtra", "Saur", Character.UnicodeScript.SAURASHTRA, "saurachtra"),
    SHAW("Shavian", "Shaw", Character.UnicodeScript.SHAVIAN, "shavien", "Shaw"),
    SINH("Sinhala", "Sinh", Character.UnicodeScript.SINHALA, "singhalais"),
    SUND("Sundanese", "Sund", Character.UnicodeScript.SUNDANESE, "sundanais"),
    SYLO("Syloti Nagri", "Sylo", Character.UnicodeScript.SYLOTI_NAGRI, "sylotî nâgrî"),
    SYRC("Syriac", "Syrc", Character.UnicodeScript.SYRIAC, "syriaque"),
    TAGB("Tagbanwa", "Tagb", Character.UnicodeScript.TAGBANWA, "tagbanoua"),
    TALE("Tai Le", "Tale", Character.UnicodeScript.TAI_LE, "taï-le"),
    TALU("New Tai Lue", "Talu", Character.UnicodeScript.NEW_TAI_LUE, "nouveau taï-lue"),
    TAML("Tamil", "Taml", Character.UnicodeScript.TAMIL, "tamoul"),
    TAVT("Tai Viet", "Tavt", Character.UnicodeScript.TAI_VIET, "taï viêt"),
    TELU("Telugu", "Telu", Character.UnicodeScript.TELUGU, "télougou"),
    TFNG("Tifinagh", "Tfng", Character.UnicodeScript.TIFINAGH, "tifinagh", "Berber", "berbère"),
    TGLG("Tagalog", "Tglg", Character.UnicodeScript.TAGALOG, "tagal", "Baybayin", "Alibata"),
    THAA("Thaana", "Thaa", Character.UnicodeScript.THAANA, "thâna"),
    THAI("Thai", "Thai", Character.UnicodeScript.THAI, "thaï"),
    TIBT("Tibetan", "Tibt", Character.UnicodeScript.TIBETAN, "tibétain"),
    UGAR("Ugaritic", "Ugar", Character.UnicodeScript.UGARITIC, "ougaritique"),
    VAII("Vai", "Vaii", Character.UnicodeScript.VAI, "vaï"),
    XPEO("Old Persian", "Xpeo", Character.UnicodeScript.OLD_PERSIAN, "cunéiforme persépolitain"),
    XSUX("Cuneiform", "Xsux", Character.UnicodeScript.CUNEIFORM, "cunéiforme suméro-akkadien", "Sumero-Akkadian"),
    YIII("Yi", "Yiii", Character.UnicodeScript.YI, "yi"),
    ZINH("Inherited script", "Zinh", Character.UnicodeScript.INHERITED, "codet pour écriture héritée"),
    ZYYY("Undetermined script", "Zyyy", Character.UnicodeScript.COMMON, "codet pour écriture indéterminée"),
    ZZZZ("Uncoded script", "Zzzz", Character.UnicodeScript.UNKNOWN, "codet pour écriture non codée");

    public static final Set<ScriptCode> CJK_SCRIPTS = Bags.staticSet(
            ScriptCode.HANT, ScriptCode.HANS, ScriptCode.HANI,
            ScriptCode.HANG, ScriptCode.HIRA, ScriptCode.KANA
    );

    public static ScriptCode getIsoCodeForString(String variant) {
        if (variant == null) {
            return null;
        }
        variant = variant.trim();
        for (ScriptCode curCode : values()) {
            if (curCode.name().equalsIgnoreCase(variant)) {
                return curCode;
            }
            if (curCode.isoFour.equalsIgnoreCase(variant)) {
                return curCode;
            }
            if (curCode.englishName.equalsIgnoreCase(variant)) {
                return curCode;
            }
            if (curCode.frenchName.equalsIgnoreCase(variant)) {
                return curCode;
            }
            for (String curForm : curCode.altForms) {
                if (variant.equalsIgnoreCase(curForm)) {
                    return curCode;
                }
            }
        }
        return null;
    }

    public static Character.UnicodeScript getUnicodeScriptForString(String variant) {
        if (variant == null) {
            return null;
        }
        variant = variant.trim();
        for (ScriptCode curCode : values()) {
            if (curCode.name().equalsIgnoreCase(variant)) {
                return curCode.unicodeScript;
            }
            if (curCode.isoFour.equalsIgnoreCase(variant)) {
                return curCode.unicodeScript;
            }
            if (curCode.englishName.equalsIgnoreCase(variant)) {
                return curCode.unicodeScript;
            }
            if (curCode.frenchName.equalsIgnoreCase(variant)) {
                return curCode.unicodeScript;
            }
            for (String curForm : curCode.altForms) {
                if (variant.equalsIgnoreCase(curForm)) {
                    return curCode.unicodeScript;
                }
            }
        }
        return null;
    }

    public static String getIsoCodeForUnicodeScript(Character.UnicodeScript script) {
        if (script == null) {
            return null;
        }

        for (ScriptCode curCode : values()) {
            if (curCode.unicodeScript == script) {
                return curCode.isoFour.toLowerCase();
            }
        }
        return null;
    }

    public static ScriptCode getScriptCodeForUnicodeScript(Character.UnicodeScript script) {
        if (script == null) {
            return null;
        }

        for (ScriptCode curCode : values()) {
            if (curCode.unicodeScript == script) {
                return curCode;
            }
        }
        return null;
    }

    private final String englishName;
    private final String frenchName;
    private final String isoFour;
    private final Character.UnicodeScript unicodeScript;
    private final Set<String> altForms;

    ScriptCode(String engname, String isoFour, Character.UnicodeScript script, String fraName,
            String... otherVariants) {
        this.englishName = engname;
        this.isoFour = isoFour;
        this.unicodeScript = script;
        this.frenchName = fraName;
        altForms = new HashSet<>();
        if (otherVariants != null) {
            Collections.addAll(altForms, otherVariants);
        }
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIsoFour() {
        return isoFour.toLowerCase();
    }

    public String getFrenchName() {
        return frenchName;
    }

    public Set<String> getAltForms() {
        return altForms;
    }
}