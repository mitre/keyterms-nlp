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

package keyterms.analyzer;

import java.io.Serializable;
import java.util.Objects;

/**
 * A generic marker object for a single output feature of an analysis.
 */
public class AnalysisFeature<V>
        implements Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 8664818036383583665L;

    /**
     * The feature name.
     */
    private final String name;

    /**
     * The value type associated with the feature.
     */
    private final Class<V> valueClass;

    /**
     * Constructor.
     *
     * @param name The feature name.
     * @param valueClass The value type associated with the feature.
     */
    public AnalysisFeature(String name, Class<V> valueClass) {
        super();
        if (name == null) {
            throw new NullPointerException("Feature name is required.");
        }
        if (valueClass == null) {
            throw new NullPointerException("Feature value class is required.");
        }
        this.name = name;
        this.valueClass = valueClass;
    }

    /**
     * Get the feature name.
     *
     * @return The feature name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value type associated with the feature.
     *
     * @return The value type associated with the feature.
     */
    public Class<V> getValueClass() {
        return valueClass;
    }

    /**
     * Cast the specified object as the value type associated with the feature.
     *
     * <p> Note: This has the potential to throw class cast exceptions. </p>
     *
     * @param object The object.
     *
     * @return The object cast as the value type of this feature.
     */
    public V cast(Object object) {
        return valueClass.cast(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (this == obj);
        if ((!equals) && (obj instanceof AnalysisFeature)) {
            AnalysisFeature<?> feature = (AnalysisFeature<?>)obj;
            equals = Objects.equals(name, feature.name);
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), name);
    }
}