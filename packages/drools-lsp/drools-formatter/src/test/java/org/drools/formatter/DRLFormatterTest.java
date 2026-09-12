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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DRLFormatterTest {

    @Test
    void lineOfExactlyMaxLengthDoesNotWrap() {
        // A pattern line landing at exactly lineLength (110) must stay on
        // one line: every other trigger in the formatter uses > / <=, only the
        // pattern path used >= (off-by-one, wrapped one column early).
        String skeleton = "    Foo(  == 1 )";
        String name = "a" + "x".repeat(110 - skeleton.length() - 1);
        String line = "    Foo( " + name + " == 1 )";
        assertThat(line.length()).isEqualTo(110);   // self-check the construction

        String drl = "package p;\nrule R\n  when\n" + line + "\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("\n" + line + "\n");   // not reflowed
    }

    @Test
    void basicFormatting() {
        String text = "rule R\nwhen\n$fact: Fact()\nthen\nlogger.info( $fact );\nend";
        String formatted = DRLFormatter.format(text);
        String expected = "rule R\r\n" +
                "  when\r\n" +
                "    $fact: Fact()\r\n" +
                "  then\r\n" +
                "    logger.info( $fact );\r\n" +
                "end\r\n";
        assertThat(formatted.replace("\r\n", "\n")).isEqualTo(expected.replace("\r\n", "\n"));
    }

    @Test
    void complexFileFormatting() throws Exception {
        String text = Files.readString(Path.of("src", "test", "resources", "order_rules.drl"));
        String once = DRLFormatter.format(text);
        assertThat(DRLFormatter.format(once)).isEqualTo(once);
        assertThat(DRLFormatter.formatChecked(text).refused()).isFalse();
    }

    @Test
    void formatsFunctionSignatureAndKeepsBodyVerbatim() {
        // Characterizes visitFunction: the signature is reconstructed with
        // "( ... )" spacing, while the body block is emitted verbatim (original
        // indentation preserved). Guards the emitVerbatim refactor of the body.
        String input = String.join("\n",
                "function int foo(int x) {",
                "    return x + 1;",
                "}");
        String formatted = DRLFormatter.format(input).replace("\r\n", "\n");
        assertThat(formatted).contains(String.join("\n",
                "function int foo( int x )",
                "{",
                "    return x + 1;",
                "}"));
    }

    @Test
    void whitespaceOnlyInputIsReturnedUnchanged() {
        String ws = "  \r\n\t\r\n";
        assertThat(DRLFormatter.format(ws)).isEqualTo(ws);   // --write must not truncate the file
        assertThat(DRLFormatter.format("")).isEqualTo("");
        assertThat(DRLFormatter.format(null)).isEqualTo("");
    }

    @Test
    void formatRangeOnBlankInputReturnsInputUnchanged() {
        DRLFormatter.RangeResult r = DRLFormatter.formatRange("  \r\n\t\r\n", 0, 1);
        assertThat(r.text()).isEqualTo("  \r\n\t\r\n");          // was "" — a --write-style truncation shape
    }

    @Test
    void formatCheckedReportsSyntaxErrors() {
        // pre-Drools-10 syntax: agenda-group was removed in Drools 10 -> parse errors
        String drl = "package p;\nrule \"A\"\n  agenda-group \"g\"\n  when\n  then\nend\n";
        DRLFormatter.FormatResult result = DRLFormatter.formatChecked(drl);
        assertThat(result.syntaxErrors()).isGreaterThan(0);

        String clean = "package p;\nrule \"A\"\n  ruleflow-group \"g\"\n  when\n  then\nend\n";
        assertThat(DRLFormatter.formatChecked(clean).syntaxErrors()).isZero();
        assertThat(DRLFormatter.formatChecked(clean).formatted())
            .isEqualTo(DRLFormatter.format(clean));
    }

    // A parenthesised OR-group used as a non-first "and" operand reaches emission
    // through lhsExpression's #lhsExpressionEnclosed alternative rather than
    // lhsUnary's LPAREN branch. Only the latter consumed the pending "and ", so
    // the connective escaped past the "(" and landed on the group's first
    // element - "( and A() or B() )" - which changes the logic and does not
    // re-parse.
    private static final String AND_GROUP =
        "package p;\nrule \"R\"\n  when\n    A( a == 1 )\n    and (\n      B( b == 2 )\n      or\n      C( c == 3 )\n    )\n  then\nend\n";

    private static final String AND_GROUP_IN_EXISTS =
        "package p;\nrule \"R\"\n  when\n    exists(\n      A( a == 1 )\n      and (\n        B( b == 2 )\n        or\n        C( c == 3 )\n      )\n      and D( d == 4 )\n    )\n  then\nend\n";

    @Test
    void andConnectiveStaysOutsideAParenthesisedGroup() {
        for (String drl : List.of(AND_GROUP, AND_GROUP_IN_EXISTS)) {
            DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
            assertThat(r.syntaxErrors()).isZero();          // input is valid DRL
            assertThat(r.outputSyntaxErrors()).isZero();    // output re-parses clean
            assertThat(r.formatted()).contains("and (");
            assertThat(r.formatted()).doesNotContain("and B(");
        }
    }

    @Test
    void formatCheckedSkipsOutputReparseWhenInputAlreadyHasErrors() {
        // pre-Drools-10 syntax: agenda-group parse error on input. Since the
        // input never parsed cleanly, the output re-parse is skipped entirely
        // (outputSyntaxErrors stays 0 - it's not meaningful when the input was
        // already broken).
        String drl = "package p;\nrule \"A\"\n  agenda-group \"g\"\n  when\n  then\nend\n";
        DRLFormatter.FormatResult result = DRLFormatter.formatChecked(drl);
        assertThat(result.syntaxErrors()).isGreaterThan(0);
        assertThat(result.outputSyntaxErrors()).isZero();
    }

    @Test
    void formatCheckedSkipsOutputReparseForBlankInput() {
        assertThat(DRLFormatter.formatChecked("  \r\n\t\r\n").outputSyntaxErrors()).isZero();
        assertThat(DRLFormatter.formatChecked("").outputSyntaxErrors()).isZero();
        assertThat(DRLFormatter.formatChecked(null).outputSyntaxErrors()).isZero();
    }

    @Test
    void formatCheckedReportsZeroOutputErrorsForHealthyInput() {
        String clean = "rule R\nwhen\n$fact: Fact()\nthen\nlogger.info( $fact );\nend";
        assertThat(DRLFormatter.formatChecked(clean).outputSyntaxErrors()).isZero();
    }

    @Test
    void formatCheckedReturnsOnBrokenAccumulateWithCommentSwallowingFunction() {
        String drl = "package p;\nrule R\n  when\n    accumulate( Foo( x == 1 ), // count()\n    )\n  then\nend\n";
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
        assertThat(r).isNotNull();
    }

    /**
     * Decorative banner comments draw their own alignment: every row starts at
     * column 0, including the closing one. Re-anchoring "*" rows one column in
     * from the comment's start put the closing row at column 1, misaligning it
     * against its own opening row.
     */
    @Test
    void decorativeBannerCommentKeepsItsOwnAlignment() {
        String banner = "*".repeat(40);
        String drl = "package p;\n\n"
            + "/" + banner + "\n"
            + "Order totals\n"
            + banner + "/\n"
            + "rule R\n  when\n    Foo()\n  then\nend\n";

        String out = DRLFormatter.format(drl);
        String lf = out.replace("\r\n", "\n");

        assertThat(lf).contains("\n" + banner + "/\n");           // still at column 0
        assertThat(lf).doesNotContain("\n " + banner + "/");      // not shifted right
        assertThat(DRLFormatter.format(out)).isEqualTo(out);
    }

    /**
     * A star-column comment has a canonical form, so a ragged one is squared up —
     * the house style is enforced here, not merely preserved. This is what a
     * banner must not be subjected to: the two shapes are told apart per comment,
     * by whether it contains a drawn run of asterisks.
     */
    @Test
    void raggedStarColumnCommentIsSquaredUp() {
        String drl = "package p;\n"
            + "/* header\n"
            + "* flush left\n"
            + "      *   over-indented\n"
            + " */\n"
            + "rule R\n  when\n  then\nend\n";

        String out = DRLFormatter.format(drl);

        assertThat(out.replace("\r\n", "\n"))
            .contains("/* header\n * flush left\n *   over-indented\n */");
        assertThat(DRLFormatter.format(out)).isEqualTo(out);
    }

    /** A comment the formatter does re-indent takes its interior along with it. */
    @Test
    void blockCommentInteriorMovesWithItsFirstLine() {
        String drl = "package p;\n"
            + "rule R\n"
            + "when\n"
            + "/* header\n"
            + " * detail\n"
            + " */\n"
            + "Foo()\n"
            + "then\nend\n";

        String out = DRLFormatter.format(drl);

        // The comment is re-indented into the when block; its "*" rows keep their
        // one-column offset from the "/*" rather than being re-anchored.
        assertThat(out.replace("\r\n", "\n")).contains("    /* header\n     * detail\n     */");
        assertThat(DRLFormatter.format(out)).isEqualTo(out);
    }

    /**
     * A conditional named consequence hangs off lhsUnary as a sibling of the
     * pattern it guards, so visiting only the pattern dropped it — silently, since
     * the result still parses. Losing it leaves the then[label] block unreachable,
     * which is a change of rule behaviour, not of layout.
     */
    @Test
    void conditionalNamedConsequenceSurvivesFormatting() {
        String drl = """
                package p;
                rule R
                  when
                    $p : Bar( age > 18 )
                    if ( $p.age > 65 ) do[senior]
                  then
                    doSomething();
                  then[senior]
                    doSenior();
                end
                """;

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
        assertThat(r.outputSyntaxErrors()).isZero();
        String out = r.formatted().replace("\r\n", "\n");

        assertThat(out).contains("if ( $p.age > 65 ) do[senior]");
        assertThat(out).contains("then[senior]");
        assertThat(DRLFormatter.format(r.formatted())).isEqualTo(r.formatted());
    }

    /** The same sibling shape on exists/not, which take a bare do[label]. */
    @Test
    void namedConsequenceOnExistsSurvivesFormatting() {
        String drl = """
                package p;
                rule R
                  when
                    exists Bar( age > 18 ) do[found]
                  then
                  then[found]
                    doFound();
                end
                """;

        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("do[found]");
    }

    /**
     * Padding inside parens is house style, enforced rather than echoed: the
     * source here is written tight everywhere and comes out padded, with empty
     * pairs left alone.
     */
    @Test
    void parenthesesArePaddedRegardlessOfSource() {
        String drl = """
                package p;
                rule R
                  timer(int: 30s)
                  when
                    $p : Bar(age>18, name != null)
                    Empty()
                    Baz(value > (3 * 2))
                  then
                end
                """;

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
        assertThat(r.outputSyntaxErrors()).isZero();
        String out = r.formatted().replace("\r\n", "\n");

        assertThat(out).contains("Bar( age > 18, name != null )");
        assertThat(out).contains("timer( int: 30s )");
        assertThat(out).contains("Empty()");
        assertThat(out).contains("( 3 * 2 )");
        assertThat(DRLFormatter.format(r.formatted())).isEqualTo(r.formatted());
    }

    /** Signatures are string-built, so they need the paren rule applied there too. */
    @Test
    void signatureParenthesesFollowTheSameRule() {
        String drl = """
                package p;
                function int add(int a, int b) {
                    return a + b;
                }
                function void noop() {
                }
                query things(String name)
                    Bar( name == name )
                end
                """;

        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("function int add( int a, int b )");
        assertThat(out).contains("function void noop()");
        assertThat(out).contains("query things( String name )");
    }

    @Test
    void evalExpressionKeepsOperatorSpacing() {
        String drl = "package p;\nrule R\n  when\n    eval(a>b)\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("eval( a > b )");            // NOT eval(a>b)
    }

    @Test
    void commentInsideConstraintListIsPreservedWithoutCorruption() {
        String drl = "package p;\nrule R\n  when\n    Foo( x == 1, // keep me\n         y == 2 )\n  then\nend\n";
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
        assertThat(r.outputSyntaxErrors()).isZero();                        // NOT corrupted
        String out = r.formatted().replace("\r\n", "\n");
        assertThat(out).contains("// keep me");                             // comment preserved
        assertThat(out).contains("y == 2");                                 // constraint survives, uncommented
        assertThat(out.substring(out.indexOf("// keep me"))).contains("\n"); // comment ends its line
        assertThat(DRLFormatter.format(r.formatted())).isEqualTo(r.formatted()); // idempotent
    }

    @Test
    void commentAfterLastConstraintIsPreservedWithoutCorruption() {
        String drl = "package p;\nrule R\n  when\n    Foo( x == 1,\n         y == 2 // tail note\n    )\n  then\nend\n";
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
        assertThat(r.outputSyntaxErrors()).isZero();                        // NOT corrupted
        String out = r.formatted().replace("\r\n", "\n");
        assertThat(out).contains("y == 2 // tail note");                    // attached to its constraint
        String afterComment = out.substring(out.indexOf("// tail note") + "// tail note".length());
        assertThat(afterComment).startsWith("\n");                          // comment ends its line
        assertThat(afterComment).contains(")");                            // ")" survives, uncommented
        assertThat(DRLFormatter.format(r.formatted())).isEqualTo(r.formatted()); // idempotent
    }

    @Test
    void blockCommentStarAlignmentSurvives() {
        String drl = "package p;\n/*\n * line one\n *   indented detail\n */\nrule R\n  when\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("\n * line one");            // star column intact (one space before *)
        assertThat(out).contains("\n *   indented detail");   // relative interior indent preserved
    }

    @Test
    void rhsTrailingCommentStaysOnItsStatementLine() {
        String drl = "package p;\nrule R\n  when\n  then\n    insert( new Foo() ); // audit note\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("insert( new Foo() ); // audit note");
    }

    @Test
    void rhsCommentInsideCallParensStaysPutAndIdempotent() {
        String drl = "package p;\nrule R\n  when\n  then\n    foo( a, // note\n         b );\nend\n";
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
        assertThat(r.outputSyntaxErrors()).isZero();
        assertThat(DRLFormatter.format(r.formatted())).isEqualTo(r.formatted());  // one-pass idempotent
        assertThat(r.formatted().replace("\r\n", "\n")).doesNotContain("then // note");
    }

    @Test
    void overWindowGetsStyledSpacing() {
        // Emission is style-enforcing, not byte-preserving: the patternFilter is
        // re-spaced per the house token-spacing rules (needsSpaceBetween) — space
        // after the label colon, padding inside the call parens.
        String drl = "package p;\nrule R\n  when\n    Foo() over window:time(30s)\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("over window: time( 30s )");
        assertThat(out).contains("Foo()");  // an empty pair is not padded
    }

    @Test
    void wrapsMultiArgRhsCallsOntoIndentedLinesAndStaysIdempotent() {
        // Characterizes RHS wrapping of long/multi-arg calls: each argument of a
        // nested constructor/method call gets its own line, indented one level
        // deeper than the enclosing call, with the closing paren dedented back
        // to the call's own indentation. Also guards that formatting is stable
        // across repeated passes.
        String drl = "package test;\nrule R\n  when\n    Fact()\n  then\n" +
            "    OrderReviewNote expl = new OrderReviewNote( OrderReviewNoteCodes.MISSING_SHIPPING_ADDRESS_ON_ORDER.getCode(), $ref );\n" +
            "    insertLogical( new OrderReview( $ref, OrderReviewCodes.AWAITING_MANUAL_REVIEW.getNominalLabel(), Arrays.asList( expl ), false, OrderReviewCodes.AWAITING_MANUAL_REVIEW.getRank() ) );\n" +
            "end\n";

        String pass1 = DRLFormatter.format(drl);
        String pass2 = DRLFormatter.format(pass1);
        String pass3 = DRLFormatter.format(pass2);

        assertThat(pass2).isEqualTo(pass1);
        assertThat(pass3).isEqualTo(pass2);

        String normalized = pass1.replace("\r\n", "\n");
        assertThat(normalized).contains(String.join("\n",
                "    OrderReviewNote expl = new OrderReviewNote(",
                "      OrderReviewNoteCodes.MISSING_SHIPPING_ADDRESS_ON_ORDER.getCode(),",
                "      $ref",
                "    );"));
        assertThat(normalized).contains(String.join("\n",
                "    insertLogical(",
                "      new OrderReview(",
                "        $ref,",
                "        OrderReviewCodes.AWAITING_MANUAL_REVIEW.getNominalLabel(),",
                "        Arrays.asList( expl ),",
                "        false,",
                "        OrderReviewCodes.AWAITING_MANUAL_REVIEW.getRank()",
                "      )",
                "    );"));
    }

    // The DRL language reference documents inline accumulate as
    //   accumulate( <source pattern>; <functions> [;<constraints>] )
    // The grammar (lhsAndDef (COMMA|SEMI) accumulateFunction ...) also accepts
    // "," after the source pattern; the formatter normalizes it to the
    // documented ";" form, like the package/import terminators.
    @Test
    void accumulateSourcePatternCommaSeparatorIsNormalizedToSemicolon() {
        String drl = "package p;\nrule R\n  when\n    accumulate(\n      Foo( x == 1 ),\n      $sum: count()\n    )\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");

        assertThat(out).contains("Foo( x == 1 );\n      $sum: count()"); // "," normalized to the documented ";"
        long inSemi = drl.chars().filter(c -> c == ';').count();
        long outSemi = out.chars().filter(c -> c == ';').count();
        assertThat(outSemi).isEqualTo(inSemi + 1);                      // exactly the separator comma flipped
        // content modulo the sanctioned normalization families (whitespace,
        // terminators, separator commas) must match the input's
        assertThat(out.replaceAll("[\\s;,]", "")).isEqualTo(drl.replaceAll("[\\s;,]", ""));
    }

    @Test
    void accumulateSourcePatternSemicolonSeparatorIsPreserved() {
        // A source already written with ";" keeps it — normalization only
        // ever flips "," to ";", never the reverse.
        String drl = "package p;\nrule R\n  when\n    accumulate(\n      Foo( x == 1 );\n      $sum: count()\n    )\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");

        assertThat(out).contains("Foo( x == 1 );\n      $sum: count()");
        long inSemi = drl.chars().filter(c -> c == ';').count();
        long outSemi = out.chars().filter(c -> c == ';').count();
        assertThat(outSemi).isEqualTo(inSemi);
    }

    @Test
    void groupBySourcePatternCommaSeparatorIsNormalizedToSemicolon() {
        // Same normalization as accumulate, in visitLhsGroupBy (lhsAndDef
        // (COMMA|SEMI) groupByKeyBinding ...).
        String drl = "package p;\nrule R\n  when\n    groupby(\n      Foo( x == 1 ),\n      $g: x;\n      $sum: count()\n    )\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");

        assertThat(out).contains("Foo( x == 1 );\n      $g: x;"); // "," normalized to ";"
        long inSemi = drl.chars().filter(c -> c == ';').count();
        long outSemi = out.chars().filter(c -> c == ';').count();
        assertThat(outSemi).isEqualTo(inSemi + 1);
        assertThat(out.replaceAll("[\\s;,]", "")).isEqualTo(drl.replaceAll("[\\s;,]", ""));
    }

    @Test
    void importSpacingIsNormalized() {
        String drl = "package p;\nimport   com.foo.Bar\nrule R\n  when\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("import com.foo.Bar;");
    }

    @Test
    void attributeSpacingIsNormalized() {
        // Attribute values are string literals; spacing rules apply between a
        // word and a string literal too (dialect "java", ruleflow-group "g").
        String drl = "package p;\nrule R\n  ruleflow-group    \"g\"\n  dialect\"java\"\n  when\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("ruleflow-group \"g\"");
        assertThat(out).contains("dialect \"java\"");
    }

    @Test
    void positionalOnlyPatternKeepsItsMandatorySemicolon() {
        // The grammar makes the trailing ";" MANDATORY whenever
        // positionalConstraints parses (positionalConstraints : constraint
        // (COMMA constraint)* SEMI) — note "Foo( 1, 2 )" without ";" parses as
        // plain named constraints, NOT positional. Dropping the ";" therefore
        // silently changes semantics on reparse (positional -> named), and the
        // output-reparse net cannot catch it because the mutated form still
        // parses cleanly.
        String drl = "package p;\nrule R\n  when\n    Foo( 1, 2; )\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).containsPattern("2\\s*;");          // grammar-mandated ; survives
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
        assertThat(r.outputSyntaxErrors()).isZero();
        assertThat(DRLFormatter.format(r.formatted())).isEqualTo(r.formatted());
    }

    @Test
    void positionalOnlyPatternKeepsItsMandatorySemicolonWhenReflowed() {
        // Same guarantee on the reflow path (emitReflowedPattern): a
        // positional-only pattern long enough to be broken across lines keeps
        // "," between entries and the grammar-mandatory ";" on the last entry.
        String drl = "package p;\nrule R\n  when\n    Foo( aVeryLongPositionalArgumentNameNumberOne, aVeryLongPositionalArgumentNameNumberTwo, aVeryLongPositionalArgumentNameNumberThree; )\n  then\nend\n";
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);
        String out = r.formatted().replace("\r\n", "\n");
        assertThat(out).contains("aVeryLongPositionalArgumentNameNumberOne,");   // entries stay comma-separated
        assertThat(out).containsPattern("aVeryLongPositionalArgumentNameNumberThree\\s*;"); // mandatory ; survives
        assertThat(r.outputSyntaxErrors()).isZero();
        assertThat(DRLFormatter.format(r.formatted())).isEqualTo(r.formatted());
    }

    @Test
    void positionalConstraintsWithNamedSectionKeepSemicolonSeparator() {
        // Regression guard: real positional-then-named patterns must keep the
        // section-separating ";" (grammar: positionalConstraints requires it).
        String drl = "package p;\nrule R\n  when\n    Foo( 1, 2; y == 3 )\n  then\nend\n";
        String out = DRLFormatter.format(drl).replace("\r\n", "\n");
        assertThat(out).contains("Foo( 1, 2; y == 3 )");
    }

    // ── formatRanges ──────────────────────────────────────────────────────────
    //
    // Three rules, each 5 lines, no blank lines between them (0-based statement
    // spans: package 0-0, A 1-5, B 6-10, C 11-15).
    private static final String THREE_RULES = String.join("\n",
            "package p;",
            "rule \"A\"",
            "when",
            "$p : Person(  age>18 )",
            "then",
            "end",
            "rule \"B\"",
            "when",
            "$q : Person(  age>21 )",
            "then",
            "end",
            "rule \"C\"",
            "when",
            "$r : Person(  age>65 )",
            "then",
            "end",
            "");

    @Test
    void formatRangesExpandsALineSelectionToItsWholeStatement() {
        // One line inside rule B widens to the whole rule, never to part of it.
        List<DRLFormatter.RangeResult> runs =
                DRLFormatter.formatRanges(THREE_RULES, List.of(new int[]{8, 8}));

        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).startLine()).isEqualTo(6);
        assertThat(runs.get(0).endLine()).isEqualTo(10);
        assertThat(runs.get(0).text()).isEqualTo("rule \"B\"\n  when\n    $q: Person( age > 21 )\n  then\nend\n");
    }

    @Test
    void formatRangesKeepsDisjointSelectionsApart() {
        // Touching A and C must NOT drag B in — that is the whole reason the
        // multi-range form exists rather than one first..last span.
        List<DRLFormatter.RangeResult> runs =
                DRLFormatter.formatRanges(THREE_RULES, List.of(new int[]{3, 3}, new int[]{13, 13}));

        assertThat(runs).hasSize(2);
        assertThat(runs.get(0).startLine()).isEqualTo(1);
        assertThat(runs.get(0).endLine()).isEqualTo(5);
        assertThat(runs.get(1).startLine()).isEqualTo(11);
        assertThat(runs.get(1).endLine()).isEqualTo(15);
        assertThat(runs.get(1).text()).doesNotContain("rule \"B\"");
    }

    @Test
    void formatRangesMergesAdjacentTouchedStatementsIntoOneRun() {
        List<DRLFormatter.RangeResult> runs =
                DRLFormatter.formatRanges(THREE_RULES, List.of(new int[]{3, 3}, new int[]{8, 8}));

        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).startLine()).isEqualTo(1);
        assertThat(runs.get(0).endLine()).isEqualTo(10);
    }

    @Test
    void formatRangesReturnsNoRunsWhenNothingIsSelected() {
        assertThat(DRLFormatter.formatRanges(THREE_RULES, List.of())).isEmpty();
        assertThat(DRLFormatter.formatRanges(THREE_RULES, null)).isEmpty();
        assertThat(DRLFormatter.formatRanges("  \r\n\t\r\n", List.of(new int[]{0, 1}))).isEmpty();
        // A selection past the last statement overlaps nothing.
        assertThat(DRLFormatter.formatRanges(THREE_RULES, List.of(new int[]{99, 120}))).isEmpty();
    }

    @Test
    void formatRangeStillCollapsesOneSelectionToASingleSpan() {
        // Regression guard for the formatRanges refactor: a single range spanning
        // A..C keeps its original whole-span contract, B included.
        DRLFormatter.RangeResult r = DRLFormatter.formatRange(THREE_RULES, 3, 13);

        assertThat(r.startLine()).isEqualTo(1);
        assertThat(r.endLine()).isEqualTo(15);
        assertThat(r.text()).contains("rule \"B\"");
    }

    @Test
    void formatRangeStillSignalsNoEditsWithEmptyText() {
        // The "" (not the input echo) is what the LSP range-format handler maps
        // to "no edits" — DroolsLspDocumentService.rangeFormatting relies on it.
        DRLFormatter.RangeResult r = DRLFormatter.formatRange(THREE_RULES, 99, 120);
        assertThat(r.text()).isEmpty();
        assertThat(r.startLine()).isEqualTo(99);
    }

    // ── formatRanges and comments trailing a statement's last line ────────────

    /** Splices runs back into {@code text} the way {@code FormatCLI.applyRangeEdits} does. */
    private static String splice(String text, List<DRLFormatter.RangeResult> runs) {
        List<String> lines = new ArrayList<>(Arrays.asList(text.split("\n", -1)));
        List<DRLFormatter.RangeResult> ordered = new ArrayList<>(runs);
        ordered.sort((a, b) -> Integer.compare(b.startLine(), a.startLine()));
        for (DRLFormatter.RangeResult run : ordered) {
            String[] pieces = run.text().split("\n", -1);
            List<String> replacement = Arrays.asList(pieces).subList(0, pieces.length - 1);
            lines.subList(run.startLine(), Math.min(run.endLine(), lines.size() - 1) + 1).clear();
            lines.addAll(run.startLine(), replacement);
        }
        return String.join("\n", lines);
    }

    private static int occurrences(String text, String needle) {
        return text.split(java.util.regex.Pattern.quote(needle), -1).length - 1;
    }

    private static String twoRules(String firstEnd, String... betweenRules) {
        List<String> lines = new ArrayList<>(List.of(
                "package p;", "rule \"A\"", "when", "$p : Person(  age>18 )", "then", firstEnd));
        lines.addAll(Arrays.asList(betweenRules));
        lines.addAll(List.of("rule \"B\"", "when", "$q : Person(  age>21 )", "then", "end", ""));
        return String.join("\n", lines);
    }

    @Test
    void rangeRunCarriesALineCommentTrailingItsLastStatementsEnd() {
        String text = twoRules("end // done");

        List<DRLFormatter.RangeResult> runs = DRLFormatter.formatRanges(text, List.of(new int[]{1, 5}));

        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).text()).contains("end\n// done\n");
        String spliced = splice(text, runs);
        assertThat(occurrences(spliced, "// done")).isEqualTo(1);
        assertThat(DRLFormatter.formatChecked(spliced).refused()).isFalse();
        assertThat(occurrences(DRLFormatter.format(spliced), "// done")).isEqualTo(1);
    }

    @Test
    void rangeRunCarriesABlockCommentTrailingItsLastStatementsEnd() {
        String text = twoRules("end /* tail */");

        List<DRLFormatter.RangeResult> runs = DRLFormatter.formatRanges(text, List.of(new int[]{1, 5}));

        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).text()).contains("end\n/* tail */\n");
        assertThat(occurrences(splice(text, runs), "/* tail */")).isEqualTo(1);
    }

    @Test
    void rangeRunCarriesATrailingCommentOnTheFilesLastStatement() {
        String text = String.join("\n",
                "package p;", "rule \"A\"", "when", "$p : Person(  age>18 )", "then", "end // done", "");

        List<DRLFormatter.RangeResult> runs = DRLFormatter.formatRanges(text, List.of(new int[]{1, 5}));

        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).text()).contains("end\n// done\n");
        assertThat(occurrences(splice(text, runs), "// done")).isEqualTo(1);
    }

    /** A comment above the next rule belongs to that rule, not to this run. */
    @Test
    void rangeRunLeavesTheFollowingStatementsOwnCommentAlone() {
        String text = twoRules("end", "", "// about B");

        List<DRLFormatter.RangeResult> runs = DRLFormatter.formatRanges(text, List.of(new int[]{1, 5}));

        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).text()).doesNotContain("// about B");
        assertThat(occurrences(splice(text, runs), "// about B")).isEqualTo(1);
    }

    /** Both comments land on their own line after {@code end}; only one is A's. */
    @Test
    void rangeRunCarriesOnlyItsOwnTrailingCommentWhenTheNextOneIsAdjacent() {
        String text = twoRules("end // done", "// about B");

        List<DRLFormatter.RangeResult> runs = DRLFormatter.formatRanges(text, List.of(new int[]{1, 5}));

        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).text()).contains("// done").doesNotContain("// about B");
        String spliced = splice(text, runs);
        assertThat(occurrences(spliced, "// done")).isEqualTo(1);
        assertThat(occurrences(spliced, "// about B")).isEqualTo(1);
    }

    /** A block comment that runs past {@code end}'s line is one comment: carried once, never split. */
    @Test
    void rangeRunCarriesATrailingBlockCommentThatSpansLines() {
        String text = twoRules("end /* a", " b */");

        List<DRLFormatter.RangeResult> runs = DRLFormatter.formatRanges(text, List.of(new int[]{1, 5}));

        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).endLine()).isEqualTo(6);
        String spliced = splice(text, runs);
        assertThat(occurrences(spliced, "/* a")).isEqualTo(1);
        assertThat(occurrences(spliced, "b */")).isEqualTo(1);
        assertThat(DRLFormatter.formatChecked(spliced).refused()).isFalse();
    }

    // ── the final newline ─────────────────────────────────────────────────────

    @Test
    void formattedOutputEndsWithExactlyOneLineTerminator() {
        String messy = "package p;\nrule R\nwhen\nthen\nend\n\n\n";

        String lf = DRLFormatter.format(messy);
        assertThat(lf).endsWith("end\n").doesNotEndWith("\n\n");
        assertThat(DRLFormatter.format(lf)).isEqualTo(lf);

        String crlf = DRLFormatter.format(messy.replace("\n", "\r\n"));
        assertThat(crlf).endsWith("end\r\n").doesNotEndWith("\r\n\r\n");
        assertThat(DRLFormatter.format(crlf)).isEqualTo(crlf);

        assertThat(DRLFormatter.format("package p;\nrule R\nwhen\nthen\nend")).endsWith("end\n");
    }

    @Test
    void rangeRunsEndWithExactlyOneLineTerminator() {
        for (DRLFormatter.RangeResult run
                : DRLFormatter.formatRanges(THREE_RULES, List.of(new int[]{3, 3}))) {
            assertThat(run.text()).endsWith("end\n").doesNotEndWith("\n\n");
        }
    }
}
