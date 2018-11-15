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

import java.io.InputStream;
import java.nio.charset.Charset;
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
import keyterms.util.io.Streams;
import keyterms.util.text.Strings;

/**
 * The service endpoints specific to text transformation (transliteration).
 */
@Provider
@Singleton
@Path("/analyzer")
public class AnalyzerEndPoints {
    /**
     * Constructor.
     */
    public AnalyzerEndPoints() {
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
     * <p> Note: Specifying an analyzer which cannot accept binary input returns results based on the assumption that
     * the preferred analyzer's detected encoding is correct. </p>
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
            InputStream textData) {
        return profile(analyzerName, textData).stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the analysis results for the specified text data.
     *
     * <p> Note: Specifying an analyzer which cannot accept binary input returns results based on the assumption that
     * the preferred analyzer's detected encoding is correct. </p>
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
            InputStream textData) {
        List<Analysis> results = new ArrayList<>();
        try {
            Object data = Streams.readFully(textData);
            Analyzer analyzer = KeyTermsService.getAnalyzer(analyzerName);
            if (!analyzer.accepts(byte[].class)) {
                TextInfo textInfo = TextInfo.of(KeyTermsService.defaultAnalyzer.analyze(data).stream()
                        .findFirst()
                        .orElse(null));
                Charset encoding = (textInfo != null) ? Encoding.getCharset(textInfo.getEncoding()) : null;
                data = (encoding != null) ? Encoding.decode((byte[])data, encoding) : null;
            }
            List<Analysis> analyzerResults = analyzer.analyze(data);
            for (int r = 0; r < Math.min(analyzerResults.size(), KeyTermsService.MAX_RESULTS); r++) {
                Analysis result = analyzerResults.get(r);
                results.add(result);
            }
        } catch (Exception error) {
            throw new ServiceError(error);
        }
        return results;
    }

    /**
     * Get the analysis results for the specified text data.
     *
     * <p> Note: Analyzers which cannot accept binary input return results based on the assumption that the preferred
     * analyzer's detected encoding is correct. </p>
     *
     * @param textData The text data to profile.
     *
     * @return The analysis results for the specified text data.
     */
    @POST
    @Path("full_profile")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Keyed<String, Analysis>> fullProfile(InputStream textData) {
        List<Keyed<String, Analysis>> analyses = new ArrayList<>();
        try {
            Object data = Streams.readFully(textData);
            TextInfo textInfo = TextInfo.of(KeyTermsService.defaultAnalyzer.analyze(data).stream()
                    .findFirst()
                    .orElse(null));
            Charset encoding = (textInfo != null) ? Encoding.getCharset(textInfo.getEncoding()) : null;
            String text = (encoding != null) ? Encoding.decode((byte[])data, encoding) : null;
            getProducts().forEach((product) -> {
                Analyzer analyzer = KeyTermsService.getAnalyzer(product);
                List<Analysis> productResults = (analyzer.accepts(byte[].class))
                        ? analyzer.analyze(data)
                        : analyzer.analyze(text);
                for (int r = 0; r < Math.min(productResults.size(), KeyTermsService.MAX_RESULTS); r++) {
                    Analysis result = productResults.get(r);
                    analyses.add(new Keyed<>(product, result));
                }
            });
            getProfiles().forEach((product) -> {
                Analyzer analyzer = KeyTermsService.getAnalyzer(product);
                List<Analysis> productResults = (analyzer.accepts(byte[].class))
                        ? analyzer.analyze(data)
                        : analyzer.analyze(text);
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

    /**
     * Get a preview of the specified text data.
     *
     * <p> Note: Analyzers which cannot accept binary input return results based on the assumption that the preferred
     * analyzer's detected encoding is correct. </p>
     * <p> Note: If the chosen analyzer doesn't produce an encoding result, the default analyzer may still be used for
     * the preview function. </p>
     *
     * @param analyzerName The identifier for the analyzer to use.
     * @param textData The text data to profile.
     *
     * @return The preview text for the data.
     */
    @POST
    @Path("preview")
    @Produces(MediaType.TEXT_PLAIN)
    public String preview(
            @QueryParam("analyzer") String analyzerName,
            InputStream textData) {
        String preview;
        try {
            Object data = Streams.read(textData, KeyTermsService.PREVIEW_BYTES);
            Analyzer analyzer = KeyTermsService.getAnalyzer(analyzerName);
            if ((!analyzer.accepts(byte[].class)) || (!analyzer.produces(TextInfo.ENCODING))) {
                analyzer = KeyTermsService.defaultAnalyzer;
            }
            TextInfo textInfo = TextInfo.of(analyzer.analyze(data).stream().findFirst().orElse(null));
            Charset encoding = (textInfo != null) ? Encoding.getCharset(textInfo.getEncoding()) : null;
            preview = (encoding != null) ? Encoding.decode((byte[])data, encoding) : null;
        } catch (Exception error) {
            throw new ServiceError(error);
        }
        return preview;
    }
}