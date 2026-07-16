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

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DRLDiagnosticHelperTest {

    @Test
    void syntaxErrorProducesDiagnostic() {
        String text = "rule R when Person( then end";
        List<Diagnostic> diags = DRLDiagnosticHelper.validate(text);
        assertThat(diags).isNotEmpty();
    }

    @Test
    void cleanFileProducesNoDiagnostics() {
        String text = "package demo;\n"
                + "rule \"R\"\n"
                + "  when\n"
                + "  then\n"
                + "end\n";
        assertThat(DRLDiagnosticHelper.validate(text)).isEmpty();
    }

    @Test
    void syntaxErrorHasRangeSeverityAndSource() {
        // Constraint paren never closed before `then`.
        String text = "package demo;\n"
                + "rule \"R\"\n"
                + "  when Person( then\n"
                + "end\n";
        List<Diagnostic> diags = DRLDiagnosticHelper.validate(text);

        assertThat(diags).isNotEmpty();
        for (Diagnostic d : diags) {
            assertThat(d.getSeverity()).isEqualTo(DiagnosticSeverity.Error);
            assertThat(d.getSource()).isEqualTo("drools-parser");
            assertThat(d.getMessage()).isNotBlank();
            assertThat(d.getRange().getStart().getLine())
                    .isEqualTo(d.getRange().getEnd().getLine());
            assertThat(d.getRange().getEnd().getCharacter())
                    .isGreaterThan(d.getRange().getStart().getCharacter());
        }
        // ANTLR recovery may anchor the error on the broken constraint line
        // (2) or the following `end` (3), depending on the recovery path —
        // both are within the broken region.
        assertThat(diags)
                .anySatisfy(d -> assertThat(d.getRange().getStart().getLine()).isBetween(2, 3));
    }

    @Test
    void multipleBrokenRulesReportMultipleDiagnostics() {
        String text = "package demo;\n"
                + "rule \"A\"\n"
                + "  when Person( then\n"
                + "end\n"
                + "rule \"B\"\n"
                + "  when Account( then\n"
                + "end\n";
        List<Diagnostic> diags = DRLDiagnosticHelper.validate(text);

        assertThat(diags.size()).isGreaterThanOrEqualTo(2);
        assertThat(diags.stream().map(d -> d.getRange().getStart().getLine()).distinct().count())
                .isGreaterThanOrEqualTo(2);
    }

    @Test
    void lexerErrorProducesDiagnosticWithSaneRange() {
        // Unterminated string literal — reported by the lexer, where the
        // offending symbol is null rather than a Token.
        String text = "package demo;\n"
                + "rule \"R\n"
                + "  when\n"
                + "  then\n"
                + "end\n";
        List<Diagnostic> diags = DRLDiagnosticHelper.validate(text);

        assertThat(diags).isNotEmpty();
        for (Diagnostic d : diags) {
            assertThat(d.getRange().getStart().getCharacter())
                    .isGreaterThanOrEqualTo(0);
            assertThat(d.getRange().getEnd().getCharacter())
                    .isGreaterThan(d.getRange().getStart().getCharacter());
        }
    }

    @Test
    void eofErrorRangeStaysWithinTheText() {
        // Truncated input: the offending symbol is the EOF token, whose
        // getText() is the 5-char literal "<EOF>" — the range must not be
        // widened past the actual end of the text by that placeholder.
        String text = "rule R when";
        List<Diagnostic> diags = DRLDiagnosticHelper.validate(text);

        assertThat(diags).isNotEmpty();
        for (Diagnostic d : diags) {
            assertThat(d.getRange().getEnd().getCharacter())
                    .as("range end must not extend past the line (len=%d): %s",
                            text.length(), d)
                    .isLessThanOrEqualTo(text.length());
        }
    }

    @Test
    void nullTextReturnsEmptyDiagnostics() {
        assertThat(DRLDiagnosticHelper.validate(null)).isEmpty();
    }

    @Test
    void emptyTextReturnsNoErrors() {
        assertThat(DRLDiagnosticHelper.validate("")).isEmpty();
    }
}
