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

import javax.ws.rs.core.Response;

/**
 * A specialized error which returns a non-standard response code with the specified reply as a JSON text.
 */
public class AlternateStatus
        extends RuntimeException {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -8182020390113044021L;

    /**
     * The value to return.
     */
    private final Object value;

    /**
     * The desired HTTP status for the error.
     */
    private final Response.Status status;

    /**
     * Constructor.
     *
     * @param status The desired HTTP status for the response.
     */
    public AlternateStatus(Response.Status status) {
        this(status, null);
    }

    /**
     * Constructor.
     *
     * @param value The value to return.
     */
    public AlternateStatus(Object value) {
        this(Response.Status.OK, value);
    }

    /**
     * Constructor.
     *
     * @param status The desired HTTP status for the response.
     * @param value The value to return.
     */
    public AlternateStatus(Response.Status status, Object value) {
        super(status.getReasonPhrase());
        this.status = status;
        this.value = value;
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
     * Get the return value.
     *
     * @return The return value.
     */
    public Object getValue() {
        return value;
    }
}