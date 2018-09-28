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

import java.util.LinkedHashSet;
import java.util.Objects;

import org.junit.Test;

import keyterms.testing.Tests;
import keyterms.util.collect.Bags;
import keyterms.util.text.parser.Parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SettingFactory_UT {

    @Test
    public void construction() {
        Setting<Integer> setting = new SettingFactory<>("test.setting", Integer.class)
                .withParser(Parsers.INTEGERS)
                .withParsingErrors()
                .withFormatter((v) -> "t")
                .withResolver(new RangeResolver<>(0, 100))
                .ignoring(Objects::isNull)
                .rejecting((v) -> v == 50)
                .withMultipleValues()
                .required()
                .withDefault(50)
                .build();
        assertNotNull(setting);
        assertEquals(50, (int)setting.getValue());
        Tests.testError(IllegalArgumentException.class, () -> setting.fromText("text"));
        Tests.testError(IllegalArgumentException.class, () -> setting.fromText("50"));
        setting.setValue(25);
        setting.setValue(75);
        assertEquals(new LinkedHashSet<>(Bags.arrayList(25, 75)), setting.getValues());
        assertTrue(setting.isRequired());
        assertEquals("[\n    t,\n    t\n]", setting.asText());
    }
}