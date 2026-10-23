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

package org.drools.formatter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * What a {@code declare enum} body must carry through, independent of how it is
 * laid out. Layout itself — the column alignment — is
 * {@link FormatterDeclareAlignmentTest}'s subject; these tests cover the content
 * that alignment must not cost.
 *
 * <p>Two things were being dropped: a comment sitting between the enum header and
 * its first constant (outside the {@code enumeratives} token span entirely) and
 * every blank line inside the body (skipped by an is-empty test). Both are how
 * string-enum files group long constant lists, so losing them
 * costs exactly the readability the layout was for.
 */
class FormatterEnumDeclarationTest {

    private static String format(String drl) {
        return DRLFormatter.format(drl).replace("\r\n", "\n");
    }

    @Test
    void aCommentBeforeTheFirstConstantSurvives() {
        String drl = String.join("\n",
                "package p;",
                "",
                "declare enum Config",
                "// BigDecimals",
                "  Alpha,",
                "  Beta",
                "end",
                "");

        assertThat(format(drl)).contains("// BigDecimals");
    }

    @Test
    void blankLinesBetweenConstantGroupsSurvive() {
        String drl = String.join("\n",
                "package p;",
                "",
                "declare enum Config",
                "  Alpha,",
                "  Beta,",
                "",
                "  Gamma,",
                "  Delta",
                "end",
                "");

        assertThat(format(drl)).contains("  Beta,\n\n  Gamma,");
    }

    /** House policy elsewhere in the formatter is at most one blank line. */
    @Test
    void aRunOfBlankLinesCollapsesToOne() {
        String drl = String.join("\n",
                "package p;",
                "",
                "declare enum Config",
                "  Alpha,",
                "",
                "",
                "",
                "  Beta",
                "end",
                "");

        assertThat(format(drl)).contains("  Alpha,\n\n  Beta");
    }

    /**
     * The body used to be emitted verbatim so that hand-aligned columns survived.
     * Column alignment supersedes that: the columns are now computed rather than
     * copied. What has to hold instead is that a block already in canonical form is
     * a fixed point — that is what makes re-formatting an aligned file a no-op.
     *
     * @see FormatterDeclareAlignmentTest
     */
    @Test
    void aBlockAlreadyInCanonicalFormIsAFixedPoint() {
        String aligned = String.join("\n",
                "  PartShipped( \"shipment_status_v1\", \"partial\", 5 ),",
                "  FullShipped( \"shipment_status_v1\", \"shipped\", 4 );");
        String drl = "package p;\n\ndeclare enum StringEnums\n" + aligned + "\nend\n";

        assertThat(format(drl)).contains(aligned);
    }

    @Test
    void theMandatoryConstantListTerminatorIsStillEmitted() {
        String drl = String.join("\n",
                "package p;",
                "",
                "declare enum Config",
                "// leading",
                "  Alpha,",
                "",
                "  Beta",
                "end",
                "");

        // enumDeclaration : ENUM name drlAnnotation* enumeratives SEMI field* DRL_END
        assertThat(format(drl)).contains("  Beta;");
        assertThat(DRLFormatter.formatChecked(drl).outputSyntaxErrors()).isZero();
    }

    /** Already worked - guards it against the fix. */
    @Test
    void aCommentBetweenConstantsStillSurvives() {
        String drl = String.join("\n",
                "package p;",
                "",
                "declare enum Config",
                "  Alpha,",
                "// Booleans",
                "  Beta",
                "end",
                "");

        assertThat(format(drl)).contains("// Booleans");
    }

    /**
     * The group-header comments in such files sit at
     * column 0, all of them. Re-indenting only the first — the one that happens to
     * open the body — would leave the set misaligned with each other, which is
     * worse than either column consistently. The body is verbatim; that has to
     * include its first line.
     */
    @Test
    void groupCommentsAllKeepTheColumnTheAuthorPutThemIn() {
        String drl = String.join("\n",
                "package p;",
                "",
                "declare enum Config",
                "// BigDecimals",
                "  Alpha,",
                "// Booleans",
                "  Beta",
                "end",
                "");

        // Anchored on the preceding newline: without it the assertion would also
        // match a re-indented "  // BigDecimals", which is the very thing at issue.
        assertThat(format(drl))
                .contains("declare enum Config\n// BigDecimals\n  Alpha,\n// Booleans\n  Beta;");
    }

    /**
     * A body crammed onto the header's line is a table with no rows yet. Each
     * constant becomes its own row, which is what gives the column alignment
     * something to align.
     */
    @Test
    void aBodyOnTheHeaderLineBecomesOneConstantPerRow() {
        String drl = "package p;\n\ndeclare enum Config Alpha, Beta;\nend\n";

        assertThat(format(drl)).contains("declare enum Config\n  Alpha,\n  Beta;\nend");
    }

    @Test
    void formattingAnEnumIsIdempotent() {
        String drl = String.join("\n",
                "package p;",
                "",
                "declare enum Config",
                "// BigDecimals",
                "  Alpha,",
                "",
                "// Booleans",
                "  Beta",
                "end",
                "");

        String once = DRLFormatter.format(drl);
        assertThat(DRLFormatter.format(once)).isEqualTo(once);
    }
}
