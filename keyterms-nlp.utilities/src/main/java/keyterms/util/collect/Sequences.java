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

package keyterms.util.collect;

import java.lang.reflect.Array;
import java.nio.CharBuffer;
import java.util.List;

/**
 * A utility library for working with sequences such as lists, arrays and character sequences.
 */
public final class Sequences {
    /**
     * Check that the specified object represents a known sequence object.
     *
     * <p> A sequence represents an array like structure which can be randomly accessed via indices. </p>
     *
     * <p> The accepted sequence objects are: </p>
     * <ul>
     * <li> {@code null} values (considered zero length sequences) </li>
     * <li> {@code CharSequence} implementations </li>
     * <li> {@code List} implementations </li>
     * <li> Arrays </li>
     * </ul>
     *
     * @param sequence The object being tested.
     */
    public static void checkSequence(Object sequence) {
        boolean valid = true;
        if (sequence != null) {
            valid = (sequence instanceof CharBuffer);
            valid |= (sequence instanceof CharSequence);
            valid |= (sequence instanceof char[]);
            valid |= (sequence.getClass().isArray());
            valid |= (sequence instanceof List);
        }
        if (!valid) {
            throw new IllegalArgumentException("Invalid sequence: " + sequence.getClass());
        }
    }

    /**
     * Determine the length of the specified sequence.
     *
     * <p> The length of a {@code null} sequence is considered to be {@code 0}. </p>
     *
     * @param sequence The sequence.
     *
     * @return The length of the specified sequence.
     */
    public static int length(Object sequence) {
        checkSequence(sequence);
        int length = -1;
        if (sequence != null) {
            if (sequence instanceof CharBuffer) {
                length = ((CharBuffer)sequence).length();
            }
            if (sequence instanceof CharSequence) {
                length = ((CharSequence)sequence).length();
            }
            if (sequence.getClass().isArray()) {
                length = Array.getLength(sequence);
            }
            if (sequence instanceof List) {
                length = ((List)sequence).size();
            }
        }
        return length;
    }

    /**
     * Get the value of the sequence at the specified index.
     *
     * <p> Values for out-of-range indices will be {@code null} rather than causing an exception. </p>
     *
     * @param sequence The sequence.
     * @param index The index of the desired value.
     *
     * @return The value of the sequence at the specified index.
     */
    public static Object get(Object sequence, int index) {
        Object value = null;
        if ((sequence != null) && (index >= 0) && (index < length(sequence))) {
            if (sequence instanceof CharBuffer) {
                value = ((CharBuffer)sequence).charAt(index);
            }
            if (sequence instanceof CharSequence) {
                value = ((CharSequence)sequence).charAt(index);
            }
            if (sequence.getClass().isArray()) {
                value = Array.get(sequence, index);
            }
            if (sequence instanceof List) {
                value = ((List)sequence).get(index);
            }
        }
        return value;
    }

    /**
     * Constructor.
     */
    private Sequences() {
        super();
    }
}