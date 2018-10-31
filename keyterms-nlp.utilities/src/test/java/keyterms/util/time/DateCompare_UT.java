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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateCompare_UT {

    private static final boolean PRINT_RESULTS = false;

    private static final String QUERY = "1971-01-31";
    private static final List<String> retrievals_gregorian = new ArrayList<>();

    @Before
    public void setup() {
        retrievals_gregorian.add("1971-01-31");
        retrievals_gregorian.add("1971-01-30");
        retrievals_gregorian.add("1970-01-31");
        retrievals_gregorian.add("1971-01");
        retrievals_gregorian.add("1971-01-13");
        retrievals_gregorian.add("1969-01-31");
        retrievals_gregorian.add("1971");
        retrievals_gregorian.add("1971-02-01");
        retrievals_gregorian.add("1971-10-31");
        retrievals_gregorian.add("1971-10");
        retrievals_gregorian.add("1917-01-31");
        retrievals_gregorian.add("1917-01-30");
        retrievals_gregorian.add("1917-01-13");
        retrievals_gregorian.add("1973-01-31");
        retrievals_gregorian.add("1917-10-31");
        retrievals_gregorian.add("1971-10-13");
        retrievals_gregorian.add("1917-10-13");
        retrievals_gregorian.add("1917-01");
        retrievals_gregorian.add("1917-10");
        retrievals_gregorian.add("1971-11-31");
        retrievals_gregorian.add("1969-12-31");
        retrievals_gregorian.add("1917-02-01");
        retrievals_gregorian.add("1971-11-21");
        retrievals_gregorian.add("1942-06-42");
    }

    private void printResults(List<String> sortedRetrievals, List<Double> sortedSims) {
        System.out.println();
        for (int i = 0; i < sortedSims.size(); i++) {
            System.out.println(QUERY + " to " + sortedRetrievals.get(i) + " = " + sortedSims.get(i));
        }

        System.out.println("\ndates only: ");
        for (int i = 0; i < sortedSims.size(); i++) {
            System.out.println(sortedRetrievals.get(i));
        }
    }

    @Test
    public void compositeCompareTest_gregorian() {
        // compare d1 to each other date
        List<String> sortedRetrievals = new ArrayList<>();
        List<Double> sortedSims = new ArrayList<>();
        for (String retrieved : retrievals_gregorian) {
            double qr = DateCompare.compare(QUERY, retrieved);

            // get index
            int i = 0;
            if (sortedSims.size() != 0) {
                while (i < sortedSims.size() && qr < sortedSims.get(i)) {
                    i++;
                }
            }

            sortedRetrievals.add(i, retrieved);
            sortedSims.add(i, qr);

        }

        if (PRINT_RESULTS) {
            printResults(sortedRetrievals, sortedSims);
        }

        assertTrue(true);
    }

    @Test
    public void cosSimCompareTest_gregorian() {
        // compare d1 to each other date
        List<String> sortedRetrievals = new ArrayList<>();
        List<Double> sortedSims = new ArrayList<>();
        for (String retrieved : retrievals_gregorian) {
            double qr = DateCompare.cosSimCompare(QUERY, retrieved);

            // get index
            int i = 0;
            if (sortedSims.size() != 0) {
                while (i < sortedSims.size() && qr < sortedSims.get(i)) {
                    i++;
                }
            }

            sortedRetrievals.add(i, retrieved);
            sortedSims.add(i, qr);

        }

        if (PRINT_RESULTS) {
            printResults(sortedRetrievals, sortedSims);
        }

        assertTrue(true);
    }

    @Ignore
    @Test
    public void cosSimCompareTest_hijrah() {
        // create some dates, some of which are not fully specified
        //@todo get human judgments to sort d2-d9 on how similar they are to d1
        //@todo add more dates
        String d1 = "1438-04-03";
        String d2 = "1438-04-05";
        String d3 = "1438-04-19";
        String d4 = "04-1438";
        String d5 = "04-1437";
        String d6 = "Rajab 6";
        String d7 = "Jumada Al-Awwal 19";
        String d8 = "1438";
        String d9 = "1437";

        // compare d1 to each other date
        double d1d1 = DateCompare.cosSimCompare(d1, d1);
        double d1d2 = DateCompare.cosSimCompare(d1, d2);
        double d1d3 = DateCompare.cosSimCompare(d1, d3);
        double d1d4 = DateCompare.cosSimCompare(d1, d4);
        double d1d5 = DateCompare.cosSimCompare(d1, d5);
        double d1d6 = DateCompare.cosSimCompare(d1, d6);
        double d1d7 = DateCompare.cosSimCompare(d1, d7);
        double d1d8 = DateCompare.cosSimCompare(d1, d8);
        double d1d9 = DateCompare.cosSimCompare(d1, d9);

        // print the results
        if (PRINT_RESULTS) {
            System.out.println(d1 + " to " + d1 + " : " + d1d1);
            System.out.println(d1 + " to " + d2 + " : " + d1d2);
            System.out.println(d1 + " to " + d3 + " : " + d1d3);
            System.out.println(d1 + " to " + d4 + " : " + d1d4);
            System.out.println(d1 + " to " + d5 + " : " + d1d5);
            System.out.println(d1 + " to " + d6 + " : " + d1d6);
            System.out.println(d1 + " to " + d7 + " : " + d1d7);
            System.out.println(d1 + " to " + d8 + " : " + d1d8);
            System.out.println(d1 + " to " + d9 + " : " + d1d9);
        }

        // run the assertion tests (this is the important part)
        assertTrue(d1d1 > d1d2);
        assertTrue(d1d2 > d1d3);
        assertTrue(d1d2 > d1d4);
        assertTrue(d1d4 > d1d5);
        assertTrue(d1d6 > d1d7);
        assertTrue(d1d8 > d1d9);

        // finally, ensure that compare is commutative
        double d2d1 = DateCompare.cosSimCompare(d2, d1);
        double d3d1 = DateCompare.cosSimCompare(d3, d1);
        double d4d1 = DateCompare.cosSimCompare(d4, d1);
        double d5d1 = DateCompare.cosSimCompare(d5, d1);
        double d6d1 = DateCompare.cosSimCompare(d6, d1);
        double d7d1 = DateCompare.cosSimCompare(d7, d1);
        double d8d1 = DateCompare.cosSimCompare(d8, d1);
        double d9d1 = DateCompare.cosSimCompare(d9, d1);

        assertEquals(d1d2, d2d1, 0.0);
        assertEquals(d1d3, d3d1, 0.0);
        assertEquals(d1d4, d4d1, 0.0);
        assertEquals(d1d5, d5d1, 0.0);
        assertEquals(d1d6, d6d1, 0.0);
        assertEquals(d1d7, d7d1, 0.0);
        assertEquals(d1d8, d8d1, 0.0);
        assertEquals(d1d9, d9d1, 0.0);
    }

    @Test
    public void digitCompareTest_gregorian() {
        // A)  1.00; exact match
        assertEquals(1.00, DateCompare.digitCompare("2017-01-07", "2017-01-07"), 0.0);
        assertEquals(1.00, DateCompare.digitCompare("2017-01-07", "2017/01/07"), 0.0);
        assertEquals(1.00, DateCompare.digitCompare("2017-01-07", "2017.01.07"), 0.0);
        assertEquals(1.00, DateCompare.digitCompare("2017-01-07", "2017 01 07"), 0.0);
        assertEquals(1.00, DateCompare.digitCompare("2017-01-07", "07-01-2017"), 0.0);
        assertEquals(1.00, DateCompare.digitCompare("2017-01-07", "07/01/2017"), 0.0);
        assertEquals(1.00, DateCompare.digitCompare("2017-01-07", "07.01.2017"), 0.0);
        assertEquals(1.00, DateCompare.digitCompare("2017-01-07", "07 01 2017"), 0.0);

        // B)  0.90; same year, inverted day and month
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "2017-07-01"), 0.0);
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "2017/07/01"), 0.0);
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "2017.07.01"), 0.0);
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "2017 07 01"), 0.0);
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "01-07-2017"), 0.0);
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "01/07/2017"), 0.0);
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "01.07.2017"), 0.0);
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "01 07 2017"), 0.0);

        // C)  0.90; same year, same month, one day off
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "2017-01-06"), 0.0);
        assertEquals(0.90, DateCompare.digitCompare("2017-01-07", "2017-01-08"), 0.0);

        // D)  0.40; same year, one month off, one day off
        assertEquals(0.40, DateCompare.digitCompare("2017-01-07", "2017-02-08"), 0.0);
        assertEquals(0.40, DateCompare.digitCompare("2017-01-07", "2017-02-06"), 0.0);
        assertEquals(0.40, DateCompare.digitCompare("2017-02-07", "2017-01-06"), 0.0);
        assertEquals(0.40, DateCompare.digitCompare("2017-02-07", "2017-01-08"), 0.0);

        // E)  0.20; one year off, inverted day and month OR one day off
        assertEquals(0.20, DateCompare.digitCompare("2017-01-07", "2016-07-01"), 0.0); // inverted day/moth
        assertEquals(0.20, DateCompare.digitCompare("2017-01-07", "2018-07-01"), 0.0); // inverted day/moth
        assertEquals(0.20, DateCompare.digitCompare("2017-01-07", "2016-01-06"), 0.0); // one day off
        assertEquals(0.20, DateCompare.digitCompare("2017-01-07", "2016-01-08"), 0.0); // one day off
        assertEquals(0.20, DateCompare.digitCompare("2017-01-07", "2018-01-06"), 0.0); // one day off
        assertEquals(0.20, DateCompare.digitCompare("2017-01-07", "2018-01-08"), 0.0); // one day off

        // F)  0.10; single digit wrong OR one pair of digits transposed
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "1017-01-07"), 0.0); // off by one digit
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2317-01-07"), 0.0); // off by one digit
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2027-01-07"), 0.0); // off by one digit
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2012-01-07"), 0.0); // off by one digit
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2017-11-07"), 0.0); // off by one digit
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2017-08-07"), 0.0); // off by one digit
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2017-01-17"), 0.0); // off by one digit
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2017-01-01"), 0.0); // off by one digit
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "0217-01-07"), 0.0); // single transposition
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2107-01-07"), 0.0); // single transposition
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2071-01-07"), 0.0); // single transposition
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2010-71-07"), 0.0); // single transposition
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2017-10-07"), 0.0); // single transposition
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2017-00-17"), 0.0); // single transposition
        assertEquals(0.10, DateCompare.digitCompare("2017-01-07", "2017-01-70"), 0.0); // single transposition

        // G)  0.00; blank for one or both of the dates
        assertEquals(0.00, DateCompare.digitCompare("2017-01-07", ""), 0.0);
        assertEquals(0.00, DateCompare.digitCompare("", "2017-01-07"), 0.0);
        assertEquals(0.00, DateCompare.digitCompare("", ""), 0.0);

        // H) -0.25; same year, different month, different day
        assertEquals(DateCompare.digitCompare("2017-01-07", "2017-02-05"), -0.25, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2017-03-09"), -0.25, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2017-12-31"), -0.25, 0.0);

        // I) -0.50; one year off, different month, different day
        assertEquals(DateCompare.digitCompare("2017-01-07", "2016-02-05"), -0.50, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2016-03-09"), -0.50, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2016-12-31"), -0.50, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2018-02-05"), -0.50, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2018-03-09"), -0.50, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2018-12-31"), -0.50, 0.0);

        // J) -1.00; totally different
        assertEquals(DateCompare.digitCompare("2017-01-07", "2015-02-05"), -1.00, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2014-03-09"), -1.00, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2013-12-31"), -1.00, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2019-02-05"), -1.00, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2020-03-09"), -1.00, 0.0);
        assertEquals(DateCompare.digitCompare("2017-01-07", "2021-12-31"), -1.00, 0.0);
    }

}