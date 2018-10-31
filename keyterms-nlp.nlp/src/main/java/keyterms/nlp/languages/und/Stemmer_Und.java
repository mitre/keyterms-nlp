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

import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import keyterms.util.text.Strings;

public class Stemmer_Und {

    private final StandardAnalyzer analyzer;

    public Stemmer_Und() {
        super();
        analyzer = new StandardAnalyzer();
    }

    public String getStem(String input) {
        if (Strings.isBlank(input)) {
            return null;
        }
        StringBuilder allStemsAsOneBigHappyString = new StringBuilder();

        //OffsetAttribute offsetAtt = rusAnalyzer.addAttribute(OffsetAttribute.class);
        try (TokenStream ts = analyzer.tokenStream("meaningless", new StringReader(input))) {
            CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                //String tokenText = termAtt.toString();
                //String stem = tokenText;
                //stemmer.stem(tokenText);
                //stemmer.setCurrent(tokenText);
//				boolean stemmed = stemmer.stem();
//				if(stemmed) {
//					stem = stemmer.getCurrent();
//				}
                allStemsAsOneBigHappyString.append(termAtt.toString());
            }
        } catch (Exception eek) {
            System.err.println("tokenizer and stemmer error: " + eek.getMessage());
        }
        return allStemsAsOneBigHappyString.toString();
    }
}