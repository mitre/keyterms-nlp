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

import java.util.List;
import java.util.Map;

import org.junit.Test;

import keyterms.nlp.iso.Country;
import keyterms.util.lang.Version;
import keyterms.util.text.Strings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class Emoji_UT {

    @Test
    public void hexToString() {
        String codePointText = "1F3F4 E0067 E0062 E0073 E0063 E0074 E007F";
        String asText = Emoji.toText(Emoji.toEmojiCharacters(codePointText));
        Emoji scotland = Emoji.VALUES.value().values().stream()
                .filter((e) -> e.getDescription() != null)
                .filter((e) -> e.getDescription().equalsIgnoreCase("scotland"))
                .findFirst()
                .orElse(null);
        assertNotNull(scotland);
        assertEquals(scotland.toString(), asText);
    }

    @Test
    public void values() {
        Map<String, Emoji> values = Emoji.VALUES.value();
        assertNotNull(values);
        assertNotEquals(0, values.size());
    }

    @Test
    public void spotCheckVariations() {
        // 262F FE0E  ; text style;  # (1.1) YIN YANG
        // 262F FE0F  ; emoji style; # (1.1) YIN YANG
        Map<String, Emoji> values = Emoji.VALUES.value();
        String yinYangText = Emoji.toText(Emoji.toEmojiCharacters("262F FE0E"));
        Emoji emoji = values.get(yinYangText);
        assertNotNull(emoji);
        assertEquals(new Version("1.1"), emoji.getVersion());
        assertEquals("yin yang", emoji.getDescription());
        yinYangText = Emoji.toText(Emoji.toEmojiCharacters("262F FE0F"));
        emoji = values.get(yinYangText);
        assertNotNull(emoji);
        assertEquals(new Version("1.1"), emoji.getVersion());
        assertEquals("yin yang", emoji.getDescription());
    }

    @Test
    public void spotCheckFlags() {
        String codePointsText = "1F3F4 E0067 E0062 E0073 E0063 E0074 E007F";
        String flagText = Emoji.toText(Emoji.toEmojiCharacters(codePointsText));
        Emoji emoji = Emoji.VALUES.value().get(flagText);
        assertEquals(new Version(7), emoji.getVersion());
        assertEquals("scotland", emoji.getDescription());
    }

    @Test
    public void spotCheckZwjSequences() {
        // 1F3F3 FE0F 200D 1F308 ; Emoji_ZWJ_Sequence  ; rainbow flag #  7.0  [1] (🏳️‍🌈)
        // 1F3F4 200D 2620 FE0F ; Emoji_ZWJ_Sequence  ; pirate flag #  7.0  [1] (🏴‍☠️)
        String codePointsText = "1F3F3 FE0F 200D 1F308";
        String sequenceText = Emoji.toText(Emoji.toEmojiCharacters(codePointsText));
        Emoji emoji = Emoji.VALUES.value().get(sequenceText);
        assertNotNull("rainbow flag", emoji);
        assertEquals(new Version(7), emoji.getVersion());
        assertEquals("rainbow flag", emoji.getDescription());
        codePointsText = "1F3F4 200D 2620 FE0F";
        sequenceText = Emoji.toText(Emoji.toEmojiCharacters(codePointsText));
        emoji = Emoji.VALUES.value().get(sequenceText);
        assertNotNull("pirate flag", emoji);
        assertEquals(new Version(7), emoji.getVersion());
        assertEquals("pirate flag", emoji.getDescription());
    }

    @Test
    public void flagSequences() {
        DataFiles.getSequenceRecords().forEach((record) -> {
            List<Integer> codePoints = DataFiles.getCodePoints(record);
            String description = Strings.toLowerCase(Strings.trim(record.replaceAll(".*;", "").replaceAll("#.*", "")));
            if (record.toLowerCase().contains("emoji_flag_sequence")) {
                String countryName = description
                        .replaceAll("&", "and")
                        .replaceAll("’", "'")
                        .replaceAll("st\\.? ", "saint ")
                        .replaceAll("u\\.s\\. ", "united states ");
                if (countryName.contains("kinshasa")) {
                    countryName = "democratic republic of the congo";
                }
                if (countryName.contains("brazzaville")) {
                    countryName = "the congo";
                }
                if (countryName.contains("burma")) {
                    countryName = "myanmar";
                }
                if (countryName.contains("príncipe")) {
                    countryName = countryName.replaceAll(" and ", " et ");
                }
                if (countryName.equals("united kingdom")) {
                    countryName = "great britain";
                }
                if (countryName.endsWith("sar china")) {
                    countryName = countryName.substring(0, countryName.length() - 9);
                }
                Country country = Country.byName(countryName);
                if (countryName.equals("guinea")) {
                    country = Country.byCode("GIN");
                }
                if (countryName.equals("samoa")) {
                    country = Country.byCode("WSM");
                }
                assertNotNull(countryName, country);
                char cc1 = (char)(codePoints.get(0) - 0x1f1e6 + 'a');
                char cc2 = (char)(codePoints.get(1) - 0x1f1e6 + 'a');
                String countryCode = String.valueOf(cc1) + cc2;
                Country byCode = Country.byCode(countryCode);
                assertNotNull(countryCode, byCode);
                assertEquals(country, byCode);
            }
        });
    }
}