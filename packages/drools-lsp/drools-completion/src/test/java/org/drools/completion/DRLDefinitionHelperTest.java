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
import java.util.Set;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DRLDefinitionHelperTest {

    private static final String DECLARE_DRL = """
            package demo;

            declare Person
              name : String
            end

            rule R
              when
                Person( name == "x" )
              then
            end
            """;

    @Test
    void declaredTypeDefinitionInSameDocument() {
        // Caret on "Person" inside the pattern.
        List<Location> defs = DRLDefinitionHelper.findDefinitions(
                "myDocument", DECLARE_DRL, new Position(8, 6),
                ClassIndex.empty(), Set.of());

        assertThat(defs).hasSize(1);
        Location loc = defs.get(0);
        assertThat(loc.getUri()).isEqualTo("myDocument");
        assertThat(loc.getRange().getStart().getLine()).isEqualTo(2);
        assertThat(loc.getRange().getStart().getCharacter()).isEqualTo(8);
    }

    @Test
    void javaSourceDefinitionViaMavenConvention(@TempDir Path module) throws Exception {
        // Maven layout: target/classes holds the compiled class, the source
        // sits at the conventional src/main/java location.
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

        // Caret on "Pet" in the pattern.
        List<Location> defs = DRLDefinitionHelper.findDefinitions(
                "myDocument", drl, new Position(6, 5),
                ClassIndex.empty(), Set.of(module.resolve("target/classes")));

        assertThat(defs).hasSize(1);
        Location loc = defs.get(0);
        assertThat(loc.getUri()).isEqualTo(petJava.toUri().toString());
        assertThat(loc.getRange().getStart().getLine()).isEqualTo(1);
        assertThat(loc.getRange().getStart().getCharacter()).isEqualTo(13);
    }

    @Test
    void declaredTypeWinsOverJavaSourceOfTheSameName(@TempDir Path module) throws Exception {
        Path classes = module.resolve("target/classes/demo");
        Files.createDirectories(classes);
        Files.createFile(classes.resolve("Person.class"));
        Path srcDir = module.resolve("src/main/java/demo");
        Files.createDirectories(srcDir);
        Files.writeString(srcDir.resolve("Person.java"),
                "package demo;\npublic class Person {\n}\n");

        List<Location> defs = DRLDefinitionHelper.findDefinitions(
                "myDocument", DECLARE_DRL, new Position(8, 6),
                ClassIndex.empty(), Set.of(module.resolve("target/classes")));

        assertThat(defs).hasSize(1);
        assertThat(defs.get(0).getUri()).isEqualTo("myDocument");
    }

    @Test
    void declaredTypeDefinitionInSiblingFile(@TempDir Path dir) throws Exception {
        Path types = dir.resolve("Types.drl");
        Files.writeString(types,
                "package demo;\ndeclare Person\n  name : String\nend\n");
        Path current = dir.resolve("rules.drl");
        String drl = "package demo;\nrule R\n  when\n    Person( )\n  then\nend\n";
        Files.writeString(current, drl);

        List<Location> defs = DRLDefinitionHelper.findDefinitions(
                current.toUri().toString(), drl, new Position(3, 5),
                ClassIndex.empty(), Set.of());

        assertThat(defs).hasSize(1);
        assertThat(defs.get(0).getUri()).isEqualTo(types.toUri().toString());
        assertThat(defs.get(0).getRange().getStart().getLine()).isEqualTo(1);
        assertThat(defs.get(0).getRange().getStart().getCharacter()).isEqualTo(8);
    }

    @Test
    void caretInCommentYieldsNoDefinitions() {
        String drl = """
                package demo;
                declare Person
                  name : String
                end
                rule R
                  when
                    // Person mentioned
                    Person( )
                  then
                end
                """;
        // Caret on "Person" inside the LHS // comment (line 6) — not a real use.
        List<Location> defs = DRLDefinitionHelper.findDefinitions(
                "myDocument", drl, new Position(6, 9),
                ClassIndex.empty(), Set.of());

        assertThat(defs).isEmpty();
    }

    @Test
    void unknownSymbolYieldsNoDefinitions() {
        List<Location> defs = DRLDefinitionHelper.findDefinitions(
                "myDocument", DECLARE_DRL, new Position(8, 14),
                ClassIndex.empty(), Set.of());
        // "name" is a field, not a definable type in v1.
        assertThat(defs).isEmpty();
    }

    @Test
    void findDefinitionsParsesTheCurrentDocumentOnce() {
        // No siblings (non-file URI), so every parser built is for the current doc.
        DRLParsers.resetParseCount();
        DRLDefinitionHelper.findDefinitions(
                "myDocument", DECLARE_DRL, new Position(8, 6), ClassIndex.empty(), Set.of());
        assertThat(DRLParsers.parseCount()).isEqualTo(1);
    }

    @Test
    void nullTextYieldsNoDefinitions() {
        assertThat(DRLDefinitionHelper.findDefinitions(
                "myDocument", null, new Position(0, 0), ClassIndex.empty(), Set.of()))
                .isEmpty();
    }
}
