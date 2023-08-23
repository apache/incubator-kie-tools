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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
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

    @Mock
    private TranslationService translationService;

    @Captor
    private ArgumentCaptor<List<ExpressionEditorDefinition>> expressionDefinitionsCaptor;

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

        when(translationService.getTranslation(Mockito.<String>any())).thenAnswer(i -> i.getArguments()[0]);

        this.popover = new UndefinedExpressionSelectorPopoverImpl(view, translationService, expressionEditorDefinitionsSupplier);
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

        popover.show();

        verify(view).show(eq(Optional.ofNullable(popover.getPopoverTitle())));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowWhenNotBound() {
        popover.show();

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
