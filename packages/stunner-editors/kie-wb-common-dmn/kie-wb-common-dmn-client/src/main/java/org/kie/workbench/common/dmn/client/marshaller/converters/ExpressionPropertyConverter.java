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
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITContext;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRelation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;

import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITContext;
import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITDecisionTable;
import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITFunctionDefinition;
import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITInvocation;
import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITList;
import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITLiteralExpression;
import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getWrappedJSITRelation;

public class ExpressionPropertyConverter {

    public static Expression wbFromDMN(final JSITExpression dmn,
                                       final JSITExpression parent,
                                       final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (JSITLiteralExpression.instanceOf(dmn)) {
            final JSITLiteralExpression jsiExpression = Js.uncheckedCast(dmn);
            final LiteralExpression e = LiteralExpressionPropertyConverter.wbFromDMN(jsiExpression);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITContext.instanceOf(dmn)) {
            final JSITContext jsiExpression = Js.uncheckedCast(dmn);
            final Context e = ContextPropertyConverter.wbFromDMN(jsiExpression,
                                                                 parent,
                                                                 hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITRelation.instanceOf(dmn)) {
            final JSITRelation jsiExpression = Js.uncheckedCast(dmn);
            final Relation e = RelationPropertyConverter.wbFromDMN(jsiExpression,
                                                                   hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITList.instanceOf(dmn)) {
            final JSITList jsiExpression = Js.uncheckedCast(dmn);
            final List e = ListPropertyConverter.wbFromDMN(jsiExpression,
                                                           hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITInvocation.instanceOf(dmn)) {
            final JSITInvocation jsiExpression = Js.uncheckedCast(dmn);
            final Invocation e = InvocationPropertyConverter.wbFromDMN(jsiExpression,
                                                                       hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITFunctionDefinition.instanceOf(dmn)) {
            final JSITFunctionDefinition jsiExpression = Js.uncheckedCast(dmn);
            final FunctionDefinition e = FunctionDefinitionPropertyConverter.wbFromDMN(jsiExpression,
                                                                                       hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITDecisionTable.instanceOf(dmn)) {
            final JSITDecisionTable jsiExpression = Js.uncheckedCast(dmn);
            final DecisionTable e = DecisionTablePropertyConverter.wbFromDMN(jsiExpression);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        }
        return null;
    }

    public static JSITExpression dmnFromWB(final Expression wb,
                                           final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        if (Objects.isNull(wb)) {
            return null;
        }

        final String uuid = wb.getId().getValue();
        if (Objects.nonNull(uuid)) {
            final JSITComponentWidths componentWidths = JSITComponentWidths.newInstance();
            componentWidths.setDmnElementRef(uuid);
            wb.getComponentWidths()
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(w -> componentWidths.addWidth(new Float(w)));
            componentWidthsConsumer.accept(componentWidths);
        }

        if (wb instanceof IsLiteralExpression) {
            final JSITLiteralExpression unwrappedJSITLiteralExpression = LiteralExpressionPropertyConverter.dmnFromWB((IsLiteralExpression) wb);
            final JSITLiteralExpression wrappedJSITLiteralExpression = getWrappedJSITLiteralExpression(unwrappedJSITLiteralExpression, "dmn", "literalExpression");
            return wrappedJSITLiteralExpression;
        } else if (wb instanceof Context) {
            final JSITContext unwrappedJSITContext = ContextPropertyConverter.dmnFromWB((Context) wb, componentWidthsConsumer);
            final JSITContext wrappedJSITContext = getWrappedJSITContext(unwrappedJSITContext, "dmn", "context");
            return wrappedJSITContext;
        } else if (wb instanceof Relation) {
            final JSITRelation unwrappedJSITRelation = RelationPropertyConverter.dmnFromWB((Relation) wb, componentWidthsConsumer);
            final JSITRelation wrappedJSITRelation = getWrappedJSITRelation(unwrappedJSITRelation, "dmn", "relation");
            return wrappedJSITRelation;
        } else if (wb instanceof List) {
            final JSITList unwrappedJSITList = ListPropertyConverter.dmnFromWB((List) wb, componentWidthsConsumer);
            final JSITList wrappedJSITList = getWrappedJSITList(unwrappedJSITList, "dmn", "list");
            return wrappedJSITList;
        } else if (wb instanceof Invocation) {
            final JSITInvocation unwrappedJSITInvocation = InvocationPropertyConverter.dmnFromWB((Invocation) wb, componentWidthsConsumer);
            final JSITInvocation wrappedJSITInvocation = getWrappedJSITInvocation(unwrappedJSITInvocation, "dmn", "invocation");
            return wrappedJSITInvocation;
        } else if (wb instanceof FunctionDefinition) {
            final JSITFunctionDefinition unwrappedJSITFunctionDefinition = FunctionDefinitionPropertyConverter.dmnFromWB((FunctionDefinition) wb, componentWidthsConsumer);
            final JSITFunctionDefinition wrappedJSITFunctionDefinition = getWrappedJSITFunctionDefinition(unwrappedJSITFunctionDefinition, "dmn", "functionDefinition");
            return wrappedJSITFunctionDefinition;
        } else if (wb instanceof DecisionTable) {
            final JSITDecisionTable unwrappedJSITDecisionTable = DecisionTablePropertyConverter.dmnFromWB((DecisionTable) wb);
            final JSITDecisionTable wrappedJSITDecisionTable = getWrappedJSITDecisionTable(unwrappedJSITDecisionTable, "dmn", "decisionTable");
            return wrappedJSITDecisionTable;
        }
        return null;
    }
}
