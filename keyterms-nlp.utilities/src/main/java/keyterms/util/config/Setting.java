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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.lang.Resolver;
import keyterms.util.text.Formatter;
import keyterms.util.text.Parser;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.Parsers;
import keyterms.util.text.parser.ReflectiveParser;

/**
 * A setting is a keyed value with facilities for parsing, formatting, validating and resolving values from
 * configuration files.
 */
public class Setting<V> {
    /**
     * Get the default parser for the specified value type.
     *
     * @param type The value type.
     *
     * @return The default parser for the specified value type.
     */
    @SuppressWarnings("unchecked")
    static <V> Parser<V> getDefaultParser(Class<V> type) {
        Parser<V> parser = null;
        if ((Boolean.class.equals(type)) || (Boolean.TYPE.equals(type))) {
            parser = (Parser<V>)Parsers.BOOLEANS;
        }
        if ((Integer.class.equals(type)) || (Integer.TYPE.equals(type))) {
            parser = (Parser<V>)Parsers.INTEGERS;
        }
        if ((Long.class.equals(type)) || (Long.TYPE.equals(type))) {
            parser = (Parser<V>)Parsers.LONGS;
        }
        if ((Float.class.equals(type)) || (Float.TYPE.equals(type))) {
            parser = (Parser<V>)Parsers.FLOATS;
        }
        if ((Double.class.equals(type)) || (Double.TYPE.equals(type))) {
            parser = (Parser<V>)Parsers.DOUBLES;
        }
        if (String.class.equals(type)) {
            parser = (Parser<V>)Parser.of(Strings::toString);
        }
        if (Enum.class.isAssignableFrom(type)) {
            return (text) -> Parsers.parseEnum(type, text);
        }
        if (parser == null) {
            parser = new ReflectiveParser<>(type);
        }
        return parser;
    }

    /**
     * The synchronization lock for the setting.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The setting's name.
     */
    private String name;

    /**
     * The value type for the setting.
     */
    private final Class<V> type;

    /**
     * The text to value parser.
     */
    private Parser<V> parser;

    /**
     * A flag indicating whether a parsing error will be thrown for invalid text values.
     */
    private boolean errorOnParse;

    /**
     * The value to text converter.
     */
    private Formatter<V> formatter;

    /**
     * The value resolvers keyed to the conditions which trigger them.
     */
    private Resolver<V> resolver;

    /**
     * The conditions under which values should be rejected without error.
     */
    private final List<Predicate<V>> ignoreConditions = new ArrayList<>();

    /**
     * The conditions under which values should be rejected on error.
     */
    private final List<Predicate<V>> rejectConditions = new ArrayList<>();

    /**
     * A flag indicating whether multiple values are allowed for the setting.
     */
    private boolean multipleValues;

    /**
     * A flag indicating whether the setting is required to be set.
     */
    private boolean required;

    /**
     * The values that have been set on the setting.
     */
    private final List<V> values = new ArrayList<>();

    /**
     * The values that have been set on the setting.
     */
    private final List<V> defaultValues = new ArrayList<>();

    /**
     * A flag indicating whether to use system properties if no value is set during configuration.
     */
    private boolean useSystemProperties = false;

    /**
     * A flag indicating whether to use the system environment if no value is set during configuration.
     */
    private boolean useSystemEnvironment = false;

    /**
     * Constructor.
     *
     * @param name The setting name.
     * @param type The setting value type.
     */
    Setting(String name, Class<V> type) {
        super();
        if (Strings.isBlank(name)) {
            throw new NullPointerException("Setting name is required.");
        }
        if (type == null) {
            throw new NullPointerException("Setting value type is required.");
        }
        this.name = name.trim();
        this.type = type;
    }

    /**
     * Create a new duplicate setting with the specified change.
     *
     * @param original The original setting.
     * @param modifier The task which modifies the new setting.
     */
    private Setting(Setting<V> original, Consumer<Setting<V>> modifier) {
        super();
        name = original.name;
        type = original.type;
        parser = original.parser;
        errorOnParse = original.errorOnParse;
        formatter = original.formatter;
        resolver = original.resolver;
        ignoreConditions.addAll(original.ignoreConditions);
        rejectConditions.addAll(original.rejectConditions);
        multipleValues = original.multipleValues;
        required = original.required;
        defaultValues.addAll(original.defaultValues);
        useSystemProperties = original.useSystemProperties;
        useSystemEnvironment = original.useSystemEnvironment;
        modifier.accept(this);
    }

