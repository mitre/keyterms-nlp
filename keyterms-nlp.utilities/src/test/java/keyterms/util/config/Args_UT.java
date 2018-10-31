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

package keyterms.util.config;

import java.util.List;

import org.junit.Test;

import keyterms.util.collect.Bags;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.Parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Args_UT {

    private final List<String> commandLineArgs = Bags.arrayList(
            "flag=on", "int=5", "float=2.3", "enum=one", "blank=",
            "list=testing", "list=one", "list=two", "one=1",
            "file1", "file2", "file3");

    private final Args args = new Args(commandLineArgs.toArray(new String[0]));

    @Test
    public void contains() {
        assertTrue(args.contains("flag"));
        assertTrue(args.contains("blank"));
    }

    @Test
    public void isSpecified() {
        assertTrue(args.isSpecified("flag"));
        assertFalse(args.isSpecified("blank"));
    }

    @Test
    public void isValid() {
        assertFalse(args.isValid("file1", Strings::toString));
        assertFalse(args.isValid("float", Parsers.INTEGERS));
        assertTrue(args.isValid("float", Parsers.DOUBLES));
    }

    @Test
    public void getText() {
        assertEquals("one", args.getText("enum").orElse(null));
    }

    @Test
    public void getBoolean() {
        Boolean arg = args.getBoolean("flag").orElse(null);
        assertNotNull(arg);
        assertTrue(arg);
    }

    @Test
    public void getInteger() {
        Integer arg = args.getInteger("int").orElse(null);
        assertNotNull(arg);
        assertEquals(5, (int)arg);
    }

    @Test
    public void getDouble() {
        Double arg = args.getDouble("float").orElse(null);
        assertNotNull(arg);
        assertEquals(2.3, arg, 0.0);
    }

    @Test
    public void getEnum() {
        assertEquals(TestEnum.ONE, args.getEnum("enum", TestEnum.class).orElse(null));

    }

    @Test
    public void getTextValues() {
        assertEquals(Bags.arrayList("testing", "one", "two"), args.getTextValues("list"));
    }

    @Test
    public void getNonKeyedTextValues() {
        assertEquals(Bags.arrayList("file1", "file2", "file3"), args.getNonKeyedTextValues());
    }

    enum TestEnum {
        ONE,
        TWO,
        THREE
    }
}