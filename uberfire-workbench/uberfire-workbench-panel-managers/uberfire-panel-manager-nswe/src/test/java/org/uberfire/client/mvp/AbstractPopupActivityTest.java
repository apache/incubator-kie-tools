package org.uberfire.client.mvp;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;

import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class AbstractPopupActivityTest  extends BaseWorkbenchTest {


    @Test
    public void testPopUpLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractPopupActivity activity = mock( AbstractPopupActivity.class );
        HashSet<Activity> activities = new HashSet<Activity>( 1 ) {{
            add( activity );
        }};

        when( activityManager.getActivities( somewhere ) ).thenReturn( activities );

        placeManager = new PlaceManagerImplUnitTestWrapper( activity, panelManager );

        placeManager.goTo( somewhere );

        verify( activity, never() ).onStartup( any( PlaceRequest.class ) );
        verify( activity ).onOpen();
    }

}
