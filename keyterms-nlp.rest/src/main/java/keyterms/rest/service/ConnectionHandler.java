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

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ContainerRequest;

import keyterms.util.collect.Bags;

/**
 * A connection request / response filter.
 */
@Provider
@Singleton
public class ConnectionHandler
        implements ContainerRequestFilter, ContainerResponseFilter {
    /**
     * The methods which may respond to requests when the service is alive but not available.
     * <p> These methods return service metadata and are potentially useful in diagnostics. </p>
     */
    private static final Set<String> META_METHODS = Bags.staticSet("ping", "up_time");

    /**
     * A flag indicating whether the associated service is active.
     */
    private static boolean active = false;

    /**
     * Set whether the associated service is active.
     *
     * @param active A flag indicating whether the associated service is active.
     */
    static void setActive(boolean active) {
        ConnectionHandler.active = active;
    }

    /**
     * Constructor.
     */
    public ConnectionHandler() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        if (!active) {
            String method = ((ContainerRequest)containerRequestContext).getPath(true);
            if (!META_METHODS.contains(method)) {
                throw new WebApplicationException(Response
                        .status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("Service is temporarily unavailable.")
                        .build());
            }
        }
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
        return (obj instanceof ConnectionHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ContainerRequestContext containerRequestContext,
            ContainerResponseContext containerResponseContext) {
        // String method = ((ContainerRequest)containerRequestContext).getPath(true);
    }
}