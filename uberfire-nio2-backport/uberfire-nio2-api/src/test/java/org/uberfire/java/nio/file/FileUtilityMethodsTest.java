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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.data.Index.atIndex;
import static org.uberfire.java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static org.uberfire.java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileUtilityMethodsTest extends AbstractBaseTest {

    @Test
    public void newBufferedReader() throws IOException {
        final Path dir = newTempDir();

        try (final OutputStream out = Files.newOutputStream(dir.resolve("file.txt"))) {
            assertThat(out).isNotNull();
            out.write("content".getBytes());
        }

        final BufferedReader reader = Files.newBufferedReader(dir.resolve("file.txt"),
                                                              Charset.defaultCharset());
        assertThat(reader).isNotNull();
        assertThat(reader.readLine()).isNotNull().isEqualTo("content");
        assertThat(reader.readLine()).isNull();
        reader.close();
        try {
            reader.read();
            fail("can't read closed stream");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void newBufferedReaderNoSuchFileException() {
        assertThatThrownBy(() -> Files.newBufferedReader(Paths.get("/some/file/here"),
                                                         Charset.defaultCharset()))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void newBufferedReaderNoSuchFileException2() {
        assertThatThrownBy(() -> Files.newBufferedReader(newTempDir(),
                                                         Charset.defaultCharset()))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void newBufferedReaderNull1() {
        assertThatThrownBy(() -> Files.newBufferedReader(null,
                                                         Charset.defaultCharset()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void newBufferedReaderNull2() {
        assertThatThrownBy(() -> Files.newBufferedReader(Files.createTempFile("foo",
                                                                              "bar"),
                                                         null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'cs' should be not null!");
    }

    @Test
    public void newBufferedReaderNull3() {
        assertThatThrownBy(() -> Files.newBufferedReader(null,
                                                         null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void newBufferedWriter() throws IOException {
        final Path dir = newTempDir();
        try (final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(writer).isNotNull();
            writer.write("content");
        }

        try (final BufferedReader reader = Files.newBufferedReader(dir.resolve("myfile.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(reader).isNotNull();
            assertThat(reader.readLine()).isNotNull().isEqualTo("content");
            assertThat(reader.readLine()).isNull();
        }

        Files.newBufferedWriter(Files.createTempFile(null,
                                                     null),
                                Charset.defaultCharset());
    }

    @Test
    public void newBufferedWriterNull1() {
        assertThatThrownBy(() -> Files.newBufferedWriter(null,
                                                         Charset.defaultCharset()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void newBufferedWriterNull2() {
        assertThatThrownBy(() -> Files.newBufferedWriter(newTempDir().resolve("some"),
                                                         null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'cs' should be not null!");
    }

    @Test
    public void newBufferedWriterNull3() {
        assertThatThrownBy(() -> Files.newBufferedWriter(null,
                                                         null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void copyIn2Path() throws IOException {
        final Path dir = newTempDir();
        try (final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(writer).isNotNull();
            writer.write("content");
        }

        try (final InputStream is = Files.newInputStream(dir.resolve("myfile.txt"))) {
            Files.copy(is,
                       dir.resolve("my_new_file.txt"));
        }

        try (final BufferedReader reader = Files.newBufferedReader(dir.resolve("my_new_file.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(reader).isNotNull();
            assertThat(reader.readLine()).isNotNull().isEqualTo("content");
            assertThat(reader.readLine()).isNull();
        }
    }

    @Test
    public void copyIn2PathReplaceExisting() throws IOException {
        final Path dir = newTempDir();
        try (final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(writer).isNotNull();
            writer.write("content");
        }

        try (final BufferedWriter writer2 = Files.newBufferedWriter(dir.resolve("my_new_file.txt"),
                                                                    Charset.defaultCharset())) {
            assertThat(writer2).isNotNull();
            writer2.write("empty_content");
        }

        try (final InputStream is = Files.newInputStream(dir.resolve("myfile.txt"))) {
            Files.copy(is,
                       dir.resolve("my_new_file.txt"),
                       REPLACE_EXISTING);
        }

        try (final BufferedReader reader = Files.newBufferedReader(dir.resolve("my_new_file.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(reader).isNotNull();
            assertThat(reader.readLine()).isNotNull().isEqualTo("content");
            assertThat(reader.readLine()).isNull();
        }
    }

    @Test
    public void copyIn2PathReplaceExistingNotExists() throws IOException {
        final Path dir = newTempDir();
        try (final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(writer).isNotNull();
            writer.write("content");
        }

        try (final InputStream is = Files.newInputStream(dir.resolve("myfile.txt"))) {
            Files.copy(is,
                       dir.resolve("my_new_file.txt"),
                       REPLACE_EXISTING);
        }

        try (final BufferedReader reader = Files.newBufferedReader(dir.resolve("my_new_file.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(reader).isNotNull();
            assertThat(reader.readLine()).isEqualTo("content");
            assertThat(reader.readLine()).isNull();
        }
    }

    @Test
    public void copyIn2PathNull1() {
        assertThatThrownBy(() -> Files.copy((InputStream) null,
                                            newTempDir().resolve("my_new_file.txt"),
                                            REPLACE_EXISTING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'in' should be not null!");
    }

    @Test
    public void copyIn2PathNull2() {
        assertThatThrownBy(() -> Files.copy(Files.newInputStream(Files.createTempFile("foo",
                                                                                      "bar")),
                                            null,
                                            REPLACE_EXISTING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'target' should be not null!");
    }

    @Test
    public void copyIn2PathNull3() {
        assertThatThrownBy(() -> Files.copy(Files.newInputStream(Files.createTempFile("foo",
                                                                                      "bar")),
                                            newTempDir().resolve("my_new_file.txt"),
                                            null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'options' should be not null!");
    }

    @Test
    public void copyIn2PathNull4() {
        assertThatThrownBy(() -> Files.copy(Files.newInputStream(Files.createTempFile("foo",
                                                                                      "bar")),
                                            newTempDir().resolve("my_new_file.txt"),
                                            new CopyOption[]{null}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'opt' should be not null!");
    }

    @Test
    public void copyIn2PathInvalidOption() {
        assertThatThrownBy(() -> Files.copy(Files.newInputStream(Files.createTempFile("foo",
                                                                                      "bar")),
                                            newTempDir().resolve("my_new_file.txt"),
                                            NOFOLLOW_LINKS))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("NOFOLLOW_LINKS not supported");
    }

    @Test
    public void copyPath2Out() throws IOException {
        final Path dir = newTempDir();
        try (final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(writer).isNotNull();
            writer.write("content");
        }

        try (final OutputStream os = Files.newOutputStream(dir.resolve("my_new_file.txt"))) {
            Files.copy(dir.resolve("myfile.txt"), os);
        }

        try (final BufferedReader reader = Files.newBufferedReader(dir.resolve("my_new_file.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(reader).isNotNull();
            assertThat(reader.readLine()).isNotNull().isEqualTo("content");
            assertThat(reader.readLine()).isNull();
        }
    }

    @Test
    public void copyPath2OutNotExists() {
        assertThatThrownBy(() -> {
            try (OutputStream os = Files.newOutputStream(newTempDir().resolve("my_new_file.txt"))) {
                Files.copy(newTempDir().resolve("myfile.txt"), os);
            }
        }).isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void copyPath2OutNull1() {
        assertThatThrownBy(() -> {
            try (OutputStream os = Files.newOutputStream(newTempDir().resolve("my_new_file.txt"))) {
                Files.copy(null, os);
            }
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'source' should be not null!");
    }

    @Test
    public void copyPath2OutNull2() {
        assertThatThrownBy(() -> Files.copy(Files.createTempFile("foo",
                                                                 "bar"),
                                            null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'out' should be not null!");
    }

    @Test
    public void copyPath2OutInvalidOption() {
        assertThatThrownBy(() -> Files.copy(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'source' should be not null!");
    }

    @Test
    public void readAllBytes() throws IOException {
        final Path dir = newTempDir();
        try (final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                                   Charset.defaultCharset())) {
            assertThat(writer).isNotNull();
            writer.write("content");
        }

        final byte[] result = Files.readAllBytes(dir.resolve("myfile.txt"));

        assertThat(result)
                .hasSize("content".getBytes().length)
                .isEqualTo("content".getBytes());
    }

    @Test(expected = OutOfMemoryError.class)
    @Ignore
    public void readAllBytesOutOfMemory() throws IOException {
        final Path file = newTempDir().resolve("file.big");
        final RandomAccessFile f = new RandomAccessFile(file.toFile(),
                                                        "rw");
        f.setLength(Integer.MAX_VALUE + 1L);

        f.close();

        Files.readAllBytes(file);
    }

    @Test
    public void readAllBytesFileNotExists() {
        assertThatThrownBy(() -> Files.readAllBytes(newTempDir().resolve("file.big")))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void readAllBytesDir() {
        assertThatThrownBy(() -> Files.readAllBytes(newTempDir()))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void readAllBytesNull() {
        assertThatThrownBy(() -> Files.readAllBytes(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAllLines() throws IOException {
        final Path dir = newTempDir();
        final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(writer).isNotNull();
        writer.write("content");
        writer.close();

        final List<String> result = Files.readAllLines(dir.resolve("myfile.txt"),
                                                       Charset.defaultCharset());
        assertThat(result).hasSize(1)
                .contains("content", atIndex(0));

        final BufferedWriter writer2 = Files.newBufferedWriter(dir.resolve("myfile2.txt"),
                                                               Charset.defaultCharset());
        assertThat(writer2).isNotNull();
        writer2.write("content\nnewFile\nline");
        writer2.close();

        final List<String> result2 = Files.readAllLines(dir.resolve("myfile2.txt"),
                                                        Charset.defaultCharset());
        assertThat(result2).hasSize(3)
                .contains("content", atIndex(0))
                .contains("newFile", atIndex(1))
                .contains("line", atIndex(2));
    }

    @Test
    public void readAllLinesFileNotExists() {
        assertThatThrownBy(() -> Files.readAllLines(newTempDir().resolve("file.big"),
                                                    Charset.defaultCharset()))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void readAllLinesDir() {
        assertThatThrownBy(() -> Files.readAllLines(newTempDir(),
                                                    Charset.defaultCharset()))
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void readAllLinesNull1() {
        assertThatThrownBy(() -> Files.readAllLines(null,
                                                    Charset.defaultCharset()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void readAllLinesNull2() {
        assertThatThrownBy(() -> Files.readAllLines(Files.createTempFile(null,
                                                                         null),
                                                    null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'cs' should be not null!");
    }

    @Test
    public void readAllLinesNull3() {
        assertThatThrownBy(() -> Files.readAllLines(null,
                                                    null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void write() {
        final Path dir = newTempDir();

        Path file = dir.resolve("file.txt");
        Files.write(file,
                    "content".getBytes());

        assertThat(Files.readAllBytes(file))
                .hasSize("content".getBytes().length)
                .isEqualTo("content".getBytes());
    }

    @Test
    public void writeDir() {
        assertThatThrownBy(() -> Files.write(newTempDir(),
                                             "content".getBytes()))
                .isInstanceOf(org.uberfire.java.nio.IOException.class)
                .hasMessage("Could not open output stream.");
    }

    @Test
    public void writeNull1() {
        assertThatThrownBy(() -> Files.write(newTempDir().resolve("file.txt"),
                                             null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'bytes' should be not null!");
    }

    @Test
    public void writeNull2() {
        assertThatThrownBy(() -> Files.write(null,
                                             "".getBytes()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void writeNull3() {
        assertThatThrownBy(() -> Files.write(null,
                                             null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void writeLines() {
        final Path dir = newTempDir();
        final List<String> content = Arrays.asList("some", "value");

        Files.write(dir.resolve("file.txt"),
                    content,
                    Charset.defaultCharset());

        final List<String> result = Files.readAllLines(dir.resolve("file.txt"),
                                                       Charset.defaultCharset());
        assertThat(result).hasSize(2)
                .contains("some", atIndex(0))
                .contains("value", atIndex(1));
    }

    @Test
    public void writeLinesDir() {
        final List<String> content = Arrays.asList("some", "value");
        assertThatThrownBy(() -> Files.write(newTempDir(),
                                             content,
                                             Charset.defaultCharset()))
                .isInstanceOf(org.uberfire.java.nio.IOException.class)
                .hasMessage("Could not open output stream.");
    }

    @Test
    public void writeLinesNull1() {
        assertThatThrownBy(() -> Files.write(newTempDir().resolve("file.txt"),
                                             null,
                                             Charset.defaultCharset()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'lines' should be not null!");
    }

    @Test
    public void writeLinesNull2() {
        final List<String> content = new ArrayList<>();
        assertThatThrownBy(() -> Files.write(null,
                                             content,
                                             Charset.defaultCharset()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void writeLinesNull4() {
        final List<String> content = new ArrayList<>();
        assertThatThrownBy(() -> Files.write(newTempDir().resolve("file.txt"),
                                             content,
                                             null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'cs' should be not null!");
    }

    @Test
    public void writeLinesNull5() {
        assertThatThrownBy(() -> Files.write(null,
                                             null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }
}
