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

package keyterms.util.collect;

import java.nio.CharBuffer;
import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Sequences_UT {

    @Test
    public void checkSequence() {
        Sequences.checkSequence(null);
        Sequences.checkSequence("");
        Sequences.checkSequence(new char[3]);
        Sequences.checkSequence(new ArrayList());
    }

    @Test
    public void length() {
        assertEquals(-1, Sequences.length(null));
        assertEquals(0, Sequences.length(new Object[0]));
        assertEquals(12, Sequences.length("Hello World!"));
        assertEquals(12, Sequences.length("Hello World!".toCharArray()));
        assertEquals(12, Sequences.length(CharBuffer.wrap("Hello World!")));
        assertEquals(12, Sequences.length(Bags.arrayList('H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!')));
    }

    @Test
    public void get() {
        assertNull(Sequences.get(null, 0));
        assertEquals('o', Sequences.get("Hello World!", 4));
        assertEquals('o', Sequences.get("Hello World!".toCharArray(), 4));
        assertEquals('o', Sequences.get(CharBuffer.wrap("Hello World!"), 4));
        assertEquals('o', Sequences.get(Bags.arrayList('H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!'), 4));
        assertNull(Sequences.get("Hello World!", -1));
        assertNull(Sequences.get("Hello World!".toCharArray(), -1));
        assertNull(Sequences.get(Bags.arrayList('H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!'), -1));
    }
}