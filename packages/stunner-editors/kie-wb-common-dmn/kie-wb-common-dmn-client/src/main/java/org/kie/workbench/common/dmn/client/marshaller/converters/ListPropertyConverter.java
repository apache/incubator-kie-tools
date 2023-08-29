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

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;

import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITExpression;

public class ListPropertyConverter {

    public static List wbFromDMN(final JSITList dmn,
                                 final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());

        final java.util.List<HasExpression> expression = new ArrayList<>();
        final List result = new List(id, description, typeRef, expression);
        final java.util.List<JSITExpression> jsiExpressions = dmn.getExpression();
        for (int i = 0; i < jsiExpressions.size(); i++) {
            final JSITExpression jsitExpression = Js.uncheckedCast(jsiExpressions.get(i));
            final Expression eConverted = ExpressionPropertyConverter.wbFromDMN(jsitExpression,
                                                                                Js.uncheckedCast(dmn),
                                                                                hasComponentWidthsConsumer);
            final HasExpression hasExpression = HasExpression.wrap(result, eConverted);
            expression.add(hasExpression);
        }

        for (HasExpression hasExpression : expression) {
            final Expression e = hasExpression.getExpression();
            if (Objects.nonNull(e)) {
                e.setParent(result);
            }
        }

        return result;
    }

    public static JSITList dmnFromWB(final List wb,
                                     final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final JSITList result = JSITList.newInstance();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        for (HasExpression hasExpression : wb.getExpression()) {
            final Expression e = hasExpression.getExpression();
            JSITExpression eConverted = ExpressionPropertyConverter.dmnFromWB(e, componentWidthsConsumer);

            if (Objects.isNull(eConverted)) {
                final JSITExpression mockExpression = JSITExpression.newInstance();
                eConverted = getWrappedJSITExpression(mockExpression, "dmn", "expression");
            }

            result.addExpression(eConverted);
        }

        return result;
    }
}
