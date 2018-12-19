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

import java.util.ArrayList;

import org.junit.Test;

import keyterms.nlp.interfaces.TextTransformer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.languages.ara.TextTransformer_Ara;
import keyterms.nlp.languages.eng.TextTransformer_Eng;
import keyterms.nlp.languages.fra.TextTransformer_Fra;
import keyterms.nlp.languages.rus.TextTransformer_Rus;
import keyterms.nlp.languages.spa.TextTransformer_Spa;
import keyterms.nlp.languages.ukr.TextTransformer_Ukr;
import keyterms.nlp.languages.und.TextTransformer_Und;
import keyterms.nlp.languages.zho.TextTransformer_Zho;
import keyterms.nlp.model.Transliteration;

import static org.junit.Assert.assertTrue;

public class TextTransformerFactory_UT {

    @Test
    public void getTextTransformer() {
        TextTransformerFactory ttf = new TextTransformerFactory();

        assertTrue(ttf.getTransformer(Language.byCode("ara")) instanceof TextTransformer_Ara);
        assertTrue(ttf.getTransformer(Language.byCode("eng")) instanceof TextTransformer_Eng);
        assertTrue(ttf.getTransformer(Language.byCode("fra")) instanceof TextTransformer_Fra);
        assertTrue(ttf.getTransformer(Language.byCode("rus")) instanceof TextTransformer_Rus);
        assertTrue(ttf.getTransformer(Language.byCode("spa")) instanceof TextTransformer_Spa);
        assertTrue(ttf.getTransformer(Language.byCode("ukr")) instanceof TextTransformer_Ukr);
        assertTrue(ttf.getTransformer(Language.byCode("zho")) instanceof TextTransformer_Zho);
        assertTrue(ttf.getTransformer(Language.byCode("und")) instanceof TextTransformer_Und);
        assertTrue(ttf.getTransformer(Language.byCode("fas")) instanceof TextTransformer_Und);
    }

    @Test
    public void TextTransformer_Ukraininian() {
        TextTransformerFactory ttf = new TextTransformerFactory();
        TextTransformer tt = ttf.getTransformer(Language.byCode("ukr"));
        ArrayList<Transliteration> results = tt.getAvailableTransforms("ці великі червоні бібліотечні книги є моїми");
        for(Transliteration curXlit : results) {
            System.out.println(curXlit.toString());
        }
        assertTrue(results.size() ==3);
    }

    @Test
    public void TextTransformer_Undefined() {
        TextTransformerFactory ttf = new TextTransformerFactory();
        TextTransformer tt = ttf.getTransformer(Language.byCode("und"));
        ArrayList<Transliteration> results = tt.getAvailableTransforms("los libros rojos son mios");
        for(Transliteration curXlit : results) {
            System.out.println(curXlit.toString());
        }
        assertTrue(results.size() ==1);

        tt = ttf.getTransformer(Language.byCode("und"));
        results = tt.getAvailableTransforms("ремонтно-восстановительные работы");
        for(Transliteration curXlit : results) {
            System.out.println(curXlit.toString());
        }
        assertTrue(results.size() ==2);
    }
}