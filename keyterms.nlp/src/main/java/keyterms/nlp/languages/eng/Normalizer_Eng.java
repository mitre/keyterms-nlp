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

import java.text.Normalizer;

import keyterms.nlp.interfaces.INormalizer;
import keyterms.nlp.text.StringNormalizer;

public class Normalizer_Eng
        implements INormalizer {
    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForIndex(String input, boolean removeSpacesForIndex) {
        return StringNormalizer.normalize(input, true/*removeNewLine*/, removeSpacesForIndex/*removeSpace*/,
                true/*removeControl*/, true/*removePunctuation*/, true/*normalizePunctuation*/,
                true/*transliteratePunctuation*/, true/*removeDiacritics*/, true/*normalizeCase*/,
                Normalizer.Form.NFKD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForScoring(String input) {
        return StringNormalizer.normalize(input, true/*removeNewLine*/, true/*removeSpace*/, true/*removeControl*/,
                true/*removePunctuation*/, false/*normalizePunctuation*/, false/*transliteratePunctuation*/,
                true/*removeDiacritics*/, false/*normalizeCase*/, Normalizer.Form.NFKD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeForDisplay(String input) {
        return StringNormalizer.normalize(input, true/*removeNewLine*/, false/*removeSpace*/, true/*removeControl*/,
                false/*removePunctuation*/, false/*normalizePunctuation*/, true/*transliteratePunctuation*/,
                false/*removeDiacritics*/, false/*normalizeCase*/, Normalizer.Form.NFKC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getRemoveSpacesForIndex() {
        return false;
    }
}