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

package keyterms.nlp.languages.und;

import java.util.List;

import org.junit.Test;

import keyterms.nlp.model.Transliteration;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextTransformer_Und_UT {

    private static TextTransformer_Und ttUnd = new TextTransformer_Und();

    private static final String normalizationInput = "La Tierra es el tercer planeta del Sol y el único objeto en el " +
            "Universo conocido por albergar vida.";
    private static final String bgnTransliteration = "La Tierra es el tercer planeta del Sol y el único objeto en el " +
            "Universo conocido por albergar vida.";


    @Test
    public void getAvailableTransliterations() {

        try {
            // Latin input
            List<Transliteration> result = ttUnd.getAvailableTransforms(normalizationInput);
            for(Transliteration curXlit : result) {
                System.out.println(curXlit.toString());
            }
            assertTrue(result.size() == 1);

            // Ukrainian input, no language code
            String ukrInput = "Земля - це третя планета від Сонця та єдиний об'єкт у Всесвіті, відомий для життя.";
            for(Transliteration curXlit : result) {
                System.out.println(curXlit.toString());
            }
            result = ttUnd.getAvailableTransforms(ukrInput);
            assertTrue(result.size() == 2);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}