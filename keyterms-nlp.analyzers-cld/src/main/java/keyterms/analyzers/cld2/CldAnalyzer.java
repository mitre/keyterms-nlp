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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.text.TextInfo;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.collect.Bags;
import keyterms.util.io.Encoding;
import keyterms.util.io.IO;
import keyterms.util.text.Strings;

/**
 * A text analyzer which uses the OpenCLD 2 classification engine.
 *
 * <p> This class requires an installation of OpenCLD 2. </p>
 * <p> The installation directory is located by searching: </p>
 * <ul>
 * <li> The {@code PATH} environment variable. </li>
 * <li> The {@code LD_LIBRARY_PATH} environment variable. </li>
 * <li> A "{@code CLD_HOME}" or "{@code CLD.HOME}" environment variable or system property. </li>
 * <li> A "{@code CLD2_HOME}" or "{@code CLD2.HOME}" environment variable or system property. </li>
 * <li> The system {@code classpath}. </li>
 * </ul>
 */
public class CldAnalyzer
        extends Analyzer {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -9154858696580766215L;

    /**
     * The types of input accepted by the analyzer.
     */
    static final Set<Class<?>> INPUT_CLASSES = Bags.staticSet(
            CharSequence.class
    );

    /**
     * The analysis features that the analyzer can produce.
     */
    static final Set<AnalysisFeature<?>> OUTPUT_FEATURES = Bags.staticSet(
            TextInfo.LANGUAGE, TextInfo.SCRIPT
    );

    /**
     * A flag indicating whether the analyzer produces multiple analyses.
     */
    static final boolean PRODUCES_RANKINGS = true;

    /**
     * A flag indicating whether the analyzer produces meaningful scores.
     */
    static final boolean PRODUCES_SCORES = true;

    /**
     * Keys for the methods used by this library.
     */
    private enum Method {
        VERSION,
        FROM_NAME,
        LANG_CODE,
        DETECT,
        EXT_DETECT
    }

    /**
     * The base library name for the basic cld2 library.
     */
    private static final String BASIC_LIB_NAME = "libcld2";

    /**
     * The base library name for the basic cld2_full library.
     */
    private static final String FULL_LIB_NAME = "libcld2_full";

    /**
     * A flag indicating whether plain text is being classified.
     */
    private static final boolean IS_PLAIN_TEXT = false;

    /**
     * The flags passed into the CLD2 detection method.
     *
     * <p> This flag indicates to give a best effort answer even on short texts. </p>
     */
    private static final int FLAGS = 0x4000;

    /**
     * This parameter is not passed into the CLD2 library.
     */
    private static final Object RESULT_CHUNK_VECTOR = null;

    /**
     * The environment variables checked for CLD installation paths.
     */
    private static final Set<String> ENV_KEYS = Bags.staticSet(
            "path", "ld_library_path", "cld_home", "cld.home", "cld2_home", "cld2.home");

    /**
     * Locate the CLD installation directory.
     *
     * @return The path to the CLD installation directory.
     */
    private static Path locateLibraryPath() {
        Path libraryPath = null;
        List<String> paths = new ArrayList<>();
        System.getenv().forEach((k, v) -> {
            if (ENV_KEYS.contains(Strings.toLowerCase(Strings.trim(k)))) {
                paths.add(Strings.trim(v));
            }
        });
        System.getProperties().forEach((k, v) -> {
            if (ENV_KEYS.contains(Strings.toLowerCase(Strings.trim(Strings.toString(k))))) {
                paths.add(Strings.trim(Strings.toString(v)));
            }
        });
        paths.add(System.getProperty("java.class.path"));
        for (String p : paths) {
            if (libraryPath == null) {
                libraryPath = locateLibraryPath(p);
            }
        }
        return libraryPath;
    }

    /**
     * Locate the CLD installation directory on the given path.
     *
     * @param pathText The textual representation of the path.
     *
     * @return The path to the CLD installation directory.
     */
    private static Path locateLibraryPath(String pathText) {
        Path installDir = null;
        if (!Strings.isBlank(pathText)) {
            for (String p : pathText.split(File.pathSeparator)) {
                if (installDir == null) {
                    Path path = IO.normalize(p);
                    if (IO.isValidDirectory(path)) {
                        Path lib = getMainLibrary(path);
                        if (lib == null) {
                            lib = getMainLibrary(path.resolve("internal"));
                        }
                        if (lib != null) {
                            installDir = lib.getParent();
                        }
                    }
                }
            }
        }
        return installDir;
    }

    /**
     * Get the path to the main shared object (dynamic link) library file.
     *
     * @param path The path which may contain the native library.
     *
     * @return The path to the native library.
     */
    private static Path getMainLibrary(Path path) {
        Path lib = path.resolve("libcld2.so");
        if (!IO.isValidFile(lib)) {
            lib = path.resolve("libcld2.dll");
            if (!IO.isValidFile(lib)) {
                lib = null;
            }
        }
        return lib;
    }

    /**
     * The native library.
     */
    private NativeLibrary nativeLibrary;

    /**
     * The library method descriptions used to find the correct library names in the native library.
     */
    private final Map<Method, LibMethod> libMethods = new HashMap<>();

    /**
     * The map of library methods to their respective functions in the native library.
     */
    private final Map<Method, Function> nativeFunctions = new HashMap<>();

    /**
     * A flag indicating whether to use the extended language tables in CLD.
     */
    private final boolean useExtended;

    /**
     * Constructor.
     */
    public CldAnalyzer() {
        this(false, false);
    }

    /**
     * Constructor.
     *
     * @param full A flag indicating whether to use the "full" CLD2 library.
     * @param extended A flag indicating whether to use the extended language tables in CLD.
     */
    public CldAnalyzer(boolean full, boolean extended) {
        this((full) ? FULL_LIB_NAME : BASIC_LIB_NAME, extended);
    }

    /**
     * Constructor.
     *
     * @param libName The base library name (no extension).
     * @param extended A flag indicating whether to use the extended language tables in CLD.
     */
    private CldAnalyzer(String libName, boolean extended) {
        super(INPUT_CLASSES, OUTPUT_FEATURES, PRODUCES_RANKINGS, PRODUCES_SCORES);
        this.useExtended = extended;
        libMethods.put(Method.VERSION, new LibMethod("DetectLanguageVersion"));
        libMethods.put(Method.FROM_NAME, new LibMethod("GetLanguageFromName"));
        libMethods.put(Method.LANG_CODE, new LibMethod("ExtLanguageCode"));
        libMethods.put(Method.DETECT, new LibMethod("DetectLanguageSummaryV2")
                .exclude("ExtDetect").exclude("CheckUTF8")
                .require("ResultChunk"));
        libMethods.put(Method.EXT_DETECT, new LibMethod("ExtDetectLanguageSummary")
                .exclude("CheckUTF8").require("ResultChunk"));
        try {
            Path libPath = locateLibraryPath();
            if (!IO.isValidDirectory(libPath)) {
                throw new FileNotFoundException("Could not find CLD2 installation.");
            }
            NativeLibs.loadLibrary(libPath.normalize().toString(), libName, (nativeLib, methodName) ->
                    libMethods.forEach((key, value) -> {
                        if (value.test(methodName)) {
                            nativeLibrary = (nativeLibrary != null) ? nativeLibrary : nativeLib;
                            nativeFunctions.put(key, nativeLib.getFunction(methodName));
                        }
                    }));
            for (Method method : Method.values()) {
                if (nativeFunctions.get(method) == null) {
                    throw new IOException("No function registered for: " + method);
                }
                getLogger().trace("Mapped {} to {}", method.name(), nativeFunctions.get(method));
            }
        } catch (Throwable error) {
            throw new IllegalStateException("Error instantiating CLD analyzer.", error);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _analyze(Object input, Consumer<Analysis> collector) {
        String text = Strings.toString(input);
        if (!Strings.isBlank(text)) {
            byte[] bytes = Encoding.encode(text, Encoding.UTF8);
            CldHints hints = new CldHints(null, "", CldUtil.UTF8, CldUtil.UNKNOWN_LANGUAGE);
            int[] language3 = new int[3];
            int[] percent3 = new int[3];
            double[] normalizedScore3 = new double[3];
            int[] textBytes = new int[1];
            boolean[] isReliable = new boolean[1];
            if (useExtended) {
                nativeFunctions.get(Method.EXT_DETECT).invokeInt(new Object[] {
                        bytes, bytes.length,
                        IS_PLAIN_TEXT, hints, FLAGS,
                        language3, percent3, normalizedScore3,
                        RESULT_CHUNK_VECTOR, textBytes, isReliable
                });
            } else {
                nativeFunctions.get(Method.DETECT).invokeInt(new Object[] {
                        bytes, bytes.length,
                        IS_PLAIN_TEXT, hints, false, FLAGS, CldUtil.UNKNOWN_LANGUAGE,
                        language3, percent3, normalizedScore3,
                        RESULT_CHUNK_VECTOR, textBytes, isReliable
                });
            }
            for (int l = 0; l < 3; l++) {
                String rawCode = Strings.toLowerCase(getLanguageCode(language3[l]));
                if (!CldUtil.UNKNOWN_LANGS.contains(rawCode)) {
                    WrittenLanguage written = CldUtil.toWrittenLanguage(rawCode);
                    if (written != null) {
                        TextInfo textInfo = new TextInfo();
                        textInfo.setLanguage(written.getLanguage());
                        textInfo.setScript(written.getScript());
                        textInfo.setScore(percent3[l] / 100.0);
                        collector.accept(textInfo);
                    }
                }
            }
        }
    }

    /**
     * Get the version of the CLD2 library that is being used.
     *
     * @return The version of the CLD2 library.
     */
    private String getVersion() {
        return nativeFunctions.get(Method.VERSION).invokeString(new Object[0], false);
    }

    /**
     * Get the native library code for the language given the language name.
     *
     * @param name The language name.
     *
     * @return The native library code for the language.
     */
    private int getLangaugeFromName(String name) {
        return nativeFunctions.get(Method.FROM_NAME).invokeInt(new Object[] { name });
    }

    /**
     * Get the ISO-639-1 code for the language given the native code for the language.
     *
     * @param language The native library language code.
     *
     * @return The ISO-639-1 code for the language.
     */
    private String getLanguageCode(int language) {
        return nativeFunctions.get(Method.LANG_CODE).invokeString(new Object[] { language }, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void _dispose() {
        nativeLibrary.dispose();
        nativeLibrary = null;
    }
}