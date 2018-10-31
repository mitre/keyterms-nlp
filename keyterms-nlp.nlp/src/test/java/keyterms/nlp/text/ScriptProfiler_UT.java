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

import org.junit.Test;

import keyterms.nlp.iso.Script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScriptProfiler_UT {

    @Test
    public void blankText() {
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(null);
        testBlankProfile(profile);
        profile = profiler.profile("");
        testBlankProfile(profile);
    }

    private void testBlankProfile(ScriptProfile profile) {
        assertNotNull(profile);
        assertNotEquals(0, profile.getEntries().size());
        assertEquals(Script.COMMON, profile.getScript());
        ScriptProfile.Entry entry = profile.getEntry(Script.COMMON);
        assertNotNull(entry);
        assertEquals(0, entry.getCodePoints());
        assertEquals(-1, entry.getFirstIndex());
        assertEquals(0, entry.getRunCount());
        assertEquals(0, entry.getMinimumRunSize());
        assertEquals(0, entry.getMeanRunSize(), 0);
        assertEquals(0, entry.getMaximumRunSize());
        assertEquals(0, entry.getCodePointPercentage(), 0);
        assertEquals(0, entry.getKnownCodePointPercentage(), 0);
    }

    @Test
    public void arabicURLS() {
        String input = "http://www.google.com , t نهفشلا";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.LATN, profile.getScript());
        assertEquals(Script.ARAB, profile.getScript(true, true));
        assertTrue(profile.containsLatin());
        assertTrue(profile.containsNonLatin());
        assertTrue(profile.contains(Script.ARAB));
        assertFalse(profile.containsCJK());
    }

    @Test
    public void arabicChineseURLS() {
        String input = "http://www.google.com , t 个个个个个个个个个نهفشلا";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.LATN, profile.getScript());
        assertEquals(Script.HANS, profile.getScript(true));
        assertTrue(profile.containsLatin());
        assertTrue(profile.containsNonLatin());
        assertTrue(profile.contains(Script.ARAB));
        assertTrue(profile.containsCJK());
    }

    @Test
    public void arabicChinese_equal() {
        String input = "http://www.google.com , t 个个个نهفشلا";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.LATN, profile.getScript());
        assertTrue(profile.contains(Script.ARAB));
        assertTrue(profile.contains(Script.HANS));
        assertEquals(Script.ARAB, profile.getScript(true, true));
    }

    @Test
    public void getScriptProfile() {
        String input = "http://www.google.com , t 个نهفشلا";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.LATN, profile.getScript());
        assertEquals(Script.ARAB, profile.getScript(true, true));
    }

    @Test
    public void chineseSimplified() {
        String input = " , t 个";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.LATN, profile.getScript());
        assertEquals(Script.HANS, profile.getScript(true));
    }

    @Test
    public void chineseSimplifiedFirst() {
        String input = "个 t , ";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.HANS, profile.getScript());
    }

    @Test
    public void arabic() {
        String input = " , t نهفشلا";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.ARAB, profile.getScript());
    }

    @Test
    public void arabicLatinEqually() {
        String input = "t " + "ن";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.LATN, profile.getScript());
        assertEquals(Script.ARAB, profile.getScript(true));
    }

    @Test
    public void arabicLatinEquallyArabicFirst() {
        String input = "ن" + " t";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.ARAB, profile.getScript());
    }

    @Test
    public void arabicMoreLatin() {
        String input = "top ن";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.LATN, profile.getScript(true));
    }

    @Test
    public void arabicMoreLatin_preferNonLatin() {
        String input = "to ن";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.ARAB, profile.getScript(true));
    }

    @Test
    public void latinOnly() {
        String input = "The quick brown fox.";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertTrue(profile.containsLatin());
        assertFalse(profile.containsNonLatin());
    }

    @Test
    public void hasNonLatin() {
        String input = "to ن";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertTrue(profile.containsLatin());
        assertTrue(profile.containsNonLatin());
    }

    @Test
    public void hasArabic() {
        String input = "to ن";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertTrue(profile.contains(Script.ARAB));
    }

    @Test
    public void hasCJK() {
        String input = "to 个 ззг ";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertFalse(profile.contains(Script.ARAB));
        assertTrue(profile.containsCJK());
    }

    @Test
    public void hasCyrillic() {
        String input = "to 个 ззг ";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertTrue(profile.contains(Script.CYRL));
    }

    @Test
    public void hantOnly() {
        String input = "這是希望在中文簡體字中的一個句子。";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.HANT, profile.getScript());
        assertFalse(profile.contains(Script.HANS));
    }

    @Test
    public void hansOnly() {
        String input = "这是希望在中文简体字中的一个句子。";
        ScriptProfiler profiler = new ScriptProfiler();
        ScriptProfile profile = profiler.profile(input);
        assertEquals(Script.HANS, profile.getScript());
        assertFalse(profile.contains(Script.HANT));
    }
}