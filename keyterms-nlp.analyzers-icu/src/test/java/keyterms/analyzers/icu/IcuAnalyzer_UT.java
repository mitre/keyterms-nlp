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

package keyterms.analyzers.icu;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.text.TextInfo;
import keyterms.testing.TestData;
import keyterms.util.collect.Bags;
import keyterms.util.io.Encoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class IcuAnalyzer_UT {

    private static IcuAnalyzer analyzer;

    @BeforeClass
    public static void setupAnalyzer() {
        analyzer = new IcuAnalyzer();
    }

    @AfterClass
    public static void disposeAnalyzer() {
        analyzer.dispose();
    }

    @Test
    public void analysis() {
        Charset encoding = Encoding.UTF32;
        TestData.LANGUAGE_PHRASES.forEach((key, phrase) -> {
            byte[] data = Encoding.encode(phrase, encoding);
            List<Analysis> results = analyzer.analyze(data);
            assertNotNull(phrase, results);
            assertNotEquals(phrase, 0, results.size());
            TextInfo best = TextInfo.of(results.get(0));
            assertNotNull(phrase, best.getEncoding());
            assertEquals(phrase, "utf-32be", best.getEncoding());
            assertNotNull(phrase, best.getLength());
            assertEquals(phrase, phrase.length(), (int)best.getLength());
        });
    }

    @Test
    public void commonEncodings() {
        AtomicInteger failures = new AtomicInteger(0);
        Bags.staticList(Encoding.ASCII, Encoding.UTF8, Encoding.UTF16, Encoding.UTF16LE, Encoding.UTF32,
                Encoding.UTF32LE)
                .forEach(encoding -> {
                    try {
                        byte[] data = TestData.LOREM_IPSUM.getBytes(encoding.name());
                        List<Analysis> results = analyzer.analyze(data);
                        assertNotNull(results);
                        assertNotEquals(0, results.size());
                        for (Analysis result : results) {
                            TextInfo textInfo = TextInfo.of(result);
                            assertNotNull(encoding.name(), textInfo.getEncoding());
                            assertNotNull(encoding.name(), textInfo.getLength());
                            assertNotEquals(encoding.name(), 0, (int)textInfo.getLength());
                        }
                    } catch (Exception error) {
                        failures.incrementAndGet();
                    }
                });
        assertEquals(0, failures.get());
    }
}