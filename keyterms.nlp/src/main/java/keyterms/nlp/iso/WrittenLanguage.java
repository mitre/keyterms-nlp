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

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import keyterms.util.collect.Bags;
import keyterms.util.text.Strings;

/**
 * An immutable language/script pair.
 */
public class WrittenLanguage
        implements Serializable {
    /**
     * The official United Nations languages.
     */
    public static final WrittenLanguage ARABIC = new WrittenLanguage(Language.ARABIC, Script.ARABIC);
    public static final WrittenLanguage ENGLISH = new WrittenLanguage(Language.ENGLISH, Script.LATIN);
    public static final WrittenLanguage FRENCH = new WrittenLanguage(Language.FRENCH, Script.LATIN);
    public static final WrittenLanguage GERMAN = new WrittenLanguage(Language.GERMAN, Script.LATIN);
    public static final WrittenLanguage RUSSIAN = new WrittenLanguage(Language.RUSSIAN, Script.CYRILLIC);
    public static final WrittenLanguage SPANISH = new WrittenLanguage(Language.SPANISH, Script.LATIN);

    /**
     * The official United Nations Languages.
     */
    public static final List<WrittenLanguage> UN_LANGUAGES = Bags.arrayList(
            ARABIC, ENGLISH, FRENCH, GERMAN, RUSSIAN, SPANISH
    );

    /**
     * The official European Union Languages.
     */
    // Also English, French, German, Spanish (already listed under the U.N. languages)
    public static final WrittenLanguage BULGARIAN = new WrittenLanguage(Language.BULGARIAN, Script.CYRILLIC);
    public static final WrittenLanguage CROATIAN = new WrittenLanguage(Language.CROATIAN, Script.LATIN);
    public static final WrittenLanguage CZECH = new WrittenLanguage(Language.CZECH, Script.LATIN);
    public static final WrittenLanguage DANISH = new WrittenLanguage(Language.DANISH, Script.LATIN);
    public static final WrittenLanguage DUTCH = new WrittenLanguage(Language.DUTCH, Script.LATIN);
    public static final WrittenLanguage ESTONIAN = new WrittenLanguage(Language.ESTONIAN, Script.LATIN);
    public static final WrittenLanguage FINNISH = new WrittenLanguage(Language.FINNISH, Script.LATIN);
    public static final WrittenLanguage GREEK = new WrittenLanguage(Language.GREEK, Script.GREEK);
    public static final WrittenLanguage HUNGARIAN = new WrittenLanguage(Language.HUNGARIAN, Script.LATIN);
    public static final WrittenLanguage IRISH = new WrittenLanguage(Language.IRISH, Script.LATIN);
    public static final WrittenLanguage ITALIAN = new WrittenLanguage(Language.ITALIAN, Script.LATIN);
    public static final WrittenLanguage LATVIAN = new WrittenLanguage(Language.LATVIAN, Script.LATIN);
    public static final WrittenLanguage LITHUANIAN = new WrittenLanguage(Language.LITHUANIAN, Script.LATIN);
    public static final WrittenLanguage MALTESE = new WrittenLanguage(Language.MALTESE, Script.LATIN);
    public static final WrittenLanguage POLISH = new WrittenLanguage(Language.POLISH, Script.LATIN);
    public static final WrittenLanguage PORTUGUESE = new WrittenLanguage(Language.PORTUGUESE, Script.LATIN);
    public static final WrittenLanguage ROMANIAN = new WrittenLanguage(Language.ROMANIAN, Script.LATIN);
    public static final WrittenLanguage SLOVAK = new WrittenLanguage(Language.SLOVAK, Script.LATIN);
    public static final WrittenLanguage SLOVENIAN = new WrittenLanguage(Language.SLOVENIAN, Script.LATIN);
    public static final WrittenLanguage SWEDISH = new WrittenLanguage(Language.SWEDISH, Script.LATIN);

    /**
     * The official European Union Languages.
     */
    public static final List<WrittenLanguage> EU_LANGUAGES = Bags.arrayList(
            ARABIC, BULGARIAN, CROATIAN, CZECH, DANISH, DUTCH, ENGLISH, ESTONIAN, FINNISH, FRENCH, GERMAN,
            GREEK, HUNGARIAN, IRISH, ITALIAN, LATVIAN, LITHUANIAN, MALTESE, POLISH, PORTUGUESE, ROMANIAN,
            RUSSIAN, SLOVAK, SLOVENIAN, SPANISH, SWEDISH
    );

    /**
     * Others.
     */
    public static final WrittenLanguage CHINESE_SIMPLIFIED =
            new WrittenLanguage(Language.CHINESE, Script.HAN_SIMPLIFIED);
    public static final WrittenLanguage CHINESE_TRADITIONAL =
            new WrittenLanguage(Language.CHINESE, Script.HAN_TRADITIONAL);
    public static final WrittenLanguage JAPANESE = new WrittenLanguage(Language.JAPANESE, Script.JAPANESE);
    public static final WrittenLanguage KOREAN = new WrittenLanguage(Language.KOREAN, Script.KOREAN);

    /**
     * Get a {@code WrittenLanguage} object given textual descriptions of the language and the script.
     *
     * @param language The textual representation of the desired language.
     * @param script The textual representation of the desired script.
     *
     * @return The specified {@code WrittenLanguage} value.
     */
    public static WrittenLanguage valueOf(CharSequence language, CharSequence script) {
        WrittenLanguage written = null;
        if ((Strings.hasText(language)) || (Strings.hasText(script))) {
            Language isoLanguage = Language.byText(language);
            Script isoScript = Script.byText(script);
            if ((isoLanguage != null) || (isoScript != null)) {
                written = new WrittenLanguage(isoLanguage, isoScript);
            }
        }
        return written;
    }

    /**
     * The language.
     */
    private final Language language;

    /**
     * The script.
     */
    private final Script script;

    /**
     * Constructor.
     */
    protected WrittenLanguage() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param language The language.
     * @param script The script.
     */
    public WrittenLanguage(Language language, Script script) {
        super();
        this.language = language;
        this.script = script;
    }

    /**
     * Get the language.
     *
     * @return The language.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Get the script.
     *
     * @return The script.
     */
    public Script getScript() {
        return script;
    }

    /**
     * Get the {@code Locale} for this written language.
     *
     * @return The {@code Locale} for this written language.
     */
    public Locale getLocale() {
        Locale locale = null;
        if (Script.HANT.equals(getScript())) {
            locale = Locale.TRADITIONAL_CHINESE;
        }
        if (Script.HANS.equals(getScript())) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        if (locale == null) {
            if (getLanguage() != null) {
                locale = Locale.forLanguageTag(getLanguage().getCode());
            }
            if ((locale == null) && (getScript() != null)) {
                locale = new Locale(getScript().getCode());
            }
        }
        return locale;
    }

    /**
     * Get a written language with no script specified.
     *
     * <p> If the script is already {@code null}, a reference to {@code this} object is returned. </p>
     *
     * @return The equivalent written language with no script specified.
     */
    public WrittenLanguage withNoScript() {
        WrittenLanguage noScript = this;
        if (script != null) {
            noScript = new WrittenLanguage(language, null);
        }
        return noScript;
    }

    /**
     * Get a written language with the default script if the script is not specified.
     *
     * <p> If the script is already specified, or there is no default script for the language,
     * a reference to {@code this} object is returned. </p>
     *
     * @return The equivalent written language with the default script.
     */
    public WrittenLanguage withAssumedScript() {
        WrittenLanguage withScript = this;
        if ((language != null) && (script == null)) {
            Script preferredScript = language.getPreferredScript();
            if (preferredScript != null) {
                withScript = new WrittenLanguage(language, preferredScript);
            }
        }
        return withScript;
    }

    /**
     * Get a written language with the default script.
     *
     * <p> If the preferred script is already specified, or there is no default script for the language,
     * a reference to {@code this} object is returned. </p>
     *
     * @return The equivalent written language with the default script.
     */
    public WrittenLanguage withPreferredScript() {
        WrittenLanguage withPreferred = this;
        if (language != null) {
            Script preferredScript = language.getPreferredScript();
            if (!Objects.equals(script, preferredScript)) {
                withPreferred = new WrittenLanguage(language, preferredScript);
            }
        }
        return withPreferred;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(language, script);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (this == obj);
        if ((!equals) && (obj instanceof WrittenLanguage)) {
            WrittenLanguage writtenLanguage = (WrittenLanguage)obj;
            equals = ((Objects.equals(language, writtenLanguage.language)) &&
                    (Objects.equals(script, writtenLanguage.script)));
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' +
                ((language != null) ? language.getCode() : "null") + ':' +
                ((script != null) ? script.getCode() : "null") + ']';
    }
}