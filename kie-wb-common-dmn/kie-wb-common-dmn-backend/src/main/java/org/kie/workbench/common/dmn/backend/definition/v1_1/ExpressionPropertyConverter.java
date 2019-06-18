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
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.xml.namespace.QName;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;

public class ExpressionPropertyConverter {

    public static Expression wbFromDMN(final org.kie.dmn.model.api.Expression dmn,
                                       final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (dmn instanceof org.kie.dmn.model.api.LiteralExpression) {
            final LiteralExpression e = LiteralExpressionPropertyConverter.wbFromDMN((org.kie.dmn.model.api.LiteralExpression) dmn);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (dmn instanceof org.kie.dmn.model.api.Context) {
            final Context e = ContextPropertyConverter.wbFromDMN((org.kie.dmn.model.api.Context) dmn,
                                                                 hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (dmn instanceof org.kie.dmn.model.api.Relation) {
            final Relation e = RelationPropertyConverter.wbFromDMN((org.kie.dmn.model.api.Relation) dmn,
                                                                   hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (dmn instanceof org.kie.dmn.model.api.List) {
            final List e = ListPropertyConverter.wbFromDMN((org.kie.dmn.model.api.List) dmn,
                                                           hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (dmn instanceof org.kie.dmn.model.api.Invocation) {
            final Invocation e = InvocationPropertyConverter.wbFromDMN((org.kie.dmn.model.api.Invocation) dmn,
                                                                       hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (dmn instanceof org.kie.dmn.model.api.FunctionDefinition) {
            final FunctionDefinition e = FunctionDefinitionPropertyConverter.wbFromDMN((org.kie.dmn.model.api.FunctionDefinition) dmn,
                                                                                       hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (dmn instanceof org.kie.dmn.model.api.DecisionTable) {
            final DecisionTable e = DecisionTablePropertyConverter.wbFromDMN((org.kie.dmn.model.api.DecisionTable) dmn);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        }
        return null;
    }

    public static org.kie.dmn.model.api.Expression dmnFromWB(final Expression wb,
                                                             final Consumer<ComponentWidths> componentWidthsConsumer) {
        // SPECIAL CASE: to represent a partially edited DMN file.
        // reference above.
        if (wb == null) {
            final org.kie.dmn.model.api.LiteralExpression mockedExpression = new org.kie.dmn.model.v1_2.TLiteralExpression();
            return mockedExpression;
        }

        final String uuid = wb.getId().getValue();
        if (Objects.nonNull(uuid)) {
            final ComponentWidths componentWidths = new ComponentWidths();
            componentWidths.setDmnElementRef(new QName(uuid));
            componentWidths.setWidths(new ArrayList<>(wb.getComponentWidths()));
            componentWidthsConsumer.accept(componentWidths);
        }

        if (wb instanceof IsLiteralExpression) {
            return LiteralExpressionPropertyConverter.dmnFromWB((IsLiteralExpression) wb);
        } else if (wb instanceof Context) {
            return ContextPropertyConverter.dmnFromWB((Context) wb,
                                                      componentWidthsConsumer);
        } else if (wb instanceof Relation) {
            return RelationPropertyConverter.dmnFromWB((Relation) wb,
                                                       componentWidthsConsumer);
        } else if (wb instanceof List) {
            return ListPropertyConverter.dmnFromWB((List) wb,
                                                   componentWidthsConsumer);
        } else if (wb instanceof Invocation) {
            return InvocationPropertyConverter.dmnFromWB((Invocation) wb,
                                                         componentWidthsConsumer);
        } else if (wb instanceof FunctionDefinition) {
            return FunctionDefinitionPropertyConverter.dmnFromWB((FunctionDefinition) wb,
                                                                 componentWidthsConsumer);
        } else if (wb instanceof DecisionTable) {
            return DecisionTablePropertyConverter.dmnFromWB((DecisionTable) wb);
        }
        return null;
    }
}
