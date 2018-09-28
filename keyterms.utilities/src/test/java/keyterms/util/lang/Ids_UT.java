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

import java.util.UUID;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Ids_UT {

    @Test
    public void isUUID() {
        for (int t = 0; t < 10; t++) {
            UUID randomUUID = UUID.randomUUID();
            String text = Ids.toString(randomUUID);
            assertTrue(Ids.isUUID(text));
            assertEquals(randomUUID, Ids.valueOf(text));
        }
    }

    @Test
    public void isCompactUUID() {
        for (int t = 0; t < 10; t++) {
            UUID randomUUID = UUID.randomUUID();
            String text = Ids.toCompactString(randomUUID);
            assertTrue(Ids.isCompactUUID(text));
            assertEquals(randomUUID, Ids.valueOf(text));
        }
    }

    @Test
    public void textHash() {
        String text = "corned beef";
        String expectedHash = "e5101f8b-d41f-3570-9240-917384859709";
        UUID expectedId = UUID.fromString(expectedHash);
        assertEquals("corned beef hash", expectedId, Ids.hashText(text));
    }

    @Test
    public void nullTextSameAsBlank() {
        assertEquals(Ids.hashText(null), Ids.hashText(""));
    }

    @Test
    public void noTrimming() {
        assertNotEquals(Ids.hashText(" "), Ids.hashText("  "));
    }

    @Test
    public void debugString() {
        assertEquals("null", Ids.debug(null));
        for (int t = 0; t < 10; t++) {
            UUID randomUUID = UUID.randomUUID();
            String text = Ids.debug(randomUUID);
            assertEquals(text, 11, text.length());
        }
    }
}