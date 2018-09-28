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
import java.util.List;
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

public class Script_UT {

    public static List<Script> getCommonScripts() {
        return Bags.arrayList(
                Script.ARABIC,
                Script.CYRILLIC,
                Script.GREEK,
                Script.LATIN,
                Script.HAN_TRADITIONAL,
                Script.HAN_SIMPLIFIED,
                Script.KOREAN,
                Script.JAPANESE
        );
    }

    @Test
    public void dataTable()
            throws Exception {
        assertNotEquals("values loaded", 0, Script.values().size());
        String codeTable = Encoding.decode(
                Script.class.getResourceAsStream("rsc/scripts.lst").readAllBytes(),
                Encoding.UTF8);
        List<String> lines = new LineSplitter().split(codeTable).stream()
                .filter((l) -> l.startsWith("number:"))
                .collect(Collectors.toList());
        assertEquals(lines.size(), Script.values().size());
    }

    @Test
    public void constants()
            throws Exception {
        for (Field field : Script.class.getFields()) {
            int modifiers = field.getModifiers();
            if ((Modifier.isStatic(modifiers)) && (Modifier.isFinal(modifiers))) {
                assertNotNull(field.getName(), field.get(null));
            }
        }
    }

    @Test
    public void byCode() {
        for (Script script : Script.values()) {
            assertNotNull(script.toString(), script.getCode());
            assertNotEquals(script.toString(), "", Strings.trim(script.getCode()));
            assertEquals(script.toString(), 4, script.getCode().length());
            assertSame(script.toString(), script, Script.byCode(script.getCode()));
        }
    }

    @Test
    public void byAltCode() {
        int altCount = 0;
        for (Script script : Script.values()) {
            for (String alt : script.getAltCodes()) {
                assertSame(script, Script.byCode(alt));
                altCount++;
            }
        }
        assertEquals(96, altCount);
    }

    @Test
    public void byName() {
        List<Script> overloads = new ArrayList<>();
        for (Script script : Script.values()) {
            String name = script.getName();
            Script byName = Script.byName(name);
            if (byName == null) {
                LoggerFactory.getLogger("Script_New").warn("Failed name lookup for {}", name);
                overloads.add(script);
            }
        }
        assertEquals(Collections.emptyList(), overloads);
    }

    @Test
    public void byAlias() {
        List<Script> overloads = new ArrayList<>();
        for (Script script : Script.values()) {
            for (String name : script.getAliases()) {
                Script byName = Script.byName(name);
                if (byName == null) {
                    LoggerFactory.getLogger("Script_New").warn("Failed alias lookup for {}", name);
                    overloads.add(script);
                }
            }
        }
        assertEquals(Collections.emptyList(), overloads);
    }

    @Test
    public void find() {
        for (Script script : Script.values()) {
            assertNotEquals(script.toString(), Collections.emptySet(), Script.find(script.getCode()));
            assertNotEquals(script.toString(), Collections.emptySet(), Script.find(script.getName()));
        }
    }

    @Test
    public void unicodeEquivalents() {
        assertNotEquals(0, Script.values().stream().filter((s) -> s.getUnicodeScript() != null).count());
    }

    @Test
    public void serialization()
            throws Exception {
        Tests.testSerialization(Script.values(), true);
    }

    @Test
    public void legacyCompatibility() {
        for (ScriptCode scriptCode : ScriptCode.values()) {
            Script script = Script.byCode(scriptCode.getIsoFour());
            assertNotNull(scriptCode.name(), script);
            scriptCode.getAltForms().forEach((a) ->
                    assertSame(a, script, Script.byName(a)));
            Character.UnicodeScript unicodeScript_New = ScriptCode.getUnicodeScriptForString(script.getCode());
            if (unicodeScript_New != null) {
                assertSame(script.toString(), unicodeScript_New, script.getUnicodeScript());
            }
            String french = scriptCode.getFrenchName();
            if (Strings.hasText(french)) {
                assertNotNull(scriptCode.getIsoFour() + ": " + french, Script.byName(french));
            }
        }
        for (Script script : Script.values()) {
            if (script.getUnicodeScript() != null) {
                assertNotNull(script.getName(), ScriptCode.getUnicodeScriptForString(script.getCode()));
            }
        }
    }
}