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

package keyterms.service;

import java.util.ArrayList;
import java.util.List;

import keyterms.nlp.factories.MorphAnalyzerFactory;
import keyterms.nlp.factories.TextTransformerFactory;
import keyterms.nlp.interfaces.IMorphAnalyzer;
import keyterms.nlp.interfaces.ITextTransformer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.model.DisplayForm;
import keyterms.nlp.model.IndexForm;
import keyterms.nlp.model.Transliteration;
import keyterms.nlp.model.WordForm;
import keyterms.nlp.model.WordFormType;
import keyterms.nlp.text.ScriptProfiler;
import keyterms.util.text.Strings;

public class TextHandler {

    private static final ScriptProfiler PROFILER = new ScriptProfiler();

    private Language language;

    // For normalization and transliteration
    private final TextTransformerFactory transformerFactory;
    private final MorphAnalyzerFactory morphFactory;
    private ITextTransformer transformer;

    // For stemming / morphological analysis
    private IMorphAnalyzer morpher;  // a bit of a hack to have this here

    public TextHandler() {
        super();
        transformerFactory = new TextTransformerFactory();
        morphFactory = new MorphAnalyzerFactory();
        setLanguage(Language.UND);
    }

    public void setLanguage(String languageCode) {
        Language language = Language.byCode(languageCode);
        if (language == null) {
            language = Language.UND;
        }
        setLanguage(language);
    }

    public void setLanguage(Language language) {
        this.language = (language != null) ? language : Language.UND;
        transformer = transformerFactory.getTransformer(this.language);
        morpher = morphFactory.getMorphAnalyzer(this.language);
    }

    public DisplayForm getDisplayText(String text) {
        String displayText = (text != null) ? transformer.normalizeForDisplay(text) : null;
        if (Strings.isBlank(displayText)) {
            displayText = Strings.trim(text);
        }
        return new DisplayForm(displayText);
    }

    public IndexForm getIndexForm(String text) {
        if (text == null) {
            return null;
        }
        Script sourceScript = PROFILER.profile(text).getScript(true);
        String scriptStr = sourceScript.getCode();

        String indexText = transformer.normalizeForIndex(text);
        if (Strings.isBlank(indexText)) {
            indexText = text;
        }
        Script preferredScript = language.getPreferredScript();
        boolean isScrScriptPreferred = preferredScript == sourceScript;
        if (preferredScript == Script.HANI) {
            if (sourceScript == Script.HANS || sourceScript == Script.HANT) {
                isScrScriptPreferred = true;
            }
        }
        if (Language.UND.equals(language)) {
            isScrScriptPreferred = true;
        }
        return new IndexForm(isScrScriptPreferred, scriptStr, indexText);
    }

    public List<WordForm> getWordForms(String text) {
        List<WordForm> forms;
        if (text == null || morpher == null) {
            forms = new ArrayList<>();
            WordForm fakeForm = new WordForm(text, WordFormType.ENTRY);
            forms.add(fakeForm);
        } else {
            forms = morpher.getWordForms(text);
        }
        return forms;
    }

    public List<Transliteration> getAvailableTransforms(String text) {
        return (text != null) ? transformer.getAvailableTransforms(text, language) : null;
    }
}