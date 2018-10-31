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

package keyterms.testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Methods for working with temporary files and directories used during testing.
 */
public final class TestFiles {
    /**
     * A reference to the system working directory.
     */
    public static final Path CWD = Paths.get(".").toAbsolutePath().normalize();

    /**
     * The project home directory.
     */
    public static final Path PROJECT_HOME = locateProjectHome();

    /**
     * The base temporary directory for all temporary files and directories created for testing.
     */
    public static final Path JAVA_TEMP = Paths.get(System.getProperty("java.io.tmpdir")).normalize().toAbsolutePath();

    /**
     * The temporary directory for all temporary files used in the current test run.
     */
    public static final Path TEMP;

    /**
     * Temporary files that have been created.
     */
    private static final List<Path> TEMP_FILES = new ArrayList<>();

    /**
     * Temporary files that have been created.
     */
    private static final List<Path> TEMP_DIRS = new ArrayList<>();

    /* Initialize the base temporary directory.
     */
    static {
        try {
            // Clean up older temp directories.
            Files.list(JAVA_TEMP)
                    .filter(Files::isDirectory)
                    .filter((p) -> p.getName(p.getNameCount() - 1).toString().toLowerCase().startsWith("junit_"))
                    .forEach(TestFiles::deleteDirectory);
            TEMP = Files.createTempDirectory(JAVA_TEMP, "junit_");
            TEMP.toFile().deleteOnExit();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    Thread.sleep(1_000);
                } catch (Exception error) {
                    // Ignore
                }
                getLogger().info("Removing testing directory: {}", TEMP);
                for (Path f : TEMP_FILES) {
                    try {
                        Files.deleteIfExists(f);
                    } catch (Exception error) {
                        getLogger().warn("Could not delete testing file: {}", f, error);
                    }
                }
                for (int d = TEMP_DIRS.size() - 1; d >= 0; d--) {
                    deleteDirectory(TEMP_DIRS.get(d));
                }
                deleteDirectory(TEMP);
            }));
        } catch (Exception error) {
            throw new IllegalStateException("Could not create temporary testing directory.");
        }
    }

    /**
     * Aggressively attempt to delete the specified directory.
     *
     * @param directory The directory.
     */
    private static void deleteDirectory(Path directory) {
        if (Files.exists(directory)) {
            RecursiveDelete recursiveDelete = new RecursiveDelete();
            try {
                Files.walkFileTree(directory, recursiveDelete);
            } catch (Exception error) {
                getLogger().warn("Could not delete testing directory: {}", directory, error);
            }
            try {
                Files.deleteIfExists(directory);
            } catch (Exception error) {
                if (recursiveDelete.failures == 0) {
                    getLogger().warn("Could not delete testing directory: {}", directory, error);
                }
            }
        }
    }

    /**
     * Get the logging topic for this class.
     *
     * @return The logging topic for this class.
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(TestFiles.class);
    }

    /**
     * Locate the project home directory.
     *
     * <p> This is done by searching for the file ".project_home" using
     * {@link #locateFile(Path, boolean, String) locateFile}. </p>
     * <p> If the project home directory cannot be located, the current working directory is used. </p>
     *
     * @return The project home directory.
     */
    private static Path locateProjectHome() {
        Path projectHome = null;
        try {
            Path projectHomeFile = locateFile(CWD, false, File.separator + ".project_home");
            if (projectHomeFile != null) {
                projectHome = projectHomeFile.getParent();
            }
        } catch (Exception error) {
            getLogger().warn("Error locating project home directory.", error);
        }
        if (projectHome == null) {
            getLogger().warn("Could not locate project home directory.");
            projectHome = CWD;
        }
        return projectHome;
    }

    /**
     * Locate the specified file.
     *
     * <p> This method will return the first file found with the specified name. </p>
     * <p> This method does recursive directory searches starting with the current working directory and moving toward
     * the root of the drive until it either finds the desired file or gets to the root directory of the drive. </p>
     * <p> If the current working directory is a root directory, this method will never find the specified file. </p>
     *
     * @param name The name of the file.
     *
     * @return The path to the specified file.
     *
     * @throws IOException for input/output errors
     */
    public static Path locateFile(String name)
            throws IOException {
        return locateFile(PROJECT_HOME, true, name);
    }

    /**
     * Locate the specified file.
     *
     * <p> This method will return the first file found with the specified name. </p>
     * <p> This method does recursive directory searches starting with the current working directory and moving toward
     * the root of the drive until it either finds the desired file or gets to the root directory of the drive. </p>
     * <p> If the current working directory is a root directory, this method will never find the specified file. </p>
     *
     * @param start The start directory.
     * @param recurse A flag indicating whether the file should be searched for from the current working directory.
     * @param name The name of the file.
     *
     * @return The path to the specified file.
     *
     * @throws IOException for input/output errors
     */
    public static Path locateFile(Path start, boolean recurse, String name)
            throws IOException {
        Path file = CWD;
        if (start != null) {
            file = Files.isDirectory(start) ? start : start.getParent();
        }
        Path lastPath = null;
        while ((file != null) &&
                (Files.isDirectory(file)) &&
                (Stream.of(file.getFileSystem().getRootDirectories()).noneMatch(file::equals))) {
            Path found;
            if (recurse) {
                RecursiveFind locator = new RecursiveFind(lastPath, name);
                Files.walkFileTree(file, locator);
                found = locator.getFile();
            } else {
                found = Files.list(file)
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(name))
                        .findFirst().orElse(null);
            }
            lastPath = file;
            file = (found == null) ? file.getParent() : found;
            if (lastPath.equals(PROJECT_HOME)) {
                if (found == null) {
                    file = null;
                }
                break;
            }
        }
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return file.toAbsolutePath().normalize();
    }

    /**
     * Get the path to a file co-located with the test class whose name differs from the test class only by the
     * extension.
     *
     * @param testClass The test class.
     * @param extension The extension of the resource file.
     *
     * @return The path to the specified file.
     *
     * @throws IOException for input/output errors
     */
    public static Path getResourcePath(Class<?> testClass, String extension)
            throws IOException {
        String resourceName = testClass.getSimpleName();
        if ((!resourceName.endsWith(".")) && (!extension.startsWith("."))) {
            resourceName += ".";
        }
        resourceName += extension;
        return getFilePath(testClass, resourceName);
    }

    /**
     * Get the path to a file co-located with the test class.
     *
     * @param testClass The test class.
     * @param resourceName The name of the resource file.
     *
     * @return The path to the specified file.
     *
     * @throws IOException for input/output errors
     */
    public static Path getFilePath(Class<?> testClass, String resourceName)
            throws IOException {
        String packageName = testClass.getPackage().getName();
        String packagePath = packageName.replaceAll("\\.", "\\" + File.separator);
        String rscName = resourceName
                .replaceAll("\\\\", "\\" + File.separator)
                .replaceAll("/", "\\" + File.separator);
        return locateFile(CWD, true, packagePath + File.separator + rscName);
    }

    /**
     * Create a temporary directory in the base test temporary directory.
     *
     * @return The newly created temporary directory.
     *
     * @throws IOException for input/output errors
     */
    public static Path createTempDirectory()
            throws IOException {
        return createTempDirectory(TEMP);
    }

    /**
     * Create a temporary directory in the specified parent directory.
     *
     * <p> If the specified parent directory is not specified then the base test temporary directory will be used. </p>
     *
     * @param parent The parent directory.
     *
     * @return The newly created temporary directory.
     *
     * @throws IOException for input/output errors
     */
    public static Path createTempDirectory(Path parent)
            throws IOException {
        Path dir = (parent != null) ? parent : TEMP;
        Path temp = Files.createTempDirectory(dir, "test_dir_");
        temp.toFile().deleteOnExit();
        TEMP_DIRS.add(temp);
        return temp;
    }

    /**
     * Create a temporary file in the base test temporary directory.
     *
     * @return A newly created temporary file.
     *
     * @throws IOException for input/output errors
     */
    public static Path createTempFile()
            throws IOException {
        return createTempFile(TEMP);
    }

    /**
     * Create a temporary file in the specified directory.
     *
     * @param directory The directory for the temporary file.
     *
     * @return A newly created temporary file.
     *
     * @throws IOException for input/output errors
     */
    public static Path createTempFile(Path directory)
            throws IOException {
        Path dir = (directory != null) ? directory : TEMP;
        Path temp = Files.createTempFile(dir, "test_", "_file");
        temp.toFile().deleteOnExit();
        TEMP_FILES.add(temp);
        return temp;
    }

    /**
     * Constructor.
     */
    private TestFiles() {
        super();
    }

    /**
     * A file visitor which locates a specific file.
     */
    private static class RecursiveFind
            implements FileVisitor<Path> {
        /**
         * The directory to skip.
         */
        private final Path skipDirectory;

        /**
         * The name of the desired file.
         */
        private final String name;

        /**
         * The located file.
         */
        private Path file;

        /**
         * Constructor.
         *
         * @param skipDirectory The subtree in the current directory to skip.
         * @param name The name of the file to locate.
         */
        RecursiveFind(Path skipDirectory, String name) {
            super();
            this.skipDirectory = (skipDirectory != null) ? skipDirectory.toAbsolutePath().normalize() : null;
            this.name = (name != null) ? name.trim() : "";
        }

        /**
         * Get the path to the file of interest.
         *
         * @return The path to the file of interest.
         */
        Path getFile() {
            return file;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {
            Path normalDir = dir.toAbsolutePath().normalize();
            return ((skipDirectory == null) || (!skipDirectory.equals(normalDir)))
                    ? FileVisitResult.CONTINUE
                    : FileVisitResult.SKIP_SUBTREE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
            Path normalFile = file.toAbsolutePath().normalize();
            if (normalFile.toString().endsWith(name)) {
                this.file = normalFile;
            }
            return (this.file == null) ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
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
        public FileVisitResult postVisitDirectory(Path dir, IOException error) {
            return (this.file == null) ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
        }
    }

    /**
     * A file visitor which deletes all the visited files.
     */
    private static class RecursiveDelete
            implements FileVisitor<Path> {
        /**
         * The number of failures in the operation.
         */
        private int failures = 0;

        /**
         * Constructor.
         */
        RecursiveDelete() {
            super();
        }

        /**
         * Get the number of failures in the operation.
         *
         * @return The number of failures in the operation.
         */
        int getFailures() {
            return failures;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
            try {
                Files.deleteIfExists(file);
            } catch (Exception deleteError) {
                failures++;
                getLogger().error("Could not delete file: {}", file, deleteError);
            }
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException error) {
            failures++;
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException error) {
            try {
                Files.deleteIfExists(dir);
            } catch (Exception deleteError) {
                failures++;
                getLogger().error("Could not delete directory: {}", dir, deleteError);
            }
            return FileVisitResult.CONTINUE;
        }
    }
}