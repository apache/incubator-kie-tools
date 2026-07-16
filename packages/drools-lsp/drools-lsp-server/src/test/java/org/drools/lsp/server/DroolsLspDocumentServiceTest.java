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

package org.drools.lsp.server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.drools.completion.ClassIndex;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DocumentDiagnosticParams;
import org.eclipse.lsp4j.DocumentDiagnosticReport;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.InlayHint;
import org.eclipse.lsp4j.InlayHintParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.PrepareRenameParams;
import org.eclipse.lsp4j.ReferenceContext;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.RenameParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.TypeHierarchyItem;
import org.eclipse.lsp4j.TypeHierarchyPrepareParams;
import org.eclipse.lsp4j.TypeHierarchySubtypesParams;
import org.eclipse.lsp4j.TypeHierarchySupertypesParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.lsp.server.TestHelperMethods.getDroolsLspDocumentService;

class DroolsLspDocumentServiceTest {

    @Test
    void getCompletionItems() {
        DroolsLspDocumentService droolsLspDocumentService = getDroolsLspDocumentService("");

        CompletionParams completionParams = new CompletionParams();
        completionParams.setTextDocument(new TextDocumentIdentifier("myDocument"));
        Position caretPosition = new Position();
        caretPosition.setCharacter(0);
        caretPosition.setLine(0);
        completionParams.setPosition(caretPosition);

        List<CompletionItem> result = droolsLspDocumentService.getCompletionItems(completionParams);
        assertThat(result.stream().map(CompletionItem::getInsertText).anyMatch("package"::equals)).isTrue();
    }

    @Test
    void getRuleName() {
        String drl = "rule MyRule when Dog(name == \"Bart\") then end";

        DroolsLspDocumentService droolsLspDocumentService = getDroolsLspDocumentService(drl);

        CompletionParams completionParams = new CompletionParams();
        completionParams.setTextDocument(new TextDocumentIdentifier("myDocument"));

        String ruleName = droolsLspDocumentService.getRuleName(completionParams);
        assertThat(ruleName).isEqualTo("MyRule");
    }

    @Test
    void getCompletionItems_findLHSandRHS() {
        String drl =
                "package org.test;\n" +
                        "import org.test.model.Person;\n" +
                        "rule TestRule when\n" +
                        "  $p:Person() \n" +
                        "then\n" +
                        "  System.out.println($p.getName()); \n" +
                        "end";

        DroolsLspDocumentService droolsLspDocumentService = getDroolsLspDocumentService(drl);

        CompletionParams completionParams = new CompletionParams();
        completionParams.setTextDocument(new TextDocumentIdentifier("myDocument"));

        completionParams.setPosition(new Position(1, 0));
        List<CompletionItem> result = droolsLspDocumentService.getCompletionItems(completionParams);
        assertThat(hasItem(result, "import")).isTrue();
        assertThat(hasItem(result, "rule")).isTrue();

        completionParams.setPosition(new Position(3, 14));
        result = droolsLspDocumentService.getCompletionItems(completionParams);
        assertThat(hasItem(result, "then")).isTrue();  // LHS

        completionParams.setPosition(new Position(5, 36));
        result = droolsLspDocumentService.getCompletionItems(completionParams);
        assertThat(hasItem(result, "end")).isTrue(); // RHS
    }

    private boolean hasItem(List<CompletionItem> result, String text) {
        return result.stream().map(CompletionItem::getInsertText).anyMatch(text::equals);
    }

