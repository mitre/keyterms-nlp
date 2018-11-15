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

package keyterms.analyzer.profiles;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.analyzer.Analyzer;
import keyterms.analyzer.AnalyzerFactory;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.util.io.IO;
import keyterms.util.io.PathFinder;
import keyterms.util.io.Serialization;
import keyterms.util.lang.Lazy;

/**
 * A shared collection of weka based analyzer profiles.
 */
public class WekaProfiles {
    /**
     * The lazily instantiated singleton instance.
     */
    private static final Lazy<WekaProfiles> INSTANCE = new Lazy<>(WekaProfiles::new);

    /**
     * Get the singleton instance of this class.
     *
     * @return The singleton instance of this class.
     */
    public static WekaProfiles getInstance() {
        return INSTANCE.value();
    }

    /**
     * Get the singleton instance of this class forcing a scan for profiles in the specified paths.
     *
     * @param scanPaths The paths to search for weka profile archives.
     *
     * @return The singleton instance of this class.
     */
    public static WekaProfiles getInstance(Object... scanPaths) {
        if (scanPaths != null) {
            for (Object scanPath : scanPaths) {
                getInstance().scanDirectory(scanPath);
            }
        }
        return getInstance();
    }

    /**
     * The analyzer thread pools keyed by the factory identifier.
     */
    private final Map<WekaProfile, WekaPool> analyzerPools = new HashMap<>();

    /**
     * Constructor.
     */
    private WekaProfiles() {
        super();
        for (String path : System.getProperty("java.class.path").split(File.pathSeparator)) {
            scanDirectory(path);
        }
        scanDirectory("lib");
        if (analyzerPools.isEmpty()) {
            getLogger().warn("No weka profiles loaded.");
        }
    }

    /**
     * Scan the specified directory for weka profile archives.
     *
     * @param profilePath The path which potentially contains weka profile archives.
     */
    private void scanDirectory(Object profilePath) {
        try {
            getLogger().debug("Scanning profile path: {}", profilePath);
            IO.walk(profilePath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (IO.getName(file).toLowerCase().endsWith(".jar")) {
                        try {
                            IO.inArchive(file, (root) -> {
                                PathFinder pathFinder = new PathFinder((p) ->
                                        WekaProfile.class.getName().equals(IO.getName(p)));
                                try {
                                    IO.walk(root, pathFinder);
                                } catch (Exception error) {
                                    getLogger().error("Could not interrogate profile jar: {}", file, error);
                                }
                                Path profileInfo = pathFinder.getPath();
                                if (profileInfo != null) {
                                    try {
                                        byte[] profileBytes = IO.readBytes(profileInfo);
                                        WekaProfile profile = Serialization.fromBytes(WekaProfile.class, profileBytes);
                                        if (!analyzerPools.containsKey(profile)) {
                                            profile.getRequiredAnalyzers().forEach((analyzerId) -> {
                                                if (CoreAnalyzers.getInstance().get(analyzerId) == null) {
                                                    throw new IllegalStateException(
                                                            "Missing core analyzer: " + analyzerId);
                                                }
                                            });
                                            Path analyzerFile = profileInfo.getParent().resolve("profile_analyzer.ser");
                                            String profileId = "profile." + profile.getName();
                                            analyzerPools.put(profile, new WekaPool(new AnalyzerFactory(
                                                    new AnalyzerId(profileId),
                                                    WekaAnalyzer.INPUT_CLASSES,
                                                    WekaAnalyzer.OUTPUT_FEATURES,
                                                    WekaAnalyzer.PRODUCES_RANKINGS,
                                                    WekaAnalyzer.PRODUCES_SCORES,
                                                    () -> {
                                                        try {
                                                            return Serialization.fromBytes(WekaAnalyzer.class,
                                                                    IO.readBytes(analyzerFile));
                                                        } catch (Exception error) {
                                                            throw new IllegalStateException(
                                                                    "Could not instantiate profile analyzer.", error);
                                                        }
                                                    })));
                                            getLogger().info("Loaded profile analyzer: {}", profileId);
                                        }
                                    } catch (Exception error) {
                                        getLogger().error("Could not load profile from: {}", profileInfo, error);
                                    }
                                }
                            });
                        } catch (Exception error) {
                            getLogger().error("Could not interrogate profile jar: {}", file, error);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception error) {
            getLogger().error("Could not scan profile directory: {}", profilePath);
        }
    }

    /**
     * Get the logging topic for this object.
     *
     * @return The logging topic for this object.
     */
    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Get the number of analyzer thread pools that have been loaded.
     *
     * @return The number of analyzer thread pools that have been loaded.
     */
    public int size() {
        return analyzerPools.size();
    }

    /**
     * Get the identifiers for the analyzers that have been loaded.
     *
     * @return The identifiers for the analyzers that have been loaded.
     */
    public Set<WekaProfile> profiles() {
        return analyzerPools.keySet();
    }

    /**
     * Get the analyzer of the specified name.
     *
     * @param name The analyzer name.
     *
     * @return The specified analyzer.
     */
    public Analyzer get(String name) {
        Analyzer analyzer = null;
        for (Map.Entry<WekaProfile, WekaPool> entry : analyzerPools.entrySet()) {
            if (entry.getKey().getName().equals(name)) {
                analyzer = entry.getValue();
            }
        }
        return analyzer;
    }

    /**
     * Dispose of the analyzer pool resources.
     */
    public void dispose() {
        analyzerPools.values().forEach(Analyzer::dispose);
    }
}