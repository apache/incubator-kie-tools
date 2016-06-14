package org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl;

import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultPinnedModeManagerTest {

    @Mock
    private GridLayer gridLayer;

    @Mock
    private Layer layer;

    @Mock
    private TransformMediator defaultMediator;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private Command enterPinnedModeCommand;

    @Mock
    private Command exitPinnedModeCommand;

    private Mediators mediators;

    private GridPinnedModeManager manager;

    @Before
    public void setup() {
        this.manager = new DefaultPinnedModeManager( gridLayer,
                                                     defaultMediator ) {
            @Override
            protected void doEnterPinnedMode( final GridWidget gridWidget,
                                              final Command onStartCommand ) {
                onStartCommand.execute();
            }

            @Override
            protected void doExitPinnedMode( final Command onCompleteCommand ) {
                onCompleteCommand.execute();
            }
        };
        this.mediators = new Mediators( viewport );
        this.mediators.push( new RestrictedMousePanMediator( gridLayer ) );

        when( gridLayer.getViewport() ).thenReturn( viewport );
        when( gridWidget.getViewport() ).thenReturn( viewport );
        when( gridWidget.getLayer() ).thenReturn( layer );
        when( viewport.getMediators() ).thenReturn( mediators );
        when( viewport.getTransform() ).thenReturn( transform );
        when( transform.copy() ).thenReturn( transform );
        when( transform.getInverse() ).thenReturn( transform );
    }

    @Test
    public void enteringPinnedModeSetsMediatorToGridTransformMediator() {
        manager.enterPinnedMode( gridWidget,
                                 enterPinnedModeCommand );

        verify( enterPinnedModeCommand,
                times( 1 ) ).execute();

        assertNotNull( manager.getPinnedContext() );

        final IMediator mediator = mediators.pop();
        assertTrue( mediator instanceof RestrictedMousePanMediator );

        final RestrictedMousePanMediator rmpm = (RestrictedMousePanMediator) mediator;
        final TransformMediator tm = rmpm.getTransformMediator();

        assertTrue( tm instanceof GridTransformMediator );
    }

    @Test
    public void exitingPinnedModeSetsMediatorToDefaultTransformMediator() {
        manager.enterPinnedMode( gridWidget,
                                 enterPinnedModeCommand );

        manager.exitPinnedMode( exitPinnedModeCommand );

        verify( exitPinnedModeCommand,
                times( 1 ) ).execute();

        assertNull( manager.getPinnedContext() );

        final IMediator mediator = mediators.pop();
        assertTrue( mediator instanceof RestrictedMousePanMediator );

        final RestrictedMousePanMediator rmpm = (RestrictedMousePanMediator) mediator;
        final TransformMediator tm = rmpm.getTransformMediator();

        assertEquals( defaultMediator,
                      tm );
    }

}
