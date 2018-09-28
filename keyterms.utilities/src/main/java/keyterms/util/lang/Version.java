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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import keyterms.util.text.Strings;

/**
 * A generic representation of a product version number.
 *
 * <p> In general product version numbers are represented by at least a major and minor number. </p>
 *
 * <p> Major numbers typically represent a major architectural change in the product.
 * Typically no backward compatibility is guaranteed on a major version number change. </p>
 *
 * <p> Minor numbers typically represent feature additions.
 * Backward compatibility within the same major release number is typically guaranteed. </p>
 *
 * <p> Patch numbers typically represent bug fix releases.
 * Backward compatibility within the same major release number is typically guaranteed. </p>
 */
public class Version
        implements Comparable<Version>, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = -4914070386714424239L;

    /**
     * The pattern describing version numbers written as text.
     */
    private static final Pattern VERSION = Pattern.compile("(\\d+)(([._])(\\d+))?(([._])(\\d+))?");

    /**
     * A placeholder for product information where version number is unknown or unimportant.
     */
    public static final Version NO_VERSION = new Version();

    /**
     * The major version number.
     */
    private final int majorNumber;

    /**
     * The minor version number.
     */
    private final int minorNumber;

    /**
     * The patch number.
     */
    private final int patchNumber;

    /**
     * Constructor.
     *
     * @param versionText The text form of the version number.
     */
    public Version(CharSequence versionText) {
        super();
        if (Strings.isBlank(versionText)) {
            majorNumber = 0;
            minorNumber = 0;
            patchNumber = 0;
        } else {
            String text = versionText.toString().trim();
            Matcher matcher = VERSION.matcher(text);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Invalid version number: " + text);
            }
            majorNumber = Integer.parseInt(matcher.group(1));
            minorNumber = (matcher.group(4) != null) ? Integer.parseInt(matcher.group(4)) : 0;
            patchNumber = (matcher.group(7) != null) ? Integer.parseInt(matcher.group(7)) : 0;
        }
    }

    /**
     * Constructor.
     */
    private Version() {
        this(0, 0, 0);
    }

    /**
     * Constructor.
     *
     * @param majorNumber The major version number.
     */
    public Version(int majorNumber) {
        this(majorNumber, 0, 0);
    }

    /**
     * Constructor.
     *
     * @param majorNumber The major version number.
     * @param minorNumber The minor version number.
     */
    public Version(int majorNumber, int minorNumber) {
        this(majorNumber, minorNumber, 0);
    }

    /**
     * Constructor.
     *
     * @param majorNumber The major version number.
     * @param minorNumber The minor version number.
     * @param patchNumber The patch number.
     */
    public Version(int majorNumber, int minorNumber, int patchNumber) {
        super();
        if (majorNumber < 0) {
            throw new IllegalArgumentException("Invalid major number: " + majorNumber);
        }
        if (minorNumber < 0) {
            throw new IllegalArgumentException("Invalid minor number: " + minorNumber);
        }
        if (patchNumber < 0) {
            throw new IllegalArgumentException("Invalid patch number: " + patchNumber);
        }
        this.majorNumber = majorNumber;
        this.minorNumber = minorNumber;
        this.patchNumber = patchNumber;
    }

    /**
     * Get the major version number.
     *
     * @return The major version number.
     */
    public int getMajorNumber() {
        return majorNumber;
    }

    /**
     * Get the minor version number.
     *
     * @return The minor version number.
     */
    public int getMinorNumber() {
        return minorNumber;
    }

    /**
     * Get the patch number.
     *
     * @return The patch number.
     */
    public int getPatchNumber() {
        return patchNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Version o) {
        return Comparator
                .comparing(Version::getMajorNumber)
                .thenComparing(Version::getMinorNumber)
                .thenComparing(Version::getPatchNumber)
                .compare(this, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(majorNumber, minorNumber, patchNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (this == obj);
        if ((!equals) && (obj instanceof Version)) {
            Version version = (Version)obj;
            equals = ((majorNumber == version.majorNumber) &&
                    (minorNumber == version.minorNumber) &&
                    (patchNumber == version.patchNumber));
        }
        return equals;
    }

    /**
     * Get a textual representation of the version number.
     *
     * @return A textual representation of the version number.
     */
    public String versionString() {
        return String.valueOf(majorNumber) + '.' + minorNumber +
                ((patchNumber > 0) ? "." + patchNumber : Strings.EMPTY_STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "v" + versionString();
    }
}