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

import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;

public class DecisionRulePropertyConverter {

    public static DecisionRule wbFromDMN(final org.kie.dmn.model.api.DecisionRule dmn) {
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());

        DecisionRule result = new DecisionRule();
        result.setId(id);
        result.setDescription(description);

        for (org.kie.dmn.model.api.UnaryTests ie : dmn.getInputEntry()) {
            UnaryTests inputEntryConverted = UnaryTestsPropertyConverter.wbFromDMN(ie);
            if (inputEntryConverted != null) {
                inputEntryConverted.setParent(inputEntryConverted);
            }
            result.getInputEntry().add(inputEntryConverted);
        }
        for (org.kie.dmn.model.api.LiteralExpression oe : dmn.getOutputEntry()) {
            LiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.wbFromDMN(oe);
            if (outputEntryConverted != null) {
                outputEntryConverted.setParent(result);
            }
            result.getOutputEntry().add(outputEntryConverted);
        }

        return result;
    }

    public static org.kie.dmn.model.api.DecisionRule dmnFromWB(final DecisionRule wb) {
        org.kie.dmn.model.api.DecisionRule result = new org.kie.dmn.model.v1_2.TDecisionRule();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));

        for (UnaryTests ie : wb.getInputEntry()) {
            org.kie.dmn.model.api.UnaryTests inputEntryConverted = UnaryTestsPropertyConverter.dmnFromWB(ie);
            if (inputEntryConverted != null) {
                inputEntryConverted.setParent(inputEntryConverted);
            }
            result.getInputEntry().add(inputEntryConverted);
        }
        for (LiteralExpression oe : wb.getOutputEntry()) {
            org.kie.dmn.model.api.LiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.dmnFromWB(oe);
            if (outputEntryConverted != null) {
                outputEntryConverted.setParent(result);
            }
            result.getOutputEntry().add(outputEntryConverted);
        }

        return result;
    }
}