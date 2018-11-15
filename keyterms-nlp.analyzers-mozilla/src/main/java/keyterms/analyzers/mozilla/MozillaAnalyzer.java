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

package keyterms.analyzers.mozilla;

import java.util.Set;
import java.util.function.Consumer;

import org.mozilla.universalchardet.UniversalDetector;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.text.TextInfo;
import keyterms.util.collect.Bags;
import keyterms.util.io.Encoding;

/**
 * A character encoding detector for the Java port of the Mozilla universal character detection library.
 */
public class MozillaAnalyzer
        extends Analyzer {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -9100098007758344999L;

    /**
     * The types of input accepted by the analyzer.
     */
    static final Set<Class<?>> INPUT_CLASSES = Bags.staticSet(
            byte[].class
    );

    /**
     * The analysis features that the analyzer can produce.
     */
    static final Set<AnalysisFeature<?>> OUTPUT_FEATURES = Bags.staticSet(
            TextInfo.SIZE, TextInfo.ENCODING, TextInfo.LENGTH
    );

    /**
     * A flag indicating whether the analyzer produces multiple analyses.
     */
    static final boolean PRODUCES_RANKINGS = false;

    /**
     * A flag indicating whether the analyzer produces meaningful scores.
     */
    static final boolean PRODUCES_SCORES = false;

    /**
     * The underlying character encoding detector.
     */
    private final UniversalDetector detector = new UniversalDetector(null);

    /**
     * Constructor.
     */
    public MozillaAnalyzer() {
        super(INPUT_CLASSES, OUTPUT_FEATURES, PRODUCES_RANKINGS, PRODUCES_SCORES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _analyze(Object input, Consumer<Analysis> collector) {
        byte[] inputData = (byte[])input;
        if (inputData.length > 0) {
            try {
                detector.reset();
                detector.handleData(inputData, 0, inputData.length);
                detector.dataEnd();
                TextInfo textInfo = new TextInfo();
                textInfo.setSize(inputData.length);
                textInfo.setEncoding(detector.getDetectedCharset());
                try {
                    String decoded = Encoding.decode(inputData,
                            Encoding.getCharset(textInfo.getEncoding()));
                    textInfo.setLength(decoded.length());
                } catch (Exception error) {
                    textInfo.setLength(-1);
                }
                collector.accept(textInfo);
            } finally {
                detector.reset();
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