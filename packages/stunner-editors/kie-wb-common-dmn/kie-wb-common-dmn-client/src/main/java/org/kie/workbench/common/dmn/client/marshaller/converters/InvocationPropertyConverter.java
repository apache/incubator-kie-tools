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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBinding;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

public class InvocationPropertyConverter {

    public static Invocation wbFromDMN(final JSITInvocation dmn,
                                       final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (Objects.isNull(dmn)) {
            return null;
        }
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());

        final Invocation result = new Invocation();
        result.setId(id);
        result.setDescription(description);
        result.setTypeRef(typeRef);

        Expression expression = null;
        final JSITExpression jsiWrapped = dmn.getExpression();
        if (Objects.nonNull(jsiWrapped)) {
            final JSITExpression jsiExpression = Js.uncheckedCast(JsUtils.getUnwrappedElement(jsiWrapped));
            expression = ExpressionPropertyConverter.wbFromDMN(jsiExpression,
                                                               Js.uncheckedCast(dmn),
                                                               hasComponentWidthsConsumer);
        }

        result.setExpression(expression);
        if (Objects.nonNull(expression)) {
            expression.setParent(result);
        }

        final List<JSITBinding> jsiBindings = dmn.getBinding();
        for (int i = 0; i < jsiBindings.size(); i++) {
            final JSITBinding jsiBinding = Js.uncheckedCast(jsiBindings.get(i));
            final Binding bConverted = BindingPropertyConverter.wbFromDMN(jsiBinding,
                                                                          hasComponentWidthsConsumer);
            if (Objects.nonNull(bConverted)) {
                bConverted.setParent(result);
            }
            result.getBinding().add(bConverted);
        }

        return result;
    }

    public static JSITInvocation dmnFromWB(final Invocation wb,
                                           final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        if (Objects.isNull(wb)) {
            return null;
        }
        final JSITInvocation result = JSITInvocation.newInstance();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        final JSITExpression convertedExpression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                                         componentWidthsConsumer);
        result.setExpression(convertedExpression);

        for (Binding b : wb.getBinding()) {
            final JSITBinding bConverted = BindingPropertyConverter.dmnFromWB(b, componentWidthsConsumer);
            result.addBinding(bConverted);
        }

        return result;
    }
}