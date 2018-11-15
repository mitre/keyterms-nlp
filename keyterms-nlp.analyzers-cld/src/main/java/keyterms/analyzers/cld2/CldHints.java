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

package keyterms.analyzers.cld2;

import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import keyterms.util.collect.Bags;

/**
 * A wrapper for the hints that can be passed into CLD2 detection methods.
 */
public class CldHints
        extends Structure {

    public String content_language_hint;
    public String tld_hint;
    public int encoding_hint;
    public int language_hint;

    public CldHints() {
        super();
    }

    public CldHints(Pointer peer) {
        super(peer);
    }

    CldHints(String content_language_hint, String tld_hint, int encoding_hint, int language_hint) {
        super();
        this.content_language_hint = content_language_hint;
        this.tld_hint = tld_hint;
        this.encoding_hint = encoding_hint;
        this.language_hint = language_hint;
    }

    @Override
    protected List<String> getFieldOrder() {
        return Bags.staticList("content_language_hint", "tld_hint", "encoding_hint", "language_hint");
    }
}