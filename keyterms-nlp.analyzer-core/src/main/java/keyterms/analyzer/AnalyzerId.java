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

package keyterms.analyzer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import keyterms.util.text.Strings;

/**
 * A unique identifier for an analyzer which specifies the base analyzer and the construction options which distinguish
 * it from other instances of the same base analyzer.
 */
public class AnalyzerId
        implements Comparable<AnalyzerId>, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -41195304790768090L;

    /**
     * Construct a key by parsing it's associated text.
     *
     * <p> The assumption is that the primary analyzer identifier is followed by an underscore ({@code '_'}) character,
     * and that subsequent options are delimited by a dash ({@code '-'}) character. </p>
     * <p> E.G.: "TST_f-x" = "TST" analyzer with "f" and "x" options. </p>
     *
     * @param text The textual representation of the analyzer key.
     *
     * @return The equivalent analyzer key.
     */
    public static AnalyzerId valueOf(CharSequence text) {
        AnalyzerId key = null;
        if (!Strings.isBlank(text)) {
            String[] parts = text.toString().split("_");
            String analyzerId = parts[0];
            String[] options = new String[0];
            if (parts.length > 1) {
                options = parts[1].split("-");
            }
            key = new AnalyzerId(analyzerId, options);
        }
        return key;
    }

    /**
     * The primary identifier for the analyzer.
     */
    private final String analyzerId;

    /**
     * The identifiers which detail options which make the analyzer unique from other instances of the same base
     * analyzer.
     */
    private final Set<String> options = new TreeSet<>();

    /**
     * Constructor.
     *
     * <p> This protected constructor exists only to aid in serialization. </p>
     */
    protected AnalyzerId() {
        super();
        analyzerId = null;
    }

    /**
     * Constructor.
     *
     * @param analyzerId The primary identifier for the analyzer.
     * @param options The identifiers which detail options which make the analyzer unique from other instances of the
     * same base analyzer.
     */
    public AnalyzerId(String analyzerId, String... options) {
        super();
        if (Strings.isBlank(analyzerId)) {
            throw new NullPointerException("Base analyzer identifier is required.");
        }
        this.analyzerId = Strings.trim(analyzerId);
        if (options != null) {
            Stream.of(options)
                    .filter((o) -> !Strings.isBlank(o))
                    .map(Strings::trim)
                    .forEach(this.options::add);
        }
    }

    /**
     * Get the primary identifier for the analyzer.
     *
     * @return The primary identifier for the analyzer.
     */
    public String getAnalyzerId() {
        return analyzerId;
    }

    /**
     * Get the identifiers which detail options which make the analyzer unique from other instances of the same base
     * analyzer.
     *
     * @return The identifiers which detail options which make the analyzer unique from other instances of the same base
     * analyzer.
     */
    public Set<String> getOptions() {
        return options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(analyzerId, Arrays.deepHashCode(options.toArray()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (obj == this);
        if ((!equals) && (obj instanceof AnalyzerId)) {
            AnalyzerId analyzerId = (AnalyzerId)obj;
            equals = ((Objects.equals(this.analyzerId, analyzerId.analyzerId)) &&
                    (options.equals(analyzerId.options)));

        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AnalyzerId o) {
        int diff = Comparator.comparing(AnalyzerId::getAnalyzerId).compare(this, o);
        if (diff == 0) {
            int this_s = options.size();
            int that_s = o.options.size();
            diff = this_s - that_s;
            if ((diff == 0) && (this_s > 0)) {
                Iterator<String> that = o.options.iterator();
                for (String option : options) {
                    diff = option.compareTo(that.next());
                    if (diff != 0) {
                        break;
                    }
                }
            }
        }
        return (int)Math.signum(diff);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(analyzerId);
        if (!options.isEmpty()) {
            sb.append('_');
            sb.append(String.join("-", options));
        }
        return sb.toString();
    }
}