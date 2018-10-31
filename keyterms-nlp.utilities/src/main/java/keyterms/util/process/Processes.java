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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import keyterms.util.io.Encoding;
import keyterms.util.io.Streams;

/**
 * A utility for executing command line scripts.
 *
 * <p> The methods in this utility execute the script inline and will not be appropriate for scripts which generate
 * large amounts of output. </p>
 */
public class Processes {
    /**
     * Get the command line arguments as a single string as it would be typed into a console.
     *
     * @param processBuilder The process builder which would launch the command line script.
     *
     * @return The command line arguments as a single string as it would be typed into a console.
     */
    public static String getCommandText(ProcessBuilder processBuilder) {
        return getCommandText((processBuilder != null) ? processBuilder.command() : null);
    }

    /**
     * Get the command line arguments as a single string as it would be typed into a console.
     *
     * @param commandLineArguments The list of command line arguments for the daughter process.
     *
     * @return The command line arguments as a single string as it would be typed into a console.
     */
    static String getCommandText(List<String> commandLineArguments) {
        StringBuilder text = new StringBuilder();
        if (commandLineArguments != null) {
            commandLineArguments.stream()
                    .filter(arg -> (arg != null) && (arg.length() > 0))
                    .forEach(arg -> {
                        boolean containsWhitespace = arg.matches(".*\\s+.*");
                        if (containsWhitespace) {
                            text.append('"');
                        }
                        text.append(arg);
                        if (containsWhitespace) {
                            text.append('"');
                        }
                        text.append(' ');
                    });
        }
        return text.toString().trim();
    }

    /**
     * Execute the specified command line process.
     *
     * @param processBuilder The factory used to create new instances of the process.
     *
     * @return The process output.
     */
    public static ProcessResult run(ProcessBuilder processBuilder) {
        return run(processBuilder, Charset.defaultCharset(), (InputStream)null);
    }

    /**
     * Execute the specified command line process.
     *
     * @param processBuilder The factory used to create new instances of the process.
     * @param encoding The input/output character encoding for the process.
     *
     * @return The process output.
     */
    public static ProcessResult run(ProcessBuilder processBuilder, Charset encoding) {
        return run(processBuilder, encoding, (InputStream)null);
    }

    /**
     * Execute the specified command line process.
     *
     * @param processBuilder The factory used to create new instances of the process.
     * @param encoding The input/output character encoding for the process.
     * @param input The text input to the process.
     *
     * @return The process output.
     */
    public static ProcessResult run(ProcessBuilder processBuilder, Charset encoding, String input) {
        Charset resolvedEncoding = (encoding != null) ? encoding : Charset.defaultCharset();
        return run(processBuilder, encoding, new ByteArrayInputStream(Encoding.encode(input, resolvedEncoding)));
    }

    /**
     * Execute the specified command line process.
     *
     * <p> For this method the encoding is only used to interpret the process output streams. </p>
     *
     * @param processBuilder The factory used to create new instances of the process.
     * @param encoding The input/output character encoding for the process.
     * @param input The text input to the process.
     *
     * @return The process output.
     */
    public static ProcessResult run(ProcessBuilder processBuilder, Charset encoding, InputStream input) {
        ProcessResult result = null;
        Process process = null;
        if (processBuilder != null) {
            result = new ProcessResult(encoding, processBuilder.redirectErrorStream());
            try {
                process = processBuilder.start();
                if (input != null) {
                    try (OutputStream processInput = process.getOutputStream()) {
                        Streams.channelCopy(input, processInput);
                    } catch (Exception error) {
                        result.addError(new IOException("Could not send process input.", error));
                    }
                }
                ForkJoinPool readPool = new ForkJoinPool(2);
                readPool.execute(new ChannelCopy(process.getInputStream(), OutputType.STANDARD,
                        result.getStandardBuffer(), result));
                if (result.hasErrorOutput()) {
                    readPool.execute(new ChannelCopy(process.getErrorStream(), OutputType.ERROR,
                            result.getErrorBuffer(), result));
                }
                process.waitFor();
                readPool.awaitQuiescence(1, TimeUnit.SECONDS);
                result.exit(process.exitValue());
            } catch (Exception error) {
                result.addError(new IOException("Execution error.", error));
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
            if (result.getExitCode() == null) {
                result.exit(1);
            }
        }
        return result;
    }

    /**
     * Execute the specified process in the background.
     *
     * @param name A display name for the process.
     * @param processBuilder The factory used to create new instances of the process to be monitored.
     * @param encoding The input/output character encoding for the process.
     * @param listener The listener to process output.
     *
     * @return A reference used to monitor, control and communicate with the running process.
     *
     * @throws IOException for input/output errors
     */
    public static ProcessReference start(String name, ProcessBuilder processBuilder,
            Charset encoding, ProcessListener listener)
            throws IOException {
        return new ProcessReference(name, processBuilder, encoding, listener);
    }

    /**
     * Constructor.
     */
    private Processes() {
        super();
    }

    /**
     * A task used to channel copy a process output stream into a result buffer.
     */
    private static class ChannelCopy
            implements Runnable {
        /**
         * The stream used to read process output.
         */
        private final InputStream processOutput;

        /**
         * The output type.
         */
        private final OutputType outputType;

        /**
         * The buffer receiving the process output.
         */
        private final ByteArrayOutputStream buffer;

        /**
         * The process result (used to collect errors).
         */
        private final ProcessResult result;

        /**
         * Constructor.
         *
         * @param processOutput The stream used to read process output.
         * @param outputType The output type.
         * @param buffer The buffer receiving the process output.
         * @param result The process result (used to collect errors).
         */
        ChannelCopy(InputStream processOutput, OutputType outputType,
                ByteArrayOutputStream buffer, ProcessResult result) {
            super();
            this.processOutput = processOutput;
            this.outputType = outputType;
            this.buffer = buffer;
            this.result = result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                Streams.channelCopy(processOutput, buffer);
            } catch (Exception error) {
                String message = "Error reading " + outputType.label() + " output from process.";
                IOException ioError = new IOException(message, error);
                result.addError(ioError);
            }
        }
    }
}