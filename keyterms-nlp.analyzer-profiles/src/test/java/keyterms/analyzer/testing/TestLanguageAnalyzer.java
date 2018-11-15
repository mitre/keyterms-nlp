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

package keyterms.analyzer.testing;

import java.util.Map;
import java.util.function.Consumer;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.text.TextInfo;
import keyterms.nlp.iso.Language;
import keyterms.testing.TestData;
import keyterms.util.collect.Bags;
import keyterms.util.text.Strings;

public class TestLanguageAnalyzer
        extends Analyzer {

    public TestLanguageAnalyzer() {
        super(Bags.staticSet(CharSequence.class), Bags.staticSet(TextInfo.LANGUAGE), false, false);
    }

    @Override
    protected void _analyze(Object input, Consumer<Analysis> collector) {
        Language language = Language.ENGLISH;
        String text = Strings.toString(input);
        for (Map.Entry<String, String> entry : TestData.LANGUAGE_PHRASES.entrySet()) {
            if (entry.getValue().equals(text)) {
                language = Language.byCode(entry.getKey().replaceFirst("([^-]*)-(.*)", "$1"));
            }
        }
        TextInfo textInfo = new TextInfo();
        textInfo.setLanguage(language);
        collector.accept(textInfo);
    }

    @Override
    protected void _dispose() {
    }
}