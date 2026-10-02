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

package org.drools.lsp.server;

import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BuildMessageRangesTest {

    private static final String ANALYSE_MSG =
            "Unable to Analyse Expression item.kind == StatusCodes.Pendng.kindType: "
            + "[Error: unable to resolve method using strict-mode: "
            + "com.example.rules.StatusCodes.Pendng()] "
            + "[Near : {... item.kind == StatusCodes.Pendng.kindType ....}]";

    private static final String CONSTRAINT = "    item.kind == StatusCodes.Pendng.kindType,";

    private static String documentWithConstraintAtLine(int line) {
        StringBuilder sb = new StringBuilder("package com.example;\n");
        while (countLines(sb) < line) {
            sb.append("// filler\n");
        }
        sb.append(CONSTRAINT).append('\n');
        return sb.toString();
    }

    private static int countLines(CharSequence s) {
        int n = 1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                n++;
            }
        }
        return n;
    }

    @Test
    void unquotedExpressionIsExtractedFromAnalyseMessage() {
        assertThat(BuildMessageRanges.extractSearchTokens(ANALYSE_MSG))
                .contains("item.kind == StatusCodes.Pendng.kindType");
    }

    @Test
    void quotedTokensStillExtracted() {
        assertThat(BuildMessageRanges.extractSearchTokens(
                "Unable to resolve ObjectType 'OrderStatus' : [Rule name='R']"))
                .contains("OrderStatus");
    }

    @Test
    void analyseMessageWithoutLineAnchorsAtTheExpressionNotTheFileStart() {
        String text = documentWithConstraintAtLine(50);

        Range range = BuildMessageRanges.trySmartRange(ANALYSE_MSG, 0, text);

        assertThat(range).isNotNull();
        assertThat(range.getStart().getLine()).isEqualTo(49);
        assertThat(range.getStart().getCharacter()).isEqualTo(4);
    }

    @Test
    void messageWithNoRecoverableTokenYieldsNoSmartRange() {
        assertThat(BuildMessageRanges.trySmartRange(
                "Rules compilation failed or interrupted", 0, "package com.example;\n")).isNull();
    }

    @Test
    void ruleHeaderIsTheFallbackWhenNothingElseAnchors() {
        String text = "package com.example;\n\nrule \"Second\"\n    when\n    then\nend\n";

        Range range = BuildMessageRanges.rangeFor(
                "Rules compilation failed or interrupted [Rule name='Second']", 0, 0, text);

        assertThat(range.getStart().getLine()).isEqualTo(2);
        assertThat(range.getStart().getCharacter()).isEqualTo(0);
        assertThat(range.getEnd().getCharacter()).isEqualTo(13);
    }

    @Test
    void rawLineAndColumnWhenNoTokenMatches() {
        String text = "package com.example;\nrule R\n    when\n        Order( total > 1 )\n    then\nend\n";

        Range range = BuildMessageRanges.rangeFor("something the file does not contain", 4, 9, text);

        assertThat(range.getStart().getLine()).isEqualTo(3);
        assertThat(range.getStart().getCharacter()).isEqualTo(8);
    }

    @Test
    void fileStartWhenNothingIsKnown() {
        Range range = BuildMessageRanges.rangeFor("no anchor at all", 0, 0, "package com.example;\n");

        assertThat(range.getStart().getLine()).isZero();
        assertThat(range.getEnd().getCharacter()).isEqualTo(1);
    }

    @Test
    void crlfDocumentAnchorsOnTheRightLine() {
        String text = "package com.example;\r\nrule R\r\n    when\r\n        Order( totl > 1 )\r\n    then\r\nend\r\n";

        Range range = BuildMessageRanges.rangeFor(
                "Unable to Analyse Expression totl > 1: [Near : {... totl > 1 ....}]", 0, 0, text);

        assertThat(range.getStart().getLine()).isEqualTo(3);
        assertThat(range.getStart().getCharacter()).isEqualTo(15);
        assertThat(range.getEnd().getLine()).isEqualTo(3);
    }

    @Test
    void firstRuleRangeCoversTheRuleHeader() {
        Range range = BuildMessageRanges.firstRuleRange("package p;\n\nrule \"A\"\n  when\n  then\nend\n");

        assertThat(range.getStart().getLine()).isEqualTo(2);
        assertThat(range.getStart().getCharacter()).isZero();
        assertThat(range.getEnd().getCharacter()).isEqualTo(8);
    }

    @Test
    void firstRuleRangeIgnoresTheWordRuleInAComment() {
        String text = "package p;\n/*\nrule of thumb\n*/\nrule \"A\"\n  when\n  then\nend\n";

        Range range = BuildMessageRanges.firstRuleRange(text);

        assertThat(range.getStart().getLine()).isEqualTo(4);
    }

    @Test
    void aTokenInACommentIsSkippedForTheCodeAfterIt() {
        String text = "package p;\n// totl is wrong here\nrule R\n  when\n    Order( totl > 1 )\n  then\nend\n";

        Range range = BuildMessageRanges.rangeFor("Field 'totl' is not on the type", 0, 0, text);

        assertThat(range.getStart().getLine()).isEqualTo(4);
        assertThat(range.getStart().getCharacter()).isEqualTo(11);
    }

    @Test
    void aParenthesisInsideAStringLiteralDoesNotEndThePattern() {
        String text = "package p;\nrule R\n  when\n    Order( note == \")\", totl > 1 )\n  then\nend\n";

        Range range = BuildMessageRanges.rangeFor("Field 'totl' is not on the type", 0, 0, text);

        assertThat(range.getStart().getLine()).isEqualTo(3);
        assertThat(range.getStart().getCharacter()).isEqualTo(24);
        assertThat(range.getEnd().getCharacter()).isEqualTo(32);
    }
}
