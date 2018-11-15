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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.profiles.model.FeatureData;
import keyterms.analyzer.profiles.model.FeatureModel;
import keyterms.analyzer.profiles.model.ModelFeature;
import keyterms.analyzer.profiles.model.NominalFeature;
import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;

import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 * A WEKA based random forest classifier.
 */
public class WekaForest<C>
        extends Analyzer {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 3124693602323023621L;

    /**
     * For unknown nominal feature values (such as {@code null}), this is the value represented in the model.
     *
     * <p> The WEKA random forest implementation cannot properly deal with sparse model instances during training. </p>
     */
    public static final String UNKNOWN_NOMINAL = "UNK";

    /**
     * For unknown numeric feature values (such as {@code null}), this is the value represented in the model.
     *
     * <p> The WEKA random forest implementation cannot properly deal with sparse model instances during training. </p>
     *
     * <p> The value {@code -1} is used since all numeric features in our models are zero or positive values. </p>
     */
    public static final double UNKNOWN_NUMERIC = -1;

    /**
     * The types of input accepted by the analyzer.
     */
    static final Set<Class<?>> INPUT_CLASSES = Bags.staticSet(
            FeatureData.class
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
     * The feature model for the classifier.
     */
    private final FeatureModel<C> featureModel;

    /**
     * The WEKA attribute model.
     */
    private final Instances wekaModel;

    /**
     * The random forest classifier based on the model.
     */
    private final RandomForest classifier;

    /**
     * The output feature for the analyzer.
     */
    private final AnalysisFeature<C> outputFeature;

    /**
     * Constructor.
     *
     * <p> This protected constructor exists only to aid in serialization. </p>
     */
    protected WekaForest() {
        super();
        this.featureModel = null;
        this.wekaModel = null;
        this.classifier = null;
        this.outputFeature = null;
    }

    /**
     * Constructor.
     *
     * @param featureModel The feature model.
     * @param wekaModel The weka model.
     * @param classifier The random forest classifier trained on the models.
     * @param outputFeature The analysis feature produced by the model.
     */
    public WekaForest(FeatureModel<C> featureModel, Instances wekaModel,
            RandomForest classifier, AnalysisFeature<C> outputFeature) {
        super(INPUT_CLASSES, Bags.staticSet(outputFeature), PRODUCES_RANKINGS, PRODUCES_SCORES);
        if (featureModel == null) {
            throw new NullPointerException("Feature model is required.");
        }
        if (wekaModel == null) {
            throw new NullPointerException("Attribute model is required.");
        }
        if (classifier == null) {
            throw new NullPointerException("Classifier instance is required.");
        }
        this.featureModel = featureModel;
        featureModel.getInputFeatures().stream()
                .filter((feature) -> feature instanceof NominalFeature)
                .map(NominalFeature.class::cast)
                .forEach(NominalFeature::close);
        if (featureModel.getOutputFeature() instanceof NominalFeature) {
            ((NominalFeature)featureModel.getOutputFeature()).close();
        }
        this.wekaModel = wekaModel;
        this.classifier = classifier;
        this.outputFeature = outputFeature;
    }

    /**
     * Get the feature model for the classifier.
     *
     * @return The feature model for the classifier.
     */
    public FeatureModel<C> getFeatureModel() {
        return featureModel;
    }

    /**
     * Get the WEKA attribute model.
     *
     * @return The WEKA attribute model.
     */
    public Instances getWekaModel() {
        return wekaModel;
    }

    /**
     * Get the output feature for the analyzer.
     *
     * @return The output feature for the analyzer.
     */
    public AnalysisFeature<C> getOutputFeature() {
        return outputFeature;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void _analyze(Object input, Consumer<Analysis> collector) {
        FeatureData inputData = (FeatureData)input;
        Instance toClassify = new SparseInstance(wekaModel.numAttributes());
        toClassify.setDataset(wekaModel);
        try {
            inputData.getFeatures().forEach((feature, value) -> {
                Attribute attribute = wekaModel.attribute(feature.name());
                if (attribute != null) {
                    if (attribute.isNumeric()) {
                        toClassify.setValue(attribute, ((Number)value).doubleValue());
                    } else {
                        String textValue = null;
                        int valueIndex = -1;
                        if (value != null) {
                            ModelFeature<Object> omf = (ModelFeature<Object>)feature;
                            textValue = omf.asText(value);
                            valueIndex = attribute.indexOfValue(textValue);
                        }
                        if (valueIndex != -1) {
                            try {
                                toClassify.setValue(attribute, textValue);
                            } catch (Exception illegal) {
                                getLogger().debug("Bad value for {} = {}", attribute.name(), value);
                            }
                        } else {
                            getLogger().debug("Novel value for {} = {}", attribute.name(), value);
                        }
                    }
                } else {
                    getLogger().warn("Unknown model feature: {}", feature);
                }
            });
            double[] distribution = classifier.distributionForInstance(toClassify);
            List<Keyed<Integer, Double>> sorted = new ArrayList<>();
            for (int d = 0; d < distribution.length; d++) {
                if (distribution[d] > 0) {
                    sorted.add(new Keyed<>(d, distribution[d]));
                }
            }
            Comparator<Keyed<Integer, Double>> comparator = Comparator.comparing(Keyed::getValue);
            comparator = comparator.reversed();
            sorted.sort(comparator);
            for (int k = 0; k < Math.min(5, sorted.size()); k++) {
                Keyed<Integer, Double> prediction = sorted.get(k);
                Object value = featureModel.getOutputFeature().parse(
                        wekaModel.classAttribute().value(prediction.getKey()));
                Analysis analysis = new Analysis();
                analysis.set(outputFeature, outputFeature.cast(value));
                analysis.setScore(prediction.getValue());
                collector.accept(analysis);
            }
        } catch (Exception error) {
            getLogger().error("Error classifying input data: {}", input, error);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _dispose() {
        // Intentional NoOp
    }

    /**
     * Lightly override the default serialization write to force initialization of the lazy value.
     *
     * @param stream The object output stream.
     *
     * @throws IOException for input/output errors
     */
    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        // Ensure that the value has been initialized so that the supplier may be safely left null
        // during the default de-serialization operation.
        wekaModel.clear();
        stream.defaultWriteObject();
    }

    /**
     * Ensure that the loaded instance model is empty of training data.
     *
     * @return The resolved object.
     */
    private Object readResolve() {
        wekaModel.clear();
        return this;
    }
}