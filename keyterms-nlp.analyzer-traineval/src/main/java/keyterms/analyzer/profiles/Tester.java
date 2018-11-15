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

import java.awt.Desktop;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.analyzer.Analyzer;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.analyzer.text.TextInfo;
import keyterms.analyzer.text.VotingAnalyzer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.iso.StdDef;
import keyterms.util.Errors;
import keyterms.util.collect.Keyed;
import keyterms.util.config.Args;
import keyterms.util.io.Encoding;
import keyterms.util.io.IO;
import keyterms.util.text.Strings;

/**
 * A utility for testing weka based ensemble analyzers.
 */
public class Tester {
    /**
     * Application error conditions which cause early termination.
     */
    enum ExitCode {
        FATAL_ERROR,
        NO_PROFILE,
        NO_TESTING,
        MISSING_PROFILE;

        /**
         * Get the equivalent system exit code.
         *
         * @return The equivalent system exit code.
         */
        private int getExitCode() {
            return -(ordinal() + 1);
        }
    }

    private enum Style {
        HEADER,
        TRUTH_HEADER,
        TRUTH_VALUE,
        MAIN_HEADER,
        MAIN_VALUE,
        CORRECT_VALUE,
        WRONG_VALUE,
        ZERO_VALUE
    }

    /**
     * The input parameter containing the name of the profile to test.
     */
    static final String PROFILE_NAME = "name";

    /**
     * The input parameter containing the path to the testing index file.
     */
    private static final String TEST_FILE = "test";

    /**
     * The input parameter which determines whether raw evaluation data is included in the report.
     */
    static final String RAW = "raw";

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
            Path inputFile = IO.normalize(clArgs.getText(TEST_FILE).orElse(null));
            if (!IO.exists(inputFile)) {
                exit("Could not find training input file: " + inputFile, ExitCode.NO_TESTING);
            }
            CoreAnalyzers.getInstance();
            Analyzer analyzer = WekaProfiles.getInstance().get(name);
            if (analyzer == null) {
                exit("The specified weka profile is not available: " + name, ExitCode.MISSING_PROFILE);
            }
            boolean outputRaw = clArgs.getBoolean(RAW).orElse(false);
            Tester tester = new Tester(name, analyzer, inputFile, outputRaw);
            tester.run();
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
        return LoggerFactory.getLogger(Tester.class.getSimpleName());
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
     * The analyzer name.
     */
    private final String name;

    /**
     * The analyzer being evaluated.
     */
    private final Analyzer analyzer;

    /**
     * The path to the CSV input file which details the testing data.
     */
    private final Path inputFile;

    /**
     * A flag indicating whether to output raw evaluation results in the report.
     */
    private final boolean outputRaw;

    /**
     * The input descriptions of the testing records.
     */
    private List<InputRecord> inputRecords;

    /**
     * The analyzers to run in report order preference.
     */
    private final List<Keyed<String, Analyzer>> analyzers;

    /**
     * Evaluations of each analyzer being tested.
     */
    private final Map<String, AnalyzerEval> encodingEvaluations = new TreeMap<>();
    private final Map<String, AnalyzerEval> lenientEncodingEvaluations = new TreeMap<>();
    private final Map<String, AnalyzerEval> languageEvaluations = new TreeMap<>();
    private final Map<String, AnalyzerEval> scriptEvaluations = new TreeMap<>();
    private final Map<String, AnalyzerEval> compositeEvaluations = new TreeMap<>();
    private final Map<String, AnalyzerEval> lenientCompositeEvaluations = new TreeMap<>();

    /**
     * Report workbook styles.
     */
    private final Map<Style, CellStyle> styles = new HashMap<>();

