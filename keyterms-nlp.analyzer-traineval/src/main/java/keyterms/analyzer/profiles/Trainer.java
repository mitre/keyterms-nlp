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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.analyzer.profiles.model.FeatureModel;
import keyterms.analyzer.text.TextInfo;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.util.Errors;
import keyterms.util.config.Args;
import keyterms.util.io.Encoding;
import keyterms.util.io.IO;
import keyterms.util.io.Serialization;
import keyterms.util.text.Strings;

import weka.core.converters.ArffSaver;

/**
 * A utility for building trained WekaAnalyzer "profiles".
 *
 * <p> A "profile" is a named analyzer with a required set of core analyzers. </p>
 * <p> Profile metadata information files will contain information related to the training cycle. </p>
 *
 * <p>Usage example:
 * <p> Prior to running: </p>
 * <ul>
 * <li> set BASIS_HOME </li>
 * <li> set CLD_HOME=/var/lib/cld2 </li>
 * </ul>
 * <ul>
 * <li>
 * <p> For training on a box without the products which require an installation: </p>
 * java nlptk.analyzer.profiles.Trainer name=udhr_noinstall train=.local/data/udhr_train.idx
 * </li>
 * <li>
 * <p> For training with all available analyzers. </p>
 * java nlptk.analyzer.profiles.Trainer name=udhr_full train=.local/data/udhr_train.idx
 * </li>
 * <li>
 * <p> For training with only the specified four open source analyzers: </p>
 * java nlptk.analyzer.profiles.Trainer name=udhr_open train=.local/data/udhr_train.idx require=CLD require=ICU
 * require=MOZ require=OPT arff=true
 * </li>
 * <li>
 * <p> With testing: </p>
 * java nlptk.analyzer.profiles.Trainer name=udhr train=.local/data/udhr_train.idx test=.local/data/udhr_test.idx
 * </li>
 * </ul>
 */
public class Trainer {
    /**
     * Application error conditions which cause early termination.
     */
    enum ExitCode {
        FATAL_ERROR,
        NO_PROFILE,
        NO_TRAINING,
        NO_ANALYZERS,
        MISSING_ANALYZER;

        /**
         * Get the equivalent system exit code.
         *
         * @return The equivalent system exit code.
         */
        private int getExitCode() {
            return -(ordinal() + 1);
        }
    }

    /**
     * The input parameter containing the new profile name.
     */
    static final String PROFILE_NAME = "name";

    /**
     * The input parameter containing the required analyzer identifiers.
     */
    static final String REQUIRED_ID = "require";

    /**
     * The input parameter containing the path to the training index file.
     */
    private static final String TRAIN_FILE = "train";

    /**
     * The input parameter containing the path to the testing index file.
     */
    private static final String TEST_FILE = "test";

    /**
     * The input parameter which determines whether ARFF files are included with the profile artifact.
     */
    static final String ARFF = "arff";

    /**
     * The input parameter which determines whether raw evaluation data is included in the report.
     */
    static final String RAW = "raw";

