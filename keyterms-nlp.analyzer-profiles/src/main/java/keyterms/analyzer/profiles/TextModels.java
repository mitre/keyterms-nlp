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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.analyzer.profiles.model.EnumeratedFeature;
import keyterms.analyzer.profiles.model.FeatureData;
import keyterms.analyzer.profiles.model.FeatureModel;
import keyterms.analyzer.profiles.model.IntegerFeature;
import keyterms.analyzer.profiles.model.ModelFeature;
import keyterms.analyzer.profiles.model.RealFeature;
import keyterms.analyzer.text.TextInfo;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.util.text.Strings;

/**
 * Utilities for building text analysis models for the random forest classifiers.
 */
public final class TextModels {
    /**
     * The maximum number of votes per analyzer to consider in the models.
     */
    private static final int MAX_VOTES = 3;

    /**
     * The encoding model prefix.
     */
    static final String ENCODING_PREFIX = "b_";

    /**
     * The language model prefix.
     */
    static final String LANGUAGE_PREFIX = "t_";

    /**
     * The script model prefix.
     */
    static final String SCRIPT_PREFIX = "t_";

    /**
     * The feature name for binary size.
     */
    static final String BINARY_SIZE = "b_size";

    /**
     * The feature name for detected encoding.
     */
    static final String DETECTED_ENCODING = "d_enc";

    /**
     * The feature name for detected language.
     */
    private static final String DETECTED_LANGUAGE = "d_lang";

    /**
     * The feature name for detected script.
     */
    private static final String DETECTED_SCRIPT = "d_script";

    /**
     * The feature name for encoding output.
     */
    private static final String ENCODING_OUTPUT = "o_enc";

    /**
     * The feature name for language output.
     */
    private static final String LANGUAGE_OUTPUT = "o_lang";

    /**
     * The feature name for script output.
     */
    private static final String SCRIPT_OUTPUT = "o_script";

    /**
     * Create a new feature to store encoding information.
     *
     * @param name The feature name.
     *
     * @return The specified feature.
     */
    private static ModelFeature<String> newEncodingFeature(String name) {
        return new EnumeratedFeature<>(name, String.class, Strings::toString, String::toLowerCase);
    }

    /**
     * Create a new feature to store language information.
     *
     * @param name The feature name.
     *
     * @return The specified feature.
     */
    private static ModelFeature<Language> newLanguageFeature(String name) {
        return new EnumeratedFeature<>(name, Language.class, Language::byText,
                (language) -> language.getCode().toLowerCase());
    }

    /**
     * Create a new feature to store script information.
     *
     * @param name The feature name.
     *
     * @return The specified feature.
     */
    private static ModelFeature<Script> newScriptFeature(String name) {
        return new EnumeratedFeature<>(name, Script.class, Script::byText,
                (script) -> script.getCode().toLowerCase());
    }

    /**
     * Construct the feature model for encoding analysis given the specified set of required analyzers.
     *
     * @param requiredAnalyzers The identifiers for analyzers that must be present in the core analyzer pools for the
     * analysis models to function correctly.
     *
     * @return The specified feature model.
     */
    public static FeatureModel<String> getEncodingModel(Set<AnalyzerId> requiredAnalyzers) {
        FeatureModel<String> model = new FeatureModel<>(newEncodingFeature(ENCODING_OUTPUT));
        model.addInputFeature(new IntegerFeature(BINARY_SIZE));
        addInputFeatures(model, ENCODING_PREFIX, requiredAnalyzers, (analyzer) ->
                ((analyzer.accepts(byte[].class)) && (analyzer.produces(TextInfo.ENCODING))));
        return model;
    }

