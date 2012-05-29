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

package org.drools.guvnor.server.impl;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.vfs.DirectoryStreamVO;
import org.drools.guvnor.vfs.ExtendedPathVO;
import org.drools.guvnor.vfs.VFSService;
import org.drools.java.nio.IOException;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.ExtendedPath;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.FileSystemNotFoundException;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.OpenOption;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.Paths;
import org.drools.java.nio.file.PatternSyntaxException;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileTime;
import org.drools.java.nio.file.attribute.UserPrincipal;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class VFSServicesServerImpl implements VFSService {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public ExtendedPath get(final String first, final String... more)
            throws IllegalArgumentException {
        return new ExtendedPathVO(Paths.extend(Paths.get(first, more)));
    }

    @Override
    public ExtendedPath get(final URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException {
        return new ExtendedPathVO(Paths.extend(Paths.get(uri)));
    }

    @Override public DirectoryStream<ExtendedPath> newDirectoryStream() throws IllegalArgumentException, NotDirectoryException, IOException {
        return new DirectoryStreamVO((DirectoryStream<ExtendedPath>) Files.newDirectoryStream(Paths.get(System.getProperty("user.home"))));
    }

    @Override
    public DirectoryStream<ExtendedPath> newDirectoryStream(String dir) throws IllegalArgumentException, NotDirectoryException, IOException {
        return new DirectoryStreamVO((DirectoryStream<ExtendedPath>) Files.newDirectoryStream(Paths.get(dir)));
    }

    @Override
    public DirectoryStream<ExtendedPath> newDirectoryStream(final Path dir)
            throws IllegalArgumentException, NotDirectoryException, IOException {
        return new DirectoryStreamVO((DirectoryStream<ExtendedPath>) Files.newDirectoryStream(dir));
    }

    @Override
    public DirectoryStream<ExtendedPath> newDirectoryStream(final Path dir, final String glob)
            throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException {
        return new DirectoryStreamVO((DirectoryStream<ExtendedPath>) Files.newDirectoryStream(dir, glob));
    }

    @Override
    public DirectoryStream<ExtendedPath> newDirectoryStream(final Path dir, final DirectoryStream.Filter<? super Path> filter)
            throws IllegalArgumentException, NotDirectoryException, IOException {
        return new DirectoryStreamVO((DirectoryStream<ExtendedPath>) Files.newDirectoryStream(dir, filter));
    }

    @Override
    public ExtendedPath createFile(final Path path, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createFile(path, attrs)));
    }

    @Override
    public ExtendedPath createDirectory(final Path dir, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createDirectory(dir, attrs)));
    }

    @Override
    public ExtendedPath createDirectories(final Path dir, final FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createDirectories(dir, attrs)));
    }

    @Override
    public ExtendedPath createSymbolicLink(final Path link, final Path target, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createSymbolicLink(link, target, attrs)));
    }

    @Override
    public ExtendedPath createLink(final Path link, final Path existing)
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createLink(link, existing)));
    }

    @Override
    public void delete(final Path path)
            throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException {
        Files.delete(path);
    }

    @Override
    public boolean deleteIfExists(final Path path)
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException {
        return Files.deleteIfExists(path);
    }

    @Override
    public ExtendedPath createTempFile(final Path dir, final String prefix, final String suffix, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createTempFile(dir, prefix, suffix, attrs)));
    }

    @Override
    public ExtendedPath createTempFile(final String prefix, final String suffix, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createTempFile(prefix, suffix, attrs)));
    }

    @Override
    public ExtendedPath createTempDirectory(final Path dir, final String prefix, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createTempDirectory(dir, prefix, attrs)));
    }

    @Override
    public ExtendedPath createTempDirectory(final String prefix, final FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.createTempDirectory(prefix, attrs)));
    }

    @Override
    public ExtendedPath copy(final Path source, final Path target, final CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.copy(source, target, options)));
    }

    @Override
    public ExtendedPath move(final Path source, final Path target, final CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.move(source, target, options)));
    }

    @Override
    public ExtendedPath readSymbolicLink(final Path link)
            throws IllegalArgumentException, UnsupportedOperationException, NotLinkException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.readSymbolicLink(link)));
    }

    @Override
    public String probeContentType(final Path path)
            throws UnsupportedOperationException, IOException {
        return Files.probeContentType(path);
    }

