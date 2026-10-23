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

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LhsBindingResolverTest {

    private static final String DECLARE = """
            package demo;

            global java.util.List results;

            declare Fact
              code : String
            end

            """;

    /**
     * The bindings visible from the consequence, which is where a rule's author
     * uses them.
     */
    private static Map<String, String> bindingsFromConsequence(String lhs) {
        String drl = DECLARE + "rule R\n  when\n" + lhs
                + "  then\n    results.add(\"x\");\nend\n";
        return LhsBindingResolver.resolveAt(drl, drl.indexOf("results.add"),
                DRLWorkspaceTypeIndex.build(drl, null, Map.of()));
    }

    @Test
    void resolvesEveryBindingInTheCondition() {
        assertThat(bindingsFromConsequence("    Fact( $first : code )\n"
                + "    Fact( $second : code )\n"))
                .containsEntry("first", "String")
                .containsEntry("second", "String");
    }

    @Test
    void aLineCommentSayingThenDoesNotHideTheBindingsAfterIt() {
        assertThat(bindingsFromConsequence("    Fact( $first : code )\n"
                + "    // match this and then the other\n"
                + "    Fact( $second : code )\n"))
                .containsEntry("first", "String")
                .containsEntry("second", "String");
    }

    @Test
    void aBlockCommentSayingThenDoesNotHideTheBindingsAfterIt() {
        assertThat(bindingsFromConsequence("    Fact( $first : code )\n"
                + "    /* match this and then\n       the other */\n"
                + "    Fact( $second : code )\n"))
                .containsEntry("first", "String")
                .containsEntry("second", "String");
    }

    @Test
    void aStringLiteralSayingThenLeavesItsOwnPatternIntact() {
        // The literal sits mid-pattern, so truncating there also unbalances the
        // parentheses and loses the binding declared before it.
        assertThat(bindingsFromConsequence("    Fact( $first : code, code == \"then\" )\n"
                + "    Fact( $second : code )\n"))
                .containsEntry("first", "String")
                .containsEntry("second", "String");
    }

    @Test
    void aStringLiteralSayingEndDoesNotTruncateTheRule() {
        assertThat(bindingsFromConsequence("    Fact( $first : code, code == \"end\" )\n"
                + "    Fact( $second : code )\n"))
                .containsEntry("first", "String")
                .containsEntry("second", "String");
    }
}
