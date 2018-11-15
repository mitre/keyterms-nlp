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

package keyterms.analyzer.profiles.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An abstract closed value feature.
 *
 * <p> Closed value features contain a small number of well-known discrete values. </p>
 */
public class EnumeratedFeature<F>
        extends ModelFeature<F> {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -2182441621285302675L;

    /**
     * A synchronization lock for the value state.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The value list for the feature.
     */
    private final List<F> values = new ArrayList<>();

    /**
     * A map of values to their ordinal position in the value list.
     */
    private final Map<F, Integer> valueIndex = new HashMap<>();

    /**
     * A flag indicating whether the value list may grow dynamically.
     *
     * <p> This attribute allows the value list to grow during training. </p>
     */
    private boolean dynamic = true;

    /**
     * Constructor.
     *
     * @param name The feature name.
     * @param valueClass The value class associated with the feature.
     * @param parser The parser which can create feature values from textual representations.
     * @param formatter The formatter which creates textual representations from feature values.
     */
    public EnumeratedFeature(String name, Class<F> valueClass, FeatureParser<F> parser, FeatureFormatter<F> formatter) {
        super(name, valueClass, parser, formatter);
    }

    /**
     * Get the number of values represented by this feature.
     *
     * <p> Only enumerated features should return a non-{@code null} value. </p> <p> All enumerated features should
     * override this method. </p>
     *
     * @return The number of values represented by this feature.
     */
    public int size() {
        lock.readLock().lock();
        try {
            return values.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the values represented by this feature.
     *
     * <p> Only enumerated features should return a non-{@code null} value. </p> <p> All enumerated features should
     * override this method. </p>
     *
     * @return The values represented by this feature.
     */
    public List<F> getValues() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(values);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Set the values for the feature.
     *
     * <p> This method is a no-op if the specified value list is {@code null} or empty. </p> <p> Note: This method may
     * only be called if the feature value list is open and empty. </p> <p> Note: This method will close the feature
     * value list. </p>
     *
     * @param values The values for the feature.
     */
    public void setValues(List<F> values) {
        if ((values != null) && (!values.isEmpty())) {
            lock.writeLock().lock();
            try {
                if (!dynamic) {
                    throw new IllegalStateException("Feature value list is closed.");
                }
                if (!this.values.isEmpty()) {
                    throw new IllegalStateException("Feature value list is not empty.");
                }
                values.forEach(this::getOrAdd);
                close();
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    /**
     * Get the index of the specified value if it is currently present in the value list.
     *
     * @param value The value.
     *
     * @return The index of the specified value.
     */
    protected int getIndex(F value) {
        lock.readLock().lock();
        try {
            return valueIndex.getOrDefault(value, -1);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the current index of the specified value or create a new entry for the value.
     *
     * @param value The value.
     *
     * @return The current or new index of the specified value.
     */
    protected int getOrAdd(F value) {
        int index = getIndex(value);
        if (index == -1) {
            lock.writeLock().lock();
            try {
                index = getIndex(value);
                if ((index == -1) && (value != null) && (dynamic)) {
                    index = values.size();
                    values.add(value);
                    valueIndex.put(value, index);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        return index;
    }

    /**
     * Get the ordinal equivalent of the specified value.
     *
     * <p> Only enumerated features should return a non-{@code null} value. </p> <p> All enumerated features should
     * override this method. </p>
     *
     * <p> An ordinal value of {@code -1} will be returned for {@code null} and unknown values. </p>
     *
     * @param value The value.
     *
     * @return The ordinal equivalent of the specified value.
     */
    public int toOrdinal(F value) {
        return (test(value)) ? getOrAdd(value) : -1;
    }

    /**
     * Get the value equivalent of the specified ordinal value.
     *
     * <p> Only enumerated features should return a non-{@code null} value. </p> <p> All enumerated features should
     * override this method. </p>
     *
     * @param ordinal The ordinal equivalent of the desired value.
     *
     * @return The value.
     */
    public F toValue(int ordinal) {
        F value = null;
        lock.readLock().lock();
        try {
            if ((ordinal >= 0) && (ordinal < values.size())) {
                value = values.get(ordinal);
            }
        } finally {
            lock.readLock().unlock();
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(Object value) {
        boolean accept = super.test(value);
        if (accept) {
            getOrAdd(cast(value));
        }
        return accept;
    }

    /**
     * Close the feature so that no more values may be added to the value list.
     */
    public void close() {
        lock.writeLock().lock();
        try {
            dynamic = false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}