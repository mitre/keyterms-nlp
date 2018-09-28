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

package keyterms.nlp.text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import keyterms.nlp.iso.WrittenLanguage;
import keyterms.util.text.Strings;
import keyterms.util.text.TextSplitter;

/**
 * An implementation of a text splitter that does a best guess split on sentences based on written language locale's.
 */
public class SentenceSplitter
        implements TextSplitter {
    /**
     * The written language handled by this splitter.
     */
    private final WrittenLanguage writtenLanguage;

    /**
     * Constructor.
     *
     * @param writtenLanguage The written language of the text to split.
     */
    public SentenceSplitter(WrittenLanguage writtenLanguage) {
        super();
        this.writtenLanguage = writtenLanguage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> split(CharSequence text) {
        List<String> sentences = new ArrayList<>();
        if (!Strings.isEmpty(text)) {
            BreakIterator sentenceSplitter = BreakIterator.getSentenceInstance(writtenLanguage.getLocale());
            sentenceSplitter.setText(text.toString());
            int current = sentenceSplitter.current();
            if (current != -1) {
                while (current != -1) {
                    int sentenceEnd = sentenceSplitter.next();
                    if (sentenceEnd != -1) {
                        if (current != sentenceEnd) {
                            sentences.add(text.subSequence(current, sentenceEnd).toString());
                        }
                    }
                    current = sentenceEnd;
                }
            } else {
                sentences.add(text.toString());
            }
        }
        return sentences;
    }
}