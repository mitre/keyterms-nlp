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

package keyterms.analyzer.profiles;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import keyterms.analyzer.Analyzer;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.analyzer.profiles.model.FeatureData;
import keyterms.analyzer.profiles.model.FeatureModel;
import keyterms.analyzer.text.TextInfo;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.testing.TestData;
import keyterms.util.io.Encoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TextModels_UT {

    @BeforeClass
    public static void setupCoreAnalyzers() {
        CoreAnalyzers coreAnalyzers = CoreAnalyzers.getInstance();
        assertNotEquals(0, coreAnalyzers.size());
    }

    @Test
    public void getModels() {
        LinkedHashSet<AnalyzerId> ids = new LinkedHashSet<>(new TreeSet<>(CoreAnalyzers.getInstance().ids()));
        FeatureModel<String> encodingModel = TextModels.getEncodingModel(ids);
        assertNotNull(encodingModel);
        assertFalse(encodingModel.getInputFeatures().isEmpty());
        FeatureModel<Language> languageModel = TextModels.getLanguageModel(ids);
        assertNotNull(languageModel);
        assertTrue(encodingModel.getInputFeatures().size() * 2 <= languageModel.getInputFeatures().size());
        FeatureModel<Script> scriptModel = TextModels.getScriptModel(ids);
        assertNotNull(scriptModel);
        assertEquals(languageModel.getInputFeatures().size(), scriptModel.getInputFeatures().size());
    }

    @Test
    public void fillEncodingModel() {
        LinkedHashSet<AnalyzerId> ids = new LinkedHashSet<>(new TreeSet<>(CoreAnalyzers.getInstance().ids()));
        FeatureModel<String> encodingModel = TextModels.getEncodingModel(ids);
        assertNotNull(encodingModel);
        assertFalse(encodingModel.getInputFeatures().isEmpty());
        TestData.LANGUAGE_PHRASES.forEach((key, phrase) -> {
            byte[] input = Encoding.encode(phrase, Encoding.UTF8);
            WekaAnalyzer.WekaWorking working = new WekaAnalyzer.WekaWorking(input);
            FeatureData featureData = working.getFeatureData();
            TextModels.fillFeatures(working.getTextInfo(), featureData, encodingModel, TextModels.ENCODING_PREFIX,
                    working.runAnalyzers(ids::contains, (analyzer) -> analyzer.produces(TextInfo.ENCODING)));
            assertNotNull(featureData);
            assertFalse(featureData.getFeatures().isEmpty());
            assertNotEquals(encodingModel.getInputFeatures().size(), featureData.getFeatures().size());
            featureData.forEach((feature, value) -> assertNotNull(feature.name(), value));
            assertTrue(featureData.contains(encodingModel.getInputFeature(TextModels.BINARY_SIZE)));
        });
    }

    @Test
    public void fillLanguageModel() {
        LinkedHashSet<AnalyzerId> ids = new LinkedHashSet<>(new TreeSet<>(CoreAnalyzers.getInstance().ids()));
        FeatureModel<Language> languageModel = TextModels.getLanguageModel(ids);
        assertNotNull(languageModel);
        assertFalse(languageModel.getInputFeatures().isEmpty());
        TestData.LANGUAGE_PHRASES.forEach((key, phrase) -> {
            byte[] input = Encoding.encode(phrase, Encoding.UTF8);
            WekaAnalyzer.WekaWorking working = new WekaAnalyzer.WekaWorking(input);
            working.setEncoding("utf-8");
            FeatureData featureData = working.getFeatureData();
            TextModels.fillFeatures(working.getTextInfo(), featureData, languageModel, TextModels.ENCODING_PREFIX,
                    working.runAnalyzers(ids::contains, (analyzer) -> analyzer.produces(TextInfo.ENCODING)));
            TextModels.fillFeatures(working.getTextInfo(), featureData, languageModel, TextModels.LANGUAGE_PREFIX,
                    working.runAnalyzers(ids::contains, (analyzer) ->
                            analyzer.produces(TextInfo.LANGUAGE) || analyzer.produces(TextInfo.SCRIPT)));
            assertNotNull(featureData);
            assertNotEquals(0, featureData.getFeatures().size());
            Set<String> langIdsPresent = languageModel.getInputFeatures().stream()
                    .filter((f) -> f.name().startsWith(TextModels.LANGUAGE_PREFIX))
                    .filter((f) -> f.name().endsWith("_1"))
                    .map((f) -> f.name().replaceAll(TextModels.LANGUAGE_PREFIX + "(.*)_.+_1", "$1"))
                    .collect(Collectors.toSet());
            long langIdsExpected = ids.stream().filter((id) -> {
                Analyzer analyzer = CoreAnalyzers.getInstance().get(id);
                return ((analyzer.accepts(CharSequence.class)) &&
                        ((analyzer.produces(TextInfo.LANGUAGE)) || (analyzer.produces(TextInfo.SCRIPT))));
            }).count();
            assertEquals(langIdsExpected, langIdsPresent.size());
            featureData.forEach((feature, value) -> assertNotNull(feature.name(), value));
            assertTrue(featureData.contains(languageModel.getInputFeature(TextModels.BINARY_SIZE)));
            assertTrue(featureData.contains(languageModel.getInputFeature(TextModels.DETECTED_ENCODING)));
        });
    }

    @Test
    public void fillScriptModel() {
        LinkedHashSet<AnalyzerId> ids = new LinkedHashSet<>(new TreeSet<>(CoreAnalyzers.getInstance().ids()));
        FeatureModel<Script> scriptModel = TextModels.getScriptModel(ids);
        assertNotNull(scriptModel);
        assertFalse(scriptModel.getInputFeatures().isEmpty());
        TestData.LANGUAGE_PHRASES.forEach((key, phrase) -> {
            byte[] input = Encoding.encode(phrase, Encoding.UTF8);
            WekaAnalyzer.WekaWorking working = new WekaAnalyzer.WekaWorking(input);
            working.setEncoding("utf-8");
            working.setLanguage(Language.ENGLISH);
            FeatureData featureData = working.getFeatureData();
            TextModels.fillFeatures(working.getTextInfo(), featureData, scriptModel, TextModels.ENCODING_PREFIX,
                    working.runAnalyzers(ids::contains, (analyzer) -> analyzer.produces(TextInfo.ENCODING)));
            TextModels.fillFeatures(working.getTextInfo(), featureData, scriptModel, TextModels.LANGUAGE_PREFIX,
                    working.runAnalyzers(ids::contains, (analyzer) ->
                            analyzer.produces(TextInfo.LANGUAGE) || analyzer.produces(TextInfo.SCRIPT)));
            TextModels.fillFeatures(working.getTextInfo(), featureData, scriptModel, TextModels.SCRIPT_PREFIX,
                    working.runAnalyzers(ids::contains, (analyzer) ->
                            analyzer.produces(TextInfo.LANGUAGE) || analyzer.produces(TextInfo.SCRIPT)));
            assertNotNull(featureData);
            assertNotEquals(0, featureData.getFeatures().size());
            Set<String> scriptIdsPresent = scriptModel.getInputFeatures().stream()
                    .filter((f) -> f.name().startsWith(TextModels.SCRIPT_PREFIX))
                    .filter((f) -> f.name().endsWith("_1"))
                    .map((f) -> f.name().replaceAll(TextModels.SCRIPT_PREFIX + "(.*)_.+_1", "$1"))
                    .collect(Collectors.toSet());
            long scriptIdsExpected = ids.stream().filter((id) -> {
                Analyzer analyzer = CoreAnalyzers.getInstance().get(id);
                return ((analyzer.accepts(CharSequence.class)) &&
                        ((analyzer.produces(TextInfo.LANGUAGE)) || (analyzer.produces(TextInfo.SCRIPT))));
            }).count();
            assertEquals(scriptIdsExpected, scriptIdsPresent.size());
            featureData.forEach((feature, value) -> assertNotNull(feature.name(), value));
            assertTrue(phrase, featureData.contains(scriptModel.getInputFeature(TextModels.BINARY_SIZE)));
            assertTrue(phrase, featureData.contains(scriptModel.getInputFeature(TextModels.DETECTED_ENCODING)));
        });
    }
}