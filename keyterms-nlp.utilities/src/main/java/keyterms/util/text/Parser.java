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
 * A generic text to object parser.
 */
@FunctionalInterface
public interface Parser<O> {
    /**
     * Wrap a lambda expression as a parser.
     *
     * @param parser The parser.
     * @param <O> The parser output class.
     *
     * @return The specified parser.
     */
    static <O> Parser<O> of(Parser<O> parser) {
        return parser;
    }

    /**
     * Convert the specified text into an equivalent object representation.
     *
     * @param text The input text.
     *
     * @return The equivalent object representation of the input text.
     */
    O parse(CharSequence text);

    /**
     * Convert the specified text into an object.
     *
     * @param text The text.
     * @param defaultValue The value to return if parsing fails.
     *
     * @return The equivalent object.
     */
    default O parse(CharSequence text, O defaultValue) {
        O value;
        try {
            value = parse(text);
        } catch (Exception error) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Get a parser in which the specified normalizer is applied before parsing.
     *
     * @param normalizer The text normalizer.
     *
     * @return The composite normalizing parser.
     */
    default Parser<O> after(Normalizer normalizer) {
        return t -> parse(normalizer.normalize(t));
    }
}