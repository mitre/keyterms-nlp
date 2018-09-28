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

package keyterms.util.text;

import java.util.Arrays;
import java.util.regex.Matcher;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Strings_UT {

    @BeforeClass
    public static void setTestProperties() {
        System.setProperty("test.prop.1", "Hello");
        System.setProperty("test.prop.2", "World");
        System.setProperty("test.prop.3", "!");
    }

    @Test
    public void lineBreakPattern() {
        String text = "This \r is \n broken \r\n text \n\r\n !";
        int count = 0;
        Matcher matcher = Strings.LINE_BREAK.matcher(text);
        while (matcher.find()) {
            count++;
        }
        assertEquals(5, count);
    }

    @Test
    public void lineBreaksPattern() {
        String text = "This \r is \n broken \r\n text \n\r\n !";
        int count = 0;
        Matcher matcher = Strings.LINE_BREAKS.matcher(text);
        while (matcher.find()) {
            count++;
        }
        assertEquals(4, count);
    }

    @Test
    public void testToString() {
        assertNull(Strings.toString(null));
        for (Object test : Arrays.asList('a', 10, 23.4, "Jello")) {
            assertEquals(String.valueOf(test), Strings.toString(test));
        }
    }

    @Test
    public void length() {
        assertEquals(-1, Strings.length(null));
        assertEquals(0, Strings.length(""));
        assertEquals(1, Strings.length(" "));
    }

    @Test
    public void isEmpty() {
        assertTrue(Strings.isEmpty(null));
        assertTrue(Strings.isEmpty(""));
        assertFalse(Strings.isEmpty(" "));
    }

    @Test
    public void isBlank() {
        assertTrue(Strings.isBlank(null));
        assertTrue(Strings.isBlank(""));
        assertTrue(Strings.isBlank(" "));
    }

    @Test
    public void trim() {
        assertNull(Strings.trim(null));
        assertEquals("", Strings.trim(""));
        assertEquals("", Strings.trim(" "));
    }

    @Test
    public void snakeCase() {
        assertNull(Strings.toSnakeCase(null));
        assertEquals("", Strings.toSnakeCase(""));
        assertEquals("hello_world", Strings.toSnakeCase("HelloWorld"));
        assertEquals("my_url_thing", Strings.toSnakeCase("MyURLThing"));
        assertEquals("my_42_thing", Strings.toSnakeCase("My42Thing"));
        assertEquals("my_42_thing_b", Strings.toSnakeCase("My42ThingB"));
        assertEquals("my_42_thing", Strings.toSnakeCase("My_42_Thing"));
        assertEquals("my_42_thing", Strings.toSnakeCase("My 42 thing"));
        assertEquals("hello_world", Strings.toSnakeCase("_Hello__World_"));
    }

    @Test
    public void replaceSystemProperties() {
        assertNull(Strings.replaceSystemTokens(null));
        assertEquals("", Strings.replaceSystemTokens(""));
        assertEquals(" ", Strings.replaceSystemTokens(" "));
        assertEquals("test.prop.1", Strings.replaceSystemTokens("test.prop.1"));
        assertEquals("Hello", Strings.replaceSystemTokens("${test.prop.1}"));
        assertEquals("  Hello  ", Strings.replaceSystemTokens("  ${test.prop.1}  "));
        assertEquals("Hello World", Strings.replaceSystemTokens("Hello ${test.prop.2}"));
        assertEquals("Hello World!", Strings.replaceSystemTokens("${test.prop.1} ${test.prop.2}${test.prop.3}"));
    }
}