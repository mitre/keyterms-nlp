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
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.Errors;
import keyterms.util.text.Strings;

/**
 * A reference to a daughter command line process.
 */
public class ProcessReference {
    /**
     * Close the specified auto-closeable object without generating normal error logs.
     *
     * @param closeable The closeable object.
     */
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception error) {
                Errors.ignore(error);
            }
        }
    }

    /**
     * The thread group for output monitoring threads for executing scripts.
     */
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup(ProcessReference.class.getSimpleName());

    /**
     * The display name for the process.
     */
    private final String name;

    /**
     * The script monitor's logging topic.
     */
    private final String loggerName;

    /**
     * The character encoding of the input and output streams of the process.
     */
    private final Charset encoding;

    /**
     * The launched process.
     */
    private final Process process;

    /**
     * The stream from which process output is read.
     */
    private final InputStream processOutput;

    /**
     * The stream from which process error output is read.
     */
    private final InputStream processError;

    /**
     * The stream over which process input is written.
     */
    private final OutputStream processInput;

    /**
     * The output monitors for the process.
     */
    private final Map<OutputType, OutputMonitor> outputMonitors = new HashMap<>();

    /**
     * Constructor.
     *
     * @param name The desired short name of the process.
     * @param processBuilder The factory used to create new instances of the process to be monitored.
     * @param encoding The character encoding of the input and output streams of the process.
     * @param listener The listener to process output.
     */
    ProcessReference(String name, ProcessBuilder processBuilder, Charset encoding, ProcessListener listener)
            throws IOException {
        super();
        this.name = (Strings.isBlank(name))
                ? Processes.getCommandText(processBuilder.command()).replaceFirst("^\\s*([^\\s]+).*", "$1")
                : name.trim();
        loggerName = "Process[" + this.name + "]";
        this.encoding = (encoding != null) ? encoding : Charset.defaultCharset();
        getLogger().info("Command: {}", Processes.getCommandText(processBuilder));
        process = processBuilder.start();
        processOutput = process.getInputStream();
        outputMonitors.put(OutputType.STANDARD, new OutputMonitor(processOutput, OutputType.STANDARD, listener));
        if (processBuilder.redirectErrorStream()) {
            processError = null;
        } else {
            processError = process.getErrorStream();
            outputMonitors.put(OutputType.ERROR, new OutputMonitor(processError, OutputType.ERROR, listener));
        }
        processInput = process.getOutputStream();
        outputMonitors.forEach((type, monitor) -> {
            String threadName = this.name + "." + type.name().toLowerCase() + ".monitor@" + hashCode();
            Thread monitorThread = new Thread(THREAD_GROUP, monitor, threadName);
            monitorThread.start();
        });
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, loggerName + ".shutdown@" + hashCode()));
    }

    /**
     * Get the logging topic for this instance.
     *
     * @return The logging topic for this instance.
     */
    private Logger getLogger() {
        return LoggerFactory.getLogger(loggerName);
    }

    /**
     * Get the display name for the process.
     *
     * @return The display name for the process.
     */
    public String getName() {
        return name;
    }

    /**
     * Determine if the script process is still executing.
     *
     * @return A flag indicating whether the script process is still executing.
     */
    public boolean isAlive() {
        return process.isAlive();
    }

    /**
     * Send input to the script process.
     *
     * @param input The text to send to the script process.
     *
     * @throws IOException for input/output errors
     */
    public void sendInput(String input)
            throws IOException {
        if (input != null) {
            OutputStreamWriter writer = new OutputStreamWriter(processInput);
            writer.write(input);
            writer.flush();
        }
    }

    /**
     * Wait indefinitely for the process to finish.
     */
    public void waitFor() {
        while (isAlive()) {
            Thread.yield();
        }
    }

    /**
     * Kill the script process.
     */
    public void stop() {
        if (process.isAlive()) {
            getLogger().warn("Forcibly stopping process.");
            process.destroyForcibly();
            outputMonitors.values().forEach(OutputMonitor::stop);
            close(processOutput);
            close(processError);
            close(processInput);
        }
    }

    /**
     * Get the process exit code.
     *
     * @return The process exit code.
     */
    public Integer getExitCode() {
        return (!process.isAlive()) ? process.exitValue() : null;
    }

    /**
     * A monitor for an individual input stream which represents the process outputs.
     */
    private class OutputMonitor
            implements Runnable {
        /**
         * The output stream being monitored.
         */
        private final InputStream processOutput;

        /**
         * The output type being monitored.
         */
        private final OutputType outputType;

        /**
         * The listener to process output.
         */
        private final ProcessListener listener;

        /**
         * A flag indicating whether the monitor should be running.
         */
        private volatile boolean running;

        /**
         * Constructor.
         *
         * @param processOutput The output stream being monitored.
         * @param outputType The output type being monitored.
         * @param listener The listener to process output.
         */
        private OutputMonitor(InputStream processOutput, OutputType outputType, ProcessListener listener) {
            super();
            this.processOutput = processOutput;
            this.outputType = outputType;
            this.listener = listener;
        }

        /**
         * Stop monitoring.
         */
        private void stop() {
            running = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            running = true;
            getLogger().debug("Started {} output monitor.", outputType.label(), loggerName);
            try (InputStreamReader reader = new InputStreamReader(processOutput, encoding);
                 BufferedReader buffered = new BufferedReader(reader)) {
                while ((process.isAlive()) && (running)) {
                    String line = buffered.readLine();
                    if (line != null) {
                        if (listener != null) {
                            listener.processOutput(outputType, line);
                        } else {
                            getLogger().debug("{}: {}", outputType.label(), line);
                        }
                    } else {
                        running = process.isAlive();
                    }
                }
            } catch (Exception error) {
                if (listener != null) {
                    listener.outputError(outputType, error);
                } else {
                    getLogger().error("Monitoring {} output stopped on error.", outputType.label(), error);
                }
            }
            getLogger().debug("Monitoring {} output stopped.", outputType.label());
        }
    }
}