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
 * Every refusal that can point at a line does. An input the parser cannot
 * read names the first error's line. A defect the formatter itself introduced
 * shows only in the output, which the user never sees, so it is mapped back by
 * rule name to the input line of the rule it sits in: that is the rule to fence
 * off or to send with a report.
 */
class FormatterRefusalLineTest {

    /** Without an annotation to absorb it, a bad constraint is a plain parse error. */
    private static final String PLAIN = "package p;\n\n"
            + "rule \"first\"\n  when\n    Parcel( weight > 1 )\n  then\n    ship();\nend\n\n"
            + "rule \"second\"\n  when\n    Parcel( weight > 1 )\n  then\n    hold();\nend\n";

    /** With one, the parser reads the rest of the rule as the annotation's payload instead. */
    private static final String ANNOTATED = "package p;\n\n"
            + "rule \"first\"\n  @Tier(1)\n  when\n    Parcel( weight > 1 )\n  then\n    ship();\nend\n\n"
            + "rule \"second\"\n  @Tier(1)\n  when\n    Parcel( weight > 1 )\n  then\n    hold();\nend\n";

    private static String withSecondCondition(String rules, String condition) {
        return rules.replace("Parcel( weight > 1 )\n  then\n    hold();", condition + "\n  then\n    hold();");
    }

    @Test
    void aParseErrorRefusalNamesTheFirstErrorsLine() {
        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(withSecondCondition(PLAIN, "Parcel( weight > )"));

        assertThat(r.syntaxErrors()).isPositive();
        assertThat(r.syntaxErrorLine()).isEqualTo(12);
        assertThat(r.refusalReason()).contains("parse error").contains("line 12");
    }

    @Test
    void anOutputDefectIsMappedToTheInputLineOfItsRule() {
        String swallowed = withSecondCondition(ANNOTATED, "Parcel( weight > 1 && <= 5 )");
        String unparseable = withSecondCondition(PLAIN, "Parcel( weight > )");

        assertThat(DRLFormatter.outputDefectLine(ANNOTATED, swallowed)).as("swallowed when-block").isEqualTo(11);
        assertThat(DRLFormatter.outputDefectLine(PLAIN, unparseable)).as("parse error").isEqualTo(10);
        assertThat(DRLFormatter.outputDefectLine(PLAIN, PLAIN)).isZero();
    }

    @Test
    void theRefusalNamesTheMangledRule() {
        DRLFormatter.FormatResult lostWhen = new DRLFormatter.FormatResult("", 0, 0, 0, 0, 0, 1, 0, 0, 0, 11, 0, false, 0, 0);
        DRLFormatter.FormatResult unparseable = new DRLFormatter.FormatResult("", 0, 0, 3, 0, 0, 0, 0, 0, 0, 11, 0, false, 0, 0);

        assertThat(lostWhen.refusalReason()).contains("when-block").contains("line 11").contains("formatter bug");
        assertThat(unparseable.refusalReason()).contains("re-parse").contains("line 11").contains("formatter bug");
    }
}
