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

package keyterms.util.process;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import keyterms.util.collect.Bags;
import keyterms.util.text.Strings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Processes_UT {

    @Test
    public void getCommandText() {
        List<String> command = Bags.arrayList(
                "cmd.exe", "/c", "dir", "/s", "/o:eng", "with spaces.config");
        assertEquals("cmd.exe /c dir /s /o:eng \"with spaces.config\"",
                Processes.getCommandText(command));
    }

    @Test
    public void script() {
        ProcessBuilder processBuilder = TestProcess.getProcessBuilder();
        ProcessResult result = Processes.run(processBuilder);
        assertNotNull(result);
        assertEquals(Charset.defaultCharset(), result.getProcessEncoding());
        assertTrue(result.hasErrorOutput());
        assertFalse(Strings.isBlank(result.getStandardOutput()));
        assertTrue(Strings.isBlank(result.getErrorOutput()));
        assertNotNull(result.getExitCode());
        assertEquals(0, (int)result.getExitCode());
        assertEquals(0, result.getErrors().size());
    }

    @Test
    public void scriptWithInput() {
        ProcessBuilder processBuilder = TestProcess.getProcessBuilder();
        ProcessResult result = Processes.run(processBuilder, StandardCharsets.UTF_8, "Hello World!");
        assertNotNull(result);
        assertEquals(StandardCharsets.UTF_8, result.getProcessEncoding());
        assertTrue(result.hasErrorOutput());
        assertFalse(Strings.isBlank(result.getStandardOutput()));
        assertFalse(Strings.isBlank(result.getErrorOutput()));
        assertEquals("Input: Hello World!", result.getErrorOutput().trim());
        assertNotNull(result.getExitCode());
        assertEquals(0, (int)result.getExitCode());
        assertEquals(0, result.getErrors().size());
    }
}