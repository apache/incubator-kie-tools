/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import java.util.Map;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.project.datamodel.oracle.DropDownData;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellTableDropDownDataValueMapProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PopupDropDownEditCellTest {

    private PopupDropDownEditCell cell;

    private static final String FACT_TYPE = "Fact";
    private static final String FACT_FIELD = "field";

    @Mock
    private AsyncPackageDataModelOracle dmo;

    @Mock
    private CellTableDropDownDataValueMapProvider dropDownManager;

    @GwtMock
    private ListBox listBox;

    @Before
    public void setup() {
        cell = new PopupDropDownEditCell( FACT_TYPE,
                                          FACT_FIELD,
                                          dmo,
                                          dropDownManager,
                                          false );
    }

    @Test
    public void testRender_NoDropDownData() {
        final Cell.Context context = mock( Cell.Context.class );
        final SafeHtmlBuilder safeHtmlBuilder = mock( SafeHtmlBuilder.class );
        cell.render( context,
                     "content",
                     safeHtmlBuilder );

        verify( safeHtmlBuilder,
                never() ).append( any( SafeHtml.class ) );
    }

    @Test
    public void testRender_SimpleDropDownData() {
        final Cell.Context context = mock( Cell.Context.class );
        final SafeHtmlBuilder safeHtmlBuilder = mock( SafeHtmlBuilder.class );

        final String[] data = new String[]{ "one", "two" };
        final DropDownData dd = DropDownData.create( data );

        when( dmo.getEnums( eq( FACT_TYPE ),
                            eq( FACT_FIELD ),
                            any( Map.class ) ) ).thenReturn( dd );
        when( listBox.getItemCount() ).thenReturn( data.length );
        when( listBox.getValue( eq( 0 ) ) ).thenReturn( "one" );
        when( listBox.getValue( eq( 1 ) ) ).thenReturn( "two" );
        when( listBox.getItemText( eq( 0 ) ) ).thenReturn( "one" );
        when( listBox.getItemText( eq( 1 ) ) ).thenReturn( "two" );

        cell.render( context,
                     "one",
                     safeHtmlBuilder );

        final ArgumentCaptor<SafeHtml> captor = ArgumentCaptor.forClass( SafeHtml.class );
        verify( safeHtmlBuilder,
                times( 1 ) ).append( captor.capture() );
        assertEquals( "one",
                      captor.getValue().asString() );
    }

    @Test
    public void testRender_LookupDropDownData() {
        final Cell.Context context = mock( Cell.Context.class );
        final SafeHtmlBuilder safeHtmlBuilder = mock( SafeHtmlBuilder.class );

        final String[] data = new String[]{ "1=one", "2=two" };
        final DropDownData dd = DropDownData.create( data );

        when( dmo.getEnums( eq( FACT_TYPE ),
                            eq( FACT_FIELD ),
                            any( Map.class ) ) ).thenReturn( dd );
        when( listBox.getItemCount() ).thenReturn( data.length );
        when( listBox.getValue( eq( 0 ) ) ).thenReturn( "1" );
        when( listBox.getValue( eq( 1 ) ) ).thenReturn( "2" );
        when( listBox.getItemText( eq( 0 ) ) ).thenReturn( "one" );
        when( listBox.getItemText( eq( 1 ) ) ).thenReturn( "two" );

        cell.render( context,
                     "1",
                     safeHtmlBuilder );

        final ArgumentCaptor<SafeHtml> captor = ArgumentCaptor.forClass( SafeHtml.class );
        verify( safeHtmlBuilder,
                times( 1 ) ).append( captor.capture() );
        assertEquals( "one",
                      captor.getValue().asString() );
    }

}
