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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DRLLintSuppressionTest {

    @AfterEach
    void clearProperties() {
        System.clearProperty("drools.lsp.lint.missingEnd");
        System.clearProperty("drools.lsp.lint.missingSeparator");
        System.clearProperty("drools.lsp.lint.missingSemicolon");
        System.clearProperty("drools.lsp.lint.unbalancedParens");
        System.clearProperty("drools.lsp.lint.unknownTypes");
        System.clearProperty("drools.lsp.lint.mvelPropertyAccess");
        System.clearProperty("drools.lsp.lint.deprecated");
    }

    @Test
    void stableDiagnosticCodesAssigned() {
        // Missing rule end
        String missingEnd = "rule \"Unclosed\"\nwhen\nthen\n";
        List<Diagnostic> endDiags = DRLLintHelper.lint(missingEnd);
        assertThat(endDiags).hasSize(1);
        assertThat(endDiags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_END);

        // Missing separator
        String missingSep = "rule \"Sep\"\nwhen\n  Person(\n    name == \"A\"\n    age > 20\n  )\nthen\nend\n";
        List<Diagnostic> sepDiags = DRLLintHelper.lint(missingSep);
        assertThat(sepDiags).hasSize(1);
        assertThat(sepDiags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_SEPARATOR);

        // Missing semicolon
        String missingSemi = "rule \"Semi\"\nwhen\nthen\n  insert(new Person())\nend\n";
        List<Diagnostic> semiDiags = DRLLintHelper.lint(missingSemi);
        assertThat(semiDiags).hasSize(1);
        assertThat(semiDiags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_SEMICOLON);

        // Unbalanced parens
        String unclosedParen = "rule \"Paren\"\nwhen\n  Person( (age > 20 )\nthen\nend\n";
        List<Diagnostic> parenDiags = DRLLintHelper.lint(unclosedParen);
        assertThat(parenDiags).hasSize(1);
        assertThat(parenDiags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_UNBALANCED_PARENS);

        // MVEL property access
        System.setProperty("drools.lsp.lint.mvelPropertyAccess", "warning");
        String mvel = "rule \"Mvel\"\nwhen\n  Person( getName() == \"John\" )\nthen\nend\n";
        List<Diagnostic> mvelDiags = DRLLintHelper.lint(mvel);
        assertThat(mvelDiags).hasSize(1);
        assertThat(mvelDiags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MVEL_PROPERTY_ACCESS);
    }

    @Test
    void disableFile_wildcardDropsAllLintDiagnostics() {
        String text = "// @drl-lsp-lint:disable-file\n"
                + "rule \"A\"\n"
                + "when\n"
                + "  Person(\n"
                + "    name == \"A\"\n"
                + "    age > 20\n"
                + "  )\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).isEmpty();
    }

    @Test
    void disableFile_specificCodeDropsOnlyTargetedDiagnostic() {
        String text = "// @drl-lsp-lint:disable-file missing-semicolon\n"
                + "rule \"A\"\n"
                + "when\n"
                + "  Person(\n"
                + "    name == \"A\"\n"
                + "    age > 20\n"
                + "  )\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_SEPARATOR);
    }

    @Test
    void disableFile_multipleCodes() {
        String text = "// @drl-lsp-lint:disable-file missing-semicolon, missing-separator\n"
                + "rule \"A\"\n"
                + "when\n"
                + "  Person(\n"
                + "    name == \"A\"\n"
                + "    age > 20\n"
                + "  )\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).isEmpty();
    }

    @Test
    void disableLine_wildcardDropsLineDiagnostics() {
        String text = "rule \"A\"\n"
                + "when\n"
                + "then\n"
                + "  insert(new Person()) // @drl-lsp-lint:disable-line\n"
                + "  update(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(4);
    }

    @Test
    void disableLine_specificCode() {
        String text = "rule \"A\"\n"
                + "when\n"
                + "then\n"
                + "  insert(new Person()) // @drl-lsp-lint:disable-line missing-semicolon\n"
                + "  update(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(4);
    }

    @Test
    void disableNextLine_wildcard() {
        String text = "rule \"A\"\n"
                + "when\n"
                + "then\n"
                + "  // @drl-lsp-lint:disable-next-line\n"
                + "  insert(new Person())\n"
                + "  update(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(5);
    }

    @Test
    void disableNextLine_specificCode() {
        String text = "rule \"A\"\n"
                + "when\n"
                + "then\n"
                + "  // @drl-lsp-lint:disable-next-line missing-semicolon\n"
                + "  insert(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).isEmpty();
    }

    @Test
    void disableNextLine_multiLineBlockComment_anchorsToLineAfterClosingComment() {
        String text = "rule \"A\"\n"
                + "when\n"
                + "then\n"
                + "  /*\n"
                + "   * Note on suppression\n"
                + "   * @drl-lsp-lint:disable-next-line missing-semicolon\n"
                + "   */\n"
                + "  insert(new Person())\n"
                + "  update(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        // Line 7 (insert) is suppressed; line 8 (update) should be reported
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(8);
    }

    @Test
    void regionDisableAndEnable() {
        String text = "// @drl-lsp-lint:disable missing-semicolon\n"
                + "rule \"A\"\n"
                + "when\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n"
                + "// @drl-lsp-lint:enable missing-semicolon\n"
                + "rule \"B\"\n"
                + "when\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(10);
    }

    @Test
    void regionPartialEnablement_usingExclusionSet() {
        String text = "// @drl-lsp-lint:disable\n"
                + "rule \"A\"\n"
                + "when\n"
                + "  Person(\n"
                + "    name == \"A\"\n"
                + "    age > 20\n"
                + "  )\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n"
                + "// @drl-lsp-lint:enable missing-separator\n"
                + "rule \"B\"\n"
                + "when\n"
                + "  Person(\n"
                + "    name == \"B\"\n"
                + "    age > 20\n"
                + "  )\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        // In Rule B: missing-separator is re-enabled, missing-semicolon remains suppressed
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_SEPARATOR);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(14);
    }

    @Test
    void disableRule_precedingComment() {
        String text = "// @drl-lsp-lint:disable-rule missing-separator\n"
                + "rule \"A\"\n"
                + "when\n"
                + "  Person(\n"
                + "    name == \"A\"\n"
                + "    age > 20\n"
                + "  )\n"
                + "then\n"
                + "end\n"
                + "rule \"B\"\n"
                + "when\n"
                + "  Person(\n"
                + "    name == \"B\"\n"
                + "    age > 30\n"
                + "  )\n"
                + "then\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(12);
    }

    @Test
    void disableRule_insideRule() {
        String text = "rule \"A\"\n"
                + "  // @drl-lsp-lint:disable-rule missing-semicolon\n"
                + "when\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n"
                + "rule \"B\"\n"
                + "when\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(9);
    }

    @Test
    void disableRule_unclosedRuleDoesNotSwallowNextRule() {
        String text = "// @drl-lsp-lint:disable-rule missing-end\n"
                + "rule \"Unclosed\"\n"
                + "when\n"
                + "then\n"
                + "rule \"NextUnclosed\"\n"
                + "when\n"
                + "then\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        // Rule "Unclosed" had missing-end suppressed; Rule "NextUnclosed" should still be flagged
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_END);
        assertThat(diags.get(0).getRange().getStart().getLine()).isEqualTo(4);
    }

    @Test
    void droolsType_unknownTypeSuppressed() {
        String text = "// @drl-lsp-lint:disable-file unknown-type\n"
                + "rule \"R\"\n"
                + "when\n"
                + "  NonExistentType()\n"
                + "then\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lintUnknownTypes(text, null, Map.of(),
                ClassIndex.empty(), ClassMemberIndex.empty(), true);
        assertThat(diags).isEmpty();
    }

    @Test
    void droolsType_unknownMemberSuppressed() {
        String text = "rule \"R\"\n"
                + "when\n"
                + "  Person( nonExistentProp == 1 ) // @drl-lsp-lint:disable-line unknown-member\n"
                + "then\n"
                + "end\n";

        Diagnostic memberDiag = new Diagnostic();
        memberDiag.setSource("drools-type");
        memberDiag.setCode(DRLLintHelper.CODE_UNKNOWN_MEMBER);
        memberDiag.setRange(new Range(new Position(2, 10), new Position(2, 25)));

        List<Diagnostic> filtered = DRLLintSuppression.filter(List.of(memberDiag), text);
        assertThat(filtered).isEmpty();
    }

    @Test
    void droolsDeprecated_halfConstraintSuppressed() {
        String text = "rule \"Deprecated\"\n"
                + "when\n"
                + "  Person( age > 10 && < 20 ) // @drl-lsp-lint:disable-line half-constraint\n"
                + "then\n"
                + "end\n";

        DRLDiagnosticHelper.Parsed parsed = DRLDiagnosticHelper.parse(text);
        // Suppressed deprecation should not be emitted, and syntax error should not resurface
        assertThat(parsed.diagnostics).isEmpty();
    }

    @Test
    void droolsDeprecated_patternConnectiveSuppressed() {
        String text = "// @drl-lsp-lint:disable-file pattern-connective\n"
                + "rule \"Deprecated\"\n"
                + "when\n"
                + "  Person() && Address()\n"
                + "then\n"
                + "end\n";

        DRLDiagnosticHelper.Parsed parsed = DRLDiagnosticHelper.parse(text);
        assertThat(parsed.diagnostics).isEmpty();
    }

    @Test
    void diagnosticCapPreservation_suppressedDiagnosticsDoNotConsumeCap() {
        // Build 25 lines of missing semicolon inside a suppressed block, followed by 1 unsuppressed line
        StringBuilder sb = new StringBuilder();
        sb.append("rule \"CapTest\"\nwhen\nthen\n");
        sb.append("// @drl-lsp-lint:disable missing-semicolon\n");
        for (int i = 0; i < 25; i++) {
            sb.append("  insert(new Person())\n");
        }
        sb.append("// @drl-lsp-lint:enable missing-semicolon\n");
        sb.append("  insert(new Person())\n");
        sb.append("end\n");

        List<Diagnostic> diags = DRLLintHelper.lint(sb.toString());
        // The 25 suppressed lines should not exhaust the 20-diagnostic limit;
        // the 1 unsuppressed line after the enable must be reported!
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_SEMICOLON);
    }

    @Test
    void parserAndCompilerDiagnosticsAreNeverSuppressed() {
        String text = "// @drl-lsp-lint:disable-file\n"
                + "rule \"SyntaxError\"\n";

        Diagnostic syntaxDiag = new Diagnostic();
        syntaxDiag.setSource("drools-drl-parser");
        syntaxDiag.setMessage("mismatched input '<EOF>' expecting 'when'");
        syntaxDiag.setRange(new Range(new Position(1, 0), new Position(1, 5)));

        Diagnostic compileDiag = new Diagnostic();
        compileDiag.setSource("drools");
        compileDiag.setMessage("Rule compilation error");
        compileDiag.setRange(new Range(new Position(1, 0), new Position(1, 5)));

        Diagnostic lintDiag = new Diagnostic();
        lintDiag.setSource("drools-lint");
        lintDiag.setCode(DRLLintHelper.CODE_MISSING_END);
        lintDiag.setMessage("Missing 'end'");
        lintDiag.setRange(new Range(new Position(1, 0), new Position(1, 4)));

        List<Diagnostic> all = List.of(syntaxDiag, compileDiag, lintDiag);
        List<Diagnostic> filtered = DRLLintSuppression.filter(all, text);

        assertThat(filtered).containsExactly(syntaxDiag, compileDiag);
    }

    @Test
    void directiveInsideStringLiteralIsIgnored() {
        String text = "rule \"A\"\n"
                + "when\n"
                + "  String( this == \"// @drl-lsp-lint:disable-file\" )\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n";

        List<Diagnostic> diags = DRLLintHelper.lint(text);
        // The missing semicolon on the consequence should still be reported
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_SEMICOLON);
    }

    @Test
    void unknownDirectiveActionEmitsWarning() {
        String text = "// @drl-lsp-lint:disable-lines missing-semicolon\n"
                + "rule \"A\"\n"
                + "when\n"
                + "then\n"
                + "  insert(new Person())\n"
                + "end\n";

        DRLLintSuppression.SuppressionIndex index = DRLLintSuppression.parse(text);
        List<Diagnostic> warnings = index.getDirectiveDiagnostics();
        assertThat(warnings).hasSize(1);
        assertThat(warnings.get(0).getSeverity()).isEqualTo(DiagnosticSeverity.Warning);
        assertThat(warnings.get(0).getMessage()).contains("Unknown directive action '@drl-lsp-lint:disable-lines'");

        // Since action was invalid, missing-semicolon is NOT suppressed
        List<Diagnostic> diags = DRLLintHelper.lint(text, index);
        assertThat(diags).hasSize(1);
        assertThat(diags.get(0).getCode().getLeft()).isEqualTo(DRLLintHelper.CODE_MISSING_SEMICOLON);
    }

    @Test
    void unknownLinterCodeEmitsWarning() {
        String text = "// @drl-lsp-lint:disable-line non-existent-code, missing-semicolon\n"
                + "rule \"A\"\n"
                + "when\n"
                + "then\n"
                + "  insert(new Person()) // @drl-lsp-lint:disable-line typo-code\n"
                + "end\n";

        DRLLintSuppression.SuppressionIndex index = DRLLintSuppression.parse(text);
        List<Diagnostic> warnings = index.getDirectiveDiagnostics();
        assertThat(warnings).hasSize(2);
        assertThat(warnings.get(0).getMessage()).contains("Unknown linter code 'non-existent-code'");
        assertThat(warnings.get(1).getMessage()).contains("Unknown linter code 'typo-code'");
    }

    @Test
    void mvelPropertyAccessQuickFixRespectsSuppression() {
        String text = "rule \"Mvel\"\n"
                + "when\n"
                + "  Person( getName() == \"John\" ) // @drl-lsp-lint:disable-line mvel-property-access\n"
                + "  Person( getAge() > 20 )\n"
                + "then\n"
                + "end\n";

        DRLLintSuppression.SuppressionIndex suppression = DRLLintSuppression.parse(text);
        List<TextEdit> edits = DRLLintHelper.mvelPropertyAccessEdits(text, suppression);
        // Only getAge() should be edited, getName() was suppressed
        assertThat(edits).hasSize(1);
        assertThat(edits.get(0).getNewText()).isEqualTo("age");
    }
}
