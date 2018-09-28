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

package keyterms.util.text.parser;

import org.junit.Test;

import keyterms.util.collect.Keyed;
import keyterms.util.lang.DecimalBytes;
import keyterms.util.text.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Parsers_UT {

    @Test
    public void booleanValues() {
        Parser<Boolean> parser = Parsers::parseBoolean;
        for (String trueValue : Parsers.TRUE_VALUES) {
            assertTrue(parser.parse(trueValue, null));
            assertTrue(parser.parse(trueValue.toUpperCase(), null));
        }
        for (String falseValue : Parsers.FALSE_VALUES) {
            assertFalse(parser.parse(falseValue, null));
            assertFalse(parser.parse(falseValue.toUpperCase(), null));
        }
        assertNull(parser.parse("z", null));
    }

    @Test
    public void integerValues() {
        Parser<Integer> parser = Parsers::parseInteger;
        assertEquals(123, (int)parser.parse(null, 123));
        assertEquals(123, (int)parser.parse("123", 0));
        assertEquals(123, (int)parser.parse("nan", 123));
        assertEquals(123_000, (int)parser.parse("123,000", 0));
        assertEquals(123_000, (int)parser.parse("123_000", 0));
        assertNull(parser.parse("z", null));
        assertNull(parser.parse(String.valueOf((long)Integer.MAX_VALUE + 1L), null));
        assertNull(parser.parse(String.valueOf((long)Integer.MIN_VALUE - 1L), null));
    }

    @Test
    public void longValues() {
        Parser<Long> parser = Parsers::parseLong;
        assertEquals(123, (long)parser.parse(null, 123L));
        assertEquals(123, (long)parser.parse("123", 0L));
        assertEquals(123, (long)parser.parse("nan", 123L));
        assertEquals(123_000, (long)parser.parse("123,000", 0L));
        assertEquals(123_000, (long)parser.parse("123_000", 0L));
        assertNull(parser.parse("z", null));
    }

    @Test
    public void floatValues() {
        Parser<Float> parser = Parsers::parseFloat;
        assertEquals(1.2f, parser.parse(null, 1.2f), 0);
        assertEquals(2.1f, parser.parse("2.1", 1.2f), 0);
        assertEquals(1.2f, parser.parse("nan", 1.2f), 0);
        assertEquals(123_000.123f, parser.parse("123,000.123", 0.0f), 0);
        assertEquals(123_000.123f, parser.parse("123_000.123", 0.0f), 0);
    }

    @Test
    public void doubleValues() {
        Parser<Double> parser = Parsers::parseDouble;
        assertEquals(1.2, parser.parse(null, 1.2), 0);
        assertEquals(2.1, parser.parse("2.1", 1.2), 0);
        assertEquals(1.2, parser.parse("nan", 1.2), 0);
        assertEquals(123_000.123, parser.parse("123,000.123", 0.0), 0);
        assertEquals(123_000.123, parser.parse("123_000.123", 0.0), 0);
    }

    @Test
    public void keyedValues() {
        assertNull(Parsers.KEYS.parse(null));
        testKeyed(Parsers.KEYS.parse(""), "", null);
        testKeyed(Parsers.KEYS.parse("  "), "", null);
        testKeyed(Parsers.KEYS.parse("="), "", "");
        testKeyed(Parsers.KEYS.parse("  =  "), "", "  ");
        testKeyed(Parsers.KEYS.parse("k=v"), "k", "v");
        testKeyed(Parsers.KEYS.parse(" k  =  v"), "k", "  v");
        testKeyed(Parsers.KEYS.parse(" k  =  v=1"), "k", "  v=1");
        testKeyed(Parsers.parseKeyed("host", ":"), "host", null);
        testKeyed(Parsers.parseKeyed("host:port", ":"), "host", "port");
    }

    private void testKeyed(Keyed<String, String> keyed, String expectedKey, String expectedValue) {
        assertNotNull(keyed);
        if (expectedKey != null) {
            assertEquals(expectedKey, keyed.getKey());
        } else {
            assertNull(keyed.getKey());
        }
        if (expectedValue != null) {
            assertEquals(expectedValue, keyed.getValue());
        } else {
            assertNull(keyed.getValue());
        }
    }

    @Test
    public void memory() {
        assertEquals(new DecimalBytes(1_000), Parsers.parseMemory(" 1k"));
        assertEquals(new DecimalBytes(1_000), Parsers.parseMemory("1 k"));
        assertEquals(new DecimalBytes(50), Parsers.parseMemory("50"));
        assertEquals(new DecimalBytes(2_500), Parsers.parseMemory("2.5 k"));
        assertEquals(new DecimalBytes(4_000_000), Parsers.parseMemory("4 M"));
        assertEquals(new DecimalBytes(1_000), Parsers.parseMemory(" 1kb"));
        assertEquals(new DecimalBytes(1_000), Parsers.parseMemory("1 kb"));
        assertEquals(new DecimalBytes(2_500), Parsers.parseMemory("2.5 kb"));
        assertEquals(new DecimalBytes(4_000_000), Parsers.parseMemory("4 mb"));
    }
}