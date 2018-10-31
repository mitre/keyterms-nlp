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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import keyterms.util.collect.Keyed;
import keyterms.util.text.Parser;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.Parsers;

/**
 * A container for command line arguments with easier access to arguments as key-value pairs.
 */
public class Args {
    /**
     * The keyed value argument parser which converts "flag" parameters, such as {@code -flag}, transparently into
     * their equivalent "flag=true" representation.
     */
    private static final Parser<Keyed<String, String>> ARGS = text -> {
        Keyed<String, String> keyed = Parsers.KEYS.parse(text);
        if (keyed != null) {
            String key = keyed.getKey();
            String value = keyed.getValue();
            if ((key != null) && (value == null) && (key.matches("^[-+].*"))) {
                key = key.substring(1);
                keyed = new Keyed<>(key, "true");
            }
        }
        return keyed;
    };

    /**
     * The arguments as a list of key-value pairs.
     */
    private final List<Keyed<String, String>> asList;

    /**
     * The arguments as a map of key-value pairs.
     */
    private final Map<String, String> asMap;

    /**
     * Constructor.
     *
     * @param args The equivalent command line arguments.
     */
    public Args(String[] args) {
        super();
        List<Keyed<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            if (Strings.hasText(arg)) {
                Keyed<String, String> keyed = ARGS.parse(arg);
                list.add(keyed);
                map.put(keyed.getKey(), keyed.getValue());
            }
        }
        asList = Collections.unmodifiableList(list);
        asMap = Collections.unmodifiableMap(map);
    }

    /**
     * Get the arguments as a list of key-value pairs.
     *
     * @return The arguments as a list of key-value pairs.
     */
    public List<Keyed<String, String>> asList() {
        return asList;
    }

    /**
     * Get the arguments as a map of key-value pairs.
     *
     * @return The arguments as a map of key-value pairs.
     */
    public Map<String, String> asMap() {
        return asMap;
    }

    /**
     * Determine if a value is present for the specified command line parameter.
     *
     * @param key The command line parameter.
     *
     * @return A flag indicating whether a value is present for the specified command line parameter.
     */
    public boolean contains(String key) {
        return asMap.containsKey(key);
    }

    /**
     * Determine if the specified command line parameter is associated with non-blank text.
     *
     * @param key The command line parameters.
     *
     * @return A flag indicating whether the specified command line parameter is associated with non-blank text.
     */
    public boolean isSpecified(String key) {
        return Strings.hasText(asMap.get(key));
    }

    /**
     * Determine if the specified command line parameter has non-{@code null} text specified.
     *
     * @param key The command line parameter.
     *
     * @return A flag indicating whether the specified command line parameter has valid text.
     */
    public boolean isValidText(String key) {
        return isValid(key, Strings::toString);
    }

    /**
     * Determine if the specified command line parameter has a specified value that can be parsed as an integer value.
     *
     * @param key The command line parameter.
     *
     * @return A flag indicating whether the specified command line parameter has a valid integer value specified.
     */
    public boolean isValidInteger(String key) {
        return isValid(key, Parsers.INTEGERS);
    }

    /**
     * Determine if the specified command line parameter has a specified value that can be parsed as a double value.
     *
     * @param key The command line parameter.
     *
     * @return A flag indicating whether the specified command line parameter has a valid double value specified.
     */
    public boolean isValidDouble(String key) {
        return isValid(key, Parsers.DOUBLES);
    }

    /**
     * Determine if the specified command line parameter has a specified value that can be parsed as a double value.
     *
     * @param key The command line parameter.
     * @param enumClass The class containing the enumerated values.
     * @param <E> The enumerated value class.
     *
     * @return A flag indicating whether the specified command line parameter has a valid double value specified.
     */
    public <E extends Enum<E>> boolean isValidEnum(String key, Class<E> enumClass) {
        return isValid(key, (text) -> Parsers.parseEnum(enumClass, text));
    }

    /**
     * Determine if the specified command line parameter is both specified and parses to a non-{@code null} value with
     * the given parser.
     *
     * @param key The command line parameter.
     * @param parser The value parser.
     * @param <V> The parser output value class.
     *
     * @return A flag indicating whether the specified command line parameter is valid.
     */
    public <V> boolean isValid(String key, Parser<V> parser) {
        return ((isSpecified(key)) && (getValue(key, parser).orElse(null) != null));
    }

    /**
     * Get text value for the specified command line parameter.
     *
     * @param key The command line parameter.
     *
     * @return The text value for the specified command line parameter.
     */
    public Optional<String> getText(String key) {
        return getValue(key, Strings::toString);
    }

    /**
     * Get the boolean value for the specified command line parameter.
     *
     * @param key The command line parameter.
     *
     * @return The boolean value for the specified command line parameter.
     */
    public Optional<Boolean> getBoolean(String key) {
        return getValue(key, Parsers.BOOLEANS);
    }

    /**
     * Get the integer value for the specified command line parameter.
     *
     * @param key The command line parameter.
     *
     * @return The integer value for the specified command line parameter.
     */
    public Optional<Integer> getInteger(String key) {
        return getValue(key, Parsers.INTEGERS);
    }

    /**
     * Get the double value for the specified command line parameter.
     *
     * @param key The command line parameter.
     *
     * @return The double value for the specified command line parameter.
     */
    public Optional<Double> getDouble(String key) {
        return getValue(key, Parsers.DOUBLES);
    }

    /**
     * Get an enumerated value for the specified command line parameter.
     *
     * <p> Note: If several parameters may hold the same enumerated type, it may be more efficient to call
     * {@link #getValue(String, Parser)} with a prefabricated parser constructed using
     * {@link Parsers#parseEnum(Class, CharSequence)} (Class)}
     * since <em>this</em> method creates a new parser instance for each call. </p>
     *
     * @param key The command line parameter.
     * @param enumClass The enumerated class.
     * @param <E> The enumerated value class.
     *
     * @return The enumerated value for the specified command line parameter.
     */
    public <E extends Enum<E>> Optional<E> getEnum(String key, Class<E> enumClass) {
        return getValue(key, (text) -> Parsers.parseEnum(enumClass, text));
    }

    /**
     * Get the value for the specified command line parameter.
     *
     * @param key The command line parameter.
     * @param parser The value parser.
     * @param <V> The parser output value class.
     *
     * @return The value for the specified command line parameter.
     */
    public <V> Optional<V> getValue(String key, Parser<V> parser) {
        Optional<V> value;
        if (isSpecified(key)) {
            value = Optional.ofNullable(parser.parse(asMap.get(key), null));
        } else {
            value = Optional.empty();
        }
        return value;
    }

    /**
     * Get the text values for a command line parameter which may be repeated.
     *
     * @param key The command line parameter.
     *
     * @return The values associated with the command line parameter.
     */
    public List<String> getTextValues(String key) {
        return getValues(key, Strings::toString);
    }

    /**
     * Get the values for a command line parameter which may be repeated.
     *
     * @param key The command line parameter.
     * @param parser The value parser.
     * @param <V> The parser output value class.
     *
     * @return The values associated with the command line parameter.
     */
    public <V> List<V> getValues(String key, Parser<V> parser) {
        return asList.stream()
                .filter(k -> Objects.equals(key, k.getKey()))
                .map(Keyed::getValue)
                .map(v -> parser.parse(v, null))
                .collect(Collectors.toList());
    }

    /**
     * Get the text values listed without keys.
     *
     * @return The text values listed without keys.
     */
    public List<String> getNonKeyedTextValues() {
        return getNonKeyedValues(Strings::toString);
    }

    /**
     * Get the values listed without keys.
     *
     * <p> Note: Values that cannot be parsed as non-{@code null} values are discarded. </p>
     *
     * @param parser The value parser.
     * @param <V> The parser output value class.
     *
     * @return The values listed without keys.
     */
    public <V> List<V> getNonKeyedValues(Parser<V> parser) {
        return asList.stream()
                .filter(k -> k.getValue() == null)
                .map(Keyed::getKey)
                .map(parser::parse)
                .collect(Collectors.toList());
    }
}