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

package keyterms.nlp.iso;

import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.LoggerFactory;

import keyterms.util.io.Encoding;
import keyterms.util.text.parser.Parsers;
import keyterms.util.text.parser.SimpleDefinitions;

/**
 * A pool of the ISO-3166-1 country codes.
 *
 * <p> Derived from <a href="https://www.iso.org/obp/ui/#search">iso.org</a>. </p>
 */
public final class Country
        implements StdDef, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 267879199299528744L;

    /**
     * The list of values.
     */
    private static final List<Country> VALUES;

    /**
     * Values by numeric code.
     */
    private static final Map<Integer, Country> BY_NUMBER = new HashMap<>();

    /**
     * Values by code.
     */
    private static final Map<String, Country> BY_CODE = new HashMap<>();

    /**
     * Values by name.
     */
    private static final Map<String, Set<Country>> BY_NAME = new HashMap<>();

    // Initialize the value space.
    static {
        List<Country> values = new ArrayList<>();
        try {
            InputStream stream = Country.class.getResourceAsStream("rsc/countries.lst");
            String countriesList = Encoding.decode(stream.readAllBytes(), Encoding.UTF8);
            SimpleDefinitions parser = new SimpleDefinitions(countriesList);
            while (parser.hasMore()) {
                Integer number = Parsers.INTEGERS.parse(parser.getField("number"), null);
                if (number == null) {
                    throw new IllegalArgumentException("Invalid country number at line #" + parser.getLineNumber());
                }
                String iso2 = parser.getField("iso2");
                String iso3 = parser.getField("iso3");
                List<String> altCodes = parser.getList("altCodes");
                String name = parser.getField("name");
                List<String> aliases = parser.getList("aliases");
                Country country = new Country(number, iso2, iso3, altCodes, name, aliases);
                values.add(country);
                BY_NUMBER.put(number, country);
                Standards.putCode(Country.class, BY_CODE, iso2, country);
                Standards.putCode(Country.class, BY_CODE, iso3, country);
                altCodes.forEach((alt) -> Standards.putCode(Country.class, BY_CODE, alt, country));
                Standards.putName(Country.class, BY_NAME, name, country);
                aliases.forEach((alias) -> Standards.putName(Country.class, BY_NAME, alias, country));
            }
        } catch (Exception error) {
            LoggerFactory.getLogger(Country.class).error("Could not load country definitions.", error);
        }
        VALUES = Collections.unmodifiableList(values);
    }

    /**
     * Convenience constants.
     */
    public static final Country USA = byCode("usa");

    /**
     * Get the country values.
     *
     * @return The country values.
     */
    public static List<Country> values() {
        return VALUES;
    }

    /**
     * Get the country given its numeric code.
     *
     * @param number The numeric country code.
     *
     * @return The specified country.
     */
    public static Country byNumber(int number) {
        return BY_NUMBER.get(number);
    }

    /**
     * Get the country given its text code.
     *
     * @param code The text code.
     *
     * @return The specified country.
     */
    public static Country byCode(CharSequence code) {
        return BY_CODE.get(Standards.key(code));
    }

    /**
     * Get the country given its textual name or alias.
     *
     * @param name The country name.
     *
     * @return The specified country.
     */
    public static Country byName(CharSequence name) {
        Country country = null;
        Set<Country> byName = BY_NAME.get(Standards.key(name));
        if ((byName != null) && (byName.size() == 1)) {
            country = byName.stream().findFirst().orElse(null);
        }
        return country;
    }

    /**
     * Get the country given a textual representation.
     *
     * <p> This method prefers codes over names. </p>
     *
     * @param text The textual representation of the value.
     *
     * @return The specified value.
     */
    public static Country byText(CharSequence text) {
        Country value = byCode(text);
        value = (value != null) ? value : byName(text);
        if (value == null) {
            Integer num = Parsers.INTEGERS.parse(text, null);
            if (num != null) {
                value = byNumber(num);
            }
        }
        return value;
    }

    /**
     * Get all matching country definitions given a textual representation.
     *
     * @param text The textual representation of the value.
     *
     * @return The specified values.
     */
    public static Set<Country> find(CharSequence text) {
        Set<Country> countries = new LinkedHashSet<>();
        String key = Standards.key(text);
        Country byNumber = BY_NUMBER.get(Parsers.INTEGERS.parse(key, null));
        if (byNumber != null) {
            countries.add(byNumber);
        }
        Country byCode = BY_CODE.get(key);
        if (byCode != null) {
            countries.add(byCode);
        }
        Set<Country> byName = BY_NAME.get(key);
        if (byName != null) {
            countries.addAll(byName);
        }
        return countries;
    }

    /**
     * The numeric country code.
     */
    private final int number;

    /**
     * The 2-letter country code.
     */
    private final String iso2;

    /**
     * The 3-letter country code.
     */
    private final String iso3;

    /**
     * Alternate codes.
     */
    private final Set<String> altCodes;

    /**
     * The primary English name.
     */
    private final String name;

    /**
     * Alternate names.
     */
    private final Set<String> aliases;

    /**
     * Constructor.
     *
     * @param number The numeric country code.
     * @param iso2 The 2-letter country code.
     * @param iso3 The 3-letter country code.
     * @param altCodes The alternate codes.
     * @param name The primary English name.
     * @param aliases The alternate names.
     */
    Country(int number, String iso2, String iso3, List<String> altCodes, String name, List<String> aliases) {
        this.number = number;
        this.iso2 = iso2;
        this.iso3 = iso3;
        this.altCodes = Collections.unmodifiableSet(new HashSet<>(altCodes));
        this.name = name;
        this.aliases = Collections.unmodifiableSet(new HashSet<>(aliases));
    }

    /**
     * {@inheritDoc}
     */
    public String getCode() {
        return (iso3 != null) ? iso3 : iso2;
    }

    /**
     * Get the numeric country code.
     *
     * @return The numeric country code.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the 2-letter country code.
     *
     * @return The 2-letter country code.
     */
    public String getIso2() {
        return iso2;
    }

    /**
     * Get the 3-letter country code.
     *
     * @return The 3-letter country code.
     */
    public String getIso3() {
        return iso3;
    }

    /**
     * Get the known alternate codes.
     *
     * @return The known alternate codes.
     */
    public Set<String> getAltCodes() {
        return altCodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the known aliases for the country.
     *
     * @return The known aliases for the country.
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return ((this == obj) || ((obj instanceof Country) && (Objects.equals(getCode(), ((Country)obj).getCode()))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "." + getCode().toUpperCase();
    }

    /**
     * Ensure only instantiated instances are returned during serialization.
     *
     * @return The resolved object.
     *
     * @throws ObjectStreamException for input/output errors
     */
    private Object readResolve()
            throws ObjectStreamException {
        Country value = BY_CODE.get(Standards.key(getCode()));
        if (value == null) {
            throw new InvalidObjectException("Unknown country: " + this);
        }
        return value;
    }
}