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

package keyterms.analyzer.text;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.nlp.iso.Language;
import keyterms.nlp.iso.Script;
import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.text.Strings;

/**
 * A text based analysis.
 */
public class TextInfo
        extends Analysis {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 3947533057362237072L;

    /**
     * The binary size of the associated text input.
     */
    public static final AnalysisFeature<Integer> SIZE = new AnalysisFeature<>("size", Integer.class);

    /**
     * The name of the character encoding scheme for the associated text input.
     */
    public static final AnalysisFeature<String> ENCODING = new AnalysisFeature<>("encoding", String.class);

    /**
     * The length of the associated text.
     */
    public static final AnalysisFeature<Integer> LENGTH = new AnalysisFeature<>("length", Integer.class);

    /**
     * The language of the associated text.
     */
    public static final AnalysisFeature<Language> LANGUAGE = new AnalysisFeature<>("language", Language.class);

    /**
     * The script of the associated text.
     */
    public static final AnalysisFeature<Script> SCRIPT = new AnalysisFeature<>("script", Script.class);

    /**
     * Get a text information object equivalent to the specified analysis.
     *
     * @param analysis The analysis (presumed to contain text information features).
     *
     * @return The equivalent text information.
     */
    public static TextInfo of(Analysis analysis) {
        TextInfo textInfo = null;
        if (analysis != null) {
            if (analysis instanceof TextInfo) {
                textInfo = (TextInfo)analysis;
            } else {
                textInfo = new TextInfo(analysis);
            }
        }
        return textInfo;
    }

    /**
     * Constructor.
     */
    public TextInfo() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param analysis The analysis information to copy.
     */
    private TextInfo(Analysis analysis) {
        super(analysis);
    }

    /**
     * Get the binary size of the associated text input.
     *
     * @return The binary size of the associated text input.
     */
    public Integer getSize() {
        return get(SIZE);
    }

    /**
     * Set the binary size of the associated text input.
     *
     * @param size The binary size of the associated text input.
     */
    public void setSize(Integer size) {
        set(SIZE, size);
    }

    /**
     * Get the name of the character encoding scheme for the associated text input.
     *
     * @return The name of the character encoding scheme for the associated text input.
     */
    public String getEncoding() {
        return get(ENCODING);
    }

    /**
     * Set the name of the character encoding scheme for the associated text input.
     *
     * @param encoding The name of the character encoding scheme for the associated text input.
     */
    public void setEncoding(String encoding) {
        set(ENCODING, Strings.toLowerCase(Strings.trim(encoding)));
    }

    /**
     * Get the length of the associated text.
     *
     * @return The length of the associated text.
     */
    public Integer getLength() {
        return get(LENGTH);
    }

    /**
     * Set the length of the associated text.
     *
     * @param length The length of the associated text.
     */
    public void setLength(Integer length) {
        set(LENGTH, length);
    }

    /**
     * Get the language of the associated text.
     *
     * @return The language of the associated text.
     */
    public Language getLanguage() {
        return get(LANGUAGE);
    }

    /**
     * Set the language of the associated text.
     *
     * @param language The language of the associated text.
     */
    public void setLanguage(Language language) {
        set(LANGUAGE, language);
    }

    /**
     * Get the script of the associated text.
     *
     * @return The script of the associated text.
     */
    public Script getScript() {
        return get(SCRIPT);
    }

    /**
     * Set the script of the associated text.
     *
     * @param script The script of the associated text.
     */
    public void setScript(Script script) {
        set(SCRIPT, script);
    }

    /**
     * Get the written language of the associated text.
     *
     * @return The written language of the associated text.
     */
    public WrittenLanguage getWritten() {
        return new WrittenLanguage(getLanguage(), getScript());
    }
}