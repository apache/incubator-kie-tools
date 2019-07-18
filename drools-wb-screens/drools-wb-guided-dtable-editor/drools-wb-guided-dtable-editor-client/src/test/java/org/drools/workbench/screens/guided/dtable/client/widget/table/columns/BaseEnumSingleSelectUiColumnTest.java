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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxStringSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxStringSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseEnumSingleSelectUiColumnTest {

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private GuidedDecisionTablePresenter.Access access;

    @Mock
    private ListBoxStringSingletonDOMElementFactory multiValueFactory;

    @Mock
    private TextBoxStringSingletonDOMElementFactory singleValueFactory;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private GuidedDecisionTableView view;

    @Mock
    private GridData uiModel;

    @Mock
    private GridBodyCellRenderContext context;

    @Mock
    private GridRenderer renderer;

    @Mock
    private GridRendererTheme theme;

    @Mock
    private Text bodyText;

    @Mock
    private Node bodyTextNode;

    @Mock
    private Callback<GridCellValue<String>> callback;

    @Captor
    private ArgumentCaptor<BaseGridCellValue<Boolean>> callbackArgumentCaptor;

    private EnumSingleSelectStringUiColumn column;

    @Before
    public void setup() {
        final List<GridColumn.HeaderMetaData> metaDataList = new ArrayList<GridColumn.HeaderMetaData>() {{
            add( headerMetaData );
        }};

        this.column = new EnumSingleSelectStringUiColumn( metaDataList,
                                                          100,
                                                          true,
                                                          true,
                                                          access,
                                                          multiValueFactory,
                                                          singleValueFactory,
                                                          presenter,
                                                          "FactType",
                                                          "FactField" );
        when( presenter.getView() ).thenReturn( view );
        when( view.getModel() ).thenReturn( uiModel );
        when( context.getRenderer() ).thenReturn( renderer );
        when( context.getCellWidth() ).thenReturn( 100.0 );
        when( context.getCellHeight() ).thenReturn( 32.0 );
        when( renderer.getTheme() ).thenReturn( theme );
        when( theme.getBodyText() ).thenReturn( bodyText );
        when( bodyText.setListening( anyBoolean() ) ).thenReturn( bodyText );
        when( bodyText.setX( anyDouble() ) ).thenReturn( bodyText );
        when( bodyText.setY( anyDouble() ) ).thenReturn( bodyText );
        when( bodyText.asNode() ).thenReturn( bodyTextNode );
    }

    @Test
    public void renderCellWhenCellValueIsInEnumData() {
        setupEnums( "A",
                    "A" );
        when( access.isEditable() ).thenReturn( true );

        final GridCell<String> cell = new BaseGridCell<>( new GuidedDecisionTableUiCell<>( "A" ) );

        column.getColumnRenderer().renderCell( cell,
                                               context );

        verify( bodyText,
                times( 1 ) ).setText( eq( "A" ) );
        verify( uiModel,
                never() ).deleteCell( anyInt(),
                                      anyInt() );
    }

    @Test
    public void clearModelWhenCellValueIsNotInEnumData() {
        setupEnums( "B",
                    "A" );
        when( access.isEditable() ).thenReturn( true );

        final GridCell<String> cell = new BaseGridCell<>( new GuidedDecisionTableUiCell<>( "B" ) );

        column.getColumnRenderer().renderCell( cell,
                                               context );

        verify( bodyText,
                times( 1 ) ).setText( eq( "" ) );
        verify( uiModel,
                times( 1 ) ).deleteCell( anyInt(),
                                         anyInt() );
    }

    @SuppressWarnings("unchecked")
    private void setupEnums( final String cellValue,
                             final String... values ) {
        final Map<String, String> enums = new HashMap<>();
        for ( String value : values ) {
            enums.put( value, value );
        }
        doAnswer( ( InvocationOnMock invocation ) -> {
            final Callback<Map<String, String>> callback = (Callback<Map<String, String>>) invocation.getArguments()[ 3 ];
            callback.callback( enums );
            return null;
        } ).when( presenter ).getEnumLookups( anyString(),
                                              anyString(),
                                              any( DependentEnumsUtilities.Context.class ),
                                              any( Callback.class ) );
        when( multiValueFactory.convert( eq( cellValue ) ) ).thenReturn( cellValue );
    }

}
