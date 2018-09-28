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

package keyterms.util;

import java.util.HashMap;

/**
 * Class for storing a table of known comparison values for pairs of same-type objects - comparison values that should
 * be stored and recalled, rather than computed anew. Uses an underlying HashMap for storage.
 */
public class ComparisonsTable<T> {

    private final HashMap<T, HashMap<T, Double>> hashMap = new HashMap<>();

    /**
     * Construct an instance of this class.
     */
    public ComparisonsTable() {
    }

    /**
     * Add an entry to this comparison table.
     *
     * @param item1 the first item
     * @param item2 the second item
     * @param score the score associated with these two items
     */
    public void addEntry(T item1, T item2, double score) {
        HashMap<T, Double> inner1 = hashMap.getOrDefault(item1, new HashMap<>());
        inner1.put(item2, score);
        hashMap.put(item1, inner1);

        HashMap<T, Double> inner2 = hashMap.getOrDefault(item2, new HashMap<>());
        inner2.put(item1, score);
        hashMap.put(item2, inner2);
    }

    /**
     * Retrieve the score associated with two items.
     *
     * @param item1 the first item
     * @param item2 the second item
     *
     * @return the score associated with the two provided items, if both present in table; else null
     */
    public Double lookupScore(T item1, T item2) {
        Double returnValue = null;
        if (hashMap.containsKey(item1) && hashMap.get(item1).containsKey(item2)) {
            returnValue = hashMap.get(item1).get(item2);
        } else {
            if (hashMap.containsKey(item2) && hashMap.get(item2).containsKey(item1)) {
                returnValue = hashMap.get(item2).get(item1);
            }
        }
        return returnValue;
    }
}