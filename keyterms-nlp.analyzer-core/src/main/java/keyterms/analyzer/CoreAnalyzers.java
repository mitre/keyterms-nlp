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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.lang.Lazy;

/**
 * A shared collection of analyzer thread pools.
 */
public class CoreAnalyzers {
    /**
     * The lazily instantiated singleton instance.
     */
    private static final Lazy<CoreAnalyzers> INSTANCE = new Lazy<>(CoreAnalyzers::new);

    /**
     * Get the singleton instance of this class.
     *
     * @return The singleton instance of this class.
     */
    public static CoreAnalyzers getInstance() {
        return INSTANCE.value();
    }

    /**
     * The analyzer thread pools keyed by the factory identifier.
     */
    private final Map<AnalyzerId, AnalyzerPool> analyzerPools = new HashMap<>();

    /**
     * Constructor.
     */
    private CoreAnalyzers() {
        super();
        ServiceLoader<AnalyzerPlugin> pluginLoader = ServiceLoader.load(AnalyzerPlugin.class);
        for (AnalyzerPlugin plugin : pluginLoader) {
            getLogger().info("Loading analyzer plugins from {}", plugin.getClass().getName());
            plugin.getAnalyzerFactories().forEach((factory) -> {
                if (analyzerPools.containsKey(factory.getId())) {
                    getLogger().error("Duplicate analyzer identifier: " + factory.getId());
                } else {
                    try {
                        analyzerPools.put(factory.getId(), new AnalyzerPool(factory));
                    } catch (Exception error) {
                        getLogger().error("Could not instantiate {} analyzers.", factory.getId(), error);
                    }
                }
            });
        }
        if (analyzerPools.isEmpty()) {
            getLogger().warn("No core analyzers loaded.");
        }
    }

    /**
     * Get the logging topic for this object.
     *
     * @return The logging topic for this object.
     */
    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Get the number of analyzer thread pools that have been loaded.
     *
     * @return The number of analyzer thread pools that have been loaded.
     */
    public int size() {
        return analyzerPools.size();
    }

    /**
     * Get the identifiers for the analyzers that have been loaded.
     *
     * @return The identifiers for the analyzers that have been loaded.
     */
    public Set<AnalyzerId> ids() {
        return analyzerPools.keySet();
    }

    /**
     * Get the specified analyzer thread pool.
     *
     * @param id The identifier for the analyzer.
     *
     * @return The specified analyzer thread pool.
     */
    public Analyzer get(AnalyzerId id) {
        return analyzerPools.get(id);
    }

    /**
     * Get the analyzer entries which pass the specified filters.
     *
     * @param idFilter A filter used to remove specific products from execution.
     * @param analyzerFilter A filter used to screen out unwanted analyzers.
     *
     * @return The analyzer entries which pass the specified filters.
     */
    public Map<AnalyzerId, Analyzer> get(Predicate<AnalyzerId> idFilter, Predicate<Analyzer> analyzerFilter) {
        return analyzerPools.entrySet().stream()
                .filter((entry) -> ((idFilter == null) || (idFilter.test(entry.getKey()))))
                .filter((entry) -> ((analyzerFilter == null) || (analyzerFilter.test(entry.getValue()))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Run all analyzers which can accept the specified input and match the specified filters.
     *
     * @param input The input.
     * @param idFilter A filter used to remove specific products from execution.
     * @param analyzerFilter A filter used to screen out unwanted analyzers.
     *
     * @return The analysis results from all matching analyzers.
     */
    public Map<AnalyzerId, List<Analysis>> run(Object input,
            Predicate<AnalyzerId> idFilter,
            Predicate<Analyzer> analyzerFilter) {
        Map<AnalyzerId, List<Analysis>> results = new HashMap<>();
        if (input != null) {
            get(idFilter, analyzerFilter).entrySet().parallelStream().forEach((entry) -> {
                AnalyzerId id = entry.getKey();
                Analyzer analyzer = entry.getValue();
                if (analyzer.accepts(input.getClass())) {
                    List<Analysis> analysis = analyzer.analyze(input);
                    if (!analysis.isEmpty()) {
                        results.put(id, analysis);
                    }
                }
            });
        }
        return results;
    }

    /**
     * Dispose of the analyzer pool resources.
     */
    public void dispose() {
        analyzerPools.values().forEach(Analyzer::dispose);
    }
}