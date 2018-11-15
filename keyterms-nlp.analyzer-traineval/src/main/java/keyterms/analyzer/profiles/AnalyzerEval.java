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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.ToDoubleFunction;

/**
 * The results from a classifier evaluation over a test data set.
 */
public class AnalyzerEval {
    /**
     * A synchronization lock for the data collection phase.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * The number of tests.
     */
    private long tests;

    /**
     * The number of correct responses.
     */
    private long correct;

    /**
     * The counts of the ground truth values in the test data.
     */
    private final Map<Object, Long> truthCounts = new HashMap<>();

    /**
     * The matrix of expected to actual values.
     *
     * <p> This is a map of expected (ground truth values) keyed to a map of counts of classification results for the
     * associated tests. </p>
     */
    private final Map<Object, Map<Object, Long>> confusionMatrix = new HashMap<>();

    /**
     * Per ground truth value classifier statistics.
     */
    private final Map<Object, AnalyzerStats> truthStats = new HashMap<>();

    /**
     * Overall classifier statistics taken from the mean of the per truth statistics.
     */
    private final AnalyzerStats overallStats = new AnalyzerStats();

    /**
     * Constructor.
     */
    public AnalyzerEval() {
        super();
    }

    /**
     * Update the truth counts and confusion matrix which is the foundation for the classifier statistics.
     *
     * <p> This should be called for each evaluated test. </p>
     *
     * @param truth The expected value.
     * @param actual The classified value.
     */
    public void addTestResult(Object truth, Object actual) {
        lock.lock();
        try {
            tests++;
            if (Objects.equals(truth, actual)) {
                correct++;
            }
            truthCounts.compute(truth, (k, v) -> (v != null) ? v + 1 : 1);
            confusionMatrix.computeIfAbsent(truth, (k) -> new HashMap<>())
                    .compute(actual, (k, v) -> (v != null) ? v + 1 : 1);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Compute the classifier statistics.
     *
     * <p> This method should be called only after all tests have been evaluated and recorded. </p>
     */
    public void computeStats() {
        lock.lock();
        try {
            for (Object truth : truthCounts.keySet()) {
                AnalyzerStats classifierStats = new AnalyzerStats();
                truthStats.put(truth, classifierStats);
                double truePositive = 0;
                double trueNegative = 0;
                double falsePositive = 0;
                double falseNegative = 0;
                for (Object classValue : confusionMatrix.keySet()) {
                    Map<Object, Long> actualCounts = confusionMatrix.get(classValue);
                    if (truth.equals(classValue)) {
                        for (Object actualValue : actualCounts.keySet()) {
                            if (truth.equals(actualValue)) {
                                truePositive += actualCounts.get(actualValue);
                            } else {
                                falseNegative += actualCounts.get(actualValue);
                            }
                        }
                    } else {
                        for (Object actualValue : actualCounts.keySet()) {
                            if (truth.equals(actualValue)) {
                                falsePositive += actualCounts.get(actualValue);
                            } else {
                                trueNegative += actualCounts.get(actualValue);
                            }
                        }
                    }
                }
                classifierStats.setTruePositive(truePositive);
                classifierStats.setTrueNegative(trueNegative);
                classifierStats.setFalsePositive(falsePositive);
                classifierStats.setFalseNegative(falseNegative);
            }
            overallStats.setTruePositive(average(AnalyzerStats::getTruePositive));
            overallStats.setTrueNegative(average(AnalyzerStats::getTrueNegative));
            overallStats.setFalsePositive(average(AnalyzerStats::getFalsePositive));
            overallStats.setFalseNegative(average(AnalyzerStats::getFalseNegative));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Average the specified field from the per truth value classifier statistics.
     *
     * @param toDoubleFunction The function which retrieves the desired field.
     *
     * @return The specified average.
     */
    private double average(ToDoubleFunction<? super AnalyzerStats> toDoubleFunction) {
        return truthStats.values().stream().mapToDouble(toDoubleFunction).sum() / truthStats.size();
    }

    /**
     * Get the ground truth values from the evaluation.
     *
     * @return The ground truth values from the evaluation.
     */
    public Set<Object> getTruthValues() {
        return truthCounts.keySet();
    }

    /**
     * Get the class values encountered during the testing.
     *
     * @return The class values encountered during the testing.
     */
    public Set<Object> getClassValues() {
        Set<Object> classValues = new HashSet<>(truthCounts.keySet());
        for (Map<Object, Long> actualCounts : confusionMatrix.values()) {
            classValues.addAll(actualCounts.keySet());
        }
        return classValues;
    }

    /**
     * Get the number of tests.
     *
     * @return The number of tests.
     */
    public long getTests() {
        return tests;
    }

    /**
     * Get the count of ground truth values in the tests for the specified class value.
     *
     * @param classValue The class value.
     *
     * @return The number of times the specified class value was the ground truth value in a test.
     */
    public long getTests(Object classValue) {
        return truthCounts.getOrDefault(classValue, 0L);
    }

    /**
     * Get the count of correct responses.
     *
     * @return The count of correct responses.
     */
    public long getCorrect() {
        return correct;
    }

    /**
     * Get the count of correct responses in the tests for the specified class value.
     *
     * @param classValue The class value.
     *
     * @return The count of correct responses for the specified class value.
     */
    public long getCorrect(Object classValue) {
        return getConfusionCount(classValue, classValue);
    }

    /**
     * Get the percentage of correct responses.
     *
     * @return The percentage of correct responses.
     */
    public double getPercentCorrect() {
        double correct = this.correct;
        double tests = this.tests;
        return (tests > 0) ? correct / tests : 0.0;
    }

    /**
     * Get the percentage of correct responses for the specified class value.
     *
     * @param classValue The class value of interest.
     *
     * @return The percentage of correct responses for the specified class value.
     */
    public double getPercentCorrect(Object classValue) {
        double correct = getCorrect(classValue);
        double tests = getTests(classValue);
        return (tests > 0) ? correct / tests : 0.0;
    }

    /**
     * Get the number of times the specified truth-actual combination was encountered in the evaluation.
     *
     * @param truth The ground truth value.
     * @param actual The classified value.
     *
     * @return The number of times the specified truth-actual combination was encountered in the evaluation.
     */
    public long getConfusionCount(Object truth, Object actual) {
        return confusionMatrix.getOrDefault(truth, Collections.emptyMap()).getOrDefault(actual, 0L);
    }

    /**
     * Get the specified statistic for the classifier.
     *
     * @param statFunction The function which produces the correct aspect of the statistics.
     *
     * @return The specified statistic for the classifier.
     */
    public double getStatistic(ToDoubleFunction<? super AnalyzerStats> statFunction) {
        if (statFunction == null) {
            throw new NullPointerException("Stat function required.");
        }
        return statFunction.applyAsDouble(overallStats);
    }

    /**
     * Get the specified statistic for the classifier for the specified class value.
     *
     * <p> If the specified class value is {@code null}, the overall statistic will be returned. </p>
     *
     * @param classValue The class value.
     * @param statFunction The function which produces the correct aspect of the statistics.
     *
     * @return The specified statistic for the classifier for the specified class value.
     */
    public double getStatistic(Object classValue, ToDoubleFunction<? super AnalyzerStats> statFunction) {
        if (statFunction == null) {
            throw new NullPointerException("Stat function required.");
        }
        AnalyzerStats classStats = (classValue != null) ?
                truthStats.getOrDefault(classValue, new AnalyzerStats()) : overallStats;
        return statFunction.applyAsDouble(classStats);
    }
}