package org.uberfire.client.mvp;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;

import org.junit.Test;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class AbstractWorkbenchPerspectiveActivityTest extends BaseWorkbenchTest {


    @Test
    public void testAbstractWorkbenchPerspectiveActivityLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractWorkbenchPerspectiveActivity perspectiveActivity = mock( AbstractWorkbenchPerspectiveActivity.class );

        HashSet<Activity> activities = new HashSet<Activity>( 1 ) {{
            add( perspectiveActivity );
        }};

        when( activityManager.getActivities( somewhere ) ).thenReturn( activities );

        placeManager = new PlaceManagerImplUnitTestWrapper( perspectiveActivity, panelManager );

        placeManager.goTo( somewhere );

        verify( perspectiveActivity ).onStartup( eq( somewhere ) );

    }

}
