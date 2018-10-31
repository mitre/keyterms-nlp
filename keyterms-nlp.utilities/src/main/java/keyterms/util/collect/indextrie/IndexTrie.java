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

package keyterms.util.collect.indextrie;

import java.util.Hashtable;
import java.util.Vector;

import keyterms.util.collect.ITrie;
import keyterms.util.text.Strings;

public class IndexTrie
        implements ITrie {

    // limit on the number of search results returned
    private static final int MAX_RESULTS = 200;

    // limit on the length of prefixes stored
    private static final int PFX_LEN = -1;

    // The length of the prefixes to be chopped from the front of a word for storage in this index
    // If this length is less than one, whole words will be used, which is the default
    public int PrefixLength;

    // The maximum number of results to return on any path
    private final int MaxResults;

    // The root node of the Trie
    private final IndexTrieNode Root;

    // Whether or not to ignore case differences when constructing the trie
    private boolean IgnoreCase = true;

    // The number of full words added
    private int WordCount = 0;

    // Under-specified Constructors
    // needed for de-serialization (in c#)
    public IndexTrie() {
        this(true, PFX_LEN, MAX_RESULTS);
    }

    public IndexTrie(boolean ignoreCase) {
        this(ignoreCase, PFX_LEN, MAX_RESULTS);
    }

    // Full Constructor
    public IndexTrie(boolean ignoreCase, int prefixLength, int maxResults) {
        this.IgnoreCase = ignoreCase;
        this.Root = new IndexTrieNode();
        this.Root.setNodeKey(" ");
        this.PrefixLength = prefixLength;
        this.MaxResults = maxResults;
    }

    public int getPrefixLength() {
        return PrefixLength;
    }

    public void setPrefixLength(int prefixLength) {
        PrefixLength = prefixLength;
    }

    public void add(String word, int index) {
        if (Strings.isBlank(word)) {
            return;
        }
        String prefix = getPrefix(word);

        IndexTrieNode cur = Root;
        IndexTrieNode tmp;
        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, 1);
            if (IgnoreCase) {
                ch = ch.toLowerCase();
            }
            if (cur.Children == null) {
                cur.Children = new Hashtable<>();
            }
            if (!cur.Children.containsKey(ch)) {
                tmp = new IndexTrieNode();
                tmp.setNodeKey(ch);
                cur.Children.put(ch, tmp);
            }
            cur = cur.Children.get(ch);
            cur.NoOfPrefix += 1;
        }
        cur.IsWord = true;
        cur.AddIndex(index);
        WordCount++;
    }

    public void add(String word) {
        if (Strings.isBlank(word)) {
            return;
        }

        IndexTrieNode cur = Root;
        IndexTrieNode tmp;
        for (int i = 0; i < word.length(); i++) {
            String ch = word.substring(i, i + 1);
            if (IgnoreCase) {
                ch = ch.toLowerCase();
            }
            if (cur.Children == null) {
                cur.Children = new Hashtable<>();
            }
            if (!cur.Children.containsKey(ch)) {
                tmp = new IndexTrieNode();
                tmp.setNodeKey(ch);
                cur.Children.put(ch, tmp);
            }
            cur = cur.Children.get(ch);
            cur.NoOfPrefix += 1;
        }
        cur.IsWord = true;
        WordCount++;
    }

    public String getPrefix(String word) {
        if (PrefixLength < 1) {
            return word;
        }
        int adjustedLength = PrefixLength;
        if (adjustedLength > word.length()) {
            adjustedLength = word.length();
        }
        return word.substring(0, adjustedLength);
    }

    public int getWordCount() {
        return WordCount;
    }

    public Vector<String> getTermsStartingWithPrefix(String prefix) {
        return searchPrefix(prefix, MAX_RESULTS);
    }

    /**
     * Return a list of words that start with the specified prefix
     */
    public Vector<String> searchPrefix(String prefix, int max) {
        Vector<String> results = new Vector<>();
        IndexTrieNode cur = Root;
        String pfx = "";
        boolean failed = false;

        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, i + 1);
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

    /**
     * Return a list of word ids that start with the specified prefix
     *
     * @param prefix the prefix
     * @param max the maximum number of values to return
     *
     * @return a list of word ids that start with the specified prefix
     */
    public Vector<Integer> searchPrefixForIndexes(String prefix, int max) {
        Vector<Integer> results = new Vector<>();
        IndexTrieNode cur = Root;
        String pfx = "";
        boolean failed = false;

        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, i + 1);
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
            if (cur.Indexes != null && cur.Indexes.size() > 0) {
                results.addAll(cur.Indexes);
            }
        }
        if (!failed) {
            // continue down the trie for more results
            getMoreWordsForIndex(cur, results, pfx, max);
        }
        return results;
    }

    public int getPrefixCount(String prefix) {
        IndexTrieNode cur = Root;
        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, i + 1);
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
        IndexTrieNode cur = Root;

        for (int i = 0; i < word.length(); i++) {
            String ch = word.substring(i, i + 1);
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

    private void getMoreWordsForIndex(IndexTrieNode cur, Vector<Integer> results, String prefix, int max) {
        if (cur.Children == null) {
            return;
        }
        for (IndexTrieNode node : cur.Children.values()) {
            String tmp = prefix + node.NodeKey;
            if (node.IsWord) {
                if (results.size() >= max) {
                    break;
                } else {
                    if (node.Indexes != null && node.Indexes.size() > 0) {
                        results.addAll(node.Indexes);
                    }
                }
            }
            getMoreWordsForIndex(node, results, tmp, max);
        }
    }

    private void getMoreWords(IndexTrieNode cur, Vector<String> result, String prefix, int max) {
        if (cur.Children == null) {
            return;
        }
        for (IndexTrieNode node : cur.Children.values()) {
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