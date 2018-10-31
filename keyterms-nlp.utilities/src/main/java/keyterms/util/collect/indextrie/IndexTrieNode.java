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

public class IndexTrieNode {

    public String NodeKey;

    public int NoOfPrefix;

    public Hashtable<String, IndexTrieNode> Children;

    public boolean IsWord;

    public Vector<Integer> Indexes;

    public IndexTrieNode() {
        Indexes = new Vector<>();
    }

    public void AddIndex(int index) {
        if (!Indexes.contains(index)) // XXX might be faster to not bother with this check
        {
            Indexes.add(index);
        }
    }

    public String getNodeKey() {
        return NodeKey;
    }

    public void setNodeKey(String nodeKey) {
        NodeKey = nodeKey;
    }

    public int getNoOfPrefix() {
        return NoOfPrefix;
    }

    public void setNoOfPrefix(int noOfPrefix) {
        NoOfPrefix = noOfPrefix;
    }

    public Hashtable<String, IndexTrieNode> getChildren() {
        return Children;
    }

    public void setChildren(Hashtable<String, IndexTrieNode> children) {
        Children = children;
    }

    public boolean getIsWord() {
        return IsWord;
    }

    public void setIsWord(boolean isWord) {
        IsWord = isWord;
    }

    public Vector<Integer> getIndexes() {
        return Indexes;
    }

    public void setIndexes(Vector<Integer> indexes) {
        Indexes = indexes;
    }

}