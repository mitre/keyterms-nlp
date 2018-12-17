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

import java.util.function.Predicate;

import keyterms.util.lang.Resolver;
import keyterms.util.text.Formatter;
import keyterms.util.text.Parser;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.ReflectiveParser;

/**
 * A setting factory acts as a constructor for {@link Setting} objects.
 */
public class SettingFactory<V> {
    /**
     * The setting being constructed.
     */
    private Setting<V> setting;

    /**
     * Constructor.
     *
     * @param name The name of the setting.
     * @param type The value type of the setting.
     */
    public SettingFactory(String name, Class<V> type) {
        super();
        setting = new Setting<>(name, type);
    }

    /**
     * Adjust the setting to use the specified parser.
     *
     * <p> Parsers are used to convert textual representations of value into their equivalent object representations.
     * </p>
     *
     * <p> The default parsing mechanism:</p>
     * <ul>
     * <li> Attempts to create the object using a {@code String} value constructor. </li>
     * <li> Attempts to create the object using a static method {@code V valueOf(String)}. </li>
     * <li> Attempts to create the object using a static method {@code V fromString(String)}. </li>
     * </ul>
     * <p>If the above fails, an {@code IllegalArgumentException} will be thrown. </p>
     *
     * <p> Note: Best practice would be for the parser and formatter to work as a unit, so that any text produced by
     * the formatter will reproduce the current value when passed through the parser. </p>
     *
     * @param parser The setting's parser.
     *
     * @return A reference to this factory for convenience in chaining.
     *
     * @see ReflectiveParser
     */
    public SettingFactory<V> withParser(Parser<V> parser) {
        setting = setting.withParser(parser);
        return this;
    }

    /**
     * Adjust the setting so that it will throw errors on parsing problems.
     *
     * <p> By default, errors in parsing text values are ignored. </p>
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> withParsingErrors() {
        setting = setting.withParsingErrors();
        return this;
    }

    /**
     * Adjust the setting so that it uses the specified formatter.
     *
     * <p> Formatters are used to convert object values into their equivalent textual representations. </p>
     *
     * <p> The default behavior is to convert the value to text using {@link Strings#toString}. </p>
     * <p> Note: The default formatting may return a value of {@code null}. </p>
     *
     * <p> Note: Best practice would be for the parser and formatter to work as a unit, so that any text produced by
     * the formatter will reproduce the current value when passed through the parser. </p>
     *
     * @param formatter The setting's formatter.
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> withFormatter(Formatter<V> formatter) {
        setting = setting.withFormatter(formatter);
        return this;
    }

    /**
     * Adjust the setting so that it resolves any attempt to set values as specified.
     *
     * @param resolver The value resolver.
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> withResolver(Resolver<V> resolver) {
        setting = setting.withResolver(resolver);
        return this;
    }

    /**
     * Adjust the setting so that it rejects values meeting the specified condition as a warning.
     *
     * @param condition The value test.
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> ignoring(Predicate<V> condition) {
        setting = setting.ignoring(condition);
        return this;
    }

    /**
     * Adjust the setting so that it rejects values meeting the specified condition as an error.
     *
     * @param condition The value test.
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> rejecting(Predicate<V> condition) {
        setting = setting.rejecting(condition);
        return this;
    }

    /**
     * Adjust the setting so that it allows multiple values.
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> withMultipleValues() {
        setting = setting.withMultipleValues();
        return this;
    }

    /**
     * Adjust the setting so that it indicates that the setting must be modified during a configuration phase.
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> required() {
        setting = setting.required();
        return this;
    }

    /**
     * Adjust the setting to have the specified default value.
     *
     * @param defaultValue The default value.
     *
     * <p> Note: Default values are not resolved or evaluated for validity. </p>
     * <p> Note: Multiple default values may be specified for settings which allow multiple values. </p>
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> withDefault(V defaultValue) {
        setting = setting.withDefault(defaultValue);
        return this;
    }

    /**
     * Use system properties to obtain a value if no explicit value or default value is set.
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> useSystemProperties() {
        setting = setting.useSystemProperties();
        return this;
    }

    /**
     * Use the system environment to obtain a value if no explicit value or default value is set.
     *
     * @return A reference to this factory for convenience in chaining.
     */
    public SettingFactory<V> useSystemEnvironment() {
        setting = setting.useSystemEnvironment();
        return this;
    }

    /**
     * Adjust the setting to have the specified default values.
     *
     * <p> Note: This method should only be used for settings that take multiple values. </p>
     *
     * @param defaultValues The default values.
     *
     * <p> Note: Default values are not resolved or evaluated for validity. </p>
     * <p> Note: Multiple default values may be specified for settings which allow multiple values. </p>
     *
     * @return A reference to this factory for convenience in chaining.
     */
    @SafeVarargs
    public final SettingFactory<V> withDefaults(V... defaultValues) {
        if (defaultValues != null) {
            for (V defaultValue : defaultValues) {
                setting = setting.withDefault(defaultValue);
            }
        }
        return this;
    }

    /**
     * Validate the get the setting from the factory.
     *
     * @return The validated setting.
     */
    public Setting<V> build() {
        setting = setting.validate();
        return setting;
    }
}