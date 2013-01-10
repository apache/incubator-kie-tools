package org.uberfire.client.mvp;

import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.widgets.events.SelectPlaceEvent;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.event.shared.EventBus;

public class PlaceManagerImplTest extends BaseWorkbenchTest {

    @Test
    //Test PlaceManager calls an Activities launch method
    public void testGoToSomeWhere() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final WorkbenchEditorActivity activity = mock( WorkbenchEditorActivity.class );
        when( activity.getDefaultPosition() ).thenReturn( Position.ROOT );
        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( activity );
        }} );

        placeManager.goTo( somewhere );

        verify( activity ).launch( any( AcceptItem.class ),
                                   eq( somewhere ),
                                   isNull( Command.class ) );

    }

    @Test
    //Test going no where doesn't throw any errors
    public void testGoToNoWhere() throws Exception {
        placeManager.goTo( DefaultPlaceRequest.NOWHERE );

        assertTrue( "Just checking we get no NPEs",
                    true );
    }

    @Test
    public void testPlaceManagerGetInitializedToADefaultPlace() throws Exception {
        verify( placeHistoryHandler ).register( any( PlaceManager.class ),
                                                any( EventBus.class ),
                                                any( PlaceRequest.class ) );
    }

    @Test
    //Test PlaceManager only calls an Activities launch method once if re-visited
    public void testGoToPreviouslyOpenedPlace() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final WorkbenchScreenActivity activity = new MockWorkbenchScreenActivity( placeManager );
        final WorkbenchScreenActivity spy = spy( activity );
        when( activityManager.getActivities( somewhere ) ).thenReturn( new HashSet<Activity>( 1 ) {{
            add( spy );
        }} );

        placeManager.goTo( somewhere );

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( selectWorkbenchPartEvent,
                times( 1 ) ).fire( any( SelectPlaceEvent.class ) );

        PlaceRequest somewhereSecondCall = new DefaultPlaceRequest( "Somewhere" );
        placeManager.goTo( somewhereSecondCall );

        verify( spy,
                times( 1 ) ).launch( any( AcceptItem.class ),
                                     eq( somewhere ),
                                     isNull( Command.class ) );
        verify( selectWorkbenchPartEvent,
                times( 2 ) ).fire( any( SelectPlaceEvent.class ) );
    }

    // TODO: Close
    // TODO: History

}
