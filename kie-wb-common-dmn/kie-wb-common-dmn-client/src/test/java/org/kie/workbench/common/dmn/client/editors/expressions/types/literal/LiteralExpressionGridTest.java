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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHeaderValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils.assertCommands;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LiteralExpressionGridTest {

    private static final String EXPRESSION_TEXT = "expression";

    private static final String NODE_UUID = "uuid";

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
    private ClientSession session;

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
    private HasExpression hasExpression;

    private Optional<LiteralExpression> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private LiteralExpressionEditorDefinition definition;

    private LiteralExpressionGrid grid;

    @Before
    public void setup() {
        tupleWithoutValue = new GridCellTuple(0, 0, gridWidget);
        tupleWithValue = new GridCellValueTuple<>(0, 0, gridWidget, new BaseGridCellValue<>("value"));

        definition = new LiteralExpressionEditorDefinition(gridPanel,
                                                           gridLayer,
                                                           definitionUtils,
                                                           sessionManager,
                                                           sessionCommandManager,
                                                           canvasCommandFactory,
                                                           cellEditorControls,
                                                           listSelector,
                                                           translationService);

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        hasName = Optional.of(decision);
        expression = definition.getModelClass();
        expression.ifPresent(e -> e.setText(EXPRESSION_TEXT));

        doReturn(session).when(sessionManager).getCurrentSession();
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
    public void testHeaderFactoryWhenNested() {
        setupGrid(1);

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderHasNameTextBoxFactory();
        assertCommands(factory.getHasNoValueCommand().apply(tupleWithoutValue),
                       DeleteHeaderValueCommand.class);
        assertCommands(factory.getHasValueCommand().apply(tupleWithValue),
                       SetHeaderValueCommand.class);
    }

    @Test
    public void testHeaderFactoryWhenNotNested() {
        setupGrid(0);

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderHasNameTextBoxFactory();
        assertCommands(factory.getHasNoValueCommand().apply(tupleWithoutValue),
                       DeleteHeaderValueCommand.class, UpdateElementPropertyCommand.class);
        assertCommands(factory.getHasValueCommand().apply(tupleWithValue),
                       SetHeaderValueCommand.class, UpdateElementPropertyCommand.class);
    }
}

