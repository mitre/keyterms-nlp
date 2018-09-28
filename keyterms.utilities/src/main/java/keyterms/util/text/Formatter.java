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
 * A generic object to text converter.
 */
@FunctionalInterface
public interface Formatter<O> {
    /**
     * Wrap a lambda expression as a formatter.
     *
     * @param formatter The formatter.
     * @param <O> The formatter input class.
     *
     * @return The specified formatter.
     */
    static <O> Formatter<O> of(Formatter<O> formatter) {
        return formatter;
    }

    /**
     * Convert the specified value to a textual representation.
     *
     * @param value The value.
     *
     * @return The textual representation of the specified value.
     */
    String asText(O value);

    /**
     * Get a formatter in which the specified normalizer is applied after formatting.
     *
     * @param normalizer The text normalizer.
     *
     * @return The composite normalizing formatter.
     */
    default Formatter<O> then(Normalizer normalizer) {
        return v -> normalizer.normalize(asText(v));
    }
}