    /**
     * Get the setting name.
     *
     * @return The setting name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the logging topic for this object.
     *
     * @return The logging topic for this object.
     */
    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass().getSimpleName() + "[" + name + "]");
    }

    /**
     * Get a setting derived from this setting that uses the specified value parser.
     *
     * <p> Parsers are used to convert textual representations of value into their equivalent object representations.
     * </p>
     * <p/>
     * <p> The default parsing mechanism:
     * <ul>
     * <li> Attempts to create the object using a {@code String} value constructor. </li>
     * <li> Attempts to create the object using a static method {@code V valueOf(String)}. </li>
     * <li> Attempts to create the object using a static method {@code V fromString(String)}. </li>
     * </ul>
     * If the above fails, an {@code IllegalArgumentException} will be thrown. </p>
     *
     * <p> Note: Best practice would be for the parser and formatter to work as a unit, so that any text produced by
     * the formatter will reproduce the current value when passed through the parser. </p>
     *
     * @param parser The setting's parser.
     *
     * @return The derived setting.
     *
     * @see ReflectiveParser
     */
    Setting<V> withParser(Parser<V> parser) {
        if (parser == null) {
            throw new NullPointerException("Setting parser is required.");
        }
        return new Setting<>(this, (setting) -> setting.parser = parser);
    }

    /**
     * Get a setting derived from this setting that will throw errors on parsing problems.
     *
     * <p> By default, errors in parsing text values are ignored. </p>
     *
     * @return The derived setting.
     */
    Setting<V> withParsingErrors() {
        return (errorOnParse) ? this : new Setting<>(this, (setting) -> setting.errorOnParse = true);
    }

    /**
     * Get a setting derived from this setting that uses the specified formatter.
     *
     * <p> Formatters are used to convert object values into their equivalent textual representations. </p>
     * <p/>
     * <p> The default behavior is to convert the value to text using {@link Strings :toString}. </p>
     * <p> Note: The default formatting may return a value of {@code null}. </p>
     *
     * <p> Note: Best practice would be for the parser and formatter to work as a unit, so that any text produced by
     * the formatter will reproduce the current value when passed through the parser. </p>
     *
     * @param formatter The setting's formatter.
     *
     * @return The derived setting.
     */
    Setting<V> withFormatter(Formatter<V> formatter) {
        if (formatter == null) {
            throw new NullPointerException("Setting formatter is required.");
        }
        return new Setting<>(this, (setting) -> setting.formatter = formatter);
    }

    /**
     * Get a setting derived from this setting that resolves any attempt to set values as specified.
     *
     * @param resolver The value resolver.
     *
     * @return The derived setting.
     */
    Setting<V> withResolver(Resolver<V> resolver) {
        if (resolver == null) {
            throw new NullPointerException("Setting resolver is required.");
        }
        return new Setting<>(this, (setting) -> setting.resolver = resolver);
    }

    /**
     * Get a setting derived from this setting that rejects values meeting the specified condition as a warning.
     *
     * @param condition The value test.
     *
     * @return The derived setting.
     */
    Setting<V> ignoring(Predicate<V> condition) {
        if (condition == null) {
            throw new NullPointerException("Ignore condition is required.");
        }
        return new Setting<>(this, (setting) -> setting.ignoreConditions.add(condition));
    }

    /**
     * Get a setting derived from this setting that rejects values meeting the specified condition as an error.
     *
     * @param condition The value test.
     *
     * @return The derived setting.
     */
    Setting<V> rejecting(Predicate<V> condition) {
        if (condition == null) {
            throw new NullPointerException("Reject condition is required.");
        }
        return new Setting<>(this, (setting) -> setting.rejectConditions.add(condition));
    }

    /**
     * Get a setting derived from this setting that allows multiple values for the setting.
     *
     * @return The derived setting.
     */
    Setting<V> withMultipleValues() {
        return (multipleValues) ? this : new Setting<>(this, (setting) -> setting.multipleValues = true);
    }

    /**
     * Determine if the setting allows multiple values.
     *
     * @return A flag indicating whether the setting allows multiple values.
     */
    public boolean isMultiValued() {
        return multipleValues;
    }

    /**
     * Get a setting derived from this setting that indicates that the setting must be modified during a
     * configuration phase.
     *
     * @return The derived setting.
     */
    Setting<V> required() {
        return (required) ? this : new Setting<>(this, (setting) -> setting.required = true);
    }

    /**
     * Determine if the setting is required to be set during a configuration phase.
     *
     * @return A flag indicating whether the setting is required to be set.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Get a setting derived from this setting that has the specified default value.
     *
     * @param defaultValue The default value.
     *
     * <p> Note: Default values are not resolved or evaluated for validity. </p>
     * <p> Note: Multiple default values may be specified for settings which allow multiple values. </p>
     *
     * @return The derived setting.
     */
    Setting<V> withDefault(V defaultValue) {
        return new Setting<>(this, (setting) -> setting.defaultValues.add(defaultValue));
    }

    /**
     * Use system properties to obtain a value if no explicit value or default value is set.
     *
     * @return The derived setting.
     */
    Setting<V> useSystemProperties() {
        return (useSystemProperties) ? this : new Setting<>(this, (setting) -> setting.useSystemProperties = true);
    }

    /**
     * Determine if the setting uses system properties to obtain a value if no explicit value or default value is set.
     *
     * @return A flag indicating whether the setting uses system properties to obtain a value if no explicit value or
     * default value is set.
     */
    public boolean usesSystemProperties() {
        return useSystemProperties;
    }

    /**
     * Use the system environment to obtain a value if no explicit value or default value is set.
     *
     * @return The derived setting.
     */
    Setting<V> useSystemEnvironment() {
        return (useSystemEnvironment) ? this : new Setting<>(this, (setting) -> setting.useSystemEnvironment = true);
    }

    /**
     * Determine if the setting uses the system environment to obtain a value if no explicit value or default value is
     * set.
     *
     * @return A flag indicating whether the setting uses the system environment to obtain a value if no explicit
     * value or default value is set.
     */
    public boolean usesSystemEnvironment() {
        return useSystemProperties;
    }

    /**
     * Determine if a value has been explicitly set on the setting.
     *
     * @return A flag indicating whether a value has been explicitly set on the setting.
     */
    public boolean isValueSet() {
        lock.readLock().lock();
        try {
            return !values.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Determine if a value has been explicitly set on the setting.
     *
     * @return A flag indicating whether a value has been explicitly set on the setting.
     */
    public boolean isDefaultSet() {
        lock.readLock().lock();
        try {
            return !defaultValues.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the current value for the setting.
     *
     * <p> If the setting value has not been explicitly set, and there is no default for the setting, a value of
     * {@code null} will be returned. </p>
     * <p> For settings which allow multiple values, this method returns only the last value that was set. </p>
     *
     * @return The current value for the setting.
     */
    public V getValue() {
        V value = null;
        lock.readLock().lock();
        try {
            if (values.isEmpty()) {
                if (!defaultValues.isEmpty()) {
                    value = defaultValues.get(defaultValues.size() - 1);
                }
            } else {
                value = values.get(values.size() - 1);
            }
        } finally {
            lock.readLock().unlock();
        }
        return value;
    }

    /**
     * Get the current values for the setting.
     *
     * <p> If the setting value has not been explicitly set, and there is no default for the setting, an empty set will
     * be returned. </p>
     * <p> For single value settings, only a single value will be returned in the set. </p>
     *
     * @return A set of the current values for the setting.
     */
    public Set<V> getValues() {
        Set<V> values = new LinkedHashSet<>();
        lock.readLock().lock();
        try {
            if (multipleValues) {
                if (this.values.isEmpty()) {
                    if (!defaultValues.isEmpty()) {
                        values.addAll(defaultValues);
                    }
                } else {
                    values.addAll(this.values);
                }
            } else {
                if ((!this.values.isEmpty()) || (!defaultValues.isEmpty())) {
                    values.add(getValue());
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return values;
    }

    /**
     * Set the value for the setting.
     *
     * <p> The new value will be accepted as the resolved value, if the resolved value for the setting does not meet
     * the setting's conditions for being ignored or rejected. </p>
     *
     * <p> If the setting allows multiple values, the new value will be added to the collection of settings. </p>
     *
     * @param value The new value.
     */
    public void setValue(V value) {
        lock.writeLock().lock();
        try {
            V resolved = (resolver != null) ? resolver.resolve(value) : value;
            boolean ignored = false;
            for (Predicate<V> condition : ignoreConditions) {
                if (condition.test(resolved)) {
                    getLogger().warn("Ignoring value {}.", value);
                    ignored = true;
                    break;
                }
            }
            if (!ignored) {
                for (Predicate<V> condition : rejectConditions) {
                    if (condition.test(resolved)) {
                        getLogger().error("Invalid value {}.", value);
                        throw new IllegalArgumentException(getLogger().getName() + ": Invalid value: " + value);
                    }
                }
                if (!multipleValues) {
                    values.clear();
                }
                values.add(resolved);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Set the value for the setting given a textual representation of the desired value.
     *
     * <p> This method expects a single value being represented in the text. </p>
     *
     * @param text The value text.
     */
    public void fromText(CharSequence text) {
        V value = null;
        boolean parsed = false;
        try {
            value = parser.parse(text);
            parsed = true;
        } catch (Exception error) {
            if (errorOnParse) {
                throw new IllegalArgumentException("Invalid value text: \"" + text + "\"");
            } else {
                getLogger().warn("Invalid value text: \"{}\"", text);
            }
        }
        if (parsed) {
            setValue(value);
        }
    }

    /**
     * Get a textual representation of the setting as it would be used in a configuration file.
     *
     * <p> Blank text will be returned for settings that have not been explicitly altered. </p>
     *
     * @return A textual representation of the setting.
     */
    public String asText() {
        StringBuilder asText = new StringBuilder();
        if (multipleValues) {
            asText.append('[');
            Set<V> values = getValues();
            if (!values.isEmpty()) {
                getValues().forEach(v -> asText.append("\n    ").append(asText(v)).append(','));
                asText.setLength(asText.length() - 1);
                asText.append('\n');
            }
            asText.append("]");

        } else {
            asText.append(asText(getValue()));
        }
        return asText.toString();
    }

    /**
     * Convert the specified value to text with the appropriate escapes.
     *
     * @param value The value.
     *
     * @return The escaped value.
     */
    private String asText(V value) {
        String asText = (value != null) ? formatter.asText(value) : "";
        if (asText != null) {
            asText = asText.replaceAll("#", "\\#");
            StringBuilder escaped = new StringBuilder();
            for (int ci = 0; ci < asText.length(); ci++) {
                char c = asText.charAt(ci);
                if (ConfigurationParser.REQUIRED_ESCAPES.contains(c)) {
                    escaped.append('\\');
                }
                escaped.append(c);
            }
            asText = escaped.toString();
        } else {
            asText = "";
        }
        return asText;
    }

    /**
     * Clear all current values from the setting.
     */
    public void reset() {
        lock.writeLock().lock();
        try {
            values.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Set the default parser and formatter if not already set, and log warnings for conditions within the parser which
     * are legal but inadvisable.
     */
    Setting<V> validate() {
        if (parser == null) {
            parser = getDefaultParser(type);
        }
        if (formatter == null) {
            formatter = Strings::toString;
        }
        if ((!multipleValues) && (defaultValues.size() > 1)) {
            getLogger().warn("Multiple default values for single value setting.");
        }
        if ((required) && (!defaultValues.isEmpty())) {
            getLogger().warn("Required setting with default value(s) specified.");
        }
        if ((!required) && (!multipleValues) && (defaultValues.isEmpty())) {
            getLogger().warn("Optional setting with no default value(s) specified.");
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (this == obj);
        if ((!equals) && (obj instanceof Setting)) {
            Setting<?> setting = (Setting)obj;
            equals = Objects.equals(name, setting.name);
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + name + ']';
    }
}