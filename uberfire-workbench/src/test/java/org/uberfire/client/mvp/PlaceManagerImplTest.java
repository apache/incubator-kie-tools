package org.uberfire.client.mvp;

import com.google.gwt.event.shared.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.client.mvp.AcceptItem;
import org.uberfire.client.mvp.ActivityMapper;
import org.uberfire.client.mvp.PlaceHistoryHandler;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PlaceManagerImplTest {


    private PlaceHistoryHandler placeHistoryHandler;
    private ActivityMapper activityMapper;
    private Event event;

    @Before
    public void setUp() throws Exception {
        placeHistoryHandler = mock(PlaceHistoryHandler.class);
        activityMapper = mock(ActivityMapper.class);
        event = mock(Event.class);
    }

    @Test
    public void testGoToSomeWhere() throws Exception {
        PlaceRequest somewhere = new PlaceRequest("Somewhere");
        WorkbenchActivity activity = mock(WorkbenchActivity.class);
        when(
                activityMapper.getActivity(somewhere)
        ).thenReturn(
                activity
        );

        PlaceManagerImpl placeManager = new PlaceManagerImpl(activityMapper, placeHistoryHandler, event);
        placeManager.goTo(somewhere);

        verify(activity).onRevealPlace(any(AcceptItem.class));

    }

    @Test
    public void testGoToNoWhere() throws Exception {
        PlaceManagerImpl placeManager = new PlaceManagerImpl(activityMapper, placeHistoryHandler, event);
        placeManager.goTo(new PlaceRequest("Nowhere"));

        assertTrue("Just checking we get no NPEs", true);
    }

    @Test
    public void testPlaceManagerGetInitializedToADefaultPlace() throws Exception {
        new PlaceManagerImpl(activityMapper, placeHistoryHandler, event);
        verify(placeHistoryHandler).register(any(PlaceManager.class), any(EventBus.class), any(PlaceRequest.class));
    }

    @Test
    public void testGoToPreviouslyOpenedPlace() throws Exception {
        PlaceRequest somewhere = new PlaceRequest("Somewhere");
        WorkbenchActivity activity = mock(WorkbenchActivity.class);
        when(
                activityMapper.getActivity(somewhere)
        ).thenReturn(
                activity
        );

        PlaceManagerImpl placeManager = new PlaceManagerImpl(activityMapper, placeHistoryHandler, event);
        placeManager.goTo(somewhere);
        verify(activity, times(1)).onRevealPlace(any(AcceptItem.class));

        PlaceRequest somewhereSecondCall = new PlaceRequest("Somewhere");
        placeManager.goTo(somewhereSecondCall);
        verify(activity, times(1)).onRevealPlace(any(AcceptItem.class));
        verify(event).fire(any(SelectWorkbenchPartEvent.class));
    }

    // TODO: Close
    // TODO: History

}
