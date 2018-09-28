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
public class BinaryBytes
        extends Scalar<BinaryBytes.Units> {
    /**
     * The binary units.
     */
    public enum Units
            implements ScalarUnit<Units> {

        BYTE("B", 1),
        KIBI("KiB", BYTE.magnitude * 1_024),
        MEBI("MiB", KIBI.magnitude * 1_024),
        GIBI("GiB", MEBI.magnitude * 1_024),
        TEBI("TiB", GIBI.magnitude * 1_024),
        PEBI("PiB", TEBI.magnitude * 1_024),
        EXBI("EiB", PEBI.magnitude * 1_024),
        ZEBI("ZiB", EXBI.magnitude * 1_024),
        YOBI("YiB", ZEBI.magnitude * 1_024);

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
    public BinaryBytes(long bytes) {
        super(Units.class, bytes);
    }

    /**
     * Constructor.
     *
     * @param ordinal The number of bytes as represented in the specified units.
     * @param units The units.
     */
    public BinaryBytes(double ordinal, Units units) {
        super(Units.class, ordinal, units);
    }
}