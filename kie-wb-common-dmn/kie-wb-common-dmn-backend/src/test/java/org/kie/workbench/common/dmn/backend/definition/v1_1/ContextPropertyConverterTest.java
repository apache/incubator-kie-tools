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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.FunctionKind;
import org.kie.dmn.model.v1_2.TContext;
import org.kie.dmn.model.v1_2.TContextEntry;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextPropertyConverterTest {

    private static final String EXPRESSION_UUID = "uuid";

    private static final String DEFAULT_EXPRESSION_UUID = "default-uuid";

    @Mock
    private BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer;

    @Mock
    private Consumer<ComponentWidths> componentWidthsConsumer;

    @Mock
    private Decision decision;

    @Mock
    private FunctionDefinition functionDefinition;

    @Captor
    private ArgumentCaptor<HasComponentWidths> hasComponentWidthsCaptor;

    @Captor
    private ArgumentCaptor<ComponentWidths> componentWidthsCaptor;

    private LiteralExpression literalExpression;

    @Before
    public void setup() {
        this.literalExpression = new LiteralExpression();
    }

    @Test
    public void testWBFromDMNWithDecisionAsParent() {
        final org.kie.dmn.model.api.Context dmn = setupWBFromDMN(decision);

        final Context wb = ContextPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertDefaultContextEntry(wb);
    }

    @Test
    public void testWBFromDMNWithJavaFunctionDefinitionAsParent() {
        when(functionDefinition.getKind()).thenReturn(FunctionKind.JAVA);

        final org.kie.dmn.model.api.Context dmn = setupWBFromDMN(functionDefinition);

        final Context wb = ContextPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertNoDefaultContextEntry(wb);
    }

    @Test
    public void testWBFromDMNWithPMMLFunctionDefinitionAsParent() {
        when(functionDefinition.getKind()).thenReturn(FunctionKind.PMML);

        final org.kie.dmn.model.api.Context dmn = setupWBFromDMN(functionDefinition);

        final Context wb = ContextPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertNoDefaultContextEntry(wb);
    }

    @Test
    public void testWBFromDMNWithFEELFunctionDefinitionAsParent() {
        when(functionDefinition.getKind()).thenReturn(FunctionKind.FEEL);

        final org.kie.dmn.model.api.Context dmn = setupWBFromDMN(functionDefinition);

        final Context wb = ContextPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertDefaultContextEntry(wb);
    }

    private void assertDefaultContextEntry(final Context wb) {
        assertThat(wb).isNotNull();
        assertThat(wb.getContextEntry()).isNotNull();
        assertThat(wb.getContextEntry().size()).isEqualTo(2);
        assertThat(wb.getContextEntry().get(0)).isNotNull();
        assertThat(wb.getContextEntry().get(0).getExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(wb.getContextEntry().get(0).getExpression().getId().getValue()).isEqualTo(EXPRESSION_UUID);

        assertThat(wb.getContextEntry().get(1)).isNotNull();
        assertThat(wb.getContextEntry().get(1).getExpression()).isNull();

        verify(hasComponentWidthsConsumer).accept(eq(EXPRESSION_UUID),
                                                  hasComponentWidthsCaptor.capture());

        final HasComponentWidths hasComponentWidths = hasComponentWidthsCaptor.getValue();
        assertThat(hasComponentWidths).isNotNull();
        assertThat(hasComponentWidths).isEqualTo(wb.getContextEntry().get(0).getExpression());
    }

    private void assertNoDefaultContextEntry(final Context wb) {
        assertThat(wb).isNotNull();
        assertThat(wb.getContextEntry()).isNotNull();
        assertThat(wb.getContextEntry().size()).isEqualTo(1);
        assertThat(wb.getContextEntry().get(0)).isNotNull();
        assertThat(wb.getContextEntry().get(0).getExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(wb.getContextEntry().get(0).getExpression().getId().getValue()).isEqualTo(EXPRESSION_UUID);

        verify(hasComponentWidthsConsumer).accept(eq(EXPRESSION_UUID),
                                                  hasComponentWidthsCaptor.capture());

        final HasComponentWidths hasComponentWidths = hasComponentWidthsCaptor.getValue();
        assertThat(hasComponentWidths).isNotNull();
        assertThat(hasComponentWidths).isEqualTo(wb.getContextEntry().get(0).getExpression());
    }

    private org.kie.dmn.model.api.Context setupWBFromDMN(final DMNModelInstrumentedBase parent) {
        final org.kie.dmn.model.api.Context dmn = new TContext();
        final org.kie.dmn.model.api.ContextEntry contextEntry = new TContextEntry();
        final org.kie.dmn.model.api.InformationItem informationItem = new TInformationItem();
        final org.kie.dmn.model.api.LiteralExpression literalExpression = new TLiteralExpression();
        literalExpression.setId(EXPRESSION_UUID);
        contextEntry.setExpression(literalExpression);
        contextEntry.setVariable(informationItem);
        dmn.getContextEntry().add(contextEntry);
        dmn.setParent(parent);
        return dmn;
    }

    @Test
    public void testDMNFromWBWithNoDefault() {
        final Context wb = setupDMNFromWB(Optional.empty());

        //If no Expression has been defined for the _last_ (default) ContextEntry it should not be converted
        final org.kie.dmn.model.api.Context dmn = ContextPropertyConverter.dmnFromWB(wb, componentWidthsConsumer);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getContextEntry()).isNotNull();
        assertThat(dmn.getContextEntry().size()).isEqualTo(1);
        assertThat(dmn.getContextEntry().get(0)).isNotNull();
        assertThat(dmn.getContextEntry().get(0).getExpression().getId()).isEqualTo(EXPRESSION_UUID);

        verify(componentWidthsConsumer).accept(componentWidthsCaptor.capture());

        final ComponentWidths componentWidths = componentWidthsCaptor.getValue();
        assertThat(componentWidths).isNotNull();
        assertThat(componentWidths.getDmnElementRef().getLocalPart()).isEqualTo(EXPRESSION_UUID);
        assertThat(componentWidths.getWidths().size()).isEqualTo(literalExpression.getRequiredComponentWidthCount());
        assertThat(componentWidths.getWidths().get(0)).isEqualTo(200.0);
    }

    @Test
    public void testDMNFromWBWithDefault() {
        final LiteralExpression defaultExpression = new LiteralExpression();
        defaultExpression.getId().setValue(DEFAULT_EXPRESSION_UUID);
        defaultExpression.getComponentWidths().set(0, 100.0);
        final Context wb = setupDMNFromWB(Optional.of(defaultExpression));

        final org.kie.dmn.model.api.Context dmn = ContextPropertyConverter.dmnFromWB(wb, componentWidthsConsumer);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getContextEntry()).isNotNull();
        assertThat(dmn.getContextEntry().size()).isEqualTo(2);

        assertThat(dmn.getContextEntry().get(0)).isNotNull();
        assertThat(dmn.getContextEntry().get(0).getExpression().getId()).isEqualTo(EXPRESSION_UUID);

        assertThat(dmn.getContextEntry().get(1)).isNotNull();
        assertThat(dmn.getContextEntry().get(1).getExpression().getId()).isEqualTo(DEFAULT_EXPRESSION_UUID);

        verify(componentWidthsConsumer, times(2)).accept(componentWidthsCaptor.capture());

        final List<ComponentWidths> componentWidths = componentWidthsCaptor.getAllValues();
        assertThat(componentWidths).isNotNull();
        assertThat(componentWidths.size()).isEqualTo(2);

        assertThat(componentWidths.get(0).getDmnElementRef().getLocalPart()).isEqualTo(EXPRESSION_UUID);
        assertThat(componentWidths.get(0).getWidths().size()).isEqualTo(literalExpression.getRequiredComponentWidthCount());
        assertThat(componentWidths.get(0).getWidths().get(0)).isEqualTo(200.0);

        assertThat(componentWidths.get(1).getDmnElementRef().getLocalPart()).isEqualTo(DEFAULT_EXPRESSION_UUID);
        assertThat(componentWidths.get(1).getWidths().size()).isEqualTo(defaultExpression.getRequiredComponentWidthCount());
        assertThat(componentWidths.get(1).getWidths().get(0)).isEqualTo(100.0);
    }

    private Context setupDMNFromWB(final Optional<Expression> defaultExpression) {
        final Context wb = new Context();
        final ContextEntry contextEntry1 = new ContextEntry();
        final ContextEntry contextEntry2 = new ContextEntry();
        final InformationItem informationItem = new InformationItem();
        literalExpression.getComponentWidths().set(0, 200.0);
        literalExpression.getId().setValue(EXPRESSION_UUID);
        contextEntry1.setExpression(literalExpression);
        contextEntry1.setVariable(informationItem);
        defaultExpression.ifPresent(contextEntry2::setExpression);
        wb.getContextEntry().add(contextEntry1);
        wb.getContextEntry().add(contextEntry2);
        return wb;
    }
}
