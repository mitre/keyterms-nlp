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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Timing_UT {

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    @Test
    public void timing()
            throws Exception {
        int testMilliseconds = 100;
        int testNanoseconds = testMilliseconds * 1_000_000;
        Timing timing = new Timing();
        assertEquals("unfinished timing summary", "incomplete", timing.summary(0));
        long millisecondStart = System.currentTimeMillis();
        long nanosecondStart = System.nanoTime();
        long nanosecondsSlept = (System.nanoTime() - nanosecondStart);
        Duration lastDuration = timing.getDuration();
        while (nanosecondsSlept <= testNanoseconds) {
            Thread.sleep(10);
            nanosecondsSlept = (System.nanoTime() - nanosecondStart);
            Duration duration = timing.getDuration();
            assertTrue(duration.compareTo(lastDuration) > 0);
            lastDuration = duration;
        }
        long millisecondsSlept = System.currentTimeMillis() - millisecondStart;
        Duration duration = timing.finish().getDuration();
        assertTrue(duration.compareTo(lastDuration) >= 0);
        Duration expected = new Duration(testMilliseconds, TimeUnit.MILLISECONDS);
        Duration milliseconds = new Duration(millisecondsSlept, TimeUnit.MILLISECONDS);
        Duration nanoseconds = new Duration(nanosecondsSlept);
        assertTrue(duration + " less than " + expected +
                        "\n    milliseconds slept: " + milliseconds.summary(2) +
                        "\n    nanoseconds slept: " + nanoseconds.summary(2),
                expected.compareTo(duration) <= 0);
        if (!milliseconds.summary(0).equals(nanoseconds.summary(0))) {
            getLogger().warn("Apparent mismatch in millisecond and nanosecond durations: {} vs. {}.",
                    milliseconds, nanoseconds);
        }
        timing.finish();
        assertEquals("multiple finish calls is no-op", duration, timing.getDuration());
    }

    @Test
    public void durationInRollover() {
        // This test may never see an actual roll-over as it is rare and dependent on cpu loading?
        for (int t = 0; t < 1000; t++) {
            Timing timing = new Timing();
            timing.finish();
            assertTrue(timing.getDuration().as(Duration.Units.NANOSECONDS) >= 0);
        }
    }
}