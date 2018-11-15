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

import java.util.function.ToDoubleFunction;

/**
 * Statistics relevant to evaluating classifier performance.
 *
 * <p> Formulas and terminology are from <a href="https://en.wikipedia.org/wiki/Confusion_matrix">Wikipedia</a>. </p>
 */
public class AnalyzerStats {

    public static ToDoubleFunction<AnalyzerStats> TRUE_POSITIVE = AnalyzerStats::getTruePositive;
    public static ToDoubleFunction<AnalyzerStats> TRUE_NEGATIVE = AnalyzerStats::getTrueNegative;
    public static ToDoubleFunction<AnalyzerStats> FALSE_POSITIVE = AnalyzerStats::getFalsePositive;
    public static ToDoubleFunction<AnalyzerStats> FALSE_NEGATIVE = AnalyzerStats::getFalseNegative;

    public static ToDoubleFunction<AnalyzerStats> PRECISION = AnalyzerStats::getPrecision;
    public static ToDoubleFunction<AnalyzerStats> NPV = AnalyzerStats::getNegativePredictiveValue;
    public static ToDoubleFunction<AnalyzerStats> RECALL = AnalyzerStats::getRecall;
    public static ToDoubleFunction<AnalyzerStats> FALL_OUT = AnalyzerStats::getFallOut;
    public static ToDoubleFunction<AnalyzerStats> F1_SCORE = AnalyzerStats::getF1Score;

    /**
     * The true positive count.
     */
    private double truePositive;

    /**
     * The true negative count.
     */
    private double trueNegative;

    /**
     * The false positive count.
     */
    private double falsePositive;

    /**
     * Teh false negative count.
     */
    private double falseNegative;

    /**
     * Constructor.
     */
    AnalyzerStats() {
        super();
    }

    /**
     * Get the true positive count.
     *
     * <p> TP </p>
     *
     * @return The true positive count.
     */
    public double getTruePositive() {
        return truePositive;
    }

    /**
     * Set the true positive count.
     *
     * @param truePositive The true positive count.
     */
    void setTruePositive(double truePositive) {
        this.truePositive = truePositive;
    }

    /**
     * Get the true negative count.
     *
     * <p> TN </p>
     *
     * @return The true negative count.
     */
    public double getTrueNegative() {
        return trueNegative;
    }

    /**
     * Set the true negative count.
     *
     * @param trueNegative The true negative count.
     */
    void setTrueNegative(double trueNegative) {
        this.trueNegative = trueNegative;
    }

    /**
     * Get the false positive count.
     *
     * <p> FP </p>
     *
     * @return The false positive count.
     */
    public double getFalsePositive() {
        return falsePositive;
    }

    /**
     * Set the false positive count.
     *
     * @param falsePositive The false positive count.
     */
    void setFalsePositive(double falsePositive) {
        this.falsePositive = falsePositive;
    }

    /**
     * Get the false negative count.
     *
     * <p> FN </p>
     *
     * @return The false negative count.
     */
    public double getFalseNegative() {
        return falseNegative;
    }

    /**
     * Set the false negative count.
     *
     * @param falseNegative The false negative count.
     */
    void setFalseNegative(double falseNegative) {
        this.falseNegative = falseNegative;
    }

    /**
     * Get the positive predictive value (precision) of the classifier.
     *
     * <p> PPV </p>
     *
     * @return The positive predictive value of the classifier.
     */
    public double getPrecision() {
        double div = truePositive + falsePositive;
        return (div != 0) ? (truePositive / div) : 0;
    }

    /**
     * Get the true positive rate of the classifier.
     *
     * <p> Aka: hit rate, sensitivity, recall </p>
     *
     * <p> TPR </p>
     *
     * @return The true positive rate of the classifier.
     */
    public double getRecall() {
        double div = truePositive + falseNegative;
        return (div != 0) ? (truePositive / div) : 0;
    }

    /**
     * Get the F1 score (the harmonic mean of the precision and sensitivity) of the classifier.
     *
     * <p> F1 </p>
     *
     * @return The F1 score of the classifier.
     */
    public double getF1Score() {
        double div = (2.0 * truePositive) + falsePositive + falseNegative;
        return (div != 0) ? ((2.0 * truePositive) / div) : 0;
    }

