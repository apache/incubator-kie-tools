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
 * The when-block guard catches a rule whose own {@code when} was swallowed. It
 * is blind when the swallowing annotation runs on into the <em>next</em> rule
 * and stops at that rule's {@code when}: the first rule then has a when-block
 * (the second rule's), the parse reports no error, and the second rule's
 * header is annotation payload that the formatter flattens onto one line.
 * Every {@code rule} and {@code query} keyword must therefore start its own
 * statement, or be the identifier the grammar lets those keywords double as.
 */
class FormatterMisreadStatementGuardTest {

    /**
     * Reduced from a rule set written for the legacy parser: {@code size >= 0 &&
     * <=20} has no left operand on its second comparison, which the DRL10 grammar
     * does not accept. The second rule's annotation is what the first rule's
     * payload needs to close on, so the shape needs both rules.
     */
    private static final String SWALLOWED = String.join("\n",
            "package p;",
            "",
            "rule \"Alpha T1 Test\"",
            "\t@Ref_(41)",
            "\twhen",
            "\t\t$input: Answer($order: ref.order, ref.group in( \"staging\", \"restaging\" ))",
            "\t\t$primary: Finding($ref: ref, ref.order == $order, size >= 0 && <=20, confirmed == true)",
            "\t\t$response: Response(ref == $input.ref, value == \"t1\", $explanationList: explanations)",
            "\t\tExplanation( explId== \"E1\" ) from $response.explanations",
            "\tthen",
            "\t\tinsertLogical( new TestOutputFact( \"Ref_\" + drools.getRule().getName(), $input.getRef(), true ) );",
            "end",
            "",
            "rule \"Alpha T4a-1 Test\"",
            "\t@Ref_(46)",
            "\twhen",
            "\t\t$input: Answer($order: ref.order, ref.group in( \"staging\", \"restaging\" ))",
            "\t\t$primary: Finding($ref: ref, ref.order == $order, size >= 0, confirmed == true)",
            "\t\t$bone: Answer(ref.order == $ref.order, ref.group == \"bone\", value == \"invasion\")",
            "\t\t$response: Response(ref == $input.ref, value == \"t4a\", $explanationList: explanations)",
            "\t\tExplanation( explId== \"EInfiltration\", question ==  $bone.ref.question) from $response.explanations",
            "\tthen",
            "\t\tinsertLogical( new TestOutputFact( \"Ref_\" + drools.getRule().getName(), $input.getRef(), true ) );",
            "end",
            "");

    @Test
    void aRuleSwallowedIntoTheAnnotationBeforeItIsRefused() {
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(SWALLOWED);

        assertThat(r.syntaxErrors()).as("the mis-derivation reports no errors").isZero();
        assertThat(r.rulesMissingLhs()).as("and the first rule ends up with the second's when-block").isZero();

        assertThat(r.refused()).isTrue();
        assertThat(r.refusalReason()).contains("line 14");
    }

    @Test
    void theKeywordsUsedAsIdentifiersAreNotStatements() {
        String drl = "package p;\nrule R\n  when\n    Foo( rule == 1, query == 2 )\n  then\nend\n";

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(drl);

        assertThat(r.syntaxErrors()).isZero();
        assertThat(r.refused()).as(r.refusalReason()).isFalse();
    }
}
