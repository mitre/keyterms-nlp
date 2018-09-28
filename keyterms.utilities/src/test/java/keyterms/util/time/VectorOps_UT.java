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

package keyterms.util.time;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class VectorOps_UT {

    private static final double THRESHOLD = 0.000001;

    private static List<Integer> integerList1 = Arrays.asList(2000, 6, 13);
    private static List<Integer> integerList2 = Arrays.asList(2000, 9, 29);
    private static List<Integer> integerTwos = Arrays.asList(2, 2, 2);
    private static List<Double> doubleList1 = Arrays.asList(2000.0, 6.0, 13.0);
    private static List<Double> doubleList2 = Arrays.asList(2000.0, 9.0, 29.0);
    private static List<Double> doubleTwos = Arrays.asList(2.0, 2.0, 2.0);
    private static List<Double> doubleListWithDifferentSize = Arrays.asList(2000.0, 2.0);
    private static List<Double> weights1 = Arrays.asList(2.0, 2.0, 2.0);
    private static List<Double> weights2 = Arrays.asList(0.5, 0.5, 0.5);
    private static List<Double> weights3 = Arrays.asList(1.0, 0.5, 0.25);

    @Test
    public void convertToDoublesTest() {
        assertEquals(VectorOps.convertToDoubles(integerList1), doubleList1);
        assertEquals(VectorOps.convertToDoubles(integerList2), doubleList2);
        assertEquals(VectorOps.convertToDoubles(integerTwos), doubleTwos);

        assertEquals(VectorOps.convertToDoubles(2000, 6, 13), doubleList1);
        assertEquals(VectorOps.convertToDoubles(2000, 9, 29), doubleList2);
        assertEquals(VectorOps.convertToDoubles(2, 2, 2), doubleTwos);
    }

    @Test
    public void magnitudeTest() {
        assertEquals(VectorOps.magnitude(doubleList1), 2000.051249, THRESHOLD);
        assertEquals(VectorOps.magnitude(doubleList2), 2000.230487, THRESHOLD);
        assertEquals(VectorOps.magnitude(doubleTwos), 3.464102, THRESHOLD);
    }

    @Test
    public void elementwiseMultiplyTest() {
        List<Double> doubleList1_doubled = Arrays.asList(4000.0, 12.0, 26.0);
        List<Double> doubleList2_doubled = Arrays.asList(4000.0, 18.0, 58.0);
        List<Double> doubleTwos_doubled = Arrays.asList(4.0, 4.0, 4.0);

        assertEquals(doubleList1_doubled, VectorOps.elementwiseMultiply(doubleList1, doubleTwos));
        assertEquals(doubleList2_doubled, VectorOps.elementwiseMultiply(doubleList2, doubleTwos));
        assertEquals(doubleTwos_doubled, VectorOps.elementwiseMultiply(doubleTwos, doubleTwos));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidElementwiseMultiplyTest() {
        VectorOps.elementwiseMultiply(doubleList1, doubleListWithDifferentSize);
        fail();
    }

    @Test
    public void dotTest() {
        assertEquals(4038.0, VectorOps.dot(doubleList1, doubleTwos), THRESHOLD);
        assertEquals(4038.0, VectorOps.dot(doubleTwos, doubleList1), THRESHOLD);
        assertEquals(4076.0, VectorOps.dot(doubleList2, doubleTwos), THRESHOLD);
        assertEquals(4076.0, VectorOps.dot(doubleTwos, doubleList2), THRESHOLD);
        assertEquals(12.0, VectorOps.dot(doubleTwos, doubleTwos), THRESHOLD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDotTest() {
        VectorOps.dot(doubleList1, doubleListWithDifferentSize);
        fail();
    }

    @Test
    public void weightedDotTest() {
        assertEquals(8076.0, VectorOps.weightedDot(doubleList1, doubleTwos, weights1), THRESHOLD);
        assertEquals(8076.0, VectorOps.weightedDot(doubleTwos, doubleList1, weights1), THRESHOLD);
        assertEquals(8152.0, VectorOps.weightedDot(doubleList2, doubleTwos, weights1), THRESHOLD);
        assertEquals(8152.0, VectorOps.weightedDot(doubleTwos, doubleList2, weights1), THRESHOLD);
        assertEquals(24.0, VectorOps.weightedDot(doubleTwos, doubleTwos, weights1), THRESHOLD);

        assertEquals(2019.0, VectorOps.weightedDot(doubleList1, doubleTwos, weights2), THRESHOLD);
        assertEquals(2019.0, VectorOps.weightedDot(doubleTwos, doubleList1, weights2), THRESHOLD);
        assertEquals(2038.0, VectorOps.weightedDot(doubleList2, doubleTwos, weights2), THRESHOLD);
        assertEquals(2038.0, VectorOps.weightedDot(doubleTwos, doubleList2, weights2), THRESHOLD);
        assertEquals(6.0, VectorOps.weightedDot(doubleTwos, doubleTwos, weights2), THRESHOLD);

        assertEquals(4012.5, VectorOps.weightedDot(doubleList1, doubleTwos, weights3), THRESHOLD);
        assertEquals(4012.5, VectorOps.weightedDot(doubleTwos, doubleList1, weights3), THRESHOLD);
        assertEquals(4023.5, VectorOps.weightedDot(doubleList2, doubleTwos, weights3), THRESHOLD);
        assertEquals(4023.5, VectorOps.weightedDot(doubleTwos, doubleList2, weights3), THRESHOLD);
        assertEquals(7.0, VectorOps.weightedDot(doubleTwos, doubleTwos, weights3), THRESHOLD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidWeightedDotTest() {
        VectorOps.weightedDot(doubleList1, doubleListWithDifferentSize, weights1);
        fail();
    }

    @Test
    public void cosSimTest() {
        assertEquals(VectorOps.cosSim(doubleList1, doubleTwos), 0.582820, THRESHOLD);
        assertEquals(VectorOps.cosSim(doubleTwos, doubleList1), 0.582820, THRESHOLD);
        assertEquals(VectorOps.cosSim(doubleList2, doubleTwos), 0.588252, THRESHOLD);
        assertEquals(VectorOps.cosSim(doubleTwos, doubleList2), 0.588252, THRESHOLD);
        assertEquals(VectorOps.cosSim(doubleTwos, doubleTwos), 1.0, THRESHOLD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCosSimTest() {
        VectorOps.cosSim(doubleList1, doubleListWithDifferentSize);
        fail();
    }

    @Test
    public void weightedCosSimTest() {
        assertEquals(VectorOps.weightedCosSim(doubleList1, doubleTwos, weights1), 0.582820, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleList1, weights1), 0.582820, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleList1, doubleTwos, weights2), 0.582820, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleList1, weights2), 0.582820, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleList1, doubleTwos, weights3), 0.758286, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleList1, weights3), 0.758286, THRESHOLD);

        assertEquals(VectorOps.weightedCosSim(doubleList2, doubleTwos, weights1), 0.588252, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleList2, weights1), 0.588252, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleList2, doubleTwos, weights2), 0.588252, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleList2, weights2), 0.588252, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleList2, doubleTwos, weights3), 0.760346, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleList2, weights3), 0.760346, THRESHOLD);

        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleTwos, weights1), 1.0, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleTwos, weights2), 1.0, THRESHOLD);
        assertEquals(VectorOps.weightedCosSim(doubleTwos, doubleTwos, weights3), 1.0, THRESHOLD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidWeightedCosSimTest() {
        VectorOps.weightedCosSim(doubleList1, doubleListWithDifferentSize, weights1);
        fail();
    }
}