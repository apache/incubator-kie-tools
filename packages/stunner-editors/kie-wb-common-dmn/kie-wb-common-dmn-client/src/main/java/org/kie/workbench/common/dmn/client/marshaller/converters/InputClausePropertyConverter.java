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

import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputClause;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class InputClausePropertyConverter {

    public static InputClause wbFromDMN(final JSITInputClause dmn) {
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final InputClauseLiteralExpression inputExpression = InputClauseLiteralExpressionPropertyConverter.wbFromDMN(dmn.getInputExpression());
        final InputClauseUnaryTests inputValues = Optional
                .ofNullable(InputClauseUnaryTestsPropertyConverter.wbFromDMN(dmn.getInputValues()))
                .orElse(new InputClauseUnaryTests());

        final InputClause result = new InputClause(id,
                                                   description,
                                                   inputExpression,
                                                   inputValues);

        if (Objects.nonNull(inputExpression)) {
            inputExpression.setParent(result);
        }
        inputValues.setParent(result);

        return result;
    }

    public static JSITInputClause dmnFromWB(final InputClause wb) {
        final JSITInputClause result = JSITInputClause.newInstance();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        final JSITLiteralExpression expression = LiteralExpressionPropertyConverter.dmnFromWB(wb.getInputExpression());
        final JSITUnaryTests inputValues = UnaryTestsPropertyConverter.dmnFromWB(wb.getInputValues());

        result.setInputExpression(expression);

        if (Objects.nonNull(inputValues) && StringUtils.nonEmpty(inputValues.getText())) {
            result.setInputValues(inputValues);
        }

        return result;
    }
}
