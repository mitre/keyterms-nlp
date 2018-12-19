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

package keyterms.nlp.languages.zho;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Vector;

import keyterms.nlp.interfaces.ITokenizer;

/*
 *  ITokenizer for Chinese that treats each character as a token, with special handling
 *  for characters such as double new lines and full stops that should not be matched
 *  across, also keep Latin characters together
 */
public class Tokenizer_Zho
        implements ITokenizer {

    private StringReader rawSourceReader;
    private Vector<String> tokenList;
    private int tokenPosition;

    public static final String NEWLINE_SURROGATE = "\u2188";

    private HashSet<String> matchBoundaryTokens;

    /* Create an ITokenizer by passing the string to be analyzed. */
    public Tokenizer_Zho() {
        tokenList = new Vector<>();
    }

    /* Create a ITokenizer by passing the string to be analyzed. */
    public Tokenizer_Zho(String sourceString) {
        tokenize(sourceString);
    }

    public String getTokenDelimiter() {
        return NEWLINE_SURROGATE;
    }

    public Vector<String> tokenize(String sourceString) {

        tokenList = new Vector<>();
        if (sourceString == null) {
            return tokenList;
        }
        this.rawSourceReader = new StringReader(sourceString);

        createValidDelimiters();

        String currentToken;
        int len = sourceString.length();
        try {
            for (int i = 0; i < len; i++) {
                char[] curChar = new char[1];
                rawSourceReader.read(curChar, 0, 1);
                currentToken = new String(curChar);
                if (isNewline(currentToken)) {
                    tokenList.add(NEWLINE_SURROGATE);
                } else {
                    tokenList.add(currentToken);
                }
                tokenList.add(NEWLINE_SURROGATE);
            }
        } catch (Exception eek) {
            System.err.println("Error tokenizing: " + eek.toString());
        }
        tokenPosition = 0;
        return tokenList;
    }

    /* Retrieve the next valid token from the input string. */
    public String nextToken() {
        String nextToken = tokenList.get(tokenPosition);
        tokenPosition++;
        return nextToken;
    }

    public boolean hasNextToken() {
        return (tokenPosition != (tokenList.size()) && tokenList.size() > 0);
    }

    /*
     * Create a HashSet of meaningful delimiters should NOT appear in TransCAT
     * search terms.
     */
    private void createValidDelimiters() {
        matchBoundaryTokens = new HashSet<>();
        matchBoundaryTokens.add("\r");
        matchBoundaryTokens.add("\n");
        matchBoundaryTokens.add("\t");
        matchBoundaryTokens.add("。");
        matchBoundaryTokens.add(".");
        // matchBoundaryTokens.add(","); // Really???
        matchBoundaryTokens.add("?");
        matchBoundaryTokens.add("!");
        matchBoundaryTokens.add(":");
        matchBoundaryTokens.add(";");
    }

    public boolean isMatchBoundaryToken(String tokenString) {
        return matchBoundaryTokens.contains(tokenString);
    }

    public static boolean isNewline(String inputText) {
        return ((inputText != null) && (inputText.matches("[\\n\\r]+")));
    }
}