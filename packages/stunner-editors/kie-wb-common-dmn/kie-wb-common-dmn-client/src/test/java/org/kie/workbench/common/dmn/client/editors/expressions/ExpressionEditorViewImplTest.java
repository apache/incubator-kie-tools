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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.ClearExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillContextExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillDecisionTableExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillFunctionExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillInvocationExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillListExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillLiteralExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.UpdateCanvasNodeNameCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DataTypeProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.keyboard.KeyboardOperationEscapeGridCell;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelContainer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetKeyboardHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationEditCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationInvokeContextMenuForSelectedCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveDown;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveLeft;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveRight;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveUp;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.CONTEXT;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.LITERAL_EXPRESSION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.UNDEFINED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorViewImplTest {

    private static final String NODE_UUID = "uuid";

    private static final String UNDEFINED_EXPRESSION_DEFINITION_NAME = "Undefined";

    @Mock
    private Span returnToDRGLabel;

    @Mock
    private Span expressionName;

    @Mock
    private Span expressionType;

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
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<DataTypePageTabActiveEvent> dataTypePageActiveEvent;

    @Mock
    private PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

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

    @Mock(extraInterfaces = DomainObject.class)
    private HasExpression hasExpression;

    @Mock
    private HasName hasName;

    @Mock
    private HTMLDivElement betaBoxedExpressionToggle;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Captor
    private ArgumentCaptor<Transform> transformArgumentCaptor;

    @Captor
    private ArgumentCaptor<GridWidget> expressionContainerArgumentCaptor;

    @Captor
    private ArgumentCaptor<TransformMediator> transformMediatorArgumentCaptor;

    @Captor
    private ArgumentCaptor<KeyboardOperation> keyboardOperationArgumentCaptor;

    @Captor
    private ArgumentCaptor<FillExpressionCommand> commandCaptor;

    private HTMLButtonElement returnToLink;

    private ExpressionGridCache expressionGridCache;

    private DMNGridPanelContainer gridPanelContainer;

    private ExpressionEditorViewImpl view;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.expressionGridCache = new ExpressionGridCacheImpl();
        this.returnToLink = new HTMLButtonElement();
        this.gridPanelContainer = spy(new DMNGridPanelContainer());
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getExpressionGridCache()).thenReturn(expressionGridCache);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);
        when(session.getMousePanMediator()).thenReturn(mousePanMediator);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);

        doReturn(viewport).when(gridPanel).getViewport();
        doReturn(viewportMediators).when(viewport).getMediators();
        doReturn(gridPanelElement).when(gridPanel).getElement();
        doReturn(Optional.of(editor)).when(editorDefinition).getEditor(any(GridCellTuple.class),
                                                                       any(Optional.class),
                                                                       any(HasExpression.class),
                                                                       any(Optional.class),
                                                                       anyBoolean(),
                                                                       anyInt());
        doReturn(new BaseGridData()).when(editor).getModel();

        this.view = spy(new ExpressionEditorViewImpl(returnToLink,
                                                     returnToDRGLabel,
                                                     expressionName,
                                                     expressionType,
                                                     gridPanelContainer,
                                                     translationService,
                                                     listSelector,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     canvasCommandFactory,
                                                     expressionEditorDefinitionsSupplier,
                                                     refreshFormPropertiesEvent,
                                                     domainObjectSelectionEvent,
                                                     editorSelectedEvent,
                                                     dataTypePageActiveEvent,
                                                     pmmlDocumentMetadataProvider,
                                                     definitionUtils,
                                                     itemDefinitionUtils));
        view.init(presenter);
        view.bind(session);

        doReturn(hasExpression).when(view).getHasExpression();
        doReturn(editorSelectedEvent).when(view).getEditorSelectedEvent();
        doReturn(NODE_UUID).when(view).getNodeUUID();
        doReturn(false).when(view).isReactBoxedExpressionVisible();

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(undefinedExpressionEditorDefinition.getModelClass()).thenReturn(Optional.empty());
        when(undefinedExpressionEditorDefinition.getName()).thenReturn(UNDEFINED_EXPRESSION_DEFINITION_NAME);
        when(undefinedExpressionEditorDefinition.getType()).thenReturn(UNDEFINED);
        when(undefinedExpressionEditor.getModel()).thenReturn(new BaseGridData());
        when(undefinedExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                           any(Optional.class),
                                                           any(HasExpression.class),
                                                           any(Optional.class),
                                                           anyBoolean(),
                                                           anyInt())).thenReturn(Optional.of(undefinedExpressionEditor));

        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(new LiteralExpression()));
        when(literalExpressionEditorDefinition.getName()).thenReturn(LITERAL_EXPRESSION.getText());
        when(literalExpressionEditorDefinition.getType()).thenReturn(LITERAL_EXPRESSION);
        when(literalExpressionEditor.getModel()).thenReturn(new BaseGridData());
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));

        doAnswer((i) -> i.getArguments()[1]).when(translationService).format(Mockito.<String>any(), anyObject());
        doAnswer((i) -> i.getArguments()[0]).when(translationService).getTranslation(Mockito.<String>any());

        betaBoxedExpressionToggle.classList = mock(DOMTokenList.class);
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
        verify(gridPanelContainer).clear();
        verify(gridPanelContainer).setWidget(gridPanel);

        verify(view, times(7)).addKeyboardOperation(any(BaseGridWidgetKeyboardHandler.class),
                                                    keyboardOperationArgumentCaptor.capture());
        final List<KeyboardOperation> operations = keyboardOperationArgumentCaptor.getAllValues();
        assertThat(operations.get(0)).isInstanceOf(KeyboardOperationEditCell.class);
        assertThat(operations.get(1)).isInstanceOf(KeyboardOperationEscapeGridCell.class);
        assertThat(operations.get(2)).isInstanceOf(KeyboardOperationMoveLeft.class);
        assertThat(operations.get(3)).isInstanceOf(KeyboardOperationMoveRight.class);
        assertThat(operations.get(4)).isInstanceOf(KeyboardOperationMoveUp.class);
        assertThat(operations.get(5)).isInstanceOf(KeyboardOperationMoveDown.class);
        assertThat(operations.get(6)).isInstanceOf(KeyboardOperationInvokeContextMenuForSelectedCell.class);
    }

    @Test
    public void testSetupGridWidget() {
        verify(gridLayer).removeAll();
        verify(gridLayer).add(expressionContainerArgumentCaptor.capture());

        final GridWidget expressionContainer = expressionContainerArgumentCaptor.getValue();

        verify(gridLayer).select(expressionContainer);
        verify(gridLayer).enterPinnedMode(eq(expressionContainer), any(Command.class));
    }

    @Test
    public void testSetupGridWidgetPanControl() {
        verify(mousePanMediator).setTransformMediator(transformMediatorArgumentCaptor.capture());

        final TransformMediator transformMediator = transformMediatorArgumentCaptor.getValue();

        verify(mousePanMediator).setBatchDraw(true);
        verify(gridLayer).setDefaultTransformMediator(transformMediator);
        verify(viewportMediators).push(mousePanMediator);
    }

    @Test
    public void testOnResize() {
        view.onResize();

        verify(gridPanelContainer).onResize();
        verify(gridPanel).onResize();
    }

    @Test
    public void testSetReturnToLinkText() {
        final String RETURN_LINK = "return-link";

        view.setReturnToLinkText(RETURN_LINK);

        verify(returnToDRGLabel).setTextContent(RETURN_LINK);
    }

    @Test
    public void testSetExpression() {
        final Optional<HasName> hasName = Optional.empty();

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName,
                           false);

        verify(gridLayer).add(expressionContainerArgumentCaptor.capture());
        final ExpressionContainerGrid expressionContainer = (ExpressionContainerGrid) expressionContainerArgumentCaptor.getValue();
        assertFalse(expressionContainer.isOnlyVisualChangeAllowed());
    }

    @Test
    public void testSetExpressionWhenOnlyVisualChangeAllowed() {
        final Optional<HasName> hasName = Optional.empty();

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName,
                           true);

        verify(gridLayer).add(expressionContainerArgumentCaptor.capture());
        final ExpressionContainerGrid expressionContainer = (ExpressionContainerGrid) expressionContainerArgumentCaptor.getValue();
        assertTrue(expressionContainer.isOnlyVisualChangeAllowed());
    }

    @Test
    public void testSetExpressionDoesUpdateExpressionNameTextWhenHasNameIsNotEmpty() {
        final String NAME = "NAME";
        final Name name = new Name(NAME);
        final HasName hasNameMock = mock(HasName.class);
        doReturn(name).when(hasNameMock).getName();
        final Optional<HasName> hasName = Optional.of(hasNameMock);

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName,
                           false);

        verify(expressionName).setTextContent(NAME);
    }

    @Test
    public void testSetExpressionDoesNotUpdateExpressionNameTextWhenHasNameIsEmpty() {
        final Optional<HasName> hasName = Optional.empty();

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName,
                           false);

        verify(expressionName, never()).setTextContent(any(String.class));
    }

    @Test
    public void testSetExpressionDoesUpdateExpressionTypeTextWhenHasExpressionIsNotEmpty() {
        final Expression expression = new LiteralExpression();
        final Optional<HasName> hasName = Optional.empty();
        when(hasExpression.getExpression()).thenReturn(expression);

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName,
                           false);

        verify(expressionType).setTextContent(LITERAL_EXPRESSION.getText());
    }

    @Test
    public void testSetExpressionDoesNotUpdateExpressionTypeTextWhenHasExpressionTextWhenHasExpressionIsEmpty() {
        final Optional<HasName> hasName = Optional.empty();

        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName,
                           false);

        verify(expressionType).setTextContent("<" + UNDEFINED_EXPRESSION_DEFINITION_NAME + ">");
    }

    @Test
    public void testOnClickReturnToLink() {
        view.onClickReturnToDRGLink(mock(ClickEvent.class));

        verify(presenter).exit();
    }

    @Test
    public void testRefresh() {
        view.refresh();

        verify(gridLayer).batch();
    }

    @Test
    public void testSetFocus() {
        doNothing().when(view).loadNewBoxedExpressionEditor();

        view.setFocus();

        verify(view).loadNewBoxedExpressionEditor();
    }

    @Test
    public void testEditingExpression() {
        final Optional<HasName> hasName = Optional.of(HasName.NOP);
        view.setExpression(NODE_UUID,
                           hasExpression,
                           hasName,
                           false);

        verify(view).setExpressionNameText(hasName);
    }

    private void assertCommandParameters(final FillExpressionCommand command,
                                         final ExpressionProps props) {
        assertEquals(hasExpression, command.getHasExpression());
        assertEquals(props, command.getExpressionProps());
        assertEquals(editorSelectedEvent, command.getEditorSelectedEvent());
        assertEquals(NODE_UUID, command.getNodeUUID());
    }

    @Test
    public void testUpdateExpressionLiteralProps() {
        final LiteralProps props = new LiteralProps("", "", ExpressionType.LITERAL_EXPRESSION.getText(), null, "", "", 0d);

        doNothing().when(view).executeUndoableExpressionCommand(any());

        view.updateExpression(props);

        verify(view).executeUndoableExpressionCommand(commandCaptor.capture());

        final FillExpressionCommand command = commandCaptor.getValue();

        assertTrue(command instanceof FillLiteralExpressionCommand);
        assertCommandParameters(command, props);
    }

    @Test
    public void testUpdateExpressionContextProps() {
        final ContextProps props = new ContextProps("", "", CONTEXT.getText(), null, null, 0d, 0d);

        doNothing().when(view).executeUndoableExpressionCommand(any());

        view.updateExpression(props);

        verify(view).executeUndoableExpressionCommand(commandCaptor.capture());

        final FillExpressionCommand command = commandCaptor.getValue();

        assertTrue(command instanceof FillContextExpressionCommand);
        assertCommandParameters(command, props);
    }

    @Test
    public void testUpdateExpressionListProps() {
        final ListProps props = new ListProps("", "", ExpressionType.LIST.getText(), null);

        doNothing().when(view).executeUndoableExpressionCommand(any());

        view.updateExpression(props);

        verify(view).executeUndoableExpressionCommand(commandCaptor.capture());

        final FillExpressionCommand command = commandCaptor.getValue();

        assertTrue(command instanceof FillListExpressionCommand);
        assertCommandParameters(command, props);
    }

    @Test
    public void testUpdateExpressionInvocationProps() {
        final InvocationProps props = new InvocationProps("", "", ExpressionType.INVOCATION.getText(), null, null, 0d, 0d);

        doNothing().when(view).executeUndoableExpressionCommand(any());

        view.updateExpression(props);

        verify(view).executeUndoableExpressionCommand(commandCaptor.capture());

        final FillExpressionCommand command = commandCaptor.getValue();

        assertTrue(command instanceof FillInvocationExpressionCommand);
        assertCommandParameters(command, props);
    }

    @Test
    public void testUpdateExpressionFunctionProps() {
        final FunctionProps props = new FunctionProps("", "", ExpressionType.FUNCTION.getText(), null, 0d, "");

        doNothing().when(view).executeUndoableExpressionCommand(any());

        view.updateExpression(props);

        verify(view).executeUndoableExpressionCommand(commandCaptor.capture());

        final FillExpressionCommand command = commandCaptor.getValue();

        assertTrue(command instanceof FillFunctionExpressionCommand);
        assertCommandParameters(command, props);
    }

    @Test
    public void testUpdateExpressionDecisionTable() {
        final DecisionTableProps props = new DecisionTableProps("", "", ExpressionType.DECISION_TABLE.getText(), null, "", null, null, null, null);

        doNothing().when(view).executeUndoableExpressionCommand(any());

        view.updateExpression(props);

        verify(view).executeUndoableExpressionCommand(commandCaptor.capture());

        final FillExpressionCommand command = commandCaptor.getValue();

        assertTrue(command instanceof FillDecisionTableExpressionCommand);
        assertCommandParameters(command, props);
    }

    @Test
    public void testUpdateExpressionUndefined() {
        final ExpressionProps props = new ExpressionProps("", "", ExpressionType.UNDEFINED.getText(), null);
        final ExpressionContainerGrid expressionContainerGrid = mock(ExpressionContainerGrid.class);

        doNothing().when(view).executeUndoableExpressionCommand(any());
        doReturn(expressionContainerGrid).when(view).getExpressionContainerGrid();

        view.updateExpression(props);

        verify(view).executeUndoableExpressionCommand(commandCaptor.capture());
        verify(expressionContainerGrid).clearExpression(NODE_UUID);

        final FillExpressionCommand command = commandCaptor.getValue();

        assertTrue(command instanceof ClearExpressionCommand);
        assertCommandParameters(command, props);
    }

    @Test
    public void testCreateUndoAction() {

        final ArgumentCaptor<SaveCurrentStateCommand> captor = ArgumentCaptor.forClass(SaveCurrentStateCommand.class);

        final CompositeCommand.Builder commandBuilder = mock(CompositeCommand.Builder.class);
        doReturn(commandBuilder).when(view).createCommandBuilder();
        doNothing().when(view).addExpressionCommand(any(), eq(commandBuilder));
        doNothing().when(view).execute(commandBuilder);
        doReturn(Optional.empty()).when(view).getHasName();

        view.createUndoCommand();

        verify(view).addExpressionCommand(captor.capture(), eq(commandBuilder));
        verify(view).execute(commandBuilder);

        final SaveCurrentStateCommand command = captor.getValue();

        assertEquals(hasExpression, command.getHasExpression());
        assertEquals(editorSelectedEvent, command.getEditorSelectedEvent());
        assertEquals(NODE_UUID, command.getNodeUUID());
    }

    @Test
    public void testOpenDataTypePage() {
        view.openDataTypePage();

        verify(dataTypePageActiveEvent).fire(any(DataTypePageTabActiveEvent.class));
    }

    @Test
    public void testAddExpressionCommand() {

        final SaveCurrentStateCommand expressionCommand = mock(SaveCurrentStateCommand.class);
        final CompositeCommand.Builder builder = mock(CompositeCommand.Builder.class);

        view.addExpressionCommand(expressionCommand, builder);

        verify(builder).addCommand(expressionCommand);
    }

    @Test
    public void testSyncExpressionWithOlderEditor() {

        final Supplier cache = mock(Supplier.class);
        final ExpressionGridCache gridCache = mock(ExpressionGridCache.class);
        final ExpressionContainerGrid expressionContainerGrid = mock(ExpressionContainerGrid.class);
        final Optional hasName = Optional.empty();

        when(cache.get()).thenReturn(gridCache);
        doReturn(cache).when(view).getExpressionGridCacheSupplier();
        doReturn(expressionContainerGrid).when(view).getExpressionContainerGrid();
        doReturn(hasName).when(view).getHasName();

        view.syncExpressionWithOlderEditor();

        verify(gridCache).removeExpressionGrid(NODE_UUID);
        verify(expressionContainerGrid).setExpression(NODE_UUID,
                                                      hasExpression,
                                                      hasName,
                                                      false);
    }

    @Test
    public void testReloadEditor() {
        doNothing().when(view).loadNewBoxedExpressionEditor();
        doReturn(true).when(view).isReactBoxedExpressionVisible();

        view.reloadEditor();

        verify(view).loadNewBoxedExpressionEditor();
    }

    @Test
    public void testReloadEditorNotShowing() {
        doNothing().when(view).loadNewBoxedExpressionEditor();

        view.reloadEditor();

        verify(view, never()).loadNewBoxedExpressionEditor();
    }

    @Test
    public void testRetrieveDefaultDataTypeProps() {
        final DataTypeProps[] dataTypeProps = view.retrieveDefaultDataTypeProps().toArray(DataTypeProps[]::new);
        assertThat(dataTypeProps)
                .isNotNull()
                .isNotEmpty()
                .hasSize(BuiltInType.values().length)
                .extracting(builtInType -> BuiltInType.valueOf(builtInType.typeRef))
                .isNotNull();
    }

    @Test
    public void testRetrieveCustomDataTypeProps() {
        final String customDataType = "CUSTOM_DATA_TYPE";
        when(itemDefinitionUtils.all()).thenReturn(
                Collections.singletonList(new ItemDefinition() {{
                    setName(new Name(customDataType));
                }})
        );

        final DataTypeProps[] dataTypeProps = view.retrieveCustomDataTypeProps().toArray(DataTypeProps[]::new);

        assertThat(dataTypeProps)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .anyMatch(typeProps -> typeProps.isCustom && typeProps.name.equals(customDataType) && typeProps.typeRef.equals(customDataType));
    }

    @Test
    public void testOnLogicTypeSelect() {
        doNothing().when(view).setExpressionNameText(any());

        view.setExpression(NODE_UUID, hasExpression, Optional.empty(), false);

        ExpressionProps expressionProps = view.getDefaultExpressionDefinition(LITERAL_EXPRESSION.getText(), BuiltInType.UNDEFINED.getName());

        verify(literalExpressionEditorDefinition).enrichRootExpression(any(), any(), any(), eq(BuiltInType.UNDEFINED.getName()));

        assertThat(expressionProps)
                .isInstanceOf(LiteralProps.class)
                .isNotNull()
                .extracting(expression -> expression.logicType)
                .isEqualTo(LITERAL_EXPRESSION.getText());

        assertThat(expressionProps)
                .extracting(expression -> expression.dataType)
                .isEqualTo(BuiltInType.UNDEFINED.getName());
    }

    @Test
    public void testFindDomainObject_WhenObjectIsInsideHasExpression() {

        final String uuid = "someUuid";
        final DomainObject domainObject = mock(DomainObject.class);

        doReturn(false).when(view).currentDomainObjectMatches(uuid);
        doReturn(false).when(view).businessKnowledgeModelMatches(uuid);
        doReturn(domainObject).when(view).findDomainObject(uuid);

        final DomainObject foundDomainObject = view.findDomainObject(uuid);

        assertEquals(domainObject, foundDomainObject);
    }

    @Test
    public void testFindDomainObject_WhenObjectIsInsideBkm() {

        final String uuid = "someUuid";
        final BusinessKnowledgeModel bkm = mock(BusinessKnowledgeModel.class);

        doReturn(false).when(view).currentDomainObjectMatches(uuid);
        doReturn(false).when(view).innerExpressionMatches(eq(uuid));
        doReturn(true).when(view).businessKnowledgeModelMatches(uuid);
        doReturn(bkm).when(view).getBusinessKnowledgeModel();

        final DomainObject foundDomainObject = view.findDomainObject(uuid);

        assertEquals(bkm, foundDomainObject);
    }

    @Test
    public void testFindDomainObject_WhenObjectIsTheInnerExpression() {

        final String uuid = "someUuid";

        doReturn(true).when(view).currentDomainObjectMatches(uuid);

        final DomainObject foundDomainObject = view.findDomainObject(uuid);

        assertEquals(hasExpression, foundDomainObject);
    }

    @Test
    public void testFindDomainObjectInCurrentExpression_WhenInnerExpressionIsNull() {

        when(hasExpression.getExpression()).thenReturn(null);

        final DomainObject domainObject = view.findDomainObjectInCurrentExpression("randomUuid");

        assertTrue(domainObject instanceof NOPDomainObject);
    }

    @Test
    public void testFindDomainObjectInCurrentExpression_WhenIsNotFound() {

        final String uuid = "uuid";
        final Expression expression = mock(Expression.class);

        when(expression.findDomainObject(uuid)).thenReturn(Optional.empty());
        when(hasExpression.getExpression()).thenReturn(expression);
        doReturn(false).when(view).innerExpressionMatches(uuid);

        final DomainObject domainObject = view.findDomainObjectInCurrentExpression(uuid);

        assertTrue(domainObject instanceof NOPDomainObject);
        verify(expression).findDomainObject(uuid);
    }

    @Test
    public void testFindDomainObjectInCurrentExpression_WhenIsFound() {

        final String uuid = "uuid";
        final Expression expression = mock(Expression.class);

        final DomainObject domainObject = mock(DomainObject.class);
        when(expression.findDomainObject(uuid)).thenReturn(Optional.of(domainObject));
        when(hasExpression.getExpression()).thenReturn(expression);

        final DomainObject result = view.findDomainObjectInCurrentExpression(uuid);

        verify(expression).findDomainObject(uuid);
        assertFalse(result instanceof NOPDomainObject);
        assertEquals(domainObject, result);
    }

    @Test
    public void testInnerExpressionMatches() {

        final String uuid = "uuid";
        final Id id = new Id(uuid);
        final Expression expression = mock(Expression.class);

        when(expression.getId()).thenReturn(id);
        when(hasExpression.getExpression()).thenReturn(expression);

        assertTrue(view.innerExpressionMatches(uuid));
    }

    @Test
    public void testInnerExpressionMatches_WhenDoesNot() {

        final String uuid = "uuid";
        final Id id = new Id(uuid);
        final Expression expression = mock(Expression.class);

        when(expression.getId()).thenReturn(id);
        when(hasExpression.getExpression()).thenReturn(expression);

        assertFalse(view.innerExpressionMatches("anotherUuid"));
    }

    @Test
    public void testInnerExpressionMatches_WhenExpressionIsNull() {

        final String uuid = "uuid";

        when(hasExpression.getExpression()).thenReturn(null);

        assertFalse(view.innerExpressionMatches(uuid));
    }

    @Test
    public void testBusinessKnowledgeModelMatches_WhenIsNotABkm() {

        final FunctionDefinition functionDefinition = mock(FunctionDefinition.class);
        final BusinessKnowledgeModel anotherBkm = mock(BusinessKnowledgeModel.class);

        when(hasExpression.getExpression()).thenReturn(functionDefinition);
        when(functionDefinition.asDMNModelInstrumentedBase()).thenReturn(functionDefinition);
        when(functionDefinition.getParent()).thenReturn(anotherBkm);

        doReturn(null).when(view).getBusinessKnowledgeModel();

        assertFalse(view.businessKnowledgeModelMatches("someUuid"));
    }

    @Test
    public void testBusinessKnowledgeModelMatches_WhenExpressionIsNotAFunctionDefinition() {

        doReturn(null).when(view).getBusinessKnowledgeModel();

        assertFalse(view.businessKnowledgeModelMatches("someUuid"));
    }

    @Test
    public void testBusinessKnowledgeModelMatches_WhenDoesNotMatch() {

        final FunctionDefinition functionDefinition = mock(FunctionDefinition.class);
        final BusinessKnowledgeModel bkm = mock(BusinessKnowledgeModel.class);
        final String someUuid = "uuid";
        final Id id = new Id(someUuid);

        when(bkm.getEncapsulatedLogic()).thenReturn(functionDefinition);
        when(functionDefinition.getId()).thenReturn(id);
        when(hasExpression.getExpression()).thenReturn(functionDefinition);
        when(functionDefinition.asDMNModelInstrumentedBase()).thenReturn(functionDefinition);
        when(functionDefinition.getParent()).thenReturn(bkm);

        doReturn(bkm).when(view).getBusinessKnowledgeModel();

        assertFalse(view.businessKnowledgeModelMatches("anotherUuid"));
    }

    @Test
    public void testBusinessKnowledgeModelMatches_WhenMatches() {

        final FunctionDefinition functionDefinition = mock(FunctionDefinition.class);
        final BusinessKnowledgeModel bkm = mock(BusinessKnowledgeModel.class);
        final String someUuid = "uuid";
        final Id id = new Id(someUuid);

        when(bkm.getEncapsulatedLogic()).thenReturn(functionDefinition);
        when(functionDefinition.getId()).thenReturn(id);
        when(hasExpression.getExpression()).thenReturn(functionDefinition);
        when(functionDefinition.asDMNModelInstrumentedBase()).thenReturn(functionDefinition);
        when(functionDefinition.getParent()).thenReturn(bkm);

        doReturn(bkm).when(view).getBusinessKnowledgeModel();

        assertTrue(view.businessKnowledgeModelMatches(someUuid));
    }

    @Test
    public void testBusinessKnowledgeModelMatches_WhenEncapsulatedLogicMatches() {

        final FunctionDefinition functionDefinition = mock(FunctionDefinition.class);
        final BusinessKnowledgeModel bkm = mock(BusinessKnowledgeModel.class);
        final String someUuid = "uuid";
        final Id id = new Id(someUuid);
        final String encapsulatedLogicUuid = "otherUuid";

        when(bkm.getEncapsulatedLogic()).thenReturn(functionDefinition);
        when(functionDefinition.getId()).thenReturn(id);
        when(hasExpression.getExpression()).thenReturn(functionDefinition);
        when(functionDefinition.asDMNModelInstrumentedBase()).thenReturn(functionDefinition);
        when(functionDefinition.getParent()).thenReturn(bkm);

        doReturn(true).when(view).encapsulatedLogicMatches(encapsulatedLogicUuid);
        doReturn(bkm).when(view).getBusinessKnowledgeModel();

        assertTrue(view.businessKnowledgeModelMatches(encapsulatedLogicUuid));
    }

    @Test
    public void testBusinessKnowledgeModelMatches_WhenNothingMatches() {

        final FunctionDefinition functionDefinition = mock(FunctionDefinition.class);
        final BusinessKnowledgeModel bkm = mock(BusinessKnowledgeModel.class);
        final String someUuid = "uuid";
        final Id id = new Id(someUuid);
        final String encapsulatedLogicUuid = "otherUuid";

        when(bkm.getEncapsulatedLogic()).thenReturn(functionDefinition);
        when(functionDefinition.getId()).thenReturn(id);
        when(hasExpression.getExpression()).thenReturn(functionDefinition);
        when(functionDefinition.asDMNModelInstrumentedBase()).thenReturn(functionDefinition);
        when(functionDefinition.getParent()).thenReturn(bkm);

        doReturn(false).when(view).encapsulatedLogicMatches(encapsulatedLogicUuid);
        doReturn(bkm).when(view).getBusinessKnowledgeModel();

        assertFalse(view.businessKnowledgeModelMatches(encapsulatedLogicUuid));
    }

    @Test
    public void test_executeUndoableExpressionCommand_WhenExpressionIsNotOfTheSameType() {

        final FillExpressionCommand fillExpressionCommand = mock(FillExpressionCommand.class);
        final ExpressionContainerGrid expressionContainerGrid = mock(ExpressionContainerGrid.class);
        final UpdateCanvasNodeNameCommand updateCanvasNodeNameCommand = mock(UpdateCanvasNodeNameCommand.class);

        doReturn(expressionContainerGrid).when(view).getExpressionContainerGrid();
        doNothing().when(view).createUndoCommand();
        doReturn(updateCanvasNodeNameCommand).when(view).getUpdateCanvasNodeNameCommand();
        doReturn(Optional.of(HasName.NOP)).when(view).getHasName();
        when(fillExpressionCommand.isCurrentExpressionOfTheSameType()).thenReturn(false);
        when(fillExpressionCommand.getExpressionProps()).thenReturn(new ExpressionProps("id", "name", "dataType", "logicType"));

        final InOrder inOrder = inOrder(view,
                                        hasExpression,
                                        expressionContainerGrid,
                                        fillExpressionCommand,
                                        updateCanvasNodeNameCommand);

        view.executeUndoableExpressionCommand(fillExpressionCommand);

        inOrder.verify(view).createUndoCommand();
        inOrder.verify(hasExpression).setExpression(null);
        inOrder.verify(expressionContainerGrid).clearExpression(NODE_UUID);
        inOrder.verify(fillExpressionCommand).execute();
        inOrder.verify(updateCanvasNodeNameCommand).execute(NODE_UUID, HasName.NOP);
    }

    @Test
    public void test_executeUndoableExpressionCommand_WhenExpressionIsOfTheSameType() {

        final FillExpressionCommand fillExpressionCommand = mock(FillExpressionCommand.class);
        final ExpressionContainerGrid expressionContainerGrid = mock(ExpressionContainerGrid.class);
        final UpdateCanvasNodeNameCommand updateCanvasNodeNameCommand = mock(UpdateCanvasNodeNameCommand.class);

        doReturn(expressionContainerGrid).when(view).getExpressionContainerGrid();
        doNothing().when(view).createUndoCommand();
        doReturn(updateCanvasNodeNameCommand).when(view).getUpdateCanvasNodeNameCommand();
        doReturn(Optional.of(HasName.NOP)).when(view).getHasName();
        when(fillExpressionCommand.isCurrentExpressionOfTheSameType()).thenReturn(true);
        when(fillExpressionCommand.getExpressionProps()).thenReturn(new ExpressionProps("id", "name", "dataType", "logicType"));

        final InOrder inOrder = inOrder(view,
                                        hasExpression,
                                        expressionContainerGrid,
                                        fillExpressionCommand,
                                        updateCanvasNodeNameCommand);

        view.executeUndoableExpressionCommand(fillExpressionCommand);

        inOrder.verify(view).createUndoCommand();
        verify(hasExpression, never()).setExpression(null);
        verify(expressionContainerGrid, never()).clearExpression(NODE_UUID);
        inOrder.verify(fillExpressionCommand).execute();
        inOrder.verify(updateCanvasNodeNameCommand).execute(NODE_UUID, HasName.NOP);
    }
}
