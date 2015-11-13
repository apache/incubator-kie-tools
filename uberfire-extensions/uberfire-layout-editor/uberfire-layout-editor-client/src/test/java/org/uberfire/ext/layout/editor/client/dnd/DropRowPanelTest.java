/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;
import org.uberfire.ext.layout.editor.client.components.InternalDragComponent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DropRowPanelTest {

    private LayoutEditorWidget layoutEditorWidget;
    private DropRowPanel dropRowPanel;
    private FlowPanel dropPanel;

    @Before
    public void setup() {
        dropPanel = mock( FlowPanel.class );
        layoutEditorWidget = new LayoutEditorWidget();
        layoutEditorWidget.setup( dropPanel, new LayoutTemplate() );
        dropRowPanel = new DropRowPanel( (layoutEditorWidget) );
    }

    @Test
    public void dropHandlerOfAGridTest() {
        DropEvent event = mock( DropEvent.class );
        String data = DndData.prepareData( InternalDragComponent.INTERNAL_DRAG_COMPONENT, "12" );
        when( event.getData( DndData.FORMAT ) ).thenReturn( data );
        dropRowPanel.dropHandler( event );
        verify( dropPanel ).remove( dropRowPanel );
        //dropped view
        verify( dropPanel, atLeastOnce() ).add( any( RowView.class ) );
        //new drop row
        verify( dropPanel, atLeastOnce() ).add( any( DropRowPanel.class ) );
    }

    @Test
    public void dropHandlerOfWrongComponentTest() {
        DropEvent event = mock( DropEvent.class );
        String data = DndData.prepareData( InternalDragComponent.INTERNAL_DRAG_COMPONENT, "" );

        when( event.getData( DndData.FORMAT ) ).thenReturn( data );
        dropRowPanel.dropHandler( event );
        //nothing happens
        verify( dropPanel, never() ).remove( dropRowPanel );
        verify( dropPanel, never() ).add( any( Widget.class ) );
    }

    @Test
    public void onDragOverShouldCreateABorderAndDragLeaveShouldRemoveTheBorder() {
        DropRowPanel spy = spy( dropRowPanel );
        spy.dragOverHandler();
        verify( spy ).addCSSClass( WebAppResource.INSTANCE.CSS().dropBorder() );
        spy.dragLeaveHandler();
        verify( spy ).removeCSSClass( WebAppResource.INSTANCE.CSS().dropBorder() );
    }

}