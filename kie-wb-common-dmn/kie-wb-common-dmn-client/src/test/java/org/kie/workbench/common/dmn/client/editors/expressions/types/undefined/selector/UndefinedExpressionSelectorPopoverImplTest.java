/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UndefinedExpressionSelectorPopoverImplTest {

    @Mock
    private UndefinedExpressionSelectorPopoverView view;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private ExpressionEditorDefinition literalExpressionPMMLEditorDefinition;

    @Mock
    private UndefinedExpressionGrid undefinedExpressionGrid;

    @Captor
    private ArgumentCaptor<List<ExpressionEditorDefinition>> expressionDefinitionsCaptor;

    private Optional<String> title = Optional.of("title");

    private LiteralExpression literalExpression = new LiteralExpression();

    private LiteralExpressionPMMLDocument literalExpressionPMMLDocument = new LiteralExpressionPMMLDocument();

    private UndefinedExpressionSelectorPopoverView.Presenter popover;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(literalExpressionPMMLEditorDefinition);

        when(undefinedExpressionEditorDefinition.getModelClass()).thenReturn(Optional.empty());

        when(literalExpressionEditorDefinition.isUserSelectable()).thenReturn(true);
        when(literalExpressionEditorDefinition.getType()).thenReturn(ExpressionType.LITERAL_EXPRESSION);
        when(literalExpressionEditorDefinition.getName()).thenReturn(LiteralExpression.class.getSimpleName());
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpression));

        when(literalExpressionPMMLEditorDefinition.isUserSelectable()).thenReturn(false);
        when(literalExpressionPMMLEditorDefinition.getType()).thenReturn(ExpressionType.LITERAL_EXPRESSION);
        when(literalExpressionPMMLEditorDefinition.getName()).thenReturn(LiteralExpressionPMMLDocument.class.getSimpleName());
        when(literalExpressionPMMLEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpressionPMMLDocument));

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);

        this.popover = new UndefinedExpressionSelectorPopoverImpl(view, expressionEditorDefinitionsSupplier);
    }

    @Test
    public void testInitialisation() {
        verify(view).setExpressionEditorDefinitions(expressionDefinitionsCaptor.capture());

        final List<ExpressionEditorDefinition> expressionDefinitions = expressionDefinitionsCaptor.getValue();

        assertThat(expressionDefinitions).hasSize(1);
        assertThat(expressionDefinitions).containsOnly(literalExpressionEditorDefinition);
    }

    @Test
    public void testShowWhenBound() {
        popover.bind(undefinedExpressionGrid, 0, 0);

        popover.show(title);

        verify(view).show(eq(title));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowWhenNotBound() {
        popover.show(title);

        verify(view, never()).show(any(Optional.class));
    }

    @Test
    public void testHideWhenBound() {
        popover.bind(undefinedExpressionGrid, 0, 0);

        popover.hide();

        verify(view).hide();
    }

    @Test
    public void testHideWhenNotBound() {
        popover.hide();

        verify(view, never()).hide();
    }

    @Test
    public void testOnExpressionEditorDefinitionSelectedWhenBound() {
        popover.bind(undefinedExpressionGrid, 0, 0);

        popover.onExpressionEditorDefinitionSelected(literalExpressionEditorDefinition);

        verify(undefinedExpressionGrid).onExpressionTypeChanged(eq(ExpressionType.LITERAL_EXPRESSION));
        verify(view).hide();
    }

    @Test
    public void testOnExpressionEditorDefinitionSelectedWhenNotBound() {
        popover.onExpressionEditorDefinitionSelected(literalExpressionEditorDefinition);

        verify(undefinedExpressionGrid, never()).onExpressionTypeChanged(any(ExpressionType.class));
        verify(view, never()).hide();
    }
}
