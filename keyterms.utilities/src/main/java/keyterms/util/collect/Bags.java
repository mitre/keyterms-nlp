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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Methods for creating collections from variable arity arguments.
 */
public final class Bags {
    /**
     * Create an {@code ArrayList} containing the specified elements.
     *
     * @param elements The desired elements of the list.
     * @param <E> The element class.
     *
     * @return The specified list.
     */
    @SafeVarargs
    public static <E> ArrayList<E> arrayList(E... elements) {
        ArrayList<E> list = new ArrayList<>();
        if (elements != null) {
            Collections.addAll(list, elements);
        }
        return list;
    }

    /**
     * Create an unmodifiable list containing the specified elements.
     *
     * @param elements The desired elements of the list.
     * @param <E> The element class.
     *
     * @return The specified list.
     */
    @SafeVarargs
    public static <E> List<E> staticList(E... elements) {
        return Collections.unmodifiableList(arrayList(elements));
    }

    /**
     * Create a {@code HashSet} containing the specified elements.
     *
     * @param elements The desired elements of the set.
     * @param <E> The element class.
     *
     * @return The specified set.
     */
    @SafeVarargs
    public static <E> HashSet<E> hashSet(E... elements) {
        HashSet<E> set = new HashSet<>();
        if (elements != null) {
            Collections.addAll(set, elements);
        }
        return set;
    }

    /**
     * Create a {@code LinkedHashSet} containing the specified elements.
     *
     * @param elements The desired elements of the set.
     * @param <E> The element class.
     *
     * @return The specified set.
     */
    @SafeVarargs
    public static <E> LinkedHashSet<E> orderedSet(E... elements) {
        LinkedHashSet<E> set = new LinkedHashSet<>();
        if (elements != null) {
            Collections.addAll(set, elements);
        }
        return set;
    }

    /**
     * Create a {@code TreeSet} containing the specified elements.
     *
     * @param elements The desired elements of the set.
     * @param <E> The element class (must be self-{@code Comparable}).
     *
     * @return The specified set.
     */
    @SafeVarargs
    public static <E extends Comparable<E>> TreeSet<E> sortedSet(E... elements) {
        TreeSet<E> set = new TreeSet<>();
        if (elements != null) {
            Collections.addAll(set, elements);
        }
        return set;
    }

    /**
     * Create a {@code TreeSet} containing the specified elements.
     *
     * @param comparator The comparator used to order the set elements.
     * @param elements The desired elements of the set.
     * @param <E> The element class.
     *
     * @return The specified set.
     */
    @SafeVarargs
    public static <E> TreeSet<E> sortedSet(Comparator<E> comparator, E... elements) {
        TreeSet<E> set = new TreeSet<>(comparator);
        if (elements != null) {
            Collections.addAll(set, elements);
        }
        return set;
    }

    /**
     * Create an unmodifiable {@code HashSet} containing the specified elements.
     *
     * @param elements The desired elements of the set.
     * @param <E> The element class.
     *
     * @return The specified set.
     */
    @SafeVarargs
    public static <E> Set<E> staticSet(E... elements) {
        return Collections.unmodifiableSet(hashSet(elements));
    }

    /**
     * Create a {@code HashMap} containing the specified entries.
     *
     * @param entries The desired entries for the map.
     * @param <K> The key class.
     * @param <V> The value class.
     *
     * @return The specified map.
     */
    @SafeVarargs
    public static <K, V> HashMap<K, V> hashMap(Map.Entry<K, V>... entries) {
        HashMap<K, V> map = new HashMap<>();
        if (entries != null) {
            Arrays.stream(entries).forEach((e) -> map.put(e.getKey(), e.getValue()));
        }
        return map;
    }

    /**
     * Create a {@code LinkedHashMap} containing the specified entries.
     *
     * @param entries The desired entries for the map.
     * @param <K> The key class.
     * @param <V> The value class.
     *
     * @return The specified map.
     */
    @SafeVarargs
    public static <K, V> LinkedHashMap<K, V> orderedMap(Map.Entry<K, V>... entries) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        if (entries != null) {
            Arrays.stream(entries).forEach((e) -> map.put(e.getKey(), e.getValue()));
        }
        return map;
    }

    /**
     * Create a {@code TreeMap} containing the specified entries.
     *
     * @param entries The desired entries for the map.
     * @param <K> The key class (must be self-{@code Comparable}).
     * @param <V> The value class.
     *
     * @return The specified map.
     */
    @SafeVarargs
    public static <K extends Comparable<K>, V> TreeMap<K, V> sortedMap(Map.Entry<K, V>... entries) {
        TreeMap<K, V> map = new TreeMap<>();
        if (entries != null) {
            Arrays.stream(entries).forEach((e) -> map.put(e.getKey(), e.getValue()));
        }
        return map;
    }

    /**
     * Create a {@code TreeMap} containing the specified entries.
     *
     * @param comparator The comparator used for sorting the key values.
     * @param entries The desired entries for the map.
     * @param <K> The key class.
     * @param <V> The value class.
     *
     * @return The specified map.
     */
    @SafeVarargs
    public static <K, V> TreeMap<K, V> sortedMap(Comparator<K> comparator, Map.Entry<K, V>... entries) {
        TreeMap<K, V> map = new TreeMap<>(comparator);
        if (entries != null) {
            Arrays.stream(entries).forEach((e) -> map.put(e.getKey(), e.getValue()));
        }
        return map;
    }

    /**
     * Create an unmodifiable {@code HashMap} containing the specified entries.
     *
     * @param entries The desired entries for the map.
     * @param <K> The key class.
     * @param <V> The value class.
     *
     * @return The specified map.
     */
    @SafeVarargs
    public static <K, V> Map<K, V> staticMap(Map.Entry<K, V>... entries) {
        return Collections.unmodifiableMap(hashMap(entries));
    }

    /**
     * Constructor.
     */
    private Bags() {
        super();
    }
}