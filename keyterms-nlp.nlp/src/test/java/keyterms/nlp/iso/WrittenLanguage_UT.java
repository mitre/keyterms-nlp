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

package keyterms.nlp.iso;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import keyterms.util.collect.Bags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class WrittenLanguage_UT {

    public static List<WrittenLanguage> getCommonWrittenLanguages() {
        return Bags.arrayList(
                WrittenLanguage.ARABIC,
                WrittenLanguage.BULGARIAN,
                WrittenLanguage.CHINESE_SIMPLIFIED,
                WrittenLanguage.CHINESE_TRADITIONAL,
                WrittenLanguage.CROATIAN,
                WrittenLanguage.CZECH,
                WrittenLanguage.DANISH,
                WrittenLanguage.DUTCH,
                WrittenLanguage.ENGLISH,
                WrittenLanguage.ESTONIAN,
                WrittenLanguage.FINNISH,
                WrittenLanguage.FRENCH,
                WrittenLanguage.GERMAN,
                WrittenLanguage.GREEK,
                WrittenLanguage.HUNGARIAN,
                WrittenLanguage.IRISH,
                WrittenLanguage.ITALIAN,
                WrittenLanguage.JAPANESE,
                WrittenLanguage.KOREAN,
                WrittenLanguage.LATVIAN,
                WrittenLanguage.LITHUANIAN,
                WrittenLanguage.MALTESE,
                WrittenLanguage.POLISH,
                WrittenLanguage.PORTUGUESE,
                WrittenLanguage.ROMANIAN,
                WrittenLanguage.RUSSIAN,
                WrittenLanguage.SLOVAK,
                WrittenLanguage.SLOVENIAN,
                WrittenLanguage.SPANISH,
                WrittenLanguage.SWEDISH
        );
    }

    @Test
    public void hashes() {
        Set<WrittenLanguage> languages = Bags.staticSet(
                WrittenLanguage.ENGLISH,
                new WrittenLanguage(Language.ENGLISH, null),
                new WrittenLanguage(null, null)
        );
        assertEquals(3, languages.size());
    }

    @Test
    public void scriptAdjustments() {
        getCommonWrittenLanguages().forEach(written -> {
            if (!written.getLanguage().equals(Language.CHINESE)) {
                assertSame(written, written.withPreferredScript());
            }
            WrittenLanguage noScript = written.withNoScript();
            assertNull(noScript.getScript());
            WrittenLanguage withDefault = noScript.withPreferredScript();
            if (!written.getLanguage().equals(Language.CHINESE)) {
                assertEquals(written.getScript(), withDefault.getScript());
            }
            WrittenLanguage withAltScript = new WrittenLanguage(written.getLanguage(), Script.HAN_TRADITIONAL);
            withDefault = withAltScript.withAssumedScript();
            assertSame(withAltScript, withDefault);
            withDefault = noScript.withAssumedScript();
            if (!written.getLanguage().equals(Language.CHINESE)) {
                assertNotNull(withDefault.getScript());
            }
        });
    }

    @Test
    public void locale() {
        for (Language language : Language.values()) {
            WrittenLanguage writtenLanguage = new WrittenLanguage(language, null);
            Locale locale = writtenLanguage.getLocale();
            assertNotNull(locale);
        }
        for (Script script : Script.values()) {
            WrittenLanguage writtenLanguage = new WrittenLanguage(null, script);
            Locale locale = writtenLanguage.getLocale();
            assertNotNull(locale);
        }
    }
}