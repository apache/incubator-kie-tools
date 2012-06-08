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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.SimplePath;
import org.drools.guvnor.vfs.VFSService;
import org.drools.guvnor.vfs.impl.BasicAttributesVO;
import org.drools.guvnor.vfs.impl.DirectoryStreamImpl;
import org.drools.guvnor.vfs.impl.PathImpl;
import org.drools.java.nio.IOException;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.OpenOption;
import org.drools.java.nio.file.Paths;
import org.drools.java.nio.file.PatternSyntaxException;
import org.drools.java.nio.file.attribute.BasicFileAttributeView;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileTime;
import org.drools.java.nio.file.attribute.UserPrincipal;
import org.drools.java.nio.fs.base.GeneralFileAttributes;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class VFSServicesServerImpl implements VFSService {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public Path get(final String first, final String... more) throws IllegalArgumentException {
        return convert(Paths.get(first, more));
    }

    @Override
    public Path get(final SimplePath path) throws IllegalArgumentException {
        return convert(Paths.get(URI.create(path.toURI())));
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream()
            throws IllegalArgumentException, NotDirectoryException, IOException {
        return newDirectoryStream(Files.newDirectoryStream(Paths.get(System.getProperty("user.home"))).iterator());
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(final SimplePath dir) throws IllegalArgumentException, NotDirectoryException, IOException {
        return newDirectoryStream(Files.newDirectoryStream(fromSimplePath(dir)).iterator());
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(SimplePath dir, String glob) throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(SimplePath dir, DirectoryStream.Filter<? super SimplePath> filter) throws IllegalArgumentException, NotDirectoryException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createFile(SimplePath path, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createDirectory(SimplePath dir, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createDirectories(SimplePath dir, FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createSymbolicLink(SimplePath link, SimplePath target, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createLink(SimplePath link, SimplePath existing) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(SimplePath path) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean deleteIfExists(SimplePath path) throws IllegalArgumentException, DirectoryNotEmptyException, IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempFile(SimplePath dir, String prefix, String suffix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempDirectory(SimplePath dir, String prefix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempDirectory(String prefix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path copy(SimplePath source, SimplePath target, CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path move(SimplePath source, SimplePath target, CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path readSymbolicLink(SimplePath link) throws IllegalArgumentException, UnsupportedOperationException, NotLinkException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String probeContentType(SimplePath path) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(SimplePath path, Class<A> type) throws IllegalArgumentException, UnsupportedOperationException, NoSuchFileException, IOException {
        return convert(Files.readAttributes(fromSimplePath(path), type), type);
    }

    @Override
    public Map<String, Object> readAttributes(SimplePath path, String attributes, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setAttribute(SimplePath path, String attribute, Object value, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getAttribute(SimplePath path, String attribute, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserPrincipal getOwner(SimplePath path, LinkOption... options) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setOwner(SimplePath path, UserPrincipal owner) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setLastModifiedTime(SimplePath path, FileTime time) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long size(SimplePath path) throws IllegalArgumentException, IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean notExists(SimplePath path, LinkOption... options) throws IllegalArgumentException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSameFile(SimplePath path, SimplePath path2) throws IllegalArgumentException, IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isExecutable(SimplePath path) throws IllegalArgumentException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte[] readAllBytes(SimplePath path) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String readAllString(SimplePath path, String charset) throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString(path, Charset.forName(charset));
    }

    @Override
    public String readAllString(final SimplePath path) throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString(path, UTF_8);
    }

    private String readAllString(final SimplePath path, final Charset cs)
            throws IllegalArgumentException, NoSuchFileException, IOException {

        final List<String> result = Files.readAllLines(fromSimplePath(path), cs);
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
    public List<String> readAllLines(SimplePath path, String charset) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> readAllLines(SimplePath path) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path write(SimplePath path, byte[] bytes, OpenOption... options) throws IOException, UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path write(SimplePath path, Iterable<? extends CharSequence> lines, String charset, OpenOption... options) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path write(SimplePath path, Iterable<? extends CharSequence> lines, OpenOption... options) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path write(SimplePath path, String content, String charset, OpenOption... options) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return convert(Files.write(fromSimplePath(path), content, Charset.forName(charset), options));
    }

    @Override
    public Path write(SimplePath path, String content, OpenOption... options) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return convert(Files.write(fromSimplePath(path), content, UTF_8, options));
    }

    private <A extends BasicFileAttributes> A convert(final A value, final Class<A> destType) {
        if (destType.equals(BasicFileAttributes.class)) {
            final boolean isRegularFile = value.isRegularFile();
            final boolean isDirectory = value.isDirectory();
            final boolean isOther = value.isOther();
            final boolean isSymbolicLink = value.isSymbolicLink();
            final Object fileKey = value.fileKey();
            final FileTime creationTime = value.creationTime();
            final FileTime lastAccessTime = value.lastAccessTime();
            final FileTime lastModifiedTime = value.lastModifiedTime();
            final long fileLenght = value.size();

            if (value instanceof GeneralFileAttributes) {
                final GeneralFileAttributes generalValue = (GeneralFileAttributes) value;
                final boolean exists = generalValue.exists();
                final boolean isReadable = generalValue.isReadable();
                final boolean isExecutable = generalValue.isExecutable();
                final boolean isHidden = generalValue.isHidden();
                return (A) new BasicAttributesVO(isRegularFile, isDirectory, isOther, isSymbolicLink,
                        fileKey, creationTime, lastAccessTime, lastModifiedTime, fileLenght,
                        exists, isReadable, isExecutable, isHidden);
            }
            return (A) new BasicAttributesVO(isRegularFile, isDirectory, isOther, isSymbolicLink,
                    fileKey, creationTime, lastAccessTime, lastModifiedTime, fileLenght);
        }
        return value;
    }

    private Path convert(final org.drools.java.nio.file.Path path) {
        final BasicFileAttributeView attributes = Files.getFileAttributeView(path, BasicFileAttributeView.class);

        return new PathImpl(path.getFileName().toString(),
                path.toUri().toString(),
                Files.exists(path),
                attributes.readAttributes().isRegularFile(),
                attributes.readAttributes().isDirectory(),
                path.isAbsolute(),
                attributes.readAttributes().isSymbolicLink(),
                Files.isReadable(path),
                Files.isWritable(path),
                Files.isHidden(path),
                attributes.readAttributes().lastModifiedTime());
    }

    private DirectoryStream<Path> newDirectoryStream(final Iterator<org.drools.java.nio.file.Path> iterator) {
        final List<Path> content = new LinkedList<Path>();
        while (iterator.hasNext()) {
            content.add(convert(iterator.next()));
        }
        return new DirectoryStreamImpl(content);
    }

    private org.drools.java.nio.file.Path fromSimplePath(final SimplePath path) {
        return Paths.get(URI.create(path.toURI()));
    }
}
