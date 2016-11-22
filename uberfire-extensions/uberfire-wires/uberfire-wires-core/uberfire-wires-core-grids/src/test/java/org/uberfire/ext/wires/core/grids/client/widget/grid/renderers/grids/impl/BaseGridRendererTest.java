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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.Collections;
import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringPopupColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.SelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.BlueTheme;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@WithClassesToStub({ Text.class })
@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridRendererTest {

    @Mock
    private GridColumnRenderer<String> columnRenderer;

    @Mock
    private GridBodyRenderContext context;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Captor
    private ArgumentCaptor<List<GridColumn<?>>> columnsCaptor;

    @Captor
    private ArgumentCaptor<SelectedRange> selectedRangeCaptor;

    private GridData model;

    private GridColumn<String> column;

    private SelectionsTransformer selectionsTransformer;

    private GridRendererTheme theme = new BlueTheme();

    private BaseGridRenderer renderer;

    @Before
    public void setup() {
        final BaseGridRenderer wrapped = new BaseGridRenderer( theme );
        this.renderer = spy( wrapped );

        this.column = new StringPopupColumn( new BaseHeaderMetaData( "title" ),
                                             columnRenderer,
                                             100.0 );

        this.model = new BaseGridData();
        this.model.appendColumn( column );
        this.model.appendRow( new BaseGridRow() );
        this.model.appendRow( new BaseGridRow() );
        this.model.appendRow( new BaseGridRow() );

        this.selectionsTransformer = new DefaultSelectionsTransformer( model,
                                                                       Collections.singletonList( column ) );

        when( context.getBlockColumns() ).thenReturn( Collections.singletonList( column ) );
        when( context.getTransformer() ).thenReturn( selectionsTransformer );
    }

    @Test
    public void checkSelectedCellsClippedByHeader() {
        checkRenderedSelectedCells( 0,
                                    0,
                                    1,
                                    3,
                                    1,
                                    2 );
    }

    @Test
    public void checkSelectedCellsNotClippedByHeader() {
        checkRenderedSelectedCells( 0,
                                    0,
                                    1,
                                    3,
                                    0,
                                    2 );
    }

    private void checkRenderedSelectedCells( final int selectionRowIndex,
                                             final int selectionColumnIndex,
                                             final int selectionColumnCount,
                                             final int selectionRowCount,
                                             final int minVisibleRowIndex,
                                             final int maxVisibleRowIndex ) {
        this.model.selectCells( selectionRowIndex,
                                selectionColumnIndex,
                                selectionColumnCount,
                                selectionRowCount );
        when( context.getMinVisibleRowIndex() ).thenReturn( minVisibleRowIndex );
        when( context.getMaxVisibleRowIndex() ).thenReturn( maxVisibleRowIndex );

        renderer.renderSelectedCells( model,
                                      context,
                                      rendererHelper );

        verify( renderer,
                times( 1 ) ).renderSelectedRange( eq( model ),
                                                  columnsCaptor.capture(),
                                                  eq( selectionColumnIndex ),
                                                  selectedRangeCaptor.capture() );

        final List<GridColumn<?>> columns = columnsCaptor.getValue();
        assertNotNull( columns );
        assertEquals( 1,
                      columns.size() );
        assertEquals( column,
                      columns.get( 0 ) );

        final SelectedRange selectedRange = selectedRangeCaptor.getValue();
        assertNotNull( selectedRange );
        assertEquals( selectionColumnIndex,
                      selectedRange.getUiColumnIndex() );
        assertEquals( minVisibleRowIndex,
                      selectedRange.getUiRowIndex() );
        assertEquals( selectionColumnCount,
                      selectedRange.getWidth() );
        assertEquals( maxVisibleRowIndex - minVisibleRowIndex + 1,
                      selectedRange.getHeight() );
    }

}
