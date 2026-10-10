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
 * Column alignment inside {@code declare} blocks. These blocks are read by
 * scanning down a column rather than across a row, so the layout is load-bearing:
 * every column is padded to its longest cell in the block plus one.
 *
 * <p>A block runs between blank lines. A comment line does not start a new block —
 * it sits inside one and takes no part in measuring the columns — so adding a note
 * to a constant list cannot silently re-align its neighbours.
 */
class FormatterDeclareAlignmentTest {

    private static String format(String drl) {
        return DRLFormatter.format(drl).replace("\r\n", "\n");
    }

    private static String enumOf(String body) {
        return "package p;\n\ndeclare enum E\n" + body + "\nend\n";
    }

    @Test
    void constantsAreAlignedIntoColumns() {
        String out = format(enumOf(String.join("\n",
                "  Short(\"a\",1),",
                "  MuchLongerName(\"bb\",2);")));

        assertThat(out).contains(String.join("\n",
                "  Short(          \"a\",  1 ),",
                "  MuchLongerName( \"bb\", 2 );"));
    }

    @Test
    void aBlankLineStartsANewBlockThatMeasuresItself() {
        String out = format(enumOf(String.join("\n",
                "  AVeryLongConstantName( 1 ),",
                "",
                "  A( 2 ),",
                "  B( 3 );")));

        // the second block is narrow: it is not padded out to the first block's width
        assertThat(out).contains(String.join("\n",
                "  A( 2 ),",
                "  B( 3 );"));
    }

    @Test
    void aCommentLineDoesNotBreakTheBlock() {
        String out = format(enumOf(String.join("\n",
                "  AAAA( 1 ),",
                "// a note",
                "  B( 2 );")));

        // B is still padded to AAAA's width - the comment did not split the block
        assertThat(out).contains(String.join("\n",
                "  AAAA( 1 ),",
                "// a note",
                "  B(    2 );"));
    }

    @Test
    void aTrailingCommentBecomesTheFinalColumn() {
        String out = format(enumOf(String.join("\n",
                "  AAAA( 1 ), // first",
                "  B( 2 );    // second")));

        assertThat(out).contains(String.join("\n",
                "  AAAA( 1 ), // first",
                "  B(    2 ); // second"));
    }

    @Test
    void fieldsAreAlignedInTwoColumns() {
        String out = format(enumOf(String.join("\n",
                "  A( 1 );",
                "  orderType:String;",
                "  shippingType:   String;")));

        assertThat(out).contains(String.join("\n",
                "  orderType:    String;",
                "  shippingType: String;"));
    }

    @Test
    void aTrailingCommentOnAFieldStaysOnItsRowAndAligns() {
        String out = format(enumOf(String.join("\n",
                "  A( 1 );",
                "  priority: String;  // specific priority",
                "  shippable: String; // ShippableState")));

        assertThat(out).contains(String.join("\n",
                "  priority:  String; // specific priority",
                "  shippable: String; // ShippableState"));
    }

    /** Alignment applies to a plain class declare's fields too, not just an enum's. */
    @Test
    void classDeclareFieldsAreAlignedToo() {
        String out = format("package p;\n\ndeclare Simple\n"
                + "  ref:LineReference;\n  values:List;\nend\n");

        assertThat(out).contains(String.join("\n",
                "  ref:    LineReference",
                "  values: List"));
    }

    /**
     * The constant list and the field list are two blocks scanning one token
     * stream. If the field block re-scans the gap the constant block already
     * consumed, the comment in that gap is emitted a second time.
     */
    @Test
    void aTrailingCommentOnTheLastConstantIsNotEmittedTwice() {
        String out = format(enumOf("  A( 1 ); // note\n  f: String;"));

        assertThat(out.lines().filter(l -> l.contains("// note"))).hasSize(1);
    }

    /**
     * A trailing comment is placed, not measured. Were it treated as an ordinary
     * cell it would set the width of whatever column it landed in, and a long
     * comment on a short row would blow that column open for every other row.
     */
    @Test
    void aTrailingCommentDoesNotWidenADataColumnOfAnotherRow() {
        String out = format(enumOf(String.join("\n",
                "  A( 1 ), // a very long trailing comment indeed",
                "  B( 2, 3, 4 );")));

        assertThat(out).contains("  B( 2, 3, 4 );");
        assertThat(out).contains("  A( 1  ), // a very long trailing comment indeed");
    }

    /**
     * The constants and the fields are separate blocks, but they are consecutive in
     * the source. A blank line between them is the author's, and the second block
     * has to know where the first one ended to keep it.
     */
    @Test
    void aBlankLineBetweenTheConstantsAndTheFieldsSurvives() {
        String out = format(enumOf("  A( 1 );\n\n  f: String;"));

        assertThat(out).contains("  A( 1 );\n\n  f: String;");
    }

    @Test
    void alignmentIsIdempotent() {
        String drl = enumOf(String.join("\n",
                "  Short(\"a\",1), // one",
                "// a note",
                "  MuchLongerName(\"bb\",2);",
                "",
                "  orderType:String;",
                "  shippingType:String;"));

        String once = DRLFormatter.format(drl);
        assertThat(DRLFormatter.format(once)).isEqualTo(once);
    }

    /** Alignment wins over the 110-column line limit; these tables are never wrapped. */
    @Test
    void alignmentIsNotAbandonedWhenARowExceedsTheLineLimit() {
        String longArg = "\"" + "x".repeat(100) + "\"";
        String out = format(enumOf("  A( " + longArg + " ),\n  BB( 1 );"));

        assertThat(out).contains("  A(  " + longArg + " ),");
        assertThat(out.lines().anyMatch(l -> l.length() > 110)).isTrue();
    }
}
