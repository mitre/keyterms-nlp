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

package keyterms.analyzers.jscript;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.text.TextInfo;
import keyterms.nlp.iso.Script;
import keyterms.nlp.text.ScriptProfile;
import keyterms.nlp.text.ScriptProfiler;
import keyterms.util.collect.Bags;
import keyterms.util.text.Strings;

/**
 * An analyzer which is based on character script profiling.
 */
public class ScriptProfileAnalyzer
        extends Analyzer {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 1981483171191556818L;

    /**
     * The types of input accepted by the analyzer.
     */
    static final Set<Class<?>> INPUT_CLASSES = Bags.staticSet(
            CharSequence.class
    );

    /**
     * The analysis features that the analyzer can produce.
     */
    static final Set<AnalysisFeature<?>> OUTPUT_FEATURES = Bags.staticSet(
            TextInfo.SCRIPT
    );

    /**
     * A flag indicating whether the analyzer produces multiple analyses.
     */
    static final boolean PRODUCES_RANKINGS = true;

    /**
     * A flag indicating whether the analyzer produces meaningful scores.
     */
    static final boolean PRODUCES_SCORES = true;

    /**
     * The script profiler.
     */
    private final ScriptProfiler profiler = new ScriptProfiler();

    /**
     * Constructor.
     */
    public ScriptProfileAnalyzer() {
        super(INPUT_CLASSES, OUTPUT_FEATURES, PRODUCES_RANKINGS, PRODUCES_SCORES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _analyze(Object input, Consumer<Analysis> collector) {
        String text = Strings.toString(input);
        if (!Strings.isBlank(text)) {
            ScriptProfile profile = profiler.profile(text);
            List<ScriptProfile.Entry> entries = profile.getEntries().stream()
                    .filter(Objects::nonNull)
                    .filter((e) -> !Script.ZYYY.equals(e.getScript()))
                    .filter((e) -> !Script.UNKNOWN.equals(e.getScript()))
                    .collect(Collectors.toList());
            if ((entries.size() > 1) && (!entries.get(0).getScript().equals(profile.getScript(true)))) {
                entries.remove(0);
            }
            for (ScriptProfile.Entry entry : entries) {
                TextInfo textInfo = new TextInfo();
                textInfo.setScript(entry.getScript());
                textInfo.setScore(entry.getCodePointPercentage());
                collector.accept(textInfo);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _dispose() {
        // Intentional NoOp
    }
}