    /**
     * Constructor.
     *
     * @param name The analyzer name.
     * @param analyzer The analyzer.
     * @param inputFile The file containing testing records.
     * @param outputRaw A flag indicating whether to output raw evaluation results in the report.
     */
    Tester(String name, Analyzer analyzer, Path inputFile, boolean outputRaw) {
        super();
        this.name = name;
        this.analyzer = analyzer;
        this.inputFile = inputFile;
        this.outputRaw = outputRaw;
        analyzers = CoreAnalyzers.getInstance().ids().stream()
                .sorted()
                .map((id) -> new Keyed<>(id.toString(), CoreAnalyzers.getInstance().get(id)))
                .collect(Collectors.toCollection(ArrayList::new));
        Set<AnalyzerId> requiredAnalyzers = Collections.emptySet();
        if (analyzer instanceof WekaAnalyzer) {
            requiredAnalyzers = ((WekaAnalyzer)analyzer).getRequiredAnalyzers();
        }
        if (analyzer instanceof WekaPool) {
            requiredAnalyzers = ((WekaPool)analyzer).getRequiredAnalyzers();
        }
        analyzers.add(0, new Keyed<>("voting", new VotingAnalyzer(requiredAnalyzers)));
        analyzers.add(0, new Keyed<>(name, analyzer));
    }

    /**
     * Load the testing records.
     *
     * @return The testing records.
     */
    List<InputRecord> loadTestingRecords()
            throws Exception {
        return InputParser.loadInputRecords(inputFile);
    }

    /**
     * Run the tester.
     */
    void run()
            throws Exception {
        getLogger().info("Loading test records.");
        inputRecords = loadTestingRecords();
        getLogger().info("Collecting test results.");
        Map<InputRecord, Map<String, TextInfo>> outputs = new LinkedHashMap<>();
        inputRecords.forEach((record) -> {
            // Results for analyzers on binary data.
            analyzers.forEach((k) -> {
                // Run any analyzers that accept binary data.
                if (k.getValue().accepts(byte[].class)) {
                    TextInfo best = TextInfo.of(k.getValue().analyze(record.data).stream()
                            .findFirst().orElse(new TextInfo()));
                    outputs.computeIfAbsent(record, (mk) -> new TreeMap<>()).put(k.getKey(), best);
                    updateEvaluations(record, k.getKey(), k.getValue(), best);
                }
            });
            // Results for analyzers on text data.
            String recordText = Encoding.decode(record.data, Encoding.getCharset(record.encoding));
            analyzers.forEach((k) -> {
                // Run only analyzers that exclusively accept text for this phase.
                // This is done to avoid overriding ensemble analyzer evaluations of the binary output.
                if ((k.getValue().accepts(CharSequence.class))
                        && (!k.getValue().accepts(byte[].class))) {
                    TextInfo best = TextInfo.of(k.getValue().analyze(recordText).stream()
                            .findFirst().orElse(new TextInfo()));
                    outputs.computeIfAbsent(record, (mk) -> new TreeMap<>()).put(k.getKey(), best);
                    updateEvaluations(record, k.getKey(), k.getValue(), best);
                }
            });
            // Progress report.
            if ((outputs.size() % 100) == 0) {
                getLogger().info("Processed {} / {} records.", outputs.size(), inputRecords.size());
            }
        });
        // Evaluation results.
        getLogger().info("Analyzing evaluation results.");
        encodingEvaluations.values().forEach(AnalyzerEval::computeStats);
        lenientEncodingEvaluations.values().forEach(AnalyzerEval::computeStats);
        languageEvaluations.values().forEach(AnalyzerEval::computeStats);
        scriptEvaluations.values().forEach(AnalyzerEval::computeStats);
        compositeEvaluations.values().forEach(AnalyzerEval::computeStats);
        lenientCompositeEvaluations.values().forEach(AnalyzerEval::computeStats);
        // Output Report
        writeReport(outputs);
    }