    /**
     * Get the accuracy of the classifier.
     *
     * <p> ACC </p>
     *
     * @return The accuracy of the classifier.
     */
    public double getAccuracy() {
        double div = truePositive + trueNegative + falsePositive + falseNegative;
        return (div != 0) ? ((truePositive + trueNegative) / div) : 0;
    }

    /**
     * Get the true negative rate (specificity) of the classifier.
     *
     * <p> TNR </p> <p> SPC </p>
     *
     * @return The true negative rate of the classifier.
     */
    public double getSpecificity() {
        double div = falsePositive + trueNegative;
        return (div != 0) ? (trueNegative / div) : 0;
    }

    /**
     * Get the negative predictive value of the classifier.
     *
     * @return The negative predictive value of the classifier.
     */
    public double getNegativePredictiveValue() {
        double div = trueNegative + falseNegative;
        return (div != 0) ? (trueNegative / div) : 0;
    }

    /**
     * Get the false positive rate (fall-out) of the classifier.
     *
     * <p> FPR </p>
     *
     * @return The false positive rate of the classifier.
     */
    public double getFallOut() {
        double div = falsePositive + trueNegative;
        return (div != 0) ? (falsePositive / div) : 0;
    }

    /**
     * Get the false discovery rate of the classifier.
     *
     * <p> FDR </p>
     *
     * @return The false discovery rate of the classifier.
     */
    public double getFalseDiscoveryRate() {
        double div = falsePositive + truePositive;
        return (div != 0) ? (falsePositive / div) : 0;
    }

    /**
     * Get the false omission rate of the classifier.
     *
     * <p> FOR </p>
     *
     * @return The false omission rate of the classifier.
     */
    public double getFalseOmissionRate() {
        double div = falseNegative + trueNegative;
        return (div != 0) ? (falseNegative / div) : 0;
    }

    /**
     * Get the false negative rate (miss rate) of the classifier.
     *
     * <p> FNR </p>
     *
     * @return The false negative rate of the classifier.
     */
    public double getMissRate() {
        double div = falseNegative + truePositive;
        return (div != 0) ? (falseNegative / div) : 0;
    }

    /**
     * Get the error rate (mis-classification rate) of the classifier.
     *
     * @return The error rate of the classifier.
     */
    public double getErrorRate() {
        double div = truePositive + trueNegative + falsePositive + falseNegative;
        return (div != 0) ? ((falsePositive + falseNegative) / div) : 0;
    }

    /**
     * Get the Matthews correlation coefficient of the classifier.
     *
     * <p> MCC </p>
     *
     * @return The Matthews correlation coefficient of the classifier.
     */
    public double getMCC() {
        double div = Math.sqrt((truePositive + falsePositive) * (truePositive + falseNegative) *
                (trueNegative + falsePositive) * (trueNegative + falseNegative));
        return (div != 0) ? (((truePositive * trueNegative) - (falsePositive * falseNegative)) / div) : 0;
    }

    /**
     * Get the informedness of the classifier.
     *
     * @return The informedness pof the classifier.
     */
    public double getInformedness() {
        return getRecall() + getSpecificity() - 1;
    }

    /**
     * Get the markedness of the classifier.
     *
     * @return The markedness of the classifier.
     */
    public double getMarkedness() {
        return getPrecision() + getNegativePredictiveValue() - 1;
    }

    /**
     * Get the positive likelihood ratio of the classifier.
     *
     * <p> LR+ </p>
     *
     * @return The positive likelihood ratio of the classifier.
     */
    public double getPositiveLikelihoodRatio() {
        double div = getFallOut();
        return (div != 0) ? (getRecall() / div) : 0;
    }

    /**
     * Get the negative likelihood ratio of the classifier.
     *
     * <p> LR- </p>
     *
     * @return The negative likelihood ratio of the classifier.
     */
    public double getNegativeLikelihoodRatio() {
        double div = getSpecificity();
        return (div != 0) ? (getMissRate() / div) : 0;
    }

    /**
     * Get the diagnostic odds ratio of the classifier.
     *
     * <p> DOR </p>
     *
     * @return The diagnostic odds ratio of the classifier.
     */
    public double getDOR() {
        double div = getNegativeLikelihoodRatio();
        return (div != 0) ? (getPositiveLikelihoodRatio() / div) : 0;
    }
}