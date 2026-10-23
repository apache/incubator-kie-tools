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
 * Some comment positions are not carried through formatting: inside
 * {@code accumulate(...)} parentheses, between an enum constant's arguments,
 * between a function's signature and its body. Rather than support each, the
 * formatter refuses a document whose formatted output holds fewer comments than
 * its input, so the author sees the refusal and moves the comment instead of
 * losing it. A comment that is kept but moved passes: nothing was lost.
 */
class FormatterCommentGuardTest {

    @Test
    void aCommentInsideAccumulateParenthesesIsRefusedNotDropped() {
        String drl = "package p;\nrule R\n  when\n"
                + "    accumulate( Foo( x == 1 ), /* per item */ $c : count() )\n"
                + "  then\nend\n";

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);

        assertThat(r.syntaxErrors()).isZero();
        assertThat(r.refused()).isTrue();
        assertThat(r.refusalReason()).contains("comment").contains("line 4");
    }

    @Test
    void aCommentBetweenEnumConstantArgumentsIsRefused() {
        String drl = "package p;\ndeclare enum Color\n"
                + "  RED( 1, /* note */ 2 ), GREEN( 3, 4 );\n"
                + "  a : int\n  b : int\nend\n";

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);

        assertThat(r.syntaxErrors()).isZero();
        assertThat(r.refused()).isTrue();
        assertThat(r.refusalReason()).contains("comment").contains("line 3");
    }

    @Test
    void aCommentInsideAFieldInitializerIsRefused() {
        String drl = "package p;\ndeclare Foo\n  value : int = 1 /* rationale */ + 2\nend\n";

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);

        assertThat(r.syntaxErrors()).isZero();
        assertThat(r.refused()).isTrue();
        assertThat(r.refusalReason()).contains("comment").contains("line 3");
    }

    @Test
    void aBlockCommentBeforeAPatternsFirstConstraintIsRefused() {
        String drl = "package p;\nrule R\n  when\n    Foo( /* note */ x > 1 )\n  then\nend\n";

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);

        assertThat(r.syntaxErrors()).isZero();
        assertThat(r.refused()).isTrue();
        assertThat(r.refusalReason()).contains("comment").contains("line 4");
    }

    @Test
    void aCommentBetweenAFunctionSignatureAndItsBodyIsRefused() {
        String drl = "package p;\nfunction int twice( int x ) /* doubles */ { return x * 2; }\n";

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);

        assertThat(r.syntaxErrors()).isZero();
        assertThat(r.refused()).isTrue();
        assertThat(r.refusalReason()).contains("comment").contains("line 2");
    }

    /** A comment inside a multi-line call is moved above the statement, not lost. */
    @Test
    void aMovedCommentIsNotARefusal() {
        String drl = "package p;\nrule R\n  when\n    $a : Foo()\n  then\n"
                + "    insert( new Bar( $a, // first\n        1 ) );\n"
                + "end\n";

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);

        assertThat(r.refused()).isFalse();
        assertThat(r.formatted()).contains("// first");
    }

    @Test
    void commentsInSupportedPositionsDoNotTripTheGuard() {
        String drl = String.join("\n",
                "/**",
                " * File header.",
                " */",
                "package p;",
                "",
                "// about the declare",
                "declare Foo",
                "  x : int // the count",
                "end",
                "",
                "/* ****",
                "   banner",
                "**** */",
                "rule R // named",
                "  when",
                "    // before the pattern",
                "    Foo( x > 1, // after a constraint",
                "         x < 9 )",
                "  then",
                "    // before the action",
                "    retract( $f ); // trailing",
                "end // done",
                "");

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);

        assertThat(r.refused()).as(r.refusalReason()).isFalse();
    }
}