//    @Override
//    public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options)
//            throws IllegalArgumentException {
//        return Files.getFileAttributeView(path, type, options);
//    }
//
//    @Override
//    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options)
//            throws IllegalArgumentException, UnsupportedOperationException, IOException {
//        return Files.readAttributes(path, type, options);
//    }

    @Override
    public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return Files.readAttributes(path, attributes, options);
    }

    @Override
    public ExtendedPath setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.setAttribute(path, attribute, value, options)));
    }

    @Override
    public Object getAttribute(final Path path, final String attribute, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return Files.getAttribute(path, attribute, options);
    }

    @Override
    public UserPrincipal getOwner(final Path path, final LinkOption... options)
            throws UnsupportedOperationException, IOException {
        return Files.getOwner(path, options);
    }

    @Override
    public ExtendedPath setOwner(final Path path, final UserPrincipal owner)
            throws UnsupportedOperationException, IOException {
        return new ExtendedPathVO(Paths.extend(Files.setOwner(path, owner)));
    }

    @Override
    public FileTime getLastModifiedTime(final Path path, final LinkOption... options)
            throws IllegalArgumentException, IOException {
        return Files.getLastModifiedTime(path, options);
    }

    @Override
    public ExtendedPath setLastModifiedTime(final Path path, final FileTime time)
            throws IOException {
        return new ExtendedPathVO(Paths.extend(Files.setLastModifiedTime(path, time)));
    }

    @Override
    public long size(final Path path)
            throws IllegalArgumentException, IOException {
        return Files.size(path);
    }

    @Override
    public boolean exists(final Path path, final LinkOption... options)
            throws IllegalArgumentException {
        return Files.exists(path, options);
    }

    @Override
    public boolean notExists(final Path path, final LinkOption... options)
            throws IllegalArgumentException {
        return Files.notExists(path, options);
    }

    @Override
    public boolean isSameFile(final Path path, final Path path2)
            throws IllegalArgumentException, IOException {
        return Files.isSameFile(path, path2);
    }

    @Override
    public boolean isHidden(final Path path)
            throws IllegalArgumentException, IOException {
        return Files.isHidden(path);
    }

    @Override
    public boolean isReadable(final Path path)
            throws IllegalArgumentException {
        return Files.isReadable(path);
    }

    @Override
    public boolean isWritable(final Path path)
            throws IllegalArgumentException {
        return Files.isWritable(path);
    }

    @Override
    public boolean isExecutable(final Path path)
            throws IllegalArgumentException {
        return Files.isExecutable(path);
    }

    @Override
    public boolean isSymbolicLink(final Path path)
            throws IllegalArgumentException {
        return Files.isSymbolicLink(path);
    }

    @Override
    public boolean isDirectory(final Path path, final LinkOption... options)
            throws IllegalArgumentException {
        return Files.isDirectory(path, options);
    }

    @Override
    public boolean isRegularFile(final Path path, final LinkOption... options)
            throws IllegalAccessError {
        return Files.isRegularFile(path, options);
    }

    @Override
    public byte[] readAllBytes(final Path path)
            throws IOException, OutOfMemoryError {
        return Files.readAllBytes(path);
    }

    @Override
    public String readAllString(final Path path, final Charset cs)
            throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String readAllString(final Path path)
            throws IllegalArgumentException, NoSuchFileException, IOException {

        final Path realPath = Paths.get(((ExtendedPath) path).toUriAsString());

        final List<String> result = Files.readAllLines(realPath, UTF_8);
        if (result == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final String s : result) {
            sb.append(s).append('\n');
        }
        return sb.toString();
    }

    @Override
    public List<String> readAllLines(final Path path, final Charset cs)
            throws IllegalArgumentException, NoSuchFileException, IOException {
        return Files.readAllLines(path, cs);
    }

    @Override
    public List<String> readAllLines(final Path path)
            throws IllegalArgumentException, NoSuchFileException, IOException {
        return Files.readAllLines(path, UTF_8);
    }

    @Override
    public ExtendedPath write(final Path path, final byte[] bytes, final OpenOption... options)
            throws IOException, UnsupportedOperationException {
        return new ExtendedPathVO(Paths.extend(Files.write(path, bytes, options)));
    }

    @Override
    public ExtendedPath write(final Path path, final Iterable<? extends CharSequence> lines, final Charset cs, final OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new ExtendedPathVO(Paths.extend(Files.write(path, lines, cs, options)));
    }

    @Override
    public ExtendedPath write(final Path path, final Iterable<? extends CharSequence> lines, final OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new ExtendedPathVO(Paths.extend(Files.write(path, lines, UTF_8, options)));
    }

    @Override
    public ExtendedPath write(final Path path, final String content, final Charset cs, final OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new ExtendedPathVO(Paths.extend(Files.write(path, content.getBytes(cs), options)));
    }

    @Override
    public ExtendedPath write(final Path path, final String content, final OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new ExtendedPathVO(Paths.extend(Files.write(path, content.getBytes(), options)));
    }
}
