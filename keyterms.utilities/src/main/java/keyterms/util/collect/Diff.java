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

package keyterms.util.collect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Values describing the difference between two sets of values.
 */
public enum Diff {
    /**
     * An indication that the associated value was added.
     */
    ADDED,
    /**
     * An indication that the associated value was modified.
     */
    MODIFIED,
    /**
     * An indication that the associated value was removed.
     */
    REMOVED,
    /**
     * An indication that the value was not modified.
     */
    SAME;

    /**
     * Determine the differences between the data in the specified sets.
     *
     * @param original The original map.
     * @param updated The updated map.
     * @param <V> The value class of the sets.
     *
     * @return A map of differences between the two sets.
     */
    public static <V> Map<V, Diff> compare(Set<V> original, Set<V> updated) {
        Map<V, Diff> comparison = new HashMap<>();
        if (original != null) {
            original.forEach((value) -> {
                if ((updated != null) && (updated.contains(value))) {
                    comparison.put(value, SAME);
                } else {
                    comparison.put(value, REMOVED);
                }
            });
        }
        if (updated != null) {
            updated.forEach((value) -> {
                if ((original == null) || (!original.contains(value))) {
                    comparison.put(value, ADDED);
                }
            });
        }
        return comparison;
    }

    /**
     * Determine the differences between the data in the specified maps.
     *
     * @param original The original map.
     * @param updated The updated map.
     * @param <K> The key class for the maps.
     * @param <V> The value class for the maps.
     *
     * @return A map of differences between the two maps.
     */
    public static <K, V> Map<K, Diff> compare(Map<K, V> original, Map<K, V> updated) {
        Map<K, Diff> comparison = new HashMap<>();
        if (original != null) {
            original.forEach((key, value) -> {
                if ((updated != null) && (updated.containsKey(key))) {
                    comparison.put(key, (Objects.equals(value, updated.get(key)) ? SAME : MODIFIED));
                } else {
                    comparison.put(key, REMOVED);
                }
            });
        }
        if (updated != null) {
            updated.forEach((key, value) -> {
                if ((original == null) || (!original.containsKey(key))) {
                    comparison.put(key, ADDED);
                }
            });
        }
        return comparison;
    }
}