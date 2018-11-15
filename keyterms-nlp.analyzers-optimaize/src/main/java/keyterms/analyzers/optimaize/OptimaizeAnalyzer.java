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

package keyterms.analyzers.optimaize;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.text.TextInfo;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.collect.Bags;
import keyterms.util.lang.Lazy;
import keyterms.util.text.Strings;

/**
 * A language identifier based on the Optimaize libraries.
 */
public class OptimaizeAnalyzer
        extends Analyzer {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -2507630786002850048L;

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
            TextInfo.LANGUAGE, TextInfo.SCRIPT
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
     * Only one instance of the text object factory should be created.
     */
    private static final Lazy<TextObjectFactory> TEXT_OBJECT_FACTORY =
            new Lazy<>(CommonTextObjectFactories::forDetectingOnLargeText);

    /**
     * The text object factory for the detector.
     */
    private final TextObjectFactory textObjectFactory;

    /**
     * d The language detector.
     */
    private final LanguageDetector detector;

    /**
     * Constructor.
     */
    public OptimaizeAnalyzer() {
        super(INPUT_CLASSES, OUTPUT_FEATURES, PRODUCES_RANKINGS, PRODUCES_SCORES);
        textObjectFactory = TEXT_OBJECT_FACTORY.value();
        LanguageDetector detector = null;
        try {
            detector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(new LanguageProfileReader().readAllBuiltIn())
                    .build();
        } catch (Exception error) {
            getLogger().error("Error initializing detector.", error);
        }
        this.detector = detector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _analyze(Object input, Consumer<Analysis> collector) {
        String text = Strings.toString(input);
        if (!Strings.isBlank(text)) {
            TextObject textObject = textObjectFactory.forText(text);
            List<DetectedLanguage> results = detector.getProbabilities(textObject);
            Collections.sort(results);
            for (DetectedLanguage result : results) {
                String langCode = result.getLocale().getLanguage();
                String scriptCode = result.getLocale().getScript().orNull();
                WrittenLanguage written = WrittenLanguage.valueOf(langCode, scriptCode);
                written = written.withPreferredScript();
                TextInfo textInfo = new TextInfo();
                textInfo.setLanguage(written.getLanguage());
                textInfo.setScript(written.getScript());
                textInfo.setScore(result.getProbability());
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