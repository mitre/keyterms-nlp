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

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * An alternate to the GSON TypeAdapter class that implements dual serializer and deserializer methods.
 */
public abstract class GsonAdapter<C>
        implements JsonSerializer<C>, JsonDeserializer<C> {
    /**
     * The provenance event class.
     */
    protected final Class<C> type;

    /**
     * Constructor.
     *
     * @param type The type class.
     */
    protected GsonAdapter(Class<C> type) {
        super();
        if (type == null) {
            throw new NullPointerException("Type class is required.");
        }
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(C src, Type typeOfSrc, JsonSerializationContext context) {
        // Default serialization for the specific object.
        return context.serialize(src);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        C value = null;
        if ((json != null) && (!json.isJsonNull())) {
            value = deserializeNonNull(json, typeOfT, context);
        }
        return value;
    }

    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the specified type.
     *
     * <p> In the implementation of this call-back method, you should consider invoking
     * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing {@code json} since that will cause an infinite loop (Gson will call your
     * call-back method again). </p>
     *
     * @param json The Json data being de-serialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context The deserialization context
     *
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     */
    protected abstract C deserializeNonNull(JsonElement json, Type typeOfT, JsonDeserializationContext context);
}