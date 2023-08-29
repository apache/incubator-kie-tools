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

import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;

public class DecisionRuleFactory {

    public static DecisionRule makeDecisionRule(final DecisionTable dtable) {
        final DecisionRule rule = new DecisionRule();
        for (int ie = 0; ie < dtable.getInput().size(); ie++) {
            final UnaryTests ut = new UnaryTests();
            ut.getText().setValue(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
            rule.getInputEntry().add(ut);
            ut.setParent(rule);
        }
        for (int oe = 0; oe < dtable.getOutput().size(); oe++) {
            final LiteralExpression le = new LiteralExpression();
            le.getText().setValue(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
            rule.getOutputEntry().add(le);
            le.setParent(rule);
        }
        for (int index = 0; index < dtable.getAnnotations().size(); index++) {
            final RuleAnnotationClauseText ruleAnnotationClauseText = new RuleAnnotationClauseText();
            ruleAnnotationClauseText.getText().setValue(DecisionTableDefaultValueUtilities.RULE_ANNOTATION_CLAUSE_EXPRESSION_TEXT);
            rule.getAnnotationEntry().add(ruleAnnotationClauseText);
            ruleAnnotationClauseText.setParent(rule);
        }

        rule.setParent(dtable);

        return rule;
    }

    public static DecisionRule duplicateDecisionRule(final int index,
                                                     final DecisionTable dtable) {
        final DecisionRule rule = new DecisionRule();
        final DecisionRule source = dtable.getRule().get(index);

        for (UnaryTests ie : source.getInputEntry()) {
            final UnaryTests ut = new UnaryTests();
            ut.getText().setValue(ie.getText().getValue());
            ut.setConstraintType(ie.getConstraintType());
            rule.getInputEntry().add(ut);
            ut.setParent(rule);
        }

        for (LiteralExpression oe : source.getOutputEntry()) {
            final LiteralExpression le = new LiteralExpression();
            le.getText().setValue(oe.getText().getValue());
            rule.getOutputEntry().add(le);
            le.setParent(rule);
        }

        for (final RuleAnnotationClauseText text : source.getAnnotationEntry()) {
            final RuleAnnotationClauseText copy = new RuleAnnotationClauseText();
            copy.getText().setValue(text.getText().getValue());
            copy.setParent(rule);
            rule.getAnnotationEntry().add(copy);
        }
        rule.setParent(dtable);

        return rule;
    }
}
