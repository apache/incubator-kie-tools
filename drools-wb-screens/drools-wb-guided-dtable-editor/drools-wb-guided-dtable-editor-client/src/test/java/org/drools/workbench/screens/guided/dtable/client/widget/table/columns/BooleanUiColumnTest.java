/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.impl.CheckBoxDOMElementFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BooleanUiColumnTest {

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private GuidedDecisionTablePresenter.Access access;

    @Mock
    private CheckBoxDOMElementFactory factory;

    @Mock
    private GridBodyCellRenderContext context;

    @Mock
    private Consumer<GridCellValue<Boolean>> callback;

    @Captor
    private ArgumentCaptor<BaseGridCellValue<Boolean>> callbackArgumentCaptor;

    private BooleanUiColumn column;

    @Before
    public void setup() {
        final List<GridColumn.HeaderMetaData> metaDataList = new ArrayList<GridColumn.HeaderMetaData>() {{
            add( headerMetaData );
        }};
        this.column = new BooleanUiColumn( metaDataList,
                                           100,
                                           true,
                                           true,
                                           access,
                                           factory );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void editTrueToFalse() {
        when( access.isEditable() ).thenReturn( true );
        final GridCell<Boolean> cell = new BaseGridCell<>( new BaseGridCellValue<>( true ) );

        column.edit( cell,
                     context,
                     callback );

        verify( callback,
                times( 1 ) ).accept( callbackArgumentCaptor.capture() );

        final BaseGridCellValue<Boolean> callbackArgument = callbackArgumentCaptor.getValue();
        assertFalse( callbackArgument.getValue() );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void editFalseToTrue() {
        when( access.isEditable() ).thenReturn( true );
        final GridCell<Boolean> cell = new BaseGridCell<>( new BaseGridCellValue<>( false ) );

        column.edit( cell,
                     context,
                     callback );

        verify( callback,
                times( 1 ) ).accept( callbackArgumentCaptor.capture() );

        final BaseGridCellValue<Boolean> callbackArgument = callbackArgumentCaptor.getValue();
        assertTrue( callbackArgument.getValue() );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void editReadOnly() {
        when( access.isEditable() ).thenReturn( false );
        final GridCell<Boolean> cell = new BaseGridCell<>( new BaseGridCellValue<>( true ) );
        column.edit( cell,
                     context,
                     callback );

        verify( callback,
                never() ).accept( any( BaseGridCellValue.class ) );
    }

}
