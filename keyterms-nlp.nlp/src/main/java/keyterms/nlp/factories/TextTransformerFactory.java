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

package keyterms.nlp.factories;

import java.util.HashMap;

import keyterms.nlp.interfaces.TextTransformer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.languages.ara.TextTransformer_Ara;
import keyterms.nlp.languages.eng.TextTransformer_Eng;
import keyterms.nlp.languages.fra.TextTransformer_Fra;
import keyterms.nlp.languages.rus.TextTransformer_Rus;
import keyterms.nlp.languages.spa.TextTransformer_Spa;
import keyterms.nlp.languages.ukr.TextTransformer_Ukr;
import keyterms.nlp.languages.und.TextTransformer_Und;
import keyterms.nlp.languages.zho.TextTransformer_Zho;
import keyterms.util.text.Strings;

public class TextTransformerFactory {

    // Store ISO3 code for name along with an ITextTransformer instance for the language
    private HashMap<String, TextTransformer> transformers;

    public TextTransformerFactory() {
        transformers = new HashMap<>();
    }

    public TextTransformer getTransformer(Language language) {
        String langKey = (language != null) ? language.getCode() : "";
        langKey = Strings.toUpperCase(langKey);
        if (transformers.containsKey(langKey)) {
            return transformers.get(langKey);
        }

        TextTransformer transformer;
        switch (langKey) {
            case "ARA": {
                transformer = new TextTransformer_Ara();
                transformers.put(langKey, transformer);
                break;
            }
            case "ENG": {
                transformer = new TextTransformer_Eng();
                transformers.put(langKey, transformer);
                break;
            }
            case "FRA": {
                transformer = new TextTransformer_Fra();
                transformers.put(langKey, transformer);
                break;
            }
            case "RUS": {
                transformer = new TextTransformer_Rus();
                transformers.put(langKey, transformer);
                break;
            }
            case "SPA": {
                transformer = new TextTransformer_Spa();
                transformers.put(langKey, transformer);
                break;
            }
            case "UKR": {
                transformer = new TextTransformer_Ukr();
                transformers.put(langKey, transformer);
                break;
            }
            case "ZHO": {
                transformer = new TextTransformer_Zho();
                transformers.put(langKey, transformer);
                break;
            }
            default: {
                // XXX convert via "generic language"
                langKey = Language.UND.getCode();
                if (transformers.containsKey(langKey)) {
                    transformer = transformers.get(langKey);
                } else {
                    transformer = new TextTransformer_Und();
                    transformers.put(langKey, transformer);
                }
            }
        }
        return transformer;
    }
}