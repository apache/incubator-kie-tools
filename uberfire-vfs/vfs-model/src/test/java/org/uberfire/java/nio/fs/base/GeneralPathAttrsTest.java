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
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeneralPathAttrsTest {

    final FileSystem fs = mock(FileSystem.class);

    @Test
    public void testFileAttrs() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        assertThat(path.getAttrs()).isNotNull();
        assertThat(path.getAttrs().exists()).isTrue();
        assertThat(path.getAttrs().isHidden()).isFalse();
        assertThat(path.getAttrs().isExecutable()).isFalse();
        assertThat(path.getAttrs().isReadable()).isTrue();
        assertThat(path.getAttrs().size()).isEqualTo(0L);
        assertThat(path.getAttrs().isSymbolicLink()).isFalse();
        assertThat(path.getAttrs().isDirectory()).isFalse();
        assertThat(path.getAttrs().isRegularFile()).isTrue();
        assertThat(path.getAttrs().fileKey()).isNull();
        assertThat(path.getAttrs().isOther()).isFalse();
        assertThat(path.getAttrs().creationTime()).isNull();
        assertThat(path.getAttrs().lastAccessTime()).isNull();
        assertThat(path.getAttrs().lastModifiedTime()).isNotNull();
        assertThat(path.getAttrs().lastModifiedTime().toMillis()).isEqualTo(file.lastModified());
        assertThat(path.getAttrs().lastModifiedTime().to(TimeUnit.MINUTES)).isNotNull();
    }

    @Test
    public void testDirectoryAttrs() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final File file = new File(new File("").getAbsoluteFile().toString());
        final GeneralPathImpl path = GeneralPathImpl.newFromFile(fs, file);

        assertThat(path.getAttrs()).isNotNull();
        assertThat(path.getAttrs().exists()).isTrue();
        assertThat(path.getAttrs().isHidden()).isFalse();
        assertThat(path.getAttrs().isExecutable()).isTrue();
        assertThat(path.getAttrs().isReadable()).isTrue();
        assertThat(path.getAttrs().size()).isEqualTo(file.length());
        assertThat(path.getAttrs().isSymbolicLink()).isFalse();
        assertThat(path.getAttrs().isDirectory()).isTrue();
        assertThat(path.getAttrs().isRegularFile()).isFalse();
        assertThat(path.getAttrs().fileKey()).isNull();
        assertThat(path.getAttrs().isOther()).isFalse();
        assertThat(path.getAttrs().creationTime()).isNull();
        assertThat(path.getAttrs().lastAccessTime()).isNull();
        assertThat(path.getAttrs().lastModifiedTime()).isNotNull();
        assertThat(path.getAttrs().lastModifiedTime().toMillis()).isEqualTo(file.lastModified());
        assertThat(path.getAttrs().lastModifiedTime().to(TimeUnit.MINUTES)).isNotNull();
    }

    @Test
    public void testNonExistenPath() throws IOException {
        when(fs.getSeparator()).thenReturn("/");

        final GeneralPathImpl path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);

        assertThat(path.getAttrs()).isNotNull();
        assertThat(path.getAttrs().exists()).isFalse();
    }

}
