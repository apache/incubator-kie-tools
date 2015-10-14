/*
* Copyright 2015 JBoss Inc
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
import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.layout.editor.client.components.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.layout.editor.client.dnd.DndData.prepareData;

public class DndDataTest {

    GridLayoutDragComponent internal;
    LayoutDragComponent external;
    DropEvent internalEvent;
    DropEvent externalEvent;

    private final String INTERNAL_TYPE = "INTERNAL_DRAG_COMPONENT";
    private final String INTERNAL_VALUE = "label";
    private final String INTERNAL_DATA = prepareData( INTERNAL_TYPE, INTERNAL_VALUE );

    private final String EXTERNAL_TYPE = "interface org.uberfire.ext.layout.editor.client.components.LayoutDragComponent";
    private final String EXTERNAL_VALUE = "org.uberfire.ext.layout.editor.client.dnd.DndDataTest$DummyLayoutDragComponent";
    private final String EXTERNAL_DATA = prepareData( EXTERNAL_TYPE, EXTERNAL_VALUE );


    @Before
    public void setup() {
        internal = mock( GridLayoutDragComponent.class );
        when( internal.label() ).thenReturn( "label" );
        external = new DummyLayoutDragComponent();

        internalEvent = mock( DropEvent.class );
        when( internalEvent.getData( DndData.FORMAT ) ).thenReturn( INTERNAL_DATA );

        externalEvent = mock( DropEvent.class );
        when( externalEvent.getData( DndData.FORMAT ) ).thenReturn( EXTERNAL_DATA );
    }

    @Test
    public void testGenerate() throws Exception {
        assertEquals( INTERNAL_DATA, DndData.generateData( internal ) );
        assertEquals( EXTERNAL_DATA, DndData.generateData( external ) );
    }

    @Test
    public void testGetEventType() throws Exception {

        assertEquals( INTERNAL_TYPE, DndData.getEventType( internalEvent ) );
        assertEquals( INTERNAL_VALUE, DndData.getEventData( internalEvent ) );

        assertEquals( EXTERNAL_TYPE, DndData.getEventType( externalEvent ) );
        assertEquals( EXTERNAL_VALUE, DndData.getEventData( externalEvent ) );

    }

    private class DummyLayoutDragComponent implements LayoutDragComponent {

        @Override
        public IsWidget getDragWidget() {
            return null;
        }

        @Override
        public IsWidget getPreviewWidget( RenderingContext ctx ) {
            return null;
        }

        @Override
        public IsWidget getShowWidget( RenderingContext ctx ) {
            return null;
        }
    }

}