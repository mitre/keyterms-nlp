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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.collect.Unique;
import keyterms.util.lang.Enums;
import keyterms.util.lang.Lazy;
import keyterms.util.lang.Version;
import keyterms.util.text.Strings;

/**
 * Single code point definition from the unicode standard.
 *
 * see http://www.unicode.org/reports/tr51
 */
class EmojiChar
        extends Unique<Integer> {
    /**
     * The character types.
     */
    enum Type {
        EMOJI,
        EMOJI_PRESENTATION,
        EMOJI_MODIFIER_BASE,
        EMOJI_COMPONENT,
        EXTENDED_PICTOGRAPHIC
    }

    /**
     * The pattern used to identify the version number in the main data file records.
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("[^#]*#\\s*([^\\[]+).*");

    /**
     * The loaded emoji code point definitions mapped to their text representations.
     */
    static final Lazy<Map<String, EmojiChar>> VALUES = new Lazy<>(() -> {
        Map<String, EmojiChar> values = new HashMap<>();
        DataFiles.getDataFileRecords().forEach((record) -> {
            try {
                List<Integer> codePoints = DataFiles.getCodePoints(record);
                String typeText = Strings.trim(record.replaceAll("^.*;", "").replaceAll("#.*$", ""));
                Type type = Enums.find(Type.class, typeText);
                Matcher versionMatcher = VERSION_PATTERN.matcher(record);
                if (!versionMatcher.matches()) {
                    throw new IllegalStateException("Could not locate unicode version.");
                }
                String versionText = Strings.trim(versionMatcher.group(1));
                if (!"NA".equalsIgnoreCase(versionText)) {
                    Version version = new Version(versionText);
                    String description = Strings.trim(record.replaceAll("^[^)]+\\)", ""));
                    List<EmojiChar> emojiChars = codePoints.stream()
                            .map((codePoint) -> new EmojiChar(codePoint, version))
                            .collect(Collectors.toList());
                    emojiChars.get(0).setDescription(description);
                    if (emojiChars.size() > 1) {
                        String[] descriptionSplit = description.split("\\.\\.");
                        emojiChars.get(0).setDescription(descriptionSplit[0]);
                        emojiChars.get(emojiChars.size() - 1).setDescription(descriptionSplit[1]);
                    }
                    emojiChars.forEach((emojiChar) ->
                            values.computeIfAbsent(emojiChar.getText(), (k) -> emojiChar).addType(type));
                }
            } catch (Exception error) {
                getClassLogger().error("Could not emoji data from record: {}", record, error);
            }
        });
        DataFiles.getVariationSequenceRecords().forEach((record) -> {
            List<Integer> codePoints = DataFiles.getCodePoints(record);
            assert (codePoints.size() == 2);
            EmojiChar base = values.get(new String(Character.toChars(codePoints.get(0))));
            assert (base != null);
            String description = Strings.toLowerCase(Strings.trim(record.replaceAll(".*\\)", "")));
            if (Strings.isBlank(base.getDescription())) {
                base.setDescription(description);
            }
        });
        DataFiles.getSequenceRecords().forEach((record) -> {
            if (record.toLowerCase().contains("emoji_modifier_sequence")) {
                List<Integer> codePoints = DataFiles.getCodePoints(record);
                assert (codePoints.size() == 2);
                String description = Strings.trim(record.replaceAll(".*;", "").replaceAll("#.*", ""));
                String[] descriptionSplit = description.split(":");
                assert (descriptionSplit.length == 2);
                String baseKey = new String(Character.toChars(codePoints.get(0)));
                String modifierKey = new String(Character.toChars(codePoints.get(1)));
                EmojiChar base = values.get(baseKey);
                assert (base.getTypes().contains(Type.EMOJI_MODIFIER_BASE));
                if (Strings.isBlank(base.getDescription())) {
                    base.setDescription(Strings.trim(descriptionSplit[0]));
                }
                EmojiChar modifier = values.get(modifierKey);
                assert (modifier.getTypes().contains(Type.EMOJI_COMPONENT));
                if (Strings.isBlank(modifier.getDescription())) {
                    modifier.setDescription(Strings.trim(descriptionSplit[1]));
                }
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
        return LoggerFactory.getLogger(EmojiChar.class);
    }

    /**
     * Get the emoji code point for the specified unicode code point.
     *
     * @param codePoint The equivalent unicode code point.
     *
     * @return The equivalent emoji code point.
     */
    public static EmojiChar getEmojiChar(int codePoint) {
        return VALUES.value().get(new String(Character.toChars(codePoint)));
    }

    /**
     * Get the emoji code point for the specified unicode text representation.
     *
     * @param text The textual representation of the emoji code point.
     *
     * @return The specified emoji code point.
     */
    public static EmojiChar getEmojiChar(String text) {
        return VALUES.value().get(text);
    }

    /**
     * The textual representation of the emoji character.
     */
    private final String text;

    /**
     * The unicode version at which the character was defined.
     */
    private final Version version;

    /**
     * The character types.
     */
    private final Set<Type> types = new HashSet<>();

    /**
     * The textual description of the emoji character.
     */
    private String description;

    /**
     * Constructor.
     *
     * @param codePoint The code point for the character.
     * @param version The unicode version at which the character was defined.
     */
    private EmojiChar(int codePoint, Version version) {
        super(codePoint);
        this.version = version;
        text = new String(Character.toChars(codePoint));
    }

    /**
     * Get the equivalent unicode code point.
     *
     * @return The equivalent unicode code point.
     */
    public int getCodePoint() {
        return getId();
    }

    /**
     * Get the textual representation of the code point.
     *
     * @return The textual representation of the code point.
     */
    public String getText() {
        return text;
    }

    /**
     * Get the unicode version at which the character was defined.
     *
     * @return The unicode version at which the character was defined.
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Get the character types.
     *
     * @return The character types.
     */
    public Set<Type> getTypes() {
        return types;
    }

    /**
     * Add a character type.
     *
     * @param type The character type.
     */
    private void addType(Type type) {
        types.add(type);
    }

    /**
     * Get the textual description of the code-point.
     *
     * @return The textual description of the code-point.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the textual description of the code-point.
     *
     * @param description The textual description of the code-point.
     */
    private void setDescription(String description) {
        this.description = Strings.toLowerCase(Strings.trim(description));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return text;
    }
}