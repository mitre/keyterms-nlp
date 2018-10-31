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

package keyterms.nlp.emoji;

import java.util.HashSet;
import java.util.Set;

import keyterms.util.collect.Unique;
import keyterms.util.text.Strings;

/**
 * The base abstraction for Emoticon and Emoji classes.
 */
public class Emote
        extends Unique<String> {
    /**
     * The emote text.
     */
    private final String text;

    /**
     * The textual description of the emote.
     */
    private final String description;

    /**
     * The text labels which identifier the emote.
     */
    private final Set<String> labels = new HashSet<>();

    /**
     * The metadata tags associated with the emote.
     */
    private final Set<String> tags = new HashSet<>();

    /**
     * Constructor.
     *
     * @param text The emote text.
     * @param description The textual description of the emote.
     */
    Emote(String text, String description) {
        super(text);
        this.text = text;
        this.description = Strings.toLowerCase(Strings.trim(description));
    }

    /**
     * Get the emote text.
     *
     * @return The emote text.
     */
    public String getText() {
        return text;
    }

    /**
     * Get the textual description of the emote.
     *
     * @return The textual description of the emote.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the text labels which identifier the emote.
     *
     * @return The text labels which identifier the emote.
     */
    public Set<String> getLabels() {
        return labels;
    }

    /**
     * Get the metadata tags associated with the emote.
     *
     * @return The metadata tags associated with the emote.
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getText();
    }
}