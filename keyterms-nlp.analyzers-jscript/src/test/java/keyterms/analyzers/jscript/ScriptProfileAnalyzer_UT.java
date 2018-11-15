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

package keyterms.analyzers.jscript;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.text.TextInfo;
import keyterms.testing.TestData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ScriptProfileAnalyzer_UT {

    private static ScriptProfileAnalyzer analyzer;

    @BeforeClass
    public static void setupAnalyzer() {
        analyzer = new ScriptProfileAnalyzer();
    }

    @AfterClass
    public static void disposeAnalyzer() {
        analyzer.dispose();
    }

    @Test
    public void analysis() {
        TestData.LANGUAGE_PHRASES.forEach((key, phrase) -> {
            List<Analysis> results = analyzer.analyze(phrase);
            assertNotNull(phrase, results);
            assertNotEquals(phrase, 0, results.size());
            TextInfo best = TextInfo.of(results.get(0));
            assertNotNull(phrase, best.getScript());
            String expectedCode = key.split("-")[1].toLowerCase();
            String actualCode = best.getScript().getCode().toLowerCase();
            assertEquals(phrase, expectedCode, actualCode);
        });
    }
}