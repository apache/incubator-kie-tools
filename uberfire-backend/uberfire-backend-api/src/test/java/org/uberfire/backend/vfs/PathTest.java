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

package org.uberfire.backend.vfs;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.uberfire.backend.vfs.PathFactory.newPath;

public class PathTest {

    @Test
    public void generalState() {
        {
            final Path path = newPath("resource",
                                      "scheme://path/to/some/resource");
            assertThat(path).isEqualTo(path);
            assertThat(path).isEqualTo(newPath("resource",
                                               "scheme://path/to/some/resource"));
            assertThat(path.hashCode()).isEqualTo(newPath("resource",
                                                          "scheme://path/to/some/resource").hashCode());
            assertThat(path.hashCode()).isEqualTo(path.hashCode());
        }

        {
            final Path path = newPath("resource",
                                      "scheme://different/path/to/some/resource");
            assertThat(path.equals(newPath("resource",
                                           "scheme://path/to/some/resource"))).isFalse();
            assertThat(path.hashCode()).isNotEqualTo(newPath("resource",
                                                             "scheme://path/to/some/resource").hashCode());
        }

        {
            final Path path = newPath("resource",
                                      "scheme://different/path/to/some/resource");
            assertThat(path.equals("something")).isFalse();
            assertThat(path.equals(null)).isFalse();
        }
    }

    @Test
    public void checkNPE() {
        final Map<Path, String> hashMap = new HashMap<>();
        final Path path = newPath("defaultPackage",
                                  "default://guvnor-jcr2vfs-migration/defaultPackage/");
        hashMap.put(path,
                    "content");
        assertThat(hashMap.get(path)).isEqualTo("content");

        assertThat(hashMap.get(newPath("defaultPackage",
                                       "default://guvnor-jcr2vfs-migration/defaultPackage/"))).isEqualTo("content");
    }
}
