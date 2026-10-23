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
import java.util.Map;
import java.util.Set;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.TypeHierarchyItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DRLTypeHierarchyHelperTest {

    private static final String HIERARCHY_DRL = """
            package demo;

            declare Animal
              legs : int
            end

            declare Dog extends Animal
              good : boolean
            end
            """;

    // --- prepare ---------------------------------------------------------

    @Test
    void prepareResolvesDeclareInSameDocument() {
        // Caret on "Dog" at its declare site (line 6, cols 8..11).
        List<TypeHierarchyItem> items = DRLTypeHierarchyHelper.prepare(
                HIERARCHY_DRL, new Position(6, 9), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), JavaSourceTypeIndex.empty());

        assertThat(items).hasSize(1);
        TypeHierarchyItem item = items.get(0);
        assertThat(item.getName()).isEqualTo("Dog");
        assertThat(item.getKind()).isEqualTo(SymbolKind.Class);
        assertThat(item.getUri()).isEqualTo("myDocument");
        assertThat(item.getData()).as("declare items carry no classpath data").isNull();
        assertThat(item.getDetail()).isEqualTo("extends Animal");
        assertThat(item.getRange().getStart().getLine()).isEqualTo(6);
        assertThat(item.getRange().getStart().getCharacter()).isEqualTo(8);
    }

    @Test
    void prepareResolvesClasspathTypeToJavaSource(@TempDir Path module) throws Exception {
        Path classes = module.resolve("target/classes/org/example");
        Files.createDirectories(classes);
        Files.createFile(classes.resolve("Pet.class"));
        Path srcDir = module.resolve("src/main/java/org/example");
        Files.createDirectories(srcDir);
        Path petJava = srcDir.resolve("Pet.java");
        Files.writeString(petJava, "package org.example;\npublic class Pet {\n}\n");

        String drl = """
                package demo;

                import org.example.Pet;

                rule R
                  when
                    Pet( )
                  then
                end
                """;

        // Caret on "Pet" in the pattern (line 6, col 5).
        List<TypeHierarchyItem> items = DRLTypeHierarchyHelper.prepare(
                drl, new Position(6, 5), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(module.resolve("target/classes")),
                JavaSourceTypeIndex.empty());

        assertThat(items).hasSize(1);
        TypeHierarchyItem item = items.get(0);
        assertThat(item.getName()).isEqualTo("Pet");
        assertThat(item.getUri()).isEqualTo(petJava.toUri().toString());
        assertThat(item.getData()).as("classpath items round-trip their FQCN in data")
                .isEqualTo("org.example.Pet");
    }

    @Test
    void prepareResolvesClasspathTypeToSourceIndexBeforeBuild(@TempDir Path srcRoot) throws Exception {
        // No target/classes at all — the type has never been compiled.
        Path petDir = srcRoot.resolve("org/example");
        Files.createDirectories(petDir);
        Path petJava = petDir.resolve("Pet.java");
        Files.writeString(petJava, "package org.example;\npublic class Pet {\n}\n");

        JavaSourceTypeIndex sourceIndex = JavaSourceTypeIndex.build(Set.of(srcRoot), List.of());

        String drl = """
                package demo;

                import org.example.Pet;

                rule R
                  when
                    Pet( )
                  then
                end
                """;

        // Caret on "Pet" in the pattern (line 6, col 5); buildOutputDirs is
        // empty so JavaSourceLocator.locate has nothing to find.
        List<TypeHierarchyItem> items = DRLTypeHierarchyHelper.prepare(
                drl, new Position(6, 5), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), sourceIndex);

        assertThat(items).hasSize(1);
        TypeHierarchyItem item = items.get(0);
        assertThat(item.getName()).isEqualTo("Pet");
        assertThat(item.getKind()).isEqualTo(SymbolKind.Class);
        assertThat(item.getUri()).isEqualTo(petJava.toUri().toString());
        assertThat(item.getData()).as("classpath items round-trip their FQCN in data")
                .isEqualTo("org.example.Pet");
    }

    @Test
    void prepareResolvesEnumClasspathTypeToSourceIndexWithEnumKind(@TempDir Path srcRoot) throws Exception {
        Path dir = srcRoot.resolve("org/example");
        Files.createDirectories(dir);
        Path statusJava = dir.resolve("Status.java");
        Files.writeString(statusJava, "package org.example;\npublic enum Status {\n  ON, OFF\n}\n");

        JavaSourceTypeIndex sourceIndex = JavaSourceTypeIndex.build(Set.of(srcRoot), List.of());

        String drl = """
                package demo;

                import org.example.Status;

                rule R
                  when
                    Status( )
                  then
                end
                """;

        // Caret on "Status" in the pattern (line 6, col 8).
        List<TypeHierarchyItem> items = DRLTypeHierarchyHelper.prepare(
                drl, new Position(6, 8), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), sourceIndex);

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getKind()).isEqualTo(SymbolKind.Enum);
    }

    @Test
    void prepareYieldsNothingForUnknownWord() {
        assertThat(DRLTypeHierarchyHelper.prepare(
                HIERARCHY_DRL, new Position(3, 3), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), JavaSourceTypeIndex.empty()))
                .isEmpty();
    }

    @Test
    void prepareYieldsNothingForNullText() {
        assertThat(DRLTypeHierarchyHelper.prepare(
                null, new Position(0, 0), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), JavaSourceTypeIndex.empty()))
                .isEmpty();
    }

    // --- supertypes ------------------------------------------------------

    @Test
    void supertypesOfDeclareResolvesExtendsParentInSameDocument() {
        TypeHierarchyItem dog = DRLTypeHierarchyHelper.prepare(
                HIERARCHY_DRL, new Position(6, 9), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), JavaSourceTypeIndex.empty()).get(0);

        List<TypeHierarchyItem> supers = DRLTypeHierarchyHelper.supertypes(
                dog, HIERARCHY_DRL, Map.of(), ClassIndex.empty(),
                ClassMemberIndex.empty(), Set.of(), JavaSourceTypeIndex.empty());

        assertThat(supers).hasSize(1);
        TypeHierarchyItem parent = supers.get(0);
        assertThat(parent.getName()).isEqualTo("Animal");
        assertThat(parent.getUri()).isEqualTo("myDocument");
        assertThat(parent.getData()).isNull();
        assertThat(parent.getRange().getStart().getLine()).isEqualTo(2);
        assertThat(parent.getRange().getStart().getCharacter()).isEqualTo(8);
    }

    @Test
    void supertypesOfDeclareWithoutExtendsYieldsNothing() {
        TypeHierarchyItem animal = DRLTypeHierarchyHelper.prepare(
                HIERARCHY_DRL, new Position(2, 9), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), JavaSourceTypeIndex.empty()).get(0);

        assertThat(DRLTypeHierarchyHelper.supertypes(
                animal, HIERARCHY_DRL, Map.of(), ClassIndex.empty(),
                ClassMemberIndex.empty(), Set.of(), JavaSourceTypeIndex.empty()))
                .isEmpty();
    }

    @Test
    void supertypesOfDeclareResolvesClasspathParentViaMavenConvention(@TempDir Path module) throws Exception {
        Path classes = module.resolve("target/classes/org/example");
        Files.createDirectories(classes);
        Files.createFile(classes.resolve("Animal.class"));
        Path srcDir = module.resolve("src/main/java/org/example");
        Files.createDirectories(srcDir);
        Path animalJava = srcDir.resolve("Animal.java");
        Files.writeString(animalJava, "package org.example;\npublic class Animal {\n}\n");

        String drl = """
                package demo;

                import org.example.Animal;

                declare Dog extends Animal
                  good : boolean
                end
                """;

        TypeHierarchyItem dog = DRLTypeHierarchyHelper.prepare(
                drl, new Position(4, 9), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(module.resolve("target/classes")),
                JavaSourceTypeIndex.empty()).get(0);

        List<TypeHierarchyItem> supers = DRLTypeHierarchyHelper.supertypes(
                dog, drl, Map.of(), ClassIndex.empty(), ClassMemberIndex.empty(),
                Set.of(module.resolve("target/classes")), JavaSourceTypeIndex.empty());

        assertThat(supers).hasSize(1);
        TypeHierarchyItem parent = supers.get(0);
        assertThat(parent.getName()).isEqualTo("Animal");
        assertThat(parent.getUri()).isEqualTo(animalJava.toUri().toString());
        assertThat(parent.getData()).isEqualTo("org.example.Animal");
    }

    @Test
    void supertypesOfDeclareResolvesSourceIndexParentBeforeBuild(@TempDir Path srcRoot) throws Exception {
        // No target/classes at all — the type has never been compiled.
        Path dir = srcRoot.resolve("org/example");
        Files.createDirectories(dir);
        Path animalJava = dir.resolve("Animal.java");
        Files.writeString(animalJava, "package org.example;\npublic class Animal {\n}\n");

        JavaSourceTypeIndex sourceIndex = JavaSourceTypeIndex.build(Set.of(srcRoot), List.of());

        String drl = """
                package demo;

                import org.example.Animal;

                declare Dog extends Animal
                  good : boolean
                end
                """;

        TypeHierarchyItem dog = DRLTypeHierarchyHelper.prepare(
                drl, new Position(4, 9), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), sourceIndex).get(0);

        List<TypeHierarchyItem> supers = DRLTypeHierarchyHelper.supertypes(
                dog, drl, Map.of(), ClassIndex.empty(), ClassMemberIndex.empty(),
                Set.of(), sourceIndex);

        assertThat(supers).hasSize(1);
        TypeHierarchyItem parent = supers.get(0);
        assertThat(parent.getName()).isEqualTo("Animal");
        assertThat(parent.getUri()).isEqualTo(animalJava.toUri().toString());
        assertThat(parent.getData()).isEqualTo("org.example.Animal");
    }

    @Test
    void supertypesOfSourceOnlyClasspathItemResolvesCrossPackageParentToFqcn(
            @TempDir Path srcRoot) throws Exception {
        // Child and Parent are both source-only (no target/classes anywhere)
        // and live in different packages, so the parent can only be found by
        // its unique simple name — the same seam JavaSourceTypeIndex#supertypesOf
        // must resolve to an FQCN for classpathItem's FQCN-keyed lookups to work.
        Path childDir = srcRoot.resolve("com/a");
        Files.createDirectories(childDir);
        Files.writeString(childDir.resolve("Child.java"),
                "package com.a;\npublic class Child extends Parent {\n}\n");
        Path parentDir = srcRoot.resolve("com/b");
        Files.createDirectories(parentDir);
        Path parentJava = parentDir.resolve("Parent.java");
        Files.writeString(parentJava, "package com.b;\npublic class Parent {\n}\n");

        JavaSourceTypeIndex sourceIndex = JavaSourceTypeIndex.build(Set.of(srcRoot), List.of());
        ClassMemberIndex memberIndex = ClassMemberIndex.of(Set.<Path>of());
        memberIndex.setSourceFallback(sourceIndex);

        String drl = """
                package demo;

                import com.a.Child;

                rule R
                  when
                    Child( )
                  then
                end
                """;

        TypeHierarchyItem child = DRLTypeHierarchyHelper.prepare(
                drl, new Position(6, 5), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), sourceIndex).get(0);

        List<TypeHierarchyItem> supers = DRLTypeHierarchyHelper.supertypes(
                child, drl, Map.of(), ClassIndex.empty(), memberIndex, Set.of(), sourceIndex);

        assertThat(supers).hasSize(1);
        TypeHierarchyItem parent = supers.get(0);
        assertThat(parent.getName()).isEqualTo("Parent");
        assertThat(parent.getUri()).isEqualTo(parentJava.toUri().toString());
        assertThat(parent.getData()).isEqualTo("com.b.Parent");
    }

    @Test
    void supertypesOfClasspathItemReflectsAncestry() {
        // A classpath item whose FQCN is a JDK type with a clear, stable
        // superclass + interfaces; reflected one level via ClassMemberIndex.
        // No project source resolves for JDK types, so all parents are
        // dropped (navigable-only) and the result is empty — but the call
        // must run the reflection path without error.
        Range origin = new Range(new Position(0, 0), new Position(0, 0));
        TypeHierarchyItem arrayList = new TypeHierarchyItem(
                "ArrayList", SymbolKind.Class, "jdk", origin, origin);
        arrayList.setData("java.util.ArrayList");

        ClassMemberIndex memberIndex = new ClassMemberIndex(getClass().getClassLoader());

        assertThat(DRLTypeHierarchyHelper.supertypes(
                arrayList, null, Map.of(), ClassIndex.empty(), memberIndex, Set.of(),
                JavaSourceTypeIndex.empty()))
                .isEmpty();
    }

    // --- subtypes --------------------------------------------------------

    @Test
    void subtypesOfDeclareFindsChildrenInSameDocument() {
        TypeHierarchyItem animal = DRLTypeHierarchyHelper.prepare(
                HIERARCHY_DRL, new Position(2, 9), "myDocument",
                Map.of(), ClassIndex.empty(), Set.of(), JavaSourceTypeIndex.empty()).get(0);

        List<TypeHierarchyItem> subs = DRLTypeHierarchyHelper.subtypes(
                animal, HIERARCHY_DRL, Map.of());

        assertThat(subs).hasSize(1);
        assertThat(subs.get(0).getName()).isEqualTo("Dog");
        assertThat(subs.get(0).getUri()).isEqualTo("myDocument");
    }

    @Test
    void subtypesOfDeclareFindsChildrenInSiblingFile(@TempDir Path dir) throws Exception {
        Path animalFile = dir.resolve("Animal.drl");
        Files.writeString(animalFile, "package demo;\ndeclare Animal\n  legs : int\nend\n");
        Path dogFile = dir.resolve("Dog.drl");
        Files.writeString(dogFile,
                "package demo;\ndeclare Dog extends Animal\n  good : boolean\nend\n");

        String animalText = Files.readString(animalFile);
        TypeHierarchyItem animal = DRLTypeHierarchyHelper.prepare(
                animalText, new Position(1, 9), animalFile.toUri().toString(),
                Map.of(), ClassIndex.empty(), Set.of(), JavaSourceTypeIndex.empty()).get(0);

        List<TypeHierarchyItem> subs = DRLTypeHierarchyHelper.subtypes(
                animal, animalText, Map.of());

        assertThat(subs).hasSize(1);
        assertThat(subs.get(0).getName()).isEqualTo("Dog");
        assertThat(subs.get(0).getUri()).isEqualTo(dogFile.toUri().toString());
    }

    @Test
    void subtypesOfClasspathItemAreNotEnumerable() {
        Range origin = new Range(new Position(0, 0), new Position(0, 0));
        TypeHierarchyItem classpath = new TypeHierarchyItem(
                "Pet", SymbolKind.Class, "file:///Pet.java", origin, origin);
        classpath.setData("org.example.Pet");

        assertThat(DRLTypeHierarchyHelper.subtypes(classpath, "package demo;", Map.of()))
                .isEmpty();
    }
}
