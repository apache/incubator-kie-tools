package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( LienzoMockitoTestRunner.class )
public class WiresManagerTest {

    private static final String LAYER_ID = "theLayer";

    private WiresManager tested;
    private Layer layer;

    @Before
    public void setup() {
        layer = spy( new Layer() );
        layer.setID( LAYER_ID );
        tested  = WiresManager.get( layer );
    }

    @Test
    public void testGetWiresManager() {
        Layer layer2 = new Layer();
        layer2.setID( "layer2" );
        WiresManager tested2  = WiresManager.get( layer2 );
        assertEquals( tested, WiresManager.get( layer ) );
        assertEquals( tested2, WiresManager.get( layer2 ) );
    }

    @Test
    public void testCreateWiresManagerInstance() {
        Layer layer2 = mock( Layer.class );
        when( layer2.uuid() ).thenReturn( "layer2" );
        WiresManager manager = WiresManager.get( layer2 );
        verify( layer2, times( 1 ) ).setOnLayerBeforeDraw( any( WiresManager.LinePreparer.class ) );
        assertNotNull( manager.getAlignAndDistribute() );
        assertNotNull( manager.getLayer() );
        WiresLayer wiresLayer = manager.getLayer();
        assertEquals( layer2, wiresLayer.getLayer() );
    }

    @Test
    public void testRegisterShape() {
        IContainmentAcceptor containmentAcceptor = mock( IContainmentAcceptor.class );
        IDockingAcceptor dockingAcceptor = mock( IDockingAcceptor.class );
        tested.setContainmentAcceptor( containmentAcceptor );
        tested.setDockingAcceptor( dockingAcceptor );
        WiresManager spied = spy( tested );
        HandlerRegistrationManager handlerRegistrationManager = mock( HandlerRegistrationManager.class );
        doReturn( handlerRegistrationManager ).when( spied ).createHandlerRegistrationManager();
        Group group = new Group();
        Group shapeGroup = spy( group );
        WiresShape shape = mock( WiresShape.class );
        when( shape.getGroup() ).thenReturn( shapeGroup );

        WiresShapeControl shapeControl = spied.register( shape );

        assertNotNull( shapeControl );
        assertNotNull( tested.getShape( group.uuid() ) );
        verify( shape, times( 1 ) ).setContainmentAcceptor( eq( containmentAcceptor ) );
        verify( shape, times( 1 ) ).setDockingAcceptor( eq( dockingAcceptor ) );
        verify( shapeGroup, times( 1 ) ).addNodeMouseDownHandler( any( NodeMouseDownHandler.class ) );
        verify( shapeGroup, times( 1 ) ).addNodeMouseUpHandler( any( NodeMouseUpHandler.class ) );
        verify( shapeGroup, times( 1 ) ).setDragConstraints( any( DragConstraintEnforcer.class ) );
        verify( shapeGroup, times( 1 ) ).addNodeDragEndHandler( any( NodeDragEndHandler.class ) );
        verify( layer, times( 1 ) ).add( eq( shapeGroup ) );
        verify( handlerRegistrationManager, times( 3 ) ).register( any( HandlerRegistration.class  ) );
    }

    @Test
    public void testDeregisterShape() {
        WiresManager spied = spy( tested );
        HandlerRegistrationManager handlerRegistrationManager = mock( HandlerRegistrationManager.class );
        doReturn( handlerRegistrationManager ).when( spied ).createHandlerRegistrationManager();
        Group group = new Group();
        String gUUID = group.uuid();
        Group shapeGroup = spy( group );
        WiresShape shape = mock( WiresShape.class );
        when( shape.getGroup() ).thenReturn( shapeGroup );

        spied.register( shape );
        spied.deregister( shape );

        assertNull( tested.getShape( gUUID ) );
        verify( handlerRegistrationManager, times( 1 ) ).removeHandler();
        verify( shape, times( 1 ) ).destroy();
        verify( layer, times( 1 ) ).remove( any( IPrimitive.class ) );
    }

