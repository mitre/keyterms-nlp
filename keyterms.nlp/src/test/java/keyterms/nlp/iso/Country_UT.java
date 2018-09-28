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

public class Country_UT {

    @Test
    public void dataTable()
            throws Exception {
        assertNotEquals("values loaded", 0, Country.values().size());
        String codeTable = Encoding.decode(
                Country.class.getResourceAsStream("rsc/countries.lst").readAllBytes(),
                Encoding.UTF8);
        List<String> lines = new LineSplitter().split(codeTable).stream()
                .filter((l) -> l.startsWith("number:"))
                .collect(Collectors.toList());
        assertEquals(lines.size(), Country.values().size());
    }

    @Test
    public void constants()
            throws Exception {
        assertNotNull(Country.USA);
        for (Field field : Country.class.getFields()) {
            int modifiers = field.getModifiers();
            if ((Modifier.isStatic(modifiers)) && (Modifier.isFinal(modifiers))) {
                assertNotNull(field.getName(), field.get(null));
            }
        }
    }

    @Test
    public void byNumber() {
        for (Country country : Country.values()) {
            assertSame(country.toString(), country, Country.byNumber(country.getNumber()));
        }
    }

    @Test
    public void byCode() {
        for (Country country : Country.values()) {
            assertNotNull(country.toString(), country.getCode());
            assertNotEquals(country.toString(), "", Strings.trim(country.getCode()));
            assertSame(country.toString(), country, Country.byCode(country.getCode()));
        }
    }

    @Test
    public void byIso2() {
        for (Country country : Country.values()) {
            if (Strings.hasText(country.getIso2())) {
                assertSame(country.toString(), country, Country.byCode(country.getIso2()));
            }
        }
    }

    @Test
    public void byIso3() {
        for (Country country : Country.values()) {
            if (Strings.hasText(country.getIso3())) {
                assertSame(country.toString(), country, Country.byCode(country.getIso3()));
            }
        }
    }

    @Test
    public void byName() {
        List<Country> knownOverloads = Bags.arrayList(
                Country.byCode("GIN"), Country.byCode("WSM"), Country.byCode("UK")
        );
        List<Country> overloads = new ArrayList<>();
        for (Country country : Country.values()) {
            String name = country.getName();
            Country byName = Country.byName(name);
            if (byName == null) {
                LoggerFactory.getLogger("Country_1").warn("Failed name lookup for {}", name);
                overloads.add(country);
            }
        }
        assertEquals(knownOverloads, overloads);
    }

    @Test
    public void byAlias() {
        for (Country country : Country.values()) {
            for (String name : country.getAliases()) {
                Country byName = Country.byName(name);
                if (byName == null) {
                    LoggerFactory.getLogger("Country_1").warn("Failed alias lookup for {}", name);
                }
            }
        }
    }

    @Test
    public void find() {
        for (Country country : Country.values()) {
            assertNotEquals(country.toString(), Collections.emptySet(), Country.find(country.getCode()));
            assertNotEquals(country.toString(), Collections.emptySet(), Country.find(country.getName()));
        }
    }

    @Test
    public void serialization()
            throws Exception {
        Tests.testSerialization(Country.values(), true);
    }
}