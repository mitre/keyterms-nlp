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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;
import keyterms.util.text.Parser;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.Parsers;
import keyterms.util.text.splitter.LineSplitter;

/**
 * A parser for configuration files.
 */
public class ConfigurationParser
        implements Parser<List<Keyed<String, List<String>>>> {
    /**
     * The characters which require escapes to be considered literal instead of parsing markers.
     */
    static final Set<Character> REQUIRED_ESCAPES = Collections.unmodifiableSet(Bags.orderedSet(
            '#', '\\', '[', ']'
    ));

    /**
     * The parser's line splitter.
     */
    private static final LineSplitter LINE_SPLITTER = new LineSplitter();

    /**
     * The pattern used to detect value list starts.
     */
    private static final Pattern LIST_START = Pattern.compile("[^=]*=\\s*\\[.*");

    /**
     * The pattern used to detect value list ends.
     */
    private static final Pattern LIST_STOP = Pattern.compile("((^])|(.*[^\\\\]]))");

    /**
     * The pattern for an empty value list.
     */
    private static final Pattern EMPTY_LIST = Pattern.compile("\\[\\s*]");

    /**
     * Constructor.
     */
    public ConfigurationParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Keyed<String, List<String>>> parse(CharSequence text) {
        List<Keyed<String, List<String>>> entries = new ArrayList<>();
        if (Strings.hasText(text)) {
            List<String> lines = LINE_SPLITTER.split(text).stream()
                    .map(Strings::trim)
                    .filter(line -> Strings.hasText(line))
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.replaceAll("(.*[^\\\\])#.*", "$1"))
                    .map(line -> line.replaceFirst("\\s*=\\s*", "="))
                    .map(Strings::trim)
                    .filter(line -> Strings.hasText(line))
                    .collect(Collectors.toList());
            ArrayList<String> merged = new ArrayList<>();
            boolean merging = false;
            for (String line : lines) {
                String mergedLine = line;
                if (merging) {
                    String prior = merged.remove(merged.size() - 1);
                    mergedLine = prior + ' ' + line;
                }
                merged.add(mergedLine);
                if (LIST_START.matcher(line).matches()) {
                    merging = true;
                }
                if (LIST_STOP.matcher(line).matches()) {
                    merging = false;
                }
            }
            merged.forEach((line) -> {
                boolean valueList = LIST_START.matcher(line).matches();
                String unescaped = line.replaceAll("\\\\(.)", "$1");
                Keyed<String, String> kv = Parsers.KEYS.parse(unescaped);
                kv.setValue(Strings.replaceSystemTokens(kv.getValue()));
                Keyed<String, List<String>> entry = new Keyed<>(kv.getKey(), new ArrayList<>());
                if (valueList) {
                    if (!EMPTY_LIST.matcher(kv.getValue()).matches()) {
                        String value = kv.getValue();
                        value = value.substring(1, value.length() - 1);
                        entry.getValue().addAll(
                                Stream.of(value.split(","))
                                        .map(Strings::trim)
                                        .collect(Collectors.toList()));
                    }
                } else {
                    if (kv.getValue() != null) {
                        entry.getValue().add(kv.getValue());
                    }
                }
                entries.add(entry);
            });
        }
        return entries;
    }
}