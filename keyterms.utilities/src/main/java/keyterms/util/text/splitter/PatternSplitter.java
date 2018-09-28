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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import keyterms.util.text.TextSplitter;

/**
 * A text splitter which breaks on a general regular expression.
 */
public class PatternSplitter
        implements TextSplitter {
    /**
     * The delimiter pattern.
     */
    private final Pattern delimiterPattern;

    /**
     * Constructor.
     *
     * @param delimiter The delimiter expression.
     */
    public PatternSplitter(String delimiter) {
        this(Pattern.compile(delimiter));
    }

    /**
     * Constructor.
     *
     * @param pattern The delimiter pattern.
     */
    PatternSplitter(Pattern pattern) {
        super();
        if (pattern == null) {
            throw new IllegalArgumentException("Text splitter pattern is required.");
        }
        delimiterPattern = pattern;
    }

    /**
     * Get the delimiter pattern expression.
     *
     * @return The delimiter pattern expression.
     */
    public Pattern getPattern() {
        return delimiterPattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> split(CharSequence text) {
        ArrayList<String> splits = new ArrayList<>();
        if (text != null) {
            int lastStart = 0;
            Matcher matcher = delimiterPattern.matcher(text);
            while (matcher.find()) {
                int start = matcher.start();
                splits.add(text.subSequence(lastStart, start).toString());
                lastStart = matcher.end();
            }
            if (lastStart < text.length()) {
                splits.add(text.subSequence(lastStart, text.length()).toString());
            }
        }
        return splits;
    }
}