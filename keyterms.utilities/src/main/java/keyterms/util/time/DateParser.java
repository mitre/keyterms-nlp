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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts and stores information from a date string in one of several valid formats. Stores some general
 * information about the Gregorian and Hijrah calendar systems, such as month names.
 */
public class DateParser {

    /**
     * A constant containing the maximum number of months in any calendar system that this class supports.
     */
    public static final int MAX_MONTHS = 12;

    /**
     * A constant containing a list of the Gregorian month names (used primarily for fancy printing).
     */
    public static final String[] gregorianMonthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    /**
     * A constant containing a list of the Hijrah month names (used primarily for fancy printing).
     */
    public static final String[] hijrahMonthNames = {
            "Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani", "Jumada al-awwal", "Jumada al-thani",
            "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    };

    /**
     * Set of dates in the format [month, day] that only exist in the Gregorian calendar system.
     */
    public static final Set<List<Integer>> gregorianDatesOnly;

    static {
        gregorianDatesOnly = new HashSet<>();
        int[] gregorianDatesOnly_months = { 1, 3, 4, 5, 6, 7, 8, 8, 10, 10, 12 };
        int[] gregorianDatesOnly_days = { 31, 31, 30, 31, 30, 31, 30, 31, 30, 31, 31 };
        for (int i = 0; i < gregorianDatesOnly_days.length; i++) {
            gregorianDatesOnly.add(Arrays.asList(gregorianDatesOnly_months[i], gregorianDatesOnly_days[i]));
        }
    }

    /**
     * A class for representing regex-related tuples in the format [regular expression, relevant regex matcher groups].
     */
    private class RegexTuple {
        private final String regex;
        private final int[] relevantGroups;

        RegexTuple(String regex, int[] relevantGroups) {
            this.regex = regex;
            this.relevantGroups = relevantGroups;
        }
    }

    private Map<String, Integer> gregorianMonthIndices;
    private Map<String, Integer> hijrahMonthIndices;
    private YearMonthDayArray.CalendarSystem calendarSystem;
    private int extractedYear;
    private String extractedMonthString;
    private int extractedMonthNumber;
    private int extractedDay;

    /**
     * Creates an instance of this class by extracting information from a date string.
     * <p>
     * The following fully-specified date formats are accepted (case and leading 0s do not matter):<br>
     * - Format family 1 (yearNumber-monthNumber-dayNumber)<br>
     * -- 20170113<br>
     * <p>
     * - Format family 2 (yearNumber-monthNumber-dayNumber, digits only)<br>
     * -- 2017-01-13<br>
     * -- 2017/01/13<br>
     * -- 2017.01.13<br>
     * -- 2017 01 13<br>
     * <p>
     * - Format family 3 (monthNumber-dayNumber-yearNumber)<br>
     * -- 01-13-2017<br>
     * -- 01/13/2017<br>
     * -- 01.13.2017<br>
     * -- 01 13 2017<br>
     * <p>
     * - Format family 4 (monthNumber-dayNumber-yearNumber)<br>
     * -- January 13, 2017<br>
     * -- Jan 13, 2017<br>
     * <p>
     * - Format family 5 (dayNumber-monthNumber-yearNumber)<br>
     * -- 13 January 2017<br>
     * -- 13 January, 2017<br>
     * -- 13 Jan. 2017<br>
     * -- 13 Jan 2017<br>
     * -- 13 Jan., 2017<br>
     * -- 13 Jan, 2017<br>
     * <p>
     * Additionally, the following underspecified date formats are accepted (case and leading 0s do not matter):<br>
     * - Format family 6 (monthNumber-dayNumber or dayNumber-monthNumber, digits only)<br>
     * -- 01-13<br>
     * -- 01/03<br>
     * -- 01.03<br>
     * -- 01 13<br>
     * -- 13-01<br>
     * -- 13/01<br>
     * -- 13.01<br>
     * -- 13 01<br>
     * <p>
     * - Format family 7 (monthNumber-dayNumber)<br>
     * -- January 13<br>
     * -- Jan. 13<br>
     * -- Jan 13<br>
     * <p>
     * - Format family 8 (dayNumber-monthNumber)<br>
     * -- 13 January<br>
     * -- 13 Jan.<br>
     * -- 13 Jan<br>
     * <p>
     * Format family 9 (yearNumber-monthNumber, digits only)<br>
     * - 2017-01
     * - 2017/01
     * - 2017.01
     * - 2017 01
     * <p>
     * - Format family 10 (monthNumber-yearNumber, digits only)<br>
     * -- 01-2017<br>
     * -- 01/2017<br>
     * -- 01.2017<br>
     * -- 01 2017<br>
     * <p>
     * - Format family 11 (monthNumber-yearNumber)<br>
     * -- January 2017<br>
     * -- January, 2017<br>
     * -- Jan. 2017<br>
     * -- Jan 2017<br>
     * -- Jan., 2017<br>
     * -- Jan, 2017<br>
     * <p>
     * - Format family 12 (yearNumber only)<br>
     * -- 2017<br>
     *
     * @param dateString the date string to parse
     */
    public DateParser(String dateString) {
        initializeMonthNumbers();
        if (!parse(dateString)) {
            throw new IllegalArgumentException("Invalid date format.");
        }
    }

    /**
     * Gets the year that was extracted from the date string.
     *
     * @return the year as an int
     */
    public int extractYear() {
        return extractedYear;
    }

    /**
     * Gets the name of the month that was extracted from the date string.
     *
     * @return the month as a string
     */
    public String extractMonthString() {
        return extractedMonthString;
    }

    /**
     * Gets the month (number) that was extracted from the date string.
     *
     * @return the month as an int
     */
    public int extractMonthNumber() {
        return extractedMonthNumber;
    }

    /**
     * Gets the day that was extracted from the date string.
     *
     * @return the day as an int
     */
    public int extractDay() {
        return extractedDay;
    }

    /**
     * Gets the calendar system that was inferred from the date string.
     *
     * @return the calendar system
     */
    public YearMonthDayArray.CalendarSystem extractCalendarSystem() {
        return calendarSystem;
    }

    /* -------------------------- Private methods -------------------------------------- */

    /**
     * Initializes the map that stores (monthNumber string, monthNumber number) pairs.
     */
    private void initializeMonthNumbers() {
        Map<String, Integer> tempGregorianNumbers = new HashMap<>();
        Map<String, Integer> tempHijrahNumbers = new HashMap<>();

        for (int i = 0; i < MAX_MONTHS; i++) {
            int realMonthNumber = i + 1;

            // Gregorian
            String keyifiedMonth = keyify(gregorianMonthNames[i]);
            tempGregorianNumbers.put(keyifiedMonth, realMonthNumber);
            tempGregorianNumbers.put(keyifiedMonth.substring(0, 3), realMonthNumber);

            // Hijrah
            keyifiedMonth = keyify(hijrahMonthNames[i]);
            tempHijrahNumbers.put(keyifiedMonth, realMonthNumber);
        }

        // other monthNumber variations, just in case
        tempGregorianNumbers.put("sept", 9);
        tempHijrahNumbers.put("dhualqadah", 11);
        tempHijrahNumbers.put("dhulqidah", 11);
        tempHijrahNumbers.put("dhulqadah", 11);
        tempHijrahNumbers.put("dhulhijjah", 12);

        gregorianMonthIndices = Collections.unmodifiableMap(tempGregorianNumbers);
        hijrahMonthIndices = Collections.unmodifiableMap(tempHijrahNumbers);
    }

    /**
     * Matches the date string to one of the accepted formats and extracts information from it.
     *
     * @param dateString the string from which to extract information
     *
     * @return true if the string was successfully parsed; else false
     */
    private boolean parse(String dateString) {
        List<RegexTuple> formatFamilies = new ArrayList<>();

        // fully specified date: < regular expression, year-month-day group index order for matched string >
        formatFamilies.add(new RegexTuple("^(\\d{4})(\\d{2})(\\d{2})$", new int[] { 1, 2, 3 })); // 1
        formatFamilies.add(
                new RegexTuple("^(\\d{4})([\\-/\\. ])(\\d{1,2})([\\-/\\. ])(\\d{1,2})$", new int[] { 1, 3, 5 })
        ); // 2
        formatFamilies.add(
                new RegexTuple("^(\\d{1,2})([\\-/\\. ])(\\d{1,2})([\\-/\\. ])(\\d{4})$", new int[] { 5, 3, 1 })
        ); // 3
        formatFamilies.add(
                new RegexTuple("^([a-zA-Z\\-`' ]+\\.?)( )(\\d{1,2})(,? )(\\d{4})$", new int[] { 5, 1, 3 })
        ); // 4
        formatFamilies.add(
                new RegexTuple("^(\\d{1,2})( )([a-zA-Z\\-`' ]+\\.?)(,? )(\\d{4})$", new int[] { 5, 3, 1 })
        ); // 5

        // partially specified date: < regular expression, month-day/day-month group index order for matched string >
        formatFamilies.add(new RegexTuple("^(\\d{1,2})([\\-/\\. ])(\\d{1,2})$", new int[] { 1, 3 })); // 6

        // partially specified date: < regular expression, month-day group index order for matched string >
        formatFamilies.add(new RegexTuple("^([a-zA-Z\\-`' ]+\\.?)( )(\\d{1,2})$", new int[] { 1, 3 })); // 7

        // partially specified date: < regular expression, day-month group index order for matched string >
        formatFamilies.add(new RegexTuple("^(\\d{1,2})( )([a-zA-Z\\-`' ]+\\.?)$", new int[] { 3, 1 })); // 8

        // partially specified date: < regular expression, year-month group index order for matched string >
        formatFamilies.add(new RegexTuple("^(\\d{4})([\\-/\\. ])(\\d{1,2})$", new int[] { 1, 3 })); // 9
        formatFamilies.add(new RegexTuple("^(\\d{1,2})([\\-/\\. ])(\\d{4})$", new int[] { 3, 1 })); // 10
        formatFamilies.add(new RegexTuple("^([a-zA-Z\\-`' ]+\\.?)(,? )(\\d{4})$", new int[] { 3, 1 })); // 11

        // partially specified date: tuple structure: < regular expression, year group index for matched string >
        formatFamilies.add(new RegexTuple("^(\\d{4})$", new int[] { 1 })); // 12

        // match the date string to one of the accepted formats
        int[] ymdMatcherGroupNumbers = null;
        List<String> matcherGroups = new ArrayList<>();
        int currentFormatFamily = 1;
        boolean formatFound = false;
        while (!formatFound && currentFormatFamily <= formatFamilies.size()) {
            RegexTuple formatFamilyTuple = formatFamilies.get(currentFormatFamily - 1);
            Pattern datePattern = Pattern.compile(formatFamilyTuple.regex);
            Matcher dateMatcher = datePattern.matcher(dateString);
            if (dateMatcher.find()) {
                formatFound = true;
                ymdMatcherGroupNumbers = formatFamilyTuple.relevantGroups;
                for (int i = 0; i <= dateMatcher.groupCount(); i++) {
                    matcherGroups.add(dateMatcher.group(i));
                }
            } else {
                currentFormatFamily += 1;
            }
        }

        // extract the date components from the date string
        extractedYear = 0;
        extractedMonthNumber = 0;
        extractedMonthString = "";
        extractedDay = 0;
        calendarSystem = null;
        if (formatFound) {
            switch (currentFormatFamily) {
                case 1:
                case 2:
                case 3:
                    extractedYear = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[0]));
                    extractedMonthNumber = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[1]));
                    extractedDay = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[2]));
                    break;
                case 4:
                case 5:
                    extractedYear = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[0]));
                    extractedMonthString = matcherGroups.get(ymdMatcherGroupNumbers[1]);
                    extractedMonthNumber = getMonthIndexAndSetCalendarSystem(extractedMonthString);
                    extractedDay = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[2]));
                    break;
                case 6:
                    extractedMonthNumber = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[0]));
                    extractedDay = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[1]));
                    break;
                case 7:
                case 8:
                    extractedMonthString = matcherGroups.get(ymdMatcherGroupNumbers[0]);
                    extractedMonthNumber = getMonthIndexAndSetCalendarSystem(extractedMonthString);
                    extractedDay = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[1]));
                    break;
                case 9:
                case 10:
                    extractedYear = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[0]));
                    extractedMonthNumber = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[1]));
                    break;
                case 11:
                    extractedYear = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[0]));
                    extractedMonthString = matcherGroups.get(ymdMatcherGroupNumbers[1]);
                    extractedMonthNumber = getMonthIndexAndSetCalendarSystem(extractedMonthString);
                    break;
                case 12:
                    extractedYear = Integer.parseInt(matcherGroups.get(ymdMatcherGroupNumbers[0]));
                    break;
                default:
                    // currentFormatFamily will not exceed formatFamilies.size()
            }

            if (calendarSystem == null) {
                inferCalendarSystem();
            }

            return true;

        } else {
            return false;
        }
    }

    /**
     * Infers the calendar system based on the extracted year.
     */
    private void inferCalendarSystem() {
        // based on year
        if (extractedYear > 1300 && extractedYear < 1600) {
            calendarSystem = YearMonthDayArray.CalendarSystem.HIJRAH;
        } else {
            if (extractedYear > 1900 && extractedYear < 2100) {
                calendarSystem = YearMonthDayArray.CalendarSystem.GREGORIAN;
            } else {
                if (gregorianDatesOnly.contains(Arrays.asList(extractedMonthNumber, extractedDay))) {
                    calendarSystem = YearMonthDayArray.CalendarSystem.GREGORIAN;
                }
            }
        }
    }

    /**
     * Retrieves the month number by setting the appropriate calendar system and using the month string as a key in
     * corresponding month-index map.
     *
     * @param monthString the month string
     *
     * @return the number that corresponds to the provided month
     */
    private int getMonthIndexAndSetCalendarSystem(String monthString) {
        String monthKey = keyify(monthString);
        if (hijrahMonthIndices.containsKey(monthKey)) {
            calendarSystem = YearMonthDayArray.CalendarSystem.HIJRAH;
            return hijrahMonthIndices.get(monthKey);
        } else {
            calendarSystem = YearMonthDayArray.CalendarSystem.GREGORIAN;
            return gregorianMonthIndices.get(monthKey);
        }
    }

    /**
     * Prepares strings for use as keys by removing common punctuation and spaces.
     *
     * @param fullString the full monthNumber string
     *
     * @return the simplified, key-ready monthNumber string
     */
    private String keyify(String fullString) {
        return fullString.replaceAll("[-'`. ]", "").toLowerCase();
    }

}