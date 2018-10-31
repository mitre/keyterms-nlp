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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.text.Strings;

/**
 * The encapsulation of the information available about the input or output of a text transformation.
 */
public class EndPoint
        implements Comparable<EndPoint>, Predicate<CharSequence> {
    /**
     * The text used in textual representations of a transform key part in place of {@code null} for
     * transformations which have unspecified end points.
     */
    static final String ANY = "any";

    /**
     * The textual description of the end-point.
     */
    private final String text;

    /**
     * The end-point type.
     */
    private final EndPointType type;

    /**
     * The primary end-point target.
     *
     * <p> This may be one of Iso639-3 (language), Iso15924 (script), IsoWritten (language+script) or text. </p>
     */
    private final Object target;

    /**
     * The qualifiers to the end-point.
     */
    private final Set<String> qualifiers;

    /**
     * Constructor.
     *
     * @param text The textual description of the key part.
     */
    public EndPoint(CharSequence text) {
        super();
        if (Strings.hasText(text)) {
            List<String> parts = Arrays.stream(Strings.trim(text).split("_"))
                    .map(String::trim)
                    .collect(Collectors.toList());
            String p = parts.remove(0);
            Object first = null;
            if (Strings.isBlank(p)) {
                first = void.class;
            }
            if (ANY.equalsIgnoreCase(p)) {
                first = void.class;
            }
            if (first == null) {
                first = Script.byText(p);
            }
            if (first == null) {
                first = Language.byText(p);
                first = Language.UND.equals(first) ? null : first;
            }
            if (first == null) {
                first = p;
            }
            Script script = null;
            if (((first.equals(void.class)) || (first instanceof Language)) && (!parts.isEmpty())) {
                p = parts.remove(0);
                script = Script.byText(p);
                if (script != null) {
                    if (first.equals(void.class)) {
                        first = script;
                        script = null;
                    }
                } else {
                    parts.add(0, p);
                }
            }
            if (first.equals(void.class)) {
                first = null;
            }
            Object endPt = first;
            if ((first instanceof Language) && (script != null)) {
                endPt = new WrittenLanguage((Language)first, script);
            }
            target = endPt;
            type = EndPointType.of(endPt);
            qualifiers = Collections.unmodifiableSortedSet(new TreeSet<>(parts.stream()
                    .filter(Strings::hasText)
                    .map(String::trim)
                    .collect(Collectors.toSet())));
            StringBuilder nsb = new StringBuilder();
            switch (type) {
                case WRITTEN:
                    WrittenLanguage written = (WrittenLanguage)endPt;
                    nsb.append(written.getLanguage().getCode());
                    nsb.append('_');
                    nsb.append(written.getScript().getCode());
                    break;
                case LANGUAGE:
                    Language lang = (Language)endPt;
                    nsb.append(lang.getCode());
                    break;
                case SCRIPT:
                    Script scr = (Script)endPt;
                    nsb.append(scr.getCode());
                    break;
                case OTHER:
                    nsb.append(endPt);
                    break;
                case ANY:
                    nsb.append(ANY);
                    break;
                default:
            }
            qualifiers.forEach(q -> nsb.append('_').append(q));
            this.text = nsb.toString();
        } else {
            type = EndPointType.ANY;
            target = null;
            qualifiers = Collections.emptySet();
            this.text = ANY;
        }
    }

    /**
     * Get the end-point type.
     *
     * @return The end-point type.
     */
    public EndPointType getType() {
        return type;
    }

    /**
     * Get the primary end-point target.
     *
     * <p> This may be one of Iso639-3 (language), Iso15924 (script), IsoWritten or text. </p>
     *
     * @return the primary end-point target.
     */

    public Object getTarget() {
        return target;
    }

    /**
     * Get the textual qualifiers to the end-point.
     *
     * @return The textual qualifiers to the end-point.
     */
    public Set<String> getQualifiers() {
        return qualifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        List<Object> hashValues = new ArrayList<>();
        hashValues.add(type);
        hashValues.add(target);
        hashValues.addAll(qualifiers);
        return Arrays.hashCode(hashValues.toArray());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof EndPoint) &&
                (Objects.equals(type, ((EndPoint)obj).type)) &&
                (Objects.equals(target, ((EndPoint)obj).target)) &&
                (Objects.equals(qualifiers, ((EndPoint)obj).qualifiers)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(EndPoint o) {
        return Comparator.comparing(EndPoint::getType)
                .thenComparing(EndPoint::toString)
                .compare(this, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(CharSequence text) {
        boolean accept;
        EndPoint toTest = new EndPoint(text);
        switch (toTest.type) {
            case WRITTEN:
                accept = (type == EndPointType.WRITTEN) && (Objects.equals(target, toTest.target));
                break;
            case LANGUAGE:
                switch (type) {
                    case WRITTEN:
                        accept = Objects.equals(((WrittenLanguage)target).getLanguage(), toTest.target);
                        break;
                    case LANGUAGE:
                        accept = Objects.equals(target, toTest.target);
                        break;
                    default:
                        accept = false;
                }
                break;
            case SCRIPT:
                switch (type) {
                    case WRITTEN:
                        accept = Objects.equals(((WrittenLanguage)target).getScript(), toTest.target);
                        break;
                    case LANGUAGE:
                        Script preferredScript = ((Language)target).getPreferredScript();
                        accept = Objects.equals(preferredScript, toTest.target);
                        break;
                    case SCRIPT:
                        accept = Objects.equals(target, toTest.target);
                        break;
                    default:
                        accept = false;
                }
                break;
            case OTHER:
                accept = Objects.equals(text, toTest.text);
                break;
            case ANY:
                accept = true;
                break;
            default:
                accept = false;
        }
        if (accept) {
            Set<String> diff = new HashSet<>(toTest.qualifiers);
            diff.removeAll(qualifiers);
            accept = diff.isEmpty();
        }
        return accept;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return text;
    }
}