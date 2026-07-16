/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class WorkspaceSiblingResolversTest {

    @AfterEach
    void resetResolver() {
        WorkspaceSiblingResolvers.setActive(null);
    }

    @Test
    void defaultResolverReturnsSameDirectoryDrlFiles(@TempDir Path tmp) throws Exception {
        Path a = Files.createFile(tmp.resolve("A.drl"));
        Path b = Files.createFile(tmp.resolve("B.drl"));
        Files.createFile(tmp.resolve("notes.txt"));

        List<Path> siblings = WorkspaceSiblingResolvers.active().resolveSiblings(a);

        assertThat(siblings).containsExactly(b);
    }

    @Test
    void defaultResolverHandlesNullAndMissingPaths(@TempDir Path tmp) {
        assertThat(WorkspaceSiblingResolvers.active().resolveSiblings(null)).isEmpty();
        assertThat(WorkspaceSiblingResolvers.active()
                .resolveSiblings(tmp.resolve("missing/X.drl"))).isEmpty();
    }

    @Test
    void setActiveSwapsResolverAndNullRestoresDefault(@TempDir Path tmp) throws Exception {
        Path a = Files.createFile(tmp.resolve("A.drl"));
        Path b = Files.createFile(tmp.resolve("B.drl"));

        WorkspaceSiblingResolvers.setActive(file -> List.of());
        assertThat(WorkspaceSiblingResolvers.active().resolveSiblings(a)).isEmpty();

        WorkspaceSiblingResolvers.setActive(null);
        assertThat(WorkspaceSiblingResolvers.active().resolveSiblings(a)).containsExactly(b);
    }
}
