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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import keyterms.testing.Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Lazy_UT {

    private final ReentrantLock lock = new ReentrantLock();

    private final AtomicInteger count = new AtomicInteger(0);

    private int sum = 0;

    @Test
    public void lifeCycle() {
        Lazy<Object> lazy = new Lazy<>(() -> null);
        assertNull(lazy.value());
        lazy = new Lazy<>(Object::new);
        Object instance1 = lazy.value();
        assertNotNull(instance1);
        Object instance2 = lazy.value();
        assertNotNull(instance2);
        assertSame(instance1, instance2);
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void multipleThreads() {
        final Lazy<Integer> lazy = new Lazy<>(count::incrementAndGet);
        int numThreads = 100;
        ForkJoinPool pool = new ForkJoinPool();
        try {
            for (int t = 0; t < numThreads; t++) {
                pool.execute(() -> {
                    int increment = lazy.value();
                    lock.lock();
                    try {
                        sum += increment;
                    } finally {
                        lock.unlock();
                    }
                });
            }
            pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.DAYS);
            assertEquals(numThreads, sum);
        } finally {
            pool.shutdown();
        }
    }

    @Test
    public void serialization()
            throws Exception {
        count.set(42);
        Lazy<Integer> original = new Lazy<>(count::incrementAndGet);
        Lazy<Integer> copy = Tests.testSerialize(original);
        assertTrue(copy.isInitialized());
        assertEquals(43, (int)copy.value());
    }
}