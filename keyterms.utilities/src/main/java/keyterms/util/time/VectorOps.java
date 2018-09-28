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

package keyterms.util.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that provides static vector operations on lists of doubles.
 */
public class VectorOps {

    /**
     * Computes the cosine similarity between two vectors. Commutative.
     *
     * @param X a vector (list of doubles)
     * @param Y a vector (list of doubles)
     *
     * @return the cosine similarity between the two vectors (scalar in the range [-1, 1])
     */
    public static double cosSim(List<Double> X, List<Double> Y) {
        int numberOfElements = X.size();
        if (numberOfElements != Y.size()) {
            throw new IllegalArgumentException("Lists X and Y must have the same length.");
        } else {
            List<Double> weights = new ArrayList<>();
            for (int i = 0; i < numberOfElements; i++) {
                weights.add(1.0);
            }
            return weightedCosSim(X, Y, weights);
        }
    }

    /**
     * Computes the weighted cosine similarity between two vectors. Commutative.
     *
     * @param X a vector (list of doubles)
     * @param Y a vector (list of doubles)
     * @param weights the weight vector (same dimensionality as the vectors)
     *
     * @return the weighted cosine similarity between the two vectors (scalar in the range [-1, 1])
     */
    public static double weightedCosSim(List<Double> X, List<Double> Y, List<Double> weights) {
        int numberOfElements = X.size();
        if (numberOfElements != Y.size() || numberOfElements != weights.size()) {
            throw new IllegalArgumentException("Lists X, Y, and weights must have the same length.");
        } else {
            double numerator = weightedDot(X, Y, weights);
            double denominator = weightedCosSimDenominator(X, Y, weights);
            return numerator / denominator;
        }
    }

    /**
     * Computes the dot product (inner product) between two vectors. Commutative.
     *
     * @param X a vector (list of doubles)
     * @param Y a vector (list of doubles)
     *
     * @return the dot product of the two vectors
     */
    public static double dot(List<Double> X, List<Double> Y) {
        int numberOfElements = X.size();
        if (numberOfElements != Y.size()) {
            throw new IllegalArgumentException("Lists X and Y must have the same length.");
        } else {
            List<Double> weights = new ArrayList<>();
            for (int i = 0; i < numberOfElements; i++) {
                weights.add(1.0);
            }
            return weightedDot(X, Y, weights);
        }
    }

    /**
     * Computes the weighted dot product (inner product) between two vectors. Commutative.
     *
     * @param X a vector (list of doubles)
     * @param Y a vector (list of doubles)
     * @param weights the weight vector (same dimensionality as the vectors)
     *
     * @return the weighted dot product of the two vectors
     */
    public static double weightedDot(List<Double> X, List<Double> Y, List<Double> weights) {
        int numberOfElements = X.size();
        if (numberOfElements != Y.size() || numberOfElements != weights.size()) {
            throw new IllegalArgumentException("Lists X, Y, and weights must have the same length.");
        } else {
            double sum = 0.0;
            for (int i = 0; i < numberOfElements; i++) {
                sum += X.get(i) * Y.get(i) * weights.get(i);
            }
            return sum;
        }
    }

    /**
     * Computes a vector of element-wise products. Commutative.
     *
     * @param X a vector (list of doubles)
     * @param Y a vector (list of doubles)
     *
     * @return a vector of element-wise products
     */
    public static List<Double> elementwiseMultiply(List<Double> X, List<Double> Y) {
        int numberOfElements = X.size();
        if (numberOfElements != Y.size()) {
            throw new IllegalArgumentException("Lists X and Y must have the same length.");
        } else {
            List<Double> toReturn = new ArrayList<>();
            for (int i = 0; i < numberOfElements; i++) {
                toReturn.add(X.get(i) * Y.get(i));
            }
            return toReturn;
        }
    }

    /**
     * Computes the magnitude (length) of a vector.
     *
     * @param X the vector (list of doubles)
     *
     * @return the magnitude (length) of a vector
     */
    public static double magnitude(List<Double> X) {
        double sum = 0.0;
        for (Double aX : X) {
            sum += aX * aX;
        }
        return Math.sqrt(sum);
    }

    /**
     * Computes the denominator for the weighted cosine similarity. Implements the following formula:
     * <p>
     * sqrt( (\sum_i W(i) * X(i)^2)(\sum_i W(i) * Y(i)^2) )
     * <p>
     * This method is private because the formula it implements is specific to the weighted cosine
     * similarity, and it doesn't make sense to use the method outside of this context.
     *
     * @param X a vector (list of doubles)
     * @param Y a vector (list of doubles)
     * @param W the weight vector (same dimensionality as the vectors)
     *
     * @return the "weighted magnitude" of two vectors
     */
    private static double weightedCosSimDenominator(List<Double> X, List<Double> Y, List<Double> W) {
        double sumX = 0.0;
        double sumY = 0.0;
        for (int i = 0; i < X.size(); i++) {
            sumX += W.get(i) * X.get(i) * X.get(i);
            sumY += W.get(i) * Y.get(i) * Y.get(i);
        }
        return Math.sqrt(sumX * sumY);
    }

    /**
     * Converts a list of integers to a list of doubles. Uses varargs.
     *
     * @param args the integers to convert to doubles and return in a list
     *
     * @return the list of doubles
     */
    public static List<Double> convertToDoubles(Integer... args) {
        List<Integer> arrayAsList = Arrays.asList(args);
        return convertToDoubles(arrayAsList);
    }

    /**
     * Converts a list of integers to a list of doubles.
     *
     * @param integerArray the list of integers
     *
     * @return the list of doubles
     */
    public static List<Double> convertToDoubles(List<Integer> integerArray) {
        return integerArray.stream().map(element -> (double)element).collect(Collectors.toList());
    }
}