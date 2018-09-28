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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import keyterms.util.collect.storagetrie.StorageTrie;
import keyterms.util.lang.Lazy;
import keyterms.util.text.Strings;

/**
 * A class which identifies sections of text represented by Emojicon data and sections them off.
 */
public class EmoteTokenizer {
    /**
     * The composite index of emoji and emoticon values.
     */
    private static final Lazy<StorageTrie<Emote>> EMOJICON_INDEX = new Lazy<>(() -> {
        StorageTrie<Emote> index = new StorageTrie<>(false);
        Emoji.VALUES.value().forEach(index::add);
        Emoticon.VALUES.value().forEach(index::add);
        return index;
    });

    /**
     * The index used to find emoji and emoticons given their textual representations.
     */
    private final StorageTrie<Emote> emojiData;

    /**
     * Constructor.
     */
    public EmoteTokenizer() {
        this(EMOJICON_INDEX.value());
    }

    /**
     * Constructor.
     *
     * @param emojiData The index used to find emoji and emoticons given their textual representations.
     */
    EmoteTokenizer(StorageTrie<Emote> emojiData) {
        super();
        if (emojiData == null) {
            throw new NullPointerException("Emoji index is required.");
        }
        this.emojiData = emojiData;
    }

    /**
     * Tokenize the specified text, breaking the text into tokens containing either text, or emoji/emoticon definitions.
     *
     * @param text The text to tokenize.
     *
     * @return The list of tokens found within the text.
     */
    public List<Token> tokenize(CharSequence text) {
        List<Token> tokens = new ArrayList<>();
        if (text != null) {
            String normalText = Normalizer.normalize(text, Normalizer.Form.NFKC);
            if (normalText.length() > 0) {
                int start = 0;
                StringBuilder prefix = new StringBuilder();
                Token candidate = null;
                for (int c = 0; c < normalText.length(); c++) {
                    prefix.append(normalText.charAt(c));
                    if (emojiData.contains(prefix.toString())) {
                        Object emoji = emojiData.getDataForWord(prefix.toString());
                        if (emoji != null) {
                            candidate = new Token(start, emoji);
                        }
                    } else {
                        if (candidate != null) {
                            tokens.add(candidate);
                            String candidateText = Strings.toString(candidate.getData());
                            prefix.delete(0, candidateText.length());
                            c -= prefix.length();
                            candidate = null;
                        } else {
                            Token last = (!tokens.isEmpty()) ? tokens.get(tokens.size() - 1) : null;
                            if ((last != null) && (last.data instanceof String)) {
                                last.data = Strings.toString(last.data) + prefix.charAt(0);
                                last.text = Strings.toString(last.data);
                                c = start;
                            } else {
                                tokens.add(new Token(start, prefix.toString()));
                            }
                        }
                        prefix.setLength(0);
                        start = c + 1;
                    }
                }
                if (candidate != null) {
                    String candidateText = Strings.toString(candidate.getData());
                    tokens.add(candidate);
                    prefix.delete(0, candidateText.length());
                    start = candidate.getStop() + 1;
                }
                if (prefix.length() > 0) {
                    Token last = (!tokens.isEmpty()) ? tokens.get(tokens.size() - 1) : null;
                    if ((last != null) && (last.data instanceof String)) {
                        last.data = Strings.toString(last.data) + prefix;
                        last.text = Strings.toString(last.data);
                    } else {
                        tokens.add(new Token(start, prefix.toString()));
                    }
                }
            }
        }
        return tokens;
    }

    /**
     * A single token for the emojicon tokenizer.
     *
     * <p> The token contains a type (which is the simple class name (no package) of the token data,
     * the index at which the token started in the original text, and the token data. </p>
     *
     * <p> The token data will be one of a "TEXT", "EMOJI" or "EMOTICON". </p>
     */
    public static class Token {

        /**
         * The type of data contained in the token.
         *
         * <p> This will be one of "TEXT", "EMOJI", or "EMOTICON". </p>
         */
        private String type;

        /**
         * The index in the original text where the token data was located.
         */
        private final int start;

        /**
         * The original text of the token.
         */
        private String text;

        /**
         * The token data.
         */
        private Object data;

        /**
         * Constructor.
         *
         * @param start The index in the original text where the token data was located.
         * @param data The token data.
         */
        Token(int start, Object data) {
            type = (data instanceof CharSequence) ? "TEXT" :
                    Strings.toUpperCase(Strings.toSnakeCase(data.getClass().getSimpleName()));
            this.start = start;
            text = data.toString();
            this.data = data;
        }

        /**
         * Get the type of data contained in the token.
         *
         * <p> This will be one of "TEXT", "EMOJI" or "EMOTICON". </p>
         *
         * @return The type of data contained in the token.
         */
        public String getType() {
            return type;
        }

        /**
         * Get the original text of the token.
         *
         * @return The original text of the token.
         */
        public String getText() {
            return text;
        }

        /**
         * Get the index in the original text where the token data was located.
         *
         * @return The start index of the token in the original text.
         */
        public int getStart() {
            return start;
        }

        /**
         * Get the last index (in the original text) which contained characters that are part of the token.
         *
         * <p> This index is non-inclusive (as in {@link String#substring(int, int)}). </p>
         *
         * @return The last index of the token (non-inclusive) in the original text.
         */
        public int getStop() {
            return start + Strings.toString(data).length();
        }

        /**
         * Get the token data.
         *
         * <p> The token data will either be a {@code String}, an {@code Emoji} or an {@code Emoticon} object. </p>
         *
         * @return The token data.
         */
        public Object getData() {
            return data;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "[" + type + ": " + start + "-" + getStop() + ": " + data + "]";
        }
    }
}