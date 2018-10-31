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

import java.util.function.Function;

/**
 * The functional interface for a transliterator.
 *
 * <p> A transliterator at its core is a simple text transformation. </p>
 */
public abstract class Transliterator
        implements Function<CharSequence, String> {
    /**
     * The unique key describing the text transformation.
     */
    protected final TransformKey key;

    /**
     * Constructor.
     *
     * @param keyText The textual representation of the transformation key.
     */
    public Transliterator(CharSequence keyText) {
        this(false, keyText);
    }

    /**
     * Constructor.
     *
     * @param custom A flag indicating whether the transliterator is a custom transliterator.
     * @param keyText The textual representation of the transformation key.
     */
    public Transliterator(boolean custom, CharSequence keyText) {
        super();
        key = new TransformKey(custom, keyText);
    }

    /**
     * Get the descriptive key for the text transformation.
     *
     * @return The transform key.
     */
    public TransformKey getKey() {
        return key;
    }

    /**
     * Determine if the transliterator is a custom transliterator.
     *
     * <p> Custom transliterators are defined in this code base. </p>
     * <p> The non-custom transliterators are provided by third party packages such as ICU4J. </p>
     *
     * @return A flag indicating whether the transliterator is a custom transliterator.
     */
    public boolean isCustom() {
        return key.isCustom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof Transliterator) &&
                (getClass().equals(obj.getClass())) &&
                (key.equals(((Transliterator)obj).key)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + key + "]";
    }
}