    /**
     * Update the evaluations as specified.
     *
     * @param record The ground truth record.
     * @param id The analyzer id.
     * @param analyzer The analyzer which produced the result.
     * @param result The analyzer result.
     */
    private void updateEvaluations(InputRecord record, String id, Analyzer analyzer, TextInfo result) {
        String encoding = null;
        Language language = null;
        Script script = null;
        if (result != null) {
            encoding = result.getEncoding();
            language = result.getLanguage();
            script = result.getScript();
        } else {
            getLogger().warn("No results for {} from {}.", IO.getName(record.inputFile), id);
        }
        int produces = 0;
        if (analyzer.produces(TextInfo.ENCODING)) {
            produces++;
            encodingEvaluations.computeIfAbsent(id, (k) -> new AnalyzerEval())
                    .addTestResult(record.encoding, encoding);
            // Evaluate whether to allow the answer as correct by lenient standards.
            String lenient = (checkLenientEncodings(record.data, record.encoding, encoding))
                    ? record.encoding
                    : encoding;
            lenientEncodingEvaluations.computeIfAbsent(id, (k) -> new AnalyzerEval())
                    .addTestResult(record.encoding, lenient);
        }
        if (analyzer.produces(TextInfo.LANGUAGE)) {
            produces++;
            languageEvaluations.computeIfAbsent(id, (k) -> new AnalyzerEval())
                    .addTestResult(record.language, language);
        }
        if (analyzer.produces(TextInfo.SCRIPT)) {
            produces++;
            scriptEvaluations.computeIfAbsent(id, (k) -> new AnalyzerEval())
                    .addTestResult(record.script, script);
        }
        if (produces == 3) {
            compositeEvaluations.computeIfAbsent(id, (k) -> new AnalyzerEval())
                    .addTestResult(toString(record.getTextInfo()), toString(result));
            if (checkLenientEncodings(record.data, record.encoding, encoding)) {
                TextInfo textInfo = new TextInfo();
                textInfo.setEncoding(record.encoding);
                textInfo.setLanguage(language);
                textInfo.setScript(script);
                lenientCompositeEvaluations.computeIfAbsent(id, (k) -> new AnalyzerEval())
                        .addTestResult(toString(record.getTextInfo()), toString(textInfo));
            } else {
                lenientCompositeEvaluations.computeIfAbsent(id, (k) -> new AnalyzerEval())
                        .addTestResult(toString(record.getTextInfo()), toString(result));
            }
        }
    }

    /**
     * Determine if the two encodings produce the same output on the specified input data.
     *
     * @param data The input data.
     * @param trueEncoding The desired encoding.
     * @param detectedEncoding The detected encoding.
     *
     * @return A flag indicating whether the specified encodings produce the same output for the specified input data.
     */
    private boolean checkLenientEncodings(byte[] data, String trueEncoding, String detectedEncoding) {
        String normalTrue = VotingAnalyzer.normalizeEncoding(trueEncoding);
        String normalDetected = VotingAnalyzer.normalizeEncoding(detectedEncoding);
        boolean equivalent = Objects.equals(normalTrue, normalDetected);
        if (!equivalent) {
            String s1;
            try {
                s1 = Encoding.decode(data, Encoding.getCharset(normalTrue));
            } catch (Exception error) {
                s1 = null;
            }
            String s2;
            try {
                s2 = Encoding.decode(data, Encoding.getCharset(normalDetected));
            } catch (Exception error) {
                s2 = null;
            }
            equivalent = ((s1 != null) && (s1.equals(s2)));
        }
        return equivalent;
    }

    /**
     * Convert the value to a textual representation.
     *
     * @param value The value.
     *
     * @return The textual representation of the value.
     */
    private String toString(Object value) {
        String text = null;
        if (value instanceof TextInfo) {
            text = toString((TextInfo)value);
        }
        if (value instanceof String) {
            text = toString((String)value);
        }
        if (value instanceof Language) {
            text = toString((Language)value);
        }
        if (value instanceof Script) {
            text = toString((Script)value);
        }
        return text;
    }

