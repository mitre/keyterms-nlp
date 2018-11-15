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

package keyterms.analyzers.cld2;

import java.util.List;

import org.junit.Test;

import keyterms.nlp.iso.Script;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.collect.Bags;
import keyterms.util.text.Strings;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class CldUtil_UT {

    @Test
    public void nativeLangCodes() {
        //@todo what to do with these ?
        List<String> skip = Bags.staticList("un", "xxx", "bh", "xx-qaai", "xx-zyyy");
        // bh = BIHARI which is a macro language with no mapping to ISO-639-3 ?
        // Also MONTENEGRIN has a script code of ME which can't be resolved
        for (Cld2Language language : Cld2Language.values()) {
            String code = language.getCode().toLowerCase();
            if ((!Strings.isBlank(code)) && (!skip.contains(code)) && (!code.startsWith("zz"))) {
                WrittenLanguage written = CldUtil.toWrittenLanguage(code);
                assertNotNull(language.toString(), written);
                if (code.startsWith("xx-")) {
                    assertNotNull(language + " script", written.getScript());
                } else {
                    assertNotNull(language + " lang", written.getLanguage());
                }
            }
        }
    }

    @Test
    public void nativeScriptCodes() {
        List<String> skip = Bags.staticList("zyyy", "zinh");
        for (Cld2Script script : Cld2Script.values()) {
            String code = script.getCode().toLowerCase();
            if ((!Strings.isBlank(code)) && (!skip.contains(code))) {
                assertNotNull(script.toString(), Script.byCode(script.getCode()));
            }
        }
    }

    @Test
    public void commonLanguages() {
        Bags.staticList(
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
        ).forEach((written) ->
                assertNotEquals(written.toString(), CldUtil.UNKNOWN_LANGUAGE, CldUtil.getCldLanguage(written))
        );
    }
}