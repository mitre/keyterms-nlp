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
import java.util.List;
import java.util.Map;
import java.util.Set;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalyzerId;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.util.collect.Keyed;
import keyterms.util.io.Encoding;
import keyterms.util.text.Strings;

/**
 * The voting ensemble is a form of text analyzer which relies on the instantiated real analyzers in the
 * {@code CoreAnalyzers} singleton in order to function.
 *
 * <p> Unlike the core {@code Analyzer} implementations, this class is inherently thread safe. </p>
 *
 * <p> Only binary ({@code byte[]}) and text inputs ({@code CharSequence} are accepted. </p>
 */
public class VotingAnalyzer
        extends EnsembleAnalyzer {
    /**
     * The maximum number of votes per analyzer to consider in an election.
     */
    private static final int MAX_VOTES = 5;

    /**
     * Normalize the encoding name as much as possible so that equivalent entries will be seen as equal.
     *
     * @param encoding The character encoding name.
     *
     * @return The normalized character encoding name.
     */
    public static String normalizeEncoding(String encoding) {
        String normalized = Encoding.getLenientName(encoding);
        if (!Strings.isBlank(normalized)) {
            // Standardize BE versions to their endian-unspecified alias.
            normalized = normalized.replaceFirst("be$", "");
            // UCS-2 has been superseded by UTF-16.
            normalized = normalized.replaceFirst("^ucs2(.*)", "utf16$1");
            Charset charset = Encoding.getCharset(normalized);
            if (charset != null) {
                normalized = charset.name();
            }
        }
        return normalized;
    }

    /**
     * The set of identifiers for analyzers to include in the voting.
     */
    private final Set<AnalyzerId> analyzerIds;

    /**
     * Constructor.
     */
    public VotingAnalyzer() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param analyzerIds The set of identifiers for analyzers to include in the voting.
     */
    public VotingAnalyzer(Set<AnalyzerId> analyzerIds) {
        super();
        this.analyzerIds = analyzerIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Working startIdentification(Object input) {
        return new Working(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void identifyEncoding(Working working) {
        byte[] inputData = working.getInputData();
        if ((inputData != null) && (inputData.length > 0)) {
            Map<Object, Map<AnalyzerId, List<Analysis>>> encodingResults =
                    working.runAnalyzers(
                            (id) -> ((analyzerIds == null) || (analyzerIds.isEmpty()) || (analyzerIds.contains(id))),
                            (analyzer) -> analyzer.produces(TextInfo.ENCODING));
            Map<String, Double> countsPerAnalyzer = new HashMap<>();
            encodingResults.forEach((i, ir) -> ir.keySet().stream()
                    .map(AnalyzerId::getAnalyzerId)
                    .forEach((analyzerId) -> countsPerAnalyzer.compute(analyzerId, (id, count) ->
                            (count != null) ? count + 1 : 1)));
            Election<String> encodingElection = new Election<>(MAX_VOTES);
            encodingResults.forEach((i, ir) -> {
                if (!ir.isEmpty()) {
                    ir.forEach((analyzer, results) -> {
                        double weight = 1.0 / countsPerAnalyzer.get(analyzer.getAnalyzerId());
                        for (int r = 0; r < results.size(); r++) {
                            Analysis result = results.get(r);
                            String encoding = normalizeEncoding(result.get(TextInfo.ENCODING));
                            result.set(TextInfo.ENCODING, encoding);
                            if (encoding != null) {
                                encodingElection.add(encoding, r + 1, weight);
                            }
                        }
                    });
                }
            });
            List<Keyed<String, Double>> electionResults = encodingElection.getResults();
            if (!electionResults.isEmpty()) {
                working.setEncoding(electionResults.get(0).getKey());
            } else {
                getLogger().error("Could not determine encoding for binary input.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void identifyLanguage(Working working) {
        if (!Strings.isBlank(working.getInputText())) {
            Map<Object, Map<AnalyzerId, List<Analysis>>> languageResults =
                    working.runAnalyzers(
                            (id) -> ((analyzerIds == null) || (analyzerIds.isEmpty()) || (analyzerIds.contains(id))),
                            (analyzer) -> analyzer.produces(TextInfo.LANGUAGE));
            Map<String, Double> countsPerAnalyzer = new HashMap<>();
            languageResults.forEach((i, ir) -> ir.keySet().stream()
                    .map(AnalyzerId::getAnalyzerId)
                    .forEach((analyzerId) -> countsPerAnalyzer.compute(analyzerId, (id, count) ->
                            (count != null) ? count + 1 : 1)));
            Election<Language> languageElection = new Election<>(MAX_VOTES);
            languageResults.forEach((i, ir) -> {
                if (!ir.isEmpty()) {
                    ir.forEach((analyzer, results) -> {
                        double weight = 1.0 / countsPerAnalyzer.get(analyzer.getAnalyzerId());
                        for (int r = 0; r < results.size(); r++) {
                            Analysis result = results.get(r);
                            Language language = result.get(TextInfo.LANGUAGE);
                            if (language != null) {
                                languageElection.add(language, r + 1, weight);
                            }
                        }
                    });
                }
            });
            List<Keyed<Language, Double>> electionResults = languageElection.getResults();
            if (!electionResults.isEmpty()) {
                working.setLanguage(electionResults.get(0).getKey());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void identifyScript(Working working) {
        if (!Strings.isBlank(working.getInputText())) {
            Map<Object, Map<AnalyzerId, List<Analysis>>> scriptResults =
                    working.runAnalyzers(
                            (id) -> ((analyzerIds == null) || (analyzerIds.isEmpty()) || (analyzerIds.contains(id))),
                            (analyzer) -> analyzer.produces(TextInfo.SCRIPT));
            Map<String, Double> countsPerAnalyzer = new HashMap<>();
            scriptResults.forEach((i, ir) -> ir.keySet().stream()
                    .map(AnalyzerId::getAnalyzerId)
                    .forEach((analyzerId) -> countsPerAnalyzer.compute(analyzerId, (id, count) ->
                            (count != null) ? count + 1 : 1)));
            Election<Script> scriptElection = new Election<>(MAX_VOTES);
            scriptResults.forEach((i, ir) -> {
                if (!ir.isEmpty()) {
                    ir.forEach((analyzer, results) -> {
                        double weight = 1.0 / countsPerAnalyzer.get(analyzer.getAnalyzerId());
                        for (int r = 0; r < results.size(); r++) {
                            Analysis result = results.get(r);
                            Script script = result.get(TextInfo.SCRIPT);
                            if (script != null) {
                                scriptElection.add(script, r + 1, weight);
                            }
                        }
                    });
                }
            });
            List<Keyed<Script, Double>> electionResults = scriptElection.getResults();
            if (!electionResults.isEmpty()) {
                working.setScript(electionResults.get(0).getKey());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _dispose() {
        //Intentional NoOp
    }
}