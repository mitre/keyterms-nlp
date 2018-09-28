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

package keyterms.nlp.languages.ukr;

import java.io.StringReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import keyterms.nlp.interfaces.IStemmer;
import keyterms.util.text.Strings;

//@todo Find a Ukrainian stemmer!
public class Stemmer_Ukr
        implements IStemmer {

    private static final ArrayList<String> SUFFIXES;
    private static final Hashtable<String, String> EXCEPTIONS;

    static {
        EXCEPTIONS = new Hashtable<>();
        EXCEPTIONS.put(norm4Analysis("істьість"), norm4Analysis("іс"));
    }

    static {
        SUFFIXES = new ArrayList<>();
        SUFFIXES.add(norm4Analysis("і"));
        SUFFIXES.add(norm4Analysis("ість"));
        SUFFIXES.add(norm4Analysis("ь"));
        SUFFIXES.add(norm4Analysis("и"));
        SUFFIXES.sort(Comparator.comparing(String::length).reversed());
    }

    private StandardAnalyzer analyzer;

    public Stemmer_Ukr() {
        super();
        analyzer = new StandardAnalyzer();
    }

    public String getStem(String input) {
        if (Strings.isBlank(input)) {
            return null;
        }
        StringBuilder allStemsAsOneBigHappyString = new StringBuilder();

        //OffsetAttribute offsetAtt = rusAnalyzer.addAttribute(OffsetAttribute.class);
        try {
            StringReader strReader = new StringReader(input);
            TokenStream ts = analyzer.tokenStream("meaningless", strReader);
            CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                String tokenText = termAtt.toString();
                String stem = removeUkrainianEndings(tokenText);
                allStemsAsOneBigHappyString.append(stem);
            }
            ts.close();
            strReader.close();
        } catch (Exception eek) {
            System.err.println("tokenizer and stemmer error: " + eek.getMessage());
        }
        return allStemsAsOneBigHappyString.toString();
    }

    public String removeUkrainianEndings(String tokenText) {
        if (Strings.isBlank(tokenText)) {
            return tokenText;
        }
        String inputToken = norm4Analysis(tokenText);
        inputToken = inputToken.toLowerCase();
        String stem = EXCEPTIONS.get(inputToken);
        if (Strings.hasText(stem)) {
            return stem;
        }
        for (String suffix : SUFFIXES) {

            if (inputToken.endsWith(suffix)) {
                int suffStart = inputToken.lastIndexOf(suffix);
                if (suffStart > 0) {
                    stem = inputToken.substring(0, suffStart);
                    break;
                }
            }
        }
        if (Strings.isBlank(stem)) {
            stem = inputToken;
        }
        return stem;
    }

    public static String norm4Analysis(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKC);
    }
}