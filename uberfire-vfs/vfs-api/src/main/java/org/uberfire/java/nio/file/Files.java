/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileVisitOption;
import org.uberfire.java.nio.file.FileVisitor;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.NotLinkException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.file.attribute.PosixFilePermission;
import org.uberfire.java.nio.file.attribute.UserPrincipal;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static java.util.Collections.*;
import static org.uberfire.java.nio.file.AccessMode.*;
import static org.uberfire.java.nio.util.Preconditions.*;

/**
 * Back port of JSR-203 from Java Platform, Standard Edition 7.
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html">Original JavaDoc</a>
 */
public final class Files {

    private static final Set<StandardOpenOption> CREATE_NEW_FILE_OPTIONS = EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

    /**
     * Maximum loop count when creating temp directories.
     */
    private static final int TEMP_DIR_ATTEMPTS = 10000;

    private Files() {
    }

    // internal shortcut
    private static FileSystemProvider providerOf(final Path path) {
        return path.getFileSystem().provider();
    }

    //contents

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws NoSuchFileException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newInputStream(java.nio.file.Path, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static InputStream newInputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);

        return providerOf(path).newInputStream(path, options);
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newOutputStream(java.nio.file.Path, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static OutputStream newOutputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);

        return providerOf(path).newOutputStream(path, options);
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newByteChannel(java.nio.file.Path, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static SeekableByteChannel newByteChannel(final Path path, final OpenOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        checkNotNull("path", path);

        final Set<OpenOption> set = new HashSet<OpenOption>(options.length);
        addAll(set, options);
        return newByteChannel(path, set);
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newByteChannel(java.nio.file.Path, java.util.Set, java.nio.file.attribute.FileAttribute...)">Original JavaDoc</a>
     */
    public static SeekableByteChannel newByteChannel(final Path path,
            final Set<? extends OpenOption> options, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("options", options);

        return providerOf(path).newByteChannel(path, options, attrs);
    }

    //directories

    /**
     * @throws IllegalArgumentException
     * @throws NotDirectoryException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newDirectoryStream(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static DirectoryStream<Path> newDirectoryStream(final Path dir)
            throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        checkNotNull("dir", dir);

        return newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
            @Override public boolean accept(Path entry) throws IOException {
                return true;
            }
        });
    }

    //TODO impl
    public static DirectoryStream<Path> newDirectoryStream(final Path dir, final String glob)
            throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException, SecurityException {
        throw new UnsupportedOperationException("feature not available");
    }

    /**
     * @throws IllegalArgumentException
     * @throws NotDirectoryException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newDirectoryStream(java.nio.file.Path, java.nio.file.DirectoryStream.Filter)">Original JavaDoc</a>
     */
    public static DirectoryStream<Path> newDirectoryStream(final Path dir, final DirectoryStream.Filter<Path> filter)
            throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        checkNotNull("dir", dir);
        checkNotNull("filter", filter);

        return providerOf(dir).newDirectoryStream(dir, filter);
    }

    //creation and deletion

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#createFile(java.nio.file.Path, java.nio.file.attribute.FileAttribute...)">Original JavaDoc</a>
     */
    public static Path createFile(final Path path, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("path", path);

        try {
            newByteChannel(path, CREATE_NEW_FILE_OPTIONS, attrs).close();
        } catch (java.io.IOException e) {
            throw new IOException();
        }

        return path;
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#createDirectory(java.nio.file.Path, java.nio.file.attribute.FileAttribute...)">Original JavaDoc</a>
     */
    public static Path createDirectory(final Path dir, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("dir", dir);

        providerOf(dir).createDirectory(dir, attrs);

        return dir;
    }

    //TODO impl
    public static Path createDirectories(final Path dir, final FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#createSymbolicLink(java.nio.file.Path, java.nio.file.Path, java.nio.file.attribute.FileAttribute...)">Original JavaDoc</a>
     */
    public static Path createSymbolicLink(final Path link, final Path target, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("link", link);
        checkNotNull("target", target);

        providerOf(link).createSymbolicLink(link, target, attrs);

        return link;
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws FileAlreadyExistsException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#createLink(java.nio.file.Path, java.nio.file.Path)">Original JavaDoc</a>
     */
    public static Path createLink(final Path link, final Path existing)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("link", link);
        checkNotNull("existing", existing);

        providerOf(link).createLink(link, existing);

        return link;
    }

    /**
     * @throws IllegalArgumentException
     * @throws NoSuchFileException
     * @throws DirectoryNotEmptyException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#delete(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static void delete(final Path path)
            throws IllegalArgumentException, NoSuchFileException,
            DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("path", path);

        providerOf(path).delete(path);
    }

    /**
     * @throws IllegalArgumentException
     * @throws DirectoryNotEmptyException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#deleteIfExists(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean deleteIfExists(final Path path)
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("path", path);

        return providerOf(path).deleteIfExists(path);
    }

    //temp related

    public static Path createTempFile(final Path dir, final String prefix,
            final String suffix, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        try {
            return Paths.get(File.createTempFile(prefix, suffix, dir.toFile()).toURI());
        } catch (java.io.IOException e) {
            throw new IOException();
        }
    }

    //TODO impl
    public static Path createTempFile(final String prefix, final String suffix, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        try {
            return Paths.get(File.createTempFile(prefix, suffix).toURI());
        } catch (java.io.IOException e) {
            throw new IOException();
        }
    }

    //TODO impl
    public static Path createTempDirectory(final Path dir, final String prefix, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException("feature not available");
    }

    //implemantation based on google's guava lib
    public static Path createTempDirectory(final String prefix, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {

        final File baseDir = new File(System.getProperty("java.io.tmpdir"));
        final String baseName = prefix + "-" + System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            final File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return Paths.get(tempDir.toURI());
            }
        }
        throw new IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }

    //copying and moving

    //TODO impl
    public static Path copy(final Path source, final Path target, final CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException, SecurityException {
        throw new UnsupportedOperationException("feature not available");
    }

    //TODO impl
    public static Path move(final Path source, final Path target, final CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException,
            AtomicMoveNotSupportedException, IOException, SecurityException {
        throw new UnsupportedOperationException("feature not available");
    }

    //misc

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws NotLinkException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#readSymbolicLink(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static Path readSymbolicLink(final Path link)
            throws IllegalArgumentException, UnsupportedOperationException,
            NotLinkException, IOException, SecurityException {
        checkNotNull("link", link);

        return providerOf(link).readSymbolicLink(link);
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#getFileStore(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static FileStore getFileStore(final Path path)
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);

        return providerOf(path).getFileStore(path);
    }

    //TODO impl
    public static String probeContentType(final Path path)
            throws UnsupportedOperationException, IOException, SecurityException {
        throw new UnsupportedOperationException("feature not available");
    }

    //attributes

    /**
     * @throws IllegalArgumentException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#getFileAttributeView(java.nio.file.Path, java.lang.Class, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static <V extends FileAttributeView> V getFileAttributeView(final Path path,
            final Class<V> type, final LinkOption... options)
            throws IllegalArgumentException {
        checkNotNull("path", path);
        checkNotNull("type", type);

        return providerOf(path).getFileAttributeView(path, type, options);
    }

    /**
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#getFileAttributeView(java.nio.file.Path, java.lang.Class, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static <A extends BasicFileAttributes> A readAttributes(final Path path,
            final Class<A> type, final LinkOption... options)
            throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("type", type);

        return providerOf(path).readAttributes(path, type, options);
    }

    /**
     * @throws UnsupportedOperationException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#readAttributes(java.nio.file.Path, java.lang.String, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotEmpty("attributes", attributes);

        return providerOf(path).readAttributes(path, attributes, options);
    }

    /**
     * @throws UnsupportedOperationException
     * @throws IllegalArgumentException
     * @throws ClassCastException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#setAttribute(java.nio.file.Path, java.lang.String, java.lang.Object, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static Path setAttribute(final Path path, final String attribute,
            final Object value, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotEmpty("attribute", attribute);

        providerOf(path).setAttribute(path, attribute, value, options);

        return path;
    }

    //TODO impl
    public static Object getAttribute(final Path path, final String attribute, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        throw new UnsupportedOperationException("feature not available");
    }

    //TODO impl
    public static Set<PosixFilePermission> getPosixFilePermissions(final Path path, final LinkOption... options)
            throws UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    //TODO impl
    public static Path setPosixFilePermissions(final Path path, final Set<PosixFilePermission> perms)
            throws UnsupportedOperationException, ClassCastException, IOException, SecurityException {
        return null;
    }

    //TODO impl
    public static UserPrincipal getOwner(final Path path, final LinkOption... options)
            throws UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    //TODO impl
    public static Path setOwner(final Path path, final UserPrincipal owner)
            throws UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#getLastModifiedTime(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static FileTime getLastModifiedTime(final Path path, final LinkOption... options)
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);

        return readAttributes(path, BasicFileAttributes.class, options).lastModifiedTime();
    }

    //TODO impl
    public static Path setLastModifiedTime(final Path path, final FileTime time)
            throws IOException, SecurityException {
        return null;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#size(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static long size(final Path path)
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);

        return readAttributes(path, BasicFileAttributes.class).size();
    }

    //accessibility

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#exists(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static boolean exists(final Path path, final LinkOption... options)
            throws IllegalArgumentException, SecurityException {
        checkNotNull("path", path);

        try {
            readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            return true;
        } catch (Exception x) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#notExists(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static boolean notExists(final Path path, final LinkOption... options)
            throws IllegalArgumentException, SecurityException {
        checkNotNull("path", path);

        try {
            readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            return false;
        } catch (NoSuchFileException x) {
            return true;
        } catch (Exception x) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isSameFile(java.nio.file.Path, java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isSameFile(final Path path, final Path path2)
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("path2", path2);

        return providerOf(path).isSameFile(path, path2);
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isHidden(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isHidden(final Path path)
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);

        return providerOf(path).isHidden(path);
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isReadable(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isReadable(final Path path) throws
            IllegalArgumentException, SecurityException {
        checkNotNull("path", path);

        try {
            providerOf(path).checkAccess(path, READ);
            return true;
        } catch (Exception x) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isWritable(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isWritable(final Path path)
            throws IllegalArgumentException, SecurityException {
        checkNotNull("path", path);

        try {
            providerOf(path).checkAccess(path, WRITE);
            return true;
        } catch (Exception x) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isExecutable(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isExecutable(final Path path)
            throws IllegalArgumentException, SecurityException {
        checkNotNull("path", path);

        try {
            providerOf(path).checkAccess(path, EXECUTE);
            return true;
        } catch (Exception x) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isSymbolicLink(java.nio.file.Path)">Original JavaDoc</a>
     */
    public static boolean isSymbolicLink(final Path path)
            throws IllegalArgumentException, SecurityException {
        checkNotNull("path", path);

        try {
            return readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS).isSymbolicLink();
        } catch (Exception ioe) {
        }
        return false;
    }

    /**
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isDirectory(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static boolean isDirectory(final Path path, final LinkOption... options)
            throws IllegalArgumentException, SecurityException {
        checkNotNull("path", path);

        try {
            return readAttributes(path, BasicFileAttributes.class, options).isDirectory();
        } catch (IOException ioe) {
        }
        return false;
    }

    /**
     * @throws IllegalAccessError
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#isRegularFile(java.nio.file.Path, java.nio.file.LinkOption...)">Original JavaDoc</a>
     */
    public static boolean isRegularFile(final Path path, final LinkOption... options)
            throws IllegalAccessError, SecurityException {
        checkNotNull("path", path);

        try {
            return readAttributes(path, BasicFileAttributes.class, options).isRegularFile();
        } catch (IOException ioe) {
        }
        return false;
    }

    //recursive operations

    //TODO impl
    public static Path walkFileTree(final Path start, final Set<FileVisitOption> options,
            final int maxDepth, final FileVisitor<Path> visitor)
            throws IllegalArgumentException, SecurityException, IOException {
        new FileTreeWalker(visitor, maxDepth).walk(start);

        return start;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#walkFileTree(java.nio.file.Path, java.nio.file.FileVisitor)">Original JavaDoc</a>
     */
    public static Path walkFileTree(final Path start, final FileVisitor<Path> visitor)
            throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("start", start);
        checkNotNull("visitor", visitor);

        final Set<FileVisitOption> options = emptySet();

        return walkFileTree(start, options, Integer.MAX_VALUE, visitor);
    }

    //utility methods - simple cases

    /**
     * @throws IllegalArgumentException
     * @throws NoSuchFileException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newBufferedReader(java.nio.file.Path, java.nio.charset.Charset)">Original JavaDoc</a>
     */
    public static BufferedReader newBufferedReader(final Path path, final Charset cs)
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("cs", cs);

        final Reader reader = new InputStreamReader(newInputStream(path), cs.newDecoder());
        return new BufferedReader(reader);
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#newBufferedWriter(java.nio.file.Path, java.nio.charset.Charset, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static BufferedWriter newBufferedWriter(final Path path,
            final Charset cs, final OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("cs", cs);

        try {
            final Writer writer = new OutputStreamWriter(newOutputStream(path, options), cs.name());
            return new BufferedWriter(writer);
        } catch (UnsupportedEncodingException e) {
            throw new IOException();
        }
    }

    //TODO impl
    public static long copy(final InputStream in, final Path target, final CopyOption... options)
            throws IOException, FileAlreadyExistsException, DirectoryNotEmptyException,
            UnsupportedOperationException, SecurityException {
        return -1;
    }

    //TODO impl
    public static long copy(final Path source, final OutputStream out)
            throws IOException, SecurityException {
        return -1;
    }

    //TODO impl
    public static byte[] readAllBytes(final Path path)
            throws IOException, OutOfMemoryError, SecurityException {
        return null;
    }

    /**
     * @throws IllegalArgumentException
     * @throws NoSuchFileException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#readAllLines(java.nio.file.Path, java.nio.charset.Charset)">Original JavaDoc</a>
     */
    public static List<String> readAllLines(final Path path, final Charset cs)
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("cs", cs);

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = newBufferedReader(path, cs);
            final List<String> result = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
            return result;
        } catch (java.io.IOException ex) {
            throw new IOException();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (java.io.IOException e) {
                    throw new IOException();
                }
            }
        }
    }

    //TODO impl
    public static Path write(final Path path, final byte[] bytes, final OpenOption... options)
            throws IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#write(java.nio.file.Path, java.lang.Iterable, java.nio.charset.Charset, java.nio.file.OpenOption...)">Original JavaDoc</a>
     */
    public static Path write(final Path path,
            final Iterable<? extends CharSequence> lines, final Charset cs, final OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("cs", cs);
        checkNotNull("lines", lines);

        final CharsetEncoder encoder = cs.newEncoder();
        final OutputStream out = newOutputStream(path, options);

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, encoder));
            for (final CharSequence line : lines) {
                try {
                    bufferedWriter.append(line);
                    bufferedWriter.newLine();
                } catch (java.io.IOException e) {
                    throw new IOException();
                }
            }
        } catch (final IOException ex) {
            throw ex;
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (java.io.IOException e) {
                    throw new IOException();
                }
            }
        }

        return path;
    }

    /**
     * This is a non standard method to write a string.
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws SecurityException
     */
    public static Path write(final Path path,
            final String content, final Charset cs, final OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("content", content);
        checkNotNull("cs", cs);

        final CharsetEncoder encoder = cs.newEncoder();
        final OutputStream out = newOutputStream(path, options);

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, encoder));
            try {
                bufferedWriter.write(content);
            } catch (java.io.IOException e) {
                throw new IOException();
            }
        } catch (final IOException ex) {
            throw ex;
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (java.io.IOException e) {
                    throw new IOException();
                }
            }
        }

        return path;
    }

}
