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

package keyterms.util.io;

import java.util.Random;

import org.junit.Test;

import keyterms.testing.Tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class Binary_UT {

    @Test
    public void leftPadding() {
        byte[] bytes = Binary.fromHex("f");
        assertNotNull(bytes);
        assertEquals(1, bytes.length);
        assertEquals(15, bytes[0]);
    }

    @Test
    public void badCharacters() {
        Tests.testError(NumberFormatException.class, () -> Binary.fromHex("gg"));
    }

    @Test
    public void fromHex() {
        assertNull(Binary.fromHex(null));
        assertArrayEquals(new byte[0], Binary.fromHex(""));
        assertArrayEquals(new byte[] { (byte)0xaa, (byte)0xff }, Binary.fromHex("AAff"));
    }

    @Test
    public void toHex() {
        assertNull(Binary.toHex(null));
        assertEquals("", Binary.toHex(new byte[0]));
        assertEquals("af", Binary.toHex(new byte[] { (byte)0xaf }));
        for (int i = -127; i <= 128; i++) {
            byte b = (byte)i;
            String hex = Binary.toHex(new byte[] { b });
            byte b1 = Binary.fromHex(hex)[0];
            assertEquals(b, b1);
        }
    }

    @Test
    public void hexConversions() {
        String hex = "a0582fb4";
        byte[] bytes = Binary.fromHex(hex);
        byte[] expectedBytes = { (byte)0xa0, (byte)0x58, (byte)0x2f, (byte)0xb4 };
        assertArrayEquals(expectedBytes, bytes);
        String hex2 = Binary.toHex(bytes);
        assertEquals(hex, hex2);
    }

    @Test
    public void allHexPairs() {
        for (int h = 0; h < 16; h++) {
            for (int l = 0; l < 16; l++) {
                String hex = String.valueOf(Integer.toHexString(h).charAt(0))
                        + Integer.toHexString(l).charAt(0);
                byte[] bytes = Binary.fromHex(hex);
                assertEquals(1, bytes.length);
                assertEquals((byte)((h * 16) + l), bytes[0]);
                String hex2 = Binary.toHex(bytes);
                assertEquals(hex, hex2);
            }
        }
    }

    @Test
    public void randomConversions() {
        Random random = new Random();
        for (int t = 0; t < 1000; t++) {
            int size = 10 + random.nextInt(11);
            if ((size % 2) != 0) {
                size++;
            }
            StringBuilder sb = new StringBuilder();
            for (int s = 0; s < size; s++) {
                sb.append(Integer.toHexString(random.nextInt(16)).charAt(0));
            }
            String hex = sb.toString();
            byte[] bytes = Binary.fromHex(hex);
            String hex2 = Binary.toHex(bytes);
            assertEquals(hex, hex2);
        }
    }
}