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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.INodeXYEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.commands.general.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionContainerGridTest {

    private static final String NODE_UUID = "uuid";

    private static final String NAME = "name";

    private static final double COLUMN_NEW_WIDTH = 300.0;

    private static final double LITERAL_EXPRESSION_EDITOR_PADDING = 10.0;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private TranslationService translationService;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ClientSession session;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private LiteralExpressionGrid literalExpressionEditor;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private UndefinedExpressionGrid undefinedExpressionEditor;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

    @Mock
    private GraphCommandExecutionContext graphExecutionContext;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private IsInformationItem isInformationItem;

    @Mock
    private ParameterizedCommand<Optional<Expression>> onHasExpressionChanged;

    @Mock
    private ParameterizedCommand<Optional<HasName>> onHasNameChanged;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private CellSelectionManager cellSelectionManager;

    @Mock
    private TextAreaSingletonDOMElementFactory textAreaSingletonDOMElementFactory;

    @Mock
    private TextAreaSingletonDOMElementFactory autocompleteTextareaDOMElementFactory;

    @Captor
    private ArgumentCaptor<Optional<HasName>> hasNameCaptor;

    @Captor
    private ArgumentCaptor<ClearExpressionTypeCommand> clearExpressionTypeCommandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    @Captor
    private ArgumentCaptor<RefreshFormPropertiesEvent> refreshFormPropertiesEventCaptor;

    private interface MockHasNameHasVariable extends HasName,
                                                     HasVariable {

    }

    private MockHasNameHasVariable hasName = new MockHasNameHasVariable() {

        private Name name = new Name(NAME);

        @Override
        public Name getName() {
            return name;
        }

        @Override
        public void setName(final Name name) {
            this.name = name;
        }

        @Override
        public IsInformationItem getVariable() {
            return isInformationItem;
        }

        @Override
        public void setVariable(final IsInformationItem informationItem) {
            throw new UnsupportedOperationException("Not supported in this Unit Test");
        }

        @Override
        public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
            throw new UnsupportedOperationException("Not supported in this Unit Test");
        }
    };

    private LiteralExpression literalExpression = new LiteralExpression();

    private ExpressionGridCache expressionGridCache;

    private DMNGridLayer gridLayer;

    private ExpressionContainerGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.expressionGridCache = new ExpressionGridCacheImpl();
        this.gridLayer = spy(new DMNGridLayer());
        this.grid = new ExpressionContainerGrid(gridLayer,
                                                cellEditorControls,
                                                translationService,
                                                listSelector,
                                                sessionManager,
                                                sessionCommandManager,
                                                canvasCommandFactory,
                                                expressionEditorDefinitionsSupplier,
                                                () -> expressionGridCache,
                                                onHasExpressionChanged,
                                                onHasNameChanged,
                                                refreshFormPropertiesEvent,
                                                domainObjectSelectionEvent) {
            @Override
            public CellSelectionManager getCellSelectionManager() {
                return cellSelectionManager;
            }
        };

        this.gridLayer.add(grid);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(literalExpressionEditor.isCacheable()).thenReturn(true);
        when(literalExpressionEditor.getParentInformation()).thenReturn(parent);
        when(literalExpressionEditor.getModel()).thenReturn(new BaseGridData());
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpression));
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));

        when(undefinedExpressionEditor.getParentInformation()).thenReturn(parent);
        when(undefinedExpressionEditor.getModel()).thenReturn(new BaseGridData());
        when(undefinedExpressionEditorDefinition.getModelClass()).thenReturn(Optional.empty());
        when(undefinedExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                           any(Optional.class),
                                                           any(HasExpression.class),
                                                           any(Optional.class),
                                                           anyBoolean(),
                                                           anyInt())).thenReturn(Optional.of(undefinedExpressionEditor));

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphExecutionContext);
        doReturn(mock(Bounds.class)).when(gridLayer).getVisibleBounds();

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

        when(parent.getGridWidget()).thenReturn(grid);
        when(parent.getRowIndex()).thenReturn(0);
        when(parent.getColumnIndex()).thenReturn(0);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(Mockito.<String>any());
    }

    @Test
    public void testInitialSetup() {
        final GridData uiModel = grid.getModel();
        assertThat(uiModel).isInstanceOf(DMNGridData.class);

        assertThat(uiModel.getColumnCount()).isEqualTo(1);
        assertThat(uiModel.getColumns().get(0)).isInstanceOf(ExpressionEditorColumn.class);

        assertThat(uiModel.getRowCount()).isEqualTo(1);
    }

    @Test
    public void testGridDraggingIsDisabled() {
        assertThat(grid.onDragHandle(mock(INodeXYEvent.class))).isFalse();
    }

    @Test
    public void testDeselect() {
        grid.getModel().selectCell(0, 0);
        assertFalse(grid.getModel().getSelectedCells().isEmpty());

        grid.deselect();

        assertTrue(grid.getModel().getSelectedCells().isEmpty());
    }

    @Test
    public void testSetUndefinedExpression() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final GridCellValue<?> gridCellValue = grid.getModel().getCell(0, 0).getValue();
        assertThat(gridCellValue).isInstanceOf(ExpressionCellValue.class);
        final ExpressionCellValue expressionCellValue = (ExpressionCellValue) gridCellValue;
        assertThat(expressionCellValue.getValue().isPresent()).isTrue();
        assertThat(expressionCellValue.getValue().get()).isSameAs(undefinedExpressionEditor);
        assertThat(grid.isOnlyVisualChangeAllowed()).isFalse();
    }

    @Test
    public void testSetDefinedExpression() {
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final GridCellValue<?> gridCellValue = grid.getModel().getCell(0, 0).getValue();
        assertThat(gridCellValue).isInstanceOf(ExpressionCellValue.class);
        final ExpressionCellValue expressionCellValue = (ExpressionCellValue) gridCellValue;
        assertThat(expressionCellValue.getValue().isPresent()).isTrue();
        assertThat(expressionCellValue.getValue().get()).isSameAs(literalExpressionEditor);
        assertThat(grid.isOnlyVisualChangeAllowed()).isFalse();
    }

    @Test
    public void testSetExpressionWhenOnlyVisualChangeAllowed() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           true);

        assertThat(grid.isOnlyVisualChangeAllowed()).isTrue();
    }

    @Test
    public void testResizeContainerCorrectlyResizesExpressionComponentWidth() {
        final BaseGridData literalExpressionModel = new BaseGridData();
        final DMNGridColumn literalExpressionEditorColumn = new LiteralExpressionColumn(Collections.emptyList(),
                                                                                        autocompleteTextareaDOMElementFactory,
                                                                                        DMNGridColumn.DEFAULT_WIDTH,
                                                                                        literalExpressionEditor);
        literalExpressionModel.appendColumn(literalExpressionEditorColumn);

        when(literalExpressionEditor.getExpression()).thenReturn(() -> Optional.of(literalExpression));
        when(literalExpressionEditor.getModel()).thenReturn(literalExpressionModel);
        when(literalExpressionEditor.getPadding()).thenReturn(LITERAL_EXPRESSION_EDITOR_PADDING);
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);
        grid.getModel().getColumns().get(0).setWidth(COLUMN_NEW_WIDTH);

        assertThat(literalExpression.getComponentWidths().get(0)).isEqualTo(COLUMN_NEW_WIDTH - LITERAL_EXPRESSION_EDITOR_PADDING * 2);
    }

    @Test
    public void testSetDefinedExpressionWhenReopeningWithResizedColumn() {
        //Emulate User setting expression and resizing column
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);
        when(literalExpressionEditor.getWidth()).thenReturn(COLUMN_NEW_WIDTH);

        //Emulate re-opening editor
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        //Verify width is preserved
        assertThat(grid.getModel().getColumns().get(0).getWidth()).isEqualTo(COLUMN_NEW_WIDTH);
    }

    @Test
    public void testSetDefinedExpressionWhenReopeningWhenWorkbenchRestarted() {
        //Emulate User setting expression and resizing column
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        //Emulate re-starting the Workbench and re-opening the editor
        when(literalExpressionEditor.getWidth()).thenReturn(DMNGridColumn.DEFAULT_WIDTH);
        when(literalExpressionEditor.getMinimumWidth()).thenReturn(UndefinedExpressionColumn.DEFAULT_WIDTH);
        expressionGridCache.removeExpressionGrid(NODE_UUID);
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        //Verify width is equal to the minimum required
        assertThat(grid.getModel().getColumns().get(0).getWidth()).isEqualTo(UndefinedExpressionColumn.DEFAULT_WIDTH);
    }

    @Test
    public void testGetItemsWithClearEnabled() {
        when(hasExpression.isClearSupported()).thenReturn(true);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items.size()).isEqualTo(1);

        final HasListSelectorControl.ListSelectorItem item = items.get(0);

        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorTextItem.class);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;
        assertThat(ti.getText()).isEqualTo(DMNEditorConstants.ExpressionEditor_Clear);
        ti.getCommand().execute();
        verify(cellEditorControls).hide();
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(ClearExpressionTypeCommand.class));
    }

    @Test
    public void testGetItemsWithClearDisabled() {
        when(hasExpression.isClearSupported()).thenReturn(false);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isEmpty();
    }

    @Test
    public void testOnItemSelected() {
        final Command command = mock(Command.class);
        final HasListSelectorControl.ListSelectorTextItem listSelectorItem = mock(HasListSelectorControl.ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testOnClearExpressionItemSelected() {
        //Emulate User setting expression and resizing column
        when(literalExpressionEditor.getWidth()).thenReturn(COLUMN_NEW_WIDTH);
        when(hasExpression.getExpression()).thenReturn(literalExpression);
        when(hasExpression.isClearSupported()).thenReturn(true);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        //Get and select ClearExpression item
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorItem item = items.get(0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;

        ti.getCommand().execute();

        assertThat(grid.getModel().getColumns().get(0).getWidth()).isEqualTo(COLUMN_NEW_WIDTH);
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              clearExpressionTypeCommandCaptor.capture());

        final ClearExpressionTypeCommand clearExpressionTypeCommand = clearExpressionTypeCommandCaptor.getValue();
        //We're using a mock HasExpression therefore the ClearExpressionCommand does not change mocked behaviour. Reset mock.
        reset(hasExpression, gridLayer);

        clearExpressionTypeCommand.execute(canvasHandler);

        //Verify Expression has been cleared and UndefinedExpressionEditor resized
        assertThat(grid.getModel().getColumns().get(0).getWidth()).isEqualTo(DMNGridColumn.DEFAULT_WIDTH);
        verify(gridLayer).select(undefinedExpressionEditor);
        verify(undefinedExpressionEditor).selectFirstCell();
        verify(gridLayer).batch();

        //Check undo operation
        clearExpressionTypeCommand.undo(canvasHandler);

        //Verify Expression has been restored and UndefinedExpressionEditor resized
        assertThat(grid.getModel().getColumns().get(0).getWidth()).isEqualTo(COLUMN_NEW_WIDTH);
        verify(gridLayer).select(literalExpressionEditor);
        verify(literalExpressionEditor, times(1)).selectFirstCell();
        verify(gridLayer, times(2)).batch();
    }

    @Test
    public void testSpyHasNameWithHasNameGet() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final Optional<HasName> spy = grid.spyHasName(Optional.of(hasName));

        assertThat(spy.isPresent()).isTrue();
        assertThat(spy.get().getName().getValue()).isEqualTo(NAME);
    }

    @Test
    public void testSpyHasNameWithHasNameSetNameObject() {
        final String NEW_NAME = "new-name";

        final Name newName = new Name(NEW_NAME);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final Optional<HasName> spy = grid.spyHasName(Optional.of(hasName));

        assertThat(spy.isPresent()).isTrue();
        spy.get().setName(newName);

        assertThat(hasName.getName().getValue()).isEqualTo(NEW_NAME);
        verify(isInformationItem).setName(eq(newName));
        verify(onHasNameChanged).execute(hasNameCaptor.capture());
        assertThat(hasNameCaptor.getValue().get().getName().getValue()).isEqualTo(NEW_NAME);
    }

    @Test
    public void testSpyHasNameWithoutHasNameGet() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.empty(),
                           false);

        final Optional<HasName> spy = grid.spyHasName(Optional.empty());

        assertThat(spy.isPresent()).isTrue();
        assertThat(spy.get().getName().getValue()).isEqualTo(HasName.NOP.getName().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSpyHasNameWithoutHasNameSetNameObject() {
        final String NEW_NAME = "new-name";

        final Name newName = new Name(NEW_NAME);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.empty(),
                           false);

        final Optional<HasName> spy = grid.spyHasName(Optional.empty());

        assertThat(spy.isPresent()).isTrue();
        spy.get().setName(newName);

        assertThat(hasName.getName().getValue()).isEqualTo(NAME);
        verify(onHasNameChanged, never()).execute(any(Optional.class));
    }

    @Test
    public void testSpyHasNameUpdateUndoWithSetHasNameCommand() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final Optional<HasName> spy = grid.spyHasName(Optional.of(hasName));

        final Name newName = new Name("new-name");
        final Name oldName = spy.get().getName();
        final org.uberfire.mvp.Command canvasOperation = mock(org.uberfire.mvp.Command.class);

        final SetHasValueCommand command = new SetHasValueCommand<>(spy.get(),
                                                                    newName,
                                                                    canvasOperation);

        command.execute(canvasHandler);
        spy.ifPresent(name -> assertThat(name.getName().getValue()).isEqualTo(newName.getValue()));

        command.undo(canvasHandler);
        spy.ifPresent(name -> assertThat(name.getName().getValue()).isEqualTo(oldName.getValue()));
    }

    @Test
    public void testSpyHasExpressionWithExpressionGet() {
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final HasExpression spy = grid.spyHasExpression(hasExpression);

        assertThat(spy.getExpression()).isEqualTo(literalExpression);
    }

    @Test
    public void testSpyHasExpressionWithoutExpressionGet() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final HasExpression spy = grid.spyHasExpression(hasExpression);

        assertThat(spy.getExpression()).isNull();
    }

    @Test
    public void testSpyHasExpressionWithExpressionSet() {
        final HasExpression hasExpression = new HasExpression() {

            private Expression expression = new LiteralExpression();

            @Override
            public Expression getExpression() {
                return expression;
            }

            @Override
            public void setExpression(final Expression expression) {
                this.expression = expression;
            }

            @Override
            public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
                return null;
            }
        };

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final HasExpression spy = grid.spyHasExpression(hasExpression);

        spy.setExpression(null);

        assertThat(hasExpression.getExpression()).isNull();
    }

    @Test
    public void testSpyHasExpressionWithExpressionAsDMNModelInstrumentedBase() {
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        final HasExpression spy = grid.spyHasExpression(hasExpression);

        assertThat(spy.asDMNModelInstrumentedBase()).isEqualTo(literalExpression);
    }

    @Test
    public void testSelectCellWithPoint() {
        final Point2D point = mock(Point2D.class);
        final LiteralExpression domainObject = mock(LiteralExpression.class);
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(domainObject);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        grid.selectCell(point, false, true);

        verify(gridLayer).select(eq(grid));
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(domainObject);

        verify(cellSelectionManager).selectCell(eq(point), eq(false), eq(true));
    }

    @Test
    public void testSelectCellWithCoordinates() {
        final int uiRowIndex = 0;
        final int uiColumnIndex = 1;
        final LiteralExpression domainObject = mock(LiteralExpression.class);
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(domainObject);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        grid.selectCell(uiRowIndex, uiColumnIndex, false, true);

        verify(gridLayer).select(eq(grid));
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(domainObject);

        verify(cellSelectionManager).selectCell(eq(uiRowIndex), eq(uiColumnIndex), eq(false), eq(true));
    }

    @Test
    public void testSelectCellWithCoordinatesNonDomainObject() {
        final int uiRowIndex = 0;
        final int uiColumnIndex = 1;

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        grid.selectCell(uiRowIndex, uiColumnIndex, false, true);

        verify(gridLayer).select(eq(grid));
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);

        verify(cellSelectionManager).selectCell(eq(uiRowIndex), eq(uiColumnIndex), eq(false), eq(true));
    }

    @Test
    public void testSelectCellWithDomainObjectInStunnerGraph() {
        final int uiRowIndex = 0;
        final int uiColumnIndex = 1;
        final LiteralExpression domainObject = mock(LiteralExpression.class);
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(domainObject);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

        final Definition definition = mock(Definition.class);
        when(node.getUUID()).thenReturn(NODE_UUID);
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(domainObject);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName),
                           false);

        grid.selectCell(uiRowIndex, uiColumnIndex, false, true);

        verify(gridLayer).select(eq(grid));
        verify(refreshFormPropertiesEvent).fire(refreshFormPropertiesEventCaptor.capture());

        final RefreshFormPropertiesEvent refreshFormPropertiesEvent = refreshFormPropertiesEventCaptor.getValue();
        assertThat(refreshFormPropertiesEvent.getUuid()).isEqualTo(NODE_UUID);
        assertThat(refreshFormPropertiesEvent.getSession()).isEqualTo(session);

        verify(cellSelectionManager).selectCell(eq(uiRowIndex), eq(uiColumnIndex), eq(false), eq(true));
    }

    @Test
    public void testGetBaseExpressionGrid() {

        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> expectedBaseExpressionGrid = Optional.of(mock(LiteralExpressionGrid.class));
        final ExpressionCellValue value = mock(ExpressionCellValue.class);
        final GridCell<?> cell = new BaseGridCell<>(value);
        final Supplier<GridCell<?>> cellSupplier = () -> cell;

        when(value.getValue()).thenReturn(expectedBaseExpressionGrid);
        grid.getModel().setCell(0, 0, cellSupplier);

        assertEquals(expectedBaseExpressionGrid, grid.getBaseExpressionGrid());
    }
}
