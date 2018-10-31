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

package keyterms.util.collect.basictrie;

import java.util.Hashtable;
import java.util.Vector;

import keyterms.util.collect.ITrie;
import keyterms.util.text.Strings;

/**
 * This class implements a Trie data structure to store words efficiently.
 */
public class Trie
        implements ITrie {

    // limit on the number of search results returned

    private static int MAX_RESULTS = 200;

    private int MaxResults;

    // The root node of the trie
    private TrieNode Root;

    // Whether or not to ignore case differences when constructing the rie
    private boolean IgnoreCase = true;

    // The number of full words added
    private int WordCount = 0;

    // Underspecified Constructors
    public Trie() {
        this(true, MAX_RESULTS);
    }

    public Trie(boolean ignoreCase) {
        this(ignoreCase, MAX_RESULTS);
    }

    // Full Constructor
    public Trie(boolean ignoreCase, int maxResults) {
        this.IgnoreCase = ignoreCase;
        this.Root = new TrieNode();
        this.Root.setNodeKey(" ");
        this.MaxResults = maxResults;
    }

    public void add(String word) {
        if (Strings.isBlank(word)) {
            return;
        }

        TrieNode cur = Root;
        TrieNode tmp;
        for (int i = 0; i < word.length(); i++) {
            String ch = word.substring(i, 1);
            if (IgnoreCase) {
                ch = ch.toLowerCase();
            }
            if (cur.Children == null) {
                cur.Children = new Hashtable<>();
            }
            if (!cur.Children.containsKey(ch)) {
                tmp = new TrieNode();
                tmp.setNodeKey(ch);
                cur.Children.put(ch, tmp);
            }
            cur = cur.Children.get(ch);
            cur.NoOfPrefix += 1;
        }
        cur.IsWord = true;
        WordCount++;
    }

    public int getWordCount() {
        return WordCount;
    }

    public Vector<String> getTermsStartingWithPrefix(String prefix) {
        return searchPrefix(prefix, MAX_RESULTS);
    }

    /**
     * Return a Vector of words that start with the specified prefix
     */
    public Vector<String> searchPrefix(String prefix, int max) {
        Vector<String> results = new Vector<>();
        TrieNode cur = Root;
        String pfx = "";
        boolean failed = false;

        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, 1);
            if (IgnoreCase) {
                ch = ch.toLowerCase();
            }
            if (cur.Children == null) {
                failed = true;
                break;
            }
            if (cur.Children.containsKey(ch)) {
                pfx += ch;
                cur = cur.Children.get(ch);
            } else {
                failed = true;
                break;
            }
        }
        if (cur.IsWord && !failed && results.size() <= max) {
            results.add(pfx);
        }
        if (!failed) {
            // continue down the trie for more results
            getMoreWords(cur, results, pfx, max);
        }
        return results;
    }

    public Vector<String> searchPrefix(String prefix) {
        return searchPrefix(prefix, MAX_RESULTS);
    }

    public int getPrefixCount(String prefix) {
        TrieNode cur = Root;
        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, 1);
            if (IgnoreCase) {
                ch = ch.toLowerCase();
            }
            if (cur.Children.containsKey(ch)) {
                cur = cur.Children.get(ch);
            } else {
                return 0;
            }
        }
        return cur.NoOfPrefix;
    }

    public boolean contains(String word) {
        if (Root == null || Strings.isBlank(word)) {
            return false;
        }
        boolean contains = true;
        TrieNode cur = Root;

        for (int i = 0; i < word.length(); i++) {
            String ch = word.substring(i, 1);
            if (IgnoreCase) {
                ch = ch.toLowerCase();
            }

            if (cur.Children != null && cur.Children.containsKey(ch)) {
                cur = cur.Children.get(ch);
            } else {
                contains = false;
                break;
            }
        }
        return contains;
    }

    public boolean contains(String word, boolean fullWordOnly) {
        if (Root == null || Strings.isBlank(word)) {
            return false;
        }
        if (fullWordOnly) {
            Vector<String> stringsStartingWithWord = this.searchPrefix(word, MAX_RESULTS);
            if (stringsStartingWithWord == null || stringsStartingWithWord.size() < 1) {
                return false;
            }
            return stringsStartingWithWord.contains(word);
        } else {
            return this.contains(word);
        }
    }

    private void getMoreWords(TrieNode cur, Vector<String> result, String prefix, int max) {
        if (cur.Children == null) {
            return;
        }
        for (TrieNode node : cur.Children.values()) {
            String tmp = prefix + node.NodeKey;
            if (node.IsWord) {
                if (result.size() >= max) {
                    break;
                } else {
                    result.add(tmp);
                }
            }
            getMoreWords(node, result, tmp, max);
        }
    }

}