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
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class DateParser_UT {

    @Test
    public void fullySpecifiedFormattingAndCalendarSystemInferredTest_gregorian() {
        // year = 2017, month = 1, day = 13
        List<String> validNumericalDates = new ArrayList<>();
        validNumericalDates.add("20170113");
        validNumericalDates.add("2017-01-13");
        validNumericalDates.add("2017-1-13");
        validNumericalDates.add("2017/01/13");
        validNumericalDates.add("2017/1/13");
        validNumericalDates.add("2017.01.13");
        validNumericalDates.add("2017.1.13");
        validNumericalDates.add("2017 01 13");
        validNumericalDates.add("2017 1 13");
        validNumericalDates.add("13-01-2017");
        validNumericalDates.add("13-1-2017");
        validNumericalDates.add("13/1/2017");
        validNumericalDates.add("13/1/2017");
        validNumericalDates.add("13.01.2017");
        validNumericalDates.add("13.1.2017");
        validNumericalDates.add("13 01 2017");
        validNumericalDates.add("13 1 2017");

        for (String stringDate : validNumericalDates) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(2017, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(13, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        // year = 2017, month = 13, day = 1
        List<String> invalidNumericalDates = new ArrayList<>();
        invalidNumericalDates.add("2017-13-01");
        invalidNumericalDates.add("01-13-2017");

        for (String stringDate : invalidNumericalDates) {
            DateParser dp = new DateParser(stringDate); // invalid stringDate
            assertEquals(2017, dp.extractYear());
            assertEquals(13, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(1, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        // year = 2017, month = January, day = 13
        List<String> fullDates = new ArrayList<>();
        fullDates.add("January 13, 2017");
        fullDates.add("13 January 2017");
        fullDates.add("13 January, 2017");

        for (String stringDate : fullDates) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(2017, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("January", dp.extractMonthString());
            assertEquals(13, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        // year = 2017, month = Jan., day = 13
        List<String> abbreviatedDatesWithPeriods = new ArrayList<>();
        abbreviatedDatesWithPeriods.add("Jan. 13, 2017");
        abbreviatedDatesWithPeriods.add("13 Jan. 2017");
        abbreviatedDatesWithPeriods.add("13 Jan., 2017");

        for (String stringDate : abbreviatedDatesWithPeriods) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(2017, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("Jan.", dp.extractMonthString());
            assertEquals(13, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        // year = 2017, month = Jan, day = 13
        List<String> abbreviatedDatesWithoutPeriods = new ArrayList<>();
        abbreviatedDatesWithoutPeriods.add("Jan 13, 2017");
        abbreviatedDatesWithoutPeriods.add("13 Jan 2017");
        abbreviatedDatesWithoutPeriods.add("13 Jan, 2017");

        for (String stringDate : abbreviatedDatesWithoutPeriods) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(2017, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("Jan", dp.extractMonthString());
            assertEquals(13, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }
    }

    @Test
    public void underspecifiedFormattingAndCalendarSystemInferredTest_gregorian() {
        // year = 0, month = 1, day = 13
        List<String> validMonthDays = new ArrayList<>();
        validMonthDays.add("01/13");
        validMonthDays.add("01-13");
        validMonthDays.add("01.13");
        validMonthDays.add("01 13");
        validMonthDays.add("1/13");
        validMonthDays.add("1-13");
        validMonthDays.add("1.13");
        validMonthDays.add("1 13");

        for (String stringDate : validMonthDays) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(0, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(13, dp.extractDay());
            assertNull(dp.extractCalendarSystem());
        }

        // year = 0, month = 13, day = 1
        List<String> invalidMonthDays = new ArrayList<>();
        invalidMonthDays.add("13/01");
        invalidMonthDays.add("13-01");
        invalidMonthDays.add("13.01");
        invalidMonthDays.add("13 01");
        invalidMonthDays.add("13/1");
        invalidMonthDays.add("13-1");
        invalidMonthDays.add("13.1");
        invalidMonthDays.add("13 1");

        for (String stringDate : invalidMonthDays) {
            DateParser dp = new DateParser(stringDate); // invalid date
            assertEquals(0, dp.extractYear());
            assertEquals(13, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(1, dp.extractDay());
            assertNull(dp.extractCalendarSystem());
        }

        // year = 0, month = January, day = 13
        List<String> fullMonthDays = new ArrayList<>();
        fullMonthDays.add("January 13");
        fullMonthDays.add("13 January");

        for (String stringDate : fullMonthDays) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(0, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("January", dp.extractMonthString());
            assertEquals(13, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        // year = 0, month = Jan., day = 13
        List<String> abbreviatedMonthDaysWithPeriods = new ArrayList<>();
        abbreviatedMonthDaysWithPeriods.add("Jan. 13");
        abbreviatedMonthDaysWithPeriods.add("13 Jan.");

        for (String stringDate : abbreviatedMonthDaysWithPeriods) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(0, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("Jan.", dp.extractMonthString());
            assertEquals(13, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        // year = 0, month = Jan, day = 13
        List<String> abbreviatedMonthDaysWithoutPeriods = new ArrayList<>();
        abbreviatedMonthDaysWithoutPeriods.add("Jan 13");
        abbreviatedMonthDaysWithoutPeriods.add("13 Jan");

        for (String stringDate : abbreviatedMonthDaysWithoutPeriods) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(0, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("Jan", dp.extractMonthString());
            assertEquals(13, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        // year = 2017, month = 1, day = 0
        List<String> yearMonths = new ArrayList<>();
        yearMonths.add("01/2017");
        yearMonths.add("01-2017");
        yearMonths.add("01.2017");
        yearMonths.add("01 2017");
        yearMonths.add("1/2017");
        yearMonths.add("1-2017");
        yearMonths.add("1.2017");
        yearMonths.add("1 2017");
        yearMonths.add("2017/01");
        yearMonths.add("2017-01");
        yearMonths.add("2017.01");
        yearMonths.add("2017 01");
        yearMonths.add("2017/1");
        yearMonths.add("2017-1");
        yearMonths.add("2017.1");
        yearMonths.add("2017 1");

        for (String stringDate : yearMonths) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(2017, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(0, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        List<String> fullYearMonths = new ArrayList<>();
        fullYearMonths.add("January 2017");
        fullYearMonths.add("January, 2017");

        for (String stringDate : fullYearMonths) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(2017, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("January", dp.extractMonthString());
            assertEquals(0, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        List<String> abbreviatedYearMonthsWithPeriods = new ArrayList<>();
        abbreviatedYearMonthsWithPeriods.add("Jan. 2017");
        abbreviatedYearMonthsWithPeriods.add("Jan., 2017");

        for (String stringDate : abbreviatedYearMonthsWithPeriods) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(2017, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("Jan.", dp.extractMonthString());
            assertEquals(0, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        List<String> abbreviatedYearMonthsWithoutPeriods = new ArrayList<>();
        abbreviatedYearMonthsWithoutPeriods.add("Jan 2017");
        abbreviatedYearMonthsWithoutPeriods.add("Jan, 2017");

        for (String stringDate : abbreviatedYearMonthsWithoutPeriods) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(2017, dp.extractYear());
            assertEquals(1, dp.extractMonthNumber());
            assertEquals("Jan", dp.extractMonthString());
            assertEquals(0, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        DateParser dp = new DateParser("2017");
        assertEquals(2017, dp.extractYear());
        assertEquals(0, dp.extractMonthNumber());
        assertEquals("", dp.extractMonthString());
        assertEquals(0, dp.extractDay());
        assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidFormattingTest_gregorian() {
        new DateParser("Jan 13 '17");
        fail();
    }

    @Test
    public void fullySpecifiedFormattingAndCalendarSystemInferredTest_hijrah() {
        // year = 1438, month = 4, day = 15
        List<String> validNumericalDates = new ArrayList<>();
        validNumericalDates.add("14380415");
        validNumericalDates.add("1438-04-15");
        validNumericalDates.add("1438-4-15");
        validNumericalDates.add("1438/04/15");
        validNumericalDates.add("1438/4/15");
        validNumericalDates.add("1438.04.15");
        validNumericalDates.add("1438.4.15");
        validNumericalDates.add("1438 04 15");
        validNumericalDates.add("1438 4 15");
        validNumericalDates.add("15-04-1438");
        validNumericalDates.add("15-4-1438");
        validNumericalDates.add("15/04/1438");
        validNumericalDates.add("15/4/1438");
        validNumericalDates.add("15.04.1438");
        validNumericalDates.add("15.4.1438");
        validNumericalDates.add("15 04 1438");
        validNumericalDates.add("15 4 1438");

        for (String stringDate : validNumericalDates) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(1438, dp.extractYear());
            assertEquals(4, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(15, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.HIJRAH);
        }

        // year = 1438, month = 15, day = 4
        List<String> invalidNumericalDates = new ArrayList<>();
        invalidNumericalDates.add("1438-15-04");
        invalidNumericalDates.add("04-15-1438");

        for (String stringDate : invalidNumericalDates) {
            DateParser dp = new DateParser(stringDate); // invalid stringDate
            assertEquals(1438, dp.extractYear());
            assertEquals(15, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(4, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.HIJRAH);
        }

        List<String> fullDates = new ArrayList<>();
        fullDates.add("Rabi Al-Thani 15, 1438");
        fullDates.add("15 Rabi Al-Thani 1438");
        fullDates.add("15 Rabi Al-Thani, 1438");

        for (String stringDate : fullDates) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(1438, dp.extractYear());
            assertEquals(4, dp.extractMonthNumber());
            assertEquals("Rabi Al-Thani", dp.extractMonthString());
            assertEquals(15, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.HIJRAH);
        }
    }

    @Test
    public void underspecifiedFormattingAndCalendarSystemInferredTest_hijrah() {
        // year = 0, month = 4, day = 15
        List<String> validMonthDays = new ArrayList<>();
        validMonthDays.add("04/15");
        validMonthDays.add("04-15");
        validMonthDays.add("04 15");
        validMonthDays.add("4/15");
        validMonthDays.add("4-15");
        validMonthDays.add("4 15");

        for (String stringDate : validMonthDays) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(0, dp.extractYear());
            assertEquals(4, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(15, dp.extractDay());
            assertNull(dp.extractCalendarSystem());
        }

        // year = 0, month = 15, day = 4
        List<String> invalidMonthDays = new ArrayList<>();
        invalidMonthDays.add("15/04");
        invalidMonthDays.add("15-04");
        invalidMonthDays.add("15 04");
        invalidMonthDays.add("15/4");
        invalidMonthDays.add("15-4");
        invalidMonthDays.add("15 4");

        for (String stringDate : invalidMonthDays) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(0, dp.extractYear());
            assertEquals(15, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(4, dp.extractDay());
            assertNull(dp.extractCalendarSystem());
        }

        // year = 0, month = Rabi Al-Thani, day = 15
        List<String> fullMonthDays = new ArrayList<>();
        fullMonthDays.add("Rabi Al-Thani 15");
        fullMonthDays.add("15 Rabi Al-Thani");

        for (String stringDate : fullMonthDays) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(0, dp.extractYear());
            assertEquals(4, dp.extractMonthNumber());
            assertEquals("Rabi Al-Thani", dp.extractMonthString());
            assertEquals(15, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.HIJRAH);
        }

        // year = 1438, month = 1, day = 0
        List<String> yearMonths = new ArrayList<>();
        yearMonths.add("04/1438");
        yearMonths.add("04-1438");
        yearMonths.add("04 1438");
        yearMonths.add("4/1438");
        yearMonths.add("4-1438");
        yearMonths.add("4 1438");

        for (String stringDate : yearMonths) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(1438, dp.extractYear());
            assertEquals(4, dp.extractMonthNumber());
            assertEquals("", dp.extractMonthString());
            assertEquals(0, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.HIJRAH);
        }

        List<String> fullYearMonths = new ArrayList<>();
        fullYearMonths.add("Rabi Al-Thani 1438");
        fullYearMonths.add("Rabi Al-Thani, 1438");

        for (String stringDate : fullYearMonths) {
            DateParser dp = new DateParser(stringDate);
            assertEquals(1438, dp.extractYear());
            assertEquals(4, dp.extractMonthNumber());
            assertEquals("Rabi Al-Thani", dp.extractMonthString());
            assertEquals(0, dp.extractDay());
            assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.HIJRAH);
        }

        DateParser dp = new DateParser("1438");
        assertEquals(1438, dp.extractYear());
        assertEquals(0, dp.extractMonthNumber());
        assertEquals("", dp.extractMonthString());
        assertEquals(0, dp.extractDay());
        assertSame(dp.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.HIJRAH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidFormattingTest_hijrah() {
        new YearMonthDayArray("4-13, '38");
        fail();
    }

    @Test
    public void calendarSystemInferredFromGregorianDatesOnlyTest() {
        int[] gregorianDatesOnly_months = { 1, 3, 4, 5, 6, 7, 8, 8, 10, 10, 12 };
        int[] gregorianDatesOnly_days = { 31, 31, 30, 31, 30, 31, 30, 31, 30, 31, 31 };
        for (int i = 0; i < gregorianDatesOnly_days.length; i++) {
            String dateString = gregorianDatesOnly_months[i] + "-" + gregorianDatesOnly_days[i];
            DateParser d = new DateParser(dateString);
            assertSame(d.extractCalendarSystem(), YearMonthDayArray.CalendarSystem.GREGORIAN);
        }

        // test a few non-Gregorian-only dates
        DateParser d = new DateParser("02-04");
        assertNull(d.extractCalendarSystem());

        d = new DateParser("9-24");
        assertNull(d.extractCalendarSystem());
    }
}