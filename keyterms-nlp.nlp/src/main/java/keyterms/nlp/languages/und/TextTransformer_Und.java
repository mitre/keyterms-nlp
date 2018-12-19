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

import keyterms.nlp.interfaces.TextTransformer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;

public class TextTransformer_Und
        extends TextTransformer {

    private static final Language SRC_LANG = Language.UND;
    private static final Language TRG_LANG = Language.ENGLISH;
    private static final String srcLang = "Any";

    public TextTransformer_Und() {
        this(SRC_LANG,TRG_LANG);
    }

    public TextTransformer_Und(Language source, Language target) {
        super(source,target);
    }

    @Override
    protected void initializeNormalizer() {
        normalizer = new Normalizer_Und();
    }

    @Override
    protected void initializeStemmer() {
        stemmer = null;
    }

    protected void initializeTransliterators() {

        // GET THE IC STANDARD TRANSLITERATOR
       // addTransliterator(srcLang, Script.LATN.toString(), "","ICU transliteration");

         // GET THE BGN STANDARD TRANSLITERATOR
        addTransliterator(srcLang, Script.LATN.toString(), TextType.BGN.getLabel(),TextType.BGN.getDisplayLabel());
    }



}

//XXX TO DO:  figure out why this chineseTransformer was needed in this original class -
// to make sure simplification happens perhaps?
/*
@Override
public String normalizeForIndex(String input) {
    String idxNormalized = undNormalizer.normalizeForIndex(input, REMOVE_SPACES_FOR_INDEX);
    if (chineseTransformer != null) {
        idxNormalized = chineseTransformer.normalizeForIndex(idxNormalized);
    }
    return idxNormalized;
}
*/