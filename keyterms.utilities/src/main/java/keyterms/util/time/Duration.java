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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import keyterms.util.lang.Scalar;
import keyterms.util.lang.ScalarUnit;

/**
 * A duration is a measurement of time internally represented in nanoseconds.  The maximum duration representable is:
 *
 * <p> {@code 106751 days, 23 hours, 47 minutes, 16 seconds, 854 milliseconds, 775 microseconds, 807 nanoseconds} or
 * approximately {@code 292.28 years}. </p>
 *
 * <p> The duration class is not intended to be a general duration utility for use in calendaring implementations.  It
 * was designed primarily to handle the durations encountered in software metering frameworks. </p>
 *
 * <p> The duration class has a variety of methods useful in summarizing the represented time period.  Durations
 * encountered in metering frameworks often vary in scale and are hard to compare with each other or interpret in
 * meaningful ways. </p>
 *
 * <p> Negative durations are not allowed. </p>
 */
public class Duration
        extends Scalar<Duration.Units>
        implements Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 3337427229788117005L;

    /**
     * A duration representing no time.
     */
    public static final Duration ZERO = new Duration(0);

    /**
     * The temporal units associated with durations.
     */
    public enum Units
            implements ScalarUnit<Units> {
        /**
         * Time unit representing one thousandth of a microsecond
         */
        NANOSECONDS(1),
        /**
         * Time unit representing one thousandth of a millisecond
         */
        MICROSECONDS(NANOSECONDS.magnitude * 1_000),
        /**
         * Time unit representing one thousandth of a second
         */
        MILLISECONDS(MICROSECONDS.magnitude * 1_000),
        /**
         * Time unit representing one second
         */
        SECONDS(MILLISECONDS.magnitude * 1_000),
        /**
         * Time unit representing sixty seconds
         */
        MINUTES(SECONDS.magnitude * 60),
        /**
         * Time unit representing sixty minutes
         */
        HOURS(MINUTES.magnitude * 60),
        /**
         * Time unit representing twenty four hours
         */
        DAYS(HOURS.magnitude * 24);

        /**
         * Get duration units given the equivalent time units.
         *
         * @param timeUnit The time unit of interest.
         *
         * @return The duration unit equivalent to the specified time unit.
         */
        public static Units of(TimeUnit timeUnit) {
            return (timeUnit != null) ? valueOf(timeUnit.name()) : null;
        }

        /**
         * The number of nanoseconds in this time unit.
         */
        private final double magnitude;

        /**
         * The label for unitary (singular) values.
         */
        private final String unitLabel;

        /**
         * The unit label for non-unitary (plural) values.
         */
        private final String pluralLabel;

        /**
         * Constructor.
         *
         * @param magnitude The magnitude of the unit in nanoseconds.
         */
        Units(double magnitude) {
            this.magnitude = magnitude;
            pluralLabel = name().toLowerCase();
            unitLabel = pluralLabel.substring(0, pluralLabel.length() - 1);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getMagnitude() {
            return magnitude;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getUnitLabel() {
            return unitLabel;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPluralLabel() {
            return pluralLabel;
        }

        /**
         * Get this unit as the equivalent {@code TimeUnit} value.
         *
         * @return This unit as the equivalent {@code TimeUnit} value.
         */
        public TimeUnit asTimeUnit() {
            return TimeUnit.valueOf(name());
        }
    }

    /**
     * Constructor.
     *
     * <p> Negative durations are not allowed. </p>
     *
     * @param nanoseconds The duration's time span in nanoseconds.
     */
    public Duration(long nanoseconds) {
        super(Units.class, nanoseconds);
    }

    /**
     * Constructor.
     *
     * <p> Negative durations are not allowed. </p>
     *
     * <p> If the specified time unit is {@code null}, a value of {@code TimeUnit.NANOSECONDS} is presumed. </p>
     *
     * @param duration The duration's time span in the specified time units.
     * @param units The time units for the specified duration.
     */
    public Duration(double duration, Units units) {
        super(Units.class, duration, units);
    }

    /**
     * Constructor.
     *
     * <p> Negative durations are not allowed. </p>
     *
     * <p> If the specified time unit is {@code null}, a value of {@code TimeUnit.NANOSECONDS} is presumed. </p>
     *
     * @param duration The duration's time span in the specified time units.
     * @param timeUnit The time units for the specified duration.
     */
    public Duration(double duration, TimeUnit timeUnit) {
        super(Units.class, duration, Units.of(timeUnit));
    }

    /**
     * Get the current value in the specified units.
     *
     * <p> If the specified unit is {@code null}, the base (smallest) unit is assumed. </p>
     *
     * @param timeUnit The temporal unit of interest.
     *
     * @return The scalar represented as a value with the specified unit.
     */
    public double as(TimeUnit timeUnit) {
        return as(Units.of(timeUnit));
    }
}