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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.rest.json.JsonAdapters;
import keyterms.rest.json.JsonConfigParser;
import keyterms.util.collect.Keyed;
import keyterms.util.config.Configuration;
import keyterms.util.config.Setting;
import keyterms.util.config.SettingFactory;
import keyterms.util.io.Encoding;
import keyterms.util.io.IO;
import keyterms.util.time.Duration;

/**
 * A REST accessible search service implementation which proxies into an ElasticSearch instance.
 */
//@Path("/")
//@WebListener
public abstract class RestService
        extends ResourceConfig
        implements ServletContextListener {
    /**
     * The context lock.
     */
    private static final ReentrantReadWriteLock CONTEXT_LOCK = new ReentrantReadWriteLock();

    /**
     * A flag indicating whether to include stack trace information in error reports.
     */
    private static final Setting<Boolean> INCLUDE_TRACES = new SettingFactory<>(
            "error.trace", Boolean.class)
            .withDefault(false)
            .build();

    /**
     * A flag indicating whether the context is in an active state.
     */
    private static boolean contextUp = false;

    /**
     * The time of the service start.
     */
    private static long serviceStart;

    /**
     * The service configuration.
     */
    private static Configuration configuration;

    /**
     * The servlet context.
     */
    @Context
    private ServletContext context;

    /**
     * Constructor.
     */
    public RestService() {
        super();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> baseProperties = getProperties();
        if (baseProperties != null) {
            properties.putAll(baseProperties);
        }
        properties.put("jersey.config.disableJsonProcessing", true);
        properties.put("jersey.config.server.disableMoxyJson", true);
        setProperties(properties);
        register(MultiPartFeature.class);
    }

    /**
     * Get the logging topic for the service.
     *
     * @return The logging topic for the service.
     */
    protected Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Get a list of the setting fields.
     *
     * @return The specified list of fields.
     */
    private List<Field> getSettingFields() {
        ArrayList<Field> settingFields = new ArrayList<>();
        Class<?> currentClass = getClass();
        while (RestService.class.isAssignableFrom(currentClass)) {
            for (Field field : currentClass.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if ((Modifier.isStatic(modifiers)) &&
                        (Setting.class.isAssignableFrom(field.getType()))) {
                    field.setAccessible(true);
                    settingFields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return settingFields;
    }

    /**
     * Create the class configuration by locating all of the static {@code Setting} fields.
     */
    private Configuration getServiceConfiguration() {
        if (configuration == null) {
            Map<String, Setting<?>> settings = new LinkedHashMap<>();
            List<Field> fields = getSettingFields();
            for (Field field : fields) {
                try {
                    Setting<?> setting = (Setting)field.get(this);
                    String name = setting.getName();
                    if (settings.containsKey(name)) {
                        getLogger().error("Duplicate setting name: {}", name);
                    } else {
                        settings.put(name, setting);
                    }
                } catch (Exception error) {
                    getLogger().error("Could not access setting field {}.", field.getName(), error);
                }
            }
            configuration = new Configuration(new HashSet<>(settings.values()));
        }
        return configuration;
    }

    /**
     * Get the application web root path.
     *
     * @return The application web root path.
     */
    protected java.nio.file.Path getWebRoot() {
        return IO.toPath(context.getRealPath("/"));
    }

    /**
     * Find the specified file in the service context path.
     *
     * @param fileName The file name.
     *
     * @return The specified file.
     *
     * @throws IOException for input/output errors
     */
    protected File findFile(String fileName)
            throws IOException {
        return IO.normalize(IO.find(getWebRoot(), (p) -> fileName.equals(IO.getName(p)))).toFile();
    }

    /**
     * Get the configuration file for the service.
     *
     * @return The configuration file for the service.
     *
     * @throws IOException for input/output errors
     */
    protected File getConfigurationFile()
            throws IOException {
        return findFile("service.json");
    }

    /**
     * Configure the service from the configuration files.
     *
     * @throws IOException for input/output errors
     */
    protected void configureService()
            throws IOException {
        CONTEXT_LOCK.writeLock().lock();
        try {
            File settingsFile = getConfigurationFile();
            if (settingsFile != null) {
                getLogger().debug("Service settings file: {}", settingsFile);
                String settingsContents = IO.readText(settingsFile, Encoding.UTF8);
                List<Keyed<String, List<String>>> settingValues = new JsonConfigParser().parse(settingsContents);
                if (settingValues != null) {
                    getServiceConfiguration().configure(settingValues);
                    getLogger().info("Service Settings:\n    {}",
                            configuration.asText().replaceAll("\n", "\n    "));
                }
            } else {
                getLogger().warn("Could not locate service settings file.");
            }
        } finally {
            CONTEXT_LOCK.writeLock().unlock();
        }
    }

    /**
     * Determine if the service is active.
     */
    private boolean isActive() {
        CONTEXT_LOCK.readLock().lock();
        try {
            return contextUp;
        } finally {
            CONTEXT_LOCK.readLock().unlock();
        }
    }

    /**
     * Ensure the service is available.
     *
     * @return A flag indicating whether the service is available.
     */
    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean ping() {
        if (!isActive()) {
            throw new AlternateStatus(
                    Response.Status.SERVICE_UNAVAILABLE,
                    "Service is temporarily unavailable.");
        }
        return true;
    }

    /**
     * Get the service up-time.
     *
     * @return The service up-time.
     */
    @GET
    @Path("up_time")
    @Produces(MediaType.APPLICATION_JSON)
    public Duration upTime() {
        return (isActive())
                ? new Duration(System.currentTimeMillis() - serviceStart, TimeUnit.MILLISECONDS)
                : Duration.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void contextInitialized(ServletContextEvent servletContextEvent) {
        JsonAdapters.initJsonHandling();
        if (!contextUp) {
            CONTEXT_LOCK.writeLock().lock();
            try {
                if (!contextUp) {
                    getLogger().info("Initializing servlet context.");
                    context = servletContextEvent.getServletContext();
                    try {
                        configureService();
                    } catch (Exception error) {
                        getLogger().error("Service configuration error.", error);
                        throw new WebApplicationException("Service configuration error.", error);
                    }
                    ErrorHandler.setIncludeTraces(INCLUDE_TRACES.getValue());
                    ErrorHandler.setLogger(getLogger());
                    try {
                        startup();
                    } catch (WebApplicationException applicationError) {
                        throw applicationError;
                    } catch (Exception error) {
                        throw new WebApplicationException("Service startup error.", error);
                    }
                    contextUp = true;
                    serviceStart = System.currentTimeMillis();
                    ConnectionHandler.setActive(true);
                    getLogger().info("Context startup complete.");
                }
            } finally {
                CONTEXT_LOCK.writeLock().unlock();
            }
        }
    }

    /**
     * Setup service specific resources.
     */
    protected abstract void startup();

    /**
     * {@inheritDoc}
     */
    @Override
    public final void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (contextUp) {
            CONTEXT_LOCK.writeLock().lock();
            try {
                if (contextUp) {
                    getLogger().info("Destroying servlet context.");
                    ConnectionHandler.setActive(false);
                    context = null;
                    try {
                        shutdown();
                    } catch (Exception error) {
                        getLogger().error("Error during service shutdown.", error);
                    }
                    unloadJdbcDrivers();
                    contextUp = false;
                    getLogger().info("Context shutdown complete.");
                }
            } finally {
                CONTEXT_LOCK.writeLock().unlock();
            }
        }
    }

    /**
     * UnRegister locally registered JDBC drivers.
     */
    private void unloadJdbcDrivers() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            // Only unload drivers loaded in the web applications context.
            if (driver.getClass().getClassLoader() == contextClassLoader) {
                try {
                    DriverManager.deregisterDriver(driver);
                    getLogger().info("Unloaded JDBC driver {}", driver);
                } catch (Exception error) {
                    getLogger().error("Could not unload JDBC driver: {}", driver, error);
                }
            }
        }
    }

    /**
     * Clean up service specific resources prior to context destruction.
     */
    protected abstract void shutdown();
}