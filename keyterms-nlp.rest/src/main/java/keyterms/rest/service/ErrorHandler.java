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

import javax.inject.Singleton;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.rest.json.GsonUtil;
import keyterms.rest.json.JsonAdapters;

/**
 * The error handling mechanism for the REST services which produces JSON error message output.
 */
@Provider
@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class ErrorHandler
        extends Throwable
        implements ExceptionMapper<Throwable> {
    /**
     * A flag indicating whether stack trace information will be included in error messages.
     */
    private static boolean includeTraces = false;

    /**
     * The logging topic for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    /**
     * Set whether error messages will include stack trace information.
     *
     * @param includeTraces A flag indicating whether stack trace information will be included in error messages.
     */
    static void setIncludeTraces(boolean includeTraces) {
        ErrorHandler.includeTraces = includeTraces;
    }

    /**
     * Set the logging topic for this class.
     *
     * @param logger The logging topic for this class.
     */
    static void setLogger(Logger logger) {
        ErrorHandler.logger = logger;
    }

    /**
     * Constructor.
     */
    public ErrorHandler() {
        super();
        JsonAdapters.initJsonHandling();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(Throwable error) {
        Response.ResponseBuilder responseBuilder;
        if (error != null) {
            if (error instanceof AlternateStatus) {
                AlternateStatus alternateStatus = (AlternateStatus)error;
                Object value = alternateStatus.getValue();
                if (value instanceof Throwable) {
                    responseBuilder = handleError((Throwable)value);
                } else {
                    responseBuilder = Response.status(alternateStatus.getStatus())
                            .entity(GsonUtil.toJson(value))
                            .type(MediaType.APPLICATION_JSON);
                }
            } else {
                responseBuilder = handleError(error);
            }
        } else {
            responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return responseBuilder.build();
    }

    /**
     * Create a response builder for the specified error.
     *
     * @param error The error.
     *
     * @return A response builder for the specified error.
     */
    private Response.ResponseBuilder handleError(Throwable error) {
        if (logger.isTraceEnabled()) {
            logger.error("Error in request handling.", error);
        }
        ServiceError serviceError = (error instanceof ServiceError)
                ? (ServiceError)error
                : new ServiceError(error);
        return Response
                .status(serviceError.getStatus())
                .entity(serviceError.getReport(includeTraces))
                .type(MediaType.APPLICATION_JSON);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ErrorHandler);
    }
}