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

/**
 * Scalar values for the measurement of bytes.
 */
public class DecimalBytes
        extends Scalar<DecimalBytes.Units> {
    /**
     * The binary units.
     */
    public enum Units
            implements ScalarUnit<Units> {

        BYTE("b", 1),
        KILO("kb", BYTE.magnitude * 1_000),
        MEGA("Mb", KILO.magnitude * 1_000),
        GIGA("Gb", MEGA.magnitude * 1_000),
        TERA("Tb", GIGA.magnitude * 1_000),
        PETA("Pb", TERA.magnitude * 1_000),
        EXA("Eb", PETA.magnitude * 1_000),
        ZETTA("Zb", EXA.magnitude * 1_000),
        YOTTA("Yb", ZETTA.magnitude * 1_000);

        /**
         * The unit order of magnitude.
         */
        private final double magnitude;

        /**
         * The unit label.
         */
        private final String label;

        /**
         * Constructor.
         *
         * @param label The unit label.
         * @param magnitude The unit order of magnitude.
         */
        Units(String label, double magnitude) {
            this.label = label;
            this.magnitude = magnitude;
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
            return label;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPluralLabel() {
            return label;
        }
    }

    /**
     * Constructor.
     *
     * @param bytes The number of bytes.
     */
    public DecimalBytes(long bytes) {
        super(Units.class, bytes);
    }

    /**
     * Constructor.
     *
     * @param ordinal The number of bytes as represented in the specified units.
     * @param units The units.
     */
    public DecimalBytes(double ordinal, Units units) {
        super(Units.class, ordinal, units);
    }
}