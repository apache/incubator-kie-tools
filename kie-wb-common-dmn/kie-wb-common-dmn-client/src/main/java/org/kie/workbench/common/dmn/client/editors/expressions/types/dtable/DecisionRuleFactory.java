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

import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;

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
        final Description d = new Description();
        d.setValue(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION);
        rule.setDescription(d);

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

        final Description d = new Description();
        d.setValue(source.getDescription().getValue());
        rule.setDescription(d);

        rule.setParent(dtable);

        return rule;
    }
}
