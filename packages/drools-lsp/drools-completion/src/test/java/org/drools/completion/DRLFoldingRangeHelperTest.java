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

import org.eclipse.lsp4j.FoldingRange;
import org.eclipse.lsp4j.FoldingRangeKind;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class DRLFoldingRangeHelperTest {

    @Test
    void emptyOrNullYieldsNoRanges() {
        assertThat(DRLFoldingRangeHelper.foldingRanges(null)).isEmpty();
        assertThat(DRLFoldingRangeHelper.foldingRanges("")).isEmpty();
    }

    @Test
    void foldsConstructsAndBlockComments() {
        String drl =
            "/*\n"                           // 0
            + " * banner\n"                  // 1
            + " */\n"                        // 2
            + "global java.util.List log\n"  // 3 (single line -> no fold)
            + "declare Person\n"             // 4
            + "  name : String\n"            // 5
            + "end\n"                        // 6
            + "rule \"R\"\n"                 // 7
            + "  when\n"                     // 8
            + "  then\n"                     // 9
            + "end\n";                       // 10

        List<FoldingRange> ranges = DRLFoldingRangeHelper.foldingRanges(drl);

        assertThat(ranges)
                .extracting(FoldingRange::getStartLine, FoldingRange::getEndLine, FoldingRange::getKind)
                .containsExactlyInAnyOrder(
                        tuple(4, 6, FoldingRangeKind.Region),    // declare Person ... end
                        tuple(7, 10, FoldingRangeKind.Region),   // rule "R" ... end
                        tuple(0, 2, FoldingRangeKind.Comment));  // /* ... */
    }

    @Test
    void singleLineConstructsProduceNoFold() {
        // A one-line declare and a global — nothing multi-line to fold.
        String drl = "global Foo bar\n";

        assertThat(DRLFoldingRangeHelper.foldingRanges(drl)).isEmpty();
    }
}
