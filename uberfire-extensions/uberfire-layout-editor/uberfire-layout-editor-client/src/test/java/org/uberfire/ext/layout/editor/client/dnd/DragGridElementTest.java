package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.client.util.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DragGridElementTest {

    private GridLayoutDragComponent type;
    private DragGridElement dragGridElement;

    @Before
    public void setup() {
        type = mock( GridLayoutDragComponent.class );
        dragGridElement = new DragGridElement( type );
    }

    @Test
    public void createDragStartExternalComponent() throws Exception {
        DragStartEvent dragStartEvent = mock( DragStartEvent.class );
        when( dragStartEvent.getDataTransfer() ).thenReturn( mock( DataTransfer.class ) );
        when( type.externalLayoutDragComponent() ).thenReturn( true );

        dragGridElement.createDragStart( dragStartEvent, type );

        verify( dragStartEvent ).setData( LayoutDragComponent.class.toString(), type.getClass().getName() );
    }

    @Test
    public void createDragStartInternalComponent() throws Exception {
        DragStartEvent dragStartEvent = mock( DragStartEvent.class );
        when( dragStartEvent.getDataTransfer() ).thenReturn( mock( DataTransfer.class ) );
        when( type.externalLayoutDragComponent() ).thenReturn( false );

        dragGridElement.createDragStart( dragStartEvent, type );

        verify( dragStartEvent ).setData( LayoutDragComponent.INTERNAL_DRAG_COMPONENT, type.label() );
    }

}