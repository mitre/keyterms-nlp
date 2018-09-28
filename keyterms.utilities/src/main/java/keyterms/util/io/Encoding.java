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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import keyterms.util.Errors;
import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;
import keyterms.util.lang.Lazy;
import keyterms.util.text.Strings;

/**
 * Methods for interacting with character encoding constructs.
 */
public final class Encoding {
    /**
     * The character set for US_ASCII.
     */
    public static final Charset ASCII = StandardCharsets.US_ASCII;

    /**
     * The character set for UTF-8.
     */
    public static final Charset UTF8 = StandardCharsets.UTF_8;

    /**
     * The character set for UTF-16.
     */
    public static final Charset UTF16 = StandardCharsets.UTF_16;

    /**
     * The character set for UTF-16LE.
     */
    public static final Charset UTF16LE = StandardCharsets.UTF_16LE;

    /**
     * The character set for UTF-16BE.
     */
    public static final Charset UTF16BE = StandardCharsets.UTF_16BE;

    /**
     * The character set for UTF-32.
     */
    public static final Charset UTF32 = getCharset("UTF-32");

    /**
     * The character set for UTF-32LE.
     */
    public static final Charset UTF32LE = getCharset("UTF-32LE");

    /**
     * The character set for UTF-32BE.
     */
    public static final Charset UTF32BE = getCharset("UTF-32BE");

    /**
     * The default character set for the operating system.
     */
    public static final Charset PLATFORM_DEFAULT = Charset.defaultCharset();

