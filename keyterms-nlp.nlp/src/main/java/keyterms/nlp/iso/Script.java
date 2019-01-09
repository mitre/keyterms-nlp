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

import keyterms.util.collect.Bags;
import keyterms.util.io.Encoding;
import keyterms.util.lang.Enums;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.Parsers;
import keyterms.util.text.parser.SimpleDefinitions;

/**
 * A pool of the ISO-15924 script codes.
 *
 * <p> Derived from <a href="http://unicode.org/Script/index.html">unicode.org</a>. </p>
 */
public final class Script
        implements StdDef, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -8838326777197234653L;

    /**
     * The list of values.
     */
    private static final List<Script> VALUES;

    /**
     * Values by numeric code.
     */
    private static final Map<Integer, Script> BY_NUMBER = new HashMap<>();

    /**
     * Values by code.
     */
    private static final Map<String, Script> BY_CODE = new HashMap<>();

    /**
     * Values by name.
     */
    private static final Map<String, Set<Script>> BY_NAME = new HashMap<>();

    /**
     * Values by unicode equivalent.
     */
    private static final Map<Character.UnicodeScript, Script> BY_UNICODE = new HashMap<>();

    // Initialize the value space.
    static {
        List<Script> values = new ArrayList<>();
        try {
            InputStream stream = Script.class.getResourceAsStream("rsc/scripts.lst");
            String scriptsList = Encoding.decode(stream.readAllBytes(), Encoding.UTF8);
            SimpleDefinitions parser = new SimpleDefinitions(scriptsList);
            while (parser.hasMore()) {
                Integer number = Parsers.INTEGERS.parse(parser.getField("number"), null);
                if (number == null) {
                    throw new IllegalArgumentException("Invalid script number at line #" + parser.getLineNumber());
                }
                String code = parser.getField("code");
                List<String> altCodes = parser.getList("altCodes");
                String name = parser.getField("name");
                String javaName = parser.getField("javaName");
                List<String> aliases = parser.getList("aliases");
                Script script = new Script(number, code, altCodes, name, javaName, aliases);
                values.add(script);
                BY_NUMBER.put(number, script);
                Standards.putCode(Script.class, BY_CODE, code, script);
                altCodes.forEach((alt) -> Standards.putCode(Script.class, BY_CODE, alt, script));
                Standards.putName(Script.class, BY_NAME, name, script);
                aliases.forEach((alias) -> Standards.putName(Script.class, BY_NAME, alias, script));
                if (Strings.hasText(javaName)) {
                    Character.UnicodeScript unicodeScript = Enums.find(Character.UnicodeScript.class, javaName);
                    if (unicodeScript != null) {
                        BY_UNICODE.put(unicodeScript, script);
                    }
                }
            }
        } catch (Exception error) {
            LoggerFactory.getLogger(Script.class).error("Could not load script definitions.", error);
        }
        VALUES = Collections.unmodifiableList(values);
    }

    /**
     * Convenience constants.
     */
    public static final Script LATN = byCode("latn");

    public static final Script ARAB = byCode("arab");
    public static final Script CYRL = byCode("cyrl");

    public static final Script HANI = byCode("hani");
    public static final Script HANS = byCode("hans");
    public static final Script HANT = byCode("hant");
    public static final Script HANG = byCode("hang");
    public static final Script HIRA = byCode("hira");
    public static final Script KANA = byCode("kana");

    /**
     * Convenience constants.
     */
    public static final Script ARABIC = byCode("arab");
    public static final Script CYRILLIC = byCode("cyrl");
    public static final Script GREEK = byCode("grek");
    public static final Script HAN_SIMPLIFIED = byCode("hans");
    public static final Script HAN_TRADITIONAL = byCode("hant");
    public static final Script JAPANESE = byCode("jpan");
    public static final Script KOREAN = byCode("kore");
    public static final Script LATIN = byCode("latn");

    /**
     * CJK scripts.
     */
    public static final Set<Script> CJK_SCRIPTS = Bags.staticSet(
            Script.HANI, Script.HANT, Script.HANS,
            Script.HANG, Script.HIRA, Script.KANA
    );

    /**
     * A script that represents "common" characters that are not associated with a specific script.
     */
    public static final Script ZYYY = byCode("zyyy");

    /**
     * A script that represents "common" characters that are not associated with a specific script.
     */
    public static final Script COMMON = byCode("zyyy");

    /**
     * A script that represents "unknown" or "non-coded" characters.
     */
    public static final Script ZZZZ = byCode("zzzz");

    /**
     * A script that represents "unknown" or "non-coded" characters.
     */
    public static final Script UNKNOWN = byCode("zzzz");

    /**
     * Get the script values.
     *
     * @return The script values.
     */
    public static List<Script> values() {
        return VALUES;
    }

    /**
     * Get the script given its numeric code.
     *
     * @param number The numeric script code.
     *
     * @return The specified script.
     */
    public static Script byNumber(int number) {
        return BY_NUMBER.get(number);
    }

    /**
     * Get the script given its text code.
     *
     * @param code The text code.
     *
     * @return The specified script.
     */
    public static Script byCode(CharSequence code) {
        return BY_CODE.get(Standards.key(code));
    }

    /**
     * Get the script given its textual name or alias.
     *
     * @param name The script name.
     *
     * @return The specified script.
     */
    public static Script byName(CharSequence name) {
        Script script = null;
        Set<Script> byName = BY_NAME.get(Standards.key(name));
        if ((byName != null) && (byName.size() == 1)) {
            script = byName.stream().findFirst().orElse(null);
        }
        return script;
    }

    /**
     * Get the script for the specified Java representation.
     *
     * @param unicodeScript The Java representation of the script.
     *
     * @return The specified script.
     */
    public static Script valueOf(Character.UnicodeScript unicodeScript) {
        return BY_UNICODE.get(unicodeScript);
    }

    /**
     * Get the script given a textual representation.
     *
     * <p> This method prefers codes over names. </p>
     *
     * @param text The textual representation of the value.
     *
     * @return The specified value.
     */
    public static Script byText(CharSequence text) {
        Script value = byCode(text);
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
     * Get all matching script definitions given a textual representation.
     *
     * @param text The textual representation of the value.
     *
     * @return The specified values.
     */
    public static Set<Script> find(CharSequence text) {
        Set<Script> scripts = new LinkedHashSet<>();
        String key = Standards.key(text);
        Script byNumber = BY_NUMBER.get(Parsers.INTEGERS.parse(key, null));
        if (byNumber != null) {
            scripts.add(byNumber);
        }
        Script byCode = BY_CODE.get(key);
        if (byCode != null) {
            scripts.add(byCode);
        }
        Set<Script> byName = BY_NAME.get(key);
        if (byName != null) {
            scripts.addAll(byName);
        }
        return scripts;
    }

    /**
     * The numeric code for the script.
     */
    private final int number;

    /**
     * The four letter code for the script.
     */
    private final String code;

    /**
     * Known alternate codes for the script.
     */
    private final Set<String> altCodes;

    /**
     * The script name.
     */
    private final String name;

    /**
     * The Java representation of the script.
     */
    private final String javaName;

    /**
     * Alternate names for the script.
     */
    private final Set<String> aliases;

    /**
     * Constructor.
     *
     * @param number The numeric code.
     * @param code The primary code.
     * @param altCodes The alternate codes.
     * @param name The name.
     * @param javaName The Java representation of the name.
     * @param aliases The alternate names.
     */
    Script(int number, String code, List<String> altCodes, String name, String javaName, List<String> aliases) {
        this.number = number;
        this.code = code;
        this.altCodes = Collections.unmodifiableSet(new HashSet<>(altCodes));
        this.name = name;
        this.javaName = javaName;
        this.aliases = Collections.unmodifiableSet(new HashSet<>(aliases));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Get the numeric script code.
     *
     * @return The numeric script code.
     */
    public int getNumber() {
        return number;
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
     * Get the Java representation of the script.
     *
     * @return The Java representation of the script.
     */
    public Character.UnicodeScript getUnicodeScript() {
        Character.UnicodeScript javaScript = null;
        if (Strings.hasText(javaName)) {
            javaScript = Enums.find(Character.UnicodeScript.class, javaName);
        }
        return javaScript;
    }

    /**
     * Get the known aliases for the script.
     *
     * @return The known aliases for the script.
     */
    public Set<String> getAliases() {
        return aliases;
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
        return ((this == obj) || ((obj instanceof Script) && (Objects.equals(code, ((Script)obj).code))));
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
        Script value = BY_CODE.get(Standards.key(code));
        if (value == null) {
            throw new InvalidObjectException("Unknown script: " + this);
        }
        return value;
    }
}