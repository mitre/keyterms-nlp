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

package keyterms.util.lang;

import org.junit.Test;

import keyterms.testing.Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class Scalar_UT {

    enum TestUnits
            implements ScalarUnit<TestUnits> {
        PUPS {
            @Override
            public double getMagnitude() {
                return 1;
            }

            @Override
            public String getUnitLabel() {
                return "pup";
            }

            @Override
            public String getPluralLabel() {
                return "pups";
            }
        },
        DOGS {
            @Override
            public double getMagnitude() {
                return 100;
            }

            @Override
            public String getUnitLabel() {
                return "dog";
            }

            @Override
            public String getPluralLabel() {
                return "dogs";
            }
        },
        CANINES {
            @Override
            public double getMagnitude() {
                return 1000;
            }

            @Override
            public String getUnitLabel() {
                return "canine";
            }

            @Override
            public String getPluralLabel() {
                return "canines";
            }
        }
    }

    class TestScalar
            extends Scalar<TestUnits> {
        /**
         * Constructor.
         *
         * @param value The number of pups.
         */
        TestScalar(long value) {
            super(TestUnits.class, value);
        }

        /**
         * Constructor.
         *
         * <p> If the specified unit is {@code null}, the base (smallest) unit is assumed. </p>
         *
         * @param value The value.
         * @param unit The unit of the specified value.
         */
        TestScalar(double value, TestUnits unit) {
            super(TestUnits.class, value, unit);
        }
    }

    @Test
    public void bad() {
        Tests.testError(IllegalArgumentException.class, () -> new TestScalar(-1));
    }

    @Test
    public void zero() {
        TestScalar scalar = new TestScalar(0);
        assertEquals(0, scalar.getBaseValue());
        assertSame(TestUnits.PUPS, scalar.getUnits());
        assertEquals("0 pups", scalar.summary(0));
        assertEquals("0 pups", scalar.summary(1));
        assertEquals("0 pups", scalar.formattedSummary(0));
        assertEquals("0.0 pups", scalar.formattedSummary(1));
        assertEquals("0 pups", scalar.summaryDetails());
        assertEquals("0 canines, 0 dogs, 0 pups", scalar.fullDetails());
    }

    @Test
    public void ones() {
        TestScalar scalar = new TestScalar(1101);
        assertEquals(1101, scalar.getBaseValue());
        assertSame(TestUnits.CANINES, scalar.getUnits());
        assertEquals("~1 canine", scalar.summary(0));
        assertEquals("~1.1 canines", scalar.summary(1));
        assertEquals("~1 canine", scalar.formattedSummary(0));
        assertEquals("~1.1 canines", scalar.formattedSummary(1));
        assertEquals("1 canine, 1 dog, 1 pup", scalar.summaryDetails());
        assertEquals("1 canine, 1 dog, 1 pup", scalar.fullDetails());
    }

    @Test
    public void scalarValue() {
        TestScalar scalar = new TestScalar(2_940);
        assertEquals(2_940, scalar.getBaseValue());
        assertSame(TestUnits.CANINES, scalar.getUnits());
        assertEquals(2.94, scalar.as(TestUnits.CANINES), 0);
        assertEquals(29.4, scalar.as(TestUnits.DOGS), 0);
        assertEquals(2_940, scalar.as(TestUnits.PUPS), 0);
        assertEquals("~3 canines", scalar.summary(0));
        assertEquals("~2.9 canines", scalar.summary(1));
        assertEquals("~3 canines", scalar.formattedSummary(0));
        assertEquals("~2.9 canines", scalar.formattedSummary(1));
        assertEquals("2 canines, 9 dogs, 40 pups", scalar.summaryDetails());
        assertEquals("2 canines, 9 dogs, 40 pups", scalar.fullDetails());
    }

    @Test
    public void specifiedWithUnits() {
        TestScalar scalar = new TestScalar(2.96, TestUnits.CANINES);
        assertEquals(2_960, scalar.getBaseValue());
        assertSame(TestUnits.CANINES, scalar.getUnits());
        assertEquals(2.96, scalar.as(TestUnits.CANINES), 0);
        assertEquals(29.6, scalar.as(TestUnits.DOGS), 0);
        assertEquals(2_960, scalar.as(TestUnits.PUPS), 0);
        assertEquals("~3 canines", scalar.summary(0));
        assertEquals("~3 canines", scalar.summary(1));
        assertEquals("~3 canines", scalar.formattedSummary(0));
        assertEquals("~3.0 canines", scalar.formattedSummary(1));
        assertEquals("2 canines, 9 dogs, 60 pups", scalar.summaryDetails());
        assertEquals("2 canines, 9 dogs, 60 pups", scalar.fullDetails());
    }

    @Test
    public void precise() {
        TestScalar scalar = new TestScalar(300);
        assertEquals(300, scalar.getBaseValue());
        assertSame(TestUnits.DOGS, scalar.getUnits());
        assertEquals(0.3, scalar.as(TestUnits.CANINES), 0);
        assertEquals(3, scalar.as(TestUnits.DOGS), 0);
        assertEquals(300, scalar.as(TestUnits.PUPS), 0);
        assertEquals("3 dogs", scalar.summary(0));
        assertEquals("3 dogs", scalar.summary(1));
        assertEquals("3 dogs", scalar.formattedSummary(0));
        assertEquals("3.0 dogs", scalar.formattedSummary(1));
        assertEquals("3 dogs", scalar.summaryDetails());
        assertEquals("0 canines, 3 dogs, 0 pups", scalar.fullDetails());
    }

    @Test
    public void preciseWithUnits() {
        TestScalar scalar = new TestScalar(3, TestUnits.DOGS);
        assertEquals(300, scalar.getBaseValue());
        assertSame(TestUnits.DOGS, scalar.getUnits());
        assertEquals(0.3, scalar.as(TestUnits.CANINES), 0);
        assertEquals(3, scalar.as(TestUnits.DOGS), 0);
        assertEquals(300, scalar.as(TestUnits.PUPS), 0);
        assertEquals("3 dogs", scalar.summary(0));
        assertEquals("3 dogs", scalar.summary(1));
        assertEquals("3 dogs", scalar.formattedSummary(0));
        assertEquals("3.0 dogs", scalar.formattedSummary(1));
        assertEquals("3 dogs", scalar.summaryDetails());
        assertEquals("0 canines, 3 dogs, 0 pups", scalar.fullDetails());
    }
}