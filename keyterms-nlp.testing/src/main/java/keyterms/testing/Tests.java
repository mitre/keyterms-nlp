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

package keyterms.testing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Some common test methods.
 */
public final class Tests {
    /**
     * Test that the specified object is fully serializable.
     *
     * @param object The object.
     * @param <S> The serializable object class.
     *
     * @return A clone of the object created by serialization.
     *
     * @throws IOException for input/output errors
     * @throws ClassNotFoundException if the class is not found
     */
    @SuppressWarnings("unchecked")
    public static <S extends Serializable> S serialCopy(S object)
            throws IOException, ClassNotFoundException {
        Class<S> objectClass = (Class<S>)object.getClass();
        byte[] objectData;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            objectData = bos.toByteArray();
        }
        S clone;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(objectData);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object deSerialized = ois.readObject();
            clone = objectClass.cast(deSerialized);
        }
        return clone;
    }

    /**
     * Test that the values are properly serializable.
     *
     * @param value The value to test.
     * @param <S> The serializable object class.
     *
     * @return The copy of the object created by serialization.
     *
     * @throws IOException for input/output errors
     * @throws ClassNotFoundException if the class is not found
     */
    public static <S extends Serializable> S testSerialize(S value)
            throws IOException, ClassNotFoundException {
        return testSerialize(value, false);
    }

    /**
     * Test that the values are properly serializable.
     *
     * @param value The value to test.
     * @param shouldBeSame A flag indicating whether a serial copy should result in the same object.
     * @param <S> The serializable object class.
     *
     * @return The copy of the object created by serialization.
     *
     * @throws IOException for input/output errors
     * @throws ClassNotFoundException if the class is not found
     */
    public static <S extends Serializable> S testSerialize(S value, boolean shouldBeSame)
            throws IOException, ClassNotFoundException {
        assertNotNull(value);
        S copy = serialCopy(value);
        assertNotNull(copy);
        assertEquals(value, copy);
        if (shouldBeSame) {
            assertSame(value, copy);
        } else {
            assertNotSame(value, copy);
        }
        return copy;
    }

    /**
     * Test that the values are properly serializable.
     *
     * @param values The values to test.
     * @param <S> The serializable object class.
     *
     * @throws IOException for input/output errors
     * @throws ClassNotFoundException if the class is not found
     */
    public static <S extends Serializable> void testSerialization(Collection<S> values)
            throws IOException, ClassNotFoundException {
        testSerialization(values, false);
    }

    /**
     * Test that the values are properly serializable.
     *
     * @param values The values to test.
     * @param shouldBeSame A flag indicating whether a serial copy should result in the same object.
     * @param <S> The serializable object class.
     *
     * @throws IOException for input/output errors
     * @throws ClassNotFoundException if the class is not found
     */
    public static <S extends Serializable> void testSerialization(Collection<S> values, boolean shouldBeSame)
            throws IOException, ClassNotFoundException {
        for (S value : values) {
            testSerialize(value, shouldBeSame);
        }
    }

    /**
     * Test that the specified task throws an error of the specified type.
     *
     * @param errorClass The expected error class.
     * @param task The task.
     * @param <E> The error class.
     *
     * @return The encountered error.
     */
    public static <E extends Throwable> E testError(Class<E> errorClass, TestTask task) {
        return testError(errorClass, null, task);
    }

    /**
     * Test that the specified task throws an expected error condition.
     *
     * @param errorClass The expected error class.
     * @param messagePattern The pattern the error message is expected to match.
     * @param task The task.
     * @param <E> The error class.
     *
     * @return The error which was thrown.
     */
    public static <E extends Throwable> E testError(Class<E> errorClass, String messagePattern, TestTask task) {
        assertNotNull("Error class required.", errorClass);
        assertNotNull("Task required.", task);
        Pattern mp = (messagePattern != null) ? Pattern.compile(messagePattern) : null;
        Throwable taskError = null;
        try {
            task.run();
        } catch (Throwable error) {
            if ((error instanceof Error) && (!(error instanceof AssertionError))) {
                throw (Error)error;
            }
            taskError = error;
        }
        if (taskError == null) {
            fail("Expected error not thrown: " + errorClass.getSimpleName() +
                    ((mp != null) ? ": " + messagePattern : ""));
        }
        if (!errorClass.isInstance(taskError)) {
            fail("Expected error class not thrown:" +
                    "\n    expected = " + errorClass.getSimpleName() +
                    "\n      actual = " + taskError.getClass().getSimpleName());
        }
        if (mp != null) {
            String message = taskError.getMessage();
            if (!mp.matcher(message).matches()) {
                fail("Error message did not match expected pattern:" +
                        "\n    expected = " + messagePattern +
                        "\n      actual = " + message);
            }
        }
        return errorClass.cast(taskError);
    }

    /**
     * Perform a multi-threaded test given a set of pass/fail style tasks to complete.
     *
     * <p> Pass/fail style tests should be designed to return {@code true} for success, {@code false} for failure, and
     * {@code null} if the test fails on an unexpected error condition. </p>
     *
     * @param numThreads The number of threads to use for the tests.
     * @param tasks The pass/fail tasks to perform on the threads.
     *
     * @return A map of the results.
     */
    public static Map<Boolean, Integer> multiThreadedPassFailTests(int numThreads, List<Callable<Boolean>> tasks) {
        Map<Boolean, Integer> results = new HashMap<>();
        List<Future<Boolean>> futures = new ArrayList<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numThreads, numThreads,
                0L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
        tasks.forEach(task -> futures.add(executor.submit(task)));
        futures.forEach(future -> {
            Boolean pass;
            try {
                pass = future.get();
            } catch (Exception error) {
                pass = null;
            }
            results.putIfAbsent(pass, 0);
            results.put(pass, results.get(pass) + 1);
        });
        executor.shutdownNow();
        return results;
    }

    /**
     * Constructor.
     */
    private Tests() {
        super();
    }
}