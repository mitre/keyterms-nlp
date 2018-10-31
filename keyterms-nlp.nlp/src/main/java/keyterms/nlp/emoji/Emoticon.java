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

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.io.Encoding;
import keyterms.util.lang.Lazy;
import keyterms.util.text.parser.SimpleDefinitions;

/**
 * Non-emoji representations of emoji like constructs.
 */
public class Emoticon
        extends Emote {
    /**
     * The loaded emoticon definitions mapped to their text representations.
     */
    static final Lazy<Map<String, Emoticon>> VALUES = new Lazy<>(() -> {
        Map<String, Emoticon> values = new HashMap<>();
        try {
            InputStream stream = Emoticon.class.getResourceAsStream("emoticons.lst");
            String emoticonList = Encoding.decode(stream.readAllBytes(), Encoding.UTF8);
            SimpleDefinitions parser = new SimpleDefinitions(emoticonList);
            while (parser.hasMore()) {
                String emoticonText = parser.getField("emoticon");
                String description = parser.getField("description");
                List<String> emoji = parser.getList("emoji");
                List<String> labels = parser.getList("labels");
                List<String> tags = parser.getList("tags");
                if (values.containsKey(emoticonText)) {
                    getClassLogger().error("Duplicate definition for: {}", emoticonText);
                }
                Emoticon emoticon = new Emoticon(emoticonText, description);
                emoticon.getEmoji().addAll(emoji);
                emoticon.getLabels().addAll(labels);
                emoticon.getTags().addAll(tags);
                values.put(emoticonText, emoticon);
            }
        } catch (Exception error) {
            getClassLogger().error("Could not load emoticon data.", error);
        }
        values.values().forEach((emoticon) -> {
            if (emoticon.getEmoji() != null) {
                emoticon.getEmoji().forEach((emojiText) -> {
                    Emoji emoji = Emoji.getEmoji(emojiText);
                    if (emoji != null) {
                        emoji.getEmoticons().add(emoticon.getText());
                    } else {
                        getClassLogger().warn("Invalid emoji {} for  emoticon {}", emojiText, emoticon);
                    }
                });
            }
        });
        return values;
    });

    /**
     * Get the logging topic for this class.
     *
     * @return The logging topic for this class.
     */
    private static Logger getClassLogger() {
        return LoggerFactory.getLogger(Emoticon.class);
    }

    /**
     * Get the complete set of emoticon values.
     *
     * @return The complete set of emoticon values.
     */
    public static Set<Emoticon> values() {
        return new HashSet<>(VALUES.value().values());
    }

    /**
     * Get the emoticon with the specified textual representation.
     *
     * @param emoticonText The unicode sequence for the emoticon.
     *
     * @return The specified emoji.
     */
    public static Emoticon getEmoticon(String emoticonText) {
        return VALUES.value().get(emoticonText);
    }

    /**
     * Emoji representations of the emoticon.
     */
    private final Set<String> emoji = new HashSet<>();

    /**
     * Constructor.
     *
     * @param text The textual representation of the emoticon.
     * @param description The textual description of the emoticon.
     */
    public Emoticon(String text, String description) {
        super(text, description);
    }

    /**
     * Get the emoji representations of the emoticon.
     *
     * @return The emoji representations of the emoticon.
     */
    public Set<String> getEmoji() {
        return emoji;
    }
}