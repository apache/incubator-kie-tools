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
 * Forms the formatter keeps as written rather than rewriting: a labelled
 * or-group's parentheses, which scope the binding over every alternative;
 * the prefix {@code (or ...)} and {@code (and ...)} forms of the conditional
 * elements; and the legacy {@code attributes:} keyword.
 */
class FormatterConditionFormsTest {

    private static String rule(String condition) {
        return "package p;\n\nrule R\n  when\n" + condition + "\n  then\nend\n";
    }

    private static void assertStable(String formatted) {
        assertThat(DRLFormatter.format(formatted)).isEqualTo(formatted);
    }

    @Test
    void aLabelledOrGroupKeepsItsParentheses() {
        String out = DRLFormatter.format(rule(
                "    $c : ( Cheese( type == \"brie\" ) or Cheese( type == \"gouda\" ) )"));

        assertThat(out).contains("    $c: ( Cheese( type == \"brie\" ) or Cheese( type == \"gouda\" ) )\n");
        assertStable(out);
    }

    @Test
    void aLabelledOrGroupLongerThanTheLineStaysWhole() {
        String out = DRLFormatter.format(rule(
                "    $c : ( Cheese( type == \"brie\", origin == \"Seine-et-Marne\", ripeness > 3 )"
                + " or Cheese( type == \"gouda\", origin == \"South Holland\", ripeness > 5 ) )"));

        assertThat(out).contains(
                "    $c: ( Cheese( type == \"brie\", origin == \"Seine-et-Marne\", ripeness > 3 )"
                + " or Cheese( type == \"gouda\", origin == \"South Holland\", ripeness > 5 ) )\n");
        assertStable(out);
    }

    @Test
    void prefixOrAndAndKeepTheirForm() {
        String out = DRLFormatter.format(rule(
                "    ( or\n        not Cheese( type == \"a\" )\n"
                + "        (and $a : Cheese( type == \"a\" )\n             $b : Cheese( type == \"b\" )\n        )\n    )"));

        assertThat(out).contains(
                "    (or\n      not Cheese( type == \"a\" )\n      (and\n"
                + "        $a: Cheese( type == \"a\" )\n        $b: Cheese( type == \"b\" )\n      )\n    )\n");
        assertStable(out);
    }

    @Test
    void aPrefixFormInsideNotKeepsItsKeywordOnTheOpeningLine() {
        String out = DRLFormatter.format(rule(
                "    (not (and Cheese( type == \"a\" )\n              Wine( region == \"b\" )\n         )\n    )"));

        assertThat(out).contains(
                "    (\n      not(and\n        Cheese( type == \"a\" )\n        Wine( region == \"b\" )\n      )\n    )\n");
        assertStable(out);
    }

    @Test
    void theAttributesKeywordIsKept() {
        String out = DRLFormatter.format(
                "package p;\n\nrule R\n  attributes : salience 42, no-loop\n  when\n    Cheese()\n  then\nend\n");

        assertThat(out).contains("rule R\n  attributes: salience 42, no-loop\n  when\n");
        assertStable(out);
    }
}
