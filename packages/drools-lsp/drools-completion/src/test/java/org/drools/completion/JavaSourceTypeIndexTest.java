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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JavaSourceTypeIndexTest {

    private Path write(Path root, String rel, String content) throws Exception {
        Path p = root.resolve(rel);
        Files.createDirectories(p.getParent());
        Files.writeString(p, content);
        return p;
    }

    @Test
    void indexesTypesAndMembersFromSourceRoot(@TempDir Path root) throws Exception {
        Path src = root.resolve("src/main/java");
        write(src, "com/example/Patient.java",
            "package com.example;\npublic class Patient { private String name; public String getName(){return name;} }\n");
        write(src, "com/example/Severity.java",
            "package com.example;\npublic enum Severity { LOW, HIGH }\n");

        JavaSourceTypeIndex idx = JavaSourceTypeIndex.build(Set.of(src), List.of());

        assertTrue(idx.classNames().getOrDefault("Patient", List.of()).contains("com.example.Patient"));
        assertTrue(idx.classNames().getOrDefault("Severity", List.of()).contains("com.example.Severity"));
        assertEquals("com.example.Patient", idx.byFqcn("com.example.Patient").fqcn);
        assertTrue(idx.membersOf("com.example.Patient").stream().anyMatch(f -> f.name.equals("name")));
        assertTrue(idx.memberNames("com.example.Severity").contains("LOW"));
        assertNull(idx.memberNames("com.example.DoesNotExist"));
    }

    @Test
    void appliesPackageFilters(@TempDir Path root) throws Exception {
        Path src = root.resolve("src/main/java");
        write(src, "com/example/A.java", "package com.example;\npublic class A {}\n");
        write(src, "org/other/B.java", "package org.other;\npublic class B {}\n");

        JavaSourceTypeIndex idx = JavaSourceTypeIndex.build(Set.of(src), List.of("com.example*"));
        assertTrue(idx.classNames().containsKey("A"));
        assertTrue(!idx.classNames().containsKey("B"));
    }

    @Test
    void discoverUnionsConventionAndConfigured(@TempDir Path root) throws Exception {
        Files.createDirectories(root.resolve("module-a/src/main/java"));
        Files.createDirectories(root.resolve("custom/gen"));
        List<Path> roots = JavaSourceRoots.discover(root, List.of("custom/gen"));
        assertTrue(roots.stream().anyMatch(p -> p.endsWith(Path.of("src/main/java"))));
        assertTrue(roots.stream().anyMatch(p -> p.endsWith(Path.of("custom/gen"))));
    }

    @Test
    void emptyIsInert() {
        assertTrue(JavaSourceTypeIndex.empty().classNames().isEmpty());
        assertNull(JavaSourceTypeIndex.empty().memberNames("x"));
        assertTrue(JavaSourceTypeIndex.empty().membersOf("x").isEmpty());
    }
}
