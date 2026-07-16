/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.drl.parser.antlr4.DRLParserHelper.computeTokenIndex;
import static org.drools.drl.parser.antlr4.DRLParserHelper.createDrlParser;

/**
 * This class is a quick test to check if the antlr4 parser is working.
 * The real tests are done in the Drools project.
 */
class DRLParserHelperTest {

    private static final String BASIC_DRL = """
            package org.test;
            import org.test.model.Person;
            global String result;
            rule TestRule
                no-loop
                salience 15
              when
                $p:Person( age >= 18 )
              then
                int a = 4;
                System.out.println($p.getName());
            end
            """;

    @Test
    void computeTokenIndex_basicRule() {
        DRL10Parser parser = createDrlParser(BASIC_DRL);
        parser.compilationUnit();

        // computeTokenIndex returns the token index of the token at the given line and column.
        // When column is right after the token, it returns the index of the previous token so that editor can suggest the token to finalize the completion
        // TODO: We may revisit the logic of computeTokenIndex and the test cases considering the requriments from the editor.

        assertThat(computeTokenIndex(parser, 1, 0)).isEqualTo(0); // beginning of 'package'
        assertThat(computeTokenIndex(parser, 1, 6)).isEqualTo(0); // end of 'package'
        assertThat(computeTokenIndex(parser, 1, 7)).isEqualTo(0); // right after 'package'
        assertThat(computeTokenIndex(parser, 1, 8)).isEqualTo(1); // right after ' '
        assertThat(computeTokenIndex(parser, 1, 9)).isEqualTo(2); // 'org'
        assertThat(computeTokenIndex(parser, 1, 12)).isEqualTo(3); // right after '.'
        assertThat(computeTokenIndex(parser, 1, 13)).isEqualTo(4); // 'test'
        assertThat(computeTokenIndex(parser, 1, 17)).isEqualTo(5); // right after ';'
        assertThat(computeTokenIndex(parser, 1, 18)).isEqualTo(6); // right after '\n'
        assertThat(computeTokenIndex(parser, 2, 0)).isEqualTo(6); // beginning of 'import'. Returns the index of the previous token
        assertThat(computeTokenIndex(parser, 2, 1)).isEqualTo(7); // 'import'
        assertThat(computeTokenIndex(parser, 2, 6)).isEqualTo(7); // right after 'import'
        assertThat(computeTokenIndex(parser, 2, 7)).isEqualTo(8); // right after ' '
    }

    @Test
    void getFirstRuleName() {

        String multipleRules = """
            package org.test;
            import org.test.model.Person;
            global String result;
            rule R1
              when
                $p:Person( age >= 18 )
              then
                int a = 4;
                System.out.println($p.getName());
            end
            
            rule R2
              when
                Person( age < 18 )
              then
            end
            """;

        String ruleName = DRLParserHelper.getFirstRuleName(multipleRules);
        assertThat(ruleName).isEqualTo("R1");
    }

    @Test
    void getFirstRuleNameWithNoRule() {

        String noRule = """
            package org.test;
            """;

        String ruleName = DRLParserHelper.getFirstRuleName(noRule);
        assertThat(ruleName).isEqualTo("");
    }
}
