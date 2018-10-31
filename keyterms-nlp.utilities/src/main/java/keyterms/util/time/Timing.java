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

import keyterms.util.Errors;

/**
 * A single use timing measurement.
 */
public class Timing {
    /**
     * The start time of the timing in nanoseconds.
     */
    private final long start;

    /**
     * The completed duration of the timing.
     */
    private Duration duration;

    /**
     * Constructor.
     */
    public Timing() {
        super();
        this.start = System.nanoTime();
    }

    /**
     * Get the duration of the timing.
     *
     * <p> This value will not change after the timing is finished. </p>
     *
     * @return The duration of the timing.
     */
    public Duration getDuration() {
        Duration currentDuration = duration;
        if (currentDuration == null) {
            long stop = System.nanoTime();
            long time = Math.abs(stop - start);
            currentDuration = new Duration(time);
        }
        return currentDuration;
    }

    /**
     * Determine if the timing is finished.
     *
     * @return A flag indicating whether the timing is still being captured.
     */
    public boolean isFinished() {
        return (duration != null);
    }

    /**
     * Complete the timing.
     *
     * <p> Subsequent calls to this method will have no effects. </p>
     *
     * @return A reference to this timing useful in method chaining.
     */
    public Timing finish() {
        if (duration == null) {
            long stop = System.nanoTime();
            long time = Math.abs(stop - start);
            duration = new Duration(time);
        } else {
            Errors.ignore(new IllegalStateException("Timing already finished."));
        }
        return this;
    }

    /**
     * Create a textual summary of the duration expressed in its largest non-zero time unit rounded to the specified
     * precision.  A {@code ~} character is prefixed to non-precise summaries.
     *
     * <p> E.g. 118 minutes = ~1.9 minutes with a precision of {@code 1}. </p>
     *
     * @param precision The desired decimal precision of the summary.  If the specified precision is less than zero,
     * the
     * output precision will be {@code 0}.
     *
     * @return A textual summary of the duration expressed in its largest non-zero time unit rounded to the specified
     * precision.
     */
    public String summary(int precision) {
        return (duration != null) ? duration.summary(precision) : "incomplete";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), summary(0));
    }
}