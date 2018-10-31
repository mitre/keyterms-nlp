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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class Enums_UT {

    @Test
    public void valueOf() {
        assertNull(Enums.valueOf(TestEnum.class, null));
        assertEquals(TestEnum.VALUE_1, Enums.valueOf(TestEnum.class, null, TestEnum.VALUE_1));
        // wrong case
        assertNull(Enums.valueOf(TestEnum.class, "value_1"));
        assertNull(Enums.valueOf(TestEnum.class, "value_2"));
        // extra whitespace
        assertNull(Enums.valueOf(TestEnum.class, " VALUE_1 "));
        assertNull(Enums.valueOf(TestEnum.class, " VALUE_2 "));
        // good values
        assertSame(TestEnum.VALUE_1, Enums.valueOf(TestEnum.class, "VALUE_1"));
        assertSame(TestEnum.VALUE_2, Enums.valueOf(TestEnum.class, "VALUE_2"));
        // defaults
        assertSame(TestEnum.VALUE_1, Enums.valueOf(TestEnum.class, "VALUE_1", TestEnum.VALUE_2));
        assertSame(TestEnum.VALUE_2, Enums.valueOf(TestEnum.class, "VALUE_2", TestEnum.VALUE_1));
        assertSame(TestEnum.VALUE_1, Enums.valueOf(TestEnum.class, "value_2", TestEnum.VALUE_1));
        assertSame(TestEnum.VALUE_2, Enums.valueOf(TestEnum.class, "value_1", TestEnum.VALUE_2));
    }

    @Test
    public void find() {
        assertNull(Enums.find(TestEnum.class, null));
        assertEquals(TestEnum.VALUE_1, Enums.find(TestEnum.class, null, TestEnum.VALUE_1));
        // good values
        assertSame(TestEnum.VALUE_1, Enums.find(TestEnum.class, "VALUE_1"));
        assertSame(TestEnum.VALUE_2, Enums.find(TestEnum.class, "VALUE_2"));
        // wrong case
        assertSame(TestEnum.VALUE_1, Enums.find(TestEnum.class, "value_1"));
        assertSame(TestEnum.VALUE_2, Enums.find(TestEnum.class, "value_2"));
        // extra whitespace,
        assertSame(TestEnum.VALUE_1, Enums.find(TestEnum.class, " VALUE_1 "));
        assertSame(TestEnum.VALUE_2, Enums.find(TestEnum.class, " Value_2 "));
        // defaults
        assertSame(TestEnum.VALUE_1, Enums.find(TestEnum.class, "value_1", TestEnum.VALUE_2));
        assertSame(TestEnum.VALUE_2, Enums.find(TestEnum.class, "value_2", TestEnum.VALUE_1));
        assertSame(TestEnum.VALUE_1, Enums.find(TestEnum.class, "v_2", TestEnum.VALUE_1));
        assertSame(TestEnum.VALUE_2, Enums.find(TestEnum.class, "v_1", TestEnum.VALUE_2));
    }

    private enum TestEnum {
        VALUE_1,
        VALUE_2
    }
}