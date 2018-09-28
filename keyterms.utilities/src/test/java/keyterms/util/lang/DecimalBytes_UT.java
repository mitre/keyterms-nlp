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

package keyterms.util.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DecimalBytes_UT {

    @Test
    public void noFormatting() {
        DecimalBytes decimal = new DecimalBytes(1_500);
        assertEquals(DecimalBytes.Units.KILO, decimal.getUnits());
        assertEquals(1.5, decimal.getValue(), 0);
    }

    @Test
    public void zero() {
        DecimalBytes bytes = new DecimalBytes(0);
        assertEquals(DecimalBytes.Units.BYTE, bytes.getUnits());
        assertEquals(0, bytes.getValue(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negative() {
        new BinaryBytes(-1_500);
    }

    @Test
    public void decimalConversion() {
        assertEquals("0 b", new DecimalBytes(0).summary(0));
        assertEquals("1.5 kb", new DecimalBytes(1_500).summary(1));
        assertEquals("2 kb", new DecimalBytes(2_000).summary(1));
        assertEquals("1.5 kb", new DecimalBytes(1_500).summary(1));
        assertEquals("2 kb", new DecimalBytes(2_000).summary(1));
        assertEquals("2.751 kb", new DecimalBytes(2_751).summary(3));
        assertEquals("1.5000 kb", new DecimalBytes(1_500).summary(4));
        assertEquals("2.0000 kb", new DecimalBytes(2_000).formattedSummary(4));
    }
}