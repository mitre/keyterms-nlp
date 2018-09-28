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

package keyterms.util.text;

/**
 * The interface implemented by objects capable of computing edit distances.
 */
@FunctionalInterface
public interface EditDistance {
    /**
     * Compute the edit distance between the given sequences.
     *
     * <p> Edit distance is a measurement of similarity.</p>
     *
     * <p> The given sequences must either be {@code Array}, {@code List} or {@code CharSequence} objects. </p>
     *
     * <p> Both sequences must be of the same type. </p>
     *
     * <p> {@code null} sequences should be treated the same as zero length sequences. </p>
     *
     * <p> All {@code EditDistance} implementations should return normalized scores such that {@code 0.0} indicates that
     * the sequences are identical and {@code 1.0} indicates that the sequences are dissimilar. </p>
     *
     * @param sequence1 The first sequence.
     * @param sequence2 The second sequence.
     * @param <O> The sequence class.
     *
     * @return The edit distance between the two sequences.
     */
    <O> double getDistance(O sequence1, O sequence2);

    /**
     * Compute a similarity score based on the distance.
     *
     * <p> NOTE: This implementation relies on the getDistance function returning a normalized distances
     * (a value between 0.0 and 1.0 where 0.0 indicates that there is no commonality between the sequences and
     * 1.0 indicates that the sequences are identical). </p>
     *
     * @param sequence1 The first sequence.
     * @param sequence2 The second sequence.
     * @param <O> The sequence class.
     *
     * @return The similarity score for the two sequences.
     */
    default <O> double getSimilarity(O sequence1, O sequence2) {
        return 1 - getDistance(sequence1, sequence2);
    }
}