    @Test
    void definitionJumpsToDeclareBlock() throws Exception {
        String drl = """
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
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        DefinitionParams params = new DefinitionParams(
                new TextDocumentIdentifier("myDocument"), new Position(8, 6));
        List<? extends Location> locations = service.definition(params).get().getLeft();

        assertThat(locations).hasSize(1);
        assertThat(locations.get(0).getUri()).isEqualTo("myDocument");
        assertThat(locations.get(0).getRange().getStart().getLine()).isEqualTo(2);
    }

    @Test
    void brokenDocumentProducesSyntaxDiagnostics() {
        String brokenDrl = "rule R when Person( then end";
        DroolsLspDocumentService service = getDroolsLspDocumentService(brokenDrl);

        List<Diagnostic> diags = service.validate("myDocument");
        assertThat(diags).isNotEmpty();
        assertThat(diags)
                 .anySatisfy(d -> assertThat(d.getSource()).isEqualTo("drools-parser"));
    }

    @Test
    void pullDiagnosticReportsErrors() throws Exception {
        String brokenDrl = "rule R when Person( then end";
        DroolsLspDocumentService service = getDroolsLspDocumentService(brokenDrl);

        DocumentDiagnosticReport report = service.diagnostic(
                new DocumentDiagnosticParams(new TextDocumentIdentifier("myDocument"))).get();

        assertThat(report.getRelatedFullDocumentDiagnosticReport().getItems())
                .isNotEmpty()
                .anySatisfy(d -> assertThat(d.getSource()).isEqualTo("drools-parser"));
    }

    @Test
    void structuralLintComplementsSyntaxDiagnostics() {
        // Missing 'end': the lint pass anchors a friendly warning at the rule
        // header, alongside whatever the parser reports near EOF.
        String drl = "rule \"A\"\n  when\n  then\n";
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        List<Diagnostic> diags = service.validate("myDocument");
        assertThat(diags)
                .anySatisfy(d -> assertThat(d.getSource()).isEqualTo("drools-lint"));
    }

    @Test
    void cleanDocumentProducesNoDiagnostics() {
        String drl = "package demo;\n"
                + "rule \"R\"\n"
                + "  when\n"
                + "  then\n"
                + "end\n";
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        assertThat(service.validate("myDocument")).isEmpty();
    }

    @Test
    void hoverShowsDeclaredTypeStructure() throws Exception {
        String drl = """
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
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        HoverParams params = new HoverParams(
                new TextDocumentIdentifier("myDocument"), new Position(8, 6));
        Hover hover = service.hover(params).get();

        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getValue())
                .contains("declare Person")
                .contains("name : String");
    }

    @Test
    void hoverShowsBoundVariableType() throws Exception {
        String drl = """
                package demo;

                declare Person
                  name : String
                end

                rule R
                  when
                    $p : Person( name == "x" )
                  then
                end
                """;
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        // Caret on "$p" at line 8, character 4.
        HoverParams params = new HoverParams(
                new TextDocumentIdentifier("myDocument"), new Position(8, 4));
        Hover hover = service.hover(params).get();

        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getValue())
                .contains("declare Person")
                .contains("name : String");
    }

    @Test
    void hoverShowsJavaLangTypeWithoutExplicitImport() throws Exception {
        // java.lang.* is implicitly available in DRL — no import needed.
        String drl = """
                package demo;

                rule R
                  when
                    Object()
                  then
                end
                """;
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        // Caret on "Object" at line 4, character 4.
        HoverParams params = new HoverParams(
                new TextDocumentIdentifier("myDocument"), new Position(4, 4));
        Hover hover = service.hover(params).get();

        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getValue())
                .contains("java.lang.Object");
    }

    @Test
    void inlayHintShowsBindingTypeAtRhsUsage() throws Exception {
        String drl = "rule \"r\" when\n"
                + "  $p : Patient(age > 18)\n"
                + "then\n"
                + "  update($p);\n"
                + "end\n";
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        InlayHintParams params = new InlayHintParams(
                new TextDocumentIdentifier("myDocument"),
                new Range(new Position(0, 0), new Position(99, 0)));
        List<InlayHint> hints = service.inlayHint(params).get();

        assertThat(hints).isNotEmpty();
        assertThat(hints).anySatisfy(h ->
                assertThat(h.getLabel().getLeft()).isEqualTo(": Patient"));
    }

    @Test
    void inlayHintReturnsEmptyWhenDisabledBySetting() throws Exception {
        String previous = System.getProperty("drools.lsp.inlayHints.enabled");
        System.setProperty("drools.lsp.inlayHints.enabled", "false");
        try {
            String drl = "rule \"r\" when\n"
                    + "  $p : Patient(age > 18)\n"
                    + "then\n"
                    + "  update($p);\n"
                    + "end\n";
            DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

            InlayHintParams params = new InlayHintParams(
                    new TextDocumentIdentifier("myDocument"),
                    new Range(new Position(0, 0), new Position(99, 0)));

            assertThat(service.inlayHint(params).get()).isEmpty();
        } finally {
            if (previous == null) {
                System.clearProperty("drools.lsp.inlayHints.enabled");
            } else {
                System.setProperty("drools.lsp.inlayHints.enabled", previous);
            }
        }
    }

    private static final String TYPE_HIERARCHY_DRL = """
            package demo;

            declare Animal
              legs : int
            end

            declare Dog extends Animal
              good : boolean
            end
            """;

    @Test
    void prepareTypeHierarchyResolvesDeclare() throws Exception {
        DroolsLspDocumentService service = getDroolsLspDocumentService(TYPE_HIERARCHY_DRL);

        TypeHierarchyPrepareParams params = new TypeHierarchyPrepareParams();
        params.setTextDocument(new TextDocumentIdentifier("myDocument"));
        params.setPosition(new Position(6, 9)); // caret on "Dog"

        List<TypeHierarchyItem> items = service.prepareTypeHierarchy(params).get();

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Dog");
        assertThat(items.get(0).getUri()).isEqualTo("myDocument");
    }

    @Test
    void typeHierarchySupertypesResolvesDeclaredParent() throws Exception {
        DroolsLspDocumentService service = getDroolsLspDocumentService(TYPE_HIERARCHY_DRL);

        TypeHierarchyPrepareParams prepare = new TypeHierarchyPrepareParams();
        prepare.setTextDocument(new TextDocumentIdentifier("myDocument"));
        prepare.setPosition(new Position(6, 9)); // "Dog"
        TypeHierarchyItem dog = service.prepareTypeHierarchy(prepare).get().get(0);

        List<TypeHierarchyItem> supers =
                service.typeHierarchySupertypes(new TypeHierarchySupertypesParams(dog)).get();

        assertThat(supers).hasSize(1);
        assertThat(supers.get(0).getName()).isEqualTo("Animal");
    }

    @Test
    void renameRewritesDeclaredTypeUses() throws Exception {
        String drl = """
                package demo;

                declare Person
                  name : String
                end

                rule R
                  when
                    Person( name == "x" )
                  then
                    insert(new Person());
                end
                """;
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        RenameParams params = new RenameParams();
        params.setTextDocument(new TextDocumentIdentifier("myDocument"));
        params.setPosition(new Position(8, 5)); // caret on the pattern "Person"
        params.setNewName("Customer");

        WorkspaceEdit edit = service.rename(params).get();

        assertThat(edit).isNotNull();
        List<TextEdit> edits = edit.getChanges().get("myDocument");
        assertThat(edits).hasSize(3); // declare, pattern, RHS new
        assertThat(edits).allSatisfy(e -> assertThat(e.getNewText()).isEqualTo("Customer"));
    }

    @Test
    void prepareRenameRejectsClasspathType() throws Exception {
        String drl = """
                package demo;
                import org.example.Pet;
                rule R
                  when
                    Pet( )
                  then
                end
                """;
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        PrepareRenameParams params = new PrepareRenameParams();
        params.setTextDocument(new TextDocumentIdentifier("myDocument"));
        params.setPosition(new Position(4, 5)); // caret on "Pet"

        // null → the client refuses the rename.
        assertThat(service.prepareRename(params).get()).isNull();
    }

    @Test
    void referencesFindsDeclaredTypeUses() throws Exception {
        String drl = """
                package demo;

                declare Person
                  name : String
                end

                rule R
                  when
                    Person( name == "x" )
                  then
                    insert(new Person());
                end
                """;
        DroolsLspDocumentService service = getDroolsLspDocumentService(drl);

        ReferenceParams params = new ReferenceParams();
        params.setTextDocument(new TextDocumentIdentifier("myDocument"));
        params.setPosition(new Position(8, 5)); // caret on the pattern "Person"
        params.setContext(new ReferenceContext(true));

        List<? extends Location> refs = service.references(params).get();

        // declare(2), pattern(8), RHS new(10)
        assertThat(refs).hasSize(3);
        assertThat(refs).allSatisfy(l -> assertThat(l.getUri()).isEqualTo("myDocument"));
    }

    @Test
    void typeHierarchySubtypesResolvesDeclaredChild() throws Exception {
        DroolsLspDocumentService service = getDroolsLspDocumentService(TYPE_HIERARCHY_DRL);

        TypeHierarchyPrepareParams prepare = new TypeHierarchyPrepareParams();
        prepare.setTextDocument(new TextDocumentIdentifier("myDocument"));
        prepare.setPosition(new Position(2, 9)); // "Animal"
        TypeHierarchyItem animal = service.prepareTypeHierarchy(prepare).get().get(0);

        List<TypeHierarchyItem> subs =
                service.typeHierarchySubtypes(new TypeHierarchySubtypesParams(animal)).get();

        assertThat(subs).hasSize(1);
        assertThat(subs.get(0).getName()).isEqualTo("Dog");
    }

    // ── unknown-type lint ────────────────────────────────────────────────

    private static final String TYPO_DRL = """
            package demo;

            declare Person
              name : String
            end

            rule R
              when
                Persn( )
              then
            end
            """;

    @Test
    void unknownTypeLintSuppressedUntilClasspathResolved() {
        // Default service: classpath unresolved (empty index) → pass is skipped,
        // so a misspelled type produces no drools-type diagnostic.
        DroolsLspDocumentService service = getDroolsLspDocumentService(TYPO_DRL);

        assertThat(service.validate("myDocument"))
                .noneSatisfy(d -> assertThat(d.getSource()).isEqualTo("drools-type"));
    }

    @Test
    void unknownTypeLintReportsTypoOnceClasspathResolved(@TempDir Path dir) throws Exception {
        Path classFile = dir.resolve("com/example/Marker.class");
        Files.createDirectories(classFile.getParent());
        Files.createFile(classFile);

        DroolsLspDocumentService service = getDroolsLspDocumentService(TYPO_DRL);
        service.setClassIndex(ClassIndex.build(Set.of(dir))); // classpath now "resolved"

        List<Diagnostic> diags = service.validate("myDocument");
        assertThat(diags).anySatisfy(d -> {
            assertThat(d.getSource()).isEqualTo("drools-type");
            assertThat(d.getMessage()).contains("Persn").contains("Did you mean 'Person'");
        });
    }

    @Test
    void buildUnknownTypeActionsOffersReplaceFix() {
        Diagnostic d = new Diagnostic();
        d.setSource("drools-type");
        d.setData("Person");
        d.setRange(new Range(new Position(8, 4), new Position(8, 9)));

        List<Either<Command, CodeAction>> actions =
                DroolsLspDocumentService.buildUnknownTypeActions("myDocument", List.of(d));

        assertThat(actions).hasSize(1);
        CodeAction ca = actions.get(0).getRight();
        assertThat(ca.getTitle()).isEqualTo("Replace with 'Person'");
        assertThat(ca.getEdit().getChanges().get("myDocument"))
                .singleElement()
                .satisfies(e -> assertThat(e.getNewText()).isEqualTo("Person"));
    }
}