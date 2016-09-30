package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.event.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( LienzoMockitoTestRunner.class )
public class WiresShapeTest {

    private WiresShape tested;
    private MultiPath path;

    @Mock  LayoutContainer layoutContainer;
    @Mock HandlerRegistrationManager handlerRegistrationManager;
    @Mock IAttributesChangedBatcher attributesChangedBatcher;
    @Mock HandlerManager handlerManager;

    @Before
    public void setup() {
        path = new MultiPath().rect( 0, 0, 100, 100 );
        Group group = new Group();
        when( layoutContainer.getGroup() ).thenReturn( group );
        when( layoutContainer.setOffset( any( Point2D.class ) ) ).thenReturn( layoutContainer );
        when( layoutContainer.setSize( anyDouble(), anyDouble() ) ).thenReturn( layoutContainer );
        when( layoutContainer.execute() ).thenReturn( layoutContainer );
        when( layoutContainer.refresh() ).thenReturn( layoutContainer );
        tested = new WiresShape( path, layoutContainer, handlerManager, handlerRegistrationManager, attributesChangedBatcher );
    }

    @Test
    public void testInit() {
        assertNull( tested.getParent() );
        assertNull( tested.getDockedTo() );
        assertEquals( IContainmentAcceptor.ALL, tested.getContainmentAcceptor() );
        assertEquals( IDockingAcceptor.ALL, tested.getDockingAcceptor() );
        assertEquals( path, tested.getPath() );
        assertEquals( 0, tested.getChildShapes().size() );
        verify( layoutContainer, times( 1 ) ).setOffset( any( Point2D.class ) );
        verify( layoutContainer, times( 1 ) ).setSize( anyDouble(), anyDouble() );
        verify( layoutContainer, times( 1 ) ).execute();
        verify( layoutContainer, times( 0 ) ).refresh();
    }

    @Test
    public void testXCoordinate() {
        tested.setX( 100 );
        assertEquals( 100, tested.getGroup().getX(), 0 );
    }

    @Test
    public void testYCoordinate() {
        tested.setY( 100 );
        assertEquals( 100, tested.getGroup().getY(), 0 );
    }

    @Test
    public void testAddChild() {
        IPrimitive<?> child = new Rectangle( 10, 10 );
        tested.addChild( child );
        verify( layoutContainer, times( 1 ) ).add( eq( child ) );
        verify( layoutContainer, times( 2 ) ).add( any( IPrimitive.class ) );
        verify( layoutContainer, times( 0 ) ).remove( any( IPrimitive.class ) );
        verify( layoutContainer, times( 0 ) ).add( eq( child ) , any( LayoutContainer.Layout.class ) );
    }

    @Test
    public void testAddChildWithLayout() {
        IPrimitive<?> child = new Rectangle( 10, 10 );
        LayoutContainer.Layout layout = LayoutContainer.Layout.CENTER;
        tested.addChild( child, layout );
        verify( layoutContainer, times( 1 ) ).add( eq( child ) , eq( layout ) );
        verify( layoutContainer, times( 1 ) ).add( any( IPrimitive.class ) );
        verify( layoutContainer, times( 0 ) ).remove( any( IPrimitive.class ) );
    }

    @Test
    public void testRemoveChild() {
        IPrimitive<?> child = new Rectangle( 10, 10 );
        tested.removeChild( child );
        verify( layoutContainer, times( 0 ) ).add( eq( child ) );
        verify( layoutContainer, times( 1 ) ).add( any( IPrimitive.class ) );
        verify( layoutContainer, times( 1 ) ).remove( eq( child ) );
        verify( layoutContainer, times( 1 ) ).remove( any( IPrimitive.class ) );
        verify( layoutContainer, times( 0 ) ).add( eq( child ) , any( LayoutContainer.Layout.class ) );
    }

    @Test
    public void testAddWiresHandlers() {
        WiresResizeStartHandler startHandler = mock( WiresResizeStartHandler.class );
        WiresResizeStepHandler stepHandler = mock( WiresResizeStepHandler.class );
        WiresResizeEndHandler endHandler = mock( WiresResizeEndHandler.class );
        tested.addWiresResizeStartHandler( startHandler );
        tested.addWiresResizeStepHandler( stepHandler );
        tested.addWiresResizeEndHandler( endHandler );
        verify( handlerManager, times( 1 ) ).addHandler( WiresResizeStartEvent.TYPE, startHandler );
        verify( handlerManager, times( 1 ) ).addHandler( WiresResizeStepEvent.TYPE, stepHandler );
        verify( handlerManager, times( 1 ) ).addHandler( WiresResizeEndEvent.TYPE, endHandler );
    }

    @Test
    public void testDestroy() {
        WiresShapeControlHandleList m_ctrls = mock( WiresShapeControlHandleList.class );
        WiresShape shape = spy( new WiresShape( path, layoutContainer ) );
        doReturn( m_ctrls ).when( shape ).getControls();
        shape.destroy();
        verify( layoutContainer, times( 1 ) ).destroy();
        verify( m_ctrls, times( 1 ) ).destroy();
        verify( shape, times( 1 ) ).removeFromParent();
    }

}
