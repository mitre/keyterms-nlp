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

package keyterms.nlp.transliterate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Scheme_UT {

    @Test
    public void sortedUniqueQualifiers() {
        Scheme scheme = new Scheme("A_H_E_L_O_L");
        assertEquals(4, scheme.getQualifiers().size());
        assertEquals("A_E_H_L_O", scheme.toString());
    }

    @Test
    public void sortOrder() {
        List<Scheme> schemes = Arrays.asList(
                new Scheme("E"),
                new Scheme("E_G_F"),
                new Scheme("E_F"),
                new Scheme("A"),
                new Scheme("A_C"),
                new Scheme("A_C_B"),
                new Scheme(""),
                new Scheme("Zero"),
                new Scheme("Zero_One_Two"),
                new Scheme("Zero_One")
        );
        List<Scheme> expected = Arrays.asList(
                schemes.get(6), // ""
                schemes.get(3), //"A"
                schemes.get(5), //"A_C_B"
                schemes.get(4), //"A_C"
                schemes.get(0), //"E"
                schemes.get(2), //"E_F"
                schemes.get(1), //"E_G_F"
                schemes.get(7), //"Zero"
                schemes.get(9), //"Zero_One"
                schemes.get(8) //"Zero_One_Two"
        );
        List<Scheme> sorted = new ArrayList<>(schemes);
        Collections.sort(sorted);
        assertEquals(expected, sorted);
    }

    @Test
    public void filter() {
        List<Scheme> schemes = Arrays.asList(
                new Scheme(""),
                new Scheme("Zero"),
                new Scheme("Zero_One"),
                new Scheme("Zero_One_Two")
        );
        List<Scheme> filtered = schemes.stream()
                .filter(scheme -> scheme.test("Zero"))
                .collect(Collectors.toList());
        assertEquals(3, filtered.size());
        filtered = schemes.stream()
                .filter(scheme -> scheme.test("Zero_One"))
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(schemes.get(2), schemes.get(3)), filtered);
        filtered = schemes.stream()
                .filter(scheme -> scheme.test("Zero_Two"))
                .collect(Collectors.toList());
        assertEquals(Collections.singletonList(schemes.get(3)), filtered);
    }
}