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

import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;

public class InputClausePropertyConverter {

    public static InputClause wbFromDMN(final org.kie.dmn.model.api.InputClause dmn) {
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        LiteralExpression inputExpression = LiteralExpressionPropertyConverter.wbFromDMN(dmn.getInputExpression());
            UnaryTests inputValues = UnaryTestsPropertyConverter.wbFromDMN(dmn.getInputValues());
        
        InputClause result = new InputClause(id, description, inputExpression, inputValues);

        return result;
    }

    public static org.kie.dmn.model.api.InputClause dmnFromWB(final InputClause wb) {
        org.kie.dmn.model.api.InputClause result = new org.kie.dmn.model.v1_1.TInputClause();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.setInputExpression(LiteralExpressionPropertyConverter.dmnFromWB(wb.getInputExpression()));
        result.setInputValues(UnaryTestsPropertyConverter.dmnFromWB(wb.getInputValues()));

        return result;
    }
}