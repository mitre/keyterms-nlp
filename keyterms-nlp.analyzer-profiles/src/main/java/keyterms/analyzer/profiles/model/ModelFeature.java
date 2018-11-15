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

package keyterms.analyzer.profiles.model;

import java.io.Serializable;
import java.util.function.Predicate;

import keyterms.util.text.Formatter;
import keyterms.util.text.Parser;
import keyterms.util.text.Strings;

/**
 * The basic abstraction for a feature in a feature based classifier.
 */
public abstract class ModelFeature<F>
        implements Parser<F>, Formatter<F>, Predicate<Object>, Comparable<ModelFeature<?>>, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 1231838090478510037L;

    /**
     * The feature name.
     */
    protected final String name;

    /**
     * The value class associated with the feature.
     */
    protected final Class<F> valueClass;

    /**
     * The parser which can create feature values from textual representations.
     */
    protected final FeatureParser<F> parser;

    /**
     * The formatter which creates textual representations from feature values.
     */
    protected final FeatureFormatter<F> formatter;

    /**
     * Constructor.
     *
     * @param name The feature name.
     * @param valueClass The value class associated with the feature.
     * @param parser The parser which can create feature values from textual representations.
     * @param formatter The formatter which creates textual representations from feature values.
     */
    protected ModelFeature(String name, Class<F> valueClass, FeatureParser<F> parser, FeatureFormatter<F> formatter) {
        super();
        if (name == null) {
            throw new NullPointerException("Feature name required.");
        }
        if (valueClass == null) {
            throw new NullPointerException("Value class required.");
        }
        if (parser == null) {
            throw new NullPointerException("Value parser required.");
        }
        if (formatter == null) {
            throw new NullPointerException("Value formatter required.");
        }
        this.name = name;
        this.valueClass = valueClass;
        this.parser = new QuestionIsNullParser(parser);
        this.formatter = new NullIsQuestionFormatter(formatter);
    }

    /**
     * Get the feature's name.
     *
     * @return The feature's name.
     */
    public String name() {
        return name;
    }

    /**
     * Get the value class for the feature.
     *
     * @return The feature's value class.
     */
    public final Class<F> getValueClass() {
        return valueClass;
    }

    /**
     * Cast the specified value to the appropriate value class.
     *
     * @param value The value.
     *
     * @return The value cast as the feature's value class.
     */
    public F cast(Object value) {
        return valueClass.cast(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public F parse(CharSequence text) {
        return parser.parse(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String asText(F object) {
        return formatter.asText(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(Object value) {
        return ((value == null) || (valueClass.isInstance(value)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ModelFeature<?> feature) {
        return name.compareTo(feature.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof ModelFeature) && (name.equals(((ModelFeature)obj).name)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + name + ']';
    }

    /**
     * A wrapper around the feature specific parsers which converts blank or "?" texts to {@code null} in preference to
     * executing the base parser.
     */
    private class QuestionIsNullParser
            implements FeatureParser<F> {
        /**
         * The class serial version identifier.
         */
        private static final long serialVersionUID = 318045281133319642L;

        /**
         * The feature specific parser.
         */
        private final Parser<F> baseParser;

        /**
         * Constructor.
         *
         * @param baseParser The feature specific parser.
         */
        private QuestionIsNullParser(Parser<F> baseParser) {
            super();
            this.baseParser = baseParser;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public F parse(CharSequence text) {
            return ((Strings.isBlank(text)) || ("?".equals(Strings.trim(text)))) ? null : baseParser.parse(text);
        }
    }

    /**
     * A wrapper around the feature specific formatters which converts {@code null} values to "?" in preference to
     * executing the base formatter.
     */
    private class NullIsQuestionFormatter
            implements FeatureFormatter<F> {
        /**
         * The class serial version identifier.
         */
        private static final long serialVersionUID = -4762340751918584213L;

        /**
         * The feature specific formatter.
         */
        private final Formatter<F> baseFormatter;

        /**
         * Constructor.
         *
         * @param baseFormatter The feature specific formatter.
         */
        private NullIsQuestionFormatter(Formatter<F> baseFormatter) {
            super();
            this.baseFormatter = baseFormatter;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String asText(F object) {
            return (object != null) ? baseFormatter.asText(object) : "?";
        }
    }
}