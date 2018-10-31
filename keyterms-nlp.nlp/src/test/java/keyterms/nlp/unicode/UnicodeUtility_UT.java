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

package keyterms.nlp.unicode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnicodeUtility_UT {

    @Test
    public void UnicodeUtility_unadornedValidHex_4digit() {
        String input = "182a";
        Character expected = 'ᠪ';
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_4digitLeadingZero() {
        String input = "0061";
        Character expected = 'a';
        Character actual = UnicodeUtility.getCharacterForCode(input, 16);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_2digitNoLeadingZero() {
        String input = "61";
        Character expected = 'a';
        Character actual = UnicodeUtility.getCharacterForCode(input, 16);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_4digitLeadingZero_DefaultDec() {
        String input = "0061";  //decimal 97
        Character expected = UnicodeUtility.getCharacterForCode(input, 10);
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_2digitNoLeadingZero_DefaultDec() {
        String input = "61"; //decimal 97
        Character expected = UnicodeUtility.getCharacterForCode(input, 10);
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_java_uXXXX() {
        String input = "\\\\u0061";
        Character expected = 'a';
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_unicode_uXXXX() {
        String input = "U+0061";
        Character expected = 'a';
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_unicode_uXXXX_space() {
        String input = "U + 0061";
        Character expected = 'a';
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_htmlHexadecimal() {
        String input = "&#xA2";
        Character expected = '¢';
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_unadornedValidHex_htmlDecimal() {
        String input = "&#162";
        Character expected = '¢';
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    @Test
    public void UnicodeUtility_htmlEscapeStyle() {
        String input = "&#162;";
        Character expected = '¢';
        Character actual = UnicodeUtility.getCharacterForCode(input);
        assertEquals(expected, actual);
    }

    // html decimal &#160;
    @Test
    public void UnicodeUtility_noPrefixNonAscii() {
        String inputHex = "2799";
        Character expectedHex = '➙';
        Character actualHex = UnicodeUtility.getCharacterForCode(inputHex, 16);
        assertEquals(expectedHex, actualHex);

        inputHex = "2799";
        expectedHex = '૯';
        actualHex = UnicodeUtility.getCharacterForCode(inputHex);
        assertEquals(expectedHex, actualHex);

        String inputDec = "10137";
        Character expectedDec = '➙';
        Character actualDec = UnicodeUtility.getCharacterForCode(inputDec, 10);
        assertEquals(expectedDec, actualDec);
    }
}