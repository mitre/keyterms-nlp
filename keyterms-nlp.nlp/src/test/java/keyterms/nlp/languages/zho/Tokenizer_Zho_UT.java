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

import java.util.ArrayList;
import java.util.Vector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Tokenizer_Zho_UT {

    private static final String input = "地球是来自太阳的第三颗行星， 也是宇宙中唯一被称为拥有生命的物体。";
    private static Tokenizer_Zho tokenizerZho = new Tokenizer_Zho(input);

    @Test
    public void tokenize() {
        Vector<String> tokens = tokenizerZho.tokenize(input);
        for (int i = 0; i < input.length(); i++) {
            assertEquals(Character.toString(input.charAt(i)), tokens.get(i * 2));
        }
    }

    @Test
    public void nextToken() {
        ArrayList<String> target = new ArrayList<>();
        for (char c : input.toCharArray()) {
            target.add(Character.toString(c));
            target.add(tokenizerZho.getTokenDelimiter());
        }

        int targetIndex = 0;
        while (tokenizerZho.hasNextToken()) {
            assertEquals(target.get(targetIndex++), tokenizerZho.nextToken());
        }
    }

    @Test
    public void isMatchBoundaryToken() {
        assertTrue(tokenizerZho.isMatchBoundaryToken("\r"));
        assertTrue(tokenizerZho.isMatchBoundaryToken("\n"));
        assertTrue(tokenizerZho.isMatchBoundaryToken("\t"));
        assertTrue(tokenizerZho.isMatchBoundaryToken("。"));
        assertTrue(tokenizerZho.isMatchBoundaryToken("."));
        assertTrue(tokenizerZho.isMatchBoundaryToken("?"));
        assertTrue(tokenizerZho.isMatchBoundaryToken("!"));
        assertTrue(tokenizerZho.isMatchBoundaryToken(":"));
        assertTrue(tokenizerZho.isMatchBoundaryToken(";"));
        assertFalse(tokenizerZho.isMatchBoundaryToken(","));
    }

    @Test
    public void isNewline() {
        assertTrue(Tokenizer_Zho.isNewline("\n"));
        assertTrue(Tokenizer_Zho.isNewline("\r"));
        assertFalse(Tokenizer_Zho.isNewline(""));
        assertFalse(Tokenizer_Zho.isNewline(","));
    }
}