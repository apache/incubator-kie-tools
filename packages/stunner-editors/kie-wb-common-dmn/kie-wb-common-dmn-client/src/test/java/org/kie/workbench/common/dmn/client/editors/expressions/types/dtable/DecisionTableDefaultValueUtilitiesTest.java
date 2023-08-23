/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;

import static org.assertj.core.api.Assertions.assertThat;

public class DecisionTableDefaultValueUtilitiesTest {

    private DecisionTable dtable;

    @Before
    public void setup() {
        this.dtable = new DecisionTable();
    }

    @Test
    public void testGetNewInputClauseName() {
        final InputClause inputClause1 = new InputClause() {{
            setInputExpression(new InputClauseLiteralExpression());
        }};
        dtable.getInput().add(inputClause1);
        inputClause1.getInputExpression().getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
        assertThat(inputClause1.getInputExpression().getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "1");

        final InputClause inputClause2 = new InputClause() {{
            setInputExpression(new InputClauseLiteralExpression());
        }};
        dtable.getInput().add(inputClause2);
        inputClause2.getInputExpression().getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
        assertThat(inputClause2.getInputExpression().getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "2");
    }

    @Test
    public void testGetNewInputClauseNameWithExistingInputClauses() {
        final InputClause inputClause1 = new InputClause() {{
            setInputExpression(new InputClauseLiteralExpression());
        }};
        dtable.getInput().add(inputClause1);
        inputClause1.getInputExpression().getText().setValue("input");

        final InputClause inputClause2 = new InputClause() {{
            setInputExpression(new InputClauseLiteralExpression());
        }};
        dtable.getInput().add(inputClause2);
        inputClause2.getInputExpression().getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
        assertThat(inputClause2.getInputExpression().getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "1");
    }

    @Test
    public void testGetNewInputClauseNameWithDeletion() {
        final InputClause inputClause1 = new InputClause() {{
            setInputExpression(new InputClauseLiteralExpression());
        }};
        dtable.getInput().add(inputClause1);
        inputClause1.getInputExpression().getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
        assertThat(inputClause1.getInputExpression().getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "1");

        final InputClause inputClause2 = new InputClause() {{
            setInputExpression(new InputClauseLiteralExpression());
        }};
        dtable.getInput().add(inputClause2);
        inputClause2.getInputExpression().getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
        assertThat(inputClause2.getInputExpression().getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "2");

        dtable.getInput().remove(inputClause1);

        final InputClause inputClause3 = new InputClause() {{
            setInputExpression(new InputClauseLiteralExpression());
        }};
        dtable.getInput().add(inputClause3);
        inputClause3.getInputExpression().getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
        assertThat(inputClause3.getInputExpression().getText().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "3");
    }

    @Test
    public void testGetNewOutputClauseName() {
        final OutputClause outputClause1 = new OutputClause();
        dtable.getOutput().add(outputClause1);
        outputClause1.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
        assertThat(outputClause1.getName()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "1");

        final OutputClause outputClause2 = new OutputClause();
        dtable.getOutput().add(outputClause2);
        outputClause2.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
        assertThat(outputClause2.getName()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "2");
    }

    @Test
    public void testGetNewOutputClauseNameWithExistingOutputClauses() {
        final OutputClause outputClause1 = new OutputClause();
        dtable.getOutput().add(outputClause1);
        outputClause1.setName("output");

        final OutputClause outputClause2 = new OutputClause();
        dtable.getOutput().add(outputClause2);
        outputClause2.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
        assertThat(outputClause2.getName()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "1");
    }

    @Test
    public void testGetNewOutputClauseNameWithDeletion() {
        final OutputClause outputClause1 = new OutputClause();
        dtable.getOutput().add(outputClause1);
        outputClause1.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
        assertThat(outputClause1.getName()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "1");

        final OutputClause outputClause2 = new OutputClause();
        dtable.getOutput().add(outputClause2);
        outputClause2.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
        assertThat(outputClause2.getName()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "2");

        dtable.getOutput().remove(outputClause1);

        final OutputClause outputClause3 = new OutputClause();
        dtable.getOutput().add(outputClause3);
        outputClause3.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
        assertThat(outputClause3.getName()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "3");
    }

    @Test
    public void testGetNewRuleAnnotationClauseName() {
        final RuleAnnotationClause ruleAnnotationClause1 = new RuleAnnotationClause();
        dtable.getAnnotations().add(ruleAnnotationClause1);
        ruleAnnotationClause1.getName().setValue(DecisionTableDefaultValueUtilities.getNewRuleAnnotationClauseName(dtable));
        assertThat(ruleAnnotationClause1.getName().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.RULE_ANNOTATION_CLAUSE_PREFIX + "1");

        final RuleAnnotationClause ruleAnnotationClause2 = new RuleAnnotationClause();
        dtable.getAnnotations().add(ruleAnnotationClause2);
        ruleAnnotationClause2.getName().setValue(DecisionTableDefaultValueUtilities.getNewRuleAnnotationClauseName(dtable));
        assertThat(ruleAnnotationClause2.getName().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.RULE_ANNOTATION_CLAUSE_PREFIX + "2");
    }

    @Test
    public void testGetNewRuleAnnotationClauseNameWithExistingAnnotationClause() {
        final RuleAnnotationClause ruleAnnotationClause1 = new RuleAnnotationClause();
        dtable.getAnnotations().add(ruleAnnotationClause1);
        ruleAnnotationClause1.getName().setValue("something");

        final RuleAnnotationClause ruleAnnotationClause2 = new RuleAnnotationClause();
        dtable.getAnnotations().add(ruleAnnotationClause2);
        ruleAnnotationClause2.getName().setValue(DecisionTableDefaultValueUtilities.getNewRuleAnnotationClauseName(dtable));

        assertThat(ruleAnnotationClause2.getName().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.RULE_ANNOTATION_CLAUSE_PREFIX + "1");
    }
}
