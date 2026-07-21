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

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DRLReferencesHelperTest {

    // "Person" used at every structured position plus the RHS consequence.
    private static final String TYPE_USES_DRL = """
            package demo;

            declare Person
              name : String
            end

            declare Employee extends Person
              salary : double
              manager : Person
            end

            global Person currentPerson

            rule R
              when
                Person( name == "x" )
              then
                insert(new Person());
            end
            """;

    private static List<Integer> startLines(List<Location> locations) {
        return locations.stream().map(l -> l.getRange().getStart().getLine()).sorted().toList();
    }

    @Test
    void declaredTypeReferencesAcrossEveryStructuredPositionAndRhs() {
        // Caret on the pattern "Person" (line 15, col 4..10).
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", TYPE_USES_DRL, new Position(15, 5),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        assertThat(refs).allSatisfy(l -> assertThat(l.getUri()).isEqualTo("myDocument"));
        // declare(2), extends(6), field type(8), global(11), pattern(15), RHS new(17)
        assertThat(startLines(refs)).containsExactly(2, 6, 8, 11, 15, 17);
    }

    @Test
    void excludesDeclarationSiteWhenNotRequested() {
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", TYPE_USES_DRL, new Position(15, 5),
                Map.of(), ClassIndex.empty(), Set.of(), false);

        // The declare site (line 2) drops out; every use remains.
        assertThat(startLines(refs)).containsExactly(6, 8, 11, 15, 17);
    }

    @Test
    void ignoresStringsAndComments() {
        String drl = """
                package demo;
                declare Person
                  name : String
                end
                rule R
                  when
                    Person( name == "Person" )
                  then
                    // Person in a comment
                    insert(new Person());
                end
                """;
        // Caret on the pattern "Person" (line 6).
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", drl, new Position(6, 5),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        // declare(1), pattern(6), RHS new(9). The "Person" string literal and the
        // // Person comment must NOT be matched.
        assertThat(startLines(refs)).containsExactly(1, 6, 9);
        assertThat(startLines(refs)).doesNotContain(8); // the comment line
    }

    @Test
    void caretInCommentYieldsNoReferences() {
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
        // Caret on "Person" inside the RHS // comment (line 8) — not a real use.
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", drl, new Position(8, 9),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        assertThat(refs).isEmpty();
    }

    @Test
    void caretInStringLiteralYieldsNoReferences() {
        String drl = """
                package demo;
                declare Person
                  name : String
                end
                rule R
                  when
                    Person( name == "Person" )
                  then
                end
                """;
        // Caret on "Person" inside the "Person" string literal (line 6).
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", drl, new Position(6, 23),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        assertThat(refs).isEmpty();
    }

    @Test
    void caretInCommentOnBindingYieldsNoReferences() {
        String drl = """
                rule A
                  when
                    $p : Person( )
                  then
                    // touch $p
                    update($p);
                end
                """;
        // Caret on "$p" inside the RHS // comment (line 4).
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", drl, new Position(4, 14),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        assertThat(refs).isEmpty();
    }

    @Test
    void boundVariableReferencesAreScopedToTheEnclosingRule() {
        String drl = """
                rule A
                  when
                    $p : Person( age > 18 )
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
        // Caret on "$p" in rule A (line 2).
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", drl, new Position(2, 5),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        // Only rule A: the binding site (line 2) and its RHS use (line 4).
        assertThat(startLines(refs)).containsExactly(2, 4);
    }

    @Test
    void boundVariableRangeSpansTheDollarPrefix() {
        String drl = """
                rule A
                  when
                    $p : Person( )
                  then
                    update($p);
                end
                """;
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", drl, new Position(2, 5),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        // The binding declaration "$p" sits at line 2, cols 4..6 ("$p").
        Location decl = refs.stream()
                .filter(l -> l.getRange().getStart().getLine() == 2).findFirst().orElseThrow();
        assertThat(decl.getRange().getStart().getCharacter()).isEqualTo(4);
        assertThat(decl.getRange().getEnd().getCharacter()).isEqualTo(6);
    }

    @Test
    void classpathTypeReferencesResolveViaImport() {
        String drl = """
                package demo;
                import org.example.Pet;
                rule R
                  when
                    Pet( )
                  then
                    insert(new Pet());
                end
                """;
        // Caret on the pattern "Pet" (line 4).
        List<Location> refs = DRLReferencesHelper.references(
                "myDocument", drl, new Position(4, 5),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        // import(1), pattern(4), RHS new(6).
        assertThat(startLines(refs)).containsExactly(1, 4, 6);
    }

    @Test
    void referencesSpanSiblingFiles(@TempDir Path dir) throws Exception {
        Path types = dir.resolve("Types.drl");
        Files.writeString(types, "package demo;\ndeclare Person\n  name : String\nend\n");
        Path rules = dir.resolve("rules.drl");
        String rulesDrl = "package demo;\nrule R\n  when\n    Person( )\n  then\nend\n";
        Files.writeString(rules, rulesDrl);

        // Caret on "Person" in rules.drl (line 3).
        List<Location> refs = DRLReferencesHelper.references(
                rules.toUri().toString(), rulesDrl, new Position(3, 5),
                Map.of(), ClassIndex.empty(), Set.of(), true);

        assertThat(refs).hasSize(2);
        assertThat(refs).anySatisfy(l -> {
            assertThat(l.getUri()).isEqualTo(rules.toUri().toString());
            assertThat(l.getRange().getStart().getLine()).isEqualTo(3); // the pattern use
        });
        assertThat(refs).anySatisfy(l -> {
            assertThat(l.getUri()).isEqualTo(types.toUri().toString());
            assertThat(l.getRange().getStart().getLine()).isEqualTo(1); // the declare site
        });
    }

    @Test
    void siblingDeclarationExcludedWhenIncludeDeclarationFalse(@TempDir Path dir) throws Exception {
        Path types = dir.resolve("Types.drl");
        Files.writeString(types, "package demo;\ndeclare Person\n  name : String\nend\n");
        Path rules = dir.resolve("rules.drl");
        String rulesDrl = "package demo;\nrule R\n  when\n    Person( )\n  then\nend\n";
        Files.writeString(rules, rulesDrl);

        List<Location> refs = DRLReferencesHelper.references(
                rules.toUri().toString(), rulesDrl, new Position(3, 5),
                Map.of(), ClassIndex.empty(), Set.of(), false);

        assertThat(refs).hasSize(1);
        assertThat(refs.get(0).getUri()).isEqualTo(rules.toUri().toString());
    }

    @Test
    void unknownWordYieldsNoReferences() {
        // Caret on a blank line.
        assertThat(DRLReferencesHelper.references(
                "myDocument", TYPE_USES_DRL, new Position(1, 0),
                Map.of(), ClassIndex.empty(), Set.of(), true))
                .isEmpty();
    }

    @Test
    void referencesParsesTheCurrentDocumentOnce() {
        // No siblings (non-file URI), so every parser built is for the current doc.
        DRLParsers.resetParseCount();
        DRLReferencesHelper.references(
                "myDocument", TYPE_USES_DRL, new Position(15, 5),
                Map.of(), ClassIndex.empty(), Set.of(), true);
        assertThat(DRLParsers.parseCount()).isEqualTo(1);
    }

    @Test
    void nullTextYieldsNoReferences() {
        assertThat(DRLReferencesHelper.references(
                "myDocument", null, new Position(0, 0),
                Map.of(), ClassIndex.empty(), Set.of(), true))
                .isEmpty();
    }
}
