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

package keyterms.nlp.languages.rus;

import java.io.StringReader;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.RussianStemmer;

import keyterms.nlp.interfaces.IStemmer;
import keyterms.nlp.text.StringNormalizer;
import keyterms.util.text.Strings;

public class Stemmer_Rus
        implements IStemmer {

    private final RussianStemmer stemmer;
    private final RussianAnalyzer analyzer;

    public Stemmer_Rus() {
        super();
        stemmer = new RussianStemmer();
        analyzer = new RussianAnalyzer();
    }

    public String getStem(String input) {
        if (Strings.isBlank(input)) {
            return null;
        }
        StringBuilder allStemsAsOneBigHappyString = new StringBuilder();
        //OffsetAttribute offsetAtt = rusAnalyzer.addAttribute(OffsetAttribute.class);
        TokenStream ts = null;
        StringReader strReader = null;
        try {
            strReader = new StringReader(input);

            ts = analyzer.tokenStream("meaningless", strReader);
            CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            int numToks = 0;
            while (ts.incrementToken()) {
                String stem = termAtt.toString();
                if(numToks>0)
                    allStemsAsOneBigHappyString.append(" ");
                allStemsAsOneBigHappyString.append(stem);
                numToks++;
            }
            ts.close();
            strReader.close();
        } catch (Exception eek) {
            System.err.println("tokenizer and stemmer error: " + eek.getMessage());
            try {
                ts.close();
                strReader.close();
            } catch (Exception yikes) {
                System.err.println(yikes.getMessage());
            }
        }
        String stemResult = allStemsAsOneBigHappyString.toString();
        if (Strings.isBlank(stemResult)) {
            stemResult = StringNormalizer.scrunch(input, true);
        }
        return stemResult;
    }

    public String[] getStopwords() {
        Object[] stopObjs;
        String[] stopStrings = null;
        CharArraySet stopwords = analyzer.getStopwordSet();
        if (stopwords != null) {
            int numStopwords = stopwords.size();
            stopObjs = new Object[numStopwords];
            stopObjs = stopwords.toArray(stopObjs);
            stopStrings = new String[stopObjs.length];
            int i = 0;
            for (Object obj : stopObjs) {
                stopStrings[i] = new String((char[])obj);
                i++;
            }
        }
        return stopStrings;
    }
}