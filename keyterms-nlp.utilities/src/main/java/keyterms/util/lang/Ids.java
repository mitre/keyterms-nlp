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

package keyterms.util.lang;

import java.util.UUID;
import java.util.regex.Pattern;

import keyterms.util.io.Encoding;
import keyterms.util.text.Strings;

/**
 * * Methods for working with {@code UUID} text conversions.
 */
public final class Ids {
    /**
     * The pattern used to match against the text versions of {@code UUID}'s.
     */
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}");

    /**
     * The pattern used to match against the text version of a compact {@code UUID} (no hyphens/dashes).
     */
    private static final Pattern COMPACT_UUID = Pattern.compile("\\p{XDigit}{32}");

    /**
     * The number of characters for a UUID to output from the leading and trailing text for debugging.
     */
    private static final int DEBUG_LENGTH = 4;

    /**
     * Determine if the specified text appears to be a {@code UUID} in its standard textual format.
     *
     * @param text The possible {@code UUID} text representation.
     *
     * @return A flag indicating whether the specified text appears to be a {@code UUID} in its standard textual format.
     */
    public static boolean isUUID(CharSequence text) {
        return ((text != null) && (UUID_PATTERN.matcher(text).matches()));
    }

    /**
     * Determine if the specified text appears to be a {@code UUID} in its compact textual format (no dashes).
     *
     * @param text The possible {@code UUID} text representation.
     *
     * @return A flag indicating whether the specified text appears to be a {@code UUID} in its standard textual format.
     */
    public static boolean isCompactUUID(CharSequence text) {
        return ((text != null) && (COMPACT_UUID.matcher(text).matches()));
    }

    /**
     * Convert the specified text to the equivalent {@code UUID} assuming the text is either the standard or compact
     * textual representation of a UUID.
     *
     * @param text The textual representation of a UUID.
     *
     * @return The specified {@code UUID}.
     */
    public static UUID valueOf(CharSequence text) {
        UUID uuid = null;
        if (isUUID(text)) {
            uuid = UUID.fromString(text.toString());
        }
        if (isCompactUUID(text)) {
            StringBuilder utb = new StringBuilder(text);
            utb.insert(8, '-').insert(13, '-');
            utb.insert(18, '-').insert(23, '-');
            uuid = UUID.fromString(utb.toString());
        }
        return uuid;
    }

    /**
     * Create a reproducible {@code UUID} from the given text.  This produces a type 3 {@code UUID}.
     *
     * @param text The UUID seed text.
     *
     * @return A UUID made from the specified text.
     *
     * @see UUID#nameUUIDFromBytes(byte[])
     */
    public static UUID hashText(CharSequence text) {
        String toHash = (text != null) ? Strings.toString(text) : Strings.EMPTY_STRING;
        byte[] nameBytes = Encoding.encode(toHash, Encoding.UTF32LE);
        return UUID.nameUUIDFromBytes(nameBytes);
    }

    /**
     * Convert the specified identifier to its standard textual representation.
     *
     * @param uuid The identifier.
     *
     * @return The standard textual representation of the identifier.
     */
    public static String toString(UUID uuid) {
        return (uuid != null) ? uuid.toString().toLowerCase() : null;
    }

    /**
     * Convert the specified identifier to its compact textual representation.
     *
     * @param uuid The identifier.
     *
     * @return The compact textual representation of the identifier.
     */
    public static String toCompactString(UUID uuid) {
        return (uuid != null) ? toString(uuid).replaceAll("-", "") : null;
    }

    /**
     * Convert the specified identifier into a very compact string useful in debugging output or highly compact
     * representations.
     *
     * <p> Note: This representation is not useful in reproducing the original UUID. </p>
     *
     * @param uuid The identifier.
     *
     * @return The textual representation of the identifier useful for debugging purposes.
     */
    public static String debug(UUID uuid) {
        String debugString = "null";
        if (uuid != null) {
            debugString = toCompactString(uuid);
            debugString = debugString.substring(0, DEBUG_LENGTH)
                    + "..."
                    + debugString.substring(debugString.length() - DEBUG_LENGTH);
        }
        return debugString;
    }

    /**
     * Constructor.
     */
    private Ids() {
        super();
    }
}