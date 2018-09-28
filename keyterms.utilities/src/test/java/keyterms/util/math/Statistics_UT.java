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

package keyterms.util.math;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Statistics_UT {

    @Test
    public void empty() {
        Statistics stats = new Statistics();
        assertTrue("isEmpty", stats.isEmpty());
        assertEquals("size", 0, stats.getSize());
        assertNull("minimum", stats.getMinimum());
        assertNull("midpoint", stats.getMidPoint());
        assertNull("mean", stats.getMean());
        assertNull("max", stats.getMaximum());
        assertNull("sum", stats.getSum());
        assertNull("variance", stats.getVariance());
        assertNull("std deviation", stats.getStandardDeviation());
        assertNull("skewness", stats.getSkewness());
        assertNull("kurtosis", stats.getKurtosis());
    }

    @Test
    public void singleValue() {
        Statistics stats = new Statistics();
        stats.add(42);
        assertFalse("isEmpty", stats.isEmpty());
        assertEquals("size", 1, stats.getSize());
        assertEquals("minimum", 42, stats.getMinimum(), Double.MIN_VALUE);
        assertEquals("midpoint", 42, stats.getMidPoint(), Double.MIN_VALUE);
        assertEquals("mean", 42, stats.getMean(), Double.MIN_VALUE);
        assertEquals("max", 42, stats.getMaximum(), Double.MIN_VALUE);
        assertEquals("sum", 42, stats.getSum(), Double.MIN_VALUE);
        assertEquals("variance", 0, stats.getVariance(), Double.MIN_VALUE);
        assertEquals("std deviation", 0, stats.getStandardDeviation(), Double.MIN_VALUE);
        assertEquals("skewness", 0, stats.getSkewness(), Double.MIN_VALUE);
        assertEquals("kurtosis", 0, stats.getKurtosis(), Double.MIN_VALUE);
    }

    @Test
    public void multipleValues() {
        Statistics stats = new Statistics();
        for (int t = 0; t < 11; t++) {
            stats.add(t * 10.0);
        }
        assertFalse("isEmpty", stats.isEmpty());
        assertEquals("size", 11, stats.getSize());
        assertEquals("minimum", 0.0, stats.getMinimum(), Double.MIN_VALUE);
        assertEquals("midpoint", 50.0, stats.getMidPoint(), Double.MIN_VALUE);
        assertEquals("mean", 50.0, stats.getMean(), Double.MIN_VALUE);
        assertEquals("max", 100.0, stats.getMaximum(), Double.MIN_VALUE);
        assertEquals("sum", 550.0, stats.getSum(), Double.MIN_VALUE);
        assertEquals("variance", 1100.0, stats.getVariance(), Double.MIN_VALUE);
        assertEquals("std deviation", Math.sqrt(1100), stats.getStandardDeviation(), Double.MIN_VALUE);
        assertEquals("skewness", 0, stats.getSkewness(), Double.MIN_VALUE);
        assertEquals("kurtosis", 1.78, stats.getKurtosis(), Double.MIN_VALUE);
    }

    @Test
    public void sample1() {
        // From https://en.wikipedia.org/wiki/Kurtosis
        Statistics stats = new Statistics();
        Arrays.asList(0, 3, 4, 1, 2, 3, 0, 2, 1, 3, 2, 0, 2, 2, 3, 2, 5, 2, 3, 999).forEach(stats::add);
        assertFalse("isEmpty", stats.isEmpty());
        assertEquals("size", 20, stats.getSize());
        assertEquals("minimum", 0.0, stats.getMinimum(), Double.MIN_VALUE);
        assertEquals("midpoint", 499.5, stats.getMidPoint(), Double.MIN_VALUE);
        assertEquals("mean", 51.95, stats.getMean(), Double.MIN_VALUE);
        assertEquals("max", 999.0, stats.getMaximum(), Double.MIN_VALUE);
        assertEquals("sum", 1039.0, stats.getSum(), Double.MIN_VALUE);
        assertEquals("variance", 49691.62894736842, stats.getVariance(), Double.MIN_VALUE);
        assertEquals("std deviation", 222.91619265402957, stats.getStandardDeviation(), Double.MIN_VALUE);
        assertEquals("skewness", 4.129251496378207, stats.getSkewness(), Double.MIN_VALUE);
        assertEquals("kurtosis", 18.051426543784185, stats.getKurtosis(), Double.MIN_VALUE);
    }

    @Test
    public void sample2() {
        // From https://brownmath.com/stat/shape.htm
        Statistics stats = new Statistics();
        Map<Double, Integer> scores = new HashMap<>();
        scores.put(61.0, 5);
        scores.put(64.0, 18);
        scores.put(67.0, 42);
        scores.put(70.0, 27);
        scores.put(73.0, 8);
        scores.forEach((k, v) -> {
            for (int s = 0; s < v; s++) {
                stats.add(k);
            }
        });
        assertFalse("isEmpty", stats.isEmpty());
        assertEquals("size", 100, stats.getSize());
        assertEquals("minimum", 61, stats.getMinimum(), Double.MIN_VALUE);
        assertEquals("midpoint", 67.0, stats.getMidPoint(), Double.MIN_VALUE);
        assertEquals("mean", 67.45, stats.getMean(), 0.001);
        assertEquals("max", 73.0, stats.getMaximum(), Double.MIN_VALUE);
        assertEquals("sum", 6745.0, stats.getSum(), Double.MIN_VALUE);
        assertEquals("variance", 8.613636363636385, stats.getVariance(), Double.MIN_VALUE);
        assertEquals("std deviation", 2.9348997195196267, stats.getStandardDeviation(), Double.MIN_VALUE);
        assertEquals("skewness", -0.1082, stats.getSkewness(), 0.0001);
        assertEquals("kurtosis", 2.7417589685396173, stats.getKurtosis(), Double.MIN_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void copy() {
        Statistics stats = new Statistics();
        for (int t = 0; t < 11; t++) {
            stats.add(t * 10.0);
        }
        Statistics copy = stats.createCopy();
        assertEquals(stats.isEmpty(), copy.isEmpty());
        assertEquals(stats.getSize(), copy.getSize());
        assertEquals(stats.getMinimum(), copy.getMinimum(), Double.MIN_VALUE);
        assertEquals(stats.getMean(), copy.getMean(), Double.MIN_VALUE);
        assertEquals(stats.getMaximum(), copy.getMaximum(), Double.MIN_VALUE);
        assertEquals(stats.getSum(), copy.getSum(), Double.MIN_VALUE);
        assertEquals(stats.getVariance(), copy.getVariance(), Double.MIN_VALUE);
        assertEquals(stats.getStandardDeviation(), copy.getStandardDeviation(), Double.MIN_VALUE);
        assertEquals(stats.getSkewness(), copy.getSkewness(), Double.MIN_VALUE);
        assertEquals(stats.getKurtosis(), copy.getKurtosis(), Double.MIN_VALUE);
    }
}