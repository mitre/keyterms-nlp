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

package keyterms.util.math;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Online computed statistics for a distribution which is evaluated one sample at a time.
 */
public class Statistics
        implements Cloneable, Serializable {
    /**
     * Get the smallest non-{@code null} value of two numbers.
     *
     * <p> If both values are {@code null}, a value of {@code null} is returned. </p>
     *
     * @param d1 The first number.
     * @param d2 The second number.
     *
     * @return The smallest non-{@code null} value of the two specified numbers.
     */
    private static Double minimum(Double d1, Double d2) {
        Double minimum = null;
        if ((d1 != null) || (d2 != null)) {
            if ((d1 != null) && (d2 != null)) {
                minimum = (d1 < d2) ? d1 : d2;
            } else {
                minimum = (d1 != null) ? d1 : d2;
            }
        }
        return minimum;
    }

    /**
     * Get the largest non-{@code null} value of two numbers.
     *
     * <p> If both values are {@code null}, a value of {@code null} is returned. </p>
     *
     * @param d1 The first number.
     * @param d2 The second number.
     *
     * @return The largest non-{@code null} value of the two specified numbers.
     */
    private static Double maximum(Double d1, Double d2) {
        Double maximum = null;
        if ((d1 != null) || (d2 != null)) {
            if ((d1 != null) && (d2 != null)) {
                maximum = (d1 > d2) ? d1 : d2;
            } else {
                maximum = (d1 != null) ? d1 : d2;
            }
        }
        return maximum;
    }

    /**
     * Compute the sum of two values where they are not {@code null}.
     *
     * <p> If both values are {@code null}, a value of {@code null} is returned. </p>
     *
     * <p> If only one of the numbers is {@code null}, the non-{@code null} number is returned. </p>
     *
     * @param d1 The first number.
     * @param d2 The second number.
     *
     * @return The sum of two values where they are not {@code null}.
     */
    private static Double sum(Double d1, Double d2) {
        Double sum = null;
        if ((d1 != null) || (d2 != null)) {
            if ((d1 != null) && (d2 != null)) {
                sum = d1 + d2;
            } else {
                sum = (d1 != null) ? d1 : d2;
            }
        }
        return sum;
    }

    /**
     * A synchronization lock for the statistics.
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * The number of values that have been collected.
     */
    private long size;

    /**
     * The smallest value collected.
     */
    private Double minimum;

    /**
     * The mean of the collected values.
     * <p> This is also the first central moment of the distribution (m1). </p>
     */
    private Double mean;

    /**
     * The largest value collected.
     */
    private Double maximum;

    /**
     * The sum of the collected values.
     */
    private Double sum;

    /**
     * The second central moment of the distribution.
     */
    private double m2;

    /**
     * The third central moment of the distribution.
     */
    private double m3;

    /**
     * The fourth central moment of the distribution.
     */
    private double m4;

    /**
     * Constructor.
     */
    public Statistics() {
        super();
    }

    /**
     * Determine if the number of values collected is <em>zero</em>.
     *
     * @return A flag indicating whether the number of values collected is <em>zero</em>.
     */
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return (size == 0);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the number of values that have been collected.
     *
     * @return The number of values that have been collected.
     */
    public long getSize() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the smallest value collected.
     *
     * @return The smallest value collected.
     */
    public Double getMinimum() {
        lock.readLock().lock();
        try {
            return minimum;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the midpoint value of the sample.
     *
     * <p> This value is half of the minimum plus the maximum. </p>
     *
     * @return The midpoint value of the sample.
     */
    public Double getMidPoint() {
        lock.readLock().lock();
        try {
            return (size > 0) ? ((minimum + maximum) / 2) : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the population mean of the collected values.
     *
     * @return The population mean of the collected values.
     */
    public Double getMean() {
        lock.readLock().lock();
        try {
            return mean;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the largest value collected.
     *
     * @return The largest value collected.
     */
    public Double getMaximum() {
        lock.readLock().lock();
        try {
            return maximum;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the sum of the collected values.
     *
     * @return The sum of the collected values.
     */
    public Double getSum() {
        lock.readLock().lock();
        try {
            return sum;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the <em>variance</em> of the distribution.
     *
     * <p> Definition from <a href="http://en.wikipedia.org/wiki/Variance">wikipedia.org</a>:
     *
     * <br>
     * In probability theory and statistics, variance measures how far a set of numbers is spread out. </p>
     *
     * @return The variance of the distribution.
     */
    public Double getVariance() {
        lock.readLock().lock();
        try {
            Double variance = null;
            if (size > 0) {
                variance = (size > 1) ? (m2 / (size - 1)) : 0;
            }
            return variance;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the <em>standard deviation</em> of the distribution.
     *
     * <p> Definition from <a href="http://en.wikipedia.org/wiki/Standard_deviation">wikipedia.org</a>:
     *
     * <br>
     * In probability theory and statistics, the standard deviation measures the amount of variation or
     * dispersion from the average. </p>
     *
     * @return The standard deviation of the distribution.
     */
    public Double getStandardDeviation() {
        lock.readLock().lock();
        try {
            Double standardDeviation = null;
            if (size > 0) {
                standardDeviation = (size > 1) ? Math.sqrt(m2 / (size - 1)) : 0;
            }
            return standardDeviation;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the <em>skewness</em> of the distribution.
     *
     * <p> Definition from <a href="http://en.wikipedia.org/wiki/Skewness">wikipedia.org</a>:
     *
     * <br>
     * In probability theory and statistics, <em>skewness</em> is a measure of the asymmetry of the probability
     * distribution of a real-valued random variable about its mean. </p>
     *
     * @return The skewness of the distribution.
     */
    public Double getSkewness() {
        lock.readLock().lock();
        try {
            Double skewness = null;
            if (size > 0) {
                skewness = (size > 1) ? (Math.sqrt(size) * m3 / Math.pow(m2, 1.5)) : 0;
            }
            return skewness;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the <em>kurtosis</em> of the distribution.
     *
     * <p> Definition from <a href="http://en.wikipedia.org/wiki/Kurtosis">wikipedia.org</a>:
     *
     * <br>
     * In probability theory and statistics, <em>kurtosis</em> is any measure of the "peakedness" of the
     * probability distribution of a real-valued random variable. </p>
     *
     * @return The kurtosis of the distribution.
     */
    public Double getKurtosis() {
        lock.readLock().lock();
        try {
            Double kurtosis = null;
            if (size > 0) {
                kurtosis = (size > 1) ? (((size * m4) / (m2 * m2))) : 0;
            }
            return kurtosis;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Add the specified value to the distribution.
     *
     * @param value The value.
     */
    public void add(double value) {
        lock.writeLock().lock();
        try {
            size++;
            minimum = minimum(value, minimum);
            maximum = maximum(value, maximum);
            sum = sum(value, sum);
            if (size > 1) {
                double m0 = mean;
                mean += ((value - mean) / size);
                updateCentralMoments(value, m0);
            } else {
                mean = value;
                zeroCentralMoments();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Set the maintained central moments to zero.
     */
    private void zeroCentralMoments() {
        m2 = 0;
        m3 = 0;
        m4 = 0;
    }

    /**
     * Compute the new central moments when the specified value is added to the distribution.
     *
     * @param value The value being added to the distribution.
     * @param mean0 The population mean of the distribution prior to the addition.
     */
    private void updateCentralMoments(double value, double mean0) {
        // population size (including the new value)
        double s1 = size;
        double s2 = s1 * s1;
        double s3 = s1 * s2;
        // deviation of the value from the initial population mean.
        double d1 = (value - mean0);
        double d2 = d1 * d1;
        double d3 = d1 * d2;
        double d4 = d1 * d3;
        // compute the new central moments.
        m4 += ((d4 * (s1 - 1) * ((s2 - (3 * s1) + 3) / s3)) + (6 * d2 * m2 / s2) - (4 * d1 * m3 / s1));
        m3 += ((d3 * (s1 - 1) * (s1 - 2) / s2) - (3 * d1 * m2 / s1));
        m2 += d2 * (s1 - 1) / s1;
    }

    /**
     * Create a copy of this object.
     *
     * @return A copy of this object.
     */
    public Statistics createCopy() {
        return copyInto(new Statistics());
    }

    /**
     * Copy the internal fields into the specified object.
     *
     * @param stats The statistics object to modify.
     *
     * @return A reference to the modified statistics object.
     */
    private Statistics copyInto(Statistics stats) {
        lock.readLock().lock();
        try {
            stats.size = size;
            stats.minimum = minimum;
            stats.mean = mean;
            stats.maximum = maximum;
            stats.sum = sum;
            stats.m2 = m2;
            stats.m3 = m3;
            stats.m4 = m4;
        } finally {
            lock.readLock().unlock();
        }
        return stats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' +
                size + ',' +
                minimum + "<=" + mean + "<=" + maximum + "<>" +
                getStandardDeviation() + ']';
    }
}