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

package org.drools.completion;

import java.util.List;

import org.eclipse.lsp4j.InlayHint;
import org.eclipse.lsp4j.InlayHintKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DRLInlayHintHelperTest {

    @Test
    void emptyTextReturnsNoHints() {
        assertThat(DRLInlayHintHelper.getHints("", null)).isEmpty();
        assertThat(DRLInlayHintHelper.getHints(null, null)).isEmpty();
    }

    @Test
    void emitsAccumulateResultBindingHint() {
        AccumulateFunctionTypes.set(java.util.Map.of("count", "Long"));
        try {
            String drl =
                "rule \"r\" when\n" +
                "  accumulate( $i : Foo(); $count : count() )\n" +
                "then\n" +
                "end\n";

            List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

            assertThat(hints).anySatisfy(h -> assertThat(labelOf(h)).isEqualTo(": Long"));
        } finally {
            AccumulateFunctionTypes.set(null);  // reset shared holder for test isolation
        }
    }

    @Test
    void emitsBigDecimalSumAccumulateHintFromBuiltins() {
        // sumBD (BigDecimalSumAccumulateFunction -> BigDecimal) is a built-in,
        // seeded by default — no manual AccumulateFunctionTypes.set needed.
        String drl =
            "rule \"r\" when\n" +
            "  accumulate( $i : Foo(); $total : sumBD($i.amount) )\n" +
            "then\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        assertThat(hints).anySatisfy(h -> assertThat(labelOf(h)).isEqualTo(": BigDecimal"));
    }

    @Test
    void singleBindingUsedInRhsProducesHint() {
        String drl =
            "rule \"r\" when\n" +
            "  $p : Patient(age > 18)\n" +
            "then\n" +
            "  update($p);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        assertThat(hints).hasSize(1);
        InlayHint hint = hints.get(0);
        assertThat(hint.getKind()).isEqualTo(InlayHintKind.Type);
        assertThat(labelOf(hint)).isEqualTo(": Patient");
        // `$p` ends at column 11 on the RHS line: "  update($p);"
        //  cols: 0..1 spaces, 2..7 "update", 8 "(", 9 "$", 10 "p", end = 11
        assertThat(hint.getPosition()).isEqualTo(new Position(3, 11));
    }

    @Test
    void hintExactlyAtRangeEndIsExcluded() {
        // Same DRL as singleBindingUsedInRhsProducesHint: the lone hint sits at
        // (3, 11). LSP ranges are end-exclusive, so a range ending exactly at the
        // hint position must NOT include it, while extending the end past it does.
        String drl =
            "rule \"r\" when\n" +
            "  $p : Patient(age > 18)\n" +
            "then\n" +
            "  update($p);\n" +
            "end\n";

        Range endsAtHint = new Range(new Position(0, 0), new Position(3, 11));
        assertThat(DRLInlayHintHelper.getHints(drl, endsAtHint)).isEmpty();

        Range endsPastHint = new Range(new Position(0, 0), new Position(3, 12));
        assertThat(DRLInlayHintHelper.getHints(drl, endsPastHint)).hasSize(1);
    }

    @Test
    void rhsReferencesWithoutLhsBindingAreIgnored() {
        String drl =
            "rule \"r\" when\n" +
            "  $p : Patient()\n" +
            "then\n" +
            "  $unknown.doStuff();\n" +
            "  update($p);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        assertThat(hints).hasSize(1);
        assertThat(labelOf(hints.get(0))).isEqualTo(": Patient");
    }

    @Test
    void fullyQualifiedPatternTypeStillBindsOuterVariable() {
        // A fully-qualified pattern type starts with a lowercase package segment;
        // the binding `$p` must still resolve to the (simple) type name and the
        // RHS usage get a hint.
        String drl =
            "rule \"r\" when\n" +
            "  $p : org.example.Patient(age > 18)\n" +
            "then\n" +
            "  update($p);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        assertThat(hints).hasSize(1);
        assertThat(labelOf(hints.get(0))).isEqualTo(": Patient");
    }

    @Test
    void multipleUsagesAllGetHints() {
        String drl =
            "rule \"r\" when\n" +
            "  $p : Patient()\n" +
            "then\n" +
            "  log($p);\n" +
            "  update($p);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        assertThat(hints).hasSize(2);
        assertThat(hints).allSatisfy(h ->
            assertThat(labelOf(h)).isEqualTo(": Patient"));
    }

    @Test
    void multipleRulesAreScopedIndependently() {
        String drl =
            "rule \"r1\" when\n" +
            "  $p : Patient()\n" +
            "then\n" +
            "  update($p);\n" +
            "end\n" +
            "\n" +
            "rule \"r2\" when\n" +
            "  $a : Account()\n" +
            "then\n" +
            "  // $p is not bound in r2 -- ignored\n" +
            "  update($p);\n" +
            "  update($a);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        // r1: one Patient hint. r2: one Account hint (the `$p` in r2 is unbound).
        assertThat(hints).hasSize(2);
        assertThat(labelOf(hints.get(0))).isEqualTo(": Patient");
        assertThat(labelOf(hints.get(1))).isEqualTo(": Account");
    }

    @Test
    void implicitFieldBindingResolvesViaDeclaredType() {
        // A DRL-declared type provides the field types. The current-file
        // declare block should be picked up automatically by the helper.
        String drl =
            "declare RelevantLesionState\n" +
            "    ref : QuestionRef\n" +
            "    value : String\n" +
            "end\n" +
            "\n" +
            "rule \"r\" when\n" +
            "  RelevantLesionState(\n" +
            "    $ref: ref,\n" +
            "    $value: value\n" +
            "  )\n" +
            "then\n" +
            "  insert(new Something($ref, $value));\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        // Two LHS hints at the declarations + two RHS hints at the usages.
        assertThat(hints).hasSize(4);
        assertThat(hints).extracting(DRLInlayHintHelperTest::labelOf)
            .containsExactlyInAnyOrder(
                ": QuestionRef", ": String",   // LHS declarations
                ": QuestionRef", ": String");  // RHS usages
    }

    @Test
    void dottedExpressionBindingProducesNoHint() {
        // `$presentOrder: ref.order` is a nested path whose tail is
        // unresolvable here: `ref` is QuestionRef, but QuestionRef isn't in the
        // type index, so the walk can't reach `order`. Must be silently skipped,
        // not hinted as the outer field's type. (Resolvable nested paths DO get
        // a hint -- see nestedPathBindingResolvesViaSharedCollector.)
        String drl =
            "declare RelevantLesionState\n" +
            "    ref : QuestionRef\n" +
            "end\n" +
            "\n" +
            "rule \"r\" when\n" +
            "  RelevantLesionState(\n" +
            "    $presentOrder: ref.order\n" +
            "  )\n" +
            "then\n" +
            "  use($presentOrder);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);
        assertThat(hints).isEmpty();
    }

    @Test
    void bindingUsedInLaterPatternConstraintGetsLhsHint() {
        // Real-world pattern: bind $ref in one pattern, then constrain by
        // $ref in subsequent patterns. The usage `$ref.order` on the LHS
        // should also get a hint, not just the RHS usage.
        String drl =
            "declare LesionState\n" +
            "    ref : QuestionRef\n" +
            "end\n" +
            "\n" +
            "rule \"r\" when\n" +
            "  LesionState(\n" +
            "    $ref: ref\n" +
            "  )\n" +
            "  exists LesionState(\n" +
            "    ref.order == $ref.order,\n" +
            "    ref.group == \"foo\"\n" +
            "  )\n" +
            "then\n" +
            "  use($ref);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        // Three hints: LHS declaration, LHS usage in constraint, RHS usage.
        assertThat(hints).hasSize(3);
        assertThat(hints).extracting(DRLInlayHintHelperTest::labelOf)
            .containsOnly(": QuestionRef");
    }

    @Test
    void fieldFromParentTypeResolvesViaExtendsChain() {
        // InputLocationFact has no fields of its own; `location` comes from
        // LocationFact (parent). The helper must walk the extends chain.
        String drl =
            "declare LocationFact\n" +
            "    location : Location\n" +
            "end\n" +
            "\n" +
            "declare InputLocationFact extends LocationFact\n" +
            "end\n" +
            "\n" +
            "rule \"r\" when\n" +
            "  InputLocationFact(\n" +
            "    $loc: location\n" +
            "  )\n" +
            "then\n" +
            "  use($loc);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        // LHS hint at the declaration ($loc: location) + RHS hint at use($loc).
        assertThat(hints).hasSize(2);
        assertThat(hints).extracting(DRLInlayHintHelperTest::labelOf)
            .containsExactly(": Location", ": Location");
    }

    @Test
    void unknownTypeYieldsNoFieldBindingHints() {
        // No declared type for SomePattern -> we can't resolve `ref`'s type,
        // so no hints. Outer pattern binding ($p) is still resolved.
        String drl =
            "rule \"r\" when\n" +
            "  $p : SomePattern(\n" +
            "    $ref: ref\n" +
            "  )\n" +
            "then\n" +
            "  use($p);\n" +
            "  use($ref);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);
        assertThat(hints).hasSize(1);
        assertThat(labelOf(hints.get(0))).isEqualTo(": SomePattern");
    }

    @Test
    void lhsBindingsThemselvesGetNoHints() {
        // The declaration `$p : Patient(...)` already names the type, so the
        // helper should not emit a hint there. Only RHS references matter.
        String drl =
            "rule \"r\" when\n" +
            "  $p : Patient()\n" +
            "then\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);
        assertThat(hints).isEmpty();
    }

    @Test
    void nestedPathBindingResolvesViaSharedCollector() {
        String drl =
            "declare Ref\n" +
            "    dynIndex : Integer\n" +
            "end\n" +
            "\n" +
            "declare InputNominalFact\n" +
            "    ref : Ref\n" +
            "end\n" +
            "\n" +
            "rule \"r\" when\n" +
            "  InputNominalFact(\n" +
            "    $idx : ref.dynIndex\n" +
            "  )\n" +
            "then\n" +
            "  use($idx);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        // LHS declaration hint + RHS usage hint, both Integer.
        assertThat(hints).extracting(DRLInlayHintHelperTest::labelOf)
            .containsExactlyInAnyOrder(": Integer", ": Integer");
    }

    @Test
    void jdkAccessorBindingResolvesViaSharedCollector() {
        // $count : intValue on a JDK Number pattern.
        String drl =
            "rule \"r\" when\n" +
            "  Number( $count : intValue )\n" +
            "then\n" +
            "  use($count);\n" +
            "end\n";

        List<InlayHint> hints = DRLInlayHintHelper.getHints(drl, null);

        assertThat(hints).extracting(DRLInlayHintHelperTest::labelOf)
            .containsExactlyInAnyOrder(": int", ": int");
    }

    private static String labelOf(InlayHint hint) {
        Either<String, ?> label = hint.getLabel();
        return label.isLeft() ? label.getLeft() : null;
    }
}
