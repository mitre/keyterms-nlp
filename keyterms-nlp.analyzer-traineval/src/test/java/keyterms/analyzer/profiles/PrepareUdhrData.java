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

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.nlp.text.SentenceSplitter;
import keyterms.testing.TestFiles;
import keyterms.util.Errors;
import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;
import keyterms.util.io.Encoding;
import keyterms.util.io.IO;
import keyterms.util.io.PathFinder;
import keyterms.util.lang.Lazy;
import keyterms.util.math.Statistics;
import keyterms.util.text.Strings;
import keyterms.util.text.TextSplitter;
import keyterms.util.text.splitter.LineSplitter;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

// This "Test" is used to prepare training and evaluation data base on the UDHR text data set.
@Ignore
public class PrepareUdhrData {

    private static final Path PROJECT_HOME = TestFiles.PROJECT_HOME;

    private static final Path DATA_DIR = PROJECT_HOME.resolve(".local/data");

    private static final Random RANDOM = new Random();

    private static final Pattern SECTION_HEADER = Pattern.compile("^([^\\s]+\\s+)?\\d+\\.?\\s*$");

    private static final Lazy<List<Keyed<Language, String>>> UDHR_DATA = new Lazy<>(() -> {
        List<Keyed<Language, String>> udhrTexts = new ArrayList<>();
        Path udhrZip = null;
        try {
            udhrZip = IO.find((p) -> "udhr_txt.zip".equals(IO.getName(p)));
        } catch (Exception error) {
            getLogger().error("Could not locate UDHR data.", error);
        }
        if (udhrZip != null) {
            try {
                IO.inArchive(udhrZip, (archiveRoot) -> {
                    PathFinder pathFinder = new PathFinder((p) -> "index.xml".equals(IO.getName(p)));
                    IO.walk(archiveRoot, pathFinder);
                    Path indexEntry = pathFinder.getPath();
                    String indexContents = IO.readText(indexEntry, Encoding.UTF8);
                    Document indexDoc = DocumentHelper.parseText(indexContents);
                    Element root = indexDoc.getRootElement();
                    List<Element> tags = root.elements("udhr");
                    for (Element tag : tags) {
                        String isoCode = tag.attributeValue("iso639-3");
                        Language isoLanguage = Language.byCode(isoCode);
                        if (isoLanguage != null) {
                            String fid = tag.attributeValue("f");
                            try {
                                String entryName = "udhr_" + fid + ".txt";
                                pathFinder = new PathFinder((p) -> entryName.equals(IO.getName(p)));
                                IO.walk(archiveRoot, pathFinder);
                                if (pathFinder.getPath() == null) {
                                    getLogger().trace("Could not load UDHR text for {}", isoLanguage);
                                } else {
                                    String text = IO.readText(pathFinder.getPath(), Encoding.UTF8);
                                    int headerEnd = text.indexOf("---");
                                    text = text.substring(headerEnd + 1);
                                    udhrTexts.add(new Keyed<>(isoLanguage, text));
                                }
                            } catch (Exception error) {
                                getLogger().trace("Could not load UDHR text for {}", isoLanguage, error);
                            }
                        }
                    }
                });
            } catch (Exception error) {
                getLogger().error("Could not load UDHR texts.", error);
            }
        }
        return Collections.unmodifiableList(udhrTexts);
    });

    private static Logger getLogger() {
        return LoggerFactory.getLogger("UDHR");
    }

    private static Set<Charset> getCharsets() {
        // return Encoding.getAvailableCharsets();
        //@todo determine a more expansive set of encodings to use.
        return Bags.staticSet(Encoding.UTF8, Encoding.UTF16, Encoding.UTF16LE);
    }

    private static Set<Language> getLanguages() {
        // Limit the number of languages evaluated for speed reasons/
        //@todo determine a more expansive set of languages to use.
        return Bags.hashSet(
                Language.BULGARIAN, Language.CHINESE, Language.CROATIAN, Language.CZECH,
                Language.DANISH, Language.DUTCH, Language.ENGLISH, Language.ESTONIAN,
                Language.FINNISH, Language.FRENCH, Language.GERMAN, Language.GREEK,
                Language.HUNGARIAN, Language.IRISH, Language.ITALIAN, Language.LATVIAN,
                Language.LITHUANIAN, Language.MALTESE, Language.POLISH, Language.PORTUGUESE,
                Language.ROMANIAN, Language.SLOVAK, Language.SLOVENIAN, Language.SPANISH,
                Language.SWEDISH
        );
    }

    @Test
    public void prepareTrainingData() {
        prepareUdhrData("train");
    }

    @Test
    public void prepareTestData() {
        prepareUdhrData("test");
    }

