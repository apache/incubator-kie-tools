/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model;

import java.util.Objects;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.api.definition.model.ImportedValues;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImportedValues;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;

public class LiteralExpressionPropertyConverter {

    public static LiteralExpression wbFromDMN(final JSITLiteralExpression dmn) {
        if (Objects.isNull(dmn)) {
            return null;
        }
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());
        final Text text = new Text(Objects.nonNull(dmn.getText()) ? dmn.getText() : "");
        final ExpressionLanguage expressionLanguage = ExpressionLanguagePropertyConverter.wbFromDMN(dmn.getExpressionLanguage());
        final ImportedValues importedValues = ImportedValuesConverter.wbFromDMN(dmn.getImportedValues());
        final LiteralExpression result = new LiteralExpression(id,
                                                               description,
                                                               typeRef,
                                                               text,
                                                               importedValues,
                                                               expressionLanguage);
        if (Objects.nonNull(importedValues)) {
            importedValues.setParent(result);
        }
        return result;
    }

    public static JSITLiteralExpression dmnFromWB(final IsLiteralExpression wb) {
        if (Objects.isNull(wb)) {
            return null;
        }
        final JSITLiteralExpression result = GWT.create(JSITLiteralExpression.class);
        result.setId(wb.getId().getValue());
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);
        result.setText(wb.getText().getValue());
        final JSITImportedValues importedValues = ImportedValuesConverter.dmnFromWB(wb.getImportedValues());
        if (Objects.nonNull(importedValues)) {
            result.setImportedValues(importedValues);
        }
        return result;
    }
}
