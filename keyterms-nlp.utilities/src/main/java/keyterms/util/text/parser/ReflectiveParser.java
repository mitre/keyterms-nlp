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

package keyterms.util.text.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import keyterms.util.Errors;
import keyterms.util.text.Parser;

/**
 * A parser for objects which attempts to use either a text only constructor or static methods of {@code valueOf(Text)},
 * {@code fromText(Text)} or {@code fromString(String)} to convert the given text into their object representations.
 *
 * <p> In this context, {@code Text} is either {@code CharSequence.class} or {@code String.class}.
 * <p> The above constructors and methods must be explicitly declared on the given type class. </p>
 */
public class ReflectiveParser<V>
        implements Parser<V> {
    /**
     * Get the specified text constructor.
     *
     * @param type The type containing the explicitly declared constructor of interest.
     * @param valueClass The type of text that the constructor should use.
     *
     * @return The specified text constructor.
     */
    private static <V> Constructor<V> getTextConstructor(Class<V> type, Class<? extends CharSequence> valueClass) {
        Constructor<V> constructor = null;
        try {
            constructor = type.getDeclaredConstructor(String.class);
            if (constructor != null) {
                constructor.setAccessible(true);
            }
        } catch (Exception error) {
            Errors.check(error);
        }
        return constructor;
    }

    /**
     * Get the specified text based factory method.
     *
     * @param type The type containing the explicitly declared static method of interest.
     * @param methodName The method name of interest.
     * @param parameterClass The type of text that the method should take as a parameter.
     *
     * @return The specified text factory method.
     */
    private static <V> Method getTextMethod(Class<V> type, String methodName,
            Class<? extends CharSequence> parameterClass) {
        Method method = null;
        try {
            method = type.getDeclaredMethod(methodName, parameterClass);
            if (method != null) {
                if ((Modifier.isStatic(method.getModifiers())) && (type.equals(method.getReturnType()))) {
                    method.setAccessible(true);
                } else {
                    method = null;
                }
            }
        } catch (Exception error) {
            Errors.check(error);
        }
        return method;
    }

    /**
     * The type of object created by this parser.
     */
    private final Class<V> type;

    /**
     * The constructor which may be used in the default parser.
     * <p> This is obtained from the value type. </p>
     */
    private final Constructor<V> textConstructor;

    /**
     * The static text method which may be used in the default parser.
     * <p> This is obtained from the value type. </p>
     */
    private final Method textMethod;

    /**
     * Constructor.
     *
     * @param type The value type to be generated by this parser.
     */
    public ReflectiveParser(Class<V> type) {
        super();
        if (type == null) {
            throw new NullPointerException("Value type required.");
        }
        this.type = type;
        Constructor<V> constructor = getTextConstructor(type, CharSequence.class);
        constructor = (constructor != null) ? constructor : getTextConstructor(type, String.class);
        textConstructor = constructor;
        Method method = null;
        if (constructor == null) {
            method = getTextMethod(type, "valueOf", CharSequence.class);
            method = (method != null) ? method : getTextMethod(type, "valueOf", String.class);
            method = (method != null) ? method : getTextMethod(type, "fromText", CharSequence.class);
            method = (method != null) ? method : getTextMethod(type, "fromText", String.class);
            method = (method != null) ? method : getTextMethod(type, "fromString", String.class);
        }
        textMethod = method;
        if ((constructor == null) && (method == null)) {
            throw new IllegalArgumentException("Could not locate constructor methods for: " + type.getSimpleName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V parse(CharSequence text) {
        V value = null;
        if (text != null) {
            if (textConstructor != null) {
                try {
                    value = textConstructor.newInstance(text.toString());
                } catch (Exception error) {
                    throw new IllegalArgumentException("Could not parse '" + text + "' as " + type.getSimpleName());
                }
            }
            if (textMethod != null) {
                try {
                    value = type.cast(textMethod.invoke(null, text.toString()));
                } catch (Exception error) {
                    throw new IllegalArgumentException("Could not parse '" + text + "' as " + type.getSimpleName());
                }
            }
        }
        return value;
    }
}