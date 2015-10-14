package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.client.components.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

import static org.mockito.Mockito.*;
import static org.uberfire.ext.layout.editor.client.dnd.DndData.prepareData;

@RunWith(GwtMockitoTestRunner.class)
public class DragGridElementTest {

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

        externalGridElement.createDragStart( dragStartEvent, externalType );

        String data = prepareData( LayoutDragComponent.class.toString(), externalType.getClass().getName() );
        verify( dragStartEvent ).setData( DndData.FORMAT, data );
    }

    @Test
    public void createDragStartInternalComponent() throws Exception {
        String data = prepareData( GridLayoutDragComponent.INTERNAL_DRAG_COMPONENT, "label" );
        DragStartEvent dragStartEvent = mock( DragStartEvent.class );
        when( internalType.label() ).thenReturn( "label" );

        final DataTransfer mock = mock( DataTransfer.class );
        when( mock.getData( DndData.FORMAT ) ).thenReturn( data );

        when( dragStartEvent.getDataTransfer() ).thenReturn( mock );

        internalGridElement.createDragStart( dragStartEvent, internalType );

        verify( dragStartEvent ).setData( DndData.FORMAT, data );
    }

}