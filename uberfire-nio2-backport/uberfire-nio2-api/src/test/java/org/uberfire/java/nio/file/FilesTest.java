/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;

import org.assertj.core.api.Assertions;
import org.junit.Assume;
import org.junit.Test;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.NotImplementedException;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FilesTest extends AbstractBaseTest {

    @Test
    public void newIOStreams() throws IOException {
        final Path dir = newTempDir();

        try (final OutputStream out = Files.newOutputStream(dir.resolve("file.txt"))) {
            assertThat(out).isNotNull();
            out.write("content".getBytes());
        }

        try (final InputStream in = Files.newInputStream(dir.resolve("file.txt"))) {

            assertThat(in).isNotNull();

            final StringBuilder sb = new StringBuilder();
            while (true) {
                int i = in.read();
                if (i == -1) {
                    break;
                }
                sb.append((char) i);
            }
            assertThat(sb.toString()).isEqualTo("content");
        }
    }

    @Test
    public void newInputStreamNonExistent() {
        assertThatThrownBy(() -> Files.newInputStream(Paths.get("/path/to/some/file.txt")))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void newInputStreamOnDir() {
        final Path dir = newTempDir();
        assertThatThrownBy(() -> Files.newInputStream(dir))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void newInputStreamNull() {
        assertThatThrownBy(() -> Files.newInputStream(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void newOutputStreamOnExistent() {
        final Path dir = newTempDir();

        assertThatThrownBy(() -> Files.newOutputStream(dir))
                .isInstanceOf(org.uberfire.java.nio.IOException.class)
                .hasMessage("Could not open output stream.");
    }

    @Test
    public void newOutpurStreamNull() {
        assertThatThrownBy(() -> Files.newOutputStream(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void newByteChannel() throws IOException {
        try (final SeekableByteChannel sbc = Files.newByteChannel(newTempDir().resolve("file.temp.txt"),
                                                                  new HashSet<>())) {
            assertThat(sbc).isNotNull();
        }

        try (final SeekableByteChannel sbc2 = Files.newByteChannel(newTempDir().resolve("file.temp2.txt"))) {
            assertThat(sbc2).isNotNull();
        }
    }

    @Test
    public void newByteChannelFileAlreadyExists() {
        assertThatThrownBy(() -> Files.newByteChannel(Files.createTempFile("foo", "bar")))
                .isInstanceOf(FileAlreadyExistsException.class);
    }

    @Test
    public void newByteChannelNull() {
        assertThatThrownBy(() -> Files.newByteChannel(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void createFile() {
        final Path path = Files.createFile(newTempDir().resolve("file.temp.txt"));

        assertThat(path).isNotNull();
        assertThat(path.toFile()).exists();
    }

    @Test
    public void createFileAlreadyExists() {
        assertThatThrownBy(() -> Files.createFile(Files.createTempFile("foo", "bar")))
                .isInstanceOf(FileAlreadyExistsException.class);
    }

    @Test
    public void createFileNull() {
        assertThatThrownBy(() -> Files.createFile(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void createDirectory() {
        final Path path = newTempDir();

        final Path dir = Files.createDirectory(path.resolve("myNewDir"));

        assertThat(dir).isNotNull();
        assertThat(dir.toFile()).exists();
        assertThat(dir.toFile()).isDirectory();

        final Path file = Files.createFile(dir.resolve("new.file.txt"));
        assertThat(file).isNotNull();
        assertThat(file.toFile()).exists();
        assertThat(file.toFile()).isFile();
    }

    @Test
    public void createDirectoryFileAlreadyExists() {
        assertThatThrownBy(() -> Files.createDirectory(newTempDir()))
                .isInstanceOf(FileAlreadyExistsException.class);
    }

    @Test
    public void createDirectoryNull() {
        assertThatThrownBy(() -> Files.createDirectory(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'dir' should be not null!");
    }

    @Test
    public void createDirectories() {
        final Path path = newTempDir();

        final Path dir = Files.createDirectories(path.resolve("myNewDir/mysubDir1/mysubDir2"));

        assertThat(dir).isNotNull();
        assertThat(dir.toFile()).exists();
        assertThat(dir.toFile()).isDirectory();

        final Path file = Files.createFile(dir.resolve("new.file.txt"));
        assertThat(file).isNotNull();
        assertThat(file.toFile()).exists();
        assertThat(file.toFile()).isFile();
    }

    @Test
    public void createDirectoriesFileAlreadyExists() {
        assertThatThrownBy(() -> Files.createDirectories(newTempDir()))
                .isInstanceOf(FileAlreadyExistsException.class);
    }

    @Test
    public void createDirectoriesNull() {
        assertThatThrownBy(() -> Files.createDirectories(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'dir' should be not null!");
    }

    @Test
    public void delete() {
        final Path path = Files.createFile(newTempDir().resolve("file.temp.txt"));

        assertThat(path).isNotNull();
        assertThat(path.toFile()).exists();

        Files.delete(path);

        assertThat(path).isNotNull();
        assertThat(path.toFile()).doesNotExist();

        final Path dir = newTempDir();

        assertThat(dir).isNotNull();
        assertThat(dir.toFile()).exists();

        Files.delete(dir);

        assertThat(dir).isNotNull();
        assertThat(dir.toFile()).doesNotExist();
    }

    @Test
    public void deleteDirectoryNotEmpty() {
        final Path dir = newTempDir();
        Files.createFile(dir.resolve("file.temp.txt"));

        assertThatThrownBy(() -> Files.delete(dir))
                .isInstanceOf(DirectoryNotEmptyException.class);
    }

    @Test
    public void deleteNoSuchFileException() {
        assertThatThrownBy(() -> Files.delete(newTempDir().resolve("file.temp.txt")))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void deleteNull() {
        assertThatThrownBy(() -> Files.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void deleteIfExists() {
        final Path path = Files.createFile(newTempDir().resolve("file.temp.txt"));

        assertThat(path).isNotNull();
        assertThat(path.toFile()).exists();

        assertThat(Files.deleteIfExists(path)).isTrue();

        assertThat(path).isNotNull();
        assertThat(path.toFile()).doesNotExist();

        final Path dir = newTempDir();

        assertThat(dir).isNotNull();
        assertThat(dir.toFile()).exists();

        assertThat(Files.deleteIfExists(dir)).isTrue();

        assertThat(dir).isNotNull();
        assertThat(dir.toFile()).doesNotExist();

        assertThat(Files.deleteIfExists(newTempDir().resolve("file.temp.txt"))).isFalse();
    }

    @Test
    public void deleteIfExistsDirectoryNotEmpty() {
        final Path dir = newTempDir();
        Files.createFile(dir.resolve("file.temp.txt"));

        assertThatThrownBy(() -> Files.deleteIfExists(dir))
                .isInstanceOf(DirectoryNotEmptyException.class);
    }

    @Test
    public void deleteIfExistsNull() {
        assertThatThrownBy(() -> Files.deleteIfExists(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void createTempFile() {
        final Path tempFile = Files.createTempFile(null,
                                                   null);
        assertThat(tempFile).isNotNull();
        assertThat(tempFile.getFileName().toString()).endsWith("tmp");
        assertThat(tempFile.toFile()).exists();

        final Path tempFile2 = Files.createTempFile("",
                                                    "");
        assertThat(tempFile2).isNotNull();
        assertThat(tempFile2.getFileName().toString()).endsWith("tmp");
        assertThat(tempFile2.toFile()).exists();

        final Path tempFile3 = Files.createTempFile("foo", "bar");
        assertThat(tempFile3).isNotNull();
        assertThat(tempFile3.toFile()).exists();
        assertThat(tempFile3.getFileName().toString()).startsWith("foo").endsWith(".bar");

        final Path tempFile4 = Files.createTempFile("",
                                                    "bar");
        assertThat(tempFile4).isNotNull();
        assertThat(tempFile4.toFile()).exists();
        assertThat(tempFile4.getFileName().toString()).endsWith(".bar");

        final Path tempFile5 = Files.createTempFile("",
                                                    ".bar");
        assertThat(tempFile5).isNotNull();
        assertThat(tempFile5.toFile()).exists();
        assertThat(tempFile5.getFileName().toString()).endsWith(".bar");

        final Path tempFile6 = Files.createTempFile("",
                                                    "bar.temp");
        assertThat(tempFile6).isNotNull();
        assertThat(tempFile6.toFile()).exists();
        assertThat(tempFile6.getFileName().toString()).endsWith(".bar.temp");

        final Path tempFile7 = Files.createTempFile("",
                                                    ".bar.temp");
        assertThat(tempFile7).isNotNull();
        assertThat(tempFile7.toFile()).exists();
        assertThat(tempFile7.getFileName().toString()).endsWith(".bar.temp");
    }

    @Test
    public void createTempFileInsideDir() {
        final Path dir = newTempDir();

        assertThat(dir.toFile().list()).isEmpty();

        final Path tempFile = Files.createTempFile(dir,
                                                   null,
                                                   null);

        assertThat(tempFile).isNotNull();
        assertThat(tempFile.getFileName().toString()).endsWith("tmp");
        assertThat(tempFile.toFile()).exists();

        assertThat(dir.toFile().list()).isNotEmpty();
    }

    @Test
    public void createTempFileNoSuchFile() {
        assertThatThrownBy(() -> Files.createTempFile(Paths.get("/path/to/"),
                                                      null,
                                                      null))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void createTempFileNull() {
        assertThatThrownBy(() -> Files.createTempFile((Path) null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'dir' should be not null!");
    }

    @Test
    public void createTempDirectory() {
        final Path tempFile = Files.createTempDirectory(null);
        assertThat(tempFile).isNotNull();
        assertThat(tempFile.toFile()).exists();
        assertThat(tempFile.toFile()).isDirectory();

        final Path tempFile2 = Files.createTempDirectory("");
        assertThat(tempFile2).isNotNull();
        assertThat(tempFile2.toFile()).exists();
        assertThat(tempFile2.toFile()).isDirectory();

        final Path tempFile3 = Files.createTempDirectory("foo");
        assertThat(tempFile3).isNotNull();
        assertThat(tempFile3.toFile()).exists();
        assertThat(tempFile3.getFileName().toString()).startsWith("foo");
        assertThat(tempFile3.toFile()).isDirectory();
    }

    @Test
    public void createTempDirectoryInsideDir() {
        final Path dir = newTempDir();

        assertThat(dir.toFile().list()).isEmpty();

        final Path tempFile = Files.createTempDirectory(dir,
                                                        null);

        assertThat(tempFile).isNotNull();
        assertThat(tempFile.toFile()).exists();
        assertThat(tempFile.toFile()).isDirectory();

        assertThat(dir.toFile().list()).isNotEmpty();
    }

    @Test
    public void createTempDirectoryNoSuchFile() {
        assertThatThrownBy(() -> Files.createTempDirectory(Paths.get("/path/to/"),
                                                           null))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void createTempDirectoryNull() {
        assertThatThrownBy(() -> Files.createTempDirectory((Path) null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'dir' should be not null!");
    }

    @Test
    public void copyDir() {
        final Path source = newTempDir();
        final Path dest = newDirToClean();

        assertThat(source.toFile()).exists();
        assertThat(dest.toFile()).doesNotExist();

        Files.copy(source,
                   dest);

        assertThat(dest.toFile()).exists();
        assertThat(source.toFile()).exists();
    }

    @Test
    public void copyDirDirectoryNotEmptyException() {
        final Path source = newTempDir();
        final Path dest = newDirToClean();
        Files.createTempFile(source, "foo", "bar");

        assertThatThrownBy(() -> Files.copy(source, dest))
                .isInstanceOf(DirectoryNotEmptyException.class);
    }

    @Test
    public void copyFile() throws IOException {
        final Path dir = newTempDir();

        final Path source = dir.resolve("temp.txt");
        final Path dest = dir.resolve("result.txt");

        try (final OutputStream stream = Files.newOutputStream(source)) {
            stream.write('a');
        }

        Files.copy(source,
                   dest);

        assertThat(dest.toFile()).exists();
        assertThat(source.toFile()).exists();
        assertThat(dest.toFile().length()).isEqualTo(source.toFile().length());
    }

    @Test
    public void copyFileInvalidSourceAndTarget() throws IOException {
        final Path source = newTempDir();
        final Path dest = newTempDir().resolve("other");

        final Path sourceFile = source.resolve("file.txt");
        try (final OutputStream stream = Files.newOutputStream(sourceFile)) {
            stream.write('a');
        }

        assertThatThrownBy(() -> Files.copy(source, dest))
                .isInstanceOf(DirectoryNotEmptyException.class);

        sourceFile.toFile().delete();
        Files.copy(source,
                   dest);

        assertThatThrownBy(() -> Files.copy(source, dest))
                .isInstanceOf(FileAlreadyExistsException.class);

        dest.toFile().delete();
        source.toFile().delete();

        assertThatThrownBy(() -> Files.copy(source, dest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Condition 'source must exist' is invalid!");
    }

    @Test
    public void copyNull1() {
        assertThatThrownBy(() -> Files.copy(newTempDir(), (Path) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'target' should be not null!");
    }

    @Test
    public void copyNull2() {
        assertThatThrownBy(() -> Files.copy((Path) null, Paths.get("/temp")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'source' should be not null!");
    }

    @Test
    public void copyNull3() {
        assertThatThrownBy(() -> Files.copy((Path) null, (Path) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'source' should be not null!");
    }

    @Test
    public void moveDir() {
        final Path source = newTempDir();
        final Path dest = newTempDir().resolve("other");

        Files.move(source,
                   dest);

        assertThat(source.toFile()).doesNotExist();
        assertThat(dest.toFile()).exists();
    }

    @Test
    public void moveFile() throws IOException {
        final Path dir = newTempDir();
        final Path source = dir.resolve("fileSource.txt");
        final Path dest = dir.resolve("fileDest.txt");
        try (final OutputStream stream = Files.newOutputStream(source)) {
            stream.write('a');
        }

        long lenght = source.toFile().length();
        Files.move(source,
                   dest);

        assertThat(dest.toFile()).exists();
        assertThat(source.toFile()).doesNotExist();
        assertThat(dest.toFile().length()).isEqualTo(lenght);
    }

    @Test
    public void moveFileInvalidSourceAndTarget() throws IOException {
        final Path source = newTempDir();
        final Path dest = newTempDir().resolve("other");

        final Path sourceFile = source.resolve("file.txt");
        try (final OutputStream stream = Files.newOutputStream(sourceFile)) {
            stream.write('a');
        }

        assertThatThrownBy(() -> Files.move(source, dest))
                .isInstanceOf(DirectoryNotEmptyException.class);

        sourceFile.toFile().delete();
        Files.copy(source,
                   dest);

        assertThatThrownBy(() -> Files.move(source, dest))
                .isInstanceOf(FileAlreadyExistsException.class);

        dest.toFile().delete();
        source.toFile().delete();

        assertThatThrownBy(() -> Files.move(source, dest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Condition 'source must exist' is invalid!");
    }

    @Test
    public void moveNull1() {
        assertThatThrownBy(() -> Files.move(newTempDir(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'target' should be not null!");
    }

    @Test
    public void moveNull2() {
        assertThatThrownBy(() -> Files.move(null, newTempDir()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'source' should be not null!");
    }

    @Test
    public void moveNull3() {
        assertThatThrownBy(() -> Files.move(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'source' should be not null!");
    }

    @Test
    public void getFileStoreNull() {
        assertThatThrownBy(() -> Files.getFileStore(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void getFileStoreN() {
        final URI uri = URI.create("nothing:///testXXXXXXX");

        assertThatThrownBy(() -> Files.getFileStore(Paths.get(uri)))
                .isInstanceOf(FileSystemNotFoundException.class)
                .hasMessage("Provider 'nothing' not found");
    }

    @Test
    public void getFileAttributeViewGeneral() {
        final Path path = Files.createTempFile(null,
                                               null);

        final BasicFileAttributeView view = Files.getFileAttributeView(path,
                                                                       BasicFileAttributeView.class);
        assertThat(view).isNotNull();
        assertThat((Object) view.readAttributes()).isNotNull();
        assertThat(view.readAttributes().isRegularFile()).isTrue();
        assertThat(view.readAttributes().isDirectory()).isFalse();
        assertThat(view.readAttributes().isSymbolicLink()).isFalse();
        assertThat(view.readAttributes().isOther()).isFalse();
        assertThat(view.readAttributes().size()).isEqualTo(0L);
    }

    @Test
    public void getFileAttributeViewBasic() {
        final Path path = Files.createTempFile(null,
                                               null);

        final BasicFileAttributeView view = Files.getFileAttributeView(path,
                                                                       BasicFileAttributeView.class);
        assertThat(view).isNotNull();
        assertThat((Object) view.readAttributes()).isNotNull();
        assertThat(view.readAttributes().isRegularFile()).isTrue();
        assertThat(view.readAttributes().isDirectory()).isFalse();
        assertThat(view.readAttributes().isSymbolicLink()).isFalse();
        assertThat(view.readAttributes().isOther()).isFalse();
        assertThat(view.readAttributes().size()).isEqualTo(0L);
    }

    @Test
    public void getFileAttributeViewInvalidView() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.getFileAttributeView(path,
                                              MyAttrsView.class)).isNull();
    }

    @Test
    public void getFileAttributeViewNoSuchFileException() {
        final Path path = Paths.get("/path/to/file.txt");

        assertThatThrownBy(() -> Files.getFileAttributeView(path,
                                                            BasicFileAttributeView.class))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void getFileAttributeViewNull1() {
        assertThatThrownBy(() -> Files.getFileAttributeView(null, MyAttrsView.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void getFileAttributeViewNull2() {
        final Path path = Paths.get("/path/to/file.txt");

        assertThatThrownBy(() -> Files.getFileAttributeView(path, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'type' should be not null!");
    }

    @Test
    public void getFileAttributeViewNull3() {
        assertThatThrownBy(() -> Files.getFileAttributeView(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAttributesGeneral() {
        final Path path = Files.createTempFile("foo", "bar");

        final BasicFileAttributesImpl attrs = Files.readAttributes(path,
                                                                   BasicFileAttributesImpl.class);
        assertThat(attrs).isNotNull();
        assertThat(attrs.isRegularFile()).isTrue();
        assertThat(attrs.isDirectory()).isFalse();
        assertThat(attrs.isSymbolicLink()).isFalse();
        assertThat(attrs.isOther()).isFalse();
        assertThat(attrs.size()).isEqualTo(0L);
    }

    @Test
    public void readAttributesBasic() {
        final Path path = Files.createTempFile("foo", "bar");

        final BasicFileAttributes attrs = Files.readAttributes(path,
                                                               BasicFileAttributes.class);

        assertThat(attrs).isNotNull();
        assertThat(attrs.isRegularFile()).isTrue();
        assertThat(attrs.isDirectory()).isFalse();
        assertThat(attrs.isSymbolicLink()).isFalse();
        assertThat(attrs.isOther()).isFalse();
        assertThat(attrs.size()).isEqualTo(0L);
    }

    @Test
    public void readAttributesNonExistentFile() {
        final Path path = Paths.get("/path/to/file.txt");
        assertThatThrownBy(() -> Files.readAttributes(path,
                                                      BasicFileAttributes.class))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void readAttributesInvalid() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.readAttributes(path,
                                        MyAttrs.class)).isNull();
    }

    @Test
    public void readAttributesNull1() {
        assertThatThrownBy(() -> Files.readAttributes(null, MyAttrs.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAttributesNull2() {
        final Path path = Paths.get("/path/to/file.txt");
        assertThatThrownBy(() -> Files.readAttributes(path, (Class<MyAttrs>) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'type' should be not null!");
    }

    @Test
    public void readAttributesNull3() {
        assertThatThrownBy(() -> Files.readAttributes(null, (Class<MyAttrs>) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAttributesMap() {
        final Path path = Files.createTempFile("foo", "bar");

        Assertions.assertThat(Files.readAttributes(path,
                                                   "*")).hasSize(9);
        Assertions.assertThat(Files.readAttributes(path,
                                                   "basic:*")).hasSize(9);
        Assertions.assertThat(Files.readAttributes(path,
                                                   "basic:isRegularFile")).hasSize(1);
        Assertions.assertThat(Files.readAttributes(path,
                                                   "basic:isRegularFile,isDirectory")).hasSize(2);
        Assertions.assertThat(Files.readAttributes(path,
                                                   "basic:isRegularFile,isDirectory,someThing")).hasSize(2);
        Assertions.assertThat(Files.readAttributes(path,
                                                   "basic:someThing")).hasSize(0);

        Assertions.assertThat(Files.readAttributes(path,
                                                   "isRegularFile")).hasSize(1);
        Assertions.assertThat(Files.readAttributes(path,
                                                   "isRegularFile,isDirectory")).hasSize(2);
        Assertions.assertThat(Files.readAttributes(path,
                                                   "isRegularFile,isDirectory,someThing")).hasSize(2);
        Assertions.assertThat(Files.readAttributes(path,
                                                   "someThing")).hasSize(0);

        assertThatThrownBy(() -> Files.readAttributes(path,
                                                      ":someThing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(":someThing");

        assertThatThrownBy(() -> Files.readAttributes(path,
                                                      "advanced:isRegularFile"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("View 'advanced' not available");
    }

    @Test
    public void readAttributesMapNull1() {
        assertThatThrownBy(() -> Files.readAttributes(null, "*"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAttributesMapNull2() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.readAttributes(path, (String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'attributes' should be filled!");
    }

    @Test
    public void readAttributesMapNull3() {
        assertThatThrownBy(() -> Files.readAttributes(null,
                                                      (String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAttributesMapEmpty() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.readAttributes(path, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'attributes' should be filled!");
    }

    @Test
    public void readAttributesMapNoSuchFileException() {
        final Path path = Paths.get("/path/to/file.txt");

        assertThatThrownBy(() -> Files.readAttributes(path,
                                                      "*"))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void setAttributeNull1() {
        final Path path = Files.createTempFile("foo", "bar");
        assertThatThrownBy(() -> Files.setAttribute(path, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'attribute' should be filled!");
    }

    @Test
    public void setAttributeNull2() {
        assertThatThrownBy(() -> Files.setAttribute(null, "some", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void setAttributeNull3() {
        assertThatThrownBy(() -> Files.setAttribute(null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void setAttributeEmpty() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.setAttribute(path, "", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'attribute' should be filled!");
    }

    @Test
    public void setAttributeInvalidAttr() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.setAttribute(path, "myattr", null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Condition 'invalid attribute' is invalid!");
    }

    @Test
    public void setAttributeInvalidView() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.setAttribute(path,
                                                    "advanced:isRegularFile",
                                                    null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("View 'advanced' not available");
    }

    @Test
    public void setAttributeInvalidView2() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.setAttribute(path,
                                                    ":isRegularFile",
                                                    null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(":isRegularFile");
    }

    @Test
    public void setAttributeNotImpl() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.setAttribute(path,
                                                    "isRegularFile",
                                                    null))
                .isInstanceOf(NotImplementedException.class);
    }

    @Test
    public void readAttribute() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.getAttribute(path,
                                      "basic:isRegularFile")).isNotNull();
        assertThat(Files.getAttribute(path,
                                      "basic:someThing")).isNull();

        assertThat(Files.getAttribute(path,
                                      "isRegularFile")).isNotNull();
        assertThat(Files.getAttribute(path,
                                      "someThing")).isNull();
    }

    @Test
    public void readAttributeInvalid() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.getAttribute(path, "*"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("*");
    }

    @Test
    public void readAttributeInvalid2() {
        final Path path = Files.createTempFile("foo", "bar");
        assertThatThrownBy(() -> Files.getAttribute(path,
                                                    "isRegularFile,isDirectory"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("isRegularFile,isDirectory");
    }

    @Test
    public void readAttributeInvalid3() {
        final Path path = Paths.get("/path/to/file.txt");

        assertThatThrownBy(() -> Files.getAttribute(path,
                                                    "isRegularFile"))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void getLastModifiedTime() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.getLastModifiedTime(path)).isNotNull();
    }

    @Test
    public void getLastModifiedTimeNoSuchFileException() {
        final Path path = Paths.get("/path/to/file");

        assertThatThrownBy(() -> Files.getLastModifiedTime(path))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void getLastModifiedTimeNull() {
        assertThatThrownBy(() -> Files.getLastModifiedTime(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void setLastModifiedTime() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.setLastModifiedTime(path, null))
                .isInstanceOf(NotImplementedException.class);
    }

    @Test
    public void setLastModifiedTimeNoSuchFileException() {
        final Path path = Paths.get("/path/to/file");

        assertThatThrownBy(() -> Files.setLastModifiedTime(path, null))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void setLastModifiedTimeNull() {
        assertThatThrownBy(() -> Files.setLastModifiedTime(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void setLastModifiedTimeNull2() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.setLastModifiedTime(path, null))
                .isInstanceOf(NotImplementedException.class);
    }

    @Test
    public void size() throws IOException {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.size(path)).isEqualTo(0L);

        final Path sourceFile = newTempDir().resolve("file.txt");
        final OutputStream stream = Files.newOutputStream(sourceFile);
        stream.write('a');
        stream.close();

        assertThat(Files.size(sourceFile)).isEqualTo(1L);
    }

    @Test
    public void sizeNoSuchFileException() {
        final Path path = Paths.get("/path/to/file");

        assertThatThrownBy(() -> Files.size(path))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void sizeNull() {
        assertThatThrownBy(() -> Files.size(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void exists() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.exists(path)).isTrue();
        assertThat(Files.exists(newTempDir())).isTrue();
        assertThat(Files.exists(Paths.get("/some/path/here"))).isFalse();
    }

    @Test
    public void existsNull() {
        assertThatThrownBy(() -> Files.exists(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void notExists() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.notExists(path)).isFalse();
        assertThat(Files.notExists(newTempDir())).isFalse();
        assertThat(Files.notExists(Paths.get("/some/path/here"))).isTrue();
        assertThat(Files.notExists(newTempDir().resolve("some.text"))).isTrue();
    }

    @Test
    public void notExistsNull() {
        assertThatThrownBy(() -> Files.notExists(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isSameFile() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.isSameFile(path,
                                    Paths.get(path.toString()))).isTrue();
        assertThat(Files.isSameFile(path,
                                    Files.createTempFile("foo", "bar"))).isFalse();
        assertThat(Files.isSameFile(newTempDir(),
                                    newTempDir())).isFalse();

        final Path dir = newTempDir();
        assertThat(Files.isSameFile(dir,
                                    Paths.get(dir.toString()))).isTrue();

        assertThat(Files.isSameFile(Paths.get("/path/to/some/place"),
                                    Paths.get("/path/to/some/place"))).isTrue();
        assertThat(Files.isSameFile(Paths.get("/path/to/some/place"),
                                    Paths.get("/path/to/some/place/a"))).isFalse();
    }

    @Test
    public void isSameFileNull1() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.isSameFile(path, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path2' should be not null!");
    }

    @Test
    public void isSameFileNull2() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThatThrownBy(() -> Files.isSameFile(null, path))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isSameFileNull3() {
        assertThatThrownBy(() -> Files.isSameFile(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isHidden() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.isHidden(path)).isFalse();
        assertThat(Files.isHidden(newTempDir())).isFalse();
        assertThat(Files.isHidden(Paths.get("/some/file"))).isFalse();
    }

    @Test
    public void isHiddenNull() {
        assertThatThrownBy(() -> Files.isHidden(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isReadable() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.isReadable(path)).isTrue();
        assertThat(Files.isReadable(newTempDir())).isTrue();
        assertThat(Files.isReadable(Paths.get("/some/file"))).isFalse();
    }

    @Test
    public void isReadableNull() {
        assertThatThrownBy(() -> Files.isReadable(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isWritable() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.isWritable(path)).isTrue();
        assertThat(Files.isWritable(newTempDir())).isTrue();
        assertThat(Files.isWritable(Paths.get("/some/file"))).isFalse();
    }

    @Test
    public void isWritableNull() {
        assertThatThrownBy(() -> Files.isWritable(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isExecutable() {
        Assume.assumeFalse(SimpleFileSystemProvider.OSType.currentOS().equals(SimpleFileSystemProvider.OSType.WINDOWS));

        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.isExecutable(path)).isFalse();
        assertThat(Files.isExecutable(newTempDir())).isTrue();
        assertThat(Files.isExecutable(Paths.get("/some/file"))).isFalse();
    }

    @Test
    public void isExecutableNull() {
        assertThatThrownBy(() -> Files.isExecutable(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isSymbolicLink() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.isSymbolicLink(path)).isFalse();
        assertThat(Files.isSymbolicLink(newTempDir())).isFalse();
        assertThat(Files.isSymbolicLink(Paths.get("/some/file"))).isFalse();
    }

    @Test
    public void isSymbolicLinkNull() {
        assertThatThrownBy(() -> Files.isSymbolicLink(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isDirectory() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.isDirectory(path)).isFalse();
        assertThat(Files.isDirectory(newTempDir())).isTrue();
        assertThat(Files.isDirectory(Paths.get("/some/file"))).isFalse();
    }

    @Test
    public void isDirectoryNull() {
        assertThatThrownBy(() -> Files.isDirectory(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void isRegularFile() {
        final Path path = Files.createTempFile("foo", "bar");

        assertThat(Files.isRegularFile(path)).isTrue();
        assertThat(Files.isRegularFile(newTempDir())).isFalse();
        assertThat(Files.isRegularFile(Paths.get("/some/file"))).isFalse();
    }

    @Test
    public void isRegularFileNull() {
        assertThatThrownBy(() -> Files.isRegularFile(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    private interface MyAttrsView extends BasicFileAttributeView {

    }

    private interface MyAttrs extends BasicFileAttributes {

    }
}
