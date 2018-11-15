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

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import keyterms.analyzer.Analysis;
import keyterms.analyzer.AnalysisFeature;
import keyterms.analyzer.profiles.model.EnumeratedFeature;
import keyterms.analyzer.profiles.model.FeatureModel;
import keyterms.analyzer.profiles.model.ModelFeature;
import keyterms.analyzer.profiles.model.NominalFeature;
import keyterms.testing.Tests;
import keyterms.util.text.parser.Parsers;
import keyterms.util.text.splitter.LineSplitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WekaForest_UT {

    static final String WEATHER_DATA =
            "sunny,hot,high,FALSE,no\n" +
                    "sunny,hot,high,TRUE,no\n" +
                    "overcast,hot,high,FALSE,yes\n" +
                    "rainy,mild,high,FALSE,yes\n" +
                    "rainy,cool,normal,FALSE,yes\n" +
                    "rainy,cool,normal,TRUE,no\n" +
                    "overcast,cool,normal,TRUE,yes\n" +
                    "sunny,mild,high,FALSE,no\n" +
                    "sunny,cool,normal,FALSE,yes\n" +
                    "rainy,mild,normal,FALSE,yes\n" +
                    "sunny,mild,normal,TRUE,yes\n" +
                    "overcast,mild,high,TRUE,yes\n" +
                    "overcast,hot,normal,FALSE,yes\n" +
                    "rainy,mild,high,TRUE,no\n";

    @Test
    @SuppressWarnings("unchecked")
    public void weatherSample()
            throws Exception {
        ModelFeature<Boolean> outputFeature = new EnumeratedFeature<>("play", Boolean.class,
                (text) -> Parsers.BOOLEANS.parse(text, null),
                (value) -> {
                    String asText = "?";
                    if (value != null) {
                        asText = (value) ? "yes" : "no";
                    }
                    return asText;
                });
        AnalysisFeature<Boolean> analyzerOutput = new AnalysisFeature<>("play", Boolean.class);
        FeatureModel<Boolean> featureModel = new FeatureModel<>(outputFeature)
                .addInputFeature(new NominalFeature("outlook"))
                .addInputFeature(new NominalFeature("temperature"))
                .addInputFeature(new NominalFeature("humidity"))
                .addInputFeature(new NominalFeature("windy"));
        WekaForestBuilder<Boolean> builder = new WekaForestBuilder<>(featureModel, analyzerOutput);
        List<String[]> rawRecords = new LineSplitter().split(WEATHER_DATA).stream()
                .map((line) -> line.split(","))
                .collect(Collectors.toList());
        for (String[] rawRecord : rawRecords) {
            Datum<Boolean> datum = new Datum<>(outputFeature, outputFeature.parse(rawRecord[rawRecord.length - 1]));
            for (int c = 0; c < (rawRecord.length - 1); c++) {
                ModelFeature<Object> feature = (ModelFeature<Object>)featureModel.getInputFeatures().get(c);
                Object value = feature.parse(rawRecord[c]);
                datum.setFeature(feature, value);
            }
            builder.addTrainingData(datum);
        }
        WekaForest<Boolean> forest = builder.build();
        testForest(rawRecords, forest);
        WekaForest<Boolean> copy = Tests.serialCopy(forest);
        testForest(rawRecords, forest);
        assertNotNull(copy);
        assertEquals(forest.getInputClasses(), copy.getInputClasses());
        assertEquals(forest.getOutputFeatures(), copy.getOutputFeatures());
        assertEquals(forest.producesRankings(), copy.producesRankings());
        assertEquals(forest.producesScores(), copy.producesScores());
        Assert.assertEquals(forest.getFeatureModel().getInputFeatures(), copy.getFeatureModel().getInputFeatures());
        assertEquals(forest.getFeatureModel().getOutputFeature(), copy.getFeatureModel().getOutputFeature());
        assertEquals(forest.getWekaModel(), copy.getWekaModel());
        assertEquals(forest.getOutputFeature(), copy.getOutputFeature());
        assertTrue(copy.isAvailable());
        testForest(rawRecords, copy);
    }

    @SuppressWarnings("unchecked")
    private void testForest(List<String[]> rawRecords, WekaForest<Boolean> forest) {
        FeatureModel<Boolean> featureModel = forest.getFeatureModel();
        ModelFeature<Boolean> outputFeature = featureModel.getOutputFeature();
        AnalysisFeature<Boolean> analyzerOutput = forest.getOutputFeature();
        for (String[] rawRecord : rawRecords) {
            Datum<Boolean> datum = new Datum<>(outputFeature, outputFeature.parse(rawRecord[rawRecord.length - 1]));
            for (int c = 0; c < (rawRecord.length - 1); c++) {
                ModelFeature<Object> feature = (ModelFeature<Object>)featureModel.getInputFeatures().get(c);
                Object value = feature.parse(rawRecord[c]);
                datum.setFeature(feature, value);
            }
            List<Analysis> results = forest.analyze(datum.getFeatureData());
            Boolean play = ((results != null) && (!results.isEmpty())) ? results.get(0).get(analyzerOutput) : null;
            assertNotNull(play);
            assertEquals(datum.getGroundTruth(), play);
        }
    }
}