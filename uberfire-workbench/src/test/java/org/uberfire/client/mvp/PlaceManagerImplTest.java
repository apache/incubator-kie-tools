package org.uberfire.client.mvp;

import com.google.gwt.event.shared.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.uberfire.shared.mvp.PlaceRequest.NOWHERE;

public class PlaceManagerImplTest {


    private PlaceHistoryHandler placeHistoryHandler;
    private ActivityManager activityManager;
    private Event event;

    @Before
    public void setUp() throws Exception {
        placeHistoryHandler = mock(PlaceHistoryHandler.class);
        activityManager = mock(ActivityManager.class);
        event = mock(Event.class);
    }

    @Test
    public void testGoToSomeWhere() throws Exception {
        PlaceRequest somewhere = new PlaceRequest("Somewhere");
        WorkbenchEditorActivity activity = mock(WorkbenchEditorActivity.class);
        when(
                activityManager.getActivity(somewhere)
        ).thenReturn(
                activity
        );

        PlaceManagerImpl placeManager = new PlaceManagerImpl(activityManager, placeHistoryHandler, event);
        placeManager.goTo(somewhere);

        verify(activity).launch(any(AcceptItem.class));

    }

    @Test
    public void testGoToNoWhere() throws Exception {
        PlaceManagerImpl placeManager = new PlaceManagerImpl(activityManager, placeHistoryHandler, event);
        placeManager.goTo(NOWHERE);

        assertTrue("Just checking we get no NPEs", true);
    }

    @Test
    public void testPlaceManagerGetInitializedToADefaultPlace() throws Exception {
        new PlaceManagerImpl(activityManager, placeHistoryHandler, event);
        verify(placeHistoryHandler).register(any(PlaceManager.class), any(EventBus.class), any(PlaceRequest.class));
    }

    @Test
    public void testGoToPreviouslyOpenedPlace() throws Exception {
        PlaceRequest somewhere = new PlaceRequest("Somewhere");
        WorkbenchEditorActivity activity = mock(WorkbenchEditorActivity.class);
        when(
                activityManager.getActivity(somewhere)
        ).thenReturn(
                activity
        );

        PlaceManagerImpl placeManager = new PlaceManagerImpl(activityManager, placeHistoryHandler, event);
        placeManager.goTo(somewhere);
        verify(activity, times(1)).launch(any(AcceptItem.class));

        PlaceRequest somewhereSecondCall = new PlaceRequest("Somewhere");
        placeManager.goTo(somewhereSecondCall);
        verify(activity, times(1)).launch(any(AcceptItem.class));
        verify(event).fire(any(SelectWorkbenchPartEvent.class));
    }

    // TODO: Close
    // TODO: History

}
