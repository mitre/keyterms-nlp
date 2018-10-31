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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import keyterms.util.Errors;
import keyterms.util.system.Threads;

class TestProcess
        implements Runnable {

    static final int TICKS = 10;

    static final int STOP_TICKS = 5;

    public static ProcessBuilder getProcessBuilder() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("java",
                "-classpath", System.getProperty("java.class.path"),
                TestProcess.class.getName(),
                "Argument 1", "Argument 2", "Argument 3");
        return processBuilder;
    }

    public static void main(String[] args) {
        try {
            System.out.println("Test process start.");
            Thread inputMonitor = new Thread(new TestProcess());
            inputMonitor.setDaemon(true);
            inputMonitor.start();
            Stream.of(args).forEach((arg) -> System.out.println("CL Argument: " + arg));
            for (int t = 0; t < TICKS; t++) {
                Threads.sleep(100);
                System.out.println("Ping " + (t + 1));
            }
            System.out.println("Test process stop.");
        } catch (Throwable error) {
            Errors.check(error);
            System.err.println("Unexpected error.\n" + Errors.stackTraceOf(error));
        }
    }

    public void run() {
        System.out.println("Monitoring process input.");
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            do {
                line = inputReader.readLine();
                if (line != null) {
                    System.err.println("Input: " + line);
                }
            } while (line != null);
        } catch (Exception error) {
            System.err.println("Error reading process input.\n" + Errors.stackTraceOf(error));
        }
        System.out.println("Process input monitoring stopped.");
    }
}