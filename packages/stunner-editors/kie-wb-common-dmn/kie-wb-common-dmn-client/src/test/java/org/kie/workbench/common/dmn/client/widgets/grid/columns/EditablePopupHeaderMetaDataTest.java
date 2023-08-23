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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Optional;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class EditablePopupHeaderMetaDataTest {

    private static final double ABSOLUTE_CELL_X = 10.0;

    private static final double ABSOLUTE_CELL_Y = 20.0;

    private static final double CELL_WIDTH = 100.0;

    private static final double CELL_HEIGHT = 32.0;

    private static final double CLIP_MIN_Y = 5.0;

    private static final double CLIP_MIN_X = 15.0;

    private static final int ROW_INDEX = 1;

    private static final int COLUMN_INDEX = 2;

    private static final boolean IS_FLOATING = false;

    private static final double RELATIVE_X = 38.0;

    private static final double RELATIVE_Y = 16.0;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private MockEditor editor;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Transform transform;

    @Mock
    private GridRenderer renderer;

    private MockEditableHeaderMetaData header;

    @Before
    public void setup() {
        this.header = new MockEditableHeaderMetaData(cellEditorControls,
                                                     editor,
                                                     gridWidget);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetColumnGroup() {
        header.setColumnGroup("group");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetTitle() {
        header.setTitle("title");
    }

    @Test
    public void testEditWithRelationLocation() {
        final GridBodyCellEditContext context = new GridBodyCellEditContext(ABSOLUTE_CELL_X,
                                                                            ABSOLUTE_CELL_Y,
                                                                            CELL_WIDTH,
                                                                            CELL_HEIGHT,
                                                                            CLIP_MIN_Y,
                                                                            CLIP_MIN_X,
                                                                            ROW_INDEX,
                                                                            COLUMN_INDEX,
                                                                            IS_FLOATING,
                                                                            transform,
                                                                            renderer,
                                                                            Optional.of(new Point2D(RELATIVE_X, RELATIVE_Y)));

        header.edit(context);

        verify(editor).bind(eq(gridWidget),
                            eq(ROW_INDEX),
                            eq(COLUMN_INDEX));
        verify(cellEditorControls).show(eq(editor),
                                        eq((int) RELATIVE_X),
                                        eq((int) RELATIVE_Y));
    }

    @Test
    public void testEditWithNoRelationLocation() {
        final GridBodyCellEditContext context = new GridBodyCellEditContext(ABSOLUTE_CELL_X,
                                                                            ABSOLUTE_CELL_Y,
                                                                            CELL_WIDTH,
                                                                            CELL_HEIGHT,
                                                                            CLIP_MIN_Y,
                                                                            CLIP_MIN_X,
                                                                            ROW_INDEX,
                                                                            COLUMN_INDEX,
                                                                            IS_FLOATING,
                                                                            transform,
                                                                            renderer,
                                                                            Optional.empty());

        header.edit(context);

        verify(editor).bind(eq(gridWidget),
                            eq(ROW_INDEX),
                            eq(COLUMN_INDEX));
        verify(cellEditorControls).show(eq(editor),
                                        eq((int) (ABSOLUTE_CELL_X + CELL_WIDTH / 2)),
                                        eq((int) (ABSOLUTE_CELL_Y + CELL_HEIGHT / 2)));
    }

    @Test
    public void testDestroyResources() {
        header.destroyResources();

        verify(editor).hide();
    }

    @Test
    public void testSupportedEditAction() {
        assertThat(header.getSupportedEditAction()).isEqualTo(GridCellEditAction.SINGLE_CLICK);
    }

    private static class MockEditableHeaderMetaData extends EditablePopupHeaderMetaData<GridWidget, MockEditor> {

        private final GridWidget gridWidget;

        public MockEditableHeaderMetaData(final CellEditorControlsView.Presenter cellEditorControls,
                                          final MockEditor editor,
                                          final GridWidget gridWidget) {
            super(cellEditorControls,
                  editor);
            this.gridWidget = gridWidget;
        }

        @Override
        protected GridWidget getPresenter() {
            return gridWidget;
        }

        @Override
        public String getColumnGroup() {
            return "GROUP";
        }

        @Override
        public String getTitle() {
            return "TITLE";
        }
    }

    private interface MockEditor extends HasCellEditorControls.Editor<GridWidget> {

    }
}
