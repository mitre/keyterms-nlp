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

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import keyterms.testing.Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Duration_UT {

    private static long nanoseconds(int days, int hours, int minutes,
            int seconds, int milliseconds, int microseconds, int nanoseconds) {
        long n = nanoseconds;
        n += (microseconds * TimeUnit.MICROSECONDS.toNanos(1));
        n += (milliseconds * TimeUnit.MILLISECONDS.toNanos(1));
        n += (seconds * TimeUnit.SECONDS.toNanos(1));
        n += (minutes * TimeUnit.MINUTES.toNanos(1));
        n += (hours * TimeUnit.HOURS.toNanos(1));
        n += (days * TimeUnit.DAYS.toNanos(1));
        return n;
    }

    @Test
    public void unitTimes() {
        for (TimeUnit timeUnit : TimeUnit.values()) {
            assertEquals(timeUnit.toNanos(1), new Duration(1, timeUnit).as(Duration.Units.NANOSECONDS), 0.0);
            Duration.Units unit = Duration.Units.of(timeUnit);
            assertNotNull(unit);
            assertEquals(timeUnit.name(), unit.name());
        }
    }

    @Test
    public void rationalUnits() {
        Duration duration;
        duration = new Duration(nanoseconds(0, 0, 0, 0, 0, 0, 0));
        assertEquals(0, duration.getValue(), Double.MIN_VALUE);
        assertEquals(Duration.Units.NANOSECONDS, duration.getUnits());
        // 1 hour
        duration = new Duration(nanoseconds(0, 1, 0, 0, 0, 0, 0));
        assertEquals(1, duration.getValue(), Double.MIN_VALUE);
        assertEquals(Duration.Units.HOURS, duration.getUnits());
        // 1 hour 2 minutes
        duration = new Duration(nanoseconds(0, 1, 2, 0, 0, 0, 0));
        assertEquals(1.033, duration.getValue(), 0.001);
        assertEquals(Duration.Units.HOURS, duration.getUnits());
        // 1 hour 21 minutes
        duration = new Duration(nanoseconds(0, 1, 21, 0, 0, 0, 0));
        assertEquals(1.35, duration.getValue(), Double.MIN_VALUE);
        assertEquals(Duration.Units.HOURS, duration.getUnits());
        // 1 hour 59 minutes
        duration = new Duration(nanoseconds(0, 1, 59, 0, 0, 0, 0));
        assertEquals(1.983, duration.getValue(), 0.001);
        assertEquals(Duration.Units.HOURS, duration.getUnits());
        // 12 hour 30 minutes
        duration = new Duration(nanoseconds(0, 12, 30, 0, 0, 0, 0));
        assertEquals(12.5, duration.getValue(), Double.MIN_VALUE);
        assertEquals(Duration.Units.HOURS, duration.getUnits());
        // 1 Day and some change
        duration = new Duration(nanoseconds(1, 11, 1, 1, 1, 1, 1));
        assertEquals(1.459, duration.getValue(), 0.001);
        assertEquals(Duration.Units.DAYS, duration.getUnits());
    }

    @Test
    public void as() {
        Duration duration = new Duration(1, TimeUnit.DAYS);
        assertEquals(TimeUnit.DAYS.toNanos(1), duration.as(TimeUnit.NANOSECONDS), 0.0);
        assertEquals(TimeUnit.DAYS.toMicros(1), duration.as(TimeUnit.MICROSECONDS), 0.0);
        assertEquals(TimeUnit.DAYS.toMillis(1), duration.as(TimeUnit.MILLISECONDS), 0.0);
        assertEquals(TimeUnit.DAYS.toMillis(1), duration.as(TimeUnit.MILLISECONDS), 0.0);
        assertEquals(TimeUnit.DAYS.toSeconds(1), duration.as(TimeUnit.SECONDS), 0.0);
        assertEquals(TimeUnit.DAYS.toMinutes(1), duration.as(TimeUnit.MINUTES), 0.0);
        assertEquals(TimeUnit.DAYS.toHours(1), duration.as(TimeUnit.HOURS), 0.0);
        assertEquals(TimeUnit.DAYS.toDays(1), duration.as(TimeUnit.DAYS), 0.0);
    }

    @Test
    public void summary() {
        Duration duration;
        duration = new Duration(nanoseconds(0, 0, 0, 0, 0, 0, 0));
        assertEquals("0 nanoseconds", duration.summary(-1));
        assertEquals("0 nanoseconds", duration.summary(0));
        assertEquals("0 nanoseconds", duration.summary(1));
        assertEquals("0 nanoseconds", duration.summary(2));
        // 1 hour
        duration = new Duration(nanoseconds(0, 1, 0, 0, 0, 0, 0));
        assertEquals("1 hour", duration.summary(-1));
        assertEquals("1 hour", duration.summary(0));
        assertEquals("1 hour", duration.summary(1));
        assertEquals("1.00 hours", duration.formattedSummary(2));
        // 1 hour 2 minutes
        duration = new Duration(nanoseconds(0, 1, 2, 0, 0, 0, 0));
        assertEquals("~1 hour", duration.summary(-1));
        assertEquals("~1 hour", duration.summary(0));
        assertEquals("~1 hour", duration.summary(1));
        assertEquals("~1.03 hours", duration.summary(2));
        // 1 hour 21 minutes
        duration = new Duration(nanoseconds(0, 1, 21, 0, 0, 0, 0));
        assertEquals("~1 hour", duration.summary(-1));
        assertEquals("~1 hour", duration.summary(0));
        assertEquals("~1.4 hours", duration.summary(1));
        assertEquals("1.35 hours", duration.summary(2));
        // 1 hour 59 minutes
        duration = new Duration(nanoseconds(0, 1, 59, 0, 0, 0, 0));
        assertEquals("~2 hours", duration.summary(-1));
        assertEquals("~2 hours", duration.summary(0));
        assertEquals("~2 hours", duration.summary(1));
        assertEquals("~1.98 hours", duration.summary(2));
        // 12 hour 30 minutes
        duration = new Duration(nanoseconds(0, 12, 30, 0, 0, 0, 0));
        assertEquals("~13 hours", duration.summary(-1));
        assertEquals("~13 hours", duration.summary(0));
        assertEquals("12.5 hours", duration.summary(1));
        assertEquals("12.50 hours", duration.summary(2));
        // 1 Day and some change
        duration = new Duration(nanoseconds(1, 11, 1, 1, 1, 1, 1));
        assertEquals("~1 day", duration.summary(-1));
        assertEquals("~1 day", duration.summary(0));
        assertEquals("~1.5 days", duration.summary(1));
        assertEquals("~1.46 days", duration.summary(2));
    }

    @Test
    public void terseDetails() {
        Duration duration = Duration.ZERO;
        assertEquals("0 nanoseconds", duration.summaryDetails());
        duration = new Duration(nanoseconds(0, 11, 0, 29, 0, 1, 0));
        assertEquals("11 hours, 29 seconds, 1 microsecond", duration.summaryDetails());
    }

    @Test
    public void fullDetails() {
        Duration duration = Duration.ZERO;
        assertEquals("0 days, 0 hours, 0 minutes, 0 seconds, 0 milliseconds, 0 microseconds, 0 nanoseconds",
                duration.fullDetails());
        duration = new Duration(nanoseconds(0, 11, 0, 29, 0, 1, 0));
        assertEquals("0 days, 11 hours, 0 minutes, 29 seconds, 0 milliseconds, 1 microsecond, 0 nanoseconds",
                duration.fullDetails());
    }

    @Test
    public void testToString() {
        Duration duration = Duration.ZERO;
        assertEquals("Duration[0 nanoseconds]", duration.toString());
    }

    @Test
    public void serialization()
            throws Exception {
        Duration original = new Duration(42);
        Duration copy = Tests.testSerialize(original);
        // no error getting the field based texts
        copy.summary(2);
        copy.summaryDetails();
    }
}