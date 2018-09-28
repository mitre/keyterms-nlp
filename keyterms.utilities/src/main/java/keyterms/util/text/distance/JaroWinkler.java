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

import java.util.Arrays;
import java.util.Objects;

import keyterms.util.collect.Sequences;
import keyterms.util.text.EditDistance;

/**
 * A basic {@code Jaro-Winkler} edit distance computer.
 *
 * <p> This is a normalized edit distance. </p>
 *
 * <p> Algorithm described at <a href="http://en.wikipedia.org/wiki/Jaro-Winkler"> wikipedia </a> </p>
 *
 * <p> Implementation ported from code at <a href="https://github.com/sunlightlabs/jellyfish/blob/master/jaro.c"> github
 * </a> </p>
 */
public class JaroWinkler
        implements EditDistance {
    /**
     * The constant scaling factor used by Winkler in his original work.
     */
    public static final double WINKLER_WEIGHT = 0.1;

    /**
     * A flag indicating whether to adjust the raw {@code Jaro} distances for common prefixes.
     */
    private final boolean winkler;

    /**
     * A constant scaling factor for how much the score is adjusted upwards for having common prefixes. This value
     * should not exceed {@code 0.25} otherwise the distance can become larger than {@code 1}. The standard value for
     * this constant in Winkler's work is {@code 0.1}.
     */
    private final double weight;

    /**
     * A flag indicating a further distance adjustment for long sequences.
     */
    private final boolean longSequences;

    /**
     * Constructor.
     */
    public JaroWinkler() {
        this(true, WINKLER_WEIGHT, false);
    }

    /**
     * Constructor.
     *
     * @param winkler A flag indicating whether to adjust the raw {@code Jaro} distances for common prefixes.
     * @param weight A constant scaling factor for how much the score is adjusted upwards for having common prefixes.
     * This value should not exceed {@code 0.25} otherwise the distance can become larger than {@code 1}. The standard
     * value for this constant in Winkler's work is {@code 0.1}.
     * @param longSequences A flag indicating a further distance adjustment for long sequences.
     */
    public JaroWinkler(boolean winkler, double weight, boolean longSequences) {
        super();
        this.winkler = winkler;
        this.weight = weight;
        this.longSequences = longSequences;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <O> double getDistance(O sequence1, O sequence2) {
        double distance = 0;
        int length1 = (sequence1 != null) ? Sequences.length(sequence1) : 0;
        int length2 = (sequence2 != null) ? Sequences.length(sequence2) : 0;
        if ((length1 > 0) || (length2 > 0)) {
            distance = 1;
            if ((length1 > 0) && (length2 > 0)) {
                boolean[] flags1 = new boolean[length1 + 1];
                Arrays.fill(flags1, false);
                boolean[] flags2 = new boolean[length2 + 1];
                Arrays.fill(flags2, false);

                int searchRange = Math.max(length1, length2);
                searchRange = (searchRange / 2) - 1;
                if (searchRange < 0) {
                    searchRange = 0;
                }

                double matches = 0;
                double transpositions = 0;

                for (int i1 = 0; i1 < length1; i1++) {
                    int lowLimit = (i1 >= searchRange) ? i1 - searchRange : 0;
                    int highLimit = ((i1 + searchRange) <= (length2 - 1)) ? (i1 + searchRange) : length2 - 1;
                    for (int c = lowLimit; c <= highLimit; c++) {
                        Object value1 = Sequences.get(sequence2, c);
                        Object value2 = Sequences.get(sequence1, i1);
                        if ((!flags2[c]) && (Objects.equals(value1, value2))) {
                            flags2[c] = true;
                            flags1[i1] = true;
                            matches++;
                            break;
                        }
                    }
                }
                if (matches > 0) {
                    int start = 0;
                    for (int i1 = 0; i1 < length1; i1++) {
                        if (flags1[i1]) {
                            int i2;
                            for (i2 = start; i2 < length2; i2++) {
                                if (flags2[i2]) {
                                    start = i2 + 1;
                                    break;
                                }
                            }
                            Object value1 = Sequences.get(sequence1, i1);
                            Object value2 = Sequences.get(sequence2, i2);
                            if (!Objects.equals(value1, value2)) {
                                transpositions++;
                            }
                        }
                    }
                    transpositions /= 2;

                    double p1 = matches / (double)length1;
                    double p2 = matches / (double)length2;
                    double pt = (matches - transpositions) / matches;
                    distance = ((p1 + p2 + pt) / 3.0);

                    if (winkler) {
                        int minLength = Math.min(length1, length2);
                        if (distance > 0.7) {
                            int firstChars = Math.max(minLength, 4);
                            int i;
                            for (i = 0; i < firstChars; i++) {
                                Object value1 = Sequences.get(sequence1, i);
                                Object value2 = Sequences.get(sequence2, i);
                                if (!Objects.equals(value1, value2)) {
                                    break;
                                }
                            }
                            if (i > 0) {
                                distance += (i * weight * (1 - distance));
                            }
                            if ((longSequences) && (minLength > 4) && (matches > i + 1) &&
                                    ((2 * matches) >= (minLength + 1))) {
                                distance += ((1.0 - distance) *
                                        ((matches - i - 1) / ((double)(length1 + length2 - i * 2 + 2))));
                            }
                        }
                    }
                    distance = 1.0 - distance;
                }
            }
        }
        return distance;
    }
}