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

package keyterms.util.lang;

import java.util.Objects;

import keyterms.util.text.Strings;

/**
 * Methods for interacting with enumerated value classes.
 */
public final class Enums {
    /**
     * Get the enumerated value with the specified name without throwing an error if it is not found.
     *
     * <p> This method requires a strict match of the enumerated instance field name. </p>
     *
     * <p> Returns a value of {@code null} if the specified value is not found. </p>
     *
     * <p> Note: An {@code IllegalArgumentException} will be thrown if the specified type is not an enumerated type.
     * </p>
     *
     * @param enumClass The enumerated class.
     * @param text The textual representation of the enumerated value of interest.
     * @param <E> The enumerated value class.
     *
     * @return The specified enumerated instance or {@code null}.
     */
    public static <E> E valueOf(Class<E> enumClass, CharSequence text) {
        return valueOf(enumClass, text, null);
    }

    /**
     * Get the enumerated value with the specified name without throwing an error if it is not found.
     *
     * <p> This method requires a strict match of the enumerated instance field name. </p>
     *
     * <p> Returns a value of {@code defaultValue} if the specified value is not found. </p>
     *
     * <p> Note: An {@code IllegalArgumentException} will be thrown if the specified type is not an enumerated type.
     * </p>
     *
     * @param enumClass The enumerated class.
     * @param text The textual representation of the enumerated value of interest.
     * @param defaultValue The value to return if the specified value is not found.
     * @param <E> The enumerated value class.
     *
     * @return The specified enumerated instance or {@code null}.
     */
    public static <E> E valueOf(Class<E> enumClass, CharSequence text, E defaultValue) {
        if (!Enum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("Not an enumerated class: " + enumClass);
        }
        E instance = defaultValue;
        if (Strings.hasText(text)) {
            String test = Strings.toString(text);
            for (E value : enumClass.getEnumConstants()) {
                if (((Enum)value).name().equals(test)) {
                    instance = value;
                }
            }
        }
        return instance;
    }

    /**
     * Get the enumerated value with the specified name without throwing an error if it is not found.
     *
     * <p> This method first attempts to locate the instance field using a strict field name. </p>
     *
     * <p> If not found with a strict name, a case insensitive search is made on the trimmed text. </p>
     *
     * <p> Returns a value of {@code null} if the specified value is not found. </p>
     *
     * <p> Note: An {@code IllegalArgumentException} will be thrown if the specified type is not an enumerated type.
     * </p>
     *
     * @param enumClass The enumerated class.
     * @param text The textual representation of the enumerated value of interest.
     * @param <E> The enumerated value class.
     *
     * @return The specified enumerated instance or {@code null}.
     */
    public static <E> E find(Class<E> enumClass, CharSequence text) {
        return find(enumClass, text, null);
    }

    /**
     * Get the enumerated value with the specified name without throwing an error if it is not found.
     *
     * <p> This method first attempts to locate the instance field using a strict field name. </p>
     *
     * <p> If not found with a strict name, a case insensitive search is made on the trimmed text. </p>
     *
     * <p> Returns a value of {@code defaultValue} if the specified value is not found. </p>
     *
     * <p> Note: An {@code IllegalArgumentException} will be thrown if the specified type is not an enumerated type.
     * </p>
     *
     * @param enumClass The enumerated class.
     * @param text The textual representation of the enumerated value of interest.
     * @param defaultValue The value to return if the specified value is not found.
     * @param <E> The enumerated value class.
     *
     * @return The specified enumerated instance or {@code null}.
     */
    public static <E> E find(Class<E> enumClass, CharSequence text, E defaultValue) {
        if (!Enum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("Not an enumerated class: " + enumClass);
        }
        String string = Strings.toString(text);
        E instance = valueOf(enumClass, string, null);
        if (instance == null) {
            instance = defaultValue;
            if (Strings.hasText(text)) {
                String test = Strings.trim(text);
                for (E value : enumClass.getEnumConstants()) {
                    if (((Enum)value).name().equalsIgnoreCase(test)) {
                        instance = value;
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Create a hash code for the specified enumerated value which will be consistent across runs of the virtual
     * machine.  Normal {@code Enum} hash codes are based on the default virtual machine hashing behavior which is
     * only valid for the life of the virtual machine in which they were created.
     *
     * <p> This method allows for reproducible hash codes that would be useful in persistent stores. </p>
     *
     * @param enumValue The enumerated value.
     *
     * @return A reproducible hash code for the specified enumerated value.
     */
    public static int hashCode(Enum<?> enumValue) {
        return (enumValue != null) ? Objects.hash(enumValue.getDeclaringClass().getName(),
                enumValue.name(), enumValue.ordinal()) : 0;
    }

    /**
     * Constructor.
     */
    private Enums() {
        super();
    }
}