    /**
     * Construct the feature model for language analysis given the specified set of required analyzers.
     *
     * @param requiredAnalyzers The identifiers for analyzers that must be present in the core analyzer pools for the
     * analysis models to function correctly.
     *
     * @return The specified feature model.
     */
    public static FeatureModel<Language> getLanguageModel(Set<AnalyzerId> requiredAnalyzers) {
        FeatureModel<Language> model = new FeatureModel<>(newLanguageFeature(LANGUAGE_OUTPUT));
        model.addInputFeature(new IntegerFeature(BINARY_SIZE));
        model.addInputFeature(newEncodingFeature(DETECTED_ENCODING));
        addInputFeatures(model, ENCODING_PREFIX, requiredAnalyzers, (analyzer) ->
                ((analyzer.accepts(byte[].class)) && (analyzer.produces(TextInfo.ENCODING))));
        addInputFeatures(model, LANGUAGE_PREFIX, requiredAnalyzers, (analyzer) ->
                ((analyzer.produces(TextInfo.LANGUAGE)) || (analyzer.produces(TextInfo.SCRIPT))));
        return model;
    }

    /**
     * Construct the feature model for script analysis given the specified set of required analyzers.
     *
     * <p> It is by design that the language and script models are essentially identical.  This allows for the best
     * re-use of analyzer output and feature data between the language/script phases. </p>
     *
     * @param requiredAnalyzers The identifiers for analyzers that must be present in the core analyzer pools for the
     * analysis models to function correctly.
     *
     * @return The specified feature model.
     */
    public static FeatureModel<Script> getScriptModel(Set<AnalyzerId> requiredAnalyzers) {
        FeatureModel<Script> model = new FeatureModel<>(newScriptFeature(SCRIPT_OUTPUT));
        model.addInputFeature(new IntegerFeature(BINARY_SIZE));
        model.addInputFeature(newEncodingFeature(DETECTED_ENCODING));
        addInputFeatures(model, ENCODING_PREFIX, requiredAnalyzers, (analyzer) ->
                ((analyzer.accepts(byte[].class)) && (analyzer.produces(TextInfo.ENCODING))));
        addInputFeatures(model, SCRIPT_PREFIX, requiredAnalyzers, (analyzer) ->
                ((analyzer.produces(TextInfo.LANGUAGE)) || (analyzer.produces(TextInfo.SCRIPT))));
        return model;
    }

    /**
     * Add input features to a feature model as specified.
     *
     * @param model The feature model.
     * @param prefix The prefix for the features.
     * @param requiredAnalyzers The identifiers for analyzers that must be present in the core analyzer pools for the
     * analysis models to function correctly.
     * @param analyzerFilter The filter used to select analyzers which will add features to the model.
     */
    private static void addInputFeatures(FeatureModel<?> model, String prefix,
            Set<AnalyzerId> requiredAnalyzers, Predicate<Analyzer> analyzerFilter) {
        new ArrayList<>(new TreeSet<>(requiredAnalyzers)).forEach((id) -> {
            Analyzer analyzer = CoreAnalyzers.getInstance().get(id);
            if (analyzer == null) {
                throw new IllegalArgumentException("Required core analyzer not available: " + id);
            }
            if ((analyzerFilter == null) || (analyzerFilter.test(analyzer))) {
                addInputFeatures(model, id, prefix, analyzer);
            }
        });
    }

    /**
     * Add input features to a feature model as specified.
     *
     * @param model The feature model.
     * @param id The identifier for the analyzer.
     * @param prefix The prefix for the features.
     * @param analyzer The analyzer for which input features are being produced.
     */
    private static void addInputFeatures(FeatureModel<?> model, AnalyzerId id, String prefix, Analyzer analyzer) {
        Set<AnalysisFeature<?>> outputFeatures = analyzer.getOutputFeatures();
        int rankings = analyzer.producesRankings() ? MAX_VOTES : 1;
        boolean scores = analyzer.producesScores();
        for (int r = 1; r <= rankings; r++) {
            if (outputFeatures.contains(TextInfo.ENCODING)) {
                model.addInputFeature(newEncodingFeature(prefix + id + "_enc_" + r));
            }
            if (outputFeatures.contains(TextInfo.LANGUAGE)) {
                model.addInputFeature(newLanguageFeature(prefix + id + "_lang_" + r));
            }
            if (outputFeatures.contains(TextInfo.SCRIPT)) {
                model.addInputFeature(newScriptFeature(prefix + id + "_script_" + r));
            }
            if (scores) {
                model.addInputFeature(new RealFeature(prefix + id + "_score_" + r));
            }
        }
    }

