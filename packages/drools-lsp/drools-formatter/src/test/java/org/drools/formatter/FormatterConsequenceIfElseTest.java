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
 * Verifies that unbraced {@code if}/{@code else if}/{@code else} chains in a
 * DRL consequence are indented following standard Java rules: the single-statement
 * consequence sits one level in from its header, and an {@code else} clause
 * aligns back to the depth of the matching {@code if}.
 *
 * <p>Scope: {@code if}, {@code else if}, and {@code else} only.
 * {@code while}, {@code for}, and {@code do} loops are out of scope.
 */
class FormatterConsequenceIfElseTest {

    private static String rule(String consequence) {
        return "package p;\n\nrule R\n  when\n    $o : Order()\n  then\n" + consequence + "\nend\n";
    }

    private static void assertStable(String formatted) {
        assertThat(DRLFormatter.format(formatted)).isEqualTo(formatted);
    }

    // ── 1. Plain unbraced if ─────────────────────────────────────────────────

    @Test
    void unbracedIfIndentsConsequence() {
        String out = DRLFormatter.format(rule(
                "    if( $o.isUrgent() )\n    expedite( $o );\n    afterChain();"));

        assertThat(out).contains(
                "    if( $o.isUrgent() )\n      expedite( $o );\n    afterChain();\n");
        assertStable(out);
    }

    // ── 2. Unbraced if / else ────────────────────────────────────────────────

    @Test
    void ifElseWithoutBraces() {
        String out = DRLFormatter.format(rule(
                "    if( $o.isUrgent() )\n    expedite( $o );\n    else\n    hold( $o );"));

        assertThat(out).contains(
                "    if( $o.isUrgent() )\n      expedite( $o );\n    else\n      hold( $o );\n");
        assertStable(out);
    }

    // ── 3. Unbraced if / else if / else ──────────────────────────────────────

    @Test
    void ifElseIfElseWithoutBraces() {
        String out = DRLFormatter.format(rule(
                "    if( $o.getPriority() == 1 )\n    expedite( $o );\n"
                + "    else if( $o.getPriority() == 2 )\n    ship( $o );\n"
                + "    else\n    hold( $o );\n"
                + "    afterChain();"));

        assertThat(out).contains(
                "    if( $o.getPriority() == 1 )\n      expedite( $o );\n"
                + "    else if( $o.getPriority() == 2 )\n      ship( $o );\n"
                + "    else\n      hold( $o );\n"
                + "    afterChain();\n");
        assertStable(out);
    }

    // ── 4. Braced if, unbraced else ──────────────────────────────────────────

    @Test
    void bracedIfUnbracedElse() {
        String out = DRLFormatter.format(rule(
                "    if( $o.isUrgent() ) {\n    expedite( $o );\n    } else\n    ship( $o );"));

        assertThat(out).contains(
                "    if( $o.isUrgent() ) {\n      expedite( $o );\n    } else\n      ship( $o );\n");
        assertStable(out);
    }

    // ── 5. Unbraced if, braced else ──────────────────────────────────────────

    @Test
    void unbracedIfBracedElse() {
        String out = DRLFormatter.format(rule(
                "    if( $o.isUrgent() )\n    expedite( $o );\n    else {\n    ship( $o );\n    }"));

        assertThat(out).contains(
                "    if( $o.isUrgent() )\n      expedite( $o );\n    else {\n      ship( $o );\n    }\n");
        assertStable(out);
    }

    // ── 6. Nested unbraced if ────────────────────────────────────────────────

    @Test
    void nestedUnbracedIf() {
        String out = DRLFormatter.format(rule(
                "    if( $o.isActive() )\n    if( $o.isUrgent() )\n    expedite( $o );\n    afterChain();"));

        // Inner consequence ("expedite") must be two levels in from base.
        // afterChain must be back at the base depth.
        assertThat(out).contains(
                "    if( $o.isActive() )\n      if( $o.isUrgent() )\n        expedite( $o );\n    afterChain();\n");
        assertStable(out);
    }

    // ── 7. Single unbraced if inside a braced block ──────────────────────────

    @Test
    void unbracedIfInsideBracedBlock() {
        String out = DRLFormatter.format(rule(
                "    if( $o.isActive() ) {\n    if( $o.isUrgent() )\n    expedite( $o );\n    ship( $o );\n    }"));

        assertThat(out).contains(
                "    if( $o.isActive() ) {\n      if( $o.isUrgent() )\n        expedite( $o );\n      ship( $o );\n    }\n");
        assertStable(out);
    }
}
