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

package keyterms.analyzer;

import java.util.Set;
import java.util.function.Supplier;

import keyterms.util.collect.Unique;

/**
 * A container for information and methods used in creating analyzer instances.
 */
public class AnalyzerFactory
        extends Unique<AnalyzerId> {
    /**
     * The types of input accepted by analyzers created by this factory.
     */
    private final Set<Class<?>> inputClasses;

    /**
     * The analysis features that analyzers created by this factory can produce.
     */
    private final Set<AnalysisFeature<?>> outputFeatures;

    /**
     * A flag indicating whether analyzers created by this factory will produce multiple analyses.
     */
    private final boolean producesRankings;

    /**
     * A flag indicating whether analyzers created by this factory will produce meaningful scores.
     */
    private final boolean producesScores;

    /**
     * A method for creating new analyzers instances.
     */
    private final Supplier<Analyzer> factory;

    /**
     * Constructor.
     *
     * @param key A key identifying analyzers created by this factory.
     * @param inputClasses The types of input accepted by analyzers created by this factory.
     * @param outputFeatures The analysis features that analyzers created by this factory can produce.
     * @param producesRankings A flag indicating whether analyzers created by this factory will produce multiple
     * analyses.
     * @param producesScores A flag indicating whether analyzers created by this factory will produce meaningful scores.
     * @param factory The factory method.
     */
    public AnalyzerFactory(
            AnalyzerId key,
            Set<Class<?>> inputClasses,
            Set<AnalysisFeature<?>> outputFeatures,
            boolean producesRankings,
            boolean producesScores,
            Supplier<Analyzer> factory) {
        super(key);
        if ((inputClasses == null) || (inputClasses.isEmpty())) {
            throw new NullPointerException("Analyzer input classes are required.");
        }
        if ((outputFeatures == null) || (outputFeatures.isEmpty())) {
            throw new NullPointerException("Analyzer output features are required.");
        }
        if (factory == null) {
            throw new NullPointerException("Factory method is required.");
        }
        this.inputClasses = inputClasses;
        this.outputFeatures = outputFeatures;
        this.producesRankings = producesRankings;
        this.producesScores = producesScores;
        this.factory = factory;
    }

    /**
     * Get the types of input accepted by analyzers created by this factory.
     *
     * @return The types of input accepted by analyzers created by this factory.
     */
    public Set<Class<?>> getInputClasses() {
        return inputClasses;
    }

    /**
     * Get the analysis features that analyzers created by this factory can produce.
     *
     * @return The analysis features that analyzers created by this factory can produce.
     */
    public Set<AnalysisFeature<?>> getOutputFeatures() {
        return outputFeatures;
    }

    /**
     * Determine if analyzers created by this factory will produce multiple analyses.
     *
     * @return A flag indicating whether analyzers created by this factory will produce multiple analyses.
     */
    public boolean producesRankings() {
        return producesRankings;
    }

    /**
     * Determine if analyzers created by this factory will produce meaningful scores.
     *
     * @return A flag indicating whether analyzers created by this factory will produce meaningful scores.
     */
    public boolean producesScores() {
        return producesScores;
    }

    /**
     * Create a new analyzer.
     *
     * @return The new analyzer.
     */
    public Analyzer newInstance() {
        return factory.get();
    }
}