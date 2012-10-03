package org.uberfire.client.mvp;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PlaceRequestImpl;

/**
 * Initial (poor coverage) integration tests for PlaceManager, PanelManager and
 * life-cycle events. There remains a lot more work to do in this class.
 */
public class AbstractWorkbenchScreenActivityTests {

    private PlaceHistoryHandler             placeHistoryHandler;
    private ActivityManager                 activityManager;
    private Event<SelectWorkbenchPartEvent> event;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        placeHistoryHandler = mock( PlaceHistoryHandler.class );
        activityManager = mock( ActivityManager.class );
        event = mock( Event.class );
    }

    @Test
    //Reveal a Place once. It should be launched, OnStart and OnReveal called once.
    public void tesGoToOnePlace() throws Exception {
        final PanelManager panelManager = mock( PanelManager.class );
        final PlaceRequest somewhere = new PlaceRequestImpl( "Somewhere" );

        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    event,
                                                                    panelManager );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );

        placeManager.goTo( somewhere );

        verify( spy ).launch( any( AcceptItem.class ),
                              eq( somewhere ) );
        verify( spy ).onStart( eq( somewhere ) );
        verify( spy ).onReveal();

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ) );
        verify( event,
                times( 1 ) ).fire( any( SelectWorkbenchPartEvent.class ) );
    }

    @Test
    //Reveal the same Place twice. It should be launched, OnStart and OnReveal called once.
    public void tesGoToOnePlaceTwice() throws Exception {
        final PanelManager panelManager = mock( PanelManager.class );
        final PlaceRequest somewhere = new PlaceRequestImpl( "Somewhere" );
        final PlaceRequest somewhereTheSame = new PlaceRequestImpl( "Somewhere" );

        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    event,
                                                                    panelManager );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy );

        placeManager.goTo( somewhere );
        placeManager.goTo( somewhereTheSame );

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ) );
        verify( spy,
                times( 1 ) ).onStart( eq( somewhere ) );
        verify( spy,
                times( 1 ) ).onReveal();

        verify( event,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

    }

    @Test
    //Reveal two different Places. Each should be launched, OnStart and OnReveal called once.
    public void tesGoToTwoDifferentPlaces() throws Exception {
        final PanelManager panelManager = mock( PanelManager.class );
        final PlaceRequest somewhere = new PlaceRequestImpl( "Somewhere" );
        final PlaceRequest somewhereElse = new PlaceRequestImpl( "SomewhereElse" );

        final PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                                    placeHistoryHandler,
                                                                    event,
                                                                    panelManager );

        //The first place
        final WorkbenchScreenActivity activity1 = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy1 = spy( activity1 );

        //The second place
        final WorkbenchScreenActivity activity2 = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy2 = spy( activity2 );

        when( activityManager.getActivity( somewhere ) ).thenReturn( spy1 );
        when( activityManager.getActivity( somewhereElse ) ).thenReturn( spy2 );

        placeManager.goTo( somewhere );
        placeManager.goTo( somewhereElse );

        verify( spy1,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ) );
        verify( spy1,
                times( 1 ) ).onStart( eq( somewhere ) );
        verify( spy1,
                times( 1 ) ).onReveal();

        verify( spy2,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhereElse ) );
        verify( spy2,
                times( 1 ) ).onStart( eq( somewhereElse ) );
        verify( spy2,
                times( 1 ) ).onReveal();

        verify( event,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

    }

}
