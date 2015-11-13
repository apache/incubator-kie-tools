/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.editor.commons.backend.version;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.*;
import org.uberfire.java.nio.file.InterruptedException;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;

public class MockIOService
        implements IOService {

    private ArrayList<Path> paths = new ArrayList<Path>();

    public void setExistingPaths(Path... paths) {
        for (Path path : paths) {
            this.paths.add(path);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void startBatch(FileSystem fileSystem) throws org.uberfire.java.nio.file.InterruptedException {

    }

    @Override
    public void startBatch(FileSystem[] fileSystems, Option... options) throws InterruptedException {

    }

    @Override
    public void startBatch(FileSystem fileSystem, Option... options) throws InterruptedException {

    }

    @Override
    public void startBatch(FileSystem... fileSystems) throws InterruptedException {

    }

    @Override
    public void endBatch() {

    }

    @Override
    public FileAttribute<?>[] convert(Map<String, ?> stringMap) {
        return new FileAttribute<?>[0];
    }

    @Override
    public Path get(String s, String... strings) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Path get(URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return null;
    }

    @Override
    public Iterable<FileSystem> getFileSystems() {
        return null;
    }

    @Override
    public FileSystem getFileSystem(URI uri) throws IllegalArgumentException, FileSystemNotFoundException, ProviderNotFoundException, SecurityException {
        return null;
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> stringMap) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, IOException, SecurityException {
        return null;
    }

    @Override
    public void onNewFileSystem(NewFileSystemListener newFileSystemListener) {

    }

    @Override
    public InputStream newInputStream(Path path, OpenOption... openOptions) throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public OutputStream newOutputStream(Path path, OpenOption... openOptions) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, OpenOption... openOptions) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> openOptions, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path path) throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path path, DirectoryStream.Filter<Path> pathFilter) throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createFile(Path path, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createDirectory(Path path, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createDirectories(Path path, FileAttribute<?>... fileAttributes) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createDirectory(Path path, Map<String, ?> stringMap) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createDirectories(Path path, Map<String, ?> stringMap) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return null;
    }

    @Override
    public void delete(Path path, DeleteOption... deleteOptions) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {

    }

    @Override
    public boolean deleteIfExists(Path path, DeleteOption... deleteOptions) throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        return false;
    }

    @Override
    public Path createTempFile(String s, String s2, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createTempFile(Path path, String s, String s2, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createTempDirectory(String s, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path createTempDirectory(Path path, String s, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path copy(Path path, Path path2, CopyOption... copyOptions) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path move(Path path, Path path2, CopyOption... copyOptions) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        return null;
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> vClass) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Map<String, Object> readAttributes(Path path) throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        return null;
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String s) throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path setAttributes(Path path, FileAttribute<?>... fileAttributes) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path setAttributes(Path path, Map<String, Object> stringObjectMap) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return null;
    }

    @Override
    public Path setAttribute(Path path, String s, Object o) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return null;
    }

    @Override
    public Object getAttribute(Path path, String s) throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        return null;
    }

    @Override
    public FileTime getLastModifiedTime(Path path) throws IllegalArgumentException, IOException, SecurityException {
        return null;
    }

    @Override
    public long size(Path path) throws IllegalArgumentException, IOException, SecurityException {
        return 0;
    }

    @Override
    public boolean exists(Path path) throws IllegalArgumentException, SecurityException {
        return paths.contains(path);
    }

    @Override
    public boolean notExists(Path path) throws IllegalArgumentException, SecurityException {
        return false;
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IllegalArgumentException, IOException, SecurityException {
        return false;
    }

    @Override
    public BufferedReader newBufferedReader(Path path, Charset charset) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return null;
    }

    @Override
    public BufferedWriter newBufferedWriter(Path path, Charset charset, OpenOption... openOptions) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    @Override
    public long copy(InputStream inputStream, Path path, CopyOption... copyOptions) throws IOException, FileAlreadyExistsException, DirectoryNotEmptyException, UnsupportedOperationException, SecurityException {
        return 0;
    }

    @Override
    public long copy(Path path, OutputStream outputStream) throws IOException, SecurityException {
        return 0;
    }

    @Override
    public byte[] readAllBytes(Path path) throws IOException, OutOfMemoryError, SecurityException {
        return new byte[0];
    }

    @Override
    public List<String> readAllLines(Path path) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return null;
    }

    @Override
    public List<String> readAllLines(Path path, Charset charset) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return null;
    }

    @Override
    public String readAllString(Path path, Charset charset) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;
    }

    @Override
    public String readAllString(Path path) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;
    }

    @Override
    public Path write(Path path, byte[] bytes, OpenOption... openOptions) throws IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    @Override
    public Path write(Path path, byte[] bytes, Map<String, ?> stringMap, OpenOption... openOptions) throws IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    @Override
    public Path write(Path path, byte[] bytes, Set<? extends OpenOption> openOptions, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> charSequences, Charset charset, OpenOption... openOptions) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return null;
    }

    @Override
    public Path write(Path path, String s, OpenOption... openOptions) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write(Path path, String s, Charset charset, OpenOption... openOptions) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write(Path path, String s, Set<? extends OpenOption> openOptions, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write(Path path, String s, Charset charset, Set<? extends OpenOption> openOptions, FileAttribute<?>... fileAttributes) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write(Path path, String s, Map<String, ?> stringMap, OpenOption... openOptions) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public Path write(Path path, String s, Charset charset, Map<String, ?> stringMap, OpenOption... openOptions) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public int priority() {
        return 0;
    }
}