    /**
     * Convert the text information result of a classification into a textual format.
     *
     * @param textInfo The text information.
     *
     * @return The textual representation of the text information used in testing analysis.
     */
    private String toString(TextInfo textInfo) {
        StringBuilder text = new StringBuilder();
        if (textInfo != null) {
            text.append(toString(textInfo.getEncoding()));
            text.append(':');
            text.append(toString(textInfo.getLanguage()));
            text.append(':');
            text.append(toString(textInfo.getScript()));
        } else {
            text.append("::");
        }
        return text.toString();
    }

    /**
     * Convert the encoding value to a textual representation.
     *
     * @param encoding The encoding.
     *
     * @return The specified text.
     */
    private String toString(String encoding) {
        return (Strings.isBlank(encoding)) ? "" : Strings.trim(encoding);
    }

    /**
     * Convert the language value to a textual representation.
     *
     * @param language The language.
     *
     * @return The specified text.
     */
    private String toString(Language language) {
        return (language != null) ? language.getCode().toLowerCase() : "";
    }

    /**
     * Convert the script value to a textual representation.
     *
     * @param script script.
     *
     * @return The specified text.
     */
    private String toString(Script script) {
        return (script != null) ? script.getCode().toLowerCase() : "";
    }

    /**
     * Set up the styles for the workbook report.
     *
     * @param workbook The workbook.
     */
    private void setupStyles(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        styles.put(Style.HEADER, headerStyle);
        // Truth header
        CellStyle truthHeaderStyle = workbook.createCellStyle();
        truthHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        Font truthHeaderFont = workbook.createFont();
        truthHeaderFont.setColor(IndexedColors.GREEN.getIndex());
        truthHeaderFont.setBold(true);
        truthHeaderStyle.setFont(truthHeaderFont);
        styles.put(Style.TRUTH_HEADER, truthHeaderStyle);
        // Truth values
        CellStyle truthValueStyle = workbook.createCellStyle();
        Font truthValueFont = workbook.createFont();
        truthValueFont.setColor(IndexedColors.GREEN.getIndex());
        truthValueStyle.setFont(truthValueFont);
        styles.put(Style.TRUTH_VALUE, truthValueStyle);
        // Main header
        CellStyle mainHeaderStyle = workbook.createCellStyle();
        mainHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        Font mainHeaderFont = workbook.createFont();
        mainHeaderFont.setColor(IndexedColors.BLUE.getIndex());
        mainHeaderFont.setBold(true);
        mainHeaderStyle.setFont(mainHeaderFont);
        styles.put(Style.MAIN_HEADER, mainHeaderStyle);
        // Main values
        CellStyle mainValueStyle = workbook.createCellStyle();
        Font mainValueFont = workbook.createFont();
        mainValueFont.setColor(IndexedColors.BLUE.getIndex());
        mainValueStyle.setFont(mainValueFont);
        styles.put(Style.MAIN_VALUE, mainValueStyle);
        // Correct classifications
        CellStyle correctStyle = workbook.createCellStyle();
        correctStyle.setAlignment(HorizontalAlignment.CENTER);
        Font correctFont = workbook.createFont();
        correctFont.setColor(IndexedColors.GREEN.getIndex());
        correctFont.setBold(true);
        correctStyle.setFont(correctFont);
        styles.put(Style.CORRECT_VALUE, correctStyle);
        // Incorrect classifications
        CellStyle wrongStyle = workbook.createCellStyle();
        wrongStyle.setAlignment(HorizontalAlignment.CENTER);
        Font wrongFont = workbook.createFont();
        wrongFont.setColor(IndexedColors.RED.getIndex());
        wrongFont.setBold(true);
        wrongStyle.setFont(wrongFont);
        styles.put(Style.WRONG_VALUE, wrongStyle);
        // Incorrect classifications
        CellStyle zeroStyle = workbook.createCellStyle();
        zeroStyle.setAlignment(HorizontalAlignment.CENTER);
        Font zeroFont = workbook.createFont();
        zeroFont.setColor(IndexedColors.BLACK.getIndex());
        zeroFont.setBold(false);
        zeroStyle.setFont(zeroFont);
        styles.put(Style.ZERO_VALUE, zeroStyle);
    }

