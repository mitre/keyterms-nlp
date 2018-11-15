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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.sun.jna.NativeLibrary;

import keyterms.util.io.IO;
import keyterms.util.process.Processes;
import keyterms.util.system.OS;
import keyterms.util.text.Strings;
import keyterms.util.text.splitter.LineSplitter;

/**
 * A utility to interact with the JNA library to get access to underlying native libraries.
 */
final class NativeLibs {
    /**
     * The interface for functions that handle callbacks from the native library interrogation, which are used to map
     * native library names to local native functions.
     */
    @FunctionalInterface
    interface Handler {
        /**
         * Test the method name from the native library to see if it's useful to the handler.
         *
         * @param library The native library.
         * @param methodName The method name from the native library.
         */
        void test(NativeLibrary library, String methodName);
    }

    /**
     * The line-splitter for the output of the library interrogation utility.
     */
    private static final LineSplitter LINE_SPLITTER = new LineSplitter();

    /**
     * Load the specified library.
     *
     * @param path The path where the library is expected to be found.
     * @param libName The base library name (no extension).
     * @param callBack The callback function that will handle matching native library names to internal
     * methods.
     */
    static void loadLibrary(String path, String libName, Handler callBack)
            throws IOException {
        boolean isWindows = OS.isWindows();
        Path libDir = IO.normalize(path);
        if (!IO.isValidDirectory(libDir)) {
            throw new IOException("Library path not found: " + path);
        }
        String fileName = libName + ((isWindows) ? ".dll" : ".so");
        Path libFile = libDir.resolve(fileName);
        if (!IO.isValidFile(libFile)) {
            throw new IOException("Library file not found: " + libFile);
        }
        NativeLibrary nativeLibrary = NativeLibrary.getInstance(libFile.toString());
        Path loadedFile = IO.normalize(nativeLibrary.getFile());
        if (!loadedFile.getParent().equals(libDir)) {
            LoggerFactory.getLogger(NativeLibs.class)
                    .warn("Library not loaded from expected path: {} loaded from {}",
                            libFile, loadedFile);
        }
        if (OS.isWindows()) {
            throw new IOException("Windows not currently supported.");
        } else {
            ProcessBuilder nmProcess = new ProcessBuilder("nm", loadedFile.toString());
            String nm = Processes.run(nmProcess).getStandardOutput();
            List<String> lines = LINE_SPLITTER.split(nm);
            lines.forEach(line -> {
                if (!Strings.isBlank(line)) {
                    String[] cols = line.split("\\s+");
                    if (cols.length >= 3) {
                        String methodName = cols[2];
                        callBack.test(nativeLibrary, methodName);
                    }
                }
            });
        }
    }

    /**
     * Constructor.
     */
    private NativeLibs() {
        super();
    }
}