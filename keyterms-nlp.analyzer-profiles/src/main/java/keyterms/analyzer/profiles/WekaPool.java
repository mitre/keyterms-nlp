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

package keyterms.analyzer.profiles;

import java.util.Set;

import keyterms.analyzer.Analyzer;
import keyterms.analyzer.AnalyzerFactory;
import keyterms.analyzer.AnalyzerId;
import keyterms.analyzer.AnalyzerPool;

/**
 * An analyzer thread pool specific to Weka analyzer profiles.
 */
public class WekaPool
        extends AnalyzerPool {
    /**
     * Constructor.
     *
     * @param factory The factory used to create new instances of the underlying analyzer.
     */
    public WekaPool(AnalyzerFactory factory) {
        super(factory);
    }

    /**
     * Get the identifiers for analyzers that must be present in the core analyzer pools for the analysis models to
     * function correctly.
     *
     * @return The identifiers for the required analyzers.
     */
    public Set<AnalyzerId> getRequiredAnalyzers() {
        Analyzer analyzer = null;
        try {
            analyzer = allocate();
            return ((WekaAnalyzer)analyzer).getRequiredAnalyzers();
        } finally {
            if (analyzer != null) {
                release(analyzer);
            }
        }
    }
}