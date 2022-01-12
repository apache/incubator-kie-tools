/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITContextEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITLiteralExpression;

public class ContextEntryPropertyConverter {

    public static ContextEntry wbFromDMN(final JSITContextEntry dmn,
                                         final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final InformationItem variable = InformationItemPropertyConverter.wbFromDMN(dmn.getVariable());

        Expression expression = null;
        final JSITExpression jsiWrapped = dmn.getExpression();
        if (Objects.nonNull(jsiWrapped)) {
            final JSITExpression jsiExpression = Js.uncheckedCast(JsUtils.getUnwrappedElement(jsiWrapped));
            expression = ExpressionPropertyConverter.wbFromDMN(jsiExpression,
                                                               Js.uncheckedCast(dmn),
                                                               hasComponentWidthsConsumer);
        }

        final ContextEntry result = new ContextEntry();
        if (Objects.nonNull(variable)) {
            variable.setParent(result);
            result.setVariable(variable);
        }
        if (Objects.nonNull(expression)) {
            expression.setParent(result);
            result.setExpression(expression);
        }
        return result;
    }

    public static JSITContextEntry dmnFromWB(final ContextEntry wb,
                                             final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final JSITContextEntry result = JSITContextEntry.newInstance();

        final JSITInformationItem variable = InformationItemPropertyConverter.dmnFromWB(wb.getVariable());
        JSITExpression expression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                          componentWidthsConsumer);
        if (Objects.isNull(expression)) {
            final JSITLiteralExpression mockLiteralExpression = JSITLiteralExpression.newInstance();
            mockLiteralExpression.setText(ContextEntry.DEFAULT_EXPRESSION_VALUE);
            expression = getWrappedJSITLiteralExpression(mockLiteralExpression, "dmn", "literalExpression");
        }

        result.setVariable(variable);
        result.setExpression(expression);
        return result;
    }
}
