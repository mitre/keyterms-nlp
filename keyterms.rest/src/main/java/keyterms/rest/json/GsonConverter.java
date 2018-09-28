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

import javax.ws.rs.ext.ParamConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.Errors;
import keyterms.util.text.parser.ReflectiveParser;

/**
 * GSON parameter converter.
 */
public class GsonConverter<T>
        implements ParamConverter<T> {
    /**
     * The object type.
     */
    private final Class<T> type;

    /**
     * The generic object type.
     */
    private final Type genericType;

    /**
     * A backup parser for invalid JSON.
     */
    private final ReflectiveParser<T> parser;

    /**
     * Constructor.
     *
     * @param type The object type.
     * @param genericType The object's generic type.
     */
    public GsonConverter(Class<T> type, Type genericType) {
        super();
        this.type = type;
        this.genericType = genericType;
        ReflectiveParser<T> reflectiveParser = null;
        try {
            reflectiveParser = new ReflectiveParser<>(type);
        } catch (Exception error) {
            if (getLogger().isTraceEnabled()) {
                getLogger().warn("Could not instantiate backup parser for {}.", type, error);
            }
        }
        parser = reflectiveParser;
        JsonAdapters.initJsonHandling();
    }

    /**
     * Get the logging topic for this object.
     *
     * @return The logging topic for this object.
     */
    protected Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Instantiate the specified type using the reflective parser.
     *
     * @param s The textual representation of the object.
     *
     * @return The object instantiated using the reflective parser.
     */
    private T simpleParse(String s) {
        T fromString = null;
        if (parser != null) {
            if (s != null) {
                try {
                    fromString = parser.parse(s);
                } catch (Exception error) {
                    Errors.ignore(error);
                }
            }
        } else {
            getLogger().error("No backup parser for {}.", type);
        }
        return fromString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T fromString(String s) {
        T fromString = null;
        if (s != null) {
            try {
                fromString = GsonUtil.toObject((genericType != null) ? genericType : type, s);
            } catch (Exception error) {
                Errors.ignore(error);
                fromString = simpleParse(s);
            }
        }
        return fromString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(T t) {
        return GsonUtil.toJson(t);
    }
}