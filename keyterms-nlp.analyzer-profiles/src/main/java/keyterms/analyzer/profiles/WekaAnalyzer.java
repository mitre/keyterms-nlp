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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.analyzer.profiles.model.FeatureData;
import keyterms.analyzer.text.EnsembleAnalyzer;
import keyterms.analyzer.text.TextInfo;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;

/**
 * A text analyzer which uses multiple random forest models to produce a single analysis.
 */
public class WekaAnalyzer
        extends EnsembleAnalyzer
        implements Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -3277600760635794585L;

    /**
     * The identifiers for analyzers that must be present in the core analyzer pools for the analysis models to
     * function correctly.
     */
    private final Set<AnalyzerId> requiredAnalyzers;

    /**
     * The classification model for encoding detection.
     */
    private final WekaForest<String> encodingAnalyzer;

    /**
     * The classification model for language detection.
     */
    private final WekaForest<Language> languageAnalyzer;

    /**
     * The classification model for script detection.
     */
    private final WekaForest<Script> scriptAnalyzer;

    /**
     * Constructor.
     *
     * @param requiredAnalyzers The identifiers for analyzers that must be present in the core analyzer pools for the
     * analysis models to function correctly.
     * @param encodingAnalyzer The encoding analyzer.
     * @param languageAnalyzer The language analyzer.
     * @param scriptAnalyzer The script analyzer.
     */
    public WekaAnalyzer(
            Set<AnalyzerId> requiredAnalyzers,
            WekaForest<String> encodingAnalyzer,
            WekaForest<Language> languageAnalyzer,
            WekaForest<Script> scriptAnalyzer) {
        super();
        if (requiredAnalyzers == null) {
            throw new NullPointerException("No required core analyzers specified.");
        }
        if (encodingAnalyzer == null) {
            throw new NullPointerException("Encoding analyzer is required.");
        }
        if (languageAnalyzer == null) {
            throw new NullPointerException("Language analyzer is required.");
        }
        if (scriptAnalyzer == null) {
            throw new NullPointerException("Script analyzer is required.");
        }
        // A new HashSet is used to avoid non-serializable set implementations.
        this.requiredAnalyzers = Collections.unmodifiableSet(requiredAnalyzers.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        if (this.requiredAnalyzers.isEmpty()) {
            throw new IllegalArgumentException("No required core analyzers specified.");
        }
        this.encodingAnalyzer = encodingAnalyzer;
        this.languageAnalyzer = languageAnalyzer;
        this.scriptAnalyzer = scriptAnalyzer;
        checkRequiredAnalyzers();
    }

    /**
     * Get the identifiers for analyzers that must be present in the core analyzer pools for the analysis models to
     * function correctly.
     *
     * @return The identifiers for the required analyzers.
     */
    public Set<AnalyzerId> getRequiredAnalyzers() {
        return requiredAnalyzers;
    }

    /**
     * Check that the required analyzers are available.
     *
     * <p> An {@code IllegalStateException} will be thrown if a required core analyzer is not available. </p>
     */
    public void checkRequiredAnalyzers() {
        Set<AnalyzerId> available = CoreAnalyzers.getInstance().ids();
        requiredAnalyzers.forEach((id) -> {
            if (!available.contains(id)) {
                throw new IllegalStateException("Required core analyzer not available: " + id);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Working startIdentification(Object input) {
        return new WekaWorking(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void identifyEncoding(Working working) {
        FeatureData featureData = ((WekaWorking)working).getFeatureData();
        TextModels.fillFeatures(working.getTextInfo(), featureData,
                encodingAnalyzer.getFeatureModel(),
                TextModels.ENCODING_PREFIX,
                working.runAnalyzers(requiredAnalyzers::contains,
                        (analyzer) -> analyzer.produces(TextInfo.ENCODING)));
        if (((WekaWorking)working).originalBinary) {
            List<Analysis> encodingResults = encodingAnalyzer.analyze(featureData);
            if (!encodingResults.isEmpty()) {
                String encoding = encodingResults.get(0).get(TextInfo.ENCODING);
                working.setEncoding(encoding);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void identifyLanguage(Working working) {
        FeatureData featureData = ((WekaWorking)working).getFeatureData();
        TextModels.fillFeatures(working.getTextInfo(), featureData,
                languageAnalyzer.getFeatureModel(),
                TextModels.LANGUAGE_PREFIX,
                working.runAnalyzers(
                        requiredAnalyzers::contains,
                        (analyzer) -> ((analyzer.produces(TextInfo.LANGUAGE)) ||
                                (analyzer.produces(TextInfo.SCRIPT)))));
        List<Analysis> languageResults = languageAnalyzer.analyze(featureData);
        if (!languageResults.isEmpty()) {
            Language language = languageResults.get(0).get(TextInfo.LANGUAGE);
            working.setLanguage(language);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void identifyScript(Working working) {
        FeatureData featureData = ((WekaWorking)working).getFeatureData();
        TextModels.fillFeatures(working.getTextInfo(), featureData,
                scriptAnalyzer.getFeatureModel(),
                TextModels.SCRIPT_PREFIX,
                working.runAnalyzers(
                        requiredAnalyzers::contains,
                        (analyzer) -> ((analyzer.produces(TextInfo.LANGUAGE)) ||
                                (analyzer.produces(TextInfo.SCRIPT)))));
        List<Analysis> scriptResults = scriptAnalyzer.analyze(featureData);
        if (!scriptResults.isEmpty()) {
            Script script = scriptResults.get(0).get(TextInfo.SCRIPT);
            working.setScript(script);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _dispose() {
        encodingAnalyzer.dispose();
        languageAnalyzer.dispose();
        scriptAnalyzer.dispose();
    }

    /**
     * A customization of the ensemble working object which maintains feature data across the analysis phases.
     */
    static class WekaWorking
            extends Working {
        /**
         * A flag indicating whether the original input was binary data.
         */
        private final boolean originalBinary;

        /**
         * The feature data for the analysis.
         */
        private final FeatureData featureData;

        /**
         * Constructor.
         *
         * @param input The input data.
         */
        WekaWorking(Object input) {
            super(input);
            originalBinary = isBinary();
            setBinary();
            featureData = new FeatureData();
        }

        /**
         * Get the feature data for the analysis.
         *
         * @return The feature data for the analysis.
         */
        FeatureData getFeatureData() {
            return featureData;
        }
    }
}