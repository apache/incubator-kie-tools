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

import org.drools.drl.parser.antlr4.DRL10Parser;
import org.drools.drl.parser.antlr4.DRLParserHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DRLDeclaredTypeParserTest {

    private static List<DeclaredType> parse(String drl) {
        DRL10Parser parser = DRLParserHelper.createDrlParser(drl);
        parser.removeErrorListeners(); // partial/malformed DRL is expected here
        return DRLDeclaredTypeParser.extractFromCompilationUnit(parser.compilationUnit());
    }

    @Test
    void parsesDeclaredTypeWithFields() {
        String drl = "package demo;\n"
                + "declare Person\n"
                + "  name : String\n"
                + "  age : int\n"
                + "end\n";
        List<DeclaredType> types = parse(drl);

        assertThat(types).hasSize(1);
        DeclaredType person = types.get(0);
        assertThat(person.name).isEqualTo("Person");
        assertThat(person.isEnum).isFalse();
        assertThat(person.fields).extracting(f -> f.name).containsExactly("name", "age");
        assertThat(person.fields).extracting(f -> f.type).containsExactly("String", "int");
    }

    @Test
    void parsesDeclaredEnumWithConstants() {
        String drl = "declare enum Severity\n"
                + "  LOW(1), HIGH(2);\n"
                + "  level : int\n"
                + "end\n";
        List<DeclaredType> types = parse(drl);

        assertThat(types).hasSize(1);
        DeclaredType severity = types.get(0);
        assertThat(severity.isEnum).isTrue();
        assertThat(severity.fields).extracting(f -> f.name)
                .contains("LOW", "HIGH", "level");
        assertThat(severity.fields.get(0).type).isEqualTo("Severity");
        assertThat(severity.fields.get(0).args).isEqualTo("1");
    }

    @Test
    void recordsExtendsParent() {
        String drl = "declare Employee extends Person\n"
                + "  salary : double\n"
                + "end\n";
        List<DeclaredType> types = parse(drl);

        assertThat(types).hasSize(1);
        assertThat(types.get(0).extendsName).isEqualTo("Person");
    }

    @Test
    void malformedSurroundingsStillYieldPartialResults() {
        // Parse errors after the declare must neither throw nor discard the
        // already-parsed type. (Recovery from errors *before* a declare may
        // legitimately consume it — partial results are best-effort.)
        String drl = "declare Alpha\n"
                + "  id : long\n"
                + "end\n"
                + "rule broken when Person( then\n";
        List<DeclaredType> types = parse(drl);

        assertThat(types).extracting(t -> t.name).contains("Alpha");
    }

    @Test
    void nullAndEmptyTextYieldNoTypes() {
        assertThat(DRLDeclaredTypeParser.extractFromCompilationUnit(null)).isEmpty();
        assertThat(parse("")).isEmpty();
    }

    @Test
    void cachedFileParsingTracksModificationTime(@TempDir Path tmp) throws Exception {
        Path file = tmp.resolve("Types.drl");
        Files.writeString(file, "declare Alpha\n  id : long\nend\n");

        List<DeclaredType> first = DRLDeclaredTypeParser.parseDeclaredTypesCached(file);
        assertThat(first).extracting(t -> t.name).containsExactly("Alpha");

        // Unchanged file: the cached list is served (same instance).
        assertThat(DRLDeclaredTypeParser.parseDeclaredTypesCached(file)).isSameAs(first);

        // Changed content with a new mtime is re-parsed.
        Files.writeString(file, "declare Beta\n  id : long\nend\n");
        Files.setLastModifiedTime(file, java.nio.file.attribute.FileTime.fromMillis(
                Files.getLastModifiedTime(file).toMillis() + 5_000));
        assertThat(DRLDeclaredTypeParser.parseDeclaredTypesCached(file))
                .extracting(t -> t.name).containsExactly("Beta");
    }

    @Test
    void cachedFileParsingHandlesMissingFiles(@TempDir Path tmp) {
        assertThat(DRLDeclaredTypeParser.parseDeclaredTypesCached(tmp.resolve("missing.drl"))).isEmpty();
        assertThat(DRLDeclaredTypeParser.parseDeclaredTypesCached(null)).isEmpty();
    }
}
