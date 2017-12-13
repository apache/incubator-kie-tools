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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class UndefinedExpressionColumnRendererTest {

    @Mock
    private ExpressionEditorDefinition<Expression> editorDefinition;

    @Mock
    private LiteralExpression expression;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private Group editorTypesContainer;

    @Mock
    private Point2D editorTypesContainerLocation;

    @Mock
    private ExpressionEditorTooltip tooltip;

    @Mock
    private Rectangle tooltipPlaceholder;

    @Mock
    private UndefinedExpressionGrid gridWidget;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private NodeMouseEnterEvent mouseEnterEvent;

    private UndefinedExpressionColumnRenderer renderer;

    @Before
    public void setup() {
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(editorDefinition);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(ExpressionType.LITERAL_EXPRESSION).when(editorDefinition).getType();
        doReturn(Optional.of(expression)).when(editorDefinition).getModelClass();
        doReturn(viewport).when(editorTypesContainer).getViewport();
        doReturn(transform).when(viewport).getTransform();
        doReturn(editorTypesContainerLocation).when(editorTypesContainer).getAbsoluteLocation();

        GwtMockito.useProviderForType(Group.class, type -> editorTypesContainer);

        this.renderer = new MockUndefinedExpressionColumnRenderer(expressionEditorDefinitionsSupplier,
                                                                  gridWidget);
    }

    @Test
    public void testTooltipPositioningNoTranslation() {
        assertPositioning(0, 0, 100, 200, 100, 200);
    }

    @Test
    public void testTooltipPositioningWithTranslationNegative() {
        assertPositioning(-20, -40, 100, 200, 120, 240);
    }

    @Test
    public void testTooltipPositioningWithTranslationPositive() {
        assertPositioning(20, 40, 100, 200, 80, 160);
    }

    private void assertPositioning(final double tx,
                                   final double ty,
                                   final double editorTypesContainerLocationX,
                                   final double editorTypesContainerLocationY,
                                   final double expectedAbsoluteCellX,
                                   final double expectedAbsoluteCellY) {
        doReturn(tx).when(transform).getTranslateX();
        doReturn(ty).when(transform).getTranslateY();
        doReturn(editorTypesContainerLocationX).when(editorTypesContainerLocation).getX();
        doReturn(editorTypesContainerLocationY).when(editorTypesContainerLocation).getY();

        final NodeMouseEnterHandler handler = renderer.getNodeMouseEnterHandler(editorDefinition,
                                                                                tooltipPlaceholder);

        handler.onNodeMouseEnter(mouseEnterEvent);

        verify(tooltip).show(eq(editorDefinition),
                             eq(expectedAbsoluteCellX),
                             eq(expectedAbsoluteCellY),
                             eq(tooltipPlaceholder));
    }

    private class MockUndefinedExpressionColumnRenderer extends UndefinedExpressionColumnRenderer {

        public MockUndefinedExpressionColumnRenderer(final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                                     final UndefinedExpressionGrid gridWidget) {
            super(expressionEditorDefinitionsSupplier,
                  gridWidget);
        }

        @Override
        ExpressionEditorTooltip getTooltip() {
            return tooltip;
        }
    }
}
