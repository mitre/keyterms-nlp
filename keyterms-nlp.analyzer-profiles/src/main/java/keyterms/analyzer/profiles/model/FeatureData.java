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

package keyterms.analyzer.profiles.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A set of feature value pairs.
 */
public class FeatureData {
    /**
     * The data features.
     */
    private final Map<ModelFeature<?>, Object> features = new HashMap<>();

    /**
     * Constructor.
     */
    public FeatureData() {
        super();
    }

    /**
     * Get the data features.
     *
     * @return The data features.
     */
    public Map<ModelFeature<?>, Object> getFeatures() {
        return features;
    }

    /**
     * Determine if the specified feature is present (has a non-{@code null} value).
     *
     * @param feature The feature of interest.
     *
     * @return A flag indicating whether the specified feature is present.
     */
    public boolean contains(ModelFeature<?> feature) {
        return features.containsKey(feature);
    }

    /**
     * Get the value associated with the specified feature.
     *
     * @param feature The feature.
     * @param <F> The feature value class.
     *
     * @return The value associated with the specified feature.
     */
    public <F> F get(ModelFeature<F> feature) {
        return feature.cast(features.get(feature));
    }

    /**
     * Set the value associated with the specified feature.
     *
     * <p> Note: Values of {@code null} will remove the feature. </p>
     *
     * @param feature The feature.
     * @param value The feature value.
     * @param <F> The feature value class.
     */
    public <F> void set(ModelFeature<F> feature, F value) {
        if (value != null) {
            features.put(feature, value);
        } else {
            features.remove(feature);
        }
    }

    /**
     * Perform the specified action on each entry set value.
     *
     * @param action The action taken for each feature/value entry.
     */
    public void forEach(BiConsumer<? super ModelFeature<?>, ? super Object> action) {
        features.forEach(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + features.size() + " features]";
    }
}