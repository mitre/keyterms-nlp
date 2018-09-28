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

package keyterms.util.process;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import keyterms.util.Errors;

import static keyterms.util.process.TestProcess.STOP_TICKS;
import static keyterms.util.process.TestProcess.TICKS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ProcessReference_UT {

    private ProcessReference process;

    private final AtomicInteger count = new AtomicInteger(0);

    @Test(timeout = 5_000)
    public void testMonitor()
            throws Exception {
        count.set(0);
        ProcessBuilder processBuilder = TestProcess.getProcessBuilder();
        process = new ProcessReference("TestMonitor", processBuilder, Charset.defaultCharset(), new TestListener());
        process.waitFor();
        assertFalse(process.isAlive());
        assertEquals(TICKS, count.get());
        assertNotNull(process.getExitCode());
        assertEquals(0, (int)process.getExitCode());
    }

    @Test(timeout = 5_000)
    public void stopEarly()
            throws Exception {
        count.set(0);
        ProcessBuilder processBuilder = TestProcess.getProcessBuilder();
        process = new ProcessReference("StopEarly", processBuilder, Charset.defaultCharset(), new TestListener());
        while (process.isAlive()) {
            if (count.get() == STOP_TICKS) {
                process.stop();
            }
            Thread.yield();
        }
        assertFalse(process.isAlive());
        assertNotEquals(TICKS, count.get());
        assertNotNull(process.getExitCode());
        assertEquals(1, (int)process.getExitCode());
    }

    private class TestListener
            implements ProcessListener {

        TestListener() {
            super();
        }

        @Override
        public void processOutput(OutputType type, String line) {
            if ((line != null) && (line.startsWith("Ping"))) {
                int newCount = count.incrementAndGet();
                try {
                    process.sendInput("Pong: " + newCount + "\n");
                } catch (Exception error) {
                    Errors.ignore(error);
                }
            }
        }

        @Override
        public void outputError(OutputType outputType, Exception error) {
            Errors.ignore(error);
        }
    }
}