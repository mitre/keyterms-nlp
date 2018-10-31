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

package keyterms.util.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * A lazily instantiated value.
 */
public class Lazy<V>
        implements Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 5005849630434924203L;

    /**
     * The synchronization lock for the lazy value computation.
     */
    private transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The value factory.
     */
    private transient final Supplier<V> supplier;

    /**
     * A flag indicating whether the value has been initialized.
     */
    private boolean initialized;

    /**
     * The value.
     */
    private V value;

    /**
     * Constructor.
     *
     * @param supplier The value factory.
     */
    public Lazy(Supplier<V> supplier) {
        super();
        if (supplier == null) {
            throw new NullPointerException("Value supplier required.");
        }
        this.supplier = supplier;
    }

    /**
     * Determine if the value has been initialized.
     *
     * @return A flag indicating whether the value has been initialized.
     */
    public boolean isInitialized() {
        lock.readLock().lock();
        try {
            return initialized;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the lazily instantiated value.
     *
     * @return The value.
     */
    public V value() {
        boolean initialized = isInitialized();
        if (!initialized) {
            lock.writeLock().lock();
            try {
                if (!this.initialized) {
                    this.initialized = true;
                    value = supplier.get();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Lazy)
                ? java.util.Objects.equals(value(), ((Lazy)obj).value())
                : java.util.Objects.equals(value(), obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.valueOf(value());
    }

    /**
     * Lightly override the default serialization write to force initialization of the lazy value.
     *
     * @param stream The object output stream.
     *
     * @throws IOException for input/output errors
     */
    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        // Ensure that the value has been initialized so that the supplier may be safely left null
        // during the default de-serialization operation.
        value();
        stream.defaultWriteObject();
    }

    /**
     * Lightly override the default serialization read to re-initialize a local synchronization lock.
     *
     * @param stream The object output stream.
     *
     * @throws ClassNotFoundException if the class is not found
     * @throws IOException for input/output errors
     */
    private void readObject(ObjectInputStream stream)
            throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        lock = new ReentrantReadWriteLock();
    }
}