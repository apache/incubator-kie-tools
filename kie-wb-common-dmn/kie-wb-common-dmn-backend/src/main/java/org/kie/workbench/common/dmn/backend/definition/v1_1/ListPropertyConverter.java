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

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;

public class ListPropertyConverter {

    public static List wbFromDMN(final org.kie.dmn.model.api.List dmn,
                                 final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        final java.util.List<Expression> expression = new ArrayList<>();
        for (org.kie.dmn.model.api.Expression e : dmn.getExpression()) {
            Expression eConverted = ExpressionPropertyConverter.wbFromDMN(e,
                                                                          hasComponentWidthsConsumer);
            expression.add(eConverted);
        }

        final List result = new List(id, description, typeRef, expression);
        for (Expression e : expression) {
            if (e != null) {
                e.setParent(result);
            }
        }
        return result;
    }

    public static org.kie.dmn.model.api.List dmnFromWB(final List wb,
                                                       final Consumer<ComponentWidths> componentWidthsConsumer) {
        final org.kie.dmn.model.api.List result = new org.kie.dmn.model.v1_2.TList();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);

        for (Expression e : wb.getExpression()) {
            final org.kie.dmn.model.api.Expression eConverted = ExpressionPropertyConverter.dmnFromWB(e,
                                                                                                      componentWidthsConsumer);
            if (eConverted != null) {
                eConverted.setParent(result);
            }
            result.getExpression().add(eConverted);
        }

        return result;
    }
}
