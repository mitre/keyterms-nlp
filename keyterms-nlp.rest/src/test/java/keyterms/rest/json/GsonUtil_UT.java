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

package keyterms.rest.json;

import java.net.URI;

import org.junit.Test;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.io.Binary;
import keyterms.util.text.Strings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GsonUtil_UT {

    private static <O> O testCopy(Class<O> objectClass, O object) {
        String json = GsonUtil.toJson(object);
        assertNotNull(json);
        assertFalse(Strings.isBlank(json));
        O copy = GsonUtil.toObject(objectClass, json);
        if (object != null) {
            assertNotNull(copy);
            assertEquals(object, copy);
        } else {
            assertNull(copy);
        }
        return copy;
    }

    @Test
    public void nullValue() {
        testCopy(Object.class, null);
    }

    @Test
    public void string() {
        testCopy(String.class, null);
        testCopy(String.class, "Hello World!");
    }

    @Test
    public void writtenLanguage() {
        String json = "{\"language\":null,\"script\":null}";
        assertEquals(new WrittenLanguage(null, null), GsonUtil.toObject(WrittenLanguage.class, json));
        json = "{\"language\":{\"code\":\"rus\"},\"script\":null}";
        assertEquals(new WrittenLanguage(Language.RUSSIAN, null), GsonUtil.toObject(WrittenLanguage.class, json));
        json = "{\"script\":null,\"language\":{\"code\":\"rus\"}}";
        assertEquals(new WrittenLanguage(Language.RUSSIAN, null), GsonUtil.toObject(WrittenLanguage.class, json));
    }

    @Test
    public void primitives() {
        testCopy(Integer.class, 5);
    }

    @Test
    public void uri() {
        URI uri = URI.create("http://www.google.com");
        testCopy(URI.class, uri);
    }

    @Test
    public void binary() {
        Binary binary = new Binary(new byte[] { 1, 2, 3 });
        Binary copy = testCopy(Binary.class, binary);
        assertEquals(binary, copy);
        assertEquals("{\"data\":\"010203\"}", GsonUtil.toJson(binary));
        binary = new Binary(new byte[] { -1, -2, -3 });
        copy = testCopy(Binary.class, binary);
        assertEquals(binary, copy);
        assertEquals("{\"data\":\"fffefd\"}", GsonUtil.toJson(binary));
    }

    @Test
    public void transientFields() {
        TestClass testClass = new TestClass();
        String json = GsonUtil.toJson(testClass);
        assertEquals("{\"field\":\"field1\",\"camel_case_field\":\"field2\"}", json);
        testClass.field = "woo";
        testClass.camelCaseField = "hoo";
        json = GsonUtil.toJson(testClass);
        TestClass copy = GsonUtil.toObject(TestClass.class, json);
        assertNotNull(copy);
        assertEquals("woo", copy.field);
        assertEquals("hoo", copy.camelCaseField);
        assertNull(copy.transientField);
        assertEquals("transient final", copy.transientFinalField);
    }

    private class TestClass {
        String field = "field1";
        String camelCaseField = "field2";
        transient String transientField = "transient";
        transient final String transientFinalField = "transient final";
    }
}