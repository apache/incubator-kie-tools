/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridTest;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringDOMElementColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringDOMElementSingletonColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.BaseCellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridWidgetKeyboardHandlerTest {

    @Mock
    private DefaultGridLayer layer;

    @Mock
    private KeyDownEvent event;

    @Mock
    private GridWidget gridWidget1;

    @Mock
    private GridWidget gridWidget2;

    @Mock
    private GridRenderer gridWidget1Renderer;

    @Mock
    private BaseGridRendererHelper gridWidget1RendererHelper;

    @Mock
    private GridLienzoPanel gridPanel;

    private GridData gridWidget1Data;

    private Bounds visibleBounds = new BaseBounds(-1000,
                                                  -1000,
                                                  2000,
                                                  2000);

    private Set<GridWidget> gridWidgets = new HashSet<>();

    private BaseCellSelectionManager cellSelectionManager;

    private KeyboardOperation keyboardOperationClearCell;
    private KeyboardOperation keyboardOperationEditCell;
    private KeyboardOperation keyboardOperationMoveLeft;
    private KeyboardOperation keyboardOperationMoveRight;
    private KeyboardOperation keyboardOperationMoveUp;
    private KeyboardOperation keyboardOperationMoveDown;
    private KeyboardOperation keyboardOperationSelectTopLeftCell;
    private KeyboardOperation keyboardOperationSelectBottomRightCell;

    private BaseGridWidgetKeyboardHandler handler;

    @Before
    public void setup() {
        gridWidgets.add(gridWidget1);
        gridWidgets.add(gridWidget2);

        gridWidget1Data = new BaseGridData();
        gridWidget1Data.appendRow(new BaseGridRow());
        gridWidget1Data.appendRow(new BaseGridRow());
        gridWidget1Data.appendColumn(new BaseGridTest.MockMergableGridColumn<>("col1",
                                                                               100));
        gridWidget1Data.appendColumn(new BaseGridTest.MockMergableGridColumn<>("col2",
                                                                               100));

        when(gridWidget1.getModel()).thenReturn(gridWidget1Data);
        when(gridWidget1.getRenderer()).thenReturn(gridWidget1Renderer);
        when(gridWidget1.getRendererHelper()).thenReturn(gridWidget1RendererHelper);
        when(gridWidget1.adjustSelection(any(SelectionExtension.class),
                                         any(Boolean.class))).thenAnswer((InvocationOnMock invocation) -> {
            final SelectionExtension direction = (SelectionExtension) invocation.getArguments()[0];
            final boolean isShiftKeyDown = (Boolean) invocation.getArguments()[1];
            return cellSelectionManager.adjustSelection(direction,
                                                        isShiftKeyDown);
        });
        when(gridWidget1Renderer.getHeaderHeight()).thenReturn(32.0);
        when(layer.getVisibleBounds()).thenReturn(visibleBounds);
        when(event.isLeftArrow()).thenCallRealMethod();
        when(event.isRightArrow()).thenCallRealMethod();
        when(event.isUpArrow()).thenCallRealMethod();
        when(event.isDownArrow()).thenCallRealMethod();

        cellSelectionManager = new BaseCellSelectionManager(gridWidget1);

        final BaseGridWidgetKeyboardHandler wrapped = new BaseGridWidgetKeyboardHandler(layer);
        handler = spy(wrapped);
        setupKeyboardOperations();
    }

    @Test
    public void testDestroyResourcesOnKeyDown() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_RIGHT);

        final StringDOMElementColumn columnWithAdditionalDomElements = mock(StringDOMElementColumn.class);
        gridWidget1Data.appendColumn(columnWithAdditionalDomElements);

        handler.onKeyDown(event);

        verify(columnWithAdditionalDomElements).destroyResources();
    }

    @Test
    public void testDestroyResourcesAndFlushOnKeyDown() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_RIGHT);

        final StringDOMElementSingletonColumn columnWithAdditionalDomElements = mock(StringDOMElementSingletonColumn.class);
        gridWidget1Data.appendColumn(columnWithAdditionalDomElements);

        handler.onKeyDown(event);

        verify(columnWithAdditionalDomElements).flush();
        verify(columnWithAdditionalDomElements).destroyResources();
    }

    @Test
    public void noSelectedDecisionTable() {
        handler.onKeyDown(event);

        verifyNoOperationsInvoked();
    }

    @Test
    public void unhandledKeysInvokeNoAction() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_A);

        handler.onKeyDown(event);

        verifyNoOperationsInvoked();
    }

    private void verifyNoOperationsInvoked() {
        verify(keyboardOperationClearCell,
               never()).perform(any(GridWidget.class),
                                any(Boolean.class),
                                any(Boolean.class));
        verify(keyboardOperationEditCell,
               never()).perform(any(GridWidget.class),
                                any(Boolean.class),
                                any(Boolean.class));
        verify(keyboardOperationMoveLeft,
               never()).perform(any(GridWidget.class),
                                any(Boolean.class),
                                any(Boolean.class));
        verify(keyboardOperationMoveRight,
               never()).perform(any(GridWidget.class),
                                any(Boolean.class),
                                any(Boolean.class));
        verify(keyboardOperationMoveUp,
               never()).perform(any(GridWidget.class),
                                any(Boolean.class),
                                any(Boolean.class));
        verify(keyboardOperationMoveDown,
               never()).perform(any(GridWidget.class),
                                any(Boolean.class),
                                any(Boolean.class));
        verify(keyboardOperationSelectTopLeftCell,
               never()).perform(any(GridWidget.class),
                                any(Boolean.class),
                                any(Boolean.class));
        verify(keyboardOperationSelectBottomRightCell,
               never()).perform(any(GridWidget.class),
                                any(Boolean.class),
                                any(Boolean.class));
    }

    @Test
    public void deleteKeyClearsCells() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_DELETE);

        gridWidget1Data.setCellValue(0,
                                     0,
                                     new BaseGridCellValue<>("hello"));
        assertEquals("hello",
                     gridWidget1Data.getCell(0,
                                             0).getValue().getValue());

        gridWidget1Data.selectCell(0,
                                   0);

        handler.onKeyDown(event);

        verify(keyboardOperationClearCell,
               times(1)).perform(eq(gridWidget1),
                                 eq(false),
                                 eq(false));
        assertNull(gridWidget1Data.getCell(0,
                                           0));
        verify(layer,
               times(1)).draw();
    }

    @Test
    public void enterKeyEditsCells() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ENTER);

        gridWidget1Data.selectCell(0,
                                   0);

        handler.onKeyDown(event);

        verify(gridWidget1,
               times(1)).startEditingCell(eq(0),
                                          eq(0));
        verify(layer,
               never()).draw();
        verify(keyboardOperationEditCell,
               times(1)).perform(eq(gridWidget1),
                                 eq(false),
                                 eq(false));
    }

    @Test
    public void homeKeyMovesToTopLeft() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(0),
                                    eq(0),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_HOME);

        handler.onKeyDown(event);

        verify(gridWidget1,
               times(1)).selectCell(eq(0),
                                    eq(0),
                                    eq(false),
                                    eq(false));
        verify(layer,
               times(1)).draw();
        verify(keyboardOperationSelectTopLeftCell,
               times(1)).perform(eq(gridWidget1),
                                 eq(false),
                                 eq(false));
    }

    @Test
    public void endKeyMovesToBottomRight() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(1),
                                    eq(1),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_END);

        handler.onKeyDown(event);

        verify(gridWidget1,
               times(1)).selectCell(eq(1),
                                    eq(1),
                                    eq(false),
                                    eq(false));
        verify(layer,
               times(1)).draw();
        verify(keyboardOperationSelectBottomRightCell,
               times(1)).perform(eq(gridWidget1),
                                 eq(false),
                                 eq(false));
    }

    @Test
    public void leftCursorWithoutShiftKeyMovesLeftOneCell() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(0),
                                    eq(0),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_LEFT);

        gridWidget1.getModel().selectCell(0,
                                          1);

        handler.onKeyDown(event);

        verify(layer,
               times(1)).draw();
        verify(keyboardOperationMoveLeft,
               times(1)).perform(eq(gridWidget1),
                                 eq(false),
                                 eq(false));
        final List<GridData.SelectedCell> selectedCells = gridWidget1Data.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertEquals(0,
                     selectedCells.get(0).getRowIndex());
        assertEquals(0,
                     selectedCells.get(0).getColumnIndex());
    }

    @Test
    public void leftCursorWithShiftKeyExtendsRangeLeftOneCell() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(0),
                                    eq(0),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_LEFT);
        when(event.isShiftKeyDown()).thenReturn(true);

        gridWidget1.getModel().selectCell(0,
                                          1);

        handler.onKeyDown(event);

        verify(layer,
               times(1)).draw();
        verify(keyboardOperationMoveLeft,
               times(1)).perform(eq(gridWidget1),
                                 eq(true),
                                 eq(false));
        final List<GridData.SelectedCell> selectedCells = gridWidget1Data.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertEquals(0,
                     selectedCells.get(0).getRowIndex());
        assertEquals(1,
                     selectedCells.get(0).getColumnIndex());
        assertEquals(0,
                     selectedCells.get(1).getRowIndex());
        assertEquals(0,
                     selectedCells.get(1).getColumnIndex());
    }

    @Test
    public void rightCursorWithoutShiftKeyMovesRightOneCell() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(0),
                                    eq(1),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_RIGHT);

        gridWidget1.getModel().selectCell(0,
                                          0);

        handler.onKeyDown(event);

        verify(layer,
               times(1)).draw();
        verify(keyboardOperationMoveRight,
               times(1)).perform(eq(gridWidget1),
                                 eq(false),
                                 eq(false));
        final List<GridData.SelectedCell> selectedCells = gridWidget1Data.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertEquals(0,
                     selectedCells.get(0).getRowIndex());
        assertEquals(1,
                     selectedCells.get(0).getColumnIndex());
    }

    @Test
    public void rightCursorWithShiftKeyExtendsRangeRightOneCell() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(0),
                                    eq(1),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_RIGHT);
        when(event.isShiftKeyDown()).thenReturn(true);

        gridWidget1.getModel().selectCell(0,
                                          0);

        handler.onKeyDown(event);

        verify(layer,
               times(1)).draw();
        verify(keyboardOperationMoveRight,
               times(1)).perform(eq(gridWidget1),
                                 eq(true),
                                 eq(false));
        final List<GridData.SelectedCell> selectedCells = gridWidget1Data.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertEquals(0,
                     selectedCells.get(0).getRowIndex());
        assertEquals(0,
                     selectedCells.get(0).getColumnIndex());
        assertEquals(0,
                     selectedCells.get(1).getRowIndex());
        assertEquals(1,
                     selectedCells.get(1).getColumnIndex());
    }

    @Test
    public void upCursorWithoutShiftKeyMovesUpOneCell() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(0),
                                    eq(0),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_UP);

        gridWidget1.getModel().selectCell(1,
                                          0);

        handler.onKeyDown(event);

        verify(layer,
               times(1)).draw();
        verify(keyboardOperationMoveUp,
               times(1)).perform(eq(gridWidget1),
                                 eq(false),
                                 eq(false));
        final List<GridData.SelectedCell> selectedCells = gridWidget1Data.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertEquals(0,
                     selectedCells.get(0).getRowIndex());
        assertEquals(0,
                     selectedCells.get(0).getColumnIndex());
    }

    @Test
    public void upCursorWithShiftKeyExtendsRangeUpOneCell() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(0),
                                    eq(0),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_UP);
        when(event.isShiftKeyDown()).thenReturn(true);

        gridWidget1.getModel().selectCell(1,
                                          0);

        handler.onKeyDown(event);

        verify(layer,
               times(1)).draw();
        verify(keyboardOperationMoveUp,
               times(1)).perform(eq(gridWidget1),
                                 eq(true),
                                 eq(false));
        final List<GridData.SelectedCell> selectedCells = gridWidget1Data.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertEquals(1,
                     selectedCells.get(0).getRowIndex());
        assertEquals(0,
                     selectedCells.get(0).getColumnIndex());
        assertEquals(0,
                     selectedCells.get(1).getRowIndex());
        assertEquals(0,
                     selectedCells.get(1).getColumnIndex());
    }

    @Test
    public void downCursorWithoutShiftKeyMovesDownOneCell() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(1),
                                    eq(0),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_DOWN);

        gridWidget1.getModel().selectCell(0,
                                          0);

        handler.onKeyDown(event);

        verify(layer,
               times(1)).draw();
        verify(keyboardOperationMoveDown,
               times(1)).perform(eq(gridWidget1),
                                 eq(false),
                                 eq(false));
        final List<GridData.SelectedCell> selectedCells = gridWidget1Data.getSelectedCells();
        assertEquals(1,
                     selectedCells.size());
        assertEquals(1,
                     selectedCells.get(0).getRowIndex());
        assertEquals(0,
                     selectedCells.get(0).getColumnIndex());
    }

    @Test
    public void downCursorWithShiftKeyExtendsRangeDownOneCell() {
        when(layer.getGridWidgets()).thenReturn(gridWidgets);
        when(gridWidget1.isSelected()).thenReturn(true);
        when(gridWidget1.selectCell(eq(1),
                                    eq(0),
                                    any(Boolean.class),
                                    any(Boolean.class))).thenReturn(true);
        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_DOWN);
        when(event.isShiftKeyDown()).thenReturn(true);

        gridWidget1.getModel().selectCell(0,
                                          0);

        handler.onKeyDown(event);

        verify(layer,
               times(1)).draw();
        verify(keyboardOperationMoveDown,
               times(1)).perform(eq(gridWidget1),
                                 eq(true),
                                 eq(false));
        final List<GridData.SelectedCell> selectedCells = gridWidget1Data.getSelectedCells();
        assertEquals(2,
                     selectedCells.size());
        assertEquals(0,
                     selectedCells.get(0).getRowIndex());
        assertEquals(0,
                     selectedCells.get(0).getColumnIndex());
        assertEquals(1,
                     selectedCells.get(1).getRowIndex());
        assertEquals(0,
                     selectedCells.get(1).getColumnIndex());
    }

    private void setupKeyboardOperations() {
        this.keyboardOperationClearCell = spy(new KeyboardOperationClearCell(layer));
        this.keyboardOperationEditCell = spy(new KeyboardOperationEditCell(layer));
        this.keyboardOperationMoveLeft = spy(new KeyboardOperationMoveLeft(layer, gridPanel));
        this.keyboardOperationMoveRight = spy(new KeyboardOperationMoveRight(layer, gridPanel));
        this.keyboardOperationMoveUp = spy(new KeyboardOperationMoveUp(layer, gridPanel));
        this.keyboardOperationMoveDown = spy(new KeyboardOperationMoveDown(layer, gridPanel));
        this.keyboardOperationSelectTopLeftCell = spy(new KeyboardOperationSelectTopLeftCell(layer));
        this.keyboardOperationSelectBottomRightCell = spy(new KeyboardOperationSelectBottomRightCell(layer));

        handler.addOperation(keyboardOperationClearCell,
                             keyboardOperationEditCell,
                             keyboardOperationMoveLeft,
                             keyboardOperationMoveRight,
                             keyboardOperationMoveUp,
                             keyboardOperationMoveDown,
                             keyboardOperationSelectTopLeftCell,
                             keyboardOperationSelectBottomRightCell);
    }
}
