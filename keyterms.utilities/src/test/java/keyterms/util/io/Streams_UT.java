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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class Streams_UT {

    private static final Random RANDOM = new Random();

    @Test
    public void read()
            throws Exception {
        byte[] helloWorldBytes = "Hello World".getBytes();
        // Request fewer bytes than is available.
        InputStream inputStream = new ByteArrayInputStream(helloWorldBytes);
        byte[] fromStream = Streams.read(inputStream, 0);
        assertArrayEquals(new byte[0], fromStream);
        // Request fewer bytes than is available.
        inputStream = new ByteArrayInputStream(helloWorldBytes);
        fromStream = Streams.read(inputStream, 5);
        byte[] expected = new byte[5];
        System.arraycopy(helloWorldBytes, 0, expected, 0, 5);
        assertArrayEquals(expected, fromStream);
        // Request a larger max than is available.
        inputStream = new ByteArrayInputStream(helloWorldBytes);
        fromStream = Streams.read(inputStream, 1000);
        assertArrayEquals(helloWorldBytes, fromStream);
    }

    @Test
    public void readFully()
            throws Exception {
        byte[] helloWorldBytes = "Hello World".getBytes();
        InputStream inputStream = new ByteArrayInputStream(helloWorldBytes);
        byte[] fromStream = Streams.readFully(inputStream);
        assertArrayEquals(helloWorldBytes, fromStream);
    }

    @Test
    public void md5()
            throws Exception {
        byte[] md5_1 = getRandomMD5();
        assertNotNull(md5_1);
        assertNotEquals(0, md5_1.length);
        byte[] md5_2 = getRandomMD5();
        assertNotNull(md5_2);
        assertNotEquals(0, md5_2.length);
        assertFalse(Arrays.equals(md5_1, md5_2));
    }

    private byte[] getRandomMD5()
            throws Exception {
        StringBuilder text = new StringBuilder();
        int randomOverage = RANDOM.nextInt(Streams.COPY_BUFFER_SIZE);
        while (text.length() < ((Streams.COPY_BUFFER_SIZE * 2) + randomOverage)) {
            text.append((char)('a' + RANDOM.nextInt(26)));
        }
        byte[] data = StandardCharsets.UTF_8.encode(text.toString()).array();
        return Streams.getMD5(new ByteArrayInputStream(data));
    }
}