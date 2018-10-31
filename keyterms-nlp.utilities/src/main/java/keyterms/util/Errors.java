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

package keyterms.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Methods for working with common exception handling.
 */
public final class Errors {
    /**
     * Ensure instances of {@code Error} are re-thrown.
     *
     * @param error The error to check.
     */
    public static void check(Throwable error) {
        if (error instanceof Error) {
            throw (Error)error;
        }
    }

    /**
     * Convert the specified error to a textual representation of its stack trace.
     *
     * @param error The error.
     *
     * @return The textual representation of the specified error's stack trace.
     */
    public static String stackTraceOf(Throwable error) {
        String stackTrace = null;
        if (error != null) {
            try (StringWriter stringWriter = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(stringWriter)) {
                error.printStackTrace(printWriter);
                stackTrace = stringWriter.toString();
            } catch (Exception exception) {
                check(exception);
            }
        }
        return stackTrace;
    }

    /**
     * Get a simplified error message for the specified error.
     *
     * @param error The error.
     *
     * @return The simplified error message for the specified error.
     */
    public static String getSimpleErrorMessage(Throwable error) {
        StringBuilder message = null;
        if (error != null) {
            message = new StringBuilder();
            Class<?> errorClass = error.getClass();
            String className = errorClass.getName();
            String simpleClassName = errorClass.getSimpleName();
            String errorMessage = error.getMessage();
            if (errorMessage != null) {
                message.append(errorMessage.trim());
                if (errorMessage.startsWith(className)) {
                    message.delete(0, errorClass.getPackage().getName().length() + 1);
                }
                int lineFeed = message.indexOf("\r");
                if (lineFeed == -1) {
                    lineFeed = message.indexOf("\n");
                }
                if (lineFeed != -1) {
                    message.setLength(lineFeed);
                }
                if (!message.toString().startsWith(simpleClassName)) {
                    message.insert(0, ": ");
                    message.insert(0, simpleClassName);
                }
            } else {
                message.append(simpleClassName);
                message.append(": no message");
            }
        }
        return (message != null) ? message.toString().trim() : null;
    }

    /**
     * Get the class name of the class most directly calling methods within this class.
     *
     * @return The class name of the class most directly calling methods within this class.
     */
    static String getCallingClassName() {
        Throwable stackTraceError = new RuntimeException("Trace");
        StackTraceElement[] stackTrace = stackTraceError.getStackTrace();
        return Arrays.stream(stackTrace)
                .filter(Objects::nonNull)
                .map(StackTraceElement::getClassName)
                .filter((n) -> !Errors.class.getName().equals(n))
                .findFirst()
                .orElse(null);
    }

    /**
     * Ignore the specified error.
     *
     * <p> If the error is an instance of {@code Error}, the error will be rethrown. </p>
     * <p> This method may choose to log a warning message to indicate that a global trap ignored an error. </p>
     *
     * @param error The error.
     */
    public static void ignore(Throwable error) {
        if (error != null) {
            check(error);
            String callingClassName = getCallingClassName();
            Logger errorLogger = LoggerFactory.getLogger(callingClassName);
            StringBuilder message = new StringBuilder();
            message.append(error.getClass().getSimpleName());
            message.append(" ignored by global trap.");
            boolean outputMessage = false;
            if (errorLogger.isTraceEnabled()) {
                // Full stack trace if trace level logging output is enabled.
                message.append('\n').append(stackTraceOf(error));
                outputMessage = true;
            }
            if ((!outputMessage) && (errorLogger.isDebugEnabled())) {
                // Simplified stack trace if debug level logging output is enabled.
                String errorMessage = getSimpleErrorMessage(error);
                message.append("\n").append(errorMessage);
                StackTraceElement[] trace = error.getStackTrace();
                for (int t = 0; t < trace.length; t++) {
                    if (trace[t].getClassName().equals(callingClassName)) {
                        if (t > 0) {
                            message.append("\n\tat ").append(trace[t - 1]);
                        }
                        message.append("\n\tat ").append(trace[t]);
                        if ((t == 0) && (t < (trace.length - 1))) {
                            message.append("\n\tat ").append(trace[t + 1]);
                        }
                        break;
                    }
                }
                outputMessage = true;
            }
            if (errorLogger.isInfoEnabled()) {
                // No stack trace if debug info logging output is enabled.
                outputMessage = true;
            }
            // No output if warn or error level logging output is enabled.
            if (outputMessage) {
                errorLogger.warn(message.toString());
            }
        }
    }

    /**
     * Constructor.
     */
    private Errors() {
        super();
    }
}