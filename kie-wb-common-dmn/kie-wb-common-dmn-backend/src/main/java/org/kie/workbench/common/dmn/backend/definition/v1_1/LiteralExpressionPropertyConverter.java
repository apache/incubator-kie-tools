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

import org.kie.workbench.common.dmn.api.definition.v1_1.ImportedValues;
import org.kie.workbench.common.dmn.api.definition.v1_1.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;

public class LiteralExpressionPropertyConverter {

    public static LiteralExpression wbFromDMN(final org.kie.dmn.model.api.LiteralExpression dmn) {
        if (dmn == null) {
            return null;
        }
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);
        Text text = new Text(dmn.getText() != null ? dmn.getText() : "");
        ExpressionLanguage expressionLanguage = ExpressionLanguagePropertyConverter.wbFromDMN(dmn.getExpressionLanguage());
        ImportedValues importedValues = ImportedValuesConverter.wbFromDMN(dmn.getImportedValues());
        LiteralExpression result = new LiteralExpression(id,
                                                         description,
                                                         typeRef,
                                                         text,
                                                         importedValues,
                                                         expressionLanguage);
        if (importedValues != null) {
            importedValues.setParent(result);
        }
        return result;
    }

    public static org.kie.dmn.model.api.LiteralExpression dmnFromWB(final IsLiteralExpression wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.api.LiteralExpression result = new org.kie.dmn.model.v1_2.TLiteralExpression();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);
        result.setText(wb.getText().getValue());
        result.setExpressionLanguage(ExpressionLanguagePropertyConverter.dmnFromWB(wb.getExpressionLanguage()));
        org.kie.dmn.model.api.ImportedValues importedValues = ImportedValuesConverter.dmnFromWB(wb.getImportedValues());
        if (importedValues != null) {
            importedValues.setParent(result);
        }
        result.setImportedValues(importedValues);
        return result;
    }
}
