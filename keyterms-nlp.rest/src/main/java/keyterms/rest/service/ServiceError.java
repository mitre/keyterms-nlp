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

import java.util.Set;

import javax.ws.rs.core.Response;

import keyterms.util.Errors;
import keyterms.util.collect.Bags;

/**
 * A specialized error used to generate sparse output on error conditions, bypassing the normal Jersey mechanism.
 */
public class ServiceError
        extends RuntimeException {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -909849379369836748L;

    /**
     * The error classes which report as a client side error (400: BAD_REQUEST).
     */
    private static final Set<Class<? extends Throwable>> CLIENT_ERRORS = Bags.staticSet(
            IllegalArgumentException.class
    );

    /**
     * Get the default response status for the specified error.
     *
     * @param error The error.
     *
     * @return The default response status for the specified error class.
     */
    private static Response.Status getDefaultResponse(Throwable error) {
        return ((error != null) && (CLIENT_ERRORS.contains(error.getClass())))
                ? Response.Status.BAD_REQUEST
                : Response.Status.INTERNAL_SERVER_ERROR;
    }

    /**
     * The desired HTTP status for the error.
     */
    private final Response.Status status;

    /**
     * The error.
     */
    private final Throwable error;

    /**
     * Constructor.
     *
     * @param error The error.
     */
    public ServiceError(Throwable error) {
        this(null, error);
    }

    /**
     * Constructor.
     *
     * @param status The desired HTTP status for the response.
     * @param error The error.
     */
    public ServiceError(Response.Status status, Throwable error) {
        super();
        this.status = (status != null) ? status : getDefaultResponse(error);
        this.error = error;
    }

    /**
     * Get the desired HTTP status for the response.
     *
     * @return The desired HTTP status for the response.
     */
    public Response.Status getStatus() {
        return status;
    }

    /**
     * Get the error.
     *
     * @return The error.
     */
    public Throwable getError() {
        return error;
    }

    /**
     * Get the error report associated with this error.
     *
     * @param includeErrorTrace A flag indicating whether to include stack trace information.
     *
     * @return The error report associated with this error.
     */
    ErrorReport getReport(boolean includeErrorTrace) {
        String errorClass = ServiceError.class.getSimpleName();
        String errorMessage = "no message";
        String errorTrace = null;
        if (error != null) {
            errorClass = error.getClass().getSimpleName();
            errorMessage = Errors.getSimpleErrorMessage(error);
            errorMessage = (errorMessage != null)
                    ? errorMessage.replaceFirst("^.+: ", "")
                    : "no message";
            if (includeErrorTrace) {
                errorTrace = Errors.stackTraceOf(error);
            }
        }
        return new ErrorReport(errorClass, errorMessage, errorTrace);
    }
}