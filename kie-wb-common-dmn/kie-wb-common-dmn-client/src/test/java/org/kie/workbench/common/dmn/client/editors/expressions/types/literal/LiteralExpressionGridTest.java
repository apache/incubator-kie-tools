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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGrid;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetCellSelectorMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetEditCellMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.EditableHeaderGridWidgetEditCellMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
public class LiteralExpressionGridTest {

    private static final int PARENT_ROW_INDEX = 0;

    private static final int PARENT_COLUMN_INDEX = 1;

    private static final String EXPRESSION_TEXT = "expression";

    private static final String NODE_UUID = "uuid";

    private static final String NAME = "name";

    private static final String NAME_NEW = "name-new";

    private GridCellTuple tupleWithoutValue;

    private GridCellValueTuple tupleWithValue;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private DMNSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

    @Mock
    private Index index;

    @Mock
    private Element element;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private GridCellTuple parent;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridUiModel;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private NodeMouseClickEvent mouseClickEvent;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private NameAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    private Text expressionText;

    @Captor
    private ArgumentCaptor<CompositeCommand> compositeCommandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    private Decision hasExpression = new Decision();

    private Optional<LiteralExpression> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private LiteralExpressionEditorDefinition definition;

    private LiteralExpressionGrid grid;

    @Before
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        tupleWithoutValue = new GridCellTuple(0, 0, gridWidget);
        tupleWithValue = new GridCellValueTuple<>(0, 0, gridWidget, new BaseGridCellValue<>("value"));

        definition = new LiteralExpressionEditorDefinition(definitionUtils,
                                                           sessionManager,
                                                           sessionCommandManager,
                                                           canvasCommandFactory,
                                                           editorSelectedEvent,
                                                           refreshFormPropertiesEvent,
                                                           domainObjectSelectionEvent,
                                                           listSelector,
                                                           translationService,
                                                           headerEditor);

