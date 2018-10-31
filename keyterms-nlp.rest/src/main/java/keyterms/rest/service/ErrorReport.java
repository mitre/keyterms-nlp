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

package keyterms.rest.service;

import keyterms.util.text.Strings;

/**
 * An error report from the platform API's.
 */
public class ErrorReport {
    /**
     * The error name.
     */
    private final String errorName;

    /**
     * The error message.
     */
    private final String errorMessage;

    /**
     * The error trace.
     */
    private final String errorTrace;

    /**
     * Constructor.
     *
     * @param errorName The error name.
     * @param errorMessage The error message.
     * @param errorTrace The trace message.
     */
    public ErrorReport(String errorName, String errorMessage, String errorTrace) {
        super();
        this.errorName = Strings.trim(errorName);
        StringBuilder message = new StringBuilder();
        if (Strings.isBlank(errorMessage)) {
            message.append("no message");
        } else {
            message.append(errorMessage);
            int lineFeed = errorMessage.indexOf('\r');
            if (lineFeed == -1) {
                lineFeed = errorMessage.indexOf('\n');
            }
            if (lineFeed != -1) {
                message.setLength(lineFeed);
            }
        }
        this.errorMessage = Strings.trim(message);
        this.errorTrace = Strings.trim(errorTrace);
    }

    /**
     * Get the error name.
     *
     * @return The error name.
     */
    public String getErrorName() {
        return errorName;
    }

    /**
     * Get the error message.
     *
     * @return The error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get the error trace.
     *
     * @return The error trace.
     */
    public String getErrorTrace() {
        return errorTrace;
    }
}