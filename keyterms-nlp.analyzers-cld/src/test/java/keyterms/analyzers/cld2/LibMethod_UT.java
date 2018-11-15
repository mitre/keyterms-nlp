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

package keyterms.analyzers.cld2;

import java.util.List;

import org.junit.Test;

import keyterms.util.collect.Bags;

import static org.junit.Assert.assertEquals;

public class LibMethod_UT {

    public static final List<String> TEST_WORDS = Bags.staticList(
            "_ZN4CLD214DetectLanguageEPKcibPb",
            "_ZN4CLD221DetectLanguageSummaryEPKcibPNS_8LanguageEPiS4_Pb",
            "_ZN4CLD221DetectLanguageSummaryEPKcibS1_iNS_8LanguageEPS2_PiS4_Pb",
            "_ZN4CLD221DetectLanguageVersionEv",
            "_ZN4CLD223DetectLanguageCheckUTF8EPKcibPbPi",
            "_ZN4CLD223DetectLanguageSummaryV2EPKcibPKNS_8CLDHintsEbiNS_8LanguageEPS5_PiPdPSt6vectorINS_11ResultChunkESaISA_EES7_Pb",
            "_ZN4CLD224ExtDetectLanguageSummaryEPKcibPKNS_8CLDHintsEiPNS_8LanguageEPiPdPSt6vectorINS_11ResultChunkESaISA_EES7_Pb",
            "_ZN4CLD224ExtDetectLanguageSummaryEPKcibPNS_8LanguageEPiS4_Pb",
            "_ZN4CLD224ExtDetectLanguageSummaryEPKcibS1_iNS_8LanguageEPS2_PiPdS4_Pb",
            "_ZN4CLD224ExtDetectLanguageSummaryEPKcibS1_iNS_8LanguageEPS2_PiS4_Pb",
            "_ZN4CLD233ExtDetectLanguageSummaryCheckUTF8EPKcibPKNS_8CLDHintsEiPNS_8LanguageEPiPdPSt6vectorINS_11ResultChunkESaISA_EES7_PbS7_"
    );

    @Test
    public void shortMethodNoAugments() {
        LibMethod libMethod = new LibMethod("DetectLanguage");
        long count = TEST_WORDS.stream()
                .filter(libMethod)
                .count();
        assertEquals(TEST_WORDS.size(), count);
    }

    @Test
    public void shortMethodWithExclusions() {
        LibMethod libMethod = new LibMethod("DetectLanguage")
                .exclude("DetectLanguageSum")
                .require("Check");
        long count = TEST_WORDS.stream()
                .filter(libMethod)
                .count();
        assertEquals(1, count);
    }
}