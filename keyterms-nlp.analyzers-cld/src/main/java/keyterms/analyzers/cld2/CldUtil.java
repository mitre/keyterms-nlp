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

package keyterms.analyzers.cld2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.collect.Bags;
import keyterms.util.io.Encoding;
import keyterms.util.io.Streams;
import keyterms.util.text.Strings;
import keyterms.util.text.parser.Parsers;
import keyterms.util.text.splitter.LineSplitter;

/**
 * Methods for converting to/from CLD constructs.
 */
final class CldUtil {
    /**
     * The CLD code for UTF-8 encoding.
     */
    static final int UTF8 = 22;

    /**
     * The CLD code for unknown encodings.
     */
    static final int UNKNOWN_ENCODING = 23;

    /**
     * The CLD code for unknown languages.
     */
    static final int UNKNOWN_LANGUAGE = 26;

    /**
     * The code returned for a non-classification.
     */
    static final Set<String> UNKNOWN_LANGS = Bags.staticSet("un", "xx", "xxx");

    /**
     * The code returned for a non-classification.
     */
    static final Set<String> UNKNOWN_SCRIPTS = Bags.staticSet("zyyy");

    /**
     * A map of known CLD language codes.
     */
    private static final Map<String, Integer> CLD_CODES = new HashMap<>();

    /**
     * The pattern used to parse the mapping values taken from generated_languages.cc
     */
    private static final Pattern MAPPING_PATTERN = Pattern.compile(".*\"(.*)\",\\s+//\\s+(\\d+)\\s+(.+)");

    /**
     * The line-splitter for the output of the library interrogation utility.
     */
    private static final LineSplitter LINE_SPLITTER = new LineSplitter();

    // Populate the CLD codes mapping.
    // <p> Created from internal/generated_languages.cc </p>
    static {
        try {
            byte[] mappingBytes = Streams.readFully(CldUtil.class.getResourceAsStream("CldMap.txt"));
            String mappingText = Encoding.decode(mappingBytes, Encoding.UTF8);
            List<String> lines = LINE_SPLITTER.split(mappingText);
            for (String line : lines) {
                Matcher matcher = MAPPING_PATTERN.matcher(line);
                // All lines in this file should match.
                Set<String> skipCodes = Bags.staticSet("xxx", "un");
                if (matcher.matches()) {
                    String isoLike = matcher.group(1);
                    String name = matcher.group(3);
                    int cldCode = Parsers.parseInteger(matcher.group(2));
                    if ((!Strings.isBlank(isoLike)) && (!skipCodes.contains(isoLike))) {
                        isoLike = isoLike.toLowerCase();
                        name = name.toLowerCase();
                        name = name.replaceFirst("^x_", "");
                        CLD_CODES.put(isoLike.toLowerCase(), cldCode);
                        CLD_CODES.put(name.toLowerCase(), cldCode);
                    }
                }
            }
        } catch (Exception error) {
            LoggerFactory.getLogger(CldUtil.class).error("Could not load CLD mappings.", error);
        }
    }

    /**
     * Get the logging topic for this class.
     *
     * @return The logging topic for this class.
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(CldUtil.class);
    }

    /**
     * Get the CLD language code for the specified written language.
     *
     * <p> This is a best effort attempt to map to the CLD codes, since this value is used only as a "classifier hint".
     * It isn't critical to get it correct all the time. </p>
     *
     * @param written The written language.
     *
     * @return The CLD language code for the specified written language.
     */
    static int getCldLanguage(WrittenLanguage written) {
        int cldLanguage = UNKNOWN_LANGUAGE;
        String key = getLookupByCode(written, true);
        if (CLD_CODES.containsKey(key)) {
            cldLanguage = CLD_CODES.get(key);
        }
        if (cldLanguage == UNKNOWN_LANGUAGE) {
            key = getLookupByCode(written, false);
            if (CLD_CODES.containsKey(key)) {
                cldLanguage = CLD_CODES.get(key);
            }
        }
        if (cldLanguage == UNKNOWN_LANGUAGE) {
            key = getLookupByName(written);
            if (CLD_CODES.containsKey(key)) {
                cldLanguage = CLD_CODES.get(key);
            }
        }
        return cldLanguage;
    }

    /**
     * Convert the native classification code to the equivalent {@code WrittenLanguage} form.
     *
     * @param code The native classification code.
     *
     * @return The equivalent {@code WrittenLanguage} form.
     */
    static WrittenLanguage toWrittenLanguage(String code) {
        String langCode = Strings.toLowerCase(code);
        String scriptCode = null;
        int dash = code.indexOf('-');
        if (dash != -1) {
            langCode = code.substring(0, dash);
            scriptCode = code.substring(dash + 1);
        }
        // Adjust for chinese, where the non-identified script is for simplified han
        if (("zh".equals(langCode)) && (scriptCode == null)) {
            scriptCode = "hans";
        }
        Language language = Language.byText(langCode);
        if ((!Strings.isBlank(langCode)) && (!UNKNOWN_LANGS.contains(langCode)) && (language == null)) {
            getLogger().debug("Got native language code {} : langCode = {} : iso = null", code, langCode);
        }
        Script script = Script.byCode(scriptCode);
        if ((!Strings.isBlank(scriptCode)) && (!UNKNOWN_SCRIPTS.contains(scriptCode)) && (script == null)) {
            getLogger().debug("Got native script code {} : scriptCode = {} : script = null", code, scriptCode);
        }
        if ((script == null) && (language != null)) {
            script = language.getPreferredScript();
        }
        return ((language != null) || (script != null)) ? new WrittenLanguage(language, script) : null;
    }

    /**
     * Get the mapping key for the specified parameters.
     *
     * @param written The written language of interest.
     * @param usePart1 A flag indicating whether to use ISO-639-1 language codes if available.
     *
     * @return The lookup key for the code mapping.
     */
    static String getLookupByCode(WrittenLanguage written, boolean usePart1) {
        StringBuilder lookup = new StringBuilder();
        if (written != null) {
            Language language = written.getLanguage();
            if (language != null) {
                if ((usePart1) && (!Strings.isBlank(language.getPart1()))) {
                    lookup.append(language.getPart1().toLowerCase());
                } else {
                    lookup.append(language.getCode().toLowerCase());
                }
            } else {
                lookup.append("xx");
            }
        }
        appendScriptCode(lookup, written);
        return lookup.toString();
    }

    /**
     * Get the mapping key for the specified parameters.
     *
     * @param written The written language of interest.
     *
     * @return The lookup key for the code mapping.
     */
    private static String getLookupByName(WrittenLanguage written) {
        StringBuilder lookup = new StringBuilder();
        if (written != null) {
            Language language = written.getLanguage();
            if (language != null) {
                lookup.append(language.getName().toLowerCase().replaceAll("\\s+", "_"));
            }
        }
        appendScriptCode(lookup, written);
        return lookup.toString();
    }

    /**
     * Append the script portion of the key to the lookup key.
     *
     * @param lookup The current lookup key.
     * @param written The written language of interest.
     */
    private static void appendScriptCode(StringBuilder lookup, WrittenLanguage written) {
        if ((written != null) && (written.getScript() != null)) {
            Language language = written.getLanguage();
            Script preferredScript = language.getPreferredScript();
            Script script = written.getScript();
            if ((preferredScript == null) || (!preferredScript.equals(script))) {
                // CLD only seems to qualify han traditional.
                if (script != Script.HAN_SIMPLIFIED) {
                    lookup.append('-').append(script.getCode().toLowerCase());
                }
            }
        }
    }

    /**
     * Constructor.
     */
    private CldUtil() {
        super();
    }
}