    private void prepareUdhrData(String label) {
        List<Keyed<Language, String>> udhrData = UDHR_DATA.value();
        assertNotNull(udhrData);
        assertNotEquals(0, udhrData.size());
        // Select only those files with known scripts.
        List<Keyed<WrittenLanguage, String>> withKnownScript = new ArrayList<>();
        Statistics sizeStats = new Statistics();
        Set<Language> languages = getLanguages();
        for (Keyed<Language, String> udhrText : udhrData) {
            Language language = udhrText.getKey();
            if (languages.contains(language)) {
                String text = udhrText.getValue();
                Script script = language.getPreferredScript();
                if (language.equals(Language.CHINESE)) {
                    script = Script.HAN_TRADITIONAL;
                }
                if (script != null) {
                    withKnownScript.add(new Keyed<>(new WrittenLanguage(language, script), text));
                    sizeStats.add(text.length());
                }
            }
        }
        getLogger().info("Selected {} UDHR files with size = {}", withKnownScript.size(), sizeStats);
        // Chunk the data into smaller pieces to increase sample size.
        List<Keyed<WrittenLanguage, List<String>>> chunks = new ArrayList<>();
        for (Keyed<WrittenLanguage, String> udhrText : withKnownScript) {
            WrittenLanguage written = udhrText.getKey();
            String text = udhrText.getValue();
            List<String> chunked = new UdhrChunker(written).split(text);
            chunks.add(new Keyed<>(written, chunked));
        }
        // Convert the data into various encodings.
        int idx = 0;
        List<InputRecord> trainingData = new ArrayList<>();
        for (Keyed<WrittenLanguage, List<String>> texts : chunks) {
            int t = 0;
            for (String text : texts.getValue()) {
                t++;
                WrittenLanguage written = texts.getKey();
                for (Charset encoding : getCharsets()) {
                    String encodingName = encoding.name().toLowerCase();
                    if (encodingName.startsWith("x-") || (encodingName.startsWith("iso-2022-"))) {
                        continue;
                    }
                    try {
                        InputRecord record = new InputRecord();
                        record.encoding = encodingName;
                        record.language = written.getLanguage();
                        record.script = written.getScript();
                        byte[] data = Encoding.encode(text, encoding);
                        Path out = DATA_DIR.resolve("udhr_" + label);
                        if (!IO.isValidDirectory(out)) {
                            IO.createDirectory(out);
                        }
                        out = out.resolve(encodingName +
                                "-" + written.getLanguage().getCode().toLowerCase() +
                                "-" + written.getScript().getCode().toLowerCase() +
                                "_" + t +
                                ".txt");
                        IO.writeBytes(out, data);
                        record.inputFile = DATA_DIR.relativize(out).toString();
                        trainingData.add(record);
                    } catch (Exception error) {
                        getLogger().error("Could save encoded data {} in {}", written, encodingName,
                                Errors.getSimpleErrorMessage(error));
                    }
                }
            }
            getLogger().info("Processed: {} / {} languages.", ++idx, chunks.size());
        }
        Path index = DATA_DIR.resolve("udhr_" + label + ".idx");
        getLogger().info("Writing training index at {}", index);
        StringBuilder out = new StringBuilder();
        try {
            out.append("File,Encoding,Language,Script\n");
            for (InputRecord record : trainingData) {
                String row = record.inputFile.replaceAll("\\\\", "/") + "," + record.encoding + ","
                        + record.language.getCode() + "," + record.script.getCode() + "\n";
                out.append(row);
            }
            IO.writeText(index, out, Encoding.UTF8);
        } catch (Exception error) {
            getLogger().error("Could not create training index.", error);
        }
    }

    private static class UdhrChunker
            implements TextSplitter {

        private final SentenceSplitter sentenceSplitter;

        UdhrChunker(WrittenLanguage written) {
            super();
            sentenceSplitter = new SentenceSplitter(written);
        }

        @Override
        public List<String> split(CharSequence text) {
            List<String> splits = new ArrayList<>();
            if (!Strings.isBlank(text)) {
                int headerStop = text.toString().indexOf("---");
                CharSequence noHeader = text.subSequence(headerStop + 3, text.length());
                List<String> lines = new LineSplitter().split(noHeader).stream()
                        .filter((line) -> !Strings.isBlank(line))
                        .map(Strings::trim)
                        .filter((line) -> !line.matches(SECTION_HEADER.pattern()))
                        .collect(Collectors.toList());
                List<String> sentences = new ArrayList<>();
                lines.forEach((line) -> sentenceSplitter.split(line).stream()
                        .filter((sentence) -> !Strings.isBlank(sentence))
                        .map(Strings::trim)
                        .filter((sentence) -> !sentence.matches(SECTION_HEADER.pattern()))
                        .forEach(sentences::add));
                for (int s = 1; s < 5; s++) {
                    for (int t = 0; t < 2; t++) {
                        List<Integer> indexes = new ArrayList<>();
                        while (indexes.size() < s) {
                            int next = RANDOM.nextInt(sentences.size());
                            if (!indexes.contains(next)) {
                                indexes.add(next);
                            }
                        }
                        StringBuilder paragraph = new StringBuilder();
                        indexes.forEach((i) -> paragraph.append(sentences.get(i)).append(" "));
                        splits.add(Strings.trim(paragraph));
                    }
                }
                splits.add(Strings.trim(text));
            }
            return splits;
        }
    }
}