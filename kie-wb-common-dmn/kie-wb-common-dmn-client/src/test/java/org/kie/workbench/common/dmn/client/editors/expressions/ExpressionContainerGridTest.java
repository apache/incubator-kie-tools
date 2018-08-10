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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionContainerGridTest {

    private static final String NODE_UUID = "uuid";

    private static final String NAME = "name";

    private static final double COLUMN_NEW_WIDTH = 200.0;

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
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ClientSession session;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private UndefinedExpressionGrid undefinedExpressionEditor;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphExecutionContext;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private ParameterizedCommand<Optional<HasName>> onHasNameChanged;

    @Mock
    private ParameterizedCommand<Optional<Expression>> onHasExpressionChanged;

    @Captor
    private ArgumentCaptor<Optional<HasName>> hasNameCaptor;

    @Captor
    private ArgumentCaptor<ClearExpressionTypeCommand> clearExpressionTypeCommandCaptor;

    private HasName hasName = new HasName() {

        private Name name = new Name(NAME);

        @Override
        public Name getName() {
            return name;
        }

        @Override
        public void setName(final Name name) {
            this.name = name;
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
                                                expressionEditorDefinitionsSupplier,
                                                () -> expressionGridCache,
                                                onHasExpressionChanged,
                                                onHasNameChanged);

        this.gridLayer.add(grid);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(parent).when(literalExpressionEditor).getParentInformation();
        doReturn(new BaseGridData()).when(literalExpressionEditor).getModel();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());

        doReturn(parent).when(undefinedExpressionEditor).getParentInformation();
        doReturn(new BaseGridData()).when(undefinedExpressionEditor).getModel();
        doReturn(Optional.empty()).when(undefinedExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(undefinedExpressionEditor)).when(undefinedExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                             any(Optional.class),
                                                                                                             any(HasExpression.class),
                                                                                                             any(Optional.class),
                                                                                                             any(Optional.class),
                                                                                                             anyInt());

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();
        doReturn(graphExecutionContext).when(canvasHandler).getGraphExecutionContext();
        doReturn(mock(Bounds.class)).when(gridLayer).getVisibleBounds();

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
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
                           Optional.of(hasName));

        final GridCellValue<?> gridCellValue = grid.getModel().getCell(0, 0).getValue();
        assertThat(gridCellValue).isInstanceOf(ExpressionCellValue.class);
        final ExpressionCellValue expressionCellValue = (ExpressionCellValue) gridCellValue;
        assertThat(expressionCellValue.getValue().isPresent()).isTrue();
        assertThat(expressionCellValue.getValue().get()).isSameAs(undefinedExpressionEditor);

        verify(undefinedExpressionEditor).selectFirstCell();
        verify(gridLayer).batch();
    }

    @Test
    public void testSetDefinedExpression() {
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));

        final GridCellValue<?> gridCellValue = grid.getModel().getCell(0, 0).getValue();
        assertThat(gridCellValue).isInstanceOf(ExpressionCellValue.class);
        final ExpressionCellValue expressionCellValue = (ExpressionCellValue) gridCellValue;
        assertThat(expressionCellValue.getValue().isPresent()).isTrue();
        assertThat(expressionCellValue.getValue().get()).isSameAs(literalExpressionEditor);

        verify(literalExpressionEditor).selectFirstCell();
        verify(gridLayer).batch();
    }

    @Test
    public void testSetDefinedExpressionWhenReopeningWithResizedColumn() {
        //Emulate User setting expression and resizing column
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));
        grid.getModel().getColumns().get(0).setWidth(COLUMN_NEW_WIDTH);

        //Emulate re-opening editor
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));

        //Verify width is preserved
        assertThat(grid.getModel().getColumns().get(0).getWidth()).isEqualTo(COLUMN_NEW_WIDTH);
    }

    @Test
    public void testGetItems() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));
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
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));
        verify(literalExpressionEditor).selectFirstCell();
        verify(gridLayer).batch();

        grid.getModel().getColumns().get(0).setWidth(COLUMN_NEW_WIDTH);

        //Get and select ClearExpression item
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorItem item = items.get(0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;

        ti.getCommand().execute();

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              clearExpressionTypeCommandCaptor.capture());

        final ClearExpressionTypeCommand clearExpressionTypeCommand = clearExpressionTypeCommandCaptor.getValue();
        //We're using a mock HasExpression therefore the ClearExpressionCommand does not change mocked behaviour. Reset mock.
        reset(hasExpression, gridLayer);

        clearExpressionTypeCommand.execute(canvasHandler);

        //Verify Expression has been cleared and UndefinedExpressionEditor
        assertThat(grid.getModel().getColumns().get(0).getWidth()).isEqualTo(DMNGridColumn.DEFAULT_WIDTH);
        verify(undefinedExpressionEditor).selectFirstCell();
        verify(gridLayer).batch();
    }

    @Test
    public void testSpyHasNameWithHasNameGet() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));

        final Optional<HasName> spy = grid.spyHasName(Optional.of(hasName));

        assertThat(spy.isPresent()).isTrue();
        assertThat(spy.get().getName().getValue()).isEqualTo(NAME);
    }

    @Test
    public void testSpyHasNameWithHasNameSetNameValue() {
        final String NEW_NAME = "new-name";

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));

        final Optional<HasName> spy = grid.spyHasName(Optional.of(hasName));

        assertThat(spy.isPresent()).isTrue();
        spy.get().getName().setValue(NEW_NAME);

        assertThat(hasName.getName().getValue()).isEqualTo(NEW_NAME);
        verify(onHasNameChanged).execute(hasNameCaptor.capture());
        assertThat(hasNameCaptor.getValue().get().getName().getValue()).isEqualTo(NEW_NAME);
    }

    @Test
    public void testSpyHasNameWithHasNameSetNameObject() {
        final String NEW_NAME = "new-name";

        final Name newName = new Name(NEW_NAME);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));

        final Optional<HasName> spy = grid.spyHasName(Optional.of(hasName));

        assertThat(spy.isPresent()).isTrue();
        spy.get().setName(newName);

        assertThat(hasName.getName().getValue()).isEqualTo(NEW_NAME);
        verify(onHasNameChanged).execute(hasNameCaptor.capture());
        assertThat(hasNameCaptor.getValue().get().getName().getValue()).isEqualTo(NEW_NAME);
    }

    @Test
    public void testSpyHasNameWithoutHasNameGet() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.empty());

        final Optional<HasName> spy = grid.spyHasName(Optional.empty());

        assertThat(spy.isPresent()).isTrue();
        assertThat(spy.get().getName().getValue()).isEqualTo(HasName.NOP.getName().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSpyHasNameWithoutHasNameSetNameValue() {
        final String NEW_NAME = "new-name";

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.empty());

        final Optional<HasName> spy = grid.spyHasName(Optional.empty());

        assertThat(spy.isPresent()).isTrue();
        spy.get().getName().setValue(NEW_NAME);

        assertThat(hasName.getName().getValue()).isEqualTo(NAME);
        verify(onHasNameChanged, never()).execute(any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSpyHasNameWithoutHasNameSetNameObject() {
        final String NEW_NAME = "new-name";

        final Name newName = new Name(NEW_NAME);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.empty());

        final Optional<HasName> spy = grid.spyHasName(Optional.empty());

        assertThat(spy.isPresent()).isTrue();
        spy.get().setName(newName);

        assertThat(hasName.getName().getValue()).isEqualTo(NAME);
        verify(onHasNameChanged, never()).execute(any(Optional.class));
    }

    @Test
    public void testSpyHasExpressionWithExpressionGet() {
        when(hasExpression.getExpression()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));

        final HasExpression spy = grid.spyHasExpression(hasExpression);

        assertThat(spy.getExpression()).isEqualTo(literalExpression);
    }

    @Test
    public void testSpyHasExpressionWithoutExpressionGet() {
        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));

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
                           Optional.of(hasName));

        final HasExpression spy = grid.spyHasExpression(hasExpression);

        spy.setExpression(null);

        assertThat(hasExpression.getExpression()).isNull();
    }

    @Test
    public void testSpyHasExpressionWithExpressionAsDMNModelInstrumentedBase() {
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(literalExpression);

        grid.setExpression(NODE_UUID,
                           hasExpression,
                           Optional.of(hasName));

        final HasExpression spy = grid.spyHasExpression(hasExpression);

        assertThat(spy.asDMNModelInstrumentedBase()).isEqualTo(literalExpression);
    }
}
