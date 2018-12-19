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

package keyterms.nlp.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.model.TextType;
import keyterms.nlp.model.Transliteration;
import keyterms.nlp.text.ScriptProfiler;
import keyterms.nlp.transliterate.TransformKey;
import keyterms.nlp.transliterate.Transliterator;
import keyterms.nlp.transliterate.Transliterators;


public abstract class TextTransformer {

    public static final boolean REMOVE_SPACES_FOR_INDEX = false;
    private boolean removeSpacesForIndex = REMOVE_SPACES_FOR_INDEX;

    protected final ScriptProfiler profiler;
    protected INormalizer normalizer;
    protected IStemmer stemmer;

    protected List<Transliterator> transliterators;


    protected Language source;
    protected Language target;
    protected Set<Script> scripts;

    public TextTransformer() {
        this(Language.ENGLISH, Language.ENGLISH);
    }

    public TextTransformer(Language src, Language trg) {
        source = src;
        target = trg;
        scripts = source.getScripts();

        transliterators = new ArrayList<>();
        profiler = new ScriptProfiler();
        initializeNormalizer();
        initializeStemmer();
        initializeTransliterators();
    }

    public String prepareIndexForm(String input) {
        String indexForm = input;
        if (normalizer != null) {
            indexForm = normalizer.normalizeForIndex(indexForm, removeSpacesForIndex);
        }
        if (stemmer != null) {
            indexForm = stemmer.getStem(indexForm);
        }
        return indexForm;
    }

    protected abstract void initializeNormalizer();

    protected abstract void initializeStemmer();

    protected abstract void initializeTransliterators();

    protected boolean addTransliterator(String source, String target, String key, String displayLabel) {
        Transliterator curXlit = null;
        Set<TransformKey> keys = Transliterators.getTransformKeys(source, target, key);
        if (keys != null && keys.size() > 0) {
            curXlit = Transliterators.get(keys.iterator().next());
        }
        if (curXlit != null) {
            curXlit.setDisplayName(displayLabel);
            transliterators.add(curXlit);
            return true;
        }
        return false;
    }


    public ArrayList<Transliteration> getAvailableTransforms(String input) {
        if (input == null) {
            return null;
        }
        ArrayList<Transliteration> results = new ArrayList<>();
        Transliteration curXlit;

        String srcDisplayText = normalizer.normalizeForDisplay(input);
        String srcIndexText = prepareIndexForm(srcDisplayText);

        String curText = srcDisplayText;
        String indexText = srcIndexText;


        Script inputScript = determineScript(input);
        boolean isSrcScript = isSrcScript(inputScript);
        curXlit = new Transliteration(isSrcScript, 0, inputScript.getCode(),
                TextType.ORIGINAL.getDisplayLabel(), curText, indexText);
        results.add(curXlit);

        int order = 1;
        for (Transliterator xlit : transliterators) {
            curXlit = getTransliterationData(xlit, srcDisplayText, srcIndexText, false, order,
                    target.getPreferredScript().getCode(), xlit.getDisplayName());
            results.add(curXlit);
            order++;
        }
        return results;
    }

    private Script determineScript(String input) {
        return profiler.profile(input).getScript(true);
    }

    // Note, if source language is UND this will always return true - lang id on input?????? short, could be dangerous
    private boolean isSrcScript(Script inputScript) {
        if(source==Language.UND) {
            return true;
        }
        if(scripts!=null) {
            if(scripts.contains(inputScript)) {
                return true;
            }
        }
        return false;
    }

    private Transliteration getTransliterationData(Transliterator xlit, String srcDisplay, String srcIndex,
            boolean isSrcScript, int order, String script, String textType) {
        if (xlit == null) {
            return null;
        }
        String displayText = xlit.apply(srcDisplay);
        String indexText = xlit.apply(srcIndex);
        return new Transliteration(isSrcScript, order, script, textType, displayText, indexText);
    }
}
