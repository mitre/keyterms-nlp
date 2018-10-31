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

package keyterms.util.text.distance;

import java.util.Objects;

import keyterms.util.collect.Sequences;
import keyterms.util.text.EditDistance;

/**
 * A basic {@code Levenshtein} edit distance computer.
 *
 * <p> The normalized edit distance returns a value between {@code 0.0} and {@code 1.0} where a value of {@code 0}
 * indicates identical values, and {@code 1.0} indicates completely unrelated values. </p>
 */
public class Levenshtein
        implements EditDistance {
    /**
     * Constructor.
     */
    public Levenshtein() {
        super();
    }

    /**
     * Get the non-normalized {@code Levenshtein} edit distance.
     *
     * @param sequence1 The first sequence.
     * @param sequence2 The second sequence.
     * @param <O> The sequence class.
     *
     * @return The non-normalized {@code Levenshtein} edit distance.
     */
    public <O> double getRawDistance(O sequence1, O sequence2) {
        O s1 = sequence1;
        O s2 = sequence2;
        int length1 = (sequence1 != null) ? Sequences.length(sequence1) : 0;
        int length2 = (sequence2 != null) ? Sequences.length(sequence2) : 0;
        if (length1 > length2) {
            s1 = sequence2;
            s2 = sequence1;
            int temp = length1;
            length1 = length2;
            length2 = temp;
        }
        double[] previous = new double[length1 + 1];
        double[] current = new double[length1 + 1];
        for (int i = 0; i <= length1; i++) {
            previous[i] = i;
        }
        for (int i2 = 0; i2 < length2; i2++) {
            Object value2 = Sequences.get(s2, i2);
            current[0] = i2 + 1;
            for (int i1 = 0; i1 < length1; i1++) {
                Object value1 = Sequences.get(s1, i1);
                double cost = (Objects.equals(value1, value2)) ? 0 : 1;
                current[i1 + 1] = min(current[i1] + 1, previous[i1 + 1] + 1, previous[i1] + cost);
            }
            double[] temp = previous;
            previous = current;
            current = temp;
        }
        return previous[length1];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <O> double getDistance(O sequence1, O sequence2) {
        double length1 = Sequences.length(sequence1);
        double length2 = Sequences.length(sequence2);
        double distance = 0;
        if ((length1 > 0) || (length2 > 0)) {
            distance = getRawDistance(sequence1, sequence2) / Math.max(length1, length2);
        }
        return distance;
    }

    /**
     * Determine the minimum value of the specified numbers.
     *
     * @param numbers The numbers.
     *
     * @return The minimum value of the specified numbers.
     */
    private double min(double... numbers) {
        double min = Double.NaN;
        if (numbers != null) {
            for (double number : numbers) {
                if ((Double.isNaN(min)) || (min > number)) {
                    min = number;
                }
            }
        }
        return min;
    }
}