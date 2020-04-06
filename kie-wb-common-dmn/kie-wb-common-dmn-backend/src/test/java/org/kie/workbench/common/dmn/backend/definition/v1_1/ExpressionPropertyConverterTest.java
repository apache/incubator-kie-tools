/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.v1_2.TContext;
import org.kie.dmn.model.v1_2.TDecisionTable;
import org.kie.dmn.model.v1_2.TFunctionDefinition;
import org.kie.dmn.model.v1_2.TInvocation;
import org.kie.dmn.model.v1_2.TList;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.dmn.model.v1_2.TRelation;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExpressionPropertyConverterTest {

    private static final String EXPRESSION_UUID = "uuid";

    @Mock
    private BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer;

    @Mock
    private Consumer<ComponentWidths> componentWidthsConsumer;

    @Captor
    private ArgumentCaptor<HasComponentWidths> hasComponentWidthsCaptor;

    @Captor
    private ArgumentCaptor<ComponentWidths> componentWidthsCaptor;

    @Test
    public void testWBFromDMN_LiteralExpressionConversion() {
        final org.kie.dmn.model.api.LiteralExpression dmn = new TLiteralExpression();
        dmn.setId(EXPRESSION_UUID);

        assertWBFromDMNConversion(dmn, LiteralExpression.class);
    }

    private void assertWBFromDMNConversion(final org.kie.dmn.model.api.Expression dmn,
                                           final Class wbClass) {
        final Expression wb = ExpressionPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);
        assertThat(wb).isInstanceOf(wbClass);

        verify(hasComponentWidthsConsumer).accept(eq(EXPRESSION_UUID),
                                                  hasComponentWidthsCaptor.capture());

        final HasComponentWidths hasComponentWidths = hasComponentWidthsCaptor.getValue();
        assertThat(hasComponentWidths).isNotNull();
        assertThat(hasComponentWidths).isEqualTo(wb);
    }

    @Test
    public void testDMNFromWB_LiteralExpressionConversion() {
        final LiteralExpression wb = new LiteralExpression();
        final List<Double> wbComponentWidths = wb.getComponentWidths();
        wbComponentWidths.set(0, 200.0);
        wb.getId().setValue(EXPRESSION_UUID);

        assertDMNFromWBConversion(wb, TLiteralExpression.class, 200.0);
    }

    private void assertDMNFromWBConversion(final Expression wb,
                                           final Class dmnClass,
                                           final double... expectedComponentWidths) {
        final org.kie.dmn.model.api.Expression dmn = ExpressionPropertyConverter.dmnFromWB(wb, componentWidthsConsumer);
        assertThat(dmn).isInstanceOf(dmnClass);

        verify(componentWidthsConsumer).accept(componentWidthsCaptor.capture());

        final ComponentWidths componentWidths = componentWidthsCaptor.getValue();
        assertThat(componentWidths).isNotNull();
        assertThat(componentWidths.getDmnElementRef().getLocalPart()).isEqualTo(EXPRESSION_UUID);

        final List<Double> widths = componentWidths.getWidths();
        assertThat(widths.size()).isEqualTo(wb.getRequiredComponentWidthCount());
        assertThat(widths.size()).isEqualTo(expectedComponentWidths.length);

        IntStream.range(0, expectedComponentWidths.length).forEach(i -> assertThat(widths.get(i)).isEqualTo(expectedComponentWidths[i]));
    }

    @Test
    public void testWBFromDMN_ContextConversion() {
        final org.kie.dmn.model.api.Context dmn = new TContext();
        dmn.setId(EXPRESSION_UUID);

        assertWBFromDMNConversion(dmn, Context.class);
    }

    @Test
    public void testDMNFromWB_ContextConversion() {
        final Context wb = new Context();
        final List<Double> wbComponentWidths = wb.getComponentWidths();
        wbComponentWidths.set(0, 100.0);
        wbComponentWidths.set(1, 200.0);
        wbComponentWidths.set(2, 300.0);
        wb.getId().setValue(EXPRESSION_UUID);

        assertDMNFromWBConversion(wb, TContext.class, 100.0, 200.0, 300.0);
    }

    @Test
    public void testWBFromDMN_RelationConversion() {
        final org.kie.dmn.model.api.Relation dmn = new TRelation();
        dmn.setId(EXPRESSION_UUID);

        assertWBFromDMNConversion(dmn, Relation.class);
    }

    @Test
    public void testDMNFromWB_RelationConversion() {
        final Relation wb = new Relation();
        final List<Double> wbComponentWidths = wb.getComponentWidths();
        wbComponentWidths.set(0, 200.0);
        wb.getId().setValue(EXPRESSION_UUID);

        assertDMNFromWBConversion(wb, TRelation.class, 200.0);
    }

    @Test
    public void testWBFromDMN_ListConversion() {
        final org.kie.dmn.model.api.List dmn = new TList();
        dmn.setId(EXPRESSION_UUID);

        assertWBFromDMNConversion(dmn, org.kie.workbench.common.dmn.api.definition.model.List.class);
    }

    @Test
    public void testDMNFromWB_ListConversion() {
        final org.kie.workbench.common.dmn.api.definition.model.List wb = new org.kie.workbench.common.dmn.api.definition.model.List();
        final List<Double> wbComponentWidths = wb.getComponentWidths();
        wbComponentWidths.set(0, 50.0);
        wbComponentWidths.set(1, 200.0);
        wb.getId().setValue(EXPRESSION_UUID);

        assertDMNFromWBConversion(wb, TList.class, 50.0, 200.0);
    }

    @Test
    public void testWBFromDMN_InvocationConversion() {
        final org.kie.dmn.model.api.Invocation dmn = new TInvocation();
        dmn.setId(EXPRESSION_UUID);

        assertWBFromDMNConversion(dmn, Invocation.class);
    }

    @Test
    public void testDMNFromWB_InvocationConversion() {
        final Invocation wb = new Invocation();
        final List<Double> wbComponentWidths = wb.getComponentWidths();
        wbComponentWidths.set(0, 100.0);
        wbComponentWidths.set(1, 200.0);
        wbComponentWidths.set(2, 300.0);
        wb.getId().setValue(EXPRESSION_UUID);

        assertDMNFromWBConversion(wb, TInvocation.class, 100.0, 200.0, 300.0);
    }

    @Test
    public void testWBFromDMN_FunctionDefinitionConversion() {
        final org.kie.dmn.model.api.FunctionDefinition dmn = new TFunctionDefinition();
        dmn.setId(EXPRESSION_UUID);

        assertWBFromDMNConversion(dmn, FunctionDefinition.class);
    }

    @Test
    public void testDMNFromWB_FunctionDefinitionConversion() {
        final FunctionDefinition wb = new FunctionDefinition();
        final List<Double> wbComponentWidths = wb.getComponentWidths();
        wbComponentWidths.set(0, 100.0);
        wbComponentWidths.set(1, 200.0);
        wb.getId().setValue(EXPRESSION_UUID);

        assertDMNFromWBConversion(wb, TFunctionDefinition.class, 100.0, 200.0);
    }

    @Test
    public void testWBFromDMN_DecisionTableConversion() {
        final org.kie.dmn.model.api.DecisionTable dmn = new TDecisionTable();
        dmn.setId(EXPRESSION_UUID);

        assertWBFromDMNConversion(dmn, DecisionTable.class);
    }

    @Test
    public void testDMNFromWB_DecisionTableConversion() {
        final DecisionTable wb = new DecisionTable();
        final List<Double> wbComponentWidths = wb.getComponentWidths();
        wbComponentWidths.set(0, 100.0);
        wb.getId().setValue(EXPRESSION_UUID);

        assertDMNFromWBConversion(wb, TDecisionTable.class, 100.0);
    }

    @Test
    public void testWBFromDMN_NullConversion() {
        assertThat(ExpressionPropertyConverter.wbFromDMN(null, hasComponentWidthsConsumer)).isNull();
    }

    @Test
    public void testDMNFromWB_NullConversion() {
        assertThat(ExpressionPropertyConverter.dmnFromWB(null, componentWidthsConsumer)).isNull();
    }
}
