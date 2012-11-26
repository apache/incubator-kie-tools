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

package org.uberfire.java.nio.fs.base;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.uberfire.java.nio.fs.base.GeneralFileAttributeView.*;

public class GeneralFileAttributeViewTest {

    final FileSystem fs = mock(FileSystem.class);

    final FileTime dummyFileTime = new FileTimeImpl(0);

    @Test
    public void checkReadAttrs() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);
        final GeneralFileAttributes attrs = (GeneralFileAttributes) view.readAttributes();

        assertThat(view).isNotNull();
        assertThat(view.name()).isNotNull().isEqualTo("basic");
        assertThat(attrs.isHidden()).isFalse();
        assertThat(attrs.isExecutable()).isFalse();
        assertThat(attrs.isReadable()).isTrue();
        assertThat(attrs.size()).isEqualTo(0L);
        assertThat(attrs.isSymbolicLink()).isFalse();
        assertThat(attrs.isDirectory()).isFalse();
        assertThat(attrs.isRegularFile()).isTrue();
        assertThat(attrs.fileKey()).isNull();
        assertThat(attrs.isOther()).isFalse();
        assertThat(attrs.creationTime()).isNull();
        assertThat(attrs.lastAccessTime()).isNull();
        assertThat(attrs.lastModifiedTime()).isNotNull();
        assertThat(attrs.lastModifiedTime().toMillis()).isEqualTo(file.lastModified());
        assertThat(attrs.lastModifiedTime().to(TimeUnit.MINUTES)).isNotNull();
    }

    @Test
    public void checkReadAttrsAsMap() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);
        final Map<String, Object> result = view.readAttributes("*");

        assertThat(result).isNotNull().hasSize(12);
        assertThat(result.get(IS_REGULAR_FILE)).isInstanceOf(Boolean.class).isEqualTo(Boolean.TRUE);
        assertThat(result.get(IS_DIRECTORY)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_HIDDEN)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_SYMBOLIC_LINK)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_OTHER)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_EXECUTABLE)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_READABLE)).isInstanceOf(Boolean.class).isEqualTo(Boolean.TRUE);
        assertThat(result.get(SIZE)).isInstanceOf(Long.class).isEqualTo(0L);
        assertThat(result.get(FILE_KEY)).isNull();
        assertThat(result.get(CREATION_TIME)).isNull();
        assertThat(result.get(LAST_MODIFIED_TIME)).isNull();
        assertThat(result.get(LAST_ACCESS_TIME)).isNull();
    }

    @Test
    public void checkReadAttrsAsMap2() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);
        final Map<String, Object> result = view.readAttributes(IS_REGULAR_FILE,
                IS_DIRECTORY, IS_SYMBOLIC_LINK, IS_OTHER, SIZE, FILE_KEY,
                IS_READABLE, IS_EXECUTABLE, IS_HIDDEN, LAST_MODIFIED_TIME,
                LAST_ACCESS_TIME, CREATION_TIME);

        assertThat(result).isNotNull().hasSize(12);
        assertThat(result.get(IS_REGULAR_FILE)).isInstanceOf(Boolean.class).isEqualTo(Boolean.TRUE);
        assertThat(result.get(IS_DIRECTORY)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_HIDDEN)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_SYMBOLIC_LINK)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_OTHER)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_EXECUTABLE)).isInstanceOf(Boolean.class).isEqualTo(Boolean.FALSE);
        assertThat(result.get(IS_READABLE)).isInstanceOf(Boolean.class).isEqualTo(Boolean.TRUE);
        assertThat(result.get(SIZE)).isInstanceOf(Long.class).isEqualTo(0L);
        assertThat(result.get(FILE_KEY)).isNull();
        assertThat(result.get(CREATION_TIME)).isNull();
        assertThat(result.get(LAST_MODIFIED_TIME)).isNull();
        assertThat(result.get(LAST_ACCESS_TIME)).isNull();
    }

    @Test
    public void checkReadAttrsAsMapNonExistent() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);
        final Map<String, Object> result = view.readAttributes(IS_REGULAR_FILE,
                "hum?");

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(IS_REGULAR_FILE)).isInstanceOf(Boolean.class).isEqualTo(Boolean.TRUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkReadAttrsAsMapEmpty() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);
        view.readAttributes(IS_REGULAR_FILE, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkReadAttrsAsMapNull1() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);
        view.readAttributes(IS_REGULAR_FILE, null, "hum?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkReadAttrsAsMapNull2() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);
        view.readAttributes(null);
    }

    @Test(expected = NotImplementedException.class)
    public void createSymbolicLinkNotImpl() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);

        view.setAttribute(LAST_MODIFIED_TIME, new Object());
    }

    @Test(expected = NotImplementedException.class)
    public void createSymbolicLinkNotImpl2() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);

        view.setAttribute(LAST_MODIFIED_TIME, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSymbolicLinkNull() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);

        view.setAttribute(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSymbolicLinkEmpty() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);

        view.setAttribute("", null);
    }

    @Test(expected = NotImplementedException.class)
    public void setTimesNotImpl() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);

        view.setTimes(dummyFileTime, dummyFileTime, dummyFileTime);
    }

    @Test(expected = NotImplementedException.class)
    public void setTimesNullOk() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        final GeneralFileAttributeView view = new GeneralFileAttributeView(path);

        view.setTimes(null, null, null);
    }

}
