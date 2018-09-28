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

package keyterms.util.text.parser;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;
import keyterms.util.lang.DecimalBytes;
import keyterms.util.lang.Enums;
import keyterms.util.text.Parser;
import keyterms.util.text.Strings;

/**
 * A collection of generically useful parsers.
 */
public final class Parsers {
    /**
     * Generic textual representations of {@code true} values.
     */
    public static final Set<String> TRUE_VALUES = Bags.staticSet("true", "t", "on", "yes", "y", "1");

    /**
     * Generic textual representations of {@code false} values.
     */
    public static final Set<String> FALSE_VALUES = Bags.staticSet("false", "f", "off", "no", "n", "0");

    /**
     * The pattern used to parse scalar numbers.
     */
    public static final Pattern SCALAR_PATTERN = Pattern.compile("([\\d.\\-+]+)\\s*(\\w*)");

    /**
     * A parser for boolean values that will accept any of {@link #TRUE_VALUES} or {@link #FALSE_VALUES} as inputs.
     */
    public static final Parser<Boolean> BOOLEANS = Parsers::parseBoolean;

    /**
     * An integer value parser.
     */
    public static final Parser<Integer> INTEGERS = Parsers::parseInteger;

    /**
     * A long integer value parser.
     */
    public static final Parser<Long> LONGS = Parsers::parseLong;

    /**
     * A float value parser.
     */
    public static final Parser<Float> FLOATS = Parsers::parseFloat;

    /**
     * A double value parser.
     */
    public static final Parser<Double> DOUBLES = Parsers::parseDouble;

    /**
     * A textual key value pair parser.
     */
    public static final Parser<Keyed<String, String>> KEYS = Parsers::parseKeyed;

    /**
     * A textual memory parameter parser.
     */
    public static final Parser<DecimalBytes> MEMORY = Parsers::parseMemory;

    /**
     * Parse a boolean value from the specified text.
     *
     * <p> A value of {@code true} will be returned if the text is in {@link #TRUE_VALUES}. </p>
     * <p> A value of {@code false} will be returned if the text is in {@link #FALSE_VALUES}. </p>
     * <p> An {@code IllegalArgumentException} will be thrown if the text is not in either
     * {@link #TRUE_VALUES} or {@link #FALSE_VALUES}. </p>
     *
     * @param text The text.
     *
     * @return The boolean value represented by the text.
     */
    public static boolean parseBoolean(CharSequence text) {
        String valueText = text.toString().trim().toLowerCase();
        Boolean value = null;
        if (TRUE_VALUES.contains(valueText)) {
            value = true;
        }
        if (FALSE_VALUES.contains(valueText)) {
            value = false;
        }
        if (value == null) {
            throw new IllegalArgumentException("Invalid boolean value: " + text);
        }
        return value;
    }

    /**
     * Trim and remove "_" and "," format markers from the presumed numeric text.
     *
     * @param text The text.
     *
     * @return The normalized text.
     */
    private static String normalizeNumericText(CharSequence text) {
        return text.toString().trim().replaceAll("[_,]+", Strings.EMPTY_STRING);
    }

    /**
     * Parse an integer value from the specified text.
     *
     * @param text The text.
     *
     * @return The integer value represented by the text.
     */
    public static Integer parseInteger(CharSequence text) {
        return Integer.parseInt(normalizeNumericText(text));
    }

    /**
     * Parse a long integer value from the specified text.
     *
     * @param text The text.
     *
     * @return The long integer value represented by the text.
     */
    public static Long parseLong(CharSequence text) {
        return Long.parseLong(normalizeNumericText(text));
    }

    /**
     * Parse a float value from the specified text.
     *
     * @param text The text.
     *
     * @return The float value represented by the text.
     */
    public static Float parseFloat(CharSequence text) {
        return Float.parseFloat(normalizeNumericText(text));
    }

    /**
     * Parse a double value from the specified text.
     *
     * @param text The text.
     *
     * @return The double value represented by the text.
     */
    public static double parseDouble(CharSequence text) {
        return Double.parseDouble(normalizeNumericText(text));
    }

    /**
     * Parse an enumerated value from the specified text.
     *
     * <p> Note: An {@code IllegalArgumentException} will be thrown if the specified type is not an enumerated type.
     * </p>
     *
     * @param type The class of the enumerated value.
     * @param text The text.
     * @param <E> The enumerated value class.
     *
     * @return The enumerated value represented by the text.
     */
    public static <E> E parseEnum(Class<E> type, CharSequence text) {
        return Enums.find(type, text);
    }

    /**
     * Parse the specified text as a textual key-value pair.
     *
     * <p> Key-Value pairs are expected in the format key=value. </p>
     * <p> The key name will be trimmed. </p>
     * <p> If no equal sign is present, the keyed value will be {@code null}. </p>
     *
     * @param text The text.
     *
     * @return The keyed value represented by the text.
     */
    public static Keyed<String, String> parseKeyed(CharSequence text) {
        return parseKeyed(text, "=");
    }

    /**
     * Parse the specified text as a textual key-value pair with the specified delimiter.
     *
     * <p> Key-Value pairs are expected in the format key=value. </p>
     * <p> Only the first instance of the delimiter is used in parsing. </p>
     * <p> The key name will be trimmed. </p>
     * <p> If no delimiter is present, the keyed value will be {@code null}. </p>
     *
     * @param text The text.
     * @param delimiter The literal delimiter sequence.
     *
     * @return The keyed value represented by the text.
     */
    public static Keyed<String, String> parseKeyed(CharSequence text, String delimiter) {
        Keyed<String, String> value = null;
        if (text != null) {
            String string = text.toString();
            int delimiterLength = Strings.length(delimiter);
            int delimiterIndex = (delimiterLength > 0) ? string.indexOf(delimiter) : -1;
            if (delimiterIndex != -1) {
                String k = string.substring(0, delimiterIndex).trim();
                String v = ((delimiterIndex + delimiterLength) < text.length())
                        ? string.substring(delimiterIndex + delimiterLength)
                        : Strings.EMPTY_STRING;
                value = new Keyed<>(k, v);
            } else {
                value = new Keyed<>(string.trim());
            }
        }
        return value;
    }

    /**
     * Parse the specified text as decimal bytes.
     *
     * @param text The text.
     *
     * @return The decimal bytes represented by the text.
     */
    public static DecimalBytes parseMemory(CharSequence text) {
        DecimalBytes decimalBytes = null;
        if (Strings.hasText(text)) {
            Matcher matcher = SCALAR_PATTERN.matcher(Strings.trim(text));
            if (matcher.matches()) {
                String ordinalText = matcher.group(1);
                String unitText = Strings.toLowerCase(Strings.trim(matcher.group(2)));
                DecimalBytes.Units unit = DecimalBytes.Units.BYTE;
                if (Strings.hasText(unitText)) {
                    unit = null;
                    if (!unitText.toLowerCase().endsWith("b")) {
                        unitText += "b";
                    }
                    for (DecimalBytes.Units u : DecimalBytes.Units.values()) {
                        if (u.getUnitLabel().equalsIgnoreCase(unitText)) {
                            unit = u;
                        }
                    }
                    if (unit == null) {
                        throw new IllegalArgumentException("Invalid byte units: " + unitText);
                    }
                }
                double ordinal = parseDouble(ordinalText);
                decimalBytes = new DecimalBytes(ordinal, unit);
            } else {
                throw new IllegalArgumentException("Not a byte representation: " + text);
            }
        }
        return decimalBytes;
    }

    /**
     * Constructor.
     */
    private Parsers() {
        super();
    }
}