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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.KeyCodes;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.StringColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class KeyboardOperationEditGridCellTest {

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private GridRenderer renderer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private TranslationService translationService;

    private KeyboardOperationEditGridCell operation;

    private BaseGridData gridData;

    private BaseGrid<Expression> gridWidget;

    @Before
    public void setUp() throws Exception {
        operation = new KeyboardOperationEditGridCell(gridLayer);

        gridData = new BaseGridData(false);

        gridWidget = spy(new BaseGrid<Expression>(gridLayer,
                                                  gridData,
                                                  renderer,
                                                  sessionManager,
                                                  sessionCommandManager,
                                                  canvasCommandFactory,
                                                  refreshFormPropertiesEvent,
                                                  domainObjectSelectionEvent,
                                                  cellEditorControls,
                                                  translationService) {
            @Override
            public List<ListSelectorItem> getItems(final int uiRowIndex,
                                                   final int uiColumnIndex) {
                return Collections.emptyList();
            }

            @Override
            public void onItemSelected(final ListSelectorItem item) {
                //NOP for tests
            }
        });
    }

    @Test
    public void testReactsOnKey() {
        assertThat(operation.getKeyCode()).isEqualTo(KeyCodes.KEY_ENTER);
    }

    @Test
    public void testNoData() {
        assertThat(operation.isExecutable(gridWidget))
                .as("No rows and columns")
                .isFalse();
    }

    @Test
    public void testMultipleSelectedDataCells() {
        final DMNGridColumn testingDmnColumn = testingDmnColumn();

        gridData.appendColumn(new RowNumberColumn());
        gridData.appendColumn(testingDmnColumn);
        gridData.appendRow(new BaseGridRow());

        gridData.selectCell(0, 0);
        gridData.selectCell(0, 1);

        assertThat(operation.isExecutable(gridWidget))
                .as("Multiple cells can't be selected")
                .isFalse();
    }

    @Test
    public void testValidState() {
        final DMNGridColumn testingDmnColumn = testingDmnColumn();

        gridData.appendColumn(new RowNumberColumn());
        gridData.appendColumn(testingDmnColumn);
        gridData.appendRow(new BaseGridRow());

        gridData.selectCell(0, 1);

        assertThat(operation.isExecutable(gridWidget))
                .as("Possible to edit if one cell is selected")
                .isTrue();
    }

    @Test
    public void testEnterInnerGrid() {
        final DMNGridColumn testingDmnColumn = testingDmnColumn();

        gridData.appendColumn(new RowNumberColumn());
        gridData.appendColumn(testingDmnColumn);
        gridData.appendRow(new BaseGridRow());

        final ExpressionCellValue innerGridCellValue = mock(ExpressionCellValue.class);
        final BaseExpressionGrid innerGrid = mock(BaseExpressionGrid.class);
        when(innerGridCellValue.getValue()).thenReturn(Optional.of(innerGrid));

        gridData.setCellValue(0, 1, innerGridCellValue);
        gridData.selectCell(0, 1);

        doReturn(false).when(gridWidget).startEditingCell(0, 1);

        operation.perform(gridWidget, false, false);

        verify(gridWidget).startEditingCell(0, 1);
        verify(gridLayer).select(innerGrid);
        verify(innerGrid).selectFirstCell();
    }

    private DMNGridColumn<BaseGrid<Expression>, String> testingDmnColumn() {
        return spy(new DMNGridColumn<BaseGrid<Expression>, String>(new BaseHeaderMetaData("column title"),
                                                                   new StringColumnRenderer(),
                                                                   DMNGridColumn.DEFAULT_WIDTH,
                                                                   gridWidget) {
        });
    }
}
