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
import com.google.gson.stream.JsonWriter;

import keyterms.nlp.iso.StdDef;

/**
 * An abstract GSON adapter for {@link StdDef} data objects.
 */
public abstract class StdDefAdapter<D extends StdDef>
        extends TypeAdapter<D> {
    /**
     * The field name for the name attribute of the standard definition.
     */
    protected static final String CODE = "code";

    /**
     * The field name for the name attribute of the standard definition.
     */
    protected static final String NAME = "name";

    /**
     * The value type handled by the adapter.
     */
    protected final Class<D> valueType;

    /**
     * Constructor.
     *
     * @param valueType The value type handled by the adapter.
     */
    protected StdDefAdapter(Class<D> valueType) {
        super();
        this.valueType = valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(JsonWriter jsonWriter, D def)
            throws IOException {
        if (def != null) {
            jsonWriter.beginObject();
            jsonWriter.name(CODE).value(def.getCode());
            jsonWriter.name(NAME).value(def.getName());
            jsonWriter.endObject();
        } else {
            jsonWriter.nullValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public D read(JsonReader jsonReader)
            throws IOException {
        D value;
        jsonReader.beginObject();
        String code = null;
        String name = null;
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName();
            String fieldValue = jsonReader.nextString();
            if ("null".equalsIgnoreCase(fieldName)) {
                fieldValue = null;
            }
            if (CODE.equals(fieldName)) {
                code = fieldValue;
            }
            if (NAME.equals(fieldName)) {
                name = fieldValue;
            }
        }
        value = resolve(code, name);
        jsonReader.endObject();
        return value;
    }

    /**
     * Resolve the specified object.
     *
     * <p> Resolution by code should be preferred over name resolution. </p>
     *
     * @param code The primary identifier for the definition.
     * @param name The primary English name for the definition.
     *
     * @return The resolved object.
     */
    protected abstract D resolve(String code, String name);
}