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

package keyterms.nlp.iso;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import keyterms.testing.Tests;
import keyterms.util.collect.Bags;
import keyterms.util.io.Encoding;
import keyterms.util.text.Strings;
import keyterms.util.text.splitter.LineSplitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Language_UT {

    public static List<Language> getCommonLanguages() {
        return Bags.arrayList(
                Language.ARABIC,
                Language.BULGARIAN,
                Language.CHINESE,
                Language.CROATIAN,
                Language.CZECH,
                Language.DANISH,
                Language.DUTCH,
                Language.ENGLISH,
                Language.ESTONIAN,
                Language.FINNISH,
                Language.FRENCH,
                Language.GERMAN,
                Language.GREEK,
                Language.HUNGARIAN,
                Language.IRISH,
                Language.ITALIAN,
                Language.JAPANESE,
                Language.KOREAN,
                Language.LATVIAN,
                Language.LITHUANIAN,
                Language.MALTESE,
                Language.POLISH,
                Language.PORTUGUESE,
                Language.ROMANIAN,
                Language.RUSSIAN,
                Language.SLOVAK,
                Language.SLOVENIAN,
                Language.SPANISH,
                Language.SWEDISH
        );
    }

    @Test
    public void dataTable()
            throws Exception {
        assertNotEquals("values loaded", 0, Language.values().size());
        String codeTable = Encoding.decode(
                Language.class.getResourceAsStream("rsc/languages.lst").readAllBytes(),
                Encoding.UTF8);
        List<String> lines = new LineSplitter().split(codeTable).stream()
                .filter((l) -> l.startsWith("code:"))
                .collect(Collectors.toList());
        assertEquals(lines.size(), Language.values().size());
    }

    @Test
    public void constants()
            throws Exception {
        for (Field field : Language.class.getFields()) {
            int modifiers = field.getModifiers();
            if ((Modifier.isStatic(modifiers)) && (Modifier.isFinal(modifiers))) {
                assertNotNull(field.getName(), field.get(null));
            }
        }
    }

    @Test
    public void byCode() {
        for (Language language : Language.values()) {
            assertNotNull(language.toString(), language.getCode());
            assertNotEquals(language.toString(), "", Strings.trim(language.getCode()));
            assertEquals(language.toString(), 3, language.getCode().length());
            assertSame(language.toString(), language, Language.byCode(language.getCode()));
        }
    }

    @Test
    public void byAltCode() {
        int part1Count = 0;
        int part2BCount = 0;
        int part2TCount = 0;
        int altCount = 0;
        for (Language language : Language.values()) {
            if (language.getPart1() != null) {
                assertSame(language, Language.byCode(language.getPart1()));
                part1Count++;
            }
            if (language.getPart2B() != null) {
                assertSame(language, Language.byCode(language.getPart2B()));
                part2BCount++;
            }
            if (language.getPart2T() != null) {
                assertSame(language, Language.byCode(language.getPart2T()));
                part2TCount++;
            }
            for (String alt : language.getAltCodes()) {
                assertSame(language, Language.byCode(alt));
                altCount++;
            }
        }
        assertEquals("part 1 codes", 185, part1Count);
        assertEquals("part 2B codes", 419, part2BCount);
        assertEquals("part 2T codes", 419, part2TCount);
        assertEquals("alternate codes", 625, altCount);
    }

    @Test
    public void byName() {
        List<Language> overloads = new ArrayList<>();
        for (Language language : Language.values()) {
            String name = language.getName();
            Language byName = Language.byName(name);
            if (byName == null) {
                LoggerFactory.getLogger("Language1").warn("Failed name lookup for {}", name);
                overloads.add(language);
            }
        }
        assertEquals(17, overloads.size());
    }

    @Test
    public void byAlias() {
        List<Language> overloads = new ArrayList<>();
        for (Language language : Language.values()) {
            for (String name : language.getAliases()) {
                Language byName = Language.byName(name);
                if (byName == null) {
                    LoggerFactory.getLogger("Language1").warn("Failed alias lookup for {}", name);
                    overloads.add(language);
                }
            }
        }
        assertEquals(206, overloads.size());
    }

    @Test
    public void find() {
        for (Language language : Language.values()) {
            assertNotEquals(language.toString(), Collections.emptySet(), Language.find(language.getCode()));
            assertNotEquals(language.toString(), Collections.emptySet(), Language.find(language.getName()));
        }
    }

    @Test
    public void memberMacroMappings() {
        int macroLanguage1s = 0;
        for (Language language : Language.values()) {
            boolean isMacroLanguage = language.isMacroLanguage();
            if (isMacroLanguage) {
                macroLanguage1s++;
                assertNotEquals(language.toString(), 0, language.getMembers().size());
                for (Language member : language.getMembers()) {
                    assertSame(language, member.getMacroLanguage());
                }
            } else {
                assertEquals(0, language.getMembers().size());
            }
        }
        assertEquals(62, macroLanguage1s);
    }

    @Test
    public void serialization()
            throws Exception {
        Tests.testSerialization(Language.values(), true);
    }

    @Test
    public void legacyCompatibility() {
        for (LanguageCode languageCode : LanguageCode.values()) {
            Language language = Language.byCode(languageCode.getIso3());
            assertNotNull(languageCode.getIso3(), language);
            String iso2 = languageCode.getIso2();
            // The two letter code for the indeterminate language is not specified in the standard and conflicts
            // with other two letter codes.
            if ((Strings.hasText(iso2)) && (!Language.UND.equals(language))) {
                assertEquals(languageCode.getIso3(), iso2.toLowerCase(), Strings.toLowerCase(language.getPart1()));
            }
            Set<Language> byAlias;
            if (Strings.hasText(languageCode.getNativeName())) {
                byAlias = Language.find(languageCode.getNativeName());
                // languages updated beyond initial compatibility
                if (!Bags.hashSet("msa", "mal", "uzb").contains(languageCode.getIso3().toLowerCase())) {
                    assertNotEquals(languageCode.getIso3() + ": " + languageCode.getNativeName(), 0, byAlias.size());
                }
            }
            if (languageCode.getAltForms() != null) {
                for (String alt : languageCode.getAltForms()) {
                    byAlias = Language.find(alt);
                    // languages updated beyond initial compatibility
                    if (!Bags.hashSet("msa", "mal", "uzb").contains(languageCode.getIso3().toLowerCase())) {
                        assertNotEquals(languageCode.getIso3() + ": " + alt, 0, byAlias.size());
                    }
                }
            }
            Set<ScriptCode> scriptCodes = new HashSet<>(LanguageToScript.getScripts(languageCode));
            Set<Script> scripts = language.getScripts();
            if (!Bags.staticSet(ScriptCode.ZYYY).equals(scriptCodes)) {
                assertTrue(language.toString(), scriptCodes.size() <= scripts.size());
            }
            if ((languageCode.getPreferredScriptCode() != null) &&
                    (!languageCode.getPreferredScriptCode().equals(ScriptCode.ZYYY))) {
                assertNotNull(language.toString(), language.getPreferredScript());
            }
        }
    }
}