    /**
     * The UTF-8 byte order mark sequence.
     */
    public static final byte[] UTF8_BOM = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };

    /**
     * The UTF-16 byte order mark sequence.
     */
    public static final byte[] UTF16_BOM = new byte[] { (byte)0xFE, (byte)0xFF };

    /**
     * The UTF-16 little endian byte order mark sequence.
     */
    public static final byte[] UTF16LE_BOM = new byte[] { (byte)0xFF, (byte)0xFE };

    /**
     * The UTF-32 byte order mark sequence.
     */
    public static final byte[] UTF32_BOM = new byte[] { (byte)0x00, (byte)0x00, (byte)0xFE, (byte)0xFF };

    /**
     * The UTF-32 little endian byte order mark sequence.
     */
    public static final byte[] UTF32LE_BOM = new byte[] { (byte)0xFF, (byte)0xFE, (byte)0x00, (byte)0x00 };

    /**
     * A map of the known character set byte order marks.
     */
    private static final Map<Charset, byte[]> BOM_MAP = Bags.staticMap(
            Keyed.of(UTF8, UTF8_BOM),
            Keyed.of(UTF16, UTF16_BOM),
            Keyed.of(UTF16LE, UTF16LE_BOM),
            Keyed.of(UTF16BE, UTF16_BOM),
            Keyed.of(UTF32, UTF32_BOM),
            Keyed.of(UTF32LE, UTF32LE_BOM),
            Keyed.of(UTF32BE, UTF32_BOM)
    );

    /**
     * A sorted set of unattributed byte order marks, sorted in descending order based on the length of the byte order
     * sequence.
     */
    private static final Set<byte[]> BOMS = BOM_MAP.values().stream()
            .collect(Collectors.toCollection(() -> new TreeSet<>((b1, b2) -> {
                int l1 = (b1 != null) ? b1.length : -1;
                int l2 = (b2 != null) ? b2.length : -1;
                int diff = l2 - l1;
                if ((diff == 0) && (l1 > 0)) {
                    for (int b = 0; b < l1; b++) {
                        diff = b2[b] - b1[b];
                        if (diff != 0) {
                            break;
                        }
                    }
                }
                return diff;
            })));

    /**
     * A cache of the available character sets, maintained and retrieved via lazy initialization as the call to {@code
     * Charset.availableCharsets()} is consistently expensive.
     */
    private static final Lazy<SortedSet<Charset>> AVAILABLE = new Lazy<>(() ->
            Collections.unmodifiableSortedSet(new TreeSet<>(Charset.availableCharsets().values())));

    /**
     * A cache of the available character sets, maintained and retrieved via lazy initialization as the call to {@code
     * Charset.availableCharsets()} is consistently expensive.
     */
    private static final Lazy<Map<String, Charset>> LENIENT_INDEX = new Lazy<>(() -> {
        Map<String, Charset> nameIndex = new HashMap<>();
        AVAILABLE.value().stream()
                .map(charset -> new Keyed<>(getLenientName(charset.name()), charset))
                .forEach(keyed -> nameIndex.put(keyed.getKey(), keyed.getValue()));
        return Collections.unmodifiableMap(nameIndex);
    });

    /**
     * A set of the lenient encoding names that have been reported as unsupported.
     */
    private static final Set<String> REPORTED = new HashSet<>();

    /**
     * Get the available character sets on the system.
     *
     * @return A sorted set of the available character sets on the system.
     */
    public static SortedSet<Charset> getAvailableCharsets() {
        return AVAILABLE.value();
    }

    /**
     * Get the name for the lenient character set index.
     *
     * @param encodingName The encoding name of interest.
     *
     * @return The name for the lenient character set index.
     */
    public static String getLenientName(CharSequence encodingName) {
        String lenientName = null;
        if (encodingName != null) {
            lenientName = encodingName.toString()
                    .toLowerCase()
                    .replaceAll("[\\s\\-_]", "")
                    .trim();
        }
        return lenientName;
    }

    /**
     * A non-error producing character set locator.
     *
     * <p> A value of {@code null} will be returned if the character set cannot be located. </p>
     *
     * @param charsetName The name of the desired encoding.
     *
     * @return The specified {@code Charset}.
     */
    public static Charset getCharset(CharSequence charsetName) {
        Charset charset = null;
        if (Strings.hasText(charsetName)) {
            String lenientName = null;
            try {
                charset = Charset.forName(charsetName.toString());
            } catch (Exception error) {
                Errors.check(error);
            }
            if (charset == null) {
                lenientName = getLenientName(charsetName);
                assert (LENIENT_INDEX != null);
                charset = LENIENT_INDEX.value().get(lenientName);
            }
            if (charset == null) {
                assert (REPORTED != null);
                if (!REPORTED.contains(lenientName)) {
                    REPORTED.add(lenientName);
                    LoggerFactory.getLogger(Encoding.class).error("UnsupportedCharSetException: " + lenientName);
                }
            }
        }
        return charset;
    }

    /**
     * Get the character set associated with the specified byte order mark.
     *
     * <p> A value of {@code null} will be returned if the byte order mark sequence is not known. </p>
     *
     * @param bom The byte order mark.
     *
     * @return The character set associated with the specified byte order mark.
     */
    public static Charset getCharset(byte[] bom) {
        return BOM_MAP.entrySet().stream()
                .filter((e) -> !Objects.equals(e.getKey(), UTF16BE))
                .filter((e) -> !Objects.equals(e.getKey(), UTF32BE))
                .filter((e) -> Arrays.equals(bom, e.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the byte order mark for the specified character set.
     *
     * @param charset The character set.
     *
     * @return The byte order mark for the specified character set.
     */
    public static byte[] getBom(Charset charset) {
        return BOM_MAP.get(charset);
    }

    /**
     * Determine if a known byte order mark is present in the specified bytes.
     *
     * @param bytes The encoded bytes.
     *
     * @return The character encoding of the associated byte order mark if one is detected.
     */
    public static Charset detectBom(byte[] bytes) {
        Charset encoding = null;
        if (bytes != null) {
            for (byte[] bom : BOMS) {
                if (bom.length <= bytes.length) {
                    boolean detected = false;
                    for (int b = 0; b < bom.length; b++) {
                        detected = (bom[b] == bytes[b]);
                        if (!detected) {
                            break;
                        }
                    }
                    if (detected) {
                        encoding = getCharset(bom);
                        break;
                    }
                }
            }
        }
        return encoding;
    }

    /**
     * Presuming a byte order mark has accidentally been prepended to the specified text, attempt to remove it.
     *
     * @param text The text.
     * @param charset The original encoding which generated the text.
     *
     * @return The text with the inadvertent bom character(s) stripped.
     */
    public static String stripBom(CharSequence text, Charset charset) {
        String stripped = (text != null) ? text.toString() : null;
        if (stripped != null) {
            ByteBuffer data = charset.encode(stripped);
            stripBom(data, getBom(charset));
            stripped = charset.decode(data).toString();
        }
        return stripped;
    }

    /**
     * Attempt to effectively strip the specified byte order mark from the byte buffer by repositioning the read cursor
     * of the buffer.
     *
     * @param buffer The byte buffer to strip.
     * @param bom The byte order mark to strip.
     */
    private static void stripBom(ByteBuffer buffer, byte[] bom) {
        if ((buffer != null) && (bom != null)) {
            int position = buffer.position();
            int limit = buffer.limit();
            int numBytes = limit - position;
            if (numBytes > bom.length) {
                byte[] testBytes = new byte[bom.length];
                buffer.get(testBytes, 0, bom.length);
                buffer.position(position);
                if (Arrays.equals(bom, testBytes)) {
                    buffer.position(position + bom.length);
                }
            }
        }
    }

    /**
     * Encode the specified text using the platform's default encoding without a byte order mark sequence.
     *
     * <p> This method will not return {@code null}. </p>
     *
     * @param text The text to encode.
     *
     * @return The encoded bytes.
     */
    public static byte[] encode(CharSequence text) {
        return encode(text, null, false);
    }

    /**
     * Encode the specified text without a byte order mark sequence.
     *
     * <p> This method will not return {@code null}. </p>
     *
     * <p> If the specified character set encoding is {@code null}, the system default encoding will be used. </p>
     *
     * @param text The text to encode.
     * @param charset The character set encoding.
     *
     * @return The encoded bytes.
     */
    public static byte[] encode(CharSequence text, Charset charset) {
        return encode(text, charset, false);
    }

    /**
     * Encode the specified text.
     *
     * <p> This method will not return {@code null}. </p>
     *
     * <p> If the specified character set encoding is {@code null}, the system default encoding will be used. </p>
     *
     * @param text The text to encode.
     * @param charset The character set encoding.
     * @param prependBom A flag indicating whether the applicable byte order mark should be prepended to the results.
     *
     * @return The encoded bytes.
     */
    public static byte[] encode(CharSequence text, Charset charset, boolean prependBom) {
        Charset encoding = (charset != null) ? charset : PLATFORM_DEFAULT;
        byte[] encoded = new byte[0];
        byte[] bom = getBom(encoding);
        if ((text != null) && (text.length() > 0)) {
            ByteBuffer buffer = encoding.encode(text.toString());
            stripBom(buffer, bom);
            int bufferLength = buffer.remaining();
            encoded = new byte[bufferLength];
            buffer.get(encoded, 0, bufferLength);
        }
        if ((prependBom) && (bom != null)) {
            byte[] expanded = new byte[encoded.length + bom.length];
            System.arraycopy(bom, 0, expanded, 0, bom.length);
            System.arraycopy(encoded, 0, expanded, bom.length, encoded.length);
            encoded = expanded;
        }
        return encoded;
    }

    /**
     * Decode the given bytes using the specified character encoding scheme.
     *
     * <p> This method will return {@code null} if the specified data is {@code null}. </p>
     *
     * <p> A detected character set will be used if a known byte order mark is present at the beginning of the bytes,
     * otherwise the platform default encoding will be used. </p>
     *
     * <p> The known byte order marks are for {@code UTF-8, UTF-16, UTF-32} and their variants. </p>
     *
     * @param bytes The bytes to decode.
     *
     * @return The decoded text.
     */
    public static String decode(byte[] bytes) {
        return decode(bytes, null);
    }

    /**
     * Decode the given bytes using the specified character encoding scheme.
     *
     * <p> This method will return {@code null} if the specified data is {@code null}. </p>
     *
     * <p> If the specified character set encoding is {@code null}, a detected character set will be used if a known
     * byte order mark is present at the beginning of the bytes, otherwise the platform default encoding will be used.
     * </p>
     *
     * @param bytes The bytes to decode.
     * @param charset The character set encoding.
     *
     * @return The decoded text.
     */
    public static String decode(byte[] bytes, Charset charset) {
        Charset encoding = charset;
        if (encoding == null) {
            encoding = detectBom(bytes);
            encoding = (encoding != null) ? encoding : PLATFORM_DEFAULT;
        }
        String decoded = null;
        if (bytes != null) {
            byte[] bom = getBom(encoding);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            stripBom(buffer, bom);
            decoded = encoding.decode(buffer).toString();
        }
        return decoded;
    }

    /**
     * Constructor.
     */
    private Encoding() {
        super();
    }
}