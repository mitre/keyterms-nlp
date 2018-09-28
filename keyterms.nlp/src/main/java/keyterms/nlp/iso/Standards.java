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

package keyterms.nlp.iso;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.LoggerFactory;

import keyterms.util.text.Strings;

/**
 * Utilities for the ISO standards enumerations.
 */
final class Standards {
    /**
     * Get the lookup key for the specified text.
     *
     * @param text The text which is to be used as a lookup key.
     *
     * @return The key representation of the specified text.
     */
    static String key(CharSequence text) {
        String key = Strings.toLowerCase(Strings.trim(text));
        if (key != null) {
            // normalize whitespace
            key = key.replaceAll("\\s+", " ");
            // remove punctuation
            key = key.replaceAll("\\p{Punct}", "");
        }
        return key;
    }

    /**
     * Populate the specified map.
     *
     * @param standard The standard class.
     * @param map The map to populate.
     * @param text The text key.
     * @param value The value.
     */
    static <S> void putCode(Class<S> standard, Map<String, S> map, String text, S value) {
        if (value != null) {
            String key = key(text);
            if (Strings.hasText(text)) {
                if (map.containsKey(key)) {
                    S existing = map.get(key);
                    if (!Objects.equals(existing, value)) {
                        throw new IllegalArgumentException("Duplicate " + standard.getSimpleName() +
                                " code entries for " + key + " = " + existing + " AND " + value);
                    }
                } else {
                    map.put(key, value);
                }
            }
        }
    }

    /**
     * Populate the specified map.
     *
     * @param standard The standard class.
     * @param map The map to populate.
     * @param text The text key.
     * @param value The value.
     */
    static <S> void putName(Class<S> standard, Map<String, Set<S>> map, String text, S value) {
        if (value != null) {
            if (Strings.hasText(text)) {
                String key = key(text);
                Set<S> named = map.computeIfAbsent(key, (k) -> new HashSet<>());
                named.add(value);
                if (named.size() > 1) {
                    LoggerFactory.getLogger(standard).trace("Multiple entries for name {} = {}.", key, named);
                }
            }
        }
    }

    /**
     * Constructor.
     */
    private Standards() {
        super();
    }
}