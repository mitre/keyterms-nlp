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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import keyterms.util.text.Strings;

/**
 * A named transformation scheme.
 */
public class Scheme
        implements Comparable<Scheme>, Predicate<CharSequence> {
    /**
     * The textual representation of the scheme.
     */
    private final String text;

    /**
     * The scheme's name.
     */
    private final String name;

    /**
     * The qualifiers to the scheme.
     */
    private final Set<String> qualifiers;

    /**
     * Constructor.
     *
     * <p> Creates a blank scheme. </p>
     */
    public Scheme() {
        super();
        name = "";
        qualifiers = Collections.emptySet();
        text = "";
    }

    /**
     * Constructor.
     *
     * @param text The textual representation of the scheme.
     */
    public Scheme(CharSequence text) {
        super();
        if (Strings.hasText(text)) {
            List<String> parts = Arrays.stream(Strings.trim(text).split("_"))
                    .filter(Strings::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList());
            name = parts.remove(0);
            qualifiers = Collections.unmodifiableSortedSet(new TreeSet<>(parts));
            StringBuilder textBuilder = new StringBuilder();
            textBuilder.append(name);
            qualifiers.forEach(q -> textBuilder.append('_').append(q));
            this.text = textBuilder.toString();
        } else {
            name = "";
            qualifiers = Collections.emptySet();
            this.text = "";
        }
    }

    /**
     * Get the scheme's name.
     *
     * @return The scheme's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the qualifiers to the scheme.
     *
     * @return The qualifiers to the scheme.
     */
    public Set<String> getQualifiers() {
        return qualifiers;
    }

    /**
     * Determine if the scheme is specified.
     *
     * @return A flag indicating whether the scheme is specified.
     */
    public boolean isBlank() {
        return ((Strings.isBlank(name)) && (qualifiers.isEmpty()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        List<Object> hashValues = new ArrayList<>();
        hashValues.add(name);
        hashValues.addAll(qualifiers);
        return Arrays.hashCode(hashValues.toArray());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof Scheme) &&
                (Objects.equals(name, ((Scheme)obj).name)) &&
                (Objects.equals(qualifiers, ((Scheme)obj).qualifiers)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Scheme scheme) {
        return text.compareTo(scheme.text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(CharSequence text) {
        boolean accept;
        Scheme toTest = new Scheme(text);
        if (toTest.isBlank()) {
            accept = true;
        } else {
            Set<String> diff = new HashSet<>(toTest.qualifiers);
            diff.add(toTest.name);
            diff.removeAll(qualifiers);
            diff.remove(name);
            accept = diff.isEmpty();
        }
        return accept;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder tsb = new StringBuilder(name);
        qualifiers.forEach(q -> tsb.append('_').append(q));
        return tsb.toString();
    }
}