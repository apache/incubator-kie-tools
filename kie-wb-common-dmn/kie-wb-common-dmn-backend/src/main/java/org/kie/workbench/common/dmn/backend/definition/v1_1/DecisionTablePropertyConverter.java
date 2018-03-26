/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class DecisionTablePropertyConverter {

    public static DecisionTable wbFromDMN(final org.kie.dmn.model.v1_1.DecisionTable dmn) {
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());

        DecisionTable result = new DecisionTable();
        result.setId(id);
        result.setDescription(description);
        result.setTypeRef(typeRef);

        for (org.kie.dmn.model.v1_1.InputClause input : dmn.getInput()) {
            result.getInput().add(InputClausePropertyConverter.wbFromDMN(input));
        }
        for (org.kie.dmn.model.v1_1.OutputClause input : dmn.getOutput()) {
            result.getOutput().add(OutputClausePropertyConverter.wbFromDMN(input));
        }
        for (org.kie.dmn.model.v1_1.DecisionRule dr : dmn.getRule()) {
            result.getRule().add(DecisionRulePropertyConverter.wbFromDMN(dr));
        }
        if (dmn.getHitPolicy() != null) {
            result.setHitPolicy(HitPolicy.fromValue(dmn.getHitPolicy().value()));
        }
        if (dmn.getAggregation() != null) {
            result.setAggregation(BuiltinAggregator.fromValue(dmn.getAggregation().value()));
        }
        if (dmn.getPreferredOrientation() != null) {
            result.setPreferredOrientation(DecisionTableOrientation.fromValue(dmn.getPreferredOrientation().value()));
        }

        result.setOutputLabel(dmn.getOutputLabel());

        return result;
    }

    public static org.kie.dmn.model.v1_1.DecisionTable dmnFromWB(final DecisionTable wb) {
        org.kie.dmn.model.v1_1.DecisionTable result = new org.kie.dmn.model.v1_1.DecisionTable();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        for (InputClause input : wb.getInput()) {
            result.getInput().add(InputClausePropertyConverter.dmnFromWB(input));
        }
        for (OutputClause input : wb.getOutput()) {
            result.getOutput().add(OutputClausePropertyConverter.dmnFromWB(input));
        }
        for (DecisionRule dr : wb.getRule()) {
            result.getRule().add(DecisionRulePropertyConverter.dmnFromWB(dr));
        }
        if (wb.getHitPolicy() != null) {
            result.setHitPolicy(org.kie.dmn.model.v1_1.HitPolicy.fromValue(wb.getHitPolicy().value()));
        }
        if (wb.getAggregation() != null) {
            result.setAggregation(org.kie.dmn.model.v1_1.BuiltinAggregator.fromValue(wb.getAggregation().value()));
        }
        if (wb.getPreferredOrientation() != null) {
            result.setPreferredOrientation(org.kie.dmn.model.v1_1.DecisionTableOrientation.fromValue(wb.getPreferredOrientation().value()));
        }

        result.setOutputLabel(wb.getOutputLabel());

        return result;
    }
}