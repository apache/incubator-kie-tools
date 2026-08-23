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

    /**
     * A configured root that contains a discovered one must collapse to the
     * outermost: indexing walks recursively, so keeping both parses every file
     * beneath the inner root twice. The ancestor is kept because it is the
     * broader instruction — naming {@code src} asks for test sources too.
     */
    @Test
    void discoverCollapsesARootNestedInsideAnother(@TempDir Path root) throws Exception {
        Files.createDirectories(root.resolve("module-a/src/main/java"));
        Files.createDirectories(root.resolve("module-a/src/test/java"));

        List<Path> roots = JavaSourceRoots.discover(root, List.of("module-a/src"));

        assertEquals(1, roots.size(), () -> "expected only the outermost root, got " + roots);
        assertTrue(roots.get(0).endsWith(Path.of("module-a/src")),
                () -> "expected module-a/src, got " + roots.get(0));
    }

    @Test
    void siblingRootsAreBothKept(@TempDir Path root) throws Exception {
        Files.createDirectories(root.resolve("module-a/src/main/java"));
        Files.createDirectories(root.resolve("module-b/src/main/java"));

        assertEquals(2, JavaSourceRoots.discover(root, List.of()).size());
    }

    /** Overlapping roots must not index the same file twice. */
    @Test
    void overlappingRootsIndexEachTypeOnce(@TempDir Path root) throws Exception {
        Path pkg = root.resolve("module-a/src/main/java/com/example");
        Files.createDirectories(pkg);
        Files.writeString(pkg.resolve("Order.java"),
                "package com.example;\npublic class Order { private int id; }\n");

        List<Path> roots = JavaSourceRoots.discover(root, List.of("module-a/src"));
        JavaSourceTypeIndex idx = JavaSourceTypeIndex.build(Set.copyOf(roots), List.of());

        assertEquals(1, idx.roots().size());
        assertEquals(List.of("com.example.Order"), idx.classNames().get("Order"));
    }

    @Test
    void emptyIsInert() {
        assertTrue(JavaSourceTypeIndex.empty().classNames().isEmpty());
        assertNull(JavaSourceTypeIndex.empty().memberNames("x"));
        assertTrue(JavaSourceTypeIndex.empty().membersOf("x").isEmpty());
    }

    @Test
    void membersOfIncludesInheritedFieldsFromSamePackageParent(@TempDir Path root) throws Exception {
        Path src = root.resolve("src/main/java");
        write(src, "com/example/Parent.java",
            "package com.example;\npublic class Parent { public String parentField; }\n");
        write(src, "com/example/Child.java",
            "package com.example;\npublic class Child extends Parent { public String childField; }\n");

        JavaSourceTypeIndex idx = JavaSourceTypeIndex.build(Set.of(src), List.of());

        assertTrue(idx.membersOf("com.example.Child").stream().anyMatch(f -> f.name.equals("parentField")));
        assertTrue(idx.memberNames("com.example.Child").contains("parentField"));
        assertTrue(idx.membersOf("com.example.Parent").stream().noneMatch(f -> f.name.equals("childField")));
    }

    @Test
    void membersOfLetsOwnFieldShadowInheritedOne(@TempDir Path root) throws Exception {
        Path src = root.resolve("src/main/java");
        write(src, "com/example/Parent.java",
            "package com.example;\npublic class Parent { public String value; }\n");
        write(src, "com/example/Child.java",
            "package com.example;\npublic class Child extends Parent { public int value; }\n");

        JavaSourceTypeIndex idx = JavaSourceTypeIndex.build(Set.of(src), List.of());

        List<Field> values = idx.membersOf("com.example.Child").stream()
                .filter(f -> f.name.equals("value")).toList();
        assertEquals(1, values.size());
        assertEquals("int", values.get(0).type);
    }

    @Test
    void membersOfResolvesCrossPackageParentByUniqueSimpleName(@TempDir Path root) throws Exception {
        Path src = root.resolve("src/main/java");
        write(src, "com/a/Child.java", "package com.a;\npublic class Child extends Base {}\n");
        write(src, "com/b/Base.java", "package com.b;\npublic class Base { public String baseField; }\n");

        JavaSourceTypeIndex idx = JavaSourceTypeIndex.build(Set.of(src), List.of());

        assertTrue(idx.membersOf("com.a.Child").stream().anyMatch(f -> f.name.equals("baseField")));
    }

    @Test
    void supertypesOfResolvesCrossPackageParentByUniqueSimpleNameToFqcn(@TempDir Path root) throws Exception {
        Path src = root.resolve("src/main/java");
        write(src, "com/a/Child.java", "package com.a;\npublic class Child extends Parent {}\n");
        write(src, "com/b/Parent.java", "package com.b;\npublic class Parent {}\n");

        JavaSourceTypeIndex idx = JavaSourceTypeIndex.build(Set.of(src), List.of());

        assertEquals(List.of("com.b.Parent"), idx.supertypesOf("com.a.Child"));
    }

    @Test
    void membersOfTerminatesOnExtendsCycle(@TempDir Path root) throws Exception {
        Path src = root.resolve("src/main/java");
        write(src, "com/example/A.java",
            "package com.example;\npublic class A extends B { public String aField; }\n");
        write(src, "com/example/B.java",
            "package com.example;\npublic class B extends A { public String bField; }\n");

        JavaSourceTypeIndex idx = JavaSourceTypeIndex.build(Set.of(src), List.of());

        List<Field> members = idx.membersOf("com.example.A");
        assertEquals(1, members.stream().filter(f -> f.name.equals("aField")).count());
        assertEquals(1, members.stream().filter(f -> f.name.equals("bField")).count());
    }
}
