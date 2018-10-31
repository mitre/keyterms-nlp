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

package keyterms.nlp.transliterate;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.text.Strings;

/**
 * An enumeration of the transformation key source/target types.
 */
public enum EndPointType {
    /**
     * The key part represents a written language.
     */
    WRITTEN(WrittenLanguage.class),
    /**
     * The key part represents a spoken language.
     */
    LANGUAGE(Language.class),
    /**
     * The key part represents a written script.
     */
    SCRIPT(Script.class),
    /**
     * The key part represents an unspecified type.
     */
    OTHER(CharSequence.class),
    /**
     * The key part accepts all inputs.
     */
    ANY(Void.TYPE);

    /**
     * Get the type associated with the specified end-point object.
     *
     * @param endPoint The end-point.
     *
     * @return The type associated with the specified end-point object.
     */
    public static EndPointType of(Object endPoint) {
        EndPointType type = ANY;
        for (EndPointType t : values()) {
            if (t.typeClass.isInstance(endPoint)) {
                type = t;
                break;
            }
        }
        if ((type == OTHER) && (Strings.isBlank(endPoint.toString()))) {
            type = ANY;
        }
        return type;
    }

    /**
     * The base class which this type represents.
     */
    private final Class<?> typeClass;

    /**
     * Constructor.
     *
     * @param typeClass The base class which this type represents.
     */
    EndPointType(Class<?> typeClass) {
        this.typeClass = typeClass;
    }
}