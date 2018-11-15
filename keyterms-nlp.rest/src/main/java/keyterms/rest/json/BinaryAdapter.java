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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import keyterms.util.io.Binary;

/**
 * A GSON adapter for the {@link Binary} data object.
 */
public class BinaryAdapter
        extends TypeAdapter<Binary> {
    /**
     * Constructor.
     */
    public BinaryAdapter() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(JsonWriter jsonWriter, Binary binary)
            throws IOException {
        if (binary != null) {
            jsonWriter.beginObject();
            jsonWriter.name("data").value(binary.hex());
            jsonWriter.endObject();
        } else {
            jsonWriter.nullValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Binary read(JsonReader jsonReader)
            throws IOException {
        Binary binary = null;
        if (!JsonToken.NULL.equals(jsonReader.peek())) {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String fieldName = jsonReader.nextName();
                String fieldValue = jsonReader.nextString();
                if ("null".equalsIgnoreCase(fieldName)) {
                    fieldValue = null;
                }
                if ("data".equals(fieldName)) {
                    binary = new Binary(fieldValue);
                }
            }
            jsonReader.endObject();
        } else {
            jsonReader.nextNull();
        }
        return binary;
    }
}