package org.uberfire.client.mvp;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
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
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.event.shared.EventBus;

public class PlaceManagerImplTest {

    private PlaceHistoryHandler             placeHistoryHandler;
    private ActivityManager                 activityManager;
    private Event<SelectWorkbenchPartEvent> event;
    private PanelManager                    panelManager;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        placeHistoryHandler = mock( PlaceHistoryHandler.class );
        activityManager = mock( ActivityManager.class );
        event = mock( Event.class );
        panelManager = mock( PanelManager.class );
    }

    @Test
    public void testGoToSomeWhere() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
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
                                   eq( somewhere ),
                                   isNull( Command.class ) );

    }

    @Test
    public void testGoToNoWhere() throws Exception {
        PlaceManagerImpl placeManager = new PlaceManagerImpl( activityManager,
                                                              placeHistoryHandler,
                                                              event,
                                                              panelManager );
        placeManager.goTo( DefaultPlaceRequest.NOWHERE );

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
        PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );
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
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( event,
                times( 1 ) ).fire( any( SelectWorkbenchPartEvent.class ) );

        PlaceRequest somewhereSecondCall = new DefaultPlaceRequest( "Somewhere" );
        placeManager.goTo( somewhereSecondCall );
        verify( activity,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( event,
                times( 2 ) ).fire( any( SelectWorkbenchPartEvent.class ) );
    }

    // TODO: Close
    // TODO: History

}
