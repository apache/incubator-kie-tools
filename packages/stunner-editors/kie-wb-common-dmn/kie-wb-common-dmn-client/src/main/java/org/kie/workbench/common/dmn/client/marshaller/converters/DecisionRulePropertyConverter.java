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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionRule;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRuleAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;

public class DecisionRulePropertyConverter {

    public static DecisionRule wbFromDMN(final JSITDecisionRule dmn) {
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());

        final DecisionRule result = new DecisionRule();
        result.setId(id);
        result.setDescription(description);

        final List<JSITRuleAnnotation> jsiAnnotationEntries = dmn.getAnnotationEntry();
        for (int i = 0; i < jsiAnnotationEntries.size(); i++) {
            final JSITRuleAnnotation jsiRuleAnnotation = Js.uncheckedCast(jsiAnnotationEntries.get(i));
            final RuleAnnotationClauseText ruleAnnotationClauseText = RuleAnnotationClauseTextPropertyConverter.wbFromDMN(jsiRuleAnnotation);
            if (Objects.nonNull(ruleAnnotationClauseText)) {
                ruleAnnotationClauseText.setParent(result);
                result.getAnnotationEntry().add(ruleAnnotationClauseText);
            }
        }

        if (result.getAnnotationEntry().isEmpty()) {
            final RuleAnnotationClauseText annotationEntryText = new RuleAnnotationClauseText();
            annotationEntryText.getText().setValue(description.getValue());
            annotationEntryText.setParent(result);
            result.getAnnotationEntry().add(annotationEntryText);
        }

        final List<JSITUnaryTests> jsiInputEntries = dmn.getInputEntry();
        for (int i = 0; i < jsiInputEntries.size(); i++) {
            final JSITUnaryTests jsiInputEntry = Js.uncheckedCast(jsiInputEntries.get(i));
            final UnaryTests inputEntryConverted = UnaryTestsPropertyConverter.wbFromDMN(jsiInputEntry);
            if (Objects.nonNull(inputEntryConverted)) {
                inputEntryConverted.setParent(result);
                result.getInputEntry().add(inputEntryConverted);
            }
        }

        final List<JSITLiteralExpression> jsiOutputEntries = dmn.getOutputEntry();
        for (int i = 0; i < jsiOutputEntries.size(); i++) {
            final JSITLiteralExpression jsiOutputEntry = Js.uncheckedCast(jsiOutputEntries.get(i));
            final LiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.wbFromDMN(jsiOutputEntry);
            if (Objects.nonNull(outputEntryConverted)) {
                outputEntryConverted.setParent(result);
                result.getOutputEntry().add(outputEntryConverted);
            }
        }

        return result;
    }

    public static JSITDecisionRule dmnFromWB(final DecisionRule wb) {
        final JSITDecisionRule result = JSITDecisionRule.newInstance();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);

        for (final RuleAnnotationClauseText ruleAnnotationClauseText : wb.getAnnotationEntry()) {
            final JSITRuleAnnotation ruleAnnotation = RuleAnnotationClauseTextPropertyConverter.dmnFromWB(ruleAnnotationClauseText);
            result.addAnnotationEntry(ruleAnnotation);
        }
        for (UnaryTests ie : wb.getInputEntry()) {
            final JSITUnaryTests inputEntryConverted = UnaryTestsPropertyConverter.dmnFromWB(ie);
            result.addInputEntry(inputEntryConverted);
        }
        for (LiteralExpression oe : wb.getOutputEntry()) {
            final JSITLiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.dmnFromWB(oe);
            result.addOutputEntry(outputEntryConverted);
        }

        return result;
    }
}
