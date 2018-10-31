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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.io.Encoding;
import keyterms.util.io.IO;
import keyterms.util.io.PathFinder;
import keyterms.util.system.ClassLoaders;
import keyterms.util.text.Strings;
import keyterms.util.text.splitter.LineSplitter;

/**
 * A factory which scans for transliterator rule and exception files and creates a registry of custom transliterators.
 */
public class Transliterators {
    /**
     * The pattern used to look for import tags in comment lines.
     */
    private static final Pattern IMPORT_LINE = Pattern.compile("#\\s*<import\\s+file=\"(.*)\"\\s*/>");

    /**
     * A flag indicating whether the built-in ICU rule based transliterators have been loaded.
     */
    private static boolean builtInsLoaded = false;

    /**
     * A flag indicating whether the custom ICU rule based transliterators have been loaded.
     */
    private static boolean customRulesLoaded = false;

    /**
     * A flag indicating whether the custom transliterator classes have been loaded.
     */
    private static boolean customClassesLoaded = false;

    /**
     * A mapping of built-in ICU transliteration schemes.
     */
    static Map<TransformKey, IcuTransliterator> BUILT_IN = new HashMap<>();

    /**
     * A mapping of custom transliterators.
     */
    static Map<TransformKey, Transliterator> CUSTOM = new HashMap<>();

    /**
     * Get the logging topic for this class.
     *
     * @return The logging topic for this class.
     */
    private static Logger getClassLogger() {
        return LoggerFactory.getLogger(Transliterators.class);
    }

    /**
     * Load the built-in transliterators from the ICU package.
     */
    public static synchronized void loadIcuBuiltIns() {
        if (!builtInsLoaded) {
            builtInsLoaded = true;
            Enumeration<String> icuIds = com.ibm.icu.text.Transliterator.getAvailableIDs();
            List<String> idList = Collections.list(icuIds);
            int[] badCounts = new int[2]; // 0 = bad ids, 1 = duplicates
            idList.forEach((id) -> {
                IcuTransliterator transliterator;
                try {
                    transliterator = new IcuTransliterator(com.ibm.icu.text.Transliterator.getInstance(id));
                } catch (Exception error) {
                    badCounts[0]++;
                    getClassLogger().error("Bad identifier {}: {}", id, error.getMessage());
                    return;
                }
                TransformKey key = transliterator.getKey();
                if (BUILT_IN.containsKey(key)) {
                    if (key.getText().length() > BUILT_IN.get(key).getKey().getText().length()) {
                        BUILT_IN.put(key, transliterator);
                        badCounts[1]++;
                    }
                } else {
                    BUILT_IN.put(key, transliterator);
                }
            });
            getClassLogger().info("Loaded {} native ICU transliterators.", idList.size());
            if (badCounts[0] > 0) {
                getClassLogger().info("Discarded {} unrecognized ids.", badCounts[0]);
            }
            if (badCounts[1] > 0) {
                getClassLogger().info("Discarded {} duplicate ids.", badCounts[1]);
            }
        }
    }

    /**
     * Load the custom ICU rule based and exception based transliterators.
     */
    public static synchronized void loadCustomRules() {
        if (!customRulesLoaded) {
            customRulesLoaded = true;
            CustomRuleLoader loader = new CustomRuleLoader();
            try {
                Path basePath = ClassLoaders.getBasePath(Transliterators.class);
                if (IO.isValidDirectory(basePath)) {
                    IO.walk(basePath, loader);
                } else {
                    IO.inArchive(basePath, false, loader);
                }
                getClassLogger().info("Loaded {} custom rule based transliterators.", CUSTOM.size());
                if (loader.ruleErrors > 0) {
                    getClassLogger().info("Skipped {} custom transliterators on error.",
                            loader.ruleErrors);
                }
                if (loader.exceptionsErrors > 0) {
                    getClassLogger().warn("{} faulty transliterators: Bad exception maps.",
                            loader.exceptionsErrors);
                }
            } catch (Exception error) {
                getClassLogger().error("Error loading custom rule based transliterators.", error);
            }
        }
    }

    /**
     * Clean up ICU rule files.
     *
     * <p> This method removes all full line and inline comments which are delimited by the {@code '#'} character. </p>
     *
     * @return The loaded rule file.
     *
     * @throws IOException An {@code IOException} will be thrown if the operation fails.
     */
    static String loadIcuRules(Path ruleFile)
            throws IOException {
        String rawRules = IO.readText(ruleFile, Encoding.UTF8);
        // Clean up the rules.
        StringBuilder clean = new StringBuilder();
        new LineSplitter().split(rawRules).stream()
                .map(Strings::trim)
                .map(l -> Transliterators.loadImports(ruleFile, l))
                .flatMap(Collection::stream)
                .map(l -> l.replaceAll("^#.*", ""))
                .map(l -> l.replaceAll("(.*)[^\\\\]#.*", "$1"))
                .filter(Strings::hasText)
                .forEach(l -> clean.append(l).append('\n'));
        return clean.toString();
    }

    /**
     * Replace the special import markers with the rule contents from another file.
     *
     * @param ruleFile The rule file containing the line.
     * @param line The line which may be an import line.
     *
     * @return The original line or the imported replacement.
     */
    private static List<String> loadImports(Path ruleFile, String line) {
        List<String> imported = Collections.singletonList(line);
        if (line != null) {
            Matcher matcher = IMPORT_LINE.matcher(line);
            if (matcher.matches()) {
                Path importFile = ruleFile.resolveSibling(matcher.group(1));
                try {
                    imported = new LineSplitter().split(loadIcuRules(importFile));
                } catch (Exception error) {
                    getClassLogger().error("Could not load imports: " + ruleFile, error);
                }
            }
        }
        return imported;
    }

