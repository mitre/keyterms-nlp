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

import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import keyterms.util.text.EditDistance;
import keyterms.util.text.distance.Levenshtein;

/**
 * A class for comparing dates.
 */
public class DateCompare {

    private static final EditDistance levenshtein = new Levenshtein();

    // weights should add to 1
    private static final List<Double> SUB_SCORE_WEIGHTS = Arrays.asList(
            0.25, // YMD score
            0.25, // Levenshtein sim score
            0.30, // proximity score
            0.15, // cosSim score
            0.05  // specification score
    );

    // weights should add to 1
    private static final List<Double> YMD_WEIGHTS = Arrays.asList(
            0.40, // year weighting
            0.35, // month weighting
            0.25  // day weighting
    );

    private static final int DEFAULT_YEAR_GREGORIAN = 2017;
    private static final int DEFAULT_YEAR_HIJRAH = 1438;
    private static final double MAX_DAYS_APART = 365.0 * 5;

    /**
     * Hybrid algorithm that incorporates heuristic rules and cosine similarity to give a better composite
     * measure.
     *
     * @param dateString1 a string representing the first date
     * @param dateString2 a string representing the second date
     *
     * @return a value between 0.00 and 1.00 indicating the degree of similarity between the two dates
     */
    public static double compare(String dateString1, String dateString2) {
        return compare(dateString1, dateString2, false);
    }

    /**
     * Hybrid algorithm that incorporates heuristic rules and cosine similarity to give a better composite
     * measure.
     *
     * @param dateString1 a string representing the first date
     * @param dateString2 a string representing the second date
     * @param verbose boolean flag for enabling verbosity during similarity computation
     *
     * @return a value between 0.00 and 1.00 indicating the degree of similarity between the two dates
     */
    public static double compare(String dateString1, String dateString2, boolean verbose) {
        YearMonthDayArray d1 = new YearMonthDayArray(dateString1);
        YearMonthDayArray d2 = new YearMonthDayArray(dateString2);

        // compute YMD score
        double ymdScore = 0;
        if (d1.getYear() != 0 && d2.getYear() != 0 && (d1.getYear() == d2.getYear())) {
            ymdScore += YMD_WEIGHTS.get(0);
        }

        if (d1.getMonth() != 0 && d2.getMonth() != 0 && (d1.getMonth() == d2.getMonth())) {
            ymdScore += YMD_WEIGHTS.get(1);
        }

        if (d1.getDay() != 0 && d2.getDay() != 0 && (d1.getDay() == d2.getDay())) {
            ymdScore += YMD_WEIGHTS.get(2);
        }

        // compute Levenshtein similarity score
        double levenshteinSimScore = levenshtein.getSimilarity(d1.toSimpleString(), d2.toSimpleString());

        // compute proximity score
        double proximityScore = 0;
        List<YearMonthDayArray> preparedDates = prepareDates(d1, d2);
        YearMonthDayArray pd1 = preparedDates.get(0);
        YearMonthDayArray pd2 = preparedDates.get(1);
        if (pd1.isValidGregorianCalendarDate() && pd2.isValidGregorianCalendarDate()) {
            long epochDayD1 = LocalDate.of(pd1.getYear(), pd1.getMonth(), pd1.getDay()).toEpochDay();
            long epochDayD2 = LocalDate.of(pd2.getYear(), pd2.getMonth(), pd2.getDay()).toEpochDay();
            long diff = Math.abs(epochDayD1 - epochDayD2);

            if (diff <= MAX_DAYS_APART) {
                proximityScore = (MAX_DAYS_APART - diff) / MAX_DAYS_APART;
            }
        }

        // compute cosSim score
        double cosSimScore = cosSimCompare(dateString1, dateString2);

        // compute specification score
        double specificationScore = (!d1.isUnderspecified() && !d2.isUnderspecified()) ? 1 : 0;

        // compute and return final score
        double score = SUB_SCORE_WEIGHTS.get(0) * ymdScore
                + SUB_SCORE_WEIGHTS.get(1) * levenshteinSimScore
                + SUB_SCORE_WEIGHTS.get(2) * proximityScore
                + SUB_SCORE_WEIGHTS.get(3) * cosSimScore
                + SUB_SCORE_WEIGHTS.get(4) * specificationScore;

        if (verbose) {
            System.out.println(d1.toSimpleString() + " to " + d2.toSimpleString() + ": ");
            System.out.println("ymdScore            = " + ymdScore);
            System.out.println("-> weighted         = " + (SUB_SCORE_WEIGHTS.get(0) * ymdScore));
            System.out.println("levenshteinSimScore = " + levenshteinSimScore);
            System.out.println("-> weighted         = " + (SUB_SCORE_WEIGHTS.get(1) * levenshteinSimScore));
            System.out.println("proximityScore      = " + proximityScore);
            System.out.println("-> weighted         = " + (SUB_SCORE_WEIGHTS.get(2) * proximityScore));
            System.out.println("cosSimScore         = " + cosSimScore);
            System.out.println("-> weighted         = " + (SUB_SCORE_WEIGHTS.get(3) * cosSimScore));
            System.out.println("specificationScore  = " + specificationScore);
            System.out.println("-> weighted         = " + (SUB_SCORE_WEIGHTS.get(4) * specificationScore));
            System.out.println("final score         = " + score);
            System.out.println();
        }

        return score;
    }

