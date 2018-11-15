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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic function which produces an analysis of input data.
 */
public abstract class Analyzer
        implements Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -1701413154913484955L;

    /**
     * A synchronization lock used to prevent analyzer resource from being disposed during use.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The types of input accepted by the analyzer.
     */
    private final Set<Class<?>> inputClasses;

    /**
     * The analysis features that the analyzer can produce.
     */
    private final Set<AnalysisFeature<?>> outputFeatures;

    /**
     * A flag indicating whether the analyzer produces multiple analyses.
     */
    private final boolean producesRankings;

    /**
     * A flag indicating whether the analyzer will produce meaningful scores.
     */
    private final boolean producesScores;

    /**
     * A flag indicating whether the analyzer is available.
     *
     * <p> The analyzer is assumed to be available once constructed and until {@code dispose()} is invoked. </p>
     */
    private boolean available = true;

    /**
     * Constructor.
     *
     * <p> This protected constructor exists only to aid in serialization. </p>
     */
    protected Analyzer() {
        super();
        this.inputClasses = null;
        this.outputFeatures = null;
        this.producesRankings = false;
        this.producesScores = false;
    }

    /**
     * Constructor.
     *
     * @param inputClasses The types of input accepted by the analyzer.
     * @param outputFeatures The analysis features that the analyzer can produce.
     * @param producesRankings A flag indicating whether the analyzer produces multiple analyses.
     * @param producesScores A flag indicating whether the analyzer will produce meaningful scores.
     */
    public Analyzer(Set<Class<?>> inputClasses, Set<AnalysisFeature<?>> outputFeatures,
            boolean producesRankings, boolean producesScores) {
        super();
        if (inputClasses == null) {
            throw new NullPointerException("Analyzer input classes are required.");
        }
        if (outputFeatures == null) {
            throw new NullPointerException("Analyzer output features are required.");
        }
        // New HashSet's are used to avoid non-serializable set implementations.
        this.inputClasses = inputClasses.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        this.outputFeatures = outputFeatures.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        if (this.inputClasses.isEmpty()) {
            throw new IllegalArgumentException("Analyzer input classes are required.");
        }
        if (this.outputFeatures.isEmpty()) {
            throw new IllegalArgumentException("Analyzer output features are required.");
        }
        this.producesRankings = producesRankings;
        this.producesScores = producesScores;
    }

    /**
     * Get the logging topic for this object.
     *
     * @return The logging topic for this object.
     */
    protected Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Determine if the analyzer is available.
     *
     * @return A flag indicating whether the analyzer is available.
     */
    public boolean isAvailable() {
        lock.readLock().lock();
        try {
            return available;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the types of input accepted by the analyzer.
     *
     * @return The types of input accepted by the analyzer.
     */
    public final Set<Class<?>> getInputClasses() {
        return inputClasses;
    }

    /**
     * Determine if the analyzer will process the specified type of input.
     *
     * @param inputClass The type of input.
     *
     * @return A flag indicating whether the analyzer will process the specified type of input.
     */
    public final boolean accepts(Class<?> inputClass) {
        boolean accepts = false;
        if (inputClass != null) {
            accepts = inputClasses.stream()
                    .anyMatch((c) -> c.isAssignableFrom(inputClass));
        }
        return accepts;
    }

    /**
     * Get the analysis features that the analyzer can produce.
     *
     * @return The analysis features that the analyzer can produce.
     */
    public final Set<AnalysisFeature<?>> getOutputFeatures() {
        return outputFeatures;
    }

    /**
     * Determine if the analyzer can produce the specified analysis feature.
     *
     * @param feature The analysis feature of interest.
     *
     * @return A flag indicating whether the analyzer can produce the specified analysis feature.
     */
    public final boolean produces(AnalysisFeature<?> feature) {
        return outputFeatures.contains(feature);
    }

    /**
     * Determine if the analyzer produces multiple analyses.
     *
     * @return A flag indicating whether the analyzer produces multiple analyses.
     */
    public final boolean producesRankings() {
        return producesRankings;
    }

    /**
     * Determine if the analyzer will produce meaningful scores.
     *
     * @return A flag indicating whether the analyzer will produce meaningful scores.
     */
    public final boolean producesScores() {
        return producesScores;
    }

    /**
     * Analyze the specified input.
     *
     * @param input The input data.
     *
     * @return The results of the analysis.
     */
    public final List<Analysis> analyze(Object input) {
        lock.readLock().lock();
        try {
            if (!available) {
                throw new IllegalStateException("Analyzer is not available.");
            }
            List<Analysis> results = new ArrayList<>();
            if (input != null) {
                if (accepts(input.getClass())) {
                    try {
                        _analyze(input, (analysis) -> {
                            if ((analysis != null) && (analysis.getFeatures().size() > 0)) {
                                results.add(analysis);
                            }
                        });
                        if (producesScores) {
                            results.sort(Analysis.SCORE_ORDER);
                        }
                    } catch (Exception error) {
                        getLogger().error("Error analyzing input: {}", input, error);
                    }
                }
            }
            return results;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Analyze the specified input, notifying the collector as analysis results are produced.
     *
     * @param input The input.
     * @param collector The collector of analysis results.
     */
    protected abstract void _analyze(Object input, Consumer<Analysis> collector);

    /**
     * Dispose of any resources associated with the analyzer.
     */
    public final void dispose() {
        lock.writeLock().lock();
        try {
            if (available) {
                available = false;
                _dispose();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Dispose of any resources associated with the analyzer.
     */
    protected abstract void _dispose();
}