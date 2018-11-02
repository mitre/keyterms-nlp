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

import java.util.ArrayList;

import keyterms.nlp.iso.Language;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;

/**
 * The interface exposed by text transformers.
 */
public interface ITextTransformer {
    /**
     * Normalized the input text for display.
     *
     * @param input The input text.
     *
     * @return The display form of the input text.
     */
    String normalizeForDisplay(String input);

    /**
     * Normalized the input text for scoring.
     *
     * <p> This creates a representation of the input that is used for fuzzy matching
     * and/or ranking text. This normalization is generally less aggressive than
     * index normalization, and should produce a Canonical/Compatibility
     * decomposed Unicode string (NFKD). </p>
     *
     * @param input The input text.
     *
     * @return The scoring form of the input text.
     */
    String normalizeForScoring(String input);

    /**
     * Normalize the input text for use in indexes.
     *
     * @param input The input text.
     *
     * @return The index form of the input text.
     */
    String normalizeForIndex(String input);

    /**
     * Transliterate text as specified.
     *
     * @param input The input text.
     * @param standard The general transliteration standard.
     *
     * @return The transliterated text.
     */
    String transliterate(String input, TextType standard);

    /**
     * This method creates one transliteration for each standard supported by
     * the transformer
     *
     * @param input The input text.
     * @param language The language of the input text.
     *
     * @return key/value pairs for all transliteration standards supported by a
     * given transliterator
     */
    ArrayList<Transliteration> getAvailableTransforms(String input, Language language);
}