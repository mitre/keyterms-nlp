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

package keyterms.nlp.emoji;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import keyterms.util.text.Strings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EmoteTokenizer_UT {

    private static final EmoteTokenizer emojiconTokenizer = new EmoteTokenizer();

    @Test
    public void findEmoticon() {
        List<EmoteTokenizer.Token> tokens = emojiconTokenizer.tokenize("  :(  ");
        assertNotNull(tokens);
        assertEquals(3, tokens.size());
        EmoteTokenizer.Token token = tokens.get(0);
        assertEquals(0, token.getStart());
        assertEquals(2, token.getStop());
        assertEquals("  ", Strings.toString(token.getData()));
        token = tokens.get(1);
        assertEquals(2, token.getStart());
        assertEquals(4, token.getStop());
        assertTrue(token.getData() instanceof Emoticon);
        assertTrue(((Emoticon)token.getData()).getLabels().contains("frowning"));
        token = tokens.get(2);
        assertEquals(4, token.getStart());
        assertEquals(6, token.getStop());
        assertEquals("  ", Strings.toString(token.getData()));
    }

    @Test
    public void findEmoji() {
        List<EmoteTokenizer.Token> tokens = emojiconTokenizer.tokenize("jello ☹ ☹ ☹ pudding");
        assertNotNull(tokens);
        assertEquals(7, tokens.size());
        EmoteTokenizer.Token token = tokens.get(0);
        assertEquals(0, token.getStart());
        assertEquals(6, token.getStop());
        assertEquals("jello ", Strings.toString(token.getData()));
        token = tokens.get(1);
        assertEquals(6, token.getStart());
        assertEquals(7, token.getStop());
        assertTrue(token.getData() instanceof Emoji);
        Assert.assertEquals("white frowning face", ((Emoji)token.getData()).getDescription());
        token = tokens.get(2);
        assertEquals(7, token.getStart());
        assertEquals(8, token.getStop());
        assertEquals(" ", Strings.toString(token.getData()));
        token = tokens.get(6);
        assertEquals(11, token.getStart());
        assertEquals(19, token.getStop());
        assertEquals(" pudding", Strings.toString(token.getData()));
        tokens = emojiconTokenizer.tokenize("\uD83D\uDE03\uD83D\uDE04\uD83D\uDE06\uD83D\uDE0D");
        assertEquals(4, tokens.size());
        tokens.forEach((t) -> assertEquals(t.toString(), "EMOJI", t.getType()));
        tokens = emojiconTokenizer.tokenize("☺️\uD83D\uDE42\uD83D\uDE0A\uD83D\uDE00\uD83D\uDE01");
        assertEquals(5, tokens.size());
        tokens.forEach((t) -> assertEquals(t.toString(), "EMOJI", t.getType()));
        // End with emoji pending.
        tokens = emojiconTokenizer.tokenize("☺️\uD83D\uDE42\uD83D\uDE0A\uD83D\uDE00\uD83D\uDE01\uD83D");
        assertEquals(6, tokens.size());
        token = tokens.get(5);
        assertEquals("TEXT", token.getType());
        assertEquals("\uD83D", Strings.toString(token.getData()));
    }

    @Test
    public void smallNonEmojiconText() {
        List<EmoteTokenizer.Token> tokens = emojiconTokenizer.tokenize(":");
        assertNotNull(tokens);
        assertEquals(1, tokens.size());
        EmoteTokenizer.Token token = tokens.get(0);
        assertEquals("TEXT", token.getType());
        assertEquals(0, token.getStart());
        assertEquals(1, token.getStop());
        assertEquals(":", Strings.toString(token.getData()));
    }

    @Test
    public void findFish() {
        String text = "I am very \uD83D\uDE42 that you are not :( in \uD83C\uDDEB\uD83C\uDDEF.\n" +
                "Enjoy the: <>< :D!!";
        List<EmoteTokenizer.Token> tokens = emojiconTokenizer.tokenize(text);
        assertNotNull(tokens);
        assertEquals(11, tokens.size());
        EmoteTokenizer.Token token = tokens.get(7);
        assertEquals("EMOTICON", token.getType());
        Emoticon emoticon = (Emoticon)token.getData();
        assertEquals("<><", emoticon.getText());
        assertEquals("fish", emoticon.getDescription());
        token = tokens.get(9);
        assertEquals("EMOTICON", token.getType());
        emoticon = (Emoticon)token.getData();
        assertEquals(":D", emoticon.getText());
        assertEquals("laughing, big grin, laugh with glasses, or wide-eyed surprise", emoticon.getDescription());
    }
}