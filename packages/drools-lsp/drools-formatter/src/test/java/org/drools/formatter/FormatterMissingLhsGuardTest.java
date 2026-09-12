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
 * The syntax-error gates alone cannot see every way the parser fails to read a
 * document. {@code drlAnnotation} carries a {@code chunk} — a catch-all that
 * absorbs arbitrary text — so when something later in a rule does not parse,
 * ANTLR can settle on a derivation where the annotation's chunk swallows the
 * rule's {@code when} block and everything after it. That derivation has ZERO
 * syntax errors, so both {@code syntaxErrors} and {@code outputSyntaxErrors}
 * report clean while the formatter is emitting a lie: the swallowed span comes
 * out as one flat line with its whitespace stripped and its structure gone.
 *
 * <p>The guard is structural rather than error-count based, which is what makes
 * it immune to that escape hatch: the grammar is
 * {@code ruledef : ... lhs? rhs ...} with {@code lhs : DRL_WHEN lhsExpression*},
 * so a {@code when} token inside a rule whose {@code lhs} is absent means the
 * rule was mis-derived. A rule with no {@code when} at all is legal and must not
 * be flagged.
 */
class FormatterMissingLhsGuardTest {

    /**
     * The production shape, minimised: an annotated rule, then a function whose
     * body does not parse (a call missing its closing paren), then another rule.
     * The function body is what fails, but the damage lands on the rule ABOVE it
     * — its when-block, the function, and the next rule's when-block are all
     * crushed onto one line.
     *
     * <p>The trigger is deliberately a genuine typo rather than a construct the
     * grammar merely cannot parse yet. This fixture used a lambda until
     * incubator-kie-6891 taught the grammar to read one, at which point it stopped
     * provoking the mis-derivation and the guard lost its regression test. Invalid
     * input cannot be fixed out from under the test the same way.
     */
    private static final String SWALLOWED = String.join("\n",
            "package p;",
            "",
            "rule \"R1\"",
            "  @implements( \"RM-1\" )",
            "  when",
            "    Foo( bar == 1 )",
            "  then",
            "    doThing();",
            "end",
            "",
            "function ArrayList collect( List xs, List out )",
            "{",
            "  out.add( xs.get( 0 );",
            "  return out;",
            "}",
            "",
            "rule \"R2\"",
            "  when",
            "    Baz( qux == 2 )",
            "  then",
            "    other();",
            "end",
            "");

    @Test
    void aSwallowedWhenBlockIsRefusedEvenThoughTheParseLooksClean() {
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(SWALLOWED);

        // The point of the guard: neither error count sees this.
        assertThat(r.syntaxErrors()).as("the mis-derivation reports no errors").isZero();
        assertThat(r.outputSyntaxErrors()).as("nor does re-parsing the output").isZero();

        assertThat(r.rulesMissingLhs()).isEqualTo(1);
        assertThat(r.refused()).isTrue();
        assertThat(r.refusalReason()).contains("when");
    }

    /**
     * The state LungRads_Base.drl was left in by the connective-escape bug: the
     * "and" sits inside the parenthesised group. Such a file must be refused, not
     * silently flattened by the next --write.
     */
    @Test
    void anAlreadyCorruptedParenGroupIsRefusedRatherThanFlattened() {
        String corrupted = String.join("\n",
                "package p;",
                "rule \"R\"",
                "  @implements( \"RM-1\" )",
                "  when",
                "    exists(",
                "      A( a == 1 )",
                "      (",
                "        and B( b == 2 )",
                "        or",
                "        C( c == 3 )",
                "      )",
                "      and D( d == 4 )",
                "    )",
                "  then",
                "end",
                "");
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(corrupted);

        assertThat(r.rulesMissingLhs()).isPositive();
        assertThat(r.refused()).isTrue();
    }

    /**
     * A lambda in a function body is ordinary DRL since incubator-kie-6891: it
     * parses, so it neither mis-derives the rules around it nor needs an
     * allowance to escape refusal.
     */
    @Test
    void aFunctionBodyUsingALambdaFormatsNormally() {
        String lambdaFunction = String.join("\n",
                "package p;",
                "",
                "function ArrayList collect( List xs, List out )",
                "{",
                "  xs.forEach( e -> out.add( e ) );",
                "  return out;",
                "}",
                "",
                "rule \"R\"",
                "  when",
                "    Foo( bar == 1 )",
                "  then",
                "end",
                "");
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(lambdaFunction);

        assertThat(r.syntaxErrors()).as("the grammar reads a lambda now").isZero();
        assertThat(r.outputSyntaxErrors()).isZero();
        assertThat(r.rulesMissingLhs()).isZero();
        assertThat(r.outputRulesMissingLhs()).isZero();
        assertThat(r.refused()).isFalse();
    }

    /** {@code lhs?} is optional in the grammar: a rule with no when-block is legal. */
    @Test
    void aRuleWithNoWhenBlockIsNotFlagged() {
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(
                "package p;\nrule \"R\"\n  then\n    doThing();\nend\n");

        assertThat(r.rulesMissingLhs()).isZero();
        assertThat(r.refused()).isFalse();
    }

    /** A "when" inside a rule NAME must not be mistaken for a when-block. */
    @Test
    void theWordWhenInsideARuleNameIsNotAWhenBlock() {
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(
                "package p;\nrule \"decide when to escalate\"\n  then\n    doThing();\nend\n");

        assertThat(r.rulesMissingLhs()).isZero();
        assertThat(r.refused()).isFalse();
    }

    @Test
    void anOrdinaryRuleIsNotRefused() {
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(
                "package p;\nrule \"R\"\n  @implements( \"RM-1\" )\n  when\n    Foo( bar == 1 )\n  then\nend\n");

        assertThat(r.refused()).isFalse();
        assertThat(r.refusalReason()).isNull();
    }
}
