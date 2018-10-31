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

package keyterms.service.json;

import java.util.Set;

import keyterms.nlp.iso.Country;

/**
 * An alternate JSON exposure for Country definitions.
 */
public class CountryDef {
    /**
     * The numeric country code.
     */
    private final int number;

    /**
     * The primary country code.
     */
    private final String code;

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
     * @param country The country definition being wrapped.
     */
    public CountryDef(Country country) {
        super();
        number = country.getNumber();
        code = country.getCode();
        iso2 = country.getIso2();
        iso3 = country.getIso3();
        altCodes = country.getAltCodes();
        name = country.getName();
        aliases = country.getAliases();
    }
}