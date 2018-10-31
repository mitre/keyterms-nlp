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

package keyterms.nlp.languages.rus;

import java.util.List;

import org.junit.Test;

import keyterms.nlp.model.WordForm;

import static org.junit.Assert.assertArrayEquals;

public class MorphAnalyzer_Rus_UT {

    private static final MorphAnalyzer_Rus MORPH_RUS = new MorphAnalyzer_Rus();

    private static final String[] INPUT = {
            "собаки", // dogs
            "бегут", // are running
            "ветреная", // windy
            "день", // day
            "очень", // very
            "приятный", // pleasant
            "бессмысленный", // meaningless
            "каникул", // vacations
            "на", // on
            "гора" // mountain
    };

    private static final String[] EXPECTED = {
            "собак",
            "бегут",
            "ветрен",
            "ден",
            "очен",
            "приятн",
            "бессмыслен",
            "каникул",
            "на",
            "гор"
    };

    @Test
    public void getWordForms() {
        String[] actual = new String[INPUT.length];

        for (int i = 0; i < INPUT.length; i++) {
            List<WordForm> results = MORPH_RUS.getWordForms(INPUT[i]);
            if (results.size() == 1) {
                actual[i] = results.get(0).getText();
            }
        }

        assertArrayEquals(EXPECTED, actual);
    }
}