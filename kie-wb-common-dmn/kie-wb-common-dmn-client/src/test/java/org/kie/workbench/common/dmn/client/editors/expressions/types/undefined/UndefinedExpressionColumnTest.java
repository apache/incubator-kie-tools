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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorTextItem;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridCell;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class UndefinedExpressionColumnTest {

    private static final int ROW_INDEX = 1;

    private static final int COLUMN_INDEX = 2;

    private static final double ABSOLUTE_CELL_X = 10.0;

    private static final double ABSOLUTE_CELL_Y = 20.0;

    private static final double RX = 37.0;

    private static final double RY = 42.0;

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private UndefinedExpressionGrid gridWidget;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridData;

    @Mock
    private GridColumn parentGridColumn;

    @Mock
    private GridCellTuple parent;

    @Mock
    private GridBodyCellEditContext context;

    @Mock
    private Consumer<GridCellValue<String>> callback;

    @Mock
    private Point2D relativeLocation;

    private LiteralExpression literalExpression = new LiteralExpression();

    private UndefinedExpressionColumn column;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.column = spy(new UndefinedExpressionColumn(headerMetaData,
                                                        gridWidget,
                                                        cellEditorControls,
                                                        expressionEditorDefinitionsSupplier));

        doReturn(parent).when(gridWidget).getParentInformation();
        doReturn(parentGridWidget).when(parent).getGridWidget();
        doReturn(parentGridData).when(parentGridWidget).getModel();
        doReturn(Collections.singletonList(parentGridColumn)).when(parentGridData).getColumns();

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        doReturn(ExpressionType.LITERAL_EXPRESSION).when(literalExpressionEditorDefinition).getType();
        doReturn(LiteralExpression.class.getSimpleName()).when(literalExpressionEditorDefinition).getName();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();

        when(context.getRowIndex()).thenReturn(ROW_INDEX);
        when(context.getColumnIndex()).thenReturn(COLUMN_INDEX);
        when(context.getRelativeLocation()).thenReturn(Optional.of(relativeLocation));
        when(context.getAbsoluteCellX()).thenReturn(ABSOLUTE_CELL_X);
        when(context.getAbsoluteCellY()).thenReturn(ABSOLUTE_CELL_Y);
    }

    @Test
    public void testRenderer() {
        assertThat(column.getColumnRenderer()).isInstanceOf(UndefinedExpressionColumnRenderer.class);
    }

    @Test
    public void testSetWidth() {
        column.setWidth(200.0);

        assertThat(column.getWidth()).isEqualTo(200.0);
        verify(column).updateWidthOfPeers();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditWithNullCell() {
        final GridCell<String> cell = null;

        column.edit(cell,
                    context,
                    callback);

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class), any(Optional.class), anyInt(), anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditWithNullCellValue() {
        final GridCell<String> cell = mock(GridCell.class);
        when(cell.getValue()).thenReturn(null);

        column.edit(cell,
                    context,
                    callback);

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class), any(Optional.class), anyInt(), anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditWithoutDefinedCellEditor() {
        final DMNGridCell cell = mock(DMNGridCell.class);
        when(cell.getEditor()).thenReturn(Optional.empty());

        column.edit(cell,
                    context,
                    callback);

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class), any(Optional.class), anyInt(), anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditWithDefinedCellEditor() {
        final DMNGridCell cell = mock(DMNGridCell.class);
        final HasCellEditorControls.Editor editor = mock(HasCellEditorControls.Editor.class);
        when(cell.getEditor()).thenReturn(Optional.of(editor));

        column.edit(cell,
                    context,
                    callback);

        verify(editor).bind(eq(column),
                            eq(ROW_INDEX),
                            eq(COLUMN_INDEX));

        verify(cellEditorControls).show(eq(editor), any(Optional.class), eq(0), eq(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditWithDefinedCellEditorWithRelativeLocation() {
        final DMNGridCell cell = mock(DMNGridCell.class);
        final HasCellEditorControls.Editor editor = mock(HasCellEditorControls.Editor.class);
        when(cell.getEditor()).thenReturn(Optional.of(editor));
        when(relativeLocation.getX()).thenReturn(RX);
        when(relativeLocation.getY()).thenReturn(RY);

        column.edit(cell,
                    context,
                    callback);

        verify(editor).bind(eq(column),
                            eq(ROW_INDEX),
                            eq(COLUMN_INDEX));

        verify(cellEditorControls).show(eq(editor),
                                        any(Optional.class),
                                        eq((int) (RX)),
                                        eq((int) (RY)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditWithDefinedCellEditorWithoutRelativeLocation() {
        final DMNGridCell cell = mock(DMNGridCell.class);
        final HasCellEditorControls.Editor editor = mock(HasCellEditorControls.Editor.class);
        when(cell.getEditor()).thenReturn(Optional.of(editor));
        when(context.getRelativeLocation()).thenReturn(Optional.empty());

        column.edit(cell,
                    context,
                    callback);

        verify(editor).bind(eq(column),
                            eq(ROW_INDEX),
                            eq(COLUMN_INDEX));

        verify(cellEditorControls).show(eq(editor),
                                        any(Optional.class),
                                        eq((int) (ABSOLUTE_CELL_X)),
                                        eq((int) (ABSOLUTE_CELL_Y)));
    }

    @Test
    public void testGetItems() {
        final List<ListSelectorItem> items = column.getItems(0, 0);

        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(1);

        final ListSelectorItem item = items.get(0);

        assertThat(item).isInstanceOf(ListSelectorTextItem.class);

        final ListSelectorTextItem textItem = (ListSelectorTextItem) item;

        assertThat(textItem.getText()).isEqualTo(LiteralExpression.class.getSimpleName());

        textItem.getCommand().execute();

        verify(gridWidget).onExpressionTypeChanged(eq(ExpressionType.LITERAL_EXPRESSION));
    }

    @Test
    public void testOnItemSelectedWithExpressionTypeSelected() {
        final List<ListSelectorItem> items = column.getItems(0, 0);
        final ListSelectorTextItem ti = (ListSelectorTextItem) items.get(0);

        column.onItemSelected(ti);

        verify(gridWidget).onExpressionTypeChanged(eq(ExpressionType.LITERAL_EXPRESSION));
    }
}
