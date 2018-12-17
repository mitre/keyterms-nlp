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

public class TextTransformer_Ara
        implements ITextTransformer {

    public static final boolean REMOVE_SPACES_FOR_INDEX = false;

    private final INormalizer araNormalizer;
    private Transliterator icTransliterator;
    private Transliterator bgnTransliterator;
    private final IStemmer araStemmer;

    public TextTransformer_Ara() {
        araNormalizer = new Normalizer_Ara();

        Set<TransformKey> keys = Transliterators.getTransformKeys(
                Language.ARABIC.getCode(),
                Language.ENGLISH.getCode(),
                TextType.KEY_TERMS.getLabel());
        if (keys != null && keys.size() > 0) {
            icTransliterator = Transliterators.get(keys.iterator().next());
        }

        keys = Transliterators.getTransformKeys(
                Language.ARABIC.getCode(),
                Script.LATN.getCode(),
                TextType.BGN.getLabel());
        if (keys != null && keys.size() > 0) {
            bgnTransliterator = Transliterators.get(keys.iterator().next());
        }

        araStemmer = new Stemmer_Ara();
    }

    // NFKC Composed
    @Override
    public String normalizeForDisplay(String input) {
        return araNormalizer.normalizeForDisplay(input);
    }

    // NFKD Decomposed
    @Override
    public String normalizeForScoring(String input) {
        return araNormalizer.normalizeForScoring(input);
    }

    // // NFKD Decomposed and stripped of punct, etc.
 /*   @Override
    public String normalizeForIndex(String input) {
        return araNormalizer.normalizeForIndex(input, REMOVE_SPACES_FOR_INDEX);
    }
*/
    @Override
    public String normalizeForIndex(String input) {
        //Script inputScript = ScriptIdentifier.guessScript(input, true);
        String normy = araNormalizer.normalizeForIndex(input, false);
        String normyStemmed = araStemmer.getStem(normy);
        if (Strings.isBlank(normyStemmed)) {
            normyStemmed = normy;
        }
        return araNormalizer.normalizeForIndex(normyStemmed, REMOVE_SPACES_FOR_INDEX);
    }

    //@todo add the ICU Any to Latin transliterator as default
    @Override
    public String transliterate(String input, TextType standard) {
        switch (standard) {
            case KEY_TERMS:
                return icTransliterator.apply(input);
            case BGN:
                return bgnTransliterator.apply(input);
            default:
                return "";
        }
    }

    public String transliterateToKeyTerms(String input) {
        return icTransliterator.apply(input);
    }

    public String transliterateToBgn(String input) {
        String bgnOutput = bgnTransliterator.apply(input);
        return icTransliterator.apply(bgnOutput);
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
        curXlit = new Transliteration(true, 0, Script.ARAB.getCode(), TextType.ORIGINAL.getDisplayLabel(),
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
}