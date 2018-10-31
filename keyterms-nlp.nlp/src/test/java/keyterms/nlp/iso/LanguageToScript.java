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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class LanguageToScript {

    private static final HashMap<LanguageCode, List<ScriptCode>> LANGUAGE_TO_SCRIPT = new HashMap<>();

    static {
        addLanguageScriptAssociation(LanguageCode.AFR, ScriptCode.LATN);  //Afrikaans
        addLanguageScriptAssociation(LanguageCode.AMH, ScriptCode.ETHI);  //Amharic
        addLanguageScriptAssociation(LanguageCode.SQI, ScriptCode.LATN);  //Albanian
        addLanguageScriptAssociation(LanguageCode.ARA, ScriptCode.ARAB);  //Arabic
        addLanguageScriptAssociation(LanguageCode.HYE, ScriptCode.ARMN);  //Armenian
        addLanguageScriptAssociation(LanguageCode.AZE, ScriptCode.LATN);  //Azerbaijani
        addLanguageScriptAssociation(LanguageCode.AZE, ScriptCode.ARAB);  //Azerbaijani
        addLanguageScriptAssociation(LanguageCode.AZE, ScriptCode.CYRL);  //Azerbaijani
        addLanguageScriptAssociation(LanguageCode.BAL, ScriptCode.ARAB);  //Baluchi
        addLanguageScriptAssociation(LanguageCode.EUS, ScriptCode.LATN);  //Basque
        addLanguageScriptAssociation(LanguageCode.BEL, ScriptCode.CYRL);  //Belarusian
        addLanguageScriptAssociation(LanguageCode.BEL, ScriptCode.LATN);  //Belarusian
        addLanguageScriptAssociation(LanguageCode.BEL, ScriptCode.ARAB);  //Belarusian
        addLanguageScriptAssociation(LanguageCode.BEL, ScriptCode.HEBR);  //Belarusian
        addLanguageScriptAssociation(LanguageCode.BEN, ScriptCode.BENG);  //Bengali
        addLanguageScriptAssociation(LanguageCode.BUL, ScriptCode.CYRL);  //Bulgarian
        addLanguageScriptAssociation(LanguageCode.CAT, ScriptCode.LATN);  //Catalan
        addLanguageScriptAssociation(LanguageCode.CBK, ScriptCode.LATN);  //Chavacano
        addLanguageScriptAssociation(LanguageCode.CHE, ScriptCode.CYRL);  //Chechen
        addLanguageScriptAssociation(LanguageCode.CHE, ScriptCode.LATN);  //Chechen
        addLanguageScriptAssociation(LanguageCode.CHE, ScriptCode.ARAB);  //Chechen
        addLanguageScriptAssociation(LanguageCode.ZHO, ScriptCode.HANI);  //Chinese
        addLanguageScriptAssociation(LanguageCode.HRV, ScriptCode.LATN);  //Croatian
        addLanguageScriptAssociation(LanguageCode.CES, ScriptCode.LATN);  //Czech
        addLanguageScriptAssociation(LanguageCode.DAN, ScriptCode.LATN);  //Danish
        addLanguageScriptAssociation(LanguageCode.PRS, ScriptCode.ARAB);  //Dari
        addLanguageScriptAssociation(LanguageCode.DIV, ScriptCode.THAA);  //Divehi
        addLanguageScriptAssociation(LanguageCode.NLD, ScriptCode.LATN);  //Dutch
        addLanguageScriptAssociation(LanguageCode.ENG, ScriptCode.LATN);  //English
        addLanguageScriptAssociation(LanguageCode.EST, ScriptCode.LATN);  //Estonian
        addLanguageScriptAssociation(LanguageCode.FIN, ScriptCode.LATN);  //Finnish
        addLanguageScriptAssociation(LanguageCode.FRA, ScriptCode.LATN);  //French
        addLanguageScriptAssociation(LanguageCode.GUJ, ScriptCode.GUJR);  //Gujarati
        addLanguageScriptAssociation(LanguageCode.KAT, ScriptCode.GEOR);  //Georgian
        addLanguageScriptAssociation(LanguageCode.DEU, ScriptCode.LATN);  //German
        addLanguageScriptAssociation(LanguageCode.ELL, ScriptCode.GREK);  //Greek
        addLanguageScriptAssociation(LanguageCode.HAT, ScriptCode.LATN);  //Haitian Creole
        addLanguageScriptAssociation(LanguageCode.HAU, ScriptCode.ARAB);  //Hausa
        addLanguageScriptAssociation(LanguageCode.HAU, ScriptCode.LATN);  //Hausa
        addLanguageScriptAssociation(LanguageCode.HEB, ScriptCode.HEBR);  //Hebrew
        addLanguageScriptAssociation(LanguageCode.HIN, ScriptCode.DEVA);  //Hindi
        addLanguageScriptAssociation(LanguageCode.MWW, ScriptCode.LATN);  //Hmong Daw
        addLanguageScriptAssociation(LanguageCode.HUN, ScriptCode.LATN);  //Hungarian
        addLanguageScriptAssociation(LanguageCode.IND, ScriptCode.LATN);  //Indonesian
        addLanguageScriptAssociation(LanguageCode.ITA, ScriptCode.LATN);  //Italian
        addLanguageScriptAssociation(LanguageCode.JPN, ScriptCode.HANI);  //Japanese
        addLanguageScriptAssociation(LanguageCode.JPN, ScriptCode.HIRA);  //Japanese
        addLanguageScriptAssociation(LanguageCode.JPN, ScriptCode.KANA);  //Japanese
        addLanguageScriptAssociation(LanguageCode.JAV, ScriptCode.JAVA);  //Javanese
        addLanguageScriptAssociation(LanguageCode.KAZ, ScriptCode.CYRL);  //Kazakh
        addLanguageScriptAssociation(LanguageCode.KAZ, ScriptCode.LATN);  //Kazakh
        addLanguageScriptAssociation(LanguageCode.KAZ, ScriptCode.ARAB);  //Kazakh
        addLanguageScriptAssociation(LanguageCode.KOR, ScriptCode.HANG);  //Korean
        addLanguageScriptAssociation(LanguageCode.CKB, ScriptCode.ARAB);  //Central Kurdish
        addLanguageScriptAssociation(LanguageCode.KUR, ScriptCode.ARAB);  //Kurdish
        addLanguageScriptAssociation(LanguageCode.KIR, ScriptCode.ARAB);  //Kirghiz
        addLanguageScriptAssociation(LanguageCode.KIR, ScriptCode.CYRL);  //Kirghiz
        addLanguageScriptAssociation(LanguageCode.LAO, ScriptCode.LAOO);  //Lao
        addLanguageScriptAssociation(LanguageCode.LAV, ScriptCode.LATN);  //Latvian
        addLanguageScriptAssociation(LanguageCode.LIN, ScriptCode.LATN);  //Lingala
        addLanguageScriptAssociation(LanguageCode.LIT, ScriptCode.LATN);  //Lithuanian
        addLanguageScriptAssociation(LanguageCode.MKD, ScriptCode.CYRL);  //Macedonian
        addLanguageScriptAssociation(LanguageCode.MKD, ScriptCode.LATN);  //Macedonian
        addLanguageScriptAssociation(LanguageCode.MSA, ScriptCode.ARAB);  //Malay
        addLanguageScriptAssociation(LanguageCode.MSA, ScriptCode.LATN);  //Malay
        addLanguageScriptAssociation(LanguageCode.MAL, ScriptCode.MLYM);  //Malayalam
        addLanguageScriptAssociation(LanguageCode.MLT, ScriptCode.LATN);  //Maltese
        addLanguageScriptAssociation(LanguageCode.MON, ScriptCode.MONG);  //Mongolian
        addLanguageScriptAssociation(LanguageCode.MON, ScriptCode.CYRL);  //Mongolian
        addLanguageScriptAssociation(LanguageCode.MON, ScriptCode.LATN);  //Mongolian
        addLanguageScriptAssociation(LanguageCode.NOR, ScriptCode.LATN);  //Norwegian
        addLanguageScriptAssociation(LanguageCode.ORI, ScriptCode.ORYA);  //Oriya
        addLanguageScriptAssociation(LanguageCode.PUS, ScriptCode.ARAB);  //Pashto, Pushto
        addLanguageScriptAssociation(LanguageCode.FAS, ScriptCode.ARAB);  //Persian
        addLanguageScriptAssociation(LanguageCode.POL, ScriptCode.LATN);  //Polish
        addLanguageScriptAssociation(LanguageCode.POR, ScriptCode.LATN);  //Portuguese
        addLanguageScriptAssociation(LanguageCode.RON, ScriptCode.LATN);  //Romanian
        addLanguageScriptAssociation(LanguageCode.RUS, ScriptCode.CYRL);  //Russian
        addLanguageScriptAssociation(LanguageCode.SRP, ScriptCode.CYRL);  //Serbian
        addLanguageScriptAssociation(LanguageCode.SLK, ScriptCode.LATN);  //Slovak
        addLanguageScriptAssociation(LanguageCode.SLV, ScriptCode.LATN);  //Slovenian
        addLanguageScriptAssociation(LanguageCode.SOM, ScriptCode.LATN);  //Somali
        addLanguageScriptAssociation(LanguageCode.SOM, ScriptCode.ARAB);  //Somali
        addLanguageScriptAssociation(LanguageCode.SPA, ScriptCode.LATN);  //Spanish
        addLanguageScriptAssociation(LanguageCode.SUN, ScriptCode.LATN);  //Sundanese
        addLanguageScriptAssociation(LanguageCode.SUN, ScriptCode.SUND);  //Sundanese
        addLanguageScriptAssociation(LanguageCode.SWA, ScriptCode.ARAB);  //Swahili
        addLanguageScriptAssociation(LanguageCode.SWA, ScriptCode.LATN);  //Swahili
        addLanguageScriptAssociation(LanguageCode.SWE, ScriptCode.LATN);  //Swedish
        addLanguageScriptAssociation(LanguageCode.TGL, ScriptCode.LATN);  //Tagalog
        addLanguageScriptAssociation(LanguageCode.TGL, ScriptCode.TGLG);  //Tagalog
        addLanguageScriptAssociation(LanguageCode.TGK, ScriptCode.ARAB);  //Tajik
        addLanguageScriptAssociation(LanguageCode.TGK, ScriptCode.LATN);  //Tajik
        addLanguageScriptAssociation(LanguageCode.TGK, ScriptCode.CYRL);  //Tajik
        addLanguageScriptAssociation(LanguageCode.TAM, ScriptCode.TAML);  //Tamil
        addLanguageScriptAssociation(LanguageCode.TSG, ScriptCode.LATN);  //Tausug
        addLanguageScriptAssociation(LanguageCode.TSG, ScriptCode.ARAB);  //Tausug
        addLanguageScriptAssociation(LanguageCode.TET, ScriptCode.LATN);  //Tetum
        addLanguageScriptAssociation(LanguageCode.THA, ScriptCode.THAI);  //Thai
        addLanguageScriptAssociation(LanguageCode.TPI, ScriptCode.LATN);  //Tok Pisin
        addLanguageScriptAssociation(LanguageCode.TUR, ScriptCode.LATN);  //Turkish
        addLanguageScriptAssociation(LanguageCode.TUK, ScriptCode.LATN);  //Turkmen
        addLanguageScriptAssociation(LanguageCode.UND, ScriptCode.ZYYY);  //Undetermined
        addLanguageScriptAssociation(LanguageCode.UKR, ScriptCode.CYRL);  //Ukrainian
        addLanguageScriptAssociation(LanguageCode.URD, ScriptCode.ARAB);  //Urdu
        addLanguageScriptAssociation(LanguageCode.UZB, ScriptCode.CYRL);  //Uzbek
        addLanguageScriptAssociation(LanguageCode.UZB, ScriptCode.LATN);  //Uzbek
        addLanguageScriptAssociation(LanguageCode.VIE, ScriptCode.LATN);  //Vietnamese
        addLanguageScriptAssociation(LanguageCode.CYM, ScriptCode.LATN);  //Welsh
    }

    private static void addLanguageScriptAssociation(LanguageCode lang, ScriptCode code) {
        List<ScriptCode> scriptCodes = LANGUAGE_TO_SCRIPT.computeIfAbsent(lang, (l) -> new ArrayList<>());
        if (!scriptCodes.contains(code)) {
            scriptCodes.add(code);
        }
    }

    static List<ScriptCode> getScripts(LanguageCode code) {
        return LANGUAGE_TO_SCRIPT.get(code);
    }
}