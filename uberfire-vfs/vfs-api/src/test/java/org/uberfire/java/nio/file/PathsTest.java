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

import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;

public class PathsTest {

//    @Test
    public void simpleGet() {
        final Path path = Paths.get("/path/to/file.txt");

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toString()).isEqualTo("/path/to/file.txt");

        assertThat(path.getFileName()).isNotNull();
        assertThat(path.getFileName().toString()).isEqualTo("file.txt");

        assertThat(path.getNameCount()).isEqualTo(3);

        assertThat(path.getName(0)).isNotNull();
        assertThat(path.getName(0).toString()).isEqualTo("path");

        assertThat(path.getName(1)).isNotNull();
        assertThat(path.getName(1).toString()).isEqualTo("to");

        assertThat(path.getName(2)).isNotNull().isEqualTo(path.getFileName());

        assertThat(path.subpath(0, 1)).isNotNull();
        assertThat(path.subpath(0, 1).toString()).isEqualTo("path");

        assertThat(path.subpath(1, 2)).isNotNull();
        assertThat(path.subpath(1, 2).toString()).isEqualTo("to");

        assertThat(path.subpath(0, 2)).isNotNull();
        assertThat(path.subpath(0, 2).toString()).isEqualTo("path/to");

        assertThat(path.subpath(1, 3)).isNotNull();
        assertThat(path.subpath(1, 3).toString()).isEqualTo("to/file.txt");

        int i = 0;
        for (final Path currentPath : path) {
            assertThat(currentPath).isEqualTo(path.getName(i));
            i++;
        }

        assertThat(path.getParent()).isNotNull();
        assertThat(path.getParent().toString()).isEqualTo("/path/to");

        assertThat(path.getParent().getParent()).isNotNull();
        assertThat(path.getParent().getParent().toString()).isEqualTo("/path");

        assertThat(path.getParent().getParent().getParent()).isNotNull();
        assertThat(path.getParent().getParent().getParent().toString()).isEqualTo("/");

        assertThat(path.getRoot()).isNotNull();
        assertThat(path.getRoot().toString()).isNotNull().isEqualTo("/");
    }

}
