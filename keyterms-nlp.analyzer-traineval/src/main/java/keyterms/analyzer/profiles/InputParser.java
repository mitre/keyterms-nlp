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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.util.io.Encoding;
import keyterms.util.io.IO;
import keyterms.util.text.Strings;
import keyterms.util.text.splitter.DelimiterSplitter;
import keyterms.util.text.splitter.LineSplitter;
import keyterms.util.time.Timing;

/**
 * Common utility for loading input data for testing and training.
 */
public class InputParser {
    /**
     * A comma delimiter splitter.
     */
    private static final DelimiterSplitter COMMA = new DelimiterSplitter(",");

    /**
     * Get the logging topic for this class.
     *
     * @return The logging topic for this class.
     */
    static Logger getLogger() {
        return LoggerFactory.getLogger(InputParser.class);
    }

    /**
     * Load the raw training or testing records into memory.
     *
     * @param inputFile The input file containing the data.
     *
     * @return The specified data.
     */
    static List<InputRecord> loadInputRecords(Path inputFile)
            throws IOException {
        List<InputRecord> inputRecords = new ArrayList<>();
        getLogger().info("Loading input records from {}", inputFile);
        Path dataRoot = inputFile.getParent();
        Timing timing = new Timing();
        String contents = IO.readText(inputFile, Encoding.UTF8);
        int lineNumber = 0;
        List<String> lines = new LineSplitter().split(contents);
        for (String line : lines) {
            if (lineNumber > 0) {
                boolean good = true;
                InputRecord inputRecord = new InputRecord();
                List<String> columns = COMMA.split(line);
                if (columns.size() != 4) {
                    getLogger().error("Wrong number of columns on line #{}", lineNumber);
                    good = false;
                }
                inputRecord.inputFile = IO.normalize(dataRoot.resolve(columns.get(0))).toString();
                if (!IO.isValidFile(inputFile)) {
                    getLogger().error("Could not find input file on line #{}: {}", lineNumber, inputFile);
                    good = false;
                }
                inputRecord.encoding = Strings.trim(columns.get(1));
                if (Strings.isBlank(inputRecord.encoding)) {
                    getLogger().error("No encoding on line #{}", lineNumber);
                    good = false;
                }
                String languageName = Strings.trim(columns.get(2));
                inputRecord.language = Language.byText(languageName);
                if (inputRecord.language == null) {
                    getLogger().error("Invalid language on line #{}: {}", lineNumber, languageName);
                    good = false;
                }
                String scriptName = Strings.trim(columns.get(3));
                inputRecord.script = Script.byText(scriptName);
                if (inputRecord.script == null) {
                    getLogger().error("Invalid script on line #{}: {}", lineNumber, scriptName);
                    good = false;
                }
                try {
                    inputRecord.data = IO.readBytes(inputRecord.inputFile);
                } catch (Exception e) {
                    getLogger().error("Could not load data from line #{}: {}", lineNumber, inputRecord.inputFile, e);
                    good = false;
                }
                try {
                    Encoding.decode(inputRecord.data, Encoding.getCharset(inputRecord.encoding));
                } catch (Exception e) {
                    getLogger().error("Could not decode data from line #{}: {}", lineNumber, inputRecord.inputFile, e);
                    good = false;
                }
                if (good) {
                    inputRecords.add(inputRecord);
                }
            }
            lineNumber++;
            if ((lineNumber % 100) == 0) {
                getLogger().info("Loaded {} / {} records.", lineNumber, lines.size() - 1);
            }
        }
        getLogger().info("Loaded {} input records in {}.", inputRecords.size(), timing.finish().summary(2));
        return inputRecords;
    }
}