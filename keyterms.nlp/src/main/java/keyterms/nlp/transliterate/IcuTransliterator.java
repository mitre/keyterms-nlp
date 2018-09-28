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

package keyterms.nlp.transliterate;

/**
 * A transliterator which uses the ICU rule based transliterator implementations.
 */
public class IcuTransliterator
        extends Transliterator {
    /**
     * The ICU transliterator.
     */
    protected final com.ibm.icu.text.Transliterator icuTransliterator;

    /**
     * Constructor.
     *
     * @param key The textual form of the transliterator key.
     * @param rules The transliteration rules.
     */
    IcuTransliterator(CharSequence key, String rules) {
        super(true, key);
        icuTransliterator = com.ibm.icu.text.Transliterator.createFromRules(getKey().getText(),
                rules, com.ibm.icu.text.Transliterator.FORWARD);
    }

    /**
     * Constructor.
     *
     * @param icuTransliterator The native ICU transliterator.
     */
    IcuTransliterator(com.ibm.icu.text.Transliterator icuTransliterator) {
        super(icuTransliterator.getID());
        this.icuTransliterator = icuTransliterator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(CharSequence text) {
        return (text != null) ? icuTransliterator.transliterate(text.toString()) : null;
    }
}