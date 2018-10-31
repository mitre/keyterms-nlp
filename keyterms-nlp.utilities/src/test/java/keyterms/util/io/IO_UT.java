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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import keyterms.testing.TestFiles;
import keyterms.testing.Tests;
import keyterms.util.collect.Bags;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class IO_UT {

    @Test
    public void corePaths()
            throws Exception {
        assertEquals(new File(".").getCanonicalFile(), IO.CWD.toFile());
        assertNotNull(IO.TEMP);
    }

    @Test
    public void toPath()
            throws Exception {
        Tests.testError(IllegalArgumentException.class, () -> IO.toPath(null));
        // String references
        assertEquals(IO.toPath("hello/world"), IO.toPath("hello", "world"));
        assertEquals(IO.toPath("hello/world"), IO.toPath("hello", "/world"));
        // File references
        assertEquals(IO.toPath("hello/world"), IO.toPath(new File("hello/world")));
        // URL references
        URL fileUrl = IO_UT.class.getResource(IO_UT.class.getSimpleName() + ".class");
        Path path = Paths.get(fileUrl.toURI()).normalize();
        File file = path.toFile();
        assertEquals(path, IO.toPath(fileUrl));
        // URI references
        URI fileUri = fileUrl.toURI();
        assertEquals(path, IO.toPath(fileUri));
        assertEquals(path, IO.toPath(file.toURI()));
        // URI with spaces
        fileUri = TestFiles.getFilePath(getClass(), "Hello World.txt").toUri();
        assertTrue(IO.isValidFile(fileUri));
    }

    @Test
    public void getRoots() {
        Path[] roots = IO.getRoots();
        assertNotNull(roots);
        assertTrue(roots.length > 0);
        for (Path root : roots) {
            assertTrue(IO.isRoot(root));
        }
        assertFalse(IO.isRoot(IO.CWD));
    }

    @Test
    public void getRoot() {
        Path cwdRoot = IO.getRoot(IO.CWD);
        assertTrue(IO.isRoot(cwdRoot));
    }

    @Test
    public void normalize() {
        assertNull(IO.normalize(null));
        assertEquals(IO.CWD, IO.normalize(""));
        assertEquals(IO.CWD, IO.normalize("."));
        Path[] roots = IO.getRoots();
        assertNotNull(roots);
        assertNotEquals(0, roots.length);
        String test = roots[0] + File.separator + "." + File.separator + ".." + File.separator + "hello_world";
        String expected = roots[0] + "hello_world";
        assertEquals(expected, IO.normalize(test).toString());
    }

    @Test
    public void existsAndValidityMethods() {
        assertTrue(IO.exists(IO.CWD));
        assertTrue(IO.isValidDirectory(IO.CWD));
        assertFalse(IO.isValidFile(IO.CWD));
        assertFalse(IO.exists("not_a_valid_path"));
        assertFalse(IO.isValidDirectory("not_a_valid_path"));
        assertFalse(IO.isValidFile("not_a_valid_path"));
    }

    @Test
    public void getNames() {
        Path p = Paths.get("/hello/world/one/two/three");
        assertEquals(Bags.arrayList("hello", "world", "one", "two", "three"), IO.getNames(p));
    }

    @Test
    public void getName() {
        for (Path root : IO.getRoots()) {
            assertNull(IO.getName(root));
        }
        assertEquals("hello", IO.getName(Paths.get("/hello")));
        assertEquals("three", IO.getName(Paths.get("/hello/world/one/two/three")));
    }

    @Test
    public void find()
            throws Exception {
        Path start = IO.CWD.resolve("randomNonExistent");
        Path projectHomeFile = IO.find(start, (p) -> ".project_home".equals(IO.getName(p)));
        assertNull(projectHomeFile);
        if (IO.CWD.equals(TestFiles.PROJECT_HOME)) {
            projectHomeFile = IO.find((p) -> ".project_home".equals(IO.getName(p)));
        } else {
            projectHomeFile = IO.find(TestFiles.PROJECT_HOME, (p) -> ".project_home".equals(IO.getName(p)));
        }
        assertTrue(IO.isValidFile(projectHomeFile));
    }

    @Test
    public void locate()
            throws Exception {
        Path start = IO.CWD.resolve("utils-core/src");
        Path projectHomeFile = IO.locate(start, (p) -> ".project_home".equals(IO.getName(p)));
        assertTrue(IO.isValidFile(projectHomeFile));
        projectHomeFile = IO.locate(start, (p) -> ".project_home".equals(IO.getName(p)));
        assertTrue(IO.isValidFile(projectHomeFile));
    }

    @Test
    public void move()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path current;
        Path dir1 = TestFiles.createTempDirectory(basePath);
        Path dir2 = TestFiles.createTempDirectory(basePath);
        Path sourceFile = TestFiles.createTempFile(basePath);
        IO.appendText(sourceFile, "Hello World!");
        Path target1 = IO.toPath(dir1, "Files_UT.txt");
        Path target2 = IO.toPath(dir2, "Files_UT.txt");
        Path target3 = IO.toPath(basePath, "move/file.txt");
        assertTrue(IO.exists(sourceFile));
        assertFalse(IO.exists(target1));
        assertFalse(IO.exists(target2));
        assertFalse(IO.exists(target3));
        // Move to target 1
        current = IO.move(sourceFile, target1);
        assertEquals("Files_UT.txt", current.getFileName().toString());
        assertFalse(IO.exists(sourceFile));
        assertTrue(IO.exists(target1));
        assertFalse(IO.exists(target2));
        assertFalse(IO.exists(target3));
        assertEquals("Hello World!", IO.readText(target1));
        // Move to dir2
        current = IO.move(target1, dir2);
        assertEquals("Files_UT.txt", current.getFileName().toString());
        assertFalse(IO.exists(sourceFile));
        assertFalse(IO.exists(target1));
        assertTrue(IO.exists(target2));
        assertFalse(IO.exists(target3));
        assertEquals("Hello World!", IO.readText(target2));
        // Non-existent parent directory for target.
        current = IO.move(target2, target3);
        assertEquals("file.txt", current.getFileName().toString());
        assertFalse(IO.exists(sourceFile));
        assertFalse(IO.exists(target1));
        assertFalse(IO.exists(target2));
        assertTrue(IO.exists(target3));
        assertEquals("Hello World!", IO.readText(target3));
        // Move back to original
        current = IO.move(target3, basePath);
        assertEquals("file.txt", current.getFileName().toString());
        current = IO.rename(current, sourceFile.getFileName().toString());
        assertNotEquals("Files_UT.txt", current.getFileName().toString());
        assertEquals(sourceFile.getFileName(), current.getFileName());
        assertTrue(IO.exists(sourceFile));
        assertFalse(IO.exists(target1));
        assertFalse(IO.exists(target2));
        assertEquals("Hello World!", IO.readText(sourceFile));
    }

    @Test(expected = IOException.class)
    public void badRename()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path sourceFile = TestFiles.createTempFile(basePath);
        Path targetFile = TestFiles.createTempFile(basePath);
        IO.rename(sourceFile, targetFile.getFileName().toString());
    }

    @Test
    public void moveWithOverwriteSuccess()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path sourceFile = TestFiles.createTempFile(basePath);
        Path targetFile = TestFiles.createTempFile(basePath);
        IO.move(sourceFile, targetFile, true);
        assertFalse(IO.exists(sourceFile));
        assertTrue(IO.exists(targetFile));
    }

    @Test(expected = IOException.class)
    public void moveWithOverwriteFailure()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path sourceFile = TestFiles.createTempFile(basePath);
        Path targetFile = TestFiles.createTempFile(basePath);
        IO.move(sourceFile, targetFile, false);
    }

    @Test
    public void copy()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path current;
        Path dir1 = TestFiles.createTempDirectory(basePath);
        Path dir2 = TestFiles.createTempDirectory(basePath);
        Path sourceFile = TestFiles.createTempFile(basePath);
        IO.appendText(sourceFile, "Hello World!");
        Path target1 = IO.toPath(dir1, "Files_UT.txt");
        Path target2 = IO.toPath(dir2, "Files_UT.txt");
        Path target3 = IO.toPath(basePath, "copy/file.txt");
        assertTrue(IO.exists(sourceFile));
        assertFalse(IO.exists(target1));
        assertFalse(IO.exists(target2));
        assertFalse(IO.exists(target3));
        // Copy to target 1
        current = IO.copy(sourceFile, target1);
        assertEquals("Files_UT.txt", current.getFileName().toString());
        assertTrue(IO.exists(sourceFile));
        assertTrue(IO.exists(target1));
        assertFalse(IO.exists(target2));
        assertFalse(IO.exists(target3));
        assertEquals("Hello World!", IO.readText(target1));
        // Copy to dir2
        current = IO.copy(target1, dir2);
        assertEquals("Files_UT.txt", current.getFileName().toString());
        assertTrue(IO.exists(sourceFile));
        assertTrue(IO.exists(target1));
        assertTrue(IO.exists(target2));
        assertFalse(IO.exists(target3));
        assertEquals("Hello World!", IO.readText(target2));
        // Non-existent parent directory for target.
        current = IO.copy(target2, target3);
        assertEquals("file.txt", current.getFileName().toString());
        assertTrue(IO.exists(sourceFile));
        assertTrue(IO.exists(target1));
        assertTrue(IO.exists(target2));
        assertTrue(IO.exists(target3));
        assertEquals("Hello World!", IO.readText(target3));
    }

    @Test
    public void copyWithOverwriteSuccess()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path sourceFile = TestFiles.createTempFile(basePath);
        Path targetFile = TestFiles.createTempFile(basePath);
        IO.writeText(sourceFile, "Hello World");
        IO.writeText(targetFile, "Jello Pudding");
        IO.copy(sourceFile, targetFile, true);
        assertTrue(IO.exists(sourceFile));
        assertTrue(IO.exists(targetFile));
        assertEquals("Hello World", IO.readText(sourceFile));
        assertEquals("Hello World", IO.readText(targetFile));
    }

    @Test(expected = IOException.class)
    public void copyWithOverwriteFailure()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path sourceFile = TestFiles.createTempFile(basePath);
        Path targetFile = TestFiles.createTempFile(basePath);
        IO.copy(sourceFile, targetFile, false);
    }

    @Test
    public void createDelete()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path directory = IO.createDirectory(basePath.resolve("temp_dir"));
        assertTrue(IO.exists(directory));
        assertTrue(IO.isValidDirectory(directory));
        assertFalse(IO.isValidFile(directory));
        Path file = IO.createFile(directory.resolve("temp_file"));
        assertTrue(IO.exists(file));
        assertFalse(IO.isValidDirectory(file));
        assertTrue(IO.isValidFile(file));
        IO.delete(directory);
        assertFalse(IO.exists(directory));
        assertFalse(IO.exists(file));
    }

    @Test
    public void readWriteData()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path testFile = basePath.resolve("Files_UT.txt");
        // Basic read/write
        String test1 = "Hello World, I am test text!";
        String test2 = "\nI like Jello Pudding!";
        byte[] data = Encoding.encode(test1, Encoding.UTF16, true);
        IO.writeBytes(testFile, data);
        byte[] read = IO.readBytes(testFile);
        assertEquals(Encoding.UTF16, Encoding.detectBom(read));
        assertEquals(test1, Encoding.decode(read));
        // Overwrite
        IO.writeBytes(testFile, data);
        read = IO.readBytes(testFile);
        assertEquals(Encoding.UTF16, Encoding.detectBom(read));
        assertEquals(test1, Encoding.decode(read));
        // Append
        data = Encoding.encode(test2, Encoding.UTF16);
        IO.appendBytes(testFile, data);
        read = IO.readBytes(testFile);
        assertEquals(test1 + test2, Encoding.decode(read));
        // Append to non-existent file.
        IO.delete(testFile);
        assertFalse(IO.isValidFile(testFile));
        data = Encoding.encode(test1 + test2, Encoding.UTF16, true);
        IO.appendBytes(testFile, data);
        read = IO.readBytes(testFile);
        assertEquals(test1 + test2, Encoding.decode(read));
    }

    @Test
    public void readBlocks()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path testFile = basePath.resolve("Files_UT.txt");
        String testText = "Hello World, I am test text!";
        byte[] contents = Encoding.encode(testText, Encoding.UTF8);
        IO.writeBytes(testFile, contents);
        byte[] block = IO.readBytes(testFile, null);
        assertArrayEquals(contents, block);
        block = IO.readBytes(testFile, -1);
        assertArrayEquals(contents, block);
        block = IO.readBytes(testFile, 0);
        assertArrayEquals(new byte[0], block);
        block = IO.readBytes(testFile, 10);
        assertArrayEquals(Arrays.copyOf(contents, 10), block);
    }

    @Test
    public void archiveListing()
            throws Exception {
        IO.inArchive(TestFiles.getFilePath(getClass(), "Simple Archive.tar"), (root) -> {
            ArrayList<Path> listing = new ArrayList<>();
            IO.walk(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    listing.add(file);
                    return FileVisitResult.CONTINUE;
                }
            });
            assertNotEquals(0, listing.size());
        });
    }

    @Test
    public void newArchive()
            throws Exception {
        Path basePath = TestFiles.createTempDirectory();
        Path archiveFile = basePath.resolve("test_archive.jar");
        IO.inArchive(archiveFile, true, (root) -> {
            Path newFile = root.resolve("Temp.txt");
            IO.writeText(newFile, "Hello World!", Encoding.UTF8);
            String text = IO.readText(newFile, Encoding.UTF8);
            assertEquals("Hello World!", text);
        });
    }
}