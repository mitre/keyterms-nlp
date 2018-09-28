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

package keyterms.util.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

/**
 * A file visitor which locates the first instance of a specified path.
 */
public class PathFinder
        implements FileVisitor<Path> {
    /**
     * The predicate used to identify the file of interest.
     */
    private final Predicate<Path> matcher;

    /**
     * The predicate used to determine if sections of the file system should be skipped.
     */
    private final Predicate<Path> skipTest;

    /**
     * The located path.
     */
    private Path path;

    /**
     * Constructor.
     *
     * @param matcher The predicate used to identify the file of interest.
     */
    public PathFinder(Predicate<Path> matcher) {
        this(matcher, null);
    }

    /**
     * Constructor.
     *
     * @param matcher The predicate used to identify the file of interest.
     * @param skipTest The predicate used to determine if sections of the file system should be skipped.
     */
    public PathFinder(Predicate<Path> matcher, Predicate<Path> skipTest) {
        super();
        this.matcher = matcher;
        this.skipTest = skipTest;
    }

    /**
     * Get the located path.
     *
     * @return The located path.
     */
    public Path getPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) {
        FileVisitResult result = FileVisitResult.CONTINUE;
        if ((matcher == null) || (matcher.test(directory))) {
            path = directory;
            result = FileVisitResult.TERMINATE;
        } else {
            if ((skipTest != null) && (skipTest.test(directory))) {
                result = FileVisitResult.SKIP_SUBTREE;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        FileVisitResult result = FileVisitResult.CONTINUE;
        if ((matcher == null) || (matcher.test(file))) {
            path = file;
            result = FileVisitResult.TERMINATE;
        } else {
            if ((skipTest != null) && (skipTest.test(file))) {
                result = FileVisitResult.SKIP_SIBLINGS;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException error) {
        return FileVisitResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileVisitResult postVisitDirectory(Path directory, IOException error) {
        return FileVisitResult.CONTINUE;
    }
}