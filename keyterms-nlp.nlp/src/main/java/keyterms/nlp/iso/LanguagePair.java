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

package keyterms.nlp.iso;

import java.util.Objects;

/**
 * A pair of written languages that can be used as a key.
 */
public class LanguagePair {
    /**
     * The source (or primary) language.
     */
    private final WrittenLanguage source;

    /**
     * The target (or secondary) language.
     */
    private final WrittenLanguage target;

    /**
     * Constructor.
     *
     * @param source The source (or primary) language.
     * @param target The target (or secondary) language.
     */
    public LanguagePair(WrittenLanguage source, WrittenLanguage target) {
        super();
        this.source = source;
        this.target = target;
    }

    /**
     * Get the source (or primary) language.
     *
     * @return The source language.
     */
    public WrittenLanguage getSource() {
        return source;
    }

    /**
     * Get the target (or secondary) language.
     *
     * @return The target language.
     */
    public WrittenLanguage getTarget() {
        return target;
    }

    /**
     * Get the language pair with no scripts specified.
     *
     * <p> If no scripts are currently specified, a reference to {@code this} object is returned. </p>
     *
     * @return The language pair with no scripts specified.
     */
    public LanguagePair withNoScripts() {
        LanguagePair noScripts = this;
        WrittenLanguage newSource = (source != null) ? source.withNoScript() : null;
        WrittenLanguage newTarget = (target != null) ? target.withNoScript() : null;
        if ((!Objects.equals(source, newSource)) || (!Objects.equals(target, newTarget))) {
            noScripts = new LanguagePair(newSource, newTarget);
        }
        return noScripts;
    }

    /**
     * Get the language pair with default scripts specified for the source and target if their respective scripts are
     * unspecified.
     *
     * <p> Languages with a script specified will be unaltered. </p>
     *
     * @return The language pair with default scripts specified if absent.
     */
    public LanguagePair withAssumedScripts() {
        LanguagePair noScripts = this;
        WrittenLanguage newSource = (source != null) ? source.withAssumedScript() : null;
        WrittenLanguage newTarget = (target != null) ? target.withAssumedScript() : null;
        if ((!Objects.equals(source, newSource)) || (!Objects.equals(target, newTarget))) {
            noScripts = new LanguagePair(newSource, newTarget);
        }
        return noScripts;

    }

    /**
     * Get the language pair with the default scripts specified for the source and target.
     *
     * <p> Languages with the preferred script already specified will be unaltered. </p>
     *
     * @return The language pair with default scripts for the source and target.
     */
    public LanguagePair withPreferredScripts() {
        LanguagePair withPreferred = this;
        WrittenLanguage newSource = (source != null) ? source.withPreferredScript() : null;
        WrittenLanguage newTarget = (target != null) ? target.withPreferredScript() : null;
        if ((!Objects.equals(source, newSource)) || (!Objects.equals(target, newTarget))) {
            withPreferred = new LanguagePair(newSource, newTarget);
        }
        return withPreferred;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (this == obj);
        if ((!equals) && (obj instanceof LanguagePair)) {
            LanguagePair languagePair = (LanguagePair)obj;
            equals = ((Objects.equals(source, languagePair.source)) &&
                    (Objects.equals(target, languagePair.target)));
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
                ((source != null) ? source.toString() : "null") + '/' +
                ((target != null) ? target.toString() : "null") + ']';
    }
}