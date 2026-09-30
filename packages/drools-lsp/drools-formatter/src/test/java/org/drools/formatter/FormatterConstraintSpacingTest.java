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
 * Operator spacing inside a classic pattern: a unary operator hugs its
 * operand, {@code :=} is spaced like every other binary operator, a temporal
 * window's closing bracket is followed by a space before the next word, and
 * {@code window:} keeps its name attached.
 */
class FormatterConstraintSpacingTest {

    private static String rule(String condition) {
        return "package p;\n\nrule R\n  when\n" + condition + "\n  then\nend\n";
    }

    private static void assertStable(String formatted) {
        assertThat(DRLFormatter.format(formatted)).isEqualTo(formatted);
    }

    @Test
    void unaryOperatorsHugTheirOperand() {
        String out = DRLFormatter.format(rule(
                "    $p : Person( ! adult, age < - 5, balance > -1.5, ! ( this instanceof Employee ) )"));

        assertThat(out).contains(
                "    $p: Person( !adult, age < -5, balance > -1.5, !( this instanceof Employee ) )\n");
        assertStable(out);
    }

    @Test
    void unificationIsSpacedLikeAnOperator() {
        String out = DRLFormatter.format(rule("    Person( $a:=age, name:=\"x\" )"));

        assertThat(out).contains("    Person( $a := age, name := \"x\" )\n");
        assertStable(out);
    }

    @Test
    void aTemporalWindowIsFollowedByASpace() {
        String out = DRLFormatter.format(rule(
                "    $a : Tick( company == \"X\" )\n    $b : Tick( this after[5s,8s] $a, this before[ 1m ] $a )"));

        assertThat(out).contains("    $b: Tick( this after[5s, 8s] $a, this before[1m] $a )\n");
        assertStable(out);
    }

    @Test
    void aBracketedOperatorParameterIsFollowedByASpace() {
        String out = DRLFormatter.format(rule(
                "    Route( code str[startsWith]\"R1\", code str[length]17, code str[endsWith]( $hub + \"-\" ) )"));

        assertThat(out).contains(
                "    Route( code str[startsWith] \"R1\", code str[length] 17, code str[endsWith] ( $hub + \"-\" ) )\n");
        assertStable(out);
    }

    @Test
    void aSlidingWindowNameStaysAttachedToItsColon() {
        String out = DRLFormatter.format(rule("    $t : Tick( price > 10 ) over window:length(5)"));

        assertThat(out).contains("    $t: Tick( price > 10 ) over window:length( 5 )\n");
        assertStable(out);
    }

    @Test
    void aTerminatingSemicolonIsDropped() {
        String out = DRLFormatter.format(rule("    $p : Person( age > 1 );\n    not( Order( open ) );"));

        assertThat(out).contains("    $p: Person( age > 1 )\n").contains("    )\n").doesNotContain(");");
        assertStable(out);
    }
}
