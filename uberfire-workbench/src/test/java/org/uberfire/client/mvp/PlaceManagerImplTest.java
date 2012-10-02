package org.uberfire.client.mvp;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PlaceRequestImpl;

import com.google.gwt.event.shared.EventBus;

public class PlaceManagerImplTest {

    private PlaceHistoryHandler placeHistoryHandler;
    private ActivityManager     activityManager;
    private Event               event;
    private PanelManager        panelManager;

    @Before
    public void setUp() throws Exception {
        placeHistoryHandler = mock( PlaceHistoryHandler.class );
        activityManager = mock( ActivityManager.class );
        event = mock( Event.class );
        panelManager = mock( PanelManager.class );
    }

    @Test
    public void testGoToSomeWhere() throws Exception {
        final PlaceRequest somewhere = new PlaceRequestImpl( "Somewhere" );
        final WorkbenchEditorActivity activity = mock( WorkbenchEditorActivity.class );
        when(
                activity.getDefaultPosition() ).thenReturn(
                                                            Position.NONE
                );

        when(
                activityManager.getActivity( somewhere ) ).thenReturn(
                                                                       activity
                );

        PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                              placeHistoryHandler,
                                                              event,
                                                              panelManager );
        placeManager.goTo( somewhere );

        verify( activity ).launch( any( AcceptItem.class ),
                                   eq( somewhere ) );

    }

    @Test
    public void testGoToNoWhere() throws Exception {
        PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                              placeHistoryHandler,
                                                              event,
                                                              panelManager );
        placeManager.goTo( PlaceRequestImpl.NOWHERE );

        assertTrue( "Just checking we get no NPEs",
                    true );
    }

    @Test
    public void testPlaceManagerGetInitializedToADefaultPlace() throws Exception {
        new PlaceManagerImpl( activityManager,
                              placeHistoryHandler,
                              event,
                              panelManager );
        verify( placeHistoryHandler ).register( any( PlaceManager.class ),
                                                any( EventBus.class ),
                                                any( PlaceRequest.class ) );
    }

    @Test
    public void testGoToPreviouslyOpenedPlace() throws Exception {
        PlaceRequest somewhere = new PlaceRequestImpl( "Somewhere" );
        WorkbenchEditorActivity activity = mock( WorkbenchEditorActivity.class );
        when(
                activityManager.getActivity( somewhere ) ).thenReturn(
                                                                       activity
                );

        PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                              placeHistoryHandler,
                                                              event,
                                                              panelManager );
        placeManager.goTo( somewhere );
        verify( activity,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ) );
        verify( event,
                times( 1 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

        PlaceRequest somewhereSecondCall = new PlaceRequestImpl( "Somewhere" );
        placeManager.goTo( somewhereSecondCall );
        verify( activity,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ) );
        verify( event,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );
    }

    // TODO: Close
    // TODO: History

}
