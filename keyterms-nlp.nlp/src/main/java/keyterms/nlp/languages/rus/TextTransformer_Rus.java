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

public class TextTransformer_Rus
        implements ITextTransformer {

    public static final boolean REMOVE_SPACES_FOR_INDEX = false;
    protected static final String SRC_LANG = "rus";
    protected static final String TRG_LANG = "eng";

    private final INormalizer rusNormalizer;
    private Transliterator icTransliterator;
    private Transliterator gostTransliterator;
    private Transliterator bgnTransliterator;
    private final IStemmer rusStemmer;

    public TextTransformer_Rus() {
        rusNormalizer = new Normalizer_Rus();

        // GET THE IC STANDARD TRANSLITERATOR
        Set<TransformKey> keys = Transliterators.getTransformKeys(SRC_LANG, TRG_LANG,
                TextType.KEY_TERMS.getLabel());
        if (keys != null && keys.size() > 0) {
            icTransliterator = Transliterators.get(keys.iterator().next());
        }

        // GET THE GOST STANDARD TRANSLITERATOR
        keys = Transliterators.getTransformKeys(SRC_LANG, TRG_LANG,
                TextType.GOST.getLabel());
        if (keys != null && keys.size() > 0) {
            gostTransliterator = Transliterators.get(keys.iterator().next());
        }

        // GET THE BGN STANDARD TRANSLITERATOR
        keys = Transliterators.getTransformKeys(SRC_LANG, TRG_LANG,
                TextType.BGN.getLabel());
        if (keys != null && keys.size() > 0) {
            bgnTransliterator = Transliterators.get(keys.iterator().next());
        }

        // CREATE THE RUSSIAN STEMMER
        rusStemmer = new Stemmer_Rus();
    }

    // NFKC Composed
    @Override
    public String normalizeForDisplay(String input) {
        return rusNormalizer.normalizeForDisplay(input);
    }

    // NFKD Decomposed
    @Override
    public String normalizeForScoring(String input) {
        return rusNormalizer.normalizeForScoring(input);
    }

    // // NFKD Decomposed and stripped of punct, etc.
    @Override
    public String normalizeForIndex(String input) {
        //Script inputScript = ScriptIdentifier.guessScript(input, true);
        String normy = rusNormalizer.normalizeForIndex(input, false);
        String normyStemmed = rusStemmer.getStem(normy);
        if (Strings.isBlank(normyStemmed)) {
            normyStemmed = normy;
        }
        return rusNormalizer.normalizeForIndex(normyStemmed, REMOVE_SPACES_FOR_INDEX);
    }

    //@todo add the ICU Any to Latin transliterator as default
    @Override
    public String transliterate(String input, TextType standard) {
        switch (standard) {

            case KEY_TERMS:
                if ( icTransliterator!=null )
                    return icTransliterator.apply(input);
            case GOST:
                if ( gostTransliterator!=null )
                    return gostTransliterator.apply(input);
            case BGN:
                if ( bgnTransliterator!=null )
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
        curXlit =
                new Transliteration(true, 0, Script.CYRL.getCode(), TextType.ORIGINAL.getDisplayLabel(), curText,
                        indexText);
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
            curXlit =
                    new Transliteration(false, 2, Script.LATN.getCode(), TextType.BGN.getDisplayLabel(), curText,
                            indexText);
            results.add(curXlit);
        }

        if (gostTransliterator != null) {
            curText = transliterateToGost(srcDisplayText);
            indexText = transliterateToGost(srcIndexText);
            curXlit = new Transliteration(false, 3, Script.LATN.getCode(), TextType.GOST.getDisplayLabel(),
                    curText,
                    indexText);
            results.add(curXlit);
        }

        return results;
    }

    public String stemForRomanizationForIndex(String input) {
        String normy = rusNormalizer.normalizeForIndex(input, false);
        return rusStemmer.getStem(normy);
    }

    public String transliterateToKeyTerms(String input) {
        if(icTransliterator!=null)
            return icTransliterator.apply(input);
        return "";
    }

    public String transliterateToGost(String input) {
        if(gostTransliterator!=null)
            return gostTransliterator.apply(input);
        return "";
    }

    public String transliterateToBgn(String input) {
        if(bgnTransliterator!=null)
            return bgnTransliterator.apply(input);
        return "";
    }
}