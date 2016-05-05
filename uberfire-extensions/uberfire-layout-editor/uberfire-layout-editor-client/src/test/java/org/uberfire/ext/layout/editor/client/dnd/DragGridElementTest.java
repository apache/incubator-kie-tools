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

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.client.components.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.dnd.mocks.DndDataJSONConverterMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DragGridElementTest {

    public static final String SPAN = "12";

    private GridLayoutDragComponent internalType;
    private LayoutDragComponent externalType;
    private DragGridElement internalGridElement;
    private DragGridElement externalGridElement;

    @Before
    public void setup() {
        internalType = mock( GridLayoutDragComponent.class );
        externalType = mock( LayoutDragComponent.class );
        internalGridElement = new DragGridElement( internalType );
        externalGridElement = new DragGridElement( externalType );
    }

    @Test
    public void createDragStartExternalComponent() throws Exception {
        DragStartEvent dragStartEvent = mock( DragStartEvent.class );
        when( dragStartEvent.getDataTransfer() ).thenReturn( mock( DataTransfer.class ) );

        DndDataJSONConverterMock converter = new DndDataJSONConverterMock( );

        externalGridElement.setConverter( converter );

        externalGridElement.createDragStart( dragStartEvent, externalType );

        String data = converter.generateDragComponentJSON( externalType );

        verify( dragStartEvent ).setData( LayoutDragComponent.FORMAT, data );
    }

    @Test
    public void createDragStartInternalComponent() throws Exception {
        DragStartEvent dragStartEvent = mock( DragStartEvent.class );
        when( dragStartEvent.getDataTransfer() ).thenReturn( mock( DataTransfer.class ) );

        when( internalType.getSpan() ).thenReturn( SPAN );
        when( internalType.getSettingsKeys() ).thenReturn( new String[]{GridLayoutDragComponent.SPAN} );
        when( internalType.getSettingValue( GridLayoutDragComponent.SPAN ) ).thenReturn( SPAN );

        DndDataJSONConverterMock converter = new DndDataJSONConverterMock( );

        String data = converter.generateDragComponentJSON( internalType );

        internalGridElement.setConverter( converter );

        internalGridElement.createDragStart( dragStartEvent, internalType );

        verify( dragStartEvent ).setData( LayoutDragComponent.FORMAT, data );
    }

}
