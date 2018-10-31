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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;

import org.junit.Test;

import keyterms.testing.Tests;
import keyterms.util.collect.Bags;
import keyterms.util.text.parser.ReflectiveParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Setting_UT {

    @Test
    public void defaultParser() {
        Tests.testError(NullPointerException.class, () -> Setting.getDefaultParser(null));
        assertFalse(Setting.getDefaultParser(Boolean.class) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Boolean.TYPE) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Integer.class) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Integer.TYPE) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Long.class) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Long.TYPE) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Float.class) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Float.TYPE) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Double.class) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(Double.TYPE) instanceof ReflectiveParser);
        assertFalse(Setting.getDefaultParser(String.class) instanceof ReflectiveParser);
    }

    @Test
    public void defaultValues() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .validate();
        assertFalse(setting.isValueSet());
        assertFalse(setting.isDefaultSet());
        assertNull(setting.getValue());
        assertEquals(Collections.emptySet(), setting.getValues());
        setting = setting.withDefault(100).withDefault(50);
        assertFalse(setting.isValueSet());
        assertTrue(setting.isDefaultSet());
        assertEquals(50, (int)setting.getValue());
        assertEquals(Collections.singleton(50), setting.getValues());
        setting = setting.withMultipleValues();
        assertEquals(50, (int)setting.getValue());
        assertEquals(new LinkedHashSet<>(Bags.arrayList(100, 50)), setting.getValues());
    }

    @Test
    public void basicSet() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .withDefault(100)
                .withDefault(50)
                .validate();
        setting.setValue(0);
        setting.setValue(25);
        assertTrue(setting.isValueSet());
        assertEquals(25, (int)setting.getValue());
        assertEquals(Collections.singleton(25), setting.getValues());
        setting = setting.withMultipleValues();
        setting.setValue(0);
        setting.setValue(25);
        assertEquals(25, (int)setting.getValue());
        assertEquals(new LinkedHashSet<>(Bags.arrayList(0, 25)), setting.getValues());
    }

    @Test
    public void setToDefaults() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .withDefault(50)
                .validate();
        assertFalse(setting.isValueSet());
        assertEquals(50, (int)setting.getValue());
        setting.setValue(50);
        assertTrue(setting.isValueSet());
        assertEquals(50, (int)setting.getValue());
    }

    @Test
    public void resolving() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .withResolver(new RangeResolver<>(5, 95))
                .validate();
        // In range.
        setting.setValue(42);
        assertTrue(setting.isValueSet());
        assertEquals(42, (int)setting.getValue());
        // Out of range low.
        setting.setValue(0);
        assertTrue(setting.isValueSet());
        assertEquals(5, (int)setting.getValue());
        // Out of range high.
        setting.setValue(100);
        assertEquals(95, (int)setting.getValue());
    }

    @Test
    public void ignoreConditions() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .ignoring(Objects::isNull)
                .withDefault(42)
                .validate();
        setting.setValue(null);
        assertEquals(42, (int)setting.getValue());

    }

    @Test
    public void rejectConditions() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .rejecting(Objects::isNull)
                .validate();
        Tests.testError(IllegalArgumentException.class, ".*Invalid value: .*", () -> setting.setValue(null));
    }

    @Test
    public void parser() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .ignoring(Objects::isNull)
                .validate();
        setting.fromText(null);
        assertFalse(setting.isValueSet());
        setting.fromText("");
        assertFalse(setting.isValueSet());
        setting.fromText("25");
        assertTrue(setting.isValueSet());
        assertEquals(25, (int)setting.getValue());
    }

    @Test
    public void parserIgnoreError() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .validate();
        setting.fromText("test");
        assertFalse(setting.isValueSet());
    }

    @Test
    public void parserWithError() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .withParsingErrors()
                .validate();
        Tests.testError(IllegalArgumentException.class, "Invalid value text: .*", () -> setting.fromText("test"));
    }

    @Test
    public void formatter() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .validate();
        setting.setValue(50);
        assertEquals("50", setting.asText());
        setting = setting.withMultipleValues();
        setting.setValue(50);
        setting.setValue(100);
        assertEquals("[\n    50,\n    100\n]", setting.asText());
    }

    @Test
    public void escapes() {
        Setting<String> setting = new Setting<>("test.setting", String.class)
                .validate();
        setting.setValue("[#\\]");
        assertEquals("\\[\\#\\\\\\]", setting.asText());
    }

    @Test
    public void emptyList() {
        Setting<String> test = new Setting<>("empty.list", String.class)
                .withMultipleValues();
        assertEquals("[]", test.asText());
    }

    @Test
    public void reset() {
        Setting<Integer> setting = new Setting<>("test.setting", Integer.class)
                .withDefault(50)
                .validate();
        // Initial state.
        assertFalse(setting.isValueSet());
        assertEquals(50, (int)setting.getValue());
        setting.setValue(42);
        assertTrue(setting.isValueSet());
        assertEquals(42, (int)setting.getValue());
        // Reset.
        setting.reset();
        assertFalse(setting.isValueSet());
        assertEquals(50, (int)setting.getValue());
    }

    @Test
    public void validation() {
        // Note: This test requires visual validation of the warning logs.
        new Setting<>("test.setting", Integer.class).validate();
        new Setting<>("test.setting", Integer.class).withDefault(50).required().validate();
        new Setting<>("test.setting", Integer.class).withDefault(25).withDefault(50).validate();

    }
}