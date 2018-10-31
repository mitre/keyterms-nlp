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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.ibm.icu.text.Transliterator;

import keyterms.nlp.interfaces.INormalizer;
import keyterms.nlp.interfaces.ITextTransformer;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;
import keyterms.nlp.text.ScriptProfiler;
import keyterms.util.text.Strings;

public class TextTransformer_Und
        implements ITextTransformer {

    private static final boolean REMOVE_SPACES_FOR_INDEX = true;

    private static final ScriptProfiler PROFILER = new ScriptProfiler();

    protected INormalizer undNormalizer;
    protected Transliterator anyToLatinTransliterator;
    protected Transliterator anyToLatinBgnTransliterator;
    protected ITextTransformer chineseTransformer;

    public TextTransformer_Und() {
        this(null);
    }

    public TextTransformer_Und(ITextTransformer chineseTextTransformer) {
        undNormalizer = new Normalizer_Und();
        anyToLatinTransliterator = Transliterator.getInstance("Any-Latin");
        anyToLatinBgnTransliterator = Transliterator.getInstance("Any-Latin/BGN");
        chineseTransformer = chineseTextTransformer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForDisplay(String input) {
        return undNormalizer.normalizeForDisplay(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForScoring(String input) {
        return undNormalizer.normalizeForScoring(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForIndex(String input) {
        String idxNormalized = undNormalizer.normalizeForIndex(input, REMOVE_SPACES_FOR_INDEX);
        if (chineseTransformer != null) {
            idxNormalized = chineseTransformer.normalizeForIndex(idxNormalized);
        }
        return idxNormalized;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String transliterate(String input, TextType standard) {
        // should probably really throw an unsupported standard exception
        if (standard != null && standard.toString().toLowerCase().contains("bgn")) {
            return anyToLatinBgnTransliterator.transliterate(input);
        }
        return anyToLatinBgnTransliterator.transliterate(input);
    }

    /**
     * Get a list of the transliteration candidates using the specified standard.
     *
     * @param input The input text.
     * @param standard The transliteration standard.
     *
     * @return A list of the transliteration candidates using the specified standard.
     */
    public List<String> getTransliterationCandidates(String input, TextType standard) {
        List<String> candidates = new Vector<>();
        candidates.add(input);
        return candidates;
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

        Script sourceScript = PROFILER.profile(input).getScript(true);
        curXlit = new Transliteration(true, 0, sourceScript.getCode(), TextType.ORIGINAL.getDisplayLabel(),
                srcDisplayText, srcIndexText);
        results.add(curXlit);
        ArrayList<Transliteration> icuTranslits = getAvailableTransliterations(input, language);
        if (icuTranslits != null && icuTranslits.size() > 0) {
            results.addAll(icuTranslits);
        }
        return results;
    }

    /**
     * This is a quick and dirty method - should eventually be replaced with better code
     *
     * @param input The input text.
     * @param language The language of the input text.
     *
     * @return A transliteration for each available standard.
     */
    public ArrayList<Transliteration> getAvailableTransliterations(String input, Language language) {
        if (Strings.isBlank(input) || !PROFILER.profile(input).containsNonLatin()) {
            return null;
        }

        try {
            input = normalizeForDisplay(input);
        } catch (Exception eek) {
            System.err.println("Error normalizing for display: " + eek.getMessage());
        }

        ArrayList<Transliteration> xlits = new ArrayList<>();
        Transliterator curTransliterator;
        String curText;
        String indexText;
        Transliteration curXlit;
        String bgnText = "";

        String sourceLanguageName = null;
        if (language != null) {
            sourceLanguageName = language.getName();
        }

        if (Strings.hasText(sourceLanguageName)) {

            // source lang -> latn -> bgn
            try {
                curTransliterator = Transliterator.getInstance(sourceLanguageName + "-Latin/BGN");
                curText = curTransliterator.transliterate(input);
                bgnText = anyToLatinBgnTransliterator.transliterate(curText);
                bgnText = bgnText.replaceAll("\u0627", "a");
                curText = bgnText;
                indexText = normalizeForIndex(curText);
                curXlit = new Transliteration(false, 1, Script.LATN.getCode(), TextType.BGN.getDisplayLabel(),
                        curText, indexText);
                xlits.add(curXlit);
            } catch (Exception eek) {
                // noop - just don't add this variant to the list
            }

            try {
                curTransliterator = Transliterator.getInstance(sourceLanguageName + "-Latin");
                curText = curTransliterator.transliterate(input);
                curText = anyToLatinTransliterator.transliterate(curText);
                if (!curText.equalsIgnoreCase(bgnText)) {
                    indexText = normalizeForIndex(curText);
                    curXlit = new Transliteration(false, 2, Script.LATN.getCode(),
                            TextType.ICU_LATIN.getDisplayLabel(), curText, indexText);
                    xlits.add(curXlit);
                }

            } catch (Exception eek) {
                // noop - just don't add this variant to the list
            }
        }

        if (xlits.size() < 1) {
            bgnText = anyToLatinBgnTransliterator.transliterate(input);
            curText = bgnText;
            indexText = normalizeForIndex(curText);
            curXlit =
                    new Transliteration(false, 1, Script.LATN.getCode(), TextType.BGN.getDisplayLabel(), curText,
                            indexText);
            xlits.add(curXlit);

            curText = anyToLatinTransliterator.transliterate(input);
            if (!curText.equalsIgnoreCase(bgnText)) {
                indexText = normalizeForIndex(curText);
                curXlit = new Transliteration(false, 2, Script.LATN.getCode(),
                        TextType.ICU_LATIN.getDisplayLabel(), curText, indexText);
                xlits.add(curXlit);
            }
        }

        return xlits;
    }
}