package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.client.components.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.InternalDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

import static org.mockito.Mockito.*;

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

        verify( dragStartEvent ).setData( LayoutDragComponent.class.toString(), externalType.getClass().getName() );
    }

    @Test
    public void createDragStartInternalComponent() throws Exception {
        DragStartEvent dragStartEvent = mock( DragStartEvent.class );
        when( dragStartEvent.getDataTransfer() ).thenReturn( mock( DataTransfer.class ) );

        internalGridElement.createDragStart( dragStartEvent, internalType );

        verify( dragStartEvent ).setData( InternalDragComponent.INTERNAL_DRAG_COMPONENT, internalType.label() );
    }

}