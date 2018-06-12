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

package org.drools.workbench.screens.enums.client.editor;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class EnumEditorTextCellTest {

    private Cell.Context context;

    @Mock
    private Element parent;

    @Mock
    private NativeEvent event;

    @Mock
    private ValueUpdater<String> valueUpdater;

    @Captor
    private ArgumentCaptor<SafeHtml> safeHtmlArgumentCaptor;

    @Spy
    private EnumEditTextCell cell;

    @Test
    public void testOnBrowserEventEnabled() {
        context = new Cell.Context( 0,
                                    0,
                                    new EnumRow( "Fact",
                                                 "field",
                                                 "['a', 'b']" ) );
        cell.onBrowserEvent( context,
                             parent,
                             "Fact",
                             event,
                             valueUpdater );
        verify( cell,
                times( 1 ) ).doOnBrowserEvent( eq( context ),
                                               eq( parent ),
                                               eq( "Fact" ),
                                               eq( event ),
                                               eq( valueUpdater ) );
    }

    @Test
    public void testOnBrowserEventDisabled() {
        context = new Cell.Context( 0,
                                    0,
                                    new EnumRow( "A raw value" ) );
        cell.onBrowserEvent( context,
                             parent,
                             "Fact",
                             event,
                             valueUpdater );
        verify( cell,
                never() ).doOnBrowserEvent( eq( context ),
                                            eq( parent ),
                                            eq( "Fact" ),
                                            eq( event ),
                                            eq( valueUpdater ) );
    }

    @Test
    public void testRenderEnabled() {
        context = new Cell.Context( 0,
                                    0,
                                    new EnumRow( "Fact",
                                                 "field",
                                                 "['a', 'b']" ) );
        final SafeHtmlBuilder sb = mock( SafeHtmlBuilder.class );
        cell.render( context,
                     "Fact",
                     sb );
        verify( cell,
                times( 1 ) ).doRender( eq( context ),
                                       eq( "Fact" ),
                                       eq( sb ) );
    }

    @Test
    public void testRenderDisabled() {
        context = new Cell.Context( 0,
                                    0,
                                    new EnumRow( "A raw value" ) );
        final SafeHtmlBuilder safeHtmlBuilder = mock( SafeHtmlBuilder.class );
        cell.render( context,
                     "Fact",
                     safeHtmlBuilder );
        verify( cell,
                never() ).doRender( eq( context ),
                                    eq( "Fact" ),
                                    eq( safeHtmlBuilder ) );
        verify( safeHtmlBuilder,
                times( 1 ) ).append( safeHtmlArgumentCaptor.capture() );

        final SafeHtml safeHtml = safeHtmlArgumentCaptor.getValue();
        assertEquals( "cellContent(disabled, invalidDefinitionDisabled, Fact)",
                      safeHtml.asString() );
    }

}
