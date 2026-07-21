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

import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DRLLintHelperTest {

    @AfterEach
    void clearLintProperties() {
        System.clearProperty("drools.lsp.lint.missingEnd");
        System.clearProperty("drools.lsp.lint.missingSeparator");
        System.clearProperty("drools.lsp.lint.missingSemicolon");
        System.clearProperty("drools.lsp.lint.unbalancedParens");
        System.clearProperty("drools.lsp.lint.unknownTypes");
        System.clearProperty("drools.lsp.lint.mvelPropertyAccess");
    }

    // ── missing 'end' ────────────────────────────────────────────────────

    @Test
    void unclosedRuleIsReported() {
        String text = "package demo;\n"
                + "rule \"A\"\n"
                + "  when\n"
                + "  then\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags).hasSize(1);
        Diagnostic d = diags.get(0);
        assertThat(d.getSource()).isEqualTo("drools-lint");
        assertThat(d.getSeverity()).isEqualTo(DiagnosticSeverity.Warning);
        assertThat(d.getMessage()).contains("end");
        assertThat(d.getRange().getStart().getLine()).isEqualTo(1);
    }

    @Test
    void closedRuleIsClean() {
        String text = "package demo;\n"
                + "rule \"A\"\n"
                + "  when\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void endWithTrailingCommentClosesRule() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "  then\n"
                + "end // done\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void singleLineRuleIsClean() {
        String text = "rule A when then end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void declareBlockEndDoesNotCloseRule() {
        String text = "declare Alpha\n"
                + "  name : String\n"
                + "end\n"
                + "rule \"A\"\n"
                + "  when\n"
                + "  then\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(3);
    }

    @Test
    void unclosedDeclareDoesNotSuppressLaterRuleTracking() {
        // The declare block is missing its own `end`; the rule after it must
        // still be tracked (and the rule's `end` closes the rule).
        String text = "declare Alpha\n"
                + "  name : String\n"
                + "rule \"A\"\n"
                + "  when\n"
                + "  then\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags)
                .anySatisfy(d -> assertThat(d.getRange().getStart().getLine()).isEqualTo(2));
    }

    @Test
    void propertyAccessEndingInEndDoesNotCloseRule() {
        // A consequence line ending with a field named 'end' (e.g. $booking.end)
        // must not be mistaken for the closing 'end' keyword.
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $b : Booking()\n"
                + "  then\n"
                + "    System.out.println($b.end);\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags)
                .anySatisfy(d -> assertThat(d.getMessage()).contains("end"));
    }

    @Test
    void wordsMerelyEndingInEndDoNotCloseRules() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $t : Trend()\n"
                + "  then\n"
                + "    trend();\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags)
                .anySatisfy(d -> assertThat(d.getMessage()).contains("end"));
    }

    @Test
    void missingEndPassCanBeDisabled() {
        System.setProperty("drools.lsp.lint.missingEnd", "off");
        String text = "rule \"A\"\n  when\n  then\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void missingEndSeverityIsConfigurable() {
        System.setProperty("drools.lsp.lint.missingEnd", "error");
        String text = "rule \"A\"\n  when\n  then\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getSeverity()).isEqualTo(DiagnosticSeverity.Error);
    }

    @Test
    void nullAndEmptyTextAreClean() {
        assertThat(DRLLintHelper.lint(null)).isEmpty();
        assertThat(DRLLintHelper.lint("")).isEmpty();
    }

    // ── missing constraint separators ────────────────────────────────────

    @Test
    void missingCommaBetweenConstraintsIsReported() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person(\n"
                + "      name == \"x\"\n"
                + "      age > 5\n"
                + "    )\n"
                + "  then\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags).hasSize(1);
        Diagnostic d = diags.get(0);
        assertThat(d.getSource()).isEqualTo("drools-lint");
        assertThat(d.getMessage()).contains(",");
        assertThat(d.getRange().getStart().getLine()).isEqualTo(3);
    }

    @Test
    void commaSeparatedConstraintsAreClean() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person(\n"
                + "      name == \"x\",\n"
                + "      age > 5\n"
                + "    )\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void logicalOperatorSeparatorsAreClean() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person(\n"
                + "      name == \"x\" &&\n"
                + "      age > 5\n"
                + "    )\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void commentLineInsidePatternDoesNotBreakParenTracking() {
        // Regression: a comment line between the pattern header and the
        // constraints previously corrupted the paren-depth bookkeeping and
        // silenced the pass for the rest of the rule.
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person(\n"
                + "      // bound fields below\n"
                + "      name == \"x\"\n"
                + "      age > 5\n"
                + "    )\n"
                + "  then\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(4);
    }

    @Test
    void parensInsideStringLiteralsDoNotInflateDepth() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person( name == \"hello (world\" )\n"
                + "    Account( id > 0 )\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void separatorPassCanBeDisabled() {
        System.setProperty("drools.lsp.lint.missingSeparator", "off");
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person(\n"
                + "      name == \"x\"\n"
                + "      age > 5\n"
                + "    )\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    // ── missing ';' in THEN ──────────────────────────────────────────────

    @Test
    void missingSemicolonInConsequenceIsReported() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    System.out.println($p)\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags).hasSize(1);
        Diagnostic d = diags.get(0);
        assertThat(d.getSource()).isEqualTo("drools-lint");
        assertThat(d.getMessage()).contains(";");
        assertThat(d.getRange().getStart().getLine()).isEqualTo(4);
    }

    @Test
    void terminatedConsequenceIsClean() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    System.out.println($p);\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void mvelDialectRuleIsExemptFromSemicolonLint() {
        // MVEL consequences legally omit semicolons.
        String text = "rule \"A\"\n"
                + "  dialect \"mvel\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    System.out.println($p)\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void packageLevelMvelDialectExemptsAllRules() {
        String text = "package demo;\n"
                + "dialect \"mvel\"\n"
                + "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    System.out.println($p)\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void modifyBlockBodyIsNotFlagged() {
        // modify(...) { ... } bodies use comma-separated setters, not
        // semicolons.
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    modify($p) {\n"
                + "      setAge(5),\n"
                + "      setName(\"x\")\n"
                + "    }\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void fluentChainContinuationIsNotFlagged() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    builder.setX(1)\n"
                + "        .setY(2);\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void multiLineCallArgumentsAreNotFlagged() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    System.out.println(\n"
                + "        $p.getName());\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void controlFlowLinesAreNotFlagged() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    if ($p.getAge() > 5) {\n"
                + "      delete($p);\n"
                + "    }\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void semicolonPassCanBeDisabled() {
        System.setProperty("drools.lsp.lint.missingSemicolon", "off");
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    System.out.println($p)\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    // ── unbalanced parentheses in the LHS ────────────────────────────────

    @Test
    void unclosedPatternParenIsReported() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person( age > 5\n"
                + "  then\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags)
                .anySatisfy(d -> {
                    assertThat(d.getSource()).isEqualTo("drools-lint");
                    assertThat(d.getMessage()).contains("'('");
                    assertThat(d.getRange().getStart().getLine()).isEqualTo(2);
                    assertThat(d.getRange().getStart().getCharacter()).isEqualTo(10);
                });
    }

    @Test
    void balancedMultiLinePatternIsClean() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person(\n"
                + "      age > 5\n"
                + "    )\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void strayClosingParenIsReported() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person( age > 5 ))\n"
                + "  then\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags)
                .anySatisfy(d -> {
                    assertThat(d.getMessage()).contains("')'");
                    assertThat(d.getRange().getStart().getLine()).isEqualTo(2);
                    assertThat(d.getRange().getStart().getCharacter()).isEqualTo(21);
                });
    }

    @Test
    void parensInsideStringsDoNotAffectBalance() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person( name == \"(((\" )\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void queryBodyParensAreCovered() {
        String text = "query \"q\"\n"
                + "    Person( age > 5\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags)
                .anySatisfy(d -> {
                    assertThat(d.getMessage()).contains("'('");
                    assertThat(d.getRange().getStart().getLine()).isEqualTo(1);
                });
    }

    @Test
    void unclosedParenInConsequenceIsReported() {
        // lintUnbalancedParens now covers the THEN section as well as the LHS.
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    modify($p { setAge(5); }\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags)
                .anySatisfy(d -> {
                    assertThat(d.getSource()).isEqualTo("drools-lint");
                    assertThat(d.getMessage()).contains("'('");
                });
    }

    @Test
    void strayClosingParenInConsequenceIsReported() {
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    retract($p));\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags)
                .anySatisfy(d -> {
                    assertThat(d.getMessage()).contains("')'");
                    assertThat(d.getRange().getStart().getLine()).isEqualTo(4);
                });
    }

    @Test
    void unbalancedParensPassCanBeDisabled() {
        System.setProperty("drools.lsp.lint.unbalancedParens", "off");
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person( age > 5\n"
                + "  then\n"
                + "end\n";
        // The separator/semicolon/end passes are quiet on this fixture, so
        // disabling the paren pass leaves nothing.
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    // ── MVEL property access (off by default) ────────────────────────────

    private static final String GETTER_IN_LHS = "rule \"A\"\n"
            + "  when\n"
            + "    Person( address.getCode() == \"X\" )\n"
            + "  then\n"
            + "end\n";

    @Test
    void mvelPropertyAccessLintIsOffByDefault() {
        assertThat(DRLLintHelper.lint(GETTER_IN_LHS)).isEmpty();
    }

    @Test
    void bareGetterCallInConstraintIsFlagged() {
        // The most common DRL anti-pattern: getName() with no explicit receiver.
        // The implicit receiver is the pattern fact, so the MVEL form is just 'name'.
        System.setProperty("drools.lsp.lint.mvelPropertyAccess", "warning");
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person( getName() == \"John\" )\n"
                + "  then\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getMessage()).contains("'name'").contains("getName()");
    }

    @Test
    void getterCallInConstraintIsFlaggedWhenEnabled() {
        System.setProperty("drools.lsp.lint.mvelPropertyAccess", "hint");
        List<Diagnostic> diags = DRLLintHelper.lint(GETTER_IN_LHS);

        assertThat(diags).hasSize(1);
        Diagnostic d = diags.get(0);
        assertThat(d.getSource()).isEqualTo("drools-lint");
        assertThat(d.getSeverity()).isEqualTo(DiagnosticSeverity.Hint);
        assertThat(d.getMessage()).contains("'code'").contains("getCode()");
        assertThat(d.getRange().getStart().getLine()).isEqualTo(2);
    }

    @Test
    void consequenceGettersAreNotFlagged() {
        // The THEN consequence is real Java — getter calls are correct there.
        System.setProperty("drools.lsp.lint.mvelPropertyAccess", "warning");
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    System.out.println($p.getName());\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void getClassIsNeverFlagged() {
        // getClass() maps to `.class`, a reserved construct in MVEL — there
        // is no safe property-access rewrite for it.
        System.setProperty("drools.lsp.lint.mvelPropertyAccess", "warning");
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person( $c : getClass() )\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void staticTypeReceiverIsNotFlagged() {
        // Type.getX() is a static call, not a bean property accessor.
        System.setProperty("drools.lsp.lint.mvelPropertyAccess", "warning");
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person( code == Defaults.getCode() )\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLLintHelper.lint(text)).isEmpty();
    }

    @Test
    void consecutiveCapsPropertyNameIsPreserved() {
        // JavaBeans decapitalize: getURL -> URL (not uRL), matching how
        // Drools resolves pattern properties back to accessors.
        System.setProperty("drools.lsp.lint.mvelPropertyAccess", "warning");
        String text = "rule \"A\"\n"
                + "  when\n"
                + "    Person( site.getURL() == \"x\" )\n"
                + "  then\n"
                + "end\n";
        List<Diagnostic> diags = DRLLintHelper.lint(text);

        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getMessage()).contains("'URL'");
    }

    // ── unknown types ────────────────────────────────────────────────────

    // Member reflection over the test classpath (for qualified-reference checks);
    // java.lang still resolves via the platform loader.
    private final ClassMemberIndex members = new ClassMemberIndex(getClass().getClassLoader());

    // Known types come from declares in the text; classpath/java.lang resolution
    // runs against an empty class index, with the classpath treated as resolved
    // (so non-declared unknowns are flagged).
    private List<Diagnostic> lintUnknownTypes(String text) {
        return DRLLintHelper.lintUnknownTypes(text, null, Map.of(), ClassIndex.empty(), members, true);
    }

    private static final String DECLARE_PERSON = "declare Person\n  name : String\nend\n";

    @Test
    void flagsUnknownPatternTypeWithTypoSuggestion() {
        String text = DECLARE_PERSON + "rule R\n  when\n    Persn( age > 18 )\n  then\nend\n";
        List<Diagnostic> diags = lintUnknownTypes(text);

        assertThat(diags).hasSize(1);
        Diagnostic d = diags.get(0);
        assertThat(d.getSource()).isEqualTo("drools-type");
        assertThat(d.getSeverity()).isEqualTo(DiagnosticSeverity.Warning);
        assertThat(d.getMessage()).contains("Unknown type 'Persn'").contains("Did you mean 'Person'");
        assertThat(d.getData()).isEqualTo("Person"); // drives the quick-fix
        assertThat(d.getRange().getStart().getLine()).isEqualTo(5); // the pattern line
        assertThat(d.getRange().getStart().getCharacter()).isEqualTo(4);
    }

    @Test
    void knownDeclaredTypeIsNotFlagged() {
        String text = DECLARE_PERSON + "rule R\n  when\n    Person( age > 18 )\n  then\nend\n";
        assertThat(lintUnknownTypes(text)).isEmpty();
    }

    @Test
    void flagsUnknownTypeInNewExpression() {
        String text = DECLARE_PERSON + "rule R\n  when\n  then\n    insert(new Persn());\nend\n";
        List<Diagnostic> diags = lintUnknownTypes(text);

        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getMessage()).contains("Persn").contains("Did you mean 'Person'");
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(6); // the new-expression line
    }

    @Test
    void unknownTypeWithoutCloseMatchHasNoSuggestion() {
        String text = DECLARE_PERSON + "rule R\n  when\n    Foobar( )\n  then\nend\n";
        List<Diagnostic> diags = lintUnknownTypes(text);

        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getMessage()).contains("Unknown type 'Foobar'").doesNotContain("Did you mean");
        assertThat(diags.get(0).getData()).isNull();
    }

    @Test
    void importedTypeIsNotFlagged() {
        // Resolved via the exact import, no declare and no classpath needed.
        String text = "package demo;\nimport org.example.Account;\n"
                + "rule R\n  when\n    Account( )\n  then\nend\n";
        assertThat(lintUnknownTypes(text)).isEmpty();
    }

    @Test
    void javaLangPatternTypeIsNotFlagged() {
        // Object resolves via java.lang reflection through resolveFqcn — not flagged.
        String text = "rule R\n  when\n    Object( )\n  then\nend\n";
        assertThat(lintUnknownTypes(text)).isEmpty();
    }

    @Test
    void ignoresTypeLikeTextInStringsAndComments() {
        String text = DECLARE_PERSON
                + "rule R\n  when\n    Person( name == \"Persn\" )\n  then\n"
                + "    // Persn is a typo only in this comment\n    insert(new Person());\nend\n";
        assertThat(lintUnknownTypes(text)).isEmpty();
    }

    @Test
    void offSettingDisablesTheUnknownTypePass() {
        System.setProperty("drools.lsp.lint.unknownTypes", "off");
        String text = DECLARE_PERSON + "rule R\n  when\n    Persn( )\n  then\nend\n";
        assertThat(lintUnknownTypes(text)).isEmpty();
    }

    // ── qualified references (Type.Member.x) ─────────────────────────────

    // PetKind is a fixture enum with constants CAT, DOG.
    private static final String IMPORT_PETKIND =
            "package demo;\nimport org.drools.completion.fixtures.PetKind;\n";

    @Test
    void flagsUnknownMemberOfQualifiedReference() {
        String text = IMPORT_PETKIND
                + "declare Animal\n  legs : int\nend\n"
                + "rule R\n  when\n    Animal( legs == PetKind.CATT )\n  then\nend\n";
        List<Diagnostic> diags = lintUnknownTypes(text);

        assertThat(diags).hasSize(1);
        Diagnostic d = diags.get(0);
        assertThat(d.getSource()).isEqualTo("drools-type");
        assertThat(d.getMessage())
                .contains("'CATT' is not a member of 'PetKind'")
                .contains("Did you mean 'CAT'");
        assertThat(d.getData()).isEqualTo("CAT"); // drives the quick-fix
    }

    @Test
    void validQualifiedMemberIsClean() {
        String text = IMPORT_PETKIND
                + "declare Animal\n  legs : int\nend\n"
                + "rule R\n  when\n    Animal( legs == PetKind.CAT )\n  then\nend\n";
        assertThat(lintUnknownTypes(text)).isEmpty();
    }

    @Test
    void flagsUnknownQualifiedHeadType() {
        // The head type itself is unresolved — flagged before any member check.
        String text = IMPORT_PETKIND
                + "declare Animal\n  legs : int\nend\n"
                + "rule R\n  when\n    Animal( legs == PetKindd.CAT )\n  then\nend\n";
        List<Diagnostic> diags = lintUnknownTypes(text);

        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getMessage()).contains("Unknown type 'PetKindd'");
    }

    @Test
    void flagsUnknownConstantOfDeclaredEnum() {
        // A declared enum's constants are verified from the DRL alone (no classpath).
        // Mirrors the real-world shape Type.MEMBER.property (e.g. TestEnum.Valu.value):
        // the upper-case member is checked, the lower-case property tail is not.
        String text = "declare enum Color\n  RED, GREEN;\n  shade : String\nend\n"
                + "declare Widget\n  c : Color\nend\n"
                + "rule R\n  when\n    Widget( c == Color.REDD.shade )\n  then\nend\n";
        List<Diagnostic> diags = lintUnknownTypes(text);

        assertThat(diags).hasSize(1);
        Diagnostic d = diags.get(0);
        assertThat(d.getSource()).isEqualTo("drools-type");
        assertThat(d.getMessage())
                .contains("'REDD' is not a member of 'Color'")
                .contains("Did you mean 'RED'");
        assertThat(d.getData()).isEqualTo("RED");
    }

    @Test
    void declaredEnumConstantCheckRunsWithoutClasspath() {
        // Same check holds when the classpath has not resolved — it needs none.
        String text = "declare enum Color\n  RED, GREEN;\nend\n"
                + "declare Widget\n  c : Color\nend\n"
                + "rule R\n  when\n    Widget( c == Color.REDD )\n  then\nend\n";
        List<Diagnostic> diags = DRLLintHelper.lintUnknownTypes(
                text, null, Map.of(), ClassIndex.empty(), members, false);

        assertThat(diags)
                .singleElement()
                .satisfies(d -> assertThat(d.getMessage()).contains("'REDD' is not a member of 'Color'"));
    }

    @Test
    void validConstantOfDeclaredEnumIsClean() {
        String text = "declare enum Color\n  RED, GREEN;\nend\n"
                + "declare Widget\n  c : Color\nend\n"
                + "rule R\n  when\n    Widget( c == Color.RED )\n  then\nend\n";
        assertThat(lintUnknownTypes(text)).isEmpty();
    }

    @Test
    void lowercasePropertyTailIsNotChased() {
        // PetKind.CAT.groupType — CAT is valid; the lower-case property is not verified.
        String text = IMPORT_PETKIND
                + "declare Animal\n  legs : int\nend\n"
                + "rule R\n  when\n    Animal( legs == PetKind.CAT.ordinal )\n  then\nend\n";
        assertThat(lintUnknownTypes(text)).isEmpty();
    }
}
