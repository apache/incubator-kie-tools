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

package org.drools.guvnor.vfs;

import java.util.List;
import java.util.Map;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.OpenOption;
import org.drools.java.nio.file.PatternSyntaxException;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileTime;
import org.drools.java.nio.file.attribute.UserPrincipal;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface VFSService {

    Path get(final String first, final String... more)
            throws IllegalArgumentException;

    Path get(final SimplePath path)
            throws IllegalArgumentException;

    //TODO for demo purpose ONLY
    DirectoryStream<Path> newDirectoryStream()
            throws IllegalArgumentException, NotDirectoryException, IOException;

    DirectoryStream<Path> newDirectoryStream(SimplePath dir)
            throws IllegalArgumentException, NotDirectoryException, IOException;

    DirectoryStream<Path> newDirectoryStream(SimplePath dir, String glob)
            throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException;

    DirectoryStream<Path> newDirectoryStream(SimplePath dir, DirectoryStream.Filter<? super SimplePath> filter)
            throws IllegalArgumentException, NotDirectoryException, IOException;

    Path createFile(SimplePath path, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    Path createDirectory(SimplePath dir, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    Path createDirectories(SimplePath dir, FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException;

    Path createSymbolicLink(SimplePath link, SimplePath target, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    Path createLink(SimplePath link, SimplePath existing)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    void delete(SimplePath path)
            throws IllegalArgumentException, NoSuchFileException,
            DirectoryNotEmptyException, IOException;

    boolean deleteIfExists(SimplePath path)
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException;

    Path createTempFile(SimplePath dir, String prefix,
            String suffix, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;

    Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;

    Path createTempDirectory(SimplePath dir, String prefix, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;

    Path createTempDirectory(String prefix, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;

    Path copy(SimplePath source, SimplePath target, CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException;

    Path move(SimplePath source, SimplePath target, CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException,
            AtomicMoveNotSupportedException, IOException;

    Path readSymbolicLink(SimplePath link)
            throws IllegalArgumentException, UnsupportedOperationException,
            NotLinkException, IOException;

    String probeContentType(SimplePath path)
            throws UnsupportedOperationException, IOException;

//TODO commented for now - seems that it's an errai (codegen on generified return type)
//    <V extends FileAttributeView> V getFileAttributeView(SimplePath path,
//            Class<V> type, LinkOption... options)
//            throws IllegalArgumentException;
//
//    <A extends BasicFileAttributes> A readAttributes(SimplePath path,
//            Class<A> type, LinkOption... options)
//            throws IllegalArgumentException, UnsupportedOperationException, NoSuchFileException, IOException;

    Map<String, Object> readAttributes(SimplePath path, String attributes, LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException;

    Path setAttribute(SimplePath path, String attribute,
            Object value, LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException;

    Object getAttribute(SimplePath path, String attribute, LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException;

    UserPrincipal getOwner(SimplePath path, LinkOption... options)
            throws UnsupportedOperationException, IOException;

    Path setOwner(SimplePath path, UserPrincipal owner)
            throws UnsupportedOperationException, IOException;

    Path setLastModifiedTime(SimplePath path, FileTime time)
            throws IOException;

    long size(SimplePath path)
            throws IllegalArgumentException, IOException;

    boolean notExists(SimplePath path, LinkOption... options)
            throws IllegalArgumentException;

    boolean isSameFile(SimplePath path, SimplePath path2)
            throws IllegalArgumentException, IOException;

    boolean isExecutable(SimplePath path)
            throws IllegalArgumentException;

    byte[] readAllBytes(SimplePath path)
            throws IOException;

    String readAllString(SimplePath path, String charset)
            throws IllegalArgumentException, NoSuchFileException, IOException;

    String readAllString(SimplePath path)
            throws IllegalArgumentException, NoSuchFileException, IOException;

    List<String> readAllLines(SimplePath path, String charset)
            throws IllegalArgumentException, NoSuchFileException, IOException;

    List<String> readAllLines(SimplePath path)
            throws IllegalArgumentException, NoSuchFileException, IOException;

    Path write(SimplePath path, byte[] bytes, OpenOption... options)
            throws IOException, UnsupportedOperationException;

    Path write(SimplePath path,
            Iterable<? extends CharSequence> lines, String charset, OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write(SimplePath path,
            Iterable<? extends CharSequence> lines, OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write(SimplePath path,
            String content, String charset, OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write(SimplePath path,
            String content, OpenOption... options)
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

}
