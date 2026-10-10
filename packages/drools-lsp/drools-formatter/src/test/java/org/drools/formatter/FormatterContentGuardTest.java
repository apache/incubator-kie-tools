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
 * The other output gates ask whether the formatted document still parses,
 * keeps its comments and keeps its rules. A clause the formatter forgot to
 * emit passes all three: what is left is shorter but valid. This gate
 * compares the two token streams themselves, so any difference beyond
 * whitespace and the separators the formatter is allowed to normalise
 * refuses the document.
 */
class FormatterContentGuardTest {

    private static final String RULE_WITH_WINDOW = "package p;\nunit MarketUnit;\n\nrule R\n  when\n"
            + "    $t: /ticks[ price > 10 ] over window:length( 5 )\n  then\nend\n";

    @Test
    void aClauseMissingFromTheOutputIsReportedAtItsInputLine() {
        String output = RULE_WITH_WINDOW.replace(" over window:length( 5 )", "");

        DRLFormatter.ContentLoss loss = DRLFormatter.contentLoss(RULE_WITH_WINDOW, output);

        assertThat(loss.changedLine()).isEqualTo(6);
        assertThat(loss.gained()).isFalse();
    }

    @Test
    void contentTheOutputChangesIsReportedAtItsInputLine() {
        String output = RULE_WITH_WINDOW.replace("price > 10", "price > 100");

        DRLFormatter.ContentLoss loss = DRLFormatter.contentLoss(RULE_WITH_WINDOW, output);

        assertThat(loss.changedLine()).isEqualTo(6);
    }

    @Test
    void contentAfterTheEndOfTheInputIsAGain() {
        String output = RULE_WITH_WINDOW + "\nrule Extra\n  when\n  then\nend\n";

        DRLFormatter.ContentLoss loss = DRLFormatter.contentLoss(RULE_WITH_WINDOW, output);

        assertThat(loss.gained()).isTrue();
        assertThat(loss.changedLine()).isZero();
    }

    @Test
    void spacingAndSeparatorNormalisationIsNotAChange() {
        String input = "package p\nimport java.util.List\nrule R\n  when\n"
                + "    accumulate( Item( $v : value ), $s : sum( $v ) );\n"
                + "    $p:Person(age>18,name matches \"A.*\");\n"
                + "  then\n    int [ ] counts = new int [ ] { 1 };\n    counts [ 0 ] = -1 ;\nend\n";
        String output = "package p;\nimport java.util.List;\nrule R\n  when\n"
                + "    accumulate(\n      Item( $v: value );\n      $s: sum( $v )\n    )\n"
                + "    $p: Person( age > 18, name matches \"A.*\" )\n"
                + "  then\n    int[] counts = new int[] { 1 };\n    counts[0] = -1;\nend\n";

        assertThat(DRLFormatter.contentLoss(input, output)).isEqualTo(DRLFormatter.ContentLoss.NONE);
    }

    @Test
    void commentsAreLeftToTheCommentGate() {
        String withComment = RULE_WITH_WINDOW.replace("  when\n", "  when\n    // reviewed\n");

        assertThat(DRLFormatter.contentLoss(withComment, RULE_WITH_WINDOW)).isEqualTo(DRLFormatter.ContentLoss.NONE);
    }

    @Test
    void theRefusalNamesTheInputLine() {
        DRLFormatter.FormatResult r = new DRLFormatter.FormatResult("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, false, 0, 0);

        assertThat(r.refused()).isTrue();
        assertThat(r.refusalReason()).contains("line 6");
    }
}
