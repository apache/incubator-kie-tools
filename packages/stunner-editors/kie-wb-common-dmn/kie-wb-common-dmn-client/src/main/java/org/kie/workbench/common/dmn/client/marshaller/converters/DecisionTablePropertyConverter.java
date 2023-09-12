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
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionRule;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITHitPolicy;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputClause;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITOutputClause;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRuleAnnotationClause;

public class DecisionTablePropertyConverter {

    public static DecisionTable wbFromDMN(final JSITDecisionTable dmn) {
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());

        final DecisionTable result = new DecisionTable();
        result.setId(id);
        result.setDescription(description);
        result.setTypeRef(typeRef);

        final List<JSITRuleAnnotationClause> jsiRuleAnnotationClauses = dmn.getAnnotation();
        if (jsiRuleAnnotationClauses.isEmpty()) {
            final RuleAnnotationClause ruleAnnotationClause = new RuleAnnotationClause();
            ruleAnnotationClause.setParent(result);
            result.getAnnotations().add(ruleAnnotationClause);
        } else {
            for (int i = 0; i < jsiRuleAnnotationClauses.size(); i++) {
                final JSITRuleAnnotationClause ruleAnnotationClause = Js.uncheckedCast(jsiRuleAnnotationClauses.get(i));
                final RuleAnnotationClause converted = RuleAnnotationClausePropertyConverter.wbFromDMN(ruleAnnotationClause);
                if (Objects.nonNull(converted)) {
                    converted.setParent(result);
                    result.getAnnotations().add(converted);
                }
            }
        }

        final List<JSITInputClause> jsiInputClauses = dmn.getInput();
        for (int i = 0; i < jsiInputClauses.size(); i++) {
            final JSITInputClause input = Js.uncheckedCast(jsiInputClauses.get(i));
            final InputClause inputClauseConverted = InputClausePropertyConverter.wbFromDMN(input);
            if (Objects.nonNull(inputClauseConverted)) {
                inputClauseConverted.setParent(result);
                result.getInput().add(inputClauseConverted);
            }
        }

        final List<JSITOutputClause> jsiOutputClauses = dmn.getOutput();
        for (int i = 0; i < jsiOutputClauses.size(); i++) {
            final JSITOutputClause output = Js.uncheckedCast(jsiOutputClauses.get(i));
            final OutputClause outputClauseConverted = OutputClausePropertyConverter.wbFromDMN(output);
            if (Objects.nonNull(outputClauseConverted)) {
                outputClauseConverted.setParent(result);
                result.getOutput().add(outputClauseConverted);
            }
        }

        if (result.getOutput().size() == 1) {
            final OutputClause outputClause = result.getOutput().get(0);
            outputClause.setName(null); // DROOLS-3281
            outputClause.setTypeRef(BuiltInType.UNDEFINED.asQName()); // DROOLS-5178
        }

        final List<JSITDecisionRule> jsiDecisionRules = dmn.getRule();
        for (int i = 0; i < jsiDecisionRules.size(); i++) {
            final JSITDecisionRule dr = Js.uncheckedCast(jsiDecisionRules.get(i));
            final DecisionRule decisionRuleConverted = DecisionRulePropertyConverter.wbFromDMN(dr);
            if (Objects.nonNull(decisionRuleConverted)) {
                decisionRuleConverted.setParent(result);
            }
            result.getRule().add(decisionRuleConverted);
        }

        //JSITHitPolicy is a String JSO so convert into the real type
        final String hitPolicy = Js.uncheckedCast(dmn.getHitPolicy());
        if (Objects.nonNull(hitPolicy)) {
            result.setHitPolicy(HitPolicy.fromValue(hitPolicy));
        }

        //JSITBuiltinAggregator is a String JSO so convert into the real type
        final String aggregation = Js.uncheckedCast(dmn.getAggregation());
        if (Objects.nonNull(aggregation)) {
            result.setAggregation(BuiltinAggregator.fromValue(aggregation));
        }

        //JSITDecisionTableOrientation is a String JSO so convert into the real type
        final String orientation = Js.uncheckedCast(dmn.getPreferredOrientation());
        if (Objects.nonNull(orientation)) {
            result.setPreferredOrientation(DecisionTableOrientation.fromValue(orientation));
        }

        result.setOutputLabel(dmn.getOutputLabel());

        return result;
    }

    public static JSITDecisionTable dmnFromWB(final DecisionTable wb) {
        final JSITDecisionTable result = JSITDecisionTable.newInstance();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        for (final RuleAnnotationClause annotation : wb.getAnnotations()) {
            final JSITRuleAnnotationClause converted = RuleAnnotationClausePropertyConverter.dmnFromWB(annotation);
            result.addAnnotation(converted);
        }
        for (InputClause input : wb.getInput()) {
            final JSITInputClause c = InputClausePropertyConverter.dmnFromWB(input);
            result.addInput(c);
        }
        for (OutputClause input : wb.getOutput()) {
            final JSITOutputClause c = OutputClausePropertyConverter.dmnFromWB(input);
            result.addOutput(c);
        }
        if (result.getOutput().size() == 1) {
            final JSITOutputClause at = Js.uncheckedCast(result.getOutput().get(0));
            at.setName(null); // DROOLS-3281
            at.setTypeRef(null); // DROOLS-5178
        }
        for (DecisionRule dr : wb.getRule()) {
            final JSITDecisionRule c = DecisionRulePropertyConverter.dmnFromWB(dr);
            result.addRule(c);
        }
        if (Objects.nonNull(wb.getHitPolicy())) {
            switch (wb.getHitPolicy()) {
                case ANY:
                    result.setHitPolicy(Js.uncheckedCast(JSITHitPolicy.ANY.value()));
                    break;
                case COLLECT:
                    result.setHitPolicy(Js.uncheckedCast(JSITHitPolicy.COLLECT.value()));
                    break;
                case FIRST:
                    result.setHitPolicy(Js.uncheckedCast(JSITHitPolicy.FIRST.value()));
                    break;
                case UNIQUE:
                    result.setHitPolicy(Js.uncheckedCast(JSITHitPolicy.UNIQUE.value()));
                    break;
                case PRIORITY:
                    result.setHitPolicy(Js.uncheckedCast(JSITHitPolicy.PRIORITY.value()));
                    break;
                case RULE_ORDER:
                    result.setHitPolicy(Js.uncheckedCast(JSITHitPolicy.RULE_ORDER.value()));
                    break;
                case OUTPUT_ORDER:
                    result.setHitPolicy(Js.uncheckedCast(JSITHitPolicy.OUTPUT_ORDER.value()));
                    break;
            }
        }
        if (Objects.nonNull(wb.getAggregation())) {
            final String wbBuiltinAggregator = wb.getAggregation().value();
            result.setAggregation(Js.uncheckedCast(wbBuiltinAggregator));
        }
        if (Objects.nonNull(wb.getPreferredOrientation())) {
            final String wbPreferredOrientation = wb.getPreferredOrientation().value();
            result.setPreferredOrientation(Js.uncheckedCast(wbPreferredOrientation));
        }

        result.setOutputLabel(wb.getOutputLabel());

        return result;
    }
}
