/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;

public class OutputClauseLiteralExpressionPropertyConverter {

    public static OutputClauseLiteralExpression wbFromDMN(final org.kie.dmn.model.api.LiteralExpression dmn) {
        if (dmn == null) {
            return null;
        }
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);
        Text text = new Text(dmn.getText());
        ExpressionLanguage expressionLanguage = ExpressionLanguagePropertyConverter.wbFromDMN(dmn.getExpressionLanguage());
        ImportedValues importedValues = ImportedValuesConverter.wbFromDMN(dmn.getImportedValues());
        OutputClauseLiteralExpression result = new OutputClauseLiteralExpression(id,
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
}
