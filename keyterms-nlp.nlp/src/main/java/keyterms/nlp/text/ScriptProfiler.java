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

package keyterms.nlp.text;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.ibm.icu.text.Transliterator;

import keyterms.nlp.iso.Script;
import keyterms.util.math.Statistics;

/**
 * A simple script profiler.
 */
public class ScriptProfiler {
    /**
     * A transliterator for Chinese traditional to Chinese simplified script.
     */
    private static final Transliterator TO_HANT = Transliterator.getInstance("Simplified-Traditional");

    /**
     * A filter used to ignore space characters.
     */
    public static final Predicate<Integer> IGNORE_SPACE_CHARACTERS = Character::isSpaceChar;

    /**
     * A filter used to ignore latin numbers.
     */
    public static final Predicate<Integer> IGNORE_LATIN_NUMBERS = (codePoint) ->
            Characters.isLatinDigit(Character.toChars(codePoint));

    /**
     * A filter used to ignore latin punctuation.
     */
    public static final Predicate<Integer> IGNORE_LATIN_PUNCTUATION = (codePoint) ->
            ((Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.LATIN) &&
                    (Characters.isPunctuation(Character.toChars(codePoint))));

    /**
     * The filters used to treat specified code points as "common" characters.
     */
    private final Collection<Predicate<Integer>> codePointFilters;

    /**
     * Constructor.
     */
    public ScriptProfiler() {
        this(Arrays.asList(IGNORE_SPACE_CHARACTERS, IGNORE_LATIN_NUMBERS, IGNORE_LATIN_PUNCTUATION));
    }

    /**
     * Constructor.
     *
     * @param codePointFilters The filters used to treat specified code points as "common" characters.
     */
    public ScriptProfiler(Collection<Predicate<Integer>> codePointFilters) {
        super();
        this.codePointFilters = codePointFilters;
    }

    /**
     * Create a character script profile for the specified text.
     *
     * @param text the text to profile.
     *
     * @return A script profile of the specified text.
     */
    public ScriptProfile profile(String text) {
        int codePoints = 0;
        Map<Script, WorkingEntry> workingEntries = new HashMap<>();
        workingEntries.put(Script.COMMON, new WorkingEntry());
        if (text != null) {
            WorkingEntry lastEntry = null;
            Script haniScript = null;
            for (int c = 0; c < text.length(); c++) {
                codePoints++;
                int codePoint = text.codePointAt(c);
                // For multi-character code points advance the loop counter.
                int codePointCharCount = Character.charCount(codePoint);
                c += (codePointCharCount - 1);
                Character.UnicodeScript unicodeScript = Character.UnicodeScript.of(codePoint);
                Script script = Script.valueOf(unicodeScript);
                boolean ignore = ((codePointFilters != null) &&
                        (codePointFilters.stream().anyMatch((filter) -> filter.test(codePoint))));
                if (ignore) {
                    script = Script.COMMON;
                }
                if ((Script.HANT.equals(script)) || (Script.HANS.equals(script))) {
                    if (haniScript == null) {
                        String s1 = text.substring(c, c + codePointCharCount);
                        String s2 = TO_HANT.transliterate(s1);
                        haniScript = (s1.equals(s2)) ? Script.HANT : Script.HANS;
                    }
                    script = haniScript;
                }
                WorkingEntry entry = workingEntries.computeIfAbsent(script, (s) -> new WorkingEntry());
                entry.increment(c, lastEntry);
                lastEntry = entry;
            }
            if (lastEntry != null) {
                lastEntry.closeRun();
            }
        }
        return new ScriptProfile(codePoints, workingEntries);
    }

    /**
     * A working entry for the script profiler.
     */
    static class WorkingEntry {
        /**
         * The character index of the first code point from the script in the analyzed text.
         */
        Integer firstIndex;

        /**
         * The number of code points in the text that are members of the associated script.
         */
        int codePoints = 0;

        /**
         * The current count of sequential code points from the associated script.
         */
        int runCount = 0;

        /**
         * The statistics on run size (sequential code points from the associated script).
         */
        final Statistics runStats = new Statistics();

        /**
         * Constructor.
         */
        private WorkingEntry() {
            super();
        }

        /**
         * Increment the number of characters that are members of the associated script.
         *
         * @param last The prior entry.
         */
        private void increment(int c, WorkingEntry last) {
            if (firstIndex == null) {
                firstIndex = c;
            }
            codePoints++;
            runCount++;
            if ((last != null) && (this != last)) {
                last.closeRun();
            }
        }

        /**
         * Close out the current sequential run.
         */
        private void closeRun() {
            if (runCount > 0) {
                runStats.add(runCount);
                runCount = 0;
            }
        }
    }
}