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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.SortedSet;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Encoding_UT {

    @Test
    public void getAvailableCharsets() {
        SortedSet<Charset> available = Encoding.getAvailableCharsets();
        assertNotNull("character sets available", available);
        assertTrue("character sets available", (available.size() > 0));
    }

    @Test
    public void getCharsetByName() {
        assertNull(Encoding.getCharset((CharSequence)null));
        assertNull(Encoding.getCharset("Jello"));
        assertNotNull(Encoding.getCharset("UTF-8"));
    }

    @Test
    public void getCharsetByBOM() {
        assertEquals(Encoding.UTF8, Encoding.getCharset(Encoding.UTF8_BOM));
        assertEquals(Encoding.UTF16, Encoding.getCharset(Encoding.UTF16_BOM));
        assertEquals(Encoding.UTF16LE, Encoding.getCharset(Encoding.UTF16LE_BOM));
        assertEquals(Encoding.UTF32, Encoding.getCharset(Encoding.UTF32_BOM));
        assertEquals(Encoding.UTF32LE, Encoding.getCharset(Encoding.UTF32LE_BOM));
    }

    @Test
    public void getBom() {
        assertArrayEquals(Encoding.UTF8_BOM, Encoding.getBom(Encoding.UTF8));
        assertArrayEquals(Encoding.UTF16_BOM, Encoding.getBom(Encoding.UTF16));
        assertArrayEquals(Encoding.UTF16LE_BOM, Encoding.getBom(Encoding.UTF16LE));
        assertArrayEquals(Encoding.UTF16_BOM, Encoding.getBom(Encoding.UTF16BE));
        assertArrayEquals(Encoding.UTF32_BOM, Encoding.getBom(Encoding.UTF32));
        assertArrayEquals(Encoding.UTF32LE_BOM, Encoding.getBom(Encoding.UTF32LE));
        assertArrayEquals(Encoding.UTF32_BOM, Encoding.getBom(Encoding.UTF32BE));
    }

    @Test
    public void detectBom() {
        assertNull(Encoding.detectBom(null));
        byte[] encoded = Encoding.encode("Hello World!", Encoding.UTF8, true);
        assertEquals(Encoding.UTF8, Encoding.detectBom(encoded));
        encoded = Encoding.encode("Hello World!", Encoding.UTF16, true);
        assertEquals(Encoding.UTF16, Encoding.detectBom(encoded));
        encoded = Encoding.encode("Hello World!", Encoding.UTF16LE, true);
        assertEquals(Encoding.UTF16LE, Encoding.detectBom(encoded));
        encoded = Encoding.encode("Hello World!", Encoding.UTF16BE, true);
        assertEquals(Encoding.UTF16, Encoding.detectBom(encoded));
        encoded = Encoding.encode("Hello World!", Encoding.UTF32, true);
        assertEquals(Encoding.UTF32, Encoding.detectBom(encoded));
        encoded = Encoding.encode("Hello World!", Encoding.UTF32LE, true);
        assertEquals(Encoding.UTF32LE, Encoding.detectBom(encoded));
        encoded = Encoding.encode("Hello World!", Encoding.UTF32BE, true);
        assertEquals(Encoding.UTF32, Encoding.detectBom(encoded));
    }

    @Test
    public void stripBom() {
        byte[] encoded = Encoding.encode("Hello World!", Encoding.UTF8, true);
        String decoded = Encoding.UTF8.decode(ByteBuffer.wrap(encoded)).toString();
        assertNotEquals("Hello World!", decoded);
        String stripped = Encoding.stripBom(decoded, Encoding.UTF8);
        assertEquals("Hello World!", stripped);
    }

    @Test
    public void encodeDecode() {
        assertArrayEquals(new byte[0], Encoding.encode(null, null, false));
        assertArrayEquals(Encoding.UTF8_BOM, Encoding.encode(null, Encoding.UTF8, true));
        String text = "Hello World, Jello Pudding.";
        byte[] encoded = Encoding.encode(text, Encoding.UTF8, false);
        String decoded = Encoding.decode(encoded, Encoding.UTF8);
        assertEquals(text, decoded);
        encoded = Encoding.encode(text, Encoding.UTF8, true);
        decoded = Encoding.decode(encoded, Encoding.UTF8);
        assertEquals(text, decoded);
        encoded = Encoding.encode(text, Encoding.UTF8, true);
        decoded = Encoding.decode(encoded);
        assertEquals(text, decoded);
        encoded = Encoding.encode(text);
        decoded = Encoding.decode(encoded);
        assertEquals(text, decoded);
        encoded = Encoding.encode(text, Encoding.UTF8);
        decoded = Encoding.decode(encoded, Encoding.UTF8);
        assertEquals(text, decoded);
    }
}