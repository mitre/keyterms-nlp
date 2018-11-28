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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;

import keyterms.testing.Tests;
import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;

import static org.junit.Assert.assertEquals;

public class Configuration_UT {

    private final Setting<Integer> setting1 = new Setting<>("setting.1", Integer.class)
            .useSystemProperties()
            .withDefault(1)
            .validate();

    private final Setting<Double> setting2 = new Setting<>("setting.2", Double.class)
            .required()
            .withDefault(2.0)
            .validate();

    private final Setting<String> setting3 = new Setting<>("setting.3", String.class)
            .withMultipleValues()
            .validate();

    @Test
    public void population() {
        Configuration configuration = new Configuration(setting1, setting2, setting3);
        assertEquals(3, configuration.size());
        assertEquals(3, configuration.getSettings().size());
        assertEquals(setting1, configuration.getSetting(setting1.getName()));
        assertEquals(setting2, configuration.getSetting(setting2.getName()));
        assertEquals(setting3, configuration.getSetting(setting3.getName()));
    }

    @Test
    public void configuration() {
        Configuration configuration = new Configuration(setting1, setting2, setting3);
        List<Keyed<String, List<String>>> configValues = new ArrayList<>();
        configValues.add(new Keyed<>("setting.1", Bags.arrayList("42")));
        configValues.add(new Keyed<>("setting.2", Bags.arrayList("4.2")));
        configValues.add(new Keyed<>("setting.3", Bags.arrayList("one", "two", "three")));
        configuration.reconfigure(configValues);
        assertEquals(42, (int)setting1.getValue());
        assertEquals(4.2, setting2.getValue(), 0);
        assertEquals(new LinkedHashSet<>(Bags.arrayList("one", "two", "three")), setting3.getValues());
        System.setProperty("setting.1", "22");
        configuration.reset();
        configuration.configureFromSystem();
        assertEquals(22, (int)setting1.getValue());
    }

    @Test
    public void duplicateKeys() {
        Configuration configuration = new Configuration(setting1, setting2, setting3);
        List<Keyed<String, List<String>>> configValues = new ArrayList<>();
        configValues.add(new Keyed<>("setting.1", Bags.arrayList("42")));
        configValues.add(new Keyed<>("setting.2", Bags.arrayList("4.2")));
        configValues.add(new Keyed<>("setting.3", Bags.arrayList("one", "two", "three")));
        configValues.add(new Keyed<>("setting.3", Bags.arrayList("three", "two", "two")));
        configValues.add(new Keyed<>("setting.2", Bags.arrayList("9.9")));
        configValues.add(new Keyed<>("setting.1", Bags.arrayList("100")));
        configuration.reconfigure(configValues);
        assertEquals(100, (int)setting1.getValue());
        assertEquals(9.9, setting2.getValue(), 0);
        assertEquals(new LinkedHashSet<>(Bags.arrayList("one", "two", "three")), setting3.getValues());
    }

    @Test
    public void missingRequired() {
        Configuration configuration = new Configuration(setting1, setting2, setting3);
        List<Keyed<String, List<String>>> configValues = new ArrayList<>();
        configValues.add(new Keyed<>("setting.1", Bags.arrayList("42")));
        configValues.add(new Keyed<>("setting.3", Bags.arrayList("one", "two", "three")));
        Tests.testError(IllegalStateException.class,
                "Required setting .* was not set.",
                () -> {
                    configuration.reconfigure(configValues);
                    configuration.checkRequiredSettings();
                });
    }

    @Test
    public void asText() {
        Configuration configuration = new Configuration(setting1, setting2, setting3);
        List<Keyed<String, List<String>>> configValues = new ArrayList<>();
        configValues.add(new Keyed<>("setting.1", Bags.arrayList("42")));
        configValues.add(new Keyed<>("setting.2", Bags.arrayList("4.2")));
        configValues.add(new Keyed<>("setting.3", Bags.arrayList("one", "two", "three")));
        configuration.reconfigure(configValues);
        String expected = "setting.1 = 42\n" +
                "setting.2 = 4.2\n" +
                "setting.3 = [\n" +
                "    one,\n" +
                "    two,\n" +
                "    three\n" +
                "]\n";
        assertEquals(expected, configuration.asText());
    }

    @Test
    public void listEndOnOwnLine() {
        String input = "cluster.addresses=[\n" +
                "dc_a=hamlet-01:8080,\n" +
                "dc_b=hamlet-01:8080\n" +
                "]\n" +
                "other.setting=true";
        ConfigurationParser parser = new ConfigurationParser();
        List<Keyed<String, List<String>>> parsed = parser.parse(input);
        assertEquals(2, parsed.size());
    }
}