    @Test
    public void testRegisterConnector() {
        IConnectionAcceptor connectionAcceptor = mock( IConnectionAcceptor.class );
        tested.setConnectionAcceptor( connectionAcceptor );
        WiresManager spied = spy( tested );
        HandlerRegistrationManager handlerRegistrationManager = mock( HandlerRegistrationManager.class );
        doReturn( handlerRegistrationManager ).when( spied ).createHandlerRegistrationManager();
        Group group = new Group();
        Group shapeGroup = spy( group );
        AbstractDirectionalMultiPointShape<?> line = mock( AbstractDirectionalMultiPointShape.class );
        MultiPath head = mock( MultiPath.class );
        MultiPath tail = mock( MultiPath.class );
        WiresConnector connector = mock( WiresConnector.class );
        doReturn( shapeGroup ).when( connector ).getGroup();
        doReturn( line ).when( connector ).getLine();
        doReturn( head ).when( connector ).getHead();
        doReturn( tail ).when( connector ).getTail();

        WiresConnectorControl connectorControl = spied.register( connector );

        assertNotNull( connectorControl );
        assertFalse( spied.getConnectorList().isEmpty() );
        verify( connector, times( 1 ) ).setConnectionAcceptor( eq( connectionAcceptor ) );
        verify( connector, times( 1 ) ).addToLayer( eq( layer ) );
        verify( handlerRegistrationManager, times( 3 ) ).register( any( HandlerRegistration.class  ) );
        verify( shapeGroup, times( 1 ) ).addNodeDragStartHandler( any( NodeDragStartHandler.class ) );
        verify( shapeGroup, times( 1 ) ).addNodeDragMoveHandler( any( NodeDragMoveHandler.class ) );
        verify( shapeGroup, times( 1 ) ).addNodeDragEndHandler( any( NodeDragEndHandler.class ) );
        verify( line, times( 1 ) ).addNodeMouseEnterHandler( any( NodeMouseEnterHandler.class ) );
        verify( line, times( 1 ) ).addNodeMouseExitHandler( any( NodeMouseExitHandler.class ) );
        verify( line, times( 1 ) ).addNodeMouseClickHandler( any( NodeMouseClickHandler.class ) );
        verify( head, times( 1 ) ).addNodeMouseEnterHandler( any( NodeMouseEnterHandler.class ) );
        verify( head, times( 1 ) ).addNodeMouseExitHandler( any( NodeMouseExitHandler.class ) );
        verify( head, times( 1 ) ).addNodeMouseClickHandler( any( NodeMouseClickHandler.class ) );
        verify( tail, times( 1 ) ).addNodeMouseEnterHandler( any( NodeMouseEnterHandler.class ) );
        verify( tail, times( 1 ) ).addNodeMouseExitHandler( any( NodeMouseExitHandler.class ) );
        verify( tail, times( 1 ) ).addNodeMouseClickHandler( any( NodeMouseClickHandler.class ) );
    }

    @Test
    public void testDeregisterConnector() {
        WiresManager spied = spy( tested );
        HandlerRegistrationManager handlerRegistrationManager = mock( HandlerRegistrationManager.class );
        doReturn( handlerRegistrationManager ).when( spied ).createHandlerRegistrationManager();
        Group group = new Group();
        Group shapeGroup = spy( group );
        AbstractDirectionalMultiPointShape<?> line = mock( AbstractDirectionalMultiPointShape.class );
        MultiPath head = mock( MultiPath.class );
        MultiPath tail = mock( MultiPath.class );
        WiresConnector connector = mock( WiresConnector.class );
        doReturn( shapeGroup ).when( connector ).getGroup();
        doReturn( line ).when( connector ).getLine();
        doReturn( head ).when( connector ).getHead();
        doReturn( tail ).when( connector ).getTail();

        spied.register( connector );
        spied.deregister( connector );

        assertTrue( spied.getConnectorList().isEmpty() );
        verify( handlerRegistrationManager, times( 1 ) ).removeHandler();
        verify( connector, times( 1 ) ).destroy();
    }

}
