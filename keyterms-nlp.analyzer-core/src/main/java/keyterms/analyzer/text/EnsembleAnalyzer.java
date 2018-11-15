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

package keyterms.analyzer.text;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.util.collect.Bags;
import keyterms.util.io.Encoding;
import keyterms.util.text.Strings;

/**
 * An ensemble analyzer for analyzer features related to text (see {@code TextInfo}).
 *
 * <p> Ensemble analyzers rely on concrete analyzers from the {@code CoreAnalyzers} instance. </p>
 * <p> Ensemble analyzers evaluate each text analysis feature semi-independently. </p>
 * <p> Ensemble analyzers produce only a single analysis which is not scored. </p>
 */
public abstract class EnsembleAnalyzer
        extends Analyzer {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 8250802275563185863L;

    /**
     * The types of input accepted by the analyzer.
     */
    public static final Set<Class<?>> INPUT_CLASSES = Bags.staticSet(
            byte[].class, CharSequence.class
    );

    /**
     * The analysis features that the analyzer can produce.
     */
    public static final Set<AnalysisFeature<?>> OUTPUT_FEATURES = Bags.staticSet(
            TextInfo.SIZE, TextInfo.ENCODING, TextInfo.LENGTH, TextInfo.LANGUAGE, TextInfo.SCRIPT
    );

    /**
     * A flag indicating whether the analyzer produces multiple analyses.
     */
    public static final boolean PRODUCES_RANKINGS = false;

    /**
     * A flag indicating whether the analyzer produces meaningful scores.
     */
    public static final boolean PRODUCES_SCORES = false;

    /**
     * Constructor.
     */
    public EnsembleAnalyzer() {
        super(INPUT_CLASSES, OUTPUT_FEATURES, PRODUCES_RANKINGS, PRODUCES_SCORES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _analyze(Object input, Consumer<Analysis> collector) {
        Working working = startIdentification(input);
        if (working.isBinary) {
            identifyEncoding(working);
            if (working.textInfo.getEncoding() != null) {
                Charset encoding = Encoding.getCharset(working.textInfo.getEncoding());
                if (encoding != null) {
                    working.inputText = Encoding.decode(working.inputData, encoding);
                    working.setLength(working.inputText.length());
                }
            }
        }
        identifyLanguage(working);
        identifyScript(working);
        collector.accept(working.textInfo);
    }

    /**
     * Start the analysis process for an individual input.
     *
     * @param input The input.
     *
     * @return A container for the working data.
     */
    protected abstract Working startIdentification(Object input);

    /**
     * Identify the character encoding of the input data.
     *
     * @param working The working analysis.
     */
    protected abstract void identifyEncoding(Working working);

    /**
     * Identify the language of the input data.
     *
     * @param working The working analysis.
     */
    protected abstract void identifyLanguage(Working working);

    /**
     * Identify the script of the input data.
     *
     * @param working The working analysis.
     */
    protected abstract void identifyScript(Working working);

    /**
     * Working information for the ensemble processes.
     */
    protected static class Working {
        /**
         * The working text information.
         */
        private final TextInfo textInfo;

        /**
         * The binary form of the input data.
         */
        private final byte[] inputData;

        /**
         * A flag indicating whether the original input was binary data.
         */
        private boolean isBinary;

        /**
         * The text form of the input data.
         */
        private String inputText;

        /**
         * A map of prior analysis results.
         */
        private final Map<Object, Map<AnalyzerId, List<Analysis>>> priorResults = new HashMap<>();

        /**
         * Constructor.
         *
         * @param input The input data.
         */
        protected Working(Object input) {
            super();
            textInfo = new TextInfo();
            isBinary = (input instanceof byte[]);
            if (isBinary) {
                inputData = (byte[])input;
            } else {
                inputText = Strings.toString(input);
                inputData = Encoding.encode(inputText, Encoding.UTF8);
                textInfo.setEncoding(Encoding.UTF8.name());
                textInfo.setLength(inputText.length());
            }
            textInfo.setSize(inputData.length);
        }

        /**
         * Determine if the original input was binary data.
         *
         * @return A flag indicating whether the original input was binary data.
         */
        public boolean isBinary() {
            return isBinary;
        }

        /**
         * Override the binary determination to force an encoding phase.
         */
        protected void setBinary() {
            this.isBinary = true;
        }

        /**
         * Get the binary form of the input data.
         *
         * @return The binary form of the input data.
         */
        public byte[] getInputData() {
            return inputData;
        }

        /**
         * Get the text form of the input data.
         *
         * @return The text form of the input data.
         */
        public String getInputText() {
            return inputText;
        }

        /**
         * Get the binary size of the associated text input.
         *
         * @return The binary size of the associated text input.
         */
        public Integer getSize() {
            return textInfo.getSize();
        }

        /**
         * Get the name of the character encoding scheme for the associated text input.
         *
         * @return The name of the character encoding scheme for the associated text input.
         */
        public String getEncoding() {
            return textInfo.getEncoding();
        }

        /**
         * Set the name of the character encoding scheme for the associated text input.
         *
         * @param encoding The name of the character encoding scheme for the associated text input.
         */
        public void setEncoding(String encoding) {
            String oldEncoding = textInfo.getEncoding();
            textInfo.setEncoding(encoding);
            if ((!Strings.isBlank(oldEncoding)) || (!Objects.equals(oldEncoding, textInfo.getEncoding()))) {
                Integer length = null;
                if (!Strings.isBlank(encoding)) {
                    Charset charset = Encoding.getCharset(encoding);
                    if (charset != null) {
                        inputText = Encoding.decode(inputData, charset);
                        length = inputText.length();
                    }
                }
                textInfo.setLength(length);
            }
        }

        /**
         * Get the length of the associated text.
         *
         * @return The length of the associated text.
         */
        public Integer getLength() {
            return textInfo.getLength();
        }

        /**
         * Set the length of the associated text.
         *
         * @param length The length of the associated text.
         */
        public void setLength(Integer length) {
            textInfo.setLength(length);
        }

        /**
         * Get the language of the associated text.
         *
         * @return The language of the associated text.
         */
        public Language getLanguage() {
            return textInfo.getLanguage();
        }

        /**
         * Set the language of the associated text.
         *
         * @param language The language of the associated text.
         */
        public void setLanguage(Language language) {
            textInfo.setLanguage(language);
        }

        /**
         * Get the script of the associated text.
         *
         * @return The script of the associated text.
         */
        public Script getScript() {
            return textInfo.getScript();
        }

        /**
         * Set the script of the associated text.
         *
         * @param script The script of the associated text.
         */
        public void setScript(Script script) {
            textInfo.setScript(script);
        }

        /**
         * Get the working analysis.
         *
         * @return The working analysis.
         */
        public TextInfo getTextInfo() {
            return textInfo;
        }

        /**
         * Run all acceptable analyzers, reusing any prior results on equivalent data.
         *
         * @param idFilter A filter used to remove specific products from execution.
         * @param analyzerFilter A filter used to screen out unwanted analyzers.
         *
         * @return The analysis results from all matching analyzers.
         */
        public Map<Object, Map<AnalyzerId, List<Analysis>>> runAnalyzers(
                Predicate<AnalyzerId> idFilter,
                Predicate<Analyzer> analyzerFilter) {
            Map<Object, Map<AnalyzerId, List<Analysis>>> results = new HashMap<>();
            if (inputData != null) {
                Map<AnalyzerId, List<Analysis>> analyzerResults = runAnalyzers(inputData, idFilter, analyzerFilter);
                priorResults.computeIfAbsent(inputData, (o) -> new HashMap<>()).putAll(analyzerResults);
                results.put(inputData, analyzerResults);
            }
            if (!Strings.isBlank(inputText)) {
                Map<AnalyzerId, List<Analysis>> analyzerResults = runAnalyzers(inputText, idFilter, analyzerFilter);
                priorResults.computeIfAbsent(inputText, (o) -> new HashMap<>()).putAll(analyzerResults);
                results.put(inputText, analyzerResults);
            }
            return results;
        }

        /**
         * Run all acceptable analyzers, reusing any prior results on equivalent data.
         *
         * @param input The input data.
         * @param idFilter A filter used to remove specific products from execution.
         * @param analyzerFilter A filter used to screen out unwanted analyzers.
         *
         * @return The analysis results from all matching analyzers.
         */
        private Map<AnalyzerId, List<Analysis>> runAnalyzers(Object input,
                Predicate<AnalyzerId> idFilter,
                Predicate<Analyzer> analyzerFilter) {
            Map<AnalyzerId, List<Analysis>> results = new HashMap<>();
            if (input != null) {
                Set<AnalyzerId> reused = new HashSet<>();
                Map<AnalyzerId, List<Analysis>> prior = priorResults.get(input);
                if (prior != null) {
                    prior.forEach((id, analyzerResults) -> {
                        if ((idFilter == null) || (idFilter.test(id))) {
                            Analyzer analyzer = CoreAnalyzers.getInstance().get(id);
                            if (((analyzerFilter == null) || (analyzerFilter.test(analyzer))) &&
                                    (analyzer.accepts(input.getClass()))) {
                                reused.add(id);
                                results.put(id, analyzerResults);
                            }
                        }
                    });
                }
                results.putAll(CoreAnalyzers.getInstance().run(input,
                        (id) -> ((!reused.contains(id)) && ((idFilter == null) || (idFilter.test(id)))),
                        analyzerFilter));
            }
            return results;
        }
    }
}