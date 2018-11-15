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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A collection of feature values produced by an analyzer.
 */
public class Analysis
        implements Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 2064069684198027500L;

    /**
     * The score order comparator for analysis objects.
     */
    public static final Comparator<Analysis> SCORE_ORDER;

    // Initialize the score order comparator for analysis objects.
    static {
        Comparator<Analysis> scoreOrder = Comparator.comparing((f) -> f.getScore().doubleValue());
        scoreOrder = scoreOrder.reversed();
        scoreOrder = Comparator.nullsLast(scoreOrder);
        SCORE_ORDER = scoreOrder;
    }

    /**
     * The feature values.
     */
    private final Map<AnalysisFeature<?>, Object> features = new HashMap<>();

    /**
     * The score associated with the collection of feature values.
     */
    private Number score;

    /**
     * Constructor.
     */
    public Analysis() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param toCopy The analysis to copy.
     */
    public Analysis(Analysis toCopy) {
        super();
        if (toCopy != null) {
            features.putAll(toCopy.features);
            score = toCopy.score;
        }
    }

    /**
     * Determine if the specified feature value has been set.
     *
     * @param feature The analysis feature of interest.
     *
     * @return A flag indicating whether the specified feature value has been set.
     */
    public boolean contains(AnalysisFeature<?> feature) {
        return features.containsKey(feature);
    }

    /**
     * Get the analysis features associated with the input data.
     *
     * @return The analysis features associated with the input data.
     */
    public Set<AnalysisFeature<?>> getFeatures() {
        return features.keySet();
    }

    /**
     * Get the value associated with the specified feature.
     *
     * @param feature The analysis feature of interest.
     * @param <V> The value class.
     *
     * @return The value associated with the specified feature.
     */
    public <V> V get(AnalysisFeature<V> feature) {
        return feature.getValueClass().cast(features.get(feature));
    }

    /**
     * Set the specified feature value.
     *
     * @param feature The analysis feature of interest.
     * @param value The feature value.
     * @param <V> The value class.
     */
    public <V> void set(AnalysisFeature<V> feature, V value) {
        if (value != null) {
            features.put(feature, value);
        } else {
            features.remove(feature);
        }
    }

    /**
     * Remove the specified analysis feature.
     *
     * @param feature The analysis feature.
     */
    public void remove(AnalysisFeature<?> feature) {
        features.remove(feature);
    }

    /**
     * Get the score associated with the analysis.
     *
     * @return The score associated with the analysis.
     */
    public Number getScore() {
        return score;
    }

    /**
     * Set the score associated with the analysis.
     *
     * @param score The score associated with the analysis.
     */
    public void setScore(Number score) {
        this.score = score;
    }
}