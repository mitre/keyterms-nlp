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

import java.util.HashMap;

public class UnicodeNormalizationData {

    private static HashMap<String, String> replacementHash;

    static {
        replacementHash = new HashMap<>();

        replacementHash.put("\uFF3A", "Z");     // FF3A  : FULLWIDTH LATIN CAPITAL LETTER Z
        replacementHash.put("\uFF5A", "z");     // FF5A  : FULLWIDTH LATIN SMALL LETTER Z
        replacementHash.put("\uFF39", "Y");     // FF39  : FULLWIDTH LATIN CAPITAL LETTER Y
        replacementHash.put("\uFF59", "y");     // FF59  : FULLWIDTH LATIN SMALL LETTER Y
        replacementHash.put("\uFF38", "X");     // FF38  : FULLWIDTH LATIN CAPITAL LETTER X
        replacementHash.put("\uFF58", "x");     // FF58  : FULLWIDTH LATIN SMALL LETTER X
        replacementHash.put("\uFF37", "W");     // FF37  : FULLWIDTH LATIN CAPITAL LETTER W
        replacementHash.put("\uFF57", "w");     // FF57  : FULLWIDTH LATIN SMALL LETTER W
        replacementHash.put("\uFF36", "V");     // FF36  : FULLWIDTH LATIN CAPITAL LETTER V
        replacementHash.put("\uFF56", "v");     // FF56  : FULLWIDTH LATIN SMALL LETTER V
        replacementHash.put("\uFF35", "U");     // FF35  : FULLWIDTH LATIN CAPITAL LETTER U
        replacementHash.put("\uFF55", "u");     // FF55  : FULLWIDTH LATIN SMALL LETTER U
        replacementHash.put("\uFF34", "T");     // FF34  : FULLWIDTH LATIN CAPITAL LETTER T
        replacementHash.put("\uFF54", "t");     // FF54  : FULLWIDTH LATIN SMALL LETTER T
        replacementHash.put("\uFF33", "S");     // FF33  : FULLWIDTH LATIN CAPITAL LETTER S
        replacementHash.put("\uFF53", "s");     // FF53  : FULLWIDTH LATIN SMALL LETTER S
        replacementHash.put("\uFF32", "R");     // FF32  : FULLWIDTH LATIN CAPITAL LETTER R
        replacementHash.put("\uFF52", "r");     // FF52  : FULLWIDTH LATIN SMALL LETTER R
        replacementHash.put("\uFF31", "Q");     // FF31  : FULLWIDTH LATIN CAPITAL LETTER Q
        replacementHash.put("\uFF51", "q");     // FF51  : FULLWIDTH LATIN SMALL LETTER Q
        replacementHash.put("\uFF30", "P");     // FF30  : FULLWIDTH LATIN CAPITAL LETTER P
        replacementHash.put("\uFF50", "p");     // FF50  : FULLWIDTH LATIN SMALL LETTER P
        replacementHash.put("\uFF2F", "O");     // FF2F  : FULLWIDTH LATIN CAPITAL LETTER O
        replacementHash.put("\uFF4F", "o");     // FF4F  : FULLWIDTH LATIN SMALL LETTER O
        replacementHash.put("\uFF2E", "N");     // FF2E  : FULLWIDTH LATIN CAPITAL LETTER N
        replacementHash.put("\uFF4E", "n");     // FF4E  : FULLWIDTH LATIN SMALL LETTER N
        replacementHash.put("\uFF2D", "M");     // FF2D  : FULLWIDTH LATIN CAPITAL LETTER M
        replacementHash.put("\uFF4D", "m");     // FF4D  : FULLWIDTH LATIN SMALL LETTER M
        replacementHash.put("\uFF2C", "L");     // FF2C  : FULLWIDTH LATIN CAPITAL LETTER L
        replacementHash.put("\uFF4C", "l");     // FF4C  : FULLWIDTH LATIN SMALL LETTER L
        replacementHash.put("\uFF2B", "K");     // FF2B  : FULLWIDTH LATIN CAPITAL LETTER K
        replacementHash.put("\uFF4B", "k");     // FF4B  : FULLWIDTH LATIN SMALL LETTER K
        replacementHash.put("\uFF2A", "J");     // FF2A  : FULLWIDTH LATIN CAPITAL LETTER J
        replacementHash.put("\uFF4A", "j");     // FF4A  : FULLWIDTH LATIN SMALL LETTER J
        replacementHash.put("\uFF29", "I");     // FF29  : FULLWIDTH LATIN CAPITAL LETTER I
        replacementHash.put("\uFF49", "i");     // FF49  : FULLWIDTH LATIN SMALL LETTER I
        replacementHash.put("\uFF28", "H");     // FF28  : FULLWIDTH LATIN CAPITAL LETTER H
        replacementHash.put("\uFF48", "h");     // FF48  : FULLWIDTH LATIN SMALL LETTER H
        replacementHash.put("\uFF27", "G");     // FF27  : FULLWIDTH LATIN CAPITAL LETTER G
        replacementHash.put("\uFF47", "g");     // FF47  : FULLWIDTH LATIN SMALL LETTER G
        replacementHash.put("\uFF26", "F");     // FF26  : FULLWIDTH LATIN CAPITAL LETTER F
        replacementHash.put("\uFF46", "f");     // FF46  : FULLWIDTH LATIN SMALL LETTER F
        replacementHash.put("\uFF25", "E");     // FF25  : FULLWIDTH LATIN CAPITAL LETTER E
        replacementHash.put("\uFF45", "e");     // FF45  : FULLWIDTH LATIN SMALL LETTER E
        replacementHash.put("\uFEFF", "delete?");     // FEFF  : ZERO WIDTH NO-BREAK SPACE
        replacementHash.put("\u0640", "delete");     // 0640  : ARABIC TATWEEL
        replacementHash.put("\uFF24", "D");     // FF24  : FULLWIDTH LATIN CAPITAL LETTER D
        replacementHash.put("\uFF44", "d");     // FF44  : FULLWIDTH LATIN SMALL LETTER D
        replacementHash.put("\uFF23", "C");     // FF23  : FULLWIDTH LATIN CAPITAL LETTER C
        replacementHash.put("\uFF43", "c");     // FF43  : FULLWIDTH LATIN SMALL LETTER C
        replacementHash.put("\uFF22", "B");     // FF22  : FULLWIDTH LATIN CAPITAL LETTER B
        replacementHash.put("\uFF42", "b");     // FF42  : FULLWIDTH LATIN SMALL LETTER B
        replacementHash.put("\uFF21", "A");     // FF21  : FULLWIDTH LATIN CAPITAL LETTER A
        replacementHash.put("\uFF41", "a");     // FF41  : FULLWIDTH LATIN SMALL LETTER A
        replacementHash.put("\u2788", "•9");     // 2788  : DINGBAT CIRCLED SANS-SERIF DIGIT NINE
        replacementHash.put("\u277E", "•9");     // 277E  : DINGBAT NEGATIVE CIRCLED DIGIT NINE
        replacementHash.put("\u2792", "•9");     // 2792  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT NINE
        replacementHash.put("\u2787", "•8");     // 2787  : DINGBAT CIRCLED SANS-SERIF DIGIT EIGHT
        replacementHash.put("\u277D", "•8");     // 277D  : DINGBAT NEGATIVE CIRCLED DIGIT EIGHT
        replacementHash.put("\u2791", "•8");     // 2791  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT EIGHT
        replacementHash.put("\u2786", "•7");     // 2786  : DINGBAT CIRCLED SANS-SERIF DIGIT SEVEN
        replacementHash.put("\u277C", "•7");     // 277C  : DINGBAT NEGATIVE CIRCLED DIGIT SEVEN
        replacementHash.put("\u2790", "•7");     // 2790  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SEVEN
        replacementHash.put("\u2785", "•6");     // 2785  : DINGBAT CIRCLED SANS-SERIF DIGIT SIX
        replacementHash.put("\u277B", "•6");     // 277B  : DINGBAT NEGATIVE CIRCLED DIGIT SIX
        replacementHash.put("\u278F", "•6");     // 278F  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SIX
        replacementHash.put("\u2784", "•5");     // 2784  : DINGBAT CIRCLED SANS-SERIF DIGIT FIVE
        replacementHash.put("\u277A", "•5");     // 277A  : DINGBAT NEGATIVE CIRCLED DIGIT FIVE
        replacementHash.put("\u278E", "•5");     // 278E  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FIVE
        replacementHash.put("\u2783", "•4");     // 2783  : DINGBAT CIRCLED SANS-SERIF DIGIT FOUR
        replacementHash.put("\u2779", "•4");     // 2779  : DINGBAT NEGATIVE CIRCLED DIGIT FOUR
        replacementHash.put("\u278D", "•4");     // 278D  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FOUR
        replacementHash.put("\u2782", "•3");     // 2782  : DINGBAT CIRCLED SANS-SERIF DIGIT THREE
        replacementHash.put("\u2778", "•3");     // 2778  : DINGBAT NEGATIVE CIRCLED DIGIT THREE
        replacementHash.put("\u278C", "•3");     // 278C  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT THREE
        replacementHash.put("\u2781", "•2");     // 2781  : DINGBAT CIRCLED SANS-SERIF DIGIT TWO
        replacementHash.put("\u2777", "•2");     // 2777  : DINGBAT NEGATIVE CIRCLED DIGIT TWO
        replacementHash.put("\u278B", "•2");     // 278B  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT TWO
        replacementHash.put("\u2789", "•10");     // 2789  : DINGBAT CIRCLED SANS-SERIF NUMBER TEN
        replacementHash.put("\u277F", "•10");     // 277F  : DINGBAT NEGATIVE CIRCLED NUMBER TEN
        replacementHash.put("\u2793", "•10");     // 2793  : DINGBAT NEGATIVE CIRCLED SANS-SERIF NUMBER TEN
        replacementHash.put("\u2780", "•1");     // 2780  : DINGBAT CIRCLED SANS-SERIF DIGIT ONE
        replacementHash.put("\u2776", "•1");     // 2776  : DINGBAT NEGATIVE CIRCLED DIGIT ONE
        replacementHash.put("\u278A", "•1");     // 278A  : DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT ONE
        replacementHash.put("\u27EB", "»");     // 27EB  : MATHEMATICAL RIGHT DOUBLE ANGLE BRACKET
        replacementHash.put("\u27ED", "»");     // 27ED  : MATHEMATICAL RIGHT WHITE TORTOISE SHELL BRACKET
        replacementHash.put("\u169C", "»");     // 169C  : OGHAM REVERSED FEATHER MARK
        replacementHash.put("\u300B", "»");     // 300B  : RIGHT DOUBLE ANGLE BRACKET
        replacementHash.put("\uFE3E", "»");     // FE3E  : PRESENTATION FORM FOR VERTICAL RIGHT DOUBLE ANGLE BRACKET
        replacementHash.put("\u27EA", "«");     // 27EA  : MATHEMATICAL LEFT DOUBLE ANGLE BRACKET
        replacementHash.put("\u27EC", "«");     // 27EC  : MATHEMATICAL LEFT WHITE TORTOISE SHELL BRACKET
        replacementHash.put("\u169B", "«");     // 169B  : OGHAM FEATHER MARK
        replacementHash.put("\u300A", "«");     // 300A  : LEFT DOUBLE ANGLE BRACKET
        replacementHash.put("\uFE3D", "«");     // FE3D  : PRESENTATION FORM FOR VERTICAL LEFT DOUBLE ANGLE BRACKET
        replacementHash.put("\u276D", ">");     // 276D  : MEDIUM RIGHT-POINTING ANGLE BRACKET ORNAMENT
        replacementHash.put("\u2771", ">");     // 2771  : HEAVY RIGHT-POINTING ANGLE BRACKET ORNAMENT
        replacementHash.put("\u27E9", ">");     // 27E9  : MATHEMATICAL RIGHT ANGLE BRACKET
        replacementHash.put("\u2992", ">");     // 2992  : RIGHT ANGLE BRACKET WITH DOT
        replacementHash.put("\u29FD", ">");     // 29FD  : RIGHT-POINTING CURVED ANGLE BRACKET
        replacementHash.put("\u232A", ">");     // 232A  : RIGHT-POINTING ANGLE BRACKET
        replacementHash.put("\u3009", ">");     // 3009  : RIGHT ANGLE BRACKET
        replacementHash.put("\uFE40", ">");     // FE40  : PRESENTATION FORM FOR VERTICAL RIGHT ANGLE BRACKET
        replacementHash.put("\uFE65", ">");     // FE65  : SMALL GREATER-THAN SIGN
        replacementHash.put("\uFF1E", ">");     // FF1E  : FULLWIDTH GREATER-THAN SIGN
        replacementHash.put("\u276F", ">");     // 276F  : HEAVY RIGHT-POINTING ANGLE QUOTATION MARK ORNAMENT
        replacementHash.put("\uFE66", "=");     // FE66  : SMALL EQUALS SIGN
        replacementHash.put("\uFF1D", "=");     // FF1D  : FULLWIDTH EQUALS SIGN
        replacementHash.put("\u276C", "<");     // 276C  : MEDIUM LEFT-POINTING ANGLE BRACKET ORNAMENT
        replacementHash.put("\u2770", "<");     // 2770  : HEAVY LEFT-POINTING ANGLE BRACKET ORNAMENT
        replacementHash.put("\u27E8", "<");     // 27E8  : MATHEMATICAL LEFT ANGLE BRACKET
        replacementHash.put("\u2991", "<");     // 2991  : LEFT ANGLE BRACKET WITH DOT
        replacementHash.put("\u29FC", "<");     // 29FC  : LEFT-POINTING CURVED ANGLE BRACKET
        replacementHash.put("\u2329", "<");     // 2329  : LEFT-POINTING ANGLE BRACKET
        replacementHash.put("\u3008", "<");     // 3008  : LEFT ANGLE BRACKET
        replacementHash.put("\uFE3F", "<");     // FE3F  : PRESENTATION FORM FOR VERTICAL LEFT ANGLE BRACKET
        replacementHash.put("\uFE64", "<");     // FE64  : SMALL LESS-THAN SIGN
        replacementHash.put("\uFF1C", "<");     // FF1C  : FULLWIDTH LESS-THAN SIGN
        replacementHash.put("\u276E", "<");     // 276E  : HEAVY LEFT-POINTING ANGLE QUOTATION MARK ORNAMENT
        replacementHash.put("\uFE62", "+");     // FE62  : SMALL PLUS SIGN
        replacementHash.put("\uFF0B", "+");     // FF0B  : FULLWIDTH PLUS SIGN
        replacementHash.put("\u20A0", "€");     // 20A0  : EURO-CURRENCY SIGN
        replacementHash.put("\u20AC", "€");     // 20AC  : EURO SIGN
        replacementHash.put("\u20A9", "₩");     // 20A9  : WON SIGN
        replacementHash.put("\uFFE6", "₩");     // FFE6  : FULLWIDTH WON SIGN
        replacementHash.put("\uFF63", "」");     // FF63  : HALFWIDTH RIGHT CORNER BRACKET
        replacementHash.put("\u300D", "」");     // 300D  : RIGHT CORNER BRACKET
        replacementHash.put("\uFE42", "」");     // FE42  : PRESENTATION FORM FOR VERTICAL RIGHT CORNER BRACKET
        replacementHash.put("\u300F", "」");     // 300F  : RIGHT WHITE CORNER BRACKET
        replacementHash.put("\uFE44", "」");     // FE44  : PRESENTATION FORM FOR VERTICAL RIGHT WHITE CORNER BRACKET
        replacementHash.put("\uFF62", "「");     // FF62  : HALFWIDTH LEFT CORNER BRACKET
        replacementHash.put("\u300C", "「");     // 300C  : LEFT CORNER BRACKET
        replacementHash.put("\uFE41", "「");     // FE41  : PRESENTATION FORM FOR VERTICAL LEFT CORNER BRACKET
        replacementHash.put("\u300E", "「");     // 300E  : LEFT WHITE CORNER BRACKET
        replacementHash.put("\uFE43", "「");     // FE43  : PRESENTATION FORM FOR VERTICAL LEFT WHITE CORNER BRACKET
        replacementHash.put("\u00A5", "¥");     // 00A5  : YEN SIGN
        replacementHash.put("\uFFE5", "¥");     // FFE5  : FULLWIDTH YEN SIGN
        replacementHash.put("\u00A3", "£");     // 00A3  : POUND SIGN
        replacementHash.put("\uFFE1", "£");     // FFE1  : FULLWIDTH POUND SIGN
        replacementHash.put("\u00A2", "¢");     // 00A2  : CENT SIGN
        replacementHash.put("\u055B", "´");     // 055B  : ARMENIAN EMPHASIS MARK
        replacementHash.put("\uFF5E", "~");     // FF5E  : FULLWIDTH TILDE
        replacementHash.put("\uFFE2", "~");     // FFE2  : FULLWIDTH NOT SIGN
        replacementHash.put("\u2984", "}");     // 2984  : RIGHT WHITE CURLY BRACKET
        replacementHash.put("\u2775", "}");     // 2775  : MEDIUM RIGHT CURLY BRACKET ORNAMENT
        replacementHash.put("\uFE5C", "}");     // FE5C  : SMALL RIGHT CURLY BRACKET
        replacementHash.put("\u007D", "}");     // 007D  : RIGHT CURLY BRACKET
        replacementHash.put("\uFF5D", "}");     // FF5D  : FULLWIDTH RIGHT CURLY BRACKET
        replacementHash.put("\uFE38", "}");     // FE38  : PRESENTATION FORM FOR VERTICAL RIGHT CURLY BRACKET
        replacementHash.put("\u0F3B", "}");     // 0F3B  : TIBETAN MARK GUG RTAGS GYAS
        replacementHash.put("\u0F3D", "}");     // 0F3D  : TIBETAN MARK ANG KHANG GYAS
        replacementHash.put("\uFE68", "|");     // FE68  : SMALL REVERSE SOLIDUS
        replacementHash.put("\uFF5C", "|");     // FF5C  : FULLWIDTH VERTICAL LINE
        replacementHash.put("\uFFE4", "|");     // FFE4  : FULLWIDTH BROKEN BAR
        replacementHash.put("\uFFE8", "|");     // FFE8  : HALFWIDTH FORMS LIGHT VERTICAL
        replacementHash.put("\u2983", "{");     // 2983  : LEFT WHITE CURLY BRACKET
        replacementHash.put("\u2774", "{");     // 2774  : MEDIUM LEFT CURLY BRACKET ORNAMENT
        replacementHash.put("\uFE5B", "{");     // FE5B  : SMALL LEFT CURLY BRACKET
        replacementHash.put("\u007B", "{");     // 007B  : LEFT CURLY BRACKET
        replacementHash.put("\uFF5B", "{");     // FF5B  : FULLWIDTH LEFT CURLY BRACKET
        replacementHash.put("\uFE37", "{");     // FE37  : PRESENTATION FORM FOR VERTICAL LEFT CURLY BRACKET
        replacementHash.put("\u0F3A", "{");     // 0F3A  : TIBETAN MARK GUG RTAGS GYON
        replacementHash.put("\u0F3C", "{");     // 0F3C  : TIBETAN MARK ANG KHANG GYON
        replacementHash.put("\uFF40", "`");     // FF40  : FULLWIDTH GRAVE ACCENT
        replacementHash.put("\uFF3F", "_");     // FF3F  : FULLWIDTH LOW LINE
        replacementHash.put("\u00B3", "^3");     // 00B3  : SUPERSCRIPT THREE
        replacementHash.put("\u00B2", "^2");     // 00B2  : SUPERSCRIPT TWO
        replacementHash.put("\u00B9", "^1");     // 00B9  : SUPERSCRIPT ONE
        replacementHash.put("\uFF3E", "^");     // FF3E  : FULLWIDTH CIRCUMFLEX ACCENT
        replacementHash.put("\u2990", "]");     // 2990  : RIGHT SQUARE BRACKET WITH TICK IN TOP CORNER
        replacementHash.put("\uFE48", "]");     // FE48  : PRESENTATION FORM FOR VERTICAL RIGHT SQUARE BRACKET
        replacementHash.put("\u298C", "]");     // 298C  : RIGHT SQUARE BRACKET WITH UNDERBAR
        replacementHash.put("\u298E", "]");     // 298E  : RIGHT SQUARE BRACKET WITH TICK IN BOTTOM CORNER
        replacementHash.put("\u27E7", "]");     // 27E7  : MATHEMATICAL RIGHT WHITE SQUARE BRACKET
        replacementHash.put("\u005D", "]");     // 005D  : RIGHT SQUARE BRACKET
        replacementHash.put("\uFF3D", "]");     // FF3D  : FULLWIDTH RIGHT SQUARE BRACKET
        replacementHash.put("\u2046", "]");     // 2046  : RIGHT SQUARE BRACKET WITH QUILL
        replacementHash.put("\u2998", "]");     // 2998  : RIGHT BLACK TORTOISE SHELL BRACKET
        replacementHash.put("\u3011", "]");     // 3011  : RIGHT BLACK LENTICULAR BRACKET
        replacementHash.put("\uFE3C", "]");     // FE3C  : PRESENTATION FORM FOR VERTICAL RIGHT BLACK LENTICULAR BRACKET
        replacementHash.put("\uFE5E", "]");     // FE5E  : SMALL RIGHT TORTOISE SHELL BRACKET
        replacementHash.put("\u3015", "]");     // 3015  : RIGHT TORTOISE SHELL BRACKET
        replacementHash.put("\uFE3A", "]");     // FE3A  : PRESENTATION FORM FOR VERTICAL RIGHT TORTOISE SHELL BRACKET
        replacementHash.put("\u3017", "]");     // 3017  : RIGHT WHITE LENTICULAR BRACKET
        replacementHash.put("\u3019", "]");     // 3019  : RIGHT WHITE TORTOISE SHELL BRACKET
        replacementHash.put("\u301B", "]");     // 301B  : RIGHT WHITE SQUARE BRACKET
        replacementHash.put("\uFE18", "]");     // FE18  : PRESENTATION FORM FOR VERTICAL RIGHT WHITE LENTICULAR BRAKCET
        replacementHash.put("\uFF3C", "\\");     // FF3C  : FULLWIDTH REVERSE SOLIDUS
        replacementHash.put("\uFE47", "[");     // FE47  : PRESENTATION FORM FOR VERTICAL LEFT SQUARE BRACKET
        replacementHash.put("\u298B", "[");     // 298B  : LEFT SQUARE BRACKET WITH UNDERBAR
        replacementHash.put("\u298D", "[");     // 298D  : LEFT SQUARE BRACKET WITH TICK IN TOP CORNER
        replacementHash.put("\u298F", "[");     // 298F  : LEFT SQUARE BRACKET WITH TICK IN BOTTOM CORNER
        replacementHash.put("\u27E6", "[");     // 27E6  : MATHEMATICAL LEFT WHITE SQUARE BRACKET
        replacementHash.put("\u005B", "[");     // 005B  : LEFT SQUARE BRACKET
        replacementHash.put("\uFF3B", "[");     // FF3B  : FULLWIDTH LEFT SQUARE BRACKET
        replacementHash.put("\u2045", "[");     // 2045  : LEFT SQUARE BRACKET WITH QUILL
        replacementHash.put("\u2997", "[");     // 2997  : LEFT BLACK TORTOISE SHELL BRACKET
        replacementHash.put("\u3010", "[");     // 3010  : LEFT BLACK LENTICULAR BRACKET
        replacementHash.put("\uFE3B", "[");     // FE3B  : PRESENTATION FORM FOR VERTICAL LEFT BLACK LENTICULAR BRACKET
        replacementHash.put("\uFE5D", "[");     // FE5D  : SMALL LEFT TORTOISE SHELL BRACKET
        replacementHash.put("\u3014", "[");     // 3014  : LEFT TORTOISE SHELL BRACKET
        replacementHash.put("\uFE39", "[");     // FE39  : PRESENTATION FORM FOR VERTICAL LEFT TORTOISE SHELL BRACKET
        replacementHash.put("\u3016", "[");     // 3016  : LEFT WHITE LENTICULAR BRACKET
        replacementHash.put("\u3018", "[");     // 3018  : LEFT WHITE TORTOISE SHELL BRACKET
        replacementHash.put("\u301A", "[");     // 301A  : LEFT WHITE SQUARE BRACKET
        replacementHash.put("\uFE17", "[");     // FE17  : PRESENTATION FORM FOR VERTICAL LEFT WHITE LENTICULAR BRACKET
        replacementHash.put("\uFE6B", "@");     // FE6B  : SMALL COMMERCIAL AT
        replacementHash.put("\u0040", "@");     // 0040  : COMMERCIAL AT
        replacementHash.put("\uFF20", "@");     // FF20  : FULLWIDTH COMMERCIAL AT
        replacementHash.put("\uFE56", "?");     // FE56  : SMALL QUESTION MARK
        replacementHash.put("\uFF1F", "?");     // FF1F  : FULLWIDTH QUESTION MARK
        replacementHash.put("\uFE54", ";");     // FE54  : SMALL SEMICOLON
        replacementHash.put("\uFF1B", ";");     // FF1B  : FULLWIDTH SEMICOLON
        replacementHash.put("\u2E35", ";");     // 2E35  : TURNED SEMICOLON
        replacementHash.put("\u1365", ":");     // 1365  : ETHIOPIC COLON
        replacementHash.put("\u1366", ":");     // 1366  : ETHIOPIC PREFACE COLON
        replacementHash.put("\u02F8", ":");     // 02F8  : MODIFIER LETTER RAISED COLON
        replacementHash.put("\uFE55", ":");     // FE55  : SMALL COLON
        replacementHash.put("\u003A", ":");     // 003A  : COLON
        replacementHash.put("\uFF1A", ":");     // FF1A  : FULLWIDTH COLON
        replacementHash.put("\u0703", ":");     // 0703  : SYRIAC SUPRALINEAR COLON
        replacementHash.put("\u0704", ":");     // 0704  : SYRIAC SUBLINEAR COLON
        replacementHash.put("\u0705", ":");     // 0705  : SYRIAC HORIZONTAL COLON
        replacementHash.put("\uFE30", ":");     // FE30  : PRESENTATION FORM FOR VERTICAL TWO DOT LEADER
        replacementHash.put("\uFE13", ":");     // FE13  : PRESENTATION FORM FOR VERTICAL COLON
        replacementHash.put("\uA789", ":");     // A789  : MODIFIER LETTER COLON
        replacementHash.put("\uFF0F", "/");     // FF0F  : FULLWIDTH SOLIDUS
        replacementHash.put("\uFE52", ".");     // FE52  : SMALL FULL STOP
        replacementHash.put("\uFF0E", ".");     // FF0E  : FULLWIDTH FULL STOP
        replacementHash.put("\uFF61", ".");     // FF61  : HALFWIDTH IDEOGRAPHIC FULL STOP
        replacementHash.put("\u1363", ",");     // 1363  : ETHIOPIC COMMA
        replacementHash.put("\uFE50", ",");     // FE50  : SMALL COMMA
        replacementHash.put("\u002C", ",");     // 002C  : COMMA
        replacementHash.put("\uFF0C", ",");     // FF0C  : FULLWIDTH COMMA
        replacementHash.put("\uFE51", ",");     // FE51  : SMALL IDEOGRAPHIC COMMA
        replacementHash.put("\uFF64", ",");     // FF64  : HALFWIDTH IDEOGRAPHIC COMMA
        replacementHash.put("\u055D", ",");     // 055D  : ARMENIAN COMMA
        replacementHash.put("\u066B", ",");     // 066B  : ARABIC DECIMAL SEPARATOR
        replacementHash.put("\u08E0", "*");     // 08E0  : ARABIC SMALL HIGH FOOTNOTE MARKER
        replacementHash.put("\u2728", "*");     // 2728  : SPARKLES
        replacementHash.put("\u0602", "*");     // 0602  : ARABIC FOOTNOTE MARKER
        replacementHash.put("\uFE61", "*");     // FE61  : SMALL ASTERISK
        replacementHash.put("\u002A", "*");     // 002A  : ASTERISK
        replacementHash.put("\uFF0A", "*");     // FF0A  : FULLWIDTH ASTERISK
        replacementHash.put("\u2217", "*");     // 2217  : ASTERISK OPERATOR
        replacementHash.put("\u2722", "*");     // 2722  : FOUR TEARDROP-SPOKED ASTERISK
        replacementHash.put("\u2723", "*");     // 2723  : FOUR BALLOON-SPOKED ASTERISK
        replacementHash.put("\u2724", "*");     // 2724  : HEAVY FOUR BALLOON-SPOKED ASTERISK
        replacementHash.put("\u2725", "*");     // 2725  : FOUR CLUB-SPOKED ASTERISK
        replacementHash.put("\u2726", "*");     // 2726  : BLACK FOUR POINTED STAR
        replacementHash.put("\u2727", "*");     // 2727  : WHITE FOUR POINTED STAR
        replacementHash.put("\u2729", "*");     // 2729  : STRESS OUTLINED WHITE STAR
        replacementHash.put("\u2730", "*");     // 2730  : SHADOWED WHITE STAR
        replacementHash.put("\u272B", "*");     // 272B  : OPEN CENTRE BLACK STAR
        replacementHash.put("\u272C", "*");     // 272C  : BLACK CENTRE WHITE STAR
        replacementHash.put("\u272D", "*");     // 272D  : OUTLINED BLACK STAR
        replacementHash.put("\u272E", "*");     // 272E  : HEAVY OUTLINED BLACK STAR
        replacementHash.put("\u272F", "*");     // 272F  : PINWHEEL STAR
        replacementHash.put("\u272A", "*");     // 272A  : CIRCLED WHITE STAR
        replacementHash.put("\u2731", "*");     // 2731  : HEAVY ASTERISK
        replacementHash.put("\u2732", "*");     // 2732  : OPEN CENTRE ASTERISK
        replacementHash.put("\u2733", "*");     // 2733  : EIGHT SPOKED ASTERISK
        replacementHash.put("\u2734", "*");     // 2734  : EIGHT POINTED BLACK STAR
        replacementHash.put("\u2735", "*");     // 2735  : EIGHT POINTED PINWHEEL STAR
        replacementHash.put("\u2736", "*");     // 2736  : SIX POINTED BLACK STAR
        replacementHash.put("\u2737", "*");     // 2737  : EIGHT POINTED RECTILINEAR BLACK STAR
        replacementHash.put("\u2738", "*");     // 2738  : HEAVY EIGHT POINTED RECTILINEAR BLACK STAR
        replacementHash.put("\u2739", "*");     // 2739  : TWELVE POINTED BLACK STAR
        replacementHash.put("\u273A", "*");     // 273A  : SIXTEEN POINTED ASTERISK
        replacementHash.put("\u273B", "*");     // 273B  : TEARDROP-SPOKED ASTERISK
        replacementHash.put("\u273C", "*");     // 273C  : OPEN CENTRE TEARDROP-SPOKED ASTERISK
        replacementHash.put("\u273D", "*");     // 273D  : HEAVY TEARDROP-SPOKED ASTERISK
        replacementHash.put("\u273E", "*");     // 273E  : SIX PETALLED BLACK AND WHITE FLORETTE
        replacementHash.put("\u273F", "*");     // 273F  : BLACK FLORETTE
        replacementHash.put("\u2740", "*");     // 2740  : WHITE FLORETTE
        replacementHash.put("\u2741", "*");     // 2741  : EIGHT PETALLED OUTLINED BLACK FLORETTE
        replacementHash.put("\u2742", "*");     // 2742  : CIRCLED OPEN CENTRE EIGHT POINTED STAR
        replacementHash.put("\u2743", "*");     // 2743  : HEAVY TEARDROP-SPOKED PINWHEEL ASTERISK
        replacementHash.put("\u2744", "*");     // 2744  : SNOWFLAKE
        replacementHash.put("\u2745", "*");     // 2745  : TIGHT TRIFOLIATE SNOWFLAKE
        replacementHash.put("\u2746", "*");     // 2746  : HEAVY CHEVRON SNOWFLAKE
        replacementHash.put("\u2747", "*");     // 2747  : SPARKLE
        replacementHash.put("\u2748", "*");     // 2748  : HEAVY SPARKLE
        replacementHash.put("\u2749", "*");     // 2749  : BALLOON-SPOKED ASTERISK
        replacementHash.put("\u274A", "*");     // 274A  : EIGHT TEARDROP-SPOKED PROPELLER ASTERISK
        replacementHash.put("\u274B", "*");     // 274B  : HEAVY EIGHT TEARDROP-SPOKED PROPELLER ASTERISK
        replacementHash.put("\u066D", "*");     // 066D  : ARABIC FIVE POINTED STAR
        replacementHash.put("\u2E29", "))");     // 2E29  : RIGHT DOUBLE PARENTHESIS
        replacementHash.put("\u2986", ")");     // 2986  : RIGHT WHITE PARENTHESIS
        replacementHash.put("\uFF60", ")");     // FF60  : FULLWIDTH RIGHT WHITE PARENTHESIS
        replacementHash.put("\uFD3F", ")");     // FD3F  : ORNATE RIGHT PARENTHESIS
        replacementHash.put("\u2769", ")");     // 2769  : MEDIUM RIGHT PARENTHESIS ORNAMENT
        replacementHash.put("\u276B", ")");     // 276B  : MEDIUM FLATTENED RIGHT PARENTHESIS ORNAMENT
        replacementHash.put("\u2773", ")");     // 2773  : LIGHT RIGHT TORTOISE SHELL BRACKET ORNAMENT
        replacementHash.put("\uFE5A", ")");     // FE5A  : SMALL RIGHT PARENTHESIS
        replacementHash.put("\u0029", ")");     // 0029  : RIGHT PARENTHESIS
        replacementHash.put("\uFF09", ")");     // FF09  : FULLWIDTH RIGHT PARENTHESIS
        replacementHash.put("\u208E", ")");     // 208E  : SUBSCRIPT RIGHT PARENTHESIS
        replacementHash.put("\u207E", ")");     // 207E  : SUPERSCRIPT RIGHT PARENTHESIS
        replacementHash.put("\uFE36", ")");     // FE36  : PRESENTATION FORM FOR VERTICAL RIGHT PARENTHESIS
        replacementHash.put("\u2E28", "((");     // 2E28  : LEFT DOUBLE PARENTHESIS
        replacementHash.put("\u2985", "(");     // 2985  : LEFT WHITE PARENTHESIS
        replacementHash.put("\uFF5F", "(");     // FF5F  : FULLWIDTH LEFT WHITE PARENTHESIS
        replacementHash.put("\uFD3E", "(");     // FD3E  : ORNATE LEFT PARENTHESIS
        replacementHash.put("\u2768", "(");     // 2768  : MEDIUM LEFT PARENTHESIS ORNAMENT
        replacementHash.put("\u276A", "(");     // 276A  : MEDIUM FLATTENED LEFT PARENTHESIS ORNAMENT
        replacementHash.put("\uFE59", "(");     // FE59  : SMALL LEFT PARENTHESIS
        replacementHash.put("\u2772", "(");     // 2772  : LIGHT LEFT TORTOISE SHELL BRACKET ORNAMENT
        replacementHash.put("\u0028", "(");     // 0028  : LEFT PARENTHESIS
        replacementHash.put("\uFF08", "(");     // FF08  : FULLWIDTH LEFT PARENTHESIS
        replacementHash.put("\u208D", "(");     // 208D  : SUBSCRIPT LEFT PARENTHESIS
        replacementHash.put("\u207D", "(");     // 207D  : SUPERSCRIPT LEFT PARENTHESIS
        replacementHash.put("\uFE35", "(");     // FE35  : PRESENTATION FORM FOR VERTICAL LEFT PARENTHESIS
        replacementHash.put("\u214B", "&");     // 214B  : TURNED AMPERSAND
        replacementHash.put("\uFE60", "&");     // FE60  : SMALL AMPERSAND
        replacementHash.put("\u0026", "&");     // 0026  : AMPERSAND
        replacementHash.put("\uFF06", "&");     // FF06  : FULLWIDTH AMPERSAND
        replacementHash.put("\u204A", "&");     // 204A  : TIRONIAN SIGN ET
        replacementHash.put("\u16B3A", "&");     // 16B3A  : PAHAWH HMONG SIGN VOS THIAB
        replacementHash.put("\u1F670", "&");     // 1F670  : SCRIPT LIGATURE ET ORNAMENT
        replacementHash.put("\u1F671", "&");     // 1F671  : HEAVY SCRIPT LIGATURE ET ORNAMENT
        replacementHash.put("\u1F672", "&");     // 1F672  : LIGATURE OPEN ET ORNAMENT
        replacementHash.put("\u1F673", "&");     // 1F673  : HEAVY LIGATURE OPEN ET ORNAMENT
        replacementHash.put("\u1F674", "&");     // 1F674  : HEAVY AMPERSAND ORNAMENT
        replacementHash.put("\u1F675", "&");     // 1F675  : SWASH AMPERSAND ORNAMENT
        replacementHash.put("\uFE6A", "%");     // FE6A  : SMALL PERCENT SIGN
        replacementHash.put("\u0025", "%");     // 0025  : PERCENT SIGN
        replacementHash.put("\uFF05", "%");     // FF05  : FULLWIDTH PERCENT SIGN
        replacementHash.put("\u066A", "%");     // 066A  : ARABIC PERCENT SIGN
        replacementHash.put("\uFE69", "$");     // FE69  : SMALL DOLLAR SIGN
        replacementHash.put("\u0024", "$");     // 0024  : DOLLAR SIGN
        replacementHash.put("\uFF04", "$");     // FF04  : FULLWIDTH DOLLAR SIGN
        replacementHash.put("\uFE5F", "#");     // FE5F  : SMALL NUMBER SIGN
        replacementHash.put("\uFF03", "#");     // FF03  : FULLWIDTH NUMBER SIGN
        replacementHash.put("\u2755", "!");     // 2755  : WHITE EXCLAMATION MARK ORNAMENT
        replacementHash.put("\u2757", "!");     // 2757  : HEAVY EXCLAMATION MARK SYMBOL
        replacementHash.put("\uFE57", "!");     // FE57  : SMALL EXCLAMATION MARK
        replacementHash.put("\u0021", "!");     // 0021  : EXCLAMATION MARK
        replacementHash.put("\uFF01", "!");     // FF01  : FULLWIDTH EXCLAMATION MARK
        replacementHash.put("\u203C", "!");     // 203C  : DOUBLE EXCLAMATION MARK
        replacementHash.put("\u00A1", "!");     // 00A1  : INVERTED EXCLAMATION MARK
        replacementHash.put("\u055C", "!");     // 055C  : ARMENIAN EXCLAMATION MARK
        replacementHash.put("\u2762", "!");     // 2762  : HEAVY EXCLAMATION MARK ORNAMENT
        replacementHash.put("\u2763", "!");     // 2763  : HEAVY HEART EXCLAMATION MARK ORNAMENT
        replacementHash.put("\uFE15", "!");     // FE15  : PRESENTATION FORM FOR VERTICAL EXCLAMATION MARK
        replacementHash.put("\uA71D", "!");     // A71D  : MODIFIER LETTER RAISED EXCLAMATION MARK
        replacementHash.put("\u00AD", "-");     // 00AD  : SOFT HYPHEN
        replacementHash.put("\u1400", "-");     // 1400  : CANADIAN SYLLABICS HYPHEN
        replacementHash.put("\u1806", "-");     // 1806  : MONGOLIAN TODO SOFT HYPHEN
        replacementHash.put("\u2E3A", "-");     // 2E3A  : TWO-EM DASH
        replacementHash.put("\u2E3B", "-");     // 2E3B  : THREE-EM DASH
        replacementHash.put("\u2E40", "-");     // 2E40  : DOUBLE HYPHEN
        replacementHash.put("\u002D", "-");     // 002D  : HYPHEN-MINUS
        replacementHash.put("\uFE63", "-");     // FE63  : SMALL HYPHEN-MINUS
        replacementHash.put("\uFF0D", "-");     // FF0D  : FULLWIDTH HYPHEN-MINUS
        replacementHash.put("\u2010", "-");     // 2010  : HYPHEN
        replacementHash.put("\u058A", "-");     // 058A  : ARMENIAN HYPHEN
        replacementHash.put("\u2011", "-");     // 2011  : NON-BREAKING HYPHEN
        replacementHash.put("\u2012", "-");     // 2012  : FIGURE DASH
        replacementHash.put("\u2013", "-");     // 2013  : EN DASH
        replacementHash.put("\uFE32", "-");     // FE32  : PRESENTATION FORM FOR VERTICAL EN DASH
        replacementHash.put("\u2014", "-");     // 2014  : EM DASH
        replacementHash.put("\uFE58", "-");     // FE58  : SMALL EM DASH
        replacementHash.put("\uFE31", "-");     // FE31  : PRESENTATION FORM FOR VERTICAL EM DASH
        replacementHash.put("\u2015", "-");     // 2015  : HORIZONTAL BAR
        replacementHash.put("\u301C", "-");     // 301C  : WAVE DASH
        replacementHash.put("\u3030", "-");     // 3030  : WAVY DASH
        replacementHash.put("\u05BE", "-");     // 05BE  : HEBREW PUNCTUATION MAQAF
        replacementHash.put("\u2E17", "-");     // 2E17  : DOUBLE OBLIQUE HYPHEN
        replacementHash.put("\u2E1A", "-");     // 2E1A  : HYPHEN WITH DIAERESIS
        replacementHash.put("\u30A0", "-");     // 30A0  : KATAKANA-HIRAGANA DOUBLE HYPHEN
        replacementHash.put("\uFF07", "'");     // FF07  : FULLWIDTH APOSTROPHE
    }

    public static String getReplacement(String input) {
        if (input != null) {
            if (replacementHash.containsKey(input)) {
                return replacementHash.get(input);
            }
        }
        return null;
    }
}