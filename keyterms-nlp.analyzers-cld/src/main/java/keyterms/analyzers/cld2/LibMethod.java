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

package keyterms.analyzers.cld2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import keyterms.util.text.Strings;

/**
 * A description of a library method used to locate mangled names within the native library.
 */
class LibMethod
        implements Predicate<String> {
    /**
     * The name of the method to find (case sensitive).
     */
    private final String name;

    /**
     * The texts which, in addition to the method name, must be present for a method signature to match this method
     * (case sensitive).
     */
    private final List<String> discriminators = new ArrayList<>();

    /**
     * The texts which, if present, prevent a method signature from matching this method (case sensitive).
     */
    private final List<String> exclusions = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param name The name of the method to find (case sensitive).
     */
    LibMethod(String name) {
        super();
        this.name = name;
    }

    /**
     * Get the method name.
     *
     * @return The method name.
     */
    public String getName() {
        return name;
    }

    /**
     * Add text which excludes a method signature from matching this method.
     *
     * <p> The collection of exclusions is used to filter out methods that contain the method name of interest but
     * should not match. </p>
     *
     * @param exclusion The exclusion.
     *
     * @return A reference to this object to facilitate method chaining.
     */
    LibMethod exclude(String exclusion) {
        if (Strings.isEmpty(exclusion)) {
            throw new IllegalArgumentException("Invalid exclusion: " + exclusion);
        }
        exclusions.add(exclusion);
        return this;
    }

    /**
     * Add text which must be present in addition to the method name for a method signature to match this method.
     *
     * <p> These discriminators need to be in the mangled signature along with the main method name.  This helps find
     * the exact method of interest in the case that the method of interest is overloaded.  In general these should
     * contain parameter type names that distinguish the method of interest from its overloaded counterparts. </p>
     *
     * @param discriminator The discriminator.
     *
     * @return A reference to this object to facilitate method chaining.
     */
    LibMethod require(String discriminator) {
        if (Strings.isEmpty(discriminator)) {
            throw new IllegalArgumentException("Invalid discriminator: " + discriminator);
        }
        discriminators.add(discriminator);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(String text) {
        boolean accept = false;
        if (!Strings.isEmpty(text)) {
            accept = text.contains(name);
            if ((accept) && (!exclusions.isEmpty())) {
                for (String exclusion : exclusions) {
                    accept = !text.contains(exclusion);
                    if (!accept) {
                        break;
                    }
                }
            }
            if ((accept) && (!discriminators.isEmpty())) {
                for (String discriminator : discriminators) {
                    accept = text.contains(discriminator);
                    if (!accept) {
                        break;
                    }
                }
            }
        }
        return accept;
    }
}