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

package keyterms.analyzer.profiles.model;

import java.io.Serializable;

import keyterms.util.text.Formatter;
import keyterms.util.text.TextTransform;

/**
 * A serializable formatter implementation used by model features.
 */
@FunctionalInterface
public interface FeatureFormatter<F>
        extends Formatter<F>, Serializable {
    /**
     * Utility method for wrapping a lambda expression as a formatter.
     *
     * @param formatter The formatter.
     * @param <O> The formatter output value class.
     *
     * @return The specified formatter.
     */
    static <O> FeatureFormatter<O> of(FeatureFormatter<O> formatter) {
        return formatter;
    }

    /**
     * Get a composite text transform which in which this transform is applied after the specified text transform.
     *
     * @param transform The text transform to apply before this transform is applied.
     *
     * @return The composite text transform.
     */
    default FeatureFormatter<F> then(TextTransform transform) {
        return object -> transform.apply(asText(object));
    }
}