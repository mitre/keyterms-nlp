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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import keyterms.nlp.iso.Country;
import keyterms.util.collect.Bags;
import keyterms.util.lang.Lazy;
import keyterms.util.lang.Version;
import keyterms.util.text.Strings;

/**
 * A container for all known emoji sequences.
 */
public class Emoji
        extends Emote {
    /**
     * The loaded emoji code point definitions mapped to their text representations.
     */
    static final Lazy<Map<String, Emoji>> VALUES = new Lazy<>(() -> {
        Map<String, Emoji> values = new HashMap<>();
        EmojiChar.VALUES.value().values().forEach((emojiChar) -> {
            if ((emojiChar.getTypes().contains(EmojiChar.Type.EMOJI)) ||
                    (emojiChar.getTypes().contains(EmojiChar.Type.EMOJI_PRESENTATION)) ||
                    (emojiChar.getTypes().contains(EmojiChar.Type.EXTENDED_PICTOGRAPHIC))) {
                Emoji emoji = new Emoji(Bags.arrayList(emojiChar), emojiChar.getVersion(), emojiChar.getDescription());
                values.put(emoji.toString(), emoji);
            }
        });
        DataFiles.getVariationSequenceRecords().forEach((record) -> {
            List<Integer> codePoints = DataFiles.getCodePoints(record);
            assert (codePoints.size() == 2);
            EmojiChar base = EmojiChar.getEmojiChar(codePoints.get(0));
            assert (base != null);
            EmojiChar modifier = EmojiChar.getEmojiChar(codePoints.get(1));
            assert (modifier != null);
            assert (modifier.getTypes().contains(EmojiChar.Type.EMOJI_COMPONENT));
            String versionText = record.replaceAll(".*\\((.*)\\).*", "$1");
            Version version = new Version(versionText);
            assert (base.getVersion().equals(version));
            Emoji emoji = new Emoji(Bags.arrayList(base, modifier), version, base.getDescription());
            values.put(emoji.toString(), emoji);
        });
        DataFiles.getSequenceRecords().forEach((record) -> {
            List<Integer> codePoints = DataFiles.getCodePoints(record);
            List<EmojiChar> emojiChars = codePoints.stream()
                    .map(EmojiChar::getEmojiChar)
                    .collect(Collectors.toList());
            String versionText = Strings.trim(record.replaceAll(".*#(.*)\\[.*", "$1"));
            Version version = new Version(versionText);
            String description = record.replaceAll(".*;", "").replaceAll("#.*", "");
            Emoji emoji = new Emoji(emojiChars, version, description);
            values.put(emoji.toString(), emoji);
            if (record.toLowerCase().contains("emoji_flag_sequence")) {
                char cc1 = (char)(codePoints.get(0) - 0x1f1e6 + 'a');
                char cc2 = (char)(codePoints.get(1) - 0x1f1e6 + 'a');
                String countryCode = String.valueOf(cc1) + cc2;
                Country country = Country.byCode(countryCode);
                assert (country != null);
                emoji.flagCountry = country;
            }
        });
        DataFiles.getZwjSequenceRecords().forEach((record) -> {
            List<Integer> codePoints = DataFiles.getCodePoints(record);
            List<EmojiChar> emojiChars = codePoints.stream()
                    .map(EmojiChar::getEmojiChar)
                    .collect(Collectors.toList());
            String versionText = Strings.trim(record.replaceAll(".*#(.*)\\[.*", "$1"));
            Version version = new Version(versionText);
            String description = record.replaceAll(".*;", "").replaceAll("#.*", "");
            Emoji emoji = new Emoji(emojiChars, version, description);
            values.put(emoji.toString(), emoji);
        });
        return values;
    });

    /**
     * Convert the specified sequence of hexadecimal code points into emoji characters.
     *
     * <p> An example input would be : {@code "1F3F4 E0067 E0062 E0073 E0063 E0074 E007F"}. </p>
     * <p> The output would be the unicode emoji sequence for the flag of Scotland. </p>
     *
     * @param codePointText The hexadecimal words.
     *
     * @return The emoji characters equivalent to the specified hexadecimal words.
     */
    static List<EmojiChar> toEmojiCharacters(String codePointText) {
        List<EmojiChar> emojiChars = null;
        if (Strings.hasText(codePointText)) {
            emojiChars = Stream.of(codePointText.split("\\s+"))
                    .filter(Strings::hasText)
                    .map(Strings::trim)
                    .map(((s) -> Integer.parseInt(s, 16)))
                    .map(EmojiChar::getEmojiChar)
                    .collect(Collectors.toList());
        }
        return emojiChars;
    }

    /**
     * Convert the list of code points to their unicode representation.
     *
     * @param codePoints The code points.
     *
     * @return The unicode representation of the specified code points.
     */
    static String toText(List<EmojiChar> codePoints) {
        return String.join("", codePoints.stream().map(Object::toString).collect(Collectors.toList()));
    }

    /**
     * Get the complete set of emoji values.
     *
     * @return The complete set of emoji values.
     */
    public static Set<Emoji> values() {
        return new HashSet<>(VALUES.value().values());
    }

    /**
     * Get the emoji with the specified textual representation.
     *
     * @param emojiText The unicode sequence for the emoji.
     *
     * @return The specified emoji.
     */
    public static Emoji getEmoji(String emojiText) {
        return VALUES.value().get(emojiText);
    }

    /**
     * The emoji characters which comprise the emoji.
     */
    private final List<EmojiChar> emojiChars;

    /**
     * The unicode version at which the emoji was defined.
     */
    private final Version version;

    /**
     * The country for flag sequences.
     */
    private Country flagCountry;

    /**
     * Emoticon representations of the emoji.
     */
    private final Set<String> emoticons = new HashSet<>();

    /**
     * Constructor.
     *
     * @param emojiChars The list of code points which make up the emoji.
     * @param version The unicode version at which the emoji was defined.
     * @param description The textual description of the emoji.
     */
    private Emoji(List<EmojiChar> emojiChars, Version version, String description) {
        super(toText(emojiChars), description);
        this.emojiChars = emojiChars;
        this.version = version;
    }

    /**
     * Get the list of code points which make up the emoji.
     *
     * @return The list of code points which make up the emoji.
     */
    public List<EmojiChar> getEmojiChars() {
        return emojiChars;
    }

    /**
     * Get the unicode version at which the emoji was defined.
     *
     * @return The unicode version at which the emoji was defined.
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Get the country associated with the emoji for flag sequences.
     *
     * <p> For all country flag sequences, this value will be non-{@code null}. </p>
     *
     * @return The country associated with the emoji for flag sequences.
     */
    public Country getFlagCountry() {
        return flagCountry;
    }

    /**
     * Get the emoticon representations for this emoji.
     *
     * @return The emoticon representations for this emoji.
     */
    public Set<String> getEmoticons() {
        return emoticons;
    }
}