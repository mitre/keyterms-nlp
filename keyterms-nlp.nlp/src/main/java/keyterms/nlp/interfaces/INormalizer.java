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

package keyterms.nlp.interfaces;

public interface INormalizer {
    /**
     * Create a representation of a string that is appropriate for determining
     * uniqueness in a text index. This normalization form should also be used
     * before searching against indexes created with this method
     *
     * @param input The input text.
     * @param removeSpacesForIndex A flag indicating whether to remove whitespace for index forms.
     *
     * @return The index form of the specified text.
     */
    String normalizeForIndex(String input, boolean removeSpacesForIndex);

    /**
     * Boolean indicating whether spaces should be removed from indexes
     * preferentially
     *
     * @return A flag indicating whether spaces will be removed from indexes by default.
     */
    boolean getRemoveSpacesForIndex();

    /**
     * Create a representation of the string that is used for fuzzy matching
     * and/or ranking text. This normalization is generally less aggressive than
     * index normalization, and should produce a Canonical/Compatibility
     * decomposed Unicode string (NFKD)
     *
     * @param input The input text.
     *
     * @return A representation of the string that is used for fuzzy matching and/or ranking text.
     */
    String normalizeForScoring(String input);

    /**
     * Create a representation of the string that is used for display text. This
     * normalization is generally less aggressive than index normalization and
     * should produce a Canonical/Compatibality composed Unicode string (NFKC)
     *
     * @param input The input text.
     *
     * @return The display form of the specified text.
     */
    String normalizeForDisplay(String input);
}