    /**
     * Compares two dates based on their cosine similarity.
     *
     * @param dateString1 a string representing the first date
     * @param dateString2 a string representing the second date
     *
     * @return a value between 0.00 and 1.00 indicating the degree of similarity between the two dates
     */
    public static double cosSimCompare(String dateString1, String dateString2) {
        String zeroDate = "00000000";
        List<YearMonthDayArray> preparedDates =
                prepareDates(new YearMonthDayArray(dateString1), new YearMonthDayArray(dateString2));

        double score;
        if (preparedDates.get(0).toSimpleString().equals(zeroDate) ||
                preparedDates.get(1).toSimpleString().equals(zeroDate)) {
            score = 0.0;
        } else {
            List<Double> dateVector1 = VectorOps.convertToDoubles(preparedDates.get(0).toArray());
            List<Double> dateVector2 = VectorOps.convertToDoubles(preparedDates.get(1).toArray());
            score = VectorOps.weightedCosSim(dateVector1, dateVector2, YMD_WEIGHTS);
        }

        return score;
    }

    /**
     * Approximation of a previously-used similarity algorithm for backwards compatibility.
     *
     * @param dateString1 a string representing the first date
     * @param dateString2 a string representing the second date
     * @param verbose flag for displaying internal information
     *
     * @return a value between 1 and -1 (inclusive) indicating the degree of similarity between the two dates
     */
    public static double digitCompare(String dateString1, String dateString2, boolean verbose) {
        double toReturn;
        if (dateString1.equals("") || dateString2.equals("")) {
            toReturn = 0.0;
        } else {
            YearMonthDayArray date1 = new YearMonthDayArray(dateString1);
            YearMonthDayArray date2 = new YearMonthDayArray(dateString2);

            boolean haveSameYear = haveSameYear(date1, date2);
            boolean haveSameMonth = haveSameMonth(date1, date2);
            boolean haveSameDay = haveSameDay(date1, date2);
            boolean dayAndMonthAreInverted = dayAndMonthAreInverted(date1, date2);
            boolean offByOneDay = offByOneDay(date1, date2);
            boolean offByOneYear = offByOneYear(date1, date2);
            boolean offByOneMonth = offByOneMonth(date1, date2);
            int numberOfDigitsOff = getNumberOfDigitsOff(date1, date2);
            int numberOfTranspositions = getNumberOfTranspositions(date1, date2);

            if (date1.isSameDateAs(date2)) {
                toReturn = 1.00;
            } else {
                if (haveSameYear && (dayAndMonthAreInverted || (offByOneDay && haveSameMonth))) {
                    toReturn = 0.90;
                } else {
                    if (haveSameYear && offByOneMonth && offByOneDay) {
                        toReturn = 0.40;
                    } else {
                        if (offByOneYear && (dayAndMonthAreInverted || (offByOneDay && haveSameMonth))) {
                            toReturn = 0.20;
                        } else {
                            if (numberOfTranspositions == 1 || numberOfDigitsOff == 1) {
                                toReturn = 0.10;
                            } else {
                                if (haveSameYear && !haveSameMonth && !haveSameDay) {
                                    toReturn = -0.25;
                                } else {
                                    if (offByOneYear && !haveSameMonth && !haveSameDay) {
                                        toReturn = -0.50;
                                    } else {
                                        toReturn = -1.0;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (verbose) {
                System.out.println(date1.toSimpleString() + " vs. " + date2.toSimpleString());
                System.out.println("haveSameYear           = " + haveSameYear);
                System.out.println("haveSameMonth          = " + haveSameMonth);
                System.out.println("dayAndMonthAreInverted = " + dayAndMonthAreInverted);
                System.out.println("offByOneDay            = " + offByOneDay);
                System.out.println("offByOneYear           = " + offByOneYear);
                System.out.println("numberOfDigitsOff      = " + numberOfDigitsOff);
                System.out.println("numberOfTranspositions = " + numberOfTranspositions);
                System.out.println("similarity             = " + toReturn);
            }
        }

        return toReturn;
    }

    /**
     * Approximation of a previously-used similarity algorithm for backwards compatibility.
     *
     * @param dateString1 a string representing the first date
     * @param dateString2 a string representing the second date
     *
     * @return a value between 1 and -1 (inclusive) indicating the degree of similarity between the two dates
     */
    public static double digitCompare(String dateString1, String dateString2) {
        return digitCompare(dateString1, dateString2, false);
    }

    /* -------------------------- Private methods -------------------------------------- */

    /**
     * Ensures that both dates are fully specified. Supplied dates can be in any of the following forms:
     * - YMD <br>
     * - YM_ <br>
     * - Y__ <br>
     * - _MD <br>
     * - _M_ <br>
     *
     * @param date1 the first YearMonthDayArray
     * @param date2 the second YearMonthDayArray
     *
     * @return an array containing two fully-specified YearMonthDayArrays
     */
    private static List<YearMonthDayArray> prepareDates(YearMonthDayArray date1, YearMonthDayArray date2) {
        YearMonthDayArray newDate1 = new YearMonthDayArray(date1.toSimpleString());
        YearMonthDayArray newDate2 = new YearMonthDayArray(date2.toSimpleString());

        // ensure that both dates have a year by setting missing years to a default year
        if (date1.isUnderspecified() || date2.isUnderspecified()) {
            if (newDate1.getYear() == 0 && newDate2.getYear() != 0) {
                newDate1.setYear(newDate2.getYear());
            } else {
                if (newDate2.getYear() == 0 && newDate1.getYear() != 0) {
                    newDate2.setYear(newDate1.getYear());
                } else {
                    if (newDate1.getYear() == 0 && newDate2.getYear() == 0) {
                        if (newDate1.getCalendarSystem() == YearMonthDayArray.CalendarSystem.HIJRAH) {
                            newDate1.setYear(DEFAULT_YEAR_HIJRAH);
                        } else {
                            newDate1.setYear(DEFAULT_YEAR_GREGORIAN);
                        }

                        if (newDate2.getCalendarSystem() == YearMonthDayArray.CalendarSystem.HIJRAH) {
                            newDate2.setYear(DEFAULT_YEAR_HIJRAH);
                        } else {
                            newDate2.setYear(DEFAULT_YEAR_GREGORIAN);
                        }
                    }
                }
            }
        }

        // ensure that both dates have a month by setting the missing month to be half a year away from the
        // non-missing month, or if both months are missing, to the year's middle month
        if (date1.isUnderspecified() || date2.isUnderspecified()) {
            if (newDate1.getMonth() == 0 && newDate2.getMonth() != 0) {
                newDate1.setMonth(newDate2.getMonth() - 1 + (DateParser.MAX_MONTHS / 2) % DateParser.MAX_MONTHS + 1);
            } else {
                if (newDate2.getMonth() == 0 && newDate1.getMonth() != 0) {
                    newDate2.setMonth(
                            newDate1.getMonth() - 1 + (DateParser.MAX_MONTHS / 2) % DateParser.MAX_MONTHS + 1);
                } else {
                    if (newDate1.getMonth() == 0 && newDate2.getMonth() == 0) {
                        int defaultMonth = DateParser.MAX_MONTHS / 2;
                        newDate1.setMonth(defaultMonth);
                        newDate2.setMonth(defaultMonth);
                    }
                }
            }
        }

        List<YearMonthDayArray> newDates = new ArrayList<>(Arrays.asList(newDate1, newDate2));

        // ensure that both dates have a day by setting missing days to the midpoint of the month
        if (date1.isUnderspecified() || date2.isUnderspecified()) {
            for (YearMonthDayArray newDate : newDates) {
                if (newDate.getDay() == 0) {
                    int dayNumber;
                    if (newDate.getCalendarSystem() == YearMonthDayArray.CalendarSystem.HIJRAH) {
                        dayNumber = (HijrahDate.of(newDate.getYear(), newDate.getMonth(), 1).lengthOfMonth() + 1) / 2;
                    } else {
                        dayNumber = (LocalDate.of(newDate.getYear(), newDate.getMonth(), 1).lengthOfMonth() + 1) / 2;
                    }
                    newDate.setDay(dayNumber);
                }
            }
        }

        return newDates;
    }

    /**
     * Checks whether two dates have the same year.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return true if the two dates have the same year, else false
     */
    private static boolean haveSameYear(YearMonthDayArray date1, YearMonthDayArray date2) {
        int year1 = date1.getYear();
        int year2 = date2.getYear();
        return (year1 == year2);
    }

    /**
     * Checks whether two dates have the same month.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return true if the two dates have the same month, else false
     */
    private static boolean haveSameMonth(YearMonthDayArray date1, YearMonthDayArray date2) {
        int month1 = date1.getMonth();
        int month2 = date2.getMonth();
        return (month1 == month2);
    }

    /**
     * Checks whether two dates have the same day.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return true if the two dates have the same day, else false
     */
    private static boolean haveSameDay(YearMonthDayArray date1, YearMonthDayArray date2) {
        int day1 = date1.getDay();
        int day2 = date2.getDay();
        return (day1 == day2);
    }

    /**
     * Checks whether one date has the inverted month and day of another date.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return true if one date has the inverted month and day of the other date, else false
     */
    private static boolean dayAndMonthAreInverted(YearMonthDayArray date1, YearMonthDayArray date2) {
        int month1 = date1.getMonth();
        int month2 = date2.getMonth();
        int day1 = date1.getDay();
        int day2 = date2.getDay();
        return (month1 == day2 && day1 == month2);
    }

    /**
     * Checks whether one date is one year off from another date.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return true if one date is one year off from another date, else false
     */
    private static boolean offByOneYear(YearMonthDayArray date1, YearMonthDayArray date2) {
        int year1 = date1.getYear();
        int year2 = date2.getYear();
        int difference = Math.abs(year1 - year2);
        return (difference == 1);
    }

    /**
     * Checks whether one date is one month off from another date.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return true if one date is one month off from another date, else false
     */
    private static boolean offByOneMonth(YearMonthDayArray date1, YearMonthDayArray date2) {
        int month1 = date1.getMonth();
        int month2 = date2.getMonth();
        int difference = Math.abs(month1 - month2);
        return (difference == 1);
    }

    /**
     * Checks whether one date is one day off from another date.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return true if one date is one day off from the other date, else false
     */
    private static boolean offByOneDay(YearMonthDayArray date1, YearMonthDayArray date2) {
        int day1 = date1.getDay();
        int day2 = date2.getDay();
        int difference = Math.abs(day1 - day2);
        return (difference == 1);
    }

    /**
     * Gets the number of digits that are different between two dates.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return the number of digits that are different between the two dates
     */
    private static int getNumberOfDigitsOff(YearMonthDayArray date1, YearMonthDayArray date2) {
        String simpleDateString1 = date1.toSimpleString();
        String simpleDateString2 = date2.toSimpleString();

        int[] differentDigits = new int[8];
        for (int i = 0; i < differentDigits.length; i++) {
            differentDigits[i] = simpleDateString1.charAt(i) == simpleDateString2.charAt(i) ? 0 : 1;
        }

        return IntStream.of(differentDigits).sum();
    }

    /**
     * Gets the number of transpositions that exist between two dates. For example,
     * getNumberOfTranspositions("20100117", "20100171") == 1.
     *
     * @param date1 the first date
     * @param date2 the second date
     *
     * @return the number of transpositions that exist between the two dates
     */
    private static int getNumberOfTranspositions(YearMonthDayArray date1, YearMonthDayArray date2) {
        String simpleDateString1 = date1.toSimpleString();
        String simpleDateString2 = date2.toSimpleString();

        int numberOfTranspositions = 0;

        for (int i = 0; i < simpleDateString1.length() - 1; i++) {
            StringBuilder tryTransposition = new StringBuilder(simpleDateString1);
            String transposed = "" + simpleDateString1.charAt(i + 1) + simpleDateString1.charAt(i);
            tryTransposition.replace(i, i + 2, transposed);
            numberOfTranspositions += (tryTransposition.toString().equals(simpleDateString2)) ? 1 : 0;
        }

        return numberOfTranspositions;
    }
}