    static final Gson GSON = new GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd HH:mm:ss zzz")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    /**
     * Command line entry point.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            Args clArgs = new Args(args);
            String name = Strings.toLowerCase(Strings.trim(clArgs.getText(PROFILE_NAME).orElse(null)));
            if (Strings.isBlank(name)) {
                exit("No profile name specified.", ExitCode.NO_PROFILE);
            }
            Set<AnalyzerId> required = clArgs.getTextValues(REQUIRED_ID).stream()
                    .filter((s) -> !Strings.isBlank(s))
                    .map(AnalyzerId::valueOf)
                    .collect(Collectors.toSet());
            if (required.isEmpty()) {
                getLogger().warn("No required core analyzers specified.");
                getLogger().warn("Using available core analyzers.");
            }
            Path inputFile = IO.normalize(clArgs.getText(TRAIN_FILE).orElse(null));
            if (!IO.exists(inputFile)) {
                exit("Could not find training input file: " + inputFile, ExitCode.NO_TRAINING);
            }
            Path testFile = null;
            String testFileName = clArgs.getText(TEST_FILE).orElse(null);
            if (!Strings.isBlank(testFileName)) {
                testFile = IO.normalize(testFileName);
                if (!IO.exists(testFile)) {
                    getLogger().warn("Could not find testing input file: {}", inputFile);
                    testFile = null;
                }
            }
            getLogger().info("Initializing core analyzers.");
            CoreAnalyzers.getInstance();
            Set<AnalyzerId> available = CoreAnalyzers.getInstance().ids();
            if (available.isEmpty()) {
                exit("No core analyzers are available.", ExitCode.NO_ANALYZERS);
            }
            if (required.isEmpty()) {
                required = available;
            } else {
                required.forEach((id) -> {
                    if (!available.contains(id)) {
                        exit("Required core analyzer not available: " + id, ExitCode.MISSING_ANALYZER);
                    }
                });
            }
            boolean arff = clArgs.getBoolean(ARFF).orElse(false);
            Trainer trainer = new Trainer(name, required, inputFile, arff);
            WekaAnalyzer analyzer = trainer.run();
            if (testFile != null) {
                boolean outputRaw = clArgs.getBoolean(RAW).orElse(false);
                Tester tester = new Tester(name, analyzer, testFile, outputRaw);
                tester.run();
            }
        } catch (Exception error) {
            exit("Fatal error: " + Errors.getSimpleErrorMessage(error) +
                            "\n" + Errors.stackTraceOf(error),
                    ExitCode.FATAL_ERROR);
        }
    }

    /**
     * Get the logging topic for this class.
     *
     * @return The logging topic for this class.
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(Trainer.class.getSimpleName());
    }

    /**
     * Output the specified error message, print usage and exit.
     *
     * @param message The error message.
     * @param reason The reason for exiting.
     */
    static void exit(String message, ExitCode reason) {
        getLogger().error("Exiting on error [{}]: {}", reason.getExitCode(), message);
        System.exit(reason.getExitCode());
    }

    /**
     * The name of the profile being trained.
     */
    private final String name;

    /**
     * The set of identifiers for required analyzers.
     */
    private final Set<AnalyzerId> required;

    /**
     * The path to the CSV input file which details the training data.
     */
    private final Path inputFile;

    /**
     * A flag indicating whether to output ARFF files with the artifact.
     */
    private final boolean arff;

    /**
     * The input descriptions of the training records.
     */
    private List<InputRecord> inputRecords;

    /**
     * The trained encoding analyzer.
     */
    private WekaForest<String> encodingAnalyzer;

    /**
     * The trained language analyzer.
     */
    private WekaForest<Language> languageAnalyzer;

    /**
     * The trained encoding analyzer.
     */
    private WekaForest<Script> scriptAnalyzer;

    /**
     * Constructor.
     *
     * @param name The name of the profile being trained.
     * @param required The set of identifiers for required analyzers.
     * @param inputFile The path to the CSV input file which details the training data.
     * @param arff A flag indicating whether to output ARFF files with the artifact.
     */
    Trainer(String name, Set<AnalyzerId> required, Path inputFile, boolean arff) {
        super();
        this.name = name;
        this.required = required;
        this.inputFile = inputFile;
        this.arff = arff;
    }

    /**
     * Load the training records.
     *
     * @return The training records.
     */
    List<InputRecord> loadTrainingRecords()
            throws Exception {
        return InputParser.loadInputRecords(inputFile);
    }

    /**
     * Run the trainer.
     *
     * @return The trained analyzer.
     */
    WekaAnalyzer run()
            throws Exception {
        inputRecords = loadTrainingRecords();
        trainEncodingAnalyzer();
        trainLanguageAnalyzer();
        trainScriptAnalyzer();
        return createArtifact();
    }

    /**
     * Train the encoding model for the text analyzer.
     */
    private void trainEncodingAnalyzer()
            throws Exception {
        getLogger().info("Creating training data for encoding model.");
        FeatureModel<String> featureModel = TextModels.getEncodingModel(required);
        WekaForestBuilder<String> trainer = new WekaForestBuilder<>(featureModel, TextInfo.ENCODING);
        AtomicLong completionCount = new AtomicLong();
        inputRecords.forEach((record) -> {
            Datum<String> datum = new Datum<>(featureModel.getOutputFeature(), record.encoding);
            Map<Object, Map<AnalyzerId, List<Analysis>>> analyzerResults = new HashMap<>();
            analyzerResults.put(record.data, CoreAnalyzers.getInstance().run(record.data,
                    required::contains, (analyzer) -> analyzer.produces(TextInfo.ENCODING)));
            TextModels.fillFeatures(record.getTextInfo(), datum.getFeatureData(), featureModel,
                    TextModels.ENCODING_PREFIX, analyzerResults);
            trainer.addTrainingData(datum);
            long count = completionCount.incrementAndGet();
            if ((count % 100) == 0) {
                getLogger().debug("Processed {} / {} training records.", count, inputRecords.size());
            }
        });
        getLogger().info("Training encoding model.");
        encodingAnalyzer = trainer.build();
        getLogger().info("Encoding model training complete.");
    }

    /**
     * Train the language model for the text analyzer.
     */
    private void trainLanguageAnalyzer()
            throws Exception {
        getLogger().info("Creating training data for language model.");
        FeatureModel<Language> featureModel = TextModels.getLanguageModel(required);
        WekaForestBuilder<Language> trainer = new WekaForestBuilder<>(featureModel, TextInfo.LANGUAGE);
        AtomicLong completionCount = new AtomicLong();
        inputRecords.forEach((record) -> {
            String recordText = Encoding.decode(record.data, Encoding.getCharset(record.encoding));
            Datum<Language> datum = new Datum<>(featureModel.getOutputFeature(), record.language);
            Map<Object, Map<AnalyzerId, List<Analysis>>> analyzerResults = new HashMap<>();
            analyzerResults.put(record.data, CoreAnalyzers.getInstance().run(record.data,
                    required::contains, (analyzer) -> analyzer.produces(TextInfo.ENCODING)));
            analyzerResults.put(recordText, CoreAnalyzers.getInstance().run(recordText,
                    required::contains, (analyzer) ->
                            ((analyzer.produces(TextInfo.LANGUAGE)) || (analyzer.produces(TextInfo.SCRIPT)))));
            TextModels.fillFeatures(record.getTextInfo(), datum.getFeatureData(), featureModel,
                    TextModels.ENCODING_PREFIX, analyzerResults);
            TextModels.fillFeatures(record.getTextInfo(), datum.getFeatureData(), featureModel,
                    TextModels.LANGUAGE_PREFIX, analyzerResults);
            trainer.addTrainingData(datum);
            long count = completionCount.incrementAndGet();
            if ((count % 100) == 0) {
                getLogger().debug("Processed {} / {} training records.", count, inputRecords.size());
            }
        });
        getLogger().info("Training language model.");
        languageAnalyzer = trainer.build();
        getLogger().info("Language model training complete.");
    }

    /**
     * Train the script model for the text analyzer.
     */
    private void trainScriptAnalyzer()
            throws Exception {
        getLogger().info("Creating training data for script model.");
        FeatureModel<Script> featureModel = TextModels.getScriptModel(required);
        WekaForestBuilder<Script> trainer = new WekaForestBuilder<>(featureModel, TextInfo.SCRIPT);
        AtomicLong completionCount = new AtomicLong();
        inputRecords.forEach((record) -> {
            String recordText = Encoding.decode(record.data, Encoding.getCharset(record.encoding));
            Datum<Script> datum = new Datum<>(featureModel.getOutputFeature(), record.script);
            Map<Object, Map<AnalyzerId, List<Analysis>>> analyzerResults = new HashMap<>();
            analyzerResults.put(record.data, CoreAnalyzers.getInstance().run(record.data,
                    required::contains, (analyzer) -> analyzer.produces(TextInfo.ENCODING)));
            analyzerResults.put(recordText, CoreAnalyzers.getInstance().run(recordText,
                    required::contains, (analyzer) ->
                            ((analyzer.produces(TextInfo.LANGUAGE)) || (analyzer.produces(TextInfo.SCRIPT)))));
            TextModels.fillFeatures(record.getTextInfo(), datum.getFeatureData(), featureModel,
                    TextModels.ENCODING_PREFIX, analyzerResults);
            TextModels.fillFeatures(record.getTextInfo(), datum.getFeatureData(), featureModel,
                    TextModels.SCRIPT_PREFIX, analyzerResults);
            trainer.addTrainingData(datum);
            long count = completionCount.incrementAndGet();
            if ((count % 100) == 0) {
                getLogger().debug("Processed {} / {} training records.", count, inputRecords.size());
            }
        });
        getLogger().info("Training script model.");
        scriptAnalyzer = trainer.build();
        getLogger().info("Script model training complete.");
    }

    /**
     * Create the profile artifact.
     *
     * @return The trained analyzer.
     */
    private WekaAnalyzer createArtifact()
            throws Exception {
        Path outputDir = IO.normalize("build/artifacts/profiles");
        IO.createDirectory(outputDir);
        Path archive = outputDir.resolve(name + ".jar");
        getLogger().info("Creating analyzer artifact: {}", archive);
        if (IO.isValidFile(archive)) {
            getLogger().info("Removing existing artifact.");
            IO.delete(archive);
        }
        AtomicReference<WekaAnalyzer> analyzer = new AtomicReference<>();
        IO.inArchive(archive, true, (root) -> {
            IO.createDirectory(root.resolve("META-INF"));
            IO.writeText(root.resolve("META-INF/Manifest.MF"), "Manifest-Version: 1.0", Encoding.UTF8);
            if (arff) {
                IO.createDirectory(root.resolve("arff"));
                // ARFF's are generated before any serialization which removes the training data information
                // from the weka portion of the WekaForest internal instances.
                IO.writeText(root.resolve("arff/encoding.arff"), getArff(encodingAnalyzer), Encoding.UTF8);
                IO.writeText(root.resolve("arff/language.arff"), getArff(languageAnalyzer), Encoding.UTF8);
                IO.writeText(root.resolve("arff/script.arff"), getArff(scriptAnalyzer), Encoding.UTF8);
            }
            analyzer.set(new WekaAnalyzer(required, encodingAnalyzer, languageAnalyzer, scriptAnalyzer));
            WekaProfile profile = new WekaProfile(name, analyzer.get(), inputFile, inputRecords.size());
            IO.writeText(root.resolve("META-INF/profile.json"), GSON.toJson(profile), Encoding.UTF8);
            IO.writeBytes(root.resolve(WekaProfile.class.getName()), Serialization.toBytes(profile));
            IO.writeBytes(root.resolve("profile_analyzer.ser"), Serialization.toBytes(analyzer.get()));
        });
        return analyzer.get();
    }

    /**
     * Get the ARFF equivalent of the training data for the specified weka forest.
     *
     * @param forest The forest of interest.
     *
     * @return The ARFF equivalent of the training data for the specified weka forest.
     */
    private String getArff(WekaForest<?> forest)
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setDestination(outputStream);
        arffSaver.setInstances(forest.getWekaModel());
        arffSaver.writeBatch();
        return outputStream.toString();
    }
}