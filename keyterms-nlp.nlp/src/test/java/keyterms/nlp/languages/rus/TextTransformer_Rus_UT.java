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

import org.junit.Ignore;
import org.junit.Test;

import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextTransformer_Rus_UT {

    private static final TextTransformer_Rus TT_RUS = new TextTransformer_Rus();

    private static final String normalizationInput = "Земля - это третья планета от Солнца и единственный объект во " +
            "Вселенной, который, как известно, питает жизнь.";

    private static final String keyTermsTransliteration =
            "Zemlya - eto tretya planeta ot Solntsa i yedinstvennyй obekt vo Vselennoй, kotoryй, kak " +
                    "izvestno, pitayet zhizn.";

    private static final String gostTransliteration = ""; // insert GOST transliteration of normalizationInput here
    private static final String bgnTransliteration = ""; // insert BGN transliteration of normalization input here

    @Test
    public void prepareIndexForm() {
        /*
         * newlines removed? yep!
         * spaces removed? yep!
         * control characters removed? yep!
         * punctuation removed? yep!
         * punctuation normalized? doesn't matter since it's removed!
         * punctuation transliterated? doesn't matter since it's removed!
         * diacritics removed? yep!
         * case normalized? yep!
         * normalized to NFKD? yep!
         * stemmed? yep!
         */

        // normalized, then stemmed, then normalized without spaces
        String expected = "земл эт трет планет солнц единственны объект вселенно которы известн пита";
        assertEquals(expected, TT_RUS.prepareIndexForm(normalizationInput));
    }

    @Test
    public void getAvailableTransforms() {
        try {
            List<Transliteration> result = TT_RUS.getAvailableTransforms(normalizationInput);
            for(Transliteration curXlit : result) {
                System.out.println(curXlit.toString());
            }
            assertTrue(result.size() == 3);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}