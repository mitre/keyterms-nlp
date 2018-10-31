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

package keyterms.util.collect.storagetrie;

import java.util.Vector;

import keyterms.util.collect.ITrie;
import keyterms.util.text.Strings;

public class StorageTrie<T>
        implements ITrie {

    /* limit on the number of search results returned */
    private static final int DEFAULT_MAX_RESULTS = 200;

    /* limit on the length of prefixes stored */
    private static final int DEFAULT_PREFIX_LENGTH = -1;

    /* length of the prefixes to be chopped from the front of a word for storage in this trie. if this length is
    less than one, whole words will be used. */
    private int prefixLength;

    /* root of the trie */
    private final StorageTrieNode<T> root;

    /* whether or not to ignore case differences when constructing the trie */
    private boolean ignoreCase = true;

    /* the total number of words added to this trie */
    private int wordCount = 0;

    /**
     * Construct a default instance of this class.
     */
    public StorageTrie() {
        this(true, DEFAULT_PREFIX_LENGTH);
    }

    /**
     * Construct an instance of this class.
     *
     * @param ignoreCase whether to ignore case for added words or not
     */
    public StorageTrie(boolean ignoreCase) {
        this(ignoreCase, DEFAULT_PREFIX_LENGTH);
    }

    /**
     * Construct an instance of this class.
     *
     * @param ignoreCase whether to ignore case for added words or not
     * @param prefixLength the length of prefixes to be chopped from the front of a word for storage in this trie
     */
    public StorageTrie(boolean ignoreCase, int prefixLength) {
        this.ignoreCase = ignoreCase;
        this.root = new StorageTrieNode<>();
        this.root.nodeKey = " ";
        this.prefixLength = prefixLength;
    }

    /**
     * Add a word to this trie.
     *
     * @param word the word to add
     * @param data the data to store at the leaf node for the added word
     */
    public void add(String word, T data) {
        if (Strings.isBlank(word)) {
            return;
        }

        String prefix = getPrefix(word);
        StorageTrieNode<T> cur = root;
        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, i + 1);
            if (ignoreCase) {
                ch = ch.toLowerCase();
            }

            if (!cur.children.containsKey(ch)) {
                StorageTrieNode<T> tmp = new StorageTrieNode<>();
                tmp.nodeKey = ch;
                cur.children.put(ch, tmp);
            }

            cur = cur.children.get(ch);
            cur.prefixCount++;

        }

        cur.isWord = true;
        wordCount++;

        if (data != null) {
            cur.data = data;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void add(String word) {
        add(word, null);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(String word) {
        if (root == null || Strings.isBlank(word)) {
            return false;
        }
        boolean contains = true;
        StorageTrieNode<T> cur = root;

        for (int i = 0; i < word.length(); i++) {
            String ch = word.substring(i, i + 1);
            if (ignoreCase) {
                ch = ch.toLowerCase();
            }

            if (cur.children.containsKey(ch)) {
                cur = cur.children.get(ch);
            } else {
                contains = false;
                break;
            }
        }
        return contains;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(String word, boolean fullWordOnly) {
        if (root == null || Strings.isBlank(word)) {
            return false;
        }
        if (fullWordOnly) {
            Vector<String> stringsStartingWithWord = this.searchPrefix(word, DEFAULT_MAX_RESULTS);
            return stringsStartingWithWord != null && stringsStartingWithWord.size() >= 1 &&
                    stringsStartingWithWord.contains(word);
        } else {
            return this.contains(word);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Vector<String> searchPrefix(String prefix, int max) {
        Vector<String> results = new Vector<>();
        StorageTrieNode<T> cur = root;
        String pfx = "";
        boolean failed = false;

        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, i + 1);
            if (ignoreCase) {
                ch = ch.toLowerCase();
            }

            if (cur.children.containsKey(ch)) {
                pfx += ch;
                cur = cur.children.get(ch);
            } else {
                failed = true;
                break;
            }
        }

        if (cur.isWord && !failed && results.size() <= max) {
            results.add(pfx);
        }

        if (!failed) {
            getMoreWords(cur, results, pfx, max);
        }

        return results;
    }

    /**
     * @param prefix the prefix to search for
     *
     * @return a list of all words in the trie that begin with (or equal) the prefix, up to the default maximum number
     */
    public Vector<String> searchPrefix(String prefix) {
        return searchPrefix(prefix, DEFAULT_MAX_RESULTS);
    }

    /**
     * Return a list of data items for words that start with the specified prefix
     *
     * @param prefix the prefix
     * @param max the maximum number of data items to return
     *
     * @return the specified list of data items
     */
    public Vector<T> searchPrefixForData(String prefix, int max) {
        Vector<T> results = new Vector<>();
        StorageTrieNode<T> cur = root;
        boolean failed = false;

        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, i + 1);
            if (ignoreCase) {
                ch = ch.toLowerCase();
            }

            if (cur.children.containsKey(ch)) {
                cur = cur.children.get(ch);
            } else {
                failed = true;
                break;
            }
        }

        if (cur.isWord && !failed && results.size() <= max) {
            T curData = cur.data;
            if (curData != null) {
                results.add(curData);
            }
        }
        if (!failed) {
            // continue down the trie for more results
            getMoreWordsForData(cur, results, max);
        }
        return results;
    }

    /**
     * @param prefix the prefix to search for
     *
     * @return a list of the data items associated with the supplied prefix
     */
    public Vector<T> searchPrefixForData(String prefix) {
        return searchPrefixForData(prefix, DEFAULT_MAX_RESULTS);
    }

    /**
     * @param word the word for which to get the prefix
     *
     * @return the prefix of a word
     */
    public String getPrefix(String word) {
        int adjustedPrefixLength = Math.min(prefixLength, word.length());
        return (adjustedPrefixLength < 1 ? word : word.substring(0, adjustedPrefixLength));
    }

    /**
     * @param prefix the prefix to count
     *
     * @return the number of times that the supplied prefix has been added to the trie
     */
    public int getPrefixCount(String prefix) {
        if (prefix.equals("")) {
            return wordCount;
        }

        StorageTrieNode<T> cur = root;
        for (int i = 0; i < prefix.length(); i++) {
            String ch = prefix.substring(i, i + 1);
            if (ignoreCase) {
                ch = ch.toLowerCase();
            }
            if (cur.children.containsKey(ch)) {
                cur = cur.children.get(ch);
            } else {
                return 0;
            }
        }

        return cur.prefixCount;
    }

    public T getDataForWord(String word) {
        StorageTrieNode<T> cur = null;
        if ((word != null) && (word.length() > 0)) {
            cur = root;
            for (int c = 0; c < word.length(); c++) {
                String ch = word.substring(c, c + 1);
                if (ignoreCase) {
                    ch = ch.toLowerCase();
                }
                if (cur.children.containsKey(ch)) {
                    cur = cur.children.get(ch);
                } else {
                    cur = null;
                    break;
                }
            }
        }
        return ((cur != null) && (cur.isWord)) ? cur.data : null;
    }

    /* getters and setters */
    /* ----------------------------------------------------------------------------------------------------- */

    /**
     * @return the prefix length of this trie
     */
    public int getPrefixLength() {
        return prefixLength;
    }

    /**
     * @param prefixLength the new prefix length for this trie
     */
    public void setPrefixLength(int prefixLength) {
        this.prefixLength = prefixLength;
    }

    /**
     * @return the number of words that have been added to this trie
     */
    public int getWordCount() {
        return wordCount;
    }

    /* private methods */
    /* ----------------------------------------------------------------------------------------------------- */

    private void getMoreWordsForData(StorageTrieNode<T> cur, Vector<T> results, int max) {
        for (StorageTrieNode<T> node : cur.children.values()) {
            if (node.isWord) {
                if (results.size() >= max) {
                    break;
                } else {
                    T curData = node.data;
                    if (curData != null) {
                        results.add(curData);
                    }
                }
            }

            getMoreWordsForData(node, results, max);
        }
    }

    private void getMoreWords(StorageTrieNode<T> cur, Vector<String> result, String prefix, int max) {
        for (StorageTrieNode<T> node : cur.children.values()) {
            String temp = prefix + node.nodeKey;
            if (node.isWord) {
                if (result.size() >= max) {
                    break;
                } else {
                    result.add(temp);
                }
            }
            getMoreWords(node, result, temp, max);
        }
    }

}