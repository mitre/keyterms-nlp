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

package keyterms.util.config;

import keyterms.util.lang.Resolver;

/**
 * A numeric value resolver which imposes minimum and/or maximum values.
 *
 * <p> Note: {@code null} values will be resolved as {@code null} by this resolver. </p>
 */
public class RangeResolver<C extends Comparable<C>>
        implements Resolver<C> {
    /**
     * The minimum allowed value.
     */
    private final C minimum;

    /**
     * The maximum allowed value.
     */
    private final C maximum;

    /**
     * Constructor.
     *
     * @param minimum The minimum allowed value.
     * @param maximum The maximum allowed value.
     */
    public RangeResolver(C minimum, C maximum) {
        super();
        this.minimum = minimum;
        this.maximum = maximum;
        if ((minimum != null) && (maximum != null) && (minimum.compareTo(maximum) > 0)) {
            throw new IllegalArgumentException("Minimum greater than maximum [" + minimum + "," + maximum + "]");
        }
    }

    /**
     * Get the minimum allowed value.
     *
     * @return The minimum allowed value.
     */
    public C getMinimum() {
        return minimum;
    }

    /**
     * Get the maximum allowed value.
     *
     * @return The maximum allowed value.
     */
    public C getMaximum() {
        return maximum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C resolve(C value) {
        C resolved = value;
        if (value != null) {
            if ((minimum != null) && (minimum.compareTo(value) > 0)) {
                resolved = minimum;
            }
            if ((maximum != null) && (maximum.compareTo(value) < 0)) {
                resolved = maximum;
            }
        }
        return resolved;
    }
}