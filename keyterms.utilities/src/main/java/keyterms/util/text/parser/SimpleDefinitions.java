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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import keyterms.util.text.Strings;
import keyterms.util.text.splitter.LineSplitter;

/**
 * A parser for simple definition files which consist of records containing fields and lists.
 *
 * <p> Blank lines and lines starting with the {@code '#'} character are ignored. </p>
 * <p> Fields are formatted as </p>
 * <pre>fieldName: value
 * </pre>
 * Lists are formatted as follows (with each value on its own line):
 * <pre> fieldName: {
 *     value1
 *     value2
 *     etc...
 * }</pre>
 *
 * <p> The fields and lists are always interpreted in the order found in the file. </p>
 *
 * <p> A complete record (ordered set of fields and lists) for each definition is required. </p>
 */
public class SimpleDefinitions {
    /**
     * The pattern for identifying field values in
     */
    private static final Pattern FIELD_PATTERN = Pattern.compile("([^:]+):\\s*(.*)?");

    /**
     * The list start marker.
     */
    private static final String LIST_START = "{";

    /**
     * The list end marker.
     */
    private static final String LIST_END = "}";

    /**
     * The lines in the data file.
     */
    private List<String> lines;

    /**
     * The current line number.
     */
    private int lineNumber = 0;

    /**
     * Constructor.
     *
     * @param definitions The contents of a resource definitions file.
     */
    public SimpleDefinitions(String definitions) {
        super();
        lines = new LineSplitter().split(definitions).stream()
                .map(Strings::trim)
                .collect(Collectors.toList());
    }

    /**
     * Determine if the file has more lines.
     *
     * @return A flag indicating whether the file has more lines.
     */
    public boolean hasMore() {
        return lines.stream()
                .filter(Strings::hasText)
                .filter(line -> !line.startsWith("#"))
                .map((line) -> true)
                .findFirst()
                .orElse(false);
    }

    /**
     * Get the next line.
     *
     * @return The next line;
     */
    private String _nextLine() {
        lineNumber++;
        String line = lines.get(0);
        lines.remove(0);
        return line;
    }

    /**
     * Get the next non-blank and non-comment line.
     *
     * @return The next line.
     */
    private String nextLine() {
        if (!hasMore()) {
            throw new IllegalStateException("Unexpected end of record at line #" + lineNumber + ".");
        }
        String line;
        do {
            line = _nextLine();
        } while ((Strings.isBlank(line)) || (line.startsWith("#")));
        return line;
    }

    /**
     * Check the specified text, returning a trimmed version of the text or {@code null} if the text is blank.
     *
     * @param text The text.
     *
     * @return The checked text.
     */
    private String text(CharSequence text) {
        String checked = null;
        if (text != null) {
            checked = text.toString().trim();
            if (checked.isEmpty()) {
                checked = null;
            }
        }
        return checked;
    }

    /**
     * Get the specified field value.
     *
     * @param fieldName The expected field name.
     *
     * @return The field text.
     */
    public String getField(String fieldName) {
        String line = nextLine();
        Matcher matcher = FIELD_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid field line #" + lineNumber + ": " + line);
        }
        String field = Strings.trim(matcher.group(1));
        String text = text(matcher.group(2));
        if (!Objects.equals(fieldName, field)) {
            throw new IllegalArgumentException("Invalid field at line #" + lineNumber + ": " + line +
                    "\nExpected: " + fieldName + " Actual: " + field);
        }
        return text;
    }

    /**
     * Get the specified field list.
     *
     * @param fieldName The expected field name.
     *
     * @return The field list.
     */
    public List<String> getList(String fieldName) {
        if (!LIST_START.equals(getField(fieldName))) {
            throw new IllegalStateException("Invalid " + fieldName + " list at line #" + lineNumber + ".");
        }
        List<String> list = new ArrayList<>();
        String line = nextLine();
        while (!line.equals(LIST_END)) {
            list.add(line);
            line = nextLine();
        }
        return list;
    }

    /**
     * Get the current line number.
     *
     * @return The current line number.
     */
    public int getLineNumber() {
        return lineNumber;
    }
}