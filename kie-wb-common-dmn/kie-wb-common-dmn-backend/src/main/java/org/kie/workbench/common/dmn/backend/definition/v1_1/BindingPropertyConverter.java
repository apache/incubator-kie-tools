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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.v1_1.Binding;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;

public class BindingPropertyConverter {

    public static Binding wbFromDMN(final org.kie.dmn.model.api.Binding dmn,
                                    final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (dmn == null) {
            return null;
        }
        InformationItem convertedParameter = InformationItemPropertyConverter.wbFromDMN(dmn.getParameter());
        Expression convertedExpression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression(),
                                                                               hasComponentWidthsConsumer);

        Binding result = new Binding();
        if (convertedParameter != null) {
            convertedParameter.setParent(result);
        }
        result.setParameter(convertedParameter);
        if (convertedExpression != null) {
            convertedExpression.setParent(result);
        }
        result.setExpression(convertedExpression);
        return result;
    }

    public static org.kie.dmn.model.api.Binding dmnFromWB(final Binding wb,
                                                          final Consumer<ComponentWidths> componentWidthsConsumer) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.api.Binding result = new org.kie.dmn.model.v1_2.TBinding();
        org.kie.dmn.model.api.InformationItem convertedParameter = InformationItemPropertyConverter.dmnFromWB(wb.getParameter());
        org.kie.dmn.model.api.Expression convertedExpression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                                                     componentWidthsConsumer);

        if (convertedParameter != null) {
            convertedParameter.setParent(result);
        }
        result.setParameter(convertedParameter);
        if (convertedExpression != null) {
            convertedExpression.setParent(result);
        }
        result.setExpression(convertedExpression);

        return result;
    }
}
