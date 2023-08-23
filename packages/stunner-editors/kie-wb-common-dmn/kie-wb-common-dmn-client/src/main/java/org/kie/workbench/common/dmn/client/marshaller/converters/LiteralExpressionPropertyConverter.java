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
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class LiteralExpressionPropertyConverter {

    // Instance to support Unit Tests.
    static JSITLiteralExpressionFactory LITERAL_EXPRESSION_PROVIDER = new JSITLiteralExpressionFactory();

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
        final JSITLiteralExpression result = LITERAL_EXPRESSION_PROVIDER.make();
        result.setId(wb.getId().getValue());
        final String description = wb.getDescription().getValue();
        if (StringUtils.nonEmpty(description)) {
            result.setDescription(description);
        }
        if (wb instanceof LiteralExpression) {
            final String expressionLanguage = ((LiteralExpression) wb).getExpressionLanguage().getValue();
            if (StringUtils.nonEmpty(expressionLanguage)) {
                result.setExpressionLanguage(expressionLanguage);
            }
        }
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);
        result.setText(wb.getText().getValue());
        final JSITImportedValues importedValues = ImportedValuesConverter.dmnFromWB(wb.getImportedValues());
        if (Objects.nonNull(importedValues)) {
            result.setImportedValues(importedValues);
        }
        return result;
    }

    static class JSITLiteralExpressionFactory {

        JSITLiteralExpression make() {
            return JSITLiteralExpression.newInstance();
        }
    }
}
