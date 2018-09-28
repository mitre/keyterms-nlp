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

package keyterms.nlp.languages.ukr;

import java.util.ArrayList;
import java.util.Set;

import keyterms.nlp.interfaces.INormalizer;
import keyterms.nlp.interfaces.IStemmer;
import keyterms.nlp.interfaces.ITextTransformer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;
import keyterms.nlp.transliterate.TransformKey;
import keyterms.nlp.transliterate.Transliterator;
import keyterms.nlp.transliterate.Transliterators;
import keyterms.util.text.Strings;

public class TextTransformer_Ukr
        implements ITextTransformer {

    public static final boolean REMOVE_SPACES_FOR_INDEX = true;
    protected static final String SRC_LANG = "ukr";
    protected static final String TRG_LANG = "eng";

    private INormalizer ukrNormalizer;
    private Transliterator icTransliterator;
    private Transliterator icAcronymTransliterator;
    private Transliterator bgnTransliterator;
    private IStemmer ukrStemmer;

    public TextTransformer_Ukr() {
        ukrNormalizer = new Normalizer_Ukr();
        //icTransliterator = new Transliterator_Ukr_Ic(ukrNormalizer);
        //bgnTransliterator = new Transliterator_Ukr_Bgn();

        // GET THE IC STANDARD TRANSLITERATOR
        Set<TransformKey> keys = Transliterators.getTransformKeys(SRC_LANG, TRG_LANG,
                TextType.KEY_TERMS.getLabel());
        if (keys != null && keys.size() > 0) {
            icTransliterator = Transliterators.get(keys.iterator().next());
        }

        // GET THE IC STANDARD TRANSLITERATOR
        keys = Transliterators.getTransformKeys(SRC_LANG, TRG_LANG,
                TextType.KEY_TERMS_ACRONYM.getLabel());
        if (keys != null && keys.size() > 0) {
            icTransliterator = Transliterators.get(keys.iterator().next());
        }

        ukrStemmer = new Stemmer_Ukr();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForDisplay(String input) {
        // NFKC Composed
        return ukrNormalizer.normalizeForDisplay(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForScoring(String input) {
        // NFKD Decomposed
        return ukrNormalizer.normalizeForScoring(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForIndex(String input) {
        //Script inputScript = ScriptIdentifier.guessScript(input, true);
        String normy = ukrNormalizer.normalizeForIndex(input, false);
        String normyStemmed = ukrStemmer.getStem(normy);
        if (Strings.isBlank(normyStemmed)) {
            normyStemmed = normy;
        }
        return ukrNormalizer.normalizeForIndex(normyStemmed, REMOVE_SPACES_FOR_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String transliterate(String input, TextType standard) {
        //@todo add the ICU Any to Latin transliterator as default
        switch (standard) {
            case KEY_TERMS:
                return icTransliterator.apply(input);
            case BGN:
                return bgnTransliterator.apply(input);
            default:
                return input;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Transliteration> getAvailableTransforms(String input, Language language) {
        if (input == null) {
            return null;
        }
        ArrayList<Transliteration> results = new ArrayList<>();
        Transliteration curXlit;

        String srcDisplayText = normalizeForDisplay(input);
        String srcIndexText = normalizeForIndex(input);

        String curText = srcDisplayText;
        String indexText = srcIndexText;
        curXlit = new Transliteration(true, 0, Script.CYRL.getCode(), TextType.ORIGINAL.getDisplayLabel(),
                curText, indexText);
        results.add(curXlit);

        // NB 11/20/2017: null checking to prevent NullPointerException (when transliterators are missing)
        if (icTransliterator != null) {
            curText = transliterateToKeyTerms(srcDisplayText);
            indexText = transliterateToKeyTerms(srcIndexText);
            curXlit = new Transliteration(false, 1, Script.LATN.getCode(), TextType.KEY_TERMS.getDisplayLabel(),
                    curText, indexText);
            results.add(curXlit);
        }

        if (bgnTransliterator != null) {
            curText = transliterateToBgn(srcDisplayText);
            indexText = transliterateToBgn(srcIndexText);
            curXlit = new Transliteration(false, 2, Script.LATN.getCode(), TextType.BGN.getDisplayLabel(),
                    curText, indexText);
            results.add(curXlit);
        }

        return results;
    }

    public String transliterateToKeyTerms(String input) {
        return icTransliterator.apply(input);
    }

    public String transliterateToBgn(String input) {
        return bgnTransliterator.apply(input);
    }
}