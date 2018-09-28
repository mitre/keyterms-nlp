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
import java.util.stream.Collectors;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;

/**
 * An alternate JSON exposure for Language definitions.
 */
public class LanguageDef {
    /**
     * The primary identifier for the language.
     */
    private final String code;

    /**
     * The equivalent 639-1 identifier.
     *
     * <p> May be null. </p>
     */
    private final String part1;

    /**
     * The equivalent 639-2 identifier of the bibliographic applications code set.
     *
     * <p> May be null. </p>
     */
    private final String part2B;

    /**
     * The equivalent 639-2 identifier of the terminology applications code set.
     *
     * <p> May be null. </p>
     */
    private final String part2T;

    /**
     * Alternate codes.
     */
    private final Set<String> altCodes;

    /**
     * The language scope.
     */
    private final String scope;

    /**
     * The language type.
     */
    private final String type;

    /**
     * The primary English name.
     */
    private final String englishName;

    /**
     * The native name.
     */
    private final String nativeName;

    /**
     * Alternate names.
     */
    private final Set<String> aliases;

    /**
     * The preferred script for the language.
     *
     * <p> May be {@code null}. </p>
     */
    private final String preferredScript;

    /**
     * The known primary scripts for the language.
     */
    private final Set<String> scripts;

    /**
     * The macro language for this language.
     */
    private String macroLanguage;

    /**
     * The member languages which belong to this macro language.
     */
    private Set<String> members;

    /**
     * Constructor.
     *
     * @param language The language definition being wrapped.
     */
    public LanguageDef(Language language) {
        super();
        code = language.getCode();
        part1 = language.getPart1();
        part2B = language.getPart2B();
        part2T = language.getPart2T();
        altCodes = language.getAltCodes();
        scope = (language.getScope() != null) ? language.getScope().name() : null;
        type = (language.getType() != null) ? language.getType().name() : null;
        englishName = language.getName();
        nativeName = language.getNativeName();
        aliases = language.getAliases();
        preferredScript = (language.getPreferredScript() != null) ? language.getPreferredScript().getCode() : null;
        scripts = language.getScripts().stream().map(Script::getCode).collect(Collectors.toSet());
        macroLanguage = (language.getMacroLanguage() != null) ? language.getMacroLanguage().getCode() : null;
        members = language.getMembers().stream().map(Language::getCode).collect(Collectors.toSet());
    }
}