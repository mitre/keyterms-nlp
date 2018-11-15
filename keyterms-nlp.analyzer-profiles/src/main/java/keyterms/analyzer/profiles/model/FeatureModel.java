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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The base abstraction for a feature based model used in classifiers.
 *
 * <p> This model may act as a parser and formatter for the output feature. </p>
 */
public class FeatureModel<C>
        implements FeatureParser<C>, FeatureFormatter<C>, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -389555577454000737L;

    /**
     * The input features for the model.
     */
    private final LinkedHashSet<ModelFeature<?>> inputFeatures = new LinkedHashSet<>();

    /**
     * The output feature for the model.
     */
    private final ModelFeature<C> outputFeature;

    /**
     * Constructor.
     *
     * @param outputFeature The output feature for the model.
     */
    public FeatureModel(ModelFeature<C> outputFeature) {
        super();
        Set<ModelFeature<?>> features = new LinkedHashSet<>();
        if (outputFeature == null) {
            throw new NullPointerException("Output feature is required.");
        }
        this.outputFeature = outputFeature;
    }

    /**
     * Get the input features for the model.
     *
     * @return The input features for the model.
     */
    public List<ModelFeature<?>> getInputFeatures() {
        return new ArrayList<>(inputFeatures);
    }

    /**
     * Get the specified input feature.
     *
     * @param name The feature name.
     *
     * @return The specified input feature.
     */
    public ModelFeature<?> getInputFeature(String name) {
        return inputFeatures.stream()
                .filter((feature) -> feature.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Add an input feature to the model.
     *
     * @param feature The input feature.
     *
     * @return A reference to this model useful in method chaining.
     */
    public FeatureModel<C> addInputFeature(ModelFeature<?> feature) {
        if (feature == null) {
            throw new NullPointerException("Model feature is required.");
        }
        if (inputFeatures.contains(feature)) {
            throw new IllegalArgumentException("Duplicate feature: " + feature);
        }
        inputFeatures.add(feature);
        return this;
    }

    /**
     * Get the output feature for the model.
     *
     * @return The output feature for the model.
     */
    public ModelFeature<C> getOutputFeature() {
        return outputFeature;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C parse(CharSequence text) {
        return outputFeature.parse(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String asText(C object) {
        return outputFeature.asText(object);
    }
}