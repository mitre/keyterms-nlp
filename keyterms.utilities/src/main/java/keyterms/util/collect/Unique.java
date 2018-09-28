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

package keyterms.util.collect;

import java.io.Serializable;
import java.util.Objects;

/**
 * An object that contains an identifier which is presumed to be unique within its class.
 */
public class Unique<K>
        implements Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -3478597913714569886L;

    /**
     * The object identifier.
     */
    protected final K id;

    /**
     * Constructor.
     *
     * @param id The Object identifier.
     */
    public Unique(K id) {
        super();
        this.id = id;
    }

    /**
     * Get the object identifier.
     *
     * @return The object identifier.
     */
    public K getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (this == obj);
        if ((!equals) && (getClass().isInstance(obj))) {
            Unique<?> unique = (Unique<?>)obj;
            equals = Objects.equals(id, unique.id);
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), id);
    }
}