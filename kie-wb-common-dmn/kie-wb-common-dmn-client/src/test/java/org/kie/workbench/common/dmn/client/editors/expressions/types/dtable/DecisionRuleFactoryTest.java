/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;

import static org.assertj.core.api.Assertions.assertThat;

public class DecisionRuleFactoryTest {

    private static final String INPUT_CLAUSE_TEXT_1 = "input1";

    private static final String INPUT_CLAUSE_TEXT_2 = "input2";

    private static final String OUTPUT_CLAUSE_TEXT_1 = "output1";

    private static final String OUTPUT_CLAUSE_TEXT_2 = "output2";

    private static final String DESCRIPTION_TEXT = "description";

    private DecisionTable dtable;

    @Before
    public void setup() {
        dtable = new DecisionTable();
        dtable.getInput().add(new InputClause());
        dtable.getInput().add(new InputClause());
        dtable.getOutput().add(new OutputClause());
        dtable.getOutput().add(new OutputClause());
    }

    @Test
    public void testMakeDecisionRule() {
        final DecisionRule rule = DecisionRuleFactory.makeDecisionRule(dtable);

        final List<UnaryTests> inputEntries = rule.getInputEntry();
        assertThat(inputEntries.size()).isEqualTo(2);
        assertThat(inputEntries)
                .allSatisfy(unaryTests -> assertUnaryTestsText(unaryTests, DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT))
                .allSatisfy(unaryTests -> assertThat(unaryTests.getConstraintType()).isNull())
                .allSatisfy(unaryTests -> assertThat(unaryTests.getParent()).isEqualTo(rule));

        final List<LiteralExpression> outputEntries = rule.getOutputEntry();
        assertThat(outputEntries.size()).isEqualTo(2);
        assertThat(outputEntries)
                .allSatisfy(literalExpression -> assertLiteralExpressionText(literalExpression, DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT))
                .allSatisfy(literalExpression -> assertThat(literalExpression.getParent()).isEqualTo(rule));

        assertThat(rule.getDescription().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION);

        assertThat(rule.getParent()).isEqualTo(dtable);
    }

    @Test
    public void testDuplicateDecisionRule() {
        final DecisionRule rule = DecisionRuleFactory.makeDecisionRule(dtable);

        final List<UnaryTests> inputEntries = rule.getInputEntry();
        assertThat(inputEntries.size()).isEqualTo(2);
        inputEntries.get(0).getText().setValue(INPUT_CLAUSE_TEXT_1);
        inputEntries.get(1).getText().setValue(INPUT_CLAUSE_TEXT_2);
        inputEntries.get(0).setConstraintType(ConstraintType.ENUMERATION);
        inputEntries.get(1).setConstraintType(ConstraintType.RANGE);

        final List<LiteralExpression> outputEntries = rule.getOutputEntry();
        assertThat(outputEntries.size()).isEqualTo(2);
        outputEntries.get(0).getText().setValue(OUTPUT_CLAUSE_TEXT_1);
        outputEntries.get(1).getText().setValue(OUTPUT_CLAUSE_TEXT_2);

        rule.getDescription().setValue(DESCRIPTION_TEXT);

        dtable.getRule().add(rule);

        final DecisionRule duplicate = DecisionRuleFactory.duplicateDecisionRule(0, dtable);

        final List<UnaryTests> duplicateInputEntries = duplicate.getInputEntry();
        assertThat(duplicateInputEntries.size()).isEqualTo(2);
        assertUnaryTestsText(duplicateInputEntries.get(0), INPUT_CLAUSE_TEXT_1);
        assertUnaryTestsText(duplicateInputEntries.get(1), INPUT_CLAUSE_TEXT_2);
        assertThat(duplicateInputEntries.get(0).getConstraintType()).isEqualTo(ConstraintType.ENUMERATION);
        assertThat(duplicateInputEntries.get(1).getConstraintType()).isEqualTo(ConstraintType.RANGE);
        assertUnaryTestsInstancesAreNotTheSame(inputEntries.get(0), duplicateInputEntries.get(0));
        assertUnaryTestsInstancesAreNotTheSame(inputEntries.get(1), duplicateInputEntries.get(1));
        assertThat(duplicateInputEntries).allSatisfy(unaryTests -> assertThat(unaryTests.getParent()).isEqualTo(duplicate));

        final List<LiteralExpression> duplicateOutputEntries = duplicate.getOutputEntry();
        assertThat(duplicateOutputEntries.size()).isEqualTo(2);
        assertLiteralExpressionText(duplicateOutputEntries.get(0), OUTPUT_CLAUSE_TEXT_1);
        assertLiteralExpressionText(duplicateOutputEntries.get(1), OUTPUT_CLAUSE_TEXT_2);
        assertLiteralExpressionInstancesAreNotTheSame(outputEntries.get(0), duplicateOutputEntries.get(0));
        assertLiteralExpressionInstancesAreNotTheSame(outputEntries.get(1), duplicateOutputEntries.get(1));
        assertThat(duplicateOutputEntries).allSatisfy(literalExpression -> assertThat(literalExpression.getParent()).isEqualTo(duplicate));

        assertThat(duplicate.getDescription().getValue()).isEqualTo(DESCRIPTION_TEXT);

        assertThat(duplicate.getParent()).isEqualTo(dtable);
    }

    private void assertUnaryTestsText(final UnaryTests unaryTests,
                                      final String expectedText) {
        assertThat(unaryTests.getText().getValue()).isEqualTo(expectedText);
    }

    private void assertUnaryTestsInstancesAreNotTheSame(final UnaryTests unaryTests1,
                                                        final UnaryTests unaryTests2) {
        assertThat(unaryTests1).isNotSameAs(unaryTests2);
        assertThat(unaryTests1.getText()).isNotSameAs(unaryTests2.getText());
    }

    private void assertLiteralExpressionText(final LiteralExpression literalExpression,
                                             final String expectedText) {
        assertThat(literalExpression.getText().getValue()).isEqualTo(expectedText);
    }

    private void assertLiteralExpressionInstancesAreNotTheSame(final LiteralExpression literalExpression1,
                                                               final LiteralExpression literalExpression2) {
        assertThat(literalExpression1).isNotSameAs(literalExpression2);
        assertThat(literalExpression1.getText()).isNotSameAs(literalExpression2.getText());
    }
}
