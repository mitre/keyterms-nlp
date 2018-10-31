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

package keyterms.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;

/**
 * Utility for simple interactions with the GSON library.
 */
public final class GsonUtil {
    /**
     * The lock controlling modification of the GSON interpreters.
     */
    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    /**
     * The date time format used in this class.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss zzz";

    /**
     * The main GSON interpreter instance factory.
     */
    private static final GsonBuilder GSON = new GsonBuilder()
            .setLenient()
            .setDateFormat(DATE_FORMAT)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

    /**
     * The pretty printing GSON interpreter instance factory.
     */
    private static final GsonBuilder PRETTY = new GsonBuilder()
            .setLenient()
            .setDateFormat(DATE_FORMAT)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting();

    /**
     * The configured GSON interpreter.
     */
    private static Gson gson = GSON.create();

    /**
     * The configured GSON interpreter.
     */
    private static Gson pretty = PRETTY.create();

    /* Register well known type adapters
     */
    static {
        JsonAdapters.initJsonHandling();
    }

    /**
     * Register the specified JSON type adapter.
     *
     * @param type The value type processed by the adapter.
     * @param instanceCreator The type adapter.
     * @param <T> The type class.
     */
    public static <T> void registerInstanceCreator(Class<? extends T> type, InstanceCreator<T> instanceCreator) {
        registerAdapter(type, instanceCreator);
    }

    /**
     * Register the specified JSON type adapter.
     *
     * @param type The value type processed by the adapter.
     * @param serializer The type adapter.
     * @param <T> The type class.
     */
    public static <T> void registerSerializer(Class<? extends T> type, JsonSerializer<T> serializer) {
        registerAdapter(type, serializer);
    }

    /**
     * Register the specified JSON type adapter.
     *
     * @param type The value type processed by the adapter.
     * @param deserializer The type adapter.
     * @param <T> The type class.
     */
    public static <T> void registerDeserializer(Class<? extends T> type, JsonDeserializer<T> deserializer) {
        registerAdapter(type, deserializer);
    }

    /**
     * Register the specified JSON type adapter.
     *
     * @param type The value type processed by the adapter.
     * @param adapter The type adapter.
     * @param <T> The type class.
     */
    public static <T> void registerGsonAdapter(Class<? extends T> type, GsonAdapter<T> adapter) {
        registerSerializer(type, adapter);
        registerDeserializer(type, adapter);
    }

    /**
     * Register the specified JSON type adapter.
     *
     * @param type The value type processed by the adapter.
     * @param typeAdapter The type adapter.
     * @param <T> The type class.
     */
    public static <T> void registerTypeAdapter(Class<? extends T> type, TypeAdapter<T> typeAdapter) {
        registerAdapter(type, typeAdapter.nullSafe());
    }

    /**
     * Register the specified JSON type adapter.
     *
     * <p> The type adapter may be one of a {@code InstanceCreator}, {@code JsonSerializer},
     * {@code JsonDeserializer} or a {@code TypeAdapter}. </p>
     *
     * @param type The type.
     * @param adapter The adapter.
     */
    private static void registerAdapter(Type type, Object adapter) {
        if (type == null) {
            throw new NullPointerException("Type is required.");
        }
        if (adapter == null) {
            throw new NullPointerException("Adapter is required.");
        }
        LOCK.writeLock().lock();
        try {
            GSON.registerTypeAdapter(type, adapter);
            gson = GSON.create();
            PRETTY.registerTypeAdapter(type, adapter);
            pretty = PRETTY.create();
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    /**
     * Convert the specified object to {@code JSON} text.
     *
     * @param object The object.
     *
     * @return The {@code JSON} representation of the object.
     */
    public static String toJson(Object object) {
        return toJson(object, false);
    }

    /**
     * Convert the specified object to {@code JSON} text.
     *
     * @param object The object.
     * @param pretty A flag indicating whether formatted output is desired.
     *
     * @return The {@code JSON} representation of the object.
     */
    public static String toJson(Object object, boolean pretty) {
        LOCK.readLock().lock();
        try {
            Gson gson = (pretty) ? GsonUtil.pretty : GsonUtil.gson;
            return gson.toJson(object);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * Convert the specified object to {@code JSON} text.
     *
     * @param object The object.
     * @param jsonStream The output stream accepting the {@code JSON} text.
     *
     * @throws IOException for input/output errors
     */
    public static void toJson(Object object, OutputStream jsonStream)
            throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(jsonStream, StandardCharsets.UTF_8)) {
            LOCK.readLock().lock();
            try {
                gson.toJson(object, writer);
            } finally {
                LOCK.readLock().unlock();
            }
        }
    }

    /**
     * Convert {@code JSON} text to an object of the specified type.
     *
     * @param type The object class.
     * @param json The {@code JSON} representation of the object.
     * @param <T> The type class.
     *
     * @return The de-serialized object.
     */
    public static <T> T toObject(Type type, CharSequence json) {
        LOCK.readLock().lock();
        try {
            String string = json.toString();
            return ("null".equalsIgnoreCase(string)) ? null : gson.fromJson(string, type);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * Convert {@code JSON} text to an object of the specified type.
     *
     * @param type The object class.
     * @param jsonStream The input stream providing the {@code JSON} text.
     * @param <T> The type class.
     *
     * @return The de-serialized object.
     *
     * @throws IOException for input/output errors
     */
    public static <T> T toObject(Type type, InputStream jsonStream)
            throws IOException {
        try (InputStreamReader reader = new InputStreamReader(jsonStream, StandardCharsets.UTF_8)) {
            LOCK.readLock().lock();
            try {
                return gson.fromJson(reader, type);
            } finally {
                LOCK.readLock().unlock();
            }
        }
    }

    /**
     * Constructor.
     */
    private GsonUtil() {
        super();
    }
}