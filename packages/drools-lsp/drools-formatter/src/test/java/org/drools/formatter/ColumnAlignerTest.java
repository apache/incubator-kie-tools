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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The alignment rule for {@code declare} blocks: every column is padded to the
 * width of its longest cell in the block plus one, so a reader scanning top-down
 * gets a straight edge down each column. The final cell of a row is never padded,
 * which keeps the emitted lines free of trailing whitespace.
 */
class ColumnAlignerTest {

    @Test
    void eachColumnIsPaddedToItsLongestCellPlusOne() {
        List<String> out = ColumnAligner.align(List.of(
                List.of("Short(", "\"a\",", "1"),
                List.of("MuchLongerName(", "\"bb\",", "2")));

        assertThat(out).containsExactly(
                "Short(          \"a\",  1",
                "MuchLongerName( \"bb\", 2");
    }

    @Test
    void theFinalCellOfARowIsNotPadded() {
        List<String> out = ColumnAligner.align(List.of(
                List.of("A(", "1 ),"),
                List.of("LongerName(", "2 ),")));

        assertThat(out).allSatisfy(line -> assertThat(line).doesNotEndWith(" "));
    }

    /** A row with fewer cells simply stops; it gets no trailing padding. */
    @Test
    void aShorterRowIsNotPaddedOutToTheWidestRow() {
        List<String> out = ColumnAligner.align(List.of(
                List.of("Name(", "\"a\",", "\"b\",", "1 ),"),
                List.of("Bare,")));

        assertThat(out.get(1)).isEqualTo("Bare,");
    }

    @Test
    void aSingleRowIsEmittedWithSingleSpaceSeparators() {
        assertThat(ColumnAligner.align(List.of(List.of("A(", "\"x\",", "1 );"))))
                .containsExactly("A( \"x\", 1 );");
    }

    /** The shape of a hand-aligned string-enum block. */
    @Test
    void reproducesTheHandFormattingOfARealEnumBlock() {
        List<String> out = ColumnAligner.align(List.of(
                List.of("StatusShipped(", "\"order_status_v1\",",
                        "\"shipped\",", "3", "),"),
                List.of("StatusCancelledByUser(", "\"order_status_v1\",",
                        "\"cancelled_by_user\",", "4", "),")));

        assertThat(out).containsExactly(
                "StatusShipped(         \"order_status_v1\", "
                        + "\"shipped\",           3 ),",
                "StatusCancelledByUser( \"order_status_v1\", "
                        + "\"cancelled_by_user\", 4 ),");
    }

    @Test
    void emptyInputProducesNoLines() {
        assertThat(ColumnAligner.align(List.of())).isEmpty();
    }
}
