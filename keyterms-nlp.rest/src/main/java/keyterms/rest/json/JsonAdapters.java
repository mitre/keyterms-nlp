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
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import keyterms.nlp.iso.Country;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.util.io.Binary;
import keyterms.util.lang.Enums;

/**
 * A class used to initialized JSON handling for a variety of data structures.
 */
public final class JsonAdapters {
    /**
     * A synchronization lock controlling the initialization process.
     */
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * A flag indicating whether the adapters have been registered.
     */
    private static boolean initialized = false;

    /**
     * Register the required JSON adapters for ISO data structures.
     */
    public static void initJsonHandling() {
        if (!initialized) {
            LOCK.lock();
            try {
                if (!initialized) {
                    initialized = true;
                    // Binary data.
                    GsonUtil.registerTypeAdapter(Binary.class, new BinaryAdapter());
                    // ISO enums.
                    GsonUtil.registerTypeAdapter(Country.class, new CountryAdapter());
                    GsonUtil.registerTypeAdapter(Language.class, new LanguageAdapter());
                    GsonUtil.registerTypeAdapter(Script.class, new ScriptAdapter());
                }
            } finally {
                LOCK.unlock();
            }
        }
    }

    /**
     * Get the fields in the object at the current JSON reader cursor.
     *
     * @param jsonReader The JSON reader.
     * @param fieldNames The enumerated class containing field names.
     * @param <F> The enumerated field marker class.
     *
     * @return The existing field names with their associated values.
     *
     * @throws IOException for input/output errors
     */
    public static <F extends Enum<F>> HashMap<F, String> getFields(JsonReader jsonReader, Class<F> fieldNames)
            throws IOException {
        HashMap<F, String> fields = new HashMap<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName();
            String fieldValue = (jsonReader.peek() != JsonToken.NULL) ? jsonReader.nextString() : null;
            F field = Enums.find(fieldNames, fieldName);
            if (field == null) {
                throw new IOException("Unexpected field name: " + fieldName);
            }
            fields.put(field, fieldValue);
        }
        jsonReader.endObject();
        return fields;
    }

    /**
     * Constructor.
     */
    private JsonAdapters() {
        super();
    }
}