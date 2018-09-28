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
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import keyterms.util.collect.Bags;
import keyterms.util.io.Encoding;
import keyterms.util.lang.Enums;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.SimpleDefinitions;

/**
 * A pool of the ISO-639-3 language definitions.
 *
 * <p> Derived from <a href="www.sil.org/iso639-3">SIL</a>. </p>
 * <p> and <a href="https://iso639-3.sil.org/code_tables/639/data">SIL</a>. </p>
 */
public final class Language
        implements StdDef, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 6568499651382845230L;

    /**
     * The language scopes as defined by the ISO-639-3 standard.
     */
    public enum Scope {
        INDIVIDUAL,
        MACROLANGUAGE,
        SPECIAL
    }

    /**
     * The language types as defined by the ISO-639-3 standard.
     */
    public enum Type {
        ANCIENT,
        CONSTRUCTED,
        EXTINCT,
        HISTORICAL,
        LIVING,
        SPECIAL
    }

    /**
     * The list of values.
     */
    private static final List<Language> VALUES;

    /**
     * Values by code.
     */
    private static final Map<String, Language> BY_CODE = new HashMap<>();

    /**
     * Values by name.
     */
    private static final Map<String, Set<Language>> BY_NAME = new HashMap<>();

    // Initialize the value space.
    static {
        List<Language> values = new ArrayList<>();
        Map<String, String> macroMap = new HashMap<>();
        Map<String, List<String>> memberMap = new HashMap<>();
        try {
            InputStream stream = Language.class.getResourceAsStream("rsc/languages.lst");
            String languagesList = Encoding.decode(stream.readAllBytes(), Encoding.UTF8);
            SimpleDefinitions parser = new SimpleDefinitions(languagesList);
            while (parser.hasMore()) {
                String code = parser.getField("code");
                String part1 = parser.getField("part1");
                String part2B = parser.getField("part2B");
                String part2T = parser.getField("part2T");
                List<String> altCodes = parser.getList("altCodes");
                Scope scope = Enums.find(Scope.class, parser.getField("scope"));
                Type type = Enums.find(Type.class, parser.getField("type"));
                String englishName = parser.getField("name");
                String nativeName = parser.getField("nativeName");
                List<String> aliases = parser.getList("aliases");
                Script script = Script.byCode(parser.getField("preferredScript"));
                List<Script> scripts = parser.getList("scripts").stream()
                        .map(Script::byCode)
                        .collect(Collectors.toList());
                String macro = parser.getField("macroLanguage");
                if (Strings.hasText(macro)) {
                    macroMap.put(code, macro);
                }
                List<String> members = parser.getList("members");
                if (!members.isEmpty()) {
                    memberMap.put(code, members);
                }
                Language language = new Language(code, part1, part2B, part2T, altCodes,
                        scope, type, englishName, nativeName, aliases, script, scripts);
                values.add(language);
                Standards.putCode(Language.class, BY_CODE, code, language);
                Standards.putCode(Language.class, BY_CODE, part1, language);
                Standards.putCode(Language.class, BY_CODE, part2B, language);
                Standards.putCode(Language.class, BY_CODE, part2T, language);
                altCodes.forEach((alt) -> Standards.putCode(Language.class, BY_CODE, alt, language));
                Standards.putName(Language.class, BY_NAME, englishName, language);
                Standards.putName(Language.class, BY_NAME, nativeName, language);
                aliases.forEach((alias) -> Standards.putName(Language.class, BY_NAME, alias, language));
            }
            macroMap.forEach((k, v) ->
                    byCode(k).setMacroLanguage(byCode(v)));
            memberMap.forEach((k, v) ->
                    byCode(k).setMembers(v.stream().map(Language::byCode).collect(Collectors.toList())));
        } catch (Exception error) {
            LoggerFactory.getLogger(Language.class).error("Could not load language definitions.", error);
        }
        VALUES = Collections.unmodifiableList(values);
    }

    /**
     * The official United Nations Languages.
     */
    public static final Language ARABIC = byCode("ara");
    public static final Language ENGLISH = byCode("eng");
    public static final Language FRENCH = byCode("fra");
    public static final Language GERMAN = byCode("deu");
    public static final Language RUSSIAN = byCode("rus");
    public static final Language SPANISH = byCode("spa");

    /**
     * The official United Nations Languages.
     */
    public static final List<Language> UN_LANGUAGES = Bags.arrayList(
            ARABIC, ENGLISH, FRENCH, GERMAN, RUSSIAN, SPANISH
    );

    /**
     * The official European Union Languages.
     */
    // Also English, French, German, Spanish (already listed under the U.N. languages)
    public static final Language BULGARIAN = byCode("bul");
    public static final Language CROATIAN = byCode("hrv");
    public static final Language CZECH = byCode("ces");
    public static final Language DANISH = byCode("dan");
    public static final Language DUTCH = byCode("nld");
    public static final Language ESTONIAN = byCode("est");
    public static final Language FINNISH = byCode("fin");
    public static final Language GREEK = byCode("ell");
    public static final Language HUNGARIAN = byCode("hun");
    public static final Language IRISH = byCode("gle");
    public static final Language ITALIAN = byCode("ita");
    public static final Language LATVIAN = byCode("lav");
    public static final Language LITHUANIAN = byCode("lit");
    public static final Language MALTESE = byCode("mlt");
    public static final Language POLISH = byCode("pol");
    public static final Language PORTUGUESE = byCode("por");
    public static final Language ROMANIAN = byCode("ron");
    public static final Language SLOVAK = byCode("slk");
    public static final Language SLOVENIAN = byCode("slv");
    public static final Language SWEDISH = byCode("swe");

    /**
     * The official European Union Languages.
     */
    public static final List<Language> EU_LANGUAGES = Bags.arrayList(
            ARABIC, BULGARIAN, CROATIAN, CZECH, DANISH, DUTCH, ENGLISH, ESTONIAN, FINNISH, FRENCH, GERMAN,
            GREEK, HUNGARIAN, IRISH, ITALIAN, LATVIAN, LITHUANIAN, MALTESE, POLISH, PORTUGUESE, ROMANIAN,
            RUSSIAN, SLOVAK, SLOVENIAN, SPANISH, SWEDISH
    );

    /**
     * Others.
     */
    public static final Language CHINESE = byCode("zho");
    public static final Language HEBREW = byCode("heb");
    public static final Language JAPANESE = byCode("jpn");
    public static final Language KOREAN = byCode("kor");
    public static final Language UKRANIAN = byCode("ukr");

    /**
     * A language that may be returned in lieu of {@code null}.
     */
    public static final Language UND = byCode("und");

    /**
     * Get the language values.
     *
     * @return The language values.
     */
    public static List<Language> values() {
        return VALUES;
    }

    /**
     * Get the language given its text code.
     *
     * @param code The text code.
     *
     * @return The specified language.
     */
    public static Language byCode(CharSequence code) {
        return BY_CODE.get(Standards.key(code));
    }

    /**
     * Get the language given its textual name or alias.
     *
     * @param name The language name.
     *
     * @return The specified language.
     */
    public static Language byName(CharSequence name) {
        Language language = null;
        Set<Language> byName = BY_NAME.get(Standards.key(name));
        if (byName != null) {
            if (byName.size() == 1) {
                language = byName.stream().findFirst().orElse(null);
            }
            if (language == null) {
                byName = byName.stream()
                        .filter((l) -> Type.LIVING.equals(l.getType()))
                        .collect(Collectors.toSet());
                if (byName.size() == 1) {
                    language = byName.stream().findFirst().orElse(null);
                }
            }
            if (language == null) {
                byName = byName.stream()
                        .filter((l) -> Scope.INDIVIDUAL.equals(l.getScope()))
                        .collect(Collectors.toSet());
                if (byName.size() == 1) {
                    language = byName.stream().findFirst().orElse(null);
                }
            }
        }
        return language;
    }

    /**
     * Get the language given a textual representation.
     *
     * <p> This method prefers codes over names. </p>
     *
     * @param text The textual representation of the value.
     *
     * @return The specified value.
     */
    public static Language byText(CharSequence text) {
        Language value = byCode(text);
        value = (value != null) ? value : byName(text);
        return value;
    }

    /**
     * Get all matching language definitions given a textual representation.
     *
     * @param text The textual representation of the value.
     *
     * @return The specified values.
     */
    public static Set<Language> find(CharSequence text) {
        Set<Language> languages = new LinkedHashSet<>();
        String key = Standards.key(text);
        Language byCode = BY_CODE.get(key);
        if (byCode != null) {
            languages.add(byCode);
        }
        Set<Language> byName = BY_NAME.get(key);
        if (byName != null) {
            languages.addAll(byName);
        }
        return languages;
    }

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
    private final Scope scope;

    /**
     * The language type.
     */
    private final Type type;

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
    private final Script preferredScript;

    /**
     * The known primary scripts for the language.
     */
    private final Set<Script> scripts;

    /**
     * The macro language for this language.
     */
    private Language macroLanguage;

    /**
     * The member languages which belong to this macro language.
     */
    private Set<Language> members = Collections.emptySet();

    /**
     * Constructor.
     *
     * @param code The primary identifier for the language.
     * @param part1 The equivalent 639-1 identifier.
     * @param part2B The equivalent 639-2 identifier of the bibliographic applications code set.
     * @param part2T The equivalent 639-2 identifier of the terminology applications code set.
     * @param altCodes The alternate codes.
     * @param englishName The primary English name.
     * @param nativeName the native name.
     * @param aliases The alternate names.
     * @param preferredScript The preferred script.
     * @param scripts The primary scripts.
     */
    private Language(String code, String part1, String part2B, String part2T, List<String> altCodes,
            Scope scope, Type type, String englishName, String nativeName, List<String> aliases,
            Script preferredScript, List<Script> scripts) {
        this.code = code;
        this.part1 = part1;
        this.part2B = part2B;
        this.part2T = part2T;
        this.altCodes = Collections.unmodifiableSet(new HashSet<>(altCodes));
        this.scope = scope;
        this.type = type;
        this.englishName = englishName;
        this.nativeName = nativeName;
        this.aliases = Collections.unmodifiableSet(new HashSet<>(aliases));
        this.preferredScript = preferredScript;
        this.scripts = Collections.unmodifiableSet(new HashSet<>(scripts));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Get the equivalent 639-1 identifier.
     *
     * @return The equivalent 639-1 identifier.
     */
    public String getPart1() {
        return part1;
    }

    /**
     * Get the equivalent 639-2 identifier of the bibliographic applications code set.
     *
     * @return The equivalent 639-2 identifier of the bibliographic applications code set.
     */
    public String getPart2B() {
        return part2B;
    }

    /**
     * Get the equivalent 639-2 identifier of the terminology applications code set.
     *
     * @return The equivalent 639-2 identifier of the terminology applications code set.
     */
    public String getPart2T() {
        return part2T;
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
     * Get the language scope.
     *
     * @return The language scope.
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * Get the language type.
     *
     * @return The language type.
     */
    public Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return englishName;
    }

    /**
     * Get the primary English name.
     *
     * @return The primary English name.
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * The native name for the language.
     *
     * @return The native name for the language.
     */
    public String getNativeName() {
        return nativeName;
    }

    /**
     * Get the known aliases for the language.
     *
     * @return The known aliases for the language.
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * Get the preferred primary script for the language.
     *
     * @return The preferred primary script for the language.
     */
    public Script getPreferredScript() {
        return preferredScript;
    }

    /**
     * Get the primary scripts for the language.
     *
     * @return The primary scripts for the language.
     */
    public Set<Script> getScripts() {
        return scripts;
    }

    /**
     * Set the macro language for this language.
     *
     * @param macroLanguage The macro language for this language.
     */
    private void setMacroLanguage(Language macroLanguage) {
        this.macroLanguage = macroLanguage;
    }

    /**
     * Set the member languages which belong to this macro language.
     *
     * @param members The member languages which belong to this macro language.
     */
    private void setMembers(List<Language> members) {
        if (members != null) {
            this.members = Collections.unmodifiableSet(new HashSet<>(members));
        } else {
            this.members = Collections.emptySet();
        }
    }

    /**
     * Get the macro language for this language.
     *
     * <p> May be {@code null}. </p>
     *
     * @return The macro language for this language.
     */
    public Language getMacroLanguage() {
        return macroLanguage;
    }

    /**
     * Determine if this is a definition of a macro language.
     *
     * <p> Language definition standards that do not support this feature should return {@code false}. </p>
     *
     * @return A flag indicating whether this is a definition of a macro language.
     */
    public boolean isMacroLanguage() {
        return !members.isEmpty();
    }

    /**
     * Get the member languages for this macro language.
     *
     * <p> The resultant list will be empty unless this is a macro language. </p>
     * <p> Language definition standards that do not support this feature should return an empty list. </p>
     *
     * @return The member languages for this macro language.
     */
    public Set<Language> getMembers() {
        return members;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return ((this == obj) || ((obj instanceof Language) && (Objects.equals(code, ((Language)obj).code))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "." + code.toUpperCase();
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
        Language value = BY_CODE.get(Standards.key(code));
        if (value == null) {
            throw new InvalidObjectException("Unknown language: " + this);
        }
        return value;
    }
}