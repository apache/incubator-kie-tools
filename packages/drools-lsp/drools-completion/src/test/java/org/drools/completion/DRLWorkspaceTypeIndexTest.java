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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DRLWorkspaceTypeIndexTest {

    @AfterEach
    void restoreDefaultResolver() {
        WorkspaceSiblingResolvers.setActive(null);
    }

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

    // ---- resolver-defined membership for open buffers ----

    @Test
    void openBufferOutsideTheResolverGroupingIsIgnored(@TempDir Path dir) throws Exception {
        Path member = dir.resolve("member.drl");
        Files.writeString(member, "declare Member\nend\n");
        Path excluded = dir.resolve("excluded.drl");
        Files.writeString(excluded, "declare Excluded\nend\n");
        Path current = dir.resolve("rules.drl");
        WorkspaceSiblingResolvers.setActive(file -> List.of(member));
        Map<Path, String> open = Map.of(excluded, "declare Excluded\nend\n");

        Map<String, DeclaredType> index =
                DRLWorkspaceTypeIndex.build("declare Foo\nend\n", current, open);

        assertThat(index).containsKeys("Foo", "Member").doesNotContainKey("Excluded");
    }

    @Test
    void openBufferOfACrossDirectorySiblingShadowsDisk(@TempDir Path dir) throws Exception {
        Path sibling = Files.createDirectories(dir.resolve("other")).resolve("types.drl");
        Files.writeString(sibling, "declare Address\n  code : String\nend\n");
        Path current = dir.resolve("rules.drl");
        WorkspaceSiblingResolvers.setActive(file -> List.of(sibling));
        Map<Path, String> open = Map.of(sibling,
                "declare Address\n  code : String\n  zip : String\nend\n");

        Map<String, DeclaredType> index =
                DRLWorkspaceTypeIndex.build("declare Foo\nend\n", current, open);

        assertThat(index.get("Address").fields).extracting(f -> f.name)
                .containsExactly("code", "zip");
    }

    @Test
    void forEachSiblingFileYieldsBufferTextForResolverSiblingsOnly(@TempDir Path dir) throws Exception {
        Path sibling = Files.createDirectories(dir.resolve("other")).resolve("types.drl");
        Files.writeString(sibling, "declare Address\nend\n");
        Path excluded = dir.resolve("excluded.drl");
        Files.writeString(excluded, "declare Excluded\nend\n");
        Path current = dir.resolve("rules.drl");
        WorkspaceSiblingResolvers.setActive(file -> List.of(sibling));
        Map<Path, String> open = Map.of(sibling, "declare Address\n  zip : String\nend\n",
                excluded, "declare Excluded\n  x : int\nend\n");
        Map<String, String> visited = new LinkedHashMap<>();

        DRLWorkspaceTypeIndex.forEachSiblingFile(current, open, visited::put);

        assertThat(visited).containsOnly(
                Map.entry(sibling.toUri().toString(), "declare Address\n  zip : String\nend\n"));
    }

    // ---- forEachSiblingInfo: one visit per sibling carrying types and imports ----

    @Test
    void forEachSiblingInfoVisitsEachSiblingOnceWithTypesAndImports(@TempDir Path dir) throws Exception {
        Path onDisk = dir.resolve("disk.drl");
        Files.writeString(onDisk, "package demo;\nimport com.example.A;\ndeclare FromDisk\nend\n");
        Path buffered = dir.resolve("buffer.drl");
        Files.writeString(buffered, "package old;\n");
        Path current = dir.resolve("rules.drl");
        Map<Path, String> open = Map.of(buffered,
                "package demo;\nimport com.example.B;\ndeclare FromBuffer\nend\n");
        List<String> visitedUris = new ArrayList<>();
        Map<String, DRLDeclaredTypeParser.FileInfo> visited = new LinkedHashMap<>();

        DRLWorkspaceTypeIndex.forEachSiblingInfo(current, open, (info, uri) -> {
            visitedUris.add(uri);
            visited.put(uri, info);
        });

        assertThat(visitedUris).containsExactly(buffered.toUri().toString(), onDisk.toUri().toString());
        DRLDeclaredTypeParser.FileInfo fromBuffer = visited.get(buffered.toUri().toString());
        assertThat(fromBuffer.packageName).isEqualTo("demo");
        assertThat(fromBuffer.imports).containsExactly("com.example.B");
        assertThat(fromBuffer.types).extracting(t -> t.name).containsExactly("FromBuffer");
        DRLDeclaredTypeParser.FileInfo fromDisk = visited.get(onDisk.toUri().toString());
        assertThat(fromDisk.packageName).isEqualTo("demo");
        assertThat(fromDisk.imports).containsExactly("com.example.A");
        assertThat(fromDisk.types).extracting(t -> t.name).containsExactly("FromDisk");
    }

    @Test
    void docForFallsBackToCurrentDocument() {
        String drl = "/** The local type. */\ndeclare Foo\nend\n";
        assertThat(DRLWorkspaceTypeIndex.docFor("Foo", drl, null, Map.of()))
                .isEqualTo("The local type.");
    }
}
