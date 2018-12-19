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

import keyterms.nlp.interfaces.TextTransformer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;

public class TextTransformer_Rus
        extends TextTransformer {

    private static final Language SRC_LANG = Language.RUSSIAN;
    private static final Language TRG_LANG = Language.ENGLISH;

    public TextTransformer_Rus() {
        this(SRC_LANG,TRG_LANG);
    }

    public TextTransformer_Rus(Language source, Language target) {
        super(source,target);
    }

    @Override
    protected void initializeNormalizer() {
        normalizer = new Normalizer_Rus();
    }

    @Override
    protected void initializeStemmer() {
        stemmer = new Stemmer_Rus();
    }

    protected void initializeTransliterators() {

        // GET THE IC STANDARD TRANSLITERATOR
        addTransliterator(source.getCode(),target.getCode(), TextType.KEY_TERMS.getLabel(),
                TextType.KEY_TERMS.getDisplayLabel());

        // GET THE IC STANDARD ACRONYM TRANSLITERATOR- should be used as an alternate when terms are acronyms
        //addTransliterator(source.getCode(), target.getCode(), TextType.KEY_TERMS_ACRONYM.getLabel(),"");

        // GET THE BGN STANDARD TRANSLITERATOR
        addTransliterator(source.getCode(), Script.LATN.toString(), TextType.BGN.getLabel(),
                TextType.BGN.getDisplayLabel());


    }
}