    /**
     * Populate the featured data as appropriate.
     *
     * @param textInfo The current analysis.
     * @param featureData The feature data to fill.
     * @param model The feature model associated with the feature data.
     * @param prefix The prefix for desired features.
     * @param analyzerResults The analyzer results used to populate the feature data.
     */
    public static void fillFeatures(TextInfo textInfo, FeatureData featureData, FeatureModel<?> model, String prefix,
            Map<Object, Map<AnalyzerId, List<Analysis>>> analyzerResults) {
        // Features related to current analysis and not analyzer output.
        fillFeature(featureData, model, BINARY_SIZE, textInfo.getSize());
        fillFeature(featureData, model, DETECTED_ENCODING, textInfo.getEncoding());
        fillFeature(featureData, model, DETECTED_LANGUAGE, textInfo.getLanguage());
        fillFeature(featureData, model, DETECTED_SCRIPT, textInfo.getScript());
        // Features related to analyzer output.
        analyzerResults.forEach((i, ir) -> {
            if ((i instanceof byte[]) && (prefix.equalsIgnoreCase(ENCODING_PREFIX))) {
                ir.forEach((id, results) ->
                        fillFeatures(featureData, model, prefix, id, results));
            }
            if ((i instanceof CharSequence) && (!prefix.equalsIgnoreCase(ENCODING_PREFIX))) {
                ir.forEach((id, results) ->
                        fillFeatures(featureData, model, prefix, id, results));
            }
        });
    }

    /**
     * Update the specified features related to a specific analyzer output.
     *
     * @param featureData The feature data to fill.
     * @param model The feature model associated with the feature data.
     * @param prefix The prefix for desired features.
     * @param id The identifier for the analyzer.
     * @param results The analyzer results.
     */
    private static void fillFeatures(FeatureData featureData, FeatureModel<?> model, String prefix,
            AnalyzerId id, List<Analysis> results) {
        Analyzer analyzer = CoreAnalyzers.getInstance().get(id);
        Set<AnalysisFeature<?>> outputFeatures = analyzer.getOutputFeatures();
        int rankings = analyzer.producesRankings() ? MAX_VOTES : 1;
        boolean scores = analyzer.producesScores();
        for (int r = 1; r <= rankings; r++) {
            Analysis analysis = (results.size() >= r) ? results.get(r - 1) : null;
            if (analysis != null) {
                if (outputFeatures.contains(TextInfo.ENCODING)) {
                    String featureName = prefix + id + "_enc_" + r;
                    fillFeature(featureData, model, featureName, analysis.get(TextInfo.ENCODING));
                }
                if (outputFeatures.contains(TextInfo.LANGUAGE)) {
                    String featureName = prefix + id + "_lang_" + r;
                    fillFeature(featureData, model, featureName, analysis.get(TextInfo.LANGUAGE));
                }
                if (outputFeatures.contains(TextInfo.SCRIPT)) {
                    String featureName = prefix + id + "_script_" + r;
                    fillFeature(featureData, model, featureName, analysis.get(TextInfo.SCRIPT));
                }
                if (scores) {
                    String featureName = prefix + id + "_score_" + r;
                    fillFeature(featureData, model, featureName, analysis.getScore());
                }
            }
        }
    }

    /**
     * Update the specified feature.
     *
     * @param featureData The feature data to update.
     * @param model The feature model associated with the specified feature data.
     * @param featureName The name of the desired input feature.
     * @param value The new value for the feature.
     */
    @SuppressWarnings("unchecked")
    private static void fillFeature(FeatureData featureData, FeatureModel<?> model, String featureName, Object value) {
        ModelFeature<Object> modelFeature = (ModelFeature<Object>)model.getInputFeature(featureName);
        if ((modelFeature != null) && (modelFeature.test(value)) &&
                (value != null) && (!Objects.equals(featureData.get(modelFeature), value))) {
            featureData.set(modelFeature, modelFeature.cast(value));
        }
    }

    /**
     * Constructor.
     */
    private TextModels() {
        super();
    }
}