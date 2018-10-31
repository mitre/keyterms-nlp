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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Class for representing simple [year, month, day] arrays. Values may or may not correspond to an actual,
 * valid date.
 */
public class YearMonthDayArray {

    /**
     * Enumerated type for different calendar systems.
     */
    public enum CalendarSystem {
        GREGORIAN,
        HIJRAH
    }

    private static final int MAX_YEAR_DIGITS = 4;
    private static final int MAX_MONTH_DIGITS = 2;
    private static final int MAX_DAY_DIGITS = 2;

    private final String originalDateString;
    private int yearNumber;
    private int monthNumber;
    private int dayNumber;
    private CalendarSystem calendarSystem;
    private boolean isUnderspecified;

    /**
     * Constructs a default instance of this class.
     */
    public YearMonthDayArray() {
        yearNumber = 0;
        monthNumber = 0;
        dayNumber = 0;
        originalDateString = toSimpleString(yearNumber, monthNumber, dayNumber);
    }

    /**
     * Constructs an instance of this class from integers.
     *
     * @param year the integer corresponding to the year
     * @param month the integer corresponding to the month
     * @param day the integer corresponding to the day
     */
    public YearMonthDayArray(int year, int month, int day) {
        originalDateString = toSimpleString(year, month, day);
        updateTo(originalDateString);
        setIsUnderspecified();
    }

    /**
     * Constructs an instance of this class from a date string.
     *
     * @param dateString the simple date string
     */
    public YearMonthDayArray(String dateString) {
        originalDateString = dateString;
        updateTo(originalDateString);
        setIsUnderspecified();
    }

    /**
     * Gets an integer representing the year of this date.
     *
     * @return the year as an integer
     */
    public int getYear() {
        return yearNumber;
    }

    /**
     * Sets the year number of this object to the provided value.
     *
     * @param newYearNumber the new year number
     */
    public void setYear(int newYearNumber) {
        yearNumber = newYearNumber;
    }

    /**
     * Gets an integer representing the month of this date.
     *
     * @return the month as an integer
     */
    public int getMonth() {
        return monthNumber;
    }

    /**
     * Sets the month number of this object to the provided value.
     *
     * @param newMonthNumber the new month number
     */
    public void setMonth(int newMonthNumber) {
        monthNumber = newMonthNumber;
    }

    /**
     * Gets an integer representing the day of this date.
     *
     * @return the day as an integer
     */
    public int getDay() {
        return dayNumber;
    }

    /**
     * Sets the day number of this object to the provided value.
     *
     * @param newDayNumber the new day number
     */
    public void setDay(int newDayNumber) {
        dayNumber = newDayNumber;
    }

    /**
     * Gets the calendar system for this date (may be null).
     *
     * @return the calendar system for this date
     */
    public CalendarSystem getCalendarSystem() {
        return calendarSystem;
    }

    /**
     * Determines whether this date array has the same year, month, and day values as another date array.
     *
     * @param other the other date array
     *
     * @return true if this date array is the same as the other, else false
     */
    public boolean isSameDateAs(YearMonthDayArray other) {
        return yearNumber == other.yearNumber && monthNumber == other.monthNumber && dayNumber == other.dayNumber;
    }

    /**
     * Determines whether this date is underspecified.
     *
     * @return true if this date is underspecified, else false
     */
    public boolean isUnderspecified() {
        return isUnderspecified;
    }

    /**
     * Converts this date array to a string in a simple standardized format (YYYYMMDD).
     *
     * @return the date array as a string
     */
    public String toSimpleString() {
        return toSimpleString(yearNumber, monthNumber, dayNumber);
    }

    /**
     * Converts this date array to a list of integers in the format [year, month, day].
     *
     * @return the list of integers
     */
    public List<Integer> toArray() {
        return Arrays.asList(yearNumber, monthNumber, dayNumber);
    }

    /**
     * Determines whether the year+month+day combination represented by this object make up a
     * valid Gregorian calendar date.
     *
     * @return true if this is a valid Gregorian date, else false
     */
    public boolean isValidGregorianCalendarDate() {
        try {
            LocalDate.of(yearNumber, monthNumber, dayNumber);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (this.toSimpleString() + originalDateString).hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof YearMonthDayArray) {
            YearMonthDayArray ymdOther = (YearMonthDayArray)other;
            return (this.toSimpleString().equals(ymdOther.toSimpleString())
                    && this.originalDateString.equals(ymdOther.originalDateString));
        } else {
            return false;
        }
    }

    /* -------------------------- Private methods -------------------------------------- */

    /**
     * Update the fields of this object based on the provided string.
     *
     * @param dateString a string representing the new date
     */
    private void updateTo(String dateString) {
        DateParser dateParser = new DateParser(dateString);
        yearNumber = dateParser.extractYear();
        monthNumber = dateParser.extractMonthNumber();
        dayNumber = dateParser.extractDay();
        calendarSystem = dateParser.extractCalendarSystem();
    }

    /**
     * Creates a simple YYYYMMDD string from a given year, month, and day.
     *
     * @param year the year
     * @param month the month
     * @param day the day
     *
     * @return a simple YYYYMMDD string
     */
    private String toSimpleString(int year, int month, int day) {
        StringBuilder yearString = new StringBuilder();
        yearString.append(year);
        while (yearString.length() < MAX_YEAR_DIGITS) {
            yearString.insert(0, "0");
        }

        StringBuilder monthString = new StringBuilder();
        monthString.append(month);
        while (monthString.length() < MAX_MONTH_DIGITS) {
            monthString.insert(0, "0");
        }

        StringBuilder dayString = new StringBuilder();
        dayString.append(day);
        while (dayString.length() < MAX_DAY_DIGITS) {
            dayString.insert(0, "0");
        }

        String simpleString = yearString.toString() + monthString.toString() + dayString.toString();
        if (simpleString.length() > (MAX_YEAR_DIGITS + MAX_MONTH_DIGITS + MAX_DAY_DIGITS)) {
            throw new IllegalArgumentException(
                    "Invalid year (must be " + MAX_YEAR_DIGITS + " digits), " +
                            "month (must be " + MAX_MONTH_DIGITS + " digits), " +
                            "or day (must be " + MAX_DAY_DIGITS + " digits).");
        }

        return simpleString;
    }

    /**
     * Sets the isUnderspecified field, which indicates whether any component of this date is underspecified.
     */
    private void setIsUnderspecified() {
        isUnderspecified = (yearNumber == 0 || monthNumber == 0 || dayNumber == 0);
    }

}