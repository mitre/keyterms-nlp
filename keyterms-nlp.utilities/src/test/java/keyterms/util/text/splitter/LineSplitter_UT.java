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

package keyterms.util.text.splitter;

import java.util.List;

import org.junit.Test;

import keyterms.util.collect.Bags;

import static org.junit.Assert.assertEquals;

public class LineSplitter_UT {

    @Test
    public void splitByLineBreaks() {
        LineSplitter splitter = new LineSplitter();
        String firstLine = "First line.";
        String secondLine = "Second line.";
        String thirdLine = "Third line.";
        String fourthLine = "Fourth line.";
        List<String> lines = Bags.arrayList(firstLine, secondLine, thirdLine, fourthLine);
        String lineBreak;
        assertEquals(Bags.arrayList(firstLine), splitter.split(firstLine));
        // Mac line breaks.
        lineBreak = "\r";
        assertEquals(lines, splitter.split(mergeText(lines, lineBreak)));
        // Unix line breaks.
        lineBreak = "\n";
        assertEquals(lines, splitter.split(mergeText(lines, lineBreak)));
        // Windows line breaks.
        lineBreak = "\r\n";
        assertEquals(lines, splitter.split(mergeText(lines, lineBreak)));
        // Multiple line breaks.
        lineBreak = "\r\r\n\n";
        assertEquals(lines, splitter.split(mergeText(lines, lineBreak)));
        // Mixed line breaks.
        String test = firstLine + "\r" + secondLine + "\n" + thirdLine + "\r\n" + fourthLine;
        assertEquals(lines, splitter.split(test));
        // Ends with line breaks.
        test = firstLine + "\r" + secondLine + "\n" + thirdLine + "\r\n" + fourthLine + "\r\r\n\n";
        assertEquals(lines, splitter.split(test));
    }

    @Test
    public void keepEmptyLines() {
        LineSplitter splitter = new LineSplitter(true);
        String firstLine = "First line.";
        String secondLine = "";
        String thirdLine = "              ";
        String fourthLine = "Fourth line.";
        List<String> lines = Bags.arrayList(firstLine, secondLine, thirdLine, fourthLine);
        String lineBreak;
        assertEquals(Bags.arrayList(firstLine), splitter.split(firstLine));
        // Mac line breaks.
        lineBreak = "\r";
        assertEquals(lines, splitter.split(mergeText(lines, lineBreak)));
        // Unix line breaks.
        lineBreak = "\n";
        assertEquals(lines, splitter.split(mergeText(lines, lineBreak)));
        // Windows line breaks.
        lineBreak = "\r\n";
        assertEquals(lines, splitter.split(mergeText(lines, lineBreak)));
        // Mixed line breaks.
        String test = firstLine + "\n" + secondLine + "\r" + thirdLine + "\r\n" + fourthLine;
        assertEquals(lines, splitter.split(test));
    }

    private String mergeText(List<String> lines, String lineBreak) {
        StringBuilder buffer = new StringBuilder();
        lines.forEach(line -> buffer.append(line).append(lineBreak));
        if (buffer.length() > 0) {
            buffer.setLength(buffer.length() - lineBreak.length());
        }
        return buffer.toString();
    }
}