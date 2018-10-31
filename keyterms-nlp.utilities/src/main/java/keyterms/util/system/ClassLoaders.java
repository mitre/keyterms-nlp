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

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Methods for interacting with class loaders.
 */
public class ClassLoaders {
    /**
     * Attempt to locate and load the specified class.
     *
     * @param className The fully qualified class name.
     *
     * @return The loaded class.
     *
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> loadClass(String className)
            throws ClassNotFoundException {
        return loadClass(className, true);
    }

    /**
     * Attempt to locate and load the specified class.
     *
     * @param className The fully qualified class name.
     * @param initialize A flag indicating whether the loaded class should be initialized.
     *
     * @return The loaded class.
     *
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> loadClass(String className, boolean initialize)
            throws ClassNotFoundException {
        Class<?> theClass = tryLoadClass(className, initialize, ClassLoader.getSystemClassLoader());
        theClass = (theClass != null) ? theClass : tryLoadClass(className, initialize,
                Thread.currentThread().getContextClassLoader());
        if (theClass == null) {
            throw new ClassNotFoundException(className);
        }
        return theClass;
    }

    /**
     * Attempt to locate and load the specified class.
     *
     * @param className The fully qualified class name.
     * @param initialize A flag indicating whether the loaded class should be initialized.
     * @param classLoader The class loader to use.
     *
     * @return The loaded class.
     */
    private static Class<?> tryLoadClass(String className, boolean initialize, ClassLoader classLoader) {
        Class<?> theClass;
        try {
            theClass = Class.forName(className, initialize, classLoader);
        } catch (Throwable error) {
            if (error instanceof Error) {
                throw (Error)error;
            }
            theClass = null;
        }
        return theClass;
    }

    /**
     * Get the base path from which the specified class was loaded.
     *
     * <p> This method will return either the path to the base directory or the archive file from which the class was
     * loaded.</p>
     *
     * @param theClass The class of interest.
     *
     * @return The base path from which the specified class was loaded.
     */
    public static Path getBasePath(Class<?> theClass) {
        Path basePath = null;
        String classResource = theClass.getName();
        classResource = classResource.replaceAll("\\.", "/") + ".class";
        URL resourceUrl = theClass.getClassLoader().getResource(classResource);
        if (resourceUrl != null) {
            String resourcePath = resourceUrl.toString();
            if (resourcePath.startsWith("jar:file:/")) {
                int pathStart = (System.getProperty("os.name").toLowerCase().contains("win")) ? 10 : 9;
                resourcePath = resourcePath.substring(pathStart);
                resourcePath = resourcePath.substring(0, resourcePath.indexOf('!'));
                basePath = Paths.get(resourcePath).normalize().toAbsolutePath();
            } else {
                int pathStart = 5;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    pathStart++;
                }
                resourcePath = resourcePath.substring(pathStart);
                resourcePath = resourcePath.substring(0, resourcePath.length() - classResource.length());
                basePath = Paths.get(resourcePath).normalize().toAbsolutePath();
            }
        }
        return basePath;
    }

    /**
     * Constructor.
     */
    private ClassLoaders() {
        super();
    }
}