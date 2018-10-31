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

import java.util.Map;

/**
 * A simple key-value pair where the key is used for identity.
 */
public class Keyed<K, V>
        extends Unique<K>
        implements Map.Entry<K, V> {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -8637491510172173570L;

    /**
     * Get a keyed value.
     *
     * @param key The key.
     * @param value The value.
     * @param <K> The key class.
     * @param <V> The value class.
     *
     * @return The specified keyed value.
     */
    public static <K, V> Keyed<K, V> of(K key, V value) {
        return new Keyed<>(key, value);
    }

    /**
     * Get a keyed value equivalent of the specified map entry.
     *
     * <p> The map entry may be {@code null}. </p>
     *
     * @param mapEntry The map entry containing the key and the value.
     * @param <K> The key class.
     * @param <V> The value class.
     *
     * @return The keyed value equivalent of the specified map entry.
     */
    public static <K, V> Keyed<K, V> of(Map.Entry<K, V> mapEntry) {
        Keyed<K, V> keyed = null;
        if (mapEntry != null) {
            keyed = new Keyed<>(mapEntry);
        }
        return keyed;
    }

    /**
     * The value.
     */
    private V value;

    /**
     * Constructor.
     *
     * @param key The key.
     */
    public Keyed(K key) {
        this(key, null);
    }

    /**
     * Constructor.
     *
     * @param key The key.
     * @param value The value.
     */
    public Keyed(K key, V value) {
        super(key);
        setValue(value);
    }

    /**
     * Constructor.
     *
     * @param mapEntry The map entry containing the key and the value.
     */
    public Keyed(Map.Entry<K, V> mapEntry) {
        this(mapEntry.getKey(), mapEntry.getValue());
    }

    /**
     * Get the key.
     *
     * @return The key.
     */
    public K getKey() {
        return id;
    }

    /**
     * Get the value.
     *
     * @return The value.
     */
    public V getValue() {
        return value;
    }

    /**
     * Set the value.
     *
     * @param value The new value.
     */
    public V setValue(V value) {
        V current = this.value;
        this.value = value;
        return current;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[%s=%s]", getClass().getSimpleName(), id, value);
    }
}