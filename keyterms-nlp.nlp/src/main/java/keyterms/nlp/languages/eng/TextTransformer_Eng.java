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

package keyterms.nlp.languages.eng;

import java.util.ArrayList;
import java.util.List;

import keyterms.nlp.interfaces.INormalizer;
import keyterms.nlp.interfaces.IStemmer;
import keyterms.nlp.interfaces.ITextTransformer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;
import keyterms.util.text.Strings;

public class TextTransformer_Eng
        implements ITextTransformer {

    private static final boolean REMOVE_SPACES_FOR_INDEX = true;

    private final INormalizer engNormalizer;
    private final IStemmer engStemmer;

    public TextTransformer_Eng() {

        engNormalizer = new Normalizer_Eng();
        engStemmer = new Stemmer_Eng();
    }

    /**
     * Normalize method of the INormalizer interface
     */
    public String normalizeForDisplay(String input) {
        return engNormalizer.normalizeForDisplay(input);
    }

    public String normalizeForScoring(String input) {
        return engNormalizer.normalizeForScoring(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForIndex(String input) {
        //ScriptCode inputScript = ScriptIdentifier.guessScript(input, true);
        String normy = engNormalizer.normalizeForIndex(input, false);
        String normyStemmed = engStemmer.getStem(normy);
        if (Strings.isBlank(normyStemmed)) {
            normyStemmed = normy;
        }
        return engNormalizer.normalizeForIndex(normyStemmed, REMOVE_SPACES_FOR_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String transliterate(String input, TextType standard) {
        return input;
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

        String displayText = normalizeForDisplay(input);
        String indexText = normalizeForIndex(input);
        curXlit = new Transliteration(true, 0, Script.LATN.getCode(), TextType.ORIGINAL.getDisplayLabel(),
                displayText, indexText);
        results.add(curXlit);
        return results;
    }

    public List<String> getTransliterationCandidates(String input, TextType standard) {
        List<String> candidates = new ArrayList<>();
        candidates.add(input);
        return candidates;
    }
}