        final Decision decision = new Decision();
        decision.setName(new Name(NAME));
        hasName = Optional.of(decision);
        expression = definition.getModelClass();
        expression.ifPresent(e -> e.getText().setValue(EXPRESSION_TEXT));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(gridLayer.getVisibleBounds()).thenReturn(mock(Bounds.class));
        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(index.get(anyString())).thenReturn(element);
        when(element.getContent()).thenReturn(mock(Definition.class));
        when(definitionUtils.getNameIdentifier(any())).thenReturn("name");
        when(canvasCommandFactory.updatePropertyValue(any(Element.class),
                                                      anyString(),
                                                      any())).thenReturn(mock(UpdateElementPropertyCommand.class));
        when(parentGridWidget.getModel()).thenReturn(parentGridUiModel);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(parent.getRowIndex()).thenReturn(PARENT_ROW_INDEX);
        when(parent.getColumnIndex()).thenReturn(PARENT_COLUMN_INDEX);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(anyString());
    }

    private void setupGrid(final int nesting) {
        this.hasExpression.setExpression(expression.get());
        this.grid = spy((LiteralExpressionGrid) definition.getEditor(parent,
                                                                     nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                                     hasExpression,
                                                                     hasName,
                                                                     false,
                                                                     nesting).get());
    }

    @Test
    public void testMouseClickEventHandlers() {
        setupGrid(0);

        final List<NodeMouseEventHandler> handlers = grid.getNodeMouseClickEventHandlers(selectionManager);
        assertThat(handlers).hasSize(2);
        assertThat(handlers.get(0)).isInstanceOf(DelegatingGridWidgetCellSelectorMouseEventHandler.class);
        assertThat(handlers.get(1)).isInstanceOf(EditableHeaderGridWidgetEditCellMouseEventHandler.class);
    }

    @Test
    public void testMouseDoubleClickEventHandlers() {
        setupGrid(0);

        final List<NodeMouseEventHandler> handlers = grid.getNodeMouseDoubleClickEventHandlers(selectionManager, gridLayer);
        assertThat(handlers).hasSize(1);
        assertThat(handlers.get(0)).isInstanceOf(DelegatingGridWidgetEditCellMouseEventHandler.class);
    }

    @Test
    public void testSelectFirstCellWhenNested() {
        setupGrid(1);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells().size()).isEqualTo(0);
        verify(parentGridUiModel).selectCell(eq(PARENT_ROW_INDEX), eq(PARENT_COLUMN_INDEX));
        verify(gridLayer).select(parentGridWidget);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(expression.get());
    }

    @Test
    public void testSelectFirstCellWhenNotNested() {
        setupGrid(0);

        grid.selectFirstCell();

        final List<GridData.SelectedCell> selectedCells = grid.getModel().getSelectedCells();
        assertThat(selectedCells.size()).isEqualTo(1);
        assertThat(selectedCells.get(0).getRowIndex()).isEqualTo(0);
        assertThat(selectedCells.get(0).getColumnIndex()).isEqualTo(0);

        verify(gridLayer).select(grid);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(expression.get());
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(0);

        final GridData uiModel = grid.getModel();
        assertThat(uiModel).isInstanceOf(DMNGridData.class);

        assertThat(uiModel.getColumnCount()).isEqualTo(1);
        assertThat(uiModel.getColumns().get(0)).isInstanceOf(LiteralExpressionColumn.class);

        assertThat(uiModel.getRowCount()).isEqualTo(1);

        assertThat(uiModel.getCell(0, 0).getValue().getValue()).isEqualTo(EXPRESSION_TEXT);
    }

    @Test
    public void testInitialColumnWidthsFromDefinition() {
        setupGrid(0);

        assertComponentWidths(DMNGridColumn.DEFAULT_WIDTH);
    }

    @Test
    public void testInitialColumnWidthsFromExpression() {
        final List<Double> componentWidths = expression.get().getComponentWidths();
        componentWidths.set(0, 200.0);

        setupGrid(0);

        assertComponentWidths(200.0);
    }

    private void assertComponentWidths(final double... widths) {
        final GridData uiModel = grid.getModel();
        IntStream.range(0, widths.length).forEach(i -> assertEquals(widths[i], uiModel.getColumns().get(i).getWidth(), 0.0));
    }

    @Test
    public void testCacheable() {
        setupGrid(0);

        assertTrue(grid.isCacheable());
    }

    @Test
    public void testPaddingWithParent() {
        setupGrid(0);

        doReturn(Optional.of(mock(BaseExpressionGrid.class))).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(LiteralExpressionGrid.PADDING);
    }

    @Test
    public void testPaddingWithNoParent() {
        setupGrid(0);

        doReturn(Optional.empty()).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(BaseExpressionGrid.DEFAULT_PADDING);
    }

    @Test
    public void testGetItemsWithNoParent() {
        setupGrid(0);

        when(parent.getGridWidget()).thenReturn(mock(GridWidget.class));
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(mock(BaseGridWidget.class)));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetItemsWithParentThatDoesSupportCellControls() {
        setupGrid(0);

        final GridData parentGridData = mock(GridData.class);
        final ContextGrid parentGridWidget = mock(ContextGrid.class);
        final HasListSelectorControl.ListSelectorItem listSelectorItem = mock(HasListSelectorControl.ListSelectorItem.class);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(parentGridWidget));
        when(parentGridWidget.getModel()).thenReturn(parentGridData);
        when(parentGridWidget.getItems(anyInt(), anyInt())).thenReturn(Collections.singletonList(listSelectorItem));
        when(parentGridData.getCell(anyInt(), anyInt())).thenReturn(mock(LiteralExpressionCell.class));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0)).isSameAs(listSelectorItem);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetItemsWithParentThatDoesSupportCellControlsButCellDoesNot() {
        setupGrid(0);

        final GridData parentGridData = mock(GridData.class);
        final ContextGrid parentGridWidget = mock(ContextGrid.class);
        final HasListSelectorControl.ListSelectorItem listSelectorItem = mock(HasListSelectorControl.ListSelectorItem.class);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(parentGridWidget));
        when(parentGridWidget.getModel()).thenReturn(parentGridData);
        when(parentGridWidget.getItems(anyInt(), anyInt())).thenReturn(Collections.singletonList(listSelectorItem));
        when(parentGridData.getCell(anyInt(), anyInt())).thenReturn(mock(BaseGridCell.class));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isEmpty();
    }

    @Test
    public void testGetItemsWithParentThatDoesNotSupportCellControls() {
        setupGrid(0);

        final GridData parentGridData = mock(GridData.class);
        final BaseExpressionGrid parentGridWidget = mock(BaseExpressionGrid.class);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(parentGridWidget));
        when(parentGridWidget.getModel()).thenReturn(parentGridData);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isEmpty();
    }

    @Test
    public void testOnItemSelected() {
        setupGrid(0);

        final Command command = mock(Command.class);
        final HasListSelectorControl.ListSelectorTextItem listSelectorItem = mock(HasListSelectorControl.ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testBodyFactoryWhenNested() {
        setupGrid(1);

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testBodyFactoryWhenNotNested() {
        setupGrid(0);

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testGetDisplayName() {
        setupGrid(0);

        assertThat(extractHeaderMetaData().getName().getValue()).isEqualTo(NAME);
    }

    private LiteralExpressionColumnHeaderMetaData extractHeaderMetaData() {
        final LiteralExpressionColumn column = (LiteralExpressionColumn) grid.getModel().getColumns().get(0);
        return (LiteralExpressionColumnHeaderMetaData) column.getHeaderMetaData().get(0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNoChange() {
        setupGrid(0);

        extractHeaderMetaData().setName(new Name(NAME));

        verify(sessionCommandManager, never()).execute(any(AbstractCanvasHandler.class),
                                                       any(org.kie.workbench.common.stunner.core.command.Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithEmptyValue() {
        setupGrid(0);

        extractHeaderMetaData().setName(new Name());

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               DeleteHasNameCommand.class,
                                               UpdateElementPropertyCommand.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNullValue() {
        setupGrid(0);

        extractHeaderMetaData().setName(null);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               DeleteHasNameCommand.class,
                                               UpdateElementPropertyCommand.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNonEmptyValue() {
        setupGrid(0);

        extractHeaderMetaData().setName(new Name(NAME_NEW));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               SetHasNameCommand.class,
                                               UpdateElementPropertyCommand.class);
    }

    @Test
    public void testGetTypeRef() {
        setupGrid(0);

        assertThat(extractHeaderMetaData().getTypeRef()).isNotNull();
    }

    @Test
    public void testSetTypeRef() {
        setupGrid(0);

        extractHeaderMetaData().setTypeRef(new QName(QName.NULL_NS_URI,
                                                     BuiltInType.DATE.getName()));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(SetTypeRefCommand.class));
    }

    @Test
    public void testSetTypeRefWithoutChange() {
        setupGrid(0);

        extractHeaderMetaData().setTypeRef(new QName());

        verify(sessionCommandManager, never()).execute(any(AbstractCanvasHandler.class),
                                                       any(SetTypeRefCommand.class));
    }

    @Test
    public void testSelectHeader() {
        setupGrid(0);

        grid.selectHeaderCell(0, 0, false, false);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(hasExpression);
    }

    @Test
    public void testAsDMNModelInstrumentedBase() {
        setupGrid(0);

        assertThat(extractHeaderMetaData().asDMNModelInstrumentedBase()).isInstanceOf(hasExpression.getVariable().getClass());
    }
}