    /**
     * Load an exception map from the specified file.
     *
     * <p> This method removes all full line and inline comments which are delimited by the {@code '#'} character. </p>
     *
     * @return The loaded exception map.
     */
    private static Map<String, String> loadExceptions(Path exceptionsFile) {
        String contents = null;
        try {
            contents = IO.readText(exceptionsFile, Encoding.UTF8);
        } catch (IOException error) {
            getClassLogger().error("Could not load exceptions: {}", exceptionsFile, error);
        }
        // Clean up the exceptions.
        Map<String, String> exceptions = new LinkedHashMap<>();
        if (Strings.hasText(contents)) {
            new LineSplitter().split(contents).stream()
                    .map(Strings::trim)
                    .map(l -> l.replaceAll("^#.*", ""))
                    .map(l -> l.replaceAll("(.*)[^\\\\]#.*", "$1"))
                    .filter(Strings::hasText)
                    .forEach(l -> {
                        String in = l;
                        int index = in.indexOf('|');
                        if (index != -1) {
                            String out = l.substring(index + 1);
                            in = l.substring(0, index);
                            exceptions.put(in, out);
                        } else {
                            getClassLogger().warn("Invalid exception {} in {}.", l, exceptionsFile);
                        }
                    });
        }
        return exceptions;
    }

    /**
     * Load custom transliterator classes.
     */
    public static synchronized void loadCustomTransliterators() {
        if (!customClassesLoaded) {
            customClassesLoaded = true;
            int initialCount = CUSTOM.size();
            try {
                ServiceLoader<Transliterator> serviceLoader = ServiceLoader.load(Transliterator.class);
                for (Transliterator transliterator : serviceLoader) {
                    CUSTOM.put(transliterator.getKey(), transliterator);
                }
            } catch (Exception error) {
                getClassLogger().error("Error loading custom transliterators.", error);
            }
            getClassLogger().info("Loaded {} custom transliterators.", CUSTOM.size() - initialCount);
        }
    }

    /**
     * Get the available transliteration keys.
     *
     * @return The available transliteration keys.
     */
    public static Set<TransformKey> getTransformKeys() {
        loadIcuBuiltIns();
        loadCustomRules();
        loadCustomTransliterators();

        Set<TransformKey> keys = new HashSet<>();
        keys.addAll(BUILT_IN.keySet());
        keys.addAll(CUSTOM.keySet());
        return keys;
    }

    /**
     * Get the available transformation keys that may be applicable to the specified source and target.
     *
     * @param source A description of the source text.
     * @param target A description of the target text.
     * @param scheme A description of the target scheme.
     *
     * @return The available transformation keys that may be applicable to the specified source and target.
     */
    public static Set<TransformKey> getTransformKeys(CharSequence source, CharSequence target, CharSequence scheme) {
        return getTransformKeys().stream()
                .filter(k -> k.getSource().test(source))
                .filter(k -> k.getTarget().test(target))
                .filter(k -> k.getScheme().test(scheme))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Get the transliterator with the specified key.
     *
     * @param key The transform key.
     *
     * @return The transliterator with the specified key.
     */
    public static Transliterator get(TransformKey key) {
        loadIcuBuiltIns();
        loadCustomRules();
        loadCustomTransliterators();

        Transliterator transliterator = CUSTOM.get(key);
        if (transliterator == null) {
            transliterator = BUILT_IN.get(key);
        }
        return transliterator;
    }

    /**
     * Constructor.
     */
    private Transliterators() {
        super();
    }

    /**
     * Custom ICU rule based transliterator creation.
     */
    private static class CustomRuleLoader
            extends SimpleFileVisitor<Path>
            implements IO.ArchiveAction {
        /**
         * The number of errors creating a rule based transliterator from a rule file.
         */
        private int ruleErrors;

        /**
         * The number of errors creating an exception based transliterator from a rule based transliterator.
         */
        private int exceptionsErrors;

        /**
         * Constructor.
         */
        CustomRuleLoader() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
            String fileName = IO.getName(file);
            if (fileName.matches("(?i:.*\\.rules)")) {
                try {
                    String key = fileName
                            .replaceFirst("(?i:\\.rules)$", "")
                            .replaceFirst("\\.", "/");
                    getClassLogger().debug("Loading rules from {} -> {}.", file, key);
                    String rules = loadIcuRules(file);
                    Transliterator transliterator = new IcuTransliterator(key, rules);
                    PathFinder exceptionsFinder = new PathFinder((p) ->
                            IO.getName(p).equalsIgnoreCase(key + ".exceptions"));
                    Files.walkFileTree(file.getParent(), exceptionsFinder);
                    Path exceptionsFile = exceptionsFinder.getPath();
                    if (exceptionsFile != null) {
                        try {
                            transliterator = new ExceptionTransliterator(key, transliterator);
                            ((ExceptionTransliterator)transliterator).addAll(loadExceptions(exceptionsFile));
                        } catch (Exception error) {
                            getClassLogger().error("Could not load exception list from: {}", exceptionsFile, error);
                            exceptionsErrors++;
                        }
                    }
                    CUSTOM.put(transliterator.getKey(), transliterator);
                } catch (Exception error) {
                    getClassLogger().error("Could not load rules from: {}", fileName, error);
                    ruleErrors++;
                }
            }
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void withRoot(Path archiveRoot) {
            try {
                Files.walkFileTree(archiveRoot, this);
            } catch (Exception error) {
                getClassLogger().error("Could not load custom rules from archive: {}", archiveRoot, error);
            }
        }
    }
}