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

package keyterms.util.text;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Methods for working with character sequences.
 */
public final class Strings {
    /**
     * A constant reference to an empty string.
     */
    public static final String EMPTY_STRING = "";

    /**
     * The text pattern used to identify a generic line feed.
     */
    public static final Pattern LINE_BREAK = Pattern.compile("((\\n)|(\\r\\n?))");

    /**
     * The text pattern used to identify a generic line feed.
     */
    public static final Pattern LINE_BREAKS = Pattern.compile("[\\r\\n]+");

    /**
     * The pattern used to identify system property markers within setting values.
     */
    public static final Pattern SYS_PROPERTY = Pattern.compile("(\\$\\{([^${}]+)})");

    /**
     * Convert the specified value to a textual representation.
     *
     * <p> For non-{@code null} values, this method acts as {@link String#valueOf(Object)}. </p>
     * <p> This method returns {@code null} if the specified value is {@code null}. </p>
     *
     * @param value The value.
     *
     * @return The textual representation of the value.
     */
    public static String toString(Object value) {
        String asString = null;
        if (value != null) {
            asString = (value instanceof String) ? (String)value : String.valueOf(value);
        }
        return asString;
    }

    /**
     * Get the length (in characters) of the specified text.
     *
     * @param text The text.
     *
     * @return The number of characters in the specified text.
     */
    public static int length(CharSequence text) {
        return (text != null) ? text.length() : -1;
    }

    /**
     * Determine if the specified text is {@code null} or zero length.
     *
     * @param text The text.
     *
     * @return A flag indicting whether the specified text is {@code null} or zero length.
     */
    public static boolean isEmpty(CharSequence text) {
        return (length(text) <= 0);
    }

    /**
     * Determine if the specified text is {@code null}, zero length, or contains only whitespace.
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is {@code null}, zero length, or contains only whitespace.
     */
    public static boolean isBlank(CharSequence text) {
        return ((length(text) <= 0) || (isEmpty(trim(text))));
    }

    /**
     * Determine if the specified text is non-{@code null} and is not zero length when trimmed.
     *
     * <p> This is effectively the opposite of {@link #isBlank(CharSequence)}. </p>
     *
     * @param text The text.
     *
     * @return A flag indicating whether the specified text is non-{@code null} and is not zero length when trimmed.
     */
    public static boolean hasText(CharSequence text) {
        return ((length(text) > 0) && (!isEmpty(trim(text))));
    }

    /**
     * Get the trimmed version of the specified text.
     *
     * <p> This method will not alter the original text. </p>
     *
     * @param text The text.
     *
     * @return The trimmed text.
     */
    public static String trim(CharSequence text) {
        return (text != null) ? text.toString().trim() : null;
    }

    /**
     * Get a lower case version of the specified text.
     *
     * <p> A value of {@code null} will be returned if the specified text is {@code null}. </p>
     *
     * @param text The text.
     *
     * @return The lower case version of the specified text.
     */
    public static String toLowerCase(CharSequence text) {
        return (text != null) ? text.toString().toLowerCase() : null;
    }

    /**
     * Get a lower case version of the specified text.
     *
     * <p> A value of {@code null} will be returned if the specified text is {@code null}. </p>
     *
     * @param text The text.
     *
     * @return The lower case version of the specified text.
     */
    public static String toUpperCase(CharSequence text) {
        return (text != null) ? text.toString().toUpperCase() : null;
    }

    /**
     * Determine if specified character indicates a snake case transition for characters adjacent to a capital letter.
     *
     * @param c The character.
     *
     * @return A flag indicating whether specified character indicates a snake case transition for characters adjacent
     * to a capital letter.
     */
    private static boolean isSnakeTransition(char c) {
        return ((c != '_') && (!Character.isUpperCase(c)) && (!Character.isDigit(c)));
    }

    /**
     * Convert the specified text to snake case.
     *
     * <p> Definition from wikipedia:
     * <p> Snake case (or snake_case) is the practice of writing compound words or phrases in which the elements are
     * separated with one underscore character (_) and no spaces, with each element's initial letter usually lower
     * cased within the compound and the first letter either upper or lower case—as in "foo_bar" and "Hello_world". It
     * is commonly used in computer code for variable names, and function names, and sometimes computer file names. At
     * least one study found that readers can recognize snake case values more quickly than camel case.
     *
     * </p>
     *
     * @param text The original text.
     *
     * @return The snake cased text.
     */
    public static String toSnakeCase(CharSequence text) {
        String snakeCase = null;
        if (text != null) {
            StringBuilder buffer = new StringBuilder();
            if (text.length() > 1) {
                String txt = trim(text).replaceAll("[\\s-]", "_");
                for (int c = 0; c < txt.length(); c++) {
                    char tc = txt.charAt(c);
                    if ((Character.isUpperCase(tc) || (Character.isDigit(tc)))) {
                        boolean transition = false;
                        if (c > 0) {
                            tc = txt.charAt(c - 1);
                            transition = isSnakeTransition(tc);
                            if ((!transition) && (c < (txt.length() - 1))) {
                                tc = txt.charAt(c + 1);
                                transition = isSnakeTransition(tc);
                            }
                        }
                        if (transition) {
                            buffer.append('_');
                        }
                    }
                    buffer.append(txt.charAt(c));
                }
            } else {
                buffer.append(text);
            }
            snakeCase = toLowerCase(buffer)
                    .replaceAll("_+", "_")
                    .replaceFirst("^_", EMPTY_STRING)
                    .replaceFirst("_$", EMPTY_STRING);
        }
        return snakeCase;
    }

    /**
     * Replace all system property references in the specified value text with their equivalent values.
     *
     * <p> System property references are in the form "{@code ${property.name}}". </p>
     *
     * @param value The value to replace.
     *
     * @return The modified value.
     */
    public static String replaceSystemTokens(CharSequence value) {
        String modified = toString(value);
        String last;
        do {
            last = modified;
            modified = _replaceSystemTokens(modified);
        } while (!Objects.equals(last, modified));
        return modified;
    }

    /**
     * Replace all system property references in the specified value text with their equivalent values.
     *
     * <p> System property references are in the form "{@code ${property.name}}". </p>
     *
     * @param value The value to replace.
     *
     * @return The modified value.
     */
    private static String _replaceSystemTokens(CharSequence value) {
        String modified = toString(value);
        if (value != null) {
            Matcher matcher = SYS_PROPERTY.matcher(value);
            if (matcher.find()) {
                modified = matcher.replaceAll((r) -> {
                    String sysKey = r.group(2);
                    String sysProperty = System.getProperty(sysKey, "");
                    return replaceSystemTokens(sysProperty
                            .replaceAll("\\\\", "\\\\\\\\")
                            .replaceAll("\\$", "\\\\\\$"));
                });
            }
        }
        return modified;
    }

    /**
     * Constructor.
     */
    private Strings() {
        super();
    }
}