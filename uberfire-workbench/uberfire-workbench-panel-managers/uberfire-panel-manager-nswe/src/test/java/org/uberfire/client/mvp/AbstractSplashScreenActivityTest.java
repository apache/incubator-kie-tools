package org.uberfire.client.mvp;

import java.util.HashSet;

import org.junit.Test;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AbstractSplashScreenActivityTest extends BaseWorkbenchTest {


    @Test
      public void testSplashScreenActivityShouldNotLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractSplashScreenActivity activity = mock( AbstractSplashScreenActivity.class );
        HashSet<Activity> activities = new HashSet<Activity>( 1 ) {{
            add( activity );
        }};

        when( activityManager.getActivities( somewhere ) ).thenReturn( activities );

        placeManager = new PlaceManagerImplUnitTestWrapper( activity, panelManager );

        placeManager.goTo( somewhere );

        verify( activity , never()).launch( eq( somewhere ), any(Command.class));

    }


    @Test
    public void testSplashScreenActivityLaunch() throws Exception {

        final PlaceRequest somewhere = new DefaultPlaceRequest( "Somewhere" );

        final AbstractSplashScreenActivity splashScreenActivity = mock( AbstractSplashScreenActivity.class );


        final AbstractWorkbenchPerspectiveActivity perspectiveActivity = mock( AbstractWorkbenchPerspectiveActivity.class );

        HashSet<Activity> activities = new HashSet<Activity>( 1 ) {{
            add( perspectiveActivity );
            add( splashScreenActivity );
        }};

        when( activityManager.getActivities( somewhere ) ).thenReturn( activities );

        placeManager = new PlaceManagerImplUnitTestWrapper( perspectiveActivity, panelManager, splashScreenActivity );

        placeManager.goTo( somewhere );

        verify( splashScreenActivity ).launch( eq( somewhere ), any( Command.class ) );

    }

}
