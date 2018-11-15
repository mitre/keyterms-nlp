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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import keyterms.util.collect.Bags;
import keyterms.util.io.Serialization;
import keyterms.util.math.Statistics;
import keyterms.util.text.splitter.LineSplitter;
import keyterms.util.time.Timing;

import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

import static org.junit.Assert.assertEquals;

public class Weka_UT {

    // Exploratory code to figure out how to use the WEKA random forest library.

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

    private static Logger getLogger() {
        return LoggerFactory.getLogger(Weka_UT.class.getSimpleName());
    }

    @Test
    public void weatherSample()
            throws Exception {
        // Create the raw model.
        ArrayList<Attribute> model = new ArrayList<>();
        model.add(new Attribute("outlook", Bags.staticList("sunny", "overcast", "rainy")));
        model.add(new Attribute("temperature", Bags.staticList("hot", "mild", "cool")));
        model.add(new Attribute("humidity", Bags.staticList("high", "normal")));
        model.add(new Attribute("windy", Bags.staticList("TRUE", "FALSE")));
        model.add(new Attribute("play", Bags.staticList("yes", "no")));

        // Create the training data set.
        Instances dataSet = new Instances("weather_nominal", model, 100);
        dataSet.setClassIndex(model.size() - 1);
        List<String[]> rawRecords = new LineSplitter().split(WEATHER_DATA).stream()
                .map((line) -> line.split(","))
                .collect(Collectors.toList());
        for (String[] datum : rawRecords) {
            Instance record = new SparseInstance(model.size());
            record.setDataset(dataSet);
            for (int a = 0; a < model.size(); a++) {
                String name = model.get(a).name();
                Attribute attribute = dataSet.attribute(name);
                attribute.addStringValue(datum[a]);
                record.setValue(attribute, datum[a]);
            }
            dataSet.add(record);
        }

        // Create and train the forest.
        RandomForest forest = new RandomForest();
        Timing timing = new Timing();
        forest.buildClassifier(dataSet);
        timing.finish();
        getLogger().info("Trained random forest in: {}", timing.summary(2));

        // Check that the forest can correctly classify all training instances.
        Statistics classifyStats = new Statistics();
        Instance testInstance = new SparseInstance(model.size());
        testInstance.setDataset(dataSet);
        for (String[] datum : rawRecords) {
            for (int a = 0; a < model.size(); a++) {
                testInstance.setValue(a, datum[a]);
            }
            Timing t = new Timing();
            double result = forest.classifyInstance(testInstance);
            t.finish();
            classifyStats.add(t.getDuration().as(TimeUnit.MILLISECONDS));
            String output = dataSet.classAttribute().value((int)result);
            assertEquals(datum[datum.length - 1], output);
        }
        getLogger().info("Timing stats for FOREST: {}", classifyStats);

        // Remove the actual training instances from the data set.
        dataSet.clear();

        // Create a serial copy (emulating saving to disk and reloading).
        classifyStats = new Statistics();
        byte[] serialized = Serialization.toBytes(dataSet);
        Instances copyDataSet = Serialization.fromBytes(Instances.class, serialized);
        serialized = Serialization.toBytes(forest);
        RandomForest copyForest = Serialization.fromBytes(RandomForest.class, serialized);

        // Check that the forest can correctly classify all training instances.
        testInstance = new SparseInstance(model.size());
        testInstance.setDataset(copyDataSet);
        for (String[] datum : rawRecords) {
            for (int a = 0; a < model.size(); a++) {
                testInstance.setValue(a, datum[a]);
            }
            Timing t = new Timing();
            double result = copyForest.classifyInstance(testInstance);
            t.finish();
            classifyStats.add(t.getDuration().as(TimeUnit.MILLISECONDS));
            String output = dataSet.classAttribute().value((int)result);
            assertEquals(datum[datum.length - 1], output);
        }
        getLogger().info("Timing stats for COPY: {}", classifyStats);
    }
}