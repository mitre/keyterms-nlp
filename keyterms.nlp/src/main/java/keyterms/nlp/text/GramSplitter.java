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

package keyterms.nlp.text;

import java.util.ArrayList;
import java.util.List;

import keyterms.util.text.Strings;
import keyterms.util.text.TextSplitter;

/**
 * A text splitter which breaks text runs into n-grams of a specified size.
 */
public class GramSplitter
        implements TextSplitter {
    /**
     * The number of characters per gram.
     */
    private final int gramSize;

    /**
     * The padding character to use when breaking up text into grams.
     */
    private final Character padCharacter;

    /**
     * Constructor.
     *
     * @param gramSize The number of characters per gram.
     */
    public GramSplitter(int gramSize) {
        this(gramSize, null);
    }

    /**
     * Constructor.
     *
     * @param gramSize The number of characters per gram.
     * @param padCharacter The padding character to use when breaking up text into grams.
     */
    public GramSplitter(int gramSize, Character padCharacter) {
        super();
        this.gramSize = gramSize;
        this.padCharacter = padCharacter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> split(CharSequence text) {
        List<String> grams = new ArrayList<>();
        if (!Strings.isEmpty(text)) {
            StringBuilder padded = new StringBuilder(text);
            if (padCharacter != null) {
                for (int p = 0; p < (gramSize - 1); p++) {
                    padded.insert(0, padCharacter);
                    padded.append(padCharacter);
                }
            }
            for (int c = 0; c < (padded.length() - gramSize + 1); c++) {
                grams.add(padded.substring(c, c + gramSize));
            }
        }
        return grams;
    }
}