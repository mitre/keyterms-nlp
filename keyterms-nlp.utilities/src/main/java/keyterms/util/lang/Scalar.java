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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * A numeric representation of a scalar value in a measurement system.
 *
 * <p> This class cannot represent fractional portions of the base unit or negative values. </p>
 * <p> Units should be an enumerated value class with the base (smallest) unit having a magnitude of {@code 1.0}. </p>
 */
public abstract class Scalar<U extends Enum<U> & ScalarUnit<U>>
        implements Comparable<Scalar<U>>, Serializable {
    /**
     * The class serial version identifier.
     */
    private static final long serialVersionUID = 7327278106708051477L;

    /**
     * Maintain only one sorted copy of the unit values per class.
     */
    private static final Map<Class<? extends ScalarUnit>, List<? extends ScalarUnit>> SORTED_UNITS = new HashMap<>();

    /**
     * Get the smallest unit of measure for the specified set of units.
     *
     * @param unitClass The enumerated unit values.
     *
     * @return The smallest unit of measure for the specified set of units.
     */
    private static <U extends Enum<U> & ScalarUnit<U>> U getBaseUnit(Class<U> unitClass) {
        if (unitClass == null) {
            throw new NullPointerException("Unit class is required.");
        }
        if (!SORTED_UNITS.containsKey(unitClass)) {
            synchronized (Scalar.class) {
                if (!SORTED_UNITS.containsKey(unitClass)) {
                    Comparator<U> sorter = Comparator.comparing(ScalarUnit::getMagnitude);
                    sorter = sorter.reversed();
                    List<U> sorted = Arrays.stream(unitClass.getEnumConstants())
                            .sorted(sorter)
                            .collect(Collectors.toList());
                    sorted = Collections.unmodifiableList(sorted);
                    if (sorted.isEmpty()) {
                        throw new IllegalArgumentException("No unit values enumerated.");
                    }
                    SORTED_UNITS.put(unitClass, sorted);
                }
            }
        }
        List<? extends ScalarUnit> sorted = SORTED_UNITS.get(unitClass);
        return unitClass.cast(sorted.get(sorted.size() - 1));
    }

    /**
     * The value of the scalar in the base unit.
     */
    private final long baseValue;

    /**
     * The base unit.
     */
    private final U baseUnit;

    /**
     * The largest whole number values for each unit in the unit space.
     */
    private final LinkedHashMap<U, Long> fields = new LinkedHashMap<>();

    /**
     * Constructor.
     *
     * @param unitClass The unit class.
     * @param value The value.
     */
    protected Scalar(Class<U> unitClass, long value) {
        this(unitClass, value, getBaseUnit(unitClass));
    }

    /**
     * Constructor.
     *
     * <p> If the specified unit is {@code null}, the base (smallest) unit is assumed. </p>
     *
     * @param unitClass The unit class.
     * @param value The value.
     * @param unit The unit of the specified value.
     */
    protected Scalar(Class<U> unitClass, double value, U unit) {
        super();
        if (unitClass == null) {
            throw new NullPointerException("Unit class is required.");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Negative values not allowed.");
        }
        baseUnit = getBaseUnit(unitClass);
        U valueUnit = (unit != null) ? unit : baseUnit;
        baseValue = (long)(value * valueUnit.getMagnitude());
        AtomicReference<Double> v = new AtomicReference<>((double)baseValue);
        SORTED_UNITS.get(unitClass).stream()
                .map(unitClass::cast)
                .forEach((u) -> {
                    double m = u.getMagnitude();
                    if (v.get() >= m) {
                        long uv = (long)(v.get() / m);
                        fields.put(u, uv);
                        v.set(v.get() - (uv * u.getMagnitude()));
                    } else {
                        fields.put(u, 0L);
                    }
                });
    }

    /**
     * Get the value of the scalar in the base unit.
     *
     * @return The value of the scalar in the base unit.
     */
    public long getBaseValue() {
        return baseValue;
    }

    /**
     * Get unit of the base value.
     *
     * @return The unit of the base value.
     */
    public U getBaseUnit() {
        return baseUnit;
    }

    /**
     * Get the value of the scalar in its largest non-zero units.
     *
     * @return The value of the scalar in its largest non-zero units.
     */
    public double getValue() {
        return as(getUnits());
    }

    /**
     * Get the largest non-zero unit associated with this value.
     *
     * @return The largest non-zero unit associated with this value.
     */
    public U getUnits() {
        return fields.entrySet().stream()
                .filter((e) -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(baseUnit);
    }

    /**
     * Get the current value in the specified units.
     *
     * <p> If the specified unit is {@code null}, the base (smallest) unit is assumed. </p>
     *
     * @param unit The unit of interest.
     *
     * @return The scalar represented as a value with the specified unit.
     */
    public double as(U unit) {
        U valueUnit = (unit != null) ? unit : baseUnit;
        return baseValue / valueUnit.getMagnitude();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(baseUnit.getClass(), baseValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = (this == obj);
        if (obj instanceof Scalar) {
            Scalar<?> scalar = (Scalar)obj;
            return ((baseUnit.getClass().equals(scalar.baseUnit.getClass())) &&
                    (baseValue == scalar.baseValue));
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Scalar<U> o) {
        if (!baseUnit.getClass().equals(o.baseUnit.getClass())) {
            throw new IllegalArgumentException("Non comparable units.");
        }
        return Double.compare(baseValue, o.baseValue);
    }

    /**
     * Create a textual summary of the scalar value expressed in its largest non-zero unit rounded to the specified
     * precision.
     *
     * <p> A {@code ~} character is prefixed to non-precise summaries. </p>
     * <p> E.g. 118 minutes = ~1.9 minutes with a precision of {@code 1}. </p>
     *
     * @param precision The desired decimal precision of the summary.  If the specified precision is less than zero,
     * the output precision will be {@code 0}.
     *
     * @return A textual summary of the scalar value expressed in its largest non-zero unit rounded to the specified
     * precision.
     */
    public String summary(int precision) {
        StringBuilder buffer = new StringBuilder();
        U topUnit = getUnits();
        int p = (precision > 0) ? precision : 0;
        double m = Math.pow(10, p);
        double v = as(topUnit);
        double r = Math.round(v * m) / m;
        String l = (r == 1) ? topUnit.getUnitLabel() : topUnit.getPluralLabel();
        buffer.append((v != r) ? "~" : "");
        if (r == Math.round(r)) {
            buffer.append(String.format("%d", (long)r)).append(' ').append(l);
        } else {
            buffer.append(String.format("%." + p + "f", r)).append(' ').append(l);
        }
        return buffer.toString();
    }

    /**
     * Create a textual summary of the scalar value expressed in its largest non-zero unit rounded to the specified
     * precision.
     *
     * <p> A {@code ~} character is prefixed to non-precise summaries. </p>
     * <p> E.g. 118 minutes = ~1.9 minutes with a precision of {@code 1}. </p>
     *
     * @param precision The desired decimal precision of the summary.  If the specified precision is less than zero,
     * the output precision will be {@code 0}.
     *
     * @return A textual summary of the scalar value expressed in its largest non-zero unit rounded to the specified
     * precision.
     */
    public String formattedSummary(int precision) {
        StringBuilder buffer = new StringBuilder();
        U topUnit = getUnits();
        int p = (precision > 0) ? precision : 0;
        double m = Math.pow(10, p);
        double v = as(topUnit);
        double r = Math.round(v * m) / m;
        buffer.append((v != r) ? "~" : "");
        String l = ((r == 1) && (p == 0)) ? topUnit.getUnitLabel() : topUnit.getPluralLabel();
        buffer.append(String.format("%." + p + "f", r)).append(' ').append(l);
        return buffer.toString();
    }

    /**
     * Create a textual description of the scalar value.
     *
     * @return A detailed textual description of the scalar value.
     */
    public String summaryDetails() {
        StringBuilder buffer = new StringBuilder();
        fields.forEach((u, v) -> {
            if (v > 0) {
                buffer.append(v);
                buffer.append(' ');
                if (v == 1) {
                    buffer.append(u.getUnitLabel());
                } else {
                    buffer.append(u.getPluralLabel());
                }
                buffer.append(", ");
            }
        });
        if (buffer.length() > 0) {
            buffer.setLength(buffer.length() - 2);
        } else {
            buffer.append("0 ").append(baseUnit.getPluralLabel());
        }
        return buffer.toString();
    }

    /**
     * Create a textual description of the scalar value.
     *
     * @return A detailed textual description of the scalar value.
     */
    public String fullDetails() {
        StringBuilder buffer = new StringBuilder();
        fields.forEach((u, v) -> {
            buffer.append(v);
            buffer.append(' ');
            if (v == 1) {
                buffer.append(u.getUnitLabel());
            } else {
                buffer.append(u.getPluralLabel());
            }
            buffer.append(", ");
        });
        if (buffer.length() > 0) {
            buffer.setLength(buffer.length() - 2);
        } else {
            buffer.append("0 ").append(baseUnit.getPluralLabel());
        }
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), summary(0));
    }
}