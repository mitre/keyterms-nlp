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

package keyterms.nlp.factories;

import org.junit.Test;

import keyterms.nlp.iso.Language;
import keyterms.nlp.languages.ara.MorphAnalyzer_Ara;
import keyterms.nlp.languages.eng.MorphAnalyzer_Eng;
import keyterms.nlp.languages.rus.MorphAnalyzer_Rus;
import keyterms.nlp.languages.ukr.MorphAnalyzer_Ukr;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MorphAnalyzerFactory_UT {

    @Test
    public void getMorphAnalyzer() {
        MorphAnalyzerFactory maf = new MorphAnalyzerFactory();

        assertTrue(maf.getMorphAnalyzer(Language.byCode("ara")) instanceof MorphAnalyzer_Ara);
        assertTrue(maf.getMorphAnalyzer(Language.byCode("eng")) instanceof MorphAnalyzer_Eng);
        assertTrue(maf.getMorphAnalyzer(Language.byCode("rus")) instanceof MorphAnalyzer_Rus);
        assertTrue(maf.getMorphAnalyzer(Language.byCode("ukr")) instanceof MorphAnalyzer_Ukr);
        assertNull(maf.getMorphAnalyzer(Language.byCode("zho")));
        assertNull(maf.getMorphAnalyzer(Language.byCode("und")));
    }
}