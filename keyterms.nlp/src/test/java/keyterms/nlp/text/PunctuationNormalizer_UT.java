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

package keyterms.nlp.text;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PunctuationNormalizer_UT {

    PunctuationNormalizer normalizer;

    @Before
    public void init() {
        normalizer = new PunctuationNormalizer();
    }

    @Test
    public void PunctNorm_01() {
        String input = "✨";
        String expected = "*";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_02() {
        String input = "❕";
        String expected = "!";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_03() {
        String input = "\u2789";
        String expected = "•10";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_04() {
        String input = "\u0002\u2789\u0002";
        String expected = "•10";
        String actual = normalizer.normalize(input, true, true);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_05() {
        String input = "\u0002\u2789\u0002";
        String expected = "\u0002•10\u0002";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_06() {
        String input = "قــــــبال";
        String expected = "قبال";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_07() {
        String input = "〰";
        String expected = "-";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_08() {
        String input = "⸵";
        String expected = ";";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_09() {
        String input = " \t";
        String expected = "     ";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void PunctNorm_10() {
        String input = "｟";
        String expected = "(";
        String actual = normalizer.normalize(input, true, false);
        assertEquals(expected, actual);
    }
}