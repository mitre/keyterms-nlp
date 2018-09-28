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

package keyterms.util.system;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OS_UT {

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    @Test
    public void cpuLoad() {
        Double cpuLoad = null;
        int tries = 0;
        while ((cpuLoad == null) && (tries < 10)) {
            tries++;
            cpuLoad = OS.getCpuLoad();
        }
        assertNotNull("cpu load null", cpuLoad);
        assertTrue("cpu load ( " + cpuLoad + " ) negative :(", cpuLoad >= 0);
        if (tries > 1) {
            getLogger().warn("{} tries to get valid cpu load.", tries);
        }
    }

    @Test
    public void physicalMemory() {
        Long memory = OS.getPhysicalMemory();
        assertNotNull(memory);
        assertNotEquals(0, (long)memory);
    }

    @Test
    public void memoryLoad() {
        Double memoryLoad = null;
        int tries = 0;
        while ((memoryLoad == null) && (tries < 10)) {
            tries++;
            memoryLoad = OS.getMemoryLoad();
        }
        assertNotNull("null memory load", memoryLoad);
        assertTrue("memory load ( " + memoryLoad + " ) negative :(", memoryLoad >= 0);
        if (tries > 1) {
            getLogger().warn("{} tries to get valid memory load.", tries);
        }
    }

    @Test
    public void javaMemoryLoad() {
        assertTrue(OS.getJavaMemoryLoad() >= 0);
    }
}