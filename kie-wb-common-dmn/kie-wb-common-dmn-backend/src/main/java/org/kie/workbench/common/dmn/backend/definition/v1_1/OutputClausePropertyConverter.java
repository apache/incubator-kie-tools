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

import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class OutputClausePropertyConverter {

    public static OutputClause wbFromDMN(final org.kie.dmn.model.api.OutputClause dmn) {
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final OutputClauseUnaryTests outputValues = OutputClauseUnaryTestsPropertyConverter.wbFromDMN(dmn.getOutputValues());
        final OutputClauseLiteralExpression defaultOutputEntry = OutputClauseLiteralExpressionPropertyConverter.wbFromDMN(dmn.getDefaultOutputEntry());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        final OutputClause result = new OutputClause();
        result.setId(id);
        result.setName(dmn.getName());
        result.setDescription(description);
        result.setOutputValues(outputValues);
        result.setDefaultOutputEntry(defaultOutputEntry);
        result.setTypeRef(typeRef);

        if (outputValues != null) {
            outputValues.setParent(result);
        }
        if (defaultOutputEntry != null) {
            defaultOutputEntry.setParent(result);
        }

        return result;
    }

    public static org.kie.dmn.model.api.OutputClause dmnFromWB(final OutputClause wb) {
        final org.kie.dmn.model.api.OutputClause result = new org.kie.dmn.model.v1_2.TOutputClause();
        result.setId(wb.getId().getValue());
        result.setName(wb.getName());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));

        final UnaryTests outputValues = UnaryTestsPropertyConverter.dmnFromWB(wb.getOutputValues());
        if (outputValues != null && StringUtils.nonEmpty(outputValues.getText())) {
            outputValues.setParent(result);
            result.setOutputValues(outputValues);
        }

        final LiteralExpression defaultOutputEntry = LiteralExpressionPropertyConverter.dmnFromWB(wb.getDefaultOutputEntry());
        if (defaultOutputEntry != null && StringUtils.nonEmpty(defaultOutputEntry.getText())) {
            defaultOutputEntry.setParent(result);
            result.setDefaultOutputEntry(defaultOutputEntry);
        }

        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        return result;
    }
}