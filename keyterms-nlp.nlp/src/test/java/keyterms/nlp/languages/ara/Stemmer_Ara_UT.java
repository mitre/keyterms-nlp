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

package keyterms.nlp.languages.ara;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Stemmer_Ara_UT {

    private static final Stemmer_Ara STEM_ARA = new Stemmer_Ara();

    private static final String[] INPUT = {
            "الكلاب", // dogs
            "تشغيل", // running
            "متهيج", // blustery
            "يوم", // day
            "جدا", // very
            "ممتع", // enjoyable
            "بلا معنى", // meaningless
            "اجازة", // a vacation
            "على", // on
            "ال", // the
            "الجبلي" // mountain
    };

    private static final String[] EXPECTED = {
            "كلاب",
            "تشغيل",
            "متهيج",
            "يوم",
            "جدا",
            "ممتع",
            "بلا معن",
            "اجاز",
            "",
            "ال",
            "جبل"
    };

    @Test
    public void getStem() {
        String[] actual = new String[INPUT.length];

        for (int i = 0; i < INPUT.length; i++) {
            actual[i] = STEM_ARA.getStem(INPUT[i]);
        }

        assertArrayEquals(EXPECTED, actual);
    }
}