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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.profiles.model.EnumeratedFeature;
import keyterms.analyzer.profiles.model.FeatureModel;
import keyterms.analyzer.profiles.model.ModelFeature;
import keyterms.analyzer.profiles.model.NumericFeature;
import keyterms.util.math.Statistics;
import keyterms.util.time.Timing;

import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A trainer for WekaForest instances.
 */
public class WekaForestBuilder<C> {
    /**
     * The feature model for the classifier.
     */
    private final FeatureModel<C> featureModel;

    /**
     * The output feature for the analyzer.
     */
    private final AnalysisFeature<C> outputFeature;

    /**
     * The training data.
     */
    private final List<Datum<C>> trainingData = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param featureModel The feature model for the classifier.
     * @param outputFeature The output feature.
     */
    public WekaForestBuilder(FeatureModel<C> featureModel, AnalysisFeature<C> outputFeature) {
        super();
        if (featureModel == null) {
            throw new NullPointerException("Feature model is required.");
        }
        if (outputFeature == null) {
            throw new NullPointerException("Analyzer output feature is required.");
        }
        this.featureModel = featureModel;
        this.outputFeature = outputFeature;
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
     * Add a record to the training data set.
     *
     * @param datum The training data record.
     */
    public void addTrainingData(Datum<C> datum) {
        if (datum != null) {
            trainingData.add(datum);
        }
    }

    /**
     * Build a working classifier from the current training data.
     *
     * @return The classifier.
     *
     * @throws Exception any exception
     */
    public WekaForest<C> build()
            throws Exception {
        getLogger().info("Creating WEKA data model.");
        // Feature model conversion.
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (ModelFeature<?> inputFeature : featureModel.getInputFeatures()) {
            attributes.add(getAttribute(inputFeature));
        }
        Attribute outputAttribute = getAttribute(featureModel.getOutputFeature());
        attributes.add(outputAttribute);
        // Instance data conversion.
        Instances dataSet = new Instances("training", attributes, 10);
        dataSet.setClassIndex(attributes.size() - 1);
        Statistics stats = new Statistics();
        trainingData.forEach((datum) -> {
            Timing timing = new Timing();
            Instance trainingInstance = new DenseInstance(attributes.size());
            trainingInstance.setDataset(dataSet);
            datum.getFeatureData().forEach((feature, value) -> {
                Attribute attribute = dataSet.attribute(feature.name());
                setAttributeValue(trainingInstance, feature, attribute, value);
            });
            setAttributeValue(trainingInstance, featureModel.getOutputFeature(),
                    outputAttribute, datum.getGroundTruth());
            for (int a = 0; a < trainingInstance.numAttributes(); a++) {
                Attribute attribute = trainingInstance.attribute(a);
                if (trainingInstance.isMissing(a)) {
                    setAttributeValue(trainingInstance, featureModel.getInputFeature(attribute.name()),
                            attribute, null);
                }
            }
            dataSet.add(trainingInstance);
            stats.add(timing.finish().getDuration().as(TimeUnit.MILLISECONDS));
        });
        getLogger().info("Per record transformation times: {}", stats);
        // Classifier training.
        getLogger().info("Training random forest from {} training records.", trainingData.size());
        RandomForest forest = new RandomForest();
        Timing timing = new Timing();
        forest.buildClassifier(dataSet);
        timing.finish();
        WekaForest<C> classifier = new WekaForest<>(featureModel, dataSet, forest, outputFeature);
        getLogger().info("Created WEKA random forest classifier in {}.", timing.summary(2));
        return classifier;
    }

    /**
     * Get the WEKA attribute equivalent to the specified feature.
     *
     * @param feature The feature.
     *
     * @return The WEKA attribute equivalent to the specified feature.
     */
    @SuppressWarnings("unchecked")
    private Attribute getAttribute(ModelFeature<?> feature) {
        Attribute attribute;
        if (feature instanceof NumericFeature) {
            attribute = new Attribute(feature.name(), false);
        } else {
            EnumeratedFeature<Object> eFeature = (EnumeratedFeature<Object>)feature;
            ArrayList<String> values = eFeature.getValues().stream()
                    .map(eFeature::asText)
                    .collect(Collectors.toCollection(ArrayList::new));
            values.add(0, WekaForest.UNKNOWN_NOMINAL);
            attribute = new Attribute(feature.name(), values);
        }
        return attribute;
    }

    /**
     * Set the instance attribute value as specified.
     *
     * @param instance The training instance.
     * @param feature The feature associated with the training attribute.
     * @param attribute The attribute being set.
     * @param value The attribute value.
     */
    @SuppressWarnings("unchecked")
    private void setAttributeValue(Instance instance, ModelFeature feature, Attribute attribute, Object value) {
        if (feature instanceof NumericFeature) {
            if (value != null) {
                instance.setValue(attribute, ((Number)value).doubleValue());
            } else {
                instance.setValue(attribute, WekaForest.UNKNOWN_NUMERIC);
            }
        } else {
            if (value != null) {
                instance.setValue(attribute, feature.asText(value));
            } else {
                instance.setValue(attribute, WekaForest.UNKNOWN_NOMINAL);
            }
        }
    }
}