    /**
     * Generate the report spreadsheet.
     */
    private void writeReport(Map<InputRecord, Map<String, TextInfo>> outputs)
            throws Exception {
        Path reportFile = IO.normalize("build/reports/" + name + "_report.xlsx");
        getLogger().info("Generating report.");
        XSSFWorkbook workbook = new XSSFWorkbook();
        setupStyles(workbook);
        AtomicInteger r = new AtomicInteger(-1);
        AtomicInteger c = new AtomicInteger(-1);
        // Metadata
        if (analyzer instanceof WekaAnalyzer) {
            WekaProfile profile = new WekaProfile(name, (WekaAnalyzer)analyzer, inputFile, inputRecords.size());
            XSSFSheet metaSheet = workbook.createSheet("Metadata");
            metaSheet.createRow(r.incrementAndGet()).createCell(0).setCellValue(name);
            metaSheet.getRow(r.get()).getCell(0).setCellStyle(styles.get(Style.MAIN_HEADER));
            List<String> analyzerIds = profile.getRequiredAnalyzers().stream()
                    .sorted()
                    .map(Strings::toString)
                    .collect(Collectors.toList());
            metaSheet.getRow(r.get()).createCell(1).setCellValue(analyzerIds.toString());
            metaSheet.getRow(r.get()).getCell(1).setCellStyle(styles.get(Style.MAIN_VALUE));
            metaSheet.createRow(r.incrementAndGet()).createCell(0).setCellValue("Test Date");
            metaSheet.getRow(r.get()).getCell(0).setCellStyle(styles.get(Style.HEADER));
            metaSheet.getRow(r.get()).createCell(1).setCellValue(
                    new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(profile.getCreateDate()));
            metaSheet.getRow(r.get()).getCell(1).setCellStyle(styles.get(Style.ZERO_VALUE));
            metaSheet.createRow(r.incrementAndGet()).createCell(0).setCellValue("Tester");
            metaSheet.getRow(r.get()).getCell(0).setCellStyle(styles.get(Style.HEADER));
            metaSheet.getRow(r.get()).createCell(1).setCellValue(profile.getTrainer());
            metaSheet.getRow(r.get()).getCell(1).setCellStyle(styles.get(Style.ZERO_VALUE));
            metaSheet.createRow(r.incrementAndGet()).createCell(0).setCellValue("Test File");
            metaSheet.getRow(r.get()).getCell(0).setCellStyle(styles.get(Style.HEADER));
            metaSheet.getRow(r.get()).createCell(1).setCellValue(profile.getTrainingFile());
            metaSheet.getRow(r.get()).getCell(1).setCellStyle(styles.get(Style.ZERO_VALUE));
            metaSheet.createRow(r.incrementAndGet()).createCell(0).setCellValue("Testing Records");
            metaSheet.getRow(r.get()).getCell(0).setCellStyle(styles.get(Style.HEADER));
            metaSheet.getRow(r.get()).createCell(1).setCellValue(profile.getTrainingInstances());
            metaSheet.getRow(r.get()).getCell(1).setCellStyle(styles.get(Style.ZERO_VALUE));
            metaSheet.createRow(r.incrementAndGet()).createCell(0).setCellValue("Testing Updated");
            metaSheet.getRow(r.get()).getCell(0).setCellStyle(styles.get(Style.HEADER));
            metaSheet.getRow(r.get()).createCell(1).setCellValue(
                    new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(profile.getLastTrainingFileUpdate()));
            metaSheet.getRow(r.get()).getCell(1).setCellStyle(styles.get(Style.ZERO_VALUE));
            for (int cc = 0; cc <= 1; cc++) {
                metaSheet.autoSizeColumn(cc);
            }
        }
        List<String> headers = analyzers.stream()
                .map(Keyed::getKey)
                .collect(Collectors.toList());
        XSSFRow headerRow;
        Cell headerCell;
        if (outputRaw) {
            // Raw Results
            r.set(-1);
            c.set(-1);
            XSSFSheet rawSheet = workbook.createSheet("Raw Results");
            headerRow = rawSheet.createRow(r.incrementAndGet());
            headerCell = headerRow.createCell(c.incrementAndGet());
            headerCell.setCellValue("File");
            headerCell.setCellStyle(styles.get(Style.HEADER));
            headerCell = headerRow.createCell(c.incrementAndGet());
            headerCell.setCellValue("Truth");
            headerCell.setCellStyle(styles.get(Style.TRUTH_HEADER));
            headerRow.createCell(c.incrementAndGet());
            headerRow.createCell(c.incrementAndGet());
            HashMap<String, Integer> headerIndexes = new HashMap<>();
            for (String h : headers) {
                int ci = c.incrementAndGet();
                Cell hc = headerRow.createCell(ci);
                hc.setCellValue(h);
                hc.setCellStyle(styles.get(Style.HEADER));
                if (h.equals(name)) {
                    hc.setCellStyle(styles.get(Style.MAIN_HEADER));
                }
                headerIndexes.put(h, ci);
                headerRow.createCell(c.incrementAndGet());
                headerRow.createCell(c.incrementAndGet());
            }
            outputs.forEach((record, resultMap) -> {
                XSSFRow outRow = rawSheet.createRow(r.incrementAndGet());
                outRow.createCell(0).setCellValue(IO.getName(record.inputFile));
                if (record.getTextInfo() != null) {
                    outRow.createCell(1).setCellValue(toString(record.getTextInfo().getEncoding()));
                    outRow.createCell(2).setCellValue(toString(record.getTextInfo().getLanguage()));
                    outRow.createCell(3).setCellValue(toString(record.getTextInfo().getScript()));
                    for (int cc = 1; cc <= 3; cc++) {
                        outRow.getCell(cc).setCellStyle(styles.get(Style.TRUTH_VALUE));
                    }
                }
                resultMap.forEach((id, textInfo) -> {
                    Integer columnIndex = headerIndexes.get(id);
                    if (textInfo != null) {
                        outRow.createCell(columnIndex).setCellValue(toString(textInfo.getEncoding()));
                        outRow.createCell(columnIndex + 1).setCellValue(toString(textInfo.getLanguage()));
                        outRow.createCell(columnIndex + 2).setCellValue(toString(textInfo.getScript()));
                        if (id.equals(name)) {
                            for (int cc = columnIndex; cc <= columnIndex + 2; cc++) {
                                outRow.getCell(cc).setCellStyle(styles.get(Style.MAIN_VALUE));
                            }
                        }
                    }
                });
            });
            rawSheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 3));
            headerIndexes.values().forEach((i) ->
                    rawSheet.addMergedRegion(new CellRangeAddress(0, 0, i, i + 2)));
            for (int col = 0; col < (4 + (headers.size() * 3)); col++) {
                rawSheet.autoSizeColumn(col);
            }
        }
        // Evaluation statistical results
        XSSFSheet statsSheet = workbook.createSheet("Statistics");
        r.set(-1);
        c.set(-1);
        headerRow = statsSheet.createRow(r.incrementAndGet());
        headerRow.createCell(c.incrementAndGet());
        headerCell = headerRow.createCell(c.incrementAndGet());
        headerCell.setCellValue("Analyzer");
        headerCell.setCellStyle(styles.get(Style.HEADER));
        headerCell = headerRow.createCell(c.incrementAndGet());
        headerCell.setCellValue("Correct");
        headerCell.setCellStyle(styles.get(Style.HEADER));
        headerCell = headerRow.createCell(c.incrementAndGet());
        headerCell.setCellValue("Precision");
        headerCell.setCellStyle(styles.get(Style.HEADER));
        headerCell = headerRow.createCell(c.incrementAndGet());
        headerCell.setCellValue("Recall");
        headerCell.setCellStyle(styles.get(Style.HEADER));
        headerCell = headerRow.createCell(c.incrementAndGet());
        headerCell.setCellValue("F1");
        headerCell.setCellStyle(styles.get(Style.HEADER));
        appendEvaluations(statsSheet, "Strict Encoding", r, headers, encodingEvaluations);
        appendEvaluations(statsSheet, "Lenient Encoding", r, headers, lenientEncodingEvaluations);
        appendEvaluations(statsSheet, "Language", r, headers, languageEvaluations);
        appendEvaluations(statsSheet, "Script", r, headers, scriptEvaluations);
        appendEvaluations(statsSheet, "Strict Composite", r, headers, compositeEvaluations);
        appendEvaluations(statsSheet, "Lenient Composite", r, headers, lenientCompositeEvaluations);
        for (int col = 0; col < 6; col++) {
            statsSheet.autoSizeColumn(col);
        }
        // Confusion matrices
        XSSFSheet encodingMatrixSheet = workbook.createSheet("Encoding (Strict)");
        XSSFSheet lenientMatrixSheet = workbook.createSheet("Encoding (Lenient)");
        XSSFSheet languageMatrixSheet = workbook.createSheet("Language");
        XSSFSheet scriptMatrixSheet = workbook.createSheet("Script");
        AtomicInteger r1 = new AtomicInteger(-1);
        AtomicInteger r2 = new AtomicInteger(-1);
        AtomicInteger r3 = new AtomicInteger(-1);
        AtomicInteger r4 = new AtomicInteger(-1);
        analyzers.forEach((k) -> {
            String id = k.getKey();
            Analyzer analyzer = k.getValue();
            if (analyzer.produces(TextInfo.ENCODING)) {
                appendMatrix(encodingMatrixSheet, id + " Encoding Confusion Matrix", r1,
                        encodingEvaluations.get(id));
                appendMatrix(lenientMatrixSheet, id + " Encoding Confusion Matrix", r2,
                        lenientEncodingEvaluations.get(id));
            }
            if (analyzer.produces(TextInfo.LANGUAGE)) {
                appendMatrix(languageMatrixSheet, id + " Language Confusion Matrix", r3,
                        languageEvaluations.get(id));
            }
            if (analyzer.produces(TextInfo.SCRIPT)) {
                appendMatrix(scriptMatrixSheet, id + " Script Confusion Matrix", r4,
                        scriptEvaluations.get(id));
            }
        });
        // Write the report.
        getLogger().info("Writing report: {}", reportFile);
        if (!IO.isValidDirectory(reportFile.getParent())) {
            IO.createDirectory(reportFile.getParent());
        }
        if (IO.exists(reportFile)) {
            getLogger().info("Removing old report.");
            IO.delete(reportFile);
        }
        try (FileOutputStream outputStream = new FileOutputStream(reportFile.toFile())) {
            workbook.write(outputStream);
        }
        // Attempt to have the O/S open the report.
        try {
            Desktop.getDesktop().browse(reportFile.toUri());
        } catch (Exception error) {
            Errors.ignore(error);
        }
    }

    /**
     * Append statistics from the specified evaluations.
     *
     * @param sheet The statistics sheet.
     * @param title The section title.
     * @param r The starting row.
     * @param headers The analyzer ids.
     * @param evaluations The evaluations to report.
     */
    private void appendEvaluations(XSSFSheet sheet, String title, AtomicInteger r,
            List<String> headers, Map<String, AnalyzerEval> evaluations) {
        AtomicInteger c = new AtomicInteger();
        AtomicBoolean showEval = new AtomicBoolean(true);
        headers.forEach((id) -> {
            AnalyzerEval eval = evaluations.get(id);
            if (eval != null) {
                XSSFRow row = sheet.createRow(r.incrementAndGet());
                c.set(0);
                if (showEval.getAndSet(false)) {
                    row.createCell(0).setCellValue(title);
                    row.getCell(0).setCellStyle(styles.get(Style.HEADER));
                }
                XSSFCell cell;
                cell = row.createCell(c.incrementAndGet());
                cell.setCellValue(id);
                cell = row.createCell(c.incrementAndGet());
                cell.setCellValue(String.format("%.4f%%", eval.getPercentCorrect() * 100.0));
                cell = row.createCell(c.incrementAndGet());
                cell.setCellValue(String.format("%.4f%%", eval.getStatistic(AnalyzerStats::getPrecision) * 100.0));
                cell = row.createCell(c.incrementAndGet());
                cell.setCellValue(String.format("%.4f%%", eval.getStatistic(AnalyzerStats::getRecall) * 100.0));
                cell = row.createCell(c.incrementAndGet());
                cell.setCellValue(String.format("%.4f%%", eval.getStatistic(AnalyzerStats::getF1Score) * 100.0));
                if (id.equals(name)) {
                    for (int cc = 1; cc <= c.get(); cc++) {
                        row.getCell(cc).setCellStyle(styles.get(Style.MAIN_HEADER));
                    }
                }
            }
        });
        r.incrementAndGet();
    }

    /**
     * Append a confusion matrix to the specified sheet.
     *
     * @param sheet The work sheet.
     * @param results The analyzer evaluation results containing the confusion matrix.
     */
    private void appendMatrix(XSSFSheet sheet, String title, AtomicInteger r, AnalyzerEval results) {
        List<Object> classValues = sort(results.getClassValues());
        XSSFRow row = sheet.createRow(r.incrementAndGet());
        row.createCell(0).setCellValue(title);
        // Header (truth labels)
        row = sheet.createRow(r.incrementAndGet());
        AtomicInteger c = new AtomicInteger(-1);
        row.createCell(c.incrementAndGet()).setCellValue("actual↓ \\ truth→");
        for (Object v : classValues) {
            XSSFCell cell = row.createCell(c.incrementAndGet());
            cell.setCellValue(toString(v));
            cell.setCellStyle(styles.get(Style.HEADER));
        }
        // Matrix
        for (Object actual : classValues) {
            row = sheet.createRow(r.incrementAndGet());
            c.set(-1);
            XSSFCell cell = row.createCell(c.incrementAndGet());
            cell.setCellValue(toString(actual));
            cell.setCellStyle(styles.get(Style.HEADER));
            for (Object truth : classValues) {
                cell = row.createCell(c.incrementAndGet());
                long count = results.getConfusionCount(truth, actual);
                cell.setCellValue(count);
                if (Objects.equals(truth, actual)) {
                    cell.setCellStyle(styles.get(Style.CORRECT_VALUE));
                } else {
                    cell.setCellStyle((count == 0)
                            ? styles.get(Style.ZERO_VALUE)
                            : styles.get(Style.WRONG_VALUE));
                }
            }
        }
        for (int cc = 0; cc <= c.get(); cc++) {
            sheet.autoSizeColumn(cc);
        }
        r.incrementAndGet();
    }

    /**
     * Sort the classifier output values for the report.
     *
     * @param unsorted The unsorted collection of output values.
     *
     * @return The sorted collection of output values.
     */
    @SuppressWarnings("unchecked")
    private static List<Object> sort(Set<Object> unsorted) {
        Comparator sorter = Comparator.naturalOrder();
        if (unsorted.stream().filter(Objects::nonNull).anyMatch(StdDef.class::isInstance)) {
            sorter = Comparator.comparing(o -> ((StdDef)o).getName());
        }
        sorter = Comparator.nullsFirst(sorter);
        List<Object> sorted = new ArrayList<>(unsorted);
        sorted.sort(sorter);
        return sorted;
    }
}