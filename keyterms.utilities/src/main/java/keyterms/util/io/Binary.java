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

package keyterms.util.io;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import keyterms.util.text.Strings;

/**
 * A binary object represents a byte array that is serialized using textual representations of the byte array.
 *
 * <p> This data structure is primarily useful where the binary data is one of several parameters that have to be
 * passed via textual means, such as path or query parameters in a REST call. </p>
 * <p> Examples where this is useful would the the representation of MD-5 or SHA-512 hash codes for a file being
 * transmitted to a web service where the service expects the client to have computed the hashes. </p>
 */
public class Binary {
    /**
     * Convert the specified byte array to a hexadecimal equivalent representation.
     *
     * @param bytes The bytes.
     *
     * @return The equivalent hexadecimal text representation.
     */
    public static String toHex(byte[] bytes) {
        String hex = null;
        if (bytes != null) {
            char[] chars = new char[bytes.length * 2];
            int c = -1;
            for (byte b : bytes) {
                chars[++c] = Integer.toHexString((b >> 4) & 0x0f).charAt(0);
                chars[++c] = Integer.toHexString(b & 0x0f).charAt(0);
            }
            hex = new String(chars);
        }
        return hex;
    }

    /**
     * Convert the specified hexadecimal text representation to a byte array.
     *
     * @param hex The hexadecimal text representation of the byte array.
     *
     * @return The equivalent byte array.
     */
    public static byte[] fromHex(CharSequence hex) {
        byte[] bytes = null;
        if (hex != null) {
            String text = Strings.trim(hex);
            if (Strings.hasText(text)) {
                int length = text.length();
                if ((length % 2) != 0) {
                    text = "0" + text;
                    length++;
                }
                bytes = new byte[length / 2];
                int c = -1;
                int b = -1;
                int l = length - 1;
                while (c < l) {
                    int i1 = Character.digit(text.charAt(++c), 16);
                    if (i1 == -1) {
                        throw new NumberFormatException(
                                "Invalid hex character '" + text.charAt(c) + "' at position " + c + ".");
                    }
                    int i2 = Character.digit(text.charAt(++c), 16);
                    if (i2 == -1) {
                        throw new NumberFormatException(
                                "Invalid hex character '" + text.charAt(c) + "' at position " + c + ".");
                    }
                    bytes[++b] = (byte)((i1 << 4) + i2);
                }
            } else {
                bytes = new byte[0];
            }
        }
        return bytes;
    }

    /**
     * The binary data.
     */
    private final byte[] data;

    /**
     * Constructor.
     *
     * @param data The binary data.
     */
    public Binary(byte[] data) {
        super();
        this.data = data;
    }

    /**
     * Constructor.
     *
     * @param hex The hex representation of the data.
     */
    public Binary(String hex) {
        super();
        byte[] data = null;
        if (hex != null) {
            if (Strings.isBlank(hex)) {
                data = new byte[0];
            } else {
                data = fromHex(hex);
            }
        }
        this.data = data;
    }

    /**
     * Get the binary data.
     *
     * @return The binary data.
     */
    public byte[] data() {
        return data;
    }

    /**
     * Get the hex representation of the binary data.
     *
     * @return The hex representation of the binary data.
     */
    public String hex() {
        return toHex(data);
    }

    /**
     * Get the base64 representation of the binary data.
     *
     * @return The base64 representation of the binary data.
     */
    public String base64() {
        return (data != null) ? Base64.getEncoder().encodeToString(data) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash((Object)data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof Binary) && (Arrays.equals(data, ((Binary)obj).data)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}