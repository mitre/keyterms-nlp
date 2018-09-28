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

package keyterms.nlp.transliterate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.collect.Bags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EndPoint_UT {

    @Test
    public void nullText() {
        EndPoint endPoint = new EndPoint(null);
        assertNotNull(endPoint);
        assertNull(endPoint.getTarget());
        assertNotNull(endPoint.getQualifiers());
        assertTrue(endPoint.getQualifiers().isEmpty());
        assertEquals(EndPoint.ANY, endPoint.toString());
    }

    @Test
    public void any() {
        EndPoint endPoint = new EndPoint(EndPoint.ANY);
        assertNotNull(endPoint);
        assertNull(endPoint.getTarget());
        assertNotNull(endPoint.getQualifiers());
        assertTrue(endPoint.getQualifiers().isEmpty());
        assertEquals(EndPoint.ANY, endPoint.toString());
        endPoint = new EndPoint("Any");
        assertNotNull(endPoint);
        assertNull(endPoint.getTarget());
        assertNotNull(endPoint.getQualifiers());
        assertTrue(endPoint.getQualifiers().isEmpty());
        assertEquals(EndPoint.ANY, endPoint.toString());
    }

    @Test
    public void explicitAnyForLanguage() {
        String key = "any_latin_jello";
        EndPoint endPoint = new EndPoint(key);
        assertNotNull(endPoint);
        assertEquals(Script.LATN, endPoint.getTarget());
        assertEquals(Bags.hashSet("jello"), endPoint.getQualifiers());
        assertEquals("latn_jello", endPoint.toString());
    }

    @Test
    public void languageOnly() {
        String key = "ru";
        EndPoint endPoint = new EndPoint(key);
        assertNotNull(endPoint);
        assertEquals(EndPointType.LANGUAGE, endPoint.getType());
        assertEquals(Language.RUSSIAN, endPoint.getTarget());
        assertNotNull(endPoint.getQualifiers());
        assertTrue(endPoint.getQualifiers().isEmpty());
        assertEquals("rus", endPoint.toString());
    }

    @Test
    public void scriptOnly() {
        String key = "latin";
        EndPoint endPoint = new EndPoint(key);
        assertNotNull(endPoint);
        assertEquals(EndPointType.SCRIPT, endPoint.getType());
        assertEquals(Script.LATN, endPoint.getTarget());
        assertNotNull(endPoint.getQualifiers());
        assertTrue(endPoint.getQualifiers().isEmpty());
        assertEquals("latn", endPoint.toString());
    }

    @Test
    public void written() {
        String key = "en_latin_bronx";
        EndPoint endPoint = new EndPoint(key);
        assertNotNull(endPoint);
        assertEquals(EndPointType.WRITTEN, endPoint.getType());
        assertEquals(new WrittenLanguage(Language.ENGLISH, Script.LATN), endPoint.getTarget());
        assertEquals(Bags.hashSet("bronx"), endPoint.getQualifiers());
        assertEquals("eng_latn_bronx", endPoint.toString());
        key = "heb_Latn";
        endPoint = new EndPoint(key);
        assertEquals(new WrittenLanguage(Language.HEBREW, Script.LATN), endPoint.getTarget());
    }

    @Test
    public void other() {
        String key = "Accents";
        EndPoint endPoint = new EndPoint(key);
        assertNotNull(endPoint);
        assertEquals("Accents", endPoint.getTarget());
        assertNotNull(endPoint.getQualifiers());
        assertTrue(endPoint.getQualifiers().isEmpty());
        assertEquals("Accents", endPoint.toString());
    }

    @Test
    public void sortOrder() {
        List<String> icuIds = new ArrayList<>(Collections.list(com.ibm.icu.text.Transliterator.getAvailableIDs()));
        icuIds.addAll(Arrays.asList("latn-jpan", "any-latn", "fonipa-fonipa",
                "latn_fonipa-any", "fonipa_large-any", "any_blue-any"));
        List<EndPoint> endPoints = icuIds.stream()
                .map(TransformKey::new)
                .map(TransformKey::getSource)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        Set<String> debugStrings = new LinkedHashSet<>();
        endPoints.stream()
                .map(endPoint -> endPoint.getType().name().toLowerCase() +
                        ((endPoint.getQualifiers().isEmpty()) ? "" : "#"))
                .forEach(debugStrings::add);
        assertEquals(Arrays.asList(
                "written", "written#", "language", "language#",
                "script", "script#", "other", "other#",
                "any", "any#"),
                new ArrayList<>(debugStrings));
    }
}