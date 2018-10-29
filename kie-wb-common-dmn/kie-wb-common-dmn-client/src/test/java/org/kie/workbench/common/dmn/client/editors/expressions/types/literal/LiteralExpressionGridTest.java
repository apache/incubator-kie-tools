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

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
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
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridData.SelectedCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LiteralExpressionGridTest {

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
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private DMNSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

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
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private NameAndDataTypePopoverView.Presenter headerEditor;

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
                                                           domainObjectSelectionEvent,
                                                           listSelector,
                                                           translationService,
                                                           headerEditor);

        final Decision decision = new Decision();
        decision.setName(new Name(NAME));
        hasName = Optional.of(decision);
        expression = definition.getModelClass();
        expression.ifPresent(e -> e.getText().setValue(EXPRESSION_TEXT));

        doReturn(canvasHandler).when(session).getCanvasHandler();
        doReturn(mock(Bounds.class)).when(gridLayer).getVisibleBounds();

        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(index.get(anyString())).thenReturn(element);
        when(element.getContent()).thenReturn(mock(Definition.class));
        when(definitionUtils.getNameIdentifier(any())).thenReturn("name");
        when(canvasCommandFactory.updatePropertyValue(any(Element.class),
                                                      anyString(),
                                                      any())).thenReturn(mock(UpdateElementPropertyCommand.class));
        when(parentGridWidget.getModel()).thenReturn(parentGridUiModel);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(parent.getRowIndex()).thenReturn(0);
        when(parent.getColumnIndex()).thenReturn(1);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(anyString());
    }

    private void setupGrid(final int nesting) {
        this.grid = spy((LiteralExpressionGrid) definition.getEditor(parent,
                                                                     nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                                     hasExpression,
                                                                     expression,
                                                                     hasName,
                                                                     nesting).get());
    }

    @Test
    public void testSelectFirstCell() {
        setupGrid(0);

        grid.selectFirstCell();

        final List<SelectedCell> selectedCells = grid.getModel().getSelectedCells();
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
    public void testHeaderVisibilityWhenNested() {
        setupGrid(1);

        assertTrue(grid.isHeaderHidden());
    }

    @Test
    public void testHeaderVisibilityWhenNotNested() {
        setupGrid(0);

        assertFalse(grid.isHeaderHidden());
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

        extractHeaderMetaData().setTypeRef(new QName(Namespace.FEEL.getUri(),
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

