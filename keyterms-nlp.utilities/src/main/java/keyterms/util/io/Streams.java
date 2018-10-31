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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.MessageDigest;

/**
 * Methods for using streams.
 */
public final class Streams {
    /**
     * The read buffer size used during channel copying operations.
     */
    static final int COPY_BUFFER_SIZE = 10240;

    /**
     * Fully read the data from the specified input stream.
     *
     * @param inputStream The input stream.
     * @param maxBytes The maximum number of bytes to read from the stream.
     *
     * @return The data from the input stream.
     *
     * @throws IOException for input/output errors
     */
    public static byte[] read(InputStream inputStream, Integer maxBytes)
            throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            channelCopy(inputStream, byteStream, maxBytes);
            return byteStream.toByteArray();
        }
    }

    /**
     * Fully read the data from the specified input stream.
     *
     * @param inputStream The input stream.
     *
     * @return The data from the input stream.
     *
     * @throws IOException for input/output errors
     */
    public static byte[] readFully(InputStream inputStream)
            throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            channelCopy(inputStream, byteStream);
            return byteStream.toByteArray();
        }
    }

    /**
     * Stream the data from the input stream to the specified output stream.
     *
     * @param inputStream The input stream.
     * @param outputStream The output stream.
     *
     * @throws IOException for input/output errors
     */
    public static void channelCopy(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        channelCopy(inputStream, outputStream, -1);
    }

    /**
     * Stream the data from the input stream to the specified output stream.
     *
     * <p> If the specified number of bytes to copy is {@code null} or less than zero,
     * the channel will be copied in full. </p>
     *
     * @param inputStream The input stream.
     * @param outputStream The output stream.
     * @param maxBytes The maximum number of bytes to read.
     *
     * @throws IOException for input/output errors
     */
    private static void channelCopy(InputStream inputStream, OutputStream outputStream, Integer maxBytes)
            throws IOException {
        int toCopy = ((maxBytes != null) && (maxBytes >= 0)) ? maxBytes : -1;
        if ((inputStream != null) && (outputStream != null)) {
            try (ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
                 WritableByteChannel outputChannel = Channels.newChannel(outputStream)) {
                int bufferSize = (toCopy >= 0) ? toCopy : COPY_BUFFER_SIZE;
                ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                int bytesRead = inputChannel.read(buffer);
                while ((bytesRead != -1) && (bytesRead != toCopy)) {
                    buffer.flip();
                    outputChannel.write(buffer);
                    buffer.compact();
                    bytesRead = inputChannel.read(buffer);
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    outputChannel.write(buffer);
                }
            }
        }
    }

    /**
     * Compute the MD5 hash for the specified input stream.
     *
     * @param stream The input stream containing the data to hash.
     *
     * @return The specified MD5 hash.
     *
     * @throws IOException for input/output errors
     */
    public static byte[] getMD5(InputStream stream)
            throws IOException {
        byte[] md5 = null;
        if (stream != null) {
            try {
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.reset();
                byte[] block = read(stream, COPY_BUFFER_SIZE);
                while (block.length > 0) {
                    m.update(block);
                    block = read(stream, COPY_BUFFER_SIZE);
                }
                md5 = m.digest();
            } catch (Exception error) {
                throw new IOException("Could not compute MD5 hash.", error);
            }
        }
        return md5;
    }

    /**
     * Constructor.
     */
    private Streams() {
        super();
    }
}