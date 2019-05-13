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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.LoggerFactory;

import keyterms.util.Errors;
import keyterms.util.collect.Bags;
import keyterms.util.collect.Keyed;
import keyterms.util.system.OS;
import keyterms.util.text.Strings;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * General I/O utilities.
 */
public final class IO {
    /**
     * The default file system.
     */
    private static final FileSystem FILE_SYSTEM = FileSystems.getDefault();

    /**
     * The root paths for the default file system.
     */
    private static final Set<Path> ROOTS = StreamSupport.stream(FILE_SYSTEM.getRootDirectories().spliterator(), false)
            .map(Path::toAbsolutePath)
            .map(Path::normalize)
            .collect(Collectors.toSet());

    /**
     * A reference to the system working directory.
     */
    public static final Path CWD = normalize(".");

    /**
     * The path to the system's preferred temporary directory.
     */
    public static final Path TEMP = normalize(System.getProperty("java.io.tmpdir"));

    /**
     * Get a {@code Path} representation of the specified path parameter.
     *
     * <p> The path parameter may be one of: </p>
     *
     * <ul>
     *
     * <li> A {@code Path} object. </li>
     *
     * <li> A {@code File} object. </li>
     *
     * <li> A {@code URL} object. </li>
     *
     * <li> A {@code URI} object. </li>
     *
     * <li> A {@code CharSequence}, which will get a path from the default file system. </li>
     *
     * </ul>
     *
     * @param basePath The base path object.
     * @param subPaths The additional path elements to be appended to the base path.
     *
     * @return A {@code Path} representation of the specified path parameter.
     */
    public static Path toPath(Object basePath, String... subPaths) {
        Path path = null;
        if (basePath instanceof Path) {
            path = (Path)basePath;
        }
        if (basePath instanceof File) {
            path = ((File)basePath).toPath();
        }
        if (basePath instanceof URL) {
            URL url = (URL)basePath;
            path = Paths.get(getFilePath(url.getProtocol(), url.getPath()));
        }
        if (basePath instanceof URI) {
            URI uri = (URI)basePath;
            path = Paths.get(getFilePath(uri.getScheme(), uri.getRawPath()));
        }
        if (basePath instanceof CharSequence) {
            path = FILE_SYSTEM.getPath(basePath.toString());
        }
        if (path == null) {
            throw new IllegalArgumentException("Unrecognized path object type: " + basePath);
        }
        if (subPaths.length > 0) {
            path = path.getFileSystem().getPath(path.toString(), subPaths);
        }
        return path.toAbsolutePath().normalize();
    }

    /**
     * Get the file path from the specified checked portions of a {@code URL} or {@code URI}.
     *
     * @param protocol The resource locator protocol or scheme.
     * @param path The path portion of the resource locator.
     *
     * @return The file path.
     */
    private static String getFilePath(String protocol, String path) {
        if (!"file".equalsIgnoreCase(protocol)) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
        String localPath = path;
        try {
            localPath = URLDecoder.decode(path, Encoding.UTF8.name());
        } catch (Exception error) {
            Errors.ignore(error);
        }
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            while (localPath.startsWith("/")) {
                localPath = localPath.substring(1);
            }
        }
        return localPath;
    }

    /**
     * Get a normalized canonical or absolute system representation of the specified path.
     *
     * <p> A value of {@code null} will be returned if the input parameter is {@code null}. </p>
     *
     * @param path The path to normalize.
     *
     * @return The normalized canonical or absolute system representation of the specified path.
     */
    public static Path normalize(Object path) {
        Path normalized = null;
        if (path != null) {
            normalized = toPath(path);
            if (normalized != null) {
                normalized = normalized.toAbsolutePath();
                normalized = normalized.normalize();
            }
        }
        return normalized;
    }

    /**
     * Get the root paths for the default file system.
     *
     * @return The root paths for the default file system.
     */
    public static Path[] getRoots() {
        return ROOTS.toArray(new Path[0]);
    }

    /**
     * Get the root directory of the specified file.
     *
     * @param path The path of interest.
     *
     * @return The root directory of the specified file.
     */
    public static Path getRoot(Object path) {
        return (path != null) ? normalize(path).getRoot() : null;
    }

    /**
     * Determine if the specified path is a root path on the default file system.
     *
     * @param path The path of interest.
     *
     * @return A flag indicating whether the specified path is a root path on the default file system.
     */
    public static boolean isRoot(Object path) {
        return ((path != null) && (ROOTS.contains(normalize(path))));
    }

    /**
     * Determine if the specified path is non-{@code null} and exists.
     *
     * @param path The path to test.
     *
     * @return A flag indicating whether the specified path is non-{@code null} and exists.
     */
    public static boolean exists(Object path) {
        return ((path != null) && (Files.exists(toPath(path))));
    }

    /**
     * Determine if the specified path represents a valid existing directory.
     *
     * @param path The path to test.
     *
     * @return A flag indicating whether the specified path represents a valid existing directory.
     */
    public static boolean isValidDirectory(Object path) {
        return ((path != null) && (Files.isDirectory(toPath(path))));
    }

    /**
     * Determine if the specified path represents a valid existing file.
     *
     * @param path The path to test.
     *
     * @return A flag indicating whether the specified path represents a valid existing file.
     */
    public static boolean isValidFile(Object path) {
        return ((path != null) && (Files.isRegularFile(toPath(path))));
    }

    /**
     * Convert the specified path to a list of strings containing the path elements.
     *
     * @param path The path.
     *
     * @return The path elements as text.
     */
    public static List<String> getNames(Object path) {
        return StreamSupport.stream(toPath(path).spliterator(), false)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    /**
     * Get the last name element of the specified path.
     *
     * @param path The path.
     *
     * @return The last name element of the specified path.
     */
    public static String getName(Object path) {
        String name = null;
        Path p = toPath(path);
        if ((p != null) && (p.getNameCount() > 0)) {
            name = p.getName(p.getNameCount() - 1).toString();
        }
        return name;
    }

    /**
     * Locate the specified path using a recursive listing of the current working directory.
     *
     * <p> This method will return the first path found which matches the filter. </p>
     * <p> Note: If the paths representing directories are rejected, the subtree for that directory will not be
     * searched. </p>
     *
     * @param filter The filter used to accept the files of interest.
     *
     * @return The path to the specified file.
     *
     * @throws IOException for input/output errors
     */
    public static Path find(Predicate<Path> filter)
            throws IOException {
        return find(CWD, filter);
    }

    /**
     * Locate the specified path using a recursive listing of the specified directory.
     *
     * <p> This method will return the first path found which matches the filter. </p>
     * <p> Note: If the paths representing directories are rejected, the subtree for that directory will not be
     * searched. </p>
     *
     * @param path The directory to search.
     * @param filter The filter used to accept the files of interest.
     *
     * @return The path to the specified file.
     *
     * @throws IOException for input/output errors
     */
    public static Path find(Object path, Predicate<Path> filter)
            throws IOException {
        PathFinder pathFinder = new PathFinder(filter);
        Path p = toPath(path);
        if (IO.isValidFile(path)) {
            pathFinder.visitFile(p, Files.readAttributes(p, BasicFileAttributes.class));
        }
        if (IO.isValidDirectory(path)) {
            Files.walkFileTree(p, pathFinder);
        }
        return pathFinder.getPath();
    }

    /**
     * Search for a matching path starting with the current working directory.
     *
     * <p> This method will return the first file found with the specified name. </p>
     * <p> This method does recursive directory searches starting with the specified directory and moving toward the
     * root of the file system until it either finds the desired file or gets to a root directory. </p>
     *
     * <p> Note: This method has the potential to search the entire root partition for the specified base path. </p>
     *
     * @param filter The filter used to accept the files of interest.
     *
     * @return The path to the specified file.
     *
     * @throws IOException for input/output errors
     */
    public static Path locate(Predicate<Path> filter)
            throws IOException {
        return locate(CWD, filter);
    }

    /**
     * Search for a matching path.
     *
     * <p> This method will return the first file found with the specified name. </p>
     * <p> This method does recursive directory searches starting with the specified directory and moving toward the
     * root of the file system until it either finds the desired file or gets to a root directory. </p>
     *
     * <p> Note: This method has the potential to search the entire root partition for the specified base path. </p>
     *
     * @param path The starting path of the search.
     * @param filter The filter used to accept the files of interest.
     *
     * @return The path to the specified file.
     *
     * @throws IOException for input/output errors
     */
    public static Path locate(Object path, Predicate<Path> filter)
            throws IOException {
        Path p = toPath(path);
        Path found = find(path, filter);
        while ((found == null) && (!isRoot(p))) {
            Path prior = p;
            p = p.getParent();
            PathFinder pathFinder = new PathFinder(filter, (tp) -> prior.equals(normalize(tp)));
            Files.walkFileTree(p, pathFinder);
            found = pathFinder.getPath();
        }
        return found;
    }

    /**
     * Create the specified directory and any parent directories required.
     *
     * <p> This method will not throw an exception if the path refers to an existing directory, but may throw an
     * exception if the path references a valid file. </p>
     *
     * @param path The path to the desired directory.
     *
     * @return The normalized path reference to the created directory.
     *
     * @throws IOException for input/output errors
     */
    public static Path createDirectory(Object path)
            throws IOException {
        Path p = toPath(path);
        if (!isValidDirectory(p)) {
            Files.createDirectories(p);
        }
        return p;
    }

    /**
     * Create the specified file and any parent directories required.
     *
     * <p> This method will not throw an exception if a valid file already exists, but may throw an exception if the
     * path references an existing directory. </p>
     *
     * @param path The path to the desired file.
     *
     * @return The normalized path reference to the created file.
     *
     * @throws IOException for input/output errors
     */
    public static Path createFile(Object path)
            throws IOException {
        Path p = toPath(path);
        if (!isValidFile(p)) {
            createDirectory(p.getParent());
            Files.createFile(p);
        }
        return p;
    }

    /**
     * Rename the specified file or directory.
     *
     * @param source The source file or directory.
     * @param newName The new name.
     *
     * @return The path to the renamed file.
     *
     * @throws IOException for input/output errors
     */
    public static Path rename(Object source, CharSequence newName)
            throws IOException {
        if (source == null) {
            throw new NullPointerException("Source required.");
        }
        if ((newName == null) || (newName.length() <= 0)) {
            throw new IllegalArgumentException("New name required.");
        }
        Path sourcePath = toPath(source);
        Path targetPath = sourcePath.resolveSibling(newName.toString());
        return Files.move(sourcePath, targetPath);
    }

    /**
     * Move a file to a target location.
     *
     * <p> This method will throw an exception if the target file already exists. </p>
     *
     * @param source The source file.
     * @param target The target directory or file.
     *
     * @return The path to the new file location.
     *
     * @throws IOException for input/output errors
     */
    public static Path move(Object source, Object target)
            throws IOException {
        return copyOrMove(source, target, false, false);
    }

    /**
     * Move a file to a target location.
     *
     * @param source The source file.
     * @param target The target directory or file.
     * @param overwrite A flag indicating whether to overwrite the target if it already exists.
     *
     * @return The path to the new file location.
     *
     * @throws IOException for input/output errors
     */
    public static Path move(Object source, Object target, boolean overwrite)
            throws IOException {
        return copyOrMove(source, target, overwrite, false);
    }

    /**
     * Copy a file to a target location.
     *
     * <p> This method will throw an exception if the target file already exists. </p>
     *
     * @param source The source file.
     * @param target The target directory or file.
     *
     * @return The path to the new file location.
     *
     * @throws IOException for input/output errors
     */
    public static Path copy(Object source, Object target)
            throws IOException {
        return copyOrMove(source, target, false, true);
    }

    /**
     * Copy a file to a target location.
     *
     * @param source The source file.
     * @param target The target directory or file.
     * @param overwrite A flag indicating whether to overwrite the target if it already exists.
     *
     * @return The path to the new file location.
     *
     * @throws IOException for input/output errors
     */
    public static Path copy(Object source, Object target, boolean overwrite)
            throws IOException {
        return copyOrMove(source, target, overwrite, true);
    }

    /**
     * Move a file to a target location.
     *
     * @param source The source file.
     * @param target The target directory or file.
     * @param overwrite A flag indicating whether to overwrite the target if it already exists.
     * @param copy A flag indicating whether to copy or move the file.
     *
     * @return The path to the new file location.
     *
     * @throws IOException for input/output errors
     */
    private static Path copyOrMove(Object source, Object target, boolean overwrite, boolean copy)
            throws IOException {
        Path newLocation;
        if (source == null) {
            throw new IOException("Source required.");
        }
        if (target == null) {
            throw new IOException("Target required.");
        }
        if (!isValidFile(source)) {
            throw new IOException("Invalid source file: " + source);
        }
        Path sourcePath = toPath(source);
        Path targetPath = toPath(target);
        if (isValidDirectory(target)) {
            targetPath = targetPath.resolve(sourcePath.getFileName());
        }
        Path targetParent = targetPath.getParent();
        if (!exists(targetParent)) {
            createDirectory(targetParent);
        }
        CopyOption[] optionsArray = (overwrite)
                ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING }
                : new CopyOption[0];
        if (copy) {
            newLocation = Files.copy(sourcePath, targetPath, optionsArray);
        } else {
            newLocation = Files.move(sourcePath, targetPath, optionsArray);
        }
        return newLocation;
    }

    /**
     * Walk the file tree as specified.
     *
     * @param path The starting path for the walk.
     * @param visitor The file visitor.
     *
     * @throws IOException for input/output errors
     */
    public static void walk(Object path, FileVisitor<Path> visitor)
            throws IOException {
        Path p = toPath(path);
        if (isValidFile(p)) {
            visitor.visitFile(p, Files.readAttributes(p, BasicFileAttributes.class));
        }
        if (isValidDirectory(p)) {
            Files.walkFileTree(p, visitor);
        }
    }

    /**
     * Read a binary file in its entirety.
     *
     * <p> Note: This operation reads the file contents into memory and may not be appropriate for large files. </p>
     *
     * @param path The path to the file.
     *
     * @return The bytes read from the file.
     *
     * @throws IOException for input/output errors
     */
    public static byte[] readBytes(Object path)
            throws IOException {
        return readBytes(path, null);
    }

    /**
     * Read up to the number of specified bytes from the beginning of the file.
     *
     * <p> Note: This operation reads the file contents into memory and may not be appropriate for large files. </p>
     *
     * <p> If the specified number of bytes is {@code null} or less than zero, the entire file will be read, up to
     * {@code Integer.MAX_VALUE} bytes. </p>
     *
     * @param path The path to the file.
     * @param numBytes The maximum number of bytes to read.
     *
     * @return The bytes read from the file.
     *
     * @throws IOException for input/output errors
     */
    public static byte[] readBytes(Object path, Integer numBytes)
            throws IOException {
        Path p = toPath(path);
        long fileSize = Files.size(p);
        int cappedSize = (int)Math.min(fileSize, Integer.MAX_VALUE);
        int wantSize = ((numBytes != null) && (numBytes >= 0)) ? numBytes : cappedSize;
        int bytesToRead = Math.min(cappedSize, wantSize);
        try (InputStream inputStream = Files.newInputStream(p, READ)) {
            return Streams.read(inputStream, bytesToRead);
        }
    }

    /**
     * Read a text file.
     *
     * <p> Note: This operation reads the file contents into memory and may not be appropriate for  large files. </p>
     *
     * <p> A detected character set will be used if a known byte order mark is present at the beginning of the file,
     * otherwise the platform default encoding will be used. </p>
     *
     * @param path The path to the file.
     *
     * @return The file's text contents.
     *
     * @throws IOException for input/output errors
     */
    public static String readText(Object path)
            throws IOException {
        return readText(path, null);
    }

    /**
     * Read a text file using the specified character encoding to interpret the binary contents.
     *
     * <p> Note: This operation reads the file contents into memory and may not be appropriate for large files. </p>
     *
     * <p> If the specified character set encoding is {@code null}, a detected character set will be used if a known
     * byte order mark is present at the beginning of the bytes, otherwise the platform default encoding will be used.
     * </p>
     *
     * @param path The path to the file.
     * @param encoding The character encoding scheme.
     *
     * @return The file's text contents.
     *
     * @throws IOException for input/output errors
     */
    public static String readText(Object path, Charset encoding)
            throws IOException {
        byte[] bytes = readBytes(path);
        Charset charset = (encoding != null) ? encoding : Encoding.detectBom(bytes);
        charset = (charset != null) ? charset : Encoding.PLATFORM_DEFAULT;
        return Encoding.decode(readBytes(path), charset);
    }

    /**
     * Write a binary file.
     *
     * <p> This operation will overwrite any pre-existing file. </p>
     *
     * @param path The path to the file.
     * @param bytes The bytes to write.
     *
     * @throws IOException for input/output errors
     */
    public static void writeBytes(Object path, byte[] bytes)
            throws IOException {
        updateFile(path, bytes, false);
    }

    /**
     * Append to a binary file's contents.
     *
     * @param path The path to the file.
     * @param bytes The bytes to write.
     *
     * @throws IOException for input/output errors
     */
    public static void appendBytes(Object path, byte[] bytes)
            throws IOException {
        updateFile(path, bytes, true);
    }

    /**
     * Write the binary file contents.
     *
     * <p> This operation may overwrite any pre-existing file. </p>
     *
     * @param path The path to the file.
     * @param contents The bytes to write.
     * @param append A flag indicating whether to append or overwrite any existing contents.
     *
     * @throws IOException for input/output errors
     */
    private static void updateFile(Object path, byte[] contents, boolean append)
            throws IOException {
        Path p = toPath(path);
        Path parent = p.getParent();
        if (!exists(parent)) {
            createDirectory(parent);
        }
        StandardOpenOption option = (append) ? APPEND : TRUNCATE_EXISTING;
        try (OutputStream outputStream = Files.newOutputStream(p, CREATE, WRITE, option)) {
            outputStream.write(contents);
            outputStream.flush();
        }
    }

    /**
     * Write a text file using the platform's default character set to encode the binary contents.
     *
     * <p> This operation will overwrite any pre-existing file. </p>
     *
     * @param path The path to the file.
     * @param contents The text to write.
     *
     * @throws IOException for input/output errors
     */
    public static void writeText(Object path, CharSequence contents)
            throws IOException {
        updateTextFile(path, contents, null, false);
    }

    /**
     * Write a text file using the specified character set to encode the binary contents.
     *
     * <p> This operation will overwrite any pre-existing file. </p>
     *
     * <p> If the specified encoding is {@code null}, the platform's default encoding will be used. </p>
     *
     * @param path The path to the file.
     * @param contents The text to write.
     * @param encoding The character encoding scheme.
     *
     * @throws IOException for input/output errors
     */
    public static void writeText(Object path, CharSequence contents, Charset encoding)
            throws IOException {
        updateTextFile(path, contents, encoding, false);
    }

    /**
     * Write a text file using the platform's default character set to encode the binary contents.
     *
     * <p> This method will behave as {@link #writeText(Object, CharSequence, Charset) writeText}
     * if the file does not exist. </p>
     *
     * @param path The path to the file.
     * @param contents The text to write.
     *
     * @throws IOException for input/output errors
     */
    public static void appendText(Object path, CharSequence contents)
            throws IOException {
        updateTextFile(path, contents, null, true);
    }

    /**
     * Write a text file using the specified character set to encode the binary contents.
     *
     * <p> This method will behave as {@link #writeText(Object, CharSequence, Charset) writeText}
     * if the file does not exist. </p>
     *
     * <p> If the specified encoding is {@code null}, the platform's default encoding will be used. </p>
     *
     * @param path The path to the file.
     * @param contents The text to write.
     * @param encoding The character encoding scheme.
     *
     * @throws IOException for input/output errors
     */
    public static void appendText(Object path, CharSequence contents, Charset encoding)
            throws IOException {
        updateTextFile(path, contents, encoding, true);
    }

    /**
     * Write a text file using the specified character set to encode the binary contents.
     *
     * <p> This operation may overwrite any pre-existing file. </p>
     *
     * <p> If the specified encoding is {@code null}, the platform's default encoding will be used. </p>
     *
     * <p> Note: A byte order mark will be prepended if the byte order mark is known for the character set and the
     * character set requires it. </p>
     *
     * @param path The path to the file.
     * @param contents The text to write.
     * @param encoding The character encoding scheme.
     * @param append A flag indicating whether to append or overwrite any existing contents.
     *
     * @throws IOException for input/output errors
     */
    private static void updateTextFile(Object path, CharSequence contents, Charset encoding, boolean append)
            throws IOException {
        Path p = toPath(path);
        boolean prependBom = !append;
        if (append) {
            if (isValidFile(p)) {
                prependBom = (Files.size(p) == 0);
            } else {
                prependBom = true;
            }
        }
        Charset charset = (encoding != null) ? encoding : Encoding.PLATFORM_DEFAULT;
        if (Encoding.UTF8.equals(charset)) {
            prependBom = false;
        }
        updateFile(p, Encoding.encode(contents, charset, prependBom), append);
    }

    /**
     * Attempt to delete the contents of the specified directory.
     *
     * @param path The path to the desired directory.
     *
     * @throws IOException for input/output errors
     */
    public static void empty(Object path)
            throws IOException {
        Path p = toPath(path);
        if (!isValidDirectory(p)) {
            throw new IOException("Invalid directory: " + path);
        }
        if (isRoot(p)) {
            throw new IOException("Attempt to empty a root directory.");
        }
        Files.walkFileTree(p, new SimpleFileVisitor<>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                tryDelete(file);
                return FileVisitResult.CONTINUE;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public FileVisitResult postVisitDirectory(Path directory, IOException error) {
                if (!p.equals(directory)) {
                    tryDelete(directory);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Attempt to delete the specified path.
     *
     * <p> If the path is a directory, this method will attempt to recursively delete all of the directory contents as
     * well. </p>
     *
     * @param path The path to delete.
     *
     * @throws IOException for input/output errors
     */
    public static void delete(Object path)
            throws IOException {
        if (path == null) {
            throw new IOException("Path required.");
        }
        Path p = toPath(path);
        if (isValidDirectory(p)) {
            if (isRoot(p)) {
                throw new IOException("Attempt to delete a root directory.");
            }
            Files.walkFileTree(p, new SimpleFileVisitor<>() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    tryDelete(file);
                    return FileVisitResult.CONTINUE;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public FileVisitResult postVisitDirectory(Path directory, IOException error) {
                    tryDelete(directory);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            tryDelete(p);
        }
    }

    /**
     * Attempt to delete the specified path without generating errors.
     *
     * @param path The path.
     */
    private static void tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (Exception error) {
            LoggerFactory.getLogger(IO.class).error("Could not delete path: {}", path, error);
        }
    }

    /**
     * Get the URI representation of the specified archive file.
     *
     * @param archiveFile The path to the archive file.
     *
     * @return The URI representation of the specified archive file.
     */
    private static URI getArchiveFileURI(Path archiveFile) {
        String uriText = archiveFile.toUri().toString()
                .replaceAll("!.*", "")
                .replaceAll("^[^/]*:", "");
        if ((OS.isWindows()) && (uriText.startsWith("/"))) {
            uriText = uriText.substring(1);
        }
        uriText = "jar:file:" + ((OS.isWindows()) ? "/" : "") + uriText;
        uriText = uriText.replaceAll("\\\\", "/");
        return URI.create(uriText);
    }

    /**
     * Get the file system for the specified archive file.
     *
     * @param archiveFile The path to the archive file.
     * @param allowCreate A flag indicating whether the archive should be created if it does not exist.
     *
     * @return The specified archive file system.
     *
     * @throws IOException for input/output errors
     */
    private static FileSystem getArchiveFileSystem(Path archiveFile, boolean allowCreate)
            throws IOException {
        FileSystem fileSystem;
        if (exists(archiveFile)) {
            fileSystem = FileSystems.newFileSystem(archiveFile, ClassLoader.getSystemClassLoader());
        } else {
            if (allowCreate) {
                fileSystem = FileSystems.newFileSystem(getArchiveFileURI(archiveFile),
                        Bags.staticMap(Keyed.of("create", "true")));
            } else {
                throw new FileNotFoundException(Strings.toString(archiveFile));
            }
        }
        return fileSystem;
    }

    /**
     * Perform an action in an archive file system.
     *
     * @param archiveFile The path to the archive file.
     * @param action The activity to perform for the archive's root path.
     *
     * @throws IOException for input/output errors
     */
    public static void inArchive(Path archiveFile, ArchiveAction action)
            throws IOException {
        inArchive(archiveFile, false, action);
    }

    /**
     * Perform an action in an archive file system.
     *
     * @param archiveFile The path to the archive file.
     * @param allowCreate A flag indicating whether the archive should be created if it does not exist.
     * @param action The activity to perform for the archive's root path.
     *
     * @throws IOException for input/output errors
     */
    public static void inArchive(Path archiveFile, boolean allowCreate, ArchiveAction action)
            throws IOException {
        try (FileSystem fileSystem = getArchiveFileSystem(archiveFile, allowCreate)) {
            // Only a single root path will exist in an archive file.
            Path archiveRoot = fileSystem.getRootDirectories().iterator().next();
            if (archiveRoot == null) {
                throw new FileNotFoundException("No root path for archive: " + archiveFile);
            }
            try {
                action.withRoot(archiveRoot);
            } catch (Exception error) {
                throw new IOException("Archive action failed.", error);
            }
        }
    }

    /**
     * The interface containing the method to perform within an archive.
     */
    @FunctionalInterface
    public interface ArchiveAction {
        /**
         * Perform the desired actions in the archive.
         *
         * @param archiveRoot The root path for the archive.
         *
         * @throws Exception any exception
         */
        void withRoot(Path archiveRoot)
                throws Exception;
    }

    /**
     * Constructor.
     */
    private IO() {
        super();
    }
}