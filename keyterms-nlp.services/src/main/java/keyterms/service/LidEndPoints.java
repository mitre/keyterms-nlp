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

package keyterms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.Analyzer;
import keyterms.analyzer.CoreAnalyzers;
import keyterms.analyzer.profiles.WekaProfile;
import keyterms.analyzer.profiles.WekaProfiles;
import keyterms.analyzer.text.TextInfo;
import keyterms.rest.service.ServiceError;
import keyterms.util.collect.Keyed;
import keyterms.util.io.Encoding;
import keyterms.util.text.Strings;

/**
 * The service endpoints specific to text transformation (transliteration).
 */
@Provider
@Singleton
@Path("/lid")
public class LidEndPoints {
    /**
     * Constructor.
     */
    public LidEndPoints() {
        super();
    }

    /**
     * Get the identifier (weka profile name or analyzer id) of the default analyzer.
     *
     * @return The identifier for the default analyzer.
     */
    @GET
    @Path("default_analyzer")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPreferredAnalyzer() {
        return KeyTermsService.defaultAnalyzerKey;
    }

    /**
     * Get the products available for text analysis.
     *
     * @return The products available for text analysis.
     */
    @GET
    @Path("products")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> getProducts() {
        return CoreAnalyzers.getInstance().ids().stream()
                .filter((id) -> CoreAnalyzers.getInstance().get(id).produces(TextInfo.LANGUAGE))
                .map(Strings::toString)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Get the weka profiles available for text analysis.
     *
     * @return The weka profiles available for text analysis.
     */
    @GET
    @Path("profiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> getProfiles() {
        return WekaProfiles.getInstance().profiles().stream()
                .map(WekaProfile::getName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Get the best analysis result for the specified text data.
     *
     * @param analyzerName The identifier for the analyzer to use.
     * @param textData The text data to analyze.
     *
     * @return The best analysis result for the specified text data.
     */
    @POST
    @Path("analyze")
    @Produces(MediaType.APPLICATION_JSON)
    public Analysis analyze(
            @QueryParam("analyzer") String analyzerName,
            String textData) {
        return profile(analyzerName, textData).stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the analysis results for the specified text data.
     *
     * @param analyzerName The identifier for the analyzer to use.
     * @param textData The text data to profile.
     *
     * @return The analysis results for the specified text data.
     */
    @POST
    @Path("profile")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Analysis> profile(
            @QueryParam("analyzer") String analyzerName,
            String textData) {
        List<Analysis> results = new ArrayList<>();
        try {
            Object data = textData;
            Analyzer analyzer = KeyTermsService.getAnalyzer(analyzerName);
            if (analyzer.produces(TextInfo.LANGUAGE)) {
                if (!analyzer.accepts(CharSequence.class)) {
                    data = Encoding.encode(textData, Encoding.UTF8);
                }
                List<Analysis> analyzerResults = analyzer.analyze(data);
                for (int r = 0; r < Math.min(analyzerResults.size(), KeyTermsService.MAX_RESULTS); r++) {
                    Analysis result = analyzerResults.get(r);
                    results.add(result);
                }
            }
        } catch (Exception error) {
            throw new ServiceError(error);
        }
        return results;
    }

    /**
     * Get the analysis results for the specified text data.
     *
     * @param textData The text data to profile.
     *
     * @return The analysis results for the specified text data.
     */
    @POST
    @Path("full_profile")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Keyed<String, Analysis>> fullProfile(String textData) {
        List<Keyed<String, Analysis>> analyses = new ArrayList<>();
        try {
            byte[] binary = Encoding.encode(textData, Encoding.UTF8);
            getProducts().forEach((product) -> {
                Analyzer analyzer = KeyTermsService.getAnalyzer(product);
                if (analyzer.produces(TextInfo.LANGUAGE)) {
                    List<Analysis> productResults = (analyzer.accepts(CharSequence.class))
                            ? analyzer.analyze(textData)
                            : analyzer.analyze(binary);
                    for (int r = 0; r < Math.min(productResults.size(), KeyTermsService.MAX_RESULTS); r++) {
                        Analysis result = productResults.get(r);
                        analyses.add(new Keyed<>(product, result));
                    }
                }
            });
            getProfiles().forEach((product) -> {
                Analyzer analyzer = KeyTermsService.getAnalyzer(product);
                List<Analysis> productResults = analyzer.analyze(textData);
                for (int r = 0; r < Math.min(productResults.size(), KeyTermsService.MAX_RESULTS); r++) {
                    Analysis result = productResults.get(r);
                    analyses.add(new Keyed<>(product, result));
                }
            });
        } catch (Exception error) {
            throw new ServiceError(error);
        }
        return analyses;
    }
}