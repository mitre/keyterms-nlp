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

package keyterms.util.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A random sampling of a collection of data using reservoir sampling techniques.
 */
public class ReservoirSample<D>
        implements Iterable<D>, Serializable {
    /**
     * Sample the specified collection using reservoir sampling.
     *
     * @param collection The data to sample.
     * @param maxSamples The maximum number of desired samples.
     * @param <D> The data value class.
     *
     * @return The sampled data.
     */
    public static <D> List<D> sample(Collection<D> collection, int maxSamples) {
        ReservoirSample<D> reservoirSample = new ReservoirSample<>(maxSamples);
        if (collection != null) {
            collection.forEach(reservoirSample::add);
        }
        return reservoirSample.getValues();
    }

    /**
     * A synchronization lock for the sample.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The maximum number of desired samples.
     */
    private final int maxSamples;

    /**
     * The random number generator for the sample.
     */
    private final Random random = new Random();

    /**
     * The number of inputs that have been evaluated.
     */
    private double evaluated = 0;

    /**
     * The collected samples.
     */
    private final ArrayList<D> samples = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param maxSamples The maximum number of desired samples.
     */
    public ReservoirSample(int maxSamples) {
        super();
        if (maxSamples < 1) {
            throw new IllegalArgumentException("Invalid sample size: " + maxSamples);
        }
        this.maxSamples = maxSamples;
    }

    /**
     * Potentially add the data to the sample.
     *
     * @param data The data.
     */
    public void add(D data) {
        lock.writeLock().lock();
        try {
            evaluated++;
            if (samples.size() >= maxSamples) {
                double p = (double)maxSamples / evaluated;
                if (random.nextDouble() < p) {
                    samples.remove(random.nextInt(maxSamples));
                    samples.add(data);
                }
            } else {
                samples.add(data);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get the number of values currently in the sample.
     *
     * @return The number of values currently in the sample.
     */
    public int size() {
        lock.readLock().lock();
        try {
            return samples.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get a list of the values currently in the sample.
     *
     * @return A list of the values currently in the sample.
     */
    public List<D> getValues() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(samples);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<D> iterator() {
        try {
            lock.readLock().lock();
            return getValues().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + size() + "/" + maxSamples + "]";
    }
}