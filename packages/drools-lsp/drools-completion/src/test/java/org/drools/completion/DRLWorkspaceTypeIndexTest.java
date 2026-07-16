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
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DRLWorkspaceTypeIndexTest {

    // ---- buildLinkTargets ----

    @Test
    void nullPathYieldsNoLinkTargets() {
        assertThat(DRLWorkspaceTypeIndex.buildLinkTargets("declare Foo\nend\n", null, Map.of()))
                .isEmpty();
    }

    @Test
    void currentDocTypeGetsLineAnchoredHref(@TempDir Path dir) {
        Path current = dir.resolve("rules.drl");
        // "Foo" is declared on the second line (0-based line 1), so #L2.
        String drl = "package demo;\ndeclare Foo\n  x : int\nend\n";

        assertThat(DRLWorkspaceTypeIndex.buildLinkTargets(drl, current, Map.of()))
                .containsEntry("Foo", current.toUri() + "#L2");
    }

    @Test
    void siblingTypesAreIncludedFromDisk(@TempDir Path dir) throws Exception {
        Path sibling = dir.resolve("types.drl");
        Files.writeString(sibling, "declare Bar\nend\n");
        Path current = dir.resolve("rules.drl");

        assertThat(DRLWorkspaceTypeIndex.buildLinkTargets("declare Foo\nend\n", current, Map.of()))
                .containsEntry("Foo", current.toUri() + "#L1")
                .containsEntry("Bar", sibling.toUri() + "#L1");
    }

    @Test
    void currentDocWinsOnNameClash(@TempDir Path dir) throws Exception {
        Path sibling = dir.resolve("types.drl");
        // Sibling also declares Foo, but two lines down.
        Files.writeString(sibling, "\n\ndeclare Foo\nend\n");
        Path current = dir.resolve("rules.drl");

        assertThat(DRLWorkspaceTypeIndex.buildLinkTargets("declare Foo\nend\n", current, Map.of()))
                .containsEntry("Foo", current.toUri() + "#L1");
    }

    // ---- build (name -> DeclaredType) ----

    @Test
    void buildResolvesSiblingFromDisk(@TempDir Path dir) throws Exception {
        Files.writeString(dir.resolve("types.drl"), "declare Address\n  code : String\nend\n");
        Path current = dir.resolve("rules.drl");

        Map<String, DeclaredType> index =
                DRLWorkspaceTypeIndex.build("declare Foo\nend\n", current, Map.of());

        assertThat(index).containsKeys("Foo", "Address");
        assertThat(index.get("Address").fields).extracting(f -> f.name).containsExactly("code");
    }

    // ---- open-buffer (unsaved) shadowing ----

    @Test
    void openBufferShadowsDiskSibling(@TempDir Path dir) throws Exception {
        Path sibling = dir.resolve("types.drl");
        Files.writeString(sibling, "declare Address\n  code : String\nend\n");
        Path current = dir.resolve("rules.drl");
        // Unsaved editor buffer of the sibling adds a field not yet on disk.
        Map<Path, String> open = Map.of(sibling.toAbsolutePath().normalize(),
                "declare Address\n  code : String\n  zip : String\nend\n");

        Map<String, DeclaredType> index =
                DRLWorkspaceTypeIndex.build("declare Foo\nend\n", current, open);

        assertThat(index.get("Address").fields).extracting(f -> f.name)
                .containsExactly("code", "zip"); // the buffer version, not disk
    }

    @Test
    void openBufferDocShadowsDisk(@TempDir Path dir) throws Exception {
        Path sibling = dir.resolve("types.drl");
        Files.writeString(sibling, "/** Old doc. */\ndeclare Address\nend\n");
        Path current = dir.resolve("rules.drl");
        Map<Path, String> open = Map.of(sibling.toAbsolutePath().normalize(),
                "/** New doc. */\ndeclare Address\nend\n");

        assertThat(DRLWorkspaceTypeIndex.docFor("Address", "declare Foo\nend\n", current, open))
                .isEqualTo("New doc.");
    }

    @Test
    void docForFallsBackToCurrentDocument() {
        String drl = "/** The local type. */\ndeclare Foo\nend\n";
        assertThat(DRLWorkspaceTypeIndex.docFor("Foo", drl, null, Map.of()))
                .isEqualTo("The local type.");
    }
}
