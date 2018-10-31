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

import java.util.List;

import org.junit.Test;

import keyterms.util.ComparisonsTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class YearMonthDayArray_UT {

    @Test
    public void constructorTest() {
        YearMonthDayArray d1 = new YearMonthDayArray();
        assertEquals(0, d1.getYear());
        assertEquals(0, d1.getMonth());
        assertEquals(0, d1.getDay());

        d1 = new YearMonthDayArray(2017, 1, 13);
        assertEquals(2017, d1.getYear());
        assertEquals(1, d1.getMonth());
        assertEquals(13, d1.getDay());

        YearMonthDayArray d2 = new YearMonthDayArray("2017-01-13");
        assertTrue(d2.isSameDateAs(d1));

        d1 = new YearMonthDayArray(1438, 4, 3);
        assertEquals(1438, d1.getYear());
        assertEquals(4, d1.getMonth());
        assertEquals(3, d1.getDay());

        d2 = new YearMonthDayArray("1438-04-03");
        assertTrue(d2.isSameDateAs(d1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidYearTest() {
        new YearMonthDayArray(10000, 0, 0);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMonthTest() {
        new YearMonthDayArray(0, 100, 0);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDayTest() {
        new YearMonthDayArray(0, 0, 100);
        fail();
    }

    @Test
    public void toArrayTest() {
        List<Integer> dArray = new YearMonthDayArray(0, 0, 29).toArray();
        assertEquals(0, (int)dArray.get(0));
        assertEquals(0, (int)dArray.get(1));
        assertEquals(29, (int)dArray.get(2));

        dArray = new YearMonthDayArray(0, 2, 29).toArray();
        assertEquals(0, (int)dArray.get(0));
        assertEquals(2, (int)dArray.get(1));
        assertEquals(29, (int)dArray.get(2));

        dArray = new YearMonthDayArray(2000, 2, 29).toArray();
        assertEquals(2000, (int)dArray.get(0));
        assertEquals(2, (int)dArray.get(1));
        assertEquals(29, (int)dArray.get(2));

        dArray = new YearMonthDayArray("00000029").toArray();
        assertEquals(0, (int)dArray.get(0));
        assertEquals(0, (int)dArray.get(1));
        assertEquals(29, (int)dArray.get(2));

        dArray = new YearMonthDayArray("00000229").toArray();
        assertEquals(0, (int)dArray.get(0));
        assertEquals(2, (int)dArray.get(1));
        assertEquals(29, (int)dArray.get(2));

        dArray = new YearMonthDayArray("20000229").toArray();
        assertEquals(2000, (int)dArray.get(0));
        assertEquals(2, (int)dArray.get(1));
        assertEquals(29, (int)dArray.get(2));
    }

    @Test
    public void toSimpleStringTest_gregorian() {
        // full date
        String fullDate = "20170113";

        YearMonthDayArray d = new YearMonthDayArray("2017-01-13");
        assertEquals(d.toSimpleString(), fullDate);

        d = new YearMonthDayArray("January 13, 2017");
        assertEquals(d.toSimpleString(), fullDate);

        d = new YearMonthDayArray("Jan 13, 2017");
        assertEquals(d.toSimpleString(), fullDate);

        d = new YearMonthDayArray("13 January, 2017");
        assertEquals(d.toSimpleString(), fullDate);

        // full date with various day/month values
        fullDate = "20170107";
        d = new YearMonthDayArray("2017-01-07");
        assertEquals(d.toSimpleString(), fullDate);

        fullDate = "20171013";
        d = new YearMonthDayArray("2017-10-13");
        assertEquals(d.toSimpleString(), fullDate);

        fullDate = "20171007";
        d = new YearMonthDayArray("2017-10-07");
        assertEquals(d.toSimpleString(), fullDate);

        // day-month
        String monthDayDate = "00000113";

        d = new YearMonthDayArray("01-13");
        assertEquals(d.toSimpleString(), monthDayDate);

        d = new YearMonthDayArray("January 13");
        assertEquals(d.toSimpleString(), monthDayDate);

        d = new YearMonthDayArray("Jan 13");
        assertEquals(d.toSimpleString(), monthDayDate);

        d = new YearMonthDayArray("13 January");
        assertEquals(d.toSimpleString(), monthDayDate);

        // year-month
        String yearMonthDate = "20170100";

        d = new YearMonthDayArray("01-2017");
        assertEquals(d.toSimpleString(), yearMonthDate);

        d = new YearMonthDayArray("January 2017");
        assertEquals(d.toSimpleString(), yearMonthDate);

        d = new YearMonthDayArray("Jan 2017");
        assertEquals(d.toSimpleString(), yearMonthDate);

        // year only
        String yearDate = "20170000";

        d = new YearMonthDayArray("2017");
        assertEquals(d.toSimpleString(), yearDate);
    }

    @Test
    public void toSimpleStringTest_hijrah() {
        // full date
        String fullDate = "14380413";

        YearMonthDayArray d = new YearMonthDayArray("1438-04-13");
        assertEquals(d.toSimpleString(), fullDate);

        d = new YearMonthDayArray("Rabi' al-thani 13, 1438");
        assertEquals(d.toSimpleString(), fullDate);

        d = new YearMonthDayArray("13 Rabi' al-thani, 1438");
        assertEquals(d.toSimpleString(), fullDate);

        // full date with various day/month values
        fullDate = "14380107";
        d = new YearMonthDayArray("1438-01-07");
        assertEquals(d.toSimpleString(), fullDate);

        fullDate = "14381013";
        d = new YearMonthDayArray("1438-10-13");
        assertEquals(d.toSimpleString(), fullDate);

        fullDate = "14381007";
        d = new YearMonthDayArray("1438-10-07");
        assertEquals(d.toSimpleString(), fullDate);

        // day-month
        String monthDayDate = "00000113";

        d = new YearMonthDayArray("01-13");
        assertEquals(d.toSimpleString(), monthDayDate);

        d = new YearMonthDayArray("Muharram 13");
        assertEquals(d.toSimpleString(), monthDayDate);

        d = new YearMonthDayArray("13 Muharram");
        assertEquals(d.toSimpleString(), monthDayDate);

        // year-month
        String yearMonthDate = "14380100";

        d = new YearMonthDayArray("01-1438");
        assertEquals(d.toSimpleString(), yearMonthDate);

        d = new YearMonthDayArray("Muharram 1438");
        assertEquals(d.toSimpleString(), yearMonthDate);

        // year only
        String yearDate = "14380000";

        d = new YearMonthDayArray("1438");
        assertEquals(d.toSimpleString(), yearDate);
    }

    @Test
    public void toSimpleStringTest_weirdDates() {
        YearMonthDayArray d = new YearMonthDayArray(0, 0, 0);
        assertEquals("00000000", d.toSimpleString());

        d = new YearMonthDayArray(1, 0, 0);
        assertEquals("00010000", d.toSimpleString());

        d = new YearMonthDayArray(12, 0, 0);
        assertEquals("00120000", d.toSimpleString());

        d = new YearMonthDayArray(123, 0, 0);
        assertEquals("01230000", d.toSimpleString());

        d = new YearMonthDayArray(0, 1, 0);
        assertEquals("00000100", d.toSimpleString());

        d = new YearMonthDayArray(0, 0, 1);
        assertEquals("00000001", d.toSimpleString());
    }

    @Test
    public void comparisonsTableTest_singlePair() {
        YearMonthDayArray smh1 = new YearMonthDayArray(0, 12, 25);
        YearMonthDayArray smh2 = new YearMonthDayArray("12/25");

        ComparisonsTable<YearMonthDayArray> ct = new ComparisonsTable<>();
        ct.addEntry(smh1, smh2, 0.99);
        assertEquals(0, Double.compare(ct.lookupScore(smh1, smh2), 0.99));
        assertEquals(0, Double.compare(ct.lookupScore(smh2, smh1), 0.99));

        ct.addEntry(smh2, smh1, 0.99);
        assertEquals(0, Double.compare(ct.lookupScore(smh1, smh2), 0.99));
        assertEquals(0, Double.compare(ct.lookupScore(smh2, smh1), 0.99));
    }

    @Test
    public void comparisonsTableTest_multiplePairs() {
        YearMonthDayArray smh1 = new YearMonthDayArray("2/29/2016");
        YearMonthDayArray smh2 = new YearMonthDayArray("3/1/2017");
        YearMonthDayArray smh3 = new YearMonthDayArray("Jumada Al-Awwal 21, 1437");

        ComparisonsTable<YearMonthDayArray> ct = new ComparisonsTable<>();
        ct.addEntry(smh1, smh1, 1.0);
        ct.addEntry(smh1, smh2, 0.8);
        ct.addEntry(smh1, smh3, 0.4);
        ct.addEntry(smh2, smh3, 0.5);

        assertEquals(0, Double.compare(ct.lookupScore(smh1, smh1), 1.0));
        assertEquals(0, Double.compare(ct.lookupScore(smh1, smh2), 0.8));
        assertEquals(0, Double.compare(ct.lookupScore(smh2, smh1), 0.8));
        assertEquals(0, Double.compare(ct.lookupScore(smh1, smh3), 0.4));
        assertEquals(0, Double.compare(ct.lookupScore(smh3, smh1), 0.4));
        assertEquals(0, Double.compare(ct.lookupScore(smh2, smh3), 0.5));
        assertEquals(0, Double.compare(ct.lookupScore(smh3, smh2), 0.5));
        assertNull(ct.lookupScore(smh2, smh2));
        assertNull(ct.lookupScore(smh3, smh3));

        ct = new ComparisonsTable<>();
        ct.addEntry(smh2, smh1, 0.8);
        ct.addEntry(smh3, smh1, 0.4);
        ct.addEntry(smh3, smh2, 0.5);

        assertEquals(0, Double.compare(ct.lookupScore(smh1, smh2), 0.8));
        assertEquals(0, Double.compare(ct.lookupScore(smh2, smh1), 0.8));
        assertEquals(0, Double.compare(ct.lookupScore(smh1, smh3), 0.4));
        assertEquals(0, Double.compare(ct.lookupScore(smh3, smh1), 0.4));
        assertEquals(0, Double.compare(ct.lookupScore(smh2, smh3), 0.5));
        assertEquals(0, Double.compare(ct.lookupScore(smh3, smh2), 0.5));
        assertNull(ct.lookupScore(smh2, smh2));
        assertNull(ct.lookupScore(smh3, smh3));
    }
}