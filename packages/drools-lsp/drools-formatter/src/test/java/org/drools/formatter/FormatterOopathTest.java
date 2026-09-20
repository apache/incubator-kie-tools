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

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OOPath patterns over rule-unit data sources. The {@code /} and {@code ?/}
 * separators are path syntax, not division, so they stay tight on both sides
 * — as every published example writes them. The {@code [ ]} hold the
 * pattern's constraint list, the same thing parentheses hold in a classic
 * pattern, so they follow {@code parenPadding} the way parentheses do.
 */
class FormatterOopathTest {

    private static String rule(String condition) {
        return "package p;\nunit ShippingUnit;\n\nrule R\n  when\n" + condition + "\n  then\nend\n";
    }

    private static void assertStable(String formatted) {
        assertThat(DRLFormatter.format(formatted)).isEqualTo(formatted);
    }

    @Test
    void separatorsStayTightAndBracketsArePaddedLikeParentheses() {
        String out = DRLFormatter.format(rule(
                "    $e : /employees[age > 18, department == \"sales\"]/address[city == \"London\"]"));

        assertThat(out).contains(
                "    $e: /employees[ age > 18, department == \"sales\" ]/address[ city == \"London\" ]\n");
        assertStable(out);
    }

    @Test
    void optionalNavigationAndInlineCastsStayTight() {
        String out = DRLFormatter.format(rule("    $s : /shipments[ weight > 0 ] ?/ parcels # Fragile[ sealed ]"));

        assertThat(out).contains("    $s: /shipments[ weight > 0 ]?/parcels#Fragile[ sealed ]\n");
        assertStable(out);
    }

    @Test
    void aSegmentWithoutConstraintsIsJustThePath() {
        String out = DRLFormatter.format(rule("    $o : / orders . lines[ quantity > 1 ]"));

        assertThat(out).contains("    $o: /orders.lines[ quantity > 1 ]\n");
        assertStable(out);
    }

    @Test
    void bracketsAreTightWhenParenPaddingIsOff() {
        FormatterOptions tight = FormatterOptions.fromJson(
                JsonParser.parseString("{\"parenPadding\":false}").getAsJsonObject());

        String out = DRLFormatter.format(rule("    $e : /employees[ age > 18 ]/address[ city == \"London\" ]"), tight);

        assertThat(out).contains("    $e: /employees[age > 18]/address[city == \"London\"]\n");
        assertThat(DRLFormatter.format(out, tight)).isEqualTo(out);
    }

    @Test
    void anOopathInsideAConditionalElementKeepsItsShape() {
        String out = DRLFormatter.format(rule("    $t : /totals[ $v : value ]\n    not( /orders[ amount > $v ] )"));

        assertThat(out).contains("/totals[ $v: value ]").contains("/orders[ amount > $v ]");
        assertStable(out);
    }

    @Test
    void aSlidingWindowOnAnOopathIsKept() {
        String out = DRLFormatter.format(rule(
                "    $t : /ticks[ price > 10 ] over window:length(5)\n    /ticks over window:time( 10s )"));

        assertThat(out)
                .contains("    $t: /ticks[ price > 10 ] over window:length( 5 )\n")
                .contains("    /ticks over window:time( 10s )\n");
        assertStable(out);
    }

    @Test
    void anOopathInsideAConstraintIsWrittenAsAPath() {
        String out = DRLFormatter.format(rule(
                "    $s : Student( / addresses[street == \"Main\"] )\n"
                + "    $e : /students/plan/exams[ /grades[result > 20] ]\n"
                + "    $g : /students/plan/exams/grades[ result > ../averageResult ]"));

        assertThat(out)
                .contains("    $s: Student( /addresses[ street == \"Main\" ] )\n")
                .contains("    $e: /students/plan/exams[ /grades[ result > 20 ] ]\n")
                .contains("    $g: /students/plan/exams/grades[ result > ../averageResult ]\n");
        assertStable(out);
    }

    @Test
    void anIndexIsNotAConstraintList() {
        String out = DRLFormatter.format(rule("    $g : /students/plan/exams[ 0 ]/grades"));

        assertThat(out).contains("    $g: /students/plan/exams[0]/grades\n");
        assertStable(out);
    }

    @Test
    void aTerminatingSemicolonIsKept() {
        String out = DRLFormatter.format(rule("    /persons[ age == 10 ];\n    /addresses[ city == \"London\" ];"));

        assertThat(out).contains("    /persons[ age == 10 ];\n    /addresses[ city == \"London\" ];\n");
        assertStable(out);
    }

    @Test
    void anOopathLongerThanTheLineLengthIsStillNormalised() {
        String out = DRLFormatter.format(rule(
                "    $o : /orders[ amount > 100, customer.name == \"Alexander Hamilton Junior\", status == \"OPEN\" ]"
                + "/lines[ quantity > 10, product.genre.name == \"Books\" ] over window:length(3)"));

        assertThat(out).contains(
                "    $o: /orders[ amount > 100, customer.name == \"Alexander Hamilton Junior\", status == \"OPEN\" ]"
                + "/lines[ quantity > 10, product.genre.name == \"Books\" ] over window:length( 3 )\n");
        assertStable(out);
    }
}
