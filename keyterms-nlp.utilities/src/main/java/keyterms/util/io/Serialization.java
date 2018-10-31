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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Methods for converting between serial (binary) and {@code Object} representations using standard Java serialization.
 */
public final class Serialization {
    /**
     * Get the serialized bytes of the specified {@code Serializable}.
     *
     * @param object The object to serialize.
     *
     * @return The serialized bytes representing the specified object.
     *
     * @throws IOException for input/output errors
     */
    public static byte[] toBytes(Serializable object)
            throws IOException {
        byte[] data = null;
        if (object != null) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(object);
                data = bos.toByteArray();
            }
        }
        return data;
    }

    /**
     * Convert the specified bytes into the specified type of object.
     *
     * @param objectClass The class representation of expected object type.
     * @param objectData The object byte data.
     * @param <C> The expected object type.
     *
     * @return The requested object.
     *
     * @throws IOException for input/output errors
     * @throws ClassNotFoundException if the class is not found
     */
    public static <C extends Serializable> C fromBytes(Class<C> objectClass, byte[] objectData)
            throws IOException, ClassNotFoundException {
        C cast = null;
        if ((objectClass != null) && (objectData != null)) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(objectData);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object object = ois.readObject();
                cast = objectClass.cast(object);
            }
        }
        return cast;
    }

    /**
     * Deeply clone the specified object.  The deep clone is provided in this method by serializing and de-serializing
     * the object.  This requires that the object contain deeply serializable object graphs.  I/O exceptions that might
     * otherwise be generated using this technique are converted into {@code CloneNotSupportedException}'s as a
     * potentially more meaningful exception for the operation.  The original errors are maintained in the generated
     * exception.
     *
     * @param originalClass The original object's class.
     * @param original The original object.
     * @param <O> The object class.
     *
     * @return The serial copy of the specified object.
     *
     * @throws IOException for input/output errors
     */
    public static <O extends Serializable> O serialCopy(Class<O> originalClass, O original)
            throws IOException {
        O copy = null;
        if (original != null) {
            try {
                byte[] serialized = toBytes(original);
                copy = originalClass.cast(fromBytes(Serializable.class, serialized));
            } catch (IOException ioException) {
                throw ioException;
            } catch (Exception error) {
                IOException errorWrapper;
                errorWrapper = new IOException("Could not serial copy the specified object.", error);
                throw errorWrapper;
            }
        }
        return copy;
    }

    /**
     * Serialize the specified object to a file.
     *
     * @param object The object to serialize.
     * @param file The file to create.
     *
     * @throws IOException for input/output errors
     */
    public static void toFile(Serializable object, Path file)
            throws IOException {
        if (object != null) {
            try (FileOutputStream fos = new FileOutputStream(file.toFile(), false)) {
                toStream(object, fos);
            }
        }
    }

    /**
     * Load an object from the specified file.
     *
     * @param objectClass The class representation of expected object type.
     * @param file The file containing the serialized representation of the desired object.
     * @param <C> The expected object type.
     *
     * @return The de-serialized object.
     *
     * @throws IOException for input/output errors
     * @throws ClassNotFoundException if the class is not found
     */
    public static <C extends Serializable> C fromFile(Class<C> objectClass, Path file)
            throws IOException, ClassNotFoundException {
        C cast;
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            cast = fromStream(objectClass, fis);
        }
        return cast;
    }

    /**
     * Serialize the specified object to a stream.
     *
     * @param object The object to serialize.
     * @param stream The data output stream.
     *
     * @throws IOException for input/output errors
     */
    public static void toStream(Serializable object, OutputStream stream)
            throws IOException {
        if (stream == null) {
            throw new NullPointerException("Output stream required.");
        }
        if (object != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(stream)) {
                oos.writeObject(object);
            }
        }
    }

    /**
     * Load an object from the specified input stream.
     *
     * @param objectClass The class representation of expected object type.
     * @param stream The stream containing the serialized representation of the desired object.
     * @param <C> The expected object type.
     *
     * @return The de-serialized object.
     *
     * @throws IOException for input/output errors
     * @throws ClassNotFoundException if the class is not found
     */
    public static <C extends Serializable> C fromStream(Class<C> objectClass, InputStream stream)
            throws IOException, ClassNotFoundException {
        C cast;
        if (stream == null) {
            throw new NullPointerException("Input stream required.");
        }
        if (objectClass == null) {
            throw new NullPointerException("Object class required.");
        }
        try (ObjectInputStream ois = new ObjectInputStream(stream)) {
            Object object = ois.readObject();
            cast = objectClass.cast(object);
        }
        return cast;
    }

    /**
     * Constructor.
     */
    private Serialization() {
        super();
    }
}