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

package keyterms.util.collect;

import java.util.Vector;

/**
 * Trie interface.
 */
public interface ITrie {

    /**
     * Add a word to this trie.
     *
     * @param word the word to add
     */
    void add(String word);

    /**
     * @param prefix the prefix to search for
     * @param max the maximum number of results to return
     *
     * @return a list of all words in the trie that begin with (or equal) the prefix
     */
    Vector<String> searchPrefix(String prefix, int max);

    Vector<String> searchPrefix(String prefix);

    /**
     * @param prefix the prefix to count
     *
     * @return the number of items that begin with the supplied prefix
     */
    int getPrefixCount(String prefix);

    /**
     * @param word the word to search for
     *
     * @return true if a matching string is contained in the trie (even partially); else false
     */
    boolean contains(String word);

    /**
     * @param word the word to search for
     * @param fullWord whether to search for the full word only
     *
     * @return true if a matching full word is contained in the trie
     */
    boolean contains(String word, boolean fullWord);
}