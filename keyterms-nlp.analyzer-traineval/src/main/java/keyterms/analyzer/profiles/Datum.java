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

package keyterms.analyzer.profiles;

import keyterms.analyzer.profiles.model.FeatureData;
import keyterms.analyzer.profiles.model.ModelFeature;

/**
 * A training datum for feature based classifiers.
 */
public class Datum<C> {
    /**
     * The output class that the feature data represents.
     */
    private final C groundTruth;

    /**
     * The feature data for the training instance.
     */
    private final FeatureData featureData = new FeatureData();

    /**
     * Constructor.
     *
     * @param outputFeature The output feature representing the ground truth.
     * @param groundTruth The output class that the feature data represents.
     */
    public Datum(ModelFeature<C> outputFeature, C groundTruth) {
        super();
        if (outputFeature == null) {
            throw new NullPointerException("Output feature is required.");
        }
        if (groundTruth == null) {
            throw new NullPointerException("Ground truth is required.");
        }
        if (!outputFeature.test(groundTruth)) {
            throw new IllegalArgumentException("Invalid ground truth value " + outputFeature + " = " + groundTruth);
        }
        this.groundTruth = groundTruth;
    }

    /**
     * Get the ground truth response for the feature data of this training instance.
     *
     * @return The output class that the feature data represents.
     */
    public C getGroundTruth() {
        return groundTruth;
    }

    /**
     * Get the feature data for the training instance.
     *
     * @return The feature data for the training instance.
     */
    public FeatureData getFeatureData() {
        return featureData;
    }

    /**
     * Get the value for the specified feature.
     *
     * @param feature The feature of interest.
     *
     * @return The value for the specified feature.
     */
    public Object getFeatureValue(ModelFeature<?> feature) {
        return featureData.get(feature);
    }

    /**
     * Add a feature value to the datum.
     *
     * @param feature The feature being set.
     * @param value The value of the feature.
     * @param <V> The value class.
     */
    public <V> void setFeature(ModelFeature<V> feature, V value) {
        if (feature == null) {
            throw new NullPointerException("Feature required.");
        }
        if (featureData.contains(feature)) {
            throw new IllegalStateException("Feature value is already specified: " + feature +
                    ": current = " + featureData.get(feature) + ": new = " + value);
        }
        if (!feature.test(value)) {
            throw new IllegalArgumentException("Invalid feature value: " + feature + " = " + value);
        }
        featureData.set(feature, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + groundTruth + " <== " + featureData + "]";
    }
}