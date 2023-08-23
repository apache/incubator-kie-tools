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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBinding;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

public class BindingPropertyConverter {

    public static Binding wbFromDMN(final JSITBinding dmn,
                                    final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (Objects.isNull(dmn)) {
            return null;
        }
        final InformationItem parameter = InformationItemPropertyConverter.wbFromDMN(dmn.getParameter());

        Expression expression = null;
        final JSITExpression jsiWrapped = dmn.getExpression();
        if (Objects.nonNull(jsiWrapped)) {
            final JSITExpression jsiExpression = Js.uncheckedCast(JsUtils.getUnwrappedElement(jsiWrapped));
            expression = ExpressionPropertyConverter.wbFromDMN(jsiExpression,
                                                               Js.uncheckedCast(dmn),
                                                               hasComponentWidthsConsumer);
        }

        final Binding result = new Binding();
        if (Objects.nonNull(parameter)) {
            parameter.setParent(result);
        }
        result.setParameter(parameter);
        if (Objects.nonNull(expression)) {
            expression.setParent(result);
        }
        result.setExpression(expression);
        return result;
    }

    public static JSITBinding dmnFromWB(final Binding wb,
                                        final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        if (Objects.isNull(wb)) {
            return null;
        }
        final JSITBinding result = JSITBinding.newInstance();
        final JSITInformationItem convertedParameter = InformationItemPropertyConverter.dmnFromWB(wb.getParameter());
        final JSITExpression convertedExpression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                                         componentWidthsConsumer);
        result.setParameter(convertedParameter);
        result.setExpression(convertedExpression);

        return result;
    }
}
