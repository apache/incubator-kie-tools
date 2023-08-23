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
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class OutputClauseLiteralExpressionPropertyConverter {

    /**
     * Returns a non-null instance of OutputClauseLiteralExpression. The Properties Panel needs
     * non-null objects to bind therefore a concrete object must always be returned.
     * @param dmn
     * @return
     */
    public static OutputClauseLiteralExpression wbFromDMN(final JSITLiteralExpression dmn) {
        if (Objects.isNull(dmn)) {
            return new OutputClauseLiteralExpression();
        }
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());
        final Text text = new Text(dmn.getText());
        final ImportedValues importedValues = ImportedValuesConverter.wbFromDMN(dmn.getImportedValues());
        final OutputClauseLiteralExpression result = new OutputClauseLiteralExpression(id,
                                                                                       description,
                                                                                       typeRef,
                                                                                       text,
                                                                                       importedValues);
        if (Objects.nonNull(importedValues)) {
            importedValues.setParent(result);
        }
        return result;
    }

    /**
     * Returns a JSITLiteralExpression of null if the text of the OutputClauseLiteralExpression is empty.
     * @param wb
     * @return
     */
    public static JSITLiteralExpression dmnFromWB(final OutputClauseLiteralExpression wb) {
        if (Objects.isNull(wb)) {
            return null;
        } else if (Objects.isNull(wb.getText())) {
            return null;
        } else if (StringUtils.isEmpty(wb.getText().getValue())) {
            return null;
        }

        return LiteralExpressionPropertyConverter.dmnFromWB(wb);
    }
}
