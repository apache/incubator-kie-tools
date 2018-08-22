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

import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class OutputClausePropertyConverter {

    public static OutputClause wbFromDMN(final org.kie.dmn.model.api.OutputClause dmn) {
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        UnaryTests outputValues = UnaryTestsPropertyConverter.wbFromDMN(dmn.getOutputValues());
        LiteralExpression defaultOutputEntry = LiteralExpressionPropertyConverter.wbFromDMN(dmn.getDefaultOutputEntry());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());
        
        OutputClause result = new OutputClause();
        result.setId(id);
        result.setName(dmn.getName());
        result.setDescription(description);
        result.setOutputValues(outputValues);
        result.setDefaultOutputEntry(defaultOutputEntry);
        result.setTypeRef(typeRef);

        return result;
    }

    public static org.kie.dmn.model.api.OutputClause dmnFromWB(final OutputClause wb) {
        org.kie.dmn.model.api.OutputClause result = new org.kie.dmn.model.v1_1.TOutputClause();
        result.setId(wb.getId().getValue());
        result.setName(wb.getName());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.setOutputValues(UnaryTestsPropertyConverter.dmnFromWB(wb.getOutputValues()));
        result.setDefaultOutputEntry(LiteralExpressionPropertyConverter.dmnFromWB(wb.getDefaultOutputEntry()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        return result;
    }
}