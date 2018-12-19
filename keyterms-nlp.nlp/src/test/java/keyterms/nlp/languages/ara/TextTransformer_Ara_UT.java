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

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TextTransformer_Ara_UT {

    private static final TextTransformer_Ara TT_ARA = new TextTransformer_Ara();

    private static final String NORMALIZATION_INPUT =
            "الأرض هي الكوكب الثالث من الشمس والكائن الوحيد في الكون المعروف أن يؤوي الحياة.";
    private static final String KEY_TERMS_TRANSLITERATION = "ala'rd hi alkukb althalth mn alshms walka'n aluhid fi " +
            "alkun alm'ruf a'n yu'wy alhyah.";
    private static final String BGN_TRANSLITERATION = "alarḑ hi lkwkb lthalth mn alshms walkan lwḩid fi alkwn lm‘rwf aٔn ywٔy alḩiah"; // insert BGN transliteration of normalization input here

    @Test
    public void getAvailableTransforms() {
        try {
            List<Transliteration> result = TT_ARA.getAvailableTransforms(NORMALIZATION_INPUT);
            for(Transliteration curXlit : result) {
                System.out.println(curXlit.toString());
            }
            assertTrue(result.size() ==2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}