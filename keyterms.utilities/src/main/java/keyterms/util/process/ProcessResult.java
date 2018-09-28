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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import keyterms.util.Errors;

/**
 * The results of running a command line process and fully reading its output streams.
 */
public class ProcessResult {
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
     * The input/output character encoding for the process.
     */
    private final Charset processEncoding;

    /**
     * A flag indicating whether the process produces error output.
     */
    private final boolean hasErrorOutput;

    /**
     * The buffer for receiving standard output from the process.
     */
    private ByteArrayOutputStream standardBuffer;

    /**
     * The standard output of the process.
     */
    private String standardOutput;

    /**
     * The buffer for receiving error output from the process.
     */
    private ByteArrayOutputStream errorBuffer;

    /**
     * The error output of the process.
     */
    private String errorOutput;

    /**
     * The process exit code.
     */
    private Integer exitCode;

    /**
     * The errors encountered attempting to run the process.
     */
    private final List<Throwable> errors = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param encoding The input/output character encoding for the process.
     * @param processRedirectsErrorStream A flag indicating whether the process produces error output.
     */
    ProcessResult(Charset encoding, boolean processRedirectsErrorStream) {
        super();
        processEncoding = (encoding != null) ? encoding : Charset.defaultCharset();
        hasErrorOutput = !processRedirectsErrorStream;
        standardBuffer = new ByteArrayOutputStream();
        if (hasErrorOutput) {
            errorBuffer = new ByteArrayOutputStream();
        }
    }

    /**
     * Get the buffer for receiving standard output from the process.
     *
     * @return The buffer for receiving standard output from the process.
     */
    ByteArrayOutputStream getStandardBuffer() {
        return standardBuffer;
    }

    /**
     * Get the buffer for receiving error output from the process.
     *
     * @return The buffer for receiving error output from the process.
     */
    ByteArrayOutputStream getErrorBuffer() {
        return errorBuffer;
    }

    /**
     * Add an error condition to the results.
     *
     * @param error The error condition.
     */
    void addError(Throwable error) {
        errors.add(error);
    }

    /**
     * Notification the the process has exited.
     *
     * @param exitCode The process exit code.
     */
    void exit(int exitCode) {
        this.exitCode = exitCode;
        if (standardBuffer != null) {
            try {
                standardOutput = standardBuffer.toString(processEncoding.name());
                close(standardBuffer);
                standardBuffer = null;
            } catch (Exception error) {
                errors.add(new IOException("Could not decode process standard output.", error));
            }
        }
        if (errorBuffer != null) {
            try {
                errorOutput = errorBuffer.toString(processEncoding.name());
                close(errorBuffer);
                errorBuffer = null;
            } catch (Exception error) {
                errors.add(new IOException("Could not decode process error output.", error));
            }
        }
    }

    /**
     * Get the input/output character encoding for the process.
     *
     * @return The input/output character encoding for the process.
     */
    public Charset getProcessEncoding() {
        return processEncoding;
    }

    /**
     * Determine if the process produced error output.
     *
     * @return A flag indicating whether the process produced error output.
     */
    public boolean hasErrorOutput() {
        return hasErrorOutput;
    }

    /**
     * Get the standard output of the process.
     *
     * @return The standard output of the process.
     */
    public String getStandardOutput() {
        return standardOutput;
    }

    /**
     * Get the error output of the process.
     *
     * @return The error output of the process.
     */
    public String getErrorOutput() {
        return errorOutput;
    }

    /**
     * Get the process exit code.
     *
     * @return The process exit code.
     */
    public Integer getExitCode() {
        return exitCode;
    }

    /**
     * Get the errors generated while executing the process.
     *
     * @return The errors generated while executing the process.
     */
    public List<Throwable> getErrors() {
        return errors;
    }
}