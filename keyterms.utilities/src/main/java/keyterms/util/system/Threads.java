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

package keyterms.util.system;

import keyterms.util.Errors;

/**
 * Methods for interacting with threads.
 */
public final class Threads {
    /**
     * Sleep on the current thread for the specified number of milliseconds, ignoring interrupted exceptions.
     *
     * @param milliseconds The time in milliseconds to sleep.
     *
     * @return A flag indicating whether the sleep was completed without interruption.
     */
    public static boolean sleep(long milliseconds) {
        boolean full = true;
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException error) {
            full = false;
        }
        return full;
    }

    /**
     * Get a textual description of all active threads.
     *
     * @return A textual description of the currently active threads.
     */
    public static String getDump() {
        StringBuilder threadDump = new StringBuilder();
        threadDump.append("============\n");
        threadDump.append("Thread dump:\n");
        threadDump.append("------------\n");
        Thread currentThread = Thread.currentThread();
        ThreadGroup ctg = currentThread.getThreadGroup();
        while (ctg.getParent() != null) {
            ctg = ctg.getParent();
        }
        threadDump.append("Thread group = ").append(ctg.getName()).append('\n');
        int count = ctg.activeCount();
        threadDump.append("  group contains ").append(count).append(" active threads.\n");
        Thread[] threads = new Thread[count];
        ctg.enumerate(threads);
        for (int t = 0; t < count; t++) {
            if (threads[t] != null) {
                ThreadGroup group = threads[t].getThreadGroup();
                threadDump.append("    ").append(t + 1)
                        .append(". ").append((threads[t].isDaemon()) ? "Daemon" : "      ")
                        .append(" Thread[").append(String.format("%2d", threads[t].getPriority()))
                        .append(',').append((group != null) ? group.getName() : "^")
                        .append("] - ").append(threads[t].getName()).append('\n');
            } else {
                threadDump.append("    ").append(t + 1).append(". null");
            }
        }
        return threadDump.toString();
    }

    /**
     * Run the specified task without generating normal error logs.
     *
     * @param task The task to run.
     */
    public static void runQuietly(ErrorProne task) {
        if (task != null) {
            try {
                task.run();
            } catch (Exception error) {
                Errors.ignore(error);
            }
        }
    }

    /**
     * Constructor.
     */
    private Threads() {
        super();
    }
}