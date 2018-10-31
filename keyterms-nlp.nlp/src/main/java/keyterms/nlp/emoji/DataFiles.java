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

package keyterms.nlp.emoji;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.io.Encoding;
import keyterms.util.io.Streams;
import keyterms.util.text.Strings;
import keyterms.util.text.TextSplitter;
import keyterms.util.text.splitter.LineSplitter;
import keyterms.util.text.splitter.PatternSplitter;

/**
 * Utility for reading and parsing unicode emoji data files.
 */
class DataFiles {
    /**
     * A line splitter which removes empty lines and comment lines.
     */
    private static final LineSplitter LINE_SPLITTER = new LineSplitter() {
        @Override
        public List<String> split(CharSequence text) {
            return super.split(text).stream()
                    .filter(Strings::hasText)
                    .map(Strings::trim)
                    .filter((line) -> (!line.startsWith("#")))
                    .collect(Collectors.toList());
        }
    };

    /**
     * A white space text splitter.
     */
    private static final TextSplitter WS_SPLITTER = new PatternSplitter("\\s+");

    /**
     * Get the logging topic for this class.
     *
     * @return The logging topic for this class.
     */
    private static Logger getClassLogger() {
        return LoggerFactory.getLogger(DataFiles.class);
    }

    /**
     * Get the specified data file contents.
     *
     * @param resourceName The file name of the desired resource.
     *
     * @return The specified data file contents.
     */
    private static List<String> getDataFileRecords(String resourceName) {
        String contents = "";
        try {
            InputStream stream = DataFiles.class.getResourceAsStream(resourceName);
            byte[] data = Streams.readFully(stream);
            contents = Encoding.decode(data, Encoding.UTF8);
        } catch (Exception error) {
            getClassLogger().error("Could not load data file contents: {}", resourceName, error);
        }
        return LINE_SPLITTER.split(contents);
    }

    /**
     * Get the non-comment records from "unicode/v11/emoji-data.txt".
     *
     * @return The non-comment records from "unicode/v11/emoji-data.txt".
     */
    static List<String> getDataFileRecords() {
        return getDataFileRecords("unicode/v11/emoji-data.txt");
    }

    /**
     * Get the non-comment records from "unicode/v11/emoji-sequences.txt".
     *
     * @return The non-comment records from "unicode/v11/emoji-sequences.txt".
     */
    static List<String> getSequenceRecords() {
        return getDataFileRecords("unicode/v11/emoji-sequences.txt");
    }

    /**
     * Get the non-comment records from "unicode/v11/emoji-variation-sequences.txt".
     *
     * @return The non-comment records from "unicode/v11/emoji-variation-sequences.txt".
     */
    static List<String> getVariationSequenceRecords() {
        return getDataFileRecords("unicode/v11/emoji-variation-sequences.txt");
    }

    /**
     * Get the non-comment records from "unicode/v11/emoji-zwj-sequences.txt".
     *
     * @return The non-comment records from "unicode/v11/emoji-zwj-sequences.txt".
     */
    static List<String> getZwjSequenceRecords() {
        return getDataFileRecords("unicode/v11/emoji-zwj-sequences.txt");
    }

    /**
     * Get the code points referenced in the specified record.
     *
     * @param record The data file record.
     *
     * @return The code points referenced in the specified record.
     */
    static List<Integer> getCodePoints(String record) {
        List<Integer> codePoints = new ArrayList<>();
        String codePointColumn = Strings.trim(record.replaceAll(";.*", ""));
        WS_SPLITTER.split(codePointColumn).forEach((value) -> {
            if (value.contains("..")) {
                String[] range = value.split("\\.\\.");
                int first = Integer.parseInt(range[0], 16);
                int last = Integer.parseInt(range[1], 16);
                for (int cp = first; cp <= last; cp++) {
                    codePoints.add(cp);
                }
            } else {
                codePoints.add(Integer.parseInt(value, 16));
            }
        });
        return codePoints;
    }
}