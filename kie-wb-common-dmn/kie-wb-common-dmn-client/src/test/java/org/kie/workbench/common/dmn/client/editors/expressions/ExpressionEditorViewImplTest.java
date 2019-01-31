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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelContainer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetKeyboardHandler;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorViewImplTest {

    private static final String NODE_UUID = "uuid";

    private static final String UNDEFINED_EXPRESSION_DEFINITION_NAME = "Undefined";

    private static final String LITERAL_EXPRESSION_DEFINITION_NAME = "Literal Expression";

    @Mock
    private Anchor returnToDRG;

    @Mock
    private Heading expressionType;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private RestrictedMousePanMediator mousePanMediator;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ExpressionEditorView.Presenter presenter;

    @Mock
    private TranslationService translationService;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNEditorSession session;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private ExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid undefinedExpressionEditor;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    @Mock
    private Viewport viewport;

    @Mock
    private Element gridPanelElement;

    @Mock
    private Mediators viewportMediators;

    @Mock
    private ExpressionEditorDefinition<Expression> editorDefinition;

    @Mock
    private BaseExpressionGrid editor;

    @Mock
    private HasExpression hasExpression;

    @Captor
    private ArgumentCaptor<Transform> transformArgumentCaptor;

    @Captor
    private ArgumentCaptor<GridWidget> expressionContainerArgumentCaptor;

    @Captor
    private ArgumentCaptor<TransformMediator> transformMediatorArgumentCaptor;

    private ExpressionGridCache expressionGridCache;

    private DMNGridPanelContainer gridPanelContainer;

    private ExpressionEditorViewImpl view;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.expressionGridCache = new ExpressionGridCacheImpl();
        this.gridPanelContainer = spy(new DMNGridPanelContainer());
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getExpressionGridCache()).thenReturn(expressionGridCache);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);
        when(session.getMousePanMediator()).thenReturn(mousePanMediator);

        doReturn(viewport).when(gridPanel).getViewport();
        doReturn(viewportMediators).when(viewport).getMediators();
        doReturn(gridPanelElement).when(gridPanel).getElement();
        doReturn(Optional.of(editor)).when(editorDefinition).getEditor(any(GridCellTuple.class),
                                                                       any(Optional.class),
                                                                       any(HasExpression.class),
                                                                       any(Optional.class),
                                                                       any(Optional.class),
                                                                       anyInt());
        doReturn(new BaseGridData()).when(editor).getModel();

        this.view = spy(new ExpressionEditorViewImpl(returnToDRG,
                                                     expressionType,
                                                     gridPanelContainer,
                                                     translationService,
                                                     listSelector,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     expressionEditorDefinitionsSupplier,
                                                     refreshFormPropertiesEvent,
                                                     domainObjectSelectionEvent));
        view.init(presenter);
        view.bind(session);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(undefinedExpressionEditorDefinition.getModelClass()).thenReturn(Optional.empty());
        when(undefinedExpressionEditorDefinition.getName()).thenReturn(UNDEFINED_EXPRESSION_DEFINITION_NAME);
        when(undefinedExpressionEditor.getModel()).thenReturn(new BaseGridData());
        when(undefinedExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                           any(Optional.class),
                                                           any(HasExpression.class),
                                                           any(Optional.class),
                                                           any(Optional.class),
                                                           anyInt())).thenReturn(Optional.of(undefinedExpressionEditor));

        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(new LiteralExpression()));
        when(literalExpressionEditorDefinition.getName()).thenReturn(LITERAL_EXPRESSION_DEFINITION_NAME);
        when(literalExpressionEditor.getModel()).thenReturn(new BaseGridData());
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         any(Optional.class),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));

        doAnswer((i) -> i.getArguments()[1]).when(translationService).format(anyString(), anyObject());
        doAnswer((i) -> i.getArguments()[0]).when(translationService).getTranslation(anyString());
    }

    @Test
    public void testBind() {
        //ExpressionEditorViewImpl.bind(..) is called in @Before setup
        verify(view).setupGridPanel();
        verify(view).setupGridWidget();
        verify(view).setupGridWidgetPanControl();
    }

    @Test
    public void testSetupGridPanel() {
        verify(viewport).setTransform(transformArgumentCaptor.capture());
        final Transform transform = transformArgumentCaptor.getValue();

        assertEquals(ExpressionEditorViewImpl.VP_SCALE,
                     transform.getScaleX(),
                     0.0);
        assertEquals(ExpressionEditorViewImpl.VP_SCALE,
                     transform.getScaleY(),
                     0.0);

        verify(gridPanel).addKeyDownHandler(any(BaseGridWidgetKeyboardHandler.class));
        verify(gridPanel).add(gridLayer);
        verify(gridPanelContainer).clear();
        verify(gridPanelContainer).setWidget(gridPanel);
    }

    @Test
    public void testSetupGridWidget() {
        verify(gridLayer).removeAll();
        verify(gridLayer).add(expressionContainerArgumentCaptor.capture());

        final GridWidget expressionContainer = expressionContainerArgumentCaptor.getValue();

        verify(gridLayer).select(eq(expressionContainer));
        verify(gridLayer).enterPinnedMode(eq(expressionContainer),
                                          any(Command.class));
    }

    @Test
    public void testSetupGridWidgetPanControl() {
        verify(mousePanMediator).setTransformMediator(transformMediatorArgumentCaptor.capture());

        final TransformMediator transformMediator = transformMediatorArgumentCaptor.getValue();

        verify(mousePanMediator).setBatchDraw(true);
        verify(gridLayer).setDefaultTransformMediator(eq(transformMediator));
        verify(viewportMediators).push(eq(mousePanMediator));
    }

    @Test
    public void testOnResize() {
        view.onResize();

        verify(gridPanelContainer).onResize();
        verify(gridPanel).onResize();
    }

    @Test
    public void testSetExpressionDoesUpdateReturnToDRGTextWhenHasNameIsNotEmpty() {
        final String NAME = "NAME";
        final Name name = new Name(NAME);
        final HasName hasNameMock = mock(HasName.class);
        doReturn(name).when(hasNameMock).getName();
        final Optional<HasName> hasName = Optional.of(hasNameMock);

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName);

        verify(returnToDRG).setTextContent(eq(NAME));
    }

    @Test
    public void testSetExpressionDoesNotUpdateReturnToDRGTextWhenHasNameIsEmpty() {
        final Optional<HasName> hasName = Optional.empty();

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName);

        verify(returnToDRG, never()).setTextContent(any(String.class));
    }

    @Test
    public void testSetExpressionDoesUpdateExpressionTypeTextWhenHasExpressionIsNotEmpty() {
        final Expression expression = new LiteralExpression();
        final Optional<HasName> hasName = Optional.empty();
        when(hasExpression.getExpression()).thenReturn(expression);

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName);

        verify(expressionType).setTextContent(eq(LITERAL_EXPRESSION_DEFINITION_NAME));
    }

    @Test
    public void testSetExpressionDoesNotUpdateExpressionTypeTextWhenHasExpressionTextWhenHasExpressionIsEmpty() {
        final Optional<HasName> hasName = Optional.empty();

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName);

        verify(expressionType).setTextContent(eq("<" + DMNEditorConstants.ExpressionEditor_UndefinedExpressionType + ">"));
    }

    @Test
    public void testSetFocus() {
        view.setFocus();

        verify(gridPanel).setFocus(true);
    }
}
