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
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DRLRenameHelperTest {

    private static final String PERSON_DRL = """
            package demo;
            declare Person
              name : String
            end
            rule R
              when
                Person( )
              then
                insert(new Person());
            end
            """;

    // --- prepare ---------------------------------------------------------

    @Test
    void prepareReturnsRangeAndNameForDeclaredType() {
        // Caret on the pattern "Person" (line 6, cols 4..10).
        DRLRenameHelper.Prepared p = DRLRenameHelper.prepare(
                "myDocument", PERSON_DRL, new Position(6, 5),
                Map.of(), ClassIndex.empty(), Set.of());

        assertThat(p).isNotNull();
        assertThat(p.placeholder).isEqualTo("Person");
        assertThat(p.range.getStart().getLine()).isEqualTo(6);
        assertThat(p.range.getStart().getCharacter()).isEqualTo(4);
        assertThat(p.range.getEnd().getCharacter()).isEqualTo(10);
    }

    @Test
    void prepareReturnsNullForClasspathType() {
        String drl = """
                package demo;
                import org.example.Pet;
                rule R
                  when
                    Pet( )
                  then
                end
                """;
        // Caret on "Pet" (line 4) — resolves to a classpath type, not renameable.
        assertThat(DRLRenameHelper.prepare(
                "myDocument", drl, new Position(4, 5),
                Map.of(), ClassIndex.empty(), Set.of()))
                .isNull();
    }

    @Test
    void prepareReturnsNullWhenCaretInComment() {
        String drl = """
                package demo;
                declare Person
                  name : String
                end
                rule R
                  when
                    Person( )
                  then
                    // Person here
                    insert(new Person());
                end
                """;
        // Caret on "Person" inside the RHS // comment (line 8).
        assertThat(DRLRenameHelper.prepare(
                "myDocument", drl, new Position(8, 9),
                Map.of(), ClassIndex.empty(), Set.of()))
                .isNull();
    }

    @Test
    void prepareReturnsRangeForBoundVariable() {
        String drl = "rule A\n  when\n    $p : Person( )\n  then\n    update($p);\nend\n";
        // Caret on "$p" (line 2, cols 4..6).
        DRLRenameHelper.Prepared p = DRLRenameHelper.prepare(
                "myDocument", drl, new Position(2, 5),
                Map.of(), ClassIndex.empty(), Set.of());

        assertThat(p).isNotNull();
        assertThat(p.placeholder).isEqualTo("$p");
        assertThat(p.range.getStart().getCharacter()).isEqualTo(4);
        assertThat(p.range.getEnd().getCharacter()).isEqualTo(6);
    }

    // --- rename ----------------------------------------------------------

    @Test
    void renameDeclaredTypeRewritesEveryUse() {
        WorkspaceEdit edit = DRLRenameHelper.rename(
                "myDocument", PERSON_DRL, new Position(6, 5), "Customer",
                Map.of(), ClassIndex.empty(), Set.of());

        assertThat(edit).isNotNull();
        List<TextEdit> edits = edit.getChanges().get("myDocument");
        // declare, pattern, RHS new
        assertThat(edits).hasSize(3);
        assertThat(edits).allSatisfy(e -> assertThat(e.getNewText()).isEqualTo("Customer"));
    }

    @Test
    void renameDeclaredTypeSpansSiblingFiles(@TempDir Path dir) throws Exception {
        Path types = dir.resolve("Types.drl");
        Files.writeString(types, "package demo;\ndeclare Person\n  name : String\nend\n");
        Path rules = dir.resolve("rules.drl");
        String rulesDrl = "package demo;\nrule R\n  when\n    Person( )\n  then\nend\n";
        Files.writeString(rules, rulesDrl);

        WorkspaceEdit edit = DRLRenameHelper.rename(
                rules.toUri().toString(), rulesDrl, new Position(3, 5), "Customer",
                Map.of(), ClassIndex.empty(), Set.of());

        assertThat(edit).isNotNull();
        Map<String, List<TextEdit>> changes = edit.getChanges();
        assertThat(changes).containsKeys(rules.toUri().toString(), types.toUri().toString());
        assertThat(changes.get(types.toUri().toString())).hasSize(1); // the declare site
        assertThat(changes.get(rules.toUri().toString())).hasSize(1); // the pattern use
    }

    @Test
    void renameBoundVariableNormalizesDollarAndStaysInRule() {
        String drl = """
                rule A
                  when
                    $p : Person( )
                  then
                    update($p);
                end

                rule B
                  when
                    $p : Account( )
                  then
                    update($p);
                end
                """;
        // Caret on "$p" in rule A (line 2); new name given without '$'.
        WorkspaceEdit edit = DRLRenameHelper.rename(
                "myDocument", drl, new Position(2, 5), "q",
                Map.of(), ClassIndex.empty(), Set.of());

        assertThat(edit).isNotNull();
        List<TextEdit> edits = edit.getChanges().get("myDocument");
        assertThat(edits).hasSize(2); // rule A only: binding site + RHS use
        assertThat(edits).allSatisfy(e -> assertThat(e.getNewText()).isEqualTo("$q"));
        assertThat(edits).allSatisfy(e -> assertThat(e.getRange().getStart().getLine()).isLessThan(6));
    }

    @Test
    void renameReturnsNullWhenCaretInComment() {
        String drl = """
                package demo;
                declare Person
                  name : String
                end
                rule R
                  when
                    Person( )
                  then
                    // Person here
                    insert(new Person());
                end
                """;
        // Caret on "Person" inside the RHS // comment (line 8).
        assertThat(DRLRenameHelper.rename(
                "myDocument", drl, new Position(8, 9), "Customer",
                Map.of(), ClassIndex.empty(), Set.of()))
                .isNull();
    }

    @Test
    void renameRejectsClasspathType() {
        String drl = """
                package demo;
                import org.example.Pet;
                rule R
                  when
                    Pet( )
                  then
                end
                """;
        assertThat(DRLRenameHelper.rename(
                "myDocument", drl, new Position(4, 5), "Animal",
                Map.of(), ClassIndex.empty(), Set.of()))
                .isNull();
    }

    @Test
    void prepareParsesTheCurrentDocumentOnce() {
        // No siblings (non-file URI), so every parser built is for the current doc.
        DRLParsers.resetParseCount();
        DRLRenameHelper.prepare(
                "myDocument", PERSON_DRL, new Position(6, 5),
                Map.of(), ClassIndex.empty(), Set.of());
        assertThat(DRLParsers.parseCount()).isEqualTo(1);
    }

    @Test
    void renameParsesTheCurrentDocumentOnce() {
        // rename shares its single parse with the find-references call it delegates to.
        DRLParsers.resetParseCount();
        DRLRenameHelper.rename(
                "myDocument", PERSON_DRL, new Position(6, 5), "Customer",
                Map.of(), ClassIndex.empty(), Set.of());
        assertThat(DRLParsers.parseCount()).isEqualTo(1);
    }

    @Test
    void renameRejectsIllegalNewName() {
        assertThat(DRLRenameHelper.rename(
                "myDocument", PERSON_DRL, new Position(6, 5), "1bad",
                Map.of(), ClassIndex.empty(), Set.of()))
                .isNull();
        assertThat(DRLRenameHelper.rename(
                "myDocument", PERSON_DRL, new Position(6, 5), "has space",
                Map.of(), ClassIndex.empty(), Set.of()))
                .isNull();
    }
}
