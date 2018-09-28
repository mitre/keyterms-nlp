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
import java.util.HashSet;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;
import keyterms.util.io.Encoding;

import static org.junit.Assert.assertEquals;

public class ConfigurationParser_UT {

    private static String configText;

    @BeforeClass
    public static void setTestProperties()
            throws Exception {
        System.setProperty("test.prop.1", "Hello");
        System.setProperty("test.prop.2", "World");
        System.setProperty("test.prop.3", "!");
        String rsc = ConfigurationParser_UT.class.getSimpleName() + ".txt";
        configText = Encoding.decode(
                ConfigurationParser_UT.class.getResourceAsStream(rsc).readAllBytes(),
                Encoding.UTF8);
    }

    @Test
    public void parse() {
        List<Keyed<String, List<String>>> expected = Bags.arrayList(
                new Keyed<>("property1", Collections.emptyList()),
                new Keyed<>("property1", Bags.arrayList("")),
                new Keyed<>("property1", Bags.arrayList("[]")),
                new Keyed<>("", Bags.arrayList("1", "2", "3")),
                new Keyed<>("property2", Bags.arrayList("Hello")),
                new Keyed<>("property3", Bags.arrayList("Jello")),
                new Keyed<>("property4", Bags.arrayList("Pudding # Love Me! #")),
                new Keyed<>("unknown", Bags.arrayList("ignore me")),
                new Keyed<>("property5", Bags.arrayList("list1")),
                new Keyed<>("property5", Bags.arrayList("list2")),
                new Keyed<>("property5", Bags.arrayList("list3")),
                new Keyed<>("property6", Bags.arrayList("Hello World!")),
                new Keyed<>("property7", Bags.arrayList("one", "two", "three")),
                new Keyed<>("property8", Bags.arrayList("Hello", "World", "!")),
                new Keyed<>("property9", Bags.arrayList("value")),
                new Keyed<>("property10", Collections.emptyList())
        );
        ConfigurationParser settingsParser = new ConfigurationParser();
        List<Keyed<String, List<String>>> entries = settingsParser.parse(configText);
        assertEquals(expected, entries);
        for (int k = 0; k < expected.size(); k++) {
            Object exp = expected.get(k).getValue();
            Object act = entries.get(k).getValue();
            assertEquals(expected.get(k).getKey(), exp, act);
        }
    }

    @Test
    public void configuration() {
        Setting<String> p1 = new SettingFactory<>("property1", String.class).build();
        Setting<String> p2 = new SettingFactory<>("property2", String.class).build();
        Setting<String> p3 = new SettingFactory<>("property3", String.class).build();
        Setting<String> p4 = new SettingFactory<>("property4", String.class).build();
        Setting<String> p5 = new SettingFactory<>("property5", String.class).withMultipleValues().build();
        Setting<String> p6 = new SettingFactory<>("property6", String.class).build();
        Setting<String> p7 = new SettingFactory<>("property7", String.class).withMultipleValues().build();
        Setting<String> p8 = new SettingFactory<>("property8", String.class).withMultipleValues().build();
        Setting<String> p9 = new SettingFactory<>("property9", String.class).build();
        Configuration settings = new Configuration(p1, p2, p3, p4, p5, p6, p7, p8, p9);
        List<Keyed<String, List<String>>> values = new ConfigurationParser().parse(configText);
        settings.configure(values);
        assertEquals("[]", p1.getValue());
        assertEquals("Hello", p2.getValue());
        assertEquals("Jello", p3.getValue());
        assertEquals("Pudding # Love Me! #", p4.getValue());
        assertEquals(new HashSet<>(Bags.arrayList("list1", "list2", "list3")), p5.getValues());
        assertEquals("Hello World!", p6.getValue());
        assertEquals(new HashSet<>(Bags.arrayList("one", "two", "three")), p7.getValues());
        assertEquals(new HashSet<>(Bags.arrayList("Hello", "World", "!")), p8.getValues());
        assertEquals("value", p9.getValue());
    }
}