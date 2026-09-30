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
 * A {@code switch} in a consequence: the statements under a label sit one
 * level in, and stay there until the next label or the switch's own closing
 * brace. A block that opens and closes inside a case, or a nested switch,
 * must not end the case early.
 */
class FormatterConsequenceSwitchTest {

    private static String rule(String consequence) {
        return "package p;\n\nrule R\n  when\n    $o : Order( $k : kind )\n  then\n" + consequence + "\nend\n";
    }

    private static void assertStable(String formatted) {
        assertThat(DRLFormatter.format(formatted)).isEqualTo(formatted);
    }

    @Test
    void labelsIndentTheirStatements() {
        String out = DRLFormatter.format(rule(
                "    switch( $k ) {\n    case 1:\n    ship( $o );\n    break;\n    default:\n    hold( $o );\n    }"));

        assertThat(out).contains(
                "    switch( $k ) {\n      case 1:\n        ship( $o );\n        break;\n      default:\n        hold( $o );\n    }\n");
        assertStable(out);
    }

    @Test
    void aBlockInsideACaseDoesNotEndTheCase() {
        String out = DRLFormatter.format(rule(
                "    switch( $k ) {\n    case 1:\n    if( $o.isUrgent() ) {\n    expedite( $o );\n    }\n"
                + "    ship( $o );\n    break;\n    default:\n    hold( $o );\n    }"));

        assertThat(out).contains(
                "    switch( $k ) {\n      case 1:\n        if( $o.isUrgent() ) {\n          expedite( $o );\n        }\n"
                + "        ship( $o );\n        break;\n      default:\n        hold( $o );\n    }\n");
        assertStable(out);
    }

    @Test
    void anElseBranchInsideACaseStaysAtTheCaseDepth() {
        String out = DRLFormatter.format(rule(
                "    switch( $k ) {\n    case 1:\n    if( $o.isUrgent() ) {\n    expedite( $o );\n    } else {\n"
                + "    ship( $o );\n    }\n    break;\n    }"));

        assertThat(out).contains(
                "    switch( $k ) {\n      case 1:\n        if( $o.isUrgent() ) {\n          expedite( $o );\n        } else {\n"
                + "          ship( $o );\n        }\n        break;\n    }\n");
        assertStable(out);
    }

    @Test
    void aNestedSwitchKeepsTheOuterCaseDepth() {
        String out = DRLFormatter.format(rule(
                "    switch( $k ) {\n    case 1:\n    switch( $o.getPriority() ) {\n    case 2:\n    expedite( $o );\n    break;\n    }\n"
                + "    ship( $o );\n    break;\n    }"));

        assertThat(out).contains(
                "    switch( $k ) {\n      case 1:\n        switch( $o.getPriority() ) {\n          case 2:\n            expedite( $o );\n"
                + "            break;\n        }\n        ship( $o );\n        break;\n    }\n");
        assertStable(out);
    }
}
