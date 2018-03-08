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

package org.uberfire.java.nio.fs.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.Test;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.GeneralPathImpl;
import org.uberfire.java.nio.base.NotImplementedException;
import org.uberfire.java.nio.file.AccessDeniedException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.uberfire.java.nio.file.AccessMode.EXECUTE;
import static org.uberfire.java.nio.file.AccessMode.READ;
import static org.uberfire.java.nio.file.AccessMode.WRITE;

public class SimpleFileSystemProviderAttrsRelatedTest {

    @Test
    public void checkIsHidden() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create(fsProvider.getFileSystem(URI.create("file:///")),
                                                 "/path/to/file.txt",
                                                 false);

        assertThat(fsProvider.isHidden(path)).isFalse();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path2 = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                       tempFile);

        assertThat(fsProvider.isHidden(path2)).isEqualTo(tempFile.isHidden());
    }

    @Test
    public void isHiddenNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.isHidden(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void checkAccess() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create(fsProvider.getFileSystem(URI.create("file:///")),
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> fsProvider.checkAccess(path, WRITE))
                .isInstanceOf(NoSuchFileException.class);

        assertThatThrownBy(() -> fsProvider.checkAccess(path, READ))
                .isInstanceOf(NoSuchFileException.class);

        assertThatThrownBy(() -> fsProvider.checkAccess(path, EXECUTE))
                .isInstanceOf(NoSuchFileException.class);

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path2 = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                       tempFile);

        fsProvider.checkAccess(path2, WRITE);

        assertThat(tempFile.setWritable(false)).isTrue();

        assertThatThrownBy(() -> fsProvider.checkAccess(path2, WRITE))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(tempFile.setWritable(true)).isTrue();

        fsProvider.checkAccess(path2, READ);

        assertThat(tempFile.setReadable(false)).isTrue();

        if (SimpleFileSystemProvider.OSType.currentOS().equals(SimpleFileSystemProvider.OSType.UNIX_LIKE)) {
            assertThatThrownBy(() -> fsProvider.checkAccess(path2, READ))
                    .isInstanceOf(AccessDeniedException.class);
        }

        assertThat(tempFile.setReadable(true)).isTrue();

        if (SimpleFileSystemProvider.OSType.currentOS().equals(SimpleFileSystemProvider.OSType.UNIX_LIKE)) {
            assertThatThrownBy(() -> fsProvider.checkAccess(path2, EXECUTE))
                    .isInstanceOf(AccessDeniedException.class);
        }

        assertThat(tempFile.setExecutable(true)).isTrue();

        fsProvider.checkAccess(path2, EXECUTE);
        fsProvider.checkAccess(path2, READ, WRITE, EXECUTE);
    }

    @Test
    public void checkAccessNull1() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.checkAccess(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void checkAccessNull2() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create(fsProvider.getFileSystem(URI.create("file:///")),
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> fsProvider.checkAccess(path, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'modes' should be not null!");
    }

    @Test
    public void checkAccessNull3() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.checkAccess(null, READ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void checkAccessNull4() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThatThrownBy(() -> fsProvider.checkAccess(path, null, READ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'mode' should be not null!");
    }

    @Test
    public void checkGetFileStore() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create(fsProvider.getFileSystem(URI.create("file:///")),
                                                 "/path/to/file.txt",
                                                 false);

        assertThat(fsProvider.getFileStore(path)).isNotNull();
        assertThat(fsProvider.getFileSystem(path.toUri()).getFileStores()).isNotNull().contains(fsProvider.getFileStore(path));
    }

    @Test
    public void getFileStoreNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.getFileStore(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void checkGetFileAttributeViewGeneral() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        final BasicFileAttributeView view = fsProvider.getFileAttributeView(path,
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
    public void checkGetFileAttributeViewBasic() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        final BasicFileAttributeView view = fsProvider.getFileAttributeView(path,
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
    public void getFileAttributeViewInvalidView() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThat(fsProvider.getFileAttributeView(path,
                                                   MyAttrsView.class)).isNull();
    }

    @Test
    public void getFileAttributeViewNull1() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.getFileAttributeView(null, MyAttrsView.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void getFileAttributeViewNull2() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create(fsProvider.getFileSystem(URI.create("file:///")),
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> fsProvider.getFileAttributeView(path, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'type' should be not null!");
    }

    @Test
    public void getFileAttributeViewNull3() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.getFileAttributeView(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void checkReadAttributesGeneral() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        final BasicFileAttributesImpl attrs = fsProvider.readAttributes(path,
                                                                        BasicFileAttributesImpl.class);
        assertThat(attrs).isNotNull();
        assertThat(attrs.isRegularFile()).isTrue();
        assertThat(attrs.isDirectory()).isFalse();
        assertThat(attrs.isSymbolicLink()).isFalse();
        assertThat(attrs.isOther()).isFalse();
        assertThat(attrs.size()).isEqualTo(0L);
    }

    @Test
    public void checkReadAttributesBasic() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        final BasicFileAttributes attrs = fsProvider.readAttributes(path,
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
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create(fsProvider.getFileSystem(URI.create("file:///")),
                                                 "/path/to/file.txt",
                                                 false);
        assertThatThrownBy(() -> fsProvider.readAttributes(path,
                                                           BasicFileAttributes.class))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void readAttributesInvalid() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThat(fsProvider.readAttributes(path,
                                             MyAttrs.class)).isNull();
    }

    @Test
    public void readAttributesNull1() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.readAttributes(null,
                                                           MyAttrs.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAttributesNull2() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create(fsProvider.getFileSystem(URI.create("file:///")),
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> fsProvider.readAttributes(path,
                                                           (Class<MyAttrs>) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'type' should be not null!");
    }

    @Test
    public void readAttributesNull3() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.readAttributes(null,
                                                           (Class<MyAttrs>) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void checkReadAttributesMap() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThat(fsProvider.readAttributes(path,
                                             "*")).isNotNull().hasSize(9);
        assertThat(fsProvider.readAttributes(path,
                                             "basic:*")).isNotNull().hasSize(9);
        assertThat(fsProvider.readAttributes(path,
                                             "basic:isRegularFile")).isNotNull().hasSize(1);
        assertThat(fsProvider.readAttributes(path,
                                             "basic:isRegularFile,isDirectory")).isNotNull().hasSize(2);
        assertThat(fsProvider.readAttributes(path,
                                             "basic:isRegularFile,isDirectory,someThing")).isNotNull().hasSize(2);
        assertThat(fsProvider.readAttributes(path,
                                             "basic:someThing")).isNotNull().hasSize(0);

        assertThat(fsProvider.readAttributes(path,
                                             "isRegularFile")).isNotNull().hasSize(1);
        assertThat(fsProvider.readAttributes(path,
                                             "isRegularFile,isDirectory")).isNotNull().hasSize(2);
        assertThat(fsProvider.readAttributes(path,
                                             "isRegularFile,isDirectory,someThing")).isNotNull().hasSize(2);
        assertThat(fsProvider.readAttributes(path,
                                             "someThing")).isNotNull().hasSize(0);

        assertThatThrownBy(() -> fsProvider.readAttributes(path, ":someThing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(":someThing");

        assertThatThrownBy(() -> fsProvider.readAttributes(path, "advanced:isRegularFile"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("View 'advanced' not available");
    }

    @Test
    public void readAttributesMapNull1() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.readAttributes(null, "*"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAttributesMapNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThatThrownBy(() -> fsProvider.readAttributes(path, (String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'attributes' should be filled!");
    }

    @Test
    public void readAttributesMapNull3() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.readAttributes(null, (String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAttributesMapEmpty() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThatThrownBy(() -> fsProvider.readAttributes(path, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'attributes' should be filled!");
    }

    @Test
    public void setAttributeNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThatThrownBy(() -> fsProvider.setAttribute(path, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'attributes' should be filled!");
    }

    @Test
    public void setAttributeNull2() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.setAttribute(null, "some", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void setAttributeNull3() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThatThrownBy(() -> fsProvider.setAttribute(null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void setAttributeEmpty() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);
        assertThatThrownBy(() -> fsProvider.setAttribute(path, "", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'attributes' should be filled!");
    }

    @Test
    public void setAttributeInvalidAttr() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThatThrownBy(() -> fsProvider.setAttribute(path, "myattr", null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Condition 'invalid attribute' is invalid!");
    }

    @Test
    public void setAttributeInvalidView() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThatThrownBy(() -> fsProvider.setAttribute(path, "advanced:isRegularFile", null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("View 'advanced' not available");
    }

    @Test
    public void setAttributeInvalidView2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThatThrownBy(() -> fsProvider.setAttribute(path, ":isRegularFile", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(":isRegularFile");
    }

    @Test
    public void setAttributeNotImpl() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile("foo",
                                                  "bar");
        final Path path = GeneralPathImpl.newFromFile(fsProvider.getFileSystem(URI.create("file:///")),
                                                      tempFile);

        assertThatThrownBy(() -> fsProvider.setAttribute(path, "isRegularFile", null))
                .isInstanceOf(NotImplementedException.class);
    }

    private interface MyAttrsView extends BasicFileAttributeView {

    }

    private interface MyAttrs extends BasicFileAttributes {

    }
}
