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

package keyterms.util.config;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;
import keyterms.util.text.Strings;

/**
 * A configuration is a group of settings that are configured as a group.
 */
public class Configuration {
    /**
     * The settings to configure.
     */
    private final LinkedHashMap<String, Setting<?>> settings = new LinkedHashMap<>();

    /**
     * Constructor.
     */
    public Configuration(Collection<Setting<?>> settings) {
        super();
        if (settings != null) {
            settings.forEach(this::addSetting);
        }
    }

    /**
     * Constructor.
     */
    public Configuration(Setting... settings) {
        super();
        if (settings != null) {
            Stream.of(settings).forEach(this::addSetting);
        }
    }

    /**
     * Get the logging topic for this object.
     *
     * @return The logging topic for this object.
     */
    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Get the number of settings in this configuration.
     *
     * @return The number of settings in this configuration.
     */
    public int size() {
        return settings.size();
    }

    /**
     * Get the settings in this configuration.
     *
     * @return The configuration settings.
     */
    public Collection<Setting<?>> getSettings() {
        return settings.values();
    }

    /**
     * Get the setting with the specified name.
     *
     * @param name The setting name.
     *
     * @return The specified setting.
     */
    public Setting<?> getSetting(String name) {
        return settings.get(name);
    }

    /**
     * Add the specified setting to the configuration.
     *
     * @param setting The setting.
     */
    private void addSetting(Setting<?> setting) {
        if (setting == null) {
            throw new NullPointerException("Setting is required.");
        }
        if (settings.containsKey(setting.getName())) {
            logWarning("Duplicate setting: {}", setting.getName());
        }
        settings.put(setting.getName(), setting);
    }

    /**
     * Log the specified warning message.
     *
     * @param message The warning message.
     * @param messageParameters The warning message parameters.
     */
    public void logWarning(String message, Object... messageParameters) {
        getLogger().warn(message, messageParameters);
    }

    /**
     * Log the specified error message.
     *
     * @param message The error message.
     * @param messageParameters The error message parameters.
     */
    public void logError(String message, Object... messageParameters) {
        getLogger().error(message, messageParameters);
    }

    /**
     * Return all settings to their initial state.
     */
    public void reset() {
        settings.values().forEach(Setting::reset);
    }

    /**
     * Configure the settings in this configuration.
     *
     * @param textValues The setting values obtained from parsing a configuration file.
     */
    public void configure(List<Keyed<String, List<String>>> textValues) {
        if (textValues != null) {
            int entryNumber = 0;
            for (Keyed<String, List<String>> entry : textValues) {
                entryNumber++;
                String name = entry.getKey();
                if (Strings.hasText(name)) {
                    Setting<?> setting = settings.get(name);
                    if (setting != null) {
                        List<String> values = entry.getValue();
                        if ((values != null) && (!values.isEmpty())) {
                            values.forEach(setting::fromText);
                        } else {
                            logWarning("Configuration entry #{} for {} has no values.", entryNumber, name);
                        }
                    } else {
                        logError("No such setting: {}", name);
                    }
                } else {
                    logError("Configuration entry #{} has no setting key.", entryNumber);
                }
            }
        }
        configureFromSystem();
    }

    /**
     * Check the required settings have valid values.
     */
    public void checkRequiredSettings() {
        settings.values().forEach(setting -> {
            if ((!setting.isValueSet()) && (setting.isRequired())) {
                logError("Required setting {} was not set.", setting.getName());
                throw new IllegalStateException("Required setting " + setting.getName() + " was not set.");
            }
        });
    }

    /**
     * Configure the settings in this configuration from environment variables or system variables of the same name.
     *
     * <p> Note: Setting values will only be modified if they have not been explicitly set. </p>
     *
     * <p> Note: All system properties and environment variables are checked in exact case, upper case and lower case
     * forms of the name (in that order of preference). All settings are also checked for versions of the name where
     * all {@code '.'} characters are replace with {@code '_'} characters. </p>
     *
     * <p> E.G. {@code 'Test.Setting'} would be checked as <br> {@code 'Test.Setting'}, {@code 'TEST.SETTING'},
     * {@code 'test.setting'}, {@code 'Test_Setting'}, {@code 'TEST_SETTING'} and {@code 'test_setting'} in sequence.
     * </p>
     */
    public void configureFromSystem() {
        settings.values().forEach((setting) -> {
            if (!setting.isValueSet()) {
                boolean usesSystemProperties = setting.usesSystemProperties();
                boolean usesSystemEnvironment = setting.usesSystemEnvironment();
                if ((usesSystemProperties) || (usesSystemEnvironment)) {
                    String property = setting.getName();
                    Set<String> keys = Bags.orderedSet(
                            property, property.toUpperCase(), property.toLowerCase(),
                            property.replaceAll("\\.", "_"),
                            property.toUpperCase().replaceAll("\\.", "_"),
                            property.toLowerCase().replaceAll("\\.", "_")
                    );
                    boolean set = false;
                    if (usesSystemProperties) {
                        Properties properties = System.getProperties();
                        for (String key : keys) {
                            if ((!set) && (properties.containsKey(key))) {
                                set = true;
                                setting.fromText(System.getProperty(key));
                            }
                        }
                    }
                    if ((!set) && (usesSystemEnvironment)) {
                        Map<String, String> environment = System.getenv();
                        for (String key : keys) {
                            if ((!set) && (environment.containsKey(key))) {
                                set = true;
                                setting.fromText(System.getenv(key));
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Reset and update the configuration.
     *
     * @param textValues The setting values obtained from parsing a configuration file.
     */
    public void reconfigure(List<Keyed<String, List<String>>> textValues) {
        reset();
        configure(textValues);
    }

    /**
     * Get a textual representation of the configuration as it would be used in a configuration file.
     *
     * @return A textual representation of the setting.
     */
    public String asText() {
        StringBuilder asText = new StringBuilder();
        settings.values().stream()
                .sorted(Comparator.comparing(Setting::getName))
                .forEach((setting) ->
                        asText.append(setting.getName()).append(" = ").append(setting.asText()).append('\n'));
        return asText.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + settings.size() + " settings]";
    }
}