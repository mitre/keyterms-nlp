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

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import keyterms.util.text.Strings;

/**
 * A transform key describes a text transformation in terms of source, target and scheme.
 *
 * <p> Each key part is allowed to have a set of descriptive qualifiers which act as discriminators and search fields.
 * </p>
 */
public class TransformKey
        implements Comparable<TransformKey>, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -2397584822041657306L;

    /**
     * The pattern used to identify and parse transformation keys from textual representations thereof.
     */
    public static final Pattern KEY_PATTERN = Pattern.compile("([^-/]+)-([^/-]+)(/([^-/]+)?)?");

    /**
     * The original key text.
     */
    private final String text;

    /**
     * A flag indicating whether the transliterator is a custom transliterator.
     * <p> The non-custom transliterators are provided by third party packages such as ICU4J. </p>
     */
    private final boolean custom;

    /**
     * The description of the types of input texts processed by the transform.
     */
    private final EndPoint source;

    /**
     * The description of the type of output text produced by the transform.
     */
    private final EndPoint target;

    /**
     * The description of the standard being applied in the transform.
     */
    private final Scheme scheme;

    /**
     * Constructor.
     *
     * @param keyText The textual representation of the key.
     */
    public TransformKey(CharSequence keyText) {
        this(false, keyText);
    }

    /**
     * Constructor.
     *
     * @param custom A flag indicating whether the transliterator is a custom transliterator.
     * @param keyText The textual representation of the key.
     */
    public TransformKey(boolean custom, CharSequence keyText) {
        super();
        this.custom = custom;
        if (Strings.hasText(keyText)) {
            text = Strings.trim(keyText);
            Matcher matcher = KEY_PATTERN.matcher(text);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid transform key: " + keyText);
            }
            source = new EndPoint(matcher.group(1));
            target = new EndPoint(matcher.group(2));
            scheme = new Scheme(matcher.group(4));
        } else {
            text = "";
            source = new EndPoint(null);
            target = new EndPoint(null);
            scheme = new Scheme(null);
        }
    }

    /**
     * Get the original key text.
     *
     * @return The original key text.
     */
    public String getText() {
        return text;
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
        return custom;
    }

    /**
     * Get the description of the types of input texts processed by the transliterator.
     *
     * @return The description of the types of input texts processed by the transliterator.
     */
    public EndPoint getSource() {
        return source;
    }

    /**
     * Get the description of the type of output text produced by the transliterator.
     *
     * @return The description of the type of output text produced by the transliterator.
     */
    public EndPoint getTarget() {
        return target;
    }

    /**
     * Get the description of the standard being applied in the transliteration.
     *
     * @return The description of the standard being applied in the transliteration.
     */
    public Scheme getScheme() {
        return scheme;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(source, target, scheme);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof TransformKey) &&
                (Objects.equals(source, ((TransformKey)obj).source)) &&
                (Objects.equals(target, ((TransformKey)obj).target)) &&
                (Objects.equals(scheme, ((TransformKey)obj).scheme)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(TransformKey o) {
        int diff = source.compareTo(o.source);
        if (diff == 0) {
            diff = target.compareTo(o.target);
            if (diff == 0) {
                diff = scheme.compareTo(o.scheme);
            }
        }
        return diff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return source + "-" + target + ((scheme.isBlank()) ? "" : "